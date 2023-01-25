package e3ps.admin.sheetvariable.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.sheetvariable.Category;
import e3ps.admin.sheetvariable.beans.ItemsColumnData;
import e3ps.admin.sheetvariable.service.CategoryHelper;
import e3ps.admin.sheetvariable.service.ItemsHelper;
import e3ps.common.util.CommonUtils;
import e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/items/**")
public class ItemsController extends BaseController {

	@Description(value = "CONFIG SHEET 아이템 리스트 가져 오는 함수")
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = ItemsHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "CONFIG SHEET 아이템 등록 함수")
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ItemsHelper.service.create(params);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "CONFIG SHEET 아이템 생성 페이지")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		Category category = (Category) CommonUtils.getObject(oid);
		model.addObject("category", category);
		model.addObject("oid", oid);
		model.setViewName("popup:/admin/items/items-create");
		return model;
	}

}