package e3ps.load.service;

import java.util.HashMap;

import e3ps.project.Output;
import e3ps.project.Project;
import e3ps.project.Task;
import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface LoadService {

	public abstract Task loadTaskFromExcel(HashMap<String, Object> map, Project project) throws WTException;

	public abstract Project loadProjectFromExcel(HashMap<String, Object> map) throws WTException;

	/**
	 * 부서 생성
	 * 
	 * @param map
	 * @return boolean
	 * @throws WTException
	 */
	public abstract boolean loadDepartmentFromExcel(HashMap<String, Object> map) throws WTException;

	/**
	 * 사용자 로더
	 * 
	 * @param map
	 * @return
	 * @throws WTException
	 */
	public abstract boolean loadUserFromExcel(HashMap<String, Object> map) throws WTException;

	/**
	 * 코드 로더
	 * 
	 * @param map
	 * @return
	 * @throws WTException
	 */
	public abstract boolean loadCommonCodeFromExcel(HashMap<String, Object> map) throws WTException;

	/**
	 * YCODE
	 * 
	 * @param map
	 * @throws WTException
	 */
	public abstract void setYCode(HashMap<String, Object> map) throws WTException;

	/**
	 * 
	 * @param task
	 * @param map
	 * @return
	 */
	public abstract Task setPlanTaskDate(Task task, HashMap<String, Object> map) throws WTException;

	public abstract Output loadOutputFromExcel(HashMap<String, Object> map, Project projects) throws WTException;

	public abstract Task setOutput(Task task, Output output, String oid) throws WTException;
}
