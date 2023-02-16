package e3ps.project.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.admin.sheetvariable.service.ItemsHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.controller.BaseController;
import e3ps.project.Project;
import e3ps.project.beans.ProjectViewData;
import e3ps.project.service.ProjectHelper;
import e3ps.project.task.Task;
import e3ps.project.task.service.TaskHelper;
import e3ps.project.template.Template;
import e3ps.project.template.beans.TemplateViewData;
import e3ps.project.template.service.TemplateHelper;
import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/project/**")
public class ProjectController extends BaseController {

	@Description(value = "프로젝트 리스트 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
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

		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.addObject("maks", maks);
		model.addObject("before", before);
		model.addObject("end", end);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/jsp/project/project-list.jsp");
		return model;
	}

	@Description(value = "프로젝트 리스트 가져 오는 함수")
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
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
}
