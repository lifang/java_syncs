package com.comdosoft.uploadimage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlDAOImpl extends BaseDAO {
	private Connection conn;

	public List<String> getId(String tableName) {
		List<String> idList = new ArrayList<String>();
		try {
			try {
				conn = getConnection();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Statement statement = conn.createStatement();
			String sql = "select id from " + tableName
					+ " where TO_DAYS(updated_at)=TO_DAYS(now())";
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				idList.add(String.valueOf(rs.getInt(1)));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection(conn);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return idList;
	}

	public Integer getStoresId() {
		int id = 0;
		try {
			try {
				conn = getConnection();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Statement statement = conn.createStatement();
			String sql = "select id from stores";
			ResultSet rs = statement.executeQuery(sql);
			if (rs.next()) {
				id = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				closeConnection(conn);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return id;
	}
}