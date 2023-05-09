package e3ps.project.issue.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.project.Project;
import e3ps.project.issue.Issue;
import e3ps.project.issue.IssueProjectLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IssueDTO {

	private String poid;
	private String oid;
	private String name;
	private String content;
	private String kekNumber;
	private String keNumber;
	private String description;
	private String projectType_code;
	private String projectType_name;
	private String projectType_oid;
	private String mak_code;
	private String mak_name;
	private String mak_oid;
	private String detail_code;
	private String detail_name;
	private String detail_oid;
	private String customer_code;
	private String customer_name;
	private String customer_oid;
	private String install_code;
	private String install_name;
	private String install_oid;
	private String creator;
	private String creatorId;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String projectType;
	private String model;
	private String userId;
	private Timestamp pdate;
	private String pdate_txt;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;

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

	public IssueDTO(Issue issue) throws Exception {
		setOid(issue.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(issue.getName());
		setContent(issue.getDescription());
		setModifiedDate(issue.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(issue.getModifyTimestamp()));
		setCreator(issue.getOwnership().getOwner().getFullName());
		setCreatedDate(issue.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(issue.getCreateTimestamp()));
	}

	public IssueDTO(IssueProjectLink link) throws Exception {
		Issue issue = link.getIssue();
		Project project = link.getProject();
		setPoid(project.getPersistInfo().getObjectIdentifier().getStringValue());
		setOid(issue.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(issue.getName());
		setContent(issue.getDescription());
		setProjectType_code(project.getProjectType().getCode());
		setProjectType_name(project.getProjectType().getName());
		setProjectType_oid(project.getProjectType().getPersistInfo().getObjectIdentifier().getStringValue());
		setMak_code(project.getMak().getCode());
		setMak_name(project.getMak().getName());
		setMak_oid(project.getMak().getPersistInfo().getObjectIdentifier().getStringValue());
		setDetail_code(project.getDetail().getCode());
		setDetail_name(project.getDetail().getName());
		setDetail_oid(project.getDetail().getPersistInfo().getObjectIdentifier().getStringValue());
		setKekNumber(project.getKekNumber());
		setKeNumber(project.getKeNumber());
		setDescription(project.getDescription());
		setCustomer_code(project.getCustomer().getCode());
		setCustomer_name(project.getCustomer().getName());
		setCustomer_oid(project.getCustomer().getPersistInfo().getObjectIdentifier().getStringValue());
		setInstall_code(project.getInstall().getCode());
		setInstall_name(project.getInstall().getName());
		setInstall_oid(project.getInstall().getPersistInfo().getObjectIdentifier().getStringValue());
		setUserId(project.getUserId());
		setModel(project.getModel());
		setPdate(project.getPDate());
		setPdate_txt(CommonUtils.getPersistableTime(project.getPDate()));
		setModifiedDate(issue.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(issue.getModifyTimestamp()));
		setCreator(issue.getOwnership().getOwner().getFullName());
		setCreatedDate(issue.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(issue.getCreateTimestamp()));
	}
}
