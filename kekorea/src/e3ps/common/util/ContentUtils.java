package e3ps.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Vector;

import org.apache.commons.io.FileUtils;

import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.representation.Representable;
import wt.representation.Representation;
import wt.util.FileUtil;
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
		String[] primarys = new String[8];
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();

			if (item instanceof ApplicationData) {
				ApplicationData data = (ApplicationData) item;
				primarys[0] = holder.getPersistInfo().getObjectIdentifier().getStringValue();
				primarys[1] = data.getPersistInfo().getObjectIdentifier().getStringValue();
				primarys[2] = data.getFileName();
				primarys[3] = data.getFileSizeKB() + "KB";
				primarys[4] = getFileIcon(primarys[2]);
				primarys[5] = ContentHelper.getDownloadURL(holder, data, false, primarys[2]).toString();
				primarys[6] = "<a href=" + primarys[5] + "><img src=" + primarys[4] + "></a>";
				primarys[7] = String.valueOf(data.getFileSize());
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
				secondary[4] = getFileIcon(secondary[2]);
				secondary[5] = ContentHelper.getDownloadURL(holder, data, false, secondary[2]).toString();
				secondary[6] = "<a href=" + secondary[5] + "><img src=" + secondary[4] + "></a>";
				secondarys.add(secondary);
			}
		}
		return secondarys;
	}

	/**
	 * 파일확장자로 파일 아이콘 경로 리턴
	 */
	private static String getFileIcon(String name) {
		String ext = FileUtil.getExtension(name);

		String icon = "/Windchill/extcore/images/fileicon/file_notepad.gif";
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
	 * Base64 형태로 IMG 소스 가져오기
	 */
	public static String imageToBase64(File image, String ext) throws Exception {
		String base64 = Base64.getEncoder().encodeToString(loadFileAsBytesArray(image));
		return "data:image/" + ext + ";base64," + base64;
	}

	/**
	 * File -> byte[] 로 변경
	 */
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