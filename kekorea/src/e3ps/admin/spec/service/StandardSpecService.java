package e3ps.admin.spec.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.spec.Spec;
import e3ps.common.util.CommonUtils;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardSpecService extends StandardManager implements SpecService {

	public static StandardSpecService newStandardSpecService() throws WTException {
		StandardSpecService instance = new StandardSpecService();
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
				String name = (String) addRow.get("name");
				int sort = (int) addRow.get("sort");
				boolean config = (boolean) addRow.get("config");
				boolean history = (boolean) addRow.get("history");
				boolean enable = (boolean) addRow.get("enable");

				Spec spec = Spec.newSpec();
				spec.setColKey("spec_" + sort);
				spec.setName(name);
				spec.setSort(sort);
				spec.setLatest(true);
				spec.setHistroy(history);
				spec.setConfig(config);
				spec.setEnable(enable);
				spec.setVersion(1);
				PersistenceHelper.manager.save(spec);
			}

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				Spec spec = (Spec) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(spec);
			}

			for (Map<String, Object> editRow : editRows) {
				String oid = (String) editRow.get("oid");
				String name = (String) editRow.get("name");
				int sort = (int) editRow.get("sort");
				boolean history = (boolean) editRow.get("history");
				boolean config = (boolean) editRow.get("config");
				boolean enable = (boolean) editRow.get("enable");

				Spec pre = (Spec) CommonUtils.getObject(oid);
				pre.setLatest(false);
				PersistenceHelper.manager.modify(pre);

				Spec latest = Spec.newSpec();
				latest.setColKey("spec_" + sort);
				latest.setName(name);
				latest.setSort(sort);
				latest.setHistroy(history);
				latest.setConfig(config);
				latest.setEnable(enable);
				latest.setLatest(true);
				latest.setVersion(pre.getVersion() + 1);
				PersistenceHelper.manager.save(latest);
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
