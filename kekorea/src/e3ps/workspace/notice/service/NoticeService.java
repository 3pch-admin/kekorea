package e3ps.workspace.notice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.workspace.notice.dto.NoticeDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface NoticeService {

	/**
	 * AUIGrid 데이터 저장
	 */
	public abstract void save(Map<String, Object> params) throws Exception;

	/**
	 * 공지사항 등록
	 */
	public abstract void create(NoticeDTO dto) throws Exception;

	/**
	 * 공지사항 삭제 그리드용
	 */
	public abstract void delete(HashMap<String, List<NoticeDTO>> dataMap) throws Exception;

	/**
	 * 공지사항 수정
	 */
	public abstract void modify(NoticeDTO dto) throws Exception;

}
