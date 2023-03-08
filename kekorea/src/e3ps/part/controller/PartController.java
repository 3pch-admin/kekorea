package e3ps.part.controller;

import java.util.HashMap;
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

import e3ps.common.controller.BaseController;
import e3ps.part.service.PartHelper;

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
		model.setViewName("/extcore/jsp/part/part-bundle.jsp");
		return model;
	}

	@Description(value = "부품 일괄 등록 검증 검증 함수")
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

	@Description(value = "부품 일괄 등록 규격 검증 함수")
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
}
