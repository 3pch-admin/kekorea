package e3ps.project.column;

import e3ps.common.util.ContentUtils;
import e3ps.project.Project;
import e3ps.project.enums.ProjectUserType;
import e3ps.project.service.ProjectHelper;

public class ProjectColumnData {

	public String oid;
	public String pType;
	public String customer;
	public String ins_location;
	public String kek_number;
	public String ke_number;
	public String userId;
	public String description;
	public String pDate;
	public String customDate;
	public String model;
	public String completeDate;
	public String systemInfo;
	public String machine = "지정안됨";
	public String elec = "지정안됨";
	public String soft = "지정안됨";
	public String number;
	public String mak;

	public String endDate;

	public String kekProgress;
	public String state;
	public String kekState;
	public String stateBar;

	public String iconPath;

	public ProjectColumnData(Project project) throws Exception {
		this.oid = project.getPersistInfo().getObjectIdentifier().getStringValue();
		this.pType = project.getPType();
		this.customer = project.getCustomer();
		this.ins_location = project.getIns_location();
		this.kek_number = project.getKekNumber();
		this.ke_number = project.getKeNumber();
		this.userId = project.getUserId();
		// this.mak = project.getMak();
		this.description = project.getDescription();
		this.pDate = project.getPDate() != null ? project.getPDate().toString().substring(0, 10) : "";
		this.customDate = project.getCustomDate() != null ? project.getCustomDate().toString().substring(0, 10) : "";
		this.model = project.getModel();
		this.systemInfo = project.getSystemInfo();
		this.mak = project.getMak();

		if (ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.MACHINE.name()) != null) {
			this.machine = ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.MACHINE.name())
					.getFullName();
		}

		if (ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.ELEC.name()) != null) {
			this.elec = ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.ELEC.name()).getFullName();
		}

		if (ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.SOFT.name()) != null) {
			this.soft = ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.SOFT.name()).getFullName();
		}

		if (this.pType != null) {

			if (this.pType.equals("견적")) {
				String gate1 = ProjectHelper.manager.getStateIcon(project.getGate1() != null ? project.getGate1() : 0);
				String gate2 = ProjectHelper.manager.getStateIcon(project.getGate2() != null ? project.getGate2() : 0);
				String gate3 = ProjectHelper.manager.getStateIcon(project.getGate3() != null ? project.getGate3() : 0);
				String gate4 = ProjectHelper.manager.getStateIcon(project.getGate4() != null ? project.getGate4() : 0);
				String gate5 = ProjectHelper.manager.getStateIcon(project.getGate5() != null ? project.getGate5() : 0);

				this.stateBar = gate1 + gate2 + gate3 + gate4 + gate5;

			} else {

				String gate1 = ProjectHelper.manager.getStateIcon(project.getGate1() != null ? project.getGate1() : 0);
				String gate2 = ProjectHelper.manager.getStateIcon(project.getGate2() != null ? project.getGate2() : 0);
				String gate3 = ProjectHelper.manager.getStateIcon(project.getGate3() != null ? project.getGate3() : 0);
				String gate4 = ProjectHelper.manager.getStateIcon(project.getGate4() != null ? project.getGate4() : 0);
				String gate5 = ProjectHelper.manager.getStateIcon(project.getGate5() != null ? project.getGate5() : 0);
				this.stateBar = gate1 + gate2 + gate3 + gate4 + gate5;
			}
		}

		this.endDate = project.getCustomDate() != null ? project.getCustomDate().toString().substring(0, 10) : "";

		this.completeDate = project.getPlanEndDate() != null ? project.getPlanEndDate().toString().substring(0, 10)
				: "";

		this.state = this.stateBar;

		// this.kekProgress = ProjectHelper.manager.getKekProgress(project) + "%";
		this.kekProgress = project.getProgress() + "%";

		this.kekState = project.getKekState();

		this.iconPath = ContentUtils.getOpenIcon(this.oid);
	}
}
