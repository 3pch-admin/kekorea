package e3ps.project.issue.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.CommonUtils;
import e3ps.project.Project;
import e3ps.project.issue.Issue;
import e3ps.project.issue.IssueProjectLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueDTO {

	private String oid;
	private String name;
	private String content;
	private String kekNumber;
	private String keNumber;
	private String description;
	private String mak_name;
	private String detail_name;
	private String creator;
	private String creatorId;
	private String projectType;
	private Timestamp createdDate;
	private String createdDate_txt;

	/**
	 * 작번 변수
	 */
	private ArrayList<Map<String, String>> addRows9 = new ArrayList<>();

	/**
	 * 첨부파일 변수
	 */
	private ArrayList<String> secondarys = new ArrayList<>();

	public IssueDTO() {

	}

	public IssueDTO(IssueProjectLink link) throws Exception {
		Issue issue = link.getIssue();
		Project project = link.getProject();
		setOid(link.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(issue.getName());
		setContent(issue.getDescription());
		setKekNumber(project.getKekNumber());
		setKeNumber(project.getKeNumber());
		setDescription(project.getDescription());
		setMak_name(project.getMak().getName());
		setDetail_name(project.getDetail().getName());
		setCreator(issue.getOwnership().getOwner().getFullName());
		setCreatorId(issue.getOwnership().getOwner().getName());
		setCreatedDate(issue.getCreateTimestamp());
		setProjectType(project.getProjectType().getName());
		setCreatedDate_txt(CommonUtils.getPersistableTime(issue.getCreateTimestamp()));
	}
}
