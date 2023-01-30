package e3ps.admin.commonCode.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.CommonCodeType;
import e3ps.admin.commonCode.beans.CommonCodeColumnData;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.Project;
import wt.fc.PagingQueryResult;
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

		System.out.println("name=" + name);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		if (!StringUtils.isNull(name)) {
			QuerySpecUtils.toLike(query, idx, CommonCode.class, CommonCode.NAME, name);
		}

		if (!StringUtils.isNull(code)) {
			QuerySpecUtils.toLike(query, idx, CommonCode.class, CommonCode.CODE, code);
		}

		if (!StringUtils.isNull(description)) {
			QuerySpecUtils.toLike(query, idx, CommonCode.class, CommonCode.DESCRIPTION, description);
		}

		if (!StringUtils.isNull(enable)) {
			QuerySpecUtils.toBoolean(query, idx, CommonCode.class, CommonCode.ENABLE, enable);
		}

		if (!StringUtils.isNull(codeType)) {
			QuerySpecUtils.toEquals(query, idx, CommonCode.class, CommonCode.CODE_TYPE, codeType);
		}

		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.CODE_TYPE, false);
		QuerySpecUtils.toOrderBy(query, idx, CommonCode.class, CommonCode.NAME, false);

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CommonCode commonCode = (CommonCode) obj[0];
			CommonCodeColumnData column = new CommonCodeColumnData(commonCode);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
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

	public ArrayList<Map<String, Object>> remoter(String term) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);
		QuerySpecUtils.toLike(query, idx, CommonCode.class, CommonCode.CODE, term);
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
}
