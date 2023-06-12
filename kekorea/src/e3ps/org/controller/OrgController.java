package e3ps.org.controller;

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

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.org.dto.UserDTO;
import e3ps.org.service.OrgHelper;
import e3ps.system.service.ErrorLogHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/org/**")
public class OrgController extends BaseController {

	@Description(value = "조직도 페이지")
	@GetMapping(value = "/organization")
	public ModelAndView organization() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		ArrayList<HashMap<String, String>> list = OrgHelper.manager.getDepartmentMap();
		JSONArray maks = CommonCodeHelper.manager.parseJson("MAK");
		model.addObject("sessionUser", sessionUser);
		model.addObject("maks", maks);
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
			result.put("msg", e.toString());
			result.put("result", FAIL);
			ErrorLogHelper.service.create(e.toString(), "/org/list", "사용자 조회 함수");
		}
		return result;
	}

	@Description(value = "사용자 조회 함수 - 결재선 지정")
	@PostMapping(value = "/loadUser")
	@ResponseBody
	public Map<String, Object> loadUser(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = OrgHelper.manager.loadUser(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
			ErrorLogHelper.service.create(e.toString(), "/org/list", "사용자 조회 함수 - 결재선 지정");
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
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "조직도 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String multi, @RequestParam String openerId) throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		ArrayList<HashMap<String, String>> list = OrgHelper.manager.getDepartmentMap();
		JSONArray maks = CommonCodeHelper.manager.parseJson("MAK");
		model.addObject("sessionUser", sessionUser);
		model.addObject("maks", maks);
		model.addObject("list", list);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.addObject("openerId", openerId);
		model.setViewName("popup:/org/organization-popup");
		return model;
	}

	@Description(value = "부서 트리 구조 가져오기")
	@PostMapping(value = "/loadDepartmentTree")
	@ResponseBody
	public Map<String, Object> loadDepartmentTree(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = OrgHelper.manager.loadDepartmentTree(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
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
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "정보 KEY-VALUE")
	@PostMapping(value = "/keyValue")
	@ResponseBody
	public Map<String, Object> keyValue(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ArrayList<Map<String, String>> list = OrgHelper.manager.keyValue(params);
			result.put("result", SUCCESS);
			result.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "사용자 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser user = (WTUser) CommonUtils.getObject(oid);
		UserDTO dto = new UserDTO(user);
		model.addObject("dto", dto);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/org/organization-view.jsp");
		return model;
	}
}