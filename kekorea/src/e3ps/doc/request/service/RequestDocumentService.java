package e3ps.doc.request.service;

import java.util.HashMap;
import java.util.List;

import e3ps.doc.request.dto.RequestDocumentDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface RequestDocumentService {

	public abstract void save(HashMap<String, List<RequestDocumentDTO>> dataMap) throws Exception;

	public abstract void create(RequestDocumentDTO dto) throws Exception;

}
