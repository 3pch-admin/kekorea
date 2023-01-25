package e3ps.korea.history.controller;

import java.util.ArrayList;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/history/**")
public class HistoryController extends BaseController {

	@Description(value = "이력관리 리스트 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/korea/history/history-list.jsp");
		return model;
	}
}
