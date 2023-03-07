package e3ps.project.output.controller;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.controller.BaseController;

@Controller
@RequestMapping(value = "/output/**")
public class OutputController extends BaseController {

	@Description(value = "산출물 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/project/output/output-list.jsp");
		return model;
	}
}
