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
	private String number;
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
	private String creatorId;
	private int version;
	private boolean latest;
	private ArrayList<String> dataFields = new ArrayList<>();

	// 변수용
	private ArrayList<String> secondarys = new ArrayList<>();
	private ArrayList<Map<String, String>> addRows = new ArrayList<>();
	private ArrayList<Map<String, String>> addRows9 = new ArrayList<>();
	private ArrayList<Map<String, String>> agreeRows = new ArrayList<>(); // 검토
	private ArrayList<Map<String, String>> approvalRows = new ArrayList<>(); // 결재
	private ArrayList<Map<String, String>> receiveRows = new ArrayList<>(); // 수신
	private int progress;

	private boolean isEdit = false;
	private boolean isRevise = false;

	public ConfigSheetDTO() {

	}

	public ConfigSheetDTO(ConfigSheet configSheet) throws Exception {
		setOid(configSheet.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(configSheet.getName());
		setNumber(configSheet.getNumber());
		setContent(StringUtils.replaceToValue(configSheet.getDescription()));
		setState(configSheet.getLifeCycleState().getDisplay());
		setCreator(configSheet.getCreatorFullName());
		setCreatedDate(configSheet.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(configSheet.getCreateTimestamp()));
		setCreatorId(configSheet.getCreatorName());
		setEdit(configSheet.getLifeCycleState().toString().equals("INWORK"));
		setRevise(configSheet.getLifeCycleState().toString().equals("APPROVED"));
		setDataFields(configSheet.getDataFields());
	}

	public ConfigSheetDTO(ConfigSheetProjectLink link) throws Exception {
		ConfigSheet configSheet = link.getConfigSheet();
		Project project = link.getProject();
		setOid(configSheet.getPersistInfo().getObjectIdentifier().getStringValue());
		setLoid(link.getPersistInfo().getObjectIdentifier().getStringValue());
		setPoid(project.getPersistInfo().getObjectIdentifier().getStringValue());
		setProjectType_name(project.getProjectType().getName());
		setName(configSheet.getName());
		setNumber(configSheet.getNumber());
		setContent(StringUtils.replaceToValue(configSheet.getDescription()));
		setCustomer_name(project.getCustomer().getName());
		setInstall_name(project.getInstall().getName());
		setMak_name(project.getMak().getName());
		setDetail_name(project.getDetail().getName());
		setKekNumber(project.getKekNumber());
		setKeNumber(project.getKeNumber());
		setUserId(project.getUserId());
		setDescription(StringUtils.replaceToValue(project.getDescription()));
		setModel(project.getModel());
		setPdate(project.getPDate());
		setPdate_txt(CommonUtils.getPersistableTime(project.getPDate()));
		setState(configSheet.getLifeCycleState().getDisplay());
		setCreator(configSheet.getCreatorFullName());
		setCreatedDate(configSheet.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(configSheet.getCreateTimestamp()));
		setCreatorId(configSheet.getCreatorName());
		setVersion(configSheet.getVersion());
		setLatest(configSheet.getLatest());
		setEdit(configSheet.getLifeCycleState().toString().equals("INWORK"));
		setRevise(configSheet.getLifeCycleState().toString().equals("APPROVED"));
		setDataFields(configSheet.getDataFields());
	}
}
