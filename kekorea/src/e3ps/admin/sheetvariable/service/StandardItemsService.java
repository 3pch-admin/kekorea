package e3ps.admin.sheetvariable.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.sheetvariable.Category;
import e3ps.admin.sheetvariable.CategoryItemsLink;
import e3ps.admin.sheetvariable.Items;
import e3ps.common.util.CommonUtils;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardItemsService extends StandardManager implements ItemsService {

	public static StandardItemsService newStandardItemsService() throws WTException {
		StandardItemsService instance = new StandardItemsService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String coid = (String) params.get("coid");
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		ArrayList<Map<String, Object>> editRows = (ArrayList<Map<String, Object>>) params.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Category category = (Category) CommonUtils.getObject(coid);

			for (Map<String, Object> addRow : addRows) {
				String name = (String) addRow.get("name");
				int sort = (int) addRow.get("sort");
				boolean enable = (boolean) addRow.get("enable");

				Items items = Items.newItems();
				items.setName(name);
				items.setSort(sort);
				items.setEnable(enable);
				PersistenceHelper.manager.save(items);

				// 신규 링크
				CategoryItemsLink link = CategoryItemsLink.newCategoryItemsLink(category, items);
				PersistenceHelper.manager.save(link);
			}

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				Items items = (Items) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(items);
			}

			for (Map<String, Object> editRow : editRows) {
				String name = (String) editRow.get("name");
				int sort = (int) editRow.get("sort");
				boolean enable = (boolean) editRow.get("enable");
				String oid = (String) editRow.get("oid");
				Items items = (Items) CommonUtils.getObject(oid);
				items.setName(name);
				items.setSort(sort);
				items.setEnable(enable);
				PersistenceHelper.manager.modify(items);
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
