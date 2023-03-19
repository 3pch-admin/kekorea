package e3ps.part.kePart.service;

import java.util.HashMap;
import java.util.List;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.dto.KeDrawingDTO;
import e3ps.part.kePart.KePart;
import e3ps.part.kePart.KePartMaster;
import e3ps.part.kePart.beans.KePartDTO;
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

public class StandardKePartService extends StandardManager implements KePartService {

	public static StandardKePartService newStandardKePartService() throws WTException {
		StandardKePartService instance = new StandardKePartService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(HashMap<String, List<KePartDTO>> dataMap) throws Exception {
		List<KePartDTO> addRows = dataMap.get("addRows");
		List<KePartDTO> editRows = dataMap.get("editRows");
		List<KePartDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = CommonUtils.sessionOwner();

			for (KePartDTO dto : addRows) {
				String keNumber = dto.getKeNumber();
				String name = dto.getName();
				int lotNo = dto.getLotNo();
				String state = dto.getState();
				String model = dto.getModel();
				String code = dto.getCode();
				String primaryPath = dto.getPrimaryPath();

				KePartMaster master = KePartMaster.newKePartMaster();
				master.setKeNumber(keNumber);
				master.setName(name);
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

				ApplicationData dd = ApplicationData.newApplicationData(kePart);
				dd.setRole(ContentRoleType.PRIMARY);
				PersistenceHelper.manager.save(dd);
				ContentServerHelper.service.updateContent(kePart, dd, primaryPath);
			}

			for (KePartDTO dto : removeRows) {
				String oid = dto.getOid();
				KePart kePart = (KePart) CommonUtils.getObject(oid);
				KePartMaster master = kePart.getMaster();
				boolean isLast = KePartHelper.manager.isLast(master);
				if (isLast) {
					PersistenceHelper.manager.delete(kePart);
					PersistenceHelper.manager.delete(master);
				} else {
					KePart pre = KePartHelper.manager.getPreKePart(kePart);
					pre.setLatest(true);
					PersistenceHelper.manager.modify(pre);
					PersistenceHelper.manager.delete(kePart);
				}
			}

			for (KePartDTO dto : editRows) {
				String oid = dto.getOid();
				String name = dto.getName();
				String keNumber = dto.getKeNumber();
				String model = dto.getModel();
				String code = dto.getCode();
				int lotNo = dto.getLotNo();
				String primaryPath = dto.getPrimaryPath();
				KePart kePart = (KePart) CommonUtils.getObject(oid);
				KePartMaster master = kePart.getMaster();
				master.setName(name);
				master.setKeNumber(keNumber);
				master.setLotNo(lotNo);
				master.setCode(code);
				master.setModel(model);
				PersistenceHelper.manager.modify(master);

				// 단순 텍스트 내용 변경건 확인이 필요..
				if (!StringUtils.isNull(primaryPath)) {
					QueryResult result = ContentHelper.service.getContentsByRole(kePart, ContentRoleType.PRIMARY);
					if (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();
						ContentServerHelper.service.deleteContent(kePart, data);
					}

					ApplicationData dd = ApplicationData.newApplicationData(kePart);
					dd.setRole(ContentRoleType.PRIMARY);
					dd = (ApplicationData) ContentServerHelper.service.updateContent(kePart, dd, primaryPath);
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
	public void revise(HashMap<String, List<KePartDTO>> dataMap) throws Exception {
		List<KePartDTO> addRows = dataMap.get("addRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (KePartDTO dto : addRows) {
				String oid = dto.getOid();
				int next = dto.getNext();
				String primaryPath = dto.getPrimaryPath();
				String note = dto.getNote();

				KePart pre = (KePart) CommonUtils.getObject(oid);

				pre.setLatest(false);
				pre = (KePart) PersistenceHelper.manager.modify(pre);

				KePart latest = KePart.newKePart();
				latest.setLatest(true);
				latest.setVersion(next);
				latest.setMaster(pre.getMaster());
				latest.setOwnership(CommonUtils.sessionOwner());
				latest.setNote(note);
				latest.setState("작업중");
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