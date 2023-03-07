package e3ps.common.controller;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.util.CommonUtils;
import e3ps.org.dto.UserDTO;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
public class IndexController extends BaseController {

	@Description(value = "메인 페이지")
	@GetMapping(value = "/index")
	public ModelAndView index() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("content:/index");
		return model;
	}

	@Description(value = "헤더 페이지")
	@GetMapping(value = "/header")
	public ModelAndView header() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		UserDTO data = new UserDTO(sessionUser);
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.addObject("data", data);
		model.setViewName("/extcore/layout/header.jsp");
		return model;
	}

	@Description(value = "푸터 페이지")
	@GetMapping(value = "/footer")
	public ModelAndView footer() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/layout/footer.jsp");
		return model;
	}
}
