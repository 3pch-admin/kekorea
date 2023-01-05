
package e3ps.project.beans;

import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.project.Project;
import e3ps.project.Template;
import e3ps.project.enums.ProjectStateType;
import e3ps.project.enums.ProjectUserType;
import e3ps.project.service.ProjectHelper;
import wt.org.WTUser;

public class ProjectViewData {

	public Project project;
	public String oid;

	public String ins_location;
	public String kek_number;
	public String ke_number;
	public String customer;
	public String userID;
	public String pType;
	public String mak;
	public String description;
	public String createDate;
	public String customDate;
	public String progress;
	public String progress_state;
	public String state;
	public String model;

	public String pDate;
	public String iconPath;

	public int machineProgress = 0;
	public int elecProgress = 0;
	public int kekProgress = 0;
	public String kekState;
	public double comp = 0D;

	public double machinePrice = 0D;
	public double elecPrice = 0D;
	public double totalPrice = 0D;

	public double outputMachinePrice = 0D;
	public double outputElecPrice = 0D;
	public double outputTotalPrice = 0D;

	public boolean isEditer = false;

	public WTUser pm;
	public WTUser subPm;
	public WTUser machine;
	public WTUser elec;
	public WTUser soft;

	public Template template;

	public boolean isStand = false;
	public boolean isStart = false;
	public boolean isStop = false;
	public boolean isDelay = false;
	public boolean isComplete = false;

	public String planStartDate;
	public String planEndDate;
	public String startDate;
	public String endDate;

	public int duration;
	public int holiday;
	public String stateBar;

	public boolean isQuotation = false;

	public ProjectViewData(Project project) throws Exception {
		this.project = project;
		this.oid = project.getPersistInfo().getObjectIdentifier().getStringValue();
		this.ins_location = StringUtils.replaceToValue(project.getIns_location());
		this.kek_number = StringUtils.replaceToValue(project.getKekNumber());
		this.ke_number = StringUtils.replaceToValue(project.getKeNumber());
		this.customer = StringUtils.replaceToValue(project.getCustomer());
		this.userID = StringUtils.replaceToValue(project.getUserId());
		this.pType = StringUtils.replaceToValue(project.getPType());
		this.model = StringUtils.replaceToValue(project.getModel());

		if ("견적".equals(this.pType)) {
			this.isQuotation = true;
		}

		this.mak = project.getMak();
		this.description = StringUtils.replaceToValue(project.getDescription());
		this.createDate = project.getCreateTimestamp().toString().substring(0, 10);
		this.pDate = project.getPDate() != null ? project.getPDate().toString().substring(0, 10) : "";
		this.customDate = project.getCustomDate() != null ? project.getCustomDate().toString().substring(0, 10) : "";
		this.progress = "";
		this.progress_state = "";
		this.stateBar = ProjectHelper.manager.getProjectStateBar(project);
		// this.state = project.getState() + "[" + this.stateBar + "]";
		this.state = this.stateBar;
		// this.state = project.getState();

		this.machineProgress = ProjectHelper.manager.getMachineAllocateProgress(project);
		this.elecProgress = ProjectHelper.manager.getElecAllocateProgress(project);
		this.kekProgress = ProjectHelper.manager.getKekProgress(project);
		this.kekState = project.getKekState();

		this.machinePrice = project.getMachinePrice() != null ? project.getMachinePrice() : 0D;
		this.elecPrice = project.getElecPrice() != null ? project.getElecPrice() : 0D;

		if (project.getMachinePrice() != null || project.getElecPrice() != null) {
			this.totalPrice = this.machinePrice + this.elecPrice;
		} else {
			this.totalPrice = 0D;
		}

		this.outputMachinePrice = project.getOutputMachinePrice() != null ? project.getOutputMachinePrice() : 0D;
		this.outputElecPrice = project.getOutputElecPrice() != null ? project.getOutputElecPrice() : 0D;
		if (project.getOutputMachinePrice() != null || project.getOutputElecPrice() != null) {
			this.outputTotalPrice = this.outputMachinePrice + this.outputElecPrice;
		} else {
			this.outputTotalPrice = 0D;
		}

//		this.isEditer = ProjectHelper.manager.isEditer(project);
		this.isEditer = true;

		this.pm = ProjectHelper.manager.getPMByProject(project);
		this.subPm = ProjectHelper.manager.getSubPMByProject(project);
		this.machine = ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.MACHINE.name());
		this.elec = ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.ELEC.name());
		this.soft = ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.SOFT.name());

		this.template = project.getTemplate();

		this.isStand = this.state != null ? this.state.equals(ProjectStateType.STAND.getDisplay()) : false;
		this.isStart = this.state != null ? this.state.equals(ProjectStateType.INWORK.getDisplay()) : false;
		this.isStop = this.state != null ? this.state.equals(ProjectStateType.STOP.getDisplay()) : false;
		this.isDelay = this.state != null ? this.state.equals(ProjectStateType.DELAY.getDisplay()) : false;
		this.isDelay = this.state != null ? this.state.equals(ProjectStateType.COMPLETE.getDisplay()) : false;

		this.planStartDate = project.getPlanStartDate() != null ? project.getPlanStartDate().toString().substring(0, 10)
				: "";
		this.planEndDate = project.getPlanEndDate() != null ? project.getPlanEndDate().toString().substring(0, 10) : "";
		this.startDate = project.getStartDate() != null ? project.getStartDate().toString().substring(0, 10) : "시작안됨";
		this.endDate = project.getEndDate() != null ? project.getEndDate().toString().substring(0, 10) : "";

		if (this.planStartDate != null && this.planEndDate != null) {
			this.duration = DateUtils.getDuration(project.getPlanStartDate(), project.getPlanEndDate());
			this.holiday = DateUtils.getPlanDurationHoliday(project.getPlanStartDate(), project.getPlanEndDate());
		} else {
			this.duration = 0;
			this.holiday = 0;
		}

		this.comp = ProjectHelper.getPreferComp(project);
	}
}