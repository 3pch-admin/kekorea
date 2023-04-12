package e3ps.admin.configSheetCode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.configSheetCode.ConfigSheetCode;
import e3ps.common.util.QuerySpecUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.services.ServiceFactory;

public class ConfigSheetCodeHelper {

	public static final ConfigSheetCodeHelper manager = new ConfigSheetCodeHelper();
	public static final ConfigSheetCodeService service = ServiceFactory.getService(ConfigSheetCodeService.class);

	/**
	 * CONFIG SHEET 카테고리 리스트
	 */
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
		int idx = query.appendClassList(ConfigSheetCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.CODE_TYPE, "CATEGORY");
//		QuerySpecUtils.toBooleanAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, ConfigSheetCode.class, ConfigSheetCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);

		JSONArray children = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheetCode categoryCode = (ConfigSheetCode) obj[0];
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

	/**
	 * CONFIG SHEET 아이템 리스트
	 */
	private void items(JSONObject parentNode, ConfigSheetCode categoryCode) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheetCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.CODE_TYPE, "CATEGORY_ITEM");
		QuerySpecUtils.toEqualsAnd(query, idx, ConfigSheetCode.class, "parentReference.key.id",
				categoryCode.getPersistInfo().getObjectIdentifier().getId());
//		QuerySpecUtils.toBooleanAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, ConfigSheetCode.class, ConfigSheetCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		JSONArray children = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheetCode itemCode = (ConfigSheetCode) obj[0];
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

	/**
	 * CONFIG SHEET 사양 리스트
	 */
	private void specs(JSONObject parentNode, ConfigSheetCode itemCode) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheetCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.CODE_TYPE, "CATEGORY_SPEC");
		QuerySpecUtils.toEqualsAnd(query, idx, ConfigSheetCode.class, "parentReference.key.id",
				itemCode.getPersistInfo().getObjectIdentifier().getId());
//		QuerySpecUtils.toBooleanAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, ConfigSheetCode.class, ConfigSheetCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		JSONArray children = new JSONArray();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheetCode specCode = (ConfigSheetCode) obj[0];
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

	/**
	 * CONFIG SHEET CODE JSON 형태로 가져오기 (KEY-VALUE)
	 */
	public JSONArray parseJson(String codeType) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheetCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, ConfigSheetCode.class, ConfigSheetCode.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheetCode configSheetCode = (ConfigSheetCode) obj[0];
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("key", configSheetCode.getCode());
			map.put("value", configSheetCode.getName());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	/**
	 * 코드와 코드타입이 일치하는 CONFIG SHEET CODE 가져오기
	 */
	public ConfigSheetCode getConfigSheetCode(String code, String codeType) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(ConfigSheetCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.CODE, code);
		QuerySpecUtils.toEqualsAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, ConfigSheetCode.class, ConfigSheetCode.ENABLE, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			ConfigSheetCode configSheetCode = (ConfigSheetCode) obj[0];
			return configSheetCode;
		}
		return null;
	}
}
