package e3ps.admin.commonCode.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.CommonCodeType;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.AUIGridUtils;
import e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/commonCode/**")
public class CommonCodeController extends BaseController {

	@Description(value = "코드 관리 리스트 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		CommonCodeType[] codeTypes = CommonCodeType.getCommonCodeTypeSet();
		JSONArray jsonList = CommonCodeHelper.manager.parseJson();
		model.addObject("jsonList", jsonList);
		model.addObject("codeTypes", codeTypes);
		model.setViewName("/jsp/admin/commonCode/commonCode-list.jsp");
		return model;
	}

	@Description(value = "코드 관리 리스트 가져 오는 함수")
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = CommonCodeHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "코드 관리 등록 함수")
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			CommonCodeHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "코드관리 리스트 AUIGrid 코드 리모트 렌더러 호출 함수")
	@ResponseBody
	@RequestMapping(value = "/remoter", method = RequestMethod.POST)
	public Map<String, Object> remoter(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, Object>> list = AUIGridUtils.remoter(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "코드의 자식 코드 가져오는 함수")
	@ResponseBody
	@RequestMapping(value = "/getChildrens", method = RequestMethod.GET)
	public Map<String, Object> getChildrens(@RequestParam String parentCode, String codeType) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, Object>> childrens = CommonCodeHelper.manager.getChildrens(parentCode, codeType);
			result.put("list", childrens);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

}
