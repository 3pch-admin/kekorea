package e3ps.epm.KDrawing.controller;

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
import e3ps.epm.KDrawing.service.KDrawingHelper;

@Controller
@RequestMapping(value = "/KDrawing/**")
public class KDrawingController extends BaseController{

	@Description("KEK 도면 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/epm/kDrawing/kDrawing-list.jsp");
		return model;
	}
	
	@Description("KEK 도면 목록")
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestBody Map<String, Object> params)  throws Exception {
		Map<String, Object> result = new HashMap<String, Object> ();
		try {
			result = KDrawingHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			result.put("result", FAIL);
			e.printStackTrace();
		}
		return result;
	}
}
