package e3ps.admin.spec.controller;

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
import e3ps.admin.sheetvariable.service.ItemsHelper;
import e3ps.admin.spec.Spec;
import e3ps.admin.spec.service.OptionsHelper;
import e3ps.common.util.CommonUtils;
import e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/option/**")
public class OptionsController extends BaseController {

	@Description(value = "CONFIG SHEET 아이템 등록 함수")
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public Map<String, Object> create(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			OptionsHelper.service.create(params);
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
		Spec spec = (Spec) CommonUtils.getObject(oid);
		model.addObject("spec", spec);
		model.addObject("oid", oid);
		model.setViewName("popup:/admin/options/options-create");
		return model;
	}
}
