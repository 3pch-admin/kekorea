package e3ps.project.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.DateUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.People;
import e3ps.project.ParentTaskChildTaskLink;
import e3ps.project.TargetTaskSourceTaskLink;
import e3ps.project.Task;
import e3ps.project.template.Template;
import e3ps.project.template.TemplateUserLink;
import e3ps.project.template.dto.TemplateDTO;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class TemplateHelper implements MessageHelper {

	/**
	 * access service
	 */
	public static final TemplateService service = ServiceFactory.getService(TemplateService.class);

	/**
	 * access helper
	 */
	public static final TemplateHelper manager = new TemplateHelper();

	public WTUser getUserTypeByProject(Template template, String userType) throws Exception {
		WTUser user = null;

		long ids = template.getPersistInfo().getObjectIdentifier().getId();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TemplateUserLink.class, true);
		SearchCondition sc = new SearchCondition(TemplateUserLink.class, TemplateUserLink.USER_TYPE, "=", userType);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(TemplateUserLink.class, "roleAObjectRef.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TemplateUserLink link = (TemplateUserLink) obj[0];
			user = link.getUser();
		}
		return user;
	}

	public WTUser getPMByTemplate(Template template) throws Exception {
		WTUser pm = null;

		long ids = template.getPersistInfo().getObjectIdentifier().getId();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TemplateUserLink.class, true);
		SearchCondition sc = new SearchCondition(TemplateUserLink.class, TemplateUserLink.USER_TYPE, "=", "PM");
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(TemplateUserLink.class, "roleAObjectRef.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TemplateUserLink link = (TemplateUserLink) obj[0];
			pm = link.getUser();
		}
		return pm;
	}

	public WTUser getSubPMByTemplate(Template template) throws Exception {
		WTUser subPm = null;

		long ids = template.getPersistInfo().getObjectIdentifier().getId();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TemplateUserLink.class, true);
		SearchCondition sc = new SearchCondition(TemplateUserLink.class, TemplateUserLink.USER_TYPE, "=", "SUB_PM");
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(TemplateUserLink.class, "roleAObjectRef.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			TemplateUserLink link = (TemplateUserLink) obj[0];
			subPm = link.getUser();
		}
		return subPm;
	}

	public Map<String, Object> find(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		List<TemplateDTO> list = new ArrayList<TemplateDTO>();
		QuerySpec query = null;

		// search param
		String name = (String) param.get("name");
		String creatorsOid = (String) param.get("creatorsOid");
		String statesTemp = (String) param.get("statesTemp");

		String sort = (String) param.get("sort");
		String sortKey = (String) param.get("sortKey");
		String duration = (String) param.get("duration");
		// String creators = (String) param.get("creators");
		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		String predate_m = (String) param.get("predate_m");
		String postdate_m = (String) param.get("postdate_m");

		ReferenceFactory rf = new ReferenceFactory();

		try {
			query = new QuerySpec();
			int idx = query.appendClassList(Template.class, true);

			SearchCondition sc = null;
			ClassAttribute ca = null;

			if (!StringUtils.isNull(duration)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Template.class, Template.DURATION);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(duration);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(name)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				ca = new ClassAttribute(Template.class, Template.NAME);
				ColumnExpression ce = StringUtils.getUpperColumnExpression(name);
				SQLFunction function = SQLFunction.newSQLFunction(SQLFunction.UPPER, ca);
				sc = new SearchCondition(function, SearchCondition.LIKE, ce);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(creatorsOid)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				People user = (People) rf.getReference(creatorsOid).getObject();
				long ids = user.getUser().getPersistInfo().getObjectIdentifier().getId();
				sc = new SearchCondition(Template.class, "ownership.owner.key.id", SearchCondition.EQUAL, ids);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(predate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate);
				sc = new SearchCondition(Template.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate);
				sc = new SearchCondition(Template.class, WTAttributeNameIfc.CREATE_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			// 수정일
			if (!StringUtils.isNull(predate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp start = DateUtils.convertStartDate(predate_m);
				sc = new SearchCondition(Template.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.GREATER_THAN_OR_EQUAL, start);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(postdate_m)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();
				Timestamp end = DateUtils.convertEndDate(postdate_m);
				sc = new SearchCondition(Template.class, WTAttributeNameIfc.MODIFY_STAMP_NAME,
						SearchCondition.LESS_THAN_OR_EQUAL, end);
				query.appendWhere(sc, new int[] { idx });
			}

			if (!StringUtils.isNull(statesTemp)) {
				if (query.getConditionCount() > 0)
					query.appendAnd();

				sc = new SearchCondition(Template.class, Template.STATE, SearchCondition.EQUAL, statesTemp);
				query.appendWhere(sc, new int[] { idx });
			}

			if (StringUtils.isNull(sort)) {
				sort = "true";
			}

			if (StringUtils.isNull(sortKey)) {
				// sortKey = WTAttributeNameIfc.MODIFY_STAMP_NAME;
				sortKey = WTAttributeNameIfc.CREATE_STAMP_NAME;
				;
			}

			ca = new ClassAttribute(Template.class, sortKey);
			OrderBy orderBy = new OrderBy(ca, Boolean.parseBoolean(sort));
			query.appendOrderBy(orderBy, new int[] { idx });

			query.setAdvancedQueryEnabled(true);
			query.setDescendantQuery(false);

			PageQueryUtils pager = new PageQueryUtils(param, query);
			PagingQueryResult result = pager.find();
			while (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				Template template = (Template) obj[0];
				TemplateDTO data = new TemplateDTO(template);
				list.add(data);
			}
			map.put("list", list);
			map.put("lastPage", pager.getLastPage());
			map.put("topListCount", pager.getTotal());
			map.put("sessionid", pager.getSessionId());
			map.put("curPage", pager.getCpage());
			map.put("total", pager.getTotalSize());
			map.put("result", "SUCCESS");
		} catch (Exception e) {
			map.put("result", "FAIL");
			e.printStackTrace();
		}
		return map;
	}

	public JSONArray openTemplateTree(Map<String, Object> param) throws Exception {
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		Template template = (Template) rf.getReference(oid).getObject();

		JSONArray jsonArray = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("type", "root");
		rootNode.put("id", template.getPersistInfo().getObjectIdentifier().getStringValue());
		rootNode.put("title", template.getName());
		rootNode.put("expanded", true);
		rootNode.put("folder", true);
		getSubTemplateTree(template, rootNode);
		jsonArray.add(rootNode);
		return jsonArray;
	}

	public void getSubTemplateTree(Template root, JSONObject rootNode) throws Exception {
		ArrayList<Task> list = new ArrayList<Task>();
		list = getterTemplateTasks(root, list);
		JSONArray jsonChildren = new JSONArray();
		for (Task child : list) {

			JSONObject node = new JSONObject();
			node.put("type", "childrens");
			node.put("id", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("title", child.getName());
			node.put("expanded", false);
			node.put("folder", true);
			getSubTemplateTaskTree(child, root, node);

			jsonChildren.add(node);
		}
		rootNode.put("children", jsonChildren);
	}

	public void getSubTemplateTaskTree(Task parentTask, Template root, JSONObject rootNode) throws Exception {
		ArrayList<Task> list = new ArrayList<Task>();
		list = getterTemplateTask(parentTask, root, list);

		JSONArray jsonChildren = new JSONArray();
		for (Task child : list) {

			JSONObject node = new JSONObject();
			node.put("type", "childrens");
			node.put("id", child.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("title", child.getName());
			node.put("expanded", false);
			node.put("folder", true);
			getSubTemplateTaskTree(child, root, node);

			jsonChildren.add(node);
		}
		rootNode.put("children", jsonChildren);
	}

	public String loadGanttTemplate(Map<String, Object> param) throws Exception {
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		Template template = (Template) rf.getReference(oid).getObject();

		ArrayList<Task> list = new ArrayList<Task>();

		list = TemplateHelper.manager.getterTemplateTask(template, list);

		// 프로젝트 추가

		StringBuffer gantt = new StringBuffer();

		gantt.append("{\"data\": [");

		// project
		gantt.append("{");

		gantt.append("\"id\": \"" + template.getPersistInfo().getObjectIdentifier().getStringValue() + "\",");
		gantt.append("\"type\": \"project\",");
		gantt.append("\"isNew\": \"false\",");
		gantt.append("\"start_date\": \"" + DateUtils.formatTime(template.getPlanStartDate()) + "\",");
		gantt.append("\"end_date\": \"" + DateUtils.formatTime(template.getPlanEndDate()) + "\",");
		gantt.append(
				"\"duration\": " + DateUtils.getDuration(template.getPlanStartDate(), template.getPlanEndDate()) + ",");

		gantt.append("\"text\": \"" + template.getName() + "\",");
		gantt.append("\"taskType\": \"\",");
		gantt.append("\"parent\": \"0\",");

		if (list.size() == 0) {
			gantt.append("\"open\": false");
			gantt.append("}");
		} else if (list.size() > 0) {
			gantt.append("\"open\": true");
			gantt.append("},");

			for (int i = 0; i < list.size(); i++) {
				Task tt = (Task) list.get(i);

				gantt.append("{");
				gantt.append("\"id\": \"" + tt.getPersistInfo().getObjectIdentifier().getStringValue() + "\",");
				gantt.append("\"text\": \"" + tt.getName() + "\",");

				boolean hasChild = false;
				QueryResult result = PersistenceHelper.manager.navigate(tt, "childTask", ParentTaskChildTaskLink.class);
				if (result.size() > 0) {
					hasChild = true;
				}

				if (hasChild) {

					if (!tt.getName().equals("전기_수배표") && !tt.getName().equals("기계_수배표")) {
						gantt.append("\"type\": \"project\",");
					} else {
						gantt.append("\"type\": \"project\",");
					}
				} else {
					gantt.append("\"type\": \"task\",");
				}

				gantt.append("\"isNew\": \"false\",");
				gantt.append("\"start_date\": \"" + DateUtils.formatTime(tt.getPlanStartDate()) + "\",");
				gantt.append("\"end_date\": \"" + DateUtils.formatTime(tt.getPlanEndDate()) + "\",");
				gantt.append("\"taskType\": \"" + tt.getTaskType() + "\",");
				gantt.append("\"allocate\": \"" + tt.getAllocate() + "\",");
				gantt.append(
						"\"duration\": \"" + DateUtils.getDuration(tt.getPlanStartDate(), tt.getPlanEndDate()) + "\",");
				if ((list.size() - 1) == i) {
					if (StringUtils.isNull(tt.getParentTask())) {
						gantt.append("\"parent\": \"" + template.getPersistInfo().getObjectIdentifier().getStringValue()
								+ "\",");
						gantt.append("\"open\": true");
					} else {
						gantt.append("\"parent\": \""
								+ tt.getParentTask().getPersistInfo().getObjectIdentifier().getStringValue() + "\",");
						gantt.append("\"open\": true");
					}
					gantt.append("}");
				} else {
					if (StringUtils.isNull(tt.getParentTask())) {
						gantt.append("\"parent\": \"" + template.getPersistInfo().getObjectIdentifier().getStringValue()
								+ "\",");
						gantt.append("\"open\": true");
					} else {
						gantt.append("\"parent\": \""
								+ tt.getParentTask().getPersistInfo().getObjectIdentifier().getStringValue() + "\",");
						gantt.append("\"open\": true");
					}
					gantt.append("},");
				}
			}
		}

		gantt.append("],");

		gantt.append("\"links\": [");

		ArrayList<TargetTaskSourceTaskLink> linkList = getAllTargetList(list);

		for (int i = 0; i < linkList.size(); i++) {
			TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink) linkList.get(i);
			gantt.append("{");
			gantt.append("\"id\": \"" + link.getPersistInfo().getObjectIdentifier().getStringValue() + "\",");
			gantt.append("\"source\": \"" + link.getTargetTask().getPersistInfo().getObjectIdentifier().getStringValue()
					+ "\",");
			gantt.append("\"target\": \"" + link.getSourceTask().getPersistInfo().getObjectIdentifier().getStringValue()
					+ "\",");
			gantt.append("\"lag\": \"" + link.getLag() + "\",");
			gantt.append("\"type\": \"0\",");

			if ((linkList.size() - 1) == i) {
				gantt.append("}");
			} else {
				gantt.append("},");
			}
		}

		gantt.append("]");
		gantt.append("}");

		return gantt.toString();
	}

	private ArrayList<TargetTaskSourceTaskLink> getAllTargetList(ArrayList<Task> list) throws Exception {
		ArrayList<TargetTaskSourceTaskLink> lists = new ArrayList<TargetTaskSourceTaskLink>();
		for (int i = 0; i < list.size(); i++) {
			Task tt = (Task) list.get(i);

			QueryResult result = PersistenceHelper.manager.navigate(tt, "targetTask", TargetTaskSourceTaskLink.class,
					false);

			while (result.hasMoreElements()) {
				TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink) result.nextElement();
				lists.add(link);
			}
		}
		return lists;
	}

	public ArrayList<Task> getterTemplateTasks(Template template, ArrayList<Task> list) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);
		long ids = template.getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(Task.class, "templateReference.key.id", "=", ids);
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
		}
		return list;
	}

	/**
	 * 템플릿 연관 모든 태스크 가져오기
	 * 
	 * @param template
	 * @param list
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Task> getterTemplateTask(Template template, ArrayList<Task> list) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);
		long ids = template.getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(Task.class, "templateReference.key.id", "=", ids);
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
			getterTasks(t, template, list);
		}
		return list;
	}

	public void getterTasks(Task parentTask, Template template, ArrayList<Task> list) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);

		long ids = parentTask.getPersistInfo().getObjectIdentifier().getId();
		long tids = template.getPersistInfo().getObjectIdentifier().getId();

		SearchCondition sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(Task.class, "templateReference.key.id", "=", tids);
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
			getterTasks(t, template, list);
		}
	}

	public ArrayList<Task> getterTemplateTask(Task parentTask, Template template, ArrayList<Task> list)
			throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);

		long ids = parentTask.getPersistInfo().getObjectIdentifier().getId();
		long tids = template.getPersistInfo().getObjectIdentifier().getId();

		SearchCondition sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(Task.class, "templateReference.key.id", "=", tids);
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
		}
		return list;
	}

	public int getMaxSort(Template template) throws Exception {
		int max = 0;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);

		long tids = template.getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(Task.class, "templateReference.key.id", "=", tids);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(Task.class, Task.DEPTH, "=", 1);
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
		OrderBy orderBy = new OrderBy(ca, false);
		query.appendOrderBy(orderBy, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Task t = (Task) obj[0];

			if (t.getSort() != null) {
				max = t.getSort() + 1;
			}
		}
		return max;
	}

	public int getMaxSort(Template template, Task parentTask, int depth) throws Exception {
		int max = 0;
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Task.class, true);

		long ids = parentTask.getPersistInfo().getObjectIdentifier().getId();
		long tids = template.getPersistInfo().getObjectIdentifier().getId();

		SearchCondition sc = new SearchCondition(Task.class, "parentTaskReference.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(Task.class, "templateReference.key.id", "=", tids);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(Task.class, Task.DEPTH, "=", depth);
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(Task.class, Task.SORT);
		OrderBy orderBy = new OrderBy(ca, false);
		query.appendOrderBy(orderBy, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Task t = (Task) obj[0];
			if (t.getSort() != null) {
				max = t.getSort() + 1;
			}
		}

		query.setAdvancedQueryEnabled(true);
		query.setDescendantQuery(false);

		return max;
	}

	public ArrayList<TargetTaskSourceTaskLink> getTargetTaskSourceTaskLinkByTarget(Task task) throws Exception {
		ArrayList<TargetTaskSourceTaskLink> list = new ArrayList<TargetTaskSourceTaskLink>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(TargetTaskSourceTaskLink.class, true);

		long ids = task.getPersistInfo().getObjectIdentifier().getId();
		SearchCondition sc = new SearchCondition(TargetTaskSourceTaskLink.class, "roleBObjectRef.key.id", "=", ids);
		query.appendWhere(sc, new int[] { idx });

		query.appendAnd();

		long tids = task.getTemplate().getPersistInfo().getObjectIdentifier().getId();
		sc = new SearchCondition(TargetTaskSourceTaskLink.class, "templateReference.key.id", "=", tids);
		query.appendWhere(sc, new int[] { idx });

		// QueryResult result = PersistenceHelper.manager.navigate(task, "targetTask",
		// TargetTaskSourceTaskLink.class,
		// false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			// TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink)
			// result.nextElement();
			TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink) obj[0];
			list.add(link);
		}
		return list;
	}

	public ArrayList<TargetTaskSourceTaskLink> getTargetTaskSourceTaskLinkBySource(Task task) throws Exception {
		ArrayList<TargetTaskSourceTaskLink> list = new ArrayList<TargetTaskSourceTaskLink>();

		QueryResult result = PersistenceHelper.manager.navigate(task, "sourceTask", TargetTaskSourceTaskLink.class,
				false);
		while (result.hasMoreElements()) {
			TargetTaskSourceTaskLink link = (TargetTaskSourceTaskLink) result.nextElement();
			list.add(link);
		}
		return list;
	}

	public Template getTemplateByName(String templateName) throws Exception {
		Template template = null;

		try {

			QuerySpec query = new QuerySpec();
			int idx = query.appendClassList(Template.class, true);

			SearchCondition sc = new SearchCondition(Template.class, Template.NAME, "=", templateName);
			query.appendWhere(sc, new int[] { idx });

			QueryResult result = PersistenceHelper.manager.find(query);
			if (result.hasMoreElements()) {
				Object[] obj = (Object[]) result.nextElement();
				template = (Template) obj[0];
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return template;
	}
}
