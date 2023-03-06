package e3ps.project.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.Project;
import e3ps.project.ProjectUserLink;
import e3ps.project.beans.ProjectDTO;
import e3ps.project.task.Task;
import e3ps.project.task.service.TaskHelper;
import e3ps.project.template.Template;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class ProjectHelper {

	public final static String PM_ID = "yspark";
	public final static String SUB_PM_ID = "19940009";

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

	public static final int DAY_PER = 20;

	public static final int PRO_PER = 50;

	public void test() throws Exception {
		System.out.println("스케줄링 메소드 test()");
	}

//	public int getMaxSort(Project project) throws Exception {
//		int max = 0;
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(Task.class, true);
//
//		long tids = project.getPersistInfo().getObjectIdentifier().getId();
//		SearchCondition sc = new SearchCondition(Task.class, "projectReference.key.id", "=", tids);
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, Task.DEPTH, "=", 1);
//		query.appendWhere(sc, new int[] { idx });
//
//		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
//		OrderBy orderBy = new OrderBy(ca, false);
//		query.appendOrderBy(orderBy, new int[] { idx });
//
//		QueryResult result = PersistenceHelper.manager.find(query);
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			Task t = (Task) obj[0];
//
//			if (t.getSort() != null) {
//				max = t.getSort() + 1;
//			}
//		}
//		return max;
//	}
//
//	public void setProgress(Project project) throws Exception {
//		ArrayList<Task> list = new ArrayList<Task>();
//		list = ProjectHelper.manager.getterProjectTask(project, list);
//
//		for (Task tt : list) {
//			ArrayList<Task> slist = new ArrayList<Task>();
//
//			slist = ProjectHelper.manager.getterProjectTask(tt, project, slist);
//
//			int sum = 0;
//			for (Task task : slist) {
//				sum += task.getProgress();
//			}
//
//			if (slist.size() != 0) {
//				int comp = sum / slist.size();
//				tt.setProgress(comp);
//
//				if (tt.getStartDate() == null) {
//					tt.setStartDate(DateUtils.getCurrentTimestamp());
//				}
//
//				if (!tt.getState().equals(TaskStateType.INWORK.getDisplay())) {
//					tt.setState(TaskStateType.INWORK.getDisplay());
//				}
//
//				if (comp == 100) {
//					if (tt.getEndDate() == null) {
//						tt.setEndDate(DateUtils.getCurrentTimestamp());
//					}
//
//					if (!tt.getState().equals(TaskStateType.COMPLETE.getDisplay())) {
//						tt.setState(TaskStateType.COMPLETE.getDisplay());
//					}
//				}
//
//				// PersistenceHelper.manager.modify(tt);
//			}
//		}
//	}
//
	public int getKekProgress(Project project) throws Exception {
		int progress = 0;

		ArrayList<Task> list = new ArrayList<Task>();
		list = getterProjectTask(project, list);

		int sumAllocate = 0;
		int sumProgress = 0;
		// 모든 태스크 수집

		for (Task task : list) {

			if (task.getTaskType().getName().equals("일반")) {
				continue;
			}

			int allocate = task.getAllocate() != null ? task.getAllocate() : 0;
			int tprogress = task.getProgress() != null ? task.getProgress() : 0;

			sumProgress += (allocate * tprogress);

			sumAllocate += allocate;

			// 기계 진행율 = SUM(기계Task 각각의 할당률xTask 진행률)/SUM(기계Task 각각의 할당률)

			// 전기 진행율 = SUM(전기Task 각각의 할당률xTask 진행률)/SUM(전기Task 각각의 할당률)
			// 기계 진행율 = SUM(기계Task 각각의 할당률xTask 진행률)/SUM(기계Task 각각의 할당률)
			// 작번 진행율 = SUM(Task 각각의 할당률xTask 진행률)/SUM(Task 각각의 할당률)

		}

		if (sumAllocate != 0) {
			progress = sumProgress / sumAllocate;
		}
		return progress;
	}

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

	// public ArrayList<Task> getterProjectNonSchduleTask(Project project,
	// ArrayList<Task> list) throws Exception {
//
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(Task.class, true);
//		long ids = project.getPersistInfo().getObjectIdentifier().getId();
//		SearchCondition sc = new SearchCondition(Task.class, "projectReference.key.id", "=", ids);
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=", 0L);
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, Task.DEPTH, "=", 1);
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		// sc = new SearchCondition(Task.class, Task.TASK_TYPE, "=", "일반");
//		// query.appendWhere(sc, new int[] { idx });
//
//		sc = new SearchCondition(Task.class, Task.TASK_TYPE, SearchCondition.NOT_EQUAL, "일반");
//		query.appendWhere(sc, new int[] { idx });
//
//		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
//		OrderBy orderBy = new OrderBy(ca, false);
//		query.appendOrderBy(orderBy, new int[] { idx });
//
//		query.setAdvancedQueryEnabled(true);
//		query.setDescendantQuery(false);
//
//		QueryResult result = PersistenceHelper.manager.find(query);
//
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			Task t = (Task) obj[0];
//			list.add(t);
//			getterNonScheduleTasks(t, project, list);
//		}
//		return list;
//	}
//
//	public void getterNonScheduleTasks(Task parentTask, Project project, ArrayList<Task> list) throws Exception {
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(Task.class, true);
//
//		long ids = parentTask.getPersistInfo().getObjectIdentifier().getId();
//		long pids = project.getPersistInfo().getObjectIdentifier().getId();
//
//		SearchCondition sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=", ids);
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, "projectReference.key.id", "=", pids);
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, Task.TASK_TYPE, "=", "일반");
//		query.appendWhere(sc, new int[] { idx });
//
//		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
//		OrderBy orderBy = new OrderBy(ca, false);
//		query.appendOrderBy(orderBy, new int[] { idx });
//
//		query.setAdvancedQueryEnabled(true);
//		query.setDescendantQuery(false);
//
//		QueryResult result = PersistenceHelper.manager.find(query);
//
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			Task t = (Task) obj[0];
//			list.add(t);
//			getterTasks(t, project, list);
//		}
//	}
//

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

//
//	public Map<String, Object> checkKekNumberAction(Map<String, Object> param) throws Exception {
//		String kekNumber = (String) param.get("kekNumber");
//		String pType = (String) param.get("pType");
//		int index = (int) param.get("index");
//		Map<String, Object> map = new HashMap<String, Object>();
//
//		try {
//
//			if (!StringUtils.isNull(kekNumber)) {
//
//				QuerySpec query = new QuerySpec();
//				int idx = query.appendClassList(Project.class, true);
//
//				SearchCondition sc = new SearchCondition(Project.class, Project.KEK_NUMBER, "=", kekNumber);
//				query.appendWhere(sc, new int[] { idx });
//				query.appendAnd();
//
//				sc = new SearchCondition(Project.class, Project.P_TYPE, "=", pType);
//				query.appendWhere(sc, new int[] { idx });
//
//				QueryResult result = PersistenceHelper.manager.find(query);
//
//				if (result.hasMoreElements()) {
//					map.put("result", SUCCESS);
//					map.put("nResult", "NG");
//					map.put("index", index);
//				} else {
//					map.put("result", SUCCESS);
//					map.put("nResult", "OK");
//					map.put("index", index);
//				}
//			} else {
//				map.put("result", FAIL);
//				map.put("nResult", "NG");
//				map.put("index", index);
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	public PagingQueryResult getMainMyProjectList() {
//		QuerySpec query = null;
//		PagingQueryResult result = null;
//		try {
//			query = new QuerySpec();
//			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
//
//			int idx = query.appendClassList(Project.class, true);
//			// int idx = query.appendClassList(Project.class, true);
//
//			SearchCondition sc = null;
//			ClassAttribute ca = null;
//
//			String userOid = sessionUser.getPersistInfo().getObjectIdentifier().getStringValue();
//
//			if (query.getConditionCount() > 0)
//				query.appendAnd();
//
//			ReferenceFactory rf = new ReferenceFactory();
//
//			Persistable pp = (Persistable) rf.getReference(userOid).getObject();
//			WTUser user = null;
//			if (pp instanceof People) {
//				People p = (People) pp;
//				user = p.getUser();
//			} else {
//				user = (WTUser) rf.getReference(userOid).getObject();
//			}
//
//			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
//			int idx_u = query.appendClassList(WTUser.class, false);
//
//			ClassAttribute roleAca = null;
//			ClassAttribute roleBca = null;
//
//			query.appendOpenParen();
//
//			roleAca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);
//			roleBca = new ClassAttribute(WTUser.class, WTAttributeNameIfc.ID_NAME);
//
//			sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class, "roleAObjectRef.key.id"), "=", roleAca);
//			query.appendWhere(sc, new int[] { idx_plink, idx });
//			query.appendAnd();
//			sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class, "roleBObjectRef.key.id"), "=", roleBca);
//			query.appendWhere(sc, new int[] { idx_plink, idx_u });
//
//			query.appendCloseParen();
//
//			query.appendAnd();
//
//			sc = new SearchCondition(ProjectUserLink.class, "roleBObjectRef.key.id", "=",
//					user.getPersistInfo().getObjectIdentifier().getId());
//			query.appendWhere(sc, new int[] { idx_plink });
//
//			query.appendAnd();
//
//			query.appendOpenParen();
//
//			sc = new SearchCondition(ProjectUserLink.class, ProjectUserLink.USER_TYPE, "=",
//					ProjectUserType.MACHINE.name());
//			query.appendWhere(sc, new int[] { idx_plink });
//			query.appendOr();
//
//			sc = new SearchCondition(ProjectUserLink.class, ProjectUserLink.USER_TYPE, "=",
//					ProjectUserType.ELEC.name());
//			query.appendWhere(sc, new int[] { idx_plink });
//			query.appendOr();
//
//			sc = new SearchCondition(ProjectUserLink.class, ProjectUserLink.USER_TYPE, "=",
//					ProjectUserType.SOFT.name());
//			query.appendWhere(sc, new int[] { idx_plink });
//
//			query.appendCloseParen();
//
//			ca = new ClassAttribute(Project.class, Project.P_DATE);
//			OrderBy orderBy = new OrderBy(ca, true);
//			query.appendOrderBy(orderBy, new int[] { idx });
//
//			result = PagingSessionHelper.openPagingSession(0, 11, query);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return result;
//	}
//
//	public Map<String, Object> findMyProject(Map<String, Object> param) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		List<ProjectColumnData> list = new ArrayList<ProjectColumnData>();
//		QuerySpec query = null;
//
////		String progress = (String) param.get("progress");
//
//		String kekNumber = (String) param.get("kekNumber");
//		// String pData = (String) param.get("postdate");
//		String keNumber = (String) param.get("keNumber");
//		String userId = (String) param.get("userId");
//		String kekState = (String) param.get("kekState");
//
//		String mak = (String) param.get("mak");
//		String model = (String) param.get("model");
//		String customer = (String) param.get("customer");
////		String machineOid = (String) param.get("machineOid");
////		String elecOid = (String) param.get("elecOid");
////		String softOid = (String) param.get("softOid");
//		String ins_location = (String) param.get("ins_location");
//		String pType = (String) param.get("pType");
//		String description = (String) param.get("description");
//
//		String sort = (String) param.get("sort");
//		String sortKey = (String) param.get("sortKey");
//
//		String predate = (String) param.get("predate");
//		String postdate = (String) param.get("postdate");
//
//		try {
//			query = new QuerySpec();
//
//			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
//
//			int idx = query.appendClassList(Project.class, true);
//			// int idx_plink = query.appendClassList(ProjectUserLink.class, false);
//			// int idx_u = query.appendClassList(WTUser.class, false);
//
//			SearchCondition sc = null;
//			ClassAttribute ca = null;
//
//			// ClassAttribute roleAca = null;
//			// ClassAttribute roleBca = null;
//			//
//			// query.appendOpenParen();
//			//
//			// roleAca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);
//			// roleBca = new ClassAttribute(WTUser.class, WTAttributeNameIfc.ID_NAME);
//			//
//			// sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class,
//			// "roleAObjectRef.key.id"), "=", roleAca);
//			// query.appendWhere(sc, new int[] { idx_plink, idx });
//			// query.appendAnd();
//			// sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class,
//			// "roleBObjectRef.key.id"), "=", roleBca);
//			// query.appendWhere(sc, new int[] { idx_plink, idx_u });
//			//
//			// query.appendCloseParen();
//
//			// KEK 작번
//			if (!StringUtils.isNull(kekNumber)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.KEK_NUMBER);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(kekNumber);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// ke 작번
//			if (!StringUtils.isNull(keNumber)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.KE_NUMBER);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(keNumber);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// USER ID
//			if (!StringUtils.isNull(userId)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.USER_ID);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(userId);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// 작업내용
//			if (!StringUtils.isNull(description)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.DESCRIPTION);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// 막종
//			if (!StringUtils.isNull(mak)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.MAK);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(mak);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// 기종
//			if (!StringUtils.isNull(model)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.MODEL);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(model);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//			// 고객사
//			if (!StringUtils.isNull(customer)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.CUSTOMER);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(customer);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(predate)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				Timestamp start = DateUtils.convertStartDate(predate);
//				sc = new SearchCondition(Project.class, Project.P_DATE, SearchCondition.GREATER_THAN_OR_EQUAL, start);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(postdate)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				Timestamp end = DateUtils.convertEndDate(postdate);
//				sc = new SearchCondition(Project.class, Project.P_DATE, SearchCondition.LESS_THAN_OR_EQUAL, end);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// 설치 장소
//			if (!StringUtils.isNull(ins_location)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.INS_LOCATION);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(ins_location);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(kekState)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				sc = new SearchCondition(Project.class, Project.KEK_STATE, "=", kekState);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// 작번 유형
//			if (!StringUtils.isNull(pType)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.P_TYPE);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(pType);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			String userOid = sessionUser.getPersistInfo().getObjectIdentifier().getStringValue();
//
//			if (query.getConditionCount() > 0)
//				query.appendAnd();
//
//			ReferenceFactory rf = new ReferenceFactory();
//
//			Persistable pp = (Persistable) rf.getReference(userOid).getObject();
//			WTUser user = null;
//			if (pp instanceof People) {
//				People p = (People) pp;
//				user = p.getUser();
//			} else {
//				user = (WTUser) rf.getReference(userOid).getObject();
//			}
//
//			// People p = (People) rf.getReference(machineOid).getObject();
//
//			int idx_plink = query.appendClassList(ProjectUserLink.class, false);
//			int idx_u = query.appendClassList(WTUser.class, false);
//
//			ClassAttribute roleAca = null;
//			ClassAttribute roleBca = null;
//
//			query.appendOpenParen();
//
//			roleAca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);
//			roleBca = new ClassAttribute(WTUser.class, WTAttributeNameIfc.ID_NAME);
//
//			sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class, "roleAObjectRef.key.id"), "=", roleAca);
//			query.appendWhere(sc, new int[] { idx_plink, idx });
//			query.appendAnd();
//			sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class, "roleBObjectRef.key.id"), "=", roleBca);
//			query.appendWhere(sc, new int[] { idx_plink, idx_u });
//
//			query.appendCloseParen();
//
//			query.appendAnd();
//
//			sc = new SearchCondition(ProjectUserLink.class, "roleBObjectRef.key.id", "=",
//					user.getPersistInfo().getObjectIdentifier().getId());
//			query.appendWhere(sc, new int[] { idx_plink });
//
//			query.appendAnd();
//
//			query.appendOpenParen();
//
//			sc = new SearchCondition(ProjectUserLink.class, ProjectUserLink.USER_TYPE, "=",
//					ProjectUserType.MACHINE.name());
//			query.appendWhere(sc, new int[] { idx_plink });
//			query.appendOr();
//
//			sc = new SearchCondition(ProjectUserLink.class, ProjectUserLink.USER_TYPE, "=",
//					ProjectUserType.ELEC.name());
//			query.appendWhere(sc, new int[] { idx_plink });
//			query.appendOr();
//
//			sc = new SearchCondition(ProjectUserLink.class, ProjectUserLink.USER_TYPE, "=",
//					ProjectUserType.SOFT.name());
//			query.appendWhere(sc, new int[] { idx_plink });
//
//			query.appendCloseParen();
//
//			if (StringUtils.isNull(sort)) {
//				sort = "true";
//			}
//
//			if (StringUtils.isNull(sortKey)) {
//				// sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
//				sortKey = Project.P_DATE;
//			}
//
//			ca = new ClassAttribute(Project.class, sortKey);
//			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
//			query.appendOrderBy(orderBy, new int[] { idx });
//
//			query.setAdvancedQueryEnabled(true);
//			query.setDescendantQuery(false);
//
//			PageQueryUtils pager = new PageQueryUtils(param, query);
//			PagingQueryResult result = pager.find();
//			while (result.hasMoreElements()) {
//				Object[] obj = (Object[]) result.nextElement();
//				Project project = (Project) obj[0];
//				ProjectColumnData data = new ProjectColumnData(project);
//				list.add(data);
//			}
//			map.put("list", list);
//			map.put("lastPage", pager.getLastPage());
//			map.put("topListCount", pager.getTotal());
//			map.put("sessionid", pager.getSessionId());
//			map.put("curPage", pager.getCpage());
//			map.put("total", pager.getTotalSize());
//			map.put("result", "SUCCESS");
//
//		} catch (Exception e) {
//			map.put("result", "FAIL");
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	public Map<String, Object> find(Map<String, Object> param) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		List<ProjectColumnData> list = new ArrayList<ProjectColumnData>();
//		QuerySpec query = null;
//
//		String kekNumber = (String) param.get("kekNumber");
//		// String pData = (String) param.get("postdate");
//		String keNumber = (String) param.get("keNumber");
//		String userId = (String) param.get("userId");
//		String kekState = (String) param.get("kekState");
//
//		String mak = (String) param.get("mak");
//		String model = (String) param.get("model");
//		String customer = (String) param.get("customer");
//		String machineOid = (String) param.get("machineOid");
//		String elecOid = (String) param.get("elecOid");
//		String softOid = (String) param.get("softOid");
//		String ins_location = (String) param.get("ins_location");
//		String pType = (String) param.get("pType");
//		String description = (String) param.get("description");
//
//		String sort = (String) param.get("sort");
//		String sortKey = (String) param.get("sortKey");
//
//		String predate = (String) param.get("predate");
//		String postdate = (String) param.get("postdate");
//
//		try {
//			query = new QuerySpec();
//			int idx = query.appendClassList(Project.class, true);
//
//			// query.setDistinct(true);
//
//			SearchCondition sc = null;
//			ClassAttribute ca = null;
//
//			// KEK 작번
//			if (!StringUtils.isNull(kekNumber)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.KEK_NUMBER);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(kekNumber);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// ke 작번
//			if (!StringUtils.isNull(keNumber)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.KE_NUMBER);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(keNumber);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// USER ID
//			if (!StringUtils.isNull(userId)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.USER_ID);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(userId);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// 작업내용
//			if (!StringUtils.isNull(description)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.DESCRIPTION);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// 막종
//			if (!StringUtils.isNull(mak)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.MAK);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(mak);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// 기종
//			if (!StringUtils.isNull(model)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.MODEL);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(model);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//			// 고객사
//			if (!StringUtils.isNull(customer)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.CUSTOMER);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(customer);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// if (StringUtils.isNull(predate)) {
//			// Calendar calendar = Calendar.getInstance();
//			// calendar.add(Calendar.MONTH, -3);
//			// Timestamp before = new Timestamp(calendar.getTime().getTime());
//			// String pre = before.toString().substring(0, 10);
//			// predate = pre;
//			// }
//
//			if (!StringUtils.isNull(predate)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				Timestamp start = DateUtils.convertStartDate(predate);
//				sc = new SearchCondition(Project.class, Project.P_DATE, SearchCondition.GREATER_THAN_OR_EQUAL, start);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(postdate)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				Timestamp end = DateUtils.convertEndDate(postdate);
//				sc = new SearchCondition(Project.class, Project.P_DATE, SearchCondition.LESS_THAN_OR_EQUAL, end);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// 설치 장소
//			if (!StringUtils.isNull(ins_location)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.INS_LOCATION);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(ins_location);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(kekState)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				sc = new SearchCondition(Project.class, Project.KEK_STATE, "=", kekState);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			// 작번 유형
//			if (!StringUtils.isNull(pType)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Project.class, Project.P_TYPE);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(pType);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(machineOid)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ReferenceFactory rf = new ReferenceFactory();
//
//				Persistable pp = (Persistable) rf.getReference(machineOid).getObject();
//				WTUser machine = null;
//				if (pp instanceof People) {
//					People p = (People) pp;
//					machine = p.getUser();
//				} else {
//					machine = (WTUser) rf.getReference(machineOid).getObject();
//				}
//
//				// People p = (People) rf.getReference(machineOid).getObject();
//
//				int idx_plink = query.appendClassList(ProjectUserLink.class, false);
//				int idx_u = query.appendClassList(WTUser.class, false);
//
//				ClassAttribute roleAca = null;
//				ClassAttribute roleBca = null;
//
//				query.appendOpenParen();
//
//				roleAca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);
//				roleBca = new ClassAttribute(WTUser.class, WTAttributeNameIfc.ID_NAME);
//
//				sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class, "roleAObjectRef.key.id"), "=",
//						roleAca);
//				query.appendWhere(sc, new int[] { idx_plink, idx });
//				query.appendAnd();
//				sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class, "roleBObjectRef.key.id"), "=",
//						roleBca);
//				query.appendWhere(sc, new int[] { idx_plink, idx_u });
//
//				query.appendAnd();
//
//				sc = new SearchCondition(ProjectUserLink.class, "roleBObjectRef.key.id", "=",
//						machine.getPersistInfo().getObjectIdentifier().getId());
//				query.appendWhere(sc, new int[] { idx_plink });
//
//				query.appendAnd();
//
//				sc = new SearchCondition(ProjectUserLink.class, ProjectUserLink.USER_TYPE, "=",
//						ProjectUserType.MACHINE.name());
//				query.appendWhere(sc, new int[] { idx_plink });
//
//				query.appendCloseParen();
//
//			}
//
//			if (!StringUtils.isNull(elecOid)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ReferenceFactory rf = new ReferenceFactory();
//				Persistable pp = (Persistable) rf.getReference(elecOid).getObject();
//				WTUser elec = null;
//				if (pp instanceof People) {
//					People p = (People) pp;
//					elec = p.getUser();
//				} else {
//					elec = (WTUser) rf.getReference(elecOid).getObject();
//				}
//
//				int idx_plink = query.appendClassList(ProjectUserLink.class, false);
//				int idx_u = query.appendClassList(WTUser.class, false);
//
//				ClassAttribute roleAca = null;
//				ClassAttribute roleBca = null;
//
//				query.appendOpenParen();
//
//				roleAca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);
//				roleBca = new ClassAttribute(WTUser.class, WTAttributeNameIfc.ID_NAME);
//
//				sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class, "roleAObjectRef.key.id"), "=",
//						roleAca);
//				query.appendWhere(sc, new int[] { idx_plink, idx });
//				query.appendAnd();
//				sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class, "roleBObjectRef.key.id"), "=",
//						roleBca);
//				query.appendWhere(sc, new int[] { idx_plink, idx_u });
//
//				query.appendAnd();
//
//				sc = new SearchCondition(ProjectUserLink.class, "roleBObjectRef.key.id", "=",
//						elec.getPersistInfo().getObjectIdentifier().getId());
//				query.appendWhere(sc, new int[] { idx_plink });
//
//				query.appendAnd();
//
//				sc = new SearchCondition(ProjectUserLink.class, ProjectUserLink.USER_TYPE, "=",
//						ProjectUserType.ELEC.name());
//				query.appendWhere(sc, new int[] { idx_plink });
//
//				query.appendCloseParen();
//
//			}
//
//			if (!StringUtils.isNull(softOid)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ReferenceFactory rf = new ReferenceFactory();
//				Persistable pp = (Persistable) rf.getReference(softOid).getObject();
//				WTUser sw = null;
//				if (pp instanceof People) {
//					People p = (People) pp;
//					sw = p.getUser();
//				} else {
//					sw = (WTUser) rf.getReference(softOid).getObject();
//				}
//
//				int idx_plink = query.appendClassList(ProjectUserLink.class, false);
//				int idx_u = query.appendClassList(WTUser.class, false);
//
//				ClassAttribute roleAca = null;
//				ClassAttribute roleBca = null;
//
//				query.appendOpenParen();
//
//				roleAca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);
//				roleBca = new ClassAttribute(WTUser.class, WTAttributeNameIfc.ID_NAME);
//
//				sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class, "roleAObjectRef.key.id"), "=",
//						roleAca);
//				query.appendWhere(sc, new int[] { idx_plink, idx });
//				query.appendAnd();
//				sc = new SearchCondition(new ClassAttribute(ProjectUserLink.class, "roleBObjectRef.key.id"), "=",
//						roleBca);
//				query.appendWhere(sc, new int[] { idx_plink, idx_u });
//
//				query.appendAnd();
//
//				sc = new SearchCondition(ProjectUserLink.class, "roleBObjectRef.key.id", "=",
//						sw.getPersistInfo().getObjectIdentifier().getId());
//				query.appendWhere(sc, new int[] { idx_plink });
//
//				query.appendAnd();
//
//				sc = new SearchCondition(ProjectUserLink.class, ProjectUserLink.USER_TYPE, "=",
//						ProjectUserType.SOFT.name());
//				query.appendWhere(sc, new int[] { idx_plink });
//				query.appendCloseParen();
//			}
//
//			if (StringUtils.isNull(sort)) {
//				sort = "true";
//			}
//
//			if (StringUtils.isNull(sortKey)) {
//				// sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
//				sortKey = Project.P_DATE;
//			}
//
//			ca = new ClassAttribute(Project.class, sortKey);
//			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
//			query.appendOrderBy(orderBy, new int[] { idx });
//
//			// query.setAdvancedQueryEnabled(true);
//			// query.setDescendantQuery(false);
//
//			PageQueryUtils pager = new PageQueryUtils(param, query);
//			PagingQueryResult result = pager.find();
//
//			while (result.hasMoreElements()) {
//				Object[] obj = (Object[]) result.nextElement();
//				Project project = (Project) obj[0];
//				ProjectColumnData data = new ProjectColumnData(project);
//				list.add(data);
//			}
//			map.put("list", list);
//			map.put("lastPage", pager.getLastPage());
//			map.put("topListCount", pager.getTotal());
//			map.put("sessionid", pager.getSessionId());
//			map.put("curPage", pager.getCpage());
//			map.put("total", pager.getTotalSize());
//			map.put("result", "SUCCESS");
//
//		} catch (Exception e) {
//			map.put("result", "FAIL");
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	public JSONArray openProjectTree(Map<String, Object> param) throws Exception {
//		String oid = (String) param.get("oid");
//		ReferenceFactory rf = new ReferenceFactory();
//		Project project = (Project) rf.getReference(oid).getObject();
//
//		JSONArray jsonArray = new JSONArray();
//		JSONObject rootNode = new JSONObject();
//		rootNode.put("type", "root");
//		rootNode.put("id", project.getPersistInfo().getObjectIdentifier().getStringValue());
//		rootNode.put("title", project.getKekNumber() + "-" + project.getPType());
//		rootNode.put("expanded", true);
//		rootNode.put("icon", "/Windchill/jsp/images/" + getProjectStateIcon(project));
//		rootNode.put("folder", true);
//		getSubProjectTree(project, rootNode);
//		jsonArray.add(rootNode);
//		return jsonArray;
//	}
//
//	public String getTaskStateIcon(Task task) throws Exception {
//		// 준비중, 진행중, 완료됨, 지연됨
//		String icon = "task_ready.gif";
//
//		String state = task.getState();
//		if (TaskStateType.COMPLETE.getDisplay().equals(state)) {
//			return "task_complete.gif";
//		} else if (TaskStateType.STAND.getDisplay().equals(state)) {
//			return "task_ready.gif";
//		} else {
//
//			Timestamp today = DateUtils.getCurrentTimestamp();
//
//			int du = DateUtils.getDuration(task.getPlanStartDate(), task.getPlanEndDate());
//			BigDecimal counting = new BigDecimal(du);
//			BigDecimal multi = new BigDecimal(0.2);
//
//			BigDecimal result = counting.multiply(multi);
//			int perDay = Math.round(result.floatValue()); // 2??
//
//			int tdu = DateUtils.getDuration(task.getPlanEndDate(), DateUtils.getCurrentTimestamp()); // 1...
//
//			if (task.getPlanEndDate() != null && today.getTime() > task.getPlanEndDate().getTime()) {
//				return "task_delay.gif";
//			} else {
//				if (tdu <= perDay) {
//					if (task.getProgress() < 50) {
//						return "task_orange.gif";
//					} else if (task.getProgress() >= 51 && task.getProgress() < 100) {
//						return "task_yellow.gif";
//					} else if (task.getProgress() == 100) {
//						return "task_complete.gif";
//					}
//				} else {
//					return "task_yellow.gif";
//				}
//			}
//		}
//		return icon;
//	}
//
//	public String getProjectStateIcon(Project project) {
//		// 준비중, 진행중, 완료됨, 지연됨
//		String icon = "task_ready.gif";
//
//		String state = project.getState();
//
//		if (ProjectStateType.COMPLETE.getDisplay().equals(state)) {
//			return "task_complete.gif";
//		} else if (ProjectStateType.STAND.getDisplay().equals(state)) {
//			return "task_ready.gif";
//		} else {
//
//			Timestamp today = DateUtils.getCurrentTimestamp();
//
//			int du = DateUtils.getDuration(project.getPlanStartDate(), project.getPlanEndDate());
//			BigDecimal counting = new BigDecimal(du);
//			BigDecimal multi = new BigDecimal(0.2);
//
//			BigDecimal result = counting.multiply(multi);
//			int perDay = Math.round(result.floatValue()); // 2??
//
//			int tdu = DateUtils.getDuration(project.getPlanEndDate(), DateUtils.getCurrentTimestamp()); // 1...
//
//			if (today.getTime() > project.getPlanEndDate().getTime()) {
//				return "task_delay.gif";
//			} else {
//				if (tdu <= perDay) {
//					if (project.getProgress() < 50) {
//						return "task_orange.gif";
//					} else if (project.getProgress() >= 51 && project.getProgress() < 100) {
//						return "task_yellow.gif";
//					} else if (project.getProgress() == 100) {
//						return "task_complete.gif";
//					}
//				} else {
//					return "task_yellow.gif";
//				}
//			}
//		}
//		return icon;
//	}
//
//	public void getSubProjectTree(Project root, JSONObject rootNode) throws Exception {
//		ArrayList<Task> list = new ArrayList<Task>();
//		list = getterProjectTasks(root, list);
//		JSONArray jsonChildren = new JSONArray();
//		for (Task child : list) {
//
//			JSONObject node = new JSONObject();
//			node.put("type", "childrens");
//			node.put("id", child.getPersistInfo().getObjectIdentifier().getStringValue());
//			node.put("title", child.getName());
//			node.put("expanded", false);
//			node.put("icon", "/Windchill/jsp/images/" + getTaskStateIcon(child));
//			node.put("folder", true);
//			getSubProjectTaskTree(child, root, node);
//
//			jsonChildren.add(node);
//		}
//		rootNode.put("children", jsonChildren);
//	}
//
//	public void getSubProjectTaskTree(Task parentTask, Project root, JSONObject rootNode) throws Exception {
//		ArrayList<Task> list = new ArrayList<Task>();
//		list = getterProjectTask(parentTask, root, list);
//
//		JSONArray jsonChildren = new JSONArray();
//		for (Task child : list) {
//
//			JSONObject node = new JSONObject();
//			node.put("type", "childrens");
//			node.put("id", child.getPersistInfo().getObjectIdentifier().getStringValue());
//			node.put("title", child.getName());
//			node.put("expanded", false);
//			node.put("folder", true);
//			node.put("icon", "/Windchill/jsp/images/" + getTaskStateIcon(child));
//			getSubProjectTaskTree(child, root, node);
//
//			jsonChildren.add(node);
//		}
//		rootNode.put("children", jsonChildren);
//	}
//
//	public ArrayList<Task> getterProjectTasks(Project project, ArrayList<Task> list) throws Exception {
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(Task.class, true);
//		long ids = project.getPersistInfo().getObjectIdentifier().getId();
//		SearchCondition sc = new SearchCondition(Task.class, "projectReference.key.id", "=", ids);
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=", 0L);
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, Task.DEPTH, "=", 1);
//		query.appendWhere(sc, new int[] { idx });
//
//		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
//		OrderBy orderBy = new OrderBy(ca, false);
//		query.appendOrderBy(orderBy, new int[] { idx });
//
//		query.setAdvancedQueryEnabled(true);
//		query.setDescendantQuery(false);
//
//		QueryResult result = PersistenceHelper.manager.find(query);
//
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			Task t = (Task) obj[0];
//			list.add(t);
//		}
//		return list;
//	}
//
//	public ArrayList<Task> getterProjectTask(Task parentTask, Project project, ArrayList<Task> list) throws Exception {
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(Task.class, true);
//
//		long ids = parentTask.getPersistInfo().getObjectIdentifier().getId();
//		long tids = project.getPersistInfo().getObjectIdentifier().getId();
//
//		SearchCondition sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=", ids);
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, "projectReference.key.id", "=", tids);
//		query.appendWhere(sc, new int[] { idx });
//
//		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
//		OrderBy orderBy = new OrderBy(ca, false);
//		query.appendOrderBy(orderBy, new int[] { idx });
//
//		query.setAdvancedQueryEnabled(true);
//		query.setDescendantQuery(false);
//
//		QueryResult result = PersistenceHelper.manager.find(query);
//
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			Task t = (Task) obj[0];
//			list.add(t);
//		}
//		return list;
//	}
//
//	public Task getReqTask(Project project) throws Exception {
//		Task task = null;
//		try {
//
//			QuerySpec query = new QuerySpec();
//			int idx = query.appendClassList(Task.class, true);
//
//			SearchCondition sc = new SearchCondition(Task.class, Task.NAME, "=", REQ_TASK_NAME);
//			query.appendWhere(sc, new int[] { idx });
//			query.appendAnd();
//
//			sc = new SearchCondition(Task.class, Task.DEPTH, "=", 1);
//			query.appendWhere(sc, new int[] { idx });
//			query.appendAnd();
//
//			sc = new SearchCondition(Task.class, "projectReference.key.id", "=",
//					project.getPersistInfo().getObjectIdentifier().getId());
//			query.appendWhere(sc, new int[] { idx });
//			//
//
//			QueryResult result = PersistenceHelper.manager.find(query);
//			if (result.hasMoreElements()) {
//				Object[] obj = (Object[]) result.nextElement();
//				task = (Task) obj[0];
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return task;
//	}
//
//	public boolean isDuplicateOutput(WTDocument document, Task compareTask) throws Exception {
//
//		boolean isDuplicate = false;
//
//		QueryResult result = PersistenceHelper.manager.navigate(document, "output", DocumentOutputLink.class);
//		while (result.hasMoreElements()) {
//			Output output = (Output) result.nextElement();
//			Task tt = output.getTask();
//
//			String toid = tt.getPersistInfo().getObjectIdentifier().getStringValue();
//			String compare = compareTask.getPersistInfo().getObjectIdentifier().getStringValue();
//
//			if (toid.equals(compare)) {
//				isDuplicate = true;
//			}
//		}
//		return isDuplicate;
//	}
//
//	public boolean isTaskOutputLink(Task task) throws Exception {
//		QueryResult result = PersistenceHelper.manager.navigate(task, "output", TaskOutputLink.class);
//		boolean isExist = result.size() > 0 ? true : false;
//		return isExist;
//	}
//
//	public boolean nonAllocateTaskState(Task task) throws Exception {
//
//		boolean isComplete = true;
//
//		QueryResult result = PersistenceHelper.manager.navigate(task, "output", TaskOutputLink.class);
//		while (result.hasMoreElements()) {
//
//			Output output = (Output) result.nextElement();
//
//			QuerySpec query = new QuerySpec();
//
//			int idx = query.appendClassList(DocumentOutputLink.class, true);
//
//			SearchCondition sc = new SearchCondition(DocumentOutputLink.class, "roleAObjectRef.key.id", "=",
//					output.getPersistInfo().getObjectIdentifier().getId());
//			query.appendWhere(sc, new int[] { idx });
//
//			ClassAttribute ca = new ClassAttribute(DocumentOutputLink.class, DocumentOutputLink.MODIFY_TIMESTAMP);
//			OrderBy by = new OrderBy(ca, false);
//			query.appendOrderBy(by, new int[] { idx });
//
//			QueryResult qr = PersistenceHelper.manager.find(query);
//			while (qr.hasMoreElements()) {
//				Object[] obj = (Object[]) qr.nextElement();
//				DocumentOutputLink link = (DocumentOutputLink) obj[0];
//				LifeCycleManaged document = link.getDocument();
//
//				if (!"".equals(document.getLifeCycleState().toString())) {
//					isComplete = false;
//					break;
//				}
//
//				if ("RELEASED".equals(document.getLifeCycleState().toString())) {
//					isComplete = false;
//					break;
//				}
//			}
//		}
//		return isComplete;
//	}
//
//	public ArrayList<DocumentOutputLink> getProjectOutputLink(Task task) throws Exception {
//
//		ArrayList<DocumentOutputLink> list = new ArrayList<DocumentOutputLink>();
//
//		ArrayList<Task> child = new ArrayList<Task>();
//
//		child.add(task);
//		child = ProjectHelper.manager.getterProjectTask(task, task.getProject(), child);
//
//		for (Task tt : child) {
//
//			QueryResult result = PersistenceHelper.manager.navigate(tt, "output", TaskOutputLink.class);
//
////			QuerySpec qs = new QuerySpec();
////			int idx_ = qs.appendClassList(TaskOutputLink.class, true);
////
////			SearchCondition sc = new SearchCondition(TaskOutputLink.class, "roleBObjectRef.key.id", "=",
////					tt.getPersistInfo().getObjectIdentifier().getId());
////			qs.appendWhere(sc, new int[] { idx_ });
////
////			ClassAttribute ca = new ClassAttribute(TaskOutputLink.class, TaskOutputLink.CREATE_TIMESTAMP);
////			OrderBy by = new OrderBy(ca, true);
////			qs.appendOrderBy(by, new int[] { idx_ });
////
////			QueryResult result = PersistenceHelper.manager.find(qs);
//
//			while (result.hasMoreElements()) {
////				Object[] oo = (Object[]) result.nextElement();
////				TaskOutputLink ll = (TaskOutputLink) oo[0];
////				Output output = ll.getOutput();
////				if (output == null) {
////					System.out.println("null?");
////					continue;
////				}
//				Output output = (Output) result.nextElement();
//
////				QuerySpec query = new QuerySpec();
////				int idx = query.appendClassList(DocumentOutputLink.class, true);
////
////				sc = new SearchCondition(DocumentOutputLink.class, "roleAObjectRef.key.id", "=",
////						output.getPersistInfo().getObjectIdentifier().getId());
////				query.appendWhere(sc, new int[] { idx });
////
////				ca = new ClassAttribute(DocumentOutputLink.class, DocumentOutputLink.CREATE_TIMESTAMP);
////				by = new OrderBy(ca, true);
////				query.appendOrderBy(by, new int[] { idx });
//
//				QueryResult qr = PersistenceHelper.manager.navigate(output, "document", DocumentOutputLink.class,
//						false);
//
////				QueryResult qr = PersistenceHelper.manager.find(query);
//				while (qr.hasMoreElements()) {
////					Object[] obj = (Object[]) qr.nextElement();
//					DocumentOutputLink link = (DocumentOutputLink) qr.nextElement();
////					DocumentOutputLink link = (DocumentOutputLink) qr.nextElement();
//					list.add(link);
//				}
//			}
//		}
//		Collections.sort(list, new OutputCompare());
//		return list;
//	}
//
//	public ArrayList<DocumentMasterOutputLink> getProjectMasterOutputLink(Task task) throws Exception {
//
//		ArrayList<DocumentMasterOutputLink> list = new ArrayList<DocumentMasterOutputLink>();
//
//		ArrayList<Task> child = new ArrayList<Task>();
//
//		child.add(task);
//		child = ProjectHelper.manager.getterProjectTask(task, task.getProject(), child);
//
//		for (Task tt : child) {
//
//			QueryResult result = PersistenceHelper.manager.navigate(tt, "output", TaskOutputLink.class);
//
////			QuerySpec qs = new QuerySpec();
////			int idx_ = qs.appendClassList(TaskOutputLink.class, true);
////
////			SearchCondition sc = new SearchCondition(TaskOutputLink.class, "roleBObjectRef.key.id", "=",
////					tt.getPersistInfo().getObjectIdentifier().getId());
////			qs.appendWhere(sc, new int[] { idx_ });
////
////			ClassAttribute ca = new ClassAttribute(TaskOutputLink.class, TaskOutputLink.CREATE_TIMESTAMP);
////			OrderBy by = new OrderBy(ca, true);
////			qs.appendOrderBy(by, new int[] { idx_ });
////
////			QueryResult result = PersistenceHelper.manager.find(qs);
//
//			while (result.hasMoreElements()) {
////				Object[] oo = (Object[]) result.nextElement();
////				TaskOutputLink ll = (TaskOutputLink) oo[0];
////				Output output = ll.getOutput();
////				if (output == null) {
////					System.out.println("null?");
////					continue;
////				}
//				Output output = (Output) result.nextElement();
//
////				QuerySpec query = new QuerySpec();
////				int idx = query.appendClassList(DocumentMasterOutputLink.class, true);
////
////				sc = new SearchCondition(DocumentMasterOutputLink.class, "roleAObjectRef.key.id", "=",
////						output.getPersistInfo().getObjectIdentifier().getId());
////				query.appendWhere(sc, new int[] { idx });
////
////				ca = new ClassAttribute(DocumentMasterOutputLink.class, DocumentMasterOutputLink.CREATE_TIMESTAMP);
////				by = new OrderBy(ca, true);
////				query.appendOrderBy(by, new int[] { idx });
//
//				QueryResult qr = PersistenceHelper.manager.navigate(output, "master", DocumentMasterOutputLink.class,
//						false);
//				while (qr.hasMoreElements()) {
//					DocumentMasterOutputLink link = (DocumentMasterOutputLink) qr.nextElement();
//					list.add(link);
//				}
//
////				QueryResult qr = PersistenceHelper.manager.find(query);
////				if (qr.hasMoreElements()) {
////					Object[] obj = (Object[]) qr.nextElement();
////					DocumentMasterOutputLink link = (DocumentMasterOutputLink) obj[0];
////					DocumentOutputLink link = (DocumentOutputLink) qr.nextElement();
////					list.add(link);		
////				}
//			}
//
//		}
//		Collections.sort(list, new OutputCompare2());
//		return list;
//	}
//
//	public ArrayList<IssueProjectLink> getIssueProjectLink(Project project) throws Exception {
//
//		ArrayList<IssueProjectLink> list = new ArrayList<IssueProjectLink>();
//
//		QueryResult result = PersistenceHelper.manager.navigate(project, "issue", IssueProjectLink.class, false);
//		while (result.hasMoreElements()) {
//			IssueProjectLink issueProjectLink = (IssueProjectLink) result.nextElement();
//			list.add(issueProjectLink);
//		}
//		return list;
//	}
//
//	public ArrayList<IssueProjectLink> getIssueProjectLinkProject(Issue issue) throws Exception {
//
//		ArrayList<IssueProjectLink> list = new ArrayList<IssueProjectLink>();
//
//		QueryResult result = PersistenceHelper.manager.navigate(issue, "project", IssueProjectLink.class, false);
//		while (result.hasMoreElements()) {
//			IssueProjectLink issueProjectLink = (IssueProjectLink) result.nextElement();
//			list.add(issueProjectLink);
//		}
//		return list;
//	}
//
//	public ArrayList<Project> getRefProjectByKekNumber(Project project) throws Exception {
//
//		ArrayList<Project> refProjectList = new ArrayList<Project>();
//
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(Project.class, true);
//
//		String kekNumber = project.getKekNumber();
//		SearchCondition sc = new SearchCondition(Project.class, Project.KEK_NUMBER, SearchCondition.LIKE,
//				kekNumber + "%");
//		query.appendWhere(sc, new int[] { idx });
//
//		query.appendAnd();
//
//		String pType = project.getPType();
//
//		sc = new SearchCondition(Project.class, Project.P_TYPE, SearchCondition.NOT_EQUAL, pType);
//		query.appendWhere(sc, new int[] { idx });
//
//		QueryResult result = PersistenceHelper.manager.find(query);
//		Project refProject = null;
//		while (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			refProject = (Project) obj[0];
//			refProjectList.add(refProject);
//		}
//		return refProjectList;
//	}
//
//	public boolean isEditer(Project project) throws Exception {
//		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
//		// 관리자 수정가능
//		if (CommonUtils.isAdmin()) {
//			return true;
//		}
//
//		WTUser pm = getPMByProject(project);
//
//		if (pm != null) {
//			if (user.getName().equals(pm.getName())) {
//				return true;
//			}
//		} else {
//			return false;
//		}
//
//		WTUser machine = getUserTypeByProject(project, ProjectUserType.MACHINE.name());
//		if (machine != null) {
//			if (user.getName().equals(machine.getName())) {
//				return true;
//			}
//		} else {
//			return false;
//		}
//
//		WTUser elec = getUserTypeByProject(project, ProjectUserType.ELEC.name());
//		if (elec != null) {
//			if (user.getName().equals(elec.getName())) {
//				return true;
//			}
//		} else {
//			return false;
//		}
//
//		return false;
//	}
//

	public WTUser getUserType(Project project, String userType) throws Exception {
		CommonCode userTypeCode = CommonCodeHelper.manager.getCommonCode(userType, "USER_TYPE");
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ProjectUserLink.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, ProjectUserLink.class, "roleAObjectRef.key.id",
				project.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toEqualsAnd(query, idx, ProjectUserLink.class, "userTypeReference.key.id",
				userTypeCode.getPersistInfo().getObjectIdentifier().getId());
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

//
//	public Map<String, Object> findIssue(Map<String, Object> param) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		List<IssueColumnData> list = new ArrayList<IssueColumnData>();
//		QuerySpec query = null;
//
//		// search param
//		String name = (String) param.get("name");
//		String creatorsOid = (String) param.get("creatorsOid");
//		String description = (String) param.get("description");
//
//		String keNumber = (String) param.get("keNumber");
//		String kekNumber = (String) param.get("kekNumber");
//		String mak = (String) param.get("mak");
//		String kek_description = (String) param.get("kek_description");
//
//		String predate = (String) param.get("predate");
//		String postdate = (String) param.get("postdate");
//
//		ReferenceFactory rf = new ReferenceFactory();
//		// 정렬
//		String sort = (String) param.get("sort");
//		String sortKey = (String) param.get("sortKey");
//
//		try {
//			query = new QuerySpec();
//
//			int idx = query.appendClassList(Issue.class, true);
//			int idx_link = query.appendClassList(IssueProjectLink.class, true);
//			int idx_p = query.appendClassList(Project.class, true);
//
//			SearchCondition sc = null;
//			ClassAttribute ca = null;
//
//			ClassAttribute roleAca = null;
//			ClassAttribute roleBca = null;
//
//			query.appendOpenParen();
//
//			roleAca = new ClassAttribute(Issue.class, WTAttributeNameIfc.ID_NAME);
//			roleBca = new ClassAttribute(Project.class, WTAttributeNameIfc.ID_NAME);
//
//			sc = new SearchCondition(new ClassAttribute(IssueProjectLink.class, "roleAObjectRef.key.id"), "=", roleAca);
//			query.appendWhere(sc, new int[] { idx_link, idx });
//			query.appendAnd();
//			sc = new SearchCondition(new ClassAttribute(IssueProjectLink.class, "roleBObjectRef.key.id"), "=", roleBca);
//			query.appendWhere(sc, new int[] { idx_link, idx_p });
//
//			query.appendCloseParen();
//
//			// query.appendAnd();
//
//			// sc = WorkInProgressHelper.getSearchCondition_CI(Issue.class);
//			// query.appendWhere(sc, new int[] { idx });
//			// query.appendAnd();
//
//			if (!StringUtils.isNull(name)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Issue.class, Issue.NAME);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(description)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//
//				ca = new ClassAttribute(Issue.class, Issue.DESCRIPTION);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(description);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(creatorsOid)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				People user = (People) rf.getReference(creatorsOid).getObject();
//				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
//				sc = new SearchCondition(Issue.class, "ownership.owner.key.id", SearchCondition.EQUAL, ids);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(predate)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				Timestamp start = DateUtils.convertStartDate(predate);
//				sc = new SearchCondition(Issue.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
//						SearchCondition.GREATER_THAN_OR_EQUAL, start);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(postdate)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				Timestamp end = DateUtils.convertEndDate(postdate);
//				sc = new SearchCondition(Issue.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
//						SearchCondition.LESS_THAN_OR_EQUAL, end);
//				query.appendWhere(sc, new int[] { idx });
//			}
//
//			if (!StringUtils.isNull(keNumber)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				ca = new ClassAttribute(Project.class, Project.KE_NUMBER);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(keNumber);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx_p });
//			}
//
//			if (!StringUtils.isNull(kekNumber)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				ca = new ClassAttribute(Project.class, Project.KEK_NUMBER);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(kekNumber);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx_p });
//			}
//
//			if (!StringUtils.isNull(kek_description)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				ca = new ClassAttribute(Project.class, Project.DESCRIPTION);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(kek_description);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx_p });
//			}
//
//			if (!StringUtils.isNull(mak)) {
//				if (query.getConditionCount() > 0)
//					query.appendAnd();
//				ca = new ClassAttribute(Project.class, Project.MAK);
//				ColumnExpression ce = StringUtils.getUpperColumnExpression(mak);
//				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
//				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
//				query.appendWhere(sc, new int[] { idx_p });
//			}
//
//			if (StringUtils.isNull(sort)) {
//				sort = "true";
//			}
//
//			if (StringUtils.isNull(sortKey)) {
//				sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
//			}
//
//			ca = new ClassAttribute(Issue.class, sortKey);
//			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
//			query.appendOrderBy(orderBy, new int[] { idx });
//
//			query.setAdvancedQueryEnabled(true);
//			query.setDescendantQuery(false);
//
//			PageQueryUtils pager = new PageQueryUtils(param, query);
//			PagingQueryResult result = pager.find();
//			while (result.hasMoreElements()) {
//				Object[] obj = (Object[]) result.nextElement();
//				Issue issue = (Issue) obj[0];
//				Project project = (Project) obj[2];
//				IssueColumnData data = new IssueColumnData(issue, project);
//				list.add(data);
//			}
//			map.put("list", list);
//			map.put("lastPage", pager.getLastPage());
//			map.put("topListCount", pager.getTotal());
//			map.put("sessionid", pager.getSessionId());
//			map.put("curPage", pager.getCpage());
//			map.put("total", pager.getTotalSize());
//			map.put("result", "SUCCESS");
//		} catch (Exception e) {
//			map.put("result", "FAIL");
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	public String loadGanttProject(Map<String, Object> param) throws Exception {
//		String oid = (String) param.get("oid");
//		ReferenceFactory rf = new ReferenceFactory();
//		Project project = (Project) rf.getReference(oid).getObject();
//
//		ArrayList<Task> list = new ArrayList<Task>();
//
//		list = ProjectHelper.manager.getterProjectTask(project, list);
//		// list = ProjectHelper.manager.getterProjectNonSchduleTask(project, list);
//
//		// 프로젝트 추가
//
//		StringBuffer gantt = new StringBuffer();
//
//		gantt.append("{\"data\": [");
//
//		// project
//		gantt.append("{");
//
//		gantt.append("\"id\": \"" + project.getPersistInfo().getObjectIdentifier().getStringValue() + "\",");
//		gantt.append("\"type\": \"project\",");
//		gantt.append("\"isNew\": \"false\",");
//		gantt.append("\"start_date\": \"" + DateUtils.formatTime(project.getPlanStartDate()) + "\",");
//		gantt.append("\"end_date\": \"" + DateUtils.formatTime(project.getPlanEndDate()) + "\",");
//		gantt.append("\"real_start_date\": \"" + DateUtils.formatTime(project.getStartDate()) + "\",");
//		gantt.append("\"real_end_date\": \"" + DateUtils.formatTime(project.getEndDate()) + "\",");
//		gantt.append(
//				"\"duration\": " + DateUtils.getDuration(project.getPlanStartDate(), project.getPlanEndDate()) + ",");
//
//		int state = ProjectHelper.manager.ganttState(project);
//		// state
//		gantt.append("\"state\": \"" + state + "\",");
//		gantt.append("\"text\": \"" + project.getKekNumber() + "-" + project.getPType() + "\",");
//
//		float progress = (float) getKekProgress(project) / 100;
//
//		gantt.append("\"progress\": \"" + StringUtils.numberFormat(progress, "#.##") + "\",");
//		gantt.append("\"taskType\": \"\",");
//		gantt.append("\"parent\": \"0\",");
//
//		if (list.size() == 0) {
//			gantt.append("\"open\": false");
//			gantt.append("}");
//		} else if (list.size() > 0) {
//			gantt.append("\"open\": true");
//			gantt.append("},");
//
//			for (int i = 0; i < list.size(); i++) {
//				Task tt = (Task) list.get(i);
//
//				gantt.append("{");
//				gantt.append("\"id\": \"" + tt.getPersistInfo().getObjectIdentifier().getStringValue() + "\",");
//				gantt.append("\"text\": \"" + tt.getName() + "\",");
//
//				boolean hasChild = false;
//				QueryResult result = PersistenceHelper.manager.navigate(tt, "childTask", ParentTaskChildTaskLink.class);
//				if (result.size() > 0) {
//					hasChild = true;
//				}
//
//				if (hasChild) {
//					gantt.append("\"type\": \"project\",");
//				} else {
//					gantt.append("\"type\": \"task\",");
//				}
//
//				gantt.append("\"isNew\": \"false\",");
//				gantt.append("\"start_date\": \"" + DateUtils.formatTime(tt.getPlanStartDate()) + "\",");
//				gantt.append("\"end_date\": \"" + DateUtils.formatTime(tt.getPlanEndDate()) + "\",");
//				gantt.append("\"real_start_date\": \"" + DateUtils.formatTime(tt.getStartDate()) + "\",");
//				gantt.append("\"real_end_date\": \"" + DateUtils.formatTime(tt.getEndDate()) + "\",");
//				gantt.append("\"taskType\": \"" + tt.getTaskType() + "\",");
//				int tstate = ProjectHelper.manager.ganttState(tt);
//				gantt.append("\"state\": \"" + tstate + "\",");
//				gantt.append("\"allocate\": \"" + tt.getAllocate() + "\",");
//
//				float tprogress = (float) tt.getProgress() / 100;
//				gantt.append("\"progress\": \"" + StringUtils.numberFormat(tprogress, "#.##") + "\",");
//				gantt.append(
//						"\"duration\": \"" + DateUtils.getDuration(tt.getPlanStartDate(), tt.getPlanEndDate()) + "\",");
//				if ((list.size() - 1) == i) {
//					if (StringUtils.isNull(tt.getParentTask())) {
//						gantt.append("\"parent\": \"" + project.getPersistInfo().getObjectIdentifier().getStringValue()
//								+ "\",");
//						gantt.append("\"open\": true");
//					} else {
//						gantt.append("\"parent\": \""
//								+ tt.getParentTask().getPersistInfo().getObjectIdentifier().getStringValue() + "\",");
//						gantt.append("\"open\": true");
//					}
//					gantt.append("}");
//				} else {
//					if (StringUtils.isNull(tt.getParentTask())) {
//						gantt.append("\"parent\": \"" + project.getPersistInfo().getObjectIdentifier().getStringValue()
//								+ "\",");
//						gantt.append("\"open\": true");
//					} else {
//						gantt.append("\"parent\": \""
//								+ tt.getParentTask().getPersistInfo().getObjectIdentifier().getStringValue() + "\",");
//						gantt.append("\"open\": true");
//					}
//					gantt.append("},");
//				}
//			}
//		}
//
//		gantt.append("],");
//
//		gantt.append("\"links\": [");
//
//		ArrayList<TargetTaskSourceTaskLink> linkList = getAllTargetList(list);
//
//		for (int i = 0; i < linkList.size(); i++) {
//			TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink) linkList.get(i);
//			gantt.append("{");
//			gantt.append("\"id\": \"" + link.getPersistInfo().getObjectIdentifier().getStringValue() + "\",");
//			gantt.append("\"source\": \"" + link.getTargetTask().getPersistInfo().getObjectIdentifier().getStringValue()
//					+ "\",");
//			gantt.append("\"target\": \"" + link.getSourceTask().getPersistInfo().getObjectIdentifier().getStringValue()
//					+ "\",");
//
//			gantt.append("\"lag\": \"" + link.getLag() + "\",");
//			gantt.append("\"type\": \"0\",");
//
//			if ((linkList.size() - 1) == i) {
//				gantt.append("}");
//			} else {
//				gantt.append("},");
//			}
//		}
//
//		gantt.append("]");
//		gantt.append("}");
//
//		return gantt.toString();
//	}
//
//	private int ganttState(Project project) throws Exception {
//		int ganttState = 4; // 준비중
//		String state = project.getState();
//
//		if (ProjectStateType.COMPLETE.getDisplay().equals(state)) {
//			ganttState = 5;
//		} else {
//			int comp = (int) ProjectHelper.getPreferComp(project);
//			int gap = project.getProgress() - comp;
//			if (gap < 0 && gap >= -30) {
//				ganttState = 1;
//			} else if (gap < -30) {
//				ganttState = 0;
//			} else if (gap > 0) {
//				ganttState = 2;
//			}
//		}
//		return ganttState;
//	}
//
//	private int ganttState(Task tt) throws Exception {
//		boolean isNormalTask = tt.getTaskType().equals("일반") || tt.getAllocate() == 0;
//		int ganttState = 4; // 준비중
//		String state = tt.getState();
//
//		if (isNormalTask) {
//			QueryResult result = PersistenceHelper.manager.navigate(tt, "output", TaskOutputLink.class);
//			if (result.size() > 0 || tt.getProgress() == 100) {
//				ganttState = 5;
//			} else if (tt.getProgress() != 100) {
//				ganttState = 4;
//			}
//		} else {
//
//			if (TaskStateType.COMPLETE.getDisplay().equals(state)) {
//				ganttState = 5;
//			} else {
//				int comp = (int) ProjectHelper.getPreferComp(tt);
//				int gap = tt.getProgress() - comp;
//				if (gap < 0 && gap >= -30) {
//					ganttState = 1;
//				} else if (gap < -30) {
//					ganttState = 0;
//				} else if (gap > 0) {
//					ganttState = 2;
//				}
//			}
//		}
//		return ganttState;
//	}
//
//	private ArrayList<TargetTaskSourceTaskLink> getAllTargetList(ArrayList<Task> list) throws Exception {
//		ArrayList<TargetTaskSourceTaskLink> lists = new ArrayList<TargetTaskSourceTaskLink>();
//		for (int i = 0; i < list.size(); i++) {
//			Task tt = (Task) list.get(i);
//
//			QueryResult result = PersistenceHelper.manager.navigate(tt, "targetTask", TargetTaskSourceTaskLink.class,
//					false);
//
//			while (result.hasMoreElements()) {
//				TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink) result.nextElement();
//				lists.add(link);
//			}
//		}
//		return lists;
//	}
//
//	public Task getProjectTaskByName(Project project, Task parent, String tname) throws Exception {
//		Task task = null;
//
//		QuerySpec query = new QuerySpec();
//		int idx = query.appendClassList(Task.class, true);
//
//		SearchCondition sc = new SearchCondition(Task.class, "projectReference.key.id", "=",
//				project.getPersistInfo().getObjectIdentifier().getId());
//		query.appendWhere(sc, new int[] { idx });
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, Task.NAME, "=", tname);
//		query.appendWhere(sc, new int[] { idx });
//
//		query.appendAnd();
//
//		sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=",
//				parent.getPersistInfo().getObjectIdentifier().getId());
//		query.appendWhere(sc, new int[] { idx });
//
//		QueryResult result = PersistenceHelper.manager.find(query);
//		if (result.hasMoreElements()) {
//			Object[] obj = (Object[]) result.nextElement();
//			task = (Task) obj[0];
//		}
//		return task;
//	}
//
//	public String getQProjectStateBar(Project project) throws Exception {
//		ArrayList<Task> list = new ArrayList<Task>();
//		list = getterProjectTask(project, list);
//		StringBuffer sb = new StringBuffer();
//		for (Task tt : list) {
//
//			String tname = tt.getName();
//
//			if (tname.equals("의뢰서")) {
//				continue;
//			}
//
//			if (!tname.equals(QTASK[0]) && !tname.equals(QTASK[1]) && !tname.equals(QTASK[2]) && !tname.equals(QTASK[3])
//					&& !tname.equals(QTASK[4])) {
//				continue;
//			}
//
//			// 완료됨
//			if (tt.getState().equals(TaskStateType.COMPLETE.getDisplay())) {
//				sb.append("<img title='" + tt.getName()
//						+ " 완료입니다.' class='pos3' src='/Windchill/jsp/images/state_green_bar.png'>");
//			} else if (tt.getState().equals(TaskStateType.STAND.getDisplay())) {
//				// 시작전
//				sb.append("<img title='" + tt.getName()
//						+ " 시작전 입니다.' class='pos3' src='/Windchill/jsp/images/state_blank_bar.png'>");
//			} else {
//				// 완료가 아닐 경우..
//				// 20%가 아닐경우.. 그냥 쭈욱 노랑이
//				int du = DateUtils.getDuration(tt.getPlanStartDate(), tt.getPlanEndDate());
//				BigDecimal counting = new BigDecimal(du);
//				BigDecimal multi = new BigDecimal(0.2);
//
//				BigDecimal result = counting.multiply(multi);
//				int perDay = Math.round(result.floatValue()); // 기간의 20%
//				int tdu = DateUtils.getDuration(tt.getPlanEndDate(), DateUtils.getCurrentTimestamp()); // 1...
//
//				// 오늘 기준으로 작아 졌을떄 체크
//				if (tdu <= perDay) {
//					if (tt.getProgress() < 50) {
//						sb.append("<img title='" + tt.getName()
//								+ " 계획 종료일 보다 초과 되서 진행 중입니다.' class='pos3' src='/Windchill/jsp/images/state_orange_bar.png'>");
//					} else if (tt.getProgress() >= 51 && tt.getProgress() < 100) {
//						sb.append("<img title='" + tt.getName()
//								+ " 정상 진행 중입니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.png'>");
//					} else if (tt.getProgress() == 100) {
//						sb.append("<img title='" + tt.getName()
//								+ " 완료입니다.' class='pos3' src='/Windchill/jsp/images/state_green_bar.png'>");
//					}
//				} else {
//					sb.append("<img title='" + tt.getName()
//							+ " 정상 진행 중입니다.' class='pos3' src='/Windchill/jsp/images/state_yellow_bar.png'>");
//				}
//			}
//		}
//		return sb.toString();
//	}
//
//	public int getQState(Task tt) throws Exception {
//		int qstate = ProjectGateState.GATE_NO_START;
//
//		if (tt.getState() != null) {
//			// 완료됨
//			if (tt.getState().equals(TaskStateType.COMPLETE.getDisplay())) {
//				qstate = ProjectGateState.GATE_COMPLETE;
//			} else if (tt.getState().equals(TaskStateType.STAND.getDisplay())) {
//				qstate = ProjectGateState.GATE_NO_START;
//			} else {
//
//				Timestamp today = DateUtils.getCurrentTimestamp();
//				int du = DateUtils.getDuration(tt.getPlanStartDate(), tt.getPlanEndDate());
//				BigDecimal counting = new BigDecimal(du);
//				BigDecimal multi = new BigDecimal(0.2);
//
//				BigDecimal result = counting.multiply(multi);
//				int perDay = Math.round(result.floatValue()); // 기간의 20%
//				int tdu = DateUtils.getDuration(tt.getPlanEndDate(), DateUtils.getCurrentTimestamp()); // 1...
//
//				if (tt.getPlanEndDate() != null && today.getTime() > tt.getPlanEndDate().getTime()) {
//					qstate = ProjectGateState.GATE_DELAY;
//				} else {
//					// 오늘 기준으로 작아 졌을떄 체크
//					if (tdu <= perDay) {
//						if (tt.getProgress() < 50) {
//							qstate = ProjectGateState.GATE_DELAY_PROGRESS;
//						} else if (tt.getProgress() >= 51 && tt.getProgress() < 100) {
//							qstate = ProjectGateState.GATE_PROGRESS;
//						} else if (tt.getProgress() == 100) {
//							qstate = ProjectGateState.GATE_COMPLETE;
//						}
//					} else {
//						qstate = ProjectGateState.GATE_PROGRESS;
//					}
//				}
//			}
//		}
//		return qstate;
//	}
//
	public String getStateIcon(int type) throws Exception {
		if (type == 0) {
			return "<img title='태스크 시작전 입니다.' style='position: relative; top: 2px;' src='/Windchill/extcore/images/project/state_blank_bar.png'>";
		} else if (type == 1) {
			return "<img title='태스크가 계획 종료일 보다 초과 되서 진행 중 입니다.' style='position: relative; top: 2px;' class='' src='/Windchill/extcore/images/project/state_red_bar.png'>";
		} else if (type == 2) {
			return "<img title='태스크가 진행 중 입니다.' style='position: relative; top: 2px;' src='/Windchill/extcore/images/project/state_yellow_bar.png'>";
		} else if (type == 3) {
			return "<img title='태스크 지연 입니다.' style='position: relative; top: 2px;' src='/Windchill/extcore/images/project/state_orange_bar.png'>";
		} else if (type == 4) {
			return "<img title='태스크 완료 입니다.' style='position: relative; top: 2px;' src='/Windchill/extcore/images/project/state_green_bar.png'>";
		}
		return "";
	}

//
//	public int gate1StateIcon(Project project) throws Exception {
//		int gate1 = ProjectGateState.GATE_NO_START;
//		// 태스크 이름으로 처리
//		ArrayList<Task> list = new ArrayList<Task>();
//
//		list = getterProjectTask(project, list);
//
//		Timestamp end = null;
//		Timestamp start = null;
//
//		boolean isStart = false; // 시작 여부
//
//		int totalProgress = 0;
//
//		for (Task task : list) {
//
//			String name = task.getName();
//
//			for (String ss : GATE1) {
//
//				if (name.equals(ss)) {
//					totalProgress += task.getProgress();
//					if (task.getState().equals(TaskStateType.INWORK.getDisplay())
//							|| task.getState().equals(TaskStateType.COMPLETE.getDisplay())) {
//						isStart = true;
//						break;
//					}
//
//					Timestamp tStart = task.getPlanStartDate();
//					Timestamp tEnd = task.getPlanEndDate();
//
//					if (start == null || (start.getTime() < tStart.getTime())) {
//						start = tStart;
//					}
//
//					if (tEnd != null) {
//						if (end == null || (end.getTime() < tEnd.getTime())) {
//							end = tEnd;
//						}
//					}
//				}
//			}
//		}
//
//		Timestamp today = DateUtils.getCurrentTimestamp();
//		boolean isOverDay = false;
//		boolean isCheckDay = false;
//		if (end == null || today.getTime() > end.getTime()) {
//			isOverDay = true;
//		} else {
//			// 실제 기간..
//			int du = DateUtils.getDuration(start, end);
//			BigDecimal counting = new BigDecimal(du);
//			BigDecimal multi = new BigDecimal(0.2);
//
//			BigDecimal result = counting.multiply(multi);
//			int perDay = Math.round(result.floatValue()); // 2??
//
//			int tdu = DateUtils.getDuration(end, DateUtils.getCurrentTimestamp()); // 1...
//			if (tdu <= perDay) {
//				isCheckDay = true;
//			}
//		}
//
//		int per = totalProgress / GATE1.length;
//
//		if (!isStart) {
//			if (isOverDay) {
//				gate1 = ProjectGateState.GATE_DELAY;
//			} else {
//				gate1 = ProjectGateState.GATE_NO_START;
//			}
//		} else {
//			// 진행중이고 완료가 아닐대...
//			if (isOverDay) {
//				if (per != 100) {
//					// 진행 오버
//					gate1 = ProjectGateState.GATE_DELAY_PROGRESS;
//				} else if (per >= 51 && per < 100) {
//					gate1 = ProjectGateState.GATE_PROGRESS;
//				} else if (per == 100) {
//					gate1 = ProjectGateState.GATE_COMPLETE;
//				}
//			} else {
//				// 진행 중이지만..
//
//				if (isCheckDay) {
//					if (per < 50) {
//						// 주황색
//						gate1 = ProjectGateState.GATE_DELAY_PROGRESS;
//					} else if (per >= 51 && per < 100) {
//						gate1 = ProjectGateState.GATE_PROGRESS;
//					} else if (per == 100) {
//						gate1 = ProjectGateState.GATE_COMPLETE;
//					}
//				} else {
//					gate1 = ProjectGateState.GATE_PROGRESS;
//				}
//			}
//		}
//		return gate1;
//	}
//
//	public int gate2StateIcon(Project project) throws Exception {
//		int gate2 = ProjectGateState.GATE_NO_START;
//		// 태스크 이름으로 처리
//		ArrayList<Task> list = new ArrayList<Task>();
//
//		list = getterProjectTask(project, list);
//
//		Timestamp end = null;
//		Timestamp start = null;
//
//		boolean isStart = false; // 시작 여부
//
//		int totalProgress = 0;
//
//		for (Task task : list) {
//
//			String name = task.getName();
//
//			for (String ss : GATE2) {
//
//				if (name.equals(ss)) {
//					totalProgress += task.getProgress();
//					if (task.getState().equals(TaskStateType.INWORK.getDisplay())
//							|| task.getState().equals(TaskStateType.COMPLETE.getDisplay())) {
//						isStart = true;
//						break;
//					}
//
//					Timestamp tStart = task.getPlanStartDate();
//					Timestamp tEnd = task.getPlanEndDate();
//
//					if (start == null || (start.getTime() < tStart.getTime())) {
//						start = tStart;
//					}
//
//					if (tEnd != null) {
//						if (end == null || (end.getTime() < tEnd.getTime())) {
//							end = tEnd;
//						}
//					}
//				}
//			}
//		}
//
//		Timestamp today = DateUtils.getCurrentTimestamp();
//		boolean isOverDay = false;
//		boolean isCheckDay = false;
//		if (end == null || today.getTime() > end.getTime()) {
//			isOverDay = true;
//		} else {
//			// 실제 기간..
//			int du = DateUtils.getDuration(start, end);
//
//			BigDecimal counting = new BigDecimal(du);
//			BigDecimal multi = new BigDecimal(0.2);
//
//			BigDecimal result = counting.multiply(multi);
//			int perDay = result.intValue(); // 2??
//
//			int tdu = DateUtils.getDuration(end, DateUtils.getCurrentTimestamp()); // 1...
//
//			if (tdu <= perDay) {
//				isCheckDay = true;
//			}
//		}
//
//		int per = totalProgress / GATE2.length;
//
//		if (!isStart) {
//			if (isOverDay) {
//				gate2 = ProjectGateState.GATE_DELAY;
//			} else {
//				gate2 = ProjectGateState.GATE_NO_START;
//			}
//		} else {
//			// 진행중이고 완료가 아닐대...
//			if (isOverDay) {
//				if (per != 100) {
//					gate2 = ProjectGateState.GATE_DELAY_PROGRESS;
//				} else if (per >= 51 && per < 100) {
//					gate2 = ProjectGateState.GATE_PROGRESS;
//				} else if (per == 100) {
//					gate2 = ProjectGateState.GATE_COMPLETE;
//				}
//			} else {
//				// 진행 중이지만..
//
//				if (isCheckDay) {
//					if (per < 50) {
//						gate2 = ProjectGateState.GATE_DELAY_PROGRESS;
//					} else if (per >= 51 && per < 100) {
//						gate2 = ProjectGateState.GATE_PROGRESS;
//					} else if (per == 100) {
//						gate2 = ProjectGateState.GATE_COMPLETE;
//					}
//				} else {
//					gate2 = ProjectGateState.GATE_PROGRESS;
//				}
//			}
//		}
//		return gate2;
//	}
//
//	public int gate3StateIcon(Project project) throws Exception {
//		int gate3 = ProjectGateState.GATE_NO_START;
//		// 태스크 이름으로 처리
//		ArrayList<Task> list = new ArrayList<Task>();
//
//		list = getterProjectTask(project, list);
//
//		Timestamp end = null;
//		Timestamp start = null;
//
//		boolean isStart = false; // 시작 여부
//
//		int totalProgress = 0;
//
//		for (Task task : list) {
//
//			String name = task.getName();
//
//			for (String ss : GATE3) {
//
//				if (name.equals(ss)) {
//					totalProgress += task.getProgress();
//					if (task.getState().equals(TaskStateType.INWORK.getDisplay())
//							|| task.getState().equals(TaskStateType.COMPLETE.getDisplay())) {
//						isStart = true;
//						break;
//					}
//
//					Timestamp tStart = task.getPlanStartDate();
//					Timestamp tEnd = task.getPlanEndDate();
//
//					if (start == null || (start.getTime() < tStart.getTime())) {
//						start = tStart;
//					}
//
//					if (tEnd != null) {
//						if (end == null || (end.getTime() < tEnd.getTime())) {
//							end = tEnd;
//						}
//					}
//				}
//			}
//		}
//
//		Timestamp today = DateUtils.getCurrentTimestamp();
//		boolean isOverDay = false;
//		boolean isCheckDay = false;
//		if (end == null || today.getTime() > end.getTime()) {
//			isOverDay = true;
//		} else {
//			// 실제 기간..
//			int du = DateUtils.getDuration(start, end);
//
//			BigDecimal counting = new BigDecimal(du);
//			BigDecimal multi = new BigDecimal(0.2);
//
//			BigDecimal result = counting.multiply(multi);
//			int perDay = result.intValue(); // 2??
//
//			int tdu = DateUtils.getDuration(end, DateUtils.getCurrentTimestamp()); // 1...
//
//			if (tdu <= perDay) {
//				isCheckDay = true;
//			}
//		}
//		int per = totalProgress / GATE3.length;
//
//		if (!isStart) {
//			if (isOverDay) {
//				gate3 = ProjectGateState.GATE_DELAY;
//			} else {
//				gate3 = ProjectGateState.GATE_NO_START;
//			}
//		} else {
//			// 진행중이고 완료가 아닐대...
//			if (isOverDay) {
//				if (per != 100) {
//					gate3 = ProjectGateState.GATE_DELAY_PROGRESS;
//				} else if (per >= 51 && per < 100) {
//					gate3 = ProjectGateState.GATE_PROGRESS;
//				} else if (per == 100) {
//					gate3 = ProjectGateState.GATE_COMPLETE;
//				}
//			} else {
//				// 진행 중이지만..
//
//				if (isCheckDay) {
//					if (per < 50) {
//						gate3 = ProjectGateState.GATE_DELAY_PROGRESS;
//					} else if (per >= 51 && per < 100) {
//						gate3 = ProjectGateState.GATE_PROGRESS;
//					} else if (per == 100) {
//						gate3 = ProjectGateState.GATE_COMPLETE;
//					}
//				} else {
//					gate3 = ProjectGateState.GATE_PROGRESS;
//				}
//			}
//		}
//		return gate3;
//	}
//
//	public int gate4StateIcon(Project project) throws Exception {
//		int gate4 = ProjectGateState.GATE_NO_START;
////		StringBuffer sb = new StringBuffer();
//
//		// 태스크 이름으로 처리
//		ArrayList<Task> list = new ArrayList<Task>();
//
//		list = getterProjectTask(project, list);
//
//		Timestamp end = null;
//		Timestamp start = null;
//
//		boolean isStart = false; // 시작 여부
//
//		int totalProgress = 0;
//
//		for (Task task : list) {
//
//			String name = task.getName();
//
//			for (String ss : GATE4) {
//
//				if (name.equals(ss)) {
//					totalProgress += task.getProgress();
//					if (task.getState().equals(TaskStateType.INWORK.getDisplay())
//							|| task.getState().equals(TaskStateType.COMPLETE.getDisplay())) {
//						isStart = true;
//						break;
//					}
//					Timestamp tStart = task.getPlanStartDate();
//					Timestamp tEnd = task.getPlanEndDate();
//
//					if (start == null || (start.getTime() < tStart.getTime())) {
//						start = tStart;
//					}
//
//					if (tEnd != null) {
//						if (end == null || (end.getTime() < tEnd.getTime())) {
//							end = tEnd;
//						}
//					}
//				}
//			}
//		}
//
//		Timestamp today = DateUtils.getCurrentTimestamp();
//		boolean isOverDay = false;
//		boolean isCheckDay = false;
//		if (end == null || today.getTime() > end.getTime()) {
//			isOverDay = true;
//		} else {
//			// 실제 기간..
//			int du = DateUtils.getDuration(start, end);
//
//			BigDecimal counting = new BigDecimal(du);
//			BigDecimal multi = new BigDecimal(0.2);
//
//			BigDecimal result = counting.multiply(multi);
//			int perDay = result.intValue(); // 2??
//
//			int tdu = DateUtils.getDuration(end, DateUtils.getCurrentTimestamp()); // 1...
//
//			if (tdu <= perDay) {
//				isCheckDay = true;
//			}
//		}
//		int per = totalProgress / GATE4.length;
//		if (!isStart) {
//			if (isOverDay) {
//				gate4 = ProjectGateState.GATE_DELAY;
//			} else {
//				gate4 = ProjectGateState.GATE_NO_START;
//			}
//		} else {
//			// 진행중이고 완료가 아닐대...
//			if (isOverDay) {
//				if (per != 100) {
//					gate4 = ProjectGateState.GATE_DELAY_PROGRESS;
//				} else if (per >= 51 && per < 100) {
//					gate4 = ProjectGateState.GATE_PROGRESS;
//				} else if (per == 100) {
//					gate4 = ProjectGateState.GATE_COMPLETE;
//				}
//			} else {
//				// 진행 중이지만..
//
//				if (isCheckDay) {
//					if (per < 50) {
//						gate4 = ProjectGateState.GATE_DELAY_PROGRESS;
//					} else if (per >= 51 && per < 100) {
//						gate4 = ProjectGateState.GATE_PROGRESS;
//					} else if (per == 100) {
//						gate4 = ProjectGateState.GATE_COMPLETE;
//					}
//				} else {
//					gate4 = ProjectGateState.GATE_PROGRESS;
//				}
//			}
//		}
//		return gate4;
//	}
//
//	public int gate5StateIcon(Project project) throws Exception {
//		int gate5 = ProjectGateState.GATE_NO_START;
////		StringBuffer sb = new StringBuffer();
//
//		// 태스크 이름으로 처리
//		ArrayList<Task> list = new ArrayList<Task>();
//
//		list = getterProjectTask(project, list);
//
//		Timestamp end = null;
//		Timestamp start = null;
//
//		boolean isStart = false; // 시작 여부
//
//		int totalProgress = 0;
//
//		for (Task task : list) {
//
//			String name = task.getName();
//
//			for (String ss : GATE5) {
//
//				if (name.equals(ss)) {
//					totalProgress += task.getProgress();
//					if (task.getState().equals(TaskStateType.INWORK.getDisplay())
//							|| task.getState().equals(TaskStateType.COMPLETE.getDisplay())) {
//						isStart = true;
//						break;
//					}
//
//					Timestamp tStart = task.getPlanStartDate();
//					Timestamp tEnd = task.getPlanEndDate();
//
//					if (start == null || (start.getTime() < tStart.getTime())) {
//						start = tStart;
//					}
//
//					if (tEnd != null) {
//						if (end == null || (end.getTime() < tEnd.getTime())) {
//							end = tEnd;
//						}
//					}
//				}
//			}
//		}
//
//		Timestamp today = DateUtils.getCurrentTimestamp();
//		boolean isOverDay = false;
//		boolean isCheckDay = false;
//		if (end == null || today.getTime() > end.getTime()) {
//			isOverDay = true;
//		} else {
//			// 실제 기간..
//			int du = DateUtils.getDuration(start, end);
//
//			BigDecimal counting = new BigDecimal(du);
//			BigDecimal multi = new BigDecimal(0.2);
//
//			BigDecimal result = counting.multiply(multi);
//			int perDay = result.intValue(); // 2??
//
//			int tdu = DateUtils.getDuration(end, DateUtils.getCurrentTimestamp()); // 1...
//
//			if (tdu <= perDay) {
//				isCheckDay = true;
//			}
//		}
//		int per = totalProgress / GATE5.length;
//		if (!isStart) {
//			if (isOverDay) {
//				gate5 = ProjectGateState.GATE_DELAY;
//			} else {
//				gate5 = ProjectGateState.GATE_NO_START;
//			}
//		} else {
//			// 진행중이고 완료가 아닐대...
//			if (isOverDay) {
//				if (per != 100) {
//					gate5 = ProjectGateState.GATE_DELAY_PROGRESS;
//				} else if (per >= 51 && per < 100) {
//					gate5 = ProjectGateState.GATE_DELAY_PROGRESS;
//				} else if (per == 100) {
//					gate5 = ProjectGateState.GATE_COMPLETE;
//				}
//			} else {
//				// 진행 중이지만..
//
//				if (isCheckDay) {
//					if (per < 50) {
//						gate5 = ProjectGateState.GATE_DELAY_PROGRESS;
//					} else if (per >= 51 && per < 100) {
//						gate5 = ProjectGateState.GATE_PROGRESS;
//					} else if (per == 100) {
//						gate5 = ProjectGateState.GATE_COMPLETE;
//					}
//				} else {
//					gate5 = ProjectGateState.GATE_PROGRESS;
//				}
//			}
//		}
//		return gate5;
//	}
//
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

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ProjectDTO> list = new ArrayList<ProjectDTO>();

		String kekNumber = (String) params.get("kekNumber");
		String keNumber = (String) params.get("keNumber");
		String mak = (String) params.get("mak");
		String detail = (String) params.get("detail");
		String install = (String) params.get("install");
		String customer = (String) params.get("customer");
		String kekState = (String) params.get("kekState");
		String model = (String) params.get("model");
		String projectType = (String) params.get("projectType");
		String predate = (String) params.get("predate");
		String postdate = (String) params.get("postdate");
		String template = (String) params.get("template");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Project.class, true);

		if (!StringUtils.isNull(kekNumber)) {
			QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.KEK_NUMBER, kekNumber);
		}

		if (!StringUtils.isNull(keNumber)) {
			QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.KE_NUMBER, keNumber);
		}

		if (!StringUtils.isNull(model)) {
			QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.MODEL, model);
		}

		if (!StringUtils.isNull(kekState)) {
			QuerySpecUtils.toLikeAnd(query, idx, Project.class, Project.KEK_STATE, kekState);
		}

		if (!StringUtils.isNull(projectType)) {
			CommonCode projectTypeCode = (CommonCode) CommonUtils.getObject(projectType);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "projectTypeReference.key.id",
					projectTypeCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(mak)) {
			CommonCode makCode = (CommonCode) CommonUtils.getObject(mak);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "makReference.key.id",
					makCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(detail)) {
			CommonCode detailCode = (CommonCode) CommonUtils.getObject(detail);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "detailReference.key.id",
					detailCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(install)) {
			CommonCode installCode = (CommonCode) CommonUtils.getObject(install);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "installReference.key.id",
					installCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(customer)) {
			CommonCode customerCode = (CommonCode) CommonUtils.getObject(customer);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "customerReference.key.id",
					customerCode.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(template)) {
			Template t = (Template) CommonUtils.getObject(template);
			QuerySpecUtils.toEqualsAnd(query, idx, Project.class, "templateReference.key.id",
					t.getPersistInfo().getObjectIdentifier().getId());
		}

		if (!StringUtils.isNull(predate)) {

		}

		if (!StringUtils.isNull(postdate)) {

		}

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

	public JSONArray load(String oid) throws Exception {
		Project project = (Project) CommonUtils.getObject(oid);
		JSONArray list = new JSONArray();
		JSONObject node = new JSONObject();
		node.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
		node.put("name", project.getKekNumber());
		node.put("description", project.getDescription());
		node.put("duration", project.getDuration());
		node.put("taskType", project.getProjectType().getName());
		node.put("isNew", false);

		JSONArray childrens = new JSONArray();
		ArrayList<Task> taskList = TaskHelper.manager.getProjectTasks(project);
		for (Task task : taskList) {
			JSONObject children = new JSONObject();
			children.put("oid", task.getPersistInfo().getObjectIdentifier().getStringValue());
			children.put("name", task.getName());
			children.put("description", task.getDescription());
			children.put("duration", task.getDuration());
			children.put("isNew", false);
			children.put("taskType", task.getTaskType().getCode());
			load(children, project, task);
			childrens.add(children);
		}
		node.put("children", childrens);
		list.add(node);
		return list;
	}

	private void load(JSONObject node, Project project, Task parentTask) throws Exception {
		JSONArray childrens = new JSONArray();
		ArrayList<Task> taskList = TaskHelper.manager.getProjectTasks(project, parentTask);
		for (Task task : taskList) {
			JSONObject children = new JSONObject();
			children.put("oid", task.getPersistInfo().getObjectIdentifier().getStringValue());
			children.put("name", task.getName());
			children.put("description", task.getDescription());
			children.put("duration", task.getDuration());
			children.put("isNew", false);
			children.put("taskType", task.getTaskType().getCode());
			load(children, project, task);
			childrens.add(children);
		}
		node.put("children", childrens);
	}
}
