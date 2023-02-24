package e3ps.part.kePart.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.epm.jDrawing.JDrawing;
import e3ps.epm.jDrawing.service.JDrawingHelper;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.KePartMaster;
import wt.fc.PersistenceHelper;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardKePartService extends StandardManager implements KePartService {

	public static StandardKePartService newStandardKePartService() throws WTException {
		StandardKePartService instance = new StandardKePartService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<Map<String, Object>> editRows = (ArrayList<Map<String, Object>>) params.get("editRows");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = CommonUtils.sessionOwner();

			for (Map<String, Object> addRow : addRows) {
				int lotNo = (int) addRow.get("lotNo");
				String kePartName = (String) addRow.get("kePartName");
				String kePartNumber = (String) addRow.get("kePartNumber");
				String state = (String) addRow.get("state");
				String model = (String) addRow.get("model");
				String code = (String) addRow.get("code");

				KePartMaster master = KePartMaster.newKePartMaster();
				master.setKePartName(kePartName);
				master.setKePartNumber(kePartNumber);
				master.setLotNo(lotNo);
				master.setModel(model);
				master.setCode(code);
				master.setOwnership(ownership);
				PersistenceHelper.manager.save(master);

				KePart kePart = KePart.newKePart();
				kePart.setMaster(master);
				kePart.setState(state);
				kePart.setLatest(true);
				kePart.setVersion(1);
				kePart.setOwnership(ownership);
				PersistenceHelper.manager.save(kePart);
			}

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				KePart kePart = (KePart) CommonUtils.getObject(oid);
				boolean isLast = KePartHelper.manager.isLast(kePart.getMaster());
				if (isLast) {
					PersistenceHelper.manager.delete(kePart.getMaster());
					PersistenceHelper.manager.delete(kePart);
				} else {
					// 이전 버전을 최신 버전으로 만드는 작업..
					KePart prePart = KePartHelper.manager.getPreKePart(kePart);
					prePart.setLatest(true);
					PersistenceHelper.manager.modify(prePart);
					PersistenceHelper.manager.delete(kePart);
				}
			}

			for (Map<String, Object> editRow : editRows) {

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
