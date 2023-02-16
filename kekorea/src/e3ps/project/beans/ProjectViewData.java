
package e3ps.project.beans;

import java.sql.Timestamp;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.project.Project;
import e3ps.project.service.ProjectHelper;
import e3ps.project.template.Template;
import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class ProjectViewData {

	private Project project;
	private String oid;
	private String install_code;
	private String install_name;
	private String install_oid;
	private String kekNumber;
	private String keNumber;
	private String customer_code;
	private String customer_name;
	private String customer_oid;
	private String userID;
	private String projectType_code;
	private String projectType_name;
	private String projectType_oid;
	private String mak_code;
	private String mak_name;
	private String mak_oid;
	private String detail_code;
	private String detail_name;
	private String detail_oid;
	private String description;
	private Timestamp createDate;
	private String createDate_txt = "";
	private Timestamp customDate;
	private String customDate_txt = "";
	private Timestamp pDate;
	private String pDate_txt = "";
	private String state;
	private String model;
	private int machineProgress = 0;
	private int elecProgress = 0;
	private int kekProgress = 0;
	private String kekState;
	private double comp = 0D;
	private double machinePrice = 0D;
	private double elecPrice = 0D;
	private double totalPrice = 0D;
	private double outputMachinePrice = 0D;
	private double outputElecPrice = 0D;
	private double outputTotalPrice = 0D;
	private WTUser pm;
	private String pm_txt = "";
	private WTUser subPm;
	private String subPm_txt = "";
	private WTUser machine;
	private String machin_txt = "";
	private WTUser elec;
	private String elec_txt = "";
	private WTUser soft;
	private String soft_txt = "";
	private Template template;
	private boolean stand = false;
	private boolean start = false;
	private boolean stop = false;
	private boolean delay = false;
	private boolean complete = false;
	private Timestamp planStartDate;
	private String planStartDate_txt = "";
	private Timestamp planEndDate;
	private String planEndDate_txt = "";
	private Timestamp startDate;
	private String startDate_txt = "";
	private Timestamp endDate;
	private String endDate_txt = "";
	private int duration;
	private int holiday;
	private boolean quotation = false;

	public ProjectViewData(Project project) throws Exception {
		setProject(project);
		setOid(project.getPersistInfo().getObjectIdentifier().getStringValue());
		if (project.getInstall() != null) {
			setInstall_code(project.getInstall().getCode());
			setInstall_name(project.getInstall().getName());
			setInstall_oid(project.getInstall().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setKekNumber(project.getKekNumber());
		setKeNumber(project.getKeNumber());
		if (project.getCustomer() != null) {
			setCustomer_code(project.getCustomer().getCode());
			setCustomer_name(project.getCustomer().getName());
			setCustomer_oid(project.getCustomer().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setUserID(project.getUserId());
		if (project.getProjectType() != null) {
			setProjectType_code(project.getProjectType().getCode());
			setProjectType_name(project.getProjectType().getName());
			setProjectType_oid(project.getProjectType().getPersistInfo().getObjectIdentifier().getStringValue());
			if (project.getProjectType().getName().equals("견적")) {
				setQuotation(true);
			}
		}
		setModel(project.getModel());
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
		setDescription(project.getDescription());

		setCreateDate(project.getCreateTimestamp());
		setCreateDate_txt(CommonUtils.getPersistableTime(project.getCreateTimestamp()));

		if (project.getCustomDate() != null) {
			setCustomDate(project.getCustomDate());
			setCustomDate_txt(CommonUtils.getPersistableTime(project.getCustomDate()));
		}

		if (project.getPDate() != null) {
			setPDate(project.getPDate());
			setPDate_txt(CommonUtils.getPersistableTime(project.getPDate()));
		}
		setState(ProjectHelper.manager.getProjectStateBar(project));
		setKekState(project.getKekState());
		setKekProgress(ProjectHelper.manager.getKekProgress(project));
		setMachineProgress(ProjectHelper.manager.getMachineAllocateProgress(project));
		setElecProgress(ProjectHelper.manager.getElecAllocateProgress(project));
		setMachinePrice(project.getMachinePrice());
		setElecPrice(project.getElecPrice());
		setTotalPrice(getMachinePrice() + getElecPrice());
		System.out.println("=" + project.getOutputMachinePrice());
		setOutputMachinePrice(project.getOutputMachinePrice());
		setOutputElecPrice(project.getOutputElecPrice());
		setOutputTotalPrice(getOutputMachinePrice() + getOutputElecPrice());

		WTUser _pm = ProjectHelper.manager.getUserType(project, "PM");
		if (_pm != null) {
			setPm(_pm);
			setPm_txt(_pm.getFullName());
		}

		WTUser _subPm = ProjectHelper.manager.getUserType(project, "SUB_PM");
		if (_subPm != null) {
			setSubPm(_subPm);
			setSubPm_txt(_subPm.getFullName());
		}

		WTUser _machine = ProjectHelper.manager.getUserType(project, "MACHINE");
		if (_machine != null) {
			setMachine(_machine);
			setMachin_txt(_machine.getFullName());
		}
		WTUser _elec = ProjectHelper.manager.getUserType(project, "ELEC");
		if (_elec != null) {
			setElec(_elec);
			setElec_txt(_elec.getFullName());
		}

		WTUser _soft = ProjectHelper.manager.getUserType(project, "SOFT");
		if (_soft != null) {
			setSoft(_soft);
			setSoft_txt(_soft.getFullName());
		}
		setTemplate(project.getTemplate());

		setPlanStartDate(project.getPlanStartDate());
		setPlanStartDate_txt(CommonUtils.getPersistableTime(project.getPlanStartDate()));
		setPlanEndDate(project.getPlanEndDate());
		setPlanEndDate_txt(CommonUtils.getPersistableTime(project.getPlanEndDate()));

		if (project.getStartDate() != null) {
			setStartDate(project.getStartDate());
			setStartDate_txt(CommonUtils.getPersistableTime(project.getStartDate()));
		}

		if (project.getEndDate() != null) {
			setEndDate(project.getEndDate());
			setEndDate_txt(CommonUtils.getPersistableTime(project.getEndDate()));
		}

		setDuration(DateUtils.getDuration(project.getPlanStartDate(), project.getPlanEndDate())); // 필수..
		setHoliday(DateUtils.getPlanDurationHoliday(project.getPlanStartDate(), project.getPlanEndDate()));
		setComp(ProjectHelper.getPreferComp(project));
		setViewState(project);
	}

	private void setViewState(Project project) throws Exception {
		setStand("준비중".equals(ProjectHelper.STAND));
		setStart("작업 중".equals(ProjectHelper.INWORK));
		setDelay("지연됨".equals(ProjectHelper.DELAY));
		setComplete("완료됨".equals(ProjectHelper.COMPLETE));
		setStop("중단됨".equals(ProjectHelper.STOP));
	}
}