package e3ps.epm.numberRule.service;

import java.util.Map;

import wt.services.ServiceFactory;

public class NumberRuleHelper {

	public static final NumberRuleHelper manager = new NumberRuleHelper();
	public static final NumberRuleService service = ServiceFactory.getService(NumberRuleService.class);
	
	public Map<String, Object> list(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}
}
