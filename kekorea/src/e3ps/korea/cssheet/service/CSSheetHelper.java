package e3ps.korea.cssheet.service;

import wt.services.ServiceFactory;

public class CSSheetHelper {

	public static final CSSheetHelper manager = new CSSheetHelper();
	public static final CSSheetService service = ServiceFactory.getService(CSSheetService.class);
}
