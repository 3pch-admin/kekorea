package e3ps.project.issue.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.project.issue.service.IssueHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/issue/**")
public class IssueController extends BaseController {

	@Description(value = "특이사항 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/project/issue/issue-list.jsp");
		return model;
	}

	@Description(value = "특이사항 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = IssueHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
}
