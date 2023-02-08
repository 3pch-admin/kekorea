package e3ps.korea.history.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.sheetvariable.service.CategoryHelper;
import e3ps.admin.spec.service.SpecHelper;
import e3ps.common.util.AUIGridUtils;
import e3ps.controller.BaseController;
import e3ps.korea.history.service.HistoryHelper;

@Controller
@RequestMapping(value = "/history/**")
public class HistoryController extends BaseController {

	@Description(value = "이력관리 리스트 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<Map<String, Object>> headers = SpecHelper.manager.getSpecKeyValue();
		model.addObject("headers", headers);
		model.setViewName("/jsp/korea/history/history-list.jsp");
		return model;
	}

	@Description(value = "이력관리 리스트 목록 가져오는 함수")
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = HistoryHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "이력관리 리스트 AUIGrid KEK작번 리모트 렌더러 호출 함수")
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
	
	@Description(value = "이력 관리 등록 함수")
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			HistoryHelper.service.create(params);
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
