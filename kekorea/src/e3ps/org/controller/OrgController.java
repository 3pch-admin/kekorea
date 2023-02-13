package e3ps.org.controller;

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
import e3ps.org.service.OrgHelper;

@Controller
@RequestMapping(value="org/**")
public class OrgController  extends BaseController {

	@Description("조직도 페이지")
	@RequestMapping(value="/viewOrg", method=RequestMethod.GET)
	public ModelAndView viewOrg() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/org/org-view.jsp");
		return model;
	}
	
	@Description("조직도")
	@RequestMapping(value="/viewOrg", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> viewOrg(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = OrgHelper.manager.find(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
}
