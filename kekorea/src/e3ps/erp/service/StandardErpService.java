package e3ps.erp.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.ptc.wvs.server.util.PublishUtils;

import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.common.db.DBCPManager;
import e3ps.common.db.DBConnectionManager;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.epm.service.EpmHelper;
import e3ps.part.UnitBom;
import e3ps.part.UnitBomPartLink;
import e3ps.part.UnitSubPart;
import e3ps.part.beans.PartViewData;
import e3ps.part.service.PartHelper;
import e3ps.project.Project;
import e3ps.project.output.Output;
import e3ps.project.output.OutputDocumentLink;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentItem;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.fc.ReferenceFactory;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.representation.Representation;
import wt.services.StandardManager;
import wt.session.SessionHelper;
import wt.util.FileUtil;
import wt.util.WTException;

public class StandardErpService extends StandardManager implements ErpService {

	// private static final String erpOutputDir = "P:\\";

	// 절대 경로..
//	public static final String erpOutputDir = File.separator + "\\192.168.1.60\\Project\\Output\\PLM";

	public static final String erpOutputDir = File.separator + "\\Erp-app\\plm2";

	public static final String epmOutputDir = File.separator + "\\Erp-app\\plm";

	private static final long serialVersionUID = 3374634627786694864L;

	public static final String erpName = "erp";
//	public static final String erpName = "erpdev";

	public static StandardErpService newStandardErpService() throws WTException {
		StandardErpService instance = new StandardErpService();
		instance.initialize();
		return instance;
	}

	@Override
	public Map<String, Object> sendOutputToERP(WTDocument document) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();

			con = DBCPManager.getConnection(erpName);

			st = con.createStatement();

//			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();

			QueryResult result = PersistenceHelper.manager.navigate(document, "output", OutputDocumentLink.class);

			System.out.println("산출물 전송reuslt=" + result.size());

			while (result.hasMoreElements()) {
				Output output = (Output) result.nextElement();
				Project project = output.getProject();

				System.out.println("산출물 전송project=" + project.getKekNumber());
				// String loc = document.getLocation();
				String loc = output.getLocation();
				if (!loc.contains("작업지시서") && !loc.contains("설비사양서") && !loc.contains("SW_지시서")) {
					System.out.println("산 출물 아님..");
					continue;
				}

				StringBuffer sql = new StringBuffer();

				sql.append("INSERT INTO KEK_TPJTOutputRptDo_IF (SEQ, StdNo, PJTSeq, StdReportSeq, RegDate, USERID, ");
				sql.append("REMARK, CREATE_TIME)");

				int SEQ = ErpHelper.manager.getMaxSeq("KEK_TPJTOutputRptDo_IF");
				sql.append(" VALUES('" + SEQ + "', ");

				String StdNo = document.getNumber();

				String t = project.getPType();

				int StdReportSeq = 1;
				if (loc.contains("작업지시서") || loc.contains("SW_지시서")) {
					StdReportSeq = 47;
					StdNo = "INS-" + StdNo;
				} else if (loc.contains("설비사양서")) {
					if (t.equals("개조")) {
						StdReportSeq = 53;
						StdNo = "REQ1-" + StdNo;
					} else if (t.equals("양산")) {
						StdReportSeq = 54;
						StdNo = "REQ2-" + StdNo;
					}
				}

				sql.append("'" + StdNo + "', ");

				String kekNumber = project.getKekNumber();
				String[] values = ErpHelper.manager.getKEK_VPJTProject(kekNumber);

				if (StringUtils.isNull(values[0])) {
					values[0] = "0";
				}

				int PJTSeq = Integer.parseInt(values[0]);
				sql.append("'" + PJTSeq + "', ");

				// 81(46), 84(52)
				// 작업지시서.. 개조사양서
				sql.append("'" + StdReportSeq + "', ");

				String RegDate = document.getCreateTimestamp().toString().substring(0, 10).replaceAll("-", "");
				sql.append("'" + RegDate + "', ");

				// String USERID = user.getName();
				String USERID = document.getCreatorName();
				sql.append("'" + USERID + "', ");

				String REMARK = StringUtils.replaceToValue(document.getDescription());
				sql.append("'" + REMARK + "', ");

				String CREATE_TIME = new Timestamp(new Date().getTime()).toString().substring(0, 16);
				sql.append("'" + CREATE_TIME + "');");

				System.out.println("sendOutputToERP sql = " + sql.toString());
				st.executeUpdate(sql.toString());

				sendOutputToERPFile(document, project);

				StringBuffer sb = new StringBuffer();
				sb.append("EXEC KEK_SPLMOutputRptDOProc ");
				sb.append("'" + StdNo + "'");
				st.executeUpdate(sb.toString());
			}

			map.put("msg", "ERP서버로 전송이 완료 되었습니다.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			DBCPManager.freeConnection(con, st, rs);
			e.printStackTrace();
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	@Override
	public String sendPartToERP(WTPart part) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String ycode = null;
		try {
			trs.start();

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			int keyIdx = 1;
			sql.append(
					"INSERT INTO KEK_TDAItem_IF (SEQ, PART_NAME, PART_SPEC, UNITSEQ, MAKERSEQ, USERID, PRICE, CURRSEQ, CUSTSEQ, ");
			sql.append("CREATE_DATE,");
			String addName = "AddFileName" + keyIdx;
			String addExt = "AddFileExt" + keyIdx;
			String addSize = "AddFileSize" + keyIdx;
			sql.append("" + addName + ", " + addExt + ", " + addSize + ", ");
			sql.append("IsCode)");

			int SEQ = ErpHelper.manager.getMaxSeq("KEK_TDAItem_IF");
			sql.append(" VALUES('" + SEQ + "', ");

			String PART_NAME = IBAUtils.getStringValue(part, "NAME_OF_PARTS");
			String PART_SPEC = IBAUtils.getStringValue(part, "DWG_NO");

			sql.append("'" + PART_NAME + "', ");
			sql.append("'" + PART_SPEC + "', ");

			String UNITSEQ = StringUtils
					.replaceToValue(ErpHelper.manager.getKEK_VDAUnit(IBAUtils.getStringValue(part, "STD_UNIT"))[0]);
			sql.append("'" + UNITSEQ + "', ");

			String MAKERSEQ = StringUtils
					.replaceToValue(ErpHelper.manager.getKEK_VDAMAKER(IBAUtils.getStringValue(part, "MAKER"))[0]);
			sql.append("'" + MAKERSEQ + "', ");

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String USERID = user.getName();
			sql.append("'" + USERID + "', ");

			int PRICE = IBAUtils.getIntegerValue(part, "PRICE");
			sql.append("'" + PRICE + "', ");

			String CURRSEQ = StringUtils
					.replaceToValue(ErpHelper.manager.getKEK_VDACURR(IBAUtils.getStringValue(part, "CURRNAME"))[0]);
			sql.append("'" + CURRSEQ + "', ");

			String CUSTSEQ = StringUtils
					.replaceToValue(ErpHelper.manager.getKEK_VDAPURCUST(IBAUtils.getStringValue(part, "CUSTNAME"))[0]);
			sql.append("'" + CUSTSEQ + "', ");

			String CREATE_DATE = new Timestamp(new Date().getTime()).toString();
			sql.append("'" + CREATE_DATE + "', ");

			String[] pp = ContentUtils.getPrimary(part);
			String AddFileName = "";
			System.out.println("===oo2=" + pp[2]);
			if (!StringUtils.isNull(pp[2])) {
				AddFileName = pp[2];
			}

			System.out.println("AddFileName=" + AddFileName);

			sql.append("'" + AddFileName + "', ");

			String AddFileExt = "";
			if (!StringUtils.isNull(pp[2])) {
				AddFileExt = FileUtil.getExtension(pp[2]);
			}
			sql.append("'" + AddFileExt + "', ");

			String size = "";
			if (!StringUtils.isNull(pp[2])) {
				size = pp[6];
			}

			sql.append("'" + size + "', ");

			String IsCode = "Y";
			sql.append("'" + IsCode + "');");

			System.out.println("sendPartToERP sql = " + sql.toString());

			st.executeUpdate(sql.toString());

			// 도면전송
			// 첨부 파일 전송...
			String dir = epmOutputDir + File.separator + PART_SPEC;
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}

//			ReferenceFactory rf = new ReferenceFactory();

			QueryResult result = ContentHelper.service.getContentsByRole(part, ContentRoleType.PRIMARY);

			while (result.hasMoreElements()) {
				ApplicationData adata = (ApplicationData) result.nextElement();
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(adata);
				File write = new File(directory + File.separator + adata.getFileName());
				FileOutputStream fos = new FileOutputStream(write);
				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
			}
			//
			StringBuffer sb = new StringBuffer();
			sb.append("EXEC KEK_SPLMItemIF");
			st.executeUpdate(sb.toString());

			ycode = setYCode(PART_SPEC, part);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			DBCPManager.freeConnection(con, st, rs);
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			DBCPManager.freeConnection(con, st, rs);
		}
		return ycode;
	}

	@Override
	public Map<String, Object> sendPartListToERP(PartListMaster master) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			// 작번 개수 만큼 전송..

			QueryResult result = PersistenceHelper.manager.navigate(master, "project", PartListMasterProjectLink.class);

			if (result.size() == 0) {
				DBCPManager.freeConnection(con, st, rs);
				trs.commit();
				trs = null;
				return map;
			}

			while (result.hasMoreElements()) {

				Project project = (Project) result.nextElement();
				String kekNumber = project.getKekNumber();

				String engType = master.getEngType();

				ArrayList<PartListData> list = PartListMasterHelper.manager.getPartListData(master);
				String DISNO = "WANT_" + master.getNumber();
				for (PartListData data : list) {
					StringBuffer sql = new StringBuffer();

					sql.append(
							"INSERT INTO KEK_TPJTBOM_IF (SEQ, DISNO, REGDATE, PJTSEQ, DESIGNTYPE, REMARKM, USERID, ");
					sql.append("ACCDATE, LOTSEQ, ITEMSEQ, MAKERSEQ, CUSTSEQ, UNITSEQ, CURRSEQ, QTY, EXRATE, ");
					sql.append("AMT, REMARK, UMSUPPLYTYPE, PRICE, CREATE_TIME, APPUSERID )");

					int SEQ = ErpHelper.manager.getMaxSeq("KEK_TPJTBOM_IF");
					sql.append(" VALUES('" + SEQ + "', ");

					sql.append("'" + DISNO + "', ");

//					String REGDATE = master.getPersistInfo().getCreateStamp().toString().substring(0, 10).trim();

					String ACCDATE = data.getPartListDate() != null ? data.getPartListDate()
							: new Timestamp(new Date().getTime()).toString().substring(0, 10).trim();

					sql.append("'" + ACCDATE.replaceAll("-", "") + "', ");

					String PJTSEQ = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VPJTProject(kekNumber)[0]);

					System.out.println("====" + PJTSEQ + "==number=" + kekNumber);

					sql.append("'" + PJTSEQ + "', ");

					if (engType.contains("기계")) {
						engType = "기계";
					} else if (engType.contains("전기")) {
						engType = "전기";
					}

					String DESIGNTYPE = StringUtils
							.replaceToValue(ErpHelper.manager.getKEK_VDADesignType(engType + "설계")[0]);
					sql.append("'" + DESIGNTYPE + "', ");

					// 특이사항?
					String REMARKM = StringUtils.replaceToValue(master.getDescription());
					sql.append("'" + REMARKM + "', ");

					// String USERID = user.getName();
					String USERID = master.getCreatorName();
					sql.append("'" + USERID + "', ");

					// 수배일자 ?? 접수일자

					System.out.println("datateuim=" + ACCDATE);
					sql.append("'" + ACCDATE.replaceAll("-", "") + "', ");

					String lotNo = data.getLotNo().trim();
					String LOTSEQ = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VDALotNo(lotNo)[0]);
					sql.append("'" + LOTSEQ + "', ");

					String yCode = data.getPartNo().trim();

					String ITEMSEQ = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VDAItem(yCode)[0]);
					sql.append("'" + ITEMSEQ + "', ");

					String makerName = data.getMaker();
					String MAKERSEQ = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VDAMAKER(makerName)[0]);
					sql.append("'" + MAKERSEQ + "', ");

					String customer = data.getCustomer();
					String CUSTSEQ = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VDAPURCUST(customer)[0]);
					sql.append("'" + CUSTSEQ + "', ");

					String unitName = data.getUnit();
					String UNITSEQ = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VDAUnit(unitName)[0]);
					sql.append("'" + UNITSEQ + "', ");

					String currency = data.getCurrency();
					String CURRSEQ = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VDACURR(currency)[0]);
					sql.append("'" + CURRSEQ + "', ");

					String QTY = data.getQuantity();

					if (StringUtils.isNull(QTY)) {
						QTY = "1";
					}

					sql.append("'" + QTY.replaceAll(",", "") + "', ");

					String EXRATE = data.getExchangeRate();
					sql.append("'" + EXRATE + "', ");

					double AMT = data.getWon();
					int amt = (int) AMT;
					sql.append("'" + amt + "', ");

					String REMARK = StringUtils.replaceToValue(data.getNote());
					sql.append("'" + REMARK + "', ");

					String classification = data.getClassification();
					String UMSUPPLYTYPE = StringUtils
							.replaceToValue(ErpHelper.manager.getKEK_VDASupplyType(classification)[0]);
					sql.append("'" + UMSUPPLYTYPE + "', ");

					double PRICE = Double.parseDouble(data.getPrice().replace(",", ""));
					int price = (int) PRICE;
					sql.append("'" + price + "', ");

//					String CREATE_TIME = new Timestamp(new Date().getTime()).toString();
//					String ACCDATE = data.getPartListDate() != null ? data.getPartListDate()
//							: new Timestamp(new Date().getTime()).toString().substring(0, 10).trim();
					sql.append("'" + ACCDATE + "', ");
//2015-*1001-
					ApprovalMaster appMaster = null;
					ApprovalMasterViewData appMaserVD = null;
					appMaster = ApprovalHelper.manager.getMaster(master);
					appMaserVD = new ApprovalMasterViewData(appMaster);
					ArrayList<ApprovalLine> agreeLines = appMaserVD.appLines;
					String APPUSERID = "";
					for (ApprovalLine agreeLine : agreeLines) {
						ApprovalLineViewData datas = new ApprovalLineViewData(agreeLine);
						if (datas.role.equals(ApprovalHelper.WORKING_SUBMIT)) {
							continue;
						}
						ApprovalLine appline = datas.approvalLine;
						APPUSERID = appline.getCompleteUserID();
					}
					sql.append("'" + APPUSERID + "');");

					System.out.println("sendPartListToERP sql = " + sql.toString());
					st.executeUpdate(sql.toString());

				} // end for
				StringBuffer sb = new StringBuffer();
				sb.append("EXEC KEK_SPLMBOMIF '" + DISNO + "'");
				st.executeUpdate(sb.toString());
			} // end while
			map.put("result", SUCCESS);
			map.put("msg", "ERP서버로 전송이 완료 되었습니다.");
			trs.commit();
			trs = null;
		} catch (Exception e) {
			map.put("result", FAIL);
			map.put("msg", "ERP서버로 전송 중 에러가 발생하였습니다." + systemMsg);
			e.printStackTrace();
			DBCPManager.freeConnection(con, st, rs);
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	@Override
	public Map<String, Object> sendOutputToERPFile(WTDocument document, Project project) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append(
					"INSERT INTO KEK_TPJTOutputRptDoFile_IF (SEQ, StdNo, RPTFILENAME, CREATE_TIME, FileExt, FileSize)");

			int SEQ = ErpHelper.manager.getMaxSeq("KEK_TPJTOutputRptDoFile_IF");
			sql.append(" VALUES('" + SEQ + "', ");

			String StdNo = document.getNumber();

			String loc = document.getLocation();
			String t = project.getPType();

//			int StdReportSeq = 1;
			if (loc.contains("작업지시서") || loc.contains("SW_지시서")) {
//				StdReportSeq = 47;
				StdNo = "INS-" + StdNo;
			} else if (loc.contains("설비사양서")) {
				if (t.equals("개조")) {
//					StdReportSeq = 53;
					StdNo = "REQ1-" + StdNo;
				} else if (t.equals("양산")) {
//					StdReportSeq = 54;
					StdNo = "REQ2-" + StdNo;
				}
			}
			sql.append("'" + StdNo + "', ");

			String RPTFILENAME = ContentUtils.getPrimary(document)[2];
			sql.append("'" + RPTFILENAME + "', ");

			String CREATE_TIME = new Timestamp(new Date().getTime()).toString();
			sql.append("'" + CREATE_TIME + "', ");

			String FileExt = FileUtil.getExtension(RPTFILENAME);
			sql.append("'" + FileExt + "', ");

			long FileSize = Long.parseLong(ContentUtils.getPrimary(document)[6]);
			sql.append("'" + FileSize + "');");

			System.out.println("sendOutputToERPFile sql = " + sql.toString());
			st.executeUpdate(sql.toString());

			// 첨부 파일 전송...
			String dir = erpOutputDir;
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.PRIMARY);
			while (qr.hasMoreElements()) {
				ApplicationData adata = (ApplicationData) qr.nextElement();

				// String version = document.getVersionIdentifier().getSeries().getValue() + "."
				// + document.getIterationIdentifier().getSeries().getValue();
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(adata);
				File write = new File(directory + File.separator + adata.getFileName());
				FileOutputStream fos = new FileOutputStream(write);
				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
			}

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			DBCPManager.freeConnection(con, st, rs);
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	@Override
	public String setYCode(String number, Persistable per) throws WTException {
		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
//		boolean isExist = false;
		String yCode = "";
		try {
			trs.start();

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT PART_NO FROM KEK_TDAItem_IF WHERE PART_SPEC='" + number + "'");

//			String ISEXISTS = "";

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				yCode = (String) rs.getString(1);
				// ISEXISTS = (String) rs.getString(2);
				// if ("1".equals(ISEXISTS)) {
				// isExist = true;
				// }
			}

			System.out.println("y=" + yCode);

			if (!StringUtils.isNull(yCode)) {

				if (per instanceof WTPart) {
					WTPart pp = (WTPart) per;
					IBAUtils.deleteIBA(pp, "PART_CODE", "s");
					IBAUtils.createIBA(pp, "s", "PART_CODE", yCode);

					EPMDocument ee = PartHelper.manager.getEPMDocument(pp);

					if (ee != null) {
						IBAUtils.deleteIBA(ee, "PART_CODE", "s");
						IBAUtils.createIBA(ee, "s", "PART_CODE", yCode);

						EPMDocument epm = EpmHelper.manager.getEPM2D(ee);

						if (epm != null) {
							IBAUtils.deleteIBA(epm, "PART_CODE", "s");
							IBAUtils.createIBA(epm, "s", "PART_CODE", yCode);
						}
					}
				}
			}
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			DBCPManager.freeConnection(con, st, rs);
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			DBCPManager.freeConnection(con, st, rs);
		}
		return yCode;
	}

	@Override
	public String sendSpecPartToERP(WTPart part) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String code = null;
		try {
			trs.start();

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			QueryResult result = PersistenceHelper.manager.navigate(part, "document", WTDocumentWTPartLink.class);

			ArrayList<WTDocument> list = new ArrayList<WTDocument>();
			while (result.hasMoreElements()) {
				WTDocument dd = (WTDocument) result.nextElement();
				list.add(dd);
			}

			int keyIdx = 1;
			sql.append(
					"INSERT INTO KEK_TDAItem_IF (SEQ, PART_NAME, PART_SPEC, UNITSEQ, MAKERSEQ, USERID, PRICE, CURRSEQ, CUSTSEQ, ");
			sql.append("CREATE_DATE,");
			for (int k = 0; k < list.size(); k++) {
				String addName = "AddFileName" + keyIdx;
				String addExt = "AddFileExt" + keyIdx;
				String addSize = "AddFileSize" + keyIdx;
				sql.append("" + addName + ", " + addExt + ", " + addSize + ", ");
				keyIdx++;
			}
			sql.append("IsCode)");

			int SEQ = ErpHelper.manager.getMaxSeq("KEK_TDAItem_IF");
			sql.append(" VALUES('" + SEQ + "', ");

			String PART_NAME = IBAUtils.getStringValue(part, "NAME_OF_PARTS");
			String PART_SPEC = IBAUtils.getStringValue(part, "DWG_NO");

			sql.append("'" + PART_NAME + "', ");
			sql.append("'" + PART_SPEC + "', ");

			String UNITSEQ = StringUtils
					.replaceToValue(ErpHelper.manager.getKEK_VDAUnit(IBAUtils.getStringValue(part, "STD_UNIT"))[0]);
			sql.append("'" + UNITSEQ + "', ");

			String MAKERSEQ = StringUtils
					.replaceToValue(ErpHelper.manager.getKEK_VDAMAKER(IBAUtils.getStringValue(part, "MAKER"))[0]);
			sql.append("'" + MAKERSEQ + "', ");

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String USERID = user.getName();
			sql.append("'" + USERID + "', ");

			int PRICE = IBAUtils.getIntegerValue(part, "PRICE");
			sql.append("'" + PRICE + "', ");

			String CURRSEQ = StringUtils
					.replaceToValue(ErpHelper.manager.getKEK_VDACURR(IBAUtils.getStringValue(part, "CURRNAME"))[0]);
			sql.append("'" + CURRSEQ + "', ");

			String CUSTSEQ = StringUtils
					.replaceToValue(ErpHelper.manager.getKEK_VDAPURCUST(IBAUtils.getStringValue(part, "CUSTNAME"))[0]);
			sql.append("'" + CUSTSEQ + "', ");

			String CREATE_DATE = new Timestamp(new Date().getTime()).toString();
			sql.append("'" + CREATE_DATE + "', ");

			for (int k = 0; k < list.size(); k++) {
				WTDocument doc = (WTDocument) list.get(k);
				String[] pp = ContentUtils.getPrimary(doc);
				String AddFileName = pp[2];
				sql.append("'" + AddFileName + "', ");

				String AddFileExt = FileUtil.getExtension(pp[2]);
				sql.append("'" + AddFileExt + "', ");

				String size = pp[6];
				sql.append("'" + size + "', ");
			}

			String IsCode = "Y";
			sql.append("'" + IsCode + "');");

			System.out.println("sendSpecPartToERP sql = " + sql.toString());

			st.executeUpdate(sql.toString());

			// 도면전송
			// 첨부 파일 전송...
			String dir = epmOutputDir + File.separator + PART_SPEC;
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			ReferenceFactory rf = new ReferenceFactory();
			for (int a = 0; a < list.size(); a++) {
				WTDocument document = (WTDocument) list.get(a);
				String aoid = ContentUtils.getPrimary(document)[1];
				ApplicationData adata = (ApplicationData) rf.getReference(aoid).getObject();
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(adata);
				File write = new File(directory + File.separator + adata.getFileName());
				FileOutputStream fos = new FileOutputStream(write);
				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
			}
			//
			StringBuffer sb = new StringBuffer();
			sb.append("EXEC KEK_SPLMItemIF");
			st.executeUpdate(sb.toString());

			code = setYCode(PART_SPEC, part);

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			DBCPManager.freeConnection(con, st, rs);
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			DBCPManager.freeConnection(con, st, rs);
		}
		return code;
	}

	@Override
	public Map<String, Object> sendPartToERP(WTPart part, String rev) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			int keyIdx = 1;
			sql.append("INSERT INTO KEK_TDAItem_IF (SEQ, PART_NAME, PART_SPEC, UNITSEQ, MAKERSEQ, USERID, PRICE, ");
			sql.append("CREATE_DATE,");
			String addName = "AddFileName" + keyIdx;
			String addExt = "AddFileExt" + keyIdx;
			String addSize = "AddFileSize" + keyIdx;
			sql.append("" + addName + ", " + addExt + ", " + addSize + ", ");
			sql.append("IsCode)");

			int SEQ = ErpHelper.manager.getMaxSeq("KEK_TDAItem_IF");
			sql.append(" VALUES('" + SEQ + "', ");

			String ver = part.getVersionIdentifier().getSeries().getValue();
			String PART_NAME = IBAUtils.getStringValue(part, "NAME_OF_PARTS");
			String PART_SPEC = IBAUtils.getStringValue(part, "DWG_NO") + "-" + StringUtils.numberFormat(ver, "000");

			sql.append("'" + PART_NAME + "', ");
			sql.append("'" + PART_SPEC + "', ");

			String UNITSEQ = StringUtils
					.replaceToValue(ErpHelper.manager.getKEK_VDAUnit(IBAUtils.getStringValue(part, "STD_UNIT"))[0]);
			sql.append("'" + UNITSEQ + "', ");

			String MAKERSEQ = StringUtils
					.replaceToValue(ErpHelper.manager.getKEK_VDAMAKER(IBAUtils.getStringValue(part, "MAKER"))[0]);
			sql.append("'" + MAKERSEQ + "', ");

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String USERID = user.getName();
			sql.append("'" + USERID + "', ");

			int PRICE = IBAUtils.getIntegerValue(part, "PRICE");
			sql.append("'" + PRICE + "', ");

			String CREATE_DATE = new Timestamp(new Date().getTime()).toString();
			sql.append("'" + CREATE_DATE + "', ");

			EPMDocument ee = PartHelper.manager.getEPMDocument(part);
			EPMDocument epm = EpmHelper.manager.getEPM2D(ee);
			String[] pp = ContentUtils.getPDF(epm);
			String AddFileName = "";
			if (pp[2] != null) {
				AddFileName = PART_SPEC + ".pdf";
			}
			sql.append("'" + AddFileName + "', ");

			String AddFileExt = "";
			if (pp[2] != null) {
				AddFileExt = FileUtil.getExtension(pp[2]);
			}
			sql.append("'" + AddFileExt + "', ");
			String size = "0";
			if (pp[8] != null) {
				size = pp[8];
			}
			sql.append("'" + size + "', ");

			String IsCode = "Y";
			sql.append("'" + IsCode + "');");

			System.out.println("sendPartToERP sql = " + sql.toString());

			st.executeUpdate(sql.toString());

			StringBuffer sb = new StringBuffer();
			sb.append("EXEC KEK_SPLMItemIF");
			st.executeUpdate(sb.toString());

			setYCode(PART_SPEC, part);

			// System.out.println(" isExist=" + isExist);

			// if (isExist) {
			// System.out.println("여ㅑ기 실행.......");
			// map.put("result", SUCCESS);
			// map.put("msg", "ERP 서버에 이미 존재하는 규격입니다.");
			// trs.commit();
			// trs = null;
			// return map;
			// }

			// 도면전송
			// 첨부 파일 전송...
			// if (!isExist) {
			String dir = StandardErpService.epmOutputDir + File.separator + PART_SPEC;
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			EPMDocument latest = (EPMDocument) CommonUtils.getLatestVersion(epm);

			Representation representation = PublishUtils.getRepresentation(latest);

			if (representation != null) {
				// QueryResult result = ContentHelper.service.getContentsByRole(representation,
				// ContentRoleType.ADDITIONAL_FILES);
				QueryResult result = ContentHelper.service.getContentsByRole(representation, ContentRoleType.SECONDARY);
				while (result.hasMoreElements()) {
					ContentItem item = (ContentItem) result.nextElement();
					if (item instanceof ApplicationData) {
						ApplicationData adata = (ApplicationData) item;
						String ext = FileUtil.getExtension(adata.getFileName());
						if (!ext.equalsIgnoreCase("pdf")) {
							continue;
						}
						byte[] buffer = new byte[10240];
						InputStream is = ContentServerHelper.service.findLocalContentStream(adata);
						// File write = new File(directory + File.separator + adata.getFileName());
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
			// }

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			DBCPManager.freeConnection(con, st, rs);
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	@Override
	public void sendUnitBomToERP(UnitBom unitBom) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sb = new StringBuffer();

			sb.append(
					"INSERT INTO KEK_TDAItem_IF (SEQ, PART_NAME, PART_SPEC, UNITSEQ, MAKERSEQ, USERID,  CURRSEQ, CUSTSEQ, ");
			sb.append("CREATE_DATE, IsCode)");

			int SEQS = ErpHelper.manager.getMaxSeq("KEK_TDAItem_IF");

			System.out.println("##sendUnitBomToERP SEQS= " + SEQS + "//");

			sb.append(" VALUES('" + SEQS + "', ");

			String PART_NAMES = unitBom.getPartName().trim();
			String PART_SPECS = unitBom.getSpec().trim();

			sb.append("'" + PART_NAMES + "', ");
			sb.append("'" + PART_SPECS + "', ");

			String UNITSEQS = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VDAUnit(unitBom.getUnit())[0]);
			sb.append("'" + UNITSEQS + "', ");

			String MAKERSEQS = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VDAMAKER(unitBom.getMaker())[0]);
			sb.append("'" + MAKERSEQS + "', ");

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String USERID = user.getName();
			sb.append("'" + USERID + "', ");

			// String PRICES = unitBom.getPrice() != null ? unitBom.getPrice().replace(",",
			// "") : "0";
			// sb.append("'" + PRICES + "', ");

			String CURRSEQS = ErpHelper.manager.getKEK_VDACURR(unitBom.getCurrency())[0];
			if (StringUtils.isNull(CURRSEQS)) {
				CURRSEQS = "0";
			}
			sb.append("'" + CURRSEQS + "', ");

			String CUSTSEQS = ErpHelper.manager.getKEK_VDAPURCUST(unitBom.getCustomer())[0];
			if (StringUtils.isNull(CUSTSEQS)) {
				CUSTSEQS = "0";
			}
			sb.append("'" + CUSTSEQS + "', ");

			String CREATE_DATE = new Timestamp(new Date().getTime()).toString();
			sb.append("'" + CREATE_DATE + "', ");

			String IsCode = "U";
			sb.append("'" + IsCode + "');");

			System.out.println("sendUnitBomToERP sql = " + sb.toString());

			st.executeUpdate(sb.toString());

			ArrayList<UnitBomPartLink> list = PartHelper.manager.getUnitBomPartLink(unitBom);

			System.out.println("list==" + list.size());

			for (UnitBomPartLink link : list) {
				UnitSubPart part = link.getSubPart();

				StringBuffer sql = new StringBuffer();

				sql.append(" INSERT INTO KEK_TPJTUnitBOM_IF (  ");
				sql.append(" SEQ, UNITPARTNO, PART_NAME, PART_SPEC, PART_NO, ");
				sql.append(" SUB_PART_NAME, SUB_PART_SPEC, SUB_PART_NO, QTY, UnitItemSeq, ");
				sql.append(" ItemSeq, SubItemSeq, MAKERSEQ, UNITSEQ, CURRSEQ, ");
				sql.append(" PRICE, FullPath, CREATE_TIME");
				sql.append(")");

				int SEQ = ErpHelper.manager.getMaxSeq("KEK_TPJTUnitBOM_IF");

				System.out.println("SEQ=" + SEQ);
				sql.append(" VALUES ('" + SEQ + "', ");

				String UNITPARTNO = unitBom.getSpec();
				sql.append("'" + UNITPARTNO + "', ");

				String PART_NAME = unitBom.getPartName();
				sql.append("'" + PART_NAME + "', ");

				String PART_SPEC = unitBom.getSpec();
				sql.append("'" + PART_SPEC + "', ");

				String PART_NO = unitBom.getPartNo();
				sql.append("'" + PART_NO + "', ");

				String SUB_PART_NAME = part.getPartName();
				sql.append("'" + SUB_PART_NAME + "', ");

				String SUB_PART_SPEC = part.getStandard();
				sql.append("'" + SUB_PART_SPEC + "', ");

				String SUB_PART_NO = part.getPartNo();
				sql.append("'" + SUB_PART_NO + "', ");

				String QTY = part.getQuantity();
				if (QTY == null || "".equals(QTY)) {
					QTY = "1";
				}
				sql.append("'" + QTY + "', ");

				String UnitItemSeq = part.getQuantity();
				if (UnitItemSeq == null || "".equals(UnitItemSeq)) {
					UnitItemSeq = "1";
				}
				sql.append("'" + UnitItemSeq + "', ");

				String ItemSeq = part.getQuantity();
				ItemSeq = "1";
				sql.append("'" + ItemSeq + "', ");

				String SubItemSeq = part.getQuantity();
				SubItemSeq = "1";
				sql.append("'" + SubItemSeq + "', ");

				String MAKERSEQ = ErpHelper.manager.getKEK_VDAMAKER(part.getMaker())[0];
				if (StringUtils.isNull(MAKERSEQ)) {
					MAKERSEQ = "1";
				}
				sql.append("'" + MAKERSEQ + "', ");

				String UNITSEQ = ErpHelper.manager.getKEK_VDAUnit(part.getUnit())[0];
				if (StringUtils.isNull(UNITSEQ)) {
					UNITSEQ = "1";
				}
				sql.append("'" + UNITSEQ + "', ");

				String CURRSEQ = ErpHelper.manager.getKEK_VDACURR(part.getCurrency())[0];
				if (StringUtils.isNull(CURRSEQ)) {
					CURRSEQ = "1";
				}
				sql.append("'" + CURRSEQ + "', ");

				String PRICE = part.getPrice().replace(",", "");
				sql.append("'" + PRICE + "', ");

				String FullPath = PART_SPEC + File.separator + SUB_PART_SPEC;
				sql.append("'" + FullPath + "', ");

				CREATE_DATE = new Timestamp(new Date().getTime()).toString();
				sql.append("'" + CREATE_DATE + "');");

				System.out.println("sendUnitBomToERP sql = " + sql.toString());

				st.executeUpdate(sql.toString());

				StringBuffer exec = new StringBuffer();
				exec.append("EXEC KEK_SPLMUnitBOM '" + PART_NO + "'");
				st.executeUpdate(exec.toString());

				setUCode(PART_SPEC, unitBom);
			}

			StringBuffer sbs = new StringBuffer();
			sbs.append("EXEC KEK_SPLMItemIF");
			st.executeUpdate(sbs.toString());

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			DBCPManager.freeConnection(con, st, rs);
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			DBCPManager.freeConnection(con, st, rs);
		}
	}

	private String setUCode(String PART_SPEC, UnitBom unitBom) {
		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String reValue = "";
		try {
			trs.start();

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT PART_NO FROM KEK_TDAItem_IF WHERE PART_SPEC='" + PART_SPEC + "'");
//			sql.append("SELECT UNITPARTNO FROM KEK_TPJTUnitBOM_IF WHERE PART_SPEC='" + PART_SPEC + "'");

			String uCode = "";

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				uCode = (String) rs.getString(1);
			}

			System.out.println("y=" + uCode);

			if (!StringUtils.isNull(uCode)) {

				unitBom.setUCode(uCode);
				PersistenceHelper.manager.modify(unitBom);

			}
			reValue = uCode;
			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			DBCPManager.freeConnection(con, st, rs);
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			DBCPManager.freeConnection(con, st, rs);
		}
		return reValue;
	}

	@Override
	public Map<String, Object> checkUnitBom(Map<String, Object> param) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		con = DBCPManager.getConnection(erpName);
		try {
			st = con.createStatement();
			StringBuffer sql = new StringBuffer();

			String spec = (String) param.get("spec");

			sql.append("SELECT COUNT(*) FROM KEK_VDAItem WHERE Spec='" + spec + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int i = Integer.parseInt(rs.getString(1));
				if (i > 0) {
//					map.put("result", SUCCESS);
					map.put("reload", true);
					map.put("msg", "규격 " + spec + "의 중복값이 있습니다.");
					map.put("check", false);
				} else {
//					map.put("result", SUCCESS);
					map.put("reload", true);
					map.put("msg", "중복확인 되었습니다.");
					map.put("check", true);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			map.put("reload", true);
			map.put("check", false);
			DBCPManager.freeConnection(con, st, rs);
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	@Override
	public void sendUnitBomToERP(UnitBom unitBom, ArrayList<UnitSubPart> childs) throws WTException {
		Map<String, Object> map = new HashMap<String, Object>();
		Transaction trs = new Transaction();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			trs.start();

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sb = new StringBuffer();

			sb.append(
					"INSERT INTO KEK_TDAItem_IF (SEQ, PART_NAME, PART_SPEC, UNITSEQ, MAKERSEQ, USERID, PRICE,  CURRSEQ, CUSTSEQ, ");
			sb.append("CREATE_DATE, IsCode)");

			int SEQS = ErpHelper.manager.getMaxSeq("KEK_TDAItem_IF");

			System.out.println("##sendUnitBomToERP SEQS= " + SEQS + "//");

			sb.append(" VALUES('" + SEQS + "', ");

			String PART_NAMES = unitBom.getPartName().trim();
			String PART_SPECS = unitBom.getSpec().trim();

			sb.append("'" + PART_NAMES + "', ");
			sb.append("'" + PART_SPECS + "', ");

			String UNITSEQS = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VDAUnit(unitBom.getUnit())[0]);
			sb.append("'" + UNITSEQS + "', ");

			String MAKERSEQS = StringUtils.replaceToValue(ErpHelper.manager.getKEK_VDAMAKER(unitBom.getMaker())[0]);
			sb.append("'" + MAKERSEQS + "', ");

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			String USERID = user.getName();
			sb.append("'" + USERID + "', ");

			String PRICES = unitBom.getPrice() != "" ? unitBom.getPrice().replace(",", "") : "0";
			sb.append("'" + PRICES + "', ");

			String CURRSEQS = ErpHelper.manager.getKEK_VDACURR(unitBom.getCurrency())[0];
			if (StringUtils.isNull(CURRSEQS)) {
				CURRSEQS = "0";
			}
			sb.append("'" + CURRSEQS + "', ");

			String CUSTSEQS = ErpHelper.manager.getKEK_VDAPURCUST(unitBom.getCustomer())[0];
			if (StringUtils.isNull(CUSTSEQS)) {
				CUSTSEQS = "0";
			}
			sb.append("'" + CUSTSEQS + "', ");

			String CREATE_DATE = new Timestamp(new Date().getTime()).toString();
			sb.append("'" + CREATE_DATE + "', ");

			String IsCode = "U";
			sb.append("'" + IsCode + "');");

			System.out.println("sendUnitBomToERP sql = " + sb.toString());

			st.executeUpdate(sb.toString());

			ArrayList<UnitBomPartLink> list = PartHelper.manager.getUnitBomPartLink(unitBom);

			System.out.println("list==" + list.size());

			String PART_SPEC = unitBom.getSpec();

			String PART_NO = unitBom.getPartNo();

			for (UnitBomPartLink link : list) {
				UnitSubPart part = link.getSubPart();

				StringBuffer sql = new StringBuffer();

				sql.append(" INSERT INTO KEK_TPJTUnitBOM_IF (  ");
				sql.append(" SEQ, UNITPARTNO, PART_NAME, PART_SPEC, PART_NO, ");
				sql.append(" SUB_PART_NAME, SUB_PART_SPEC, SUB_PART_NO, QTY, UnitItemSeq, ");
				sql.append(" ItemSeq, SubItemSeq, MAKERSEQ, UNITSEQ, CURRSEQ, UMLotSeq,");
				sql.append(" PRICE, FullPath, CREATE_TIME");
				sql.append(")");

				int SEQ = ErpHelper.manager.getMaxSeq("KEK_TPJTUnitBOM_IF");

				System.out.println("SEQ=" + SEQ);
				sql.append(" VALUES ('" + SEQ + "', ");

				String UNITPARTNO = unitBom.getSpec();
				sql.append("'" + UNITPARTNO + "', ");

				String PART_NAME = unitBom.getPartName();
				sql.append("'" + PART_NAME + "', ");

				sql.append("'" + PART_SPEC + "', ");

				sql.append("'" + PART_NO + "', ");

				String SUB_PART_NAME = part.getPartName();
				sql.append("'" + SUB_PART_NAME + "', ");

				String SUB_PART_SPEC = part.getStandard();
				sql.append("'" + SUB_PART_SPEC + "', ");

				String SUB_PART_NO = part.getPartNo();
				sql.append("'" + SUB_PART_NO + "', ");

				String QTY = part.getQuantity();
				if (QTY == null || "".equals(QTY)) {
					QTY = "1";
				}
				sql.append("'" + QTY + "', ");

				String UnitItemSeq = part.getQuantity();
				if (UnitItemSeq == null || "".equals(UnitItemSeq)) {
					UnitItemSeq = "1";
				}
				sql.append("'" + UnitItemSeq + "', ");

				String ItemSeq = part.getQuantity();
				ItemSeq = "1";
				sql.append("'" + ItemSeq + "', ");

				String SubItemSeq = part.getQuantity();
				SubItemSeq = "1";
				sql.append("'" + SubItemSeq + "', ");

				String MAKERSEQ = ErpHelper.manager.getKEK_VDAMAKER(part.getMaker())[0];
				if (StringUtils.isNull(MAKERSEQ)) {
					MAKERSEQ = "1";
				}
				sql.append("'" + MAKERSEQ + "', ");

				String UNITSEQ = ErpHelper.manager.getKEK_VDAUnit(part.getUnit())[0];
				if (StringUtils.isNull(UNITSEQ)) {
					UNITSEQ = "1";
				}
				sql.append("'" + UNITSEQ + "', ");

				String CURRSEQ = ErpHelper.manager.getKEK_VDACURR(part.getCurrency())[0];
				if (StringUtils.isNull(CURRSEQ)) {
					CURRSEQ = "1";
				}
				sql.append("'" + CURRSEQ + "', ");

				String UMLotSeq = ErpHelper.manager.getKEK_VDALotNo(part.getLotNo())[0];
				sql.append("'" + UMLotSeq + "', ");

//				String Umsupplytype = part.getClassification();
//				sql.append("'"+Umsupplytype+"', ");
//				
//				String Remark = part.getNote();
//				sql.append("'"+Remark+"', ");

				String PRICE = part.getPrice().replace(",", "");
				sql.append("'" + PRICE + "', ");

				String FullPath = PART_SPEC + File.separator + SUB_PART_SPEC;
				sql.append("'" + FullPath + "', ");

				CREATE_DATE = new Timestamp(new Date().getTime()).toString();
				sql.append("'" + CREATE_DATE + "');");

				System.out.println("sendUnitBomToERP sql = " + sql.toString());

				st.executeUpdate(sql.toString());

			}
			StringBuffer exec = new StringBuffer();
			exec.append("EXEC KEK_SPLMUnitBOM '" + PART_SPEC + "'");
			st.executeUpdate(exec.toString());

			String uCode = setUCode(PART_SPEC, unitBom);

			System.out.println("uCode ::  " + uCode);

			StringBuffer sbs = new StringBuffer();
			sbs.append("EXEC KEK_SPLMItemIF");
			st.executeUpdate(sbs.toString());

			trs.commit();
			trs = null;
		} catch (Exception e) {
			e.printStackTrace();
			DBCPManager.freeConnection(con, st, rs);
			trs.rollback();
		} finally {
			if (trs != null)
				trs.rollback();
			DBCPManager.freeConnection(con, st, rs);
		}
	}
}