package e3ps.admin.numberRuleCode.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardNumberRuleCodeService extends StandardManager implements NumberRuleCodeService {

	public static StandardNumberRuleCodeService newStandardNumberRuleCodeService() throws WTException {
		StandardNumberRuleCodeService instance = new StandardNumberRuleCodeService();
		instance.initialize();
		return instance;
	}
}
