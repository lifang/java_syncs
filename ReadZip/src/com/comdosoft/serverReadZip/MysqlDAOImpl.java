package com.comdosoft.serverReadZip;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MysqlDAOImpl extends BaseDAO {
	private Connection conn;
	private PreparedStatement psmt;
	private ResultSet rs;
	private int count = 0;

	public void addData(String tableName, String param, List<String[]> par)
			throws IOException {

		String sql = "insert into " + tableName + " values(" + param + ")";
		System.out.println(sql);
		try {
			try {
				conn = getConnection();
				psmt = conn.prepareStatement(sql);
				for (int i = 0; i < par.size(); i++) {
					if (par.get(i)[1].equals("string")) {
						psmt.setString(i + 1, par.get(i)[0]);
					}
					if (par.get(i)[1].equals("num")
							&& par.get(i)[0].length() < 10) {
						psmt.setInt(i + 1, Integer.parseInt(par.get(i)[0]));
					}
					if (par.get(i)[1].equals("float")
							&& par.get(i)[0].length() < 10) {
						psmt.setFloat(i + 1, Float.parseFloat(par.get(i)[0]));
					}
					if (par.get(i)[1].equals("boolean")) {
						psmt.setBoolean(i + 1,
								par.get(i)[0].equals("true") ? true : false);
					} else {
						psmt.setString(i + 1, par.get(i)[0]);
					}
				}
				psmt.executeUpdate();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					closeConnection(conn);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			// System.out.println("该条记录已添加!");
		}
	}

	public void update(String tableName, String param, List<String[]> par,
			List<String> setParam) {
		StringBuffer sql = new StringBuffer();
		sql.append("update ").append(tableName).append(" set ");
		for (int i = 1; i < setParam.size(); i++) {
			sql.append(setParam.get(i)).append(" = ");
			sql.append("? ,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append("where id = ?");
		System.out.println(sql.toString());
		try {
			conn = getConnection();
			psmt = conn.prepareStatement(sql.toString());
			for (int i = 1; i < par.size(); i++) {
				if (par.get(i)[1].equals("string")) {
					psmt.setString(i, par.get(i)[0]);
				}
				if (par.get(i)[1].equals("num") && par.get(i)[0].length() < 10) {
					psmt.setInt(i, Integer.parseInt(par.get(i)[0]));
				}
				if (par.get(i)[1].equals("float")
						&& par.get(i)[0].length() < 10) {
					psmt.setFloat(i, Float.parseFloat(par.get(i)[0]));
				}
				if (par.get(i)[1].equals("boolean")) {
					psmt.setBoolean(i, par.get(i)[0].equals("true") ? true
							: false);
				} else {
					psmt.setString(i, par.get(i)[0]);
				}
			}
			psmt.setInt(par.size(), Integer.parseInt(par.get(0)[0]));
			psmt.executeUpdate();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			System.out.println("错误:" + count++);
			System.out.println("表:" + tableName + "----id:" + par.get(0)[0]);
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
	}

	public List<String> getId(String tableName) {
		List<String> idList = new ArrayList<String>();
		try {
			try {
				conn = getConnection();
				Statement statement = conn.createStatement();
				String sql = "select id from " + tableName + " ";
				rs = statement.executeQuery(sql);
				while (rs.next()) {
					idList.add(String.valueOf(rs.getInt(1)));
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					closeConnection(conn);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			System.out.println(tableName + "表出错!");
			e.printStackTrace();
		}
		return idList;
	}
}