package e3ps.korea.configSheet.controller;

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

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.dto.KeDrawingDTO;
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.beans.ConfigSheetDTO;
import e3ps.korea.configSheet.service.ConfigSheetHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/configSheet/**")
public class ConfigSheetController extends BaseController {

	@Description(value = "CONFIG SHEET 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/korea/configSheet/configSheet-list.jsp");
		return model;
	}

	@Description(value = "CONFIG SHEET 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ConfigSheetHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "CONFIG SHEET 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create(@RequestParam(required = false) String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray categorys = CommonCodeHelper.manager.parseJson("CATEGORY");

		net.sf.json.JSONArray baseData = null;
		if (!StringUtils.isNull(oid)) {
			baseData = ConfigSheetHelper.manager.loadBaseGridData(oid);
		} else {
			baseData = ConfigSheetHelper.manager.loadBaseGridData();
		}
		model.addObject("oid", oid);
		model.addObject("baseData", baseData);
		model.addObject("categorys", categorys);
		model.setViewName("popup:/korea/configSheet/configSheet-create");
		return model;
	}

	@Description(value = "CONFIG SHEET 등록")
	@PostMapping(value = "/create")
	@ResponseBody
	public Map<String, Object> create(@RequestBody ConfigSheetDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ConfigSheetHelper.service.create(dto);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "CONFIG SHEET 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		ConfigSheet configSheet = (ConfigSheet) CommonUtils.getObject(oid);
		net.sf.json.JSONArray data = ConfigSheetHelper.manager.loadBaseGridData(oid);
		ConfigSheetDTO dto = new ConfigSheetDTO(configSheet);
		model.addObject("oid", oid);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.setViewName("popup:/korea/configSheet/configSheet-view");
		return model;
	}
}
