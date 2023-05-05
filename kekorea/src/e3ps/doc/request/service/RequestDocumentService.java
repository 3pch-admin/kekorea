package e3ps.doc.request.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.doc.request.dto.RequestDocumentDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface RequestDocumentService {

	/**
	 * 의뢰서 그리드 저장
	 */
	public abstract void save(HashMap<String, List<RequestDocumentDTO>> dataMap) throws Exception;

	/**
	 * 의뢰서 등록
	 */
	public abstract void create(RequestDocumentDTO dto) throws Exception;

	/**
	 * 의뢰서 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * 태스크 의뢰서 연결 제거
	 */
	public abstract void disconnect(String oid) throws Exception;

	/**
	 * 태스크 의뢰서 연결
	 */
	public abstract Map<String, Object> connect(Map<String, Object> params) throws Exception;

}
