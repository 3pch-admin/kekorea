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
	private int lotNo;
	private String unitName;
	private String number;
	private String businessSector_oid;
	private String businessSector_txt;
	private String businessSector_code;
	private String drawingCompany_oid;
	private String drawingCompany_txt;
	private String drawingCompany_code;
	private String classificationWritingDepartments_oid;
	private String classificationWritingDepartments_txt;
	private String classificationWritingDepartments_code;
	private String writtenDocuments_oid;
	private String writtenDocuments_txt;
	private String writtenDocuments_code;
	private String size_oid;
	private String size_txt;
	private String size_code;
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
		setLotNo(numberRule.getMaster().getLotNo());
		setUnitName(numberRule.getMaster().getUnitName());
		setBusinessSector_oid(
				numberRule.getMaster().getSector().getPersistInfo().getObjectIdentifier().getStringValue());
		setBusinessSector_txt(numberRule.getMaster().getSector().getName());
		setBusinessSector_code(numberRule.getMaster().getSector().getCode());
		setDrawingCompany_oid(
				numberRule.getMaster().getCompany().getPersistInfo().getObjectIdentifier().getStringValue());
		setDrawingCompany_txt(numberRule.getMaster().getCompany().getName());
		setDrawingCompany_code(numberRule.getMaster().getCompany().getCode());
		setClassificationWritingDepartments_oid(
				numberRule.getMaster().getDepartment().getPersistInfo().getObjectIdentifier().getStringValue());
		setClassificationWritingDepartments_txt(numberRule.getMaster().getDepartment().getName());
		setClassificationWritingDepartments_code(numberRule.getMaster().getDepartment().getCode());
		setWrittenDocuments_oid(
				numberRule.getMaster().getDocument().getPersistInfo().getObjectIdentifier().getStringValue());
		setWrittenDocuments_txt(numberRule.getMaster().getDocument().getName());
		setWrittenDocuments_code(numberRule.getMaster().getDocument().getCode());
		setSize_oid(numberRule.getMaster().getSize().getPersistInfo().getObjectIdentifier().getStringValue());
		setSize_txt(numberRule.getMaster().getSize().getName());
		setSize_code(numberRule.getMaster().getSize().getCode());
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
