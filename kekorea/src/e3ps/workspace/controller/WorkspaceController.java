package e3ps.workspace.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.controller.BaseController;
import e3ps.workspace.service.WorkspaceHelper;

@Controller
@RequestMapping(value = "/workspace/**")
public class WorkspaceController extends BaseController {

	@Description(value = "검토함 리스트 페이지")
	@RequestMapping(value = "/agree", method = RequestMethod.GET)
	public ModelAndView agree() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/workspace/agree-list.jsp");
		return model;
	}

	@Description(value = "검토함 리스트 함수")
	@ResponseBody
	@RequestMapping(value = "/agree", method = RequestMethod.POST)
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
	@RequestMapping(value = "/approval", method = RequestMethod.GET)
	public ModelAndView approval() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/workspace/approval-list.jsp");
		return model;
	}

	@Description(value = "결재함 리스트 함수")
	@ResponseBody
	@RequestMapping(value = "/approval", method = RequestMethod.POST)
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
	@RequestMapping(value = "/receive", method = RequestMethod.GET)
	public ModelAndView receive() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/workspace/receive-list.jsp");
		return model;
	}

	@Description(value = "수신함 리스트 함수")
	@ResponseBody
	@RequestMapping(value = "/receive", method = RequestMethod.POST)
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
	@RequestMapping(value = "/progress", method = RequestMethod.GET)
	public ModelAndView progress() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/workspace/progress-list.jsp");
		return model;
	}

	@Description(value = "진행함 리스트 함수")
	@RequestMapping(value = "/progress", method = RequestMethod.POST)
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
	@RequestMapping(value = "/complete", method = RequestMethod.GET)
	public ModelAndView complete() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/workspace/complete-list.jsp");
		return model;
	}

	@Description(value = "완료함 리스트 함수")
	@ResponseBody
	@RequestMapping(value = "/complete", method = RequestMethod.POST)
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
	@RequestMapping(value = "/reject", method = RequestMethod.GET)
	public ModelAndView reject() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/workspace/reject-list.jsp");
		return model;
	}

	@Description(value = "반려함 리스트 함수")
	@ResponseBody
	@RequestMapping(value = "/reject", method = RequestMethod.POST)
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
}
