package e3ps.doc.request.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.bom.partlist.service.PartlistHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.doc.request.RequestDocument;
import e3ps.doc.request.RequestDocumentProjectLink;
import e3ps.doc.request.dto.RequestDocumentDTO;
import e3ps.doc.request.service.RequestDocumentHelper;
import e3ps.epm.keDrawing.KeDrawing;
import e3ps.epm.keDrawing.dto.KeDrawingDTO;
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import e3ps.org.service.OrgHelper;
import e3ps.project.Project;
import e3ps.project.template.service.TemplateHelper;
import e3ps.workspace.service.WorkspaceHelper;
import net.sf.json.JSONArray;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/requestDocument/**")
public class RequestDocumentController extends BaseController {

	@Description(value = "의뢰서 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/document/request/requestDocument-list.jsp");
		return model;
	}

	@Description(value = "의뢰서 조회 함수")
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = RequestDocumentHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "의뢰서 그리드 저장")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> removeRows = params.get("removeRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			ArrayList<RequestDocumentDTO> removeRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> remove : removeRows) {
				RequestDocumentDTO dto = mapper.convertValue(remove, RequestDocumentDTO.class);
				removeRow.add(dto);
			}

			HashMap<String, List<RequestDocumentDTO>> dataMap = new HashMap<>();
			dataMap.put("removeRows", removeRow); // 삭제행

			RequestDocumentHelper.service.save(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "의뢰서 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody RequestDocumentDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			if (dto.isConnect()) {
				Project project = (Project) CommonUtils.getObject(dto.getPoid());
				QueryResult qr = PersistenceHelper.manager.navigate(project, "requestDocument",
						RequestDocumentProjectLink.class);
				if (qr.size() > 0) {
					result.put("result", FAIL);
					result.put("msg", "작번(" + project.getKekNumber() + ")과 연결된 의뢰서가 이미 존재합니다.");
				}
			}

			RequestDocumentHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "의뢰서 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create(@RequestParam(required = false) String poid, @RequestParam(required = false) String toid)
			throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();
		JSONArray elecs = OrgHelper.manager.getDepartmentUser("ELEC");
		JSONArray softs = OrgHelper.manager.getDepartmentUser("SOFT");
		JSONArray machines = OrgHelper.manager.getDepartmentUser("MACHINE");
		JSONArray maks = CommonCodeHelper.manager.parseJson("MAK");
		JSONArray customers = CommonCodeHelper.manager.parseJson("CUSTOMER");
		JSONArray installs = CommonCodeHelper.manager.parseJson("INSTALL");
		JSONArray projectTypes = CommonCodeHelper.manager.parseJson("PROJECT_TYPE");
		model.addObject("maks", maks);
		model.addObject("installs", installs);
		model.addObject("customers", customers);
		model.addObject("elecs", elecs);
		model.addObject("softs", softs);
		model.addObject("machines", machines);
		model.addObject("projectTypes", projectTypes);
		model.addObject("list", list);
		model.addObject("poid", poid);
		model.addObject("toid", toid);
		model.setViewName("popup:/document/request/requestDocument-create");
		return model;
	}

	@Description(value = "의뢰서 태스크에서 등록 페이지")
	@GetMapping(value = "/connect")
	public ModelAndView connect(@RequestParam String poid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();
		JSONArray elecs = OrgHelper.manager.getDepartmentUser("ELEC");
		JSONArray softs = OrgHelper.manager.getDepartmentUser("SOFT");
		JSONArray machines = OrgHelper.manager.getDepartmentUser("MACHINE");
		JSONArray maks = CommonCodeHelper.manager.parseJson("MAK");
		JSONArray customers = CommonCodeHelper.manager.parseJson("CUSTOMER");
		JSONArray installs = CommonCodeHelper.manager.parseJson("INSTALL");
		JSONArray projectTypes = CommonCodeHelper.manager.parseJson("PROJECT_TYPE");
		model.addObject("maks", maks);
		model.addObject("installs", installs);
		model.addObject("customers", customers);
		model.addObject("elecs", elecs);
		model.addObject("softs", softs);
		model.addObject("machines", machines);
		model.addObject("projectTypes", projectTypes);
		model.addObject("list", list);
		model.addObject("poid", poid);
		model.addObject("toid", toid);
		model.setViewName("popup:/document/request/requestDocument-connect");
		return model;
	}

	@Description(value = "의뢰서 등록 검증")
	@ResponseBody
	@PostMapping(value = "/validate")
	public Map<String, Object> validate(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = RequestDocumentHelper.manager.validate(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "의뢰서 태스크 연결 제거 함수")
	@ResponseBody
	@GetMapping(value = "/disconnect")
	public Map<String, Object> disconnect(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			RequestDocumentHelper.service.disconnect(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "의뢰서 삭제 함수")
	@ResponseBody
	@GetMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			RequestDocumentHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "의뢰서 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		RequestDocument requestDocument = (RequestDocument) CommonUtils.getObject(oid);
		RequestDocumentDTO dto = new RequestDocumentDTO(requestDocument);
		JSONArray history = WorkspaceHelper.manager.jsonArrayHistory(requestDocument);
		JSONArray data = RequestDocumentHelper.manager.jsonAuiProject(dto.getOid());
		model.addObject("isAdmin", isAdmin);
		model.addObject("history", history);
		model.addObject("data", data);
		model.addObject("dto", dto);
		model.setViewName("popup:/document/request/requestDocument-view");
		return model;
	}
}
