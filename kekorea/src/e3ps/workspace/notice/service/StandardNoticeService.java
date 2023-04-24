package e3ps.workspace.notice.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.workspace.notice.Notice;
import e3ps.workspace.notice.dto.NoticeDTO;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardNoticeService extends StandardManager implements NoticeService {

	public static StandardNoticeService newStandardNoticeService() throws WTException {
		StandardNoticeService instance = new StandardNoticeService();
		instance.initialize();
		return instance;
	}

	@Override
	public void create(NoticeDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		ArrayList<String> primarys = dto.getPrimarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Notice notice = Notice.newNotice();
			notice.setName(name);
			notice.setDescription(description);
			notice.setOwnership(CommonUtils.sessionOwner());
			PersistenceHelper.manager.save(notice);

			for (int i = 0; i < primarys.size(); i++) {
				String cacheId = (String) primarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(notice);
				if (i == 0) {
					applicationData.setRole(ContentRoleType.PRIMARY);
				} else {
					applicationData.setRole(ContentRoleType.SECONDARY);
				}
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(notice, applicationData, vault.getPath());
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			CommonContentHelper.manager.clean();
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void delete(HashMap<String, List<NoticeDTO>> dataMap) throws Exception {
		List<NoticeDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (NoticeDTO dto : removeRows) {
				String oid = dto.getOid();
				Notice notice = (Notice) CommonUtils.getObject(oid);

				// 첨부 파일 삭제
				CommonContentHelper.manager.clear(notice);

				PersistenceHelper.manager.delete(notice);
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
	public void modify(NoticeDTO dto) throws Exception {
		String name = dto.getName();
		String oid = dto.getOid();
		String description = dto.getDescription();
		ArrayList<String> primarys = dto.getPrimarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Notice notice = (Notice) CommonUtils.getObject(oid);
			notice.setName(name);
			notice.setDescription(description);

			CommonContentHelper.manager.clear(notice);

			for (int i = 0; i < primarys.size(); i++) {
				String cacheId = (String) primarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(notice);
				if (i == 0) {
					applicationData.setRole(ContentRoleType.PRIMARY);
				} else {
					applicationData.setRole(ContentRoleType.SECONDARY);
				}
				PersistenceHelper.manager.save(applicationData);

				ContentServerHelper.service.updateContent(notice, applicationData, vault.getPath());
			}

			PersistenceHelper.manager.modify(notice);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
			throw e;
		} finally {
			CommonContentHelper.manager.clean();
			if (trs != null)
				trs.rollback();
		}
	}
}
