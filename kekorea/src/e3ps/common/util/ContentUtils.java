package e3ps.common.util;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import com.ptc.wvs.server.util.PublishUtils;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
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
import wt.util.WTException;

/**
 * @author user
 */
public class ContentUtils {

	/**
	 * Windchill codebase 경로
	 */
//	private static String CODEBASE;

//	private static final String DEFAULT_ICONS = "/Windchill/jsp/images/xx.gif";

//	static {
//		try {
//			CODEBASE = WTProperties.getLocalProperties().getProperty("wt.server.codebase");
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * 첨부 파일 1번째를 주 첨부 파일로ㄴㄹ
	 */
	private static final String ALL_UPLOAD_KEY = "allContent";

	/**
	 * 주 첨부파일 키
	 */
	private static final String PRIMARY_KEY = "primary";

	/*
	 * 첨부파일 키
	 */
	private static final String SECONDARY_KEY = "secondary";

	/**
	 * 객체 생성 방지
	 */
	private ContentUtils() {

	}

	/**
	 * @param oid : 객체 OID
	 * @return String[]
	 *         <p>
	 *         객체 OID로 찾은 후 주 첨부파일 내용 가져 오기
	 *         </p>
	 */
	public static String[] getPrimary(String oid) {
		ReferenceFactory rf = new ReferenceFactory();
		ContentHolder holder = null;
		try {
			holder = (ContentHolder) rf.getReference(oid).getObject();
			return getPrimary(holder);
		} catch (WTException e) {
			e.printStackTrace();
		}
		return getPrimary(holder);
	}

	/**
	 * @param holder : 주 첨부파일을 가져올 객체
	 * @return String[]
	 *         <p>
	 *         객체의 주 첨부파일 내용을 가져 오기<br>
	 *         0 객체 OID <br>
	 *         1 컨텐츠 OID <br>
	 *         2 파일명 <br>
	 *         3 파일 크기<br>
	 *         4 아이콘<br>
	 *         5 다운로드 링크<br>
	 *         </p>
	 */
	public static String[] getPrimary(ContentHolder holder) {
		// 6 개를 담아야하면 6개 생성
		// 담는건 0 부터 시작
		String[] primarys = new String[7];

		QueryResult result = null;
		try {
			result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		} catch (WTException e) {
			e.printStackTrace();
		}
		// primary 하나
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();

			if (item instanceof ApplicationData) {
				ApplicationData data = (ApplicationData) item;
				// 0 객체 oid
				primarys[0] = holder.getPersistInfo().getObjectIdentifier().getStringValue();
				// 1 content oid
				primarys[1] = data.getPersistInfo().getObjectIdentifier().getStringValue();
				// 2 name
				primarys[2] = data.getFileName();
				if (holder instanceof EPMDocument) {
					primarys[2] = ((EPMDocument) holder).getCADName();
				}
				// 3 file size
				primarys[3] = data.getFileSizeKB() + "KB";
				// 4 icon
				primarys[4] = getFileIcons(primarys[2]);
				// 5 download url
				try {
					primarys[5] = ContentHelper.getDownloadURL(holder, data, false, primarys[2]).toString();
				} catch (WTException e) {
					e.printStackTrace();
				}
				primarys[6] = String.valueOf(data.getFileSize());
			}
		}
		return primarys;
	}

	public static void updateContents(Map<String, Object> param, ContentHolder holder) throws Exception {
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}

		result.reset();
		result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}

		Iterator<String> it = param.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();

			if (key.contains(ALL_UPLOAD_KEY)) {
				String value = (String) param.get(key);
				String path = value.split("&")[0];
				String fname = value.split("&")[1];

				File file = new File(path);
				String category = getCategory(file.getName());
				ApplicationData data = ApplicationData.newApplicationData(holder);
				if (key.equalsIgnoreCase("allContent_0")) {
					data.setRole(ContentRoleType.PRIMARY);
				} else {
					data.setRole(ContentRoleType.SECONDARY);
				}
				data.setCategory(category);
				data.setCreatedBy(SessionHelper.manager.getPrincipalReference());
				data = (ApplicationData) ContentServerHelper.service.updateContent(holder, data, path);

				data.setFileName(fname);
				PersistenceHelper.manager.modify(data);

			}
		}
	}

	/**
	 * @param name : 확장자를 가져올 문자열
	 * @return String
	 *         <p>
	 *         parameter 뒤에서 부터 . 찾은 후 확장자 return
	 *         </p>
	 */
//	private static String getExtension(String name) {
//		for (int i = name.length() - 1; i >= 0; i--) {
//			char pos = name.charAt(i);
//			if (pos == '.') {
//				int start = (i + 1);
//				int end = name.length();
//				String ext = name.substring(start, end);
//				return ext;
//			}
//		}
//		return new String("");
//	}

	/**
	 * @param file : File 객체
	 * @return String
	 *         <p>
	 *         특정 파일의 확장자 가져오기
	 *         </p>
	 */
//	private static String getFileIcons(File file) {
//		String name = file.getName();
//		return getFileIcons(name);
//	}

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

	public static String getFileIcon(String name) {
		String icon = "/Windchill/jsp/images/fileicon/file_notepad.gif";
		if (!StringUtils.isNull(name)) {
			String ext = FileUtil.getExtension(name);
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
		}
		return icon;
	}

	public static Vector<String[]> getSecondary(ContentHolder holder) throws Exception {
		Vector<String[]> secondarys = new Vector<String[]>();
		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			if (item instanceof ApplicationData) {
				String[] secondary = new String[8];
				ApplicationData data = (ApplicationData) item;
				// 0 = holder oid
				secondary[0] = holder.getPersistInfo().getObjectIdentifier().getStringValue();
				// 1 = app oid
				secondary[1] = data.getPersistInfo().getObjectIdentifier().getStringValue();
				// 2 = name
				secondary[2] = data.getFileName();
				// 3 = size
				secondary[3] = data.getFileSizeKB() + " KB";
				// 4 = icon
				secondary[4] = getFileIcon(secondary[2]);
				// 5 = down url
				secondary[5] = ContentHelper.getDownloadURL(holder, data, false, secondary[2]).toString();
				// 6 = file version
				secondary[6] = data.getFileVersion();
				// 7 = file category
				secondary[7] = data.getCategory();

				secondarys.add(secondary);
			}
		}
		return secondarys;
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

	public static void updatePrimary(Map<String, Object> param, ContentHolder holder) throws Exception {

		QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
		if (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			ContentServerHelper.service.deleteContent(holder, item);
		}

		Iterator<String> it = param.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (key.contains(PRIMARY_KEY)) {
				String value = (String) param.get(key);

				String path = value.split("&")[0];
				String fname = value.split("&")[1];
				System.out.println("path : " + path);
				System.out.println("fname : " + fname);
				File file = new File(path);
				String category = getCategory(file.getName());
				System.out.println("category : " + category);
				ApplicationData data = ApplicationData.newApplicationData(holder);
				data.setRole(ContentRoleType.PRIMARY);
				data.setCategory(category);
				data.setCreatedBy(SessionHelper.manager.getPrincipalReference());
				data = (ApplicationData) ContentServerHelper.service.updateContent(holder, data, path);

				data.setFileName(fname);
				PersistenceHelper.manager.modify(data);

			}
		}
	}

	public static Vector<String[]> getImages(ContentHolder holder) throws Exception {
		Vector<String[]> images = new Vector<String[]>();
		QueryResult result = ContentHelper.service.getContentsByRole(holder,
				ContentRoleType.toContentRoleType("IMAGE"));
		while (result.hasMoreElements()) {
			ContentItem item = (ContentItem) result.nextElement();
			if (item instanceof ApplicationData) {
				String[] image = new String[8];
				ApplicationData data = (ApplicationData) item;
				// 0 = holder oid
				image[0] = holder.getPersistInfo().getObjectIdentifier().getStringValue();
				// 1 = app oid
				image[1] = data.getPersistInfo().getObjectIdentifier().getStringValue();
				// 2 = name
				image[2] = data.getFileName();
				// 3 = size
				image[3] = data.getFileSizeKB() + " KB";
				// 4 = icon
				image[4] = getFileIcon(image[2]);
				// 5 = down url
				image[5] = ContentHelper.getDownloadURL(holder, data, false, image[2]).toString();
				// 6 = file version
				image[6] = data.getFileVersion();
				// 7 = file category
				image[7] = data.getCategory();

				images.add(image);
			}
		}
		return images;
	}

	public static Vector<File> getFileLists(String path, Map<String, Object> param) {
		Vector<File> list = new Vector<File>();
		File dir = new File(path);
		String name = (String) param.get("name");
		String fileType = (String) param.get("fileType");

		String ext = null;
		if ("파워포인트".equals(fileType)) {
			ext = "pptx";
		} else if ("엑셀".equals(fileType)) {
			ext = "xlsx";
		}

		File[] f = dir.listFiles();
		for (int i = 0; i < f.length; i++) {
			if (f[i].isDirectory()) {
				continue;
			} else {
				if (!StringUtils.isNull(fileType)) {
					if (FileUtil.getExtension(f[i].getName()).equalsIgnoreCase(ext)) {
						if (!list.contains(f[i])) {
							list.add(f[i]);
						}
					}
				}

				if (!StringUtils.isNull(name)) {
					if (f[i].getName().contains(name.toUpperCase())) {
						if (!list.contains(f[i])) {
							list.add(f[i]);
						}
					}
				}

				if (StringUtils.isNull(name) && StringUtils.isNull(fileType)) {
					if (!list.contains(f[i])) {
						list.add(f[i]);
					}
				}
			}
		}
		return list;
	}

	public static void updatePartContents(String value, ContentHolder holder) throws Exception {
		String path = value.split("&")[0];
		String fname = value.split("&")[1];

		File file = new File(path);
		String category = getCategory(file.getName());
		ApplicationData data = ApplicationData.newApplicationData(holder);
		data.setRole(ContentRoleType.PRIMARY);
		// data.setRole(ContentRoleType.SECONDARY);
		data.setCategory(category);
		data.setCreatedBy(SessionHelper.manager.getPrincipalReference());
		data = (ApplicationData) ContentServerHelper.service.updateContent(holder, data, path);

		data.setFileName(fname);
		PersistenceHelper.manager.modify(data);
	}
}