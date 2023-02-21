package e3ps.bom.partlist.beans;

import java.sql.Timestamp;

import e3ps.common.util.ContentUtils;
import e3ps.common.util.StringUtils;
import e3ps.partlist.PartListMaster;
import e3ps.project.Project;
import lombok.Getter;
import lombok.Setter;
import wt.session.SessionHelper;

@Getter
@Setter
public class PartListColumnData {

	private String oid;
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

	public PartListColumnData(PartListMaster partListMaster, Project project) throws Exception {
		setOid(partListMaster.getPersistInfo().getObjectIdentifier().getStringValue());
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
		if(project.getDetail() != null) {
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
	}
}
