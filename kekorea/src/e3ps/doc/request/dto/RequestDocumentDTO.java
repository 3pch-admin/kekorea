package e3ps.doc.request.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import e3ps.common.util.CommonUtils;
import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.project.Project;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDocumentDTO {

	private String oid;
	private String projectType_name;
	private String name;
	private String customer_name;
	private String install_name;
	private String mak_name;
	private String detail_name;
	private String kekNumber;
	private String keNumber;
	private String userId;
	private String description;
	private String version;
	private String state;
	private String model;
	private String docType;
	private Timestamp pdate;
	private String pdate_txt;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String modifier;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;

	// 변수 담기 용도
	private ArrayList<Map<String, String>> addRows = new ArrayList<>();
	private ArrayList<Map<String, String>> agreeRows = new ArrayList<>(); // 검토
	private ArrayList<Map<String, String>> approvalRows = new ArrayList<>(); // 결재
	private ArrayList<Map<String, String>> receiveRows = new ArrayList<>(); // 수신
	private ArrayList<String> primarys = new ArrayList<>();
	private String template;
	private String poid;
	private String toid;
	private boolean connect;

	public RequestDocumentDTO() {

	}

	public RequestDocumentDTO(RequestDocument request) throws Exception {
		setOid(request.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(request.getName());
//		setVersion(CommonUtils.getFullVersion(request));
		setState(request.getLifeCycleState().getDisplay());
		setCreator(request.getCreatorFullName());
		setCreatedDate(request.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(request.getCreateTimestamp()));
		setModifier(request.getModifierFullName());
		setModifiedDate(request.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(request.getModifyTimestamp()));
		setDocType(request.getDocType().getDisplay());
	}

	public RequestDocumentDTO(RequestDocumentProjectLink link) throws Exception {
		RequestDocument request = link.getRequestDocument();
		Project project = link.getProject();
		setOid(request.getPersistInfo().getObjectIdentifier().getStringValue());

		if (project.getProjectType() != null) {
			setProjectType_name(project.getProjectType().getName());
		}

		setName(request.getName());

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
//		setVersion(CommonUtils.getFullVersion(request));
		setState(request.getLifeCycleState().getDisplay());
		setModel(project.getModel());
		if (project.getPDate() != null) {
			setPdate(project.getPDate());
			setPdate_txt(CommonUtils.getPersistableTime(project.getPDate()));
		}
		setCreator(request.getCreatorFullName());
		setCreatedDate(request.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(request.getCreateTimestamp()));
		setModifier(request.getModifierFullName());
		setModifiedDate(request.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(request.getModifyTimestamp()));
		setDocType(request.getDocType().getDisplay());
	}
}