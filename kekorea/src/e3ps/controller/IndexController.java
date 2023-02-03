//package e3ps.controller;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.servlet.ModelAndView;
//
//import e3ps.org.beans.UserData;
//import wt.org.WTUser;
//import wt.session.SessionHelper;
//
//@Controller
//public class IndexController extends BaseController {

//	@RequestMapping(value = "/index")
//	public ModelAndView index(HttpServletRequest request) throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("content:/index");
//		return model;
//	}
//
//	@RequestMapping(value = "/header")
//	public ModelAndView header(HttpServletRequest request) throws Exception {
//		ModelAndView model = new ModelAndView();
//		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
//		UserData data = new UserData(sessionUser);
//		model.addObject("data", data);
//		model.setViewName("/jsp/layout/header.jsp");
//		return model;
//	}
//
//	@RequestMapping(value = "/footer")
//	public ModelAndView footer(HttpServletRequest request) throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("/jsp/layout/footer.jsp");
//		return model;
//	}
//}
