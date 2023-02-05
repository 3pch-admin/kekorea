package e3ps.common.content.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface CommonContentService {

	/**
	 * 첨부 파일 업로드 서버
	 * 
	 * @param request
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> uploadContent(HttpServletRequest request) throws WTException;

	/**
	 * 첨부 파일 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteContent(Map<String, Object> param) throws WTException;

	/**
	 * 첨부 파일 가져오기
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract List<Map<String, Object>> getPrimaryContent(Map<String, Object> param) throws WTException;

	/**
	 * 주 첨부 파일 가져오기
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract List<Map<String, Object>> getSecondaryContent(Map<String, Object> param) throws WTException;

	/**
	 * 첨부 파일 링크 생성
	 * 
	 * @param param
	 * @throws WTException
	 */
	public abstract void createContents(Map<String, Object> param) throws WTException;

	/**
	 * 첨부 파일 링크 삭제
	 * 
	 * 
	 * @param param
	 * @throws WTException
	 */
	public abstract void deleteContents(Map<String, Object> param) throws WTException;

	/**
	 * 멀티 다운로드
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> contentsMultiDown(Map<String, Object> param) throws WTException;

	/**
	 * 일반 다운로드
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> contentsDown(Map<String, Object> param) throws WTException;

	/**
	 * 전체
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> downSecondary(Map<String, Object> param) throws WTException;

	/**
	 * 주 첨부
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> downPrimary(Map<String, Object> param) throws WTException;

	/**
	 * 첨부
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> downContentAll(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> upload(HttpServletRequest request) throws Exception;

	public abstract Map<String, Object> auiPreview(HttpServletRequest request) throws Exception;

	public abstract Map<String, Object> auiUpload(HttpServletRequest request) throws Exception;

}
