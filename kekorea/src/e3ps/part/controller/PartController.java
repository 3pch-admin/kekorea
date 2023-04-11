package e3ps.part.controller;

import java.util.HashMap;
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

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.part.beans.PartDTO;
import e3ps.part.service.PartHelper;
import e3ps.workspace.service.WorkspaceHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/part/**")
public class PartController extends BaseController {

	@Description(value = "부품 조회 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/part/part-list.jsp");
		return model;
	}

	@Description(value = "부품 조회")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = PartHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "부품 일괄 등록 리스트 페이지")
	@GetMapping(value = "/bundle")
	public ModelAndView bundle() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/part/part-bundle.jsp");
		return model;
	}

	@Description(value = "부품 일괄 등록")
	@PostMapping(value = "/bundle")
	@ResponseBody
	public Map<String, Object> bundle(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = PartHelper.service.bundle(params);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "부품 일괄 등록 IBA PART_CODE 검증 함수")
	@GetMapping(value = "/bundleValidatorNumber")
	@ResponseBody
	public Map<String, Object> bundleValidatorNumber(@RequestParam String number) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = PartHelper.manager.bundleValidatorNumber(number);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			result.put("result", FAIL);
			e.printStackTrace();
		}
		return result;
	}

	@Description(value = "부품 일괄 등록 WTPART Number 검증 함수")
	@GetMapping(value = "/bundleValidatorSpec")
	@ResponseBody
	public Map<String, Object> bundleValidatorSpec(@RequestParam String spec) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = PartHelper.manager.bundleValidatorSpec(spec);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			result.put("result", FAIL);
			e.printStackTrace();
		}
		return result;
	}

	@Description(value = "도면 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTPart part = (WTPart) CommonUtils.getObject(oid);
		PartDTO dto = new PartDTO(part);
		JSONArray history = WorkspaceHelper.manager.jsonArrayHistory(part.getMaster());
		JSONArray data = PartHelper.manager.jsonArrayAui(dto.getOid());
		JSONArray list = PartHelper.manager.list(part.getMaster());
		model.addObject("dto", dto);
		model.addObject("history", history);
		model.addObject("data", data);
		model.addObject("list", list);
		model.setViewName("popup:/part/part-view");
		return model;
	}

	@Description(value = "제작사양서 등록 리스트 페이지")
	@GetMapping(value = "/spec")
	public ModelAndView spec() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/part/part-spec.jsp");
		return model;
	}

	@Description(value = "제작사양서 등록")
	@PostMapping(value = "/spec")
	@ResponseBody
	public Map<String, Object> spec(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartHelper.service.spec(params);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

}
