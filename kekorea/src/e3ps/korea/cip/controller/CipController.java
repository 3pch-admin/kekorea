package e3ps.korea.cip.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.controller.BaseController;
import e3ps.korea.cip.service.CipHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/cip/**")
public class CipController extends BaseController {

	@Description(value = "CIP 리스트 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray maks = CommonCodeHelper.manager.parseJson("MAK");
		JSONArray customers = CommonCodeHelper.manager.parseJson("CUSTOMER");
		JSONArray installs = CommonCodeHelper.manager.parseJson("INSTALL");
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		String userId = sessionUser.getName();
		model.addObject("userId", userId);
		model.addObject("maks", maks);
		model.addObject("installs", installs);
		model.addObject("customers", customers);
		model.setViewName("/jsp/korea/cip/cip-list.jsp");
		return model;
	}

	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = CipHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description("CIP 등록")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			CipHelper.service.create(params);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
}
