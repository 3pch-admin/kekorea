package e3ps.project.output.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardOutputService extends StandardManager implements OutputService {

	public static StandardOutputService newStandardOutputService() throws WTException {
		StandardOutputService instance = new StandardOutputService();
		instance.initialize();
		return instance;
	}
}
