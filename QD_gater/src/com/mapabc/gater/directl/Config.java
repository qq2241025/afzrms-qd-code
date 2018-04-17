package com.mapabc.gater.directl;

import java.util.Properties;
import java.io.*;

public class Config {
	private static Config instance = null;
	private static Properties configProp = new Properties();
	private static Properties dbProp = new Properties();

	public static synchronized Config getInstance() {
		if (instance == null) {
			instance = new Config();

			InputStream is = Config.class
					.getResourceAsStream("/config.properties");
			if (is != null) {
				try {
					configProp.load(is);
					is.close();
				} catch (IOException ex) {
				}
			}
			is = Config.class.getResourceAsStream("/db.properties");

			if (is != null) {
				try {
					dbProp.load(is);
					is.close();
				} catch (IOException ex) {
				} finally {
					if (is != null) {
						try {
							is.close();
						} catch (Exception ex) {

						}
					}
				}
			}

		}
		return instance;
	}

	private Config() {

	}

	public String getString(String prop) {
		return configProp.getProperty(prop);
	}

	public String getDBString(String prop) {
		return dbProp.getProperty(prop);

	}

	public String getProperty(String prop) {
		return getString(prop);
	}
}
