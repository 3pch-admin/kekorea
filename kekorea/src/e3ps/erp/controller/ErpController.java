package e3ps.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import e3ps.controller.BaseController;
import e3ps.erp.service.ErpHelper;
import e3ps.erp.service.ErpSendService;

@Controller
@RequestMapping(value = "/erp/**")
public class ErpController extends BaseController {

	@Autowired
	private ErpSendService service;

	
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
				List<Map<String, Object>> list = service.partListItemValue(params);
				result.put("list", list);
			}
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
}
