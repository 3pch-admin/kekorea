package e3ps.bom.partlist.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.project.Project;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartListDTO {

	private String oid;
	private String loid;
	private String poid;
	private String projectType_code;
	private String projectType_name;
	private String projectType_oid;
	private String name;
	private String mak_code;
	private String mak_name;
	private String mak_oid;
	private String detail_code;
	private String detail_name;
	private String detail_oid;
	private String kekNumber;
	private String keNumber;
	private String userId;
	private String description;
	private String customer_code;
	private String customer_name;
	private String customer_oid;
	private String install_code;
	private String install_name;
	private String install_oid;
	private Timestamp pdate;
	private String model;
	private String creator;
	private Timestamp createdDate;
	private String state;
	private String engType;
	private String content; // 수배표 설명

	// 변수용
	private ArrayList<Map<String, Object>> _addRows = new ArrayList<>(); // 프로젝트
	private ArrayList<Map<String, Object>> addRows = new ArrayList<>(); // 수배표
	private ArrayList<String> secondarys = new ArrayList<>();

	public PartListDTO() {

	}

	public PartListDTO(PartListMasterProjectLink link) throws Exception {
		PartListMaster partListMaster = link.getPartListMaster();
		Project project = link.getProject();
		setOid(partListMaster.getPersistInfo().getObjectIdentifier().getStringValue());
		setLoid(link.getPersistInfo().getObjectIdentifier().getStringValue());
		setPoid(project.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(partListMaster.getName());
		if (project.getProjectType() != null) {
			setProjectType_code(project.getProjectType().getCode());
			setProjectType_name(project.getProjectType().getName());
			setProjectType_oid(project.getProjectType().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		if (project.getMak() != null) {
			setMak_code(project.getMak().getCode());
			setMak_name(project.getMak().getName());
			setMak_oid(project.getMak().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		if (project.getDetail() != null) {
			setDetail_code(project.getDetail().getCode());
			setDetail_name(project.getDetail().getName());
			setDetail_oid(project.getDetail().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setKekNumber(project.getKekNumber());
		setKeNumber(project.getKeNumber());
		setUserId(project.getUserId());
		setDescription(project.getDescription());
		if (project.getCustomer() != null) {
			setCustomer_code(project.getCustomer().getCode());
			setCustomer_name(project.getCustomer().getName());
			setCustomer_oid(project.getCustomer().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		if (project.getInstall() != null) {
			setInstall_code(project.getInstall().getCode());
			setInstall_name(project.getInstall().getName());
			setInstall_oid(project.getInstall().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setPdate(project.getPDate());
		setModel(project.getModel());
		setState(partListMaster.getLifeCycleState().getDisplay());
		setCreator(partListMaster.getCreatorFullName());
		setCreatedDate(partListMaster.getCreateTimestamp());
		setEngType(partListMaster.getEngType());
	}
}
