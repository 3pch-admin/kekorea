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
}
