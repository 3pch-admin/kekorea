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

public class StandardCategoryService extends StandardManager implements CategoryService {

	public static StandardCategoryService newStandardCategoryService() throws WTException {
		StandardCategoryService instance = new StandardCategoryService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		ArrayList<Map<String, Object>> editRows = (ArrayList<Map<String, Object>>) params.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> addRow : addRows) {
				String cname = (String) addRow.get("cname");
				int csort = (int) addRow.get("csort");
				boolean enable = (boolean) addRow.get("enable");

				Category category = Category.newCategory();
				category.setName(cname);
				category.setSort(csort);
				category.setVersion(1);
				category.setLatest(true);
				category.setEnable(enable);
				PersistenceHelper.manager.save(category);
			}

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				Category category = (Category) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(category);
			}

			for (Map<String, Object> editRow : editRows) {
				String cname = (String) editRow.get("cname");
				int csort = (int) editRow.get("csort");
				String oid = (String) editRow.get("oid");
				boolean enable = (boolean) editRow.get("enable");
				Category pre = (Category) CommonUtils.getObject(oid);
				// 기존꺼는 이전 버전으로 돌린다
				pre.setLatest(false);
				PersistenceHelper.manager.modify(pre);

				Category latest = Category.newCategory();
				latest.setName(cname);
				latest.setSort(csort);
				latest.setVersion(pre.getVersion() + 1);
				latest.setLatest(true);
				latest.setEnable(enable);
				PersistenceHelper.manager.save(latest);

				ArrayList<Items> list = ItemsHelper.manager.getItems(pre);
				for (Items items : list) {
					CategoryItemsLink link = CategoryItemsLink.newCategoryItemsLink(latest, items);
					PersistenceHelper.manager.save(link);
				}
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
