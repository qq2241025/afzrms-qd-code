package com.mapabc.gater.directl.dbutil;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.LogFactory;

/**
 * @author a
 * 
 */
public class DbOperation {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(DbOperation.class);

	/**
	 * 建立数据库连接
	 * 
	 * @return boolean
	 */
	public static Connection getConnection() {
		Connection conn = null;
		conn = DbUtil.getConnection();

		return conn;
	}

	/**
	 * 释放资源
	 */
	public static void release(Statement stmt, ResultSet rs,
			PreparedStatement pstmt, CallableStatement cst, Connection con) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
			if (pstmt != null) {
				pstmt.close();
			}
			if (cst != null) {
				cst.close();
			}
			if (con != null && !con.isClosed()) {
				con.close();
				con = null;
			}
		} catch (SQLException e) {
			log.error("关闭数据库资源异常：" + e.getMessage(), e);
			e.printStackTrace();
		}
	}
	
	/**
	 * 释放资源
	 */
	public  static void releaseDbResource(ResultSet rs, Statement[] stmt, Connection con) {
		try {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null && stmt.length>0){
				for (int i=0; i<stmt.length; i++){
					Statement s = stmt[i];
					if (s != null){
						s.close();
					}
				}
			}
			if (con != null && !con.isClosed()) {
				con.close();
				con = null;
			}
		} catch (SQLException e) {
			log.error("关闭数据库资源异常："+e.getMessage(), e);
			e.printStackTrace();
		}
	}

	/**
	 * 批量执行多条非查询SQL语句
	 * 
	 * @param sql
	 * @author 
	 */
	public static void executeSql(String[] sqls) throws Exception {
		if (sqls == null || sqls.length < 0)
			return;

		Connection conn = DbUtil.getConnection();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();

			for (int i = 0; i < sqls.length; i++) {
				stmt.addBatch(sqls[i]);
			}

			stmt.executeBatch();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			release(stmt, null, null, null, conn);
		}
	}

}
