package e3ps.project.template.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.project.Project;
import e3ps.project.dto.ProjectDTO;
import e3ps.project.template.service.TemplateHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/template/**")
public class TemplateController extends BaseController {

	@Description(value = "템플릿 리스트 페이지")
	@GetMapping(value = "/list")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/project/template/template-list.jsp");
		return model;
	}

	@Description(value = "템플릿 리스트 가져 오는 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = TemplateHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "템플릿 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TemplateHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "템플릿 생성 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();
		model.addObject("list", list);
		model.setViewName("popup:/project/template/template-create");
		return model;
	}

	@Description(value = "템플릿 트리")
	@GetMapping(value = "/load")
	@ResponseBody
	public Map<String, Object> load(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = TemplateHelper.manager.load(oid);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "템플릿 트리 저장")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, Object> params) {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TemplateHelper.service.save(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "템플릿 프레임 페이지")
	@GetMapping(value = "/info")
	public ModelAndView info(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/project/template/template-info");
		return model;
	}

	@Description(value = "템플릿 태스크 트리 페이지")
	@GetMapping(value = "/tree")
	public ModelAndView tree(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("/extcore/jsp/project/template/template-tree.jsp");
		return model;
	}

	@Description(value = "템플릿 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(oid);
		ProjectDTO dto = new ProjectDTO(project);
		model.addObject("dto", dto);
		model.setViewName("/extcore/jsp/project/template/template-view.jsp");
		return model;
	}

}
