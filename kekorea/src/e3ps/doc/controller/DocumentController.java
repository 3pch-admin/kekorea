package e3ps.doc.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.doc.dto.DocumentDTO;
import e3ps.doc.service.DocumentHelper;
import e3ps.system.service.ErrorLogHelper;
import net.sf.json.JSONArray;
import wt.clients.folder.FolderTaskLogic;
import wt.doc.WTDocument;
import wt.folder.Folder;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
@RequestMapping(value = "/doc/**")
public class DocumentController extends BaseController {

	@Description(value = "문서 조회 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("/extcore/jsp/document/document-list.jsp");
		return model;
	}

	@Description(value = "문서 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = DocumentHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/doc/list", "문서 조회 함수");
		}
		return result;
	}

	@Description(value = "문서 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/document/document-create");
		return model;
	}

	@Description(value = "문서 등록 함수")
	@ResponseBody
	@PostMapping(value = "/create")
	public Map<String, Object> create(@RequestBody DocumentDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.create(dto);
			result.put("msg", SAVE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/doc/create", "문서 등록 함수");
		}
		return result;
	}

	@Description(value = "문서 결재 페이지")
	@GetMapping(value = "/register")
	public ModelAndView register() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/jsp/document/document-register.jsp");
		return model;
	}

	@Description(value = "문서 팝업 페이지")
	@GetMapping(value = "/popup")
	public ModelAndView popup(@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.addObject("method", method);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("popup:/document/document-popup");
		return model;
	}

	@Description(value = "문서 결재 함수")
	@PostMapping(value = "/register")
	@ResponseBody
	public Map<String, Object> register(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.register(params);
			result.put("result", SUCCESS);
			result.put("msg", REGISTER_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("msg", e.toString());
			result.put("result", FAIL);
			ErrorLogHelper.service.create(e.toString(), "/doc/register", "문서 결재 함수");
		}
		return result;
	}

	@Description(value = "문서 번호 세팅 함수")
	@PostMapping(value = "/setNumber")
	@ResponseBody
	public Map<String, Object> setNumber(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			String number = DocumentHelper.manager.setNumber(params);
			result.put("result", SUCCESS);
			result.put("number", number);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/doc/setNumber", "문서 번호 세팅 함수");
		}
		return result;
	}

	@Description(value = "문서 정보 페이지")
	@GetMapping(value = "/view")
	public ModelAndView view(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		WTDocument document = (WTDocument) CommonUtils.getObject(oid);
		DocumentDTO dto = new DocumentDTO(document);
		boolean isAdmin = CommonUtils.isAdmin();
		JSONArray versionHistory = DocumentHelper.manager.versionHistory(document);
		model.addObject("dto", dto);
		model.addObject("versionHistory", versionHistory);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/document/document-view");
		return model;
	}

	@Description(value = "제작사양서 선택페이지.")
	@GetMapping(value = "/only")
	public ModelAndView only(@RequestParam String method, @RequestParam String multi, @RequestParam String isNew)
			throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();

		Folder folder = null;
		if (Boolean.parseBoolean(isNew)) {
			folder = FolderTaskLogic.getFolder(DocumentHelper.SPEC_NEW_ROOT, CommonUtils.getPDMLinkProductContainer());
			model.addObject("loc", DocumentHelper.SPEC_NEW_ROOT);
		} else {
			folder = FolderTaskLogic.getFolder(DocumentHelper.SPEC_OLD_ROOT, CommonUtils.getPDMLinkProductContainer());
			model.addObject("loc", DocumentHelper.SPEC_OLD_ROOT);
		}

		model.addObject("oid", folder.getPersistInfo().getObjectIdentifier().getStringValue());
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.addObject("method", method);
		model.addObject("isAdmin", isAdmin);
		model.addObject("sessionUser", sessionUser);
		model.setViewName("popup:/document/document-only");
		return model;
	}

	@Description(value = "문서 수정 및 개정")
	@GetMapping(value = "/update")
	public ModelAndView update(@RequestParam String oid, @RequestParam String mode) throws Exception {
		ModelAndView model = new ModelAndView();
		WTDocument document = (WTDocument) CommonUtils.getObject(oid);
		DocumentDTO dto = new DocumentDTO(document);
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("dto", dto);
		model.addObject("mode", mode);
		model.addObject("isAdmin", isAdmin);
		model.setViewName("popup:/document/document-update");
		return model;
	}

	@Description(value = "문서 수정 함수")
	@ResponseBody
	@PostMapping(value = "/modify")
	public Map<String, Object> modify(@RequestBody DocumentDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.modify(dto);
			result.put("msg", MODIFY_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/doc/modify", "문서 수정 함수");
		}
		return result;
	}

	@Description(value = "문서 개정 함수")
	@ResponseBody
	@PostMapping(value = "/revise")
	public Map<String, Object> revise(@RequestBody DocumentDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.revise(dto);
			result.put("msg", REVISE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/doc/revise", "문서 수정 함수");
		}
		return result;
	}

	@Description(value = "문서 삭제 함수")
	@ResponseBody
	@GetMapping(value = "/delete")
	public Map<String, Object> delete(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			DocumentHelper.service.delete(oid);
			result.put("msg", DELETE_MSG);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/doc/delete", "문서 수정 함수");
		}
		return result;
	}
}