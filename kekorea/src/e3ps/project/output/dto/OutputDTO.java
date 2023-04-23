package e3ps.project.output.dto;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import wt.doc.WTDocument;

@Getter
@Setter
public class OutputDTO {

	private String oid;
	private String name;
	private String number;
	private String description;
	private String location;
	private String state;
	private String version;
	private String docType;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String modifier;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private String primary;
	// 변수담기용
	private ArrayList<Map<String, String>> addRows9 = new ArrayList<>(); // 작번
	private ArrayList<Map<String, String>> agreeRows = new ArrayList<>(); // 검토
	private ArrayList<Map<String, String>> approvalRows = new ArrayList<>(); // 결재
	private ArrayList<Map<String, String>> receiveRows = new ArrayList<>(); // 수신
	private ArrayList<String> primarys = new ArrayList<>();
	private String poid;
	private String toid;
	private boolean connect;
	private int progress = 0;

	public OutputDTO() {

	}

	public OutputDTO(WTDocument output) throws Exception {
		setOid(output.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(output.getName());
		setNumber(output.getNumber());
		setDescription(StringUtils.replaceToValue(output.getDescription()));
		setLocation(output.getLocation());
		setState(output.getLifeCycleState().getDisplay());
		setVersion(CommonUtils.getFullVersion(output));
		setCreator(output.getCreatorFullName());
		setCreatedDate(output.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(output.getCreateTimestamp()));
		setModifier(output.getModifierFullName());
		setModifiedDate(output.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(output.getModifyTimestamp()));
		setDocType(output.getDocType().getDisplay());
		setPrimary(AUIGridUtils.primaryTemplate(output));
	}
}
