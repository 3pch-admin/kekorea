package e3ps.common.content.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import e3ps.common.content.service.CommonContentHelper;
import e3ps.controller.BaseController;

@Controller
@RequestMapping(value = "/content/**")
public class CommonContentController extends BaseController {

	@Description("일반 첨부파일 업로드")
	@RequestMapping(value = "/upload")
	@ResponseBody
	public Map<String, Object> upload(HttpServletRequest request) throws Exception {
		return CommonContentHelper.service.upload(request);
	}

	
	@Description("그리드 첨부파일 업로드")
	@RequestMapping(value = "/aui/auiUpload")
	@ResponseBody
	public Map<String, Object> auiUpload(HttpServletRequest request) throws Exception {
		return CommonContentHelper.service.auiUpload(request);
	}

	@Description("미리보기 업로드")
	@RequestMapping(value = "/aui/auiPreview")
	@ResponseBody
	public Map<String, Object> auiPreview(HttpServletRequest request) throws Exception {
		return CommonContentHelper.service.auiPreview(request);
	}
}
