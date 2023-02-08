package e3ps.korea.history.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface HistoryService {

	public abstract void create(Map<String, Object> params) throws Exception;

}
