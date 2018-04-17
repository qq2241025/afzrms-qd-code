package com.mapabc.gater.directl.parse;

import java.net.Socket;
import java.util.ArrayList;

import com.mapabc.gater.directl.bean.status.AbstractTTermStatusRecord;
import com.mapabc.gater.directl.bean.status.AbstractTermExtendStatus;

public class ParseBase implements Cloneable {

	// --------------------常用属性信息-------------------------------// 
	private String coordX;// 原始经度
	private String coordY;// 原始纬度
	private String time;// GPS时间
	private String phnum;// SIM卡号
	private String direction;// 方向
	private String speed;// 速度
	private String altitude; // 高程
	private String satellites; // 卫星个数
	private String mileage; // 里程
 	private String deviceSN; // 设备ID
	private String address; // 位置描述
	
	private String alarmType;// 报警类型 1超速 2区域 3紧急 4偏航 5断电
	private String alarmSubType;// 报警子类型 
	private String alarmCaseId; // 报警案例ID 
	private String alarmDesc;//报警描述
	
	private Socket socket;// TCP客户端地址
	private int coordType = 0;// 0原始坐标 1偏移坐标 2道路纠偏后坐标
	private String locateType = "1"; // 0:LBS 1:GPS,默认GPS
	private String locateStatus = "";// 定位状态 0:未定位 1：已定位 2:补偿数据
  	
	private byte[] replyByte;// 中心应答终端信息
	private byte[] replyByte1;// 中心应答终端信息【作为候选】
	private byte[] replyByte2;// 中心应答终端信息【作为候选】
 

	private AbstractTTermStatusRecord statusRecord = null;// 终端所有状态
	private AbstractTermExtendStatus extendStatus; // 开发者扩展状态
  
	private ArrayList<String> jmsInfoList = new ArrayList<String>(); // 存储待转发的JMS信息列表
	@SuppressWarnings("rawtypes")
	private ArrayList<?> alarmInfoList = new ArrayList(); // 存储待转发的JMS信息列表
 	
	//GPS批量数据列表
  	private ArrayList<ParseBase> parseList = new ArrayList<ParseBase>();
  	
  	//是否是补偿数据
  	private boolean isCompense;

	private String areaNo;// 区域报警的区域编号(一般1～20)
	private String speedThreshold;// 超速报警设置的阀值
 
	/**
	 * 定位类型： 0:LBS 1:GPS,默认GPS
	 * 
	 * @return the locateType
	 */
	public String getLocateType() {
		return this.locateType;
	}

	/**
	 * 定位类型： 0:LBS 1:GPS,默认GPS
	 * 
	 * @param locateType
	 *            the locateType to set
	 */
	public void setLocateType(String locateType) {
		this.locateType = locateType;
	}

 
 
 

	/**
	 * 获取原始经度
	 * 
	 * @return
	 * @author 
	 */
	public String getCoordX() {
		return coordX;
	}

	/**
	 * 设置原始经度
	 * 
	 * @param coordX
	 * @author 
	 */
	public void setCoordX(String coordX) {
		this.coordX = coordX;
	}

	/**
	 * 获取原始纬度
	 * 
	 * @return
	 * @author 
	 */
	public String getCoordY() {
		return coordY;
	}

	/**
	 * 设置原始纬度
	 * 
	 * @param coordY
	 * @author 
	 */
	public void setCoordY(String coordY) {
		this.coordY = coordY;
	}

	/**
	 * 获取GPS时间字符串
	 * 
	 * @return
	 * @author 
	 */
	public String getTime() {
		return time;
	}

	/**
	 * 设置GPS时间字符串 格式：yyyy-MM-dd HH:mm:ss
	 * 
	 * @param time
	 * @author 
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * 获取终端SIM卡号
	 * 
	 * @return
	 * @author 
	 */
	public String getPhnum() {
		return phnum;
	}

	/**
	 * 设置终端SIM卡号
	 * 
	 * @param phnum
	 * @author 
	 */
	public void setPhnum(String phnum) {
		this.phnum = phnum;
	}

	/**
	 * 获取GPS方向，单位度
	 * 
	 * @return direction
	 * @author 
	 */
	public String getDirection() {
		return direction;
	}

	/**
	 * 设置GPS方向，单位度
	 * 
	 * @param direction
	 * @author 
	 */
	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * 获取GPS速度，单位公里/小时
	 * 
	 * @return speed
	 * @author 
	 */
	public String getSpeed() {
		return speed;
	}

	/**
	 * 设置GPS速度，单位公里/小时
	 * 
	 * @return
	 * @author 
	 */
	public void setSpeed(String speed) {
		this.speed = speed;
	}

	/**
	 * 获取GPS高程数据
	 * 
	 * @return
	 * @author 
	 */
	public String getAltitude() {
		return altitude;
	}

	/**
	 * 设置GPS高程数据
	 * 
	 * @param altitude
	 * @author 
	 */
	public void setAltitude(String altitude) {
		this.altitude = altitude;
	}

	/**
	 * 获取GPS卫星数
	 * 
	 * @return
	 * @author 
	 */
	public String getSatellites() {
		return satellites;
	}

	/**
	 * 设置GPS卫星数
	 * 
	 * @param satellites
	 * @author 
	 */
	public void setSatellites(String satellites) {
		this.satellites = satellites;
	}

	 

	/**
	 * 设备ID
	 * 
	 * @return deviceSN
	 * @author 
	 */
	public String getDeviceSN() {
		return deviceSN;
	}
 

	/**
	 * 设置终端ID
	 * 
	 * @param deviceSN
	 * @author 
	 */
	public void setDeviceSN(String deviceSN) {
		this.deviceSN = deviceSN;
	}

	/**
	 * 中心应答终端的二进制信息
	 */
	public byte[] getReplyByte() {
		return replyByte;
	}

	/**
	 * 设置中心应答终端的二进制信息
	 */
	public void setReplyByte(byte[] replyByte) {
		this.replyByte = replyByte;
	}

	/**
	 * 中心应答终端的二进制信息【作为候选选择】
	 */
	public byte[] getReplyByte1() {
		return replyByte1;
	}

	/**
	 * 中心应答终端的二进制信息【作为候选选择】
	 */
	public void setReplyByte1(byte[] replyByte1) {
		this.replyByte1 = replyByte1;
	}

	/**
	 * 中心应答终端的二进制信息【作为候选选择】
	 */
	public byte[] getReplyByte2() {
		return replyByte2;
	}

	/**
	 * 中心应答终端的二进制信息【作为候选选择】
	 */
	public void setReplyByte2(byte[] replyByte2) {
		this.replyByte2 = replyByte2;
	}

	/**
	 * 位置描述
	 * 
	 * @return the address
	 */
	public String getAddress() {
		return this.address;
	}

	/**
	 * 位置描述
	 * 
	 * @param address
	 *            the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
 

	/**
	 * 获取需要转发到JMS的信息
	 * 
	 * @return the jmsInfoList
	 */
	public ArrayList<String> getJmsInfoList() {
		return this.jmsInfoList;
	}

 

	/**
	 * 存储需要转发到JMS的信息,用户可以不关心怎么发送的，只需把要发送的内容加到列表
	 * 
	 * @param info:发送到JMS的信息
	 */
	public void addJmsInfoToList(String info) {
		this.jmsInfoList.add(info);
	}

	/**
	 * 设置JMS转发信息列表
	 * 
	 * @param jmsInfoList
	 *            the jmsInfoList to set
	 */
	public void setJmsInfoList(ArrayList<String> jmsInfoList) {
		this.jmsInfoList = jmsInfoList;
	}

	/**
	 * 坐标类型： 0原始坐标 1偏移坐标
	 * 
	 * @return the coordType
	 */
	public int getCoordType() {
		return this.coordType;
	}

	/**
	 * 坐标类型： 0原始坐标 1偏移坐标
	 * 
	 * @param coordType
	 *            the coordType to set
	 */
	public void setCoordType(int coordType) {
		this.coordType = coordType;
	}

	/**
	 * 报警类型：类型码见AlarmType类
	 * 
	 * @return the alarmType
	 */
	public String getAlarmType() {
		return this.alarmType;
	}

	/**
	 * 报警对应设置的CASEID
	 * 
	 * @return the alarmCaseId
	 */
	public String getAlarmCaseId() {
		return this.alarmCaseId;
	}

	/**
	 * 
	 * 报警类型：类型码见AlarmType类
	 * 
	 * @param alarmType
	 *            the alarmType to set
	 */
	public void setAlarmType(String alarmType) {
		this.alarmType = alarmType;
	}

	/**
	 * 
	 * 报警对应设置的CASEID
	 * 
	 * @param alarmCaseId
	 *            the alarmCaseId to set
	 */
	public void setAlarmCaseId(String alarmCaseId) {
		this.alarmCaseId = alarmCaseId;
	}
   

	/**
	 * 终端状态及扩展信息
	 * 
	 * @return the statusRecord
	 */
	public AbstractTTermStatusRecord getStatusRecord() {
		return this.statusRecord;
	}

	/**
	 * 设置获取终端状态及扩展信息
	 * 
	 * @param statusRecord
	 *            the statusRecord to set
	 */
	public void setStatusRecord(AbstractTTermStatusRecord statusRecord) {
		this.statusRecord = statusRecord;
	}

	/**
	 * 远程终端TCP链路地址
	 * 
	 * @return the socket
	 */
	public Socket getSocket() {
		return this.socket;
	}

	/**
	 * 远程终端TCP链路地址
	 * 
	 * @param socket
	 *            the socket to set
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	/**
	 * 报警子类型
	 * 
	 * @return the alarmSubType
	 */
	public synchronized String getAlarmSubType() {
		return this.alarmSubType;
	}

	/**
	 * 报警子类型
	 * 
	 * @param alarmSubType
	 *            the alarmSubType to set
	 */
	public synchronized void setAlarmSubType(String alarmSubType) {
		this.alarmSubType = alarmSubType;
	}

	/**
	 * 里程
	 * 
	 * @return the mileage
	 */
	public String getMileage() {
		return this.mileage;
	}

	/**
	 * 里程
	 * 
	 * @param mileage
	 *            the mileage to set
	 */
	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
 

	/**
	 * 扩展的设备状态
	 * 
	 * @return the extendStatus
	 */
	public AbstractTermExtendStatus getExtendStatus() {
		return this.extendStatus;
	}

	/**
	 * 设置扩展的设备状态
	 * 
	 * @param extendStatus
	 *            the extendStatus to set
	 */
	public void setExtendStatus(AbstractTermExtendStatus extendStatus) {
		this.extendStatus = extendStatus;
	}
 
	/**
	 * 获取GPS批量数据列表
	 * @return parseList
	 */
	public  ArrayList<ParseBase> getParseList() {
		return parseList;
	}
	/**
	 * 设置GPS批量数据列表，用于处理GPS设备批次上传信息处理
	 * @param parseList
	 */
	public  void setParseList(ArrayList<ParseBase> parseList) {
		this.parseList = parseList;
	}
 
	/**
	 * 返回定位状态
	 * @return
	 */
	public String getLocateStatus() {
		return locateStatus;
	}
	/**
	 * 设置定位状态
	 * @param locateStatus
	 */
	public void setLocateStatus(String locateStatus) {
		this.locateStatus = locateStatus;
	}
	
	public void resetObject(ParseBase base) { 
		this.setDeviceSN(base.deviceSN);
		this.setPhnum(base.phnum);
		this.setCoordX(base.coordX);
		this.setCoordY(base.coordY);
		this.setSpeed(base.speed);
		this.setAddress(base.address); 
		this.setAltitude(base.altitude); 
		this.setDirection(base.direction);   
		this.setMileage(base.mileage); 
		this.setSatellites(base.satellites);
		this.setTime(base.time);
		this.setCoordType(base.coordType);
		this.setLocateType(base.locateType);
		this.setLocateStatus(base.locateStatus); 
		
 		this.setAlarmCaseId(base.alarmCaseId);
		this.setAlarmType(base.alarmType);
		this.setAlarmSubType(base.alarmSubType);
		this.setAlarmDesc(base.alarmDesc); 
		
		this.setReplyByte(base.getReplyByte());
		this.setReplyByte1(base.getReplyByte1());
		this.setReplyByte2(base.getReplyByte2());    
 
		
		this.setStatusRecord(base.statusRecord);
		this.setExtendStatus(base.getExtendStatus());
  		this.setSocket(base.socket); 
  		this.alarmInfoList = base.alarmInfoList;
  		this.setJmsInfoList(base.jmsInfoList);
  		this.setParseList(base.parseList);
		 
	}
	
	/**
	 * 复制对象
	 * 
	 * @return Object
	 */
	public Object clone() { 
		ParseBase base = new ParseBase();
		base.setDeviceSN(this.deviceSN);
		base.setPhnum(this.phnum);
		base.setCoordX(this.coordX);
		base.setCoordY(this.coordY);
		base.setSpeed(this.speed);
		base.setAddress(this.address); 
		base.setAltitude(this.altitude); 
		base.setDirection(this.direction);   
		base.setMileage(this.mileage); 
		base.setSatellites(this.satellites);
		base.setTime(this.time);
		base.setCoordType(this.coordType);
		base.setLocateType(this.locateType);
		base.setLocateStatus(this.locateStatus); 
		
 		base.setAlarmCaseId(this.alarmCaseId);
		base.setAlarmType(this.alarmType);
		base.setAlarmSubType(this.alarmSubType);
		base.setAlarmDesc(this.alarmDesc); 
		
		base.setReplyByte(this.getReplyByte());
		base.setReplyByte1(this.getReplyByte1());
		base.setReplyByte2(this.getReplyByte2());    
 
		
		base.setStatusRecord(this.statusRecord);
		base.setExtendStatus(this.getExtendStatus());
  		base.setSocket(this.socket); 
  		base.alarmInfoList = this.alarmInfoList;
  		base.setJmsInfoList(this.jmsInfoList);
  		base.setParseList(this.parseList);
  		
  		base.setSpeedThreshold(this.getSpeedThreshold());
  		base.setAreaNo(this.getAreaNo());
  		
  		return base;
	}
	
	/**
	 * 返回报警描述
	 * @return
	 */
	public String getAlarmDesc() {
		return alarmDesc;
	}
	/**
	 * 设置报警描述
	 * @param alarmDesc
	 */
	public void setAlarmDesc(String alarmDesc) {
		this.alarmDesc = alarmDesc;
	}
	/**
	 * 是否是补偿数据
	 * @return 
	 */
	public boolean isCompense() {
		return isCompense;
	}
	/**
	 * 设置数据是否是补偿数据
	 * @param isCompense
	 */
	public void setCompense(boolean isCompense) {
		this.isCompense = isCompense;
	}

	public String getAreaNo() {
		return areaNo;
	}

	public void setAreaNo(String areaNo) {
		this.areaNo = areaNo;
	}

	public String getSpeedThreshold() {
		return speedThreshold;
	}

	public void setSpeedThreshold(String speedThreshold) {
		this.speedThreshold = speedThreshold;
	}
	
}
