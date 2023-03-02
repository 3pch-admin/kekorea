package e3ps.erp.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.erp.service.ErpHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/erp/**")
public class ErpController extends BaseController {

	@Description(value = "ERP 로그 리스트 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
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
}
