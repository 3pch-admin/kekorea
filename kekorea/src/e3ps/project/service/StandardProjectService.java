package e3ps.project.service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
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
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.StringUtils;
import e3ps.doc.ReqDocumentProjectLink;
import e3ps.doc.RequestDocument;
import e3ps.doc.beans.DocumentViewData;
import e3ps.org.People;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.beans.ProjectViewData;
import e3ps.project.enums.ProjectStateType;
import e3ps.project.enums.ProjectUserType;
import e3ps.project.enums.TaskStateType;
import e3ps.project.issue.Issue;
import e3ps.project.issue.IssueProjectLink;
import e3ps.project.output.Output;
import e3ps.project.task.ParentTaskChildTaskLink;
import e3ps.project.task.TargetTaskSourceTaskLink;
import e3ps.project.task.Task;
import e3ps.project.task.beans.TaskTreeNode;
import e3ps.project.task.service.TaskHelper;
import e3ps.project.task.variable.TaskStateVariable;
import e3ps.project.template.Template;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.OrganizationServicesHelper;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.session.SessionContext;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardProjectService extends StandardManager implements ProjectService, MessageHelper {

	public static StandardProjectService newStandardProjectService() throws WTException {
		StandardProjectService instance = new StandardProjectService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> createProjectAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String kekNumber = (String) param.get("kekNumber");
		String pDate = (String) param.get("postdate");
		String keNumber = (String) param.get("keNumber");
		String userId = (String) param.get("userId");

		String mak = (String) param.get("mak");
		String model = (String) param.get("model");
		String customer = (String) param.get("customer");
		String install = (String) param.get("install");
		String detail = (String) param.get("detail");
		String projectType = (String) param.get("projectType");
		String pTemplate = (String) param.get("pTemplate");
		String description = (String) param.get("description");

		String customDate = (String) param.get("postdate_m");
		String systemInfo = (String) param.get("systemInfo");

		Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());
		Project project = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			project = Project.newProject();

			project.setOwnership(ownership);
			project.setKekNumber(kekNumber);
			project.setKeNumber(keNumber);
			project.setPDate(DateUtils.convertDate(pDate));
			project.setUserId(userId);
			project.setMak((CommonCode) CommonUtils.getObject(mak));
			project.setDetail((CommonCode) CommonUtils.getObject(detail));
			project.setModel(model);
			project.setInstall((CommonCode) CommonUtils.getObject(install));
			project.setProjectType((CommonCode) CommonUtils.getObject(projectType));
			project.setKekState("준비");
			project.setCustomer((CommonCode) CommonUtils.getObject(customer));
			project.setDescription(description);
			project.setCustomDate(DateUtils.convertDate(customDate));
			project.setMachinePrice(0D);
			project.setElecPrice(0D);

			PersistenceHelper.manager.save(project);

			if (!StringUtils.isNull(pTemplate)) {
				Template template = TemplateHelper.manager.getTemplateByName(pTemplate);

				if (!StringUtils.isNull(template)) {

					Timestamp start = DateUtils.getPlanStartDate();
					// 계획 시작일, 계획 종료일은 등록일로 세팅 한다. 템플릿의 경우 태스크 생성시 일정을 다시 조절한다.
					project.setPlanStartDate(start);

					Calendar eCa = Calendar.getInstance();
					eCa.setTimeInMillis(start.getTime());
					eCa.add(Calendar.DATE, template.getDuration());

					Timestamp end = new Timestamp(eCa.getTime().getTime());
					project.setPlanEndDate(end);

					project.setTemplate(template);

					project = (Project) PersistenceHelper.manager.modify(project);

					copyTasks(project, template, null);
					// 없을경우

					WTUser pm = TemplateHelper.manager.getPMByTemplate(template);
					if (pm != null) {
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, pm);
						userLink.setUserType(ProjectUserType.PM.name());
						PersistenceHelper.manager.save(userLink);
					} else {
						pm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectUserType.PM_ID);
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, pm);
						userLink.setUserType(CommonCodeHelper.manager.getCommonCode("PM", "USER_TYPE"));
						PersistenceHelper.manager.save(userLink);
					}

					WTUser subPm = TemplateHelper.manager.getSubPMByTemplate(template);
					if (subPm != null) {
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, subPm);
						userLink.setUserType(ProjectUserType.SUB_PM.name());
						PersistenceHelper.manager.save(userLink);
					} else {
						subPm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectUserType.SUB_PM_ID);
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, subPm);
						userLink.setUserType(ProjectUserType.SUB_PM.name());
						PersistenceHelper.manager.save(userLink);
					}
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

				PersistenceHelper.manager.modify(project);
			}

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "작번 " + CREATE_OK);
			map.put("url", "/Windchill/plm/project/listProject");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "작번 " + CREATE_FAIL);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void createProjectByJExcels(RequestDocument reqDoc, Map<String, Object> param) throws WTException {
		List<ArrayList<String>> jexcels = (List<ArrayList<String>>) param.get("jexcels");
		Project project = null;
		Transaction trs = new Transaction();
		String pTemplate = (String) param.get("pTemplate");
		ReferenceFactory rf = new ReferenceFactory();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			Timestamp planStartDate = new Timestamp(new Date().getTime());
			Timestamp planEndDate = new Timestamp(new Date().getTime());

			for (int i = 0; i < jexcels.size(); i++) {
				project = Project.newProject();

				boolean isCreate = true;
				ArrayList<String> cells = (ArrayList<String>) jexcels.get(i);
				// for (int k = 0; k < cells.size(); k++) {
				String pType = cells.get(1);
				String customer = cells.get(2);
				String ins_location = cells.get(3);
				String mak = cells.get(4);
				String kekNumber = cells.get(5);
				String keNumber = cells.get(6);
				String userId = cells.get(7);
				String customDate = cells.get(8);
				String description = cells.get(9);
				String model = cells.get(10);
				// String systemInfo = cells.get(11);
				String pDate = cells.get(11);
				String elec = cells.get(12);
				String machine = cells.get(13);
				String sw = cells.get(14);
				// 전기담당자
				// 기계담당자
				// sw담당자
				// 템플릿

				if (StringUtils.isNull(kekNumber)) {
					isCreate = false;
					continue;
				}

				project.setOwnership(ownership);
				project.setKekNumber(kekNumber);

				if (StringUtils.isNull(pDate)) {
					project.setPDate(new Timestamp(new Date().getTime()));
				} else {
					project.setPDate(DateUtils.convertDate(pDate));
				}
				project.setKeNumber(keNumber);
				project.setUserId(userId);
				project.setMak(mak);
				project.setModel(model);
				project.setCustomer(customer);
				project.setIns_location(ins_location);
				project.setPType(pType);
				project.setDescription(description);
				project.setCustomDate(DateUtils.convertDate(customDate));
				project.setProgress(0);
				project.setState(ProjectStateType.STAND.getDisplay());
				project.setKekState("준비");
				project.setMachinePrice(0D);
				project.setElecPrice(0D);

				if (isCreate) {

					// 일단 일정 기본 설정
					Timestamp start = new Timestamp(new Date().getTime());
					//
					Calendar ca = Calendar.getInstance();
					ca.setTime(start);
					ca.add(Calendar.DATE, 1);
					Timestamp end = new Timestamp(ca.getTime().getTime());

					project.setPlanStartDate(start);
					project.setPlanEndDate(end);

					project = (Project) PersistenceHelper.manager.save(project);

					// task..name의뢰서

					if (!StringUtils.isNull(elec)) {
						WTUser elecUser = OrganizationServicesHelper.manager.getAuthenticatedUser(elec.trim());
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, elecUser);
						userLink.setUserType(ProjectUserType.ELEC.name());
						PersistenceHelper.manager.save(userLink);
					}

					if (!StringUtils.isNull(machine)) {
						WTUser machineUser = OrganizationServicesHelper.manager.getAuthenticatedUser(machine.trim());
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, machineUser);
						userLink.setUserType(ProjectUserType.MACHINE.name());
						PersistenceHelper.manager.save(userLink);
					}

					if (!StringUtils.isNull(sw)) {
						WTUser swUser = OrganizationServicesHelper.manager.getAuthenticatedUser(sw.trim());
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, swUser);
						userLink.setUserType(ProjectUserType.SOFT.name());
						PersistenceHelper.manager.save(userLink);
					}

					// 템플릿이 있을경우

					if (!StringUtils.isNull(pTemplate)) {
						// Template template = TemplateHelper.manager.getTemplateByName(pTemplate);
						Template template = (Template) rf.getReference(pTemplate).getObject();

						if (!StringUtils.isNull(template)) {
							copyTasks(project, template, reqDoc);
							// 없을경우

							WTUser pm = TemplateHelper.manager.getPMByTemplate(template);
							if (pm != null) {
								ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, pm);
								userLink.setUserType(ProjectUserType.PM.name());
								PersistenceHelper.manager.save(userLink);
							} else {
								pm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectUserType.PM_ID);
								ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, pm);
								userLink.setUserType(ProjectUserType.PM.name());
								PersistenceHelper.manager.save(userLink);
							}

							WTUser subPm = TemplateHelper.manager.getSubPMByTemplate(template);
							if (subPm != null) {
								ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, subPm);
								userLink.setUserType(ProjectUserType.SUB_PM.name());
								PersistenceHelper.manager.save(userLink);
							} else {
								subPm = OrganizationServicesHelper.manager
										.getAuthenticatedUser(ProjectUserType.SUB_PM_ID);
								ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, subPm);
								userLink.setUserType(ProjectUserType.SUB_PM.name());
								PersistenceHelper.manager.save(userLink);
							}
						} else {

							Task task = Task.newTask();

							task.setName("의뢰서");
							task.setDepth(1);
							task.setDescription("의뢰서 태스크");
							task.setSort(0);
							task.setProject(project);
							task.setTemplate(null);
							task.setPlanStartDate(planStartDate);
							task.setPlanEndDate(planEndDate);
							task.setStartDate(planStartDate);
							task.setEndDate(null);
							task.setDuration(1);
							task.setTaskType("일반");
							task.setOwnership(ownership);
							task.setProgress(0);
							// task.setState("작업 중");
							task.setState(TaskStateType.INWORK.getDisplay());
							task.setAllocate(0);

							task = (Task) PersistenceHelper.manager.save(task);

							Output output = Output.newOutput();
							output.setName(reqDoc.getName());
							output.setLocation(reqDoc.getLocation());
							output.setTask(task);
							output.setProject(project);
							output.setDocument(reqDoc);
							output.setOwnership(ownership);
							output = (Output) PersistenceHelper.manager.save(output);

							// pm 연결
							WTUser pm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectUserType.PM_ID);
							ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, pm);
							userLink.setUserType(ProjectUserType.PM.name());
							PersistenceHelper.manager.save(userLink);

							WTUser subPm = OrganizationServicesHelper.manager
									.getAuthenticatedUser(ProjectUserType.SUB_PM_ID);
							ProjectUserLink links = ProjectUserLink.newProjectUserLink(project, subPm);
							links.setUserType(ProjectUserType.SUB_PM.name());
							PersistenceHelper.manager.save(links);
						}
					} else {

						Task task = Task.newTask();

						task.setName("의뢰서");
						task.setDepth(1);
						task.setDescription("의뢰서 태스크");
						task.setSort(0);
						task.setProject(project);
						task.setTemplate(null);
						task.setPlanStartDate(planStartDate);
						task.setPlanEndDate(planEndDate);
						task.setStartDate(planStartDate);
						task.setEndDate(null);
						task.setDuration(1);
						task.setTaskType("일반");
						task.setOwnership(ownership);
						task.setProgress(0);
						// task.setState("작업 중");
						task.setState(TaskStateType.INWORK.getDisplay());
						task.setAllocate(0);

						task = (Task) PersistenceHelper.manager.save(task);

						Output output = Output.newOutput();
						output.setName(reqDoc.getName());
						output.setLocation(reqDoc.getLocation());
						output.setTask(task);
						output.setProject(project);
						output.setDocument(reqDoc);
						output.setOwnership(ownership);
						output = (Output) PersistenceHelper.manager.save(output);

						// pm 연결
						WTUser pm = OrganizationServicesHelper.manager.getAuthenticatedUser(ProjectUserType.PM_ID);
						ProjectUserLink userLink = ProjectUserLink.newProjectUserLink(project, pm);
						userLink.setUserType(ProjectUserType.PM.name());
						PersistenceHelper.manager.save(userLink);

						WTUser subPm = OrganizationServicesHelper.manager
								.getAuthenticatedUser(ProjectUserType.SUB_PM_ID);
						ProjectUserLink links = ProjectUserLink.newProjectUserLink(project, subPm);
						links.setUserType(ProjectUserType.SUB_PM.name());
						PersistenceHelper.manager.save(links);
					}

					ReqDocumentProjectLink link = ReqDocumentProjectLink.newReqDocumentProjectLink(reqDoc, project);
					PersistenceHelper.manager.save(link);
				}

				commit(project);
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
	}

	@Override
	public void copyTasks(Project project, Template template) throws WTException {
		copyTasks(project, template, null);
	}

	@Override
	public void copyTasks(Project project, Template template, WTDocument document) throws WTException {
		ArrayList<Task> list = new ArrayList<Task>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			list = TemplateHelper.manager.getterTemplateTask(template, list);

			HashMap<Task, Task> parentMap = new HashMap<Task, Task>();

			// for (int i = list.size() - 1; i >= 0; i--) {
			for (int i = 0; i < list.size(); i++) {
				Task orgTask = (Task) list.get(i);

				Calendar ca = Calendar.getInstance();

				Template orgTemplate = orgTask.getTemplate();

				Timestamp rTime = DateUtils
						.convertStartDate(orgTemplate.getPlanStartDate().toString().substring(0, 10));

				int gap = DateUtils.getDuration(rTime, orgTask.getPlanStartDate());

				Task newTask = Task.newTask();

				// 원본 카피
				newTask.setName(orgTask.getName());
				newTask.setDescription(orgTask.getDescription());
				newTask.setAllocate(orgTask.getAllocate());
				newTask.setSort(orgTask.getSort());
				newTask.setDepth(orgTask.getDepth());
				newTask.setDuration(orgTask.getDuration());

				if (orgTask.getName().equals("의뢰서")) {
					newTask.setState(TaskStateType.INWORK.getDisplay());
					newTask.setStartDate(DateUtils.getCurrentTimestamp());
				} else {
					newTask.setState(TaskStateType.STAND.getDisplay()); // 대기중으로 생성 프로젝트 스타트와 함께 변경...
				}
				newTask.setTaskType(orgTask.getTaskType());

				newTask.setOwnership(ownership);

				// 프로젝트 생성일로

				ca.add(Calendar.DATE, gap);
				Timestamp newPlanStartDate = new Timestamp(ca.getTime().getTime());

				// newTask.setPlanStartDate(project.getCreateTimestamp());
				newTask.setPlanStartDate(newPlanStartDate);

				// Calendar eCa = Calendar.getInstance();
				// eCa.setTimeInMillis(project.getCreateTimestamp().getTime());
				ca.add(Calendar.DATE, orgTask.getDuration());

				Timestamp end = new Timestamp(ca.getTime().getTime());

				newTask.setPlanEndDate(end);

				// 진행율
				newTask.setProgress(0);

				newTask.setProject(project);
				newTask.setTemplate(null);

				// 모자 관계
				Task parent = (Task) parentMap.get(orgTask.getParentTask());
				newTask.setParentTask(parent);
				newTask = (Task) PersistenceHelper.manager.save(newTask);

				parentMap.put(orgTask, newTask);

				if (document != null)
					if (newTask.getName().trim().equals("의뢰서")) {
						Output output = Output.newOutput();
						output.setName(document.getName());
						output.setLocation(document.getLocation());
						output.setTask(newTask);
						output.setProject(project);
						output.setDocument(document);
						output.setOwnership(ownership);
						output = (Output) PersistenceHelper.manager.save(output);
					}
			}

			for (int i = 0; i < list.size(); i++) {
				Task orgTask = (Task) list.get(i);
				Task newTask = (Task) parentMap.get(orgTask);

				// ArrayList<TargetTaskSourceTaskLink> targetList = TemplateHelper.manager
				// .getTargetTaskSourceTaskLinkByTarget(orgTask);

				QueryResult result = PersistenceHelper.manager.navigate(orgTask, "targetTask",
						TargetTaskSourceTaskLink.class, false);

				while (result.hasMoreElements()) {
					TargetTaskSourceTaskLink ll = (TargetTaskSourceTaskLink) result.nextElement();
					Task targetTask = (Task) ll.getTargetTask();
					Task newPreTask = (Task) parentMap.get(targetTask);
					TargetTaskSourceTaskLink link = TargetTaskSourceTaskLink.newTargetTaskSourceTaskLink(newPreTask,
							newTask);
					link.setLag(ll.getLag() != null ? ll.getLag() : 0);
					PersistenceHelper.manager.save(link);
				}
			}

			PersistenceHelper.manager.modify(project);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public Map<String, Object> addProjectAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		Transaction trs = new Transaction();
		try {
			trs.start();
			List<String> list = (List<String>) param.get("list");
			Project project = null;
			ReferenceFactory rf = new ReferenceFactory();
			ArrayList<String[]> data = new ArrayList<String[]>();

			for (String oid : list) {
				project = (Project) rf.getReference(oid).getObject();

				ProjectViewData pdata = new ProjectViewData(project);

				// 0 oid, 1 number, 2 name, 3 version, 4 state, 5 creator, 6 createdate
				String[] s = new String[] { pdata.oid, pdata.kek_number, pdata.ke_number, pdata.state, pdata.createDate,
						pdata.iconPath, pdata.customer, pdata.mak, pdata.description, pdata.pType };
				data.add(s);
			}

			map.put("result", SUCCESS);
			map.put("list", data);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "작번 추가 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/project/addProject");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> addOutputAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		String toid = (String) param.get("toid");
		WTDocument document = null;
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<String[]> data = new ArrayList<String[]>();
		Task task = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			task = (Task) rf.getReference(toid).getObject();
			for (String oid : list) {
				document = (WTDocument) rf.getReference(oid).getObject();
				DocumentViewData ddata = new DocumentViewData(document);
				// 0 oid, 1 number, 2 name, 3 version, 4 state, 5 creator, 6 createdate
				String[] s = new String[] { ddata.oid, ddata.number, ddata.name, ddata.state + "$" + ddata.stateKey,
						ddata.version + "." + ddata.iteration, ddata.modifier, ddata.modifyDate, ddata.iconPath,
						ddata.location, ddata.createDate, ddata.creator, ddata.primary[5], ddata.primary[4] };
				data.add(s);

				boolean isDuplicate = ProjectHelper.manager.isDuplicateOutput(document, task);
				if (!isDuplicate) {

					Output output = Output.newOutput();
					output.setName(document.getName());
					output.setLocation(document.getLocation());
					output.setTask(task);
					output.setDocument(document);
					output.setProject(task.getProject());
					output.setOwnership(ownership);
					output = (Output) PersistenceHelper.manager.save(output);
				}
			}
			map.put("result", SUCCESS);
			map.put("list", data);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "산출물 추가 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/project/addOutput");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}// end addOutputAction(Map<String, Object>);

	@Override
	public Map<String, Object> delOutputAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		// String oid = (String) param.get("oid");
		Output output = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String outputOid : list) {
				output = (Output) rf.getReference(outputOid).getObject();
				PersistenceHelper.manager.delete(output);
			}

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "산출물 삭제 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/project/viewProjectTask");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> startProjectAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		Project project = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			project = (Project) rf.getReference(oid).getObject();

			project.setState(ProjectStateType.INWORK.getDisplay());

			Timestamp start = new Timestamp(new Date().getTime());
			project.setStartDate(start);

			project = (Project) PersistenceHelper.manager.modify(project);

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "산출물 시작 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/project/viewProjectTask");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> completeProjectAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		Project project = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			project = (Project) rf.getReference(oid).getObject();

			// 하위 태스크체크..
			// 관리자일 경우 강제로 모두 완료??

			project.setState(ProjectStateType.COMPLETE.getDisplay());
			project.setProgress(100);

			project = (Project) PersistenceHelper.manager.modify(project);

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "프로젝트 완료 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/project/viewProject?oid=" + oid);
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> stopProjectAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		Project project = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			project = (Project) rf.getReference(oid).getObject();

			project.setState(ProjectStateType.STOP.getDisplay());

			project = (Project) PersistenceHelper.manager.modify(project);

			ArrayList<Task> list = new ArrayList<Task>();
			list = ProjectHelper.manager.getterProjectTask(project, list);
			for (Task task : list) {
				task.setState(TaskStateType.STOP.getDisplay());
				PersistenceHelper.manager.modify(task);
			}

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "프로젝트 중단 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/project/viewProject?oid=" + oid);
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> restartProjectAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		Project project = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			project = (Project) rf.getReference(oid).getObject();

			// 하위 태스크체크..
			// 관리자일 경우 강제로 모두 완료??

			project.setState(ProjectStateType.INWORK.getDisplay());
			// project.setProgress(100);

			project = (Project) PersistenceHelper.manager.modify(project);

			ArrayList<Task> list = new ArrayList<Task>();
			list = ProjectHelper.manager.getterProjectTask(project, list);
			for (Task task : list) {

				task.setState(TaskStateType.INWORK.getDisplay());
				PersistenceHelper.manager.modify(task);
			}

			trs.commit();
			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "프로젝트 재시작 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/project/viewProject?oid=" + oid);
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyProjectPriceAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		String oid = (String) param.get("oid");
		String machinePrice = (String) param.get("machinePrice");
		String elecPrice = (String) param.get("elecPrice");
//		String kekState = (String) param.get("kekState");

		Project project = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			project = (Project) rf.getReference(oid).getObject();

			if (!StringUtils.isNull(machinePrice)) {
				machinePrice = machinePrice.replace(",", "");
				project.setMachinePrice(Double.parseDouble(machinePrice));
			}

			if (!StringUtils.isNull(elecPrice)) {
				elecPrice = elecPrice.replace(",", "");
				project.setElecPrice(Double.parseDouble(elecPrice));
			}

			// project.setKekState(kekState);

			project = (Project) PersistenceHelper.manager.modify(project);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "작번이 " + MODIFY_OK);
			map.put("url", "/Windchill/plm/project/viewProject?oid=" + oid + "&popup=true");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "작번 " + MODIFY_FAIL);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public synchronized void commit(Project project) throws WTException {
		ArrayList<Task> list = new ArrayList<Task>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			ProjectHelper.manager.setProgress(project);

			// 모든 태스크 수집
			// list = ProjectHelper.manager.getterProjectTask(project, list);

			// list = ProjectHelper.manager.getterProjectNonSchduleTask(project, list);

			list = ProjectHelper.manager.getterProjectTask(project, list);

			// initAllProjectPlanDate(project.getPlanStartDate(), list);

			// setDependencyTask(list);

			setParentProgressSet(list);

			setProjectParentDate(list);

			setProjectDuration(project);

			if ("견적".equals(project.getPType())) {
				setQState(project);
			} else {
				int gate1 = ProjectHelper.manager.gate1StateIcon(project);
				int gate2 = ProjectHelper.manager.gate2StateIcon(project);
				int gate3 = ProjectHelper.manager.gate3StateIcon(project);
				int gate4 = ProjectHelper.manager.gate4StateIcon(project);
				int gate5 = ProjectHelper.manager.gate5StateIcon(project);

				project.setGate1(gate1);
				project.setGate2(gate2);
				project.setGate3(gate3);
				project.setGate4(gate4);
				project.setGate5(gate5);
			}

			int pro = ProjectHelper.manager.getKekProgress(project);

			project.setProgress(pro);
			PersistenceHelper.manager.modify(project);

			setProgressCheck(project);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void setParentProgressSet(ArrayList<Task> list) {
		Transaction trs = new Transaction();
		try {
			trs.start();
			for (Task tt : list) {
				QueryResult result = PersistenceHelper.manager.navigate(tt, "childTask", ParentTaskChildTaskLink.class);
				int sum = 0;
				int size = result.size();
				while (result.hasMoreElements()) {
					Task child = (Task) result.nextElement();
					sum += child.getProgress() != null ? child.getProgress() : 0;
				}
				if (size > 0) {
					sum = sum / size;
					tt.setProgress(sum);
					PersistenceHelper.manager.modify(tt);
				}
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void initAllProjectPlanDate(Timestamp planStartDate, ArrayList<Task> list) throws WTException {
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (int i = 0; i < list.size(); i++) {
				Task task = (Task) list.get(i);
				// 뎁스 재정렬
				Task parent = task.getParentTask();
				if (parent == null) {
					task.setDepth(1);
				} else {
					task.setDepth(task.getParentTask().getDepth() + 1);
				}
			}

			for (int i = list.size() - 1; i >= 0; i--) {
				Task task = (Task) list.get(i);

				// if (task.getTaskType().equals("일반")) {
				// continue;
				// }

				// 계획 시작일은 템플릿의 계획 시작일로 설정 2000-01-01
				Calendar sCa = Calendar.getInstance();
				sCa.setTime(planStartDate);
				// 계획 시작일
				Timestamp start = new Timestamp(sCa.getTime().getTime());
				task.setPlanStartDate(start);
				// 태스크의 기간으로 계획 종료일을 세팅
				int duration = task.getDuration();

				Calendar eCa = Calendar.getInstance();
				eCa.setTime(planStartDate);
				eCa.add(Calendar.DATE, duration);

				Timestamp end = new Timestamp(eCa.getTime().getTime());
				task.setPlanEndDate(end);

				task = (Task) PersistenceHelper.manager.modify(task);
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
	}

	@Override
	public void setProjectDuration(Project project) throws WTException {
		Transaction trs = new Transaction();

		try {
			trs.start();

			Timestamp start = null;
			Timestamp end = null;
			boolean edit = false;

			ArrayList<Task> list = new ArrayList<Task>();
			list = ProjectHelper.manager.getterProjectTasks(project, list);

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

				if (cend != null) {
					if (end == null || (end.getTime() < cend.getTime())) {
						end = cend;
						edit = true;
					}
				} else {

				}
			}

			if (edit) {
				project.setPlanStartDate(start);
				project.setPlanEndDate(end);

				duration = DateUtils.getDuration(start, end);
				project.setDuration(duration);
			}

			PersistenceHelper.manager.modify(project);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void setProjectParentDate(ArrayList<Task> list) throws WTException {
		Transaction trs = new Transaction();

		try {
			trs.start();

			for (int i = list.size() - 1; i >= 0; i--) {
				Task task = (Task) list.get(i);

				// 상위 계획 시작
				Timestamp start = null;
				// 상위 계획 종료
				Timestamp end = null;

				boolean edit = false;

				// 하위 태스크가 없을 경우 일정 정리 필요가 없음
				QueryResult result = PersistenceHelper.manager.navigate(task, "childTask",
						ParentTaskChildTaskLink.class);
				while (result.hasMoreElements()) {
					Task child = (Task) result.nextElement();

					// 하위 계획 시작
					Timestamp cstart = child.getPlanStartDate();
					// 하위 계획 종료
					Timestamp cend = child.getPlanEndDate();
					// 상위 계획 시작일이 null
					// 2000-01-02 2000-01-01
					// 계획 시작일이 늦은 쪽으로 세팅 한다.
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
					task.setPlanStartDate(start);
					task.setPlanEndDate(end);

					int duration = DateUtils.getDuration(start, end);
					task.setDuration(duration);

					task = (Task) PersistenceHelper.manager.modify(task);
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
		}
	}

	@Override
	public void setDependencyTask(ArrayList<Task> list) throws WTException {
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (int i = 0; i < list.size(); i++) {
				Task task = (Task) list.get(i);

				// targettask = 선행
				// sourcetask = 후행
				ArrayList<TargetTaskSourceTaskLink> targetList = ProjectHelper.manager
						.getTargetTaskSourceTaskLinkByTarget(task);

				// 후행 태스크의 계획 시작일
				Timestamp preStart = null;
				// 후행 태스크의 계획 종료일
				Timestamp preEnd = null;

				boolean edit = false;

				for (TargetTaskSourceTaskLink link : targetList) {
					Task targetTask = link.getTargetTask();
					// 선행 태스크의 계획 종료일 세팅
					Calendar sCa = Calendar.getInstance();
					sCa.setTime(targetTask.getPlanEndDate());
					sCa.add(Calendar.DATE, -1);

					int lag = 1;
					if (link.getLag() == null) {
						lag = 1;
					}

					sCa.add(Calendar.DATE, lag);

					Timestamp start = new Timestamp(sCa.getTime().getTime());

					BigDecimal bd = new BigDecimal(task.getDuration()); // 원래 태스크의 기간을 설정
					bd = bd.setScale(0, BigDecimal.ROUND_UP);
					int duration = bd.intValue();

					// 종료일
					Calendar eCa = Calendar.getInstance();
					// 2000-01-04...
					eCa.setTime(start);
					// + 10일
					for (int k = 0; k < duration; k++) {
						eCa.add(Calendar.DATE, 1);
						// eCa = DateUtils.checkHoliday(eCa);
					}
					// 2000-01-14..

					// 계획 종료일
					Timestamp end = new Timestamp(eCa.getTime().getTime());

					// 후행 태스크의 계획 시작일 null
					// 후행 2000-01-02 선행 2000-01-03
					if (preStart == null || (preStart.getTime() < start.getTime())) {
						preStart = start;
						edit = true;
					}

					// 후행 태스크의 계획 종료일 null
					// 후행 2000-01-02 선행 2000-01-03
					if (preEnd == null || (preEnd.getTime() < end.getTime())) {
						preEnd = end;
						edit = true;
					}
				}

				if (edit) {
					task.setPlanStartDate(preStart);
					task.setPlanEndDate(preEnd);
					task = (Task) PersistenceHelper.manager.modify(task);
				}
			}

			trs.commit();
			trs = null;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public Map<String, Object> completeTaskAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");

		Project project = null;
		Task task = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			task = (Task) rf.getReference(oid).getObject();

			task.setProgress(100);
			task.setState(TaskStateType.COMPLETE.getDisplay());
			task.setEndDate(DateUtils.getCurrentTimestamp());
			task = (Task) PersistenceHelper.manager.modify(task);

			project = task.getProject();

			commit(project);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "태스크가 완료 되었습니다.");
			map.put("url", "/Windchill/plm/project/viewProjectTask?oid=" + oid + "&popup=true");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "태스크 완료 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createIssueAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		Issue issue = null;
		Project project = null;
		String name = (String) param.get("name");
		String description = (String) param.get("description");
		String poid = (String) param.get("poid");
		ArrayList<String[]> data = new ArrayList<String[]>();
		ArrayList<IssueProjectLink> issueList = new ArrayList<IssueProjectLink>();

		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			project = (Project) rf.getReference(poid).getObject();
			issue = Issue.newIssue();
			issue.setName(name);
			issue.setDescription(description);
			issue.setOwnership(ownership);

			issue = (Issue) PersistenceHelper.manager.save(issue);

			IssueProjectLink link = IssueProjectLink.newIssueProjectLink(issue, project);
			PersistenceHelper.manager.save(link);
			//////////////////////////////////////////////
			ContentUtils.updatePrimary(param, issue);
			//////////////////////////////////////////////
			issueList = ProjectHelper.manager.getIssueProjectLink(project);
			for (IssueProjectLink issueLink : issueList) {
				Issue iss = issueLink.getIssue();
				String issueOid = iss.getPersistInfo().getObjectIdentifier().getStringValue();
				// DocumentViewData ddata = new DocumentViewData(document);
				// 0 oid, 1 number, 2 name, 3 version, 4 state, 5 creator, 6 createdate
				String[] s = new String[] { issueOid, iss.getName(), iss.getDescription(),
						iss.getOwnership().getOwner().getFullName(),
						iss.getCreateTimestamp().toString().substring(0, 16) };
				data.add(s);
			}
			////////////////////////////////////////////////
			map.put("list", data);
			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "특이사항이  " + CREATE_OK);
			map.put("url", "/Windchill/plm/document/listOutput");

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "특이사항 " + CREATE_FAIL);
			map.put("url", "/Windchill/plm/document/createOutput");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void setProgressCheck(Project project) throws WTException {
		ArrayList<Task> list = new ArrayList<Task>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			ProjectViewData dd = new ProjectViewData(project);
			// int progress = project.getProgress();
			int progress = dd.kekProgress;
			if (progress == 100) {
				project.setState(ProjectStateType.COMPLETE.getDisplay());

				if (project.getEndDate() == null) {
					project.setEndDate(DateUtils.getCurrentTimestamp());
				}

				if (project.getKekState() != null) {
					switch (project.getKekState()) {
					case "작업완료":
						break;
					default:
						project.setKekState("설계완료");
					}
				} else {
					project.setKekState("설계완료");
				}

				PersistenceHelper.manager.modify(project);

				project = (Project) PersistenceHelper.manager.refresh(project);

				list = ProjectHelper.manager.getterProjectTask(project, list);
				for (Task tt : list) {
					tt.setState(TaskStateType.COMPLETE.getDisplay());
					if (tt.getEndDate() == null) {
						tt.setEndDate(DateUtils.getCurrentTimestamp());
					}

					if (tt.getStartDate() == null) {
						tt.setStartDate(DateUtils.getCurrentTimestamp());
					}

					tt.setProgress(100);
					tt = (Task) PersistenceHelper.manager.modify(tt);
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
		}

	}

	@Override
	public Map<String, Object> delIssueAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		// String oid = (String) param.get("oid");
		Issue issue = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String issueOid : list) {
				issue = (Issue) rf.getReference(issueOid).getObject();
				PersistenceHelper.manager.delete(issue);
			}

			map.put("result", SUCCESS);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "산출물 삭제 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/project/viewProject");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> setUserAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String kekState = (String) param.get("kekState");
		String pmOid = (String) param.get("pmOid");
		String subpmOid = (String) param.get("subpmOid");
		String machineOid = (String) param.get("machineOid");
		String elecOid = (String) param.get("elecOid");
		String softOid = (String) param.get("softOid");
		ReferenceFactory rf = new ReferenceFactory();
		Project project = null;
		WTUser pm = null;
		WTUser subpm = null;
		WTUser machine = null;
		WTUser elec = null;
		WTUser soft = null;
		Persistable per = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			project = (Project) rf.getReference(oid).getObject();
			project.setKekState(kekState);

			ArrayList<Task> list = new ArrayList<Task>();

			if ("설계완료".equals(kekState)) {
				list = ProjectHelper.manager.getterProjectTask(project, list);

				for (Task tt : list) {

					if (!"견적".equals(project.getPType())) {

						if (tt.getTaskType().equals("일반")) {
							continue;
						}
					}

					tt.setProgress(100);
					tt.setState(TaskStateType.COMPLETE.getDisplay());

					if (tt.getStartDate() == null) {
						tt.setStartDate(DateUtils.getCurrentTimestamp());
					}

					if (tt.getEndDate() == null) {
						tt.setEndDate(DateUtils.getCurrentTimestamp());
					}
					PersistenceHelper.manager.modify(tt);
				}

				if (project.getStartDate() == null) {
					project.setStartDate(DateUtils.getCurrentTimestamp());
				}

				int kekProgress = ProjectHelper.manager.getKekProgress(project);
				if (kekProgress == 100) {

					project.setState(ProjectStateType.COMPLETE.getDisplay());

					if (project.getEndDate() == null) {
						project.setEndDate(DateUtils.getCurrentTimestamp());
					}
				}
			} else if ("작업완료".equals(kekState)) {
				list = ProjectHelper.manager.getterProjectTask(project, list);

				for (Task tt : list) {

					// if (!tt.getTaskType().equals("일반")) {
					// continue;
					// }

					tt.setProgress(100);
					tt.setState(TaskStateType.COMPLETE.getDisplay());

					if (tt.getStartDate() == null) {
						tt.setStartDate(DateUtils.getCurrentTimestamp());
					}

					if (tt.getEndDate() == null) {
						tt.setEndDate(DateUtils.getCurrentTimestamp());
					}
					PersistenceHelper.manager.modify(tt);
				}

				if (project.getStartDate() == null) {
					project.setStartDate(DateUtils.getCurrentTimestamp());
				}

				int kekProgress = ProjectHelper.manager.getKekProgress(project);
				if (kekProgress == 100) {

					project.setState(ProjectStateType.COMPLETE.getDisplay());

					if (project.getEndDate() == null) {
						project.setEndDate(DateUtils.getCurrentTimestamp());
					}
				}
			}

			project = (Project) PersistenceHelper.manager.modify(project);

			QueryResult result = PersistenceHelper.manager.navigate(project, "user", ProjectUserLink.class, false);
			while (result.hasMoreElements()) {
				ProjectUserLink ll = (ProjectUserLink) result.nextElement();
				PersistenceHelper.manager.delete(ll);
			}

			if (!StringUtils.isNull(pmOid)) {
				per = (Persistable) rf.getReference(pmOid).getObject();
				if (per instanceof WTUser) {
					pm = (WTUser) per;
				} else if (per instanceof People) {
					People p = (People) per;
					pm = p.getUser();
				}
				ProjectUserLink link = ProjectUserLink.newProjectUserLink(project, pm);
				link.setUserType(ProjectUserType.PM.name());
				PersistenceHelper.manager.save(link);
			}

			if (!StringUtils.isNull(subpmOid)) {
				per = (Persistable) rf.getReference(subpmOid).getObject();
				if (per instanceof WTUser) {
					subpm = (WTUser) per;
				} else if (per instanceof People) {
					People p = (People) per;
					subpm = p.getUser();
				}
				ProjectUserLink link = ProjectUserLink.newProjectUserLink(project, subpm);
				link.setUserType(ProjectUserType.SUB_PM.name());
				PersistenceHelper.manager.save(link);
			}

			if (!StringUtils.isNull(machineOid)) {
				per = (Persistable) rf.getReference(machineOid).getObject();
				if (per instanceof WTUser) {
					machine = (WTUser) per;
				} else if (per instanceof People) {
					People p = (People) per;
					machine = p.getUser();
				}
				ProjectUserLink link = ProjectUserLink.newProjectUserLink(project, machine);
				link.setUserType(ProjectUserType.MACHINE.name());
				PersistenceHelper.manager.save(link);
			}

			if (!StringUtils.isNull(elecOid)) {
				per = (Persistable) rf.getReference(elecOid).getObject();
				if (per instanceof WTUser) {
					elec = (WTUser) per;
				} else if (per instanceof People) {
					People p = (People) per;
					elec = p.getUser();
				}
				ProjectUserLink link = ProjectUserLink.newProjectUserLink(project, elec);
				link.setUserType(ProjectUserType.ELEC.name());
				PersistenceHelper.manager.save(link);
			}

			if (!StringUtils.isNull(softOid)) {
				per = (Persistable) rf.getReference(softOid).getObject();
				if (per instanceof WTUser) {
					soft = (WTUser) per;
				} else if (per instanceof People) {
					People p = (People) per;
					soft = p.getUser();
				}
				ProjectUserLink link = ProjectUserLink.newProjectUserLink(project, soft);
				link.setUserType(ProjectUserType.SOFT.name());
				PersistenceHelper.manager.save(link);
			}

			commit(project);

			map.put("result", SUCCESS);
			map.put("msg", "담당자 지정이 완료 되었습니다.");
			map.put("reload", true);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("reload", false);
			map.put("msg", "담당자 지정 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/project/viewProject");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> setProgressAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String progress = (String) param.get("progress");
		Task task = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			task = (Task) rf.getReference(oid).getObject();

			if (task.getStartDate() == null) {
				task.setStartDate(DateUtils.getCurrentTimestamp());
			}

			if (task.getState().equals(TaskStateType.STAND.getDisplay())) {
				task.setState(TaskStateType.INWORK.getDisplay());
			}

			if (Integer.parseInt(progress) == 100) {
				task.setState(TaskStateType.COMPLETE.getDisplay());

				if (task.getEndDate() == null) {
					task.setEndDate(DateUtils.getCurrentTimestamp());
				}
			} else {

			}

			task.setProgress(Integer.parseInt(progress));

			task = (Task) PersistenceHelper.manager.modify(task);

			commit(task.getProject());

			map.put("result", SUCCESS);
			map.put("msg", "진행률이 변경 완료 되었습니다.");
			map.put("reload", true);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("reload", false);
			map.put("msg", "진행률 변경 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/project/viewProjectTask?oid=" + oid + "&popup=true");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyProjectAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		Project project = null;
		String oid = (String) param.get("oid");
		String postdate = (String) param.get("postdate");
		String keNumber = (String) param.get("keNumber");
		String postdate_m = (String) param.get("postdate_m");
		String userId = (String) param.get("userId");
		String customer = (String) param.get("customer");
		String mak = (String) param.get("mak");
		String ins_location = (String) param.get("ins_location");
		String model = (String) param.get("model");
		String pType = (String) param.get("pType");
		String description = (String) param.get("description");

		Transaction trs = new Transaction();
		try {
			trs.start();
//			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());
			project = (Project) rf.getReference(oid).getObject();

			project.setPDate(DateUtils.convertDate(postdate));
			project.setKeNumber(keNumber);
			project.setCustomDate(DateUtils.convertDate(postdate_m));
			project.setUserId(userId);
			project.setCustomer(customer);
			project.setMak(mak);
			project.setIns_location(ins_location);
			project.setPType(pType);
			project.setModel(model);
			project.setDescription(description);

			project = (Project) PersistenceHelper.manager.modify(project);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "작번이 " + MODIFY_OK);

			// map.put("url", "/Windchill/plm/document/listDocument");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "작번수정 " + MODIFY_FAIL);
			// map.put("url", "/Windchill/plm/document/modifyOutput?oid=" + oid);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void startScheduler() throws WTException {
		ArrayList<Task> list = new ArrayList<Task>();

		SessionContext prev = SessionContext.newContext();

		try {
			QuerySpec query = new QuerySpec();

			SessionHelper.manager.setAdministrator();

//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);
			Project project = null;
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				project = (Project) obj[0];

				list = ProjectHelper.manager.getterProjectTask(project, list);

				for (Task tt : list) {

					System.out.println("planStartDate = " + tt.getPlanStartDate());
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SessionContext.setContext(prev);
		}
	}

	@Override
	public Map<String, Object> deleteProjectAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		Project project = null;
		List<String> list = (List<String>) param.get("list");
		Transaction trs = new Transaction();
		try {
			trs.start();
			for (String oid : list) {
				project = (Project) rf.getReference(oid).getObject();
				ArrayList<Task> lists = new ArrayList<Task>();
				lists = ProjectHelper.manager.getterProjectTask(project, lists);
				for (Task tt : lists) {
					tt = (Task) PersistenceHelper.manager.refresh(tt);
					PersistenceHelper.manager.delete(tt);
				}
				PersistenceHelper.manager.delete(project);
			}
			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "작번이 " + DELETE_OK);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "작번삭제 " + DELETE_FAIL);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void setQState(Project project) throws WTException {
//		Map<String, Object> map = new HashMap<String, Object>();
//		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();

		SessionContext prev = SessionContext.newContext();
		String[] QTASK = ProjectHelper.QTASK;

		try {
			trs.start();

			SessionHelper.manager.setAdministrator();

			ArrayList<Task> list = new ArrayList<Task>();
			list = ProjectHelper.manager.getterProjectTask(project, list);
//			StringBuffer sb = new StringBuffer();
			for (Task tt : list) {

				String tname = tt.getName();

				if (tname.equals("의뢰서")) {
					continue;
				}

				if (!tname.equals(QTASK[0]) && !tname.equals(QTASK[1]) && !tname.equals(QTASK[2])
						&& !tname.equals(QTASK[3]) && !tname.equals(QTASK[4])) {
					continue;
				}

				if (tname.equals(QTASK[0])) {
					int qstate = ProjectHelper.manager.getQState(tt);
					project.setGate1(qstate);
				}

				if (tname.equals(QTASK[1])) {
					int qstate = ProjectHelper.manager.getQState(tt);
					project.setGate2(qstate);
				}

				if (tname.equals(QTASK[2])) {
					int qstate = ProjectHelper.manager.getQState(tt);
					project.setGate3(qstate);
				}

				if (tname.equals(QTASK[3])) {
					int qstate = ProjectHelper.manager.getQState(tt);
					project.setGate4(qstate);
				}

				if (tname.equals(QTASK[4])) {
					int qstate = ProjectHelper.manager.getQState(tt);
					project.setGate5(qstate);
				}

			}

			PersistenceHelper.manager.modify(project);

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
	public Map<String, Object> onSaveAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String toid = (String) param.get("toid");
		String col = (String) param.get("col");
		String newValue = (String) param.get("newValue");

		Task task = null;
		Project project = null;
		ReferenceFactory rf = new ReferenceFactory();

		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
		Ownership ownership = Ownership.newOwnership(user);

		Transaction trs = new Transaction();
		try {
			trs.start();

			if (oid.indexOf("e3ps.project.Task") > -1) {
				// 기존 수정
				task = (Task) rf.getReference(oid).getObject();
				if ("text".equals(col)) {
					task.setName(newValue);
					task.setTaskType("일반");
					task.setAllocate(0);
				} else if ("duration".equals(col)) {

					Calendar ca = Calendar.getInstance();
					ca.setTimeInMillis(task.getPlanStartDate().getTime());
					ca.add(Calendar.DATE, Integer.parseInt(newValue));

					Timestamp end = new Timestamp(ca.getTime().getTime());
					task.setPlanEndDate(end);
					task.setDuration(Integer.parseInt(newValue));
					task.setTaskType("일반");
				} else if ("taskType".equals(col)) {
					task.setTaskType(newValue);
				} else if ("allocate".equals(col)) {
					task.setAllocate(Integer.parseInt(newValue));
				}

				task.setState("작업 중");

				project = task.getProject();
				task = (Task) PersistenceHelper.manager.modify(task);
				task = (Task) PersistenceHelper.manager.refresh(task);
				map.put("oid", task.getPersistInfo().getObjectIdentifier().getStringValue());
			} else {
				// 신규 생성
				// 템플릿 연결
				task = Task.newTask();
				project = (Project) rf.getReference(toid).getObject();
				task.setProject(project);
				task.setOwnership(ownership);
				int sort = ProjectHelper.manager.getMaxSort(project);

				if ("text".equals(col)) {
					task.setName(newValue);
					task.setDescription(newValue);
					task.setPlanStartDate(DateUtils.getPlanStartDate());
					task.setDepth(1);
					task.setSort(sort);
					task.setTaskType("일반");
					task.setState("작업 중");

					Calendar ca = Calendar.getInstance();
					ca.setTimeInMillis(task.getPlanStartDate().getTime());
					ca.add(Calendar.DATE, 1);

					Timestamp end = new Timestamp(ca.getTime().getTime());
					task.setPlanEndDate(end);
					task.setDuration(1);
					task.setAllocate(0);

					// tools sql_script -Dgen.input=e3ps.doc.**

					task = (Task) PersistenceHelper.manager.save(task);
				} else if ("duration".equals(col)) {
					task.setName("새 태스크");
					task.setDescription("새 태스크");
					task.setPlanStartDate(DateUtils.getPlanStartDate());
					task.setDepth(1);
					task.setSort(sort);
					task.setState("작업 중");
					task.setTaskType("일반");

					Calendar ca = Calendar.getInstance();
					ca.setTimeInMillis(task.getPlanStartDate().getTime());
					ca.add(Calendar.DATE, Integer.parseInt(newValue));

					Timestamp end = new Timestamp(ca.getTime().getTime());
					task.setPlanEndDate(end);
					task.setDuration(Integer.parseInt(newValue));
					task.setAllocate(0);

					task = (Task) PersistenceHelper.manager.save(task);
				} else if ("taskType".equals(col)) {
					task.setName("새 태스크");
					task.setDescription("새 태스크");
					task.setPlanStartDate(DateUtils.getPlanStartDate());
					task.setDepth(1);
					task.setSort(sort);
					task.setState("작업 중");

					Calendar ca = Calendar.getInstance();
					ca.setTimeInMillis(task.getPlanStartDate().getTime());
					ca.add(Calendar.DATE, 1);

					Timestamp end = new Timestamp(ca.getTime().getTime());
					task.setPlanEndDate(end);
					task.setDuration(1);
					task.setTaskType(newValue);
					task.setAllocate(0);

					task = (Task) PersistenceHelper.manager.save(task);
				} else if ("allocate".equals(col)) {
					task.setName("새 태스크");
					task.setDescription("새 태스크");
					task.setPlanStartDate(DateUtils.getPlanStartDate());
					task.setDepth(1);
					task.setSort(sort);
					task.setState("작업 중");

					Calendar ca = Calendar.getInstance();
					ca.setTimeInMillis(task.getPlanStartDate().getTime());
					ca.add(Calendar.DATE, 1);

					Timestamp end = new Timestamp(ca.getTime().getTime());
					task.setPlanEndDate(end);
					task.setDuration(1);
					task.setTaskType("일반");
					task.setAllocate(Integer.parseInt(newValue));

					task = (Task) PersistenceHelper.manager.save(task);
				}
				map.put("id", oid);
				map.put("oid", task.getPersistInfo().getObjectIdentifier().getStringValue());
			}

			project = (Project) PersistenceHelper.manager.refresh(project);

			commit(project);

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
	public Map<String, Object> onDeleteTaskAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String toid = (String) param.get("toid");
		String parent = (String) param.get("parent");

		Task task = null;
		Project project = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			task = (Task) rf.getReference(oid).getObject();

			PersistenceHelper.manager.delete(task);

			project = (Project) rf.getReference(toid).getObject();

			commit(project);

			map.put("parent", parent);
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
	public Map<String, Object> onAfterLinkDeleteAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String toid = (String) param.get("toid");
		Project project = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink) rf.getReference(oid).getObject();
			PersistenceHelper.manager.delete(link);

			project = (Project) rf.getReference(toid).getObject();
			project = (Project) PersistenceHelper.manager.refresh(project);
			commit(project);

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
	public Map<String, Object> onMoveTaskAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String toid = (String) param.get("toid");
		String oid = (String) param.get("oid");
		int depth = (int) param.get("depth");
		String parent = (String) param.get("parent");
		List<String> childrens = (List<String>) param.get("childrens");
		Task task = null;
		Project project = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			// 태스크 정보 수정
			task = (Task) rf.getReference(oid).getObject();
			// task.setDepth(depth - 1);
			task.setDepth(depth);
			if (!StringUtils.isNull(parent)) {
				Persistable per = (Persistable) rf.getReference(parent).getObject();

				if (per instanceof Project) {
					task.setParentTask(null);
				} else if (per instanceof Task) {
					Task parentTask = (Task) per;
					task.setParentTask(parentTask);
				}
			}

			task = (Task) PersistenceHelper.manager.modify(task);
			task = (Task) PersistenceHelper.manager.refresh(task);
			int idx = 0;
			for (String s : childrens) {
				Task t = (Task) rf.getReference(s).getObject();

				t.setSort(idx);
				idx++;
				PersistenceHelper.manager.modify(t);
				t = (Task) PersistenceHelper.manager.refresh(t);
			}

			project = (Project) rf.getReference(toid).getObject();
			project = (Project) PersistenceHelper.manager.refresh(project);
			commit(project);

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
	public Map<String, Object> onBeforeLinkAddAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String toid = (String) param.get("toid");
		long id = (long) param.get("id");
		String source = (String) param.get("source");
		String target = (String) param.get("target");

		Task sourceTask = null;
		Task targetTask = null;

		Project project = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			project = (Project) rf.getReference(toid).getObject();

			sourceTask = (Task) rf.getReference(source).getObject(); // 선행
			targetTask = (Task) rf.getReference(target).getObject(); // 후행

			TargetTaskSourceTaskLink link = TargetTaskSourceTaskLink.newTargetTaskSourceTaskLink(sourceTask,
					targetTask);
			link.setProject(project);
			link.setProject(null);
			link.setLag(1);
			PersistenceHelper.manager.save(link);
			project = (Project) PersistenceHelper.manager.refresh(project);
			commit(project);

			map.put("result", SUCCESS);

			map.put("id", id);
			map.put("linkId", link.getPersistInfo().getObjectIdentifier().getStringValue());

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
	public Map<String, Object> onAfterTaskResizeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String toid = (String) param.get("toid");
		String start_date = (String) param.get("start_date");
		String end_date = (String) param.get("end_date");
		int duration = (int) param.get("duration");
		Project project = null;
		Task task = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			task = (Task) rf.getReference(oid).getObject();
			task.setPlanStartDate(DateUtils.convertStartDate(start_date));
			task.setPlanEndDate(DateUtils.convertEndDate(end_date));
			task.setDuration(duration);

			task = (Task) PersistenceHelper.manager.modify(task);

			task = (Task) PersistenceHelper.manager.refresh(task);

			project = (Project) rf.getReference(toid).getObject();
			project = (Project) PersistenceHelper.manager.refresh(project);
			commit(project);

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
	public Map<String, Object> onAfterTaskMoveAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String start_date = (String) param.get("start_date");
		String end_date = (String) param.get("end_date");
		Task task = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			task = (Task) rf.getReference(oid).getObject();
			task.setPlanStartDate(DateUtils.convertStartDate(start_date));
			task.setPlanEndDate(DateUtils.convertEndDate(end_date));

			task = (Task) PersistenceHelper.manager.modify(task);

			task = (Task) PersistenceHelper.manager.refresh(task);

			commit(task.getProject());

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
	public Map<String, Object> onAfterLinkUpdateAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String toid = (String) param.get("toid");
		int lag = (int) param.get("lag");
		Project project = null;
		TargetTaskSourceTaskLink link = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			project = (Project) rf.getReference(toid).getObject();

			link = (TargetTaskSourceTaskLink) rf.getReference(oid).getObject();
			link.setLag(lag);

			PersistenceHelper.manager.modify(link);
			project = (Project) PersistenceHelper.manager.refresh(project);
			commit(project);

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
	public void setStartDate(Project project) throws WTException {
//		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void test() throws WTException {
		// SessionContext pre = SessionContext.newContext();

		try {

			SessionHelper.manager.setAdministrator();

			QuerySpec query = new QuerySpec();

//			int idx = query.appendClassList(Project.class, true);

			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();

				Project pro = (Project) obj[0];
				System.out.println(pro.getKekNumber());

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// SessionContext.setContext(pre);
		}
	}

	@Override
	public Map<String, Object> completeStepAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		String step = (String) param.get("step");
		ReferenceFactory rf = new ReferenceFactory();

		String[] gate1 = ProjectHelper.GATE1;
		String[] gate2 = ProjectHelper.GATE2;
		String[] gate3 = ProjectHelper.GATE3;
		String[] gate4 = ProjectHelper.GATE4;
		String[] gate5 = ProjectHelper.GATE5;

		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				// 태스크 이름으로 처리
				Project project = (Project) rf.getReference(oid).getObject();
				ArrayList<Task> ll = new ArrayList<Task>();

				ll = ProjectHelper.manager.getterProjectTask(project, ll);

				if ("1".equals(step)) {

					project.setGate1(4);

					for (Task task : ll) {

						String name = task.getName();

						for (String ss : gate1) {
							if (name.equals(ss)) {

								System.out.println("durl tlfodafsdfl");
								task.setProgress(100);
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
								PersistenceHelper.manager.modify(task);
							}
						}
					}

				} else if ("2".equals(step)) {

					project.setGate2(4);
					for (Task task : ll) {

						String name = task.getName();

						for (String ss : gate2) {

							if (name.equals(ss)) {
								task.setProgress(100);
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
								PersistenceHelper.manager.modify(task);
							}
						}
					}
				} else if ("3".equals(step)) {
					project.setGate3(4);
					for (Task task : ll) {

						String name = task.getName();

						for (String ss : gate3) {

							if (name.equals(ss)) {
								task.setProgress(100);
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
								PersistenceHelper.manager.modify(task);
							}
						}
					}
				} else if ("4".equals(step)) {
					project.setGate4(4);
					for (Task task : ll) {

						String name = task.getName();

						for (String ss : gate4) {

							if (name.equals(ss)) {
								task.setProgress(100);
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
								PersistenceHelper.manager.modify(task);
							}
						}
					}
				} else if ("5".equals(step)) {
					project.setGate5(4);
					for (Task task : ll) {

						String name = task.getName();

						for (String ss : gate5) {

							if (name.equals(ss)) {
								task.setProgress(100);
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
								PersistenceHelper.manager.modify(task);
							}
						}
					}
				} else if ("6".equals(step)) {
					project.setGate1(4);
					project.setGate2(4);
					project.setGate3(4);
					project.setGate4(4);
					project.setGate5(4);
					for (Task task : ll) {

						String name = task.getName();

						for (String ss : gate1) {

							if (name.equals(ss)) {
								task.setProgress(100);
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
								PersistenceHelper.manager.modify(task);
							}
						}

						for (String ss : gate2) {

							if (name.equals(ss)) {
								task.setProgress(100);
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
								PersistenceHelper.manager.modify(task);
							}
						}

						for (String ss : gate3) {

							if (name.equals(ss)) {
								task.setProgress(100);
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
								PersistenceHelper.manager.modify(task);
							}
						}

						for (String ss : gate4) {

							if (name.equals(ss)) {
								task.setProgress(100);
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
								PersistenceHelper.manager.modify(task);
							}
						}

						for (String ss : gate5) {

							if (name.equals(ss)) {
								task.setProgress(100);
								task.setState(TaskStateType.COMPLETE.getDisplay());
								task.setEndDate(DateUtils.getCurrentTimestamp());
								PersistenceHelper.manager.modify(task);
							}
						}
					}
				}

				PersistenceHelper.manager.modify(project);
			}

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "상태변경 완료");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "상태변경 에러");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String kekNumber = (String) params.get("kekNumber");
		String pDate = (String) params.get("pDate");
		String keNumber = (String) params.get("keNumber");
		String userId = (String) params.get("userId");
		String mak = (String) params.get("mak");
		String detail = (String) params.get("detail");
		String model = (String) params.get("model");
		String customer = (String) params.get("customer");
		String install = (String) params.get("install");
		String projectType = (String) params.get("projectType");
		String templateName = (String) params.get("templateName");
		String description = (String) params.get("description");
		String customDate = (String) params.get("customDate");
		String systemInfo = (String) params.get("systemInfo");

		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			Project project = Project.newProject();

			project.setOwnership(ownership);
			project.setKekNumber(kekNumber);
			project.setKeNumber(keNumber);
			project.setPDate(DateUtils.convertDate(pDate));
			project.setUserId(userId);
			project.setMak((CommonCode) CommonUtils.getObject(mak));
			project.setModel(model);
			project.setInstall((CommonCode) CommonUtils.getObject(install));
			project.setProjectType((CommonCode) CommonUtils.getObject(projectType));
			project.setDetail((CommonCode) CommonUtils.getObject(detail));
			project.setKekState("준비");
			project.setCustomer((CommonCode) CommonUtils.getObject(customer));
			project.setDescription(description);
			project.setCustomDate(DateUtils.convertDate(customDate));
			project.setMachinePrice(0D);
			project.setElecPrice(0D);

			PersistenceHelper.manager.save(project);

			if (!StringUtils.isNull(templateName)) {
				Template template = TemplateHelper.manager.getTemplateByName(templateName);

				if (!StringUtils.isNull(template)) {

					Timestamp start = DateUtils.getPlanStartDate();
					// 계획 시작일, 계획 종료일은 등록일로 세팅 한다. 템플릿의 경우 태스크 생성시 일정을 다시 조절한다.
					project.setPlanStartDate(start);

					Calendar eCa = Calendar.getInstance();
					eCa.setTimeInMillis(start.getTime());
					eCa.add(Calendar.DATE, template.getDuration());

					Timestamp end = new Timestamp(eCa.getTime().getTime());
					project.setPlanEndDate(end);
					project.setTemplate(template);

					project.setDuration(DateUtils.getDuration(start, end));

					project = (Project) PersistenceHelper.manager.modify(project);

					copyTasks(project, template, null);
					// 없을경우

					WTUser pm = TemplateHelper.manager.getPMByTemplate(template);
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

					WTUser subPm = TemplateHelper.manager.getSubPMByTemplate(template);
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
				save(project, null, childrens);
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
	
	private void save(Project project, Task parentTask, ArrayList<TaskTreeNode> childrens) throws Exception {
		Ownership ownership = CommonUtils.sessionOwner();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (TaskTreeNode node : childrens) {
				int depth = node.get_$depth();
				String oid = node.getOid();
				String name = node.getName();
				String description = node.getDescription();
				int duration = node.getDuration();
				boolean isNew = node.isNew();
				ArrayList<TaskTreeNode> n = node.getChildren();
				int sort = TaskHelper.manager.getSort(project, parentTask);
				String taskType = node.getTaskType();
				Task t = null;
				if (isNew) {
					t = Task.newTask();
					t.setName(name);
					t.setDepth(depth);
					t.setDescription(description);
					t.setDuration(duration);
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
					t.setState(TaskStateVariable.READY);
					t.setDescription(description);
					t.setDuration(duration);
					t.setOwnership(ownership);
					t.setParentTask(parentTask);
					t.setProject(project);
					t.setPlanStartDate(project.getPlanStartDate());
					t.setPlanEndDate(project.getPlanEndDate());
					t.setDuration(DateUtils.getDuration(project.getPlanStartDate(), project.getPlanEndDate()));
					t.setSort(sort);
					t.setTaskType(CommonCodeHelper.manager.getCommonCode(taskType, "TASK_TYPE"));
					PersistenceHelper.manager.modify(t);
				}
				save(project, t, n);
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
// end class
