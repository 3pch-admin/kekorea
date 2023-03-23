package e3ps.epm.keDrawing.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.KeDrawingMaster;
import e3ps.epm.keDrawing.dto.KeDrawingDTO;
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;

@Controller
@RequestMapping(value = "/keDrawing/**")
public class KeDrawingController extends BaseController {

	@Description(value = "KE 도면 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = CommonUtils.sessionUser();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/epm/keDrawing/keDrawing-list.jsp");
		return model;
	}

	@Description(value = "KE 도면 리스트 가져 오는 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = KeDrawingHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "KE 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> addRows = params.get("addRows");
		ArrayList<LinkedHashMap<String, Object>> editRows = params.get("editRows");
		ArrayList<LinkedHashMap<String, Object>> removeRows = params.get("removeRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			ArrayList<KeDrawingDTO> addRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> add : addRows) {
				KeDrawingDTO dto = mapper.convertValue(add, KeDrawingDTO.class);
				addRow.add(dto);
			}

			ArrayList<KeDrawingDTO> editRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> edit : editRows) {
				KeDrawingDTO dto = mapper.convertValue(edit, KeDrawingDTO.class);
				editRow.add(dto);
			}

			ArrayList<KeDrawingDTO> removeRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> remove : removeRows) {
				KeDrawingDTO dto = mapper.convertValue(remove, KeDrawingDTO.class);
				removeRow.add(dto);
			}
			HashMap<String, List<KeDrawingDTO>> dataMap = new HashMap<>();
			dataMap.put("addRows", addRow); // 추가행
			dataMap.put("editRows", editRow); // 수정행
			dataMap.put("removeRows", removeRow); // 삭제행

			result = KeDrawingHelper.manager.isWorkOrder(removeRow);
			if ((boolean) result.get("workOrder")) {
				result.put("result", FAIL);
				return result;
			}

			result = KeDrawingHelper.manager.numberValidate(addRow, editRow);

			result = KeDrawingHelper.manager.isValid(addRow, editRow);
			// true 중복있음
			if ((boolean) result.get("isExist")) {
				result.put("result", FAIL);
				return result;
			}

			KeDrawingHelper.service.create(dataMap);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "KE 도면 개정 페이지")
	@GetMapping(value = "/revise")
	public ModelAndView revise() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/epm/keDrawing/keDrawing-revise");
		return model;
	}

	@Description(value = "KE 도면 개정")
	@ResponseBody
	@PostMapping(value = "/revise")
	public Map<String, Object> revise(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> addRows = params.get("addRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			ArrayList<KeDrawingDTO> addRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> add : addRows) {
				KeDrawingDTO dto = mapper.convertValue(add, KeDrawingDTO.class);
				addRow.add(dto);
			}

			HashMap<String, List<KeDrawingDTO>> dataMap = new HashMap<>();
			dataMap.put("addRows", addRow); // 추가행

			for (KeDrawingDTO dto : addRow) {
				KeDrawing keDrawing = (KeDrawing) CommonUtils.getObject(dto.getOid()); // 원본
				String keNumber = dto.getKeNumber(); // 변경 되는 값
				if (!keNumber.equals(keDrawing.getMaster().getKeNumber())) {
					result.put("result", FAIL);
					result.put("msg", "개정전 후의 도번이 일치 하지 않습니다.\n데이터를 확인 해주세요.");
					return result;
				}
			}

			KeDrawingHelper.service.revise(dataMap);
			result.put("msg", REVISE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "KE 도면 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		KeDrawing keDrawing = (KeDrawing) CommonUtils.getObject(oid);
		KeDrawingDTO dto = new KeDrawingDTO(keDrawing);
		String[] primarys = ContentUtils.getPrimary(dto.getOid());
		JSONArray list = KeDrawingHelper.manager.history(keDrawing.getMaster());
		JSONArray data = KeDrawingHelper.manager.jsonArrayAui(oid);
		model.addObject("data", data);
		model.addObject("list", list);
		model.addObject("primarys", primarys);
		model.addObject("dto", dto);
		model.setViewName("popup:/epm/keDrawing/keDrawing-view");
		return model;
	}
}
