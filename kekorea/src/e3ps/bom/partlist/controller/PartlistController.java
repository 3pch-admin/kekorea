package e3ps.bom.partlist.controller;

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

import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.partlist.dto.PartListDTO;
import e3ps.bom.partlist.service.PartlistHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.project.Project;
import e3ps.workspace.service.WorkspaceHelper;
import net.sf.json.JSONArray;
import wt.fc.Persistable;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/partlist/**")
public class PartlistController extends BaseController {

	@Description(value = "수배표 조회 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/bom/partlist/partlist-list.jsp");
		return model;
	}

	@Description(value = "수배표 조회")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = PartlistHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", false);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "수배된 리스트 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		Persistable per = (Persistable) CommonUtils.getObject(oid);
		PartListDTO dto = null;
		JSONArray history = null;
		if (per instanceof PartListMasterProjectLink) {
			PartListMasterProjectLink link = (PartListMasterProjectLink) per;
			dto = new PartListDTO(link);
			history = WorkspaceHelper.manager.jsonArrayHistory(link.getMaster());
		} else if (per instanceof PartListMaster) {
			PartListMaster master = (PartListMaster) per;
			dto = new PartListDTO(master);
			history = WorkspaceHelper.manager.jsonArrayHistory(master);
		}

		JSONArray list = PartlistHelper.manager.getData(dto.getOid());
		JSONArray data = PartlistHelper.manager.jsonAuiProject(dto.getOid());
		model.addObject("history", history);
		model.addObject("isAdmin", isAdmin);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.setViewName("popup:/bom/partlist/partlist-view");
		return model;
	}

	@Description(value = "수배된 비교 페이지")
	@GetMapping(value = "/compare")
	public ModelAndView compare(@RequestParam String oid, @RequestParam String _oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Project p1 = (Project) CommonUtils.getObject(oid);
		Project p2 = (Project) CommonUtils.getObject(_oid);
		ArrayList<Map<String, Object>> data = PartlistHelper.manager.compare(p1, p2, null, null);
		model.addObject("p1", p1);
		model.addObject("p2", p2);
		model.addObject("oid", oid);
		model.addObject("_oid", _oid);
		model.addObject("data", JSONArray.fromObject(data));
		model.setViewName("popup:/bom/partlist/partlist-compare");
		return model;
	}

	@Description(value = "수배표 비교")
	@ResponseBody
	@PostMapping(value = "/compare")
	public Map<String, Object> compare(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<>();
		try {
			String oid = (String) params.get("oid");
			String _oid = (String) params.get("_oid");
			String compareKey = (String) params.get("compareKey");
			String sort = (String) params.get("sort");
			Project p1 = (Project) CommonUtils.getObject(oid);
			Project p2 = (Project) CommonUtils.getObject(_oid);
			ArrayList<Map<String, Object>> data = PartlistHelper.manager.compare(p1, p2, compareKey, sort);
			result.put("list", data);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "수배표 팝업 조회 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/bom/partlist/partlist-popup");
		return model;
	}

	@Description(value = "수배표 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/bom/partlist/partlist-create");
		return model;
	}

	@Description(value = "수배표 등록")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody PartListDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartlistHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "수배표 수정 페이지")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		PartListMasterProjectLink link = (PartListMasterProjectLink) CommonUtils.getObject(oid);
		PartListDTO dto = new PartListDTO(link);
		JSONArray list = PartlistHelper.manager.getData(dto.getOid());
		JSONArray data = PartlistHelper.manager.jsonAuiProject(dto.getOid());
		model.addObject("isAdmin", isAdmin);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.setViewName("popup:/bom/partlist/partlist-modify");
		return model;
	}

	@Description(value = "수배표 수정")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody PartListDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartlistHelper.service.modify(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "수배표 삭제")
	@ResponseBody
	@GetMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartlistHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "수배표 태스크에서 등록 페이지")
	@GetMapping(value = "/connect")
	public ModelAndView connect(@RequestParam String poid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		People people = CommonUtils.sessionPeople();
		Department department = people.getDepartment();
		String engType = "";
		if (department.getCode().equals("MACHINE")) {
			engType = "기계";
		} else if (department.getCode().equals("ELEC")) {
			engType = "전기";
		}

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
		model.addObject("toid", toid);
		model.addObject("poid", poid);
		model.addObject("list", JSONArray.fromObject(list));
		model.addObject("engType", engType);
		model.setViewName("popup:/bom/partlist/partlist-connect");
		return model;
	}

	@Description(value = "수배표 태스크 연결 제거 함수")
	@ResponseBody
	@GetMapping(value = "/disconnect")
	public Map<String, Object> disconnect(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			PartlistHelper.service.disconnect(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "수배된 통합 페이지")
	@GetMapping(value = "/integrated")
	public ModelAndView integrated(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		Project project = (Project) CommonUtils.getObject(oid);
		ArrayList<Map<String, Object>> list = PartlistHelper.manager.integratedData(project);
		model.addObject("project", project);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("integratedData", JSONArray.fromObject(list));
		model.setViewName("popup:/bom/partlist/partlist-integrated");
		return model;
	}
}
