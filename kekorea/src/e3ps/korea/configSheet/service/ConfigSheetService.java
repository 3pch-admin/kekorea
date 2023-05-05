package e3ps.korea.configSheet.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.korea.configSheet.beans.ConfigSheetDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface ConfigSheetService {

	/**
	 * CONFIG SHEET 등록
	 */
	public abstract void create(ConfigSheetDTO dto) throws Exception;

	/**
	 * CONFIG SHEET 그리드 저장
	 */
	public abstract void save(HashMap<String, List<ConfigSheetDTO>> dataMap) throws Exception;

	/**
	 * CONFIG SHEET 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * CONFIG SHEET 산출물 연결
	 */
	public abstract Map<String, Object> connect(Map<String, Object> params) throws Exception;
	
	/**
	 * 태스크 CONFIG SHEET 연결 제거
	 */
	public abstract void disconnect(String oid) throws Exception;
}
