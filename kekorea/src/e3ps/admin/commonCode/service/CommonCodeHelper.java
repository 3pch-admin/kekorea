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
import e3ps.common.util.StringUtils;
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
		String codeType = (String) params.get("codeType");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CommonCode.class, true);

		SearchCondition sc = null;
		ClassAttribute ca = null;
		OrderBy by = null;

		if (!StringUtils.isNull(codeType)) {
			if (query.getConditionCount() > 0) {
				query.appendAnd();
			}
			sc = new SearchCondition(CommonCode.class, CommonCode.CODE_TYPE, "=", codeType);
			query.appendWhere(sc, new int[] { idx });
		}

		// 코드 타입 순서
		ca = new ClassAttribute(CommonCode.class, CommonCode.CODE_TYPE);
		by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

		// 코드명 순서
		ca = new ClassAttribute(CommonCode.class, CommonCode.NAME);
		by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

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
}
