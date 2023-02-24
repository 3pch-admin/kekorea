package e3ps.epm.KeDrawing.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.epm.KeDrawing.KeDrawing;
import e3ps.epm.KeDrawing.KeDrawingMaster;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardKeDrawingService extends StandardManager implements KeDrawingService {

	public static StandardKeDrawingService newStandardKeDrawingService() throws WTException {
		StandardKeDrawingService instance = new StandardKeDrawingService();
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
				String number = (String) addRow.get("number");
				int version = (int) addRow.get("version");
				String lot = (String) addRow.get("lot");
				String primaryPath = (String) addRow.get("primaryPath");

				KeDrawingMaster master = KeDrawingMaster.newKeDrawingMaster();
				master.setKeNumber(number);
				master.setName(name);
				master.setOwnership(CommonUtils.sessionOwner());
				master = (KeDrawingMaster) PersistenceHelper.manager.save(master);

				KeDrawing keDrawing = KeDrawing.newKeDrawing();
				keDrawing.setLot(lot);
				keDrawing.setOwnership(CommonUtils.sessionOwner());
				keDrawing.setVersion(version);
				keDrawing.setMaster(master);
				keDrawing.setLatest(true);
				PersistenceHelper.manager.save(keDrawing);

				ApplicationData dd = ApplicationData.newApplicationData(keDrawing);
				dd.setRole(ContentRoleType.PRIMARY);
				dd = (ApplicationData) ContentServerHelper.service.updateContent(keDrawing, dd, primaryPath);
			}

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				KeDrawing keDrawing = (KeDrawing) CommonUtils.getObject(oid);
				boolean isLast = KeDrawingHelper.manager.isLast(keDrawing.getMaster());
				if (isLast) {
					PersistenceHelper.manager.delete(keDrawing.getMaster());
					PersistenceHelper.manager.delete(keDrawing);
				} else {
					// 이전 버전을 최신 버전으로 만드는 작업..
				}
			}

			for (Map<String, Object> editRow : editRows) {
				String oid = (String) editRow.get("oid");
				KeDrawing keDrawing = (KeDrawing) CommonUtils.getObject(oid);
				PersistenceHelper.manager.modify(keDrawing);
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
