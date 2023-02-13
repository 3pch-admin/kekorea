package e3ps.doc.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.code.service.CommonCodeHelper;
import e3ps.controller.BaseController;
import e3ps.doc.service.DocumentHelper;
import e3ps.project.template.Template;

@Controller
@RequestMapping(value = "/document/**")
public class DocumentController extends BaseController {

	@Description("문서 조회 페이지")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/document/document-list.jsp");
		return model;
	}

	@Description("문서 목록")
	@ResponseBody
	@RequestMapping(value = "/list", method = RequestMethod.POST)
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentHelper.manager.find(params);
			result.put("result", true);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", false);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description("문서 등록 페이지")
	@RequestMapping(value = "/create", method = RequestMethod.GET)
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/document/document-create");
		return model;
	}

	@Description("문서 결재 페이지")
	@RequestMapping(value = "/approval", method = RequestMethod.GET)
	public ModelAndView approval() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/document/approvalDocument.jsp");
		return model;
	}

	@Description("문서 결재")
	@RequestMapping(value = "/approval", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> approval(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentHelper.service.approvalDocumentAction(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			result.put("result", FAIL);
			e.printStackTrace();
		}
		return result;
	}

	@Description("산출물 조회 페이지")
	@RequestMapping(value = "/listOutput", method = RequestMethod.GET)
	public ModelAndView listOutput() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/document/output-list.jsp");
		return model;
	}

	@Description("산출물 조회")
	@ResponseBody
	@RequestMapping(value = "/listOutput", method = RequestMethod.POST)
	public Map<String, Object> listOutput(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentHelper.manager.findOutput(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description("산출물 등록 페이지")
	@RequestMapping(value = "/createOutput", method = RequestMethod.GET)
	public ModelAndView createOutput() throws Exception {
		ModelAndView model = new ModelAndView();
//
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//
//		String number = DocumentHelper.manager.getNextNumber("PJ-");
//
//		model.addObject("number", number);
//		if (isPopup) {
//			model.setViewName("popup:/document/createOutput");
//		} else {
//			model.setViewName("default:/document/createOutput");
//		}
//		model.setViewName("/jsp/document/output-create.jsp");
		model.setViewName("popup:/document/output-create");
		return model;
	}

	@Description("의뢰서 조회 페이지")
	@RequestMapping(value = "/listRequestDocument", method = RequestMethod.GET)
	public ModelAndView listRequestDocument() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/document/requestDocument-list.jsp");
		return model;
	}

	@Description("의뢰서 조회")
	@ResponseBody
	@RequestMapping(value = "/listRequestDocument", method = RequestMethod.POST)
	public Map<String, Object> listRequestDocument(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentHelper.manager.findRequestDocument(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
	
	@Description("의뢰서 등록 페이지")
	@RequestMapping(value="/createRequestDocument", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView createRequestDocument(@RequestParam Map<String, Object> params) throws Exception {
		ModelAndView model = new ModelAndView();
		
		String install = CommonCodeHelper.manager.getCommonCodeByCommonCodeType("INSTALL");
		String customer = CommonCodeHelper.manager.getCommonCodeByCommonCodeType("CUSTOMER");
		boolean isPopup = Boolean.parseBoolean((String) params.get("popup"));
//		ArrayList<Template> tmp = TemplateHelper.service.getTemplate();
		model.addObject("install", install);
		model.addObject("customer", customer);
		model.addObject("isPopup", isPopup);
		
		if(isPopup) {
			model.setViewName("popup:/document/requestDocument-create");
		} else {
			model.setViewName("popup:/document/requestDocument-create");
		}
		return model;
	}
	

	@Description("첨부파일 조회 페이지")
	@RequestMapping(value = "/listContents", method = RequestMethod.GET)
	public ModelAndView listContents() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/jsp/document/contents-list.jsp");
		return model;
	}

	@Description("첨부파일 조회")
	@ResponseBody
	@RequestMapping(value = "/listContents", method = RequestMethod.POST)
	public Map<String, Object> listContents(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentHelper.manager.findContents(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
}