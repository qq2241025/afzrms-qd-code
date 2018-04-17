/**
 * 
 */
package com.mapabc.gater.lbsgateway.service.ext;

import java.io.Serializable;

/**
 * @author 
 *
 */
public class Property implements Serializable {
	
	private String name;
	
	private String type;
	
	private String column;

	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the column
	 */
	public String getColumn() {
		return this.column;
	}

	/**
	 * @param column the column to set
	 */
	public void setColumn(String column) {
		this.column = column;
	}
	
	

}
