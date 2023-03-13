package e3ps.workspace.notice.service;

import java.util.Map;

import e3ps.workspace.notice.dto.NoticeDTO;

public interface NoticeService {

	/**
	 * AUIGrid 데이터 저장
	 * 
	 * @param params : AUIGrid 데이터
	 * @throws Exception
	 */
	public abstract void save(Map<String, Object> params) throws Exception;

	/**
	 * 공지사항 등록
	 * 
	 * @param dto : 공지사항 등록 변수 객체
	 * @throws Exception
	 */
	public abstract void create(NoticeDTO dto) throws Exception;
}
