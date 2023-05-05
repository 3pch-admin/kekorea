package e3ps.bom.tbom.service;

import java.util.Map;

import e3ps.bom.tbom.dto.TBOMDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface TBOMService {

	/**
	 * T-BOM 생성
	 */
	public abstract void create(TBOMDTO dto) throws Exception;

	/**
	 * T-BOM 그리드 저장
	 */
	public abstract void save(Map<String, Object> params) throws Exception;

	/**
	 * T-BOM 태스크 연결 제거
	 */
	public abstract void disconnect(Map<String, Object> params) throws Exception;

	/**
	 * T-BOM 수정
	 */
	public abstract void modify(TBOMDTO dto) throws Exception;

	/**
	 * T-BOM 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * T-BOM 개정
	 */
	public abstract void revise(TBOMDTO dto) throws Exception;

	/**
	 * T-BOM 산출물에 연결
	 */
	public abstract Map<String, Object> connect(Map<String, Object> params) throws Exception;
}
