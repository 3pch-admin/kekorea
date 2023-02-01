package e3ps.epm.numberRule.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardNumberRuleService extends StandardManager implements NumberRuleService {

	public static StandardNumberRuleService newStandardNumberRuleService() throws WTException {
		StandardNumberRuleService instance = new StandardNumberRuleService();
		instance.initialize();
		return instance;
	}
}
