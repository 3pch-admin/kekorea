package e3ps.admin.numberRuleCode.service;

import wt.services.ServiceFactory;

public class NumberRuleCodeHelper {

	public static final NumberRuleCodeHelper manager = new NumberRuleCodeHelper();
	public static final NumberRuleCodeService service = ServiceFactory.getService(NumberRuleCodeService.class);
}
