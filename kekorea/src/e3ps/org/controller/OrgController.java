package e3ps.org.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.controller.BaseController;
import e3ps.org.Department;
import e3ps.org.service.OrgHelper;
import net.sf.json.JSONArray;

@Controller
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
	
	@Description("조직도 페이지")
	@RequestMapping(value = "/viewOrg", method=RequestMethod.GET)
	public ModelAndView viewOrg() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/org/org-view.jsp");
		return model;
	}
	
	@Description("조직도 ")
	@RequestMapping(value = "/viewOrg", method=RequestMethod.POST)
	public Map<String, Object> viewOrg(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
//		Map<String, Object> result = null;
		try {
			result = OrgHelper.manager.find(params);
			result.put("result", SUCCESS);
			System.out.println("ㅁㅁㅁㅁㅁㅁㅁㅁㅁㅁ");
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
	
	@Description("폴더트리 부서 가져오기")
	@RequestMapping(value = "/getDeptTree")
	@ResponseBody
	public JSONArray getDeptTree(@RequestParam Map<String, Object> params) throws Exception {
//		JSONArray node = null;
		JSONArray node = new JSONArray();
		try {
			node = OrgHelper.manager.getDeptTree(params);
//			node.add(node);
			System.out.println("zzzzzzzzzz");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("xxxxxxxxxxxxxxxxxxx");
		}
		return node;
	}
	
//	@Description("부서 별 유저")
//	@RequestMapping(value = "getUserForDept")
//	@ResponseBody
//	public Map<String, Object> getUserForDept(@RequestBody Map<String, Object> params) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		try {
//			map = OrgHelper.manager.getUserForDept(params);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
}
