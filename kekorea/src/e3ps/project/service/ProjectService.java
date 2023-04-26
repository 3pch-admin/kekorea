package e3ps.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import e3ps.project.Project;
import e3ps.project.dto.ProjectDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface ProjectService {

	/**
	 * 작번 생성
	 */
	public abstract void create(Map<String, Object> params) throws Exception;

	/**
	 * 프로젝트 태스크 트리 저장
	 */
	public abstract void treeSave(Map<String, Object> params) throws Exception;

	/**
	 * 프로젝트 진행율 계산
	 */
	public abstract void calculation(Project project) throws Exception;

	/**
	 * 프로젝트 진행율 및 일정 전체 조정
	 */
	public abstract void commit(Project project) throws Exception;

	/**
	 * 프로젝트 그리드에서 저장 - 담당자, 돈
	 */
	public abstract void save(HashMap<String, List<ProjectDTO>> dataMap) throws Exception;

	/**
	 * 프로젝트 담당자 수정
	 */
	public abstract void editUser(Map<String, Object> params) throws Exception;

	/**
	 * 프로젝트 금액 수정
	 */
	public abstract void money(Map<String, Object> params) throws Exception;

	/**
	 * 프로젝트 수정
	 */
	public abstract void modify(Map<String, Object> params) throws Exception;

	/**
	 * 프로젝트 삭제
	 */
	public abstract void delete(String oid) throws Exception;
}
