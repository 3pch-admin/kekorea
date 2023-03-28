package e3ps.bom.partlist.service;

import e3ps.bom.partlist.dto.PartListDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface PartlistService {

	public abstract void create(PartListDTO dto) throws Exception;

	public abstract void modify(PartListDTO dto) throws Exception;

	public abstract void delete(String oid) throws Exception;
}
