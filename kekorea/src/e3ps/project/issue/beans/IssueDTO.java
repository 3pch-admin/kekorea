package e3ps.project.issue.beans;

import java.sql.Timestamp;

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
	private String pType;
	private Timestamp createdDate;
	private String createdDate_txt;

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
		if (project.getMak() != null) {
			setMak_name(project.getMak().getName());
		}
		if (project.getDetail() != null) {
			setDetail_name(project.getDetail().getName());
		}
		setCreator(issue.getOwnership().getOwner().getFullName());
		setCreatedDate(issue.getCreateTimestamp());
		setPType(project.getPType());
		setCreatedDate_txt(CommonUtils.getPersistableTime(issue.getCreateTimestamp()));
	}
}
