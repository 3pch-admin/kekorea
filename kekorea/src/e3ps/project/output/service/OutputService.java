package e3ps.project.output.service;

import java.util.Map;

import e3ps.project.output.dto.OutputDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface OutputService {

	/**
	 * 산출물 생성
	 */
	public abstract void create(OutputDTO dto) throws Exception;

	/**
	 * 산출물 연결 - 태스크
	 */
	public Map<String, Object> connect(Map<String, Object> params) throws Exception;

	/**
	 * 산출물 직접적인 삭제
	 */
	public abstract void delete(String oid) throws Exception;
}
