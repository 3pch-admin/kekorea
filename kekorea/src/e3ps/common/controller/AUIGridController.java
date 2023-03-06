package e3ps.common.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.ContentUtils;

@Controller
@RequestMapping(value = "/aui/**")
public class AUIGridController extends BaseController {

	@Description(value = "그리드 리스트서 주 첨부파일 추가 페이지")
	@GetMapping(value = "/primary")
	public ModelAndView primary(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/common/aui/primary");
		return model;
	}

	@Description(value = "그리드 리스트서 첨부파일 추가 페이지")
	@RequestMapping(value = "/secondary")
	public ModelAndView create(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/common/aui/secondary");
		return model;
	}

	@Description(value = "그리드 썸네일 보기 페이지")
	@GetMapping(value = "/thumbnail")
	public ModelAndView thumbnail(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		String base64 = ContentUtils.getPreViewBase64(oid);
		model.addObject("base64", base64);
		model.setViewName("popup:/common/aui/thumbnail");
		return model;
	}

	@Description(value = "그리드 리스트서 주 프리뷰 추가 페이지")
	@RequestMapping(value = "/preview", method = RequestMethod.GET)
	public ModelAndView preview(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/layout/aui/preview");
		return model;
	}
	
	@Description(value = "그리드 첨부파일 업로드")
	@GetMapping(value = "/upload")
	@ResponseBody
	public Map<String, Object> upload(HttpServletRequest request) throws Exception {
		return AUIGridUtils.upload(request);
	}

	@Description(value = "미리보기 업로드")
	@PostMapping(value = "/preview")
	@ResponseBody
	public Map<String, Object> preview(HttpServletRequest request) throws Exception {
		return AUIGridUtils.preview(request);
	}
}
