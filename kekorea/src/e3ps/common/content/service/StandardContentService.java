package e3ps.common.content.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import e3ps.common.content.Contents;
import e3ps.common.content.ContentsPersistablesLink;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.StringUtils;
import e3ps.common.util.ZipUtils;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.enterprise.RevisionControlled;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.pom.Transaction;
import wt.services.ManagerException;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTProperties;

public class StandardContentService extends StandardManager implements ContentService, MessageHelper {

	private static String downRoot;
	private static String temp;
	static {
		try {
			temp = WTProperties.getLocalProperties().getProperty("wt.home") + File.separator + "temp" + File.separator
					+ "upload";
			File dir = new File(temp);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			dir = null;
			downRoot = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator + "jsp"
					+ File.separator + "temp" + File.separator + "pdm";
			dir = new File(downRoot);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2126933766422868557L;

	private static final String PRIMARY_KEY = "primary";

	private static final String SECONDARY_KEY = "secondary";

	private static final String ALL_UPLOAD_KEY = "allContent";

	public static StandardContentService newStandardContentService() throws WTException {
		StandardContentService instance = new StandardContentService();
		instance.initialize();
		return instance;
	}

	protected synchronized void performStartupProcess() throws ManagerException {
		super.performStartupProcess();
		try {
			File dir = new File(temp);
			File[] fs = dir.listFiles();
			for (int i = 0; i < fs.length; i++) {
				// fs[i].delete();
				// 생각좀 해보자..
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public Map<String, Object> uploadContent(HttpServletRequest request) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String path = temp;

		Transaction trs = new Transaction();
		try {
			trs.start();

			File tt = new File(path);

			if (tt != null) {
				File[] files = tt.listFiles();
				for (File ff : files) {

					if (ff.getName().equals(tt.getName())) {
						ff.delete();
					}
				}
			}

			File directory = new File(path);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			int limit = 1024 * 1024 * 1024;
			MultipartRequest multi = new MultipartRequest(request, path, limit, "UTF-8", new DefaultFileRenamePolicy());

			String roleType = multi.getParameter("roleType");
			String origin = multi.getOriginalFileName(roleType);
			String name = multi.getFilesystemName(roleType);
			String type = multi.getContentType(roleType);

			String fullPath = path + "/" + name;
			File file = new File(fullPath);
			InputStream[] streams = new InputStream[1];
			streams[0] = new FileInputStream(file);

			long[] size = new long[1];
			size[0] = file.length();

			String[] paths = new String[1];
			paths[0] = file.getPath();

			fullPath = fullPath.replace("\\", "/");

			map.put("name", origin);
			map.put("type", type);
			map.put("saveName", name);
			map.put("fileSize", size);
			map.put("ext", FileUtil.getExtension(name));
			// map.put("uploadedPath : '/jsp/upload/', ");
			map.put("thumbUrl", "");
			map.put("roleType", roleType);
			map.put("saveLoc", fullPath);
			map.put("delocId", "");

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("success", false);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;

	}

	@Override
	public Map<String, Object> deleteContent(Map<String, Object> param) throws WTException {
		Transaction trs = new Transaction();
		try {
			trs.start();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return null;
	}

	@Override
	public List<Map<String, Object>> getSecondaryContent(Map<String, Object> param) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		String oid = (String) param.get("oid");
		ContentHolder holder = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			holder = (ContentHolder) rf.getReference(oid).getObject();
			QueryResult result = wt.content.ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);
			int id = 0;
			while (result.hasMoreElements()) {
				ContentItem item = (ContentItem) result.nextElement();
				if (item instanceof ApplicationData) {
					ApplicationData data = (ApplicationData) item;
					Map<String, Object> fileMap = new HashMap<String, Object>();
					fileMap.put("id", id);
					fileMap.put("name", data.getFileName());
					fileMap.put("size", data.getFileSize());
					fileMap.put("uuid", UUID.randomUUID());
					fileMap.put("thumbnailUrl", "");
					fileMap.put("path", data.getUploadedFromPath());
					fileMap.put("oid", data.getPersistInfo().getObjectIdentifier().getStringValue());
					list.add(fileMap);
					id++;
				}
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return list;
	}

	@Override
	public List<Map<String, Object>> getPrimaryContent(Map<String, Object> param) throws WTException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
		String oid = (String) param.get("oid");
		ContentHolder holder = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			holder = (ContentHolder) rf.getReference(oid).getObject();
			QueryResult result = wt.content.ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);
			int id = 0;
			if (result.hasMoreElements()) {
				ContentItem item = (ContentItem) result.nextElement();
				if (item instanceof ApplicationData) {
					ApplicationData data = (ApplicationData) item;
					Map<String, Object> fileMap = new HashMap<String, Object>();
					fileMap.put("id", id);
					fileMap.put("name", data.getFileName());
					fileMap.put("size", data.getFileSize());
					fileMap.put("uuid", UUID.randomUUID());
					fileMap.put("thumbnailUrl", "");
					fileMap.put("path", data.getUploadedFromPath());
					fileMap.put("oid", data.getPersistInfo().getObjectIdentifier().getStringValue());
					list.add(fileMap);
				}
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return list;
	}

	@Override
	public void createContents(Map<String, Object> param) throws WTException {
		String oid = (String) param.get("oid");
		String name = (String) param.get("name");
		String number = (String) param.get("number");
		String description = (String) param.get("descriptionDoc");
		ReferenceFactory rf = new ReferenceFactory();
		Persistable persistable = null;
		Contents contents = null;
		Transaction trs = new Transaction();

		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			persistable = (Persistable) rf.getReference(oid).getObject();

			String version = "";
			if (persistable instanceof RevisionControlled) {
				RevisionControlled rc = (RevisionControlled) persistable;
				version = rc.getVersionIdentifier().getSeries().getValue() + "."
						+ rc.getIterationIdentifier().getSeries().getValue();
			}

			String fileName = ContentUtils.getPrimary((ContentHolder) persistable)[2];

			if (!StringUtils.isNull(fileName)) {

				contents = Contents.newContents();
				contents.setFileName(fileName);
				contents.setName(name);
				contents.setVersion(version);
				contents.setNumber(number);
				contents.setDescription(description);
				contents.setOwnership(ownership);
				contents.setPersistables(persistable);

				contents = (Contents) PersistenceHelper.manager.save(contents);

				if (param.containsKey(ALL_UPLOAD_KEY)) {
					ContentUtils.updateContents(param, contents);
				} else {
					ContentUtils.updatePrimary(param, contents);
				}

				ContentsPersistablesLink link = ContentsPersistablesLink.newContentsPersistablesLink(contents,
						persistable);
				PersistenceHelper.manager.save(link);

				// 첨부 파일 잇을 경우..

				Iterator<String> it = param.keySet().iterator();
				while (it.hasNext()) {
					String key = (String) it.next();
					if (key.contains(SECONDARY_KEY) || key.contains(ALL_UPLOAD_KEY) || key.contains(PRIMARY_KEY)) {
						String svalue = (String) param.get(key);

						String path = svalue.split("&")[0];
						String fname = svalue.split("&")[1];

						Contents scontents = Contents.newContents();
						scontents.setFileName(fname);
						scontents.setName(name);
						scontents.setVersion(version);
						scontents.setNumber(number);
						scontents.setDescription(description);
						scontents.setOwnership(ownership);
						scontents.setPersistables(persistable);

						scontents = (Contents) PersistenceHelper.manager.save(scontents);

						File file = new File(path);
						String category = ContentUtils.getCategory(file.getName());
						ApplicationData data = ApplicationData.newApplicationData(scontents);
						data.setRole(ContentRoleType.PRIMARY);
						data.setCategory(category);
						data.setCreatedBy(SessionHelper.manager.getPrincipalReference());
						data = (ApplicationData) ContentServerHelper.service.updateContent(scontents, data, path);

						data.setFileName(fname);
						PersistenceHelper.manager.modify(data);

						ContentsPersistablesLink slink = ContentsPersistablesLink.newContentsPersistablesLink(scontents,
								persistable);
						PersistenceHelper.manager.save(slink);
					}
				}
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public void deleteContents(Map<String, Object> param) throws WTException {
		String oid = (String) param.get("oid");
		ReferenceFactory rf = new ReferenceFactory();
		Persistable persistable = null;
		Contents contents = null;
		Transaction trs = new Transaction();
		try {
			trs.start();

			persistable = (Persistable) rf.getReference(oid).getObject();

			QueryResult qr = PersistenceHelper.manager.navigate(persistable, "contents", ContentsPersistablesLink.class,
					false);
			while (qr.hasMoreElements()) {
				ContentsPersistablesLink link = (ContentsPersistablesLink) qr.nextElement();
				contents = link.getContents();
				PersistenceHelper.manager.delete(contents);
				PersistenceHelper.manager.delete(link);
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
	}

	@Override
	public Map<String, Object> contentsMultiDown(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Contents contents = null;
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		String path = "contents";
		Transaction trs = new Transaction();
		try {
			trs.start();

			File root = new File(downRoot + File.separator + path);
			if (!root.exists()) {
				root.mkdirs();
			}

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			String zipFileName = sessionUser.getName() + "_CONTENTS_"
					+ new Timestamp(new Date().getTime()).toString().substring(0, 10) + ".zip";

			for (String oid : list) {
				contents = (Contents) rf.getReference(oid).getObject();

				QueryResult result = ContentHelper.service.getContentsByRole(contents, ContentRoleType.PRIMARY);

				if (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();

					byte[] buffer = new byte[10240];
					InputStream is = ContentServerHelper.service.findLocalContentStream(data);
					// String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
					String name = data.getFileName();
					File file = new File(downRoot + File.separator + path + File.separator + name);
					FileOutputStream fos = new FileOutputStream(file);
					int j = 0;
					while ((j = is.read(buffer, 0, 10240)) > 0) {
						fos.write(buffer, 0, j);
					}
					fos.close();
					is.close();
				}
			}

			ZipUtils.compress(path, zipFileName);
			// 파일삭제
			File delFiles = new File(downRoot + File.separator + path);
			File[] fs = delFiles.listFiles();
			for (int i = 0; i < fs.length; i++) {
				fs[i].delete();
			}

			File rtnFile = new File(downRoot + File.separator + "zip" + File.separator + zipFileName);

			map.put("result", SUCCESS);
			// 다운 URL
			map.put("url", "/Windchill/jsp/temp/pdm/zip/" + rtnFile.getName());

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
			// map.put("url", "/Windchill/plm/EPM/listEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> contentsDown(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Contents contents = null;
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		String path = "contents";
		Transaction trs = new Transaction();
		try {
			trs.start();

			File root = new File(downRoot + File.separator + path);
			if (!root.exists()) {
				root.mkdirs();
			}

			String[] primary = null;

			for (String oid : list) {
				contents = (Contents) rf.getReference(oid).getObject();

				primary = ContentUtils.getPrimary(contents);
			}

			map.put("result", SUCCESS);
			// 다운 URL
			map.put("url", primary[5]);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
			// map.put("url", "/Windchill/plm/EPM/listEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	// @Override
	// public Map<String, Object> contentsDocMultiDown(Map<String, Object> param)
	// throws WTException {
	// Map<String, Object> map = new HashMap<String, Object>();
	// WTDocument document = null;
	// List<String> list = (List<String>) param.get("list");
	// ReferenceFactory rf = new ReferenceFactory();
	// String path = "contents";
	// Transaction trs = new Transaction();
	// try {
	// trs.start();
	//
	// File root = new File(downRoot + File.separator + path);
	// if (!root.exists()) {
	// root.mkdirs();
	// }
	//
	// WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
	// String zipFileName = sessionUser.getName() + "_DOCUMENT_"
	// + new Timestamp(new Date().getTime()).toString().substring(0, 10) + ".zip";
	//
	// for (String oid : list) {
	// document = (WTDocument) rf.getReference(oid).getObject();
	//
	// QueryResult result = ContentHelper.service.getContentsByRole(document,
	// ContentRoleType.PRIMARY);
	//
	// if (result.hasMoreElements()) {
	// ApplicationData data = (ApplicationData) result.nextElement();
	//
	// byte[] buffer = new byte[10240];
	// InputStream is = ContentServerHelper.service.findLocalContentStream(data);
	// String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
	// File file = new File(downRoot + File.separator + path + File.separator +
	// name);
	// FileOutputStream fos = new FileOutputStream(file);
	// int j = 0;
	// while ((j = is.read(buffer, 0, 10240)) > 0) {
	// fos.write(buffer, 0, j);
	// }
	// fos.close();
	// is.close();
	// }
	//
	// result.reset();
	//
	// result = ContentHelper.service.getContentsByRole(document,
	// ContentRoleType.SECONDARY);
	//
	// while (result.hasMoreElements()) {
	// ApplicationData data = (ApplicationData) result.nextElement();
	//
	// byte[] buffer = new byte[10240];
	// InputStream is = ContentServerHelper.service.findLocalContentStream(data);
	// String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
	// File file = new File(downRoot + File.separator + path + File.separator +
	// name);
	// FileOutputStream fos = new FileOutputStream(file);
	// int j = 0;
	// while ((j = is.read(buffer, 0, 10240)) > 0) {
	// fos.write(buffer, 0, j);
	// }
	// fos.close();
	// is.close();
	// }
	// }
	//
	// ZipUtils.compress(path, zipFileName);
	// // 파일삭제
	// File delFiles = new File(downRoot + File.separator + path);
	// File[] fs = delFiles.listFiles();
	// for (int i = 0; i < fs.length; i++) {
	// fs[i].delete();
	// }
	//
	// File rtnFile = new File(downRoot + File.separator + "zip" + File.separator +
	// zipFileName);
	//
	// map.put("result", SUCCESS);
	// // 다운 URL
	// map.put("url", "/Windchill/jsp/temp/pdm/zip/" + rtnFile.getName());
	//
	// trs.commit();
	// trs = null;
	// } catch (Exception e) {
	// map.put("result", FAIL);
	// map.put("msg", "다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
	//// map.put("url", "/Windchill/plm/EPM/listEpm");
	// e.printStackTrace();
	// trs.rollback();
	// } finally {
	// if (trs != null)
	// trs.rollback();
	// }
	// return map;
	// }

	@Override
	public Map<String, Object> downSecondary(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ContentHolder holder = null;
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		String path = "contents";
		Transaction trs = new Transaction();
		try {
			trs.start();

			File root = new File(downRoot + File.separator + path);
			if (!root.exists()) {
				root.mkdirs();
			}

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			String zipFileName = sessionUser.getName() + "_SECONDARY_"
					+ new Timestamp(new Date().getTime()).toString().substring(0, 10) + ".zip";

			for (String oid : list) {
				holder = (ContentHolder) rf.getReference(oid).getObject();

				QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);

				while (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();

					byte[] buffer = new byte[10240];
					InputStream is = ContentServerHelper.service.findLocalContentStream(data);
					// System.out.println("name=" + data.getFileName());
					String name = data.getFileName();
					// String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
					File file = new File(downRoot + File.separator + path + File.separator + name);
					FileOutputStream fos = new FileOutputStream(file);
					int j = 0;
					while ((j = is.read(buffer, 0, 10240)) > 0) {
						fos.write(buffer, 0, j);
					}
					fos.close();
					is.close();
				}
			}

			ZipUtils.compress(path, zipFileName);
			// 파일삭제
			File delFiles = new File(downRoot + File.separator + path);
			File[] fs = delFiles.listFiles();
			for (int i = 0; i < fs.length; i++) {
				fs[i].delete();
			}

			File rtnFile = new File(downRoot + File.separator + "zip" + File.separator + zipFileName);

			map.put("result", SUCCESS);
			// 다운 URL
			map.put("url", "/Windchill/jsp/temp/pdm/zip/" + rtnFile.getName());

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
			// map.put("url", "/Windchill/plm/EPM/listEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> downPrimary(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ContentHolder holder = null;
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		String path = "contents";
		Transaction trs = new Transaction();
		try {
			trs.start();

			File root = new File(downRoot + File.separator + path);
			if (!root.exists()) {
				root.mkdirs();
			}

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			String zipFileName = sessionUser.getName() + "_PRIMARY_"
					+ new Timestamp(new Date().getTime()).toString().substring(0, 10) + ".zip";

			for (String oid : list) {
				holder = (ContentHolder) rf.getReference(oid).getObject();

				QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);

				if (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();

					byte[] buffer = new byte[10240];
					InputStream is = ContentServerHelper.service.findLocalContentStream(data);
					// String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
					String name = data.getFileName();
					File file = new File(downRoot + File.separator + path + File.separator + name);
					FileOutputStream fos = new FileOutputStream(file);
					int j = 0;
					while ((j = is.read(buffer, 0, 10240)) > 0) {
						fos.write(buffer, 0, j);
					}
					fos.close();
					is.close();
				}
			}

			ZipUtils.compress(path, zipFileName);
			// 파일삭제
			File delFiles = new File(downRoot + File.separator + path);
			File[] fs = delFiles.listFiles();
			for (int i = 0; i < fs.length; i++) {
				fs[i].delete();
			}

			File rtnFile = new File(downRoot + File.separator + "zip" + File.separator + zipFileName);

			map.put("result", SUCCESS);
			// 다운 URL
			map.put("url", "/Windchill/jsp/temp/pdm/zip/" + rtnFile.getName());

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
			// map.put("url", "/Windchill/plm/EPM/listEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> downContentAll(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ContentHolder holder = null;
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		String path = "contents";

//		EPMDocument epm = (EPMDocument) holder;
		String sFileName = "";
		Transaction trs = new Transaction();
		try {
			trs.start();

			File root = new File(downRoot + File.separator + path);
			if (!root.exists()) {
				root.mkdirs();
			}

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			String zipFileName = sessionUser.getName() + "_CONTENTALL_"
					+ new Timestamp(new Date().getTime()).toString().substring(0, 10) + ".zip";

			for (String oid : list) {
				holder = (ContentHolder) rf.getReference(oid).getObject();

				QueryResult result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.PRIMARY);

				if (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();
					String fileExt = sFileName.substring(sFileName.lastIndexOf(".") + 1);
					sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + "." + fileExt;
					byte[] buffer = new byte[10240];
					InputStream is = ContentServerHelper.service.findLocalContentStream(data);
					// String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
//					String name = data.getFileName();
					File file = new File(
							downRoot + File.separator + path + File.separator + sFileName.toUpperCase() + ".DWG");
					FileOutputStream fos = new FileOutputStream(file);
					int j = 0;
					while ((j = is.read(buffer, 0, 10240)) > 0) {
						fos.write(buffer, 0, j);
					}
					fos.close();
					is.close();
				}

				result.reset();

				result = ContentHelper.service.getContentsByRole(holder, ContentRoleType.SECONDARY);

				while (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();
					String fileExt = sFileName.substring(sFileName.lastIndexOf(".") + 1);
					sFileName = sFileName.substring(0, sFileName.lastIndexOf(".")) + "." + fileExt;
					byte[] buffer = new byte[10240];
					InputStream is = ContentServerHelper.service.findLocalContentStream(data);
					// String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
//					String name = data.getFileName();
					File file = new File(
							downRoot + File.separator + path + File.separator + sFileName.toUpperCase() + ".PDF");
					FileOutputStream fos = new FileOutputStream(file);
					int j = 0;
					while ((j = is.read(buffer, 0, 10240)) > 0) {
						fos.write(buffer, 0, j);
					}
					fos.close();
					is.close();
				}
			}

			ZipUtils.compress(path, zipFileName);
			// 파일삭제
			File delFiles = new File(downRoot + File.separator + path);
			File[] fs = delFiles.listFiles();
			for (int i = 0; i < fs.length; i++) {
				fs[i].delete();
			}

			File rtnFile = new File(downRoot + File.separator + "zip" + File.separator + zipFileName);

			map.put("result", SUCCESS);
			// 다운 URL
			map.put("url", "/Windchill/jsp/temp/pdm/zip/" + rtnFile.getName());

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
			// map.put("url", "/Windchill/plm/EPM/listEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}
}
