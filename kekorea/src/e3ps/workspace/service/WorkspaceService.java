package e3ps.workspace.service;

import java.util.ArrayList;
import java.util.Map;

import wt.fc.Persistable;
import wt.method.RemoteInterface;

@RemoteInterface
public interface WorkspaceService {

	/**
	 * 결재등록
	 */
	public abstract void register(Persistable persistable, ArrayList<Map<String, String>> agreeRows,
			ArrayList<Map<String, String>> approvalRows, ArrayList<Map<String, String>> receiveRows) throws Exception;

	/**
	 * 검토 완료
	 */
	public abstract void _agree(Map<String, Object> params) throws Exception;

	/**
	 * 검토 반려
	 */
	public abstract void _unagree(Map<String, Object> params) throws Exception;

	/**
	 * 결재 승인
	 */
	public abstract void _approval(Map<String, Object> params) throws Exception;

	/**
	 * 결재 반려
	 */
	public abstract void _reject(Map<String, Object> params) throws Exception;

	/**
	 * 수신 확인
	 */
	public abstract void _receive(Map<String, Object> params) throws Exception;
}
