//package e3ps.controller;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
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
//import e3ps.common.StateKeys;
//import e3ps.common.util.StringUtils;
//import e3ps.erp.service.ErpHelper;
//import e3ps.part.beans.PartViewData;
//import e3ps.part.service.PartHelper;
//import net.sf.json.JSONArray;
//import wt.fc.ReferenceFactory;
//import wt.part.PartType;
//import wt.part.WTPart;
//import wt.util.WTProperties;
//
//@Controller
//public class PartController extends BaseController {

//	@Description("문서 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/part/listDocumentAction")
//	public Map<String, Object> listDocumentAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.manager.findEplan(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("UNITBOM 트리 가져오기")
//	@RequestMapping(value = "/part/getBomUnitBomTree")
//	@ResponseBody
//	public JSONArray getBomUnitBomTree(@RequestParam Map<String, Object> param) throws Exception {
//		JSONArray list = null;
//		try {
//			list = PartHelper.manager.getBomUnitBomTree(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return list;
//	}
//
//	@Description("UNITBOM 페이지")
//	@RequestMapping(value = "/part/viewUnitBom")
//	public ModelAndView viewUnitBom(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		String popup = (String) param.get("popup");
//		boolean isPopup = Boolean.parseBoolean(popup);
//		model.addObject("oid", oid);
//		if (isPopup) {
//			model.setViewName("popup:/part/viewUnitBom");
//		} else {
//			model.setViewName("default:/part/viewPart");
//		}
//		return model;
//	}
//
//	@Description("UNIT BOM 생성")
//	@RequestMapping(value = "/part/createUnitCodeAction")
//	@ResponseBody
//	public Map<String, Object> createUnitCodeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createUnitCodeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("UNIT BOM 추가 페이지")
//	@RequestMapping(value = "/part/addUnitBom")
//	public ModelAndView addUnitBom() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("popup:/part/addUnitBom");
//		return model;
//	}
//
//	@Description("UNIT BOM 추가")
//	@RequestMapping(value = "/part/addUnitBomAction")
//	@ResponseBody
//	public Map<String, Object> addUnitBomAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.addUnitBomAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("UNIT BOM 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/part/listUnitBomAction")
//	public Map<String, Object> listUnitBomAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.manager.findUnitBom(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("UNIT BOM 조회 페이지")
//	@RequestMapping(value = "/part/listUnitBom")
//	public ModelAndView listUnitBom() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/listUnitBom");
//		return model;
//	}
//
//	@Description("YCODE 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/part/listYcodeAction")
//	public Map<String, Object> listYcodeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.manager.findYcode(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("YCODE 조회 페이지")
//	@RequestMapping(value = "/part/listYcode")
//	public ModelAndView listYcode() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/listYcode");
//		return model;
//	}
//
//	@Description("부품 수정")
//	@RequestMapping(value = "/part/modifyPartAction")
//	@ResponseBody
//	public Map<String, Object> modifyPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.modifyPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("EPLAN 결재 페이지")
//	@RequestMapping(value = "/part/approvalEplan")
//	public ModelAndView approvalEplan() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/approvalEplan");
//		return model;
//	}
//
//	@Description("EPLAN 결재")
//	@RequestMapping(value = "/part/approvalEplanAction")
//	@ResponseBody
//	public Map<String, Object> approvalEplanAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			// map = EpmHelper.service.approvalEpmAction(param);
//			map = PartHelper.service.approvalEplanAction(param);
//			map.put("none", false);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("YCODE 체크")
//	@RequestMapping(value = "/part/plmPartCheckYcode")
//	@ResponseBody
//	public Map<String, Object> plmPartCheckYcode(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.manager.plmPartCheckYcode(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("품목정보 가져오기")
//	@RequestMapping(value = "/part/plmPartDataCheck")
//	@ResponseBody
//	public Map<String, Object> plmPartDataCheck(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.manager.plmPartDataCheck(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("코드 생성")
//	@RequestMapping(value = "/part/createCodeAction")
//	@ResponseBody
//	public Map<String, Object> createCodeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createCodeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("UNIT BOM 중복확인")
//	@RequestMapping(value = "/part/checkUnitBom")
//	@ResponseBody
//	public Map<String, Object> checkUnitBom(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ErpHelper.service.checkUnitBom(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("UNIT BOM 재전송")
//	@RequestMapping(value = "/part/reSendAction")
//	@ResponseBody
//	public Map<String, Object> reSendAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.reSendAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("UNIT BOM 등록")
//	@RequestMapping(value = "/part/createUnitBomAction")
//	@ResponseBody
//	public Map<String, Object> createUnitBomAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createUnitBomAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("UNIT BOM 체크")
//	@RequestMapping(value = "/part/checkUnitBomAction")
//	@ResponseBody
//	public Map<String, Object> checkUnitBomAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createUnitBomAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("부품 일괄 등록")
//	@RequestMapping(value = "/part/createProductSpecAction")
//	@ResponseBody
//	public Map<String, Object> createProductSpecAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createProductSpecAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("부품 일괄 등록")
//	@RequestMapping(value = "/part/createBundlePartAction")
//	@ResponseBody
//	public Map<String, Object> createBundlePartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createBundlePartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("부품 번호 체크")
//	@RequestMapping(value = "/part/checkPartNumber")
//	@ResponseBody
//	public Map<String, Object> checkPartNumber(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.manager.checkPartNumber(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("BOM 편집")
//	@RequestMapping(value = "/part/modifyBom")
//	public ModelAndView modifyBom() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/modifyBom");
//		return model;
//	}
//
//	@Description("부품 일괄 등록")
//	@RequestMapping(value = "/part/createBundlePart")
//	public ModelAndView createBundlePart() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/createBundlePart");
//		return model;
//	}
//
//	@Description("제품 사양서 등록")
//	@RequestMapping(value = "/part/createSpec")
//	public ModelAndView createSpec() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/createSpec");
//		return model;
//	}
//
//	@Description("UNITBOM 등록 페이지")
//	@RequestMapping(value = "/part/createUnitbom")
//	public ModelAndView createUnitbom() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/createUnitbom");
//		return model;
//	}
//
//	@Description("코드생성 페이지")
//	@RequestMapping(value = "/part/createCode")
//	public ModelAndView createCode() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/createCode");
//		return model;
//	}
//
//	@Description("BOM 엑셀출력")
//	@RequestMapping(value = "/part/exportBomExcel")
//	@ResponseBody
//	public Map<String, Object> exportExcel(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		String url = (String) param.get("url");
//		try {
//
//			File excel = PartHelper.manager.exportBomExcel(param);
//
//			map.put("result", SUCCESS);
//			map.put("url", "/Windchill/jsp/temp/pdm/excelForm/" + excel.getName());
//		} catch (Exception e) {
//			e.printStackTrace();
//			map.put("msg", "엑셀출력에 실패하였습니다.\n시스템 관리자에게 문의하세요");
//			map.put("url", url);
//			map.put("result", FAIL);
//		}
//		return map;
//	}
//
//	@Description("가공품 조회 페이지")
//	@RequestMapping(value = "/part/listProductPart")
//	public ModelAndView listProductPart() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/listProductPart");
//		return model;
//	}
//
//	@Description("부품 목록 가져오기")
//	@RequestMapping(value = "/part/listProductPartAction")
//	@ResponseBody
//	public Map<String, Object> listProductPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			param.put("context", PartHelper.PRODUCT_CONTEXT);
//			map = PartHelper.manager.find(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("구매품 조회 페이지")
//	@RequestMapping(value = "/part/listLibraryPart")
//	public ModelAndView listLibraryPart() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/listLibraryPart");
//		return model;
//	}
//
//	@Description("구매품 목록 가져오기")
//	@RequestMapping(value = "/part/listLibraryPartAction")
//	@ResponseBody
//	public Map<String, Object> listLibraryPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			param.put("context", PartHelper.LIBRARY_CONTEXT);
//			map = PartHelper.manager.find(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("EPALN 목록 가져오기")
//	@RequestMapping(value = "/part/listEplanPartAction")
//	@ResponseBody
//	public Map<String, Object> listEplanPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			param.put("context", PartHelper.EPLAN_CONTEXT);
//			map = PartHelper.manager.find(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("eplan 추가 페이지")
//	@RequestMapping(value = "/part/addEplanDoc")
//	public ModelAndView addEplanDoc() throws Exception {
//		ModelAndView model = new ModelAndView();
//		StateKeys[] states = StateKeys.values();
//		model.addObject("states", states);
//		model.setViewName("popup:/part/addEplanDoc");
//		return model;
//	}
//
//	@Description("부품 추가 페이지")
//	@RequestMapping(value = "/part/addPart")
//	public ModelAndView addPart() throws Exception {
//		ModelAndView model = new ModelAndView();
//		PartType[] partTypes = PartType.getPartTypeSet();
//		model.addObject("partTypes", partTypes);
//		model.setViewName("popup:/part/addPart");
//		return model;
//	}
//
//	@Description("부품 추가")
//	@RequestMapping(value = "/part/addPartAction")
//	@ResponseBody
//	public Map<String, Object> addPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.addPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("부품정보 페이지")
//	@RequestMapping(value = "/part/viewPart")
//	public ModelAndView viewPart(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		String popup = (String) param.get("popup");
//		boolean isPopup = Boolean.parseBoolean(popup);
//		WTPart part = null;
//		PartViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			part = (WTPart) rf.getReference(oid).getObject();
//			data = new PartViewData(part);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/part/viewPart");
//		} else {
//			model.setViewName("default:/part/viewPart");
//		}
//		return model;
//	}
//
//	@Description("부품 수정 페이지")
//	@RequestMapping(value = "/part/modifyPart")
//	public ModelAndView modifyPart(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		WTPart part = null;
//		PartViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			part = (WTPart) rf.getReference(oid).getObject();
//			data = new PartViewData(part);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/part/modifyPart");
//		} else {
//			model.setViewName("default:/part/modifyPart");
//		}
//		return model;
//	}
//
//	@Description("옵션 BOM 등록 페이지")
//	@RequestMapping(value = "/part/createBom")
//	public ModelAndView createOptionBom() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/createBom");
//		return model;
//	}
//
//	@Description("옵션 BOM 등록")
//	@RequestMapping(value = "/part/createBomAction")
//	@ResponseBody
//	public Map<String, Object> createBomAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createBomAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("제품 정보 페이지")
//	@RequestMapping(value = "/part/infoEndPart")
//	public ModelAndView infoEndPart(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		ReferenceFactory rf = new ReferenceFactory();
//		WTPart part = (WTPart) rf.getReference(oid).getObject();
//		String context = "";
//		if (part.getContainer().getName().equalsIgnoreCase("Commonspace")) {
//			context = "product";
//		} else if (part.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
//			context = "library";
//		}
//
//		String popup = (String) param.get("popup");
//		boolean isPopup = Boolean.parseBoolean(popup);
//		ArrayList<PartViewData> list = PartHelper.manager.infoEndPart(oid, new ArrayList<PartViewData>());
//		model.addObject("list", list);
//		model.addObject("context", context);
//		if (isPopup) {
//			model.setViewName("popup:/part/infoEndPart");
//		} else {
//			model.setViewName("default:/part/infoEndPart");
//		}
//		return model;
//	}
//
//	@Description("상위부품 정보 페이지")
//	@RequestMapping(value = "/part/infoUpPart")
//	public ModelAndView infoUpPart(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		ReferenceFactory rf = new ReferenceFactory();
//		WTPart part = (WTPart) rf.getReference(oid).getObject();
//		String context = "";
//		if (part.getContainer().getName().equalsIgnoreCase("Commonspace")) {
//			context = "product";
//		} else if (part.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
//			context = "library";
//		}
//		String popup = (String) param.get("popup");
//		boolean isPopup = Boolean.parseBoolean(popup);
//		ArrayList<PartViewData> list = PartHelper.manager.getUpPart(oid);
//		model.addObject("list", list);
//		model.addObject("context", context);
//		if (isPopup) {
//			model.setViewName("popup:/part/infoUpPart");
//		} else {
//			model.setViewName("default:/part/infoUpPart");
//		}
//		return model;
//	}
//
//	@Description("하위부품 정보 페이지")
//	@RequestMapping(value = "/part/infoDownPart")
//	public ModelAndView infoDownPart(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		ReferenceFactory rf = new ReferenceFactory();
//		WTPart part = (WTPart) rf.getReference(oid).getObject();
//		String context = "";
//		if (part.getContainer().getName().equalsIgnoreCase("Commonspace")) {
//			context = "product";
//		} else if (part.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
//			context = "library";
//		}
//		String popup = (String) param.get("popup");
//		boolean isPopup = Boolean.parseBoolean(popup);
//		ArrayList<PartViewData> list = PartHelper.manager.getDownPart(oid);
//		model.addObject("list", list);
//		model.addObject("context", context);
//		if (isPopup) {
//			model.setViewName("popup:/part/infoDownPart");
//		} else {
//			model.setViewName("default:/part/infoDownPart");
//		}
//		return model;
//	}
//
//	@Description("BOM 데이터 가져오기 가져오기")
//	@RequestMapping(value = "/part/getBomData")
//	@ResponseBody
//	public JSONArray getBomData(@RequestParam Map<String, Object> param) throws Exception {
//		JSONArray node = null;
//		try {
//			node = PartHelper.manager.getBomData(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return node;
//	}
//
//	@Description("BOM 정보 페이지")
//	@RequestMapping(value = "/part/infoBom")
//	public ModelAndView infoBom(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		String popup = (String) param.get("popup");
//		boolean isPopup = Boolean.parseBoolean(popup);
//		model.addObject("oid", oid);
//		if (isPopup) {
//			model.setViewName("popup:/part/infoBom");
//		} else {
//			model.setViewName("default:/part/infoBom");
//		}
//		return model;
//	}
//
//	@Description("BOM 부품 연결 제거")
//	@RequestMapping(value = "/part/deleteBomPartAction")
//	@ResponseBody
//	public Map<String, Object> deleteBomPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.deleteBomPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("부품 추가")
//	@RequestMapping(value = "/part/insertBomPartAction")
//	@ResponseBody
//	public Map<String, Object> insertBomPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.insertBomPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("BOM 부품 체크인")
//	@RequestMapping(value = "/part/checkinBomPartAction")
//	@ResponseBody
//	public Map<String, Object> checkinBomPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.checkinBomPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("BOM 부품 체크아웃")
//	@RequestMapping(value = "/part/checkoutBomPartAction")
//	@ResponseBody
//	public Map<String, Object> checkoutBomPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.checkoutBomPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("BOM 부품 체크아웃 취소")
//	@RequestMapping(value = "/part/undocheckoutBomPartAction")
//	@ResponseBody
//	public Map<String, Object> undocheckoutBomPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.undocheckoutBomPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("부품 일괄 등록 페이지")
//	@RequestMapping(value = "/part/createAllParts")
//	public ModelAndView createAllParts(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/createAllParts");
//		return model;
//	}
//
//	@Description("위치 변경")
//	@RequestMapping(value = "/part/setDndUrlAction")
//	@ResponseBody
//	public Map<String, Object> setDndUrlAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.setDndUrlAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("위치 변경 (Indent)")
//	@RequestMapping(value = "/part/setIndentUrlAction")
//	@ResponseBody
//	public Map<String, Object> setIndentUrlAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.setIndentUrlAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("위치 변경 (Outdent)")
//	@RequestMapping(value = "/part/setOutdentUrlAction")
//	@ResponseBody
//	public Map<String, Object> setOutdentUrlAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.setOutdentUrlAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("부품 일괄 등록")
//	@RequestMapping(value = "/part/createAllPartsAction")
//	@ResponseBody
//	public Map<String, Object> createAllPartsAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createAllPartsAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("구매품 수정 페이지")
//	@RequestMapping(value = "/part/modifyLibraryPart")
//	public ModelAndView modifyLibraryPart(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		WTPart part = null;
//		PartViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			part = (WTPart) rf.getReference(oid).getObject();
//			data = new PartViewData(part);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/part/modifyLibraryPart");
//		} else {
//			model.setViewName("default:/part/modifyLibraryPart");
//		}
//		return model;
//	}
//
//	@Description("구매품 수정")
//	@RequestMapping(value = "/part/modifyLibraryPartAction")
//	@ResponseBody
//	public Map<String, Object> modifyLibraryPartAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.modifyLibraryPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	// end...
//
//	@Description("가공품 삭제")
//	@RequestMapping(value = "/part/deleteProductPartAction")
//	@ResponseBody
//	public Map<String, Object> deleteProductPartAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.deleteProductPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("구매품 삭제")
//	@RequestMapping(value = "/part/deleteLibraryPartAction")
//	@ResponseBody
//	public Map<String, Object> deleteLibraryPartAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.deleteLibraryPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("도면-> 설변 페이지")
//	@RequestMapping(value = "/part/viewToEChange")
//	public ModelAndView viewToEChange(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		String popup = (String) param.get("popup");
//		WTPart part = null;
//		PartViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			part = (WTPart) rf.getReference(oid).getObject();
//			data = new PartViewData(part);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("oid", oid);
//		model.addObject("data", data);
//		if (Boolean.parseBoolean(popup)) {
//			model.setViewName("popup:/part/viewToEChange");
//		} else {
//			model.setViewName("default:/part/viewToEChange");
//		}
//		return model;
//	}
//
//	@Description("구매품 결재 페이지")
//	@RequestMapping(value = "/part/approvalLibraryPart")
//	public ModelAndView approvalLibraryPart(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/approvalLibraryPart");
//		return model;
//	}
//
//	@Description("구매품 결재")
//	@RequestMapping(value = "/part/approvalLibraryPartAction")
//	@ResponseBody
//	public Map<String, Object> approvalLibraryPartAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		try {
//			if (ErpHelper.isSendERP) {
//				System.out.println("ERP 연동 여부..");
//				// map = ErpHelper.manager.checkApprovalLibraryPartAction(param);
//				boolean bool = (boolean) map.get("bool");
//				if (bool) {
//					String number = (String) map.get("number");
//					map.put("result", FAIL);
//					map.put("none", true);
//					map.put("msg", "부품 번호 : " + number + "가 ERP 서버에 이미 존재 합니다.");
//				} else {
//					map = PartHelper.service.approvalLibraryPartAction(param);
//					map.put("none", false);
//				}
//			} else {
//				map = PartHelper.service.approvalLibraryPartAction(param);
//				map.put("none", false);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("전장품 결재")
//	@RequestMapping(value = "/part/approvalElecPartAction")
//	@ResponseBody
//	public Map<String, Object> approvalElecPartAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.approvalElecPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("부품 삭제")
//	@RequestMapping(value = "/part/deletePartAction")
//	@ResponseBody
//	public Map<String, Object> deletePartAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.deletePartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("구매품 등록")
//	@RequestMapping(value = "/part/createLibraryPartAction")
//	@ResponseBody
//	public Map<String, Object> createLibraryPartAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createLibraryPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("가공품 등록")
//	@RequestMapping(value = "/part/createProductPartAction")
//	@ResponseBody
//	public Map<String, Object> createProductPartAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createProductPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("전장부품등록 페이지")
//	@RequestMapping(value = "/part/uploadElecPart")
//	public ModelAndView uploadElecPart(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/uploadElecPart");
//		return model;
//	}
//
//	@Description("구매품등록 페이지")
//	@RequestMapping(value = "/part/createLibraryPart")
//	public ModelAndView createLibraryPart(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/createLibraryPart");
//		return model;
//	}
//
//	@Description("가공품등록 페이지")
//	@RequestMapping(value = "/part/createProductPart")
//	public ModelAndView createProductPart(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/createProductPart");
//		return model;
//	}
//
//	@Description("전장품등록 페이지")
//	@RequestMapping(value = "/part/createElecPart")
//	public ModelAndView createElecPart(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/part/createElecPart");
//		return model;
//	}
//
//	@Description("전장품 등록")
//	@RequestMapping(value = "/part/createElecPartAction")
//	@ResponseBody
//	public Map<String, Object> createElecPartAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.createElecPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("BOM 에디터")
//	@RequestMapping(value = "/part/bomEditor")
//	public ModelAndView bomEditor(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		WTPart part = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		String codebase = null;
//		try {
//			if (!StringUtils.isNull(oid)) {
//				part = (WTPart) rf.getReference(oid).getObject();
//			}
//			codebase = WTProperties.getLocalProperties().getProperty("wt.server.codebase");
//			model.addObject("part", part);
//			model.addObject("codebase", codebase);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("oid", oid);
//		model.setViewName("popup:/part/bomEditor");
//		return model;
//	}
//
//	@Description("가공품 새이름저장 등록")
//	@RequestMapping(value = "/part/saveAsProductPartAction")
//	@ResponseBody
//	public Map<String, Object> saveAsProductPartAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.saveAsPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("구매품 새이름저장 등록")
//	@RequestMapping(value = "/part/saveAsLibraryPartAction")
//	@ResponseBody
//	public Map<String, Object> saveAsLibraryPartAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = PartHelper.service.saveAsPartAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("MAKER 정보 가져오기")
//	@RequestMapping(value = "/part/getAllKEK_VDAMAKER")
//	@ResponseBody
//	public ArrayList<Map<String, Object>> getAllKEK_VDAMAKER() throws Exception {
//		ArrayList<Map<String, Object>> map = null;
//		try {
//			if (ErpHelper.isSendERP) {
//				map = ErpHelper.manager.getAllKEK_VDAMAKER();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}

//}
