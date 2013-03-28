package com.comdosoft.serverReadZip;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseDAO {
	public Connection getConn() {
		try {
			Class.forName("org.gjt.mm.mysql.Driver");
			return DriverManager.getConnection("jdbc:mysql://192.168.0.250:3306/lantan_db_all?useUnicode=true&characterEncoding=UTF-8", "root", "comdo2012");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void close(ResultSet rs, Statement psmt, Connection conn) {
		try {
			if (rs != null) {
				rs.close();
			}
			if(psmt!=null){
				psmt.close() ;
			}
			if(conn!=null && !conn.isClosed()){
				conn.close() ;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
}
