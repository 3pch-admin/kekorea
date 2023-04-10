package e3ps.common.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

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

	/**
	 * AUIGrid 주 첨부파일 아이콘으로 표시 하기 위한 함수
	 */
	public static String primaryTemplate(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			String icon = getAUIGridFileIcon(ext);
			String url = ContentHelper.getDownloadURL(holder, data, false, data.getFileName()).toString();
			template += "<a href=" + url + "><img src=" + icon + " style='position: relative; top: 2px;'></a>";
		}
		return template;
	}

	/**
	 * AUIGrid 첨부파일 아이콘으로 표시 하기 위한 함수
	 */
	public static String secondaryTemplate(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			String icon = getAUIGridFileIcon(ext);
			String url = ContentHelper.getDownloadURL(holder, data, false, data.getFileName()).toString();
			template += "<a href=" + url + "><img src=" + icon + " style='position: relative; top: 2px;'></a>&nbsp;";
		}
		return template;
	}

	/**
	 * 뷰어블 파일을 AUIGrid 상 표기 위한 함수
	 */
	public static String getThumnailSmall(EPMDocument epm) throws Exception {
		String thumnail_mini = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(epm),
				ContentRoleType.THUMBNAIL_SMALL);
		if (thumnail_mini == null) {
			thumnail_mini = "/Windchill/extcore/images/productview_publish_24.png";
		}
		return thumnail_mini;
	}

	/**
	 * 뷰어블 파일을 AUIGrid 상 표기 위한 함수
	 */
	public static String getThumnailSmall(WTPart part) throws Exception {
		String thumnail_mini = FileHelper.getViewContentURLForType(PublishUtils.findRepresentable(part),
				ContentRoleType.THUMBNAIL_SMALL);
		if (thumnail_mini == null) {
			thumnail_mini = "/Windchill/extcore/images/productview_publish_24.png";
		}
		return thumnail_mini;
	}

	/**
	 * 그리드 상에서 첨부 파일 올리는 경우 호출하는 함수
	 */
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
			map.put("icon", getAUIGridFileIcon(ext));
			map.put("base64", ContentUtils.imageToBase64(new File(fullPath), ext));

		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		return map;
	}

	/**
	 * AUIGrid 에서 사용할 파일 아이콘 가져 오는 함수
	 */
	private static String getAUIGridFileIcon(String ext) {
		String icon = "";
		if (ext.equalsIgnoreCase("pdf")) {
			icon = "/Windchill/extcore/images/fileicon/file_pdf.gif";
		} else if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")) {
			icon = "/Windchill/extcore/images/fileicon/file_excel.gif";
		} else if (ext.equalsIgnoreCase("ppt") || ext.equalsIgnoreCase("pptx")) {
			icon = "/Windchill/extcore/images/fileicon/file_ppoint.gif";
		} else if (ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("docs")) {
			icon = "/Windchill/extcore/images/fileicon/file_msword.gif";
		} else if (ext.equalsIgnoreCase("html") || ext.equalsIgnoreCase("htm")) {
			icon = "/Windchill/extcore/images/fileicon/file_html.gif";
		} else if (ext.equalsIgnoreCase("gif")) {
			icon = "/Windchill/extcore/images/fileicon/file_gif.gif";
		} else if (ext.equalsIgnoreCase("png")) {
			icon = "/Windchill/extcore/images/fileicon/file_png.gif";
		} else if (ext.equalsIgnoreCase("bmp")) {
			icon = "/Windchill/extcore/images/fileicon/file_bmp.gif";
		} else if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
			icon = "/Windchill/extcore/images/fileicon/file_jpg.jpg";
		} else if (ext.equalsIgnoreCase("zip") || ext.equalsIgnoreCase("rar") || ext.equalsIgnoreCase("jar")) {
			icon = "/Windchill/extcore/images/fileicon/file_zip.gif";
		} else if (ext.equalsIgnoreCase("tar") || ext.equalsIgnoreCase("gz")) {
			icon = "/Windchill/extcore/images/fileicon/file_zip.gif";
		} else if (ext.equalsIgnoreCase("exe")) {
			icon = "/Windchill/extcore/images/fileicon/file_exe.gif";
		} else if (ext.equalsIgnoreCase("dwg")) {
			icon = "/Windchill/extcore/images/fileicon/file_dwg.gif";
		} else if (ext.equalsIgnoreCase("xml")) {
			icon = "/Windchill/extcore/images/fileicon/file_xml.png";
		}
		return icon;
	}
}
