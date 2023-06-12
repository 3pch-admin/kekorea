package e3ps.admin.commonCode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.CommonCodeType;
import e3ps.admin.commonCode.dto.CommonCodeDTO;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
//import net.sf.json.JSONArray;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class CommonCodeHelper {

	public static final CommonCodeHelper manager = new CommonCodeHelper();
	public static final CommonCodeService service = ServiceFactory.getService(CommonCodeService.class);

	/**
	 * 코드타입 검색
	 */
	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CommonCodeDTO> list = new ArrayList<CommonCodeDTO>();

		String name = (String) params.get("name");
		String code = (String) params.get("code");
		String codeType = (String) params.get("codeType");
		String description = (String) params.get("description");
		boolean enable = (boolean) params.get("enable");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		QuerySpecUtils.toLikeAnd(query, idx, CommonCode.class, CommonCode.NAME, name);
		QuerySpecUtils.toLikeAnd(query, idx, CommonCode.class, CommonCode.CODE, code);
		QuerySpecUtils.toLikeAnd(query, idx, CommonCode.class, CommonCode.DESCRIPTION, description);
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, enable);

		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, codeType);

		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.CODE_TYPE, false);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			CommonCodeDTO column = new CommonCodeDTO(commonCode);
			list.add(column);
		}
		map.put("list", list);
		return map;
	}

	public JSONArray parseJson(String codeType) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("key", commonCode.getCode());
			map.put("value", commonCode.getName());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	public JSONArray parseJson() throws Exception {
		CommonCodeType[] codeTypes = CommonCodeType.getCommonCodeTypeSet();
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		for (CommonCodeType codeType : codeTypes) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("key", codeType.toString());
			map.put("value", codeType.getDisplay());
			list.add(map);
		}
		return JSONArray.fromObject(list);
	}

	public ArrayList<CommonCode> getArrayCodeList(String codeType) throws Exception {
		ArrayList<CommonCode> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.NAME, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			list.add(commonCode);
		}
		return list;
	}

	public ArrayList<Map<String, Object>> remoter(Map<String, Object> params) throws Exception {
		String term = (String) params.get("term");
		ArrayList<Map<String, Object>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toLikeAnd(query, idx, CommonCode.class, CommonCode.CODE, term);

		if (query.getConditionCount() > 0) {
			query.appendAnd();
		}

		query.appendOpenParen();
		QuerySpecUtils.toEquals(query, idx, CommonCode.class, CommonCode.CODE_TYPE, "MAK");
		QuerySpecUtils.toEqualsOr(query, idx, CommonCode.class, CommonCode.CODE_TYPE, "CUSTOMER");
		query.appendCloseParen();

		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.NAME, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("key", commonCode.getPersistInfo().getObjectIdentifier().getStringValue());
			data.put("value", commonCode.getName() + " [" + commonCode.getCodeType().getDisplay() + "]");
			data.put("codeType", commonCode.getCodeType().toString());
			list.add(data);
		}
		return list;
	}

	/**
	 * 코드 & 코드타입으로 코드 객체 찾아오기
	 */
	public CommonCode getCommonCode(String code, String codeType) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE, code);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, true);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			return commonCode;
		}
		return null;
	}

	/**
	 * 부모가 동일한 자식 코드 가져오기 KEY-VALUE 그리드용
	 */
	public ArrayList<Map<String, Object>> getChildrens(String parentCode, String codeType) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		CommonCode parent = getCommonCode(parentCode, codeType);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, "parentReference.key.id", parent);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("key", commonCode.getCode());
			map.put("value", commonCode.getName());
			list.add(map);
		}
		return list;
	}

	public ArrayList<Map<String, String>> getChildrens(String parentOid) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		CommonCode parent = (CommonCode) CommonUtils.getObject(parentOid);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, "parentReference.key.id",
				parent.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.SORT, false);
		QueryResult result = PersistenceHelper.manager.find(query);
		Map<String, String> empty = new HashMap<>();
		empty.put("value", "");
		empty.put("name", "선택");
		list.add(empty);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			Map<String, String> map = new HashMap<>();
			map.put("value", commonCode.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", commonCode.getName());
			list.add(map);
		}
		return list;
	}

	public ArrayList<Map<String, String>> getArrayKeyValueMap(String codeType) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Map<String, String> map = new HashMap<>();
			CommonCode commonCode = (CommonCode) obj[0];
			map.put("key", commonCode.getCode());
			map.put("value", commonCode.getName());
			list.add(map);
		}
		return list;
	}

	public ArrayList<Map<String, String>> getValueMap(String codeType) throws Exception {
		ArrayList<Map<String, String>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, codeType);
		QuerySpecUtils.toBooleanAnd(query, idx, CommonCode.class, CommonCode.ENABLE, true);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Map<String, String> map = new HashMap<>();
			CommonCode commonCode = (CommonCode) obj[0];
			map.put("key", commonCode.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("value", commonCode.getCode());
			list.add(map);
		}
		return list;
	}

	/**
	 * 코드타입 트리 가져오는 함수
	 */
	public JSONArray loadTree(Map<String, String> params) throws Exception {

		JSONArray list = new JSONArray();
		JSONObject rootNode = new JSONObject();
		rootNode.put("codeType", "ROOT");
		rootNode.put("name", "코드타입");

		JSONArray children = new JSONArray();

		CommonCodeType[] result = CommonCodeType.getCommonCodeTypeSet();
		for (CommonCodeType type : result) {
			JSONObject node = new JSONObject();
			node.put("codeType", type.toString());
			node.put("name", type.getDisplay());
			children.add(node);
		}
		rootNode.put("children", children);
		list.add(rootNode);
		return list;
	}
}
