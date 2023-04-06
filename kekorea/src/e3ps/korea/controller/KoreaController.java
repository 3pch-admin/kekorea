package e3ps.korea.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.korea.service.KoreaHelper;
import e3ps.org.service.OrgHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/korea/**")
public class KoreaController extends BaseController {

	@Description(value = "한국 생산 리스트+차트 프레임 페이지")
	@GetMapping(value = "/info")
	public ModelAndView info(@RequestParam String code) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("code", code);
		model.setViewName("/extcore/jsp/korea/korea-info.jsp");
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
	public ModelAndView chart(@RequestParam(required = false) String kekNumbers,
			@RequestParam(required = false) String pdateFrom, @RequestParam(required = false) String pdateTo,
			@RequestParam(required = false) String projectType, @RequestParam(required = false) String maks)
			throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = CommonUtils.sessionUser();
		boolean isAdmin = CommonUtils.isAdmin();

		ArrayList<String> list = new ArrayList<>();
		for (String mak : maks.split(",")) {
			list.add(mak);
		}

		ArrayList<CommonCode> customers = CommonCodeHelper.manager.getArrayCodeList("CUSTOMER");
		Map<String, ArrayList<Integer>> data = KoreaHelper.manager.data(kekNumbers, pdateFrom, pdateTo, projectType,
				maks);
//		Map<String, ArrayList<String>> drillDown = KoreaHelper.manager.drillDown(code, data);
//		CommonCode makCode = CommonCodeHelper.manager.getCommonCode(code, "MAK");
		model.addObject("customers", customers);
//		model.addObject("sessionUser", sessionUser);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("data", data);
		model.addObject("list", list);
		model.setViewName("/extcore/jsp/korea/korea-chart.jsp");
		return model;
	}

	@Description(value = "한국생산 탭 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = CommonUtils.sessionUser();
		ArrayList<CommonCode> maks = OrgHelper.manager.getUserMaks(sessionUser);
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -4);
		Timestamp date = new Timestamp(calendar.getTime().getTime());
		String before = date.toString().substring(0, 10);
		String end = DateUtils.getCurrentTimestamp().toString().substring(0, 10);

		ArrayList<Map<String, String>> projectTypes = CommonCodeHelper.manager.getValueMap("PROJECT_TYPE");
		model.addObject("before", before);
		model.addObject("end", end);
		model.addObject("projectTypes", projectTypes);
		model.addObject("maks", maks);
		model.setViewName("/extcore/jsp/korea/korea-list.jsp");
		return model;
	}
}
