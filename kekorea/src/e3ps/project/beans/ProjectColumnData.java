package e3ps.project.beans;

import java.sql.Timestamp;

import e3ps.common.util.StringUtils;
import e3ps.project.Project;
import e3ps.project.service.ProjectHelper;
import lombok.Getter;
import lombok.Setter;
import wt.org.WTUser;

@Getter
@Setter
public class ProjectColumnData {

	private String oid;
	private String state;
	private String projectType;
	private String customer;
	private String install;
	private String kekNumber;
	private String keNumber;
	private String mak;
	private String detail;
	private String userId;
	private String description;
	private Timestamp pdate;
	private Timestamp completeDate;
	private Timestamp endDate;
	private String model;
	private String machine;
	private String elec;
	private String soft;
	private int kekProgress;
	private String kekState;

	public ProjectColumnData() {

	}

	public ProjectColumnData(Project project) throws Exception {
		setOid(project.getPersistInfo().getObjectIdentifier().getStringValue());

		// 진행상태 아이콘
		String gate1 = ProjectHelper.manager.getStateIcon(project.getGate1() != null ? project.getGate1() : 0);
		String gate2 = ProjectHelper.manager.getStateIcon(project.getGate2() != null ? project.getGate2() : 0);
		String gate3 = ProjectHelper.manager.getStateIcon(project.getGate3() != null ? project.getGate3() : 0);
		String gate4 = ProjectHelper.manager.getStateIcon(project.getGate4() != null ? project.getGate4() : 0);
		String gate5 = ProjectHelper.manager.getStateIcon(project.getGate5() != null ? project.getGate5() : 0);
		setState(gate1 + gate2 + gate3 + gate4 + gate5);

		setProjectType(project.getProjectType() != null ? project.getProjectType().getName() : "");
		setCustomer(project.getCustomer() != null ? project.getCustomer().getName() : "");
		setInstall(project.getInstall() != null ? project.getInstall().getName() : "");
		setMak(project.getMak() != null ? project.getMak().getName() : "");
		setDetail(project.getDetail() != null ? project.getDetail().getName() : "");
		setKekNumber(project.getKekNumber());
		setKeNumber(project.getKeNumber());
		setUserId(project.getUserId());
		setDescription(StringUtils.replaceToValue(project.getDescription()));
		setPdate(project.getPDate());
		setCompleteDate(project.getEndDate());
		setEndDate(project.getCustomDate());
		setModel(project.getModel());

		WTUser machineUser = ProjectHelper.manager.getUserTypeByProject(project, "MACHINE");
		WTUser elecUser = ProjectHelper.manager.getUserTypeByProject(project, "ELEC");
		WTUser softUser = ProjectHelper.manager.getUserTypeByProject(project, "SOFT");

		if (machineUser != null) {
			setMachine(machineUser.getFullName());
		}

		if (elecUser != null) {
			setElec(elecUser.getFullName());
		}

		if (softUser != null) {
			setSoft(softUser.getFullName());
		}

		setKekProgress(project.getProgress());
		setKekState(project.getKekState());
	}
}
