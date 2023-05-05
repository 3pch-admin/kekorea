package e3ps.korea.configSheet.controller;

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
import e3ps.admin.configSheetCode.ConfigSheetCode;
import e3ps.admin.configSheetCode.service.ConfigSheetCodeHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.korea.configSheet.ConfigSheet;
import e3ps.korea.configSheet.ConfigSheetProjectLink;
import e3ps.korea.configSheet.beans.ConfigSheetDTO;
import e3ps.korea.configSheet.service.ConfigSheetHelper;
import e3ps.org.service.OrgHelper;
import e3ps.project.Project;
import e3ps.project.template.service.TemplateHelper;
import net.sf.json.JSONArray;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
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
			result.put("msg", e.toString());
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
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "CONFIG SHEET 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		ConfigSheet configSheet = (ConfigSheet) CommonUtils.getObject(oid);
		JSONArray data = ConfigSheetHelper.manager.loadBaseGridData(oid);
//		JSONArray history = WorkspaceHelper.manager.jsonAuiHistory(configSheet);
		ConfigSheetDTO dto = new ConfigSheetDTO(configSheet);
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("oid", oid);
		model.addObject("data", data);
		model.addObject("dto", dto);
//		model.addObject("history", history);
		model.setViewName("popup:/korea/configSheet/configSheet-view");
		return model;
	}

	@Description(value = "CONIFG SHEET 비교 페이지 공통 함수")
	@GetMapping(value = "/compare")
	public ModelAndView compare(@RequestParam String oid, @RequestParam String compareArr) throws Exception {
		ModelAndView model = new ModelAndView();
		Project p1 = (Project) CommonUtils.getObject(oid);

		String[] compareOids = compareArr.split(",");
		ArrayList<Project> destList = new ArrayList<>(compareOids.length);
		for (String _oid : compareOids) {
			Project project = (Project) CommonUtils.getObject(_oid);
			destList.add(project);
		}

		ArrayList<ConfigSheetCode> fixedList = ConfigSheetCodeHelper.manager.getConfigSheetCode("CATEGORY");
		ArrayList<Map<String, Object>> data = ConfigSheetHelper.manager.compare(p1, destList, fixedList);

		model.addObject("p1", p1);
		model.addObject("oid", oid);
		model.addObject("fixedList", fixedList);
		model.addObject("destList", destList); // 최초 선택 데이터 제거
		model.addObject("data", JSONArray.fromObject(data));
		model.setViewName("popup:/korea/configSheet/configSheet-compare");
		return model;
	}

	@Description(value = "CONFIG SHEET 복사할 작번 추가 페이지")
	@GetMapping(value = "/copy")
	public ModelAndView copy(@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -4);
		Timestamp date = new Timestamp(calendar.getTime().getTime());
		String before = date.toString().substring(0, 10);
		String end = DateUtils.getCurrentTimestamp().toString().substring(0, 10);

		ArrayList<Map<String, String>> customers = CommonCodeHelper.manager.getValueMap("CUSTOMER");
		ArrayList<Map<String, String>> maks = CommonCodeHelper.manager.getValueMap("MAK");
		ArrayList<Map<String, String>> projectTypes = CommonCodeHelper.manager.getValueMap("PROJECT_TYPE");
		ArrayList<HashMap<String, String>> list = TemplateHelper.manager.getTemplateArrayMap();

		JSONArray elecs = OrgHelper.manager.getDepartmentUser("ELEC");
		JSONArray softs = OrgHelper.manager.getDepartmentUser("SOFT");
		JSONArray machines = OrgHelper.manager.getDepartmentUser("MACHINE");

		model.addObject("elecs", elecs);
		model.addObject("softs", softs);
		model.addObject("machines", machines);
		model.addObject("list", list);
		model.addObject("customers", customers);
		model.addObject("projectTypes", projectTypes);
		model.addObject("maks", maks);
		model.addObject("before", before);
		model.addObject("end", end);
		model.addObject("sessionUser", sessionUser);
		model.addObject("isAdmin", isAdmin);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/korea/configSheet/configSheet-copy");
		return model;
	}

	@Description(value = "CONFIG SHEET 복사하기")
	@PostMapping(value = "/copy")
	@ResponseBody
	public Map<String, Object> copy(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		String oid = (String) params.get("oid");
		try {

			ConfigSheet configSheet = null;
			Project project = (Project) CommonUtils.getObject(oid);
			QueryResult qr = PersistenceHelper.manager.navigate(project, "configSheet", ConfigSheetProjectLink.class);
			if (qr.size() == 0) {
				result.put("result", FAIL);
				result.put("msg", "작번에 연결된 CONFIG SHEET가 존재하지 않습니다.");
				return result;
			}

			if (qr.hasMoreElements()) {
				configSheet = (ConfigSheet) qr.nextElement();
				ArrayList<Map<String, Object>> list = ConfigSheetHelper.manager.copyBaseData(configSheet);
				result.put("list", list);
				result.put("result", SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "CONFIG SHEET 그리드 저장 - 관리자용")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> removeRows = params.get("removeRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			ArrayList<ConfigSheetDTO> removeRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> remove : removeRows) {
				ConfigSheetDTO dto = mapper.convertValue(remove, ConfigSheetDTO.class);
				removeRow.add(dto);
			}

			HashMap<String, List<ConfigSheetDTO>> dataMap = new HashMap<>();
			dataMap.put("removeRows", removeRow); // 삭제행

			ConfigSheetHelper.service.save(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "CONFIG SHEET 삭제")
	@GetMapping(value = "/delete")
	@ResponseBody
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ConfigSheetHelper.service.delete(oid);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "산출물 태스크에서 CONFIG SHEET 연결 페이지")
	@GetMapping(value = "/connect")
	public ModelAndView connect(@RequestParam String poid, @RequestParam String toid) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.addObject("toid", toid);
		model.addObject("poid", poid);
		model.setViewName("popup:/korea/configSheet/configSheet-connect");
		return model;
	}

	@Description(value = "CONFIG SHEET 태스크에서 연결")
	@PostMapping(value = "/connect")
	@ResponseBody
	public Map<String, Object> connect(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ConfigSheetHelper.service.connect(params);

			if ((boolean) result.get("exist")) {
				result.put("result", FAIL);
				result.put("msg", "이미 해당 태스크와 연결된 CONFIG SHEET 입니다.");
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

	@Description(value = "CONFIG SHEET 태스크 연결 제거 함수")
	@ResponseBody
	@GetMapping(value = "/disconnect")
	public Map<String, Object> disconnect(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ConfigSheetHelper.service.disconnect(oid);
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
