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

			int key = 0;
			for (Map<String, Object> addRow : addRows) {
				String name = (String) addRow.get("name");
				int sort = (int) addRow.get("sort");
				boolean enable = (boolean) addRow.get("enable");

				Spec spec = Spec.newSpec();
				spec.setColumnKey("COLUMN_" + key);
				spec.setName(name);
				spec.setSort(sort);
				spec.setLatest(true);
				spec.setEnable(enable);
				spec.setVersion(1);
				spec.setOwnership(CommonUtils.sessionOwner());
				PersistenceHelper.manager.save(spec);
				key++;
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
				boolean enable = (boolean) editRow.get("enable");

				Spec pre = (Spec) CommonUtils.getObject(oid);
				pre.setLatest(false);
				PersistenceHelper.manager.modify(pre);

				Spec latest = Spec.newSpec();
				latest.setColumnKey(pre.getColumnKey()); // 키 값은 불변으로 한다
				latest.setName(name);
				latest.setSort(sort);
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
