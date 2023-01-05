package e3ps.common.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

public class DBConnectionPool {
	private int checkedOut;
	private Vector<Connection> freeConnection = new Vector<Connection>();
	private int maxConn;
	private String name;
	private String password;
	private String URL;
	private String user;

	public DBConnectionPool(String name, String URL, String user, String password, int maxConn) {
		this.name = name;
		this.URL = URL;
		this.user = user;
		this.password = password;
		this.maxConn = maxConn;
	}

	public synchronized void freeConnection(Connection con) {
		freeConnection.addElement(con);
		checkedOut--;
		if (checkedOut < 0) {
			checkedOut = 0;
		}
		notifyAll();
	}

	public synchronized Connection getConnection() {
		Connection con = null;
		if (freeConnection.size() > 0) {
			con = (Connection) freeConnection.firstElement();
			freeConnection.removeElementAt(0);
			try {
				if (con.isClosed()) {
					con = getConnection();
				}
			} catch (SQLException e) {
				System.out.println(name);
				// con = getConnection();
			}
		} else if (maxConn == 0 || checkedOut < maxConn) {
			con = newConnection();
		}
		if (con != null) {
			checkedOut++;
		}
		return con;
	}

	public synchronized Connection getConnection(long timeout) {
		long startTime = new Date().getTime();
		Connection con;
		while ((con = getConnection()) == null) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if ((new Date().getTime() - startTime) >= timeout) {
				return null;
			}
		}
		return con;
	}

	public synchronized void release() {
		Enumeration connections = freeConnection.elements();
		while (connections.hasMoreElements()) {
			Connection con = (Connection) connections.nextElement();
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		freeConnection.removeAllElements();
	}

	private Connection newConnection() {
		Connection con = null;
		try {
			if (user == null) {
				con = DriverManager.getConnection(URL);
			} else {
				con = DriverManager.getConnection(URL, user, password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return con;
	}
}