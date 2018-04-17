package com.mapabc.gater.directl.dbutil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.Context;
import javax.sql.DataSource;

import org.apache.commons.logging.LogFactory;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.directl.Config;

/**
 * @author a
 *
 */
public class DbUtil {
	private static org.apache.commons.logging.Log log = LogFactory.getLog(DbUtil.class);

	public static Context initContext;

	public static Context ctx;

	public static DataSource ds;

	public static Connection getDirectConnection() {
		String url = Config.getInstance().getString("database.url");
		String username = Config.getInstance().getString("database.user");
		String password = Config.getInstance().getString("database.password");
		String driver = Config.getInstance().getString("database.driver");
		try {
			Class.forName(driver).newInstance();
			return DriverManager.getConnection(url, username, password);
		} catch (InstantiationException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (SQLException e) {
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取数据库连接 getConnection
	 * 
	 * @param name
	 * @return
	 */

	public static Connection getConnection() {
		try {
			DataSource ds = (DataSource) AppCtxServer.getInstance().getBean("dataSource");
			return ds.getConnection();
		} catch (Exception e) {
			log.error("获取数据连接异常", e);
			e.printStackTrace();
		}

		return null;
	}
}
