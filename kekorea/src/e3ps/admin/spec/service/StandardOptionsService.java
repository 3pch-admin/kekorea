package e3ps.admin.spec.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.sheetvariable.Category;
import e3ps.admin.sheetvariable.CategoryItemsLink;
import e3ps.admin.sheetvariable.Items;
import e3ps.admin.spec.Options;
import e3ps.admin.spec.Spec;
import e3ps.admin.spec.SpecOptionsLink;
import e3ps.common.util.CommonUtils;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardOptionsService extends StandardManager implements OptionsService {

	public static StandardOptionsService newStandardOptionService() throws WTException {
		StandardOptionsService instance = new StandardOptionsService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		String soid = (String) params.get("soid");
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		ArrayList<Map<String, Object>> editRows = (ArrayList<Map<String, Object>>) params.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Spec spec = (Spec) CommonUtils.getObject(soid);

			for (Map<String, Object> addRow : addRows) {
				String name = (String) addRow.get("name");
				int sort = (int) addRow.get("sort");

				Options options = Options.newOptions();
				options.setName(name);
				options.setSort(sort);
				PersistenceHelper.manager.save(options);

				// 신규 링크
				SpecOptionsLink link = SpecOptionsLink.newSpecOptionsLink(spec, options);
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
				String oid = (String) editRow.get("oid");
				Options optoins = (Options) CommonUtils.getObject(oid);
				optoins.setName(name);
				optoins.setSort(sort);
				PersistenceHelper.manager.modify(optoins);
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
