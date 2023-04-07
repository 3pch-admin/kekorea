package e3ps.admin.specCode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.spec.SpecCode;
import e3ps.common.util.QuerySpecUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class SpecCodeHelper {

	public static final SpecCodeHelper manager = new SpecCodeHelper();
	public static final SpecCodeService service = ServiceFactory.getService(SpecCodeService.class);

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
		int idx = query.appendClassList(SpecCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, SpecCode.CODE_TYPE, "SPEC");
		QuerySpecUtils.toBooleanAnd(query, idx, SpecCode.class, SpecCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, SpecCode.class, SpecCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);

		JSONArray children = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			SpecCode specCode = (SpecCode) obj[0];
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

	private void options(JSONObject parentNode, SpecCode specCode) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, SpecCode.CODE_TYPE, "OPTION");
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, "parentReference.key.id", specCode);
		QuerySpecUtils.toBooleanAnd(query, idx, SpecCode.class, SpecCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, SpecCode.class, SpecCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		JSONArray children = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			SpecCode optionCode = (SpecCode) obj[0];
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

	public Map<String, ArrayList<Map<String, String>>> getOptionList() throws Exception {
		Map<String, ArrayList<Map<String, String>>> map = new HashMap<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, SpecCode.CODE_TYPE, "SPEC");
		QuerySpecUtils.toBooleanAnd(query, idx, SpecCode.class, SpecCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, SpecCode.class, SpecCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			SpecCode specCode = (SpecCode) obj[0];
			ArrayList<Map<String, String>> list = getOption(specCode);
			map.put(specCode.getCode(), list);
		}
		return map;
	}

	private ArrayList<Map<String, String>> getOption(SpecCode specCode) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, SpecCode.CODE_TYPE, "OPTION");
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, "parentReference.key.id", specCode);
		QuerySpecUtils.toBooleanAnd(query, idx, SpecCode.class, SpecCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, SpecCode.class, SpecCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			SpecCode optionCode = (SpecCode) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("key", optionCode.getCode());
			map.put("value", optionCode.getName());
			list.add(map);
		}
		return list;
	}
}