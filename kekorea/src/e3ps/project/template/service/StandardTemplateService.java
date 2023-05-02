package e3ps.project.template.service;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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
import e3ps.project.service.ProjectHelper;
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
			template.setUpdateUser(ownership);
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
				copyTask(template, copy);
				saveUserLink(template, copy);
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

	private void saveUserLink(Template template, Template copy) throws Exception {

		WTUser pm = TemplateHelper.manager.getUserType(copy, "PM");
		if (pm != null) {
			TemplateUserLink link = TemplateUserLink.newTemplateUserLink(template, pm);
			link.setUserType(CommonCodeHelper.manager.getCommonCode("PM", "USER_TYPE"));
			PersistenceHelper.manager.save(link);
		}

		WTUser subPm = TemplateHelper.manager.getUserType(copy, "SUB_PM");
		if (subPm != null) {
			TemplateUserLink link = TemplateUserLink.newTemplateUserLink(template, subPm);
			link.setUserType(CommonCodeHelper.manager.getCommonCode("SUB_PM", "USER_TYPE"));
			PersistenceHelper.manager.save(link);
		}
	}

	private void copyTask(Template template, Template copy) throws Exception {

		HashMap<Task, Task> parentMap = new HashMap<Task, Task>();
		ArrayList<Task> list = TemplateHelper.manager.recurciveTask(copy);
		for (int i = 0; i < list.size(); i++) {
			Task orgTask = (Task) list.get(i);
			Task newTask = Task.newTask();

			// 원본 카피
			newTask.setName(orgTask.getName());
			newTask.setDescription(orgTask.getDescription());
			newTask.setAllocate(orgTask.getAllocate());
			newTask.setSort(orgTask.getSort());
			newTask.setDepth(orgTask.getDepth());
			newTask.setDuration(orgTask.getDuration());
			newTask.setState(orgTask.getState());
			newTask.setTaskType(orgTask.getTaskType());
			newTask.setOwnership(orgTask.getOwnership());
			newTask.setUpdateUser(orgTask.getUpdateUser());
			newTask.setPlanStartDate(orgTask.getPlanStartDate());
			newTask.setPlanEndDate(orgTask.getPlanEndDate());
			newTask.setProgress(0);
			newTask.setProject(null);
			newTask.setTemplate(template);

			Task parent = (Task) parentMap.get(orgTask.getParentTask());
			newTask.setParentTask(parent);
			newTask = (Task) PersistenceHelper.manager.save(newTask);
			parentMap.put(orgTask, newTask);
		}

	}

	private void commit(Template template) throws Exception {
		ArrayList<Task> list = TemplateHelper.manager.recurciveTask(template);
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
			template.setPlanStartDate(start);
			template.setPlanEndDate(end);
			template.setDuration(1);
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
			template.setPlanStartDate(start);
			template.setPlanEndDate(end);
			duration = DateUtils.getDuration(start, end);
			template.setDuration(duration);
		}
		PersistenceHelper.manager.modify(template);
	}

	@Override
	public void treeSave(Map<String, Object> params) throws Exception {
		String json = (String) params.get("json");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

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
			}

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				Task task = (Task) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(task);
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
	public void modify(Map<String, Object> params) throws Exception {
		String oid = (String) params.get("oid");
		String description = (String) params.get("description");
		String pmOid = (String) params.get("pmOid");
		String subPmOid = (String) params.get("subPmOid");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Template template = (Template) CommonUtils.getObject(oid);
			template.setDescription(description);
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(TemplateUserLink.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, TemplateUserLink.class, "roleAObjectRef.key.id", template);
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

			if (!StringUtils.isNull(subPmOid)) {
				WTUser user = (WTUser) CommonUtils.getObject(subPmOid);
				TemplateUserLink link = TemplateUserLink.newTemplateUserLink(template, user);
				link.setUserType(CommonCodeHelper.manager.getCommonCode("SUB_PM", "USER_TYPE"));
				PersistenceHelper.manager.save(link);
			}

			PersistenceHelper.manager.modify(template);

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
	public void delete(String oid) throws Exception {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Template template = (Template) CommonUtils.getObject(oid);

			// 템플릿 담당자들 삭제
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(TemplateUserLink.class, true);
			QuerySpecUtils.toEqualsAnd(query, idx, TemplateUserLink.class, "roleAObjectRef.key.id", template);
			QueryResult result = PersistenceHelper.manager.find(query);
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				TemplateUserLink link = (TemplateUserLink) obj[0];
				PersistenceHelper.manager.delete(link);
			}

			QuerySpec qs = new QuerySpec();
			int _idx = qs.appendClassList(Task.class, true);
			QuerySpecUtils.toEqualsAnd(qs, _idx, Task.class, "templateReference.key.id", template);
			QueryResult qr = PersistenceHelper.manager.find(qs);
			while (qr.hasMoreElements()) {
				Object[] obj = (Object[]) qr.nextElement();
				Task task = (Task) obj[0];
				PersistenceHelper.manager.delete(task);
			}

			PersistenceHelper.manager.delete(template);

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
