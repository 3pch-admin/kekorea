package e3ps.doc.request.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.doc.request.dto.RequestDocumentDTO;
import e3ps.doc.request.service.RequestDocumentHelper;
import e3ps.org.service.OrgHelper;
import e3ps.project.template.service.TemplateHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/request/**")
public class RequestDocumentController extends BaseController {

	@Description(value = "의뢰서 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/doc/request/request-list.jsp");
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
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();
		JSONArray elecs = OrgHelper.manager.getDepartmentUser("ELEC");
		JSONArray softs = OrgHelper.manager.getDepartmentUser("SOFT");
		JSONArray machines = OrgHelper.manager.getDepartmentUser("MACHINE");
		JSONArray maks = CommonCodeHelper.manager.parseJson("MAK");
		JSONArray customers = CommonCodeHelper.manager.parseJson("CUSTOMER");
		JSONArray installs = CommonCodeHelper.manager.parseJson("INSTALL");
		model.addObject("maks", maks);
		model.addObject("installs", installs);
		model.addObject("customers", customers);
		model.addObject("elecs", elecs);
		model.addObject("softs", softs);
		model.addObject("machines", machines);
		model.addObject("list", list);
		model.setViewName("popup:/doc/request/request-create");
		return model;
	}
}
