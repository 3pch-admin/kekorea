package e3ps.approval.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.approval.service.ApprovalHelper;
import e3ps.controller.BaseController;

@Controller
@RequestMapping(value="/approval/**")
public class ApprovalController extends BaseController{

	@Description("검토함 리스트 페이지")
	@RequestMapping(value="/listAgree", method=RequestMethod.GET)
	public ModelAndView listaAree() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/approval/agree-list.jsp");
		return  model;
	}
	
	@Description("검토함")
	@ResponseBody
	@RequestMapping(value="/listAgree", method=RequestMethod.POST)
	public Map<String, Object> listAgree(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ApprovalHelper.manager.findAgreeList(params);
			result.put("result", SUCCESS);
		} catch(Exception e ) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
	
	@Description("결재함 리스트 페이지")
	@RequestMapping(value="/listApproval", method=RequestMethod.GET)
	public ModelAndView listApproval() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/approval/approval-list.jsp");
		return  model;
	}
	
	@Description(value="결재함")
	@ResponseBody
	@RequestMapping(value="/listApproval", method=RequestMethod.POST)
	public Map<String, Object>listApproval(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ApprovalHelper.manager.findApprovalList(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
	
	@Description("수신함 페이지")
	@RequestMapping(value="/listReceive", method=RequestMethod.GET)
	public ModelAndView listReceive() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/approval/receive-list.jsp");
		return model;
	}
	
	@Description("수신함")
	@ResponseBody
	@RequestMapping(value="/listReceive", method=RequestMethod.POST)
	public Map<String, Object> listReceive(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ApprovalHelper.manager.findReceiveList(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
	
	@Description("진행함 페이지")
	@RequestMapping(value="/listIng", method=RequestMethod.GET)
	public ModelAndView listIng() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/approval/ing-list.jsp");
		return model;
	}
	
	@Description("진행함")
	@RequestMapping(value="/listIng", method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> listIng(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ApprovalHelper.manager.findIng(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
	
	@Description("완료함 페이지")
	@RequestMapping(value="/listComplete", method=RequestMethod.GET)
	public ModelAndView listComplete() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/approval/complete-list.jsp");
		return model;
	}
	
	@Description("완료함")
	@ResponseBody
	@RequestMapping(value="/listComplete", method=RequestMethod.POST)
	public Map<String, Object> listComplete(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ApprovalHelper.manager.findCompleteList(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
	
	@Description("반려함 페이지")
	@RequestMapping(value="/listReturn", method=RequestMethod.GET)
	public ModelAndView listReturn() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/approval/return-list.jsp");
		return model;
	}
	
	@Description("반려함")
	@ResponseBody
	@RequestMapping(value="/listReturn", method=RequestMethod.POST)
	public Map<String, Object> listReturn(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ApprovalHelper.manager.findReturnList(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
	
	@Description("공지사항 페이지")
	@RequestMapping(value="/listNotice", method=RequestMethod.GET)
	public ModelAndView listNotice() throws Exception {
		ModelAndView model = new ModelAndView();
		
		return model;
	}
}
