/**
 * 
 */
package com.mapabc.gater.lbsgateway.service.ext;

import java.io.Serializable;

/**
 * @author 
 *
 */
public class ExtStatusConfig implements Serializable{
	
	private String className;
	
	private String tableName;
	
	private Property[] properteis;

	/**
	 * @return the properteis
	 */
	public Property[] getProperteis() {
		return this.properteis;
	}

	/**
	 * @param properteis the properteis to set
	 */
	public void setProperteis(Property[] properteis) {
		this.properteis = properteis;
	}

	/**
	 * @return the className
	 */
	public String getClassName() {
		return this.className;
	}

	/**
	 * @param className the className to set
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @return the tableName
	 */
	public String getTableName() {
		return this.tableName;
	}

	/**
	 * @param tableName the tableName to set
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	

}
