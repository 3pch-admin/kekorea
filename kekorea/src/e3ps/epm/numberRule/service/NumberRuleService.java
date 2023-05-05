package e3ps.epm.numberRule.service;

import java.util.HashMap;
import java.util.List;

import e3ps.epm.numberRule.dto.NumberRuleDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface NumberRuleService {

	/**
	 * KEK 도번 등록
	 */
	public abstract void save(HashMap<String, List<NumberRuleDTO>> dataMap) throws Exception;

	/**
	 * KEK 도번 개정
	 */
	public abstract void revise(HashMap<String, List<NumberRuleDTO>> dataMap) throws Exception;

}
