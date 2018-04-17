/**
 * 
 */
package com.mapabc.gater.lbsgateway.bean;

import java.io.Serializable;
import java.util.Hashtable;

/**
 * @author 
 *
 */
public class TableConfigBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Hashtable<String, String>   columns;
	
	private String tableName;
	
	private String sequence;

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return this.sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

  
	/**
	 * @return the columns
	 */
	public Hashtable<String, String> getColumns() {
		return this.columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(Hashtable<String, String> columns) {
		this.columns = columns;
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
