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
	public abstract void disconnect(String oid) throws Exception;
}
