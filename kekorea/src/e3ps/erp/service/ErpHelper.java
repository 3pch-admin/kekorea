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
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;

import e3ps.bom.partlist.PartListData;
import e3ps.bom.partlist.PartListMaster;
import e3ps.bom.partlist.PartListMasterProjectLink;
import e3ps.bom.partlist.cache.CacheProcessor;
import e3ps.bom.partlist.service.PartlistHelper;
import e3ps.common.db.DBCPManager;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.DateUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.service.EpmHelper;
import e3ps.erp.ErpConnectionPool;
import e3ps.erp.ErpSendHistory;
import e3ps.part.service.PartHelper;
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
import wt.epm.EPMDocument;
import wt.esi.ERPMaterialCollectorDelegate;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.pom.Transaction;
import wt.services.ServiceFactory;
import wt.session.SessionHelper;
import wt.util.FileUtil;

public class ErpHelper {

	public static final boolean isOperation = true;

	public static final String OUTPUT_PATH = "";

	public static final String EPM_PATH = "";

	public static boolean isSendERP = true;

	public static final String erpName = "erp";

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
	 * 표준산출물
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getKEK_VPJTOutputStdRpt(Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(); // data
//		String key = (String) param.get("key");
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT StdReportSeq, StdReportName from KEK_VPJTOutputStdRpt");

			rs = st.executeQuery(sql.toString());
			while (rs.next()) {
				int StdReportSeq = (int) rs.getInt(1);
				String StdReportName = (String) rs.getString(2);

				Map<String, Object> dataMap = new HashMap<String, Object>();
				dataMap.put("name", StdReportSeq);
				dataMap.put("value", StdReportName);
				list.add(dataMap);
			}
			map.put("result", SUCCESS);
			map.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
		} finally {
//			instance.freeConnection(erpName, con);
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	/**
	 * 메이커
	 * 
	 * @param makerName
	 * @return
	 * @throws Exception
	 */
	public ArrayList<Map<String, Object>> getAllKEK_VDAMAKER() throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT MakerSeq, MakerName from KEK_VDAMAKER ");

			rs = st.executeQuery(sql.toString());
			while (rs.next()) {
				int MakerSeq = (int) rs.getInt(1);
				String MakerName = (String) rs.getString(2);

				Map<String, Object> mapp = new HashMap<String, Object>();

				mapp.put("id", MakerSeq);
				mapp.put("name", MakerName);
				list.add(mapp);
			}

			map.put("result", SUCCESS);
			map.put("list", list);

		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return list;
	}

	public int getDevMaxSeq(String tableName) {
		int seq = 0;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			// TCW_PART_IF
			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();
			String sql = "SELECT MAX(SEerpName " + tableName;
			System.out.println("### getDevMaxSeq = " + sql);
			rs = st.executeQuery(sql);

			while (rs.next()) {
				seq = rs.getInt(1);
				// System.out.println(rs.getInt(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return seq + 1;
	}

	public boolean searchPartSpec(String spec) {
		boolean reValue = false;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		int seq = 0;
		try {
			// TCW_PART_IF
			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();
			String sql = "SELECT COUNT(*)  from KEK_VDAItem WHERE SPEC = '" + spec + "' ";

			rs = st.executeQuery(sql);

			while (rs.next()) {
				seq = rs.getInt(1);
				// System.out.println(rs.getInt(1));
			}

			if (seq > 1) {
				reValue = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return reValue;
	}

	public Map<String, Object> getKEK_LotNo(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		String lotNo = (String) param.get("lotNo");
		int index = (int) param.get("index");
//		DBConnectionManager instance = null;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			if (!StringUtils.isNull(lotNo)) {

				con = DBCPManager.getConnection(erpName);
				st = con.createStatement();

				StringBuffer sql = new StringBuffer();

				sql.append("SELECT LOTSEQ, LOTNO, LOTUNITNAME FROM KEK_VDALOTNO WHERE LOTNO='" + lotNo + "'");

				rs = st.executeQuery(sql.toString());
				if (rs.next()) {
					// int LotSeq = (int) rs.getInt(1);
					// String LotNo = (String) rs.getString(2);
					String LotUnitName = (String) rs.getString(3);

					map.put("LotUnitName", LotUnitName);
				}
			}
			map.put("index", index);
		} catch (Exception e) {
			e.printStackTrace();
			DBCPManager.freeConnection(con, st, rs);
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	public Map<String, Object> getKEK_VDAItemBySpec(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		String spec = (String) param.get("spec");
		String qty = (String) param.get("qty");
		int index = (int) param.get("index");
//		DBConnectionManager instance = null;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			if (!StringUtils.isNull(spec)) {
				con = DBCPManager.getConnection(erpName);
				st = con.createStatement();

				StringBuffer sql = new StringBuffer();

				sql.append("SELECT ItemSeq, ItemName, Spec, ItemNo");
				sql.append(" from KEK_VDAItem");
				sql.append(" WHERE Spec='" + spec.trim() + "'");

				if (StringUtils.isNull(qty)) {
					qty = "1";
				}

				rs = st.executeQuery(sql.toString());
				if (rs.next()) {

					int ItemSeq = (int) rs.getInt(1);
					String ItemName = (String) rs.getString(2);
//					String Spec = (String) rs.getString(3);
					String ItemNo = (String) rs.getString(4);

					// PLM 체크..
					boolean isYcode = EpmHelper.manager.checkPLMYCode(ItemNo);

					if (isYcode) {
						System.out.println("중복 리턴..");
						map.put("result", FAIL);
						map.put("YCODE", "true");
						return map;
					}

					StringBuffer sb = new StringBuffer();
					sb.append("EXEC KEK_SPLMBaseGetPrice '" + ItemSeq + "', '', '" + qty.trim() + "'");

					ResultSet result = st.executeQuery(sb.toString());

					String MakerName = "";
					String CustName = "";
					String UnitName = "";
					String CurrName = "";
					String Price = "";
					String ExRate = "";
					if (result.next()) {

						MakerName = (String) result.getString("MakerName");
						CustName = (String) result.getString("CustName");
						UnitName = (String) result.getString("UnitName");
						CurrName = (String) result.getString("CurrName");
						Price = (String) result.getString("Price");
						ExRate = (String) result.getString("ExRate");

						if (StringUtils.isNull(Price)) {
							Price = "1";
						}
					}
					map.put("ItemNo", ItemNo);
					map.put("ExRate", ExRate);
					map.put("ItemName", ItemName);
					// map.put("Spec", Spec);
					map.put("MakerName", MakerName);
					map.put("CustName", CustName);
					map.put("UnitName", UnitName);

					String price = String.format("%,f", Double.parseDouble(Price));
					map.put("Price", price.substring(0, price.lastIndexOf(".")));
					map.put("CurrName", CurrName);

					String ext = "";

					if (qty.contains("-")) {
						ext = qty.substring(0, 1);
						qty = qty.substring(1);
					}

					String Won = String.format("%,f",
							Double.parseDouble(qty) * Double.parseDouble(Price) * Double.parseDouble(ExRate));
					map.put("won", ext.trim() + Won.substring(0, Won.lastIndexOf(".")));

				}
			}
			map.put("result", SUCCESS);
			map.put("index", index);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
			DBCPManager.freeConnection(con, st, rs);
		} finally {
			if (spec != null) {
				DBCPManager.freeConnection(con, st, rs);
			}
		}
		return map;
	}

	public Map<String, Object> checkYCode(Map<String, Object> param) {
		String yCode = (String) param.get("number");
		int index = (int) param.get("index");
		Map<String, Object> map = new HashMap<String, Object>();
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			if (!StringUtils.isNull(yCode)) {
				con = DBCPManager.getConnection(erpName);
				st = con.createStatement();

				StringBuffer sql = new StringBuffer();

				sql.append("SELECT ItemSeq, ItemName");
				sql.append(" from KEK_VDAItem");
//				sql.append(" WHERE ItemNo='" + yCode.trim + "'");
				sql.append(" WHERE ItemNo='" + yCode.trim() + "' AND SMSatausNAME != '폐기'");

				rs = st.executeQuery(sql.toString());
				if (rs.next()) {
					map.put("check", "true");
				} else {
					map.put("check", "false");
				}
			} else {
				map.put("check", "false");
			}

			map.put("result", SUCCESS);
			map.put("index", index);
		} catch (Exception e) {
			DBCPManager.freeConnection(con, st, rs);
			map.put("result", FAIL);
			map.put("index", index);
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	public Map<String, Object> getOutputList(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
//		String lotNo = (String) param.get("lotNo");
		int index = 0;// (int) param.get("index");
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		String stdNo = (String) param.get("stdNo");
		String pjtSeq = (String) param.get("pjtSeq");
		String stdReportSeq = (String) param.get("stdReportSeq");
		String ifFlag = (String) param.get("ifFlag");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		ArrayList<Map<String, Object>> list = new ArrayList<>();

		try {
			String pjtSeqValue = "";
			if (pjtSeq != null && pjtSeq.length() > 0) {
				pjtSeq = pjtSeq.trim().toUpperCase();
				String[] ss = ErpHelper.manager.getKEK_VPJTProject(pjtSeq);

				if (ss != null) {
					if (ss[0] != null) {
						pjtSeqValue = ss[0];
					}
				}
			}

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append(
					" SELECT STDNO, (SELECT PJTNO FROM KEK_VPJTProject WHERE A1.PJTSEQ=PJTSEQ), (SELECT STDREPORTNAME FROM KEK_VPJTOutputStdRpt WHERE A1.STDREPORTSEQ=STDREPORTSEQ), CREATE_TIME, FLAG, INTERFACE_DATE, RESULT, USERID ");
			sql.append(" FROM KEK_TPJTOutputRptDo_IF A1 ");
			sql.append(" WHERE ");
			if ("true".equals(ifFlag)) {
				sql.append(" FLAG = '0'");
			} else {
				sql.append(" FLAG IS NULL");
			}
			if (stdNo != null && stdNo.length() > 0) {
				sql.append(" AND STDNO= '" + stdNo + "'");
			}
			if (pjtSeqValue != null && pjtSeqValue.length() > 0) {
				sql.append(" AND PJTSEQ= '" + pjtSeqValue + "'");
			}
			if (stdReportSeq != null && stdReportSeq.length() > 0) {
				sql.append(" AND STDREPORTSEQ = '" + stdReportSeq + "'");
			}

			if (predate != null && predate.length() > 0 && postdate != null && postdate.length() > 0) {

				sql.append(" AND CREATE_TIME BETWEEN '" + DateUtils.convertStartDate(predate) + "' AND '"
						+ DateUtils.convertStartDate(postdate) + "'");
			}

			sql.append(" ORDER BY CREATE_TIME");

			System.out.println("### sql = " + sql);

			rs = st.executeQuery(sql.toString());
			while (rs.next()) {
				// STDNO, PJTSEQ, STDREPORTSEQ, CREATE_TIME, FLAG,
				// INTERFACE_DATE, RESULT, USERID

				String re1 = (String) rs.getString(1);
				String re2 = (String) rs.getString(2);
				String re3 = (String) rs.getString(3);
				String re4 = (String) rs.getString(4);
				String re5 = (String) rs.getString(5);

				String re6 = (String) rs.getString(6);

				// String re6Va = DateUtils.getDateString(re6, "yyyy-MM-dd");

				String re7 = (String) rs.getString(7);
				String re8 = (String) rs.getString(8);

				// System.out.println("##
				// ="+re1+"//"+re2+"//"+re3+"//"+re4+"//"+re5+"//"+re6+"//"+re7);

				Map<String, Object> reMap = new HashMap<String, Object>(); // json

				reMap.put("STDNO", re1);
				reMap.put("PJTSEQ", re2);
				reMap.put("STDREPORTSEQ", re3);
				reMap.put("CREATE_TIME", re4);
				reMap.put("FLAG", re5);
				reMap.put("INTERFACE_DATE", re6);
				reMap.put("RESULT", re7);
				reMap.put("USERID", re8);
				list.add(reMap);
			}
			map.put("result", SUCCESS);
			map.put("index", index);
			map.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
			DBCPManager.freeConnection(con, st, rs);
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	public Map<String, Object> getPjtBomAction(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		int index = 0;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		String resultMsg = (String) param.get("resultMsg");
		String pjtSeq = (String) param.get("pjtSeq");
		String stdReportSeq = (String) param.get("stdReportSeq");
		String ifFlag = (String) param.get("ifFlag");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		System.out.println("##" + resultMsg + "--" + pjtSeq + "--" + stdReportSeq + "--" + ifFlag);

		ArrayList<Map<String, Object>> list = new ArrayList<>();

		try {
			String pjtSeqValue = "";
			if (pjtSeq != null && pjtSeq.length() > 0) {
				pjtSeq = pjtSeq.trim().toUpperCase();
				String[] ss = ErpHelper.manager.getKEK_VPJTProject(pjtSeq);

				if (ss != null) {
					if (ss[0] != null) {
						pjtSeqValue = ss[0];
					}
				}
			}
			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append(
					" SELECT DISNO, (SELECT PJTNO FROM KEK_VPJTProject WHERE A1.PJTSEQ=PJTSEQ), REMARKM, DISTRIBUTIONNO, CREATE_TIME, FLAG, INTERFACE_DATE, RESULT, USERID, ITEMSEQ ");
			sql.append(" FROM KEK_TPJTBOM_IF A1 ");
			sql.append(" WHERE ");
			if ("2".equals(ifFlag)) {
				sql.append(" FLAG = '0' ");
			} else if ("3".equals(ifFlag)) {
				sql.append(" FLAG ='1' ");
			} else {
				sql.append(" FLAG IS NULL ");
			}
			if (resultMsg != null && resultMsg.length() > 0) {
				sql.append(" AND RESULT= '" + resultMsg + "'");
			}
			if (pjtSeq != null && pjtSeq.length() > 0) {
				sql.append(" AND PJTSEQ= '" + pjtSeqValue + "'");
			}
			if (stdReportSeq != null && stdReportSeq.length() > 0) {
				sql.append(" AND STDREPORTSEQ = '" + stdReportSeq + "'");
			}

			if (predate != null && predate.length() > 0 && postdate != null && postdate.length() > 0) {

				sql.append(" AND CREATE_TIME BETWEEN '" + DateUtils.convertStartDate(predate) + "' AND '"
						+ DateUtils.convertStartDate(postdate) + "'");
			}

			sql.append(" ORDER BY CREATE_TIME");

			System.out.println("### sql = " + sql);

			rs = st.executeQuery(sql.toString());
			while (rs.next()) {
				// DISNO, (SELECT PJTNO FROM KEK_VPJTProject WHERE A1.PJTSEQ=PJTSEQ), REMARKM,
				// DISTRIBUTIONNO, CREATE_TIME, FLAG,
				// INTERFACE_DATE, RESULT, USERID

				String re1 = (String) rs.getString(1);
				String re2 = (String) rs.getString(2);
				String re3 = (String) rs.getString(3);
				String re4 = (String) rs.getString(4);

				String re5 = (String) rs.getString(5);

				String re6 = (String) rs.getString(6);

				String re7 = (String) rs.getString(7);
				String re8 = (String) rs.getString(8);
				String re9 = (String) rs.getString(9);
				int re10 = (int) rs.getInt(10);
				// System.out.println("##
				// ="+re1+"//"+re2+"//"+re3+"//"+re4+"//"+re5+"//"+re6+"//"+re7+"//"+re8);

				Map<String, Object> reMap = new HashMap<String, Object>(); // json

				reMap.put("DISNO", re1);
				reMap.put("PJTSEQ", re2);
				reMap.put("REMARKM", re3);
				reMap.put("DISTRIBUTIONNO", re4);
				reMap.put("CREATE_TIME", re5);

				reMap.put("FLAG", re6);
				reMap.put("INTERFACE_DATE", re7);
				reMap.put("RESULT", re8);
				reMap.put("USERID", re9);
				reMap.put("ITEMSEQ", re10);
				list.add(reMap);
			}
			map.put("result", SUCCESS);
			map.put("index", index);
			map.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
			DBCPManager.freeConnection(con, st, rs);
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	public Map<String, Object> getErpPartAction(Map<String, Object> param) {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		int index = 0;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		String resultMsg = (String) param.get("resultMsg");
		String partSpec = (String) param.get("partSpec");
		String stdReportSeq = (String) param.get("stdReportSeq");
		String ifFlag = (String) param.get("ifFlag");

		String predate = (String) param.get("predate");
		String postdate = (String) param.get("postdate");

		System.out.println("##" + resultMsg + "--" + partSpec + "--" + stdReportSeq + "--" + ifFlag);

		ArrayList<Map<String, Object>> list = new ArrayList<>();

		try {
			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append(
					" SELECT A1.PART_NO, A1.PART_NAME, A1.PART_SPEC, A1.USERID, A1.FLAG, A1.CREATE_DATE, A1.INTERFACE_DATE, A1.RESULT  ");
			sql.append(" FROM KEK_TDAItem_IF A1 ");
			sql.append(" WHERE ");
			if ("2".equals(ifFlag)) {
				// 정상처리
				sql.append(" FLAG = '0' ");
			} else if ("3".equals(ifFlag)) {
				// 오류처리
				sql.append(" FLAG ='1' ");
			} else if ("1".equals(ifFlag)) {
				sql.append(" FLAG IS NULL ");
			}
			if (resultMsg != null && resultMsg.length() > 0) {
				sql.append(" AND RESULT= '" + resultMsg + "'");
			}
			if (partSpec != null && partSpec.length() > 0) {
				sql.append(" AND A1.PART_SPEC= '" + partSpec + "'");
			}
			if (stdReportSeq != null && stdReportSeq.length() > 0) {
				sql.append(" AND STDREPORTSEQ = '" + stdReportSeq + "'");
			}

			if (predate != null && predate.length() > 0 && postdate != null && postdate.length() > 0) {

				sql.append(" AND A1.CREATE_DATE BETWEEN '" + DateUtils.convertStartDate(predate) + "' AND '"
						+ DateUtils.convertStartDate(postdate) + "'");
			}

			sql.append(" ORDER BY CREATE_DATE");

			System.out.println("### sql = " + sql);

			rs = st.executeQuery(sql.toString());
			while (rs.next()) {
				// A1.PART_NO, A1.PART_NAME, A1.PART_SPEC,
				// A1.USERID, A1.FLAG, A1.CREATE_DATE,
				// A1.INTERFACE_DATE, A1.RESULT

				String re1 = (String) rs.getString(1);
				String re2 = (String) rs.getString(2);
				String re3 = (String) rs.getString(3);

				String re4 = (String) rs.getString(4);
				String re5 = (String) rs.getString(5);
				String re6 = (String) rs.getString(6);

				String re7 = (String) rs.getString(7);
				String re8 = (String) rs.getString(8);
				// System.out.println("##
				// ="+re1+"//"+re2+"//"+re3+"//"+re4+"//"+re5+"//"+re6+"//"+re7+"//"+re8);

				Map<String, Object> reMap = new HashMap<String, Object>(); // json

				reMap.put("PART_NO", re1);
				reMap.put("PART_NAME", re2);
				reMap.put("PART_SPEC", re3);
				reMap.put("USERID", re4);
				reMap.put("FLAG", re5);

				reMap.put("CREATE_TIME", re6);
				reMap.put("INTERFACE_DATE", re7);
				reMap.put("RESULT", re8);

				list.add(reMap);
			}
			map.put("result", SUCCESS);
			map.put("index", index);
			map.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
			DBCPManager.freeConnection(con, st, rs);
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	public Map<String, Object> getMainDataAction() {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		int index = 0;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

//		String flagSql = "";

//		String[] tableName = {
//				
//				"KEK_TPJTOutputRptDo_IF",
//				"KEK_TPJTBOM_IF",
//				"KEK_TDAItem_IF",
//				"KEK_TPJTUnitBOM_IF"
//		
//		};

		ArrayList<Map<String, Object>> list = new ArrayList<>();

		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			/*
			 * SELECT count(distinct(disno)) FROM KEK_TPJTBOM_IF WHERE FLAG IS NOT NULL AND
			 * RESULT!= '정상처리';
			 */
			sql.append(" SELECT COUNT(*) ");
			sql.append(" FROM KEK_TPJTBOM_IF ");
			sql.append(" WHERE ");

			rs = st.executeQuery(sql.toString());
			while (rs.next()) {
//				int re1 = (int) rs.getInt(1);

				Map<String, Object> reMap = new HashMap<String, Object>(); // json

				list.add(reMap);
			}

			map.put("result", SUCCESS);
			map.put("index", index);
			map.put("list", list);
		} catch (Exception e) {
			e.printStackTrace();
			map.put("result", FAIL);
			map.put("msg", FAIL_DATA_LOAD);
			DBCPManager.freeConnection(con, st, rs);
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	/**
	 * 부품 일괄 등록시(YCODE NG 시) ERP 조회 해서 데이터 가져오기
	 */
	public Map<String, Object> bundleGetErpData(String spec) throws Exception {

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;

		try {
			con = DBCPManager.getConnection(erpName);
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
				sb.append("EXEC KEK_SPLMBaseGetPrice '" + itemSeq + "', '', '1'");

				ResultSet result = st.executeQuery(sb.toString());
				String maker = "";
				String customer = "";
				String unit = "";
				String price = "";
				String currency = "";
				String rate = "";

				if (result.next()) {
					maker = (String) result.getString("MakerName");
					customer = (String) result.getString("CustName");
					unit = (String) result.getString("UnitName");
					currency = (String) result.getString("CurrName");
					price = (String) result.getString("Price");
					rate = (String) result.getString("ExRate");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		return null;
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

			System.out.println("cacheData=" + cacheData);

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
				System.out.println("캐싱 데이터로 가져오는건지?");
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
		String partNo = "";
		try {
			
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			ErpConnectionPool.free(con, st, rs);
		}
		return partNo;
	}
}