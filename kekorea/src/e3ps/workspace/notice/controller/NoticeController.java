package e3ps.workspace.notice.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import e3ps.doc.meeting.dto.MeetingDTO;
import e3ps.doc.meeting.service.MeetingHelper;
import e3ps.workspace.notice.dto.NoticeDTO;
import e3ps.workspace.notice.service.NoticeHelper;;

@Controller
@RequestMapping(value = "/notice/**")
public class NoticeController extends BaseController {

	@Description(value = "공지사항 리스트 페이지")
	@GetMapping(value = "/list")
	public ModelAndView list() throws Exception {
		ModelAndView model = new ModelAndView();
		boolean isAdmin = CommonUtils.isAdmin();
		model.addObject("isAdmin", isAdmin);
		model.setViewName("/extcore/jsp/workspace/notice/notice-list.jsp");
		return model;
	}

	@Description(value = "공지사항 조회 함수")
	@ResponseBody
	@PostMapping(value = "/list")
	public Map<String, Object> list(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			result = NoticeHelper.manager.list(params);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
	

	@Description(value = "공지사항 등록 페이지")
	@GetMapping(value = "/create")
	public ModelAndView create() throws Exception {
		ModelAndView model = new ModelAndView();
		model.setViewName("popup:/workspace/notice/notice-create");
		return model;
	}

	@Description(value = "공지사항 등록")
	@PostMapping(value = "/create")
	@ResponseBody
	public Map<String, Object> create(@RequestBody NoticeDTO dto) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			NoticeHelper.service.create(dto);
			result.put("result", SUCCESS);
			result.put("msg", SAVE_MSG);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
		}
		return result;
	}
}
