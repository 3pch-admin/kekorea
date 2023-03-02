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
	private String projectType_code;
	private String projectType_name;
	private String projectType_oid;
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
	private Timestamp completeDate;
	private Timestamp customDate;
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

		if (project.getProjectType() != null) {
			setProjectType_code(project.getProjectType().getCode());
			setProjectType_name(project.getProjectType().getName());
			setProjectType_oid(project.getProjectType().getPersistInfo().getObjectIdentifier().getStringValue());
		}

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
		setPdate(project.getPDate());
		setCompleteDate(project.getEndDate());
		setCustomDate(project.getCustomDate());
		setModel(project.getModel());

		WTUser machineUser = ProjectHelper.manager.getUserType(project, "MACHINE");
		WTUser elecUser = ProjectHelper.manager.getUserType(project, "ELEC");
		WTUser softUser = ProjectHelper.manager.getUserType(project, "SOFT");

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
