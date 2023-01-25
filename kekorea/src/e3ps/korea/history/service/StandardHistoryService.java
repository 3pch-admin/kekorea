package e3ps.korea.history.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardHistoryService extends StandardManager implements HistoryService {

	public static StandardHistoryService newStandardHistoryService() throws WTException {
		StandardHistoryService instance = new StandardHistoryService();
		instance.initialize();
		return instance;
	}
}
