package e3ps.admin.spec.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface SpecService {

	public abstract void create(Map<String, Object> params) throws Exception;

}
