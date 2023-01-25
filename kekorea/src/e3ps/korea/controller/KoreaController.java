package e3ps.korea.controller;

import java.util.ArrayList;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/korea/**")
public class KoreaController extends BaseController {

	@Description(value = "한국 생산 차트 리스트 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		ArrayList<CommonCode> installs = CommonCodeHelper.manager.getArrayCodeList("INSTALL");
		model.addObject("installs", installs);
		model.setViewName("/jsp/korea/korea-list.jsp");
		return model;
	}
}
