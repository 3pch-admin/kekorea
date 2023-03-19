package e3ps.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.IconDelegate;
import wt.fc.IconDelegateFactory;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.fc.WTObject;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.session.SessionHelper;
import wt.util.FileUtil;
import wt.util.IconSelector;
import wt.util.WTProperties;

public class ContentUtils {

	public static String TMP_PATH = "";
	static {
		try {
			TMP_PATH = WTProperties.getServerProperties().getProperty("wt.temp");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 객체 생성 방지
	 */
	private ContentUtils() {

	}

	/**
	 * OID로 주 첨부 파일 내용들 가져오는 함수
	 */
	public static String[] getPrimary(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();
		return getPrimary(holder);
	}

	/**
	 * ContentHolder 객체로 주 첨부 파일 내용들 가져오는 함수
	 */
	public static String[] getPrimary(ContentHolder holder) throws Exception {
		String[] primarys = new String[7];
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();

			if (item instanceof ApplicationData) {
				ApplicationData data = (ApplicationData) item;
				primarys[0] = holder.getPersistInfo().getObjectIdentifier().getStringValue();
				primarys[1] = data.getPersistInfo().getObjectIdentifier().getStringValue();
				primarys[2] = data.getFileName();
				primarys[3] = data.getFileSizeKB() + "KB";
				primarys[4] = getFileIcons(primarys[2]);
				primarys[5] = ContentHelper.getDownloadURL(holder, data, false, primarys[2]).toString();
				primarys[6] = "<a href=" + primarys[5] + "><img src=" + primarys[4] + "></a>";
			}
		}
		return primarys;
	}

	/**
	 * 객체 OID로 첨부파일 가져오기
	 */
	public static Vector<String[]> getSecondary(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		ContentHolder holder = (ContentHolder) rf.getReference(oid).getObject();
		return getSecondary(holder);
	}

	/**
	 * ContentHolder 객체에 대한 첨부파일 가져오기
	 */
	public static Vector<String[]> getSecondary(ContentHolder holder) throws Exception {
		Vector<String[]> secondarys = new Vector<String[]>();
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			if (item instanceof ApplicationData) {
				String[] secondary = new String[8];
				ApplicationData data = (ApplicationData) item;
				secondary[0] = holder.getPersistInfo().getObjectIdentifier().getStringValue();
				secondary[1] = data.getPersistInfo().getObjectIdentifier().getStringValue();
				secondary[2] = data.getFileName();
				secondary[3] = data.getFileSizeKB() + "KB";
				secondary[4] = getFileIcons(secondary[2]);
				secondary[5] = ContentHelper.getDownloadURL(holder, data, false, secondary[2]).toString();
				secondary[6] = "<a href=" + secondary[5] + "><img src=" + secondary[4] + "></a>";
				secondarys.add(secondary);
			}
		}
		return secondarys;
	}

	/**
	 * @param name : 파일 명
	 * @return String
	 *         <p>
	 *         파일명을 뒤에서 부터 검색 해서 처음 나오는 . 기준으로 확장자 가져오기
	 *         </p>
	 */
	private static String getFileIcons(String name) {
		String ext = FileUtil.getExtension(name);

		String icon = "/Windchill/jsp/images/fileicon/file_notepad.gif";
		if (ext.equalsIgnoreCase("pdf")) {
			icon = "/Windchill/jsp/images/fileicon/file_pdf.gif";
		} else if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")) {
			icon = "/Windchill/jsp/images/fileicon/file_excel.gif";
		} else if (ext.equalsIgnoreCase("ppt") || ext.equalsIgnoreCase("pptx")) {
			icon = "/Windchill/jsp/images/fileicon/file_ppoint.gif";
		} else if (ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("docs")) {
			icon = "/Windchill/jsp/images/fileicon/file_msword.gif";
		} else if (ext.equalsIgnoreCase("html") || ext.equalsIgnoreCase("htm")) {
			icon = "/Windchill/jsp/images/fileicon/file_html.gif";
		} else if (ext.equalsIgnoreCase("gif")) {
			icon = "/Windchill/jsp/images/fileicon/file_gif.gif";
		} else if (ext.equalsIgnoreCase("png")) {
			icon = "/Windchill/jsp/images/fileicon/file_png.gif";
		} else if (ext.equalsIgnoreCase("bmp")) {
			icon = "/Windchill/jsp/images/fileicon/file_bmp.gif";
		} else if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
			icon = "/Windchill/jsp/images/fileicon/file_jpg.jpg";
		} else if (ext.equalsIgnoreCase("zip") || ext.equalsIgnoreCase("rar") || ext.equalsIgnoreCase("jar")) {
			icon = "/Windchill/jsp/images/fileicon/file_zip.gif";
		} else if (ext.equalsIgnoreCase("tar") || ext.equalsIgnoreCase("gz")) {
			icon = "/Windchill/jsp/images/fileicon/file_zip.gif";
		} else if (ext.equalsIgnoreCase("exe")) {
			icon = "/Windchill/jsp/images/fileicon/file_exe.gif";
		} else if (ext.equalsIgnoreCase("dwg")) {
			icon = "/Windchill/jsp/images/fileicon/file_dwg.gif";
		} else if (ext.equalsIgnoreCase("xml")) {
			icon = "/Windchill/jsp/images/fileicon/file_xml.png";
		}
		return icon;
	}

	public static String getOpenIcon(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		WTObject obj = (WTObject) rf.getReference(oid).getObject();
		return getOpenIcon(obj, "");
	}

	public static String getStandardIcon(String oid) throws Exception {
		ReferenceFactory rf = new ReferenceFactory();
		WTObject obj = (WTObject) rf.getReference(oid).getObject();
		return getStandardIcon(obj, "");
	}

	public static String getStandardIcon(WTObject obj) throws Exception {
		return getStandardIcon(obj, "");
	}

	public static String getOpenIcon(WTObject obj) throws Exception {
		return getOpenIcon(obj, "");
	}

	public static String getStandardIcon(WTObject obj, String tooltip) throws Exception {
		IconDelegateFactory factory = IconDelegateFactory.getInstance();
		IconDelegate delegate = factory.getIconDelegate(obj);
		IconSelector selector = delegate.getStandardIconSelector();
		if (StringUtils.isNull(tooltip)) {
			tooltip = delegate.getToolTip(SessionHelper.getLocale());
		}
		/// Windchill/jsp/images/part.gif
		// icon.append("<img src=\"/Windchill/" + selector.getIconKey() + "\" title=\""
		/// + tooltip + "\">");
		return "/Windchill/" + selector.getIconKey();
	}

	public static String getOpenIcon(WTObject obj, String tooltip) throws Exception {
		// StringBuffer icon = new StringBuffer();
		IconDelegateFactory factory = IconDelegateFactory.getInstance();
		IconDelegate delegate = factory.getIconDelegate(obj);
		IconSelector selector = delegate.getOpenIconSelector();
		if (StringUtils.isNull(tooltip)) {
			tooltip = delegate.getToolTip(SessionHelper.getLocale());
		}
		// icon.append("<img src=\"/Windchill/" + selector.getIconKey() + "\" title=\""
		// + tooltip + "\">");
		return "/Windchill/" + selector.getIconKey();
	}

	public static String[] getRepresentationData(ContentHolder holder) throws Exception {
		String[] representationData = new String[6];
		Representable representable = PublishUtils.findRepresentable(holder);
		Representation representation = PublishUtils.getRepresentation(representable, true, null, false);

		if (representation != null) {
			QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
			while (result.hasMoreElements()) {
				ApplicationData data = (ApplicationData) result.nextElement();
				representationData[0] = data.getPersistInfo().getObjectIdentifier().getStringValue();
				representationData[1] = representation.getPersistInfo().getObjectIdentifier().getStringValue();
				representationData[2] = data.getFileName();
				representationData[3] = data.getFileSizeKB() + " KB";
				// representationData[4] = getFileIcon(representationData[2]);
				representationData[5] = ContentHelper
						.getDownloadURL(representation, data, true, representationData[2], true).toString();
			}
		}
		return representationData;
	}

	public static String[] getPDF(ContentHolder holder) throws Exception {
		String[] pdf = new String[9];

		Representation representation = PublishUtils.getRepresentation(holder);

		if (representation != null) {
			// QueryResult result = ContentHelper.service.getContentsByRole(representation,
			// ContentRoleType.ADDITIONAL_FILES);
			QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
			while (result.hasMoreElements()) {
				ContentItem item = (ContentItem) result.nextElement();
				if (item instanceof ApplicationData) {
					ApplicationData data = (ApplicationData) item;

					String ext = FileUtil.getExtension(data.getFileName());

					if (!ext.equalsIgnoreCase("pdf")) {
						continue;
					}

					// 0 = holder oid
					pdf[0] = representation.getPersistInfo().getObjectIdentifier().getStringValue();
					// 1 = app oid
					pdf[1] = data.getPersistInfo().getObjectIdentifier().getStringValue();
					// 2 = name
					pdf[2] = data.getFileName();
					// 3 = size
					pdf[3] = data.getFileSizeKB() + " KB";
					// 4 = icon
					pdf[4] = getFileIcon(pdf[2]);
					// 5 = down url
					pdf[5] = ContentHelper.getDownloadURL(representation, data, false, pdf[2]).toString();
					// 6 = file version
					pdf[6] = data.getFileVersion();
					// 7 = file category
					pdf[7] = data.getCategory();

					pdf[6] = String.valueOf(data.getFileSize());
				}
			}
		}
		return pdf;
	}

	public static String[] getDWG(ContentHolder holder) throws Exception {
		String[] dwg = new String[8];

		Representation representation = PublishUtils.getRepresentation(holder);

		if (representation != null) {
			// QueryResult result = ContentHelper.service.getContentsByRole(representation,
			// ContentRoleType.SECONDARY);
			QueryResult result = ContentHelper.service.getContentsByRole(representation,
					ContentRoleType.ADDITIONAL_FILES);
			if (result.hasMoreElements()) {
				ContentItem item = (ContentItem) result.nextElement();
				if (item instanceof ApplicationData) {
					ApplicationData data = (ApplicationData) item;

					String ext = FileUtil.getExtension(data.getFileName());
					if (ext.equalsIgnoreCase("dwg")) {
						// 0 = holder oid
						dwg[0] = representation.getPersistInfo().getObjectIdentifier().getStringValue();
						// 1 = app oid
						dwg[1] = data.getPersistInfo().getObjectIdentifier().getStringValue();
						// 2 = name
						dwg[2] = data.getFileName();
						// 3 = size
						dwg[3] = data.getFileSizeKB() + " KB";
						// 4 = icon
						dwg[4] = getFileIcon(dwg[2]);
						// 5 = down url
						dwg[5] = ContentHelper.getDownloadURL(representation, data, false, dwg[2]).toString();
						// 6 = file version
						dwg[6] = data.getFileVersion();
						// 7 = file category
						dwg[7] = data.getCategory();
					}
				}
			}
		}
		return dwg;
	}

	/**
	 * 파일 확장자로 첨부 파일 이미지를 가져 오는 함수
	 */
	public static String getFileIcon(String ext) throws Exception {
		if (ext.equalsIgnoreCase("pdf")) {
			return "/Windchill/extcore/images/fileicon/file_pdf.gif";
		}
		return "/Windchill/extcore/images/fileicon/file_generic.gif";
	}

	public static void updateSecondary(Map<String, Object> param, ContentHolder holder) throws Exception {

		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}

		Iterator<String> it = param.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (key.contains(SECONDARY_KEY)) {
				String value = (String) param.get(key);

				String path = value.split("&")[0];
				String fname = value.split("&")[1];

				File file = new File(path);
				String category = getCategory(file.getName());
				ApplicationData data = ApplicationData.newApplicationData(holder);
				data.setRole(ContentRoleType.SECONDARY);
				data.setCategory(category);
				data.setCreatedBy(SessionHelper.manager.getPrincipalReference());
				data = (ApplicationData) ContentServerHelper.service.updateContent(holder, data, path);

				data.setFileName(fname);
				PersistenceHelper.manager.modify(data);
			}
		}
	}

	public static String getCategory(String name) {
		String ext = FileUtil.getExtension(name);
		String category = "기타파일";
		if (ext.equalsIgnoreCase("pdf")) {
			category = "PDF 파일";
		} else if (ext.equalsIgnoreCase("xls") || ext.equalsIgnoreCase("xlsx")) {
			category = "엑셀 파일";
		} else if (ext.equalsIgnoreCase("ppt") || ext.equalsIgnoreCase("pptx")) {
			category = "파워포인트 파일";
		} else if (ext.equalsIgnoreCase("doc") || ext.equalsIgnoreCase("docs")) {
			category = "워드 파일";
		} else if (ext.equalsIgnoreCase("html") || ext.equalsIgnoreCase("htm")) {
			category = "웹 페이지 파일";
		} else if (ext.equalsIgnoreCase("gif")) {
			category = "이미지 파일 (GIF)";
		} else if (ext.equalsIgnoreCase("png")) {
			category = "이미지 파일 (PNG)";
		} else if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
			category = "이미지 파일 (JPG)";
		} else if (ext.equalsIgnoreCase("bmp")) {
			category = "이미지 파일 (BMP)";
		} else if (ext.equalsIgnoreCase("tif") || ext.equalsIgnoreCase("tiff")) {
			category = "실행 파일 (TIFF)";
		} else if (ext.equalsIgnoreCase("exe")) {
			category = "실행 파일";
		}
		return category;

	}

	public static String imageToBase64(File image, String ext) throws Exception {
		String base64 = Base64.getEncoder().encodeToString(loadFileAsBytesArray(image));
		return "data:image/" + ext + ";base64," + base64;
	}

	private static byte[] loadFileAsBytesArray(File file) throws Exception {
		int length = (int) file.length();
		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
		byte[] bytes = new byte[length];
		reader.read(bytes, 0, length);
		reader.close();
		return bytes;
	}

	/**
	 * 썸네일 저장
	 */
	public static void saveThumbnail(ContentHolder holder, String path) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.THUMBNAIL);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}

		ApplicationData data = ApplicationData.newApplicationData(holder);
		data.setRole(ContentRoleType.THUMBNAIL);
		data = (ApplicationData) ContentServerHelper.service.updateContent(holder, data, path);
	}

	/**
	 * 주 첨부 파일 저장
	 */
	public static void savePrimary(ContentHolder holder, String path) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}

		ApplicationData data = ApplicationData.newApplicationData(holder);
		data.setRole(ContentRoleType.PRIMARY);
		data = (ApplicationData) ContentServerHelper.service.updateContent(holder, data, path);
	}

	/**
	 * 첨부 파일 저장
	 */
	public static void saveSecondary(ContentHolder holder, ArrayList<String> secondaryPaths) throws Exception {
		if (secondaryPaths.isEmpty()) {
			return;
		}

		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}

		for (String path : secondaryPaths) {
			ApplicationData data = ApplicationData.newApplicationData(holder);
			data.setRole(ContentRoleType.SECONDARY);
			data = (ApplicationData) ContentServerHelper.service.updateContent(holder, data, path);
		}
	}

	/**
	 * 썸네일 보기 위해 base64 형태로 반환
	 */
	public static String getPreViewBase64(String oid) throws Exception {
		ContentHolder holder = (ContentHolder) CommonUtils.getObject(oid);
		return getPreViewBase64(holder);
	}

	/**
	 * 썸네일 보기 위해 base64 형태로 반환
	 */
	public static String getPreViewBase64(ContentHolder holder) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.THUMBNAIL);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ApplicationData data = (ApplicationData) item;
			String ext = FileUtil.getExtension(data.getFileName());
			InputStream is = ContentServerHelper.service.findLocalContentStream(data);
			File tempFile = File.createTempFile(String.valueOf(is.hashCode()), ".tmp");
			tempFile.deleteOnExit();
			FileUtils.copyInputStreamToFile(is, tempFile);
			String base64 = Base64.getEncoder().encodeToString(loadFileAsBytesArray(tempFile));
			return "data:image/" + ext + ";base64," + base64;
		}
		return null;
	}

	/**
	 * TEMP 파일 얻어오기
	 */
	public static File getTempFile() throws Exception {
		File directory = new File(TMP_PATH + File.separator + "tempFile");
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return File.createTempFile("TEMP_", "", directory);
	}

	/**
	 * 파일명을 받아 TEMP 파일 만들기
	 */
	public static File getTempFile(String name) throws Exception {
		File directory = new File(TMP_PATH + File.separator + "tempFile");
		if (!directory.exists()) {
			directory.mkdirs();
		}
		return new File(TMP_PATH + File.separator + "tempFile" + File.separator + name);
	}
}