package e3ps.bom.tbom.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.bom.partlist.service.PartlistHelper;
import e3ps.bom.tbom.TBOMMaster;
import e3ps.bom.tbom.dto.TBOMDTO;
import e3ps.bom.tbom.service.TBOMHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import e3ps.project.Project;
import e3ps.workspace.service.WorkspaceHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/tbom/**")
public class TBOMController extends BaseController {

	@Description(value = "T-BOM 조회 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/bom/tbom/tbom-list.jsp");
		return model;
	}

	@Description(value = "T-BOM 조회")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = TBOMHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", false);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "T-BOM 등록")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/bom/tbom/tbom-create");
		return model;
	}

	@Description(value = "T-BOM 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody TBOMDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TBOMHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "KE부품 번호로 찾아오기 (KE OR EPM)")
	@ResponseBody
	@GetMapping(value = "/getData")
	public Map<String, Object> getData(@RequestParam String number) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = TBOMHelper.manager.getData(number);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "T-BOM 그리드 저장 함수")
	@ResponseBody
	@PostMapping(value = "/save")
	public Map<String, Object> save(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TBOMHelper.service.save(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "T-BOM 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		TBOMMaster master = (TBOMMaster) CommonUtils.getObject(oid);
		TBOMDTO dto = new TBOMDTO(master);
		JSONArray history = WorkspaceHelper.manager.jsonArrayHistory(master);
		JSONArray list = TBOMHelper.manager.jsonAuiProject(oid);
		JSONArray data = TBOMHelper.manager.getData(master);
		model.addObject("data", data);
		model.addObject("list", list);
		model.addObject("dto", dto);
		model.addObject("history", history);
		model.setViewName("popup:/bom/tbom/tbom-view");
		return model;
	}

	@Description(value = "수배표 태스크에서 T-BOM 페이지")
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
		model.addObject("toid", toid);
		model.addObject("poid", poid);
		model.addObject("list", JSONArray.fromObject(list));
		model.setViewName("popup:/bom/tbom/tbom-connect");
		return model;
	}

	@Description(value = "T-BOM 태스크 연결 제거 함수")
	@ResponseBody
	@GetMapping(value = "/disconnect")
	public Map<String, Object> disconnect(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			TBOMHelper.service.disconnect(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "T-BOM 팝업 조회 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/bom/tbom/tbom-popup");
		return model;
	}

	@Description(value = "T-BOM 비교 페이지")
	@GetMapping(value = "/compare")
	public ModelAndView compare(@RequestParam String oid, @RequestParam String _oid,
			@RequestParam(required = false) String compareKey, @RequestParam(required = false) String sort)
			throws Exception {
		ModelAndView model = new ModelAndView();
		Project p1 = (Project) CommonUtils.getObject(oid);
		Project p2 = (Project) CommonUtils.getObject(_oid);
		ArrayList<Map<String, Object>> data = TBOMHelper.manager.compare(p1, p2, compareKey, sort);
		model.addObject("p1", p1);
		model.addObject("p2", p2);
		model.addObject("oid", oid);
		model.addObject("_oid", _oid);
		model.addObject("data", JSONArray.fromObject(data));
		model.setViewName("popup:/bom/tbom/tbom-compare");
		return model;
	}
}
