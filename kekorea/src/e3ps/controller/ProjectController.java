package e3ps.controller;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.StateKeys;
import e3ps.common.code.service.CommonCodeHelper;
import e3ps.org.Department;
import e3ps.org.service.OrgHelper;
import e3ps.project.DocumentMasterOutputLink;
import e3ps.project.DocumentOutputLink;
import e3ps.project.Issue;
import e3ps.project.IssueProjectLink;
import e3ps.project.Project;
import e3ps.project.Task;
import e3ps.project.Template;
import e3ps.project.beans.ProjectViewData;
import e3ps.project.beans.TaskViewData;
import e3ps.project.enums.ProjectUserType;
import e3ps.project.service.ProjectHelper;
import e3ps.project.service.TemplateHelper;
import net.sf.json.JSONArray;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
public class ProjectController extends BaseController {

	
	@RequestMapping(value = "/project/completeStepAction")
	@ResponseBody
	public Map<String, Object> completeStepAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.completeStepAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
	@Description("작번 삭제")
	@RequestMapping(value = "/project/deleteProjectAction")
	@ResponseBody
	public Map<String, Object> deleteProjectAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.deleteProjectAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("작번 수정")
	@RequestMapping(value = "/project/modifyProjectAction")
	@ResponseBody
	public Map<String, Object> modifyProjectAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.modifyProjectAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("작번 수정 페이지")
	@RequestMapping(value = "/project/modifyProject")
	public ModelAndView modifyProject(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<Template> tmp = TemplateHelper.service.getTemplate();
		ArrayList<String> customer = CommonCodeHelper.manager.getCommonCode("CUSTOMER");
		ArrayList<String> project_type = CommonCodeHelper.manager.getCommonCode("PROJECT_TYPE");
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		Project project = (Project) rf.getReference(oid).getObject();
		ProjectViewData data = new ProjectViewData(project);
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		model.addObject("customer", customer);
		model.addObject("project_type", project_type);
		model.addObject("template", tmp);
		model.addObject("data", data);
		if (isPopup) {
			model.setViewName("popup:/project/modifyProject");
		} else {
			model.setViewName("default:/project/modifyProject");
		}
		return model;
	}

	@Description("진행률 변경")
	@RequestMapping(value = "/project/setProgressAction")
	@ResponseBody
	public Map<String, Object> setProgressAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.setProgressAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("프로젝트 일정 보기")
	@RequestMapping(value = "/project/openProjectTaskCalendar")
	public ModelAndView openProjectTaskCalendar(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		if (isPopup) {
			model.setViewName("popup:/project/openProjectTaskCalendar");
		} else {
			model.setViewName("default:/project/openProjectTaskCalendar");
		}

		String gantt = ProjectHelper.manager.loadGanttProject(param);
		model.addObject("gantt", gantt);
		model.addObject("oid", oid);
		return model;
	}

	@Description("담당자 지정")
	@RequestMapping(value = "/project/setUserAction")
	@ResponseBody
	public Map<String, Object> setUserAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.setUserAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("담당자 지정 페이지")
	@RequestMapping(value = "/project/setUser")
	public ModelAndView setUser(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));

		ReferenceFactory rf = new ReferenceFactory();
		Project project = (Project) rf.getReference(oid).getObject();
		WTUser pm = ProjectHelper.manager.getPMByProject(project);
		WTUser subpm = ProjectHelper.manager.getSubPMByProject(project);

		WTUser machine = ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.MACHINE.name());
		WTUser elec = ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.ELEC.name());
		WTUser soft = ProjectHelper.manager.getUserTypeByProject(project, ProjectUserType.SOFT.name());

		if (isPopup) {
			model.setViewName("popup:/project/setUser");
		} else {
			model.setViewName("default:/project/setUser");
		}
		model.addObject("kekState", project.getKekState());
		model.addObject("pm", pm);
		model.addObject("subpm", subpm);
		model.addObject("machine", machine);
		model.addObject("elec", elec);
		model.addObject("soft", soft);
		return model;
	}

	@Description("태스크 완료")
	@RequestMapping(value = "/project/completeTaskAction")
	@ResponseBody
	public Map<String, Object> completeTaskAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.completeTaskAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("수배표 프로젝트")
	@RequestMapping(value = "/project/viewProjectByKekNumber")
	public ModelAndView viewProjectByKekNumber(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		Project project = null;
		ProjectViewData data = null;
		ArrayList<Project> refProjectList = new ArrayList<Project>();
		try {
			project = ProjectHelper.manager.getProjectByKekNumber(param);
			data = new ProjectViewData(project);
			refProjectList = ProjectHelper.manager.getRefProjectByKekNumber(project);
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addObject("refProjectList", refProjectList);
		model.addObject("data", data);
		if (isPopup) {
			model.setViewName("popup:/project/viewProject");
		} else {
			model.setViewName("default:/project/viewProject");
		}
		return model;
	}

	@Description("프로젝트 재시작")
	@RequestMapping(value = "/project/restartProjectAction")
	@ResponseBody
	public Map<String, Object> restartProjectAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.restartProjectAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("프로젝트 완료")
	@RequestMapping(value = "/project/completeProjectAction")
	@ResponseBody
	public Map<String, Object> completeProjectAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.completeProjectAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("프로젝트 중단")
	@RequestMapping(value = "/project/stopProjectAction")
	@ResponseBody
	public Map<String, Object> stopProjectAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.stopProjectAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("프로젝트 시작")
	@RequestMapping(value = "/project/startProjectAction")
	@ResponseBody
	public Map<String, Object> startProjectAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.startProjectAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("산출물 삭제")
	@RequestMapping(value = "/project/delOutputAction")
	@ResponseBody
	public Map<String, Object> delOutputAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.delOutputAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("산출물 추가")
	@RequestMapping(value = "/project/addOutputAction")
	@ResponseBody
	public Map<String, Object> addOutputAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.addOutputAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("산출물 추가 페이지")
	@RequestMapping(value = "/project/addOutput")
	public ModelAndView addDocument() throws Exception {
		ModelAndView model = new ModelAndView();
		StateKeys[] states = StateKeys.values();
		model.addObject("states", states);
		model.setViewName("popup:/project/addOutput");
		return model;
	}

	@Description("의뢰서 추가 페이지")
	@RequestMapping(value = "/project/addRequestDocument")
	public ModelAndView addRequestDocument() throws Exception {
		ModelAndView model = new ModelAndView();
		StateKeys[] states = StateKeys.values();
		model.addObject("states", states);
		model.setViewName("popup:/project/addRequestDocument");
		return model;
	}

	@Description("템플릿 태스크 편집 페이지")
	@RequestMapping(value = "/project/openProjectTaskEditor")
	public ModelAndView openProjectTaskEditor(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		if (isPopup) {
			model.setViewName("popup:/project/openProjectTaskEditor");
		} else {
			model.setViewName("default:/project/openProjectTaskEditor");
		}

		String gantt = ProjectHelper.manager.loadGanttProject(param);
		// String gantt = TemplateHelper.manager.loadGanttTemplate(param);
		model.addObject("gantt", gantt);
		model.addObject("oid", oid);
		return model;
	}

	@Description("작번 추가 페이지")
	@RequestMapping(value = "/project/addProject")
	public ModelAndView addProject() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<String> customer = CommonCodeHelper.manager.getCommonCode("CUSTOMER");
		ArrayList<String> project_type = CommonCodeHelper.manager.getCommonCode("PROJECT_TYPE");
		ArrayList<String> install = CommonCodeHelper.manager.getCommonCode("INSTALL");
		StateKeys[] states = StateKeys.values();

		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

		Department dept = OrgHelper.manager.getDepartment(user);
		model.addObject("dept", dept);
		model.addObject("install", install);
		model.addObject("states", states);
		model.addObject("customer", customer);
		model.addObject("project_type", project_type);
		model.setViewName("popup:/project/addProject");
		return model;
	}

	@Description("작번 추가")
	@RequestMapping(value = "/project/addProjectAction")
	@ResponseBody
	public Map<String, Object> addEpmAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.addProjectAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("프로젝트 태스크 정보 페이지")
	@RequestMapping(value = "/project/viewProjectTask")
	public ModelAndView viewProjectTask(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		Task task = null;
		Project project = null;
		TaskViewData data = null;
		ProjectViewData pdata = null;
		ArrayList<DocumentOutputLink> outputList = new ArrayList<DocumentOutputLink>();
		ArrayList<DocumentMasterOutputLink> outputMasterLink = new ArrayList<DocumentMasterOutputLink>();
		ReferenceFactory rf = new ReferenceFactory();
		try {
			task = (Task) rf.getReference(oid).getObject();
			project = task.getProject();
			data = new TaskViewData(task);
			pdata = new ProjectViewData(project);
			outputMasterLink = ProjectHelper.manager.getProjectMasterOutputLink(task);
			outputList = ProjectHelper.manager.getProjectOutputLink(task);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("outputMasterLink", outputMasterLink);
		model.addObject("outputList", outputList);
		model.addObject("pdata", pdata);
		model.addObject("data", data);
		if (isPopup) {
			model.setViewName("popup:/project/viewProjectTask");
		} else {
			model.setViewName("default:/project/viewProjectTask");
		}
		return model;
	}

	@Description("프로젝트 태스크 트리 가져오기")
	@RequestMapping(value = "/project/getProjectTaskTree")
	@ResponseBody
	public JSONArray getProjectTaskTree(@RequestParam Map<String, Object> param) {
		JSONArray node = null;
		try {
			node = ProjectHelper.manager.openProjectTree(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return node;
	}

	@Description("프로젝트 정보 페이지")
	@RequestMapping(value = "/project/viewProject")
	public ModelAndView viewProject(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		Project project = null;
		ProjectViewData data = null;
		ArrayList<Project> refProjectList = new ArrayList<Project>();
		ArrayList<IssueProjectLink> issueLink = new ArrayList<IssueProjectLink>();
		ReferenceFactory rf = new ReferenceFactory();
		try {
			if (rf.getReference(oid).getObject() instanceof Issue) {
				Issue isseu = (Issue) rf.getReference(oid).getObject();
				ArrayList<IssueProjectLink> projectList = ProjectHelper.manager.getIssueProjectLinkProject(isseu);
				for (IssueProjectLink link : projectList) {
					project = link.getProject();
				}
			} else {
				project = (Project) rf.getReference(oid).getObject();
			}
			data = new ProjectViewData(project);
			refProjectList = ProjectHelper.manager.getRefProjectByKekNumber(project);
			issueLink = ProjectHelper.manager.getIssueProjectLink(project);
		} catch (Exception e) {
			e.printStackTrace();
		}

		model.addObject("issueLink", issueLink);
		model.addObject("refProjectList", refProjectList);
		model.addObject("data", data);
		if (isPopup) {
			model.setViewName("popup:/project/viewProject");
		} else {
			model.setViewName("default:/project/viewProject");
		}
		return model;
	}

	@Description("작번 목록 가져오기")
	@ResponseBody
	@RequestMapping(value = "/project/listProjectAction")
	public Map<String, Object> listProjectAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.manager.find(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("작번 가격 수정")
	@RequestMapping(value = "/project/modifyProjectPriceAction")
	@ResponseBody
	public Map<String, Object> modifyProjectPriceAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.modifyProjectPriceAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("작번 등록")
	@RequestMapping(value = "/project/createProjectAction")
	@ResponseBody
	public Map<String, Object> createProjectAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.createProjectAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("KEK 작번 체크")
	@RequestMapping(value = "/project/checkKekNumberAction")
	@ResponseBody
	public Map<String, Object> checkKekNumberAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.manager.checkKekNumberAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("작번 조회 페이지")
	@RequestMapping(value = "/project/listProject")
	public ModelAndView listProject() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<String> customer = CommonCodeHelper.manager.getCommonCode("CUSTOMER");
		ArrayList<String> project_type = CommonCodeHelper.manager.getCommonCode("PROJECT_TYPE");
		ArrayList<String> install = CommonCodeHelper.manager.getCommonCode("INSTALL");
		// HashMap<String, String[]> locationCode
		// =CommonCodeHelper.manager.getLocationCode();
		// default 승인됨
		StateKeys[] states = StateKeys.values();

		model.addObject("install", install);
		model.addObject("states", states);
		model.addObject("customer", customer);
		model.addObject("project_type", project_type);
		model.setViewName("default:/project/listProject");
		return model;
	}

	@Description("작번 등록 페이지")
	@RequestMapping(value = "/project/createProject")
	public ModelAndView createProject() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<Template> tmp = TemplateHelper.service.getTemplate();
		ArrayList<String> customer = CommonCodeHelper.manager.getCommonCode("CUSTOMER");
		ArrayList<String> project_type = CommonCodeHelper.manager.getCommonCode("PROJECT_TYPE");

		model.addObject("customer", customer);
		model.addObject("project_type", project_type);
		model.addObject("template", tmp);
		model.setViewName("default:/project/createProject");
		return model;
	}

	@Description("코드 등록 페이지")
	@RequestMapping(value = "/project/createCode")
	public ModelAndView createCode() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<String> customer = CommonCodeHelper.manager.getCommonCode("CUSTOMER");

		model.addObject("customer", customer);
		model.setViewName("default:/project/createCode");
		return model;
	}

	@Description("코드 등록")
	@RequestMapping(value = "/project/createCodeAction")
	@ResponseBody
	public Map<String, Object> createCodeAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = CommonCodeHelper.service.createCodeAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("특이 사항 추가 페이지")
	@RequestMapping(value = "/project/addIssue")
	public ModelAndView addIssue() throws Exception {
		ModelAndView model = new ModelAndView();
		StateKeys[] states = StateKeys.values();
		model.addObject("states", states);
		model.setViewName("popup:/project/addIssue");
		return model;
	}

	@Description("특이 사항 추가")
	@RequestMapping(value = "/project/createIssueAction")
	@ResponseBody
	public Map<String, Object> createIssueAction(@RequestBody Map<String, Object> param) throws Exception {
		System.out.println("createIssueActioncreateIssueActioncreateIssueAction");
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.createIssueAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("특이사항 삭제")
	@RequestMapping(value = "/project/delIssueAction")
	@ResponseBody
	public Map<String, Object> delIssueAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.delIssueAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("특이사항 조회 페이지")
	@RequestMapping(value = "/project/issueList")
	public ModelAndView listDocument() throws Exception {
		ModelAndView model = new ModelAndView();
		// default 승인됨
		model.setViewName("default:/project/issueList");
		return model;
	}

	@Description("특이사항 목록 가져오기")
	@ResponseBody
	@RequestMapping(value = "/project/listIssueAction")
	public Map<String, Object> listIssueAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.manager.findIssue(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("특이사항 조회 페이지")
	@RequestMapping(value = "/project/viewIssue")
	public ModelAndView viewIssue(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		Issue isseu = null;
		ArrayList<IssueProjectLink> projectList = new ArrayList<IssueProjectLink>();
		ReferenceFactory rf = new ReferenceFactory();
		try {
			isseu = (Issue) rf.getReference(oid).getObject();
			projectList = ProjectHelper.manager.getIssueProjectLinkProject(isseu);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("projectList", projectList);
		model.addObject("isseu", isseu);
		if (isPopup) {
			model.setViewName("popup:/project/viewIssue");
		} else {
			model.setViewName("default:/project/viewIssue");
		}
		return model;
	}

	@RequestMapping(value = "/project/onSaveAction")
	@ResponseBody
	public Map<String, Object> onSaveAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.onSaveAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/project/onDeleteTaskAction")
	@ResponseBody
	public Map<String, Object> onDeleteTaskAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.onDeleteTaskAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/project/onAfterLinkDeleteAction")
	@ResponseBody
	public Map<String, Object> onAfterLinkDeleteAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.onAfterLinkDeleteAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/project/onMoveTaskAction")
	@ResponseBody
	public Map<String, Object> onMoveTaskAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.onMoveTaskAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/project/onBeforeLinkAddAction")
	@ResponseBody
	public Map<String, Object> onBeforeLinkAddAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.onBeforeLinkAddAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/project/onAfterTaskMoveAction")
	@ResponseBody
	public Map<String, Object> onAfterTaskMoveAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.onAfterTaskMoveAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/project/onAfterTaskResizeAction")
	@ResponseBody
	public Map<String, Object> onAfterTaskResizeAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.onAfterTaskResizeAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@RequestMapping(value = "/project/onAfterLinkUpdateAction")
	@ResponseBody
	public Map<String, Object> onAfterLinkUpdateAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ProjectHelper.service.onAfterLinkUpdateAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
