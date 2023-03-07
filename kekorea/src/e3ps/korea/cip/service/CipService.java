package e3ps.korea.cip.service;

import java.util.HashMap;
import java.util.List;

import e3ps.korea.cip.dto.CipDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface CipService {

	public abstract void save(HashMap<String, List<CipDTO>> dataMap) throws Exception;
}
