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
	 * @return Department
	 * @throws WTException
	 */
	public abstract Department makeRoot() throws WTException;

	/**
	 * @param department
	 * @throws WTException
	 */
	public abstract void inspectUser(Department department) throws WTException;

	/**
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
	 * 사용자 개인 테이블 컬럼 셋 저장
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> saveUserTableSet(Map<String, Object> param) throws WTException;

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
	 * 개인 테이블 넓이 저장
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> saveUserTableStyle(Map<String, Object> param) throws WTException;

	/**
	 * 개인 목록 리스트 수 저장
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> saveUserPaging(Map<String, Object> param) throws WTException;

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
	 * 개인 테이블 순서
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> saveUserTableIndexs(Map<String, Object> param) throws WTException;

}
