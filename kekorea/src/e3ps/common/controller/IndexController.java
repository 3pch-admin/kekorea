package e3ps.common.controller;

import java.sql.Timestamp;
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

import e3ps.common.util.CommonUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.FolderUtils;
import e3ps.common.util.ThumnailUtils;
import e3ps.org.People;
import e3ps.org.PeopleWTUserLink;
import e3ps.org.dto.UserDTO;
import e3ps.project.service.ProjectHelper;
import e3ps.system.service.ErrorLogHelper;
import e3ps.workspace.notice.service.NoticeHelper;
import e3ps.workspace.service.WorkspaceHelper;
import net.sf.json.JSONArray;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
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
		JSONArray pList = ProjectHelper.manager.firstPageData(sessionUser);
		

		WTUser user = CommonUtils.sessionUser();
		People people = null;
		QueryResult result = PersistenceHelper.manager.navigate(user, "people", PeopleWTUserLink.class);
		Timestamp last = null;
		if (result.hasMoreElements()) {
			people = (People) result.nextElement();
			last = people.getLast();
		}

		Timestamp today = DateUtils.today();
		int gap = 0;
		if (last != null) {
			gap = DateUtils.getDuration(last, today);
		}
		boolean setting = people.getSetting();
		model.addObject("setting", setting);
		model.addObject("gap", gap);
		model.addObject("isGap", gap >= people.getGap() ? true : false);
		model.addObject("pList", pList);
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
		Map<String, Integer> count = WorkspaceHelper.manager.count();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("count", count);
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
			result.put("msg", e.toString());
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

	@Description(value = "뷰어 생성")
	@GetMapping(value = "/doPublish")
	@ResponseBody
	public Map<String, Object> doPublish(@RequestParam String oid) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			ThumnailUtils.doPublisher(oid);
			result.put("msg", "워커에 제출 되었습니다.");
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
			ErrorLogHelper.service.create(e.toString(), "/doPublish", "뷰어 생성");
		}
		return result;
	}
}
