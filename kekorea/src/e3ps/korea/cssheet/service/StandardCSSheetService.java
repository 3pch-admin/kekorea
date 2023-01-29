package e3ps.korea.cssheet.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardCSSheetService extends StandardManager implements CSSheetService {

	public static StandardCSSheetService newStandardCSSheetService() throws WTException {
		StandardCSSheetService instance = new StandardCSSheetService();
		instance.initialize();
		return instance;
	}
}
