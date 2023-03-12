package e3ps.korea.history.service;

import java.util.ArrayList;
import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface HistoryService {

	public abstract void save(Map<String, ArrayList<Map<String, String>>> params) throws Exception;

}
