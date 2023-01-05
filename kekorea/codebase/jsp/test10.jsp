<%@page import="e3ps.common.util.StringUtils"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="wt.fc.ReferenceFactory"%>
<%@page import="e3ps.part.UnitBom"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="e3ps.common.db.DBCPManager"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%
Map<String, Object> map = new HashMap<String, Object>(); // json
String yCode = "Y200055984";
String qty =null;
int index =0;
// DBConnectionManager instance = null;
Connection con = null;
Statement st = null;
ResultSet rs = null;
try {
	// instance = DBConnectionManager.getInstance();
	if (!StringUtils.isNull(yCode)) {
		con = DBCPManager.getConnection("erp");
		st = con.createStatement();

		StringBuffer sql = new StringBuffer();

		sql.append("SELECT ItemSeq, ItemName, Spec");
		sql.append(" from KEK_VDAItem");
		sql.append(" WHERE ItemNo='" + yCode.trim() + "' AND SMSatausNAME != '폐기'");
		//sql.append("SELECT PJTSeq, PJTName, PJTNo from KEK_VPJTProject;" );
		System.out.println("<br>sql=" + sql.toString());

		if (StringUtils.isNull(qty)) {
			qty = "1";
		}
		System.out.println("<br>qty=" + qty);
		rs = st.executeQuery(sql.toString());
		System.out.println("<br>나오나용?=");
		if (rs.next()) {
			//String ItemName = (String) rs.getString(2);
			//out.print(ItemName);
			 System.out.println("<br>나오나?=");
			int ItemSeq = (int) rs.getInt(1);
			String ItemName = (String) rs.getString(2);
			String Spec = (String) rs.getString(3);
			System.out.println("<br>ItemSeq=" + ItemSeq);
			System.out.println("<br>ItemName=" + ItemName);
			System.out.println("<br>Spec=" + Spec);
			StringBuffer sb = new StringBuffer();
			sb.append("EXEC KEK_SPLMBaseGetPrice '" + ItemSeq + "', '', '" + qty.trim() + "'");

			ResultSet result = st.executeQuery(sb.toString());
			System.out.println("<br>EXEC=" + sb.toString());
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
			System.out.println("<br>ExRate=" + ExRate);
			System.out.println("<br>ItemName=" + ItemName);
			System.out.println("<br>Spec=" + Spec);
			System.out.println("<br>MakerName=" + MakerName);
			System.out.println("<br>CustName=" + CustName);
			System.out.println("<br>UnitName=" + UnitName);
			System.out.println("<br>Price=" +  price.substring(0, price.lastIndexOf(".")));

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

			System.out.println("<br>Ws=" + Ws);

			map.put("won", ext.trim() + Ws.substring(0, Ws.lastIndexOf("."))); 

		}
	}
	
	map.put("result",  "SUCCESS");
	map.put("index", index);
} catch (Exception e) {
	e.printStackTrace();
	map.put("result", "FAIL");
	map.put("msg", "데이터 로드 중 에러가 발생하였습니다.\n시스템 관리자에게 문의하세요.");
	DBCPManager.freeConnection(con, st, rs);
} finally {
	DBCPManager.freeConnection(con, st, rs);
}


System.out.println("<br>result=" + map.get("msg"));
%>