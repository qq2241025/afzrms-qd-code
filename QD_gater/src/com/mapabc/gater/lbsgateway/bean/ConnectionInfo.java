/**
 * 
 */
package com.mapabc.gater.lbsgateway.bean;

import java.io.Serializable;
import java.util.ArrayList;

import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;

/**
 * @author 
 * 
 */
public class ConnectionInfo implements Serializable {

	private String type; // 端口类型 0：TCP 1:UDP 2:串口
	private String port; // 端口值
	private String listenClass; // 端口监听类
	private ArrayList<TerminalTypeBean> termTypeList = new ArrayList<TerminalTypeBean>();
	private int readDataPoolNum = 3; //读数据线程数
	private int parsePoolNum = 3;//解析线程数
	private int dbPoolNum;//入库线程数

	 

	public String getListenClass() {
		return this.listenClass;
	}

	public void setListenClass(String listenClass) {
		this.listenClass = listenClass;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * @return the termTypeList
	 */
	public ArrayList<TerminalTypeBean> getTermTypeList() {
		return this.termTypeList;
	}

	/**
	 * @param termTypeList the termTypeList to set
	 */
	public void setTermTypeList(ArrayList<TerminalTypeBean> termTypeList) {
		this.termTypeList = termTypeList;
	}

	 

	/**
	 * @return the readDataPoolNum
	 */
	public int getReadDataPoolNum() {
		return this.readDataPoolNum;
	}

	/**
	 * @param readDataPoolNum the readDataPoolNum to set
	 */
	public void setReadDataPoolNum(int readDataPoolNum) {
		this.readDataPoolNum = readDataPoolNum;
	}

	/**
	 * @return the parsePoolNum
	 */
	public int getParsePoolNum() {
		return this.parsePoolNum;
	}

	/**
	 * @param parsePoolNum the parsePoolNum to set
	 */
	public void setParsePoolNum(int parsePoolNum) {
		this.parsePoolNum = parsePoolNum;
	}

	/**
	 * @return the dbPoolNum
	 */
	public int getDbPoolNum() {
		return this.dbPoolNum;
	}

	/**
	 * @param dbPoolNum the dbPoolNum to set
	 */
	public void setDbPoolNum(int dbPoolNum) {
		this.dbPoolNum = dbPoolNum;
	}

	 

}
