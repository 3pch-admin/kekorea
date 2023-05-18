package e3ps.system.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardErrorLogService extends StandardManager implements ErrorLogService {

	public static StandardErrorLogService newStandardErrorLogService() throws WTException {
		StandardErrorLogService instance = new StandardErrorLogService();
		instance.initialize();
		return instance;
	}
}
