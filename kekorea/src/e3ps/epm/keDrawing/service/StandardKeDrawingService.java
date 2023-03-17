package e3ps.epm.keDrawing.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.KeDrawingMaster;
import e3ps.epm.keDrawing.dto.KeDrawingDTO;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
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
	public void create(HashMap<String, List<KeDrawingDTO>> dataMap) throws Exception {
		List<KeDrawingDTO> addRows = dataMap.get("addRows");
		List<KeDrawingDTO> removeRows = dataMap.get("removeRows");
		List<KeDrawingDTO> editRows = dataMap.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (KeDrawingDTO dto : addRows) {
				String name = dto.getName();
				String keNumber = dto.getKeNumber();
				int version = dto.getVersion();
				int lotNo = dto.getLotNo();
				String primaryPath = dto.getPrimaryPath();

				KeDrawingMaster master = KeDrawingMaster.newKeDrawingMaster();
				master.setKeNumber(keNumber);
				master.setName(name);
				master.setLotNo(lotNo);
				master.setOwnership(CommonUtils.sessionOwner());
				master = (KeDrawingMaster) PersistenceHelper.manager.save(master);

				KeDrawing keDrawing = KeDrawing.newKeDrawing();
				keDrawing.setOwnership(CommonUtils.sessionOwner());
				keDrawing.setVersion(version);
				keDrawing.setMaster(master);
				keDrawing.setLatest(true);
				PersistenceHelper.manager.save(keDrawing);

				ApplicationData dd = ApplicationData.newApplicationData(keDrawing);
				dd.setRole(ContentRoleType.PRIMARY);
				PersistenceHelper.manager.save(dd);
				ContentServerHelper.service.updateContent(keDrawing, dd, primaryPath);
			}

			for (KeDrawingDTO dto : removeRows) {
				String oid = dto.getOid();
				KeDrawing keDrawing = (KeDrawing) CommonUtils.getObject(oid);
				boolean isLast = KeDrawingHelper.manager.isLast(keDrawing.getMaster());
				if (isLast) {
					PersistenceHelper.manager.delete(keDrawing.getMaster());
				} else {
					KeDrawing pre = KeDrawingHelper.manager.getPreKeDrawing(keDrawing);
					pre.setLatest(true);
					PersistenceHelper.manager.modify(pre);
				}
				PersistenceHelper.manager.delete(keDrawing);
			}

			for (KeDrawingDTO dto : editRows) {
				String oid = dto.getOid();
				String name = dto.getName();
				int lotNo = dto.getLotNo();
				String primaryPath = dto.getPrimaryPath();
				KeDrawing keDrawing = (KeDrawing) CommonUtils.getObject(oid);
				KeDrawingMaster master = keDrawing.getMaster();
				master.setName(name);
				master.setLotNo(lotNo);
				PersistenceHelper.manager.modify(master);

				QueryResult result = ContentHelper.service.getContentsByRole(keDrawing, ContentRoleType.PRIMARY);
				if (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();
					ContentServerHelper.service.deleteContent(keDrawing, data);
				}

				ApplicationData dd = ApplicationData.newApplicationData(keDrawing);
				dd.setRole(ContentRoleType.PRIMARY);
				dd = (ApplicationData) ContentServerHelper.service.updateContent(keDrawing, dd, primaryPath);
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

	@Override
	public void revise(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (Map<String, Object> addRow : addRows) {
				String oid = (String) addRow.get("oid");
				int next = (int) addRow.get("next");
				String primaryPath = (String) addRow.get("primaryPath");
				String note = (String) addRow.get("note");

				KeDrawing pre = (KeDrawing) CommonUtils.getObject(oid);
				pre.setLatest(false);
				pre = (KeDrawing) PersistenceHelper.manager.modify(pre);

				KeDrawing latest = KeDrawing.newKeDrawing();
				latest.setLatest(true);
				latest.setVersion(next);
				latest.setMaster(pre.getMaster());
				latest.setOwnership(CommonUtils.sessionOwner());
				PersistenceHelper.manager.save(latest);

				ApplicationData dd = ApplicationData.newApplicationData(latest);
				dd.setRole(ContentRoleType.PRIMARY);
				dd = (ApplicationData) ContentServerHelper.service.updateContent(latest, dd, primaryPath);
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
