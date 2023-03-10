package e3ps.workspace.notice.service;

import java.util.ArrayList;
import java.util.Map;

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
	public void save(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> addRows = (ArrayList<Map<String, Object>>) params.get("addRows");
		ArrayList<Map<String, Object>> removeRows = (ArrayList<Map<String, Object>>) params.get("removeRows");
		ArrayList<Map<String, Object>> editRows = (ArrayList<Map<String, Object>>) params.get("editRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			// 일반 for 문
			for (Map<String, Object> addRow : addRows) {

			}

			// Lamda 표현식
			addRows.forEach(addRow -> {
				String oid = (String) addRow.get("oid");
			});

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
	public void create(NoticeDTO dto) throws Exception {
		String name = dto.getName();
		String description = dto.getDescription();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Notice notice = Notice.newNotice();
			notice.setName(name);
			notice.setDescription(description);
			notice.setOwnership(CommonUtils.sessionOwner());
			PersistenceHelper.manager.save(notice);

			for (String secondary : secondarys) {
				ApplicationData applicationData = ApplicationData.newApplicationData(meeting);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(notice, applicationData, secondary);
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
