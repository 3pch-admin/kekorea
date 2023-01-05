package e3ps.load.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import e3ps.common.code.CommonCode;
import e3ps.common.code.CommonCodeType;
import e3ps.common.db.DBCPManager;
import e3ps.common.util.DateUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.E3PSDocumentMaster;
import e3ps.epm.service.EpmHelper;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.org.WTUserPeopleLink;
import e3ps.org.service.OrgHelper;
import e3ps.project.Output;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.Task;
import e3ps.project.enums.ProjectUserType;
import e3ps.project.enums.TaskStateType;
import wt.epm.EPMDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardLoadService extends StandardManager implements LoadService {

	private static final long serialVersionUID = 1649329587211994755L;

	public static StandardLoadService newStandardLoadService() throws WTException {
		StandardLoadService instance = new StandardLoadService();
		instance.initialize();
		return instance;
	}

	@Override
	public boolean loadDepartmentFromExcel(HashMap<String, Object> map) throws WTException {
		SessionContext pre = SessionContext.newContext();
		boolean isExist = false;
		Transaction trs = new Transaction();
		String code = (String) map.get("code");
		String pcode = (String) map.get("pcode");
		String name = (String) map.get("name");
		String sort = (String) map.get("sort");
		String depth = (String) map.get("depth");
		Department department = null;
		SessionHelper.manager.setAdministrator();
		try {
			trs.start();

			Department root = OrgHelper.manager.getRoot();
			if (root == null) {
				OrgHelper.service.makeRoot();
			}

			if (OrgHelper.manager.isDepartment(code)) {
				isExist = true;
			} else {
				department = Department.newDepartment();
				department.setCode(code);
				department.setName(name);
				department.setSort(Integer.parseInt(sort));
				department.setDepth(Integer.parseInt(depth));

				Department parent = OrgHelper.manager.getDepartment(pcode);
				if (parent != null) {
					department.setParent(parent);
				}
				department = (Department) PersistenceHelper.manager.save(department);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(pre);
		}
		return isExist;
	}

	@Override
	public boolean loadUserFromExcel(HashMap<String, Object> map) throws WTException {
		SessionContext pre = SessionContext.newContext();
		boolean isExist = false;
		Transaction trs = new Transaction();
		String name = (String) map.get("name");
		String email = (String) map.get("email");
		String id = (String) map.get("id");
		String duty = (String) map.get("duty");
		String departmentName = (String) map.get("department");

		People user = null;
		WTUser wtuser = null;
		Department department = null;
		SessionHelper.manager.setAdministrator();
		try {
			trs.start();

			department = OrgHelper.manager.getDepartmentByName(departmentName);

			System.out.println("department=" + department);

			wtuser = OrganizationServicesHelper.manager.getAuthenticatedUser(id.trim());
			System.out.println("wt=User+" + wtuser);
			if (wtuser == null) {
				// 사용자 존재 안함
				return false;
			} else {
				// user 객체 찾기

				wtuser.setEMail(email);
				wtuser.setFullName(name);

				QueryResult result = PersistenceHelper.manager.navigate(wtuser, "people", WTUserPeopleLink.class);
				if (result.hasMoreElements()) {
					user = (People) result.nextElement();
					user.setEmail(email);
					user.setId(id);
					user.setDuty(duty);
					user.setRank(duty);
					user.setDepartment(department);
					PersistenceHelper.manager.modify(user);
				} else {
					// 없으면 생성..?

					user = People.newPeople();
					user.setId(id);
					user.setEmail(email);
					user.setDuty(duty);
					user.setRank(duty);
					user.setName(name);
					user.setDepartment(department);
					user.setUser(wtuser);
					PersistenceHelper.manager.save(user);
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
			SessionContext.setContext(pre);
		}
		return isExist;
	}

	@Override
	public boolean loadCommonCodeFromExcel(HashMap<String, Object> map) throws WTException {
		SessionContext pre = SessionContext.newContext();
		boolean isExist = false;
		Transaction trs = new Transaction();

		String name = (String) map.get("name");
		String code = (String) map.get("code");
		String description = (String) map.get("description");
		String codeType = (String) map.get("codeType");

		SessionHelper.manager.setAdministrator();

		CommonCodeType commonCodeType = CommonCodeType.toCommonCodeType(codeType);

		CommonCode commonCode = null;
		try {
			trs.start();

			commonCode = CommonCode.newCommonCode();
			commonCode.setName(name);
			commonCode.setCode(code);
			commonCode.setDescription(description);
			commonCode.setCodeType(commonCodeType);
			PersistenceHelper.manager.save(commonCode);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(pre);
		}
		return isExist;

	}

	@Override
	public void setYCode(HashMap<String, Object> map) throws WTException {
		Transaction trs = new Transaction();
		String number = (String) map.get("number");
		String ycode = (String) map.get("ycode");

		SessionContext prev = SessionContext.newContext();
		ReferenceFactory rf = new ReferenceFactory();
//		Timestamp start = new Timestamp(new Date().getTime());
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();

			con = DBCPManager.getConnection("local");

			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT A0.IDA2A2 FROM EPMDocument A0,EPMDocumentMaster A1 ");
			sql.append(
					"WHERE (A0.statecheckoutInfo = 'c/i') AND (A0.idA3masterReference = A1.idA2A2)  AND (A0.latestiterationInfo = 1) ");
			sql.append("AND (A1.documentNumber = '" + number + "') ");
			sql.append("AND (A0.versionSortIdA2versionInfo = (SELECT MAX(versionSortIdA2versionInfo) ");
			sql.append("FROM EPMDocument WHERE A0.IDA3MASTERREFERENCE = IDA3MASTERREFERENCE)) ");
			sql.append("ORDER BY A0.modifyStampA2 DESC");

			rs = st.executeQuery(sql.toString());

			if (rs.next()) {
				String oid = (String) rs.getString(1);

				EPMDocument epm = (EPMDocument) rf.getReference("wt.epm.EPMDocument:" + oid).getObject();

				System.out.println("3D 변경=" + epm.getNumber());
				IBAUtils.deleteIBA(epm, "PART_CODE", "s");
				IBAUtils.createIBA(epm, "s", "PART_CODE", ycode);

				EPMDocument ee = EpmHelper.manager.getEPM2D(epm);

				if (ee != null) {
					IBAUtils.deleteIBA(ee, "PART_CODE", "s");
					IBAUtils.createIBA(ee, "s", "PART_CODE", ycode);
				}

				WTPart pp = EpmHelper.manager.getPart(epm);

				if (pp != null) {
					IBAUtils.deleteIBA(pp, "PART_CODE", "s");
					IBAUtils.createIBA(pp, "s", "PART_CODE", ycode);
				}
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			DBCPManager.freeConnection(con, st, rs);
			e.printStackTrace();
			trs.rollback();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
	}

	@Override
	public Project loadProjectFromExcel(HashMap<String, Object> map) throws WTException {
//		boolean isSuccess = true;
		Transaction trs = new Transaction();
		String customer = (String) map.get("customer");
		String description = (String) map.get("description");
//		String equipment = (String) map.get("equipment");
		String line = (String) map.get("line");
		String process = (String) map.get("process");
		String itemcode = (String) map.get("itemcode");
		String keNumber = (String) map.get("keNumber");
		String model = (String) map.get("model");
		String kekNumber = (String) map.get("kekNumber");

		String userid = (String) map.get("userid");
		String planEndDate = (String) map.get("planEndDate");
		String planStartDate = (String) map.get("planStartDate");
		String startDate = (String) map.get("startDate");
//		String endDate = (String) map.get("endDate");

		String CREATESTAMPA2 = (String) map.get("CREATESTAMPA2");

		SessionContext prev = SessionContext.newContext();
		Project project = null;
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();

			project = Project.newProject();

			project.setPType(itemcode);
			project.setMak(process);
			project.setUserId(userid);
			project.setKekState("설계완료");
			project.setCustomer(customer);
			project.setIns_location(line); // ke
			project.setKekNumber(kekNumber);
			project.setKeNumber(keNumber);
			project.setModel(model);
			project.setDescription(description);

			project.setPlanEndDate(DateUtils.convertDate(planEndDate));
			project.setPlanStartDate(DateUtils.convertDate(planStartDate));
//			project.setEndDate(DateUtils.convertDate(endDate));
			project.setStartDate(DateUtils.convertDate(startDate));

			project.setPDate(DateUtils.convertDate(CREATESTAMPA2));

			project = (Project) PersistenceHelper.manager.save(project);

			WTUser pm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectUserType.PM_ID);
			ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, pm);
			userLink.setUserType(ProjectUserType.PM.name());
			PersistenceHelper.manager.save(userLink);

			WTUser subPm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectUserType.SUB_PM_ID);
			ProjectUserLink userLinks = ProjectUserLink.newProjectUserLink(project, subPm);
			userLinks.setUserType(ProjectUserType.SUB_PM.name());
			PersistenceHelper.manager.save(userLinks);

			project = (Project) PersistenceHelper.manager.refresh(project);

			trs.commit();
			trs = null;
		} catch (Exception e) {
//			isSuccess = false;
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			SessionContext.setContext(prev);
		}
		return project;
	}

	@Override
	public Task loadTaskFromExcel(HashMap<String, Object> map, Project project) throws WTException {
		Transaction trs = new Transaction();
		String name = (String) map.get("name");
		String progress = (String) map.get("progress");

		int pro = 0;
		if (!StringUtils.isNull(progress)) {
			pro = Integer.parseInt(progress);
		}

		SessionContext prev = SessionContext.newContext();
		Task task = null;
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();

			task = Task.newTask();

			task.setTaskType("일반");
			task.setName(name);
			task.setDescription(name);
			task.setDepth(1);
			task.setAllocate(0);
			task.setProgress(pro);

			if (pro == 100) {
				task.setState(TaskStateType.COMPLETE.getDisplay());
			} else if (pro == 0) {
				task.setState(TaskStateType.STAND.getDisplay());
			} else if (pro > 0 && pro < 100) {
				task.setState(TaskStateType.INWORK.getDisplay());
			}

			task.setProject(project);

			task = (Task) PersistenceHelper.manager.save(task);

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
		return task;
	}

	@Override
	public Task setPlanTaskDate(Task task, HashMap<String, Object> map) throws WTException {
		Transaction trs = new Transaction();

		String planEndDate = (String) map.get("planEndDate");
		String planStartDate = (String) map.get("planStartDate");
		String startDate = (String) map.get("startDate");
		String endDate = (String) map.get("endDate");

		SessionContext prev = SessionContext.newContext();
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();

			task = (Task) PersistenceHelper.manager.refresh(task);

			task.setPlanEndDate(DateUtils.convertDate(planEndDate));
			task.setPlanStartDate(DateUtils.convertDate(planStartDate));
			task.setEndDate(DateUtils.convertDate(endDate));
			task.setStartDate(DateUtils.convertDate(startDate));

			task = (Task) PersistenceHelper.manager.modify(task);

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
		return task;
	}

	@Override
	public Output loadOutputFromExcel(HashMap<String, Object> map, Project projects) throws WTException {
		Transaction trs = new Transaction();

		String name = (String) map.get("name");

		Output output = null;

		SessionContext prev = SessionContext.newContext();
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();

			output = Output.newOutput();

			output.setName(name);
			output.setDescription(name);
			output.setProject(projects);

			PersistenceHelper.manager.save(output);

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
		return output;
	}

	@Override
	public Task setOutput(Task task, Output output, String oid) throws WTException {
		Transaction trs = new Transaction();
		ReferenceFactory rf = new ReferenceFactory();

		System.out.println("oid=" + oid);

//		WTDocument doc = null;

		E3PSDocumentMaster mm = null;

		SessionContext prev = SessionContext.newContext();
		try {
			trs.start();
			SessionHelper.manager.setAdministrator();

			mm = (E3PSDocumentMaster) rf.getReference(oid).getObject();

			task = (Task) PersistenceHelper.manager.refresh(task);
			output = (Output) PersistenceHelper.manager.refresh(output);

			output.setTask(task);
			output.setMaster(mm);

			System.out.println("mmm=" + output.getMaster());

			PersistenceHelper.manager.modify(output);

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
		return task;
	}
}
