package e3ps.workspace.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.org.Department;
import e3ps.org.service.OrgHelper;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.dto.ApprovalLineDTO;
import e3ps.workspace.notice.Notice;
import e3ps.workspace.notice.dto.NoticeDTO;
import e3ps.workspace.service.WorkspaceHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/workspace/**")
public class WorkspaceController extends BaseController {

	@Description(value = "검토함 리스트 페이지")
	@GetMapping(value = "/agree")
	public ModelAndView agree() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/workspace/agree-list.jsp");
		return model;
	}

	@Description(value = "검토함 리스트 함수")
	@ResponseBody
	@PostMapping(value = "/agree")
	public Map<String, Object> agree(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.agree(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "결재함 리스트 페이지")
	@GetMapping(value = "/approval")
	public ModelAndView approval() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/workspace/approval-list.jsp");
		return model;
	}

	@Description(value = "결재함 리스트 함수")
	@ResponseBody
	@PostMapping(value = "/approval")
	public Map<String, Object> approval(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.approval(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "수신함 페이지")
	@GetMapping(value = "/receive")
	public ModelAndView receive() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/workspace/receive-list.jsp");
		return model;
	}

	@Description(value = "수신함 리스트 함수")
	@ResponseBody
	@PostMapping(value = "/receive")
	public Map<String, Object> receive(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.receive(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "진행함 리스트 페이지")
	@GetMapping(value = "/progress")
	public ModelAndView progress() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/workspace/progress-list.jsp");
		return model;
	}

	@Description(value = "진행함 리스트 함수")
	@PostMapping(value = "/progress")
	@ResponseBody
	public Map<String, Object> progress(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.progress(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "완료함 리스트 페이지")
	@GetMapping(value = "/complete")
	public ModelAndView complete() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/workspace/complete-list.jsp");
		return model;
	}

	@Description(value = "완료함 리스트 함수")
	@ResponseBody
	@PostMapping(value = "/complete")
	public Map<String, Object> complete(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.complete(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "반려함 페이지")
	@GetMapping(value = "/reject")
	public ModelAndView reject() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/workspace/reject-list.jsp");
		return model;
	}

	@Description(value = "반려함 리스트 함수")
	@ResponseBody
	@PostMapping(value = "/reject")
	public Map<String, Object> reject(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkspaceHelper.manager.reject(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "결재선 지정 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup() throws Exception {
		ModelAndView model = new ModelAndView();
		Department department = OrgHelper.manager.getRoot();
		model.addObject("oid", department.getPersistInfo().getObjectIdentifier().getStringValue());
		model.setViewName("popup:/workspace/register-popup");
		return model;
	}

	@Description(value = "검토함 뷰 페이지")
	@GetMapping(value = "/agreeView")
	public ModelAndView agreeView(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		ApprovalLine approvalLine = (ApprovalLine) CommonUtils.getObject(oid);
		ApprovalLineDTO dto = new ApprovalLineDTO(approvalLine, "COLUMN_AGREE");
		model.addObject("dto", dto);
		model.setViewName("popup:/workspace/agree-view");
		return model;
	}

	@Description(value = "결재 정보 보기")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid, @RequestParam String columnType) throws Exception {
		ModelAndView model = new ModelAndView();
		ApprovalLine approvalLine = (ApprovalLine) CommonUtils.getObject(oid);
		ApprovalLineDTO dto = new ApprovalLineDTO(approvalLine, columnType);
		model.addObject("dto", dto);
		model.addObject("oid", oid);
		model.setViewName("popup:/workspace/approval-view");
		return model;
	}
}
