package e3ps.erp.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp2.BasicDataSource;

import e3ps.bom.partlist.cache.CacheProcessor;
import e3ps.common.db.DBCPManager;
import e3ps.common.util.DateUtils;
import e3ps.common.util.StringUtils;
import e3ps.epm.service.EpmHelper;
import e3ps.erp.ErpConnectionPool;
import wt.services.ServiceFactory;

public class ErpHelper {

	public static final boolean isOperation = false;

	public static final String OUTPUT_PATH = "";

	public static final String EPM_PATH = "";

	public static boolean isSendERP = true;

	public static final String erpName = "erp";

	public static final ErpService service = ServiceFactory.getService(ErpService.class);
	public static final ErpHelper manager = new ErpHelper();

	private static final BasicDataSource dataSource = ErpConnectionPool.getDataSource();

	public String[] getKEK_VDAItem(String yCode) throws Exception {
		String[] values = new String[2];

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT ItemSeq, ItemName");
			sql.append(" from KEK_VDAItem");
			sql.append(" WHERE ItemNo='" + yCode + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int ItemSeq = (int) rs.getInt(1);
				String ItemName = (String) rs.getString(2);
				values[0] = String.valueOf(ItemSeq);
				values[1] = ItemName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}

		return values;
	}

	/**
	 * 품목정보 조회
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public Map<String, Object> getKEK_VDAItem(Map<String, Object> param) throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(); // json
		String yCode = (String) param.get("yCode");
		String qty = (String) param.get("qty");
		int index = (int) param.get("index");
		// DBConnectionManager instance = null;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			// instance = DBConnectionManager.getInstance();
			if (!StringUtils.isNull(yCode)) {
				con = DBCPManager.getConnection(erpName);
				st = con.createStatement();

				StringBuffer sql = new StringBuffer();

				sql.append("SELECT ItemSeq, ItemName, Spec");
				sql.append(" from KEK_VDAItem");
				sql.append(" WHERE ItemNo='" + yCode.trim() + "' AND SMSatausNAME != '폐기'");

				System.out.println("sql=" + sql.toString());

				if (StringUtils.isNull(qty)) {
					qty = "1";
				}

				rs = st.executeQuery(sql.toString());
				if (rs.next()) {

					int ItemSeq = (int) rs.getInt(1);
					String ItemName = (String) rs.getString(2);
					String Spec = (String) rs.getString(3);

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
					map.put("ExRate", ExRate);
					map.put("ItemName", ItemName);
					map.put("Spec", Spec);
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

					Won = Won.replaceAll(",", "");

					double Won1 = Double.parseDouble(Won);

					double Won2 = Math.floor(Won1);

					String Ws = String.format("%,f", Won2);

					System.out.println("Ws=" + Ws);

					map.put("won", ext.trim() + Ws.substring(0, Ws.lastIndexOf(".")));

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
			DBCPManager.freeConnection(con, st, rs);
		}
		return map;
	}

	/**
	 * 조달구분
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String[] getKEK_VDASupplyType(String classification) throws Exception {
		String[] values = new String[2];
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT UMSupplyType, UMSupplyTypeName from KEK_VDASupplyType where UMSupplyTypeName='"
					+ classification + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int UMSupplyType = (int) rs.getInt(1);
				String UMSupplyTypeName = (String) rs.getString(2);

				values[0] = String.valueOf(UMSupplyType);
				values[1] = UMSupplyTypeName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
//			instance.freeConnection(erpName, con);
		}
		return values;
	}

	/**
	 * LOT NO
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String[] getKEK_VDALotNo(String lotNo) throws Exception {
		String[] values = new String[3];
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT LotSeq, LotNo, LotUnitName from KEK_VDALotNo where LotNo='" + lotNo + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int LotSeq = (int) rs.getInt(1);
				String LotNo = (String) rs.getString(2);
				String LotUnitName = (String) rs.getString(3);
				values[0] = String.valueOf(LotSeq);
				values[1] = LotNo;
				values[2] = LotUnitName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
//			instance.freeConnection(erpName, con);
		}
		return values;
	}

	/**
	 * 설계구분
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String[] getKEK_VDADesignType(String engType) throws Exception {
		String[] values = new String[2];
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append(
					"SELECT DesignType, DesignTypeName from KEK_VDADesignType where DesignTypeName='" + engType + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int DesignType = (int) rs.getInt(1);
				String DesignTypeName = (String) rs.getString(2);
				// String IsCfm = (String) rs.getString(4);
				values[0] = String.valueOf(DesignType);
				values[1] = DesignTypeName;

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return values;
	}

	/**
	 * 작번
	 * 
	 * @param PJTNo
	 * @return
	 * @throws Exception
	 */
	public String[] getKEK_VPJTProject(String kekNumber) throws Exception {
		String[] values = new String[3];
//		DBConnectionManager instance = null;
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT PJTSeq, PJTName, PJTNo from KEK_VPJTProject where PJTNo='" + kekNumber + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int PJTSeq = (int) rs.getInt(1);
				String PJTName = (String) rs.getString(2);
				String PJTNo = (String) rs.getString(3);
				// String IsCfm = (String) rs.getString(4);
				values[0] = String.valueOf(PJTSeq);
				values[1] = PJTName;
				values[2] = PJTNo;
				// values[3] = IsCfm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return values;
	}

	public String[] getKEK_VPJTProject_NAME(String pjtSeq) throws Exception {
		String[] values = new String[3];
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT PJTSeq, PJTName, PJTNo from KEK_VPJTProject where PJTSEQ='" + pjtSeq + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int PJTSeq = (int) rs.getInt(1);
				String PJTName = (String) rs.getString(2);
				String PJTNo = (String) rs.getString(3);
				// String IsCfm = (String) rs.getString(4);
				values[0] = String.valueOf(PJTSeq);
				values[1] = PJTName;
				values[2] = PJTNo;
				// values[3] = IsCfm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return values;
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
	 * 통화
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String[] getKEK_VDACURR(String currency) throws Exception {
		String[] values = new String[2];
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT CurrSeq, CurrName from KEK_VDACURR where CurrName='" + currency + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int CurrSeq = (int) rs.getInt(1);
				String CurrName = (String) rs.getString(2);

				values[0] = String.valueOf(CurrSeq);
				values[1] = CurrName;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return values;
	}

	/**
	 * 기본 구매처
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public String[] getKEK_VDAPURCUST(String customer) throws Exception {
		String[] values = new String[3];
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT CustSeq, CustName, BizNo from KEK_VDAPURCUST where CustName='" + customer + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int CustSeq = (int) rs.getInt(1);
				String CustName = (String) rs.getString(2);
				String BizNo = (String) rs.getString(3);

				values[0] = String.valueOf(CustSeq);
				values[1] = CustName;
				values[2] = BizNo;

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return values;
	}

	/**
	 * 메이커
	 * 
	 * @param makerName
	 * @return
	 * @throws Exception
	 */
	public String[] getKEK_VDAMAKER(String makerName) throws Exception {
		String[] values = new String[2];
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT MakerSeq, MakerName from KEK_VDAMAKER WHERE MakerName='" + makerName + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int MakerSeq = (int) rs.getInt(1);
				String MakerName = (String) rs.getString(2);

				values[0] = String.valueOf(MakerSeq);
				values[1] = MakerName;

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return values;
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

	/**
	 * 기준단위
	 * 
	 * @param unitName
	 * @return
	 * @throws Exception
	 */
	public String[] getKEK_VDAUnit(String unitName) throws Exception {
		String[] values = new String[2];
		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {

			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();

			StringBuffer sql = new StringBuffer();

			sql.append("SELECT UnitSeq, UnitName from KEK_VDAUnit WHERE UnitName='" + unitName + "'");

			rs = st.executeQuery(sql.toString());
			if (rs.next()) {
				int UnitSeq = (int) rs.getInt(1);
				String UnitName = (String) rs.getString(2);
				values[0] = String.valueOf(UnitSeq);
				values[1] = UnitName;

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBCPManager.freeConnection(con, st, rs);
		}
		return values;
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

	public int getMaxSeq(String tableName) {
		int seq = 0;

		Connection con = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			// TCW_PART_IF
			con = DBCPManager.getConnection(erpName);
			st = con.createStatement();
			String sql = "SELECT MAX(SEQ) FROM " + tableName;

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
			CacheProcessor cache = new CacheProcessor();
			System.out.println("캐시 데이터 확인 해보기 = " + cache.get());
			Map<String, Object> cacheData = cache.getValue(cacheKey);
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
						maker = (String) _rs.getString("MakerName");
						customer = (String) _rs.getString("CustName");
						unit = (String) _rs.getString("UnitName");
						currency = (String) _rs.getString("CurrName");
						price = (int) _rs.getInt("price");
						exchangeRate = (int) _rs.getInt("ExRate");

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

					cache.setValue(cacheKey, result);
				} else {
					result = cache.getValue(cacheKey);
				}
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
					maker = (String) _rs.getString("MakerName");
					customer = (String) _rs.getString("CustName");
					unit = (String) _rs.getString("UnitName");
					currency = (String) _rs.getString("CurrName");
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
}