package e3ps.bom.tbom.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface TBOMService {

	public abstract void create(Map<String, Object> params) throws Exception;

	public abstract void save(Map<String, Object> params) throws Exception;
}
