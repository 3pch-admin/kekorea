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
//import e3ps.approval.ApprovalContract;
//import e3ps.approval.ApprovalLine;
//import e3ps.approval.ApprovalMaster;
//import e3ps.approval.Notice;
//import e3ps.approval.beans.ApprovalLineViewData;
//import e3ps.approval.beans.ApprovalMasterViewData;
//import e3ps.approval.beans.NoticeViewData;
//import e3ps.approval.service.ApprovalHelper;
//import e3ps.approval.service.NoticeHelper;
//import e3ps.common.StateKeys;
//import e3ps.common.code.service.CommonCodeHelper;
//import e3ps.org.Department;
//import e3ps.org.service.OrgHelper;
//import e3ps.project.service.ProjectHelper;
//import wt.fc.Persistable;
//import wt.fc.ReferenceFactory;
//import wt.org.WTUser;
//import wt.session.SessionHelper;
//
//@Controller
//public class ApprovalController extends BaseController {

//	@Description("요청 사항 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/approval/listErrorReportAction")
//	public Map<String, Object> listErrorReportAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.manager.findErrorReport(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("요청 사항 조회 페이지")
//	@RequestMapping(value = "/approval/listErrorReport")
//	public ModelAndView listErrorReport() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/approval/listErrorReport");
//		return model;
//	}
//
//	@Description("결재완료함 페이지")
//	@RequestMapping(value = "/approval/infoCompleteApproval")
//	public ModelAndView infoCompleteApproval(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		ReferenceFactory rf = new ReferenceFactory();
//		ApprovalMaster master = null;
//		ApprovalMasterViewData data = null;
//		try {
//			master = (ApprovalMaster) rf.getReference(oid).getObject();
//			data = new ApprovalMasterViewData(master);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/approval/infoCompleteApproval");
//		} else {
//			model.setViewName("default:/approval/infoCompleteApproval");
//		}
//		return model;
//	}
//
//	@Description("결재라인 초기화")
//	@RequestMapping(value = "/approval/initApprovalLineAction")
//	@ResponseBody
//	public Map<String, Object> initApprovalLineAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.initApprovalLineAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("결재마스터 초기화")
//	@RequestMapping(value = "/approval/initApprovalAction")
//	@ResponseBody
//	public Map<String, Object> initApprovalAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.initApprovalAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("나의 작번 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/approval/listMyProjectAction")
//	public Map<String, Object> listMyProjectAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ProjectHelper.manager.findMyProject(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("나의 작번 조회 페이지")
//	@RequestMapping(value = "/approval/myProject")
//	public ModelAndView myProject() throws Exception {
//		ModelAndView model = new ModelAndView();
//		ArrayList<String> customer = CommonCodeHelper.manager.getCommonCode("CUSTOMER");
//		ArrayList<String> project_type = CommonCodeHelper.manager.getCommonCode("PROJECT_TYPE");
//		ArrayList<String> install = CommonCodeHelper.manager.getCommonCode("INSTALL");
//		// HashMap<String, String[]> locationCode
//		// =CommonCodeHelper.manager.getLocationCode();
//		// default 승인됨
//		StateKeys[] states = StateKeys.values();
//
//		WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
//		Department dept = OrgHelper.manager.getDepartment(user);
//
//		model.addObject("dept", dept);
//		model.addObject("install", install);
//		model.addObject("states", states);
//		model.addObject("customer", customer);
//		model.addObject("project_type", project_type);
//		model.setViewName("default:/approval/myProject");
//		return model;
//	}
//
//	@Description("결재함 리스트 페이지")
//	@RequestMapping(value = "/approval/listApproval")
//	public ModelAndView listApproval() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/approval/listApproval");
//		return model;
//	}
//
//	@Description("결재함 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/approval/listApprovalAction")
//	public Map<String, Object> listApprovalAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.manager.findApprovalList(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("진행함 리스트 페이지")
//	@RequestMapping(value = "/approval/listIng")
//	public ModelAndView listIng() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/approval/listIng");
//		return model;
//	}
//
//	@Description("진행함 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/approval/listIngAction")
//	public Map<String, Object> listIngAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.manager.findIng(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("완료함 리스트 페이지")
//	@RequestMapping(value = "/approval/listComplete")
//	public ModelAndView listComplete() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/approval/listComplete");
//		return model;
//	}
//
//	@Description("완료함 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/approval/listCompleteAction")
//	public Map<String, Object> listCompleteAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.manager.findCompleteList(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("수신함 리스트 페이지")
//	@RequestMapping(value = "/approval/listReceive")
//	public ModelAndView listReceive() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/approval/listReceive");
//		return model;
//	}
//
//	@Description("수신함 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/approval/listReceiveAction")
//	public Map<String, Object> listReceiveAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.manager.findReceiveList(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("합의함 리스트 페이지")
//	@RequestMapping(value = "/approval/listAgree")
//	public ModelAndView listAgree() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/approval/listAgree");
//		return model;
//	}
//
//	@Description("합의함 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/approval/listAgreeAction")
//	public Map<String, Object> listAgreeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.manager.findAgreeList(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("반려함 리스트 페이지")
//	@RequestMapping(value = "/approval/listReturn")
//	public ModelAndView listReturn() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/approval/listReturn");
//		return model;
//	}
//
//	@Description("반려함 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/approval/listReturnAction")
//	public Map<String, Object> listReturnAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.manager.findReturnList(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("공지사항 리스트 페이지")
//	@RequestMapping(value = "/approval/listNotice")
//	public ModelAndView listNotice() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/approval/listNotice");
//		return model;
//	}
//
//	@Description("공지사항 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/approval/listNoticeAction")
//	public Map<String, Object> listNoticeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = NoticeHelper.manager.find(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("공지사항 정보 페이지")
//	@RequestMapping(value = "/approval/viewNotice")
//	public ModelAndView viewNotice(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String popup = (String) param.get("popup");
//		String oid = (String) param.get("oid");
//		Notice notice = null;
//		NoticeViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			notice = (Notice) rf.getReference(oid).getObject();
//			data = new NoticeViewData(notice);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (Boolean.parseBoolean(popup)) {
//			model.setViewName("popup:/approval/viewNotice");
//		} else {
//			model.setViewName("default:/approval/viewNotice");
//		}
//		return model;
//	}
//
//	@Description("공지사항 등록 페이지")
//	@RequestMapping(value = "/approval/createNotice")
//	public ModelAndView createNotice() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/approval/createNotice");
//		return model;
//	}
//
//	@Description("공지사항 등록")
//	@RequestMapping(value = "/approval/createNoticeAction")
//	@ResponseBody
//	public Map<String, Object> createNoticeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = NoticeHelper.service.createNoticeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("공지사항수정 페이지")
//	@RequestMapping(value = "/approval/modifyNotice")
//	public ModelAndView modifyNotice(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		String popup = (String) param.get("popup");
//		Notice notice = null;
//		NoticeViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			notice = (Notice) rf.getReference(oid).getObject();
//			data = new NoticeViewData(notice);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (Boolean.parseBoolean(popup)) {
//			model.setViewName("popup:/approval/modifyNotice");
//		} else {
//			model.setViewName("default:/approval/modifyNotice");
//		}
//		return model;
//	}
//
//	@Description("공지사항 수정")
//	@RequestMapping(value = "/approval/modifyNoticeAction")
//	@ResponseBody
//	public Map<String, Object> modifyNoticeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = NoticeHelper.service.modifyNoticeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("공지사항 삭제")
//	@RequestMapping(value = "/approval/deleteNoticeAction")
//	@ResponseBody
//	public Map<String, Object> deleteNoticeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = NoticeHelper.service.deleteNoticeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("결재정보 페이지")
//	@RequestMapping(value = "/approval/infoApproval")
//	public ModelAndView infoApproval(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		ReferenceFactory rf = new ReferenceFactory();
//		ApprovalLine line = null;
//		ApprovalLineViewData data = null;
//		try {
//			line = (ApprovalLine) rf.getReference(oid).getObject();
//			data = new ApprovalLineViewData(line);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/approval/infoApproval");
//		} else {
//			model.setViewName("default:/approval/infoApproval");
//		}
//		return model;
//	}
//
//	@Description("승인")
//	@RequestMapping(value = "/approval/approvalAction")
//	@ResponseBody
//	public Map<String, Object> approvalAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.approvalAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("반려")
//	@RequestMapping(value = "/approval/returnAction")
//	@ResponseBody
//	public Map<String, Object> returnAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.returnAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("검토완료")
//	@RequestMapping(value = "/approval/agreeAction")
//	@ResponseBody
//	public Map<String, Object> agreeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.agreeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("검토반려")
//	@RequestMapping(value = "/approval/unagreeAction")
//	@ResponseBody
//	public Map<String, Object> unagreeAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.unagreeAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("수신확인")
//	@RequestMapping(value = "/approval/receiveAction")
//	@ResponseBody
//	public Map<String, Object> receiveAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.receiveAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("결재이력 페이지")
//	@RequestMapping(value = "/approval/infoApprovalHistory")
//	public ModelAndView infoApprovalHistory(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		ReferenceFactory rf = new ReferenceFactory();
//		Persistable per = null;
//		ApprovalMaster master = null;
//		ApprovalMasterViewData data = null;
//		try {
//			per = (Persistable) rf.getReference(oid).getObject();
//			master = ApprovalHelper.manager.getMaster(per);
//
//			if (master == null) {
//				ApprovalContract contract = ApprovalHelper.manager.getContract(per);
//
//				System.out.print("+co=" + contract);
//
//				if (contract != null) {
//					master = ApprovalHelper.manager.getMaster(contract);
//				}
//			}
//
//			if (master != null) {
//				data = new ApprovalMasterViewData(master);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		model.setViewName("popup:/common/infoApprovalHistory");
//		return model;
//	}
//
//	@Description("결재정보 페이지")
//	@RequestMapping(value = "/approval/infoMasterApproval")
//	public ModelAndView infoMasterApproval(@RequestParam Map<String, Object> param) {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		ReferenceFactory rf = new ReferenceFactory();
//		ApprovalMaster master = null;
//		ApprovalMasterViewData data = null;
//		try {
//			master = (ApprovalMaster) rf.getReference(oid).getObject();
//			data = new ApprovalMasterViewData(master);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/approval/infoMasterApproval");
//		} else {
//			model.setViewName("default:/approval/infoMasterApproval");
//		}
//		return model;
//	}
//
//	@Description("결재 스킵")
//	@RequestMapping(value = "/approval/skipApprovalAction")
//	@ResponseBody
//	public Map<String, Object> skipApprovalAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.skipApproval(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("결재 스킵")
//	@RequestMapping(value = "/approval/reassignApprovalAction")
//	@ResponseBody
//	public Map<String, Object> reassignApprovalAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.reassignApproval(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	// end
//
//	@Description("결재 회수 처리")
//	@RequestMapping(value = "/approval/recoveryAction")
//	@ResponseBody
//	public Map<String, Object> recoveryAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.recoveryAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("결재 부재중 처리")
//	@RequestMapping(value = "/approval/setAbsenceAction")
//	@ResponseBody
//	public Map<String, Object> setAbsenceAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.setAbsenceAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//	
//	@Description("반려함에서 반려 삭제 처리")
//	@RequestMapping(value = "/approval/deleteReturnAction")
//	@ResponseBody
//	public Map<String, Object> deleteReturnAction(@RequestBody Map<String, Object> param) {
//		Map<String, Object> map = null;
//		try {
//			map = ApprovalHelper.service.deleteReturnAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}

//}
