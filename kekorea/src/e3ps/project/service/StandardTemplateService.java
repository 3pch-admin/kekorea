package e3ps.project.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.DateUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.StringUtils;
import e3ps.org.People;
import e3ps.project.ParentTaskChildTaskLink;
import e3ps.project.TargetTaskSourceTaskLink;
import e3ps.project.Task;
import e3ps.project.Template;
import e3ps.project.TemplateUserLink;
import e3ps.project.dto.TemplateViewData;
import e3ps.project.enums.ProjectUserType;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardTemplateService extends StandardManager implements TemplateService, MessageHelper {

	private static final long serialVersionUID = 7399639769714198619L;

	public static StandardTemplateService newStandardTemplateService() throws WTException {
		StandardTemplateService instance = new StandardTemplateService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> createTemplateAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String name = (String) param.get("name");
		// String duration = (String) param.get("duration");
		String duration = "1";// 기본 1일로 일단..
		String enable = (String) param.get("enable");
		String descriptionTemp = (String) param.get("descriptionTemp");
		String templateOid = (String) param.get("templateOid");
		Template template = null;
		Template copy = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		
		try {
			trs.start();

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);

			template = Template.newTemplate();
			template.setName(name);
			// template.setState("작업 중");

			template.setUpdateUser(ownership);

			if ("true".equals(enable)) {
				template.setEnable(true);
			} else {
				template.setEnable(false);
			}

			template.setOwnership(ownership);
			template.setDuration(Integer.parseInt(duration));
			template.setDescription(descriptionTemp);

			Timestamp start = DateUtils.getPlanStartDate();
			// 계획 시작일, 계획 종료일은 등록일로 세팅 한다. 템플릿의 경우 태스크 생성시 일정을 다시 조절한다.
			template.setPlanStartDate(start);

			Calendar eCa = Calendar.getInstance();
			eCa.setTimeInMillis(start.getTime());
			eCa.add(Calendar.DATE, Integer.parseInt(duration));

			Timestamp end = new Timestamp(eCa.getTime().getTime());
			template.setPlanEndDate(end);

			PersistenceHelper.manager.save(template);

			if (!StringUtils.isNull(templateOid)) {
				// 템플릿 복사...
				copy = (Template) rf.getReference(templateOid).getObject();
				copyTasksInfo(template, copy);

				copyTemplateInfo(template, copy);
			}

			commit(template);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "템플릿이 " + CREATE_OK);
			map.put("url", "/Windchill/plm/template/listTemplate");
			map.put("reObj",template);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "템플릿 " + CREATE_FAIL);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> deleteTemplateAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		Transaction trs = new Transaction();
		Template template = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {
			trs.start();

			template = (Template) rf.getReference(oid).getObject();

			template = (Template) PersistenceHelper.manager.refresh(template);

			ArrayList<Task> list = new ArrayList<Task>();
			list = TemplateHelper.manager.getterTemplateTask(template, list);

			for (Task tt : list) {
				tt = (Task) PersistenceHelper.manager.refresh(tt);
				PersistenceHelper.manager.delete(tt);
			}

			PersistenceHelper.manager.delete(template);

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "템플릿이 " + DELETE_OK);
			map.put("url", "/Windchill/plm/template/listTemplate");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "템플릿 " + DELETE_FAIL);
			map.put("url", "/Windchill/plm/template/viewTemplate?oid=" + oid);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> modifyTemplateAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		Template template = null;
		String name = (String) param.get("name");
		String pmOid = (String) param.get("pmOid");
		String sub_pmOid = (String) param.get("sub_pmOid");

		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			template = (Template) rf.getReference(oid).getObject();
			template.setName(name);
			template = (Template) PersistenceHelper.manager.modify(template);

			QueryResult result = PersistenceHelper.manager.navigate(template, "user", TemplateUserLink.class, false);
			while (result.hasMoreElements()) {
				TemplateUserLink link = (TemplateUserLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			if (!StringUtils.isNull(pmOid)) {
				Persistable per = (Persistable) rf.getReference(pmOid).getObject();
				if (per instanceof People) {
					People pm = (People) per;
					TemplateUserLink userLink = TemplateUserLink.newTemplateUserLink(template, pm.getUser());
					userLink.setUserType(ProjectUserType.PM.name());
					PersistenceHelper.manager.save(userLink);
				} else if (per instanceof WTUser) {
					WTUser pm = (WTUser) per;
					TemplateUserLink userLink = TemplateUserLink.newTemplateUserLink(template, pm);
					userLink.setUserType(ProjectUserType.PM.name());
					PersistenceHelper.manager.save(userLink);
				}
			}

			if (!StringUtils.isNull(sub_pmOid)) {
				Persistable per = (Persistable) rf.getReference(sub_pmOid).getObject();
				if (per instanceof People) {
					People sub_pm = (People) per;
					TemplateUserLink userLink = TemplateUserLink.newTemplateUserLink(template, sub_pm.getUser());
					userLink.setUserType(ProjectUserType.SUB_PM.name());
					PersistenceHelper.manager.save(userLink);
				} else if (per instanceof WTUser) {
					WTUser sub_pm = (WTUser) per;
					TemplateUserLink userLink = TemplateUserLink.newTemplateUserLink(template, sub_pm);
					userLink.setUserType(ProjectUserType.SUB_PM.name());
					PersistenceHelper.manager.save(userLink);
				}
			}

			map.put("reload", true);
			map.put("result", SUCCESS);
			map.put("msg", "템플릿이 " + MODIFY_OK);
			map.put("url", "/Windchill/plm/template/listTemplate");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("reload", false);
			map.put("result", FAIL);
			map.put("msg", "템플릿 " + MODIFY_FAIL);
			map.put("url", "/Windchill/plm/template/viewTemplate?oid=" + oid);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> onSaveTemplateTaskAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String poid = (String) param.get("poid");
		String text = (String) param.get("text");
		Object oid = (Object) param.get("oid");
		String description = (String) param.get("description");
		Object duration = (Object) param.get("duration");
		String start_date = (String) param.get("start_date");
		String end_date = (String) param.get("end_date");
		String taskType = (String) param.get("taskType");
		String toid = (String) param.get("toid");
		List<String> childrens = (List<String>) param.get("childrens");

		Template template = null;
		Task task = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			Ownership ownership = Ownership.newOwnership(user);

			// 정렬 필요..
			template = (Template) rf.getReference(toid).getObject();
			if (oid instanceof Long) {

				task = Task.newTask();
				task.setName(text);
				task.setDepth(1);
				task.setParentTask(null);
				task.setOwnership(ownership);
				task.setTemplate(template);
				task.setDescription(description);

				if (duration != null) {
					task.setDuration((int) duration);
				} else {
					task.setDuration(1);
				}

				task.setTaskType(taskType);
				task.setPlanStartDate(DateUtils.convertStartDate(start_date));

				Calendar ca = Calendar.getInstance();
				ca.setTimeInMillis(task.getPlanStartDate().getTime());
				if (duration != null) {
					ca.add(Calendar.DATE, (int) duration);
				} else {
					ca.add(Calendar.DATE, 1);
				}

				Timestamp end = new Timestamp(ca.getTime().getTime());

				task.setPlanEndDate(end);
				task.setStartDate(DateUtils.convertStartDate(start_date));
				task.setEndDate(DateUtils.convertEndDate(end_date));

				PersistenceHelper.manager.save(task);
			} else if (oid instanceof String) {
				Task parentTask = (Task) rf.getReference(poid).getObject();

				task = (Task) rf.getReference((String) oid).getObject();
				task.setName(text);
				task.setTemplate(template);
				task.setParentTask(parentTask);
				task.setDescription(description);

				if (duration != null) {
					task.setDuration((int) duration);
				} else {
					task.setDuration(1);
				}

				task.setTaskType(taskType);
				task.setPlanStartDate(DateUtils.convertStartDate(start_date));

				Calendar ca = Calendar.getInstance();
				ca.setTimeInMillis(task.getPlanStartDate().getTime());
				if (duration != null) {
					ca.add(Calendar.DATE, (int) duration);
				} else {
					ca.add(Calendar.DATE, 1);
				}

				Timestamp end = new Timestamp(ca.getTime().getTime());

				task.setPlanEndDate(end);
				task.setStartDate(DateUtils.convertStartDate(start_date));
				task.setEndDate(DateUtils.convertEndDate(end_date));

				PersistenceHelper.manager.modify(task);
			}

			int idx = 0;
			// 마지막..
			for (int k = 0; k < childrens.size(); k++) {
				Object obj = (Object) childrens.get(k);
				if (obj instanceof String) {
					String s = (String) obj;
					Task t = (Task) rf.getReference(s).getObject();

					t.setSort(idx);
					idx++;
					PersistenceHelper.manager.modify(t);
				} else if (obj instanceof Long) {
					task.setSort(idx);
					idx++;
					PersistenceHelper.manager.modify(task);
				}
			}

			commit(template);

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
	public Map<String, Object> onSaveAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String toid = (String) param.get("toid");
		String col = (String) param.get("col");
		String newValue = (String) param.get("newValue");

		Task task = null;
		Template template = null;
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

				template = task.getTemplate();
				task = (Task) PersistenceHelper.manager.modify(task);

				map.put("oid", task.getPersistInfo().getObjectIdentifier().getStringValue());
			} else {
				// 신규 생성
				// 템플릿 연결
				task = Task.newTask();
				template = (Template) rf.getReference(toid).getObject();
				task.setTemplate(template);
				task.setOwnership(ownership);
				int sort = TemplateHelper.manager.getMaxSort(template);

				task.setUpdateUser(ownership);

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

			commit(template);

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
	public Map<String, Object> onSaveTaskAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Object id = (Object) param.get("id");
		String poid = (String) param.get("poid");
		String toid = (String) param.get("toid");
		String text = (String) param.get("text");
		String description = (String) param.get("description");
		int duration = (int) param.get("duration");
		String taskType = (String) param.get("taskType");
		int depth = (int) param.get("depth");
		String start_date = (String) param.get("start_date");
		String end_date = (String) param.get("end_date");

		Task parentTask = null;
		Task task = null;
		Template template = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			if (id instanceof Long) {
				parentTask = (Task) rf.getReference(poid).getObject();
				template = (Template) rf.getReference(toid).getObject();

				WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
				Ownership ownership = Ownership.newOwnership(user);

				// 정렬 필요..

				// 부모 레벨
				int sort = TemplateHelper.manager.getMaxSort(template, parentTask, depth);

				task = Task.newTask();
				task.setName(text);
				task.setDepth(depth);
				task.setSort(sort);
				task.setTaskType(taskType);
				task.setParentTask(parentTask);
				task.setOwnership(ownership);
				task.setTemplate(template);
				task.setDescription(description);
				task.setDuration(duration);
				task.setPlanStartDate(DateUtils.convertStartDate(start_date));

				Calendar ca = Calendar.getInstance();
				ca.setTimeInMillis(task.getPlanStartDate().getTime());
				ca.add(Calendar.DATE, duration);
				Timestamp end = new Timestamp(ca.getTime().getTime());

				task.setPlanEndDate(end);
				task.setStartDate(DateUtils.convertStartDate(start_date));
				task.setEndDate(DateUtils.convertEndDate(end_date));

				task = (Task) PersistenceHelper.manager.save(task);

				map.put("id", id);
				map.put("oid", task.getPersistInfo().getObjectIdentifier().getStringValue());

			} else if (id instanceof String) {
				// id e3ps.project.Task....
				// 기존 내용 수정만..

				String oid = (String) id;
				task = (Task) rf.getReference(oid).getObject();

				task.setName(text);
				task.setTaskType(taskType);
				task.setDescription(description);
				task.setDuration(duration);
				task.setPlanStartDate(DateUtils.convertStartDate(start_date));

				Calendar ca = Calendar.getInstance();
				ca.setTimeInMillis(task.getPlanStartDate().getTime());
				ca.add(Calendar.DATE, duration);
				Timestamp end = new Timestamp(ca.getTime().getTime());

				task.setPlanEndDate(end);
				task.setStartDate(DateUtils.convertStartDate(start_date));
				task.setEndDate(DateUtils.convertEndDate(end_date));

				task = (Task) PersistenceHelper.manager.modify(task);

				template = task.getTemplate();

			}

			commit(template);

			map.put("poid", poid);
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
	public void commit(Template template) throws WTException {
		ArrayList<Task> list = new ArrayList<Task>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			// 모든 태스크 수집
			list = TemplateHelper.manager.getterTemplateTask(template, list);

//			initAllTemplatePlanDate(template.getPlanStartDate(), list);

//			setDependencyTask(list);

			setTemplateParentDate(list);

			setTemplateDuration(template);

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
				ArrayList<TargetTaskSourceTaskLink> targetList = TemplateHelper.manager
						.getTargetTaskSourceTaskLinkByTarget(task);

				// 후행 태스크의 계획 시작일
				Timestamp preStart = null;
				// 후행 태스크의 계획 종료일
				Timestamp preEnd = null;

				boolean edit = false;

				for (TargetTaskSourceTaskLink link : targetList) {
					Task targetTask = link.getTargetTask();
//					Task targetTask = link.getTargetTask();
					// 선행 태스크의 계획 종료일 세팅

					System.out.println("선행 = " + targetTask.getName());

					Calendar sCa = Calendar.getInstance();
					sCa.setTime(targetTask.getPlanEndDate());

					sCa.add(Calendar.DATE, link.getLag());

					Timestamp start = new Timestamp(sCa.getTime().getTime());

					BigDecimal bd = new BigDecimal(task.getDuration()); // 원래 태스크의 기간을 설정
					bd = bd.setScale(0, BigDecimal.ROUND_UP);
					int duration = bd.intValue();

					// 종료일
					Calendar eCa = Calendar.getInstance();
					// 2000-01-04...
					eCa.setTime(start);

					System.out.println("state==" + start);

					// + 10일
					for (int k = 0; k < duration; k++) {
						eCa.add(Calendar.DATE, 1);
						// eCa = DateUtils.checkHoliday(eCa);
					}
					// 2000-01-14..

					// 계획 종료일
					Timestamp end = new Timestamp(eCa.getTime().getTime());

					System.out.println("end==" + end);

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
		}
	}

	@Override
	public void initAllTemplatePlanDate(Timestamp planStartDate, ArrayList<Task> list) throws WTException {
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
	public void setTemplateDuration(Template template) throws WTException {
		Transaction trs = new Transaction();

		try {
			trs.start();

			Timestamp start = null;
			Timestamp end = null;
			boolean edit = false;

			ArrayList<Task> list = new ArrayList<Task>();
			list = TemplateHelper.manager.getterTemplateTasks(template, list);

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
	public void setTemplateParentDate(ArrayList<Task> list) throws WTException {
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
	public Map<String, Object> onDeleteTaskAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String toid = (String) param.get("toid");
		String parent = (String) param.get("parent");

		Task task = null;
		Template template = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			task = (Task) rf.getReference(oid).getObject();

			PersistenceHelper.manager.delete(task);

			template = (Template) rf.getReference(toid).getObject();

			commit(template);

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
	public Map<String, Object> onMoveTaskAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String toid = (String) param.get("toid");
		String oid = (String) param.get("oid");
		int depth = (int) param.get("depth");
		String parent = (String) param.get("parent");
		List<String> childrens = (List<String>) param.get("childrens");
		Task task = null;
		Template template = null;
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

				if (per instanceof Template) {
					task.setParentTask(null);
				} else if (per instanceof Task) {
					Task parentTask = (Task) per;
					task.setParentTask(parentTask);
				}
			}

			task = (Task) PersistenceHelper.manager.modify(task);

			int idx = 0;
			for (String s : childrens) {
				Task t = (Task) rf.getReference(s).getObject();

				t.setSort(idx);
				idx++;
				PersistenceHelper.manager.modify(t);
			}

			template = (Template) rf.getReference(toid).getObject();

			commit(template);

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

		Template template = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			template = (Template) rf.getReference(toid).getObject();

			sourceTask = (Task) rf.getReference(source).getObject(); // 선행
			targetTask = (Task) rf.getReference(target).getObject(); // 후행

			TargetTaskSourceTaskLink link = TargetTaskSourceTaskLink.newTargetTaskSourceTaskLink(sourceTask,
					targetTask);
			link.setTemplate(template);
			link.setProject(null);
			link.setLag(1);
			PersistenceHelper.manager.save(link);

			commit(template);

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
	public Map<String, Object> onAfterLinkDeleteAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String toid = (String) param.get("toid");
		Template template = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink) rf.getReference(oid).getObject();
			PersistenceHelper.manager.delete(link);

			template = (Template) rf.getReference(toid).getObject();

			commit(template);

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
	public Map<String, Object> onAfterTaskResizeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		String toid = (String) param.get("toid");
		String start_date = (String) param.get("start_date");
		String end_date = (String) param.get("end_date");
		int duration = (int) param.get("duration");
		Template template = null;
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

			template = (Template) rf.getReference(toid).getObject();

			commit(template);

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
	public Map<String, Object> addTemplateAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		Template template = null;
		ReferenceFactory rf = new ReferenceFactory();
		ArrayList<String[]> data = new ArrayList<String[]>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				template = (Template) rf.getReference(oid).getObject();
				TemplateViewData tdata = new TemplateViewData(template);
				// 0 oid, 1 number, 2 name, 3 version, 4 state, 5 creator, 6 createdate
				String[] s = new String[] { tdata.oid, tdata.duration, tdata.name, tdata.state, tdata.creator,
						tdata.createDate, tdata.modifier, tdata.modifyDate, tdata.iconPath, tdata.description };
				data.add(s);
			}

			map.put("result", SUCCESS);
			map.put("list", data);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "템플릿 추가 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/template/addTemplate");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void copyTasksInfo(Template org, Template copy) throws WTException {
		ArrayList<Task> list = new ArrayList<Task>();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			list = TemplateHelper.manager.getterTemplateTask(copy, list);

			HashMap<Task, Task> parentMap = new HashMap<Task, Task>();

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

				newTask.setOwnership(ownership);
				newTask.setUpdateUser(ownership);

				// 프로젝트 생성일로
				newTask.setPlanStartDate(orgTask.getPlanStartDate());
				newTask.setPlanEndDate(orgTask.getPlanEndDate());

				// 진행율
				newTask.setProgress(0);

				newTask.setProject(null);
				newTask.setTemplate(org);

				// 모자 관계
				Task parent = (Task) parentMap.get(orgTask.getParentTask());
				newTask.setParentTask(parent);
				newTask = (Task) PersistenceHelper.manager.save(newTask);

				parentMap.put(orgTask, newTask);
			}

			for (int i = 0; i < list.size(); i++) {
				Task orgTask = (Task) list.get(i);

				Task newTask = (Task) parentMap.get(orgTask);

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
	public void copyTemplateInfo(Template org, Template copy) throws WTException {
		Transaction trs = new Transaction();
		WTUser pm = null;
//		WTUser sub_pm = null;
		try {
			trs.start();

			pm = TemplateHelper.manager.getPMByTemplate(copy);

			if (pm != null) {
				TemplateUserLink link = TemplateUserLink.newTemplateUserLink(org, pm);
				link.setUserType(ProjectUserType.PM.name());
				PersistenceHelper.manager.save(link);
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
	public ArrayList<Template> getTemplate() throws WTException {
		ArrayList<Template> list = new ArrayList<Template>();
		try {
			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Template.class, true);

			ClassAttribute ca = new ClassAttribute(Template.class, Template.CREATE_TIMESTAMP);
			OrderBy orderBy = new OrderBy(ca, true);
			query.appendOrderBy(orderBy, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);

			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Template tp = (Template) obj[0];
				list.add(tp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
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

			commit(task.getTemplate());

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
		Template template = null;
		TargetTaskSourceTaskLink link = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			template = (Template) rf.getReference(toid).getObject();

			link = (TargetTaskSourceTaskLink) rf.getReference(oid).getObject();
			link.setLag(lag);

			PersistenceHelper.manager.modify(link);

			commit(template);

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
	public Map<String, Object> onSaveTemplate(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String text = (String) param.get("text");
		String oid = (String) param.get("oid");

		Template template = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			// 정렬 필요..
			template = (Template) rf.getReference(oid).getObject();
			template.setName(text);
			PersistenceHelper.manager.modify(template);
			commit(template);

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
