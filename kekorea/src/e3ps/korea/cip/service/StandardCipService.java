package e3ps.korea.cip.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.cip.Cip;
import e3ps.korea.cip.dto.CipDTO;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;

public class StandardCipService extends StandardManager implements CipService {

	public static StandardCipService newStandardCipService() throws Exception {
		StandardCipService instance = new StandardCipService();
		return instance;
	}

	@Override
	public void save(HashMap<String, List<CipDTO>> dataMap) throws Exception {
		List<CipDTO> addRows = dataMap.get("addRows");
		List<CipDTO> editRows = dataMap.get("editRows");
		List<CipDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (CipDTO dto : addRows) {
				String item = dto.getItem();
				String improvements = dto.getImprovements();
				String improvement = dto.getImprovement();
				String apply = dto.getApply();
				String note = dto.getNote();
				String mak = dto.getMak_code();
				String detail = dto.getDetail_code();
				String customer = dto.getCustomer_code();
				String install = dto.getInstall_code();
				String preViewPath = dto.getPreViewPath();
				ArrayList<String> secondaryPaths = dto.getSecondaryPaths();

				Cip cip = Cip.newCip();
				cip.setOwnership(CommonUtils.sessionOwner());
				cip.setItem(item);
				cip.setImprovement(improvement);
				cip.setImprovements(improvements);
				cip.setApply(apply);
				cip.setNote(note);
				cip.setMak(CommonCodeHelper.manager.getCommonCode(mak, "MAK"));
				cip.setDetail(CommonCodeHelper.manager.getCommonCode(detail, "MAK_DETAIL"));
				cip.setCustomer(CommonCodeHelper.manager.getCommonCode(customer, "CUSTOMER"));
				cip.setInstall(CommonCodeHelper.manager.getCommonCode(install, "INSTALL"));
				PersistenceHelper.manager.save(cip);

				if (!StringUtils.isNull(preViewPath)) {
					ContentUtils.savePrimary(cip, preViewPath);
				}

				if (secondaryPaths.size() > 0) {
					ContentUtils.saveSecondary(cip, secondaryPaths);
				}

			}

			for (CipDTO dto : removeRows) {
				String oid = dto.getOid();
				Cip cip = (Cip) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(cip);
			}

			for (CipDTO dto : editRows) {
				String item = dto.getItem();
				String improvements = dto.getImprovements();
				String improvement = dto.getImprovement();
				String apply = dto.getApply();
				String note = dto.getNote();
				String mak = dto.getMak_code();
				String detail = dto.getDetail_code();
				String customer = dto.getCustomer_code();
				String install = dto.getInstall_code();
				String oid = dto.getOid();
				String preViewPath = dto.getPreViewPath();
				ArrayList<String> secondaryPaths = dto.getSecondaryPaths();

				Cip cip = (Cip) CommonUtils.getObject(oid);
				cip.setItem(item);
				cip.setImprovement(improvement);
				cip.setImprovements(improvements);
				cip.setApply(apply);
				cip.setNote(note);
				cip.setMak(CommonCodeHelper.manager.getCommonCode(mak, "MAK"));
				cip.setDetail(CommonCodeHelper.manager.getCommonCode(detail, "MAK_DETAIL"));
				cip.setCustomer(CommonCodeHelper.manager.getCommonCode(customer, "CUSTOMER"));
				cip.setInstall(CommonCodeHelper.manager.getCommonCode(install, "INSTALL"));
				PersistenceHelper.manager.modify(cip);

				if (!StringUtils.isNull(preViewPath)) {
					ContentUtils.savePrimary(cip, preViewPath);
				}

				// 기존꺼 삭제하고 만드는거가...??
				if (secondaryPaths.size() > 0) {
					ContentUtils.saveSecondary(cip, secondaryPaths);
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
