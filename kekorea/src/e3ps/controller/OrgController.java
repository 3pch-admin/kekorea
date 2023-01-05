package e3ps.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.org.People;
import e3ps.org.beans.UserViewData;
import e3ps.org.service.OrgHelper;
import net.sf.json.JSONArray;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.session.SessionHelper;
import wt.util.WTException;

@Controller
public class OrgController extends BaseController {

	@Description("유저정보 페이지")
	@RequestMapping(value = "/org/viewUser")
	public ModelAndView viewUser(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		People user = null;
		UserViewData data = null;
		ReferenceFactory rf = new ReferenceFactory();
		try {
			user = (People) rf.getReference(oid).getObject();
			data = new UserViewData(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("data", data);
		if (isPopup) {
			model.setViewName("popup:/org/viewUser");
		} else {
			model.setViewName("default:/org/viewUser");
		}
		return model;
	}

	@Description("퇴사처리")
	@RequestMapping(value = "/org/setResignAction")
	@ResponseBody
	public Map<String, Object> setResignAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.setResignAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("조직도 페이지")
	@RequestMapping(value = "/org/viewOrg")
	public ModelAndView viewOrg() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("default:/org/viewOrg");
		return model;
	}

	@Description("조직도 목록 가져오기")
	@RequestMapping(value = "/org/viewOrgAction")
	@ResponseBody
	public Map<String, Object> viewOrgAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = OrgHelper.manager.find(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("비밀번호 변경 페이지")
	@RequestMapping(value = "/org/changePassword")
	public ModelAndView changePassword() throws Exception {
		ModelAndView model = new ModelAndView();
		WTUser user = null;
		try {
			user = (WTUser) SessionHelper.manager.getPrincipal();
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("user", user);
		model.setViewName("default:/org/changePassword");
		return model;
	}

	@Description("비밀번호 변경")
	@RequestMapping(value = "/org/changePasswordAction")
	@ResponseBody
	public Map<String, Object> changePasswordAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.changePasswordAction(param);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("로그아웃")
	@RequestMapping(value = "/org/logout")
	public ModelAndView logout(@RequestParam Map<String, Object> param) throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("/login/logout.jsp");
		return model;
	}

	@Description("검색 사용자 추가 페이지")
	@RequestMapping(value = "/org/addUser")
	public ModelAndView addUser() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/approval/addUser");
		return model;
	}

	@Description("사용자 가져오기")
	@ResponseBody
	@RequestMapping(value = "/org/listUserAction")
	public Map<String, Object> listUserAction(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = OrgHelper.manager.find(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("검색 사용자 추가")
	@RequestMapping(value = "/org/addUserAction")
	@ResponseBody
	public Map<String, Object> addUserAction(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.addUserAction(param);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("결재선 지정 페이지")
	@RequestMapping(value = "/org/addLine")
	public ModelAndView addLine() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/org/addLine");
		return model;
	}

	@Description("비밀번호 초기화")
	@RequestMapping(value = "/org/initPasswordAction")
	@ResponseBody
	public Map<String, Object> initPasswordAction(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.initPasswordAction(param);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return map;
	}

	// end

	@Description("사용자 정보 수정")
	@RequestMapping(value = "/org/modifyUserAction")
	@ResponseBody
	public Map<String, Object> modifyUserAction(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.modifyUserAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("유저수정 페이지")
	@RequestMapping(value = "/org/modifyUser")
	public ModelAndView modifyUser(@RequestParam Map<String, Object> param) {
		ModelAndView model = new ModelAndView();
		String oid = (String) param.get("oid");
		boolean isPopup = Boolean.parseBoolean((String) param.get("popup"));
		People user = null;
		UserViewData data = null;
		ReferenceFactory rf = new ReferenceFactory();
		String[] ranks = OrgHelper.ranks;
		String[] dutys = OrgHelper.dutys;
		try {
			user = (People) rf.getReference(oid).getObject();
			data = new UserViewData(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		model.addObject("oid", oid);
		model.addObject("data", data);
		model.addObject("ranks", ranks);
		model.addObject("dutys", dutys);
		if (isPopup) {
			model.setViewName("popup:/approval/modifyUser");
		} else {
			model.setViewName("default:/approval/modifyUser");
		}
		return model;
	}

	@Description("부서 별 유저")
	@RequestMapping(value = "/org/getUserForDept")
	@ResponseBody
	public Map<String, Object> getUserForDept(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.manager.getUserForDept(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("부서 가져오기")
	@RequestMapping(value = "/org/getDeptTree")
	@ResponseBody
	public JSONArray getDeptTree(@RequestParam Map<String, Object> param) throws Exception {
		JSONArray node = null;
		try {
			node = OrgHelper.manager.getDeptTree(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return node;
	}

	@Description("개인결재선 삭제")
	@RequestMapping(value = "/org/deleteUserLineAction")
	@ResponseBody
	public Map<String, Object> deleteUserLineAction(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.deleteUserLineAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("개인결재선 목록")
	@RequestMapping(value = "/org/getUserLine")
	@ResponseBody
	public Map<String, Object> getUserLine(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.manager.getUserLine(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("개인결재선 등록")
	@RequestMapping(value = "/org/saveUserLineAction")
	@ResponseBody
	public Map<String, Object> saveUserLineAction(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.service.saveUserLineAction(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("유저 목록")
	@RequestMapping(value = "/org/getUserList")
	@ResponseBody
	public Map<String, Object> getUserList(@RequestBody Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			map = OrgHelper.manager.getUserList(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
