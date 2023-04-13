package e3ps.erp;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.apache.commons.dbcp2.BasicDataSource;;

public class ErpConnectionPool {

	private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private static final String URL = "jdbc:sqlserver://211.171.82.73:14233;databasename=KEK";
//	private static final String URL = "jdbc:sqlserver://211.171.82.73:14233;databasename=KEKPLM_IF";
	private static final String USERNAME = "plm_e3ps";
	private static final String PASSWORD = "proe2015!";
	private static final int MAX_TOTAL = 100; // 최대 생성 가능한 Connection 수
	private static final int MAX_IDLE = 50; // 최대 유휴 Connection 수
	private static final int MIN_IDLE = 10; // 최소 유휴 Connection 수

	private static final BasicDataSource dataSource;

	static {
		dataSource = new BasicDataSource();
		dataSource.setDriverClassName(DRIVER);
		dataSource.setUrl(URL);
		dataSource.setUsername(USERNAME);
		dataSource.setPassword(PASSWORD);
		dataSource.setMaxTotal(MAX_TOTAL);
		dataSource.setMaxIdle(MAX_IDLE);
		dataSource.setMinIdle(MIN_IDLE);
	}

	public static BasicDataSource getDataSource() {
		return dataSource;
	}

	public static void free(Connection con, Statement st, ResultSet rs) throws Exception {
		System.out.println("Free Start");
		if (con != null) {
			con.close();
		}

		if (st != null) {
			st.close();
		}

		if (rs != null) {
			rs.close();
		}
	}
}
