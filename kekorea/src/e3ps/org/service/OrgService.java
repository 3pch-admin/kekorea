package e3ps.org.service;

import java.util.Map;

import e3ps.org.Department;
import e3ps.org.People;
import wt.method.RemoteInterface;
import wt.org.WTUser;
import wt.util.WTException;

@RemoteInterface
public interface OrgService {

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
}
