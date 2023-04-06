package e3ps.project.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.bom.partlist.service.PartlistHelper;
import e3ps.bom.tbom.service.TBOMHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import e3ps.korea.cip.dto.CipDTO;
import e3ps.korea.cip.service.CipHelper;
import e3ps.org.service.OrgHelper;
import e3ps.project.Project;
import e3ps.project.dto.ProjectDTO;
import e3ps.project.issue.service.IssueHelper;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.project.task.dto.TaskDTO;
import e3ps.project.template.service.TemplateHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/project/**")
public class ProjectController extends BaseController {

	@Description(value = "프로젝트 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -4);
		Timestamp date = new Timestamp(calendar.getTime().getTime());
		String before = date.toString().substring(0, 10);
		String end = DateUtils.getCurrentTimestamp().toString().substring(0, 10);

		ArrayList<Map<String, String>> customers = CommonCodeHelper.manager.getValueMap("CUSTOMER");
		ArrayList<Map<String, String>> maks = CommonCodeHelper.manager.getValueMap("MAK");
		ArrayList<Map<String, String>> projectTypes = CommonCodeHelper.manager.getValueMap("PROJECT_TYPE");
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();

		org.json.JSONArray elecs = OrgHelper.manager.getDepartmentUser("ELEC");
		org.json.JSONArray softs = OrgHelper.manager.getDepartmentUser("SOFT");
		org.json.JSONArray machines = OrgHelper.manager.getDepartmentUser("MACHINE");

		model.addObject("elecs", elecs);
		model.addObject("softs", softs);
		model.addObject("machines", machines);
		model.addObject("list", list);
		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.addObject("maks", maks);
		model.addObject("before", before);
		model.addObject("end", end);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/project/project-list.jsp");
		return model;
	}

	@Description(value = "프로젝트 리스트 가져 오는 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ProjectHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "프로젝트 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ProjectHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "프로젝트 생성 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<CommonCode> customers = CommonCodeHelper.manager.getArrayCodeList("CUSTOMER");
		ArrayList<CommonCode> projectTypes = CommonCodeHelper.manager.getArrayCodeList("PROJECT_TYPE");
		ArrayList<CommonCode> maks = CommonCodeHelper.manager.getArrayCodeList("MAK");
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();
		model.addObject("list", list);
		model.addObject("maks", maks);
		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.setViewName("popup:/project/project-create");
		return model;
	}

	@Description(value = "프로젝트 트리")
	@GetMapping(value = "/load")
	@ResponseBody
	public Map<String, Object> load(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = ProjectHelper.manager.load(oid);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "프로젝트 트리 저장")
	@PostMapping(value = "/treeSave")
	@ResponseBody
	public Map<String, Object> treeSave(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ProjectHelper.service.treeSave(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "프로젝트 추가 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -4);
		Timestamp date = new Timestamp(calendar.getTime().getTime());
		String before = date.toString().substring(0, 10);
		String end = DateUtils.getCurrentTimestamp().toString().substring(0, 10);

		ArrayList<Map<String, String>> customers = CommonCodeHelper.manager.getValueMap("CUSTOMER");
		ArrayList<Map<String, String>> maks = CommonCodeHelper.manager.getValueMap("MAK");
		ArrayList<Map<String, String>> projectTypes = CommonCodeHelper.manager.getValueMap("PROJECT_TYPE");
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();

		org.json.JSONArray elecs = OrgHelper.manager.getDepartmentUser("ELEC");
		org.json.JSONArray softs = OrgHelper.manager.getDepartmentUser("SOFT");
		org.json.JSONArray machines = OrgHelper.manager.getDepartmentUser("MACHINE");

		model.addObject("elecs", elecs);
		model.addObject("softs", softs);
		model.addObject("machines", machines);
		model.addObject("list", list);
		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.addObject("maks", maks);
		model.addObject("before", before);
		model.addObject("end", end);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/project/project-popup");
		return model;
	}

	@Description(value = "프로젝트 프레임 페이지")
	@GetMapping(value = "/info")
	public ModelAndView info(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray list = CommonCodeHelper.manager.parseJson("TASK_TYPE");
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("list", list);
		model.addObject("oid", oid);
		model.setViewName("popup:/project/project-info");
		return model;
	}

	@Description(value = "프로젝트 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		ProjectDTO dto = new ProjectDTO(project);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/project/project-view.jsp");
		return model;
	}

	@Description(value = "프로젝트 태스트 일반 페이지")
	@GetMapping(value = "/normal")
	public ModelAndView normal(@RequestParam String oid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		Task task = (Task) CommonUtils.getObject(toid);
		ProjectDTO data = new ProjectDTO(project);
		TaskDTO dto = new TaskDTO(task);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		JSONArray list = ProjectHelper.manager.jsonAuiNormal(project, task);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("project", project);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-task-normal.jsp");
		return model;
	}

	@Description(value = "프로젝트 태스트 의뢰서 페이지")
	@GetMapping(value = "/request")
	public ModelAndView request(@RequestParam String oid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		Task task = (Task) CommonUtils.getObject(toid);
		ProjectDTO data = new ProjectDTO(project);
		TaskDTO dto = new TaskDTO(task);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		JSONArray list = ProjectHelper.manager.jsonAuiRequest(project);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("project", project);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-task-request.jsp");
		return model;
	}

	@Description(value = "막종상세, 거래처, 설치라인 관련 CIP 프로젝트 상세에서 보기")
	@GetMapping(value = "/cipTab")
	public ModelAndView cipTab(@RequestParam String mak_oid, @RequestParam String detail_oid,
			@RequestParam String customer_oid, @RequestParam String install_oid) throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<CipDTO> list = CipHelper.manager.cipTab(mak_oid, detail_oid, customer_oid, install_oid);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-cipTab.jsp");
		return model;
	}

	@Description(value = "도면 일람표 프로젝트 상세에서 보기")
	@GetMapping(value = "/workOrderTab")
	public ModelAndView workOrderTab(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray list = KeDrawingHelper.manager.workOrderTab(oid);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-workOrderTab.jsp");
		return model;
	}

	@Description(value = "특이사항 프로젝트 상세에서 보기")
	@GetMapping(value = "/issueTab")
	public ModelAndView issueTab(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray list = IssueHelper.manager.issueTab(oid);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-issueTab.jsp");
		return model;
	}

	@Description(value = "프로젝트 수배표 탭")
	@GetMapping(value = "/partlistTab")
	public ModelAndView partlistTab(@RequestParam String oid, @RequestParam String invoke) throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<Map<String, Object>> list = PartlistHelper.manager.partlistTab(oid, invoke);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-partlistTab.jsp");
		return model;
	}

	@Description(value = "프로젝트 T-BOM 탭")
	@GetMapping(value = "/tbomTab")
	public ModelAndView tbomTab(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<Map<String, Object>> list = TBOMHelper.manager.tbomTab(oid);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-tbomTab.jsp");
		return model;
	}

	@Description(value = "관련작번 프로젝트 상세에서 보기")
	@GetMapping(value = "/referenceTab")
	public ModelAndView referenceTab(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray list = ProjectHelper.manager.referenceTab(oid);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-referenceTab.jsp");
		return model;
	}

	@Description(value = "프로젝트 수배표통합 페이지")
	@GetMapping(value = "/partlist")
	public ModelAndView partlist(@RequestParam String oid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		Task task = (Task) CommonUtils.getObject(toid);
		ProjectDTO data = new ProjectDTO(project);
		TaskDTO dto = new TaskDTO(task);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		JSONArray list = ProjectHelper.manager.jsonAuiPartlist(project, task);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("project", project);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-task-partlist.jsp");
		return model;
	}

	@Description(value = "프로젝트 T-BOM 페이지")
	@GetMapping(value = "/tbom")
	public ModelAndView tbom(@RequestParam String oid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		Task task = (Task) CommonUtils.getObject(toid);
		ProjectDTO data = new ProjectDTO(project);
		TaskDTO dto = new TaskDTO(task);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		JSONArray list = ProjectHelper.manager.jsonAuiTbom(project, task);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("project", project);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-task-tbom.jsp");
		return model;
	}

	@Description(value = "프로젝트 수배표(1차, 2차) 페이지")
	@GetMapping(value = "/step")
	public ModelAndView step(@RequestParam String oid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		Task task = (Task) CommonUtils.getObject(toid);
		ProjectDTO data = new ProjectDTO(project);
		TaskDTO dto = new TaskDTO(task);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		JSONArray list = ProjectHelper.manager.jsonAuiStepPartlist(project, task);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("project", project);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/project/project-task-step.jsp");
		return model;
	}
}
