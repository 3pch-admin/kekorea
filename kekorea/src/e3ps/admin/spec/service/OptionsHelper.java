package e3ps.admin.spec.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.sheetvariable.Category;
import e3ps.admin.sheetvariable.CategoryItemsLink;
import e3ps.admin.sheetvariable.Items;
import e3ps.admin.sheetvariable.beans.ItemsColumnData;
import e3ps.admin.spec.Options;
import e3ps.admin.spec.Spec;
import e3ps.admin.spec.SpecOptionsLink;
import e3ps.admin.spec.beans.OptionsColumnData;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import wt.fc.PagingQueryResult;
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
}
