package e3ps.common.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

public class DBConnectionManager {
	static private DBConnectionManager instance;
	static private int clients;

	private Vector<Driver> drivers = new Vector<Driver>();
	// private PrintWriter log;
	private Hashtable<String, DBConnectionPool> pools = new Hashtable<String, DBConnectionPool>();

	static synchronized public DBConnectionManager getInstance() throws IOException {
		if (instance == null) {
			instance = new DBConnectionManager();
		}
		clients++;

		return instance;
	}

	private DBConnectionManager() throws IOException {
		init();
	}

	private void init() throws IOException {
		InputStream is = getClass().getResourceAsStream("/e3ps/db.properties");
		Properties props = new Properties();
		try {
			props.load(is);
		} catch (Exception e) {
			return;
		}
		loadDrivers(props);
		createPool(props);
	}

	private void loadDrivers(Properties props) {
		Enumeration propNames = props.propertyNames();
		String className = props.getProperty("driver");
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			if (name.endsWith(".driver")) {
				String poolName = name.substring(0, name.lastIndexOf("."));
				className = props.getProperty(poolName + ".driver");
			}
		}

		StringTokenizer st = new StringTokenizer(className);
		while (st.hasMoreElements()) {
			String stName = st.nextToken().trim();
			try {
				Driver driver = (Driver) Class.forName(stName).newInstance();
				DriverManager.registerDriver(driver);
				drivers.addElement(driver);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void freeConnection(String name, Connection con) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			pool.freeConnection(con);
		}
	}

	public Connection getConnection(String name) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			return pool.getConnection();
		}
		return null;
	}

	public Connection getConnection(String name, long time) {
		DBConnectionPool pool = (DBConnectionPool) pools.get(name);
		if (pool != null) {
			return pool.getConnection(time);
		}
		return null;
	}

	public synchronized void release() {
		if (--clients != 0) {
			return;
		}

		Enumeration allPools = pools.elements();
		while (allPools.hasMoreElements()) {
			DBConnectionPool pool = (DBConnectionPool) allPools.nextElement();
			pool.release();
		}

		Enumeration allDrivers = drivers.elements();
		while (allDrivers.hasMoreElements()) {
			Driver driver = (Driver) allDrivers.nextElement();
			try {
				DriverManager.deregisterDriver(driver);
			} catch (SQLException e) {

			}
		}
	}

	private void createPool(Properties props) {
		Enumeration propNames = props.propertyNames();
		while (propNames.hasMoreElements()) {
			String name = (String) propNames.nextElement();
			if (name.endsWith(".url")) {
				String poolName = name.substring(0, name.lastIndexOf("."));
				String url = props.getProperty(poolName + ".url");
				if (url == null) {
					continue;
				}
				String user = props.getProperty(poolName + ".user");
				String password = props.getProperty(poolName + ".password");
				String maxconn = props.getProperty(poolName + ".maxconn", "0");
				int max;
				try {
					max = Integer.valueOf(maxconn).intValue();
				} catch (NumberFormatException e) {
					max = 0;
				}

				DBConnectionPool pool = new DBConnectionPool(poolName, url, user, password, max);
				pools.put(poolName, pool);
			} // end if
		}
	}
}