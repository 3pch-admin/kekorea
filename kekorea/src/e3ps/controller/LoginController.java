package e3ps.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import e3ps.admin.service.AdminHelper;

@Controller
public class LoginController extends BaseController {

	@Description("로긴 페이지")
	@RequestMapping(value = "/loginPage")
	public ModelAndView loginPage(HttpServletRequest request) throws Exception {
		ModelAndView model = new ModelAndView();
		String ip = request.getRemoteAddr();
		String id = request.getRemoteUser();
		AdminHelper.service.loginHistoryAction(id, ip);

		String url = "/Windchill/plm/common/main";
		RedirectView rv = new RedirectView(url);
		model.setView(rv);
		return model;
	}

	@Description("로그아웃 페이지")
	@RequestMapping(value = "/logout")
	public ModelAndView logout() {
		ModelAndView model = new ModelAndView();
		model.setViewName("/login/logout.jsp");
		return model;
	}
}
