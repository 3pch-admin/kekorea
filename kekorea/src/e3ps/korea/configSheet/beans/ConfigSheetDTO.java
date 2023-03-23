package e3ps.korea.configSheet.beans;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.ConfigSheetProjectLink;
import e3ps.project.Project;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigSheetDTO {

	private String oid;
	private String loid;
	private String poid;
	private String projectType_name;
	private String name;
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

	// 변수용
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> addRows = new ArrayList<>();
	private ArrayList<Map<String, String>> _addRows = new ArrayList<>();
	private ArrayList<Map<String, String>> _addRows_ = new ArrayList<>();

	public ConfigSheetDTO() {

	}

	public ConfigSheetDTO(ConfigSheet configSheet) throws Exception {
		setOid(configSheet.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(configSheet.getName());
		setContent(StringUtils.replaceToValue(configSheet.getDescription()));
		setState(configSheet.getState());
		setCreator(configSheet.getOwnership().getOwner().getFullName());
		setCreatedDate(configSheet.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(configSheet.getCreateTimestamp()));
	}

	public ConfigSheetDTO(ConfigSheetProjectLink link) throws Exception {
		ConfigSheet configSheet = link.getConfigSheet();
		Project project = link.getProject();
		setOid(configSheet.getPersistInfo().getObjectIdentifier().getStringValue());
		setLoid(link.getPersistInfo().getObjectIdentifier().getStringValue());
		setPoid(project.getPersistInfo().getObjectIdentifier().getStringValue());
		if (project.getProjectType() != null) {
			setProjectType_name(project.getProjectType().getName());
		}

		setName(configSheet.getName());
		setContent(StringUtils.replaceToValue(configSheet.getDescription()));
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
		setModel(project.getModel());

		if (project.getPDate() != null) {
			setPdate(project.getPDate());
			setPdate_txt(CommonUtils.getPersistableTime(project.getPDate()));
		}
		setState(configSheet.getState());
		setCreator(configSheet.getOwnership().getOwner().getFullName());
		setCreatedDate(configSheet.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(configSheet.getCreateTimestamp()));
	}
}
