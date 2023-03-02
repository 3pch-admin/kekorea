package e3ps.common.content.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.controller.BaseController;

@Controller
@RequestMapping(value = "/content/**")
public class CommonContentController extends BaseController {

	@Description(value = "일반 첨부파일 업로드")
	@GetMapping(value = "/upload")
	@ResponseBody
	public Map<String, Object> upload(HttpServletRequest request) throws Exception {
		return CommonContentHelper.service.upload(request);
	}
}
