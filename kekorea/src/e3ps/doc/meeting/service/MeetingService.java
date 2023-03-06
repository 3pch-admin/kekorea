package e3ps.doc.meeting.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.doc.meeting.beans.MeetingDTO;
import e3ps.doc.meeting.beans.MeetingTemplateDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface MeetingService {

	/**
	 * 회의록 템플릿 등록 함수
	 * 
	 * @param dto : 등록 페이지에서의 값들을 담아올 DTO 객체
	 * @throws Exception
	 */
	public abstract void format(MeetingTemplateDTO dto) throws Exception;

	/**
	 * 회의록 등록 함수
	 * 
	 * @param params :
	 * @throws Exception
	 */
	public abstract void create(MeetingDTO dto) throws Exception;

	/**
	 * 회의록 템플릿 그리드 상에서의 데이터 삭제
	 * 
	 * @param dataMap : 그리드에서 삭제할 데이터
	 * @throws Exception
	 */
	public abstract void save(HashMap<String, List<MeetingTemplateDTO>> dataMap) throws Exception;

	/**
	 * 회의록 그리드 상에서의 데이터 삭제
	 * 
	 * @param dataMap : 그리드에서 삭제할 데이터
	 * @throws Exception
	 */
	public abstract void delete(HashMap<String, List<MeetingDTO>> dataMap) throws Exception;
}
