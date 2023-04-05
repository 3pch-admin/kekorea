package e3ps.project.service;

import java.util.Map;

import e3ps.project.Project;
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
}
