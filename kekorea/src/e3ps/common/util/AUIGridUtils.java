package e3ps.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

import e3ps.admin.commonCode.service.CommonCodeHelper;
import e3ps.admin.spec.service.OptionsHelper;
import e3ps.common.content.service.CommonContentHelper;
import e3ps.project.service.ProjectHelper;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.util.FileUtil;
import wt.util.WTProperties;

public class AUIGridUtils {

	private static String TEMP;
	static {
		try {
			TEMP = WTProperties.getLocalProperties().getProperty("wt.home") + File.separator + "temp" + File.separator
					+ "upload";
			File dir = new File(TEMP);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private AUIGridUtils() {

	}

	public static ArrayList<Map<String, Object>> remoter(Map<String, Object> params) throws Exception {
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		String target = (String) params.get("target");

		// 프로젝트 리모터
		if (target.contains("project")) {
			list = ProjectHelper.manager.remoter(params);
		} else if (target.contains("spec")) {
//			list = SpecHelper.manager.remoter(params);
		} else if (target.contains("code")) {
			list = CommonCodeHelper.manager.remoter(params);
		} else if (target.contains("options")) {
			list = OptionsHelper.manager.remoter(params);
		}

		return list;
	}

	public static String primaryTemplate(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			String icon = CommonContentHelper.manager.getIconPath(ext);
			template += "<a><img src=" + icon + "></a>&nbsp;";
		}
		return template;
	}

	public static String secondaryTemplate(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			String icon = CommonContentHelper.manager.getIconPath(ext);
			template += "<a><img src=" + icon + "></a>&nbsp;";
		}
		return template;
	}

	public static String getThumnailSmall(EPMDocument epm) throws Exception {
		String thumnail_mini = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm),
				ContentRoleType.THUMBNAIL_SMALL);

		if (thumnail_mini == null) {
			thumnail_mini = "/Windchill/jsp/images/productview_publish_24.png";
		}
		return thumnail_mini;
	}

	public static String getThumnailSmall(WTPart part) throws Exception {
		String thumnail_mini = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(part),
				ContentRoleType.THUMBNAIL_SMALL);

		if (thumnail_mini == null) {
			thumnail_mini = "/Windchill/jsp/images/productview_publish_24.png";
		}
		return thumnail_mini;
	}

	public static Map<String, Object> upload(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			int limit = 1024 * 1024 * 1024;
			MultipartRequest multi = new MultipartRequest(request, TEMP, limit, "UTF-8", new DefaultFileRenamePolicy());

			String roleType = multi.getParameter("roleType");
			String origin = multi.getOriginalFileName(roleType);
			String name = multi.getFilesystemName(roleType);

			String ext = FileUtil.getExtension(origin);
			String fullPath = TEMP + File.separator + name;

			map.put("name", origin);
			map.put("fullPath", fullPath);
			map.put("icon", CommonContentHelper.manager.getIconPath(ext));

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return map;
	}

	public static Map<String, Object> preview(HttpServletRequest request) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			int limit = 1024 * 1024 * 1024;
			MultipartRequest multi = new MultipartRequest(request, TEMP, limit, "UTF-8", new DefaultFileRenamePolicy());

			String roleType = multi.getParameter("roleType");
			String origin = multi.getOriginalFileName(roleType);
			String name = multi.getFilesystemName(roleType);

			String ext = FileUtil.getExtension(origin);
			String fullPath = TEMP + File.separator + name;

			map.put("name", origin);
			map.put("fullPath", fullPath);
			map.put("base64", ContentUtils.imageToBase64(new File(fullPath), ext));

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return map;
	}
}
