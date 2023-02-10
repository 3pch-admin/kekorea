package e3ps.project.template.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.project.template.Template;
import e3ps.project.template.beans.TemplateColumnData;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class TemplateHelper {

	public static final TemplateHelper manager = new TemplateHelper();
	public static final TemplateService service = ServiceFactory.getService(TemplateService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<TemplateColumnData> list = new ArrayList<TemplateColumnData>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Template.class, true);
		QuerySpecUtils.toOrderBy(query, idx, Template.class, Template.CREATE_TIMESTAMP, true);
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Template template = (Template) obj[0];
			TemplateColumnData column = new TemplateColumnData(template);
			list.add(column);
		}
		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public ArrayList<HashMap<String, Object>> getTemplateArrayMap() throws Exception {
		ArrayList<HashMap<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Template.class, true);
		QuerySpecUtils.toBoolean(query, idx, Template.class, Template.ENABLE, SearchCondition.IS_TRUE);
		QuerySpecUtils.toOrderBy(query, idx, Template.class, Template.CREATE_TIMESTAMP, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			HashMap<String, Object> map = new HashMap<>();
			Template template = (Template) obj[0];
			map.put("key", template.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("value", template.getName());
			list.add(map);
		}
		return list;
	}

	public JSONArray load(String oid) throws Exception {
		Template template = (Template) CommonUtils.getObject(oid);
		JSONArray list = new JSONArray();
		JSONObject node = new JSONObject();
		node.put("oid", template.getPersistInfo().getObjectIdentifier().getStringValue());
		node.put("id", template.getPersistInfo().getObjectIdentifier().getId());
		node.put("name", template.getName());
		node.put("description", template.getDescription());
		node.put("duration", template.getDuration());
		list.add(node);
		return list;
	}
}
