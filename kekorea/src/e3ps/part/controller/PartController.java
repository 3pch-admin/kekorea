package e3ps.part.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.part.service.PartHelper;

@Controller
@RequestMapping(value = "/part/**")
public class PartController {

	@Description("부품 조회 페이지")
	@RequestMapping(value = "/listPart", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		
		model.setViewName("/jsp/part/listProductPart2.jsp");
		return model;
	}
	
	@Description("부품 조회")
	@ResponseBody
	@RequestMapping(value = "/listPart", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
//		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, Object> result = null;
		try {
			params.put("context", PartHelper.PRODUCT_CONTEXT);
			result = PartHelper.manager.find(params);
			result.put("result", true);
		} catch(Exception e) {
			result.put("result", false);
			result.put("msg", e.toString());
		}
		return result;
	}
}
