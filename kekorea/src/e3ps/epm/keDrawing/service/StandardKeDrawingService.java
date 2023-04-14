package e3ps.epm.keDrawing.service;

import java.util.HashMap;
import java.util.List;

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
import wt.ownership.Ownership;
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

			Ownership ownership = CommonUtils.sessionOwner();

			for (KeDrawingDTO dto : addRows) {
				String name = dto.getName();
				String keNumber = dto.getKeNumber();
				String state = dto.getState();
				int version = dto.getVersion();
				int lotNo = dto.getLotNo();
				String primaryPath = dto.getPrimaryPath();

				KeDrawingMaster master = KeDrawingMaster.newKeDrawingMaster();
				master.setKeNumber(keNumber);
				master.setName(name);
				master.setLotNo(lotNo);
				master.setOwnership(ownership);
				master = (KeDrawingMaster) PersistenceHelper.manager.save(master);

				KeDrawing keDrawing = KeDrawing.newKeDrawing();
				keDrawing.setOwnership(ownership);
				keDrawing.setVersion(version);
				keDrawing.setMaster(master);
				keDrawing.setLatest(true);
				keDrawing.setState(state);
				PersistenceHelper.manager.save(keDrawing);

				ApplicationData dd = ApplicationData.newApplicationData(keDrawing);
				dd.setRole(ContentRoleType.PRIMARY);
				PersistenceHelper.manager.save(dd);
				ContentServerHelper.service.updateContent(keDrawing, dd, primaryPath);

				KeDrawingHelper.manager.postAfterAction(
						keDrawing.getPersistInfo().getObjectIdentifier().getStringValue(), primaryPath);
			}

			for (KeDrawingDTO dto : removeRows) {
				String oid = dto.getOid();
				KeDrawing keDrawing = (KeDrawing) CommonUtils.getObject(oid);
				KeDrawingMaster master = keDrawing.getMaster();
				boolean isLast = KeDrawingHelper.manager.isLast(master);
				if (isLast) {
					PersistenceHelper.manager.delete(keDrawing);
					PersistenceHelper.manager.delete(master);
				} else {
					KeDrawing pre = KeDrawingHelper.manager.getPreKeDrawing(keDrawing);
					pre.setLatest(true);
					PersistenceHelper.manager.modify(pre);
					PersistenceHelper.manager.delete(keDrawing);
				}
			}

			for (KeDrawingDTO dto : editRows) {
				String oid = dto.getOid();
				String name = dto.getName();
				String state = dto.getState();
				int lotNo = dto.getLotNo();
				String primaryPath = dto.getPrimaryPath();
				KeDrawing keDrawing = (KeDrawing) CommonUtils.getObject(oid);
				KeDrawingMaster master = keDrawing.getMaster();
				keDrawing.setState(state);
				keDrawing = (KeDrawing) PersistenceHelper.manager.modify(keDrawing);
				master.setName(name);
				master.setLotNo(lotNo);
				PersistenceHelper.manager.modify(master);

				// 단순 텍스트 내용 변경건 확인이 필요..
				if (!StringUtils.isNull(primaryPath)) {
					QueryResult result = ContentHelper.service.getContentsByRole(keDrawing, ContentRoleType.PRIMARY);
					if (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();
						ContentServerHelper.service.deleteContent(keDrawing, data);
					}

					ApplicationData dd = ApplicationData.newApplicationData(keDrawing);
					dd.setRole(ContentRoleType.PRIMARY);
					dd = (ApplicationData) ContentServerHelper.service.updateContent(keDrawing, dd, primaryPath);

					KeDrawingHelper.manager.postAfterAction(
							keDrawing.getPersistInfo().getObjectIdentifier().getStringValue(), primaryPath);
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

	@Override
	public void revise(HashMap<String, List<KeDrawingDTO>> dataMap) throws Exception {
		List<KeDrawingDTO> addRows = dataMap.get("addRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (KeDrawingDTO dto : addRows) {
				String oid = dto.getOid();
				int next = dto.getNext();
				String primaryPath = dto.getPrimaryPath();
				String note = dto.getNote();

				KeDrawing pre = (KeDrawing) CommonUtils.getObject(oid);

				pre.setLatest(false);
				pre = (KeDrawing) PersistenceHelper.manager.modify(pre);

				KeDrawing latest = KeDrawing.newKeDrawing();
				latest.setLatest(true);
				latest.setVersion(next);
				latest.setMaster(pre.getMaster());
				latest.setOwnership(CommonUtils.sessionOwner());
				latest.setNote(note);
				PersistenceHelper.manager.save(latest);

				ApplicationData dd = ApplicationData.newApplicationData(latest);
				dd.setRole(ContentRoleType.PRIMARY);
				dd = (ApplicationData) ContentServerHelper.service.updateContent(latest, dd, primaryPath);

				KeDrawingHelper.manager.postAfterAction(latest.getPersistInfo().getObjectIdentifier().getStringValue(),
						primaryPath);
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