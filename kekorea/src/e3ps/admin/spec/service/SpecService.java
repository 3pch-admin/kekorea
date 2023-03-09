package e3ps.admin.spec.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.admin.commonCode.dto.CommonCodeDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface SpecService {

	public abstract void save(HashMap<String, List<CommonCodeDTO>> dataMap) throws Exception;

}
