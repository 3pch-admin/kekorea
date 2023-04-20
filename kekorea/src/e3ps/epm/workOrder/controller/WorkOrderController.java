package e3ps.epm.workOrder.controller;

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
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.dto.WorkOrderDTO;
import e3ps.epm.workOrder.service.WorkOrderHelper;
import e3ps.org.Department;
import e3ps.org.People;
import e3ps.project.Project;
import e3ps.project.template.service.TemplateHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/workOrder/**")
public class WorkOrderController extends BaseController {

	@Description(value = "작업지시서 리스트 페이지")
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
		model.setViewName("/extcore/jsp/epm/workOrder/workOrder-list.jsp");
		return model;
	}

	@Description(value = "작업지시서 리스트 가져 오는 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkOrderHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "작업지시서 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody WorkOrderDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkOrderHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "작업지시서 생성 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		People people = CommonUtils.sessionPeople();
		Department department = people.getDepartment();
		String workOrderType = "";
		if (department.getCode().equals("MACHINE")) {
			workOrderType = "기계";
		} else if (department.getCode().equals("ELEC")) {
			workOrderType = "전기";
		}
		model.addObject("workOrderType", workOrderType);
		model.setViewName("popup:/epm/workOrder/workOrder-create");
		return model;
	}

	@Description(value = "KE도면들 번호로 찾아오기 (KE OR EPM)")
	@ResponseBody
	@GetMapping(value = "/getData")
	public Map<String, Object> getData(@RequestParam String number) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = WorkOrderHelper.manager.getData(number);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "도면 일람표 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);
		WorkOrderDTO dto = new WorkOrderDTO(workOrder);
		JSONArray list = KeDrawingHelper.manager.getData(workOrder);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = CommonUtils.sessionUser();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.addObject("list", list);
		model.addObject("dto", dto);
		model.setViewName("popup:/epm/workOrder/workOrder-view");
		return model;
	}

	@Description(value = "도면일람표 비교 페이지 공통 함수")
	@GetMapping(value = "/compare")
	public ModelAndView compare(@RequestParam String oid, @RequestParam String compareArr) throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		Project p1 = (Project) CommonUtils.getObject(oid);
		String[] compareOids = compareArr.split(",");
		ArrayList<Project> destList = new ArrayList<>(compareOids.length);
		for (String _oid : compareOids) {
			Project project = (Project) CommonUtils.getObject(_oid);
			destList.add(project);
		}

		ArrayList<Map<String, Object>> data = WorkOrderHelper.manager.compare(p1, destList);
		model.addObject("sessionUser", sessionUser);
		model.addObject("p1", p1);
		model.addObject("oid", oid);
		model.addObject("destList", destList);
		model.addObject("data", JSONArray.fromObject(data));
		model.setViewName("popup:/epm/workOrder/workOrder-compare");
		return model;
	}

	@Description(value = "도면 일람표 수정 페이지")
	@GetMapping(value = "/modify")
	public ModelAndView modify(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);
		WorkOrderDTO dto = new WorkOrderDTO(workOrder);
		JSONArray list = KeDrawingHelper.manager.getData(workOrder);
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = CommonUtils.sessionUser();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.addObject("list", list);
		model.addObject("dto", dto);
		model.setViewName("popup:/epm/workOrder/workOrder-modify");
		return model;
	}

	@Description(value = "도면일람표 수정 페이지 등록")
	@PostMapping(value = "/modify")
	@ResponseBody
	public Map<String, Object> modify(@RequestBody WorkOrderDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			WorkOrderHelper.service.modify(dto);
			result.put("result", SUCCESS);
			result.put("msg", MODIFY_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

}
