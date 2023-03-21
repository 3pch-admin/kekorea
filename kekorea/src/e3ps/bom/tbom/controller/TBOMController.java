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

import e3ps.bom.tbom.dto.TBOMDTO;
import e3ps.bom.tbom.service.TBOMHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.workOrder.service.WorkOrderHelper;
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

	@Description(value = "T-BOM 비교")
	@GetMapping(value = "/compare")
	public ModelAndView compare(HttpServletRequest request) throws Exception {
		int count = StringUtils.parseInt(request.getParameter("count"));
		ModelAndView model = new ModelAndView();
		ArrayList<Map<String, Object>> headers = TBOMHelper.manager.headers(request, count);
		ArrayList<Map<String, Object>> compareData = TBOMHelper.manager.compare(request, count);
		model.addObject("compareData", JSONArray.fromObject(compareData));
		model.addObject("headers", headers);
		model.addObject("count", count);
		model.setViewName("popup:/bom/tbom/tbom-compare");
		return model;
	}

	@Description(value = "T-BOM 탭 페이지")
	@GetMapping(value = "/tabper")
	public ModelAndView tabper(@RequestParam String oid, @RequestParam String moid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.addObject("moid", moid);
		model.setViewName("popup:/bom/tbom/tbom-tabper");
		return model;
	}
}
