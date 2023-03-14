package e3ps.org.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.infoengine.SAK.Task;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.mail.MailUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.org.PeopleInstallLink;
import e3ps.org.PeopleMakLink;
import e3ps.org.PeopleWTUserLink;
import e3ps.org.dto.UserDTO;
import e3ps.workspace.ApprovalUserLine;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.federation.PrincipalManager.DirContext;
import wt.org.OrganizationServicesHelper;
import wt.org.OrganizationServicesMgr;
import wt.org.WTGroup;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTAttributeNameIfc;
import wt.util.WTException;
import wt.util.WTProperties;

public class StandardOrgService extends StandardManager implements OrgService {

	private static String userAdapter;
	private static String userDirectory;
	private static String userSearch;

	private String email_prefix = "@kekorea.co.kr";

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
				// not value...
				People user = null;

				if (!_qr.hasMoreElements()) {
					user = People.newPeople();
					// set foreignKey
					user.setDepartment(department);
					user.setWtUser(wtuser);

					String a = wtuser.getName();
					user.setName(wtuser.getFullName());
					user.setId(a);
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
	public Map<String, Object> changePasswordAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		String password = (String) param.get("password");
		WTUser user = null;

		try {
			trs.start();

			user = (WTUser) SessionHelper.manager.getPrincipal();
			String id = user.getName();
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

			// success
			map.put("msg", user.getFullName() + " 사용자의 비밀번호가 변경 되었습니다.");

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("msg", user.getFullName() + " 사용자의 비밀번호 변경 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> initPasswordAction(Map<String, Object> param) throws WTException {
		String oid = (String) param.get("oid");
		People user = null;
		ReferenceFactory rf = new ReferenceFactory();
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();

		try {
			trs.start();

			user = (People) rf.getReference(oid).getObject();

			String email = user.getEmail();
			Random rnd = new Random();
			String password = "";
			for (int i = 0; i < 10; i++) {
				String lowerStr = String.valueOf((char) ((int) (rnd.nextInt(26)) + 97));
				String upperStr = String.valueOf((char) ((int) (rnd.nextInt(26)) + 65));
				String number = String.valueOf(rnd.nextInt(10));
				if (i % 3 == 0) {
					password += lowerStr;
				} else if (i % 3 == 1) {
					password += upperStr;
				} else {
					password += number;
				}
			}

			String id = user.getWtUser().getName();
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

			// email = "jongwoong.moon@techwing.co.kr";
//			email = "jhkim@e3ps.com";

			if (MailUtils.isMail && !StringUtils.isNull(email)) {
				// 메일 연동할 경우만..
				System.out.println("이메일 전송 : " + email);
				MailUtils.sendInitPasswordMail(email, password);
			}

			// success
			map.put("msg", user.getName() + " 사용자의 비밀번호가 초기화되었습니다.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("msg", user.getName() + " 사용자의 비밀번호 초기화 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			// fail..
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> saveUserLineAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		List<String> appList = (List<String>) param.get("appList");
		List<String> agreeList = (List<String>) param.get("agreeList");
		List<String> receiveList = (List<String>) param.get("receiveList");
		String lineName = (String) param.get("lineName");
		String lineType = (String) param.get("lineType");

		ApprovalUserLine userLine = null;

		Transaction trs = new Transaction();
		try {
			trs.start();

			userLine = ApprovalUserLine.newApprovalUserLine();
			userLine.setName(lineName);
			userLine.setLineType(lineType);
			userLine.setApprovalList(appList);
			userLine.setAgreeList(agreeList);
			userLine.setReceiveList(receiveList);

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());
			userLine.setOwnership(ownership);
			PersistenceHelper.manager.save(userLine);

			trs.commit();
			trs = null;
			map.put("msg", "개인 결재선이 저장 되었습니다.");
			map.put("url", "/Windchill/plm/org/addLine?lineType=" + lineType);
		} catch (Exception e) {
			map.put("msg", "개인 결재선 저장 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/org/addLine?lineType=" + lineType);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> deleteUserLineAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ApprovalUserLine userLine = null;
//		String oid = (String) param.get("oid");
		List<String> list = (List<String>) param.get("list");
		String lineType = (String) param.get("lineType");
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				userLine = (ApprovalUserLine) rf.getReference(oid).getObject();
				PersistenceHelper.manager.delete(userLine);
			}

			map.put("msg", "개인 결재선이 삭제 되었습니다.");
			map.put("url", "/Windchill/plm/org/addLine?lineType=" + lineType);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("msg", "개인 결재선 삭제 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/org/addLine?lineType=" + lineType);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> setResignAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		People user = null;
		ReferenceFactory rf = new ReferenceFactory();
		WTGroup group = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			group = OrganizationServicesMgr.getGroup("국제엘렉트릭코리아 퇴사자 그룹");

			if (group != null) {
				System.out.println("그룹이 존재 합니다.");
			} else {
				group = WTGroup.newWTGroup("국제엘렉트릭코리아 퇴사자 그룹");
				OrganizationServicesHelper.manager.savePrincipal(group);
			}

			for (String oid : list) {
				user = (People) rf.getReference(oid).getObject();
				user.setResign(true);
				PersistenceHelper.manager.modify(user);

				boolean success = OrganizationServicesHelper.manager.addMember(group, user.getWtUser());
				if (success) {
					System.out.println("퇴사자 그룹에 할당된 사용자 이름 : " + user.getWtUser().getFullName() + ", 아이디 :  "
							+ user.getWtUser().getName());
				}
			}

			map.put("msg", "선택한 사용자들의 퇴사 처리가 되었습니다.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("msg", "사용자 퇴사 처리 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> addUserAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		People user = null;
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<String[]> data = new ArrayList<String[]>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			user = (People) rf.getReference(oid).getObject();
			UserViewData udata = new UserViewData(user);
			// 0 oid, 1 number, 2 name, 3 version, 4 state, 5 creator, 6 createdate
			String[] s = new String[] { udata.oid, udata.name, udata.id, udata.duty, udata.departmentName, udata.email,
					udata.resign, udata.createDate };
			data.add(s);

			map.put("list", data);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("msg", "사용자 추가 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/approval/addUser");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyUserAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String name = (String) param.get("name");
		String rank = (String) param.get("rank");
		String duty = (String) param.get("duty");
		String email = (String) param.get("email");
		People user = null;
		ReferenceFactory rf = new ReferenceFactory();
		WTUser wtuser = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			user = (People) rf.getReference(oid).getObject();
			user.setName(name);
			user.setDuty(duty);
			user.setEmail(email + email_prefix);

			wtuser = user.getWtUser();
			wtuser.setFullName(name);
			wtuser.setEMail(email + email_prefix);
			PersistenceHelper.manager.modify(wtuser);

			user.setWtUser(wtuser);

			PersistenceHelper.manager.modify(user);

			map.put("msg", "사용자 정보가 수정 되었습니다.");
			map.put("url", "/Windchill/plm/org/viewUser?oid=" + oid);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("msg", "사용자 정보 수정 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/org/modifyUser?oid=" + oid);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> setDutyAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		String duty = (String) param.get("duty");
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				People user = (People) rf.getReference(oid).getObject();
				user.setDuty(duty);
				PersistenceHelper.manager.modify(user);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}

		return map;
	}

	@Override
	public Map<String, Object> setDeptAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		String doid = (String) param.get("doid");
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				People user = (People) rf.getReference(oid).getObject();
				Department dept = null;
				if (!StringUtils.isNull(doid)) {
					dept = (Department) rf.getReference(doid).getObject();
				}
				user.setDepartment(dept);
				PersistenceHelper.manager.modify(user);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}

		return map;
	}

	@Override
	public void save(WTUser wtuser) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			People user = People.newPeople();
			user.setWtUser(wtuser);
			user.setName(wtuser.getFullName());
			user.setId(wtuser.getName());
			user.setEmail(wtuser.getEMail());
			user.setDepartment(OrgHelper.manager.getRoot());
			user.setDuty("사원");
			PersistenceHelper.manager.save(user);

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
	public void modify(WTUser wtuser) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			People user = OrgHelper.manager.getUser(wtuser.getName());
			user.setName(wtuser.getFullName());
			user.setId(wtuser.getName());
			user.setEmail(wtuser.getEMail());
			PersistenceHelper.manager.modify(user);

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
	public void save(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> editRow = (ArrayList<Map<String, Object>>) params.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			editRow.forEach(map -> {
				String name = (String) map.get("name");
			});

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
	public void save(HashMap<String, List<UserDTO>> dataMap) throws Exception {
		List<UserDTO> editRows = dataMap.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (UserDTO dto : editRows) {
				String oid = dto.getOid();
				String maks = dto.getMak();
				String installs = dto.getInstall();
				String email = dto.getEmail();
				String duty = dto.getDuty();
				String department_oid = dto.getDepartment_oid();
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
				PersistenceHelper.manager.modify(people);

				ArrayList<PeopleMakLink> makLinks = OrgHelper.manager.getMakLinks(people);
				ArrayList<PeopleInstallLink> installLinks = OrgHelper.manager.getInstallLinks(people);

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

				for (PeopleInstallLink link : installLinks) {
					PersistenceHelper.manager.delete(link);
				}

				if (!StringUtils.isNull(installs)) {
					String[] installArray = installs.split(",");
					for (String install : installArray) {
						CommonCode installCode = CommonCodeHelper.manager.getCommonCode(install.trim(), "INSTALL");
						PeopleInstallLink link = PeopleInstallLink.newPeopleInstallLink(people, installCode);
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
}