package e3ps.admin.sheetvariable.service;

import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.QuerySpecUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class SheetVariableHelper {

	public static final SheetVariableHelper manager = new SheetVariableHelper();
	public static final SheetVariableService service = ServiceFactory.getService(SheetVariableService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();

		JSONArray root = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", "0");
		rootNode.put("name", "CONFIG SHEET 카테고리");
		rootNode.put("code", "ROOT");
		rootNode.put("codeType", "");
		rootNode.put("description", "CONFIG SHEET 카테고리");
		rootNode.put("sort", 0);
		rootNode.put("enable", true);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, "CATEGORY");
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);

		JSONArray children = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode categoryCode = (CommonCode) obj[0];
			JSONObject node = new JSONObject();
			node.put("oid", categoryCode.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", categoryCode.getName());
			node.put("code", categoryCode.getCode());
			node.put("codeType", categoryCode.getCodeType().toString());
			node.put("description", categoryCode.getDescription());
			node.put("enable", categoryCode.getEnable());
			node.put("sort", categoryCode.getSort());
			items(node, categoryCode);
			children.add(node);
		}
		rootNode.put("children", children);
		root.add(rootNode);
		map.put("list", root);
		return map;
	}

	private void items(JSONObject parentNode, CommonCode categoryCode) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, "CATEGORY_ITEM");
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, "parentReference.key.id",
				categoryCode.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		JSONArray children = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode itemCode = (CommonCode) obj[0];
			JSONObject node = new JSONObject();
			node.put("oid", itemCode.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", itemCode.getName());
			node.put("code", itemCode.getCode());
			node.put("codeType", itemCode.getCodeType().toString());
			node.put("description", itemCode.getDescription());
			node.put("sort", itemCode.getSort());
			node.put("enable", itemCode.getEnable());
			specs(node, itemCode);
			children.add(node);
		}
		parentNode.put("children", children);
	}

	private void specs(JSONObject parentNode, CommonCode itemCode) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, "CATEGORY_SPEC");
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, "parentReference.key.id",
				itemCode.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		JSONArray children = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode specCode = (CommonCode) obj[0];
			JSONObject node = new JSONObject();
			node.put("oid", specCode.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", specCode.getName());
			node.put("code", specCode.getCode());
			node.put("codeType", specCode.getCodeType().toString());
			node.put("description", specCode.getDescription());
			node.put("sort", specCode.getSort());
			node.put("enable", specCode.getEnable());
			children.add(node);
		}
		parentNode.put("children", children);
	}
}
