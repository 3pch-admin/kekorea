package e3ps.org.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.infoengine.SAK.Task;

import e3ps.approval.ApprovalUserLine;
import e3ps.common.mail.MailUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.StringUtils;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.org.UserTableSet;
import e3ps.org.WTUserPeopleLink;
import e3ps.org.beans.UserViewData;
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
import wt.util.WTException;
import wt.util.WTProperties;

public class StandardOrgService extends StandardManager implements OrgService, MessageHelper {

	private static final long serialVersionUID = 6434143991228325882L;

	private static String userAdapter;
	private static String userDirectory;
	private static String userSearch;
	private String ADMIN_ID = "wcadmin";

	private String email_prefix = "@kekorea.co.kr";

//	private String email_prefix = "@e3ps.com";

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
			// root department create
			Department department = makeRoot();
			// user create object
			inspectUser(department);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Department makeRoot() throws WTException {
		Department department = null;
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			if (!OrgHelper.manager.duplicate("ROOT")) {

				department = Department.newDepartment();
				department.setName("국제엘렉트릭코리아");
				department.setCode("ROOT");
				department.setDescription("국제엘렉트릭코리아");
				department = (Department) PersistenceHelper.manager.save(department);
			} else {

				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(Department.class, true);

				SearchCondition sc = new SearchCondition(Department.class, Department.CODE, "=", "ROOT");
				query.appendWhere(sc, new int[] { idx });

				QueryResult result = PersistenceHelper.manager.find(query);
				if (result.hasMoreElements()) {
					Object[] obj = (Object[]) result.nextElement();
					department = (Department) obj[0];
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
		return department;
	}

	@Override
	public void inspectUser(Department department) throws WTException {
		SessionContext prev = SessionContext.newContext();
		Transaction trs = new Transaction();
		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			QuerySpec spec = new QuerySpec();
			spec.appendClassList(People.class, true);
			QueryResult navi = PersistenceHelper.manager.find(spec);
			while (navi.hasMoreElements()) {
				Object[] obj = (Object[]) navi.nextElement();
				People u = (People) obj[0];
				String id = u.getId();
				WTUser user = OrganizationServicesMgr.getUser(id);
				if (user == null) {
					PersistenceHelper.manager.delete(u);
				}
			}

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(WTUser.class, true);

			SearchCondition sc = new SearchCondition(WTUser.class, WTUser.DISABLED, SearchCondition.IS_FALSE);
			query.appendWhere(sc, new int[] { idx });
			query.appendAnd();

			sc = new SearchCondition(WTUser.class, WTUser.REPAIR_NEEDED, SearchCondition.IS_FALSE);
			query.appendWhere(sc, new int[] { idx });

			ClassAttribute ca = new ClassAttribute(WTUser.class, WTUser.FULL_NAME);
			OrderBy orderBy = new OrderBy(ca, false);
			query.appendOrderBy(orderBy, new int[] { idx });
			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				WTUser wtuser = (WTUser) obj[0];

				QueryResult qr = PersistenceHelper.manager.navigate(wtuser, "people", WTUserPeopleLink.class);
				// not value...
				People user = null;

				Timestamp today = new Timestamp(new Date().getTime());

				if (!qr.hasMoreElements()) {
					user = People.newPeople();
					// set foreignKey
					user.setDepartment(department);
					user.setUser(wtuser);

					String a = wtuser.getName();
					if (a.equals("Administrator")) {
						a = ADMIN_ID;
					}
					user.setName(wtuser.getFullName());
//					user.setName(a);
					user.setId(a);
					user.setPasswordUpdateTime(today);
					user.setEmail(wtuser.getEMail() != null ? wtuser.getEMail() : "");
					user = (People) PersistenceHelper.manager.save(user);
				} else {
					user = (People) qr.nextElement();
//					user.setPasswordUpdateTime(today);
//					user.setDepartment(department);
					user = (People) PersistenceHelper.manager.modify(user);
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
			if (a.equals("Administrator")) {
				a = ADMIN_ID;
			}
//			user.setId(sessionUser.getName());
			user.setId(a);
			user.setEmail(sessionUser.getEMail() != null ? sessionUser.getEMail() : "");
			user.setFax(sessionUser.getFaxNumber());
			user.setUser(sessionUser);
			user.setDuty("사원");
			user.setDepartment(OrgHelper.manager.getDepartment("ROOT"));
			user.setPhone(null);
			user.setMobile(null);

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
			if (id.equals("Administrator")) {
				id = ADMIN_ID;
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

			// success
			map.put("result", SUCCESS);
			map.put("msg", user.getFullName() + " 사용자의 비밀번호가 변경 되었습니다.");
			map.put("url", BASE_URL + "/org/logout");

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", user.getFullName() + " 사용자의 비밀번호 변경 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", BASE_URL + "/org/changePassword");
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

			String id = user.getUser().getName();
			if (id.equals("Administrator")) {
				id = ADMIN_ID;
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

			// email = "jongwoong.moon@techwing.co.kr";
//			email = "jhkim@e3ps.com";

			if (MailUtils.isMail && !StringUtils.isNull(email)) {
				// 메일 연동할 경우만..
				System.out.println("이메일 전송 : " + email);
				MailUtils.sendInitPasswordMail(email, password);
			}

			// success
			map.put("result", SUCCESS);
			map.put("msg", user.getName() + " 사용자의 비밀번호가 초기화되었습니다.");
			map.put("url", BASE_URL + "/admin/initPassword");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", user.getName() + " 사용자의 비밀번호 초기화 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", BASE_URL + "/admin/initPassword");
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
	public Map<String, Object> saveUserTableSet(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		List<String> keys = (List<String>) param.get("keys");
		List<String> headers = (List<String>) param.get("headers");
		List<String> styles = (List<String>) param.get("styles");
		List<String> indexs = (List<String>) param.get("indexs");
		String module = (String) param.get("module");

		String[] keysData = new String[keys.size()];
		String[] headersData = new String[headers.size()];
		String[] stylesData = new String[styles.size()];
		String[] indexsData = new String[indexs.size()];

		UserTableSet tableSet = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			boolean isUserTableSet = OrgHelper.manager.isUserTableSet(module);
			if (!isUserTableSet) {
				WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

				for (int i = 0; i < keys.size(); i++) {
					String key = (String) keys.get(i);
					keysData[i] = key;
				}

				for (int i = 0; i < headers.size(); i++) {
					String header = (String) headers.get(i);
					headersData[i] = header;
				}

				for (int i = 0; i < styles.size(); i++) {
					String style = (String) styles.get(i);
					stylesData[i] = style;
				}

				for (int i = 0; i < indexs.size(); i++) {
					String index = (String) indexs.get(i);
					indexsData[i] = index;
				}

				tableSet = UserTableSet.newUserTableSet();
				tableSet.setModule(module);
				tableSet.setKeys(keysData);
				tableSet.setStyles(stylesData);
				tableSet.setHeaders(headersData);
				tableSet.setTabIndex(indexsData);
				tableSet.setWtuser(user);

				PersistenceHelper.manager.save(tableSet);
			} else {
				// 테이블 셋 데이터가 있을경우
				for (int i = 0; i < keys.size(); i++) {
					String key = (String) keys.get(i);
					keysData[i] = key;
				}

				for (int i = 0; i < headers.size(); i++) {
					String header = (String) headers.get(i);
					headersData[i] = header;
				}

				for (int i = 0; i < styles.size(); i++) {
					String style = (String) styles.get(i);
					stylesData[i] = style;
				}

				for (int i = 0; i < indexs.size(); i++) {
					String index = (String) indexs.get(i);
					indexsData[i] = index;
				}

				tableSet = OrgHelper.manager.getUserTableSet(module);
				tableSet.setModule(module);
				tableSet.setKeys(keysData);
				tableSet.setStyles(stylesData);
				tableSet.setHeaders(headersData);
				tableSet.setTabIndex(indexsData);
				PersistenceHelper.manager.modify(tableSet);
			}

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
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
			map.put("result", SUCCESS);
			map.put("msg", "개인 결재선이 저장 되었습니다.");
			map.put("url", "/Windchill/plm/org/addLine?lineType=" + lineType);
		} catch (Exception e) {
			map.put("result", FAIL);
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

			map.put("result", SUCCESS);
			map.put("msg", "개인 결재선이 삭제 되었습니다.");
			map.put("url", "/Windchill/plm/org/addLine?lineType=" + lineType);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
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
	public Map<String, Object> saveUserTableStyle(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		List<Object> cols = (List<Object>) param.get("cols");
		String module = (String) param.get("module");

		String[] colsData = new String[cols.size()];

		UserTableSet tableSet = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			boolean isUserTableSet = OrgHelper.manager.isUserTableSet(module);
			if (!isUserTableSet) {
				WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

				for (int i = 0; i < cols.size(); i++) {
					Object data = (Object) cols.get(i);
					String col = String.valueOf(data);
					colsData[i] = col;
				}

				tableSet = UserTableSet.newUserTableSet();
				tableSet.setCols(colsData);
				tableSet.setModule(module);
				tableSet.setWtuser(user);

				PersistenceHelper.manager.save(tableSet);
			} else {
				// 테이블 셋 데이터가 있을경우
				for (int i = 0; i < cols.size(); i++) {
					Object data = (Object) cols.get(i);
					String col = String.valueOf(data);
					colsData[i] = col;
				}

				tableSet = OrgHelper.manager.getUserTableSet(module);
				tableSet.setCols(colsData);
				tableSet.setModule(module);
				PersistenceHelper.manager.modify(tableSet);
			}

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> saveUserPaging(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		String list = (String) param.get("list"); // 사이즈
		String module = (String) param.get("module");

		UserTableSet tableSet = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			boolean isUserTableSet = OrgHelper.manager.isUserTableSet(module);
			if (!isUserTableSet) {
				WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

				tableSet = UserTableSet.newUserTableSet();
				tableSet.setModule(module);
				tableSet.setWtuser(user);
				tableSet.setPsize(list);

				PersistenceHelper.manager.save(tableSet);
			} else {
				// 테이블 셋 데이터가 있을경우

				tableSet = OrgHelper.manager.getUserTableSet(module);
				tableSet.setPsize(list);
				tableSet.setModule(module);
				PersistenceHelper.manager.modify(tableSet);
			}

			map.put("list", list);
			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
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

				boolean success = OrganizationServicesHelper.manager.addMember(group, user.getUser());
				if (success) {
					System.out.println("퇴사자 그룹에 할당된 사용자 이름 : " + user.getUser().getFullName() + ", 아이디 :  "
							+ user.getUser().getName());
				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "선택한 사용자들의 퇴사 처리가 되었습니다.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
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

			map.put("result", SUCCESS);
			map.put("list", data);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
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
			user.setRank(rank);
			user.setDuty(duty);
			user.setEmail(email + email_prefix);

			wtuser = user.getUser();
			wtuser.setFullName(name);
			wtuser.setEMail(email + email_prefix);
			PersistenceHelper.manager.modify(wtuser);

			user.setUser(wtuser);

			PersistenceHelper.manager.modify(user);

			map.put("result", SUCCESS);
			map.put("msg", "사용자 정보가 수정 되었습니다.");
			map.put("url", "/Windchill/plm/org/viewUser?oid=" + oid);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
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

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
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

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}

		return map;
	}

	@Override
	public Map<String, Object> saveUserTableIndexs(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> indexs = (List<String>) param.get("indexs");
		List<String> texts = (List<String>) param.get("texts");
		String module = (String) param.get("module");
		String[] indexsData = new String[indexs.size()];
		String[] textsData = new String[texts.size()];

		UserTableSet tableSet = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			boolean isUserTableSet = OrgHelper.manager.isUserTableSet(module);
			if (!isUserTableSet) {
				WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

				for (int i = 0; i < indexs.size(); i++) {
					String index = (String) indexs.get(i);
					indexsData[i] = index;
				}

				for (int i = 0; i < texts.size(); i++) {
					String text = (String) texts.get(i);
					textsData[i] = text;
				}

				tableSet = UserTableSet.newUserTableSet();
				tableSet.setHeaders(textsData);
				tableSet.setModule(module);
				tableSet.setKeys(indexsData);
				tableSet.setTabIndex(indexsData);
				tableSet.setWtuser(user);

				PersistenceHelper.manager.save(tableSet);
			} else {
				// 테이블 셋 데이터가 있을경우
				for (int i = 0; i < indexs.size(); i++) {
					String index = (String) indexs.get(i);
					indexsData[i] = index;
				}

				for (int i = 0; i < texts.size(); i++) {
					String text = (String) texts.get(i);
					textsData[i] = text;
				}

				tableSet = OrgHelper.manager.getUserTableSet(module);
				tableSet.setHeaders(textsData);
				tableSet.setKeys(indexsData);
				tableSet.setModule(module);
				tableSet.setTabIndex(indexsData);

				System.out.println("몇번 실행되는지??");

				PersistenceHelper.manager.modify(tableSet);
			}

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}
}