package e3ps.admin.specCode.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import e3ps.admin.spec.dto.SpecCodeDTO;
import e3ps.admin.spec.service.SpecCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/spec/**")
public class SpecCodeController extends BaseController {

	@Description(value = "사양 관리 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/admin/spec/spec-list.jsp");
		return model;
	}

	@Description(value = "사양 관리 리스트 가져 오는 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = SpecCodeHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "사양 관리 그리드 저장")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> addRows = params.get("addRows");
		ArrayList<LinkedHashMap<String, Object>> editRows = params.get("editRows");
		ArrayList<LinkedHashMap<String, Object>> removeRows = params.get("removeRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ArrayList<SpecCodeDTO> addRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> add : addRows) {
				SpecCodeDTO dto = mapper.convertValue(add, SpecCodeDTO.class);
				addRow.add(dto);
			}

			ArrayList<SpecCodeDTO> editRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> edit : editRows) {
				SpecCodeDTO dto = mapper.convertValue(edit, SpecCodeDTO.class);
				editRow.add(dto);
			}

			ArrayList<SpecCodeDTO> removeRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> remove : removeRows) {
				SpecCodeDTO dto = mapper.convertValue(remove, SpecCodeDTO.class);
				removeRow.add(dto);
			}

			HashMap<String, List<SpecCodeDTO>> dataMap = new HashMap<>();
			dataMap.put("addRows", addRow); // 추가행
			dataMap.put("editRows", editRow); // 수정행
			dataMap.put("removeRows", removeRow); // 삭제행

			SpecCodeHelper.service.save(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}
}
