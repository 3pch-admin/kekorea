package e3ps.bom.partlist.controller;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.dto.PartListMasterViewData;
import e3ps.bom.partlist.service.PartlistHelper;
import e3ps.common.controller.BaseController;
import wt.fc.ReferenceFactory;

@Controller
@RequestMapping(value = "/partlist/**")
public class PartlistController extends BaseController {

	@Description(value = "수배표 조회 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/bom/partlist/partlist-list.jsp");
		return model;
	}

	@Description(value = "수배표 조회")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = PartlistHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", false);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "수배표 등록")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/bom/partList/partList-create");
		return model;
	}

	@Description(value = "수배된 리스트 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray data = PartlistHelper.manager.getData(oid);
		model.addObject("data", data);
		model.setViewName("popup:/bom/partlist/partlist-view");
		return model;
	}
}