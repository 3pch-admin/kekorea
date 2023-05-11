package e3ps.common.util;

import com.ptc.wvs.server.util.FileHelper;
import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.epm.EPMDocument;
import wt.fc.QueryResult;
import wt.part.WTPart;
import wt.representation.Representation;
import wt.util.FileUtil;

public class AUIGridUtils {

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
			String url = "/Windchill/plm/content/download?oid="
					+ data.getPersistInfo().getObjectIdentifier().getStringValue();
			template += "<a href=" + url + "><img src=" + icon + " style='position: relative; top: 2px;'></a>";
		}
		return template;
	}

	/**
	 * AUIGrid 도면 첨부파일 아이콘으로 표시 하기 위한 함수
	 */
	public static String additionalTemplate(ContentHolder holder) throws Exception {
		String template = "";
		Representation representation = PublishUtils.getRepresentation(holder);
		if (representation != null) {
			QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
			if (result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				String ext = FileUtil.getExtension(data.getFileName());
				String icon = getAUIGridFileIcon(ext);
				String url = "/Windchill/plm/content/download?oid="
						+ data.getPersistInfo().getObjectIdentifier().getStringValue();
				template += "<a href=" + url + "><img src=" + icon
						+ " style='position: relative; top: 2px;'></a>&nbsp;";
			}
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
			String url = "/Windchill/plm/content/download?oid="
					+ data.getPersistInfo().getObjectIdentifier().getStringValue();
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
	 * AUIGrid 에서 사용할 파일 아이콘 가져 오는 함수
	 */
	private static String getAUIGridFileIcon(String ext) {
		String icon = "/Windchill/extcore/images/fileicon/file_generic.gif";
		if (ext.equalsIgnoreCase("pdf")) {
			icon = "/Windchill/extcore/images/fileicon/file_pdf.gif";
		} else if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx") || ext.equalsIgnoreCase("csv")) {
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

	/**
	 * AUIGrid 첨부파일 타입에 의한 템플릿
	 */
	public static String thumbnailTemplate(ContentHolder holder) throws Exception {
		String template = "";
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.THUMBNAIL);
		if (result.hasMoreElements()) {
			ApplicationData data = (ApplicationData) result.nextElement();
			String ext = FileUtil.getExtension(data.getFileName());
			String icon = getAUIGridFileIcon(ext);
			String url = "/Windchill/plm/content/download?oid="
					+ data.getPersistInfo().getObjectIdentifier().getStringValue();
			template += "<a href=" + url + "><img src=" + icon + " style='position: relative; top: 2px;'></a>";
		}
		return template;
	}
}
