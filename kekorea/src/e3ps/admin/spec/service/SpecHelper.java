package e3ps.admin.spec.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.beans.CommonCodeColumnData;
import e3ps.admin.spec.Spec;
import e3ps.admin.spec.beans.SpecColumnData;
import e3ps.common.util.PageQueryUtils;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;

public class SpecHelper {

	public static final SpecHelper manager = new SpecHelper();
	public static final SpecService service = ServiceFactory.getService(SpecService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<SpecColumnData> list = new ArrayList<SpecColumnData>();

		String name = (String) params.get("name");

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Spec.class, true);

		SearchCondition sc = null;
		ClassAttribute ca = null;
		OrderBy by = null;

		// 코드 타입 순서
		ca = new ClassAttribute(Spec.class, Spec.SORT);
		by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Spec spec = (Spec) obj[0];
			SpecColumnData column = new SpecColumnData(spec);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public ArrayList<Map<String, Object>> getSpecKeyValue() throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		QuerySpec query = new QuerySpec();

		int idx = query.appendClassList(Spec.class, true);

		SearchCondition sc = new SearchCondition(Spec.class, Spec.ENABLE, SearchCondition.IS_TRUE);
		query.appendWhere(sc, new int[] { idx });
		query.appendAnd();

		sc = new SearchCondition(Spec.class, Spec.LATEST, SearchCondition.IS_TRUE);
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Spec spec = (Spec) obj[0];

			Map<String, Object> data = new HashMap<>();
			data.put("key", spec.getColKey());
			data.put("value", spec.getName());
			list.add(data);

		}

		return list;
	}
}
