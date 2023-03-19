package e3ps.korea.controller;

import java.util.ArrayList;
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
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.korea.service.KoreaHelper;
import e3ps.org.service.OrgHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/korea/**")
public class KoreaController extends BaseController {

	@Description(value = "한국 생산 차트 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list(@RequestParam String code) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("code", code);
		model.setViewName("/extcore/jsp/korea/korea-list.jsp");
		return model;
	}

	@Description(value = "한국생산 검색 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = KoreaHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "한국 생산 차트 페이지")
	@GetMapping(value = "/chart")
	public ModelAndView chart(@RequestParam String code, @RequestParam(required = false) String projectType)
			throws Exception {
		ModelAndView model = new ModelAndView();
		CommonCode makCode = CommonCodeHelper.manager.getCommonCode(code, "MAK");
		WTUser sessionUser = CommonUtils.sessionUser();
		ArrayList<CommonCode> customers = CommonCodeHelper.manager.getArrayCodeList("CUSTOMER");
		ArrayList<CommonCode> installs = OrgHelper.manager.getUserInstalls(sessionUser);
		if (installs.size() == 0) {
			installs = CommonCodeHelper.manager.getArrayCodeList("INSTALL");
		}
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("makCode", makCode);
		model.addObject("projectType", projectType);
		model.addObject("code", code);
		model.addObject("customers", customers);
		model.addObject("installs", installs);
		model.setViewName("/extcore/jsp/korea/korea-chart.jsp");
		return model;
	}
}
