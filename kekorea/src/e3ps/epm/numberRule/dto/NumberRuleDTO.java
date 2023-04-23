package e3ps.epm.numberRule.dto;

import e3ps.common.util.CommonUtils;
import e3ps.epm.numberRule.NumberRule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberRuleDTO {

	private String oid;
	private String moid;
	private String name;
	private String number;
	private String businessSector;
	private String drawingCompany;
	private String classificationWritingDepartments;
	private String writtenDocuments;
	private String size;
	private int version;
	private boolean latest;
	private String state;
	private String note;
	private String creatorId;
	private String creator;
	private String createdDate_txt;
	private String modifier;
	private String modifierId;
	private String modifiedDate_txt;
	
	/**
	 * 변수
	 */
	private int next;

	public NumberRuleDTO() {

	}

	public NumberRuleDTO(NumberRule numberRule) throws Exception {
		setOid(numberRule.getPersistInfo().getObjectIdentifier().getStringValue());
		setMoid(numberRule.getMaster().getPersistInfo().getObjectIdentifier().getStringValue());
		setNumber(numberRule.getMaster().getNumber());
		setName(numberRule.getMaster().getName());
		setBusinessSector(numberRule.getMaster().getSector().getCode());
		setDrawingCompany(numberRule.getMaster().getCompany().getCode());
		setClassificationWritingDepartments(numberRule.getMaster().getDepartment().getCode());
		setWrittenDocuments(numberRule.getMaster().getDocument().getCode());
		setSize(numberRule.getMaster().getSize().getName());
		setVersion(numberRule.getVersion());
		setLatest(numberRule.getLatest());
		setState(numberRule.getState());
		setNote(numberRule.getNote());
		setCreatorId(numberRule.getMaster().getOwnership().getOwner().getName());
		setCreator(numberRule.getMaster().getOwnership().getOwner().getFullName());
		setCreatedDate_txt(CommonUtils.getPersistableTime(numberRule.getMaster().getCreateTimestamp()));
		setModifierId(numberRule.getOwnership().getOwner().getName());
		setModifier(numberRule.getOwnership().getOwner().getFullName());
		setModifiedDate_txt(CommonUtils.getPersistableTime(numberRule.getModifyTimestamp()));
	}

}
