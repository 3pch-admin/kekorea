package e3ps.korea.configSheet.service;

import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.korea.configSheet.beans.ConfigSheetDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface ConfigSheetService {

	/**
	 * CONFIG SHEET 등록
	 */
	public abstract void create(ConfigSheetDTO dto) throws Exception;

}
