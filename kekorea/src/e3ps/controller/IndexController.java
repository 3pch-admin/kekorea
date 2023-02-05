package e3ps.controller;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.common.util.ContentUtils;
import e3ps.org.beans.UserData;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
public class IndexController extends BaseController {

	@Description("메인 페이지")
	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest request) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("content:/index");
		return model;
	}

	@Description("헤더 페이지")
	@RequestMapping(value = "/header")
	public ModelAndView header(HttpServletRequest request) throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		UserData data = new UserData(sessionUser);
		model.addObject("data", data);
		model.setViewName("/jsp/layout/header.jsp");
		return model;
	}

	@Description("푸터 페이지")
	@RequestMapping(value = "/footer")
	public ModelAndView footer(HttpServletRequest request) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/layout/footer.jsp");
		return model;
	}

	@Description("그리드 리스트서 주 첨부파일 추가 페이지")
	@RequestMapping(value = "/aui/primary", method = RequestMethod.GET)
	public ModelAndView primary(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/layout/common/primary");
		return model;
	}

	@Description("그리드 리스트서 첨부파일 추가 페이지")
	@RequestMapping(value = "/aui/secondary", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/layout/aui/secondary");
		return model;
	}

	@Description("그리드 리스트서 주 프리뷰 추가 페이지")
	@RequestMapping(value = "/aui/preview", method = RequestMethod.GET)
	public ModelAndView preview(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("oid", oid);
		model.setViewName("popup:/layout/aui/preview");
		return model;
	}

	@Description("그리드 썸네일 보기 페이지")
	@RequestMapping(value = "/aui/thumbnail", method = RequestMethod.GET)
	public ModelAndView thumbnail(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		String base64 = ContentUtils.getPreViewBase64(oid);
		model.addObject("base64", base64);
		model.setViewName("popup:/layout/aui/thumbnail");
		return model;
	}
}
