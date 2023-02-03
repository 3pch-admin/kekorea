//package e3ps.controller;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.log4j.Logger;
//import org.springframework.context.annotation.Description;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.ModelAndView;
//
//import e3ps.common.StateKeys;
//import e3ps.common.code.service.CommonCodeHelper;
//import e3ps.common.content.Contents;
//import e3ps.common.content.ContentsPersistablesLink;
//import e3ps.doc.ReqDocumentProjectLink;
//import e3ps.doc.RequestDocument;
//import e3ps.doc.beans.DocumentViewData;
//import e3ps.doc.beans.OutputViewData;
//import e3ps.doc.beans.RequestDocumentViewData;
//import e3ps.doc.service.DocumentHelper;
//import e3ps.project.ProjectOutputLink;
//import e3ps.project.Template;
//import e3ps.project.service.TemplateHelper;
//import wt.doc.WTDocument;
//import wt.fc.PersistenceHelper;
//import wt.fc.ReferenceFactory;
//import wt.log4j.LogR;
//import wt.org.WTUser;
//import wt.session.SessionHelper;
//import wt.vc.VersionControlHelper;
//import wt.vc.Versioned;

//@Controller
//public class DocumentController extends BaseController {

//	private static final Logger logger = LogR.getLogger(DocumentController.class.getName());
//
//	@Description("문서 번호 세팅")
//	@ResponseBody
//	@RequestMapping(value = "/document/setNumber")
//	public Map<String, Object> setNumber(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.manager.setNumber(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("의뢰서 수정 페이지")
//	@RequestMapping(value = "/document/modifyRequestDocument")
//	public ModelAndView modifyRequestDocument(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		RequestDocument document = null;
//		RequestDocumentViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			document = (RequestDocument) rf.getReference(oid).getObject();
//			data = new RequestDocumentViewData(document);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		String install = CommonCodeHelper.manager.getCommonCodeByCommonCodeType("INSTALL");
//
//		model.addObject("install", install);
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/document/modifyRequestDocument");
//		} else {
//			model.setViewName("default:/document/modifyRequestDocument");
//		}
//		return model;
//	}
//
//	@Description("의뢰서 정보 페이지")
//	@RequestMapping(value = "/document/viewRequestDocument")
//	public ModelAndView viewRequestDocument(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		RequestDocument reqDoc = null;
//		RequestDocumentViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		ArrayList<ReqDocumentProjectLink> list = new ArrayList<ReqDocumentProjectLink>();
//		try {
//			reqDoc = (RequestDocument) rf.getReference(oid).getObject();
//			data = new RequestDocumentViewData(reqDoc);
//			list = DocumentHelper.manager.getProjectReqLink(reqDoc);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("projectList", list);
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/document/viewRequestDocument");
//		} else {
//			model.setViewName("default:/document/viewRequestDocument");
//		}
//		return model;
//	}
//
//	@Description("의뢰서 조회 페이지")
//	@RequestMapping(value = "/document/listRequestDocument")
//	public ModelAndView listRequestDocument() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/document/listRequestDocument");
//		return model;
//	}
//
//	@Description("의뢰서 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/document/listRequestDocumentAction")
//	public Map<String, Object> listRequestDocumentAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.manager.findRequestDocument(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("의뢰서 수정")
//	@RequestMapping(value = "/document/modifyRequestDocumentAction")
//	@ResponseBody
//	public Map<String, Object> modifyRequestDocumentAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.service.modifyRequestDocumentAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("의뢰서 등록")
//	@RequestMapping(value = "/document/createRequestDocumentAction")
//	@ResponseBody
//	public Map<String, Object> createRequestDocumentAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.service.createRequestDocumentAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("산출물 조회 페이지")
//	@RequestMapping(value = "/document/viewOutput")
//	public ModelAndView viewOutput(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isTask = Boolean.parseBoolean((String) param.get("isTask"));
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		WTDocument master = null;
//		OutputViewData data = null;
//		ArrayList<ProjectOutputLink> projectList = new ArrayList<ProjectOutputLink>();
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			master = (WTDocument) rf.getReference(oid).getObject();
//			data = new OutputViewData(master);
//			projectList = DocumentHelper.manager.getProjectOutputLink(master);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("projectList", projectList);
//		model.addObject("data", data);
//		model.addObject("isTask", isTask);
//		if (isPopup) {
//			model.setViewName("popup:/document/viewOutput");
//		} else {
//			model.setViewName("default:/document/viewOutput");
//		}
//		return model;
//	}
//
//	@Description("산출물 통합 조회 페이지")
//	@RequestMapping(value = "/document/listOutput")
//	public ModelAndView listOutput() throws Exception {
//		ModelAndView model = new ModelAndView();
//		// default 승인됨
//		StateKeys[] states = StateKeys.values();
//		model.addObject("states", states);
//		model.setViewName("default:/document/listOutput");
//		return model;
//	}
//
//	@Description("산출물 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/document/listOutputAction")
//	public Map<String, Object> listOutputAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.manager.findOutput(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("산출물 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/document/listOldOuptutAction")
//	public Map<String, Object> listOldOuptutAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.manager.findOldOutput(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("산출물 등록 페이지")
//	@RequestMapping(value = "/document/createOutput")
//	public ModelAndView createOutput(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
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
//		return model;
//	}
//
//	@Description("산출물 등록")
//	@RequestMapping(value = "/document/createOutputAction")
//	@ResponseBody
//	public Map<String, Object> createOutputAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.service.createOutputAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("의뢰서 등록 페이지")
//	@RequestMapping(value = "/document/createRequestDocument")
//	@ResponseBody
//	public ModelAndView createRequestDocument(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//
//		String customer = CommonCodeHelper.manager.getCommonCodeByCommonCodeType("CUSTOMER");
//
//		String install = CommonCodeHelper.manager.getCommonCodeByCommonCodeType("INSTALL");
//		ArrayList<Template> tmp = TemplateHelper.service.getTemplate();
//
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//
//		model.addObject("tmp", tmp);
//		model.addObject("customer", customer);
//		model.addObject("install", install);
//		model.addObject("isPopup", isPopup);
//		if (isPopup) {
//			model.setViewName("popup:/document/createRequestDocument");
//		} else {
//			model.setViewName("default:/document/createRequestDocument");
//		}
//		return model;
//	}
//
//	@Description("파일 조회 페이지")
//	@RequestMapping(value = "/document/listContents")
//	public ModelAndView listContents() throws Exception {
//		ModelAndView model = new ModelAndView();
//		// default 승인됨
//		StateKeys[] states = StateKeys.values();
//		model.addObject("states", states);
//		model.setViewName("default:/document/listContents");
//		return model;
//	}
//
//	@Description("파일 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/document/listContentsAction")
//	public Map<String, Object> listContentsAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.manager.findContents(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("문서 조회 페이지")
//	@RequestMapping(value = "/document/listDocument")
//	public ModelAndView listDocument() throws Exception {
//		ModelAndView model = new ModelAndView();
//		// default 승인됨
//		StateKeys[] states = StateKeys.values();
//		model.addObject("states", states);
////		model.setViewName("default:/document/listDocument");
//		model.setViewName("/jsp/document/listDocument.jsp");
//		return model;
//	}
//
//	@Description("제작사양서 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/document/listDocumentSpecAction")
//	public Map<String, Object> listDocumentSpecAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.manager.findSpec(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("구 제작사양서 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/document/listDocumentOldSpecAction")
//	public Map<String, Object> listDocumentOldSpecAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.manager.findOldSpec(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("문서 목록 가져오기")
//	@ResponseBody
//	@RequestMapping(value = "/document/listDocumentAction")
//	public Map<String, Object> listDocumentAction(@RequestBody Map<String, Object> params) throws Exception {
//		Map<String, Object> result = new HashMap<String, Object>();
//		try {
//			logger.info("Call DocumentController listDocumentAction Method !!");
//			result = DocumentHelper.manager.find(params);
//			result.put("result", SUCCESS);
//		} catch (Exception e) {
//			e.printStackTrace();
//			result.put("result", FAIL);
//			result.put("msg", e.toString());
//		}
//		return result;
//	}
//
//	@Description("문서 등록 페이지")
//	@RequestMapping(value = "/document/createDocument")
//	public ModelAndView createDocument() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/document/createDocument");
//		return model;
//	}
//
//	@Description("문서 등록")
//	@RequestMapping(value = "/document/createDocumentAction")
//	@ResponseBody
//	public Map<String, Object> createDocumentAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.service.createDocumentAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("문서 수정 페이지")
//	@RequestMapping(value = "/document/modifyDocument")
//	public ModelAndView modifyDocument(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		WTDocument document = null;
//		DocumentViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			document = (WTDocument) rf.getReference(oid).getObject();
//			data = new DocumentViewData(document);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/document/modifyDocument");
//		} else {
//			model.setViewName("default:/document/modifyDocument");
//		}
//		return model;
//	}
//
//	@Description("산출물 수정 페이지")
//	@RequestMapping(value = "/document/modifyOutput")
//	public ModelAndView modifyOutput(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		ArrayList<ProjectOutputLink> projectList = new ArrayList<ProjectOutputLink>();
//		WTDocument document = null;
//		OutputViewData data = null;
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			document = (WTDocument) rf.getReference(oid).getObject();
//			data = new OutputViewData(document);
//			projectList = DocumentHelper.manager.getProjectOutputLink(document);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		model.addObject("projectList", projectList);
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/document/modifyOutput");
//		} else {
//			model.setViewName("default:/document/modifyOutput");
//		}
//		return model;
//	}
//
//	@Description("산출물 수정")
//	@RequestMapping(value = "/document/modifyOutputAction")
//	@ResponseBody
//	public Map<String, Object> modifyOutputAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.service.modifyOutputAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("문서 수정")
//	@RequestMapping(value = "/document/modifyDocumentAction")
//	@ResponseBody
//	public Map<String, Object> modifyDocumentAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.service.modifyDocumentAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("문서 삭제")
//	@RequestMapping(value = "/document/deleteDocumentAction")
//	@ResponseBody
//	public Map<String, Object> deleteDocumentAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.service.deleteDocumentAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("문서 결재 페이지")
//	@RequestMapping(value = "/document/approvalDocument")
//	public ModelAndView approvalDocument() throws Exception {
//		ModelAndView model = new ModelAndView();
//		model.setViewName("default:/document/approvalDocument");
//		return model;
//	}
//
//	@Description("문서 추가 페이지")
//	@RequestMapping(value = "/document/addDocument")
//	public ModelAndView addDocument() throws Exception {
//		ModelAndView model = new ModelAndView();
//		StateKeys[] states = StateKeys.values();
//		model.addObject("states", states);
//		model.setViewName("popup:/document/addDocument");
//		return model;
//	}
//
//	@Description("문서 정보 페이지")
//	@RequestMapping(value = "/document/viewDocument")
//	public ModelAndView viewDocument(@RequestParam Map<String, Object> param) throws Exception {
//		ModelAndView model = new ModelAndView();
//		String oid = (String) param.get("oid");
//		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
//		WTDocument document = null;
//		DocumentViewData data = null;
//		ArrayList<ProjectOutputLink> projectList = new ArrayList<ProjectOutputLink>();
//		ReferenceFactory rf = new ReferenceFactory();
//		try {
//			if (rf.getReference(oid).getObject() instanceof Contents) {
//				Contents content = (Contents) rf.getReference(oid).getObject();
//				ContentsPersistablesLink link = DocumentHelper.manager.getWTDOcument(content);
//				document = (WTDocument) link.getPersistables();
//			} else {
//				document = (WTDocument) rf.getReference(oid).getObject();
//			}
//			data = new DocumentViewData(document);
//			projectList = DocumentHelper.manager.getProjectOutputLink(document);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		model.addObject("projectList", projectList);
//		model.addObject("data", data);
//		if (isPopup) {
//			model.setViewName("popup:/document/viewDocument");
//		} else {
//			model.setViewName("default:/document/viewDocument");
//		}
//		return model;
//	}
//
//	@Description("문서 추가")
//	@RequestMapping(value = "/document/addDocumentAction")
//	@ResponseBody
//	public Map<String, Object> addDocumentAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.service.addDocumentAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("문서 결재")
//	@RequestMapping(value = "/document/approvalDocumentAction")
//	@ResponseBody
//	public Map<String, Object> approvalDocumentAction(@RequestBody Map<String, Object> param) throws Exception {
//		Map<String, Object> map = null;
//		try {
//			map = DocumentHelper.service.approvalDocumentAction(param);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return map;
//	}
//
//	@Description("산출물개정")
//	@RequestMapping(value = "/common/reviseOutput")
//	@ResponseBody
//	public Map<String, Object> reviseOutput(@RequestParam Map<String, Object> param) {
//		Map<String, Object> map = new HashMap<String, Object>();
//		ReferenceFactory rf = new ReferenceFactory();
//		Versioned versioned = null;
//		String oid = (String) param.get("oid");
////		String location = (String) param.get("location ");
//		// boolean task = (boolean)param.get("task");
//		try {
//
//			versioned = (Versioned) rf.getReference(oid).getObject();
//			versioned = (Versioned) VersionControlHelper.service.newVersion(versioned);
//			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
//			VersionControlHelper.setNote(versioned, "사용자 " + user.getFullName() + "에 의해서 개정됨");
//			versioned = (Versioned) PersistenceHelper.manager.save(versioned);
//
//			DocumentHelper.service.reviseOutput(param, versioned);
//
//			map.put("msg", "개정 되었습니다.");
//			map.put("url", "/Windchill/plm/document/listOutput");
//			map.put("result", SUCCESS);
//		} catch (Exception e) {
//			map.put("msg", "개정에 실패하였습니다.\n시스템 관리자에게 문의하세요");
//			map.put("result", FAIL);
//			e.printStackTrace();
//		}
//		return map;
//	}

	// end
//}
