package e3ps.epm.controller;

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
import e3ps.epm.service.EpmHelper;

@Controller
@RequestMapping(value = "/epm/**")
public class EpmController extends BaseController{
	
	@Description("도면 결재 페이지")
	@RequestMapping(value = "approval", method= RequestMethod.GET)
	public ModelAndView approval() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/epm/approvalEpm.jsp");
		return model;
	}
	
	@Description("도면 결재")
	@ResponseBody
	@RequestMapping(value = "/approval", method = RequestMethod.POST)
	public Map<String, Object> approval(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = EpmHelper.service.approvalEpmAction(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
}