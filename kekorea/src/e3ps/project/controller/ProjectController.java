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
import e3ps.project.service.ProjectHelper;

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
		ArrayList<CommonCode> installs = CommonCodeHelper.manager.getArrayCodeList("INSTALL");
		model.addObject("installs", installs);
		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.setViewName("popup:/project/project-create");
		return model;
	}
}
