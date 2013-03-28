package com.comdosoft.serverReadZip;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MysqlDAOImpl extends BaseDAO {
	private Connection conn;
	private PreparedStatement psmt;

	public void addData(String tableName, String param, List<String[]> par)
			throws IOException {
		String sql = "insert into " + tableName + " values(" + param + ")";
		try {
			conn = getConn();
			psmt = conn.prepareStatement(sql);
			for (int i = 0; i < par.size(); i++) {
				if (par.get(i)[1].equals("string")) {
					psmt.setString(i + 1, par.get(i)[0]);
				}
				if (par.get(i)[1].equals("num")) {
					psmt.setInt(i + 1, Integer.parseInt(par.get(i)[0]));
				}
				if (par.get(i)[1].equals("float")) {
					psmt.setFloat(i + 1, Float.parseFloat(par.get(i)[0]));
				}
			}
			psmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println(sql);
	}
}