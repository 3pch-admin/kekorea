package e3ps.epm.numberRule.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.admin.numberRuleCode.service.NumberRuleCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.dto.KeDrawingDTO;
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import e3ps.epm.numberRule.NumberRule;
import e3ps.epm.numberRule.dto.NumberRuleDTO;
import e3ps.epm.numberRule.service.NumberRuleHelper;
import e3ps.org.Department;
import e3ps.org.service.OrgHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/numberRule/**")
public class NumberRuleController extends BaseController {

	@Description(value = "KEK 도번 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray sizes = NumberRuleCodeHelper.manager.parseJson("SIZE");
		JSONArray drawingCompanys = NumberRuleCodeHelper.manager.parseJson("DRAWING_COMPANY");
		JSONArray writtenDocuments = NumberRuleCodeHelper.manager.parseJson("WRITTEN_DOCUMENT");
		JSONArray businessSectors = NumberRuleCodeHelper.manager.parseJson("BUSINESS_SECTOR");
		JSONArray classificationWritingDepartments = NumberRuleCodeHelper.manager
				.parseJson("CLASSIFICATION_WRITING_DEPARTMENT");
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		Timestamp time = new Timestamp(new Date().getTime());
		model.addObject("time", time);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("drawingCompanys", drawingCompanys);
		model.addObject("writtenDocuments", writtenDocuments);
		model.addObject("businessSectors", businessSectors);
		model.addObject("classificationWritingDepartments", classificationWritingDepartments);
		model.addObject("sizes", sizes);
		model.setViewName("/extcore/jsp/epm/numberRule/numberRule-list.jsp");
		return model;
	}

	@Description(value = "KEK 도번 리스트 가져 오는 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = NumberRuleHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "KEK 도번 등록 함수")
	@ResponseBody
	@PostMapping(value = "/save")
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> addRows = params.get("addRows");
		ArrayList<LinkedHashMap<String, Object>> editRows = params.get("editRows");
		ArrayList<LinkedHashMap<String, Object>> removeRows = params.get("removeRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			ArrayList<NumberRuleDTO> addRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> add : addRows) {
				NumberRuleDTO dto = mapper.convertValue(add, NumberRuleDTO.class);
				addRow.add(dto);
			}

			ArrayList<NumberRuleDTO> editRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> edit : editRows) {
				NumberRuleDTO dto = mapper.convertValue(edit, NumberRuleDTO.class);
				editRow.add(dto);
			}

			ArrayList<NumberRuleDTO> removeRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> remove : removeRows) {
				NumberRuleDTO dto = mapper.convertValue(remove, NumberRuleDTO.class);
				removeRow.add(dto);
			}
			
			
			result = NumberRuleHelper.manager.isValid(addRow, editRow);
			// true 중복있음
			if ((boolean) result.get("isExist")) {
				result.put("result", FAIL);
				return result;
			}
			
			HashMap<String, List<NumberRuleDTO>> dataMap = new HashMap<>();
			dataMap.put("addRows", addRow); // 추가행
			dataMap.put("editRows", editRow); // 수정행
			dataMap.put("removeRows", removeRow); // 삭제행


			NumberRuleHelper.service.save(dataMap);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "KEK 도번 생성 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray drawingCompanys = CommonCodeHelper.manager.parseJson("DRAWING_COMPANY");
		JSONArray writtenDocuments = CommonCodeHelper.manager.parseJson("WRITTEN_DOCUMENT");
		JSONArray businessSectors = CommonCodeHelper.manager.parseJson("BUSINESS_SECTOR");
		JSONArray classificationWritingDepartment = CommonCodeHelper.manager
				.parseJson("CLASSIFICATION_WRITING_DEPARTMENT");
		model.addObject("drawingCompanys", drawingCompanys);
		model.addObject("writtenDocuments", writtenDocuments);
		model.addObject("businessSectors", businessSectors);
		model.addObject("classificationWritingDepartment", classificationWritingDepartment);
		model.setViewName("popup:/epm/numberRule/numberRule-create");
		return model;
	}

	@Description(value = "KEK 최종 도번 가져오기")
	@ResponseBody
	@GetMapping(value = "/last")
	public Map<String, Object> last(@RequestParam String number) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = NumberRuleHelper.manager.last(number);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "KEK 도면 개정 페이지")
	@GetMapping(value = "/revise")
	public ModelAndView revise() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/epm/numberRule/numberRule-revise");
		return model;
	}

	@Description(value = "KEK 도면 개정")
	@ResponseBody
	@PostMapping(value = "/revise")
	public Map<String, Object> revise(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> addRows = params.get("addRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			ArrayList<NumberRuleDTO> addRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> add : addRows) {
				NumberRuleDTO dto = mapper.convertValue(add, NumberRuleDTO.class);
				addRow.add(dto);
			}

			HashMap<String, List<NumberRuleDTO>> dataMap = new HashMap<>();
			dataMap.put("addRows", addRow); // 추가행

			for (NumberRuleDTO dto : addRow) {
				NumberRule numberRule = (NumberRule) CommonUtils.getObject(dto.getOid()); // 원본
				String number = dto.getNumber(); // 변경 되는 값
				if (!number.equals(numberRule.getMaster().getNumber())) {
					result.put("result", FAIL);
					result.put("msg", "개정전 후의 도번이 일치 하지 않습니다.\n데이터를 확인 해주세요.");
					return result;
				}
			}

			NumberRuleHelper.service.revise(dataMap);
			result.put("msg", REVISE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
	
	
	@Description(value = "도번 결재등록 페이지")
	@GetMapping(value = "/register")
	public ModelAndView register() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/epm/numberRule/register-popup");
		return model;
	}
}
