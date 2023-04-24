package e3ps.common.controller;

import java.util.ArrayList;
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

import e3ps.common.util.ColumnParseUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.StringUtils;
import wt.fc.PagingQueryResult;
import wt.fc.PagingSessionHelper;

@Controller
@RequestMapping(value = "/aui/**")
public class AUIGridController extends BaseController {

	@Description(value = "그리드 리스트 상에서 레이지 로딩시 호출 하는 함수")
	@PostMapping(value = "/appendData")
	@ResponseBody
	public Map<String, Object> appendData(@RequestBody Map<String, Object> params) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		long sessionid = StringUtils.parseLong((String) params.get("sessionid"));
		int start = (int) params.get("start");
		int end = (int) params.get("end");
		try {
			PagingQueryResult qr = PagingSessionHelper.fetchPagingSession(start, end, sessionid);
			ArrayList list = ColumnParseUtils.parse(qr);
			result.put("list", list);
			result.put("result", SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			result.put("result", FAIL);
			result.put("msg", e.toString());
		}
		return result;
	}

	@Description(value = "그리드 리스트서 주 첨부파일 추가 페이지")
	@GetMapping(value = "/primary")
	public ModelAndView primary(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/common/aui/aui-primary");
		return model;
	}

	@Description(value = "그리드 리스트서 첨부파일 추가 페이지")
	@GetMapping(value = "/secondary")
	public ModelAndView create(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/common/aui/aui-secondary");
		return model;
	}

	@Description(value = "그리드 썸네일 보기 페이지")
	@GetMapping(value = "/thumbnail")
	public ModelAndView thumbnail(@RequestParam String oid) throws Exception {
		ModelAndView model = new ModelAndView();
		String base64 = ContentUtils.getPreViewBase64(oid);
		model.addObject("base64", base64);
		model.setViewName("popup:/common/aui/aui-thumbnail");
		return model;
	}

	@Description(value = "그리드 리스트서 미리보기 추가 페이지")
	@GetMapping(value = "/preview")
	public ModelAndView preview(@RequestParam(required = false) String oid, @RequestParam String method)
			throws Exception {
		ModelAndView model = new ModelAndView();
		model.addObject("method", method);
		model.addObject("oid", oid);
		model.setViewName("popup:/common/aui/aui-preview");
		return model;
	}
}
