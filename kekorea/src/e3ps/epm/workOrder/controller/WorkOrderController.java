package e3ps.epm.workOrder.controller;

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

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.epm.keDrawing.service.KeDrawingHelper;
import e3ps.epm.workOrder.WorkOrder;
import e3ps.epm.workOrder.dto.WorkOrderDTO;
import e3ps.epm.workOrder.service.WorkOrderHelper;
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
		model.addObject("dto", dto);
		model.setViewName("popup:/epm/workOrder/workOrder-view");
		return model;
	}
	
	@Description(value = "도면 일람표에 등록된 도면 정보 페이지")
	@GetMapping(value = "/data")
	public ModelAndView data(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WorkOrder workOrder = (WorkOrder) CommonUtils.getObject(oid);
		JSONArray list = KeDrawingHelper.manager.getData(workOrder);
		model.addObject("list", list);
		model.setViewName("popup:/epm/workOrder/workOrder-data");
		return model;
	}

	@Description(value = "작업지시서 탭 페이지")
	@GetMapping(value = "/tabper")
	public ModelAndView tabper(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/epm/workOrder/workOrder-tabper");
		return model;
	}
}
