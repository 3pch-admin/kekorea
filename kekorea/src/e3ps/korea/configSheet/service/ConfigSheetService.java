package e3ps.korea.configSheet.service;

import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.korea.configSheet.ConfigSheetDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface ConfigSheetService {

	void create(ConfigSheetDTO dto) throws Exception;

}
