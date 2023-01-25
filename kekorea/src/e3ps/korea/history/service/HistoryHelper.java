package e3ps.korea.history.service;

import java.util.Map;

import org.json.JSONArray;

import wt.services.ServiceFactory;

public class HistoryHelper {

	public static final HistoryHelper manager = new HistoryHelper();
	public static final HistoryService service = ServiceFactory.getService(HistoryService.class);

	public Map<String, Object> list(Map<String, Object> params) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
