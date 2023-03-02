package e3ps.doc.request.beans;

import java.sql.Timestamp;

import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.project.Project;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestDocumentColumnData {

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
	private Timestamp pdate;
	private String creator;
	private Timestamp createdDate;
	private String modifier;
	private Timestamp modifiedDate;

	public RequestDocumentColumnData() {

	}

	public RequestDocumentColumnData(RequestDocumentProjectLink link) throws Exception {
		RequestDocument request = link.getRequest();
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
		setPdate(project.getPDate());
		setCreator(request.getCreatorFullName());
		setCreatedDate(request.getCreateTimestamp());
		setModifier(request.getModifierFullName());
		setModifiedDate(request.getModifyTimestamp());
	}
}