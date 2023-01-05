package e3ps.migrator;

import java.util.HashMap;
import java.util.Map;

import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface MigratorService {

	public abstract void setProjectState() throws WTException;

	public abstract void setParentTaskType() throws WTException;

	public abstract void setCompleteTask() throws WTException;

	public abstract void setProjectType() throws WTException;

	public abstract void setProjectGateState() throws WTException;

	public abstract void setProjectUser() throws WTException;

	public abstract void setPlanStartDate() throws WTException;

	public abstract void setTaskType() throws WTException;

	public abstract void createSubTask() throws WTException;

	public abstract void setProjectProgress() throws WTException;

	public abstract void createProjectTask(String tName) throws WTException;

	public abstract void deleteNoOutputTask(String tName) throws WTException;

	public abstract void changeStateDoc(HashMap<String, Object> map) throws WTException;

	/**
	 * 프로젝트 정보 설정
	 * 
	 * @throws WTException
	 */
	public abstract void setProjectInfo(HashMap<String, Object> map) throws WTException;

	/**
	 * 체크아웃
	 * 
	 * @throws WTException
	 */
	public abstract void undoCheckout() throws WTException;

	/**
	 * 기존 사용자 부서 이동
	 * 
	 * @throws WTException
	 */
	public abstract void assignToDepartmentByUser(String path) throws WTException;

	/**
	 * 라이프 사이클 변경
	 * 
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> replaceLifeCycleForDoc() throws WTException;

	/**
	 * 라이프 사이클 변경
	 * 
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> replaceLifeCycleForPart() throws WTException;

	/**
	 * 라이프 사이클 변경
	 * 
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> replaceLifeCycleForEpm() throws WTException;

	/**
	 * 뷰 체인지
	 * 
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> replaceToView() throws WTException;

	/**
	 * 부품 만들기
	 * 
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> createPart() throws WTException;

}