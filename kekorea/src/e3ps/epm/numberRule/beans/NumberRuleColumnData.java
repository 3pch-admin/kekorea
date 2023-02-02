package e3ps.epm.numberRule.beans;

import java.sql.Timestamp;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.epm.numberRule.NumberRule;
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
	private String creator;
	private Timestamp createdDate;

	public NumberRuleColumnData() {

	}

	public NumberRuleColumnData(NumberRule numberRule) throws Exception {
		setOid(numberRule.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(numberRule.getName());
		setNumber(numberRule.getNumber());
		setBusinessSector(numberRule.getBusinessSector().getName());
		setDrawingCompany(numberRule.getDrawingCompany().getName());
		setDocument(numberRule.getDocument().getName());
		setDepartment(numberRule.getDepartment().getName());
		setCreator(numberRule.getOwnership().getOwner().getFullName());
		setCreatedDate(numberRule.getCreateTimestamp());

	}
}
