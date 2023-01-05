package e3ps.project.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import e3ps.project.Task;
import e3ps.project.Template;
import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface TemplateService {

	public abstract Map<String, Object> onSaveTemplate(Map<String, Object> param) throws WTException;

	/**
	 * 템플릿 가져오기
	 * 
	 * @param param
	 * @return ArrayList<Template>
	 */
	public abstract ArrayList<Template> getTemplate() throws WTException;

	/**
	 * 템플릿 추가
	 * 
	 * @param param
	 * @return Map<String, Object>
	 */
	public abstract Map<String, Object> addTemplateAction(Map<String, Object> param) throws WTException;

	/**
	 * 템플릿 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createTemplateAction(Map<String, Object> param) throws WTException;

	/**
	 * 템플릿 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> deleteTemplateAction(Map<String, Object> param) throws WTException;

	/**
	 * 템플릿 수정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> modifyTemplateAction(Map<String, Object> param) throws WTException;

	/**
	 * 템플릿 태스크 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onSaveTaskAction(Map<String, Object> param) throws WTException;

	/**
	 * 템플릿 태스크 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onSaveTemplateTaskAction(Map<String, Object> param) throws WTException;

	/**
	 * 인라인 수정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onSaveAction(Map<String, Object> param) throws WTException;

	/**
	 * 기간 계산..
	 * 
	 * @param template
	 * @throws WTException
	 */
	public abstract void commit(Template template) throws WTException;

	/**
	 * 기간 다시 계산
	 * 
	 * @param planStartDate
	 * @param list
	 * @throws WTException
	 */
	public abstract void initAllTemplatePlanDate(Timestamp planStartDate, ArrayList<Task> list) throws WTException;

	/**
	 * 템플릿 기간
	 * 
	 * @param template
	 * @throws WTException
	 */
	public abstract void setTemplateDuration(Template template) throws WTException;

	/**
	 * 상위 태스크 일정 편집
	 * 
	 * @param list
	 * @throws WTException
	 */
	public abstract void setTemplateParentDate(ArrayList<Task> list) throws WTException;

	/**
	 * 태스크 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onDeleteTaskAction(Map<String, Object> param) throws WTException;

	/**
	 * 태스크 이동
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onMoveTaskAction(Map<String, Object> param) throws WTException;

	/**
	 * 선후행 만들기
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onBeforeLinkAddAction(Map<String, Object> param) throws WTException;

	/**
	 * 선 후행 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onAfterLinkDeleteAction(Map<String, Object> param) throws WTException;

	/**
	 * 태스크 일정 드래그
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onAfterTaskResizeAction(Map<String, Object> param) throws WTException;

	/**
	 * 선후행 일정 정리
	 * 
	 * @param list
	 * @throws WTException
	 */
	public void setDependencyTask(ArrayList<Task> list) throws WTException;

	/**
	 * 템플릿 복사
	 */
	public void copyTasksInfo(Template org, Template copy) throws WTException;

	/**
	 * 템플릿 정보 복사
	 * 
	 * @param org
	 * @param copy
	 * @throws WTException
	 */
	public void copyTemplateInfo(Template org, Template copy) throws WTException;

	/**
	 * 태스크 이동
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> onAfterTaskMoveAction(Map<String, Object> param) throws WTException;

	/**
	 * 선후행 수정
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> onAfterLinkUpdateAction(Map<String, Object> param) throws WTException;
}
