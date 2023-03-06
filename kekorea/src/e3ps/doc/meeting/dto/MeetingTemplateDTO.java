package e3ps.doc.meeting.dto;

import java.sql.Timestamp;

import e3ps.doc.meeting.MeetingTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetingTemplateDTO {

	private String oid;
	private String name;
	private String content;
	private String creator;
	private Timestamp createdDate;

	public MeetingTemplateDTO() {

	}

	public MeetingTemplateDTO(MeetingTemplate meetingTemplate) throws Exception {
		setOid(meetingTemplate.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(meetingTemplate.getName());
		setContent(meetingTemplate.getContent());
		setCreator(meetingTemplate.getOwnership().getOwner().getFullName());
		setCreatedDate(meetingTemplate.getCreateTimestamp());
	}
}
