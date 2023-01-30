package e3ps.korea.cip.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.controller.BaseController;
import e3ps.korea.cip.service.CipHelper;

@Controller
@RequestMapping(value = "/cip/**")
public class CipController extends BaseController {

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/korea/cip/cip-list.jsp");
		return model;
	}
	@Description("cip 등록 페이지")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/korea/cip/create.jsp");
		return model;
	}
	
	@Description("cip 등록")
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> create(@RequestBody Map<String, Object> params)  throws Exception{
		Map<String, Object> result = new HashMap<String, Object>();
//		Map<String, Object> result = null;
		try {
//			result = CipHelper.manager.create(params);
			CipHelper.service.create(params);
			result.put("result", SUCCESS);
//			System.out.println("ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
}
