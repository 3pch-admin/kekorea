package e3ps.epm.numberRule.beans;

import java.sql.Timestamp;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.epm.numberRule.NumberRule;
import e3ps.epm.numberRule.NumberRuleMaster;
import lombok.Getter;
import lombok.Setter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberRuleColumnData {

	private String oid;
	private String name;
	private String number;
	private String businessSector;
	private String drawingCompany;
	private String department;
	private String document;
	private boolean latest;
	private int version;
	private String creator;
	private Timestamp createdDate;
	private String modifier;
	private Timestamp modifiedDate;

	public NumberRuleColumnData() {

	}

	public NumberRuleColumnData(NumberRule numberRule) throws Exception {
		setOid(numberRule.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(numberRule.getMaster().getName());
		setNumber(numberRule.getMaster().getNumber());
		setBusinessSector(numberRule.getMaster().getSector().getName());
		setDrawingCompany(numberRule.getMaster().getCompany().getName());
		setDocument(numberRule.getMaster().getDocument().getName());
		setDepartment(numberRule.getMaster().getDepartment().getName());
		setCreator(numberRule.getMaster().getOwnership().getOwner().getFullName());
		setCreatedDate(numberRule.getMaster().getCreateTimestamp());
		setLatest(numberRule.getLatest());
		setVersion(numberRule.getVersion());
		setModifier(numberRule.getOwnership().getOwner().getFullName());
		setModifiedDate(numberRule.getCreateTimestamp());
	}
}
