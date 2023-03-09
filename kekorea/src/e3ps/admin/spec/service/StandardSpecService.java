package e3ps.admin.spec.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardSpecService extends StandardManager implements SpecService {

	public static StandardSpecService newStandardSpecService() throws WTException {
		StandardSpecService instance = new StandardSpecService();
		instance.initialize();
		return instance;
	}
}