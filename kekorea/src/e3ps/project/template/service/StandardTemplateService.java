package e3ps.project.template.service;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.xml.bind.DatatypeConverter;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.People;
import e3ps.project.task.ParentTaskChildTaskLink;
import e3ps.project.task.Task;
import e3ps.project.task.dto.TaskTreeNode;
import e3ps.project.task.service.TaskHelper;
import e3ps.project.task.variable.TaskStateVariable;
import e3ps.project.template.Template;
import e3ps.project.template.TemplateUserLink;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardTemplateService extends StandardManager implements TemplateService {

	public static StandardTemplateService newStandardTemplateService() throws WTException {
		StandardTemplateService instance = new StandardTemplateService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String name = (String) params.get("name");
		String description = (String) params.get("description");
		String reference = (String) params.get("reference");
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);

			Template template = Template.newTemplate();
			template.setName(name);
			template.setEnable(true);
			template.setOwnership(ownership);
			template.setDescription(description);
			Timestamp start = DateUtils.getPlanStartDate();
			template.setPlanStartDate(start);
			template.setState("완료됨");
			Calendar eCa = Calendar.getInstance();
			eCa.setTimeInMillis(start.getTime());
			eCa.add(Calendar.DATE, 1);
			Timestamp end = new Timestamp(eCa.getTime().getTime());
			template.setPlanEndDate(end);
			template.setDuration(DateUtils.getDuration(template.getPlanStartDate(), template.getPlanEndDate()));

			PersistenceHelper.manager.save(template);

			if (!StringUtils.isNull(reference)) {
				Template copy = (Template) CommonUtils.getObject(reference);
//				copyTasksInfo(template, copy);
//				copyTemplateInfo(template, copy);
			}

			commit(template);

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

	private void commit(Template template) throws Exception {

		ArrayList<Task> list = TemplateHelper.manager.recurciveTask(template);
		sortedTask(list);
		finalSet(template);
	}

	private void finalSet(Template template) throws Exception {

		System.out.println("최종 작업시작....");

	}

	private void sortedTask(ArrayList<Task> list) throws Exception {

		for (int i = list.size() - 1; i >= 0; i--) {
			Task task = (Task) list.get(i);

			Timestamp start = null;
			Timestamp end = null;

			boolean edit = false;

			QueryResult result = PersistenceHelper.manager.navigate(task, "childTask", ParentTaskChildTaskLink.class);
			while (result.hasMoreElements()) {
				Task child = (Task) result.nextElement();

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
				task.setPlanStartDate(start);
				task.setPlanEndDate(end);

				int duration = DateUtils.getDuration(start, end);
				task.setDuration(duration);
				task = (Task) PersistenceHelper.manager.modify(task);
			}
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
			Template template = null;
			for (TaskTreeNode node : nodes) {
				String oid = node.getOid();
				ArrayList<TaskTreeNode> childrens = node.getChildren();
				String name = node.getName();
				String d = node.getDescription();
				template = (Template) CommonUtils.getObject(oid);
				template.setName(name);
				template.setDescription(StringUtils.replaceToValue(d, name));
				PersistenceHelper.manager.modify(template);
				template = (Template) PersistenceHelper.manager.refresh(template);
				treeSave(template, null, childrens);
				commit(template);
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

	private void treeSave(Template template, Task parentTask, ArrayList<TaskTreeNode> childrens) throws Exception {
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
				int allocate = node.getAllocate();
				boolean isNew = node.isNew();
				ArrayList<TaskTreeNode> n = node.getChildren();
				int sort = TaskHelper.manager.getSort(template, parentTask);
				String taskType = node.getTaskType();
				Task t = null;
				if (isNew) {
					t = Task.newTask();
					t.setName(name);
					t.setDepth(depth);
					t.setDescription(description);
					t.setDuration(duration);
					t.setAllocate(allocate);
					t.setState(TaskStateVariable.READY);
					t.setOwnership(ownership);
					t.setParentTask(parentTask);
					t.setTemplate(template);
					t.setPlanStartDate(template.getPlanStartDate());
					t.setPlanEndDate(template.getPlanEndDate());
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
					t.setState(TaskStateVariable.READY);
					t.setOwnership(ownership);
					t.setParentTask(parentTask);
					t.setTemplate(template);
					t.setPlanStartDate(template.getPlanStartDate());
					t.setPlanEndDate(template.getPlanEndDate());
					t.setSort(sort);
					t.setTaskType(CommonCodeHelper.manager.getCommonCode(taskType, "TASK_TYPE"));
					PersistenceHelper.manager.modify(t);
				}
				treeSave(template, t, n);
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
	public void saveUserLink(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String pmOid = (String) params.get("pmOid");
		String sub_pmOid = (String) params.get("sub_pmOid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Template template = (Template) CommonUtils.getObject(oid);

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(TemplateUserLink.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, TemplateUserLink.class, "roleAObjectRef.key.id",
					template.getPersistInfo().getObjectIdentifier().getId());
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				TemplateUserLink link = (TemplateUserLink) obj[0];
				PersistenceHelper.manager.delete(link);
			}

			if (!StringUtils.isNull(pmOid)) {
				WTUser user = (WTUser) CommonUtils.getObject(pmOid);
				TemplateUserLink link = TemplateUserLink.newTemplateUserLink(template, user);
				link.setUserType(CommonCodeHelper.manager.getCommonCode("PM", "USER_TYPE"));
				PersistenceHelper.manager.save(link);
			}

			if (!StringUtils.isNull(sub_pmOid)) {
				WTUser user = (WTUser) CommonUtils.getObject(pmOid);
				TemplateUserLink link = TemplateUserLink.newTemplateUserLink(template, user);
				link.setUserType(CommonCodeHelper.manager.getCommonCode("SUB_PM", "USER_TYPE"));
				PersistenceHelper.manager.save(link);
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
