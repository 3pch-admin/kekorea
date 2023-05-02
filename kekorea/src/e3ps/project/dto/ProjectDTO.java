package e3ps.project.dto;

import java.sql.Timestamp;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.project.Project;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.variable.TaskTypeVariable;
import e3ps.project.variable.ProjectUserTypeVariable;
import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class ProjectDTO {

	private String oid;
	private String state;
	private String projectType_code;
	private String projectType_name;
	private String projectType_oid;
	private boolean estimate = false;
	private String customer_code;
	private String customer_name;
	private String customer_oid;
	private String install_code;
	private String install_name;
	private String install_oid;
	private String kekNumber;
	private String keNumber;
	private String mak_code;
	private String mak_name;
	private String mak_oid;
	private String detail_name;
	private String detail_code;
	private String detail_oid;
	private String userId;
	private String description;
	private Timestamp pdate;
	private String pdate_txt;
	private Timestamp endDate;
	private String endDate_txt = "";
	private Timestamp customDate;
	private String customDate_txt;
	private String model;

	private String machine_name;
	private String machine_oid;
	private String elec_name;
	private String elec_oid;
	private String soft_name;
	private String soft_oid;

	private int kekProgress;
	private int machineProgress;
	private int elecProgress;
	private String kekState;

	private String planStartDate_txt;
	private String planEndDate_txt;
	private String startDate_txt = "";
	private int duration;
	private int holiday;

	private String pm = "지정안됨";
	private String subPm = "지정안됨";

	private double machinePrice = 0D;
	private double elecPrice = 0D;
	private double totalPrice = 0D;

	private double outputMachinePrice = 0D;
	private double outputElecPrice = 0D;
	private double outputTotalPrice = 0D;

	private String template_oid;
	private String template_txt;

	public ProjectDTO() {

	}

	public ProjectDTO(Project project) throws Exception {
		setOid(project.getPersistInfo().getObjectIdentifier().getStringValue());

		// 진행상태 아이콘
		String gate1 = ProjectHelper.manager.getStateIcon(project.getGate1() != null ? project.getGate1() : 0);
		String gate2 = ProjectHelper.manager.getStateIcon(project.getGate2() != null ? project.getGate2() : 0);
		String gate3 = ProjectHelper.manager.getStateIcon(project.getGate3() != null ? project.getGate3() : 0);
		String gate4 = ProjectHelper.manager.getStateIcon(project.getGate4() != null ? project.getGate4() : 0);
		String gate5 = ProjectHelper.manager.getStateIcon(project.getGate5() != null ? project.getGate5() : 0);
		setState(gate1 + gate2 + gate3 + gate4 + gate5);

		setProjectType_code(project.getProjectType().getCode());
		setProjectType_name(project.getProjectType().getName());
		setProjectType_oid(project.getProjectType().getPersistInfo().getObjectIdentifier().getStringValue());
		setEstimate(project.getProjectType().getCode().equals("견적"));
		setCustomer_code(project.getCustomer().getCode());
		setCustomer_name(project.getCustomer().getName());
		setCustomer_oid(project.getCustomer().getPersistInfo().getObjectIdentifier().getStringValue());
		setInstall_code(project.getInstall().getCode());
		setInstall_name(project.getInstall().getName());
		setInstall_oid(project.getInstall().getPersistInfo().getObjectIdentifier().getStringValue());
		setMak_code(project.getMak().getCode());
		setMak_name(project.getMak().getName());
		setMak_oid(project.getMak().getPersistInfo().getObjectIdentifier().getStringValue());
		setDetail_code(project.getDetail().getCode());
		setDetail_name(project.getDetail().getName());
		setDetail_oid(project.getDetail().getPersistInfo().getObjectIdentifier().getStringValue());
		setKekNumber(project.getKekNumber());
		setKeNumber(project.getKeNumber());
		setUserId(project.getUserId());
		setDescription(project.getDescription());
		setPdate(project.getPDate());
		setPdate_txt(CommonUtils.getPersistableTime(project.getPDate()));
		if (project.getEndDate() != null) {
			setEndDate(project.getEndDate());
			setEndDate_txt(CommonUtils.getPersistableTime(project.getEndDate()));
		}
		if (project.getCustomDate() != null) {
			setCustomDate(project.getCustomDate());
			setCustomDate_txt(CommonUtils.getPersistableTime(project.getCustomDate()));
		}
		setModel(project.getModel());
		WTUser machineUser = ProjectHelper.manager.getUserType(project, ProjectUserTypeVariable.MACHINE);

		if (machineUser != null) {
			setMachine_name(machineUser.getFullName());
			setMachine_oid(machineUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		WTUser elecUser = ProjectHelper.manager.getUserType(project, ProjectUserTypeVariable.ELEC);
		if (elecUser != null) {
			setElec_name(elecUser.getFullName());
			setElec_oid(elecUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		WTUser softUser = ProjectHelper.manager.getUserType(project, ProjectUserTypeVariable.SOFT);
		if (softUser != null) {
			setSoft_name(softUser.getFullName());
			setSoft_oid(softUser.getPersistInfo().getObjectIdentifier().getStringValue());
		}

		setKekProgress(ProjectHelper.manager.getKekProgress(project));
		setMachineProgress(ProjectHelper.manager.getTaskProgress(project, TaskTypeVariable.MACHINE));
		setElecProgress(ProjectHelper.manager.getTaskProgress(project, TaskTypeVariable.ELEC));
		setKekState(project.getKekState());

		setPlanStartDate_txt(CommonUtils.getPersistableTime(project.getPlanStartDate()));
		setPlanEndDate_txt(CommonUtils.getPersistableTime(project.getPlanEndDate()));
		if (project.getStartDate() != null) {
			setStartDate_txt(CommonUtils.getPersistableTime(project.getStartDate()));
		}
		setDuration(DateUtils.getDuration(project.getPlanStartDate(), project.getPlanEndDate()));
		setHoliday(DateUtils.getPlanDurationHoliday(project.getPlanStartDate(), project.getPlanEndDate()));

		WTUser pmUser = ProjectHelper.manager.getUserType(project, ProjectUserTypeVariable.PM);
		if (pmUser != null) {
			setPm(pmUser.getFullName());
		}

		WTUser subPmUser = ProjectHelper.manager.getUserType(project, ProjectUserTypeVariable.SUB_PM);
		if (subPmUser != null) {
			setSubPm(subPmUser.getFullName());
		}

		setMachinePrice(project.getMachinePrice() != null ? project.getMachinePrice() : 0D);
		setElecPrice(project.getElecPrice() != null ? project.getElecPrice() : 0D);

		if (project.getMachinePrice() != null || project.getElecPrice() != null) {
			setTotalPrice(getMachinePrice() + getElecPrice());
		}

		setOutputMachinePrice(project.getOutputMachinePrice() != null ? project.getOutputMachinePrice() : 0D);
		setOutputElecPrice(project.getOutputElecPrice() != null ? project.getOutputElecPrice() : 0D);
		if (project.getOutputMachinePrice() != null || project.getOutputElecPrice() != null) {
			setOutputTotalPrice(getOutputMachinePrice() + getOutputElecPrice());
		}

		if (project.getTemplate() != null) {
			setTemplate_oid(project.getTemplate().getPersistInfo().getObjectIdentifier().getStringValue());
			setTemplate_txt(project.getTemplate().getName());
		}
	}
}
