package e3ps.admin.specCode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.specCode.SpecCode;
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

	/**
	 * 이력관리 컬럼 리스트
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<>();

		JSONArray root = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("oid", "0");
		rootNode.put("name", "이력관리 컬럼");
		rootNode.put("code", "ROOT");
		rootNode.put("codeType", "");
		rootNode.put("description", "이력관리 컬럼");
		rootNode.put("sort", 0);
		rootNode.put("enable", true);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, SpecCode.CODE_TYPE, "SPEC");
//		QuerySpecUtils.toBooleanAnd(query, idx, SpecCode.class, SpecCode.ENABLE, true);
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
			node.put("parent", null);
			options(node, specCode);
			children.add(node);
		}
		rootNode.put("children", children);
		root.add(rootNode);
		map.put("list", root);
		return map;
	}

	/**
	 * 이력관리 컬럼 옵션 리스트 가져오기
	 */
	private void options(JSONObject parentNode, SpecCode specCode) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, SpecCode.CODE_TYPE, "OPTION");
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, "parentReference.key.id", specCode);
//		QuerySpecUtils.toBooleanAnd(query, idx, SpecCode.class, SpecCode.ENABLE, true);
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
			node.put("parent", specCode.getPersistInfo().getObjectIdentifier().getStringValue());
			children.add(node);
		}
		parentNode.put("children", children);
	}

	/**
	 * 이력관리 헤더에 사용될 사양에 따른 옵션 리스트
	 */
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

	/**
	 * 이력관리 헤더에 사용될 사양에 따른 옵션 리스트
	 */
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

	/**
	 * 이력관리 코드 KEY-VALUE 값으로 저장되어있는 배열데이터로 가져오기
	 */
	public ArrayList<Map<String, String>> getArrayKeyValueMap(String codeType) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, SpecCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, SpecCode.class, SpecCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, SpecCode.class, SpecCode.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Map<String, String> map = new HashMap<>();
			SpecCode specCode = (SpecCode) obj[0];
			map.put("key", specCode.getCode());
			map.put("value", specCode.getName());
			list.add(map);
		}
		return list;
	}

	/**
	 * 코드 타입과 코드에 맞는 이력관리 코드 가져오기
	 */
	public SpecCode getSpecCode(String code, String codeType) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, SpecCode.CODE, code);
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, SpecCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, SpecCode.class, SpecCode.ENABLE, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			SpecCode specCode = (SpecCode) obj[0];
			return specCode;
		}
		return null;
	}

	/**
	 * SPEC 코드만 가져오기
	 */
	public ArrayList<SpecCode> getSpecCode() throws Exception {
		ArrayList<SpecCode> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, SpecCode.class, SpecCode.CODE_TYPE, "SPEC");
		QuerySpecUtils.toBooleanAnd(query, idx, SpecCode.class, SpecCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, SpecCode.class, SpecCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			SpecCode specCode = (SpecCode) obj[0];
			list.add(specCode);
		}
		return list;
	}
}