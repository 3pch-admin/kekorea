package e3ps.project.task.service;

import java.math.BigDecimal;
import java.util.ArrayList;

import e3ps.common.util.QuerySpecUtils;
import e3ps.project.Project;
import e3ps.project.task.Task;
import e3ps.project.template.Template;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class TaskHelper {

	public static final TaskHelper manager = new TaskHelper();
	public static final TaskService service = ServiceFactory.getService(TaskService.class);

	public int getSort(Template template) throws Exception {
		return getSort(template, null);
	}

	/**
	 * 같은 레벨의 태스크 정렬 순거 가져오기
	 */
	public int getSort(Template template, Task task) throws Exception {
		int sort = 1;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, false);
		int idx_t = query.appendClassList(Template.class, false);
		query.setAdvancedQueryEnabled(true);

		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.MAXIMUM, ca);
		query.appendSelect(function, false);

		QuerySpecUtils.toInnerJoin(query, Task.class, Template.class, "templateReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_t);
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "templateReference.key.id", template);
		if (task != null) {
			QuerySpecUtils.toEqualsAnd(query, idx, Task.class, Task.DEPTH, task.getDepth());
		}

		QueryResult result = PersistenceServerHelper.manager.query(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			BigDecimal next = (BigDecimal) obj[0];
			if (next != null) {
				sort = next.intValue() + 1;
			}
		}
		return sort;
	}

	/**
	 * 템플릿 관련 태스크 가져오기 부모X
	 */
	public ArrayList<Task> getTemplateTasks(Template template) throws Exception {
		return getTemplateTasks(template, null);
	}

	/**
	 * 템플릿 관련 태스크 가져오기 부모 O
	 */
	public ArrayList<Task> getTemplateTasks(Template template, Task parentTask) throws Exception {
		ArrayList<Task> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "templateReference.key.id",
				template.getPersistInfo().getObjectIdentifier().getId());
		if (parentTask != null) {
			QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "parentTaskReference.key.id",
					parentTask.getPersistInfo().getObjectIdentifier().getId());
		} else {
			QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "parentTaskReference.key.id", 0L);
		}
		QuerySpecUtils.toOrderBy(query, idx, Task.class, Task.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Task task = (Task) obj[0];
			list.add(task);
		}
		return list;
	}

	public ArrayList<Task> getTemplateTaskDepth(Template template) throws Exception {
		return getTemplateTaskDepth(template, 1);
	}

	public ArrayList<Task> getTemplateTaskDepth(Template template, int depth) throws Exception {

		ArrayList<Task> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "templateReference.key.id",
				template.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, Task.DEPTH, (depth + 1)); // grid 상 레벨이 하나 더 붙어서 간다..
		QuerySpecUtils.toOrderBy(query, idx, Task.class, Task.SORT, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Task task = (Task) obj[0];
			list.add(task);
		}
		return list;
	}

	public ArrayList<Task> getProjectTasks(Project project) throws Exception {
		return getProjectTasks(project, null);
	}

	public ArrayList<Task> getProjectTasks(Project project, Task parentTask) throws Exception {
		ArrayList<Task> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "projectReference.key.id",
				project.getPersistInfo().getObjectIdentifier().getId());
		if (parentTask != null) {
			QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "parentTaskReference.key.id",
					parentTask.getPersistInfo().getObjectIdentifier().getId());
		} else {
			QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "parentTaskReference.key.id", 0L);
		}
		QuerySpecUtils.toOrderBy(query, idx, Task.class, Task.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Task task = (Task) obj[0];
			System.out.println("트리 로드..." + task.getName() + ",==" + task.getSort());
			list.add(task);
		}
		return list;
	}

	public int getSort(Project project) throws Exception {
		return getSort(project, null);
	}

	public int getSort(Project project, Task task) throws Exception {
		int sort = 1;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, false);
		int idx_t = query.appendClassList(Project.class, false);
		query.setAdvancedQueryEnabled(true);

		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
		SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.MAXIMUM, ca);
		query.appendSelect(function, false);

		QuerySpecUtils.toInnerJoin(query, Task.class, Project.class, "projectReference.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_t);
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "projectReference.key.id", project);
		if (task != null) {
			QuerySpecUtils.toEqualsAnd(query, idx, Task.class, Task.DEPTH, task.getDepth());
		}

		QueryResult result = PersistenceServerHelper.manager.query(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			BigDecimal next = (BigDecimal) obj[0];
			if (next != null) {
				sort = next.intValue() + 1;
			}
		}
		return sort;
	}
}
