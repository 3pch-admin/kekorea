package e3ps.epm.numberRule.beans;

import e3ps.epm.numberRule.NumberRule;
import lombok.Getter;
import lombok.Setter;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberRuleColumnData {

	private String oid;

	public NumberRuleColumnData() {

	}

	public NumberRuleColumnData(NumberRule numberRule) throws Exception {
		setOid(numberRule.getPersistInfo().getObjectIdentifier().getStringValue());
	}
}
