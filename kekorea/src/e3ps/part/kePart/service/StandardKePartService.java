package e3ps.part.kePart.service;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import e3ps.common.Constants;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
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
	public void save(HashMap<String, List<KePartDTO>> dataMap) throws Exception {
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
				int version = dto.getVersion();
				String state = dto.getState();
				String model = dto.getModel();
				String code = dto.getCode();
				String cacheId = dto.getCacheId();

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
				kePart.setVersion(version);
				kePart.setOwnership(ownership);
				PersistenceHelper.manager.save(kePart);

				if (!StringUtils.isNull(cacheId)) {
					ApplicationData dd = ApplicationData.newApplicationData(kePart);
					File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
					dd.setRole(ContentRoleType.PRIMARY);
					PersistenceHelper.manager.save(dd);
					ContentServerHelper.service.updateContent(kePart, dd, vault.getPath());
				}
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
				String state = dto.getState();
				int lotNo = dto.getLotNo();
				String cacheId = dto.getCacheId();
				KePart kePart = (KePart) CommonUtils.getObject(oid);
				KePartMaster master = kePart.getMaster();
				kePart.setState(state);
				PersistenceHelper.manager.modify(kePart);
				master.setName(name);
				master.setKeNumber(keNumber);
				master.setLotNo(lotNo);
				master.setCode(code);
				master.setModel(model);
				PersistenceHelper.manager.modify(master);

				// 단순 텍스트 내용 변경건 확인이 필요..
				if (!StringUtils.isNull(cacheId)) {
					QueryResult result = ContentHelper.service.getContentsByRole(kePart, ContentRoleType.PRIMARY);
					if (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();
						ContentServerHelper.service.deleteContent(kePart, data);
					}

					ApplicationData dd = ApplicationData.newApplicationData(kePart);
					File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
					dd.setRole(ContentRoleType.PRIMARY);
					dd = (ApplicationData) ContentServerHelper.service.updateContent(kePart, dd, vault.getPath());
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
				String cacheId = dto.getCacheId();
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
				latest.setState(Constants.KeState.USE);
				PersistenceHelper.manager.save(latest);

				if (!StringUtils.isNull(cacheId)) {
					ApplicationData dd = ApplicationData.newApplicationData(latest);
					File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
					dd.setRole(ContentRoleType.PRIMARY);
					dd = (ApplicationData) ContentServerHelper.service.updateContent(latest, dd, vault.getPath());
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