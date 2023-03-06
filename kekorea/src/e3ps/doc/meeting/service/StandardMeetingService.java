package e3ps.doc.meeting.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.common.Constants;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.doc.meeting.MeetingTemplate;
import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.doc.meeting.dto.MeetingTemplateDTO;
import e3ps.project.Project;
import wt.content.ApplicationData;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.PersistenceHelper;
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
		System.out.println(dto.toString());
		String name = dto.getName();
		String content = dto.getContent();
		String template = dto.getTemplate();
		ArrayList<Map<String, String>> _addRows = dto.get_addRows();
		String[] secondarys = dto.getSecondarys();
		Transaction trs = new Transaction();
		try {
			trs.start();

			Meeting meeting = Meeting.newMeeting();
			meeting.setName(name);
			meeting.setContent(content);
			meeting.setOwnership(CommonUtils.sessionOwner());
			meeting.setState(Constants.State.INWORK);

			if (!StringUtils.isNull(template)) {
				MeetingTemplate meetingTemplate = (MeetingTemplate) CommonUtils.getObject(template);
				meeting.setTemplate(meetingTemplate);
			}

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
				MeetingProjectLink link = (MeetingProjectLink)CommonUtils.getObject(oid);
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
