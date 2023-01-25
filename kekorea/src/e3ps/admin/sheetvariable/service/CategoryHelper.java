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

public class CategoryHelper {

	public static final CategoryHelper manager = new CategoryHelper();
	public static final CategoryService service = ServiceFactory.getService(CategoryService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		List<CategoryColumnData> list = new ArrayList<CategoryColumnData>();

		QuerySpec query = new QuerySpec();
		int idx = query.appendClassList(Category.class, true);
		int idx_l = query.appendClassList(CategoryItemsLink.class, true);
		int idx_i = query.appendClassList(Items.class, false);

		SearchCondition sc = null;
		ClassAttribute ca = null;
		OrderBy by = null;

		sc = new SearchCondition(CategoryItemsLink.class, "roleAObjectRef.key.id", Category.class,
				WTAttributeNameIfc.ID_NAME);
		sc.setFromIndicies(new int[] { idx_l, idx }, 0);
		sc.setOuterJoin(1);
		query.appendWhere(sc, new int[] { idx_l, idx });
		query.appendAnd();

		sc = new SearchCondition(CategoryItemsLink.class, "roleBObjectRef.key.id", Items.class,
				WTAttributeNameIfc.ID_NAME);
		sc.setFromIndicies(new int[] { idx_l, idx_i }, 0);
		sc.setOuterJoin(2);
		query.appendWhere(sc, new int[] { idx_l, idx_i });
		query.appendAnd();

		sc = new SearchCondition(Category.class, Category.LATEST, SearchCondition.IS_TRUE);
		query.appendWhere(sc, new int[] { idx });

		// 코드 타입 순서
		ca = new ClassAttribute(Category.class, Category.SORT);
		by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx });

		ca = new ClassAttribute(Items.class, Items.SORT);
		by = new OrderBy(ca, false);
		query.appendOrderBy(by, new int[] { idx_i });
		
		PageQueryUtils pager = new PageQueryUtils(params, query);
		PagingQueryResult result = pager.find();

		while (result.hasMoreElements()) {
			Object[] obj = (Object[]) result.nextElement();
			Category category = (Category) obj[0];
			CategoryItemsLink link = (CategoryItemsLink) obj[1];
			CategoryColumnData column = new CategoryColumnData(category, link);
			list.add(column);
		}

		map.put("list", list);
		map.put("sessionid", pager.getSessionId());
		map.put("curPage", pager.getCpage());
		return map;
	}
}
