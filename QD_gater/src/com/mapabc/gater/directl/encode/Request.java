/**
 * 
 */
package com.mapabc.gater.directl.encode;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author 
 * 
 */
public class Request {
	/**
	 * 用户ID
	 */
	private String userId = "";
	/**
	 * 接口方法名称
	 */
	private String serviceName = "";
	/**
	 * 指令标识ID
	 */
	private String cmdId = "";
	/**
	 * 设备ID
	 */
	private String deviceId = "";
	/**
	 * 设备类型
	 */
	private String deviceType = "";
	/**
	 * 请求的XML内容
	 */
	private String reqXml = "";
	/**
	 * 第一次发送时间
	 */
	private long firstTime = 0;
	/**
	 * 发送次数
	 */
	private int reSendCount = 0;
	/**
	 * 发送的序列号
	 */
	private String sequence;
	/**
	 * 是否同步等待终端确认
	 */
	private boolean isSynch;
	/**
	 * 指令功能类型
	 */
	private String cmdType;
	/**
	 * 指令接收时间
	 */
	private Date receiveDate;
	/**
	 * 指令发送时间
	 */
	private Date sendDate;
	/**
	 * 接口类Key
	 */
	private String serviceKey;

	/**
	 * 传入的参数映射关系
	 */
	private HashMap<String, Object> datas = new HashMap<String, Object>();

	/**
	 * 是否是负载请求
	 */
	private boolean isLoad;

	/**
	 * 设置用户ID
	 * 
	 * @param userId
	 *            用户ID
	 */
	protected void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 获取用户ID
	 * 
	 * @return userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 设置接口方法名
	 * 
	 * @param serviceName
	 * @author 
	 */
	protected void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	/**
	 * 获取接口方法名
	 * 
	 * @return serviceName
	 * @author 
	 */
	protected String getServiceName() {
		return serviceName;
	}

	/**
	 * 传入的参数列表数据
	 * 
	 * @param datas
	 *            参数映射关系集
	 * 
	 */
	protected void setDatas(HashMap<String, Object> datas) {
		this.datas = datas;
	}

	/**
	 * 传入的参数列表数据
	 * 
	 * @param datas
	 *            参数映射关系集
	 * @retun 参数映射关系集
	 */
	public HashMap<String, Object> getDatas() {
		return datas;
	}

	// public HashMap<String, ArrayList<?>> getSubDatas() {
	// return datas;
	// }

	/**
	 * 设备ID
	 * 
	 * @param deviceId
	 * @author 
	 */
	protected void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * 设备ID
	 * 
	 * @param deviceId
	 * @author 
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * 设备类型
	 * 
	 * @param deviceType
	 * @author 
	 */
	protected void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	/**
	 * 设备类型
	 * 
	 * @return deviceType 设备类型
	 * @author 
	 * 
	 */
	public String getDeviceType() {
		return deviceType;
	}

	/**
	 * 指令标识ID
	 * 
	 * @param cmdId
	 * @author 
	 */
	protected void setCmdId(String cmdId) {
		this.cmdId = cmdId;
	}

	/**
	 * 指令标识ID
	 * 
	 * @param cmdId
	 * @author 
	 */
	protected String getCmdId() {
		return cmdId;
	}

	/**
	 * POST接口XML
	 * 
	 * @return the reqXml
	 */
	public String getReqXml() {
		return this.reqXml;
	}

	/**
	 * POST接口XML
	 * 
	 * @param reqXml
	 *            the reqXml to set
	 */
	protected void setReqXml(String reqXml) {
		this.reqXml = reqXml;
	}

	/**
	 * 发送次数
	 * 
	 * @return
	 * @author 
	 */
	protected int getReSendCount() {
		return reSendCount;
	}

	protected void increseReSendCount() {
		reSendCount++;
	}

	protected void setFirstTime(long firstTime) {
		this.firstTime = firstTime;
	}

	protected long getFirstTime() {
		return firstTime;
	}

	/**
	 * 判断是否超出指定时间
	 * 
	 * @param sec，单位秒
	 * @return
	 * @author 
	 */
	protected boolean isTimeUp(int sec) {
		long time = System.currentTimeMillis() - firstTime;
		if (time >= (sec * 1000)) {// 修改间隔控制单位为秒
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 对应指令表的序列
	 * 
	 * @return the sequence
	 */
	public String getSequence() {
		return this.sequence;
	}

	/**
	 * 是否同步等待
	 * 
	 * @return the isSynch
	 */
	public boolean isSynch() {
		return this.isSynch;
	}

	/**
	 * 对应指令表的序列
	 * 
	 * @param sequence
	 *            the sequence to set
	 */
	protected void setSequence(String sequence) {
		this.sequence = sequence;
	}

	/**
	 * 是否同步等待
	 * 
	 * @param isSynch
	 *            the isSynch to set
	 */
	protected void setSynch(boolean isSynch) {
		this.isSynch = isSynch;
	}

	/**
	 * 指令类型
	 * 
	 * @return the cmdType
	 */
	protected String getCmdType() {
		return this.cmdType;
	}

	/**
	 * 接收时间
	 * 
	 * @return the receiveDate
	 */
	protected Date getReceiveDate() {
		return this.receiveDate;
	}

	/**
	 * 发送时间
	 * 
	 * @return the sendDate
	 */
	protected Date getSendDate() {
		return this.sendDate;
	}

	/**
	 * 指令类型
	 * 
	 * @param cmdType
	 *            the cmdType to set
	 */
	protected void setCmdType(String cmdType) {
		this.cmdType = cmdType;
	}

	/**
	 * 接收时间
	 * 
	 * @param receiveDate
	 *            the receiveDate to set
	 */
	protected void setReceiveDate(Date receiveDate) {
		this.receiveDate = receiveDate;
	}

	/**
	 * 发送时间
	 * 
	 * @param sendDate
	 *            the sendDate to set
	 */
	protected void setSendDate(Date sendDate) {
		this.sendDate = sendDate;
	}

	/**
	 * 适配工厂接口方法key值
	 * 
	 * @return the serviceKey
	 */
	protected String getServiceKey() {
		return this.serviceKey;
	}

	/**
	 * 适配工厂接口方法key值
	 * 
	 * @param serviceKey
	 *            the serviceKey to set
	 */
	protected void setServiceKey(String serviceKey) {
		this.serviceKey = serviceKey;
	}

	/**
	 * 根据参数名获取传入的参数值
	 * 
	 * @param key
	 *            参数名
	 * @return value 参数值
	 * @author 
	 */
	public Object getReqData(String key) {
		Object ret = "";

		ret = this.getDatas().get(key);
		if (ret == null)
			ret = "";

		return ret;
	}

	protected boolean isLoad() {
		return isLoad;
	}

	protected void setLoad(boolean isLoad) {
		this.isLoad = isLoad;
	}

}
