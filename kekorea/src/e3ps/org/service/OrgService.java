package e3ps.org.service;

import java.util.Map;

import e3ps.org.Department;
import e3ps.org.People;
import wt.method.RemoteInterface;
import wt.org.WTUser;
import wt.util.WTException;

@RemoteInterface
public interface OrgService {

	/**
	 * 최상위 부서 생성
	 * 
	 * @return Department
	 * @throws WTException
	 */
	public abstract Department makeRoot() throws WTException;

	/**
	 * 서버 실행시 유저 검색하여 People 객체 생성 및 수정
	 * 
	 * @param department
	 * @throws WTException
	 */
	public abstract void inspectUser(Department department) throws WTException;

	/**
	 * WTUser로 People 객체 생성
	 * 
	 * @param sessionUser
	 * @return User
	 * @throws WTException
	 */
	public abstract People createUser(WTUser sessionUser) throws WTException;

	/**
	 * 비밀번호 변경
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> changePasswordAction(Map<String, Object> param) throws WTException;

	/**
	 * 비밀번호 초기화
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> initPasswordAction(Map<String, Object> param) throws WTException;;

	/**
	 * 개인 결재선 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> saveUserLineAction(Map<String, Object> param) throws WTException;

	/**
	 * 개인 결재선 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteUserLineAction(Map<String, Object> param) throws WTException;

	/**
	 * 퇴사 처리
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> setResignAction(Map<String, Object> param) throws WTException;

	/**
	 * 사용자 추가
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> addUserAction(Map<String, Object> param) throws WTException;

	/**
	 * 사용자 정보 수정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> modifyUserAction(Map<String, Object> param) throws WTException;

	/**
	 * 직급 설정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> setDutyAction(Map<String, Object> param) throws WTException;

	/**
	 * 부서 설정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> setDeptAction(Map<String, Object> param) throws WTException;

	/**
	 * WTUser 생성시 호출 되는 이벤트
	 * 
	 * @param wtuser
	 * @throws Exception
	 */
	public abstract void save(WTUser wtuser) throws Exception;

	/**
	 * WTUser 수정시 호출 되는 이벤트
	 * 
	 * @param wtuser
	 * @throws Exception
	 */
	public abstract void modify(WTUser wtuser) throws Exception;
}
