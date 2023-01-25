package e3ps.korea.cip.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/cip/**")
public class CipController extends BaseController {

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/korea/cip/cip-list.jsp");
		return model;
	}
}
