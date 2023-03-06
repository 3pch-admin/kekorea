package e3ps.epm.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ptc.wvs.common.ui.PublishResult;
import com.ptc.wvs.server.publish.Publish;
import com.ptc.wvs.server.util.PublishUtils;

import e3ps.approval.beans.ApprovalMasterViewData;
import e3ps.approval.service.ApprovalHelper;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.MessageHelper;
import e3ps.common.util.StringUtils;
import e3ps.common.util.ZipUtils;
import e3ps.epm.ViewerData;
import e3ps.epm.dto.EpmViewData;
import e3ps.epm.dto.ProcessOutputThread;
import e3ps.erp.service.StandardErpService;
import e3ps.workspace.ApprovalContract;
import e3ps.workspace.ApprovalContractPersistableLink;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.ownership.Ownership;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.representation.Representation;
import wt.series.MultilevelSeries;
import wt.series.Series;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.FileUtil;
import wt.util.WTException;
import wt.util.WTProperties;
import wt.vc.IterationIdentifier;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.config.ConfigSpec;
import wt.vc.wip.WorkInProgressHelper;

public class StandardEpmService extends StandardManager implements EpmService, MessageHelper {

	private static final long serialVersionUID = 8782888052535449244L;

	private static String downRoot;
	static {
		try {
			downRoot = WTProperties.getLocalProperties().getProperty("wt.codebase.location") + File.separator + "jsp"
					+ File.separator + "temp" + File.separator + "pdm";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static StandardEpmService newStandardEpmService() throws WTException {
		StandardEpmService instance = new StandardEpmService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> addEpmAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		Transaction trs = new Transaction();
		try {
			trs.start();
			List<String> list = (List<String>) param.get("list");
			EPMDocument epm = null;
			ReferenceFactory rf = new ReferenceFactory();
			ArrayList<String[]> data = new ArrayList<String[]>();

			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();
				EpmViewData edata = new EpmViewData(epm);

				// 0 oid, 1 number, 2 name, 3 version, 4 state, 5 creator, 6 createdate
				String[] s = new String[] { edata.oid, edata.number, edata.name, edata.state + "$" + edata.stateKey,
						edata.version + "." + edata.iteration, edata.creator, edata.createDate, edata.iconPath,
						edata.cadData[4], edata.cadData[5], edata.dwg[4], edata.dwg[5], edata.pdf[4], edata.pdf[5],
						String.valueOf(edata.is2D), edata.modifier, edata.modifierFullName, edata.name_of_parts,
						edata.creatorFullName, StringUtils.replaceToValue(epm.getDescription()),
						edata.location.substring(8) };
				data.add(s);
			}

			map.put("result", SUCCESS);
			map.put("list", data);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "도면 추가 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요");
			map.put("url", "/Windchill/plm/epm/addEpm");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> printDrw(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		EPMDocument epm = null;
		ReferenceFactory rf = new ReferenceFactory();
		String path = "pdf";
		Transaction trs = new Transaction();
		try {
			trs.start();

			File root = new File(downRoot + File.separator + path);
			if (!root.exists()) {
				root.mkdirs();
			}

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			String zipFileName = sessionUser.getName() + "_"
					+ new Timestamp(new Date().getTime()).toString().substring(0, 10) + ".zip";

			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();

				Representation representation = PublishUtils.getRepresentation(epm);

				if (representation != null) {
					// PDF
					QueryResult result = ContentHelper.service.getContentsByRole(representation,
							ContentRoleType.SECONDARY);
					while (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();

						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
						String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
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
			}

			ZipUtils.compress(path, zipFileName);
			// 파일삭제
			File delFiles = new File(downRoot + File.separator + path);
			File[] fs = delFiles.listFiles();
			for (int i = 0; i < fs.length; i++) {
				fs[i].delete();
			}

			// zip = new File(downRoot + File.separator + "zip" + File.separator +
			// zipFileName);
			//
			// reValue = zipFileName + "▦▦" + zip.getPath();

			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "도면 다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/epm/printDrw");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> downDrw(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		EPMDocument epm = null;
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		String path = "drw";
		Transaction trs = new Transaction();
		try {
			trs.start();

			File root = new File(downRoot + File.separator + path);
			if (!root.exists()) {
				root.mkdirs();
			}

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			String zipFileName = sessionUser.getName() + "_DRW_"
					+ new Timestamp(new Date().getTime()).toString().substring(0, 10) + ".zip";

			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();

				String type = epm.getDocType().toString();
				if (type.equals("CADDRAWING")) {

					QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.PRIMARY);
					while (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();

						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
						String name = new String(epm.getCADName().getBytes("EUC-KR"), "8859_1");
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
			map.put("msg", "DRW 도면 다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/epm/printEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> downDwg(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		EPMDocument epm = null;
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		String path = "dwg";
		Transaction trs = new Transaction();
		try {
			trs.start();

			File root = new File(downRoot + File.separator + path);
			if (!root.exists()) {
				root.mkdirs();
			}

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			String zipFileName = sessionUser.getName() + "_DWG_"
					+ new Timestamp(new Date().getTime()).toString().substring(0, 10) + ".zip";

			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();

				String cadType = epm.getAuthoringApplication().toString();
				System.out.println("cadType=" + cadType);
				if ("ACAD".equals(cadType)) {
					// autocad 주 첨부파일이 dwg
					// QueryResult result = ContentHelper.service.getContentsByRole(representation,
					// ContentRoleType.SECONDARY);
					QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.PRIMARY);
					if (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();

						String ext = FileUtil.getExtension(epm.getCADName());
						if (!ext.equalsIgnoreCase("dwg") && !ext.equalsIgnoreCase("zip")) {
							continue;
						}

						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
						String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
						File file = new File(downRoot + File.separator + path + File.separator + name);
						FileOutputStream fos = new FileOutputStream(file);
						int j = 0;
						while ((j = is.read(buffer, 0, 10240)) > 0) {
							fos.write(buffer, 0, j);
						}
						fos.close();
						is.close();
					}
				} else {

					Representation representation = PublishUtils.getRepresentation(epm);

					if (representation != null) {
						// QueryResult result = ContentHelper.service.getContentsByRole(representation,
						// ContentRoleType.SECONDARY);
						QueryResult result = ContentHelper.service.getContentsByRole(representation,
								ContentRoleType.ADDITIONAL_FILES);
						String sFileName = epm.getCADName();
						sFileName = sFileName.substring(0, sFileName.lastIndexOf("."));
						while (result.hasMoreElements()) {
							ApplicationData data = (ApplicationData) result.nextElement();

							String ext = FileUtil.getExtension(data.getFileName());
							if (!ext.equalsIgnoreCase("dwg") && !ext.equalsIgnoreCase("zip")) {
								continue;
							}

							byte[] buffer = new byte[10240];
							InputStream is = ContentServerHelper.service.findLocalContentStream(data);
//							String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
							File file = new File(downRoot + File.separator + path + File.separator
									+ sFileName.toUpperCase() + ".DWG");
							FileOutputStream fos = new FileOutputStream(file);
							int j = 0;
							while ((j = is.read(buffer, 0, 10240)) > 0) {
								fos.write(buffer, 0, j);
							}
							fos.close();
							is.close();
						}
					}
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
			map.put("msg", "DWG 도면 다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/epm/listEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> downPdf(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		EPMDocument epm = null;
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		String path = "pdf";
		Transaction trs = new Transaction();
		try {
			trs.start();

			File root = new File(downRoot + File.separator + path);
			if (!root.exists()) {
				root.mkdirs();
			}

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			String zipFileName = sessionUser.getName() + "_PDF_"
					+ new Timestamp(new Date().getTime()).toString().substring(0, 10) + ".zip";

			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();

				String sFileName = epm.getCADName();

				sFileName = sFileName.substring(0, sFileName.lastIndexOf("."));
				Representation representation = PublishUtils.getRepresentation(epm);
				// if (representation == null) {
				// map.put("result", "SUCCESS");
				// map.put("reload", true);
				// map.put("msg", "변환된 PDF 파일이 없습니다.");
				// return map;
				// }

				if (representation != null) {
					// PDF
					// QueryResult result = ContentHelper.service.getContentsByRole(representation,
					// ContentRoleType.ADDITIONAL_FILES);
					QueryResult result = ContentHelper.service.getContentsByRole(representation,
							ContentRoleType.SECONDARY);
					while (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();

						String ext = FileUtil.getExtension(data.getFileName());

						if (!ext.equalsIgnoreCase("pdf")) {
							continue;
						}

						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
//						String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
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
			map.put("msg", "PDF 도면 다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/epm/listEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> downAll(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		EPMDocument epm = null;
		List<String> list = (List<String>) param.get("list");
		ReferenceFactory rf = new ReferenceFactory();
		String path = "all";
		Transaction trs = new Transaction();
		try {
			trs.start();

			File root = new File(downRoot + File.separator + path);
			if (!root.exists()) {
				root.mkdirs();
			}

			WTUser sessionUser = (WTUser) SessionHelper.manager.getPrincipal();
			String zipFileName = sessionUser.getName() + "_ALL_"
					+ new Timestamp(new Date().getTime()).toString().substring(0, 10) + ".zip";

			// 주 도면 3D or 2D drw..
			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();
				// PDF
				QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.PRIMARY);
				System.out.println("1qjs");
				while (result.hasMoreElements()) {
					ApplicationData data = (ApplicationData) result.nextElement();

					byte[] buffer = new byte[10240];
					InputStream is = ContentServerHelper.service.findLocalContentStream(data);
					String name = new String(epm.getCADName().toUpperCase().getBytes("EUC-KR"), "8859_1");
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

			// 변환 파일
			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();

				Representation representation = PublishUtils.getRepresentation(epm);

				if (representation != null) {
					// PDF
					QueryResult result = ContentHelper.service.getContentsByRole(representation,
							ContentRoleType.SECONDARY);
					while (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();

						String sFileName = epm.getCADName();

						sFileName = sFileName.substring(0, sFileName.lastIndexOf("."));

						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
//						String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
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
			}

			// 변환 추가 파일
			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();

				Representation representation = PublishUtils.getRepresentation(epm);

				if (representation != null) {
					// PDF
					QueryResult result = ContentHelper.service.getContentsByRole(representation,
							ContentRoleType.ADDITIONAL_FILES);
					while (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();
						String sFileName = epm.getCADName();

						sFileName = sFileName.substring(0, sFileName.lastIndexOf("."));
						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
//						String name = new String(data.getFileName().getBytes("EUC-KR"), "8859_1");
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
			map.put("msg", "도면과 관련된 모든 첨부파일 다운로드 중 에러가 발생 하였습니다.\n시스템 관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/epm/listEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void sendContent(EPMDocument epm) throws WTException {

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
	}

	@Override
	public Map<String, Object> doPublishView(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		EPMDocument epm = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			epm = (EPMDocument) rf.getReference(oid).getObject();

			ConfigSpec configspec = null;
			PublishResult rs = Publish.doPublish(false, true, epm, configspec, null, false, null, null, 1, null, 2,
					null);

			if (rs.isSuccessful()) {
				System.out.println("메일? 생성??");
			}

			map.put("result", SUCCESS);
			map.put("msg", "뷰어 파일이 생성 되어집니다.\n잠시 후에 확인 해주세요.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "뷰어 파일 생성 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> deletePublishView(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		String oid = (String) param.get("oid");
		EPMDocument epm = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			epm = (EPMDocument) rf.getReference(oid).getObject();

			QueryResult result = ContentHelper.service.getContentsByRole(epm,
					ContentRoleType.toContentRoleType("IMAGE"));
			while (result.hasMoreElements()) {
				ContentItem item = (ContentItem) result.nextElement();
				ContentServerHelper.service.deleteContent(epm, item);
			}

			Representation representation = PublishUtils.getRepresentation(epm);

			if (representation != null) {
				PersistenceHelper.manager.delete(representation);
			}

			map.put("result", SUCCESS);
			map.put("msg", "뷰어 파일이 삭제 되었습니다.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "뷰어 파일 삭제 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			e.printStackTrace();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public void dwgToPDF(EPMDocument epm) throws WTException {
		Transaction trs = new Transaction();
		try {
			trs.start();

			Runtime rt = Runtime.getRuntime();

			String dwgFilePath = epm.getCADName();
			String pdfFileName = epm.getCADName() + ".pdf";

			dwgFilePath = dwgFilePath.replace("/", "\\");
			pdfFileName = pdfFileName.replace("/", "\\");

			String exec1 = "C:\\Program Files (x86)\\AutoDWG\\AutoDWG DWG to PDF Converter\\d2p.exe /InFile";
			String conFile = "C:\\Program Files (x86)\\AutoDWG\\AutoDWG DWG to PDF Converter\\AutoDWGPdf.ddp";

			String exec = exec1 + " " + dwgFilePath + " /OutFile " + pdfFileName + " /InConfigFile " + conFile;
			System.out.println(exec);

			Process p = rt.exec(exec);
			ProcessOutputThread o = new ProcessOutputThread(p.getInputStream(), new StringBuffer());
			o.start();
			p.waitFor();

			ApplicationData data = ApplicationData.newApplicationData(epm);
			data.setRole(ContentRoleType.SECONDARY);
			// data.setRole(ContentRoleType.ADDITIONAL_FILES);

			ContentServerHelper.service.updateContent(epm, data, pdfFileName);

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
	public Map<String, Object> uploadEpmAttrAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		String primary = (String) param.get("primary");
		XSSFWorkbook wb = null;
		try {
			trs.start();

			File file = new File(primary);
			wb = new XSSFWorkbook(new FileInputStream(file));

			XSSFCell cell; // 전역 변수..
			XSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			for (int i = 1; i < rows; i++) {
				XSSFRow row = sheet.getRow(i);

				cell = row.getCell(0); // ....cell.. epm number
				String number = cell.getStringCellValue();

				System.out.println("number=" + number);

				cell = row.getCell(2);
				String product_name = cell.getStringCellValue(); // iba product_name

				cell = row.getCell(3); // spec..?
				String spec = cell.getStringCellValue();

				cell = row.getCell(4);
				String maker = cell.getStringCellValue();

				cell = row.getCell(5);
				String bom = cell.getStringCellValue();

				cell = row.getCell(6);
				double weight = cell.getNumericCellValue();

				cell = row.getCell(7);
//				String itemClass = cell.getStringCellValue();

				EPMDocument epm = EpmHelper.manager.getLatestEPM(number);

				if (epm != null) {
					System.out.println("도면 존재..");
					boolean isLatestEpm = CommonUtils.isLatestVersion(epm);
					if (!isLatestEpm) {
						System.out.println("최신 도면 아님..");
						epm = (EPMDocument) CommonUtils.getLatestVersion(epm);
					}

					// COLOR_FINISH, MAIN_ASSY, MAKER, MATERIAL, MODELED_BY, PRODUCT_NAME, SPEC,
					// TREATMENT, DRAWING_BY, DIMENSION, BOM,
					// WEIGHT, MASTER_TYPE, ERP_CODE, ITEMCLASSNAME, ITEMCLASSSEQ
					// 삭제..
					IBAUtils.deleteIBA(epm, "MAKER", "s");
					IBAUtils.deleteIBA(epm, "PRODUCT_NAME", "s");
					IBAUtils.deleteIBA(epm, "SPEC", "s");
					IBAUtils.deleteIBA(epm, "BOM", "s");
					IBAUtils.deleteIBA(epm, "WEIGHT", "s");
					// erp 연동...
					IBAUtils.deleteIBA(epm, "ITEMCLASSNAME", "s");
					IBAUtils.deleteIBA(epm, "ITEMCLASSSEQ", "s");

					// 생성
					IBAUtils.createIBA(epm, "s", "MAKER", maker);
					IBAUtils.createIBA(epm, "s", "PRODUCT_NAME", product_name);
					IBAUtils.createIBA(epm, "s", "SPEC", spec);
					IBAUtils.createIBA(epm, "s", "BOM", bom);
					IBAUtils.createIBA(epm, "s", "WEIGHT", String.valueOf(weight));
					// erp 연동...
					IBAUtils.createIBA(epm, "s", "ITEMCLASSNAME", "");
					IBAUtils.createIBA(epm, "s", "ITEMCLASSSEQ", "");

					WTPart part = EpmHelper.manager.getPart(epm);

					if (part != null) {
						System.out.println("부품 존재..");
						boolean isLatest = CommonUtils.isLatestVersion(part);
						if (!isLatest) {
							System.out.println("최신 부품 아님..");
							part = (WTPart) CommonUtils.getLatestVersion(part);
						}

						// 삭제..
						IBAUtils.deleteIBA(part, "MAKER", "s");
						IBAUtils.deleteIBA(part, "PRODUCT_NAME", "s");
						IBAUtils.deleteIBA(part, "SPEC", "s");
						IBAUtils.deleteIBA(part, "BOM", "s");
						IBAUtils.deleteIBA(part, "WEIGHT", "s");
						// erp 연동...
						IBAUtils.deleteIBA(part, "ITEMCLASSNAME", "s");
						IBAUtils.deleteIBA(part, "ITEMCLASSSEQ", "s");

						// 생성
						IBAUtils.createIBA(part, "s", "MAKER", maker);
						IBAUtils.createIBA(part, "s", "PRODUCT_NAME", product_name);
						IBAUtils.createIBA(part, "s", "SPEC", spec);
						IBAUtils.createIBA(part, "s", "BOM", bom);
						IBAUtils.createIBA(part, "s", "WEIGHT", String.valueOf(weight));
						// erp 연동...
						IBAUtils.createIBA(part, "s", "ITEMCLASSNAME", "");
						IBAUtils.createIBA(part, "s", "ITEMCLASSSEQ", "");
					}
				}

			}

			// wb.close();

			map.put("result", SUCCESS);
			map.put("msg", "속성 업로드가 완료 되었습니다.");
			map.put("url", "/Windchill/plm/epm/inputDrwAttr");
			trs.commit();
			trs = null;

		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "속성 업로드 중 에러가 발생하였습니다.\n관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/epm/inputDrwAttr");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> reviseCadDataAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		String primary = (String) param.get("primary");
		XSSFWorkbook wb = null;
		try {
			trs.start();

			File file = new File(primary);
			wb = new XSSFWorkbook(new FileInputStream(file));

			XSSFCell cell; // 전역 변수..
			XSSFSheet sheet = wb.getSheetAt(0);
			int rows = sheet.getPhysicalNumberOfRows();
			for (int i = 1; i < rows; i++) {
				XSSFRow row = sheet.getRow(i);

				System.out.println("i=" + i);

				cell = row.getCell(1); // ....cell.. epm number
				String value = cell.getStringCellValue();
				String number = value.substring(0, value.lastIndexOf("-"));

				String version = value.substring(value.lastIndexOf("-") + 1);

				QuerySpec query = new QuerySpec();
				int idx = query.appendClassList(EPMDocument.class, true);

				SearchCondition sc = null;

				sc = WorkInProgressHelper.getSearchCondition_CI(EPMDocument.class);
				query.appendWhere(sc, new int[] { idx });
				query.appendAnd();

				query.appendOpenParen();
				sc = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, "=",
						number.toUpperCase().trim() + ".PRT");
				query.appendWhere(sc, new int[] { idx });
				query.appendOr();

				sc = new SearchCondition(EPMDocument.class, EPMDocument.NUMBER, "=",
						number.toUpperCase().trim() + ".ASM");
				query.appendWhere(sc, new int[] { idx });
				query.appendCloseParen();
				query.appendAnd();

				sc = VersionControlHelper.getSearchCondition(EPMDocument.class, true);
				query.appendWhere(sc, new int[] { idx });

				CommonUtils.addLastVersionCondition(query, idx);

				QueryResult result = PersistenceHelper.manager.find(query);
				EPMDocument e = null;
				if (result.hasMoreElements()) {
					Object[] obj = (Object[]) result.nextElement();
					e = (EPMDocument) obj[0];
				}

				if (e != null) {
					System.out.println("변경될 도번=" + e.getNumber());

					MultilevelSeries ms = MultilevelSeries.newMultilevelSeries("wt.series.HarvardSeries.StateBased",
							version);
					VersionIdentifier vi = VersionIdentifier.newVersionIdentifier(ms);
					Series series = Series.newSeries("wt.series.HarvardSeries");
					IterationIdentifier ii = IterationIdentifier.newIterationIdentifier(series);

					String oldEpmVersion = e.getVersionIdentifier().getSeries().getValue();

					// epm 2d... 도 생각해봐야할 문제..

					if (!oldEpmVersion.contentEquals(version)) {
						System.out.println("버전이(도면) 존재 하지 않을 경우만..");
						EPMDocument newEPM = (EPMDocument) VersionControlHelper.service.newVersion(e, vi, ii);
						VersionControlHelper.setNote(newEPM, "일괄 개정(도면) 마이그레이션 데이터");
						newEPM = (EPMDocument) PersistenceHelper.manager.save(newEPM);
					}
					// 이전 도면 데이터를 들고 와야...
					WTPart part = EpmHelper.manager.getPart(e);

					if (part != null) {
						String oldPartVersion = part.getVersionIdentifier().getSeries().getValue();
						if (!oldPartVersion.equals(version)) {
							System.out.println("버전이(부품) 존재 하지 않을 경우만..");
							WTPart newPART = (WTPart) VersionControlHelper.service.newVersion(part, vi, ii);
							VersionControlHelper.setNote(newPART, "일괄 개정(부품) 마이그레이션 데이터");
							newPART = (WTPart) PersistenceHelper.manager.save(newPART);
						}
					}

				}

			}

			map.put("result", SUCCESS);
			map.put("msg", "일괄 개정이 완료 되었습니다.");
			map.put("url", "/Windchill/plm/epm/reviseCadData");
			trs.commit();
			trs = null;

		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "일괄 개정 중 에러가 발생하였습니다.\n관리자에게 문의하세요.");
			map.put("url", "/Windchill/plm/epm/reviseCadData");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> approvalEpmAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalContract contract = null;
		String name = (String) param.get("name");
		String comment = (String) param.get("comment");
		List<String> epmOids = (List<String>) param.get("epmOids");
		List<String> description = (List<String>) param.get("description");

		Transaction trs = new Transaction();
		try {
			trs.start();

			contract = ApprovalContract.newApprovalContract();
			contract.setName(name);
			contract.setDescription(comment);
			contract.setStartTime(new Timestamp(new Date().getTime()));
			contract.setState(WorkspaceHelper.LINE_APPROVING);
			contract = (ApprovalContract) PersistenceHelper.manager.save(contract);

			for (int i = 0; i < epmOids.size(); i++) {
				String oid = (String) epmOids.get(i);
				String desc = (String) description.get(i);

				EPMDocument epm = (EPMDocument) rf.getReference(oid).getObject();

				epm.setDescription(desc);
				PersistenceServerHelper.manager.update(epm);

				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, epm);
				PersistenceHelper.manager.save(aLink);
			}

			WorkspaceHelper.service.submitApp(contract, param);

			map.put("result", SUCCESS);
			map.put("reload", true);
			map.put("msg", "도면 결재가 등록 되었습니다.");
			map.put("url", "/Windchill/plm/approval/listApproval");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("reload", false);
			map.put("msg", "도면 결재 등록 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			// map.put("url", "/Windchill/plm/epm/approvalEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createPartCodeAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalContract contract = null;
		String name = (String) param.get("name");
		List<String> epmOids = (List<String>) param.get("epmOids");
		List<String> description = (List<String>) param.get("description");
		boolean self = (boolean) param.get("self");
		Transaction trs = new Transaction();
		try {
			trs.start();

			contract = ApprovalContract.newApprovalContract();
			contract.setName(name);
			contract.setStartTime(new Timestamp(new Date().getTime()));
			contract.setState(WorkspaceHelper.LINE_APPROVING);
			contract = (ApprovalContract) PersistenceHelper.manager.save(contract);

			for (int i = 0; i < epmOids.size(); i++) {
				String oid = (String) epmOids.get(i);
				String desc = (String) description.get(i);
				System.out.println("desc=" + desc);

				EPMDocument epm = (EPMDocument) rf.getReference(oid).getObject();

				epm.setDescription(desc);
				PersistenceServerHelper.manager.update(epm);

				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, epm);
				PersistenceHelper.manager.save(aLink);
			}

			if (self) {
				WorkspaceHelper.service.selfApproval((Persistable) contract);
			}

			map.put("result", SUCCESS);
			map.put("reload", true);
			map.put("msg", "코드 생성이 되었습니다.");
			map.put("url", "/Windchill/plm/part/createCode");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("reload", false);
			map.put("msg", "코드 생성 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> sendDWGAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		EPMDocument epm = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();

				// boolean isExist = setYCode(PART_SPEC, part);

				String PART_SPEC = "";
				String cadType = epm.getAuthoringApplication().toString();
				String ver = epm.getVersionIdentifier().getSeries().getValue();
				if ("PROE".equals(cadType)) {
					PART_SPEC = IBAUtils.getStringValue(epm, "DWG_NO") + "-" + StringUtils.numberFormat(ver, "000");
				} else if ("ACAD".equals(cadType)) {
					PART_SPEC = IBAUtils.getStringValue(epm, "DWG_No") + "-" + StringUtils.numberFormat(ver, "000");
				}

				// 첨부 파일 전송...
				String dir = StandardErpService.epmOutputDir + File.separator + PART_SPEC;
				File directory = new File(dir);
				if (!directory.exists()) {
					directory.mkdirs();
				}

				if ("ACAD".equals(cadType)) {
					QueryResult result = ContentHelper.service.getContentsByRole(epm, ContentRoleType.PRIMARY);
					if (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();

						String ext = FileUtil.getExtension(epm.getCADName());

						if (!ext.equalsIgnoreCase("dwg") && !ext.equalsIgnoreCase("zip")) {
							continue;
						}

						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
						File write = new File(directory + File.separator + PART_SPEC + ".dwg");
						FileOutputStream fos = new FileOutputStream(write);
						int j = 0;
						while ((j = is.read(buffer, 0, 10240)) > 0) {
							fos.write(buffer, 0, j);
						}
						fos.close();
						is.close();
					}
				} else {

					Representation representation = PublishUtils.getRepresentation(epm);

					if (representation != null) {
						QueryResult result = ContentHelper.service.getContentsByRole(representation,
								ContentRoleType.ADDITIONAL_FILES);
						while (result.hasMoreElements()) {
							ApplicationData data = (ApplicationData) result.nextElement();

							String ext = FileUtil.getExtension(data.getFileName());

							if (!ext.equalsIgnoreCase("dwg") && !ext.equalsIgnoreCase("zip")) {
								continue;
							}

							byte[] buffer = new byte[10240];
							InputStream is = ContentServerHelper.service.findLocalContentStream(data);
							File write = new File(directory + File.separator + PART_SPEC + ".dwg");
							FileOutputStream fos = new FileOutputStream(write);
							int j = 0;
							while ((j = is.read(buffer, 0, 10240)) > 0) {
								fos.write(buffer, 0, j);
							}
							fos.close();
							is.close();
						}
					}
				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "ERP서버로 전송이 완료 되었습니다.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "ERP서버로 전송 중 에러가 발생하였습니다." + systemMsg);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> sendPDFAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		List<String> list = (List<String>) param.get("list");
		EPMDocument epm = null;
		ReferenceFactory rf = new ReferenceFactory();
		Transaction trs = new Transaction();
		try {
			trs.start();

			for (String oid : list) {
				epm = (EPMDocument) rf.getReference(oid).getObject();

				String PART_SPEC = "";
				String cadType = epm.getAuthoringApplication().toString();
				String ver = epm.getVersionIdentifier().getSeries().getValue();
				if ("PROE".equals(cadType)) {
					PART_SPEC = IBAUtils.getStringValue(epm, "DWG_NO") + "-" + StringUtils.numberFormat(ver, "000");
				} else if ("ACAD".equals(cadType)) {
					PART_SPEC = IBAUtils.getStringValue(epm, "DWG_No") + "-" + StringUtils.numberFormat(ver, "000");
				}

				// 첨부 파일 전송...
				String dir = StandardErpService.epmOutputDir + File.separator + PART_SPEC;
				File directory = new File(dir);
				if (!directory.exists()) {
					directory.mkdirs();
				}

				Representation representation = PublishUtils.getRepresentation(epm);

				if (representation != null) {
					QueryResult result = ContentHelper.service.getContentsByRole(representation,
							ContentRoleType.SECONDARY);
					while (result.hasMoreElements()) {
						ApplicationData data = (ApplicationData) result.nextElement();

						String ext = FileUtil.getExtension(data.getFileName());

						if (!ext.equalsIgnoreCase("pdf") && !ext.equalsIgnoreCase("zip")) {
							continue;
						}

						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(data);
						File write = new File(directory + File.separator + PART_SPEC + ".pdf");
						FileOutputStream fos = new FileOutputStream(write);
						int j = 0;
						while ((j = is.read(buffer, 0, 10240)) > 0) {
							fos.write(buffer, 0, j);
						}
						fos.close();
						is.close();
					}
				}
			}

			map.put("result", SUCCESS);
			map.put("msg", "ERP서버로 전송이 완료 되었습니다.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "ERP서버로 전송 중 에러가 발생하였습니다." + systemMsg);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> createViewerAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ViewerData data = null;
		String name = (String) param.get("name");
		String number = (String) param.get("number");
		String fileName = (String) param.get("fileName");
		Transaction trs = new Transaction();
		try {
			trs.start();

			Ownership ownership = Ownership.newOwnership(SessionHelper.manager.getPrincipal());

			data = ViewerData.newViewerData();
			data.setName(name);
			data.setNumber(number);
			data.setFileName(fileName);
			data.setOwnership(ownership);

			data = (ViewerData) PersistenceHelper.manager.save(data);

			ContentUtils.updatePrimary(param, data);
			// ContentUtils.updateContents(param, data);

			map.put("result", SUCCESS);
			map.put("msg", "뷰어가 " + CREATE_OK);
			map.put("url", "/Windchill/plm/epm/listViewer");
			map.put("reload", true);
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "뷰어 생성 " + CREATE_FAIL);
			map.put("reload", false);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

	@Override
	public Map<String, Object> approvalModifyEpmAction(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		ReferenceFactory rf = new ReferenceFactory();
		ApprovalContract contract = null;
		String oids = (String) param.get("oid");
		String moid = (String) param.get("moid");
		String name = (String) param.get("name");
		String comment = (String) param.get("comment");
		List<String> epmOids = (List<String>) param.get("epmOids");
		List<String> description = (List<String>) param.get("description");

		ApprovalLine line = null;

		Transaction trs = new Transaction();
		try {
			trs.start();

			line = (ApprovalLine) rf.getReference(moid).getObject();

			ApprovalMaster master = line.getMaster();
			master.setName(name);

			PersistenceHelper.manager.modify(master);

			ApprovalMasterViewData dd = new ApprovalMasterViewData(master);

			ArrayList<ApprovalLine> appLines = dd.appLines;
			int idx = 0;
			for (ApprovalLine lines : appLines) {
				lines.setName(name);
				if (idx == 0) {
					lines.setDescription(comment);
				}
				PersistenceHelper.manager.modify(lines);
			}

			ArrayList<ApprovalLine> receiveLines = dd.receiveLines;
			for (ApprovalLine lines : receiveLines) {
				lines.setName(name);
				PersistenceHelper.manager.modify(lines);
			}

			ArrayList<ApprovalLine> agreeLines = dd.agreeLines;
			for (ApprovalLine lines : agreeLines) {
				lines.setName(name);
				PersistenceHelper.manager.modify(lines);
			}

			contract = (ApprovalContract) rf.getReference(oids).getObject();
			contract.setName(name);
			contract.setDescription(comment);
//			contract.setStartTime(new Timestamp(new Date().getTime()));
//			contract.setState(ApprovalHelper.LINE_APPROVING);
			contract = (ApprovalContract) PersistenceHelper.manager.save(contract);

			QueryResult result = PersistenceHelper.manager.navigate(contract, "persist",
					ApprovalContractPersistableLink.class, false);
			while (result.hasMoreElements()) {
				ApprovalContractPersistableLink link = (ApprovalContractPersistableLink) result.nextElement();
				PersistenceHelper.manager.delete(link);
			}

			for (int i = 0; i < epmOids.size(); i++) {
				String oid = (String) epmOids.get(i);
				String desc = (String) description.get(i);

				EPMDocument epm = (EPMDocument) rf.getReference(oid).getObject();

				epm.setDescription(desc);
				PersistenceServerHelper.manager.update(epm);

				ApprovalContractPersistableLink aLink = ApprovalContractPersistableLink
						.newApprovalContractPersistableLink(contract, epm);
				PersistenceHelper.manager.save(aLink);
			}

//			ApprovalHelper.service.submitApp(contract, param);

			map.put("result", SUCCESS);
			map.put("reload", true);
			map.put("msg", "도면 결재가 수정 되었습니다.");
			map.put("url", "/Windchill/plm/approval/listApproval");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("reload", false);
			map.put("msg", "도면 결재 수정 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
			// map.put("url", "/Windchill/plm/epm/approvalEpm");
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
		}
		return map;
	}

}
