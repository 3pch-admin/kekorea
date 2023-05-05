package e3ps.doc.meeting.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.doc.meeting.dto.MeetingTemplateDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface MeetingService {

	/**
	 * 회의록 템플릿 등록 함수
	 */
	public abstract void format(MeetingTemplateDTO dto) throws Exception;

	/**
	 * 회의록 등록 함수
	 */
	public abstract void create(MeetingDTO dto) throws Exception;

	/**
	 * 회의록 템플릿 그리드 상에서의 데이터 삭제
	 */
	public abstract void save(HashMap<String, List<MeetingTemplateDTO>> dataMap) throws Exception;

	/**
	 * 회의록 그리드 상에서의 데이터 삭제
	 */
	public abstract void delete(HashMap<String, List<MeetingDTO>> dataMap) throws Exception;

	/**
	 * 회의록 템플릿 수정
	 */
	public abstract void modify(MeetingTemplateDTO dto) throws Exception;

	/**
	 * 회의록 수정
	 */
	public abstract void update(MeetingDTO dto) throws Exception;

	/**
	 * 회의록 산출물 연결
	 */
	public abstract Map<String, Object> connect(Map<String, Object> params) throws Exception;

	/**
	 * 태스크 회의록 연결 제거
	 */
	public abstract void disconnect(String oid) throws Exception;
}
