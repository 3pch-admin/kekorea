package e3ps.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import e3ps.common.content.service.ContentHelper;

@Controller
public class ContentController {

	@Description("전체 다운로드")
	@RequestMapping(value = "/content/downContentAll")
	@ResponseBody
	public Map<String, Object> downContentAll(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ContentHelper.service.downContentAll(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("주 첨부 파일만")
	@RequestMapping(value = "/content/downPrimary")
	@ResponseBody
	public Map<String, Object> downPrimary(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ContentHelper.service.downPrimary(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("첨부파일 모두")
	@RequestMapping(value = "/content/downSecondary")
	@ResponseBody
	public Map<String, Object> downSecondary(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ContentHelper.service.downSecondary(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("다운로드")
	@RequestMapping(value = "/content/contentsDown")
	@ResponseBody
	public Map<String, Object> contentsDown(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ContentHelper.service.contentsDown(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("멀티다운로드")
	@RequestMapping(value = "/content/contentsMultiDown")
	@ResponseBody
	public Map<String, Object> contentsMultiDown(@RequestBody Map<String, Object> param) throws Exception {
		Map<String, Object> map = null;
		try {
			map = ContentHelper.service.contentsMultiDown(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	// end

	@Description("첨부파일 업로드")
	@RequestMapping(value = "/content/uploadContent")
	@ResponseBody
	public Map<String, Object> uploadContent(HttpServletRequest request) {
		Map<String, Object> map = null;
		try {
			map = ContentHelper.service.uploadContent(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}

	@Description("첨부파일 삭제")
	@RequestMapping(value = "/content/deleteContent")
	@ResponseBody
	public Map<String, Object> deleteContent(@RequestParam Map<String, Object> param) {
		Map<String, Object> map = null;
		try {
			map = ContentHelper.service.deleteContent(param);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
}
