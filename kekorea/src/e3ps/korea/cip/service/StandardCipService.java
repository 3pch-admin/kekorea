package e3ps.korea.cip.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.cip.Cip;
import e3ps.korea.cip.dto.CipDTO;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
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
				String preViewCacheId = dto.getPreViewCacheId();
				ArrayList<String> secondarys = dto.getSecondarys();

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

				if (!StringUtils.isNull(preViewCacheId)) {
					File vault = CommonContentHelper.manager.getFileFromCacheId(preViewCacheId);
					ApplicationData applicationData = ApplicationData.newApplicationData(cip);
					applicationData.setRole(ContentRoleType.THUMBNAIL);
					PersistenceHelper.manager.save(applicationData);
					ContentServerHelper.service.updateContent(cip, applicationData, vault.getPath());
				}

				for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
					String cacheId = (String) secondarys.get(i);
					File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
					ApplicationData applicationData = ApplicationData.newApplicationData(cip);
					applicationData.setRole(ContentRoleType.SECONDARY);
					PersistenceHelper.manager.save(applicationData);
					ContentServerHelper.service.updateContent(cip, applicationData, vault.getPath());
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
				String preViewCacheId = dto.getPreViewCacheId();
				ArrayList<String> secondarys = dto.getSecondarys();

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

				if (!StringUtils.isNull(preViewCacheId)) {

					QueryResult result = ContentHelper.service.getContentsByRole(cip, ContentRoleType.PRIMARY);
					if (result.hasMoreElements()) {
						ContentItem contentItem = (ContentItem) result.nextElement();
						ContentServerHelper.service.deleteContent(cip, contentItem);
					}

					File vault = CommonContentHelper.manager.getFileFromCacheId(preViewCacheId);
					ApplicationData applicationData = ApplicationData.newApplicationData(cip);
					applicationData.setRole(ContentRoleType.THUMBNAIL);
					PersistenceHelper.manager.save(applicationData);
					ContentServerHelper.service.updateContent(cip, applicationData, vault.getPath());
				}

				for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
					QueryResult result = ContentHelper.service.getContentsByRole(cip, ContentRoleType.SECONDARY);
					while (result.hasMoreElements()) {
						ContentItem contentItem = (ContentItem) result.nextElement();
						ContentServerHelper.service.deleteContent(cip, contentItem);
					}

					String cacheId = (String) secondarys.get(i);
					File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
					ApplicationData applicationData = ApplicationData.newApplicationData(cip);
					applicationData.setRole(ContentRoleType.SECONDARY);
					PersistenceHelper.manager.save(applicationData);
					ContentServerHelper.service.updateContent(cip, applicationData, vault.getPath());
				}
			}

			trs.commit();
			trs = null;
		} catch (

		Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}
}
