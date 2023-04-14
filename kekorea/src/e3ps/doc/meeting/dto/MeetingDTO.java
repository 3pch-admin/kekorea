package e3ps.doc.meeting.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.doc.meeting.MeetingTemplate;
import e3ps.project.Project;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MeetingDTO {

	private String oid;
	private String poid;
	private String loid;
	private String name;
	private String t_name;
	private String number;
	private String projectType_name;
	private String content;
	private String customer_name;
	private String install_name;
	private String mak_name;
	private String detail_name;
	private String kekNumber;
	private String keNumber;
	private String userId;
	private String description;
	private String model;
	private Timestamp pdate;
	private String pdate_txt;
	private String state;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;
	
	private String t_name;
	private String toid;

	// 변수 담기 용도
	private ArrayList<Map<String, String>> _addRows = new ArrayList<>();
	private ArrayList<String> secondarys = new ArrayList<>();
	private String tiny;

	public MeetingDTO() {

	}

	public MeetingDTO(MeetingProjectLink link) throws Exception {
		Meeting meeting = link.getMeeting();
		Project project = link.getProject();
		setOid(meeting.getPersistInfo().getObjectIdentifier().getStringValue());
		setPoid(project.getPersistInfo().getObjectIdentifier().getStringValue());
		setLoid(link.getPersistInfo().getObjectIdentifier().getStringValue());
		if (project.getProjectType() != null) {
			setProjectType_name(project.getProjectType().getName());
		}
		setName(meeting.getName());
		setNumber(meeting.getNumber());
		setContent(meeting.getContent());

		if (project.getCustomer() != null) {
			setCustomer_name(project.getCustomer().getName());
		}

		if (project.getInstall() != null) {
			setInstall_name(project.getInstall().getName());
		}

		if (project.getMak() != null) {
			setMak_name(project.getMak().getName());
		}

		if (project.getDetail() != null) {
			setDetail_name(project.getDetail().getName());
		}
		setKekNumber(project.getKekNumber());
		setKeNumber(project.getKeNumber());
		setUserId(project.getUserId());
		setDescription(project.getDescription());
		setModel(project.getModel());
		if (project.getPDate() != null) {
			setPdate(project.getPDate());
			setPdate_txt(CommonUtils.getPersistableTime(project.getPDate()));
		}
		setState(meeting.getLifeCycleState().getDisplay());
		setCreator(meeting.getOwnership().getOwner().getFullName());
		setCreatedDate(meeting.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(meeting.getCreateTimestamp()));
	}


}