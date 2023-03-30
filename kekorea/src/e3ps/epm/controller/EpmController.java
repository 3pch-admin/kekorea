package e3ps.epm.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.epm.dto.EpmDTO;
import e3ps.epm.service.EpmHelper;
import e3ps.project.Project;
import e3ps.project.dto.ProjectDTO;
import e3ps.workspace.service.WorkspaceHelper;
import net.sf.json.JSONArray;
import wt.epm.EPMDocument;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/epm/**")
public class EpmController extends BaseController {

	@Description(value = "도면 결재 페이지")
	@GetMapping(value = "/register")
	public ModelAndView register() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/epm/epm-register.jsp");
		return model;
	}

	@Description("도면 결재")
	@ResponseBody
	@PostMapping(value = "/register")
	public Map<String, Object> register(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = EpmHelper.service.approvalEpmAction(params);
			result.put("msg", REGISTER_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "도면 조회 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/epm/epm-list.jsp");
		return model;
	}

	@Description(value = "도면 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = EpmHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "도면 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		EPMDocument epm = (EPMDocument) CommonUtils.getObject(oid);
		EpmDTO dto = new EpmDTO(epm);
		JSONArray list = EpmHelper.manager.history(epm.getMaster());
		JSONArray data = EpmHelper.manager.jsonArrayAui(dto.getOid());
//		JSONArray history = WorkspaceHelper.manager.jsonArrayHistory(epm.get());
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.setViewName("popup:/epm/epm-view");
		return model;
	}
}
