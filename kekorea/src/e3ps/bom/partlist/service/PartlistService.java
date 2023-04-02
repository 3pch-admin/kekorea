package e3ps.bom.partlist.service;

import e3ps.bom.partlist.dto.PartListDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface PartlistService {

	/**
	 * 수배표 생성
	 */
	public abstract void create(PartListDTO dto) throws Exception;

	/**
	 * 수배표 수정
	 */
	public abstract void modify(PartListDTO dto) throws Exception;

	/**
	 * 수배표 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * 수배표 태스크 연결 제거
	 */
	public abstract void disconnect(String oid) throws Exception;
}
