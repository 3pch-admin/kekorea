package e3ps.erp.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardErpService extends StandardManager implements ErpService {

	public static StandardErpService newStandardErpService() throws WTException {
		StandardErpService instance = new StandardErpService();
		instance.initialize();
		return instance;
	}
}