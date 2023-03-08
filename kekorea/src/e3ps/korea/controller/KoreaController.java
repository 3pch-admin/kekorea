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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.korea.service.KoreaHelper;
import e3ps.org.service.OrgHelper;
import wt.org.WTUser;

@Controller
@RequestMapping(value = "/korea/**")
public class KoreaController extends BaseController {

	@Description(value = "한국 생산 차트 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
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
	public ModelAndView chart() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = CommonUtils.sessionUser();
		ArrayList<CommonCode> maks = OrgHelper.manager.getUserMaks(sessionUser);
		ArrayList<CommonCode> installs = OrgHelper.manager.getUserInstalls(sessionUser);
		// 설정을 안한 경우 모든 막종을 보여준다.
		if (maks.size() == 0) {
			maks = CommonCodeHelper.manager.getArrayCodeList("MAK");
		}

		model.addObject("maks", maks);
		model.addObject("installs", installs);
		model.setViewName("/extcore/jsp/korea/korea-chart.jsp");
		return model;
	}
}
