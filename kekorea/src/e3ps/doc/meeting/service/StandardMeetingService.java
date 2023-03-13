package e3ps.doc.meeting.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.doc.meeting.MeetingTemplate;
import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.doc.meeting.dto.MeetingTemplateDTO;
import e3ps.project.Project;
import wt.clients.folder.FolderTaskLogic;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.DocumentType;
import wt.fc.PersistenceHelper;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.pom.Transaction;
import wt.services.StandardManager;
import wt.util.WTException;

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
		ArrayList<Map<String, String>> _addRows = dto.get_addRows();
		ArrayList<String> secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Meeting meeting = Meeting.newMeeting();
			meeting.setNumber(MeetingHelper.manager.getNextNumber());
			meeting.setName(name);
			meeting.setDescription(content);

			if (!StringUtils.isNull(tiny)) {
				MeetingTemplate meetingTemplate = (MeetingTemplate) CommonUtils.getObject(tiny);
				meeting.setTiny(meetingTemplate);
			}

			meeting.setDocType(DocumentType.toDocumentType("$$Meeting"));

			Folder folder = FolderTaskLogic.getFolder(MeetingHelper.LOCATION, CommonUtils.getContainer());
			FolderHelper.assignLocation((FolderEntry) meeting, folder);

			PersistenceHelper.manager.save(meeting);

			for (String secondary : secondarys) {
				ApplicationData applicationData = ApplicationData.newApplicationData(meeting);
				applicationData.setRole(ContentRoleType.SECONDARY);
				PersistenceHelper.manager.save(applicationData);
				ContentServerHelper.service.updateContent(meeting, applicationData, secondary);
			}

			for (Map<String, String> _addRow : _addRows) {
				String oid = _addRow.get("oid");
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
}
