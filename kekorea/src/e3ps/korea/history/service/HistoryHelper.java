package e3ps.korea.history.service;

import wt.services.ServiceFactory;

public class HistoryHelper {

	public static final HistoryHelper manager = new HistoryHelper();
	public static final HistoryService service = ServiceFactory.getService(HistoryService.class);
}
