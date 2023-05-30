package e3ps.doc.meeting.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.meeting.Meeting;
import e3ps.doc.meeting.MeetingProjectLink;
import e3ps.doc.meeting.MeetingTemplate;
import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.doc.meeting.dto.MeetingTemplateDTO;
import e3ps.doc.meeting.service.MeetingHelper;
import e3ps.project.Project;
import e3ps.project.task.Task;
import e3ps.project.template.service.TemplateHelper;
import net.sf.json.JSONArray;
import wt.fc.Persistable;
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
	public ModelAndView create(@RequestParam(required = false) String poid, @RequestParam(required = false) String toid)
			throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<Map<String, String>> list = MeetingHelper.manager.getMeetingTemplateMap();

		if (!StringUtils.isNull(poid) && !StringUtils.isNull(toid)) {
			Project project = (Project) CommonUtils.getObject(poid);
			ArrayList<Map<String, String>> data = new ArrayList<>();
			Map<String, String> map = new HashMap<>();
			map.put("oid", project.getPersistInfo().getObjectIdentifier().getStringValue());
			map.put("projectType_name", project.getProjectType().getName());
			map.put("customer_name", project.getCustomer() != null ? project.getCustomer().getName() : "");
			map.put("mak_name", project.getMak() != null ? project.getMak().getName() : "");
			map.put("detail_name", project.getDetail() != null ? project.getDetail().getName() : "");
			map.put("install_name", project.getInstall() != null ? project.getInstall().getName() : "");
			map.put("kekNumber", project.getKekNumber());
			map.put("keNumber", project.getKeNumber());
			map.put("description", project.getDescription());
			data.add(map); // 기본 선택한 작번

			Task task = (Task) CommonUtils.getObject(toid);
			model.addObject("location", "/Default/프로젝트/" + task.getName());
			model.addObject("toid", toid);
			model.addObject("poid", poid);
			model.addObject("data", JSONArray.fromObject(data));
		}

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
			String content = MeetingHelper.manager.getContent(oid);
			System.out.println(content);
			result.put("content", content);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "회의록 뷰")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Persistable per = CommonUtils.getObject(oid);
		MeetingDTO dto = null;
		if (per instanceof MeetingProjectLink) {
			MeetingProjectLink link = (MeetingProjectLink) per;
			dto = new MeetingDTO(link);
		} else if (per instanceof Meeting) {
			Meeting meeting = (Meeting) per;
			dto = new MeetingDTO(meeting);
		}
		model.addObject("dto", dto);
		model.setViewName("popup:/document/meeting/meeting-view");
		return model;
	}

	@Description(value = "회의록 수정 뷰")
	@GetMapping(value = "/update")
	public ModelAndView update(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		MeetingProjectLink link = (MeetingProjectLink) CommonUtils.getObject(oid);
		MeetingDTO dto = new MeetingDTO(link);
		ArrayList<Map<String, String>> list = MeetingHelper.manager.getMeetingTemplateMap();
		model.addObject("dto", dto);
		model.addObject("list", list);
		model.setViewName("popup:/document/meeting/meeting-update");
		return model;
	}

	@Description(value = "회의록 수정")
	@PostMapping(value = "/update")
	@ResponseBody
	public Map<String, Object> update(@RequestBody MeetingDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MeetingHelper.service.update(dto);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
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
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "산출물 회의록 연결 페이지")
	@GetMapping(value = "/connect")
	public ModelAndView connect(@RequestParam String poid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		ArrayList<Map<String, String>> customers = CommonCodeHelper.manager.getValueMap("CUSTOMER");
		ArrayList<Map<String, String>> maks = CommonCodeHelper.manager.getValueMap("MAK");
		ArrayList<Map<String, String>> projectTypes = CommonCodeHelper.manager.getValueMap("PROJECT_TYPE");
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();
		model.addObject("list", list);
		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.addObject("maks", maks);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.addObject("toid", toid);
		model.addObject("poid", poid);
		model.setViewName("popup:/document/meeting/meeting-connect");
		return model;
	}

	@Description(value = "회의록 태스크에서 연결")
	@PostMapping(value = "/connect")
	@ResponseBody
	public Map<String, Object> connect(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = MeetingHelper.service.connect(params);

			if ((boolean) result.get("exist")) {
				result.put("result", FAIL);
				result.put("msg", "이미 해당 태스크와 연결된 회의록 입니다.");
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

	@Description(value = "회의록 태스트 연결 제거 함수")
	@ResponseBody
	@PostMapping(value = "/disconnect")
	public Map<String, Object> disconnect(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			MeetingHelper.service.disconnect(params);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
