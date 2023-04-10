package e3ps.admin.specCode.service;

import java.util.HashMap;
import java.util.List;

import e3ps.admin.specCode.dto.SpecCodeDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface SpecCodeService {

	public abstract void save(HashMap<String, List<SpecCodeDTO>> dataMap) throws Exception;

}
