package e3ps.korea.configSheet.service;

import java.util.HashMap;
import java.util.List;

import e3ps.doc.meeting.dto.MeetingDTO;
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

}
