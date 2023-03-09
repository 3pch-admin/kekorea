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

import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.partlist.dto.PartListDTO;
import e3ps.bom.partlist.service.PartlistHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;

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

	@Description(value = "수배된 리스트 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray data = PartlistHelper.manager.getData(oid);
		model.addObject("data", data);
		model.setViewName("popup:/bom/partlist/partlist-view");
		return model;
	}

	@Description(value = "수배된 비교 페이지")
	@GetMapping(value = "/compare")
	public ModelAndView compare(@RequestParam String loid, @RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		PartListMasterProjectLink link = (PartListMasterProjectLink) CommonUtils.getObject(loid);
		PartListDTO dto = new PartListDTO(link);
		JSONArray data = PartlistHelper.manager.getData(oid);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.setViewName("popup:/bom/partlist/partlist-compare");
		return model;
	}

	@Description(value = "수배표 팝업 조회 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/bom/partlist/partlist-popup");
		return model;
	}

	@Description(value = "수배표 비교")
	@ResponseBody
	@PostMapping(value = "/compare")
	public Map<String, Object> compare(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = PartlistHelper.manager.compare(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", false);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "수배표 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/bom/partlist/partlist-create");
		return model;
	}

	@Description(value = "수배표 등록")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody PartListDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartlistHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
