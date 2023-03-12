package e3ps.admin.sheetvariable.service;

import java.util.HashMap;
import java.util.List;

import e3ps.admin.commonCode.dto.CommonCodeDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface SheetVariableService {

	public abstract void save(HashMap<String, List<CommonCodeDTO>> dataMap) throws Exception;

}
