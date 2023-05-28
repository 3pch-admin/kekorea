package e3ps.epm.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.epm.dto.EpmDTO;
import e3ps.epm.service.EpmHelper;
import e3ps.system.service.ErrorLogHelper;
import net.sf.json.JSONArray;
import wt.epm.EPMDocument;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/epm/**")
public class EpmController extends BaseController {

	@Description(value = "도면 결재 페이지")
	@GetMapping(value = "/register")
	public ModelAndView register() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		boolean isSuperviosr = CommonUtils.isSupervisor();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("isSuperviosr", isSuperviosr);
		model.setViewName("/extcore/jsp/epm/epm-register.jsp");
		return model;
	}

	@Description(value = "도면 결재 함수")
	@PostMapping(value = "/register")
	@ResponseBody
	public Map<String, Object> register(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			EpmHelper.service.register(params);
			result.put("result", SUCCESS);
			result.put("msg", REGISTER_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
			ErrorLogHelper.service.create(e.toString(), "/epm/register", "도면 결재 함수");
		}
		return result;
	}

	@Description(value = "도면 조회 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		boolean isSuperviosr = CommonUtils.isSupervisor();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("isSuperviosr", isSuperviosr);
		model.setViewName("/extcore/jsp/epm/epm-list.jsp");
		return model;
	}

	@Description(value = "도면 조회(라이브러리) 페이지")
	@GetMapping(value = "/library")
	public ModelAndView library() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		boolean isSuperviosr = CommonUtils.isSupervisor();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("isSuperviosr", isSuperviosr);
		model.setViewName("/extcore/jsp/epm/epm-library-list.jsp");
		return model;
	}

	@Description(value = "KEK 도면 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = EpmHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
			ErrorLogHelper.service.create(e.toString(), "/epm/list", "KEK 도면 조회 함수");
		}
		return result;
	}

	@Description(value = "도면 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EPMDocument epm = (EPMDocument) CommonUtils.getObject(oid);
		EpmDTO dto = new EpmDTO(epm);
		JSONArray versionHistory = EpmHelper.manager.versionHistory(epm);
		JSONArray data = EpmHelper.manager.jsonAuiProject(dto.getOid());
		boolean isAutoCad = dto.getApplicationType().equalsIgnoreCase("AUTOCAD");
		boolean isCreo = dto.getApplicationType().equalsIgnoreCase("CREO");
		Vector<Map<String, Object>> secondary = ContentUtils.getSecondary(epm);
		boolean isAdmin = CommonUtils.isAdmin();
		boolean isSuperviosr = CommonUtils.isSupervisor();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("isSuperviosr", isSuperviosr);
		model.addObject("secondary", secondary);
		model.addObject("isAutoCad", isAutoCad);
		model.addObject("isCreo", isCreo);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.addObject("versionHistory", versionHistory);
		model.setViewName("popup:/epm/epm-view");
		return model;
	}

	@Description(value = "도면 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		boolean isSuperviosr = CommonUtils.isSupervisor();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("isSuperviosr", isSuperviosr);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.addObject("method", method);
		model.setViewName("popup:/epm/epm-popup");
		return model;
	}

	@Description(value = "도면 결재시 추가 할때 도번 검증")
	@PostMapping(value = "/append")
	@ResponseBody
	public Map<String, Object> append(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = EpmHelper.manager.append(params);

			// 없을시 FAIL
//			if ((boolean) result.get("exist")) {
//
//			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
			ErrorLogHelper.service.create(e.toString(), "/epm/append", "도면 결재시 도번 검증");
		}
		return result;
	}
}
