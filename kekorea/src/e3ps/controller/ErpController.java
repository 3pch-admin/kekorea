package e3ps.controller;

import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.epm.service.EpmHelper;
import e3ps.erp.service.ErpHelper;

@Controller
public class ErpController extends BaseController {

	@Description("체크 YCODE")
	@RequestMapping(value = "/erp/checkYCode")
	@ResponseBody
	public Map<String, Object> checkYCode(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			if (ErpHelper.isSendERP) {
				map = ErpHelper.manager.checkYCode(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("DWG 전송")
	@RequestMapping(value = "/erp/sendERPDWGAction")
	@ResponseBody
	public Map<String, Object> sendERPDWGAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = EpmHelper.service.sendDWGAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("PDF 전송")
	@RequestMapping(value = "/erp/sendERPPDFAction")
	@ResponseBody
	public Map<String, Object> sendERPPDFAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = EpmHelper.service.sendPDFAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("ERP 전송 이력")
	@RequestMapping(value = "/erp/viewERPHistory")
	public ModelAndView viewERPHistory(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		ReferenceFactory rf = new ReferenceFactory();
		try {
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (isPopup) {
			model.setViewName("popup:/erp/viewERPHistory");
		} else {
			model.setViewName("default:/erp/viewERPHistory");
		}
		return model;
	}

	@Description("품목정보 가져오기")
	@RequestMapping(value = "/erp/getKEK_VDAItemBySpec")
	@ResponseBody
	public Map<String, Object> getKEK_VDAItemBySpec(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			if (ErpHelper.isSendERP) {
				map = ErpHelper.manager.getKEK_VDAItemBySpec(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("품목정보 가져오기")
	@RequestMapping(value = "/erp/getKEK_VDAItem")
	@ResponseBody
	public Map<String, Object> getKEK_VDAItem(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			if (ErpHelper.isSendERP) {
				map = ErpHelper.manager.getKEK_VDAItem(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("품목정보 가져오기")
	@RequestMapping(value = "/erp/getKEK_LotNo")
	@ResponseBody
	public Map<String, Object> getKEK_LotNo(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			if (ErpHelper.isSendERP) {
				map = ErpHelper.manager.getKEK_LotNo(param);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	
}