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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.org.service.OrgHelper;
import e3ps.project.Project;
import e3ps.project.dto.ProjectViewData;
import e3ps.project.service.ProjectHelper;
import e3ps.project.template.service.TemplateHelper;
import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/project/**")
public class ProjectController extends BaseController {

	@Description(value = "프로젝트 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -4);
		Timestamp date = new Timestamp(calendar.getTime().getTime());
		String before = date.toString().substring(0, 10);
		String end = DateUtils.getCurrentTimestamp().toString().substring(0, 10);

		ArrayList<Map<String, String>> customers = CommonCodeHelper.manager.getArrayKeyValueMap("CUSTOMER");
		ArrayList<Map<String, String>> maks = CommonCodeHelper.manager.getArrayKeyValueMap("MAK");
		ArrayList<Map<String, String>> projectTypes = CommonCodeHelper.manager.getArrayKeyValueMap("PROJECT_TYPE");
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
	@RequestMapping(value = "/create", method = RequestMethod.POST)
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
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<CommonCode> customers = CommonCodeHelper.manager.getArrayCodeList("CUSTOMER");
		ArrayList<CommonCode> projectTypes = CommonCodeHelper.manager.getArrayCodeList("PROJECT_TYPE");
		ArrayList<CommonCode> maks = CommonCodeHelper.manager.getArrayCodeList("MAK");
		ArrayList<HashMap<String, Object>> list = TemplateHelper.manager.getTemplateArrayMap();
		model.addObject("list", list);
		model.addObject("maks", maks);
		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.setViewName("popup:/project/project-create");
		return model;
	}

	@Description(value = "그리드상 리모터로 프로젝트 정보 가져오기")
	@ResponseBody
	@RequestMapping(value = "/get", method = RequestMethod.GET)
	public Map<String, Object> get(@RequestParam String kekNumber) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ProjectHelper.manager.get(kekNumber);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "프로젝트 정보 트리 페이지")
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		org.json.JSONArray taskTypes = CommonCodeHelper.manager.parseJson("TASK_TYPE");
		model.addObject("taskTypes", taskTypes);
		model.addObject("oid", oid);
		model.setViewName("popup:/project/project-view");
		return model;
	}

	@Description(value = "프로젝트 정보 페이지")
	@RequestMapping(value = "/projectView", method = RequestMethod.GET)
	public ModelAndView projectView(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		ProjectViewData data = new ProjectViewData(project);
//		ArrayList<Task> list = TaskHelper.manager.getTemplateTaskDepth(template);
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
//		model.addObject("list", list);
		model.addObject("data", data);
		model.setViewName("popup:/project/projectView");
		return model;
	}

	@Description(value = "프로젝트 트리")
	@RequestMapping(value = "/load", method = RequestMethod.GET)
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
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ProjectHelper.service.save(params);
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

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -4);
		Timestamp date = new Timestamp(calendar.getTime().getTime());
		String before = date.toString().substring(0, 10);
		String end = DateUtils.getCurrentTimestamp().toString().substring(0, 10);

		ArrayList<CommonCode> customers = CommonCodeHelper.manager.getArrayCodeList("CUSTOMER");
		ArrayList<CommonCode> projectTypes = CommonCodeHelper.manager.getArrayCodeList("PROJECT_TYPE");
		ArrayList<CommonCode> maks = CommonCodeHelper.manager.getArrayCodeList("MAK");
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();

		model.addObject("list", list);
		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.addObject("maks", maks);
		model.addObject("before", before);
		model.addObject("end", end);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.addObject("method", method);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/project/project-popup");
		return model;
	}
}
