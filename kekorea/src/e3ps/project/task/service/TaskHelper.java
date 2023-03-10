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
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "templateReference.key.id",
				template.getPersistInfo().getObjectIdentifier().getId());
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

	public ArrayList<Task> getTemplateTasks(Template template) throws Exception {
		return getTemplateTasks(template, null);
	}

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
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, Task.DEPTH, (depth + 1)); // grid ??? ????????? ?????? ??? ????????? ??????..
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
		QuerySpecUtils.toEqualsAnd(query, idx, Task.class, "projectReference.key.id",
				project.getPersistInfo().getObjectIdentifier().getId());
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
