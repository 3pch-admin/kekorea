package e3ps.project.template.controller;

import java.util.ArrayList;
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

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.CommonUtils;
import e3ps.controller.BaseController;
import e3ps.project.task.Task;
import e3ps.project.task.service.TaskHelper;
import e3ps.project.template.Template;
import e3ps.project.template.beans.TemplateViewData;
import e3ps.project.template.service.TemplateHelper;
import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/template/**")
public class TemplateController extends BaseController {

	@Description(value = "템플릿 리스트 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/project/template/template-list.jsp");
		return model;
	}

	@Description(value = "템플릿 리스트 가져 오는 함수")
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = TemplateHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "템플릿 등록 함수")
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TemplateHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "템플릿 생성 페이지")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<HashMap<String, Object>> list = TemplateHelper.manager.getTemplateArrayMap();
		model.addObject("list", list);
		model.setViewName("popup:/project/template/template-create");
		return model;
	}

	@Description(value = "템플릿 정보 트리 페이지")
	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		org.json.JSONArray taskTypes = CommonCodeHelper.manager.parseJson("TASK_TYPE");
		model.addObject("taskTypes", taskTypes);
		model.addObject("oid", oid);
		model.setViewName("popup:/project/template/template-view");
		return model;
	}

	@Description(value = "템플릿 트리")
	@RequestMapping(value = "/load", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> load(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = TemplateHelper.manager.load(oid);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "템플릿 트리 저장")
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TemplateHelper.service.save(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "템플릿 정보 페이지")
	@RequestMapping(value = "/templateView", method = RequestMethod.GET)
	public ModelAndView templateView(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Template template = (Template) CommonUtils.getObject(oid);
		TemplateViewData data = new TemplateViewData(template);
		ArrayList<Task> list = TaskHelper.manager.getTemplateTaskDepth(template);
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("list", list);
		model.addObject("data", data);
		model.setViewName("popup:/project/template/templateView");
		return model;
	}
	

	@Description(value = "템플릿 책임자 등록 함수")
	@ResponseBody
	@RequestMapping(value = "/saveUserLink", method = RequestMethod.POST)
	public Map<String, Object> saveUserLink(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TemplateHelper.service.saveUserLink(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

}
