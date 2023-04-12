package e3ps.korea.configSheet.controller;

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

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.admin.configSheetCode.ConfigSheetCode;
import e3ps.admin.configSheetCode.service.ConfigSheetCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.beans.ConfigSheetDTO;
import e3ps.korea.configSheet.service.ConfigSheetHelper;
import e3ps.project.Project;
import e3ps.workspace.service.WorkspaceHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/configSheet/**")
public class ConfigSheetController extends BaseController {

	@Description(value = "CONFIG SHEET 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/korea/configSheet/configSheet-list.jsp");
		return model;
	}

	@Description(value = "CONFIG SHEET 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ConfigSheetHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "CONFIG SHEET 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		JSONArray categorys = ConfigSheetCodeHelper.manager.parseJson("CATEGORY");
		JSONArray baseData = ConfigSheetHelper.manager.loadBaseGridData();
		model.addObject("baseData", baseData);
		model.addObject("categorys", categorys);
		model.setViewName("popup:/korea/configSheet/configSheet-create");
		return model;
	}

	@Description(value = "CONFIG SHEET 등록")
	@PostMapping(value = "/create")
	@ResponseBody
	public Map<String, Object> create(@RequestBody ConfigSheetDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ConfigSheetHelper.service.create(dto);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "CONFIG SHEET 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		ConfigSheet configSheet = (ConfigSheet) CommonUtils.getObject(oid);
		JSONArray data = ConfigSheetHelper.manager.loadBaseGridData(oid);
		JSONArray list = ConfigSheetHelper.manager.jsonAuiProject(configSheet);
		JSONArray history = WorkspaceHelper.manager.jsonArrayHistory(configSheet);
		ConfigSheetDTO dto = new ConfigSheetDTO(configSheet);
		model.addObject("oid", oid);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.addObject("history", history);
		model.setViewName("popup:/korea/configSheet/configSheet-view");
		return model;
	}

	@Description(value = "CONFIG SHEET 비교 페이지")
	@GetMapping(value = "/compare")
	public ModelAndView compare(@RequestParam String oid, @RequestParam String compareArr) throws Exception {
		ModelAndView model = new ModelAndView();

		String[] compareOids = compareArr.split(",");
		ArrayList<Project> destList = new ArrayList<>(compareOids.length);
		for (String _oid : compareOids) {
			Project project = (Project) CommonUtils.getObject(_oid);
			destList.add(project);
		}

		Project p1 = (Project) CommonUtils.getObject(oid);
		ArrayList<Map<String, Object>> data = ConfigSheetHelper.manager.compare(p1, destList);
		model.addObject("p1", p1);
		model.addObject("destList", destList);
		model.addObject("oid", oid);
		model.addObject("compareArr", compareArr);
		model.addObject("data", JSONArray.fromObject(data));
		model.setViewName("popup:/korea/configSheet/configSheet-compare");
		return model;
	}
}
