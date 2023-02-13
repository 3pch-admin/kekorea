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

import org.springframework.web.bind.annotation.ResponseBody;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.controller.BaseController;
import e3ps.org.service.OrgHelper;

@RequestMapping(value = "/org/**")
public class OrgController extends BaseController {

	@Description("사용자 검색 바인딩")
	@RequestMapping(value = "/getUserBind")
	@ResponseBody
	public Map<String, Object> getUserBind(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = OrgHelper.manager.getUserBind(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
