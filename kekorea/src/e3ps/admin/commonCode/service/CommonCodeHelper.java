package e3ps.admin.commonCode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.CommonCodeType;
import e3ps.admin.commonCode.beans.CommonCodeColumnData;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class CommonCodeHelper {

	public static final CommonCodeHelper manager = new CommonCodeHelper();
	public static final CommonCodeService service = ServiceFactory.getService(CommonCodeService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CommonCodeColumnData> list = new ArrayList<CommonCodeColumnData>();

		String name = (String) params.get("name");
		String code = (String) params.get("code");
		String codeType = (String) params.get("codeType");
		String description = (String) params.get("description");
		String enable = (String) params.get("enable");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		if (!StringUtils.isNull(name)) {
			QuerySpecUtils.toLikeAnd(query, idx, CommonCode.class, CommonCode.NAME, name);
		}

		if (!StringUtils.isNull(code)) {
			QuerySpecUtils.toLikeAnd(query, idx, CommonCode.class, CommonCode.CODE, code);
		}

		if (!StringUtils.isNull(description)) {
			QuerySpecUtils.toLikeAnd(query, idx, CommonCode.class, CommonCode.DESCRIPTION, description);
		}

		if (!StringUtils.isNull(enable)) {
			QuerySpecUtils.toBoolean(query, idx, CommonCode.class, CommonCode.ENABLE, Boolean.parseBoolean(enable));
		}

		if (!StringUtils.isNull(codeType)) {

			if (codeType.equals("MAK")) {
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", "MAK"),
						new int[] { idx });
				query.appendOr();
				query.appendWhere(new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", "MAK_DETAIL"),
						new int[] { idx });
				query.appendCloseParen();
			} else if (codeType.equals("CUSTOMER")) {
				query.appendOpenParen();
				query.appendWhere(new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", "CUSTOMER"),
						new int[] { idx });
				query.appendOr();
				query.appendWhere(new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", "INSTALL"),
						new int[] { idx });
				query.appendCloseParen();
			} else {
				QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, codeType);
			}
		}

		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.CODE_TYPE, false);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.NAME, false);

//		PageQueryUtils pager = new PageQueryUtils(params, query);
//		PagingQueryResult result = pager.find();
		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			CommonCodeColumnData column = new CommonCodeColumnData(commonCode);
			list.add(column);
		}

		map.put("list", list);
//		map.put("sessionid", pager.getSessionId());
//		map.put("curPage", pager.getCpage());
		return map;
	}

	public JSONArray parseJson(String codeType) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		SearchCondition sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", codeType);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(CommonCode.class, CommonCode.ENABLE, SearchCondition.IS_TRUE);
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.NAME);
		OrderBy by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("key", commonCode.getCode());
			map.put("value", commonCode.getName());
			list.add(map);
		}
		return new JSONArray(list);
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
		return new JSONArray(list);
	}

	public ArrayList<CommonCode> getArrayCodeList(String codeType) throws Exception {
		ArrayList<CommonCode> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		SearchCondition sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", codeType);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(CommonCode.class, CommonCode.ENABLE, SearchCondition.IS_TRUE);
		query.appendWhere(sc, new int[] { idx });

		ClassAttribute ca = new ClassAttribute(CommonCode.class, CommonCode.NAME);
		OrderBy by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

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

	public CommonCode getCommonCode(String code, String codeType) throws Exception {
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE, code);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, CommonCode.CODE_TYPE, codeType);
		QueryResult result = PersistenceHelper.manager.find(query);
		if (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			return commonCode;
		}
		return null;
	}

	public ArrayList<Map<String, Object>> getChildrens(String parentCode, String codeType) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		CommonCode parent = getCommonCode(parentCode, codeType);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, "parentReference.key.id",
				parent.getPersistInfo().getObjectIdentifier().getId());
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

	public ArrayList<Map<String, Object>> getChildrens(String parentOid) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		CommonCode parent = (CommonCode) CommonUtils.getObject(parentOid);
		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toEqualsAnd(query, idx, CommonCode.class, "parentReference.key.id",
				parent.getPersistInfo().getObjectIdentifier().getId());
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.NAME, false);
		QueryResult result = PersistenceHelper.manager.find(query);

		Map<String, Object> empty = new HashMap<>();
		empty.put("value", "");
		empty.put("name", "선택");
		list.add(empty);

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("value", commonCode.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("name", commonCode.getName());
			list.add(map);
		}
		return list;
	}
}
