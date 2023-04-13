package e3ps.project.service;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.dto.ProjectDTO;
import e3ps.project.task.Task;
import e3ps.project.task.dto.TaskTreeNode;
import e3ps.project.task.service.TaskHelper;
import e3ps.project.task.variable.TaskStateVariable;
import e3ps.project.template.Template;
import e3ps.project.template.service.TemplateHelper;
import e3ps.project.variable.ProjectStateVariable;
import e3ps.project.variable.ProjectUserTypeVariable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardProjectService extends StandardManager implements ProjectService {

	public static StandardProjectService newStandardProjectService() throws WTException {
		StandardProjectService instance = new StandardProjectService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String kekNumber = (String) params.get("kekNumber");
		String pdate = (String) params.get("pdate");
		String keNumber = (String) params.get("keNumber");
		String userId = (String) params.get("userId");
		String mak = (String) params.get("mak");
		String detail = (String) params.get("detail");
		String model = (String) params.get("model");
		String customer = (String) params.get("customer");
		String install = (String) params.get("install");
		String projectType = (String) params.get("projectType");
		String reference = (String) params.get("reference");
		String description = (String) params.get("description");
		String customDate = (String) params.get("customDate");

		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			Project project = Project.newProject();

			project.setOwnership(ownership);
			project.setKekNumber(kekNumber);
			project.setKeNumber(keNumber);
			project.setPDate(DateUtils.convertDate(pdate));
			project.setUserId(userId);
			project.setMak((CommonCode) CommonUtils.getObject(mak));
			project.setModel(model);
			project.setInstall((CommonCode) CommonUtils.getObject(install));
			project.setProjectType((CommonCode) CommonUtils.getObject(projectType));
			project.setDetail((CommonCode) CommonUtils.getObject(detail));
			project.setKekState(ProjectStateVariable.READY);
			project.setCustomer((CommonCode) CommonUtils.getObject(customer));
			project.setDescription(description);
			project.setCustomDate(DateUtils.convertDate(customDate));
			project.setMachinePrice(0D);
			project.setElecPrice(0D);
			project.setPlanStartDate(DateUtils.getPlanStartDate());
			project.setPlanEndDate(DateUtils.getPlanStartDate());
			PersistenceHelper.manager.save(project);

			System.out.println("reference=" + reference);
			if (!StringUtils.isNull(reference)) {
				Template template = (Template) CommonUtils.getObject(reference);

				Timestamp start = DateUtils.getPlanStartDate();
				project.setPlanStartDate(start);

				Calendar eCa = Calendar.getInstance();
				eCa.setTimeInMillis(start.getTime());
				eCa.add(Calendar.DATE, template.getDuration());

				Timestamp end = new Timestamp(eCa.getTime().getTime());
				project.setPlanEndDate(end);
				project.setTemplate(template);

				project.setDuration(DateUtils.getDuration(start, end));

				project = (Project) PersistenceHelper.manager.modify(project);

				copyTask(project, template);

				WTUser pm = TemplateHelper.manager.getUserType(template, "PM");
				if (pm != null) {
					ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, pm);
					userLink.setUserType(CommonCodeHelper.manager.getCommonCode("PM", "USER_TYPE"));
					PersistenceHelper.manager.save(userLink);
				} else {
					pm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectHelper.PM_ID);
					ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, pm);
					userLink.setUserType(CommonCodeHelper.manager.getCommonCode("PM", "USER_TYPE"));
					PersistenceHelper.manager.save(userLink);
				}

				WTUser subPm = TemplateHelper.manager.getUserType(template, "SUB_PM");
				if (subPm != null) {
					ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, subPm);
					userLink.setUserType(CommonCodeHelper.manager.getCommonCode("SUB_PM", "USER_TYPE"));
					PersistenceHelper.manager.save(userLink);
				} else {
					subPm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectHelper.SUB_PM_ID);
					ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, subPm);
					userLink.setUserType(CommonCodeHelper.manager.getCommonCode("SUB_PM", "USER_TYPE"));
					PersistenceHelper.manager.save(userLink);
				}
			} else {
				Timestamp start = DateUtils.getPlanStartDate();
				// 계획 시작일, 계획 종료일은 등록일로 세팅 한다. 템플릿의 경우 태스크 생성시 일정을 다시 조절한다.
				project.setPlanStartDate(start);

				Calendar eCa = Calendar.getInstance();
				eCa.setTimeInMillis(start.getTime());
				eCa.add(Calendar.DATE, 1);
				Timestamp end = new Timestamp(eCa.getTime().getTime());
				project.setPlanEndDate(end);
				project.setDuration(DateUtils.getDuration(start, end));

				PersistenceHelper.manager.modify(project);
			}

			commit(project);

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

	private void copyTask(Project project, Template copy) throws Exception {

		ArrayList<Task> list = TemplateHelper.manager.recurciveTask(copy);
		HashMap<Task, Task> parentMap = new HashMap<Task, Task>();

		Ownership ownership = CommonUtils.sessionOwner();

		for (int i = 0; i < list.size(); i++) {
			Task orgTask = (Task) list.get(i);

			System.out.println("탯크 어떤거 나옴?=" + orgTask.getName() + ",=" + orgTask.getSort());

			Calendar ca = Calendar.getInstance();

			Template template = orgTask.getTemplate();
			int gap = DateUtils.getDuration(template.getPlanStartDate(), orgTask.getPlanStartDate());
			Task newTask = Task.newTask();

			// 원본 카피
			newTask.setName(orgTask.getName());
			newTask.setDescription(orgTask.getDescription());
			newTask.setAllocate(orgTask.getAllocate());
			newTask.setSort(orgTask.getSort());
			newTask.setDepth(orgTask.getDepth());
			newTask.setDuration(orgTask.getDuration());

			if (orgTask.getName().equals("의뢰서")) {
				newTask.setState(TaskStateVariable.INWORK);
				newTask.setStartDate(DateUtils.getCurrentTimestamp());
			} else {
				newTask.setState(TaskStateVariable.READY); // 대기중으로 생성 프로젝트 스타트와 함께 변경...
			}
			newTask.setTaskType(orgTask.getTaskType());
			newTask.setOwnership(ownership);
			newTask.setUpdateUser(ownership);

			ca.add(Calendar.DATE, gap);
			Timestamp newPlanStartDate = new Timestamp(ca.getTime().getTime());
			newTask.setPlanStartDate(newPlanStartDate);
			ca.add(Calendar.DATE, orgTask.getDuration());

			Timestamp end = new Timestamp(ca.getTime().getTime());

			newTask.setPlanEndDate(end);
			newTask.setProgress(0);
			newTask.setProject(project);
			newTask.setTemplate(null);

			Task parent = (Task) parentMap.get(orgTask.getParentTask());
			newTask.setParentTask(parent);
			newTask = (Task) PersistenceHelper.manager.save(newTask);
			parentMap.put(orgTask, newTask);
		}
	}

	@Override
	public void treeSave(Map<String, Object> params) throws Exception {
		String json = (String) params.get("json");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				Task task = (Task) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(task);
			}

			String parse = new String(DatatypeConverter.parseBase64Binary(json), "UTF-8");
			Type listType = new TypeToken<ArrayList<TaskTreeNode>>() {
			}.getType();

			Gson gson = new Gson();
			List<TaskTreeNode> nodes = gson.fromJson(parse, listType);
			Project project = null;
			for (TaskTreeNode node : nodes) {
				String oid = node.getOid();
				ArrayList<TaskTreeNode> childrens = node.getChildren();
				String name = node.getName();
				String d = node.getDescription();
				project = (Project) CommonUtils.getObject(oid);
				treeSave(project, null, childrens);
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

	private void treeSave(Project project, Task parentTask, ArrayList<TaskTreeNode> childrens) throws Exception {
		Ownership ownership = CommonUtils.sessionOwner();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (TaskTreeNode node : childrens) {
				int depth = node.get_$depth();
				String oid = node.getOid();
				String name = node.getName();
				String description = StringUtils.replaceToValue(node.getDescription(), name);
				int duration = node.getDuration();
				boolean isNew = node.isNew();
				ArrayList<TaskTreeNode> n = node.getChildren();
				int sort = TaskHelper.manager.getSort(project, parentTask);
				int allocate = node.getAllocate();
				String taskType = node.getTaskType();
				Task t = null;
				if (isNew) {
					t = Task.newTask();
					t.setName(name);
					t.setDepth(depth);
					t.setDescription(description);
					t.setDuration(duration);
					t.setAllocate(allocate);
					t.setOwnership(ownership);
					t.setParentTask(parentTask);
					t.setProject(project);
					t.setState(TaskStateVariable.READY);
					t.setPlanStartDate(project.getPlanStartDate());
					t.setPlanEndDate(project.getPlanEndDate());
					t.setDuration(DateUtils.getDuration(project.getPlanStartDate(), project.getPlanEndDate()));
					t.setSort(sort);
					t.setTaskType(CommonCodeHelper.manager.getCommonCode(taskType, "TASK_TYPE"));
					PersistenceHelper.manager.save(t);
				} else {
					t = (Task) CommonUtils.getObject(oid);
					t.setName(name);
					t.setDepth(depth);
					t.setDescription(description);
					t.setDuration(duration);
					t.setAllocate(allocate);
					t.setOwnership(ownership);
					t.setParentTask(parentTask);
					t.setProject(project);
					t.setState(TaskStateVariable.READY);
					t.setPlanStartDate(project.getPlanStartDate());
					t.setPlanEndDate(project.getPlanEndDate());
					t.setDuration(DateUtils.getDuration(project.getPlanStartDate(), project.getPlanEndDate()));
					t.setSort(sort);
					t.setTaskType(CommonCodeHelper.manager.getCommonCode(taskType, "TASK_TYPE"));
					PersistenceHelper.manager.modify(t);
				}
				treeSave(project, t, n);
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
	public void calculation(Project project) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			int kekProgress = ProjectHelper.manager.getKekProgress(project);
			// 완료 되었을 경우...
			if (kekProgress == 100) {
				project.setState(ProjectStateVariable.COMPLETE);
				// 종료일이 없을 경우만 입력해서 중복으로 갱신안되게 처리
				if (project.getEndDate() == null) {
					project.setEndDate(new Timestamp(new Date().getTime()));
				}
				project.setKekState(ProjectStateVariable.KEK_COMPLETE);

				project = (Project) PersistenceHelper.manager.modify(project);
				project = (Project) PersistenceHelper.manager.refresh(project);

				ArrayList<Task> list = ProjectHelper.manager.recurciveTask(project);
				for (Task task : list) {
					task.setState(TaskStateVariable.COMPLETE);
					if (task.getEndDate() == null) {
						task.setEndDate(new Timestamp(new Date().getTime()));
					}
					if (task.getStartDate() == null) {
						task.setStartDate(new Timestamp(new Date().getTime()));
					}
					task.setProgress(100);
					PersistenceHelper.manager.modify(task);
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
	public synchronized void commit(Project project) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			ProjectHelper.manager.calculation(project);
			ArrayList<Task> list = ProjectHelper.manager.recurciveTask(project);

			// 자식 태스크 계산하여 날짜 변경
			TaskHelper.service.calculation(list);

			// 기간 계산
			Timestamp start = null;
			Timestamp end = null;
			boolean edit = false;
			int duration = 1;
			// 태스크 모두 삭제 될 경우
			if (list.size() == 0) {
				// 계획 시작일 계획 종료일 동일
				start = DateUtils.getPlanStartDate();
				end = DateUtils.getPlanEndDate();
				project.setPlanStartDate(start);
				project.setPlanEndDate(end);
				project.setDuration(1);
			}

			for (int i = list.size() - 1; i >= 0; i--) {
				Task child = (Task) list.get(i);

				Timestamp cstart = child.getPlanStartDate();
				Timestamp cend = child.getPlanEndDate();

				if (start == null || (start.getTime() > cstart.getTime())) {
					start = cstart;
					edit = true;
				}

				if (end == null || (end.getTime() < cend.getTime())) {
					end = cend;
					edit = true;
				}
			}

			if (edit) {
				project.setPlanStartDate(start);
				project.setPlanEndDate(end);
				duration = DateUtils.getDuration(start, end);
				project.setDuration(duration);
			}

			int kekProgress = ProjectHelper.manager.getKekProgress(project);
			project.setProgress(kekProgress);
			PersistenceHelper.manager.modify(project);

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
	public void save(HashMap<String, List<ProjectDTO>> dataMap) throws Exception {
		List<ProjectDTO> editRows = dataMap.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();
			for (ProjectDTO edit : editRows) {
				String oid = edit.getOid();
				Double elecPrice = edit.getElecPrice();
				Double machinePrice = edit.getMachinePrice();

				Project project = (Project) CommonUtils.getObject(oid);
//				WTUser wtuser = null;
				String soft = edit.getSoft();
				String elec = edit.getElec();
				String machine = edit.getMachine();

//				WTUser machineUser = ProjectHelper.manager.getUserType(project, TaskTypeVariable.MACHINE);
//				WTUser softUser = ProjectHelper.manager.getUserType(project, TaskTypeVariable.SOFT);
//				WTUser elecUser = ProjectHelper.manager.getUserType(project, TaskTypeVariable.ELEC);
				WTUser machineUser = (WTUser) CommonUtils.getObject(machine);
				WTUser softUser = (WTUser) CommonUtils.getObject(soft);
				WTUser elecUser = (WTUser) CommonUtils.getObject(elec);

//				wtuser.setFullName(soft);
//				wtuser.setFullName(machine);
//				wtuser.setFullName(elec);
				project.setElecPrice(elecPrice);
				project.setMachinePrice(machinePrice);
//				edit.setSoft(soft);
//				edit.setElec(elec);
//				edit.setMachine(machine);
//				System.out.println(machineUser.getName());
//				machineUser.setFullName(machine);

				machineUser.setName(machine);
				softUser.setFullName(soft);
				elecUser.setFullName(elec);
				PersistenceHelper.manager.modify(project);
//				System.out.println(machineUser.getName());
//				System.out.println("저장 후 이름 나오나" + machineUser + elecUser + softUser);
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
	public void editUser(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String pmOid = (String) params.get("pmOid");
		String subPmOid = (String) params.get("subPmOid");
		String machineOid = (String) params.get("machineOid");
		String elecOid = (String) params.get("elecOid");
		String softOid = (String) params.get("softOid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Project project = (Project) CommonUtils.getObject(oid);

			// 기존링크 다 삭제

			QueryResult qr = PersistenceHelper.manager.navigate(project, "user", ProjectUserLink.class, false);
			while (qr.hasMoreElements()) {
				ProjectUserLink link = (ProjectUserLink) qr.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			if (!StringUtils.isNull(pmOid)) {
				WTUser pm = (WTUser) CommonUtils.getObject(pmOid);
				ProjectUserLink newLink = ProjectUserLink.newProjectUserLink(project, pm);
				newLink.setUserType(CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.PM, "USER_TYPE"));
				PersistenceHelper.manager.save(newLink);
			}

			if (!StringUtils.isNull(subPmOid)) {
				WTUser subPm = (WTUser) CommonUtils.getObject(subPmOid);
				ProjectUserLink newLink = ProjectUserLink.newProjectUserLink(project, subPm);
				newLink.setUserType(
						CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.SUB_PM, "USER_TYPE"));
				PersistenceHelper.manager.save(newLink);
			}

			if (!StringUtils.isNull(machineOid)) {
				WTUser machine = (WTUser) CommonUtils.getObject(machineOid);
				ProjectUserLink newLink = ProjectUserLink.newProjectUserLink(project, machine);
				newLink.setUserType(
						CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.MACHINE, "USER_TYPE"));
				PersistenceHelper.manager.save(newLink);
			}

			if (!StringUtils.isNull(elecOid)) {
				WTUser elec = (WTUser) CommonUtils.getObject(elecOid);
				ProjectUserLink newLink = ProjectUserLink.newProjectUserLink(project, elec);
				newLink.setUserType(CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.ELEC, "USER_TYPE"));
				PersistenceHelper.manager.save(newLink);
			}

			if (!StringUtils.isNull(softOid)) {
				WTUser soft = (WTUser) CommonUtils.getObject(softOid);
				ProjectUserLink newLink = ProjectUserLink.newProjectUserLink(project, soft);
				newLink.setUserType(CommonCodeHelper.manager.getCommonCode(ProjectUserTypeVariable.SOFT, "USER_TYPE"));
				PersistenceHelper.manager.save(newLink);
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
	public void money(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String type = (String) params.get("type");
		int money = (int) params.get("money");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Project project = (Project) CommonUtils.getObject(oid);
			if ("m".equals(type)) {
				project.setMachinePrice((double) money);
			} else if ("e".equals(type)) {
				project.setElecPrice((double) money);
			}
			PersistenceHelper.manager.modify(project);

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