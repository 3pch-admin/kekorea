package e3ps.epm.numberRule.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
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
import e3ps.epm.numberRule.service.NumberRuleHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/numberRule/**")
public class NumberRuleController extends BaseController {

	@Description(value = "KEK 도번 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<CommonCode> sizes = CommonCodeHelper.manager.getArrayCodeList("SIZE");
		ArrayList<CommonCode> drawingCompanys = CommonCodeHelper.manager.getArrayCodeList("DRAWING_COMPANY");
		ArrayList<CommonCode> writtenDocuments = CommonCodeHelper.manager.getArrayCodeList("WRITTEN_DOCUMENT");
		ArrayList<CommonCode> businessSectors = CommonCodeHelper.manager.getArrayCodeList("BUSINESS_SECTOR");
		ArrayList<CommonCode> classificationWritingDepartment = CommonCodeHelper.manager
				.getArrayCodeList("CLASSIFICATION_WRITING_DEPARTMENT");
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("drawingCompanys", drawingCompanys);
		model.addObject("writtenDocuments", writtenDocuments);
		model.addObject("businessSectors", businessSectors);
		model.addObject("classificationWritingDepartment", classificationWritingDepartment);
		model.addObject("sizes", sizes);
		model.setViewName("/extcore/jsp/epm/numberRule/numberRule-list.jsp");
		return model;
	}

	@Description(value = "KEK 도번 리스트 가져 오는 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = NumberRuleHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "KEK 도번 카테고리 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			NumberRuleHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "KEK 도번 생성 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray drawingCompanys = CommonCodeHelper.manager.parseJson("DRAWING_COMPANY");
		JSONArray writtenDocuments = CommonCodeHelper.manager.parseJson("WRITTEN_DOCUMENT");
		JSONArray businessSectors = CommonCodeHelper.manager.parseJson("BUSINESS_SECTOR");
		JSONArray classificationWritingDepartment = CommonCodeHelper.manager
				.parseJson("CLASSIFICATION_WRITING_DEPARTMENT");
		model.addObject("drawingCompanys", drawingCompanys);
		model.addObject("writtenDocuments", writtenDocuments);
		model.addObject("businessSectors", businessSectors);
		model.addObject("classificationWritingDepartment", classificationWritingDepartment);
		model.setViewName("popup:/epm/numberRule/numberRule-create");
		return model;
	}

	@Description(value = "KEK 최종 도번 가져오기")
	@ResponseBody
	@GetMapping(value = "/last")
	public Map<String, Object> last(@RequestParam String number) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = NumberRuleHelper.manager.last(number);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "KEK 도번 개정 페이지")
	@GetMapping(value = "/revise")
	public ModelAndView revise() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/epm/numberRule/numberRule-revise");
		return model;
	}

	@Description(value = "KEK 최종 도번 개정")
	@ResponseBody
	@PostMapping(value = "/revise")
	public Map<String, Object> revise(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			NumberRuleHelper.service.revise(params);
			result.put("msg", REVISE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
