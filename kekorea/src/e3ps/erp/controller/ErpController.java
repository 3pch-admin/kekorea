package e3ps.erp.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.controller.BaseController;
import e3ps.erp.service.ErpHelper;

@Controller
@RequestMapping(value = "/erp/**")
public class ErpController extends BaseController {

	@Description(value = "ERP 로그 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/erp/erp-list.jsp");
		return model;
	}

	@Description(value = "파트리스트 등록시 품목 정보 가져오기")
	@GetMapping(value = "/partListItemValue")
	@ResponseBody
	public Map<String, Object> partListItemValue(@RequestParam String partNo, @RequestParam String quantity)
			throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (ErpHelper.isOperation) {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("partNo", partNo);
				params.put("quantity", quantity);
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "부품 일괄 등록시 ERP 조회 해서 데이터 가져오기")
	@GetMapping(value = "/bundleGetErpData")
	@ResponseBody
	public Map<String, Object> bundleGetErpData(@RequestParam String spec) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (ErpHelper.isOperation) {
				result = ErpHelper.manager.bundleGetErpData(spec);
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "수배표 YCODE 체크")
	@GetMapping(value = "/validate")
	@ResponseBody
	public Map<String, Object> validate(@RequestParam String partNo) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (ErpHelper.isOperation) {
				result = ErpHelper.manager.validate(partNo);
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "수배표 UNITNAME 가져오기")
	@GetMapping(value = "/getUnitName")
	@ResponseBody
	public Map<String, Object> getUnitName(@RequestParam int lotNo) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (ErpHelper.isOperation) {
				result = ErpHelper.manager.getUnitName(lotNo);
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "수배표 부품정보 가져오기")
	@GetMapping(value = "/getErpItemByPartNoAndQuantity")
	@ResponseBody
	public Map<String, Object> getErpItemByPartNoAndQuantity(@RequestParam String partNo, @RequestParam int quantity)
			throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (ErpHelper.isOperation) {
				result = ErpHelper.manager.getErpItemByPartNoAndQuantity(partNo, quantity);
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "규격으로 ERP 부품정보 가져오기")
	@GetMapping(value = "/getErpItemBySpec")
	@ResponseBody
	public Map<String, Object> getErpItemBySpec(@RequestParam String spec) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			if (ErpHelper.isOperation) {
				result = ErpHelper.manager.getErpItemBySpec(spec);
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}
}
