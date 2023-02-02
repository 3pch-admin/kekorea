package e3ps.korea.cip.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface CipService {

	public abstract void create(Map<String, Object> params) throws Exception;
//	public abstract Map<String, Object> create(Map<String, Object> params) throws Exception;

//	public abstract void list(Map<String, Object> params) throws Exception;
}
