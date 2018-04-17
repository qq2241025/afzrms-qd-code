package com.mapabc.gater.lbmp.bean;

import java.io.Serializable;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年1月29日 上午10:59:08
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class DPoint implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public double x;
	public double y;
	public String encryptX;
	public String encryptY;

	public DPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public DPoint(String encryptX, String encryptY) {
		this.encryptX = encryptX;
		this.encryptY = encryptY;
	}

	public DPoint() {
		this.x = 0.0D;
		this.y = 0.0D;
	}

	public double getX() {
		return this.x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return this.y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public String getEncryptX() {
		return this.encryptX;
	}

	public void setEncryptX(String encryptX) {
		this.encryptX = encryptX;
	}

	public String getEncryptY() {
		return this.encryptY;
	}

	public void setEncryptY(String encryptY) {
		this.encryptY = encryptY;
	}
}