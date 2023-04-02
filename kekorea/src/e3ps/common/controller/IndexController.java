package e3ps.common.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.FolderUtils;
import e3ps.doc.meeting.service.MeetingHelper;
import e3ps.korea.cip.service.CipHelper;
import e3ps.org.dto.UserDTO;
import e3ps.org.service.OrgHelper;
import e3ps.workspace.notice.service.NoticeHelper;
import e3ps.workspace.service.WorkspaceHelper;
import net.sf.json.JSONArray;
import wt.org.WTUser;
import wt.session.SessionHelper;

@Controller
public class IndexController extends BaseController {

	@Description(value = "메인 페이지")
	@GetMapping(value = "/index")
	public ModelAndView index() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("content:/index");
		return model;
	}

	@Description(value = "메인 첫 화면 페이지")
	@GetMapping(value = "/firstPage")
	public ModelAndView firstPage() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		UserDTO data = new UserDTO(sessionUser);
		boolean isAdmin = CommonUtils.isAdmin();
		JSONArray nList = NoticeHelper.manager.firstPageData();
		JSONArray aList = WorkspaceHelper.manager.firstPageData(sessionUser);
		model.addObject("aList", aList);
		model.addObject("nList", nList);
		model.addObject("isAdmin", isAdmin);
		model.addObject("data", data);
		model.setViewName("/extcore/layout/firstPage.jsp");
		return model;
	}

	@Description(value = "헤더 페이지")
	@GetMapping(value = "/header")
	public ModelAndView header() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
		UserDTO data = new UserDTO(sessionUser);
		boolean isAdmin = CommonUtils.isAdmin();
		ArrayList<CommonCode> maks = OrgHelper.manager.getUserMaks(sessionUser);
		model.addObject("maks", maks);
		model.addObject("isAdmin", isAdmin);
		model.addObject("data", data);
		model.setViewName("/extcore/layout/header.jsp");
		return model;
	}

	@Description(value = "푸터 페이지")
	@GetMapping(value = "/footer")
	public ModelAndView footer() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/extcore/layout/footer.jsp");
		return model;
	}

	@Description(value = "폴더 트리 구조 가져오기")
	@PostMapping(value = "/loadFolderTree")
	@ResponseBody
	public Map<String, Object> loadFolderTree(@RequestBody Map<String, String> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			JSONArray list = FolderUtils.loadFolderTree(params);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}

	@Description(value = "폴더 선택 페이지")
	@GetMapping(value = "/folder")
	public ModelAndView folder(@RequestParam String location, @RequestParam String container,
			@RequestParam String method, @RequestParam String multi) throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("location", location);
		model.addObject("container", container);
		model.addObject("method", method);
		model.addObject("multi", Boolean.parseBoolean(multi));
		model.setViewName("popup:/common/folder/folder-popup");
		return model;
	}

}
