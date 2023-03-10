package e3ps.org.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.org.dto.UserDTO;
import e3ps.org.service.OrgHelper;

@Controller
@RequestMapping(value = "/org/**")
public class OrgController extends BaseController {

	@Description(value = "조직도 페이지")
	@GetMapping(value = "/organization")
	public ModelAndView organization() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		ArrayList<HashMap<String, String>> list = OrgHelper.manager.getDepartmentMap();
		JSONArray maks = CommonCodeHelper.manager.parseJson("MAK");
		JSONArray installs = CommonCodeHelper.manager.parseJson("INSTALL");
		model.addObject("maks", maks);
		model.addObject("installs", installs);
		model.addObject("list", list);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/org/organization-list.jsp");
		return model;
	}

	@Description(value = "사용자 조회 함수")
	@PostMapping(value = "/list")
	@ResponseBody
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = OrgHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "사용자 정보 저장 그리드 용")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> editRows = params.get("editRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			ArrayList<UserDTO> editRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> edit : editRows) {
				UserDTO dto = mapper.convertValue(edit, UserDTO.class);
				editRow.add(dto);
			}

			HashMap<String, List<UserDTO>> dataMap = new HashMap<>();
			dataMap.put("editRows", editRow); // 삭제행

			OrgHelper.service.save(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "조직도 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.addObject("method", method);
		model.setViewName("popup:/org/organization-popup");
		return model;
	}

	@Description(value = "부서 트리 구조 가져오기")
	@PostMapping(value = "/loadDepartmentTree")
	@ResponseBody
	public Map<String, Object> loadDepartmentTree(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			net.sf.json.JSONArray list = OrgHelper.manager.loadDepartmentTree(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
	
	

	@Description(value = "부서별 사용자 리스트 가져오기")
	@GetMapping(value = "/loadDepartmentUser")
	@ResponseBody
	public Map<String, Object> loadDepartmentUser(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<UserDTO> list = OrgHelper.manager.loadDepartmentUser(oid);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
}