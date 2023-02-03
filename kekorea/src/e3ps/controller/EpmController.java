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
//import org.springframework.web.servlet.ModelAndView;
//
//import e3ps.epm.beans.EpmViewData;
//import e3ps.epm.service.EpmHelper;
//import wt.epm.EPMDocument;
//import wt.epm.EPMDocumentType;
//import wt.fc.ReferenceFactory;
//
//@Controller
//public class EpmController extends BaseController {

//	@Description("뷰어 생성 페이지")
//	@RequestMapping(value = "/epm/createViewer")
//	public ModelAndView createViewer() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/epm/createViewer");
//		return model;
//	}
//
//	@Description("뷰어 생성")
//	@RequestMapping(value = "/epm/createViewerAction")
//	@ResponseBody
//	public Map<String, Object> createViewerAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.createViewerAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("뷰어 목록 가져오기")
//	@RequestMapping(value = "/epm/listViewerAction")
//	@ResponseBody
//	public Map<String, Object> listViewerAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.manager.findViewer(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("뷰어 조회 페이지")
//	@RequestMapping(value = "/epm/listViewer")
//	public ModelAndView listViewer() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/epm/listViewer");
//		return model;
//	}
//
//	@Description("DWG 전송")
//	@RequestMapping(value = "/epm/sendDWGAction")
//	@ResponseBody
//	public Map<String, Object> sendDWGAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.sendDWGAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("코드 생성")
//	@RequestMapping(value = "/epm/createPartCodeAction")
//	@ResponseBody
//	public Map<String, Object> createPartCodeAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.createPartCodeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("도면 결재")
//	@RequestMapping(value = "/epm/approvalEpmAction")
//	@ResponseBody
//	public Map<String, Object> approvalEpmAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.approvalEpmAction(param);
//			map.put("none", false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//	
//	@Description("도면 결재")
//	@RequestMapping(value = "/epm/approvalModifyEpmAction")
//	@ResponseBody
//	public Map<String, Object> approvalModifyEpmAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.approvalModifyEpmAction(param);
//			map.put("none", false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("도면 결재 페이지")
//	@RequestMapping(value = "/epm/modifyApprovalEpm")
//	public ModelAndView modifyApprovalEpm(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		String moid = (String) param.get("moid");
//		model.addObject("oid", oid);
//		model.addObject("moid", moid);
//		model.setViewName("popup:/epm/modifyApprovalEpm");
//		return model;
//	}
//
//	@Description("도면 결재 페이지")
//	@RequestMapping(value = "/epm/approvalEpm")
//	public ModelAndView approvalEpm() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/epm/approvalEpm");
//		return model;
//	}
//
//	@Description("2D 도면 확인")
//	@RequestMapping(value = "/epm/checkDrawing")
//	@ResponseBody
//	public Map<String, Object> checkDrawing(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.manager.checkDrawing(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("구매품 조회 페이지")
//	@RequestMapping(value = "/epm/listLibraryEpm")
//	public ModelAndView listLibraryEpm() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/epm/listLibraryEpm");
//		return model;
//	}
//
//	@Description("가공품 조회 페이지")
//	@RequestMapping(value = "/epm/listProductEpm")
//	public ModelAndView listProductEpm() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/epm/listProductEpm");
//		return model;
//	}
//
//	@Description("가공품 목록 가져오기")
//	@RequestMapping(value = "/epm/listProductEpmAction")
//	@ResponseBody
//	public Map<String, Object> listProductEpmAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			param.put("context", EpmHelper.PRODUCT_CONTEXT);
//			map = EpmHelper.manager.find(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("구매품 목록 가져오기")
//	@RequestMapping(value = "/epm/listLibraryEpmAction")
//	@ResponseBody
//	public Map<String, Object> listLibraryEpmAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			param.put("context", EpmHelper.LIBRARY_CONTEXT);
//			map = EpmHelper.manager.find(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("도면출력 페이지")
//	@RequestMapping(value = "/epm/printEpm")
//	public ModelAndView printEpm() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/epm/printEpm");
//		return model;
//	}
//
//	@Description("도면 추가 페이지")
//	@RequestMapping(value = "/epm/addEpm")
//	public ModelAndView addEpm() throws Exception {
//		ModelAndView model = new ModelAndView();
//		EPMDocumentType[] epmTypes = EPMDocumentType.getEPMDocumentTypeSet();
//		model.addObject("epmTypes", epmTypes);
//		model.setViewName("popup:/epm/addEpm");
//		return model;
//	}
//
//	@Description("도면 추가")
//	@RequestMapping(value = "/epm/addEpmAction")
//	@ResponseBody
//	public Map<String, Object> addEpmAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.addEpmAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("2D (DWG) 출력")
//	@RequestMapping(value = "/epm/downDwg")
//	@ResponseBody
//	public Map<String, Object> downDwg(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.downDwg(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("2D (DRW) 출력")
//	@RequestMapping(value = "/epm/downDrw")
//	@ResponseBody
//	public Map<String, Object> downDrw(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.downDrw(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("PDF 출력")
//	@RequestMapping(value = "/epm/downPdf")
//	@ResponseBody
//	public Map<String, Object> downPdf(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.downPdf(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("도면정보 페이지")
//	@RequestMapping(value = "/epm/viewEpm")
//	public ModelAndView viewEpm(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		String popup = (String) param.get("popup");
//		EPMDocument epm = null;
//		EpmViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			epm = (EPMDocument) rf.getReference(oid).getObject();
//			data = new EpmViewData(epm);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (Boolean.parseBoolean(popup)) {
//			model.setViewName("popup:/epm/viewEpm");
//		} else {
//			model.setViewName("default:/epm/viewEpm");
//		}
//		return model;
//	}
//
//	// end
//
//	@Description("일괄 개정")
//	@RequestMapping(value = "/epm/reviseCadDataAction")
//	@ResponseBody
//	public Map<String, Object> reviseCadDataAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.reviseCadDataAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("속성 업로드")
//	@RequestMapping(value = "/epm/uploadEpmAttrAction")
//	@ResponseBody
//	public Map<String, Object> uploadEpmAttrAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.uploadEpmAttrAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("도면등록 페이지")
//	@RequestMapping(value = "/epm/createEpm")
//	public ModelAndView createEpm(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/epm/createEpm");
//		return model;
//	}
//
//	@Description("도면-> 설변 페이지")
//	@RequestMapping(value = "/epm/viewToEChange")
//	public ModelAndView viewToEChange(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		String popup = (String) param.get("popup");
//		EPMDocument epm = null;
//		EpmViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			epm = (EPMDocument) rf.getReference(oid).getObject();
//			data = new EpmViewData(epm);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("oid", oid);
//		model.addObject("data", data);
//		if (Boolean.parseBoolean(popup)) {
//			model.setViewName("popup:/epm/viewToEChange");
//		} else {
//			model.setViewName("default:/epm/viewToEChange");
//		}
//		return model;
//	}
//
//	@Description("도면->이미지 페이지")
//	@RequestMapping(value = "/epm/viewToImage")
//	public ModelAndView viewToImage(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		String popup = (String) param.get("popup");
//		EPMDocument epm = null;
//		EpmViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			epm = (EPMDocument) rf.getReference(oid).getObject();
//			data = new EpmViewData(epm);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("oid", oid);
//		model.addObject("data", data);
//		if (Boolean.parseBoolean(popup)) {
//			model.setViewName("popup:/epm/viewToImage");
//		} else {
//			model.setViewName("default:/epm/viewToImage");
//		}
//		return model;
//	}
//
//	@Description("속성 일괄 입력")
//	@RequestMapping(value = "/epm/inputDrwAttr")
//	public ModelAndView inputDrwAttr(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/epm/inputDrwAttr");
//		return model;
//	}
//
//	@Description("일괄 개정")
//	@RequestMapping(value = "/epm/reviseCadData")
//	public ModelAndView reviseCadData(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/epm/reviseCadData");
//		return model;
//	}
//
//	@Description("모든 관련 도면, 변환 파일 다운로드")
//	@RequestMapping(value = "/epm/downAll")
//	@ResponseBody
//	public Map<String, Object> downAll(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = EpmHelper.service.downAll(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//}
