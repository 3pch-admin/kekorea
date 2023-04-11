package e3ps.erp.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;

import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.partlist.service.PartlistHelper;
import e3ps.common.db.DBCPManager;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.StringUtils;
import e3ps.doc.WTDocumentWTPartLink;
import e3ps.erp.ErpConnectionPool;
import e3ps.project.Project;
import e3ps.project.output.Output;
import e3ps.project.output.OutputDocumentLink;
import e3ps.workspace.ApprovalLine;
import e3ps.workspace.ApprovalMaster;
import e3ps.workspace.service.WorkspaceHelper;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.FileUtil;

public class ErpHelper {

	public static final boolean isOperation = true;

	public static final ErpService service = ServiceFactory.getService(ErpService.class);
	public static final ErpHelper manager = new ErpHelper();

	private static final BasicDataSource dataSource = ErpConnectionPool.getDataSource();

	/**
	 * ERP 물리 파일 전송 위치 변수
	 */
	private static final String erpOutputDir = File.separator + "\\Erp-app\\plm2";
	private static final String epmOutputDir = File.separator + "\\Erp-app\\plm";

	/**
	 * 캐시 처리
	 */
	private static HashMap<String, Map<String, Object>> cacheManager = null;
	static {
		if (cacheManager == null) {
			cacheManager = new HashMap<>();
		}
	}

	/**
	 * YCODE 체크 수배표 등록시
	 */
	public Map<String, Object> validate(String partNo) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT *");
			sql.append(" FROM KEK_VDAITEM");
			sql.append(" WHERE ITEMNO='" + partNo.trim() + "' AND SMSATAUSNAME != '폐기'");
			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				result.put("check", "OK");
			} else {
				result.put("check", "NG");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErpConnectionPool.free(con, st, rs);
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return result;
	}

	/**
	 * 수배표 UNITNAME 가져오기
	 */
	public Map<String, Object> getUnitName(int lotNo) throws Exception {
		Map<String, Object> result = new HashMap<>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT LOTSEQ, LOTNO, LOTUNITNAME FROM KEK_VDALOTNO WHERE LOTNO='" + lotNo + "'");
			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				result.put("unitName", (String) rs.getString(3));
			}
		} catch (Exception e) {
			e.printStackTrace();
			ErpConnectionPool.free(con, st, rs);
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return result;
	}

	/**
	 * 부품수배표 부품정보 가져오기
	 */
	public Map<String, Object> getErpItemByPartNoAndQuantity(String partNo, int quantity) throws Exception {
		Map<String, Object> result = new HashMap<>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		ResultSet _rs = null;
		try {

			String cacheKey = partNo + quantity;
			Map<String, Object> cacheData = cacheManager.get(cacheKey);

			if (cacheData == null) {
				con = dataSource.getConnection();
				st = con.createStatement();

				StringBuffer sql = new StringBuffer();
				sql.append("SELECT ITEMSEQ, ITEMNAME, SPEC");
				sql.append(" FROM KEK_VDAITEM");
				sql.append(" WHERE ITEMNO='" + partNo.trim() + "' AND SMSATAUSNAME != '폐기'");

				rs = st.executeQuery(sql.toString());
				if (rs.next()) {
					int itemSeq = (int) rs.getInt(1);
					String itemName = (String) rs.getString(2);
					String spec = (String) rs.getString(3);

					StringBuffer sb = new StringBuffer();
					sb.append("EXEC KEK_SPLMBASEGETPRICE '" + itemSeq + "', '', '" + quantity + "'");
					_rs = st.executeQuery(sb.toString());

					String maker = "";
					String customer = "";
					String unit = "";
					String currency = "";
					int price = 0;
					Integer exchangeRate = 0;
					if (_rs.next()) {
						maker = (String) _rs.getString("makerName");
						customer = (String) _rs.getString("custName");
						unit = (String) _rs.getString("unitName");
						currency = (String) _rs.getString("currName");
						price = (int) _rs.getInt("price");
						exchangeRate = (int) _rs.getInt("exRate");

					}
					result.put("maker", maker);
					result.put("customer", customer);
					result.put("unit", unit);
					result.put("currency", currency);
					result.put("price", price);
					result.put("exchangeRate", exchangeRate);
					result.put("standard", spec);
					result.put("partName", itemName);
					result.put("won", quantity * price * exchangeRate);
				}
			} else {
				result = cacheManager.get(cacheKey);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (con != null) {
				con.close();
			}

			if (st != null) {
				st.close();
			}

			if (rs != null) {
				rs.close();
			}

			if (_rs != null) {
				_rs.close();
			}
		} finally {
			if (con != null) {
				con.close();
			}

			if (st != null) {
				st.close();
			}

			if (rs != null) {
				rs.close();
			}

			if (_rs != null) {
				_rs.close();
			}
		}
		return result;
	}

	/**
	 * 규격으로 ERP 부품정보 가져오기
	 */
	public Map<String, Object> getErpItemBySpec(String spec) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>(); // json
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		ResultSet _rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT ITEMSEQ, ITEMNAME, SPEC, ITEMNO");
			sql.append(" FROM KEK_VDAITEM");
			sql.append(" WHERE SPEC='" + spec.trim() + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int itemSeq = (int) rs.getInt(1);
				String itemName = (String) rs.getString(2);
				String itemNo = (String) rs.getString(4);

				StringBuffer sb = new StringBuffer();
				sb.append("EXEC KEK_SPLMBASEGETPRICE '" + itemSeq + "', '', '1'");

				_rs = st.executeQuery(sb.toString());

				String maker = "";
				String customer = "";
				String unit = "";
				String currency = "";
				int price = 0;
				if (_rs.next()) {
					maker = (String) _rs.getString("makerName");
					customer = (String) _rs.getString("custName");
					unit = (String) _rs.getString("unitName");
					currency = (String) _rs.getString("currName");
					price = (int) _rs.getInt("price");
				}

				result.put("itemName", itemName);
				result.put("itemNo", itemNo);
				result.put("maker", maker);
				result.put("customer", customer);
				result.put("unit", unit);
				result.put("price", price);
				result.put("currency", currency);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (con != null) {
				con.close();
			}

			if (st != null) {
				st.close();
			}

			if (rs != null) {
				rs.close();
			}

			if (_rs != null) {
				_rs.close();
			}
		} finally {
			if (con != null) {
				con.close();
			}

			if (st != null) {
				st.close();
			}

			if (rs != null) {
				rs.close();
			}

			if (_rs != null) {
				_rs.close();
			}
		}
		return result;
	}

	/**
	 * PDM에 등록된 데이터 YCODE로 ERP에서 추가 정보 가져오기 ㄴ
	 */
	public Map<String, Object> getErpItemByPartNo(String partNo) throws Exception {
		Map<String, Object> result = new HashMap<>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		ResultSet _rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT ITEMSEQ, ITEMNAME, SPEC");
			sql.append(" FROM KEK_VDAITEM");
			sql.append(" WHERE ITEMNO='" + partNo.trim() + "' AND SMSATAUSNAME != '폐기'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int itemSeq = (int) rs.getInt(1);
				String itemName = (String) rs.getString(2);
				String spec = (String) rs.getString(3);

				StringBuffer sb = new StringBuffer();
				sb.append("EXEC KEK_SPLMBASEGETPRICE '" + itemSeq + "', '', '1'");
				_rs = st.executeQuery(sb.toString());

				String maker = "";
				String customer = "";
				String unit = "";
				String currency = "";
				int price = 0;
				Integer exchangeRate = 0;
				if (_rs.next()) {
					maker = (String) _rs.getString("makerName");
					customer = (String) _rs.getString("custName");
					unit = (String) _rs.getString("unitName");
					currency = (String) _rs.getString("currName");
					price = (int) _rs.getInt("price");
					exchangeRate = (int) _rs.getInt("exRate");
				}
				result.put("maker", maker);
				result.put("customer", customer);
				result.put("unit", unit);
				result.put("currency", currency);
				result.put("price", price);
				result.put("exchangeRate", exchangeRate);
				result.put("standard", spec);
				result.put("partName", itemName);
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (con != null) {
				con.close();
			}

			if (st != null) {
				st.close();
			}

			if (rs != null) {
				rs.close();
			}

			if (_rs != null) {
				_rs.close();
			}
		} finally {
			if (con != null) {
				con.close();
			}

			if (st != null) {
				st.close();
			}

			if (rs != null) {
				rs.close();
			}

			if (_rs != null) {
				_rs.close();
			}
		}
		return result;
	}

	/**
	 * 산출물 ERP 전송
	 */
	public void sendToErp(WTDocument document) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			QueryResult result = PersistenceHelper.manager.navigate(document, "output", OutputDocumentLink.class);
			while (result.hasMoreElements()) {
				Output output = (Output) result.nextElement();
				Project project = output.getProject();
				String loc = output.getLocation();

				// 산출물이 아니면 패스
				if (skip(loc)) {
					continue;
				}

				StringBuffer sql = new StringBuffer();

				sql.append("INSERT INTO KEK_TPJTOUTPUTRPTDO_IF (SEQ, STDNO, PJTSEQ, STDREPORTSEQ, REGDATE, USERID, ");
				sql.append("REMARK, CREATE_TIME)");

				int seq = getMaxSequence("KEK_TPJTOUTPUTRPTDO_IF");
				sql.append(" VALUES('" + seq + "', ");

				String stdNo = document.getNumber();
				String projectType = project.getProjectType().getName();

				int stdReportSeq = 1;
				if (loc.contains("작업지시서") || loc.contains("SW_지시서")) {
					stdReportSeq = 47;
					stdNo = "INS-" + stdNo;
				} else if (loc.contains("설비사양서")) {
					if (projectType.equals("개조")) {
						stdReportSeq = 53;
						stdNo = "REQ1-" + stdNo;
					} else if (projectType.equals("양산")) {
						stdReportSeq = 54;
						stdNo = "REQ2-" + stdNo;
					}
				}

				sql.append("'" + stdNo + "', ");

				String kekNumber = project.getKekNumber();
				Map<String, Object> pjtData = ErpHelper.manager.getPjtInfoByKekNumber(kekNumber);

				String pjtSeq = (String) pjtData.get("pjtSeq");

				if (StringUtils.isNull(pjtSeq)) {
					pjtSeq = "0";
				}

				sql.append("'" + Integer.parseInt(pjtSeq) + "', ");
				sql.append("'" + stdReportSeq + "', ");

				String regDate = erpDateStringFormat(document.getCreateTimestamp());
				sql.append("'" + regDate + "', ");

				String userId = document.getCreatorName();
				sql.append("'" + userId + "', ");

				String remark = StringUtils.replaceToValue(document.getDescription());
				sql.append("'" + remark + "', ");

				String createTime = new Timestamp(new Date().getTime()).toString().substring(0, 16);
				sql.append("'" + createTime + "');");
				st.executeUpdate(sql.toString());

				sendToErpFile(document, project);

				// 프로시저 실행
				StringBuffer sb = new StringBuffer();
				sb.append("EXEC KEK_SPLMOutputRptDOProc ");
				sb.append("'" + stdNo + "'");
				st.executeUpdate(sb.toString());

//				ErpSendHistory sendHistory = ErpSendHistory.newErpSendHistory();
//				sendHistory.setOwnership(CommonUtils.sessionOwner()); // 전송자
//				sendHistory.setSendQuery(sql.toString());
//				sendHistory.setSendType("산출물 전송");
//				sendHistory.setResult(true);
//				PersistenceHelper.manager.save(sendHistory);

			}

		} catch (Exception e) {
			e.printStackTrace();
			// 실패
//			ErpSendHistory sendHistory = ErpSendHistory.newErpSendHistory();
//			sendHistory.setOwnership(CommonUtils.sessionOwner()); // 전송자
//			sendHistory.setSendQuery(sql.toString());
//			sendHistory.setSendType("산출물 전송");
//			sendHistory.setResult(true);
//			PersistenceHelper.manager.save(sendHistory);
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
	}

	/**
	 * 프로젝트 산출물 물리파일 ERP 전송
	 */
	private void sendToErpFile(WTDocument document, Project project) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append(
					"INSERT INTO KEK_TPJTOUTPUTRPTDOFILE_IF (SEQ, STDNO, RPTFILENAME, CREATE_TIME, FILEEXT, FILESIZE)");

			int seq = getMaxSequence("KEK_TPJTOUTPUTRPTDOFILE_IF");
			sql.append(" VALUES('" + seq + "', ");

			String stdNo = document.getNumber();
			String loc = document.getLocation();
			String projectType = project.getProjectType().getName();

			if (loc.contains("작업지시서") || loc.contains("SW_지시서")) {
				stdNo = "INS-" + stdNo;
			} else if (loc.contains("설비사양서")) {
				if (projectType.equals("개조")) {
					stdNo = "REQ1-" + stdNo;
				} else if (projectType.equals("양산")) {
					stdNo = "REQ2-" + stdNo;
				}
			}
			sql.append("'" + stdNo + "', ");

			String rptFileName = ContentUtils.getPrimary(document)[2];
			sql.append("'" + rptFileName + "', ");

			String createTime = new Timestamp(new Date().getTime()).toString().substring(0, 16);
			sql.append("'" + createTime + "', ");

			String ext = FileUtil.getExtension(rptFileName);
			sql.append("'" + ext + "', ");

			long fileSize = Long.parseLong(ContentUtils.getPrimary(document)[8]);
			sql.append("'" + fileSize + "');");
			st.executeUpdate(sql.toString());

			// 첨부 파일 전송...
			String dir = erpOutputDir;
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			QueryResult qr = ContentHelper.service.getContentsByRole(document, ContentRoleType.PRIMARY);
			if (qr.hasMoreElements()) {
				ApplicationData data = (ApplicationData) qr.nextElement();
				byte[] buffer = new byte[10240];
				InputStream is = ContentServerHelper.service.findLocalContentStream(data);
				File write = new File(directory + File.separator + data.getFileName());
				FileOutputStream fos = new FileOutputStream(write);
				int j = 0;
				while ((j = is.read(buffer, 0, 10240)) > 0) {
					fos.write(buffer, 0, j);
				}
				fos.close();
				is.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
	}

	/**
	 * 산출물 여부 확인
	 */
	private boolean skip(String loc) {
		if (!loc.contains("작업지시서") && !loc.contains("설비사양서") && !loc.contains("SW_지시서")) {
			return true;
		}
		return false;
	}

	/**
	 * 테이블 최대 Sequence + 1 값 반환
	 */
	public int getMaxSequence(String table) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			String sql = "SELECT MAX(SEQ) FROM " + table;
			con = dataSource.getConnection();
			st = con.createStatement();
			rs = st.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1) + 1;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return 0;
	}

	/**
	 * KEK 작번으로 ERP쪽 프로젝트 데이터 가져오기
	 */
	public Map<String, Object> getPjtInfoByKekNumber(String kekNumber) throws Exception {
		Map<String, Object> data = new HashMap<>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PJTSEQ, PJTNAME, PJTNO FROM KEK_VPJTPROJECT WHERE PJTNO='" + kekNumber + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int pjtSeq = (int) rs.getInt(1);
				String pjtName = (String) rs.getString(2);
				String pjtNo = (String) rs.getString(3);
				data.put("pjtSeq", String.valueOf(pjtSeq));
				data.put("pjtName", pjtName);
				data.put("pjtNo", pjtNo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return data;
	}

	/**
	 * ERP 날짜 전송 포맷
	 */
	private String erpDateStringFormat(Timestamp time) {
		return time.toString().substring(0, 10).replaceAll("-", "").replace("/", "");
	}

	/**
	 * 수배표 전송
	 */
	public void sendToErp(PartListMaster master) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			st = con.createStatement();

			// 작번 개수 만큼 전송
			QueryResult result = PersistenceHelper.manager.navigate(master, "project", PartListMasterProjectLink.class);

			if (result.size() == 0) {
				ErpConnectionPool.free(con, st, rs);
				return;
			}

			while (result.hasMoreElements()) {

				Project project = (Project) result.nextElement();
				String kekNumber = project.getKekNumber();
				String engType = master.getEngType();

				ArrayList<PartListData> list = PartlistHelper.manager.getPartListData(master);
				String disNo = "WANT_" + master.getNumber();
				for (PartListData data : list) {
					StringBuffer sql = new StringBuffer();

					sql.append(
							"INSERT INTO KEK_TPJTBOM_IF (SEQ, DISNO, REGDATE, PJTSEQ, DESIGNTYPE, REMARKM, USERID, ");
					sql.append("ACCDATE, LOTSEQ, ITEMSEQ, MAKERSEQ, CUSTSEQ, UNITSEQ, CURRSEQ, QTY, EXRATE, ");
					sql.append("AMT, REMARK, UMSUPPLYTYPE, PRICE, CREATE_TIME, APPUSERID )");

					int seq = getMaxSequence("KEK_TPJTBOM_IF");
					sql.append(" VALUES('" + seq + "', ");
					sql.append("'" + disNo + "', ");

					String accDate = erpDateStringFormat(data.getPartListDate());
					sql.append("'" + accDate.replaceAll("-", "") + "', ");

					Map<String, Object> pjtData = ErpHelper.manager.getPjtInfoByKekNumber(kekNumber);
					String pjtSeq = (String) pjtData.get("pjtSeq");
					sql.append("'" + pjtSeq + "', ");

					if (engType.contains("기계")) {
						engType = "기계";
					} else if (engType.contains("전기")) {
						engType = "전기";
					}

					sql.append("'" + getKekDesignType(engType + "설계") + "', ");

					String remark = StringUtils.replaceToValue(master.getDescription());
					sql.append("'" + remark + "', ");

					String userId = master.getCreatorName();
					sql.append("'" + userId + "', ");

					sql.append("'" + accDate + "', ");

					int lotNo = data.getLotNo();
					sql.append("'" + getKekLotSeq(String.valueOf(lotNo)) + "', ");

					String partNo = data.getPartNo();
					sql.append("'" + getKekItemSeq(partNo) + "', ");

					String makerName = data.getMaker();
					sql.append("'" + getKekMakerSeq(makerName) + "', ");

					String customer = data.getCustomer();
					sql.append("'" + getKekCustSeq(customer) + "', ");

					String unitName = data.getUnit();
					sql.append("'" + getKekUnitSeq(unitName) + "', ");

					String currency = data.getCurrency();
					sql.append("'" + getKekCurrencySeq(currency) + "', ");

					int qty = data.getQuantity();
					sql.append("'" + qty + "', ");

					int rate = data.getExchangeRate();
					sql.append("'" + rate + "', ");
					sql.append("'" + (int) data.getWon() + "', ");
					sql.append("'" + data.getNote() + "', ");

					String classification = data.getClassification();
					sql.append("'" + getKekSupplySeq(classification) + "', ");
					sql.append("'" + data.getPrice() + "', ");
					sql.append("'" + accDate + "', ");

					ApprovalMaster am = WorkspaceHelper.manager.getMaster(master);
					ArrayList<ApprovalLine> agreeLines = WorkspaceHelper.manager.getAgreeLines(am);
					// 여기 머 들어가는지 확인..
//					sql.append("'" + APPUSERID + "');");
					st.executeUpdate(sql.toString());
				}

				StringBuffer sb = new StringBuffer();
				sb.append("EXEC KEK_SPLMBOMIF '" + disNo + "'");
				st.executeUpdate(sb.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
	}

	/**
	 * ERP LOT NO SEQ 가져오기
	 */
	public int getKekLotSeq(String lotNo) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT LOTSEQ FROM KEK_VDALOTNO WHERE LOTNO='" + lotNo + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				return (int) rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return 0;
	}

	/**
	 * ERP 설계 타입 SEQ 가져오기
	 */
	public int getKekDesignType(String engType) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT DESIGNTYPE FROM KEK_VDADESIGNTYPE WHERE DESIGNTYPENAME='" + engType + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				return (int) rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return 0;
	}

	/**
	 * ERP 품목 SEQ 가져오기
	 * 
	 * @param yCode
	 * @return
	 * @throws Exception
	 */
	public int getKekItemSeq(String partNo) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT ITEMSEQ");
			sql.append(" FROM KEK_VDAITEM");
			sql.append(" WHERE ITEMNO='" + partNo + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				return (int) rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return 0;
	}

	/**
	 * ERP 메이커 SEQ
	 */
	public int getKekMakerSeq(String makerName) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT MAKERSEQ FROM KEK_VDAMAKER WHERE MAKERNAME='" + makerName + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				return (int) rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return 0;
	}

	public int getKekCustSeq(String customer) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT CUSTSEQ FROM KEK_VDAPURCUST WHERE CUSTNAME='" + customer + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				return (int) rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return 0;
	}

	/**
	 * ERP 기준단위 SEQ
	 */
	public int getKekUnitSeq(String unitName) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT UNITSEQ FROM KEK_VDAUNIT WHERE UNITNAME='" + unitName + "'");
			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				return (int) rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return 0;
	}

	/**
	 * ERP 통화 SEQ
	 */
	public int getKekCurrencySeq(String currency) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT CURRSEQ FROM KEK_VDACURR WHERE CURRNAME='" + currency + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				return (int) rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return 0;
	}

	/**
	 * ERP 조달구분 SEQ
	 */
	public int getKekSupplySeq(String classification) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT UMSUPPLYTYPE FROM KEK_VDASUPPLYTYPE WHERE UMSUPPLYTYPENAME='" + classification + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				return (int) rs.getInt(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return 0;
	}

	/**
	 * ERP 로 품목 전송 후 YCODE 리턴
	 */
	public String sendToErp(WTPart part) throws Exception {
		String partNo = null;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			int keyIdx = 1;
			sql.append(
					"INSERT INTO KEK_TDAITEM_IF (SEQ, PART_NAME, PART_SPEC, UNITSEQ, MAKERSEQ, USERID, PRICE, CURRSEQ, CUSTSEQ, ");
			sql.append("CREATE_DATE,");
			String addName = "ADDFILENAME" + keyIdx;
			String addExt = "ADDFILEEXT" + keyIdx;
			String addSize = "ADDFILESIZE" + keyIdx;
			sql.append("" + addName + ", " + addExt + ", " + addSize + ", ");
			sql.append("ISCODE)");

			int seq = getMaxSequence("KEK_TDAITEM_IF");
			sql.append(" VALUES('" + seq + "', ");

			String partName = IBAUtils.getStringValue(part, "NAME_OF_PARTS");
			String spec = IBAUtils.getStringValue(part, "DWG_NO");
			sql.append("'" + partName + "', ");
			sql.append("'" + spec + "', ");

			sql.append("'" + getKekUnitSeq(IBAUtils.getStringValue(part, "STD_UNIT")) + "', ");
			sql.append("'" + getKekMakerSeq(IBAUtils.getStringValue(part, "MAKER")) + "', ");

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			sql.append("'" + user.getName() + "', ");
			sql.append("'" + IBAUtils.getIntegerValue(part, "PRICE") + "', ");
			sql.append("'" + getKekCurrencySeq(IBAUtils.getStringValue(part, "CURRNAME")) + "', ");
			sql.append("'" + getKekCustSeq(IBAUtils.getStringValue(part, "CUSTNAME")) + "', ");

			String createTime = new Timestamp(new Date().getTime()).toString();
			sql.append("'" + createTime + "', ");

			String[] primarys = ContentUtils.getPrimary(part);
			String fileName = primarys[2];
			sql.append("'" + fileName + "', ");
			sql.append("'" + FileUtil.getExtension(fileName) + "', ");
			sql.append("'" + primarys[7] + "', ");
			sql.append("'Y');");
			st.executeUpdate(sql.toString());

			String dir = epmOutputDir + File.separator + spec;
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			QueryResult result = ContentHelper.service.getContentsByRole(part, ContentRoleType.PRIMARY);
			if (result.hasMoreElements()) {
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

			StringBuffer sb = new StringBuffer();
			sb.append("EXEC KEK_SPLMITEMIF");
			st.executeUpdate(sb.toString());

			partNo = savePartNo(part, spec);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return partNo;
	}

	/**
	 * PDM 품목, 도면 데이터 IBA값 설정
	 */
	private String savePartNo(WTPart part, String spec) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String partNo = "";
		try {

			con = dataSource.getConnection();
			st = con.createStatement();
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PART_NO FROM KEK_TDAITEM_IF WHERE PART_SPEC='" + spec + "'");
			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				partNo = (String) rs.getString(1);

				if (!StringUtils.isNull(partNo)) {
					IBAUtils.deleteIBA(part, "PART_CODE", "s");
					IBAUtils.createIBA(part, "s", "PART_CODE", partNo);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return partNo;
	}

	/**
	 * ERP 자재 전송
	 */
	public String sendToErpItem(WTPart part) throws Exception {
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		ArrayList<WTDocument> list = new ArrayList<>();
		String partNo = "";
		try {

			con = dataSource.getConnection();
			st = con.createStatement();

			int keyIdx = 1;
			StringBuffer sql = new StringBuffer();
			sql.append(
					"INSERT INTO KEK_TDAITEM_IF (SEQ, PART_NAME, PART_SPEC, UNITSEQ, MAKERSEQ, USERID, PRICE, CURRSEQ, CUSTSEQ, ");
			sql.append("CREATE_DATE,");

			QueryResult result = PersistenceHelper.manager.navigate(part, "document", WTDocumentWTPartLink.class);
			while (result.hasMoreElements()) {
				WTDocument doc = (WTDocument) result.nextElement();
				list.add(doc);
			}

			for (int i = 0; i < list.size(); i++) {
				String addName = "ADDFILENAME" + keyIdx;
				String addExt = "ADDFILEEXT" + keyIdx;
				String addSize = "ADDFILESIZE" + keyIdx;
				sql.append("" + addName + ", " + addExt + ", " + addSize + ", ");
				keyIdx++;
			}

			sql.append("ISCODE)");

			int seq = getMaxSequence("KEK_TDAITEM_IF");
			sql.append(" VALUES('" + seq + "', ");

			String partName = IBAUtils.getStringValue(part, "NAME_OF_PARTS");
			String spec = IBAUtils.getStringValue(part, "DWG_NO");
			sql.append("'" + partName + "', ");
			sql.append("'" + spec + "', ");

			sql.append("'" + getKekUnitSeq(IBAUtils.getStringValue(part, "STD_UNIT")) + "', ");
			sql.append("'" + getKekMakerSeq(IBAUtils.getStringValue(part, "MAKER")) + "', ");

			WTUser user = (WTUser) SessionHelper.manager.getPrincipal();
			sql.append("'" + user.getName() + "', ");

			int price = IBAUtils.getIntegerValue(part, "PRICE");
			sql.append("'" + price + "', ");
			sql.append("'" + getKekCurrencySeq(IBAUtils.getStringValue(part, "CURRNAME")) + "', ");
			sql.append("'" + getKekCustSeq(IBAUtils.getStringValue(part, "CUSTNAME")) + "', ");

			String createTime = new Timestamp(new Date().getTime()).toString();
			sql.append("'" + createTime + "', ");

			for (WTDocument doc : list) {
				String[] primarys = ContentUtils.getPrimary(doc);
				sql.append("'" + primarys[2] + "', ");
				sql.append("'" + FileUtil.getExtension(primarys[2]) + "', ");
				sql.append("'" + primarys[7] + "', ");
			}

			sql.append("'Y');");
			st.executeUpdate(sql.toString());

			String dir = epmOutputDir + File.separator + spec;
			File directory = new File(dir);
			if (!directory.exists()) {
				directory.mkdirs();
			}

			for (WTDocument doc : list) {
				QueryResult qr = ContentHelper.service.getContentsByRole(doc, ContentRoleType.PRIMARY);
				if (qr.hasMoreElements()) {
					ApplicationData data = (ApplicationData) qr.nextElement();
					byte[] buffer = new byte[10240];
					InputStream is = ContentServerHelper.service.findLocalContentStream(data);
					File write = new File(directory + File.separator + data.getFileName());
					FileOutputStream fos = new FileOutputStream(write);
					int j = 0;
					while ((j = is.read(buffer, 0, 10240)) > 0) {
						fos.write(buffer, 0, j);
					}
					fos.close();
					is.close();
				}
			}

			StringBuffer sb = new StringBuffer();
			sb.append("EXEC KEK_SPLMITEMIF");
			st.executeUpdate(sb.toString());

			partNo = savePartNo(part, spec);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return partNo;
	}
}