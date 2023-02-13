package e3ps.epm.jDrawing.service;

import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.epm.jDrawing.JDrawing;
import e3ps.epm.jDrawing.JDrawingMaster;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;

public class StandardJDrawingService extends StandardManager implements JDrawingService {

	public static StandardJDrawingService newStandardJDrawingService() throws WTException {
		StandardJDrawingService instance = new StandardJDrawingService();
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

				JDrawingMaster master = JDrawingMaster.newJDrawingMaster();
				master.setNumber(number);
				master.setName(name);
				master.setOwnership(CommonUtils.sessionOwner());
				master = (JDrawingMaster) PersistenceHelper.manager.save(master);

				JDrawing jDrawing = JDrawing.newJDrawing();
				jDrawing.setLot(lot);
				jDrawing.setOwnership(CommonUtils.sessionOwner());
				jDrawing.setVersion(version);
				jDrawing.setMaster(master);
				jDrawing.setLatest(true);
				PersistenceHelper.manager.save(jDrawing);

				ApplicationData dd = ApplicationData.newApplicationData(jDrawing);
				dd.setRole(ContentRoleType.PRIMARY);
				dd = (ApplicationData) ContentServerHelper.service.updateContent(jDrawing, dd, primaryPath);
			}

			for (Map<String, Object> removeRow : removeRows) {
				String oid = (String) removeRow.get("oid");
				JDrawing jDrawing = (JDrawing) CommonUtils.getObject(oid);
				boolean isLast = JDrawingHelper.manager.isLast(jDrawing.getMaster());
				if (isLast) {
					PersistenceHelper.manager.delete(jDrawing.getMaster());
					PersistenceHelper.manager.delete(jDrawing);
				} else {
					// 이전 버전을 최신 버전으로 만드는 작업..
				}
			}

			for (Map<String, Object> editRow : editRows) {
				String oid = (String) editRow.get("oid");
				JDrawing jDrawing = (JDrawing) CommonUtils.getObject(oid);

				PersistenceHelper.manager.modify(jDrawing);
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
