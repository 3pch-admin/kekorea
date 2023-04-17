package e3ps.common.content.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Description;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import e3ps.common.content.service.CommonContentHelper;
import e3ps.common.controller.BaseController;
import e3ps.common.util.CommonUtils;
import net.sf.json.JSONObject;
import wt.content.ApplicationData;
import wt.content.ContentServerHelper;

@Controller
@RequestMapping(value = "/content/**")
public class CommonContentController extends BaseController {

	@Description(value = "파일 다운로드")
	@GetMapping(value = "/download")
	public ResponseEntity<byte[]> download(@RequestParam String oid) throws Exception {
		ApplicationData data = (ApplicationData) CommonUtils.getObject(oid);
		InputStream is = ContentServerHelper.service.findLocalContentStream(data);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = is.read(buffer)) != -1) {
			byteArrayOutputStream.write(buffer, 0, length);
		}

		byte[] bytes = byteArrayOutputStream.toByteArray();
		String name = URLEncoder.encode(data.getFileName(), "UTF-8").replaceAll("\\+", "%20");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headers.setContentLength(bytes.length);
		headers.setContentDispositionFormData("attachment", name);

		return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
	}

	@Description(value = "첨부 파일 리스트 가져오기")
	@ResponseBody
	@PostMapping(value = "/list")
	public JSONObject list(HttpServletRequest param) throws Exception {
		String oid = (String) param.getParameter("oid");
		String roleType = (String) param.getParameter("roleType");
		JSONObject list = CommonContentHelper.manager.list(oid, roleType);
		return list;
	}

	@Description(value = "첨부 파일 업로드")
	@ResponseBody
	@PostMapping(value = "/upload")
	public JSONObject upload(HttpServletRequest request) throws Exception {
		return CommonContentHelper.manager.upload(request);
	}

	@Description(value = "첨부 파일 삭제(화면에서의 제거)")
	@ResponseBody
	@PostMapping(value = "/delete")
	public JSONObject delete(HttpServletRequest param) throws Exception {
		JSONObject result = new JSONObject();
		result.put("status", 0);
		result.put("result", "ok");
		return result;
	}

}
