//package e3ps.controller;
//
//import java.util.Map;
//
//import org.springframework.context.annotation.Description;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import e3ps.admin.service.AdminHelper;
//import e3ps.common.code.service.CommonCodeHelper;
//import e3ps.common.util.CommonUtils;
//import e3ps.org.service.OrgHelper;
//import e3ps.part.service.PartHelper;
//
//@Controller
//public class BindSelectorController extends BaseController {

//	@Description("설치장소 검색 바인딩")
//	@RequestMapping(value = "/bind/getInstall")
//	@ResponseBody
//	public Map<String, Object> getInstall(@RequestParam Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = CommonCodeHelper.manager.getInstall(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("사용자 검색 바인딩")
//	@RequestMapping(value = "/bind/getUserBind")
//	@ResponseBody
//	public Map<String, Object> getUserBind(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = OrgHelper.manager.getUserBind(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("상태값 검색 바인딩")
//	@RequestMapping(value = "/bind/getStateBind")
//	@ResponseBody
//	public Map<String, Object> getStateBind(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = CommonUtils.getStateBind(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("부품 타입 검색 바인딩")
//	@RequestMapping(value = "/bind/getPartTypeBind")
//	@ResponseBody
//	public Map<String, Object> getPartTypeBind(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = CommonUtils.getPartTypeBind(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("BOM 검색 바인딩")
//	@RequestMapping(value = "/bind/getBomBind")
//	@ResponseBody
//	public Map<String, Object> getBomBind(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.manager.getBomBind(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("코드타입 검색 바인딩")
//	@RequestMapping(value = "/bind/getCodeType")
//	@ResponseBody
//	public Map<String, Object> getCodeType(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = AdminHelper.manager.getCodeType(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}

	// end
//}