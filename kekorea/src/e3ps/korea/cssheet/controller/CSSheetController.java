package e3ps.korea.cssheet.controller;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/cssheet/**")
public class CSSheetController extends BaseController {

	@Description(value = "CSSheet 리스트 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/korea/cssheet/cssheet-list.jsp");
		return model;
	}
}
