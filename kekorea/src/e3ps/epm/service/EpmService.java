package e3ps.epm.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface EpmService {
	/**
	 * 도면 결재
	 */
	public abstract void register(Map<String, Object> params) throws Exception;

}
