package e3ps.admin.sheetvariable.service;

import wt.services.StandardManager;
import wt.util.WTException;

public class StandardSheetVariableService extends StandardManager implements SheetVariableService {

	public static StandardSheetVariableService newStandardSheetVariableService() throws WTException {
		StandardSheetVariableService instance = new StandardSheetVariableService();
		instance.initialize();
		return instance;
	}
}
