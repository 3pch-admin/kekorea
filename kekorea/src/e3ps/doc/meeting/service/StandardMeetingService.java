package e3ps.doc.meeting.service;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.doc.meeting.MeetingTemplate;
import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.doc.meeting.dto.MeetingTemplateDTO;
import e3ps.project.Project;
import wt.clients.folder.FolderTaskLogic;
import wt.clients.vc.CheckInOutTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.doc.WTDocumentMaster;
import wt.doc.WTDocumentMasterIdentity;
import wt.fc.IdentityHelper;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.org.WTUser;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.vc.wip.CheckoutLink;
import wt.vc.wip.WorkInProgressHelper;

public class StandardMeetingService extends StandardManager implements MeetingService {

	public static StandardMeetingService newStandardMeetingService() throws WTException {
		StandardMeetingService instance = new StandardMeetingService();
		instance.initialize();
		return instance;
	}

	@Override
	public void format(MeetingTemplateDTO dto) throws Exception {
		String name = dto.getName();
		String content = dto.getContent();
		Transaction trs = new Transaction();
		try {
			trs.start();

			MeetingTemplate meetingTemplate = MeetingTemplate.newMeetingTemplate();
			meetingTemplate.setName(name);
			meetingTemplate.setContent(content);
			meetingTemplate.setOwnership(CommonUtils.sessionOwner());
			meetingTemplate.setEnable(true);
			PersistenceHelper.manager.save(meetingTemplate);

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
	public void create(MeetingDTO dto) throws Exception {
		String name = dto.getName();
		String content = dto.getContent();
		String tiny = dto.getTiny();
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Meeting meeting = Meeting.newMeeting();
			meeting.setNumber(MeetingHelper.manager.getNextNumber());
			meeting.setName(name);
			meeting.setOwnership(CommonUtils.sessionOwner());
			meeting.setContent(content);

			if (!StringUtils.isNull(tiny)) {
				MeetingTemplate meetingTemplate = (MeetingTemplate) CommonUtils.getObject(tiny);
				meeting.setTiny(meetingTemplate);
			}

			meeting.setDocType(DocumentType.toDocumentType("$$Meeting"));

			Folder folder = FolderTaskLogic.getFolder(MeetingHelper.LOCATION, CommonUtils.getPDMLinkProductContainer());
			FolderHelper.assignLocation((FolderEntry) meeting, folder);

			PersistenceHelper.manager.save(meeting);

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(meeting);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(meeting, applicationData, vault.getPath());
			}

			for (Map<String, String> addRow9 : addRows9) {
				String oid = addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(oid);
				MeetingProjectLink link = MeetingProjectLink.newMeetingProjectLink(meeting, project);
				PersistenceHelper.manager.save(link);
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
	public void save(HashMap<String, List<MeetingTemplateDTO>> dataMap) throws Exception {
		List<MeetingTemplateDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (MeetingTemplateDTO remove : removeRows) {
				String oid = remove.getOid();
				MeetingTemplate meetingTemplate = (MeetingTemplate) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(meetingTemplate);
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
	public void delete(HashMap<String, List<MeetingDTO>> dataMap) throws Exception {
		List<MeetingDTO> removeRows = dataMap.get("removeRows");
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (MeetingDTO remove : removeRows) {
				String oid = remove.getLoid();
				MeetingProjectLink link = (MeetingProjectLink) CommonUtils.getObject(oid);
				PersistenceHelper.manager.delete(link);
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
	public void modify(MeetingTemplateDTO dto) throws Exception {
		String name = dto.getName();
		String content = dto.getContent();
		String oid = dto.getOid();
		Transaction trs = new Transaction();
		try {
			trs.start();

			MeetingTemplate meetingTemplate = (MeetingTemplate) CommonUtils.getObject(oid);
			meetingTemplate.setName(name);
			meetingTemplate.setContent(content);
			meetingTemplate.setOwnership(CommonUtils.sessionOwner());
			meetingTemplate.setEnable(true);
			PersistenceHelper.manager.modify(meetingTemplate);

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
	public void update(MeetingDTO dto) throws Exception {
		String oid = dto.getOid();
		String name = dto.getName();
		String content = dto.getContent();
		String tiny = dto.getTiny();
		ArrayList<Map<String, String>> addRows9 = dto.getAddRows9();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Meeting meeting = (Meeting) CommonUtils.getObject(oid);

			// 기존 연결 제거한다..
			QueryResult result = PersistenceHelper.manager.navigate(meeting, "project", MeetingProjectLink.class,
					false);
			while (result.hasMoreElements()) {
				MeetingProjectLink link = (MeetingProjectLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			Folder cFolder = CheckInOutTaskLogic.getCheckoutFolder();
			CheckoutLink clink = WorkInProgressHelper.service.checkout(meeting, cFolder, "회의록 수정 체크 아웃");
			Meeting newMeeting = (Meeting) clink.getWorkingCopy();
			WTDocumentMaster master = (WTDocumentMaster) newMeeting.getMaster();
			WTDocumentMasterIdentity identity = (WTDocumentMasterIdentity) master.getIdentificationObject();
			identity.setName(name);
			master = (WTDocumentMaster) IdentityHelper.service.changeIdentity(master, identity);
			newMeeting.setContent(content);

			if (!StringUtils.isNull(tiny)) {
				MeetingTemplate meetingTemplate = (MeetingTemplate) CommonUtils.getObject(tiny);
				newMeeting.setTiny(meetingTemplate);
			}

			CommonContentHelper.manager.clear(newMeeting);

			for (int i = 0; secondarys != null && i < secondarys.size(); i++) {
				String cacheId = (String) secondarys.get(i);
				File vault = CommonContentHelper.manager.getFileFromCacheId(cacheId);
				ApplicationData applicationData = ApplicationData.newApplicationData(newMeeting);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(newMeeting, applicationData, vault.getPath());
			}

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String msg = user.getFullName() + " 사용자가 문서를 수정 하였습니다.";
			// 필요하면 수정 사유로 대체
			newMeeting = (Meeting) WorkInProgressHelper.service.checkin(newMeeting, msg);

			for (Map<String, String> addRow9 : addRows9) {
				String poid = addRow9.get("oid");
				Project project = (Project) CommonUtils.getObject(poid);
				MeetingProjectLink link = MeetingProjectLink.newMeetingProjectLink(newMeeting, project);
				PersistenceHelper.manager.modify(link);
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
