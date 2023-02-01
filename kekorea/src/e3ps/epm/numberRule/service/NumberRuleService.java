package e3ps.epm.numberRule.service;

import java.util.Map;

import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface NumberRuleService {

	public abstract void create(Map<String, Object> params) throws WTException;

}
