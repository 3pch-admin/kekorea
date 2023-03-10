package e3ps.doc.controller;

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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.doc.service.DocumentHelper;
import e3ps.korea.cip.dto.CipDTO;
import e3ps.korea.cip.service.CipHelper;

@Controller
@RequestMapping(value = "/document/**")
public class DocumentController extends BaseController {

	@Description(value = "문서 조회 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/document/document-list.jsp");
		return model;
	}

	@Description(value = "문서 목록")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "문서 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/document/document-create");
		return model;
	}

	@Description(value = "문서 결재 페이지")
	@GetMapping(value = "/register")
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public ModelAndView register() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/document/document-register.jsp");
		return model;
	}

	@Description(value = "문서 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.addObject("method", method);
		model.setViewName("popup:/document/document-popup");
		return model;
	}

	@Description(value = "문서 결재")
	@PostMapping(value = "/register")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.register(params);
			result.put("result", SUCCESS);
			result.put("msg", REGISTER_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "문서 번호")
	@PostMapping(value = "/setNumber")
	@ResponseBody
	public Map<String, Object> setNumber(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String number = DocumentHelper.manager.setNumber(params);
			result.put("result", SUCCESS);
			result.put("number", number);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
}