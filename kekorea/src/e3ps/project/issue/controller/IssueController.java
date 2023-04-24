package e3ps.project.issue.controller;

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

import e3ps.common.controller.BaseController;
import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.project.Project;
import e3ps.project.issue.IssueProjectLink;
import e3ps.project.issue.beans.IssueDTO;
import e3ps.project.issue.service.IssueHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/issue/**")
public class IssueController extends BaseController {

	@Description(value = "특이사항 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/project/issue/issue-list.jsp");
		return model;
	}

	@Description(value = "특이사항 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = IssueHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "특이사항 뷰 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		IssueProjectLink link = (IssueProjectLink) CommonUtils.getObject(oid);
		IssueDTO dto = new IssueDTO(link);
		model.addObject("dto", dto);
		model.setViewName("popup:/project/issue/issue-view");
		return model;
	}

	@Description(value = "특이사항 등록")
	@PostMapping(value = "/save")
	@ResponseBody
	public Map<String, Object> save(@RequestBody Map<String, ArrayList<LinkedHashMap<String, Object>>> params)
			throws Exception {
		ArrayList<LinkedHashMap<String, Object>> removeRows = params.get("removeRows");
		Map<String, Object> result = new HashMap<String, Object>();
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			ArrayList<IssueDTO> removeRow = new ArrayList<>();
			for (LinkedHashMap<String, Object> remove : removeRows) {
				IssueDTO dto = mapper.convertValue(remove, IssueDTO.class);
				removeRow.add(dto);
			}

			HashMap<String, List<IssueDTO>> dataMap = new HashMap<>();
			dataMap.put("removeRows", removeRow); // 삭제행

			IssueHelper.service.save(dataMap);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "이슈 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/project/issue/issue-create");
		return model;
	}

	@Description(value = "이슈 등록")
	@PostMapping(value = "/create")
	@ResponseBody
	public Map<String, Object> create(@RequestBody IssueDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			IssueHelper.service.create(dto);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
