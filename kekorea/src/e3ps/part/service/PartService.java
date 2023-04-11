package e3ps.part.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface PartService {

	/**
	 * 부품 일괄 등록
	 */
	public abstract Map<String, Object> bundle(Map<String, Object> params) throws Exception;

	/**
	 * 제작사양서 등록
	 */
	public abstract Map<String, Object> spec(Map<String, Object> params) throws Exception;
}
