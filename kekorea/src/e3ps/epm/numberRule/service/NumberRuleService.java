package e3ps.epm.numberRule.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface NumberRuleService {

	public abstract void create(Map<String, Object> params) throws Exception;

	public abstract void revise(Map<String, Object> params) throws Exception;

}
