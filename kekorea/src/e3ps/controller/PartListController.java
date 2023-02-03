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
//import e3ps.org.Department;
//import e3ps.org.service.OrgHelper;
//import e3ps.part.beans.PartViewData;
//import e3ps.partlist.PartListMaster;
//import e3ps.partlist.PartListMasterProjectLink;
//import e3ps.partlist.beans.PartListMasterViewData;
//import e3ps.partlist.service.PartListMasterHelper;
//import e3ps.project.Project;
//import wt.fc.ReferenceFactory;
//import wt.org.WTUser;
//import wt.part.WTPart;
//import wt.session.SessionHelper;
//
//@Controller
//public class PartListController extends BaseController {
	
//	@Description("수배표 삭제")
//	@RequestMapping(value = "/partList/deletePartListAction")
//	@ResponseBody
//	public Map<String, Object> deletePartListAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartListMasterHelper.service.deletePartListMasterAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("수배표 수정 페이지")
//	@RequestMapping(value = "/partList/modifyPartListMaster")
//	public ModelAndView modifyPartListMaster(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		ArrayList<PartListMasterProjectLink> projectList = new ArrayList<PartListMasterProjectLink>();
//		PartListMaster master = null;
//		PartListMasterViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			master = (PartListMaster) rf.getReference(oid).getObject();
//			data = new PartListMasterViewData(master);
//			projectList = PartListMasterHelper.manager.getPartListMasterProjectLink(master);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		model.addObject("projectList", projectList);
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/partList/modifyPartListMaster");
//		} else {
//			model.setViewName("default:/partList/modifyPartListMaster");
//		}
//		return model;
//	}
//
//	@Description("수배표 수정")
//	@RequestMapping(value = "/partList/modifyPartListAction")
//	@ResponseBody
//	public Map<String, Object> modifyPartListAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartListMasterHelper.service.modifyPartListAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("수배표 통합보기 페이지")
//	@RequestMapping(value = "/partList/viewTotalPartList")
//	public ModelAndView viewTotalPartList(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		String engType = (String) param.get("engType");
//		String pname = (String) param.get("pname");
//		Project project = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		ArrayList<PartListMaster> data = new ArrayList<PartListMaster>();
//		try {
//			project = (Project) rf.getReference(oid).getObject();
//			data = PartListMasterHelper.manager.findPartListByProject(project, engType, pname);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/partList/viewTotalPartList");
//		} else {
//			model.setViewName("default:/partList/viewTotalPartList");
//		}
//		return model;
//	}
//
//	@Description("수배표 등록")
//	@RequestMapping(value = "/partList/createPartListAction")
//	@ResponseBody
//	public Map<String, Object> createPartListAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartListMasterHelper.service.createPartListMasterAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("수배표 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/partList/listPartListAction")
//	public Map<String, Object> listPartListAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = PartListMasterHelper.manager.findPartList(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("수배표 등록 페이지")
//	@RequestMapping(value = "/partList/createPartListMaster")
//	public ModelAndView createPartListMaster(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//
//		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
//
//		Department dept = OrgHelper.manager.getDepartment(user);
//
//		model.addObject("dept", dept);
//
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//
//		if (isPopup) {
//			model.setViewName("popup:/partList/createPartListMaster");
//		} else {
//			model.setViewName("default:/partList/createPartListMaster");
//		}
//		return model;
//	}
//
//	@Description("수배표 통합 조회 페이지")
//	@RequestMapping(value = "/partList/listPartList")
//	public ModelAndView listPartList() throws Exception {
//		ModelAndView model = new ModelAndView();
//		// default 승인됨
//		StateKeys[] states = StateKeys.values();
//		model.addObject("states", states);
//		model.setViewName("default:/partList/listPartList");
//		return model;
//	}
//	
//	@Description("수배리스트정보 페이지")
//	@RequestMapping(value = "/partList/viewPartListMasterInfo")
//	public ModelAndView viewPartListMasterInfo(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		ArrayList<PartListMasterProjectLink> projectList = new ArrayList<PartListMasterProjectLink>();
//		PartListMaster master = null;
//		PartListMasterViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			master = (PartListMaster) rf.getReference(oid).getObject();
//			data = new PartListMasterViewData(master);
//			projectList = PartListMasterHelper.manager.getPartListMasterProjectLink(master);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		model.addObject("projectList", projectList);
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/partList/viewPartListMasterInfo");
//		} else {
//			model.setViewName("default:/partList/viewPartListMasterInfo");
//		}
//		return model;
//	}
//
//	@Description("수배리스트 페이지")
//	@RequestMapping(value = "/partList/viewPartListMaster")
//	public ModelAndView viewPartListMaster(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		PartListMaster master = null;
//		PartListMasterViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			master = (PartListMaster) rf.getReference(oid).getObject();
//			data = new PartListMasterViewData(master);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/partList/viewPartListMaster");
//		} else {
//			model.setViewName("default:/partList/viewPartListMaster");
//		}
//		return model;
//	}
//
//	@Description("YCODE로 PART")
//	@RequestMapping(value = "/partList/viewPartByYCode")
//	public ModelAndView viewPartByYCode(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		WTPart part = null;
//		PartViewData data = null;
//		try {
//			part = PartListMasterHelper.manager.getPartByYCode(param);
//
//			System.out.println("part====" + part);
//
//			if (part == null) {
//				model.addObject("checkY", "false");
//				model.addObject("number", param.get("yCode"));
//				if (isPopup) {
//					model.setViewName("popup:/part/viewNoPart");
//				} else {
//					model.setViewName("default:/part/viewNoPart");
//				}
//			} else {
//				data = new PartViewData(part);
//				model.addObject("data", data);
//				if (isPopup) {
//					model.setViewName("popup:/part/viewPart");
//				} else {
//					model.setViewName("default:/part/viewPart");
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return model;
//	}
//
//	@Description("엑셀 다운로드")
//	@RequestMapping(value = "/partList/installExcelAction")
//	@ResponseBody
//	public Map<String, Object> installExcelAction(@RequestBody Map<String, Object> param) throws Exception {
//
//		Map<String, Object> map = new HashMap<String, Object>();
//		String url = (String) param.get("url");
//		try {
//
//			File excel = PartListMasterHelper.manager.installExcel(param);
//
//			String name = excel.getName();// new String(excel.getName().getBytes("EUC-KR"), "8859_1");
//			map.put("result", SUCCESS);
//			// map.put("url", "/Windchill/jsp/temp/pdm/excelForm/" + excel.getName());
//			map.put("url", "/Windchill/jsp/temp/pdm/excelForm/" + name);
//		} catch (Exception e) {
//			e.printStackTrace();
//			map.put("msg", "엑셀출력에 실패하였습니다.\n시스템 관리자에게 문의하세요");
//			map.put("url", url);
//			map.put("result", FAIL);
//		}
//		return map;
//
//	}
//}
