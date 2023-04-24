package e3ps.project.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.service.PartlistHelper;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.bom.tbom.service.TBOMHelper;
import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.service.MeetingHelper;
import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.doc.request.service.RequestDocumentHelper;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import e3ps.epm.service.EpmHelper;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.service.WorkOrderHelper;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.service.KePartHelper;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.dto.ProjectDTO;
import e3ps.project.output.Output;
import e3ps.project.output.OutputTaskLink;
import e3ps.project.task.ParentTaskChildTaskLink;
import e3ps.project.task.Task;
import e3ps.project.task.service.TaskHelper;
import e3ps.project.task.variable.TaskStateVariable;
import e3ps.project.template.Template;
import e3ps.project.variable.ProjectStateVariable;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.PagingQueryResult;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class ProjectHelper {

	public final static String PM_ID = "jhkim";
	public final static String SUB_PM_ID = "jhkim";

//	public final static String PM_ID = "yspark";
//	public final static String SUB_PM_ID = "19940009";

	public static final String STAND = "준비중";
	public static final String INWORK = "작업 중";
	public static final String DELAY = "지연됨";
	public static final String COMPLETE = "완료됨";
	public static final String STOP = "중단됨";

	public static final String[] GATE1 = { "공정계획서", "사양체크리스트", "DR자료", "Risk Management" };
	public static final String[] GATE2 = { "1차_수배", "1차_수배" };
	public static final String[] GATE3 = { "전기_제작사양서", "기계_제작사양서" };
	public static final String[] GATE4 = { "2차_수배", "2차_수배" };
	public static final String[] GATE5 = { "전기_작업지시서", "기계_작업지시서", "설비사양서" };
	public static final String[] QTASK = { "사양체크리스트", "Risk Management", "견적설계검토서", "DR자료", "고객제출용자료" };

	public static final String REQ_TASK_NAME = "의뢰서";

	public static final ProjectService service = ServiceFactory.getService(ProjectService.class);
	public static final ProjectHelper manager = new ProjectHelper();

	public int getElecAllocateProgress(Project project) throws Exception {
		int progress = 0;

		ArrayList<Task> list = new ArrayList<Task>();
		list = getterProjectTask(project, list);

		int sumAllocate = 0;
		int sumProgress = 0;
		// 모든 태스크 수집

		for (Task task : list) {

			String taskType = task.getTaskType().getName();

			if ("전기".equals(taskType)) {

				int allocate = task.getAllocate() != null ? task.getAllocate() : 0;
				int tprogress = task.getProgress() != null ? task.getProgress() : 0;

				sumProgress += (allocate * tprogress);

				sumAllocate += allocate;

				// 기계 진행율 = SUM(기계Task 각각의 할당률xTask 진행률)/SUM(기계Task 각각의 할당률)
			}

		}

		if (sumAllocate != 0) {
			progress = sumProgress / sumAllocate;
		}
		return progress;
	}

	public int getMachineAllocateProgress(Project project) throws Exception {
		int progress = 0;

		ArrayList<Task> list = new ArrayList<Task>();

		list = getterProjectTask(project, list);

		int sumAllocate = 0;
		int sumProgress = 0;
		// 모든 태스크 수집
		for (Task task : list) {

			String taskType = task.getTaskType().getName();

			if ("기계".equals(taskType)) {

				int allocate = task.getAllocate() != null ? task.getAllocate() : 0;
				int tprogress = task.getProgress() != null ? task.getProgress() : 0;

				sumProgress += (allocate * tprogress);

				sumAllocate += allocate;

				// 기계 진행율 = SUM(기계Task 각각의 할당률xTask 진행률)/SUM(기계Task 각각의 할당률)
			}

		}

		if (sumAllocate != 0) {
			progress = sumProgress / sumAllocate;
		}
		return progress;
	}

	public ArrayList<Task> getterProjectTask(Project project, ArrayList<Task> list) throws Exception {

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);
		long ids = project.getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(Task.class, "projectReference.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=", 0L);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(Task.class, Task.DEPTH, "=", 1);
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
		OrderBy orderBy = new OrderBy(ca, false);
		query.appendOrderBy(orderBy, new int[] { idx });

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Task t = (Task) obj[0];
			list.add(t);
			getterTasks(t, project, list);
		}
		return list;
	}

	public void getterTasks(Task parentTask, Project project, ArrayList<Task> list) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);

		long ids = parentTask.getPersistInfo().getObjectIdentifier().getId();
		long pids = project.getPersistInfo().getObjectIdentifier().getId();

		SearchCondition sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(Task.class, "projectReference.key.id", "=", pids);
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
		OrderBy orderBy = new OrderBy(ca, false);
		query.appendOrderBy(orderBy, new int[] { idx });

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Task t = (Task) obj[0];
			list.add(t);
			getterTasks(t, project, list);
		}
	}

	public WTUser getUserType(Project project, String userType) throws Exception {
		CommonCode userTypeCode = CommonCodeHelper.manager.getCommonCode(userType, "USER_TYPE");
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ProjectUserLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, ProjectUserLink.class, "roleAObjectRef.key.id", project);
		QuerySpecUtils.toEqualsAnd(query, idx, ProjectUserLink.class, "userTypeReference.key.id", userTypeCode);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ProjectUserLink link = (ProjectUserLink) obj[0];
			return link.getUser();
		}
		return null;
	}

//
//	public int getProjectTaskProgress(Task task, boolean isNormalTask) throws Exception {
//		int progress = 0;
//		// 일반 태스크 일경우
//		if (isNormalTask) {
//			ArrayList<DocumentOutputLink> list = getProjectOutputLink(task);
//			int count = list.size();
//			int data = 0;
//			for (DocumentOutputLink link : list) {
//				LifeCycleManaged document = link.getDocument();
//
//				if (document.getLifeCycleState().toString().equals("APPROVED")
//						|| document.getLifeCycleState().toString().equals("RELEASED")) {
//					data += 1;
//				}
//			}
//
//			if (data == 0) {
//				progress = 0;
//			} else {
//				BigDecimal counting = new BigDecimal(data);
//				BigDecimal total = new BigDecimal(count);
//
//				BigDecimal result = counting.divide(total, 2, BigDecimal.ROUND_FLOOR);
//				progress = (int) (result.doubleValue() * 100);
//			}
//		}
//		return progress;
//	}
//
//	public String getProjectPartListTask(Project project, Task tt) throws Exception {
//		String tname = "1차_수배";
//		Task task = null;
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(Task.class, true);
//
//		SearchCondition sc = new SearchCondition(Task.class, "projectReference.key.id", "=",
//				project.getPersistInfo().getObjectIdentifier().getId());
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=",
//				tt.getPersistInfo().getObjectIdentifier().getId());
//		query.appendWhere(sc, new int[] { idx });
//
//		QueryResult result = PersistenceHelper.manager.find(query);
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			task = (Task) obj[0];
//
//			QueryResult qr = PersistenceHelper.manager.navigate(task, "output", TaskOutputLink.class);
//			if (qr.size() >= 1) {
//				tname = "2차_수배";
//			}
//
//		}
//		return tname;
//	}
//
//	public Task getProjectTaskByName(Project project, String location) throws Exception {
//
//		int end = location.lastIndexOf("/");
//		Task task = null;
//		if (end > -1) {
//			String taskName = location.substring(end + 1);
//
//			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
//
//			Department dept = OrgHelper.manager.getDepartment(user);
//
//			if ("작업지시서".equals(taskName.trim())) {
//				if ("기계설계".equals(dept.getName())) {
//					taskName = "기계_" + taskName;
//				} else if ("전기설계".equals(dept.getName())) {
//					taskName = "전기_" + taskName;
//				}
//			} else if ("도면일람표".equals(taskName.trim())) {
//				if ("기계설계".equals(dept.getName())) {
//					taskName = "기계_" + taskName;
//				} else if ("전기설계".equals(dept.getName())) {
//					taskName = "전기_" + taskName;
//				}
//			} else if ("제작사양서".equals(taskName.trim())) {
//				if ("기계설계".equals(dept.getName())) {
//					taskName = "기계_" + taskName;
//				} else if ("전기설계".equals(dept.getName())) {
//					taskName = "전기_" + taskName;
//				}
//			}
//
//			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Task.class, true);
//
//			SearchCondition sc = new SearchCondition(Task.class, "projectReference.key.id", "=",
//					project.getPersistInfo().getObjectIdentifier().getId());
//			query.appendWhere(sc, new int[] { idx });
//			query.appendAnd();
//
//			sc = new SearchCondition(Task.class, Task.NAME, "=", taskName);
//			query.appendWhere(sc, new int[] { idx });
//
//			QueryResult result = PersistenceHelper.manager.find(query);
//			while (result.hasMoreElements()) {
//				Object[] obj = (Object[]) result.nextElement();
//				task = (Task) obj[0];
//
//				if (taskName.equals(task.getName())) {
//					break;
//				}
//			}
//		}
//
//		return task;
//	}
//
//	public Task getProjectTaskByName(Project project, String location, String engType) throws Exception {
//
//		int end = location.lastIndexOf("/");
//		Task task = null;
//		if (end > -1) {
//			String taskName = location.substring(end + 1);
//
//			// 1차 수배..
//
//			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Task.class, true);
//
//			SearchCondition sc = new SearchCondition(Task.class, "projectReference.key.id", "=",
//					project.getPersistInfo().getObjectIdentifier().getId());
//			query.appendWhere(sc, new int[] { idx });
//			query.appendAnd();
//
//			sc = new SearchCondition(Task.class, Task.NAME, "=", taskName);
//			query.appendWhere(sc, new int[] { idx });
//
//			QueryResult result = PersistenceHelper.manager.find(query);
//			while (result.hasMoreElements()) {
//				Object[] obj = (Object[]) result.nextElement();
//				task = (Task) obj[0];
//
//				if (taskName.equals(task.getName())) {
//					break;
//				}
//			}
//		}
//
//		if (task == null) {
//			if (engType.equals("기계")) {
//				QuerySpec query = new QuerySpec();
//				int idx = query.appendClassList(Task.class, true);
//
//				SearchCondition sc = new SearchCondition(Task.class, "projectReference.key.id", "=",
//						project.getPersistInfo().getObjectIdentifier().getId());
//				query.appendWhere(sc, new int[] { idx });
//				query.appendAnd();
//
//				sc = new SearchCondition(Task.class, Task.NAME, "=", "기계_수배표작성");
//				query.appendWhere(sc, new int[] { idx });
//
//				QueryResult result = PersistenceHelper.manager.find(query);
//				while (result.hasMoreElements()) {
//					Object[] obj = (Object[]) result.nextElement();
//					task = (Task) obj[0];
//
//					if ("기계_수배표작성".equals(task.getName())) {
//						break;
//					}
//				}
//			} else if (engType.equals("전기")) {
//				QuerySpec query = new QuerySpec();
//				int idx = query.appendClassList(Task.class, true);
//
//				SearchCondition sc = new SearchCondition(Task.class, "projectReference.key.id", "=",
//						project.getPersistInfo().getObjectIdentifier().getId());
//				query.appendWhere(sc, new int[] { idx });
//				query.appendAnd();
//
//				sc = new SearchCondition(Task.class, Task.NAME, "=", "전기_수배표작성");
//				query.appendWhere(sc, new int[] { idx });
//
//				QueryResult result = PersistenceHelper.manager.find(query);
//				while (result.hasMoreElements()) {
//					Object[] obj = (Object[]) result.nextElement();
//					task = (Task) obj[0];
//
//					if ("전기_수배표작성".equals(task.getName())) {
//						break;
//					}
//				}
//			}
//		}
//
//		return task;
//	}
//
//	public Project getProjectByKekNumber(Map<String, Object> param) throws Exception {
//		String ss = (String) param.get("kekNumber");
//
//		String kekNumber = ss.split("/")[0];
//		String pType = ss.split("/")[1];
//
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(Project.class, true);
//
//		SearchCondition sc = new SearchCondition(Project.class, Project.KEK_NUMBER, "=", kekNumber);
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Project.class, Project.P_TYPE, "=", pType);
//		query.appendWhere(sc, new int[] { idx });
//
//		QueryResult result = PersistenceHelper.manager.find(query);
//		Project project = null;
//		if (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			project = (Project) obj[0];
//		}
//		return project;
//	}
//
//	public ArrayList<TargetTaskSourceTaskLink> getTargetTaskSourceTaskLinkByTarget(Task task) throws Exception {
//		ArrayList<TargetTaskSourceTaskLink> list = new ArrayList<TargetTaskSourceTaskLink>();
//
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(TargetTaskSourceTaskLink.class, true);
//
//		long ids = task.getPersistInfo().getObjectIdentifier().getId();
//		SearchCondition sc = new SearchCondition(TargetTaskSourceTaskLink.class, "roleBObjectRef.key.id", "=", ids);
//		query.appendWhere(sc, new int[] { idx });
//
//		query.appendAnd();
//
//		long tids = task.getProject().getPersistInfo().getObjectIdentifier().getId();
//		sc = new SearchCondition(TargetTaskSourceTaskLink.class, "projectReference.key.id", "=", tids);
//		query.appendWhere(sc, new int[] { idx });
//
//		// QueryResult result = PersistenceHelper.manager.navigate(task, "targetTask",
//		// TargetTaskSourceTaskLink.class,
//		// false);
//		QueryResult result = PersistenceHelper.manager.find(query);
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			// TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink)
//			// result.nextElement();
//			TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink) obj[0];
//			list.add(link);
//		}
//		return list;
//	}
//
	public static double getPreferComp(Task task) throws Exception {
		double preferComp = 0;
		Timestamp today = DateUtils.getCurrentTimestamp();
		String cdate = DateUtils.getDateString(today, "date");
		String sdate = DateUtils.getDateString(task.getPlanStartDate(), "date");
		String edate = DateUtils.getDateString(task.getPlanEndDate(), "date");

		if (cdate.compareTo(sdate) < 0) {
			preferComp = 0;
		} else if (cdate.compareTo(edate) >= 0) {
			preferComp = 100;
		} else {
			double du = DateUtils.getPlanDurationHoliday(task.getPlanStartDate(), today);
			double planDuration = DateUtils.getPlanDurationHoliday(task.getPlanStartDate(), task.getPlanEndDate());

			if (planDuration == 0) {
				preferComp = 0;
			} else {
				preferComp = (du / planDuration) * 100;
			}
		}
		return preferComp;
	}

	public static double getPreferComp(Project project) throws Exception {
		double preferComp = 0;

		Timestamp today = DateUtils.getCurrentTimestamp();
		String cdate = DateUtils.getDateString(today, "date");
		String sdate = DateUtils.getDateString(project.getPlanStartDate(), "date");
		String edate = DateUtils.getDateString(project.getPlanEndDate(), "date");

		if (cdate.compareTo(sdate) < 0) {
			preferComp = 0;
		} else if (cdate.compareTo(edate) >= 0) {
			preferComp = 100;
		} else {
			double du = DateUtils.getPlanDurationHoliday(project.getPlanStartDate(), today);
			double planDuration = DateUtils.getPlanDurationHoliday(project.getPlanStartDate(),
					project.getPlanEndDate());

			if (planDuration == 0) {
				preferComp = 0;
			} else {
				preferComp = (du / planDuration) * 100;
			}
		}

		return preferComp;
	}

	public String getProjectStateBar(Project project) throws Exception {

		int progress = ProjectHelper.manager.getKekProgress(project);
		int comp = (int) ProjectHelper.getPreferComp(project);

		int gap = progress - comp;
		String bar = "";

		if (COMPLETE.equals(project.getState())) {
			bar += "<img title='프로젝트 완료 되었습니다.' class='pos3' src='/Windchill/jsp/images/state_green_bar.gif'>";
			bar += "<img title='프로젝트 완료 되었습니다.' class='pos3' src='/Windchill/jsp/images/state_green_bar.gif'>";
			bar += "<img title='프로젝트 완료 되었습니다.' class='pos3' src='/Windchill/jsp/images/state_green_bar.gif'>";
		} else {

			if (progress != 100) {

				// 프로젝트 시작
				if (!"준비".equals(project.getKekState())) {
					if (gap < 0 && gap >= -30) {
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.gif'>";
					} else if (gap < -30) {
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_red_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_red_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_red_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_red_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_red_bar.gif'>";
					} else if (gap == 0) {
						bar += "<img class='pos3' src='/Windchill/jsp/images/state_blank_bar.gif'>";
						bar += "<img class='pos3' src='/Windchill/jsp/images/state_blank_bar.gif'>";
						bar += "<img class='pos3' src='/Windchill/jsp/images/state_blank_bar.gif'>";
						bar += "<img class='pos3' src='/Windchill/jsp/images/state_blank_bar.gif'>";
						bar += "<img class='pos3' src='/Windchill/jsp/images/state_blank_bar.gif'>";
					} else if (gap > 0) {
						bar += "<img title='프로젝트 정상진행 중 입니다.' class='pos3' src='/Windchill/jsp/images/state_blue_bar.gif'>";
						bar += "<img title='프로젝트 정상진행 중 입니다.' class='pos3' src='/Windchill/jsp/images/state_blue_bar.gif'>";
						bar += "<img title='프로젝트 정상진행 중 입니다.' class='pos3' src='/Windchill/jsp/images/state_blue_bar.gif'>";
						bar += "<img title='프로젝트 정상진행 중 입니다.' class='pos3' src='/Windchill/jsp/images/state_blue_bar.gif'>";
						bar += "<img title='프로젝트 정상진행 중 입니다.' class='pos3' src='/Windchill/jsp/images/state_blue_bar.gif'>";
					}
				} else {
					if (gap > 0) {
						bar += "<img title='프로젝트 시작전 입니다.' class='pos3' src='/Windchill/jsp/images/state_blank_bar.gif'>";
						bar += "<img title='프로젝트 시작전 입니다.' class='pos3' src='/Windchill/jsp/images/state_blank_bar.gif'>";
						bar += "<img title='프로젝트 시작전 입니다.' class='pos3' src='/Windchill/jsp/images/state_blank_bar.gif'>";
						bar += "<img title='프로젝트 시작전 입니다.' class='pos3' src='/Windchill/jsp/images/state_blank_bar.gif'>";
						bar += "<img title='프로젝트 시작전 입니다.' class='pos3' src='/Windchill/jsp/images/state_blank_bar.gif'>";
					} else if (gap < 0 && gap >= -30) {
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.gif'>";
					} else if (gap < -30) {
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_red_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_red_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_red_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_red_bar.gif'>";
						bar += "<img title='프로젝트 지연되고 있습니다.' class='pos3' src='/Windchill/jsp/images/state_red_bar.gif'>";
					}
				}
			} else if (progress == 100) {
				bar += "<img title='프로젝트 완료 되었습니다.' class='pos3' src='/Windchill/jsp/images/state_green_bar.gif'>";
				bar += "<img title='프로젝트 완료 되었습니다.' class='pos3' src='/Windchill/jsp/images/state_green_bar.gif'>";
				bar += "<img title='프로젝트 완료 되었습니다.' class='pos3' src='/Windchill/jsp/images/state_green_bar.gif'>";
			}
		}
		return bar;
	}

	public String getStateIcon(int type) throws Exception {
		if (type == 0) {
			return "<img title='태스크 시작전 입니다.' style='position: relative; top: 3px;' src='/Windchill/extcore/images/project/state_blank_bar.png'>";
		} else if (type == 1) {
			return "<img title='태스크가 계획 종료일 보다 초과 되서 진행 중 입니다.' style='position: relative; top: 3px;' class='' src='/Windchill/extcore/images/project/state_red_bar.png'>";
		} else if (type == 2) {
			return "<img title='태스크가 진행 중 입니다.' style='position: relative; top: 3px;' src='/Windchill/extcore/images/project/state_yellow_bar.png'>";
		} else if (type == 3) {
			return "<img title='태스크 지연 입니다.' style='position: relative; top: 3px;' src='/Windchill/extcore/images/project/state_orange_bar.png'>";
		} else if (type == 4) {
			return "<img title='태스크 완료 입니다.' style='position: relative; top: 3px;' src='/Windchill/extcore/images/project/state_green_bar.png'>";
		}
		return "";
	}

	public ArrayList<Map<String, Object>> remoter(Map<String, Object> params) throws Exception {
		String term = (String) params.get("term");
		ArrayList<Map<String, Object>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.KEK_NUMBER, term);
		QuerySpecUtils.toOrderBy(query, idx, Project.class, Project.KEK_NUMBER, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Project project = (Project) obj[0];
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("key", project.getPersistInfo().getObjectIdentifier().getStringValue());
			data.put("value", project.getKekNumber());
			list.add(data);
		}
		return list;
	}

	/**
	 * 작번 조회 함수
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		System.out.println("작번검색 START = " + new Timestamp(new Date().getTime()));
		Map<String, Object> map = new HashMap<String, Object>();
		List<ProjectDTO> list = new ArrayList<ProjectDTO>();

		String kekNumber = (String) params.get("kekNumber");
		String keNumber = (String) params.get("keNumber");
		String pdateFrom = (String) params.get("pdateFrom");
		String pdateTo = (String) params.get("pdateTo");
		String userId = (String) params.get("userId");
		String kekState = (String) params.get("kekState");
		String model = (String) params.get("model");
		String customer_name = (String) params.get("customer_name");
		String install_name = (String) params.get("install_name");
		String projectType = (String) params.get("projectType");
		String machineOid = (String) params.get("machineOid");
		String elecOid = (String) params.get("elecOid");
		String softOid = (String) params.get("softOid");
		String mak_name = (String) params.get("mak_name");
		String detail_name = (String) params.get("detail_name");
		String template = (String) params.get("template");
		String description = (String) params.get("description");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.KEK_NUMBER, kekNumber);
		QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.KE_NUMBER, keNumber);
		QuerySpecUtils.toTimeGreaterAndLess(query, idx, Project.class, Project.P_DATE, pdateFrom, pdateTo);
		QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.USER_ID, userId);
		QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.KEK_STATE, kekState);
		QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.MODEL, model);

		if (!StringUtils.isNull(customer_name)) {
			CommonCode customerCode = (CommonCode) CommonUtils.getObject(customer_name);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "customerReference.key.id", customerCode);
		}

		if (!StringUtils.isNull(install_name)) {
			CommonCode installCode = (CommonCode) CommonUtils.getObject(install_name);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "installReference.key.id", installCode);
		}

		if (!StringUtils.isNull(projectType)) {
			CommonCode projectTypeCode = (CommonCode) CommonUtils.getObject(projectType);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "projectTypeReference.key.id", projectTypeCode);
		}

		if (!StringUtils.isNull(machineOid)) {
			WTUser machine = (WTUser) CommonUtils.getObject(machineOid);
			CommonCode machineCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
			int idx_u = query.appendClassList(WTUser.class, false);

			QuerySpecUtils.toInnerJoin(query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", idx, idx_plink);
			QuerySpecUtils.toInnerJoin(query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", idx_u, idx_plink);
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", machine);
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id",
					machineCode);
		}

		if (!StringUtils.isNull(elecOid)) {
			WTUser elec = (WTUser) CommonUtils.getObject(machineOid);
			CommonCode elecCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
			int idx_u = query.appendClassList(WTUser.class, false);

			QuerySpecUtils.toInnerJoin(query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", idx, idx_plink);
			QuerySpecUtils.toInnerJoin(query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", idx_u, idx_plink);
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", elec);
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id", elecCode);
		}

		if (!StringUtils.isNull(softOid)) {
			WTUser soft = (WTUser) CommonUtils.getObject(machineOid);
			CommonCode softCode = CommonCodeHelper.manager.getCommonCode("MACHINE", "USER_TYPE");
			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
			int idx_u = query.appendClassList(WTUser.class, false);

			QuerySpecUtils.toInnerJoin(query, Project.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleAObjectRef.key.id", idx, idx_plink);
			QuerySpecUtils.toInnerJoin(query, WTUser.class, ProjectUserLink.class, WTAttributeNameIfc.ID_NAME,
					"roleBObjectRef.key.id", idx_u, idx_plink);
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "roleBObjectRef.key.id", soft);
			QuerySpecUtils.toEqualsAnd(query, idx_plink, ProjectUserLink.class, "userTypeReference.key.id", softCode);
		}

		if (!StringUtils.isNull(mak_name)) {
			CommonCode makCode = (CommonCode) CommonUtils.getObject(mak_name);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "makReference.key.id", makCode);
		}

		if (!StringUtils.isNull(detail_name)) {
			CommonCode detailCode = (CommonCode) CommonUtils.getObject(detail_name);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "detailReference.key.id", detailCode);
		}

		if (!StringUtils.isNull(template)) {
			Template t = (Template) CommonUtils.getObject(template);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "templateReference.key.id", t);
		}

		QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.DESCRIPTION, description);
		QuerySpecUtils.toOrderBy(query, idx, Project.class, Project.P_DATE, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Project project = (Project) obj[0];
			ProjectDTO column = new ProjectDTO(project);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		System.out.println("작번검색 END = " + new Timestamp(new Date().getTime()));
		return map;
	}

	public Map<String, Object> get(String kekNumber) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, Project.class, Project.KEK_NUMBER, kekNumber);

		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Project project = (Project) obj[0];
			map.put("kekNumber", kekNumber);
			map.put("keNumber", project.getKeNumber());
			map.put("mak", project.getMak() != null ? project.getMak().getName() : "");
			map.put("install", project.getInstall() != null ? project.getInstall().getName() : "");
			map.put("pDate", project.getPDate());
			map.put("customer", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		return map;
	}

	/**
	 * 프로젝트 트리
	 */
	public JSONArray load(String oid) throws Exception {
		Project project = (Project) CommonUtils.getObject(oid);
		JSONArray list = new JSONArray();
		JSONObject node = new JSONObject();
		node.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
		node.put("name", project.getKekNumber());
		node.put("description", project.getDescription());
		node.put("duration", project.getDuration());
		node.put("allocate", 0);
		node.put("taskType", project.getProjectType().getName());
		node.put("treeIcon", getTreeIcon(project));
		node.put("type", "project");
		node.put("isNew", false);

		JSONArray childrens = new JSONArray();
		ArrayList<Task> taskList = TaskHelper.manager.getProjectTasks(project);
		for (Task task : taskList) {
			JSONObject children = new JSONObject();
			children.put("oid", task.getPersistInfo().getObjectIdentifier().getStringValue());
			children.put("name", task.getName());
			children.put("description", task.getDescription());
			children.put("duration", task.getDuration());
			children.put("allocate", task.getAllocate() != null ? task.getAllocate() : 0);
			children.put("taskType", task.getTaskType().getCode());
			children.put("type", "task");
			children.put("isNew", false);
			children.put("treeIcon", getTreeIcon(task));
			load(children, project, task);
			childrens.add(children);
		}
		node.put("children", childrens);
		list.add(node);
		return list;
	}

	/**
	 * 프로젝트 태스크 트리 아이콘
	 */
	private Object getTreeIcon(Task task) {
		// 준비중, 진행중, 완료됨, 지연됨
		String icon = "/Windchill/extcore/images/task_ready.gif";

		String state = task.getState();
		if (TaskStateVariable.COMPLETE.equals(state)) {
			return "/Windchill/extcore/images/task_complete.gif";
		} else if (TaskStateVariable.READY.equals(state)) {
			return "/Windchill/extcore/images/task_ready.gif";
		} else {

			Timestamp today = DateUtils.getCurrentTimestamp();

			int du = DateUtils.getDuration(task.getPlanStartDate(), task.getPlanEndDate());
			BigDecimal counting = new BigDecimal(du);
			BigDecimal multi = new BigDecimal(0.2);

			BigDecimal result = counting.multiply(multi);
			int perDay = Math.round(result.floatValue()); // 2??

			int tdu = DateUtils.getDuration(task.getPlanEndDate(), DateUtils.getCurrentTimestamp()); // 1...

			if (task.getPlanEndDate() != null && today.getTime() > task.getPlanEndDate().getTime()) {
				return "/Windchill/extcore/images/task_delay.gif";
			} else {
				if (tdu <= perDay) {
					if (task.getProgress() < 50) {
						return "/Windchill/extcore/images/task_orange.gif";
					} else if (task.getProgress() >= 51 && task.getProgress() < 100) {
						return "/Windchill/extcore/images/task_yellow.gif";
					} else if (task.getProgress() == 100) {
						return "/Windchill/extcore/images/task_complete.gif";
					}
				} else {
					return "/Windchill/extcore/images/task_yellow.gif";
				}
			}
		}
		return icon;
	}

	/**
	 * 프로젝트 트리 아이콘 경로
	 */
	private String getTreeIcon(Project project) {
		// 준비중, 진행중, 완료됨, 지연됨
		String icon = "/Windchill/extcore/images/task_ready.gif";

		String state = project.getState();

		if (ProjectStateVariable.COMPLETE.equals(state)) {
			return "/Windchill/extcore/images/task_complete.gif";
		} else if (ProjectStateVariable.READY.equals(state)) {
			return "/Windchill/extcore/images/task_ready.gif";
		} else {
			Timestamp today = DateUtils.getCurrentTimestamp();
			int du = DateUtils.getDuration(project.getPlanStartDate(), project.getPlanEndDate());
			BigDecimal counting = new BigDecimal(du);
			BigDecimal multi = new BigDecimal(0.2);

			BigDecimal result = counting.multiply(multi);
			int perDay = Math.round(result.floatValue()); // 2??

			int tdu = DateUtils.getDuration(project.getPlanEndDate(), DateUtils.getCurrentTimestamp()); // 1...

			if (today.getTime() > project.getPlanEndDate().getTime()) {
				return "/Windchill/extcore/images/task_delay.gif";
			} else {
				if (tdu <= perDay) {
					if (project.getProgress() < 50) {
						return "/Windchill/extcore/images/task_orange.gif";
					} else if (project.getProgress() >= 51 && project.getProgress() < 100) {
						return "/Windchill/extcore/images/task_yellow.gif";
					} else if (project.getProgress() == 100) {
						return "/Windchill/extcore/images/task_complete.gif";
					}
				} else {
					return "/Windchill/extcore/images/task_yellow.gif";
				}
			}
		}
		return icon;
	}

	/**
	 * 프로젝트 태스크 트리
	 */
	private void load(JSONObject node, Project project, Task parentTask) throws Exception {
		JSONArray childrens = new JSONArray();
		ArrayList<Task> taskList = TaskHelper.manager.getProjectTasks(project, parentTask);
		for (Task task : taskList) {
			JSONObject children = new JSONObject();
			children.put("oid", task.getPersistInfo().getObjectIdentifier().getStringValue());
			children.put("name", task.getName());
			children.put("description", task.getDescription());
			children.put("duration", task.getDuration());
			children.put("allocate", task.getAllocate() != null ? task.getAllocate() : 0);
			children.put("taskType", task.getTaskType().getCode());
			children.put("type", "task");
			children.put("isNew", false);
			children.put("treeIcon", getTreeIcon(task));
			load(children, project, task);
			childrens.add(children);
		}
		node.put("children", childrens);
	}

	/**
	 * 프로젝트 참조 작번
	 */
	public JSONArray referenceTab(String oid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		Project project = (Project) CommonUtils.getObject(oid);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);
		QuerySpecUtils.toLikeRightAnd(query, idx, Project.class, Project.KEK_NUMBER, project.getKekNumber() + "%");
		QuerySpecUtils.toNotEqualsAnd(query, idx, Project.class, "projectTypeReference.key.id",
				project.getProjectType());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Project p = (Project) obj[0];
			Map<String, Object> map = new HashMap<>();
			map.put("kekNumber", p.getKekNumber());
			map.put("projectType_name", p.getProjectType().getName());
			map.put("mak_name", p.getMak().getName());
			map.put("detail_name", p.getDetail().getName());
			map.put("customer_name", p.getCustomer().getName());
			map.put("install_name", p.getInstall().getName());
			map.put("description", p.getDescription());
			map.put("pdate_txt", CommonUtils.getPersistableTime(p.getPDate()));
			map.put("customDate_txt", CommonUtils.getPersistableTime(p.getCustomDate()));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 태스크 정보 의뢰서 데이터 가져오기
	 */
	public JSONArray jsonAuiRequest(Project project) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(RequestDocumentProjectLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, RequestDocumentProjectLink.class, "roleBObjectRef.key.id", project);
		QuerySpecUtils.toOrderBy(query, idx, RequestDocumentProjectLink.class,
				RequestDocumentProjectLink.CREATE_TIMESTAMP, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			RequestDocumentProjectLink link = (RequestDocumentProjectLink) obj[0];
			RequestDocument requestDocument = link.getRequestDocument();
			Map<String, String> map = new HashMap<>();
			map.put("oid", requestDocument.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", requestDocument.getName());
			map.put("version", requestDocument.getVersionIdentifier().getSeries().getValue() + "."
					+ requestDocument.getIterationIdentifier().getSeries().getValue());
			map.put("state", requestDocument.getLifeCycleState().getDisplay());
			map.put("creator", requestDocument.getCreatorFullName());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(requestDocument.getCreateTimestamp()));
			map.put("primary", AUIGridUtils.primaryTemplate(requestDocument));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 작번 기본 산출물
	 */
	public JSONArray jsonAuiNormal(Project project, Task task) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Output.class, true); // ... inner join 필요 없을..
		QuerySpecUtils.toEqualsAnd(query, idx, Output.class, "taskReference.key.id", task);
		QuerySpecUtils.toEqualsAnd(query, idx, Output.class, "projectReference.key.id", project);
		QuerySpecUtils.toOrderBy(query, idx, Output.class, Output.CREATE_TIMESTAMP, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Output output = (Output) obj[0];
			WTDocument document = (WTDocument) output.getDocument();
			Map<String, String> map = new HashMap<>();
			map.put("oid", output.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", document.getName());
			map.put("version", document.getVersionIdentifier().getSeries().getValue() + "."
					+ document.getIterationIdentifier().getSeries().getValue());
			map.put("state", document.getLifeCycleState().getDisplay());
			map.put("creator", document.getCreatorFullName());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(document.getCreateTimestamp()));
			map.put("primary", AUIGridUtils.primaryTemplate(document));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 태스크 이름이 일치하는 태스크가 있는지 검색 프로젝트 내에서
	 */
	public Task getTaskByName(Project project, String name) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "projectReference.key.id", project);
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, Task.NAME, name);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			return (Task) obj[0];
		}
		return null;
	}

	/**
	 * 국제에 맞는 KEK 진행율 가져오기 계산식 들어감
	 */
	public int getKekProgress(Project project) throws Exception {
		int kekProgress = 0;
		String[] taskTypes = new String[] { "COMMON", "MACHINE", "ELEC", "SOFT" };
		ArrayList<Task> list = recurciveTask(project, taskTypes);

		int sumAllocate = 0;
		int sumProgress = 0;
		for (Task task : list) {

			int allocate = task.getAllocate() != null ? task.getAllocate() : 0;
			int tprogress = task.getProgress() != null ? task.getProgress() : 0;

			sumProgress += (allocate * tprogress);
			sumAllocate += allocate;
		}

		if (sumAllocate != 0) {
			kekProgress = sumProgress / sumAllocate;
		}
		return kekProgress;
	}

	public ArrayList<Task> recurciveTask(Project project) throws Exception {
		return recurciveTask(project, null);
	}

	/**
	 * 모든 프로젝트 태스크 재귀함수 태스크 타입 조건 추가
	 */
	public ArrayList<Task> recurciveTask(Project project, String[] taskTypes) throws Exception {
		ArrayList<Task> list = new ArrayList<Task>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "projectReference.key.id",
				project.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "parentTaskReference.key.id", 0L);

		if (taskTypes != null) {
			long[] ids = new long[taskTypes.length];
			for (int i = 0; i < taskTypes.length; i++) {
				String taskType = taskTypes[i];
				CommonCode taskTypeCode = CommonCodeHelper.manager.getCommonCode(taskType, "TASK_TYPE");
				ids[i] = taskTypeCode.getPersistInfo().getObjectIdentifier().getId();
			}
			QuerySpecUtils.toIn(query, idx, Task.class, "taskTypeReference.key.id", ids);
		}

		QuerySpecUtils.toOrderBy(query, idx, Task.class, Task.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Task task = (Task) obj[0];
			list.add(task);
			recurciveTask(project, task, list, taskTypes);
		}
		return list;
	}

	/**
	 * 모든 프로젝트 태스크 재귀함수 태스크 타입 조건 추가
	 */
	private void recurciveTask(Project project, Task parentTask, ArrayList<Task> list, String[] taskTypes)
			throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "projectReference.key.id",
				project.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "parentTaskReference.key.id", parentTask);
		if (taskTypes != null) {
			long[] ids = new long[taskTypes.length];
			for (int i = 0; i < taskTypes.length; i++) {
				String taskType = taskTypes[i];
				CommonCode taskTypeCode = CommonCodeHelper.manager.getCommonCode(taskType, "TASK_TYPE");
				ids[i] = taskTypeCode.getPersistInfo().getObjectIdentifier().getId();
			}
			QuerySpecUtils.toIn(query, idx, Task.class, "taskTypeReference.key.id", ids);
		}

		QuerySpecUtils.toOrderBy(query, idx, Task.class, Task.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Task task = (Task) obj[0];
			list.add(task);
			recurciveTask(project, task, list, taskTypes);
		}
	}

	/**
	 * 프로젝트 수배표 통합목록
	 */
	public JSONArray jsonAuiPartlist(Project project, Task task) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();

		QueryResult qr = PersistenceHelper.manager.navigate(task, "childTask", ParentTaskChildTaskLink.class);
		while (qr.hasMoreElements()) {
			Task child = (Task) qr.nextElement();
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Output.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, Output.class, "projectReference.key.id", project);
			QuerySpecUtils.toEqualsAnd(query, idx, Output.class, "taskReference.key.id", child);
			QuerySpecUtils.toOrderBy(query, idx, Output.class, Output.CREATE_TIMESTAMP, true);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Output output = (Output) obj[0];
				PartListMaster master = (PartListMaster) output.getDocument();
				Map<String, String> map = new HashMap<>();
				map.put("oid", master.getPersistInfo().getObjectIdentifier().getStringValue());
				map.put("name", master.getName());
				map.put("engType", master.getEngType());
				map.put("state", master.getLifeCycleState().getDisplay());
				map.put("creator", master.getCreatorFullName());
				map.put("createdDate_txt", CommonUtils.getPersistableTime(master.getCreateTimestamp()));
				map.put("secondary", AUIGridUtils.secondaryTemplate(master));
				list.add(map);
			}
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 부모 태스크와 연관된 자식 태스크 가져오기.. 수배표에서만 사용용도
	 */
	public Task getTaskByParent(Project project, Task parentTask) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "projectReference.key.id", project);
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "parentTaskReference.key.id", parentTask);
		QuerySpecUtils.toOrderBy(query, idx, Task.class, Task.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		Task task = null;
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			task = (Task) obj[0];
			// 기존 태스크랑 연결된 수배표가 있다면 패스 시킨다. // 0개 1차수배 2차수배 1개이후 쿼리대로 2차 수배만 계속 리턴
			QueryResult qr = PersistenceHelper.manager.navigate(task, "output", OutputTaskLink.class);
			if (qr.size() == 0) {
				break;
			}
		}
		return task;
	}

	/**
	 * 프로젝트 태스크 전체 진행율 계산
	 */
	public void calculation(Project project) throws Exception {
		ArrayList<Task> list = recurciveTask(project);

		for (Task tt : list) {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Task.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "parentTaskReference.key.id", tt);
			QuerySpecUtils.toOrderBy(query, idx, Task.class, Task.SORT, false);
			QueryResult result = PersistenceHelper.manager.find(query);
			int sum = 0;
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Task task = (Task) obj[0];
				sum += task.getProgress();
			}

			if (result.size() != 0) {
				int comp = sum / result.size();
				tt.setProgress(comp);

				if (tt.getStartDate() == null) {
					tt.setStartDate(DateUtils.getCurrentTimestamp());
				}

				tt.setState(TaskStateVariable.INWORK);

				if (comp == 100) {
					// 실제 완료일이 매번 변경안되도록
					if (tt.getEndDate() == null) {
						tt.setEndDate(DateUtils.getCurrentTimestamp());
					}
					// 어차피 100일 경우 무조건 완료
					tt.setState(TaskStateVariable.COMPLETE);
				}
				PersistenceHelper.manager.modify(tt);
			}
		}

	}

	/**
	 * 각 태스크와 관련된 수배표 리스트
	 */
	public JSONArray jsonAuiStepPartlist(Project project, Task task) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Output.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, Output.class, "projectReference.key.id", project);
		QuerySpecUtils.toEqualsAnd(query, idx, Output.class, "taskReference.key.id", task);
		QuerySpecUtils.toOrderBy(query, idx, Output.class, Output.CREATE_TIMESTAMP, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Output output = (Output) obj[0];
			PartListMaster master = (PartListMaster) output.getDocument();
			Map<String, String> map = new HashMap<>();
			map.put("oid", master.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", master.getName());
			map.put("engType", master.getEngType());
			map.put("state", master.getLifeCycleState().getDisplay());
			map.put("creator", master.getCreatorFullName());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(master.getCreateTimestamp()));
			map.put("primary", AUIGridUtils.primaryTemplate(master));
			list.add(map);
		}

		return JSONArray.fromObject(list);
	}

	/**
	 * 기계 전기 진행율
	 */
	public int getTaskProgress(Project project, String taskType) throws Exception {
		int progress = 0;
		ArrayList<Task> list = recurciveTask(project, new String[] { taskType });
		int sumAllocate = 0;
		int sumProgress = 0;
		// 모든 태스크 수집
		for (Task task : list) {
			int allocate = task.getAllocate() != null ? task.getAllocate() : 0;
			int tprogress = task.getProgress() != null ? task.getProgress() : 0;
			sumProgress += (allocate * tprogress);
			sumAllocate += allocate;
			// 기계 진행율 = SUM(기계Task 각각의 할당률xTask 진행률)/SUM(기계Task 각각의 할당률)
		}
		if (sumAllocate != 0) {
			progress = sumProgress / sumAllocate;
		}
		return progress;
	}

	/**
	 * 프로젝트 T-BOM 통합리스트
	 */
	public JSONArray jsonAuiTbom(Project project, Task task) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Output.class, true); // ... inner join 필요 없을..
		QuerySpecUtils.toEqualsAnd(query, idx, Output.class, "taskReference.key.id", task);
		QuerySpecUtils.toEqualsAnd(query, idx, Output.class, "projectReference.key.id", project);
		QuerySpecUtils.toOrderBy(query, idx, Output.class, Output.CREATE_TIMESTAMP, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Output output = (Output) obj[0];
			TBOMMaster master = (TBOMMaster) output.getDocument();
			Map<String, String> map = new HashMap<>();
			map.put("oid", master.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", master.getName());
			map.put("state", master.getLifeCycleState().getDisplay());
			map.put("creator", master.getCreatorFullName());
			map.put("createdDate_txt", CommonUtils.getPersistableTime(master.getCreateTimestamp()));
			map.put("secondary", AUIGridUtils.secondaryTemplate(master));
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 객체와 연관된 작번을 가져오는 함수 객체로 구분해서 처리
	 */
	public JSONArray jsonAuiProject(String oid) throws Exception {
		Persistable per = CommonUtils.getObject(oid);

		// 작업 지시서
		if (per instanceof WorkOrder) {
			return WorkOrderHelper.manager.jsonAuiProject(oid);
			// 수배표
		} else if (per instanceof PartListMaster) {
			return PartlistHelper.manager.jsonAuiProject(oid);
			// 회의록
		} else if (per instanceof Meeting) {
			return MeetingHelper.manager.jsonAuiProject(oid);
			// TBOM
		} else if (per instanceof TBOMMaster) {
			return TBOMHelper.manager.jsonAuiProject(oid);
		} else if (per instanceof RequestDocument) {
			return RequestDocumentHelper.manager.jsonAuiProject(oid);
			// 도면
		} else if (per instanceof EPMDocument) {
			return EpmHelper.manager.jsonAuiProject(oid);
		}
		return new JSONArray();
	}

	/**
	 * 특정 객체가 사용된 작번 확인
	 */
	public JSONArray jsonAuiReferenceProject(String oid) throws Exception {
		Persistable per = CommonUtils.getObject(oid);

		if (per instanceof KePart) {
			return KePartHelper.manager.jsonAuiReferenceProject(oid);
		} else if (per instanceof KeDrawing) {
			return KeDrawingHelper.manager.jsonAuiReferenceProject(oid);
		}
		return new JSONArray();
	}

	/**
	 * 참조 작번
	 */
	public ArrayList<Project> getReferenceBy(Project project) throws Exception {
		ArrayList<Project> list = new ArrayList<Project>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);
		QuerySpecUtils.toLikeRightAnd(query, idx, Project.class, Project.KEK_NUMBER, project.getKekNumber());
		QuerySpecUtils.toNotEqualsAnd(query, idx, Project.class, "projectTypeReference.key.id",
				project.getProjectType());
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Project p = (Project) obj[0];
			list.add(p);
		}
		return list;
	}
}
