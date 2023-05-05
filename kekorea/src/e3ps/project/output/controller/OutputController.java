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

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.service.DocumentHelper;
import e3ps.project.Project;
import e3ps.project.output.Output;
import e3ps.project.output.dto.OutputDTO;
import e3ps.project.output.service.OutputHelper;
import e3ps.project.task.Task;
import net.sf.json.JSONArray;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/output/**")
public class OutputController extends BaseController {

	@Description(value = "산출물 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		ArrayList<Map<String, String>> maks = CommonCodeHelper.manager.getValueMap("MAK");
		model.addObject("maks", maks);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/project/output/output-list.jsp");
		return model;
	}

	@Description(value = "산출물(OLD) 리스트 페이지")
	@GetMapping(value = "/old")
	public ModelAndView old() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		ArrayList<Map<String, String>> maks = CommonCodeHelper.manager.getValueMap("MAK");
		model.addObject("maks", maks);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/project/output/output-old.jsp");
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
	@GetMapping(value = "/create")
	public ModelAndView create(@RequestParam(required = false) String poid, @RequestParam(required = false) String toid)
			throws Exception {
		ModelAndView model = new ModelAndView();
		if (!StringUtils.isNull(poid)) {
			Project project = (Project) CommonUtils.getObject(poid);
			ArrayList<Map<String, String>> list = new ArrayList<>();
			Map<String, String> map = new HashMap<>();
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("projectType_name", project.getProjectType().getName());
			map.put("customer_name", project.getCustomer().getName());
			map.put("mak_name", project.getMak().getName());
			map.put("detail_name", project.getDetail().getName());
			map.put("install_name", project.getInstall().getName());
			map.put("kekNumber", project.getKekNumber());
			map.put("keNumber", project.getKeNumber());
			map.put("description", project.getDescription());
			list.add(map); // 기본 선택한 작번
			model.addObject("poid", poid);
			model.addObject("list", JSONArray.fromObject(list));
		}

		if (!StringUtils.isNull(toid)) {
			Task task = (Task) CommonUtils.getObject(toid);
			model.addObject("location", "/Default/프로젝트/" + task.getName());
			model.addObject("toid", toid);
		}
		String number = OutputHelper.manager.getNextNumber();
		model.addObject("number", number);
		model.setViewName("popup:/project/output/output-create");
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

	@Description(value = "산출물 태스크에서 기본산출물 연결 페이지")
	@GetMapping(value = "/connect")
	public ModelAndView connect(@RequestParam String poid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		ArrayList<Map<String, String>> maks = CommonCodeHelper.manager.getValueMap("MAK");
		model.addObject("maks", maks);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.addObject("toid", toid);
		model.addObject("poid", poid);
		model.setViewName("popup:/project/output/output-connect");
		return model;
	}

	@Description(value = "산출물 태스크에서 연결")
	@PostMapping(value = "/connect")
	@ResponseBody
	public Map<String, Object> connect(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = OutputHelper.service.connect(params);

			if ((boolean) result.get("exist")) {
				result.put("result", FAIL);
				result.put("msg", "이미 해당 태스크와 연결된 산출물 입니다.");
				return result;
			}

			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "산출물 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		Persistable per = CommonUtils.getObject(oid);
		OutputDTO dto = null;
		JSONArray versionHistory = null;

		if (per instanceof Output) {
			Output output = (Output) per;
			dto = new OutputDTO((WTDocument) output.getDocument());
			versionHistory = DocumentHelper.manager.versionHistory((WTDocument) output.getDocument());
		} else if (per instanceof WTDocument) {
			WTDocument doc = (WTDocument) per;
			dto = new OutputDTO(doc);
			versionHistory = DocumentHelper.manager.versionHistory(doc);
		}

		model.addObject("versionHistory", versionHistory);
		model.addObject("dto", dto);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/project/output/output-view");
		return model;
	}

	@Description(value = "산출물 태스크에서 연결 제거")
	@PostMapping(value = "/disconnect")
	@ResponseBody
	public Map<String, Object> disconnect(@RequestBody Map<String, ArrayList<String>> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			OutputHelper.service.disconnect(params);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "산출물 수정 및 개정")
	@GetMapping(value = "/update")
	public ModelAndView update(@RequestParam String oid, @RequestParam String mode) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		Persistable per = CommonUtils.getObject(oid);
		OutputDTO dto = null;
		if (per instanceof Output) {
			Output output = (Output) per;
			dto = new OutputDTO((WTDocument) output.getDocument());
		} else if (per instanceof WTDocument) {
			WTDocument doc = (WTDocument) per;
			dto = new OutputDTO(doc);
		}
		model.addObject("dto", dto);
		model.addObject("mode", mode);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("popup:/project/output/output-update");
		return model;
	}

	@Description(value = "산출물 수정")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody OutputDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			OutputHelper.service.modify(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "산출물 개정")
	@ResponseBody
	@PostMapping(value = "/revise")
	public Map<String, Object> revise(@RequestBody OutputDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			OutputHelper.service.revise(dto);
			result.put("msg", REVISE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
