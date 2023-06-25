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
import e3ps.doc.dto.DocumentDTO;
import e3ps.doc.service.DocumentHelper;
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
		boolean isSupervisor = CommonUtils.isSupervisor();
		WTUser sessionUser = CommonUtils.sessionUser();
		WTUser user = (WTUser) CommonUtils.getObject(oid);
		boolean isSessionUser = user.getName().equals(sessionUser.getName());
		UserDTO dto = new UserDTO(user);
		model.addObject("isSessionUser", isSessionUser);
		model.addObject("dto", dto);
		model.addObject("isSupervisor", isSupervisor);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/org/organization-view");
		return model;
	}

	@Description(value = "사용자 정보 수정 페이지")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser user = (WTUser) CommonUtils.getObject(oid);

		ArrayList<HashMap<String, String>> list = OrgHelper.manager.getDepartmentMap();
		String[] dutys = new String[] { "사장", "부사장", "PL", "TL" };

		UserDTO dto = new UserDTO(user);
		model.addObject("list", list);
		model.addObject("dutys", dutys);
		model.addObject("dto", dto);
		model.setViewName("popup:/org/organization-modify");
		return model;
	}

	@Description(value = "사용자 정보 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody UserDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			OrgHelper.service.modify(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/org/modify", "사용자 정보 수정 함수");
		}
		return result;
	}

	@Description(value = "사용자 퇴사처리 함수")
	@ResponseBody
	@GetMapping(value = "/fire")
	public Map<String, Object> fire(@RequestParam String oid, @RequestParam String fire) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			boolean isFire = Boolean.parseBoolean(fire);
			OrgHelper.service.fire(oid, isFire);
			if (isFire) {
				result.put("msg", "퇴사처리 되었습니다.");
			} else {
				result.put("msg", "복직처리 되었습니다.");
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/doc/modify", "사용자 퇴사처리 함수");
		}
		return result;
	}

	@Description(value = "비밀번호 변경 페이지")
	@GetMapping(value = "/password")
	public ModelAndView password() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/org/password-edit");
		return model;
	}

	@Description(value = "비밀번호 변경 함수")
	@ResponseBody
	@PostMapping(value = "/password")
	public Map<String, Object> password(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			OrgHelper.service.password(params);
			result.put("msg", "비밀번호가 변경 되었습니다.\n자동으로 시스템에서 로그아웃 되어집니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/org/password", "비밀번호 변경 함수");
		}
		return result;
	}

	@Description(value = "비밀번호 초기화 함수")
	@ResponseBody
	@PostMapping(value = "/init")
	public Map<String, Object> init(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			OrgHelper.service.init(params);
			result.put("msg", "비밀번호가 1로 초기화 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/org/password", "비밀번호 초기화 함수");
		}
		return result;
	}

	@Description(value = "비밀번호 기간 지났을시 변경 팝업창")
	@GetMapping(value = "/notice")
	public ModelAndView notice(@RequestParam String gap) throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser user = CommonUtils.sessionUser();
		UserDTO dto = new UserDTO(user);
		model.addObject("dto", dto);
		model.addObject("gap", Integer.parseInt(gap));
		model.setViewName("popup:/org/password-notice");
		return model;
	}
}