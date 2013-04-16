package com.comdosoft.serverReadZip;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class BaseDAO {
	// jdbc:mysql://192.168.0.250:3306/lantan_db_all?useUnicode=true&characterEncoding=UTF-8
	private String dbUrl = "jdbc:mysql://192.168.1.100:3306/lantan_db_all?useUnicode=true&characterEncoding=UTF-8";
	private String connUrl = "jdbc:mysql://192.168.1.100:3306/lantan_db_all?useUnicode=true&characterEncoding=UTF-8";
	private int poolSize = 5;
	private String driver = "org.gjt.mm.mysql.Driver";
	private Map<java.sql.Connection, String> connectionPool = null;

	{
		initPool();
	}

	private void initPool() {
		try {
			connectionPool = new HashMap<java.sql.Connection, String>();
			Class.forName(driver);
			java.sql.Connection con = DriverManager.getConnection(dbUrl,"root","");
			for (int i = 0; i < poolSize; i++) {
				connectionPool.put(con, "AVAILABLE");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() throws ClassNotFoundException,
			SQLException {
		boolean isConnectionAvailable = true;
		for (Entry<java.sql.Connection, String> entry : connectionPool
				.entrySet()) {
			synchronized (entry) {
				if (entry.getValue() == "AVAILABLE") {
					entry.setValue("NOTAVAILABLE");
					return (java.sql.Connection) entry.getKey();
				}
				isConnectionAvailable = false;
			}
		}
		if (!isConnectionAvailable) {
			Class.forName(driver);
			java.sql.Connection con = DriverManager.getConnection(connUrl,"root","");
			connectionPool.put(con, "NOTAVAILABLE");
			return con;
		}
		return null;
	}

	public void closeConnection(java.sql.Connection connection)
			throws ClassNotFoundException, SQLException {
		for (Entry<java.sql.Connection, String> entry : connectionPool
				.entrySet()) {
			synchronized (entry) {
				if (entry.getKey().equals(connection)) {
					entry.setValue("AVAILABLE");
				}
			}
		}
	}
}
