package e3ps.korea.controller;

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

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.controller.BaseController;
import e3ps.korea.service.KoreaHelper;

@Controller
@RequestMapping(value = "/korea/**")
public class KoreaController extends BaseController {

	@Description(value = "한국 생산 차트 리스트 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<CommonCode> installs = CommonCodeHelper.manager.getArrayCodeList("INSTALL");
		ArrayList<CommonCode> maks = CommonCodeHelper.manager.getArrayCodeList("MAK");
		model.addObject("maks", maks);
		model.addObject("installs", installs);
		model.setViewName("/jsp/korea/korea-list.jsp");
		return model;
	}

	@Description(value = "KOREA 검색 함수")
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
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
}
