package e3ps.bom.tbom.service;

import java.util.Map;

import e3ps.bom.tbom.dto.TBOMDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface TBOMService {

	public abstract void create(TBOMDTO dto) throws Exception;

	public abstract void save(Map<String, Object> params) throws Exception;
}
