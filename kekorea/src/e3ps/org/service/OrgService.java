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
	 * AUIGrid 리스트 저장
	 * 
	 * @param params : AUIGrid 데이터
	 * @throws Exception
	 */
	public abstract void save(Map<String, Object> params) throws Exception;

	public abstract void inspectUser(Department department) throws WTException;

	public abstract People createUser(WTUser sessionUser) throws WTException;

	public abstract Map<String, Object> changePasswordAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> initPasswordAction(Map<String, Object> param) throws WTException;;

	public abstract Map<String, Object> saveUserLineAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> deleteUserLineAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> setResignAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> addUserAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> modifyUserAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> setDutyAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> setDeptAction(Map<String, Object> param) throws WTException;

	public abstract void save(WTUser wtuser) throws Exception;

	public abstract void modify(WTUser wtuser) throws Exception;

	/**
	 * 사용자 정보 저장 그리드 용
	 * 
	 * @param dataMap : 사용자 정보를 담는 변수
	 * @throws Exception
	 */
	public abstract void save(HashMap<String, List<UserDTO>> dataMap) throws Exception;
}
