/**
 * 
 */
package com.mapabc.gater.lbsgateway.bean;

import java.io.Serializable;

/**
 * @author 
 *
 */
public class TerminalTypeBean implements Serializable {
	
	
	
	private String id;
	/* 类型名称，是唯一的 */
	private String term_type_name;
 
	/* 解析适配类*/
	private String parseClass;
	/* 编码适配类 */
	private String encodeClass;
	/* 指令开头 16进制串 */
	private String start;
	/* 指令结尾 16进制串 */
	private String end;
	/* 是否入库 */
	private boolean isSaveDb;
	/* 是否转发到JMS */
    private	boolean isSendJms;
	/* 坐标是否加密 */
	private boolean isEncrypt;
	 /* 坐标是否偏转 */
	private boolean isDeflection;
	/* 是否偏转加密 */
	private boolean isEncryptCvt;	
	/* 坐标是否道理纠偏 */
	private boolean isRouteCorret;
	/* 是否需要位置描述 */
	private boolean isLocateDesc;
	/* 坐标转换接口实现类 */
	private String lbmsInterfaceImpl;
	/* JMS接口实现类 */
	private String jmsInterfaceImpl;
	/* 坐标类型 */
	private String coordType;
	/*下发通道类型*/
	private String mtType;
	/* 下发目的地地址 */
	private String mtUrl;
	/*JMS topicNmae */
	private String topicName;
	/*是否批量发送到JMS*/
	private String isBatchToJms;
	
	
	/**
	 * 类型标识，是唯一的  
	 */
	public String getTopicName() {
		return this.topicName;
	}
	/**
	 * 类型标识，是唯一的  
	 */
	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	/**
	 * 下发目的地地址
	 */
	public String getMtUrl() {
		return this.mtUrl;
	}
	/**
	 * 下发目的地地址
	 */
	public void setMtUrl(String mtUrl) {
		this.mtUrl = mtUrl;
	}
	/**
	 * 下发通道类型
	 */
	public String getMtType() {
		return this.mtType;
	}
	/**
	 * 下发通道类型
	 */
	public void setMtType(String mtType) {
		this.mtType = mtType;
	}
	/**
	 * 坐标类型
	 */
	public String getCoordType() {
		return this.coordType;
	}
	/**
	 * 坐标类型
	 */
	public void setCoordType(String coordType) {
		this.coordType = coordType;
	}
	/**
	 *JMS接口实现类
	 */
	public String getJmsInterfaceImpl() {
		return this.jmsInterfaceImpl;
	}
	/**
	 * JMS接口实现类
	 */
	public void setJmsInterfaceImpl(String jmsInterfaceImpl) {
		this.jmsInterfaceImpl = jmsInterfaceImpl;
	}
	/**
	 * 坐标转换接口实现类
	 */
	public String getLbmsInterfaceImpl() {
		return this.lbmsInterfaceImpl;
	}
	/**
	 * 坐标转换接口实现类
	 */
	public void setLbmsInterfaceImpl(String lbmsInterfaceImpl) {
		this.lbmsInterfaceImpl = lbmsInterfaceImpl;
	}
	/**
	 * 是否需要位置描述
	 */
	public boolean isLocateDesc() {
		return this.isLocateDesc;
	}
	/**
	 * 是否需要位置描述
	 */
	public void setLocateDesc(boolean isLocateDesc) {
		this.isLocateDesc = isLocateDesc;
	}
	/**
	 * 类型名称，是唯一的
	 */
	public String getTerm_type_name() {
		return this.term_type_name;
	}
	/**
	 * 类型名称，是唯一的
	 */
	public void setTerm_type_name(String term_type_name) {
		this.term_type_name = term_type_name;
	}
 
	 
	/**
	 * 指令开头 16进制串
	 */
	public String getStart() {
		return this.start;
	}
	/**
	 * 指令开头 16进制串
	 */
	public void setStart(String start) {
		this.start = start;
	}
	/**
	 * 指令结尾 16进制串
	 */
	public String getEnd() {
		return this.end;
	}
	/**
	 * 指令结尾 16进制串
	 */
	public void setEnd(String end) {
		this.end = end;
	}
	/**
	 * 是否入库
	 */
	public boolean isSaveDb() {
		return this.isSaveDb;
	}
	/**
	 * 是否入库
	 */
	public void setSaveDb(boolean isSaveDb) {
		this.isSaveDb = isSaveDb;
	}
	/**
	 * 是否转发到JMS
	 */
	public boolean isSendJms() {
		return this.isSendJms;
	}
	/**
	 * 是否转发到JMS
	 */
	public void setSendJms(boolean isSendJms) {
		this.isSendJms = isSendJms;
	}
	/**
	 * 坐标是否加密
	 */
	public boolean isEncrypt() {
		return this.isEncrypt;
	}
	/**
	 * 坐标是否加密
	 */
	public void setEncrypt(boolean isEncrypt) {
		this.isEncrypt = isEncrypt;
	}
	/**
	 * 坐标是否偏转
	 */
	public boolean isDeflection() {
		return this.isDeflection;
	}
	/**
	 *  坐标是否偏转
	 */
	public void setDeflection(boolean isDeflection) {
		this.isDeflection = isDeflection;
	}
	/**
	 * 坐标是否道理纠偏
	 */
	public boolean isRouteCorret() {
		return this.isRouteCorret;
	}
	/**
	 * 坐标是否道理纠偏
	 */
	public void setRouteCorret(boolean isRouteCorret) {
		this.isRouteCorret = isRouteCorret;
	}
	/**
	 * 是否偏转加密
	 */
	public boolean isEncryptCvt() {
		return this.isEncryptCvt;
	}
	/**
	 * 是否偏转加密
	 */
	public void setEncryptCvt(boolean isEncryptCvt) {
		this.isEncryptCvt = isEncryptCvt;
	}
	/**
	 *  终端类型标识ID，为自定义唯一标识
	 */
	public String getId() {
		return this.id;
	}
	/**
	 * 终端类型标识ID，为自定义唯一标识
	 */
	public void setId(String id) {
		this.id = id;
	}
	 
	/**
	 * 编码工厂适配类
	 */
	public String getEncodeClass() {
		return this.encodeClass;
	}
	/**
	 
	/**
	 *  编码工厂适配类
	 */
	public void setEncodeClass(String encodeClass) {
		this.encodeClass = encodeClass;
	}
	/**
	 *  解析适配类
	 */
	public String getParseClass() {
		return this.parseClass;
	}
	/**
	 *  解析适配类
	 */
	public void setParseClass(String parseClass) {
		this.parseClass = parseClass;
	}
	/**
	 * @return the isBatchToJms
	 */
	public String getIsBatchToJms() {
		return this.isBatchToJms;
	}
	/**
	 * @param isBatchToJms the isBatchToJms to set
	 */
	public void setIsBatchToJms(String isBatchToJms) {
		this.isBatchToJms = isBatchToJms;
	}
	
	

}
