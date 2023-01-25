package e3ps.admin.service;

import java.util.Map;

import e3ps.admin.commonCode.CommonCode;
import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface AdminService {

	
	public abstract Map<String, Object> changePasswordSetting(Map<String, Object> param) throws WTException;
	
	/**
	 * 접속 이력
	 * 
	 * @param ip
	 * @throws WTException
	 */
	public abstract void loginHistoryAction(String id, String ip) throws WTException;

	/**
	 * 코드 타입 초기화
	 * 
	 * @throws WTException
	 */
	public abstract void init() throws WTException;

	/**
	 * 루트 코드 생성
	 * 
	 * @param codeType
	 * @return Code
	 * @throws WTException
	 */
	public abstract CommonCode makeRoot(String codeType) throws WTException;

	/**
	 * 코드 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteCodeAction(Map<String, Object> param) throws WTException;

	/**
	 * 코드 생성
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createCodeAction(Map<String, Object> param) throws WTException;

	/**
	 * 접속 이력 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deteleLoginHistory(Map<String, Object> param) throws WTException;

}
