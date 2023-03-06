package e3ps.epm.workOrder.dto;

import java.sql.Timestamp;

import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.project.Project;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkOrderDTO {

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
	private String state;
	private String model;
	private Timestamp pdate;
	private String creator;
	private Timestamp createdDate;

	public WorkOrderDTO() {

	}

	public WorkOrderDTO(WorkOrderProjectLink link) throws Exception {
		WorkOrder workOrder = link.getWorkOrder();
		Project project = link.getProject();

		setOid(workOrder.getPersistInfo().getObjectIdentifier().getStringValue());
		if (project.getProjectType() != null) {
			setProjectType_name(project.getProjectType().getName());
		}

		setName(workOrder.getName());

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
		setState(workOrder.getState());
		setModel(project.getModel());
		setPdate(project.getPDate());
		setCreator(workOrder.getOwnership().getOwner().getFullName());
		setCreatedDate(workOrder.getCreateTimestamp());
	}
}
