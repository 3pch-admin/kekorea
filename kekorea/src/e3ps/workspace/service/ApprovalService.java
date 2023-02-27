package e3ps.workspace.service;

import java.util.Map;

import e3ps.workspace.ApprovalMaster;
import wt.fc.Persistable;
import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface ApprovalService {
	
	/**
	 * 결재라인 초기화
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> initApprovalLineAction(Map<String, Object> param) throws WTException;

	/**
	 * 결재마스터 초기화
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> initApprovalAction(Map<String, Object> param) throws WTException;
	/**
	 * 자가 결재
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract void selfApproval(Persistable per) throws WTException;

	/**
	 * 회수
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> recoveryAction(Map<String, Object> param) throws WTException;

	/**
	 * 부재중 처리
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> setAbsenceAction(Map<String, Object> param) throws WTException;

	/**
	 * 첫 결재
	 * 
	 * @param per
	 * @param param
	 * @throws WTException
	 */
	public abstract void submitApp(Persistable per, Map<String, Object> param) throws WTException;

	/**
	 * 승인
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> approvalAction(Map<String, Object> param) throws WTException;

	/**
	 * 반려
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> returnAction(Map<String, Object> param) throws WTException;

	/**
	 * 수신
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> receiveAction(Map<String, Object> param) throws WTException;

	/**
	 * 불합의
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> unagreeAction(Map<String, Object> param) throws WTException;

	/**
	 * 합의
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> agreeAction(Map<String, Object> param) throws WTException;

	/**
	 * 객체 상태값 변경
	 * 
	 * @param per
	 * @throws WTException
	 */
	public void approvalPersist(Persistable per) throws WTException;

	/**
	 * 모든 결재 라인들 삭제
	 * 
	 * @param per
	 * @throws WTException
	 */
	public abstract void deleteAllLine(Persistable per) throws WTException;

	/**
	 * 모든 결재 라인들 삭제
	 * 
	 * @param per
	 * @throws WTException
	 */
	public abstract void deleteAllLine(ApprovalMaster master) throws WTException;

	/**
	 * 결재 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteLines(Map<String, Object> param) throws WTException;

	/**
	 * 결재 스킵
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> skipApproval(Map<String, Object> param) throws WTException;

	/**
	 * 결재 스킵
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> reassignApproval(Map<String, Object> param) throws WTException;
	
	
	/**
	 * 반려함에서 반려 삭제 처리
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteReturnAction(Map<String, Object> param) throws WTException;
}
