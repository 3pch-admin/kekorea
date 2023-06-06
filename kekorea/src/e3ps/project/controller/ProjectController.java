package e3ps.project.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.bom.partlist.service.PartlistHelper;
import e3ps.bom.tbom.service.TBOMHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.epm.workOrder.service.WorkOrderHelper;
import e3ps.korea.cip.service.CipHelper;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.org.service.OrgHelper;
import e3ps.project.Project;
import e3ps.project.dto.ProjectDTO;
import e3ps.project.issue.service.IssueHelper;
import e3ps.project.output.service.OutputHelper;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.project.task.dto.TaskDTO;
import e3ps.project.task.service.TaskHelper;
import e3ps.project.template.service.TemplateHelper;
import e3ps.project.variable.ProjectUserTypeVariable;
import e3ps.system.service.ErrorLogHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/project/**")
public class ProjectController extends BaseController {

	@Description(value = "작번 리스트 페이지")
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

		JSONArray elecs = OrgHelper.manager.getDepartmentUser("ELEC");
		JSONArray softs = OrgHelper.manager.getDepartmentUser("SOFT");
		JSONArray machines = OrgHelper.manager.getDepartmentUser("MACHINE");

		JSONArray maksJson = CommonCodeHelper.manager.parseJson("MAK");
		JSONArray customersJson = CommonCodeHelper.manager.parseJson("CUSTOMER");
		
		model.addObject("maksJson", maksJson);
		model.addObject("customersJson", customersJson);
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

	@Description(value = "작번 조회 함수")
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
			ErrorLogHelper.service.create(e.toString(), "/project/list", "작번 조회 함수");
		}
		return result;
	}

	@Description(value = "나의 작번 리스트 페이지")
	@GetMapping(value = "/my")
	public ModelAndView my() throws Exception {
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

		JSONArray elecs = OrgHelper.manager.getDepartmentUser("ELEC");
		JSONArray softs = OrgHelper.manager.getDepartmentUser("SOFT");
		JSONArray machines = OrgHelper.manager.getDepartmentUser("MACHINE");

		People people = CommonUtils.sessionPeople();
		Department department = people.getDepartment();

		boolean isMachine = false;
		if (department.getName().equals("기계설계")) {
			isMachine = true;
		}
		boolean isElec = false;
		if (department.getName().equals("전기설계")) {
			isElec = true;
		}
		boolean isSw = false;
		if (department.getName().equals("SW설계")) {
			isSw = true;
		}
		model.addObject("isMachine", isMachine);
		model.addObject("isElec", isElec);
		model.addObject("isSw", isSw);
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
		model.setViewName("/extcore/jsp/project/project-my-list.jsp");
		return model;
	}

	@Description(value = "작번 등록 함수")
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
			ErrorLogHelper.service.create(e.toString(), "/project/create", "작번 등록 함수");
		}
		return result;
	}

	@Description(value = "작번 등록 페이지")
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

	@Description(value = "작번 트리 함수")
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
			ErrorLogHelper.service.create(e.toString(), "/project/load", "작번 트리 함수");
		}
		return result;
	}

	@Description(value = "작번 트리 저장 함수")
	@PostMapping(value = "/treeSave")
	@ResponseBody
	public Map<String, Object> treeSave(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ProjectHelper.service.treeSave(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/project/treeSave", "작번 트리 저장 함수");
		}
		return result;
	}

	@Description(value = "작번 추가 페이지")
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

		JSONArray elecs = OrgHelper.manager.getDepartmentUser("ELEC");
		JSONArray softs = OrgHelper.manager.getDepartmentUser("SOFT");
		JSONArray machines = OrgHelper.manager.getDepartmentUser("MACHINE");

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

	@Description(value = "작번 프레임 페이지")
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

	@Description(value = "작번 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		ProjectDTO dto = new ProjectDTO(project);
		boolean isAdmin = CommonUtils.isAdmin();
		boolean isSupervisor = CommonUtils.isSupervisor();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		ArrayList<Project> list = ProjectHelper.manager.getReferenceBy(project);
		int mProgress = ProjectHelper.manager.getMachineAllocateProgress(project);
		int eProgress = ProjectHelper.manager.getElecAllocateProgress(project);
		model.addObject("mProgress", mProgress);
		model.addObject("eProgress", eProgress);
		model.addObject("isSupervisor", isSupervisor);
		model.addObject("list", list);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("dto", dto);
		model.addObject("project", project);
		model.setViewName("/extcore/jsp/project/project-view.jsp");
		return model;
	}

	@Description(value = "작번 그리드 저장 함수")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> editRows = params.get("editRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ArrayList<ProjectDTO> editRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> edit : editRows) {
				ProjectDTO dto = mapper.convertValue(edit, ProjectDTO.class);

				editRow.add(dto);
			}

			HashMap<String, List<ProjectDTO>> dataMap = new HashMap<>();
			dataMap.put("editRows", editRow);
			ProjectHelper.service.save(dataMap);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
			ErrorLogHelper.service.create(e.toString(), "/project/save", "작번 그리드 저장 함수");
		}
		return result;
	}

	@Description(value = "작번 담당자 수정 페이지")
	@GetMapping(value = "/editUser")
	public ModelAndView editUser(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		WTUser pmUser = ProjectHelper.manager.getUserType(project, ProjectUserTypeVariable.PM);
		WTUser subPmUser = ProjectHelper.manager.getUserType(project, ProjectUserTypeVariable.SUB_PM);
		WTUser machineUser = ProjectHelper.manager.getUserType(project, ProjectUserTypeVariable.MACHINE);
		WTUser elecUser = ProjectHelper.manager.getUserType(project, ProjectUserTypeVariable.ELEC);
		WTUser softUser = ProjectHelper.manager.getUserType(project, ProjectUserTypeVariable.SOFT);
		model.addObject("pmUser", pmUser);
		model.addObject("subPmUser", subPmUser);
		model.addObject("machineUser", machineUser);
		model.addObject("elecUser", elecUser);
		model.addObject("softUser", softUser);
		model.addObject("oid", oid);
		model.setViewName("popup:/project/project-edit-user");
		return model;
	}

	@Description(value = "작번 담당자 수정 함수")
	@ResponseBody
	@PostMapping(value = "/editUser")
	public Map<String, Object> editUser(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ProjectHelper.service.editUser(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/project/editUser", "작번 담당자 수정 함수");
		}
		return result;
	}

	@Description(value = "작번 금액 수정 페이지")
	@GetMapping(value = "/money")
	public ModelAndView money(@RequestParam String oid, @RequestParam String money, @RequestParam String type)
			throws Exception {
		ModelAndView model = new ModelAndView();

		if ("m".equals(type)) {
			model.addObject("name", "기계");
		} else if ("e".equals(type)) {
			model.addObject("name", "전기");
		}
		model.addObject("type", type);
		model.addObject("money", Double.parseDouble(money.replaceAll(",", "")));
		model.addObject("oid", oid);
		model.setViewName("popup:/project/project-money");
		return model;
	}

	@Description(value = "작번 금액 수정 함수")
	@ResponseBody
	@PostMapping(value = "/money")
	public Map<String, Object> money(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ProjectHelper.service.money(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/project/money", "작번 금액 수정 함수");
		}
		return result;
	}

	@Description(value = "작번 수배표 탭 페이지")
	@GetMapping(value = "/partlistTab")
	public ModelAndView partlistTab(@RequestParam String oid, @RequestParam String invoke) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray data = PartlistHelper.manager.partlistTab(oid, invoke);
		model.addObject("data", data);
		model.setViewName("/extcore/jsp/project/tab/project-partlist-tab.jsp");
		return model;
	}

	@Description(value = "작번 T-BOM 탭 페이지")
	@GetMapping(value = "/tbomTab")
	public ModelAndView tbomTab(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray data = TBOMHelper.manager.tbomTab(oid);
		model.addObject("data", data);
		model.setViewName("/extcore/jsp/project/tab/project-tbom-tab.jsp");
		return model;
	}

	@Description(value = "작번 도면일람표 탭 페이지")
	@GetMapping(value = "/workOrderTab")
	public ModelAndView workOrderTab(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray data = WorkOrderHelper.manager.workOrderTab(oid);
		model.addObject("data", data);
		model.setViewName("/extcore/jsp/project/tab/project-workOrder-tab.jsp");
		return model;
	}

	@Description(value = "작번 CIP 탭 페이지")
	@GetMapping(value = "/cipTab")
	public ModelAndView cipTab(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray data = CipHelper.manager.cipTab(oid);
		model.addObject("data", data);
		model.setViewName("/extcore/jsp/project/tab/project-cip-tab.jsp");
		return model;
	}

	@Description(value = "작번 특이사항 탭 페이지")
	@GetMapping(value = "/issueTab")
	public ModelAndView issueTab(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray data = IssueHelper.manager.issueTab(oid);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.addObject("data", data);
		model.setViewName("/extcore/jsp/project/tab/project-issue-tab.jsp");
		return model;
	}

	@Description(value = "작번 산출물 탭 페이지")
	@GetMapping(value = "/outputTab")
	public ModelAndView outputTab(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray data = OutputHelper.manager.outputTab(oid);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.addObject("data", data);
		model.setViewName("/extcore/jsp/project/tab/project-output-tab.jsp");
		return model;
	}

	@Description(value = "작번 수정 페이지")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		ProjectDTO dto = new ProjectDTO(project);
		ArrayList<CommonCode> customers = CommonCodeHelper.manager.getArrayCodeList("CUSTOMER");
		ArrayList<CommonCode> projectTypes = CommonCodeHelper.manager.getArrayCodeList("PROJECT_TYPE");
		ArrayList<CommonCode> maks = CommonCodeHelper.manager.getArrayCodeList("MAK");
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.addObject("maks", maks);
		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.setViewName("popup:/project/project-modify");
		return model;
	}

	@Description(value = "작번 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ProjectHelper.service.modify(params);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/project/modify", "작번 수정 함수");
		}
		return result;
	}

	@Description(value = "작번 삭제 함수")
	@ResponseBody
	@PostMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ProjectHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/project/delete", "작번 삭제 함수");
		}
		return result;
	}

	@Description(value = "작번 태스크 페이지")
	@GetMapping(value = "/task")
	public ModelAndView task(@RequestParam String oid, @RequestParam String toid, @RequestParam String name)
			throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		Task task = (Task) CommonUtils.getObject(toid);
		ProjectDTO data = new ProjectDTO(project);
		TaskDTO dto = new TaskDTO(task);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		int taskType = TaskHelper.manager.getTaskType(name);
		model.addObject("taskType", taskType);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("project", project);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/project/task/project-task-view.jsp");
		return model;
	}

	@Description(value = "작번 산출물 결재 페이지")
	@GetMapping(value = "/register")
	public ModelAndView register(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		JSONArray data = ProjectHelper.manager.registerData(project);
		model.addObject("project", project);
		model.addObject("data", data);
		model.addObject("oid", oid);
		model.setViewName("/extcore/jsp/project/project-register.jsp");
		return model;
	}

	@Description(value = "작번 산출물 결재")
	@PostMapping(value = "/register")
	@ResponseBody
	public Map<String, Object> register(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ProjectHelper.service.register(params);
			result.put("result", SUCCESS);
			result.put("msg", REGISTER_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
			ErrorLogHelper.service.create(e.toString(), "/project/register", "작번 산출물 결재 함수");
		}
		return result;
	}
}
