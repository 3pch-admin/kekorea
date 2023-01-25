package e3ps.admin.sheetvariable.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.sheetvariable.Category;
import e3ps.admin.sheetvariable.CategoryItemsLink;
import e3ps.admin.sheetvariable.Items;
import e3ps.admin.sheetvariable.beans.CategoryColumnData;
import e3ps.admin.sheetvariable.beans.ItemsColumnData;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.PageQueryUtils;
import wt.fc.PagingQueryResult;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.OrderBy;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.services.ServiceFactory;
import wt.util.WTAttributeNameIfc;

public class ItemsHelper {

	public static final ItemsHelper manager = new ItemsHelper();
	public static final ItemsService service = ServiceFactory.getService(ItemsService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<ItemsColumnData> list = new ArrayList<ItemsColumnData>();

		String oid = (String) params.get("oid");
		Category category = (Category) CommonUtils.getObject(oid);

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CategoryItemsLink.class, true);
		int idx_i = query.appendClassList(Items.class, false);
		int idx_c = query.appendClassList(Category.class, false);

		SearchCondition sc = null;
		ClassAttribute ca = null;
		OrderBy by = null;

		sc = new SearchCondition(CategoryItemsLink.class, "roleAObjectRef.key.id", Category.class,
				WTAttributeNameIfc.ID_NAME);
		query.appendWhere(sc, new int[] { idx, idx_c });
		query.appendAnd();

		sc = new SearchCondition(CategoryItemsLink.class, "roleBObjectRef.key.id", Items.class,
				WTAttributeNameIfc.ID_NAME);
		query.appendWhere(sc, new int[] { idx, idx_i });
		query.appendAnd();

		sc = new SearchCondition(CategoryItemsLink.class, "roleAObjectRef.key.id", "=",
				category.getPersistInfo().getObjectIdentifier().getId());
		query.appendWhere(sc, new int[] { idx });

		// 코드 타입 순서
		ca = new ClassAttribute(Items.class, Items.SORT);
		by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx_i });

		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			CategoryItemsLink link = (CategoryItemsLink) obj[0];
			ItemsColumnData column = new ItemsColumnData(link);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}

	public ArrayList<Items> getItems(Category category) throws Exception {
		ArrayList<Items> list = new ArrayList<>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(CategoryItemsLink.class, true);
		int idx_c = query.appendClassList(Category.class, false);

		SearchCondition sc = null;

		sc = new SearchCondition(CategoryItemsLink.class, "roleAObjectRef.key.id", Category.class,
				WTAttributeNameIfc.ID_NAME);
		query.appendWhere(sc, new int[] { idx, idx_c });
		query.appendAnd();

		sc = new SearchCondition(CategoryItemsLink.class, "roleAObjectRef.key.id", "=",
				category.getPersistInfo().getObjectIdentifier().getStringValue());
		query.appendWhere(sc, new int[] { idx });

		QueryResult result = PersistenceHelper.manager.find(query);
		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Items items = (Items) obj[1];
			list.add(items);
		}
		return list;
	}
}
