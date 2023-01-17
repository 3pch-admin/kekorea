package e3ps.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import wt.log4j.LogR;

@Controller
public class IndexController {

	private static final Logger logger = LogR.getLogger(IndexController.class.getName());

	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest request) {
		logger.info("Call IndexController index Method");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("content:/index");
		return mv;
	}

}
