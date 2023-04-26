package e3ps.doc.service;

import java.util.Map;

import e3ps.doc.dto.DocumentDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface DocumentService {

	/**
	 * 문서 결재
	 */
	public abstract void register(Map<String, Object> params) throws Exception;

	/**
	 * 문서 등록
	 */
	public abstract void create(DocumentDTO dto) throws Exception;

	/**
	 * 문서 삭제
	 */
	public abstract void delete(String oid) throws Exception;

}
