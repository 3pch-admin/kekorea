package e3ps.admin.numberRuleCode.service;

import java.util.HashMap;
import java.util.List;

import e3ps.admin.numberRuleCode.dto.NumberRuleCodeDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface NumberRuleCodeService {

	public abstract void save(HashMap<String, List<NumberRuleCodeDTO>> dataMap) throws Exception;

}
