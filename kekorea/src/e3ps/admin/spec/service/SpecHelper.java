package e3ps.admin.spec.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.spec.Options;
import e3ps.admin.spec.Spec;
import e3ps.admin.spec.SpecOptionsLink;
import e3ps.admin.spec.beans.SpecColumnData;
import e3ps.common.util.PageQueryUtils;
import e3ps.common.util.QuerySpecUtils;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class SpecHelper {

	public static final SpecHelper manager = new SpecHelper();
	public static final SpecService service = ServiceFactory.getService(SpecService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<SpecColumnData> list = new ArrayList<SpecColumnData>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Spec.class, true);
		int idx_l = query.appendClassList(SpecOptionsLink.class, true);
		int idx_i = query.appendClassList(Options.class, false);

		SearchCondition sc = null;
		ClassAttribute ca = null;
		OrderBy by = null;

		sc = new SearchCondition(SpecOptionsLink.class, "roleAObjectRef.key.id", Spec.class,
				WTAttributeNameIfc.ID_NAME);
		sc.setFromIndicies(new int[] { idx_l, idx }, 0);
		sc.setOuterJoin(1);
		query.appendWhere(sc, new int[] { idx_l, idx });
		query.appendAnd();

		sc = new SearchCondition(SpecOptionsLink.class, "roleBObjectRef.key.id", Options.class,
				WTAttributeNameIfc.ID_NAME);
		sc.setFromIndicies(new int[] { idx_l, idx_i }, 0);
		sc.setOuterJoin(2);
		query.appendWhere(sc, new int[] { idx_l, idx_i });
		query.appendAnd();

		sc = new SearchCondition(Spec.class, Spec.LATEST, SearchCondition.IS_TRUE);
		query.appendWhere(sc, new int[] { idx });

		// 코드 타입 순서
		ca = new ClassAttribute(Spec.class, Spec.SORT);
		by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

		ca = new ClassAttribute(Options.class, Options.SORT);
		by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx_i });

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Spec spec = (Spec) obj[0];
			SpecOptionsLink link = (SpecOptionsLink) obj[1];
			SpecColumnData column = new SpecColumnData(spec, link);
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
			data.put("key", spec.getColumnKey());
			data.put("value", spec.getName());
			list.add(data);

		}

		return list;
	}

	public ArrayList<Map<String, Object>> remoter(String term, String target) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecOptionsLink.class, true);
		int idx_o = query.appendClassList(Options.class, true);
		int idx_s = query.appendClassList(Spec.class, false);

		QuerySpecUtils.toInnerJoin(query, SpecOptionsLink.class, Spec.class, "roleAObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_s);
		QuerySpecUtils.toInnerJoin(query, SpecOptionsLink.class, Options.class, "roleBObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_o);
		QuerySpecUtils.toEquals(query, idx_s, Spec.class, Spec.COLUMN_KEY, target);
		QuerySpecUtils.toLike(query, idx_o, Options.class, Options.NAME, term);
		QuerySpecUtils.toOrderBy(query, idx_o, Options.class, Options.SORT, false);

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Options options = (Options) obj[1];
			Map<String, Object> data = new HashMap<>();
			data.put("key", options.getPersistInfo().getObjectIdentifier().getStringValue());
			data.put("value", options.getName());
			list.add(data);
		}
		return list;
	}
}
