package e3ps.admin.spec.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface OptionsService {

	public abstract void create(Map<String, Object> params) throws Exception;

}
