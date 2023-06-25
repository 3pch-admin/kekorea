package e3ps.org.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.infoengine.SAK.Task;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.org.PeopleMakLink;
import e3ps.org.PeopleWTUserLink;
import e3ps.org.dto.UserDTO;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.federation.PrincipalManager.DirContext;
import wt.org.OrganizationServicesMgr;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTProperties;

public class StandardOrgService extends StandardManager implements OrgService {

	private static String userAdapter;
	private static String userDirectory;
	private static String userSearch;

	private String postFix = "@kokusai-electric.com";

	static {
		try {
			WTProperties props = WTProperties.getServerProperties();
			String taskRootDirectory = props.getProperty("wt.federation.taskRootDirectory");
			String taskCodebase = props.getProperty("wt.federation.taskCodebase");
			String mapCredentials = props.getProperty("wt.federation.task.mapCredentials");
			String credentialsMapper = System.getProperty("com.infoengine.credentialsMapper");

			userDirectory = props.getProperty("wt.federation.org.defaultDirectoryUser",
					props.getProperty("wt.admin.defaultAdministratorName"));

			String VMName = props.getProperty("wt.federation.ie.VMName");
			if (VMName != null) {
				System.setProperty("com.infoengine.vm.name", VMName);
			}
			if (taskCodebase != null) {
				System.setProperty("com.infoengine.taskProcessor.codebase", taskCodebase);
			}
			if (taskRootDirectory != null) {
				System.setProperty("com.infoengine.taskProcessor.templateRoot", taskRootDirectory);
			}
			if (mapCredentials != null && credentialsMapper == null) {
				System.setProperty("com.infoengine.credentialsMapper", mapCredentials);
			}

			userAdapter = DirContext.getDefaultJNDIAdapter();
			userSearch = DirContext.getJNDIAdapterSearchBase(userAdapter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static StandardOrgService newStandardOrgService() throws WTException {
		StandardOrgService instance = new StandardOrgService();
		instance.initialize();
		return instance;
	}

	protected synchronized void performStartupProcess() throws ManagerException {
		super.performStartupProcess();
		try {
			Department department = makeRoot();
			inspectUser(department);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Department makeRoot() throws Exception {
		Department department = null;
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Department.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, Department.class, Department.CODE, "ROOT");
			QueryResult result = PersistenceHelper.manager.find(query);
			// 루트 부서가 있을 경우
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				department = (Department) obj[0];
			} else {
				// 루트 부서가 없을 경우
				department = Department.newDepartment();
				department.setName("국제엘렉트릭코리아");
				department.setCode("ROOT");
				department.setDescription("국제엘렉트릭코리아");
				department = (Department) PersistenceHelper.manager.save(department);
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
		return department;
	}

	@Override
	public void inspectUser(Department department) throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec qs = new QuerySpec();
			int _idx = qs.appendClassList(People.class, true);
			QuerySpecUtils.toOrderBy(qs, _idx, People.class, People.NAME, true);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				People u = (People) obj[0];
				String id = u.getId();
				WTUser user = OrganizationServicesMgr.getUser(id);
				if (user == null) {
					PersistenceHelper.manager.delete(u);
				}
			}

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(WTUser.class, true);
			QuerySpecUtils.toBooleanAnd(query, idx, WTUser.class, WTUser.DISABLED, false);
			QuerySpecUtils.toBooleanAnd(query, idx, WTUser.class, WTUser.REPAIR_NEEDED, false);
			QuerySpecUtils.toOrderBy(query, idx, WTUser.class, WTUser.FULL_NAME, false);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTUser wtuser = (WTUser) obj[0];

				QueryResult _qr = PersistenceHelper.manager.navigate(wtuser, "people", PeopleWTUserLink.class);

				People user = null;
				if (!_qr.hasMoreElements()) {
					user = People.newPeople();
					user.setDepartment(department);
					user.setWtUser(wtuser);
					user.setName(wtuser.getFullName());
					user.setId(wtuser.getName());
					user.setEmail(wtuser.getEMail() != null ? wtuser.getEMail() : "");
					user = (People) PersistenceHelper.manager.save(user);
				}
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
	}

	@Override
	public People createUser(WTUser sessionUser) throws WTException {
		People user = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			user = People.newPeople();
			user.setName(sessionUser.getFullName());

			String a = sessionUser.getName();
			user.setId(a);
			user.setEmail(sessionUser.getEMail() != null ? sessionUser.getEMail() : "");
			user.setWtUser(sessionUser);
			user.setDuty("사원");
			user.setDepartment(OrgHelper.manager.getDepartment("ROOT"));

			user = (People) PersistenceHelper.manager.save(user);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return user;
	}

	@Override
	public void save(HashMap<String, List<UserDTO>> dataMap) throws Exception {
		List<UserDTO> editRows = dataMap.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (UserDTO dto : editRows) {
				String oid = dto.getOid();
				String maks = dto.getMak();
				String email = dto.getEmail();
				String duty = dto.getDuty();
				String department_oid = dto.getDepartment_oid();
				boolean setting = dto.isSetting();
				int gap = dto.getGap();
				boolean resign = dto.isResign();

				People people = (People) CommonUtils.getObject(oid);
				Department department = null;
				if (!StringUtils.isNull(department_oid)) {
					department = (Department) CommonUtils.getObject(department_oid);
					people.setDepartment(department);
				}
				people.setEmail(email);
				people.setDuty(duty);
				people.setResign(resign);
				people.setSetting(setting);
				people.setGap(gap);
				PersistenceHelper.manager.modify(people);

				ArrayList<PeopleMakLink> makLinks = OrgHelper.manager.getMakLinks(people);

				for (PeopleMakLink link : makLinks) {
					PersistenceHelper.manager.delete(link);
				}

				if (!StringUtils.isNull(maks)) {
					String[] makArray = maks.split(",");
					for (String mak : makArray) {
						CommonCode makCode = CommonCodeHelper.manager.getCommonCode(mak.trim(), "MAK");
						PeopleMakLink link = PeopleMakLink.newPeopleMakLink(people, makCode);
						PersistenceHelper.manager.save(link);
					}
				}
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void save(WTUser wtUser) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			People user = People.newPeople();
			user.setDepartment(OrgHelper.manager.getRoot());
			user.setWtUser(wtUser);
			user.setName(wtUser.getFullName());
			user.setId(wtUser.getName());
			user.setEmail(wtUser.getEMail() != null ? wtUser.getEMail() : "");
			user = (People) PersistenceHelper.manager.save(user);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();

		}
	}

	@Override
	public void modify(WTUser wtUser) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();
			People user = null;
			QueryResult result = PersistenceHelper.manager.navigate(wtUser, "people", PeopleWTUserLink.class);
			if (result.hasMoreElements()) {
				user = (People) result.nextElement();
				user.setDepartment(OrgHelper.manager.getRoot());
				user.setWtUser(wtUser);
				user.setName(wtUser.getFullName());
				user.setId(wtUser.getName());
				user.setEmail(wtUser.getEMail() != null ? wtUser.getEMail() : "");
				user = (People) PersistenceHelper.manager.modify(user);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();

		}
	}

	@Override
	public void modify(UserDTO dto) throws Exception {
		String oid = dto.getOid();
		String department_oid = dto.getDepartment_oid();
		String duty = dto.getDuty();
		String email = dto.getEmail();
		Transaction trs = new Transaction();
		try {
			trs.start();

			People people = (People) CommonUtils.getObject(oid);
			people.setDuty(duty);
			if (!StringUtils.isNull(department_oid)) {
				Department department = (Department) CommonUtils.getObject(department_oid);
				people.setDepartment(department);
			}
			people.setEmail(email + postFix);
			PersistenceHelper.manager.modify(people);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void fire(String oid, boolean isFire) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			People people = (People) CommonUtils.getObject(oid);
			people.setResign(isFire);
			PersistenceHelper.manager.modify(people);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void init(Map<String, String> params) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser user = CommonUtils.sessionUser();

			QueryResult result = PersistenceHelper.manager.navigate(user, "people", PeopleWTUserLink.class);
			if (result.hasMoreElements()) {
				People people = (People) result.nextElement();
				people.setLast(new Timestamp(new Date().getTime()));
				PersistenceHelper.manager.modify(people);
			}

			String id = user.getName();
			if ("wcadmin".equals(id)) {
				id = "Administrator";
			}
			String uid = DirContext.getMapping(userAdapter, "user.uniqueIdAttribute",
					DirContext.getMapping(userAdapter, "user.uid"));
			String object = uid + "=" + id + "," + userSearch;

			Task task = new Task("/wt/federation/UpdatePrincipal.xml");
			task.addParam("object", object);
			task.addParam("field", DirContext.getMapping(userAdapter, "user.userPassword") + "=1");
			task.addParam("modification", "replace");
			task.addParam("instance", userAdapter);
			task.setUsername(userDirectory);
			task.invoke();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void password(Map<String, String> params) throws Exception {
		String password = params.get("password");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser user = CommonUtils.sessionUser();

			QueryResult result = PersistenceHelper.manager.navigate(user, "people", PeopleWTUserLink.class);
			if (result.hasMoreElements()) {
				People people = (People) result.nextElement();
				people.setLast(new Timestamp(new Date().getTime()));
				PersistenceHelper.manager.modify(people);
			}

			String id = user.getName();
			if ("Administrator".equals(id)) {
				id = "wcadmin";
			}

			String uid = DirContext.getMapping(userAdapter, "user.uniqueIdAttribute",
					DirContext.getMapping(userAdapter, "user.uid"));
			String object = uid + "=" + id + "," + userSearch;

			Task task = new Task("/wt/federation/UpdatePrincipal.xml");
			task.addParam("object", object);
			task.addParam("field", DirContext.getMapping(userAdapter, "user.userPassword") + "=" + password);
			task.addParam("modification", "replace");
			task.addParam("instance", userAdapter);
			task.setUsername(userDirectory);
			task.invoke();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

}