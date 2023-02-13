package e3ps.admin.spec.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.spec.Options;
import e3ps.admin.spec.Spec;
import e3ps.admin.spec.SpecOptionsLink;
import e3ps.admin.spec.beans.OptionsColumnData;
import e3ps.common.util.CommonUtils;
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

public class OptionsHelper {

	public static final OptionsHelper manager = new OptionsHelper();
	public static final OptionsService service = ServiceFactory.getService(OptionsService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<OptionsColumnData> list = new ArrayList<OptionsColumnData>();

		String oid = (String) params.get("oid");
		Spec spec = (Spec) CommonUtils.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecOptionsLink.class, true);
		int idx_i = query.appendClassList(Options.class, false);
		int idx_c = query.appendClassList(Spec.class, false);

		SearchCondition sc = null;
		ClassAttribute ca = null;
		OrderBy by = null;

		sc = new SearchCondition(SpecOptionsLink.class, "roleAObjectRef.key.id", Spec.class,
				WTAttributeNameIfc.ID_NAME);
		query.appendWhere(sc, new int[] { idx, idx_c });
		query.appendAnd();

		sc = new SearchCondition(SpecOptionsLink.class, "roleBObjectRef.key.id", Options.class,
				WTAttributeNameIfc.ID_NAME);
		query.appendWhere(sc, new int[] { idx, idx_i });
		query.appendAnd();

		sc = new SearchCondition(SpecOptionsLink.class, "roleAObjectRef.key.id", "=",
				spec.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		// 코드 타입 순서
		ca = new ClassAttribute(Options.class, Options.SORT);
		by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx_i });

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			SpecOptionsLink link = (SpecOptionsLink) obj[0];
			OptionsColumnData column = new OptionsColumnData(link);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public ArrayList<Map<String, Object>> remoter(Map<String, Object> params) throws Exception {
		String columnKey = (String) params.get("columnKey");
		String term = (String) params.get("term");
		ArrayList<Map<String, Object>> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(SpecOptionsLink.class, true);
		int idx_o = query.appendClassList(Options.class, true);
		int idx_s = query.appendClassList(Spec.class, false);

		QuerySpecUtils.toInnerJoin(query, SpecOptionsLink.class, Spec.class, "roleAObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_s);
		QuerySpecUtils.toInnerJoin(query, SpecOptionsLink.class, Options.class, "roleBObjectRef.key.id",
				WTAttributeNameIfc.ID_NAME, idx, idx_o);
		QuerySpecUtils.toEqualsAnd(query, idx_s, Spec.class, Spec.COLUMN_KEY, columnKey);
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

	public ArrayList<SpecOptionsLink> getLinks(Spec spec) throws Exception {
		ArrayList<SpecOptionsLink> list = new ArrayList<>();
		QueryResult result = PersistenceHelper.manager.navigate(spec, "options", SpecOptionsLink.class, false);
		while (result.hasMoreElements()) {
			SpecOptionsLink link = (SpecOptionsLink) result.nextElement();
			list.add(link);
		}
		;
		return list;
	}
}
