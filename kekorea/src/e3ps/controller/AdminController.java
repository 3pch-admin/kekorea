//package e3ps.controller;
//
//import java.util.ArrayList;
//import java.util.Map;
//
//import org.springframework.context.annotation.Description;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.ModelAndView;
//
//import e3ps.admin.service.AdminHelper;
//import e3ps.common.code.CommonCode;
//import e3ps.common.code.service.CommonCodeHelper;
//import e3ps.erp.service.ErpHelper;
//import e3ps.org.People;
//import e3ps.org.service.OrgHelper;
//import net.sf.json.JSONArray;
//import wt.fc.ReferenceFactory;
//
//@Controller
//public class AdminController extends BaseController {
//
//	@Description("코드 생성")
//	@RequestMapping(value = "/admin/setPasswordAction")
//	@ResponseBody
//	public Map<String, Object> setPasswordAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = AdminHelper.service.changePasswordSetting(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//	
//	@Description("코드 생성")
//	@RequestMapping(value = "/admin/createCodeAction")
//	@ResponseBody
//	public Map<String, Object> createCodeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = AdminHelper.service.createCodeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("코드 삭제")
//	@RequestMapping(value = "/admin/deleteCodeAction")
//	@ResponseBody
//	public Map<String, Object> deleteCodeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = AdminHelper.service.deleteCodeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("코드 가져오기")
//	@RequestMapping(value = "/admin/getCodeTree")
//	@ResponseBody
//	public JSONArray getCodeTree(@RequestParam Map<String, Object> param) throws Exception {
//		JSONArray node = null;
//		try {
//			node = AdminHelper.manager.getCodeTree(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return node;
//	}
//	
//	@Description("코드정보 페이지")
//	@RequestMapping(value = "/admin/viewCode")
//	public ModelAndView viewUser(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		
//		CommonCode code = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			
//			code = (CommonCode) rf.getReference(oid).getObject();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		model.addObject("data", code);
//		
//		if (isPopup) {
//			model.setViewName("popup:/admin/viewCode");
//		} else {
//			model.setViewName("default:/admin/viewCode");
//		}
//		return model;
//	}
//	
//
//	@Description(" 관리 페이지")
//	@RequestMapping(value = "/admin/setPassword")
//	public ModelAndView setPassword() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/admin/setPassword");
//		return model;
//	}
//	
//
//	@Description("코드 관리 페이지")
//	@RequestMapping(value = "/admin/manageCode")
//	public ModelAndView manageCode() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/admin/manageCode");
//		return model;
//	}
//
//	@Description("접속이력 리스트 페이지")
//	@RequestMapping(value = "/admin/loginHistory")
//	public ModelAndView loginHistory() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/admin/loginHistory");
//		return model;
//	}
//
//	@Description("접속 이력 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/admin/loginHistoryAction")
//	public Map<String, Object> loginHistoryAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = AdminHelper.manager.find(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("코드 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/admin/listCodeAction")
//	public Map<String, Object> listCodeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = AdminHelper.manager.listCode(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("비밀번호 초기화 페이지")
//	@RequestMapping(value = "/admin/initPassword")
//	public ModelAndView initPassword() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/admin/initPassword");
//		return model;
//	}
//
//	@Description("유저관리 페이지")
//	@RequestMapping(value = "/admin/manageUser")
//	public ModelAndView manageUser() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/admin/viewOrg");
//		return model;
//	}
//	// end
//
//	@Description("부서 설정 페이지")
//	@RequestMapping(value = "/admin/setDept")
//	public ModelAndView setDept(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		ArrayList<People> noReglist = OrgHelper.manager.getNoRegUserListByDept(oid);
//		ArrayList<People> reglist = OrgHelper.manager.getRegUserListByDept(oid);
//		model.addObject("noReglist", noReglist);
//		model.addObject("reglist", reglist);
//		model.setViewName("popup:/admin/setDept");
//		return model;
//	}
//
//	@Description("부서 설정")
//	@RequestMapping(value = "/admin/setDeptAction")
//	@ResponseBody
//	public Map<String, Object> setDeptAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = OrgHelper.service.setDeptAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("직급 설정 페이지")
//	@RequestMapping(value = "/admin/setDuty")
//	public ModelAndView setDuty(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		String duty = (String) param.get("duty");
//		ArrayList<People> noReglist = OrgHelper.manager.getNoRegUserListByDuty(duty);
//		ArrayList<People> reglist = OrgHelper.manager.getRegUserListByDuty(duty);
//		String[] dutys = OrgHelper.dutys;
//		model.addObject("dutys", dutys);
//		model.addObject("noReglist", noReglist);
//		model.addObject("reglist", reglist);
//		model.setViewName("popup:/admin/setDuty");
//		return model;
//	}
//
//	@Description("직급 설정")
//	@RequestMapping(value = "/admin/setDutyAction")
//	@ResponseBody
//	public Map<String, Object> setDutyAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = OrgHelper.service.setDutyAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("메일관리 리스트 페이지")
//	@RequestMapping(value = "/admin/manageMail")
//	public ModelAndView manageMail(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
////		QuerySpec query = NoticeHelper.manager.find(param);
////		PageQueryUtils pager = new PageQueryUtils(param, query);
////		model.addObject("pager", pager);
//		model.setViewName("default:/admin/manageMail");
//		return model;
//	}
//	
//	@Description("코드 등록 페이지")
//	@RequestMapping(value = "/admin/createCode")
//	public ModelAndView createCode() throws Exception {
//		ModelAndView model = new ModelAndView();
//		ArrayList<String> customer = CommonCodeHelper.manager.getCommonCode("CUSTOMER");
//
//		model.addObject("customer", customer);
//		model.setViewName("default:/admin/createCode");
//		return model;
//	}
//	
//	@Description("코드수정")
//	@RequestMapping(value = "/admin/modifyCodeAction")
//	@ResponseBody
//	public Map<String, Object> modifyCodeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			System.out.println("######### controller modifyCodeAction");
//			
//			map = CommonCodeHelper.service.modifyCodeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//	
//	
//	@Description("erp 산출물 페이지")
//	@RequestMapping(value = "/admin/searchErpOutput")
//	public ModelAndView searchErpOutput() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/admin/searchErpOutput");
//		return model;
//	}
//	
//	@Description("erp 산출물 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/admin/searchErpOutputAction")
//	public Map<String, Object> searchErpOutputAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			
//			map = ErpHelper.manager.getOutputList(param);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//	@Description("erp 수배표 관리 페이지")
//	@RequestMapping(value = "/admin/searchErpPjtBom")
//	public ModelAndView searchErpPjtBom() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/admin/searchErpPjtBom");
//		return model;
//	}
//	
//	@Description("erp 수배표 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/admin/searchErpPjtBomAction")
//	public Map<String, Object> searchErpPjtBomAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			
//			map = ErpHelper.manager.getPjtBomAction(param);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//	@Description("erp 품목 관리 페이지")
//	@RequestMapping(value = "/admin/searchErpPart")
//	public ModelAndView searchErpPart() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/admin/searchErpPart");
//		return model;
//	}
//	
//	@Description("erp 품목 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/admin/searchErpPartAction")
//	public Map<String, Object> searchErpPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			
//			map = ErpHelper.manager.getErpPartAction(param);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//	@Description("erp unit bom 관리 페이지")
//	@RequestMapping(value = "/admin/searchErpUnitBom")
//	public ModelAndView searchErpUnitBom() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/admin/searchErpOutput");
//		return model;
//	}
//	
//	@Description("erp unit bom 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/admin/searchErpUnitBomAction")
//	public Map<String, Object> searchErpUnitBomAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			
//			map = ErpHelper.manager.getOutputList(param);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//	
//	
//	
//	
//}
