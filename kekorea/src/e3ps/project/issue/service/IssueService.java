package e3ps.project.issue.service;

import java.util.HashMap;
import java.util.List;

import e3ps.workspace.notice.dto.NoticeDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface IssueService {

	/**
	 * 특이사항 등록
	 */
	public abstract void create(NoticeDTO dto) throws Exception;

	/**
	 * 특이사항 삭제 그리드용
	 */
	public abstract void delete(HashMap<String, List<NoticeDTO>> dataMap) throws Exception;
}
