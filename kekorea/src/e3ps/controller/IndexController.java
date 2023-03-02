package e3ps.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.util.ColumnParseUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.StringUtils;
import e3ps.org.beans.UserViewData;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
public class IndexController extends BaseController {

	@Description(value = "메인 페이지")
	@RequestMapping(value = "/index")
	public ModelAndView index(HttpServletRequest request) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("content:/index");
		return model;
	}

	@Description(value = "헤더 페이지")
	@RequestMapping(value = "/header")
	public ModelAndView header(HttpServletRequest request) throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		UserViewData data = new UserViewData(sessionUser);
		model.addObject("data", data);
		model.setViewName("/jsp/layout/header.jsp");
		return model;
	}

	@Description(value = "푸터 페이지")
	@RequestMapping(value = "/footer")
	public ModelAndView footer(HttpServletRequest request) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/layout/footer.jsp");
		return model;
	}

	@Description(value = "그리드 리스트서 주 첨부파일 추가 페이지")
	@RequestMapping(value = "/aui/primary", method = RequestMethod.GET)
	public ModelAndView primary(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/layout/aui/primary");
		return model;
	}

	@Description(value = "그리드 리스트서 첨부파일 추가 페이지")
	@RequestMapping(value = "/aui/secondary", method = RequestMethod.GET)
	public ModelAndView create(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/layout/aui/secondary");
		return model;
	}

	@Description(value = "그리드 리스트서 주 프리뷰 추가 페이지")
	@RequestMapping(value = "/aui/preview", method = RequestMethod.GET)
	public ModelAndView preview(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/layout/aui/preview");
		return model;
	}

	@Description(value = "그리드 썸네일 보기 페이지")
	@RequestMapping(value = "/aui/thumbnail", method = RequestMethod.GET)
	public ModelAndView thumbnail(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		String base64 = ContentUtils.getPreViewBase64(oid);
		model.addObject("base64", base64);
		model.setViewName("popup:/layout/aui/thumbnail");
		return model;
	}

	@Description(value = "그리드 리스트 상에서 Lazy Load 시 호출 하는 함수")
	@PostMapping(value = "/appendData")
	@ResponseBody
	public Map<String, Object> appendData(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		long sessionid = StringUtils.parseLong((String) params.get("sessionid"));
		int start = (int) params.get("start");
		int end = (int) params.get("end");
		try {
			PagingQueryResult qr = PagingSessionHelper.fetchPagingSession(start, end, sessionid);
			ArrayList list = ColumnParseUtils.parse(qr);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}
}
