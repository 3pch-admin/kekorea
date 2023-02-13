package e3ps.partlist.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.partlist.service.PartListMasterHelper;

@Controller
@RequestMapping(value = "/partlist/**")
public class PartListController {
	
	@Description("수배표 조회 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/partList/partList-list.jsp");
		return model;
	}
	
	@Description("수배표 조회")
	@ResponseBody
	@RequestMapping(value= "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = PartListMasterHelper.manager.findPartList(params);
			result.put("result", true);
		} catch(Exception e) {
			e.printStackTrace();
			result.put("result", false);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	@Description("수배표 등록")
	@RequestMapping(value="/create", method=RequestMethod.GET)
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
//		model.setViewName("/jsp/partList/partList-create.jsp");
		model.setViewName("popup:/partList/partList-create");
		return model;
	}
}
