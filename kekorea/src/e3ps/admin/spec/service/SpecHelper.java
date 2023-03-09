package e3ps.admin.spec.service;

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

public class SpecHelper {

	public static final SpecHelper manager = new SpecHelper();
	public static final SpecService service = ServiceFactory.getService(SpecService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();

		JSONArray root = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", "0");
		rootNode.put("name", "사양관리");
		rootNode.put("code", "ROOT");
		rootNode.put("codeType", "");
		rootNode.put("description", "사양관리");
		rootNode.put("sort", 0);
		rootNode.put("enable", true);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, "SPEC");
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
			node.put("enable", specCode.getEnable());
			node.put("sort", specCode.getSort());
			options(node, specCode);
			children.add(node);
		}
		rootNode.put("children", children);
		root.add(rootNode);
		map.put("list", root);
		return map;
	}

	private void options(JSONObject parentNode, CommonCode specCode) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, "OPTION");
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, "parentReference.key.id",
				specCode.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		JSONArray children = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode optionCode = (CommonCode) obj[0];
			JSONObject node = new JSONObject();
			node.put("oid", optionCode.getPersistInfo().getObjectIdentifier().getStringValue());
			node.put("name", optionCode.getName());
			node.put("code", optionCode.getCode());
			node.put("codeType", optionCode.getCodeType().toString());
			node.put("description", optionCode.getDescription());
			node.put("sort", optionCode.getSort());
			node.put("enable", optionCode.getEnable());
			children.add(node);
		}
		parentNode.put("children", children);
	}
}