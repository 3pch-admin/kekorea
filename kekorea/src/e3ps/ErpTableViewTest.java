package e3ps;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import e3ps.common.db.DBConnectionManager;

public class ErpTableViewTest {

	public static void main(String[] args) throws Exception {
		DBConnectionManager instance = null;
		Connection con = null;
		try {
			instance = DBConnectionManager.getInstance();
			con = instance.getConnection("erp");
			// TCW_PART_IF
			Statement st = con.createStatement();

			StringBuffer sql = new StringBuffer();
			sql.append("SELECT ItemSeq, ItemName, Spec");
			sql.append(" from KEK_VDAItem");
			sql.append(" WHERE ItemNo='Y200055974' AND SMSatausNAME != '폐기'");
			
			
//			sql.append("SELECT MakerSeq, MakerName");
//			sql.append(" from KEK_VDAMAKER");
			// sql.append(" WHERE ItemName='Y200060564'");

			System.out.println("sq=" + sql.toString());

			ResultSet rs = st.executeQuery(sql.toString());
			int i = 0;
			while (rs.next()) {
				// int DesignType = (int) rs.getInt(1);
//				String StdReportSeq = (String) rs.getString(1);
//				String StdReportName = (String) rs.getString(2);
//				String StdReportName = (String) rs.getString(2);
				String StdReportName = (String) rs.getString(3);
				// System.out.println("DesignTypeName=" + DesignTypeName + ",==" + DesignType);
//				System.out.println("StdReportSeq=" + StdReportSeq + "=======" + i);
				System.out.println("StdReportName=" + StdReportName + "=======" + i);
				 i++;
			}

			// DesignTypeName=KRW=======0
			// DesignTypeName=USD=======1
			// DesignTypeName=JPY=======2

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			instance.freeConnection("erp", con);
		}
		System.out.println("종료.");
		System.exit(0);
	}
}
