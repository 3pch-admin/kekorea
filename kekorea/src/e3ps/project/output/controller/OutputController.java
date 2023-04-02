package e3ps.project.output.controller;

import java.util.ArrayList;
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
import e3ps.project.Project;
import e3ps.project.output.dto.OutputDTO;
import e3ps.project.output.service.OutputHelper;
import e3ps.project.task.Task;
import net.sf.json.JSONArray;

@Controller
@RequestMapping(value = "/output/**")
public class OutputController extends BaseController {

	@Description(value = "산출물 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/project/output/output-list.jsp");
		return model;
	}

	@Description(value = "산출물 목록")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = OutputHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "산출물 태스크에서 등록 페이지")
	@GetMapping(value = "/connect")
	public ModelAndView connect(@RequestParam String poid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project project = (Project) CommonUtils.getObject(poid);
		ArrayList<Map<String, String>> list = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
		map.put("projectType_name", project.getProjectType() != null ? project.getProjectType().getName() : "");
		map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
		map.put("mak_name", project.getMak() != null ? project.getMak().getName() : "");
		map.put("detail_name", project.getDetail() != null ? project.getDetail().getName() : "");
		map.put("kekNumber", project.getKekNumber());
		map.put("keNumber", project.getKeNumber());
		map.put("description", project.getDescription());
		list.add(map); // 기본 선택한 작번

		Task task = (Task) CommonUtils.getObject(toid);
		model.addObject("location", "/Default/프로젝트/" + task.getName());
		model.addObject("toid", toid);
		model.addObject("poid", poid);
		model.addObject("list", JSONArray.fromObject(list));
		model.setViewName("popup:/project/output/output-connect");
		return model;
	}

	@Description(value = "산출물 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody OutputDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			OutputHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
