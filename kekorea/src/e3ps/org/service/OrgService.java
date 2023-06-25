package e3ps.org.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.org.Department;
import e3ps.org.People;
import e3ps.org.dto.UserDTO;
import wt.method.RemoteInterface;
import wt.org.WTUser;
import wt.util.WTException;

@RemoteInterface
public interface OrgService {

	/**
	 * 서버 시작이 유저 검색후 PEOPLE 객체 생성
	 */
	public abstract void inspectUser(Department department) throws WTException;

	public abstract People createUser(WTUser sessionUser) throws WTException;

	/**
	 * 사용자 정보 저장 그리드 용
	 */
	public abstract void save(HashMap<String, List<UserDTO>> dataMap) throws Exception;

	/**
	 * OOTB WTUSER 생성시 이벤트로 PEOPLE 객체 생성
	 */
	public abstract void save(WTUser wtUser) throws Exception;

	/**
	 * OOTB WTUSER 수정시 이벤트로 PEOPLE 객체 수정
	 */
	public abstract void modify(WTUser wtUser) throws Exception;

	/**
	 * 사용자 정보 수정 함수
	 */
	public abstract void modify(UserDTO dto) throws Exception;

	/**
	 * 사용자 퇴사 처리 함수
	 */
	public abstract void fire(String oid, boolean isFire) throws Exception;

	/**
	 * 사용자 비밀번호 변경 함수
	 */
	public abstract void init(Map<String, String> params) throws Exception;

	/**
	 * 사용자 비밀번호 초기화 함수
	 */
	public abstract void password(Map<String, String> params) throws Exception;
}
