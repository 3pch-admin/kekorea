package e3ps.admin.sheetvariable.service;

import java.util.HashMap;
import java.util.List;

import e3ps.admin.commonCode.dto.CommonCodeDTO;
import wt.services.StandardManager;
import wt.util.WTException;

public class StandardSheetVariableService extends StandardManager implements SheetVariableService {

	public static StandardSheetVariableService newStandardSheetVariableService() throws WTException {
		StandardSheetVariableService instance = new StandardSheetVariableService();
		instance.initialize();
		return instance;
	}

	@Override
	public void save(HashMap<String, List<CommonCodeDTO>> dataMap) throws Exception {

	
	}
}
