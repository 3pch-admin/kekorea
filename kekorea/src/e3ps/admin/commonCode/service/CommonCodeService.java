package e3ps.admin.commonCode.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface CommonCodeService {

	public abstract void create(Map<String, Object> params) throws Exception;

}
