package e3ps.project.issue.service;

import java.util.HashMap;
import java.util.List;

import e3ps.project.issue.beans.IssueDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface IssueService {

	/**
	 * 특이사항 등록
	 */
	public abstract void create(IssueDTO dto) throws Exception;

	/**
	 * 이슈 그리드 저장
	 */
	public abstract void save(HashMap<String, List<IssueDTO>> dataMap) throws Exception;

	/**
	 * 이슈 삭제
	 */
	public abstract void delete(String oid) throws Exception;
}
