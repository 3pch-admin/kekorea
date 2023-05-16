package e3ps.epm.workOrder.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.WorkOrderProjectLink;
import e3ps.project.Project;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkOrderDTO {

	private String oid;
	private String loid;
	private String poid;
	private String projectType_name;
	private String name;
	private String number;
	private String workOrderType;
	private String content;
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
	private String pdate_txt;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String primary;
	private int version;
	private String note;
	private String thumbnail;
	private String icons;
	private boolean latest;

	private boolean isEdit = false;
	private boolean isRevise = false;

	// 변수용
	private ArrayList<Map<String, Object>> addRows = new ArrayList<>(); // 도면 일람표

	/**
	 * 결재 변수
	 */
	private ArrayList<Map<String, String>> agreeRows = new ArrayList<>(); // 검토
	private ArrayList<Map<String, String>> approvalRows = new ArrayList<>(); // 결재
	private ArrayList<Map<String, String>> receiveRows = new ArrayList<>(); // 수신

	/**
	 * 작번 변수
	 */
	private ArrayList<Map<String, String>> addRows9 = new ArrayList<>();

	/**
	 * 도번
	 */
	private ArrayList<Map<String, String>> addRows11 = new ArrayList<>();

	/**
	 * 태스크 변수
	 */
	private String toid;
	private int progress;

	private ArrayList<String> secondarys = new ArrayList<>();

	public WorkOrderDTO() {

	}

	public WorkOrderDTO(WorkOrder workOrder) throws Exception {
		setOid(workOrder.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(workOrder.getName());
		setNumber(workOrder.getNumber());
		setWorkOrderType(workOrder.getWorkOrderType());
		setState(workOrder.getLifeCycleState().getDisplay());
		setContent(StringUtils.replaceToValue(workOrder.getDescription()));
		setVersion(workOrder.getVersion());
		setCreator(workOrder.getCreatorFullName());
		setCreatedDate(workOrder.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(workOrder.getCreateTimestamp()));
		setEdit(workOrder.getLifeCycleState().toString().equals("INWORK"));
		setRevise(workOrder.getLifeCycleState().toString().equals("APPROVED"));
		setNote(workOrder.getNote());
		setThumbnail(AUIGridUtils.thumbnailTemplate(workOrder));
		setIcons(AUIGridUtils.secondaryTemplate(workOrder));
		setLatest(workOrder.getLatest());
	}

	public WorkOrderDTO(WorkOrderProjectLink link) throws Exception {
		WorkOrder workOrder = link.getWorkOrder();
		Project project = link.getProject();

		setOid(workOrder.getPersistInfo().getObjectIdentifier().getStringValue());
		setLoid(link.getPersistInfo().getObjectIdentifier().getStringValue());
		setPoid(project.getPersistInfo().getObjectIdentifier().getStringValue());
		if (project.getProjectType() != null) {
			setProjectType_name(project.getProjectType().getName());
		}

		setName(workOrder.getName());
		setNumber(workOrder.getNumber());
		setWorkOrderType(workOrder.getWorkOrderType());
		setVersion(workOrder.getVersion());
		setContent(StringUtils.replaceToValue(workOrder.getDescription()));
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
		setDescription(StringUtils.replaceToValue(project.getDescription()));
		setState(workOrder.getLifeCycleState().getDisplay());
		setModel(project.getModel());

		if (project.getPDate() != null) {
			setPdate(project.getPDate());
			setPdate_txt(CommonUtils.getPersistableTime(project.getPDate()));
		}
		setCreator(workOrder.getCreatorFullName());
		setPrimary(AUIGridUtils.primaryTemplate(workOrder));
		setCreatedDate(workOrder.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(workOrder.getCreateTimestamp()));
		setEdit(workOrder.getLifeCycleState().toString().equals("INWORK"));
		setRevise(workOrder.getLifeCycleState().toString().equals("APPROVED"));
		setNote(workOrder.getNote());
		setThumbnail(AUIGridUtils.thumbnailTemplate(workOrder));
		setIcons(AUIGridUtils.secondaryTemplate(workOrder));
		setLatest(workOrder.getLatest());
	}
}
