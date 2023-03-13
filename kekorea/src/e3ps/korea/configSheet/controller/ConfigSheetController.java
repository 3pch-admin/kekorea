package e3ps.korea.configSheet.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.admin.spec.service.SpecHelper;
import e3ps.common.controller.BaseController;
import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.doc.meeting.service.MeetingHelper;
import e3ps.korea.configSheet.ConfigSheetDTO;
import e3ps.korea.configSheet.service.ConfigSheetHelper;

@Controller
@RequestMapping(value = "/configSheet/**")
public class ConfigSheetController extends BaseController {

	@Description(value = "CONFIG SHEET 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
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
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray categorys = CommonCodeHelper.manager.parseJson("CATEGORY");
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
}
