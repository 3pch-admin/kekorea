package e3ps.doc.meeting.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.doc.meeting.MeetingTemplate;
import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.doc.meeting.dto.MeetingTemplateDTO;
import e3ps.doc.meeting.service.MeetingHelper;
import e3ps.org.service.OrgHelper;
import e3ps.project.template.service.TemplateHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/meeting/**")
public class MeetingController extends BaseController {

	@Description(value = "회의록 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		ArrayList<Map<String, String>> customers = CommonCodeHelper.manager.getValueMap("CUSTOMER");
		ArrayList<Map<String, String>> maks = CommonCodeHelper.manager.getValueMap("MAK");
		ArrayList<Map<String, String>> projectTypes = CommonCodeHelper.manager.getValueMap("PROJECT_TYPE");
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();

		org.json.JSONArray elecs = OrgHelper.manager.getDepartmentUser("ELEC");
		org.json.JSONArray softs = OrgHelper.manager.getDepartmentUser("SOFT");
		org.json.JSONArray machines = OrgHelper.manager.getDepartmentUser("MACHINE");

		model.addObject("elecs", elecs);
		model.addObject("softs", softs);
		model.addObject("machines", machines);
		model.addObject("list", list);
		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.addObject("maks", maks);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/document/meeting/meeting-list.jsp");
		return model;
	}

	@Description(value = "회의록 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = MeetingHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "회의록 템플릿 리스트 페이지")
	@GetMapping(value = "/template")
	public ModelAndView template() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/document/meeting/meeting-template-list.jsp");
		return model;
	}

	@Description(value = "회의록 템플릿 조회 함수")
	@ResponseBody
	@PostMapping(value = "/template")
	public Map<String, Object> template(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = MeetingHelper.manager.template(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "회의록 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<Map<String, String>> list = MeetingHelper.manager.getMeetingTemplateMap();
		model.addObject("list", list);
		model.setViewName("popup:/document/meeting/meeting-create");
		return model;
	}

	@Description(value = "회의록 등록")
	@PostMapping(value = "/create")
	@ResponseBody
	public Map<String, Object> create(@RequestBody MeetingDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MeetingHelper.service.create(dto);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "회의록 템플릿 페이지")
	@GetMapping(value = "/format")
	public ModelAndView format() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/document/meeting/meeting-template-format");
		return model;
	}

	@Description(value = "회의록 템플릿 등록")
	@PostMapping(value = "/format")
	@ResponseBody
	public Map<String, Object> format(@RequestBody MeetingTemplateDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MeetingHelper.service.format(dto);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "회의록 템플릿 그리드 저장 - 관리자용")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> removeRows = params.get("removeRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ArrayList<MeetingTemplateDTO> removeRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> remove : removeRows) {
				MeetingTemplateDTO dto = mapper.convertValue(remove, MeetingTemplateDTO.class);
				removeRow.add(dto);
			}

			HashMap<String, List<MeetingTemplateDTO>> dataMap = new HashMap<>();
			dataMap.put("removeRows", removeRow); // 삭제행

			MeetingHelper.service.save(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "회의록 템플릿 뷰")
	@GetMapping(value = "/info")
	public ModelAndView info(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		MeetingTemplate meetingTemplate = (MeetingTemplate) CommonUtils.getObject(oid);
		MeetingTemplateDTO dto = new MeetingTemplateDTO(meetingTemplate);
		model.addObject("dto", dto);
		model.setViewName("popup:/document/meeting/meeting-template-info");
		return model;
	}

	@Description(value = "회의록 그리드 저장 - 관리자용")
	@PostMapping(value = "/delete")
	@ResponseBody
	public Map<String, Object> delete(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> removeRows = params.get("removeRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ArrayList<MeetingDTO> removeRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> remove : removeRows) {
				MeetingDTO dto = mapper.convertValue(remove, MeetingDTO.class);
				removeRow.add(dto);
			}

			HashMap<String, List<MeetingDTO>> dataMap = new HashMap<>();
			dataMap.put("removeRows", removeRow); // 삭제행

			MeetingHelper.service.delete(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "회의록 템플릿 내용 가져오기")
	@GetMapping(value = "/getContent")
	@ResponseBody
	public Map<String, Object> getContent(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String content = MeetingHelper.manager.getContent(oid);System.out.println(content);
			result.put("content", content);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "회의록 템플릿 내용 가져오기 ajax")
	@ResponseBody
	@PostMapping(value = "/getContents")
	public Map<String, Object> getContents(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();System.out.println("★★★★★★★★★★★★★★★★★★★★★★★★");
		try {System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			ArrayList<Map<String, String>> content = MeetingHelper.manager.getContents(oid);
			result.put("content", content);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);System.out.println("#################################");
		}System.out.println("&&&&&&&&&&&&&&&&&"+result);
		return result;
	}

	@Description(value = "회의록 뷰")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		MeetingProjectLink link = (MeetingProjectLink) CommonUtils.getObject(oid);
		MeetingDTO dto = new MeetingDTO(link);
		model.addObject("dto", dto);
		model.setViewName("popup:/document/meeting/meeting-view");
		return model;
	}

	@Description(value = "회의록 템플릿 수정 페이지")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		MeetingTemplate meetingTemplate = (MeetingTemplate) CommonUtils.getObject(oid);
		MeetingTemplateDTO dto = new MeetingTemplateDTO(meetingTemplate);
		model.addObject("dto", dto);
		model.setViewName("popup:/document/meeting/meeting-template-modify");
		return model;
	}

	@Description(value = "회의록 템플릿 수정 페이지 등록")
	@PostMapping(value = "/modify")
	@ResponseBody
	public Map<String, Object> modify(@RequestBody MeetingTemplateDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MeetingHelper.service.modify(dto);
			result.put("result", SUCCESS);
			result.put("msg", MODIFY_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

}
