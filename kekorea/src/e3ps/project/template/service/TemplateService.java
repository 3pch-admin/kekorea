package e3ps.project.template.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface TemplateService {

	/**
	 * 템플릿 생성
	 */
	public abstract void create(Map<String, Object> params) throws Exception;

	/**
	 * 템플릿 트리 저장
	 */
	public abstract void treeSave(Map<String, Object> params) throws Exception;

	/**
	 * 템플릿 수정
	 */
	public abstract void modify(Map<String, Object> params) throws Exception;

}
