package e3ps.project.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

import e3ps.doc.request.RequestDocument;
import e3ps.project.Project;
import e3ps.project.task.Task;
import e3ps.project.template.Template;
import wt.doc.WTDocument;
import wt.method.RemoteInterface;
import wt.util.WTException;

@RemoteInterface
public interface ProjectService {

	public abstract void setParentProgressSet(ArrayList<Task> list) throws WTException;

	public abstract void test() throws WTException;

	public abstract void setStartDate(Project project) throws WTException;

	public abstract void setQState(Project project) throws WTException;

	public abstract Map<String, Object> deleteProjectAction(Map<String, Object> param) throws WTException;

	public void startScheduler() throws WTException;

	/**
	 * 선후행 수정
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> onAfterLinkUpdateAction(Map<String, Object> param) throws WTException;

	/**
	 * 태스크 일정 드래그
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onAfterTaskResizeAction(Map<String, Object> param) throws WTException;

	/**
	 * 태스크 이동
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> onAfterTaskMoveAction(Map<String, Object> param) throws WTException;

	/**
	 * 선후행 만들기
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onBeforeLinkAddAction(Map<String, Object> param) throws WTException;

	/**
	 * 태스크 이동
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onMoveTaskAction(Map<String, Object> param) throws WTException;

	/**
	 * 선 후행 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onAfterLinkDeleteAction(Map<String, Object> param) throws WTException;

	/**
	 * 태스크 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onDeleteTaskAction(Map<String, Object> param) throws WTException;

	/**
	 * 인라인 수정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> onSaveAction(Map<String, Object> param) throws WTException;

	/**
	 * 완료 체크
	 * 
	 * @param project
	 * @throws WTException
	 */
	public void setProgressCheck(Project project) throws WTException;

	/**
	 * 선후행 일정 정리
	 * 
	 * @param list
	 * @throws WTException
	 */
	public void setDependencyTask(ArrayList<Task> list) throws WTException;

	/**
	 * 기간 다시 계산
	 * 
	 * @param planStartDate
	 * @param list
	 * @throws WTException
	 */
	public abstract void initAllProjectPlanDate(Timestamp planStartDate, ArrayList<Task> list) throws WTException;

	/**
	 * 프로젝트 기간
	 * 
	 * @param project
	 * @throws WTException
	 */
	public abstract void setProjectDuration(Project project) throws WTException;

	/**
	 * 상위 태스크 일정 편집
	 * 
	 * @param list
	 * @throws WTException
	 */
	public abstract void setProjectParentDate(ArrayList<Task> list) throws WTException;


	/**
	 * 작번 수정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> modifyProjectAction(Map<String, Object> param) throws WTException;

	/**
	 * 작번 가격 수정
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> modifyProjectPriceAction(Map<String, Object> param) throws WTException;

	/**
	 * 프로젝트 재시작
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> restartProjectAction(Map<String, Object> param) throws WTException;

	/**
	 * 프로젝트중단
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> stopProjectAction(Map<String, Object> param) throws WTException;

	/**
	 * 프로젝트 완료
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> completeProjectAction(Map<String, Object> param) throws WTException;

	/**
	 * 프로젝트 시작
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> startProjectAction(Map<String, Object> param) throws WTException;

	/**
	 * 산출물 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 */
	public abstract Map<String, Object> delOutputAction(Map<String, Object> param) throws WTException;

	/**
	 * 산출물 추가
	 * 
	 * @param param
	 * @return Map<String, Object>
	 */
	public abstract Map<String, Object> addOutputAction(Map<String, Object> param) throws WTException;

	/**
	 * 작번 추가
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> addProjectAction(Map<String, Object> param) throws WTException;


	/**
	 * 템플릿 태스크 복사
	 * 
	 * @param project
	 * @param template
	 * @throws WTException
	 */
	public void copyTasks(Project project, Template template) throws WTException;

	/**
	 * 산출물까지 복사
	 * 
	 * @param project
	 * @param template
	 * @param document
	 * @throws WTException
	 */
	public void copyTasks(Project project, Template template, WTDocument document) throws WTException;

	/**
	 * 태스크 완료
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public Map<String, Object> completeTaskAction(Map<String, Object> param) throws WTException;

	/**
	 * 특이사항 등록
	 * 
	 * @param param
	 * @return Map<String, Object>
	 * @throws WTException
	 */
	public abstract Map<String, Object> createIssueAction(Map<String, Object> param) throws WTException;

	/**
	 * 특이사항 삭제
	 * 
	 * @param param
	 * @return Map<String, Object>
	 */
	public abstract Map<String, Object> delIssueAction(Map<String, Object> param) throws WTException;

	/**
	 * 담당자 지정
	 * 
	 * @param param
	 * @return
	 * @throws Wte
	 */
	public abstract Map<String, Object> setUserAction(Map<String, Object> param) throws WTException;

	/**
	 * 진행률 변경
	 * 
	 * @param param
	 * @return
	 * @throws WTException
	 */
	public abstract Map<String, Object> setProgressAction(Map<String, Object> param) throws WTException;

	public abstract Map<String, Object> completeStepAction(Map<String, Object> param) throws WTException;

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
