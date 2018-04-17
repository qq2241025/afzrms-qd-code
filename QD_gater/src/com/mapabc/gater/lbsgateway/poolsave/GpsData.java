package com.mapabc.gater.lbsgateway.poolsave;

import com.mapabc.gater.directl.bean.status.AbstractTTermStatusRecord;
import com.mapabc.gater.directl.bean.status.AbstractTermExtendStatus;

public class GpsData {
	  public GpsData() {
	  }
	  private long id; //位置ID
	  private String DEVICE_ID="";
	  private String SIMCARD = "";//手机号
	  private String TIME = "";//时间
	  private Float X = 0.0f;//原始经度
	  private Float Y = 0.0f;//原始纬度
	  private Float S = 0.0f;//速度
	  private Float direction = 0.0f;//方向
//	  private Float LC = 0.0f;//里程
	  private Float H = 0.0f;//高程
	  private int C = 0;//卫星个数
	  private String F = "";//定位状态
	  private String Adress="";//位置描述
	  
	  private String PICURL = "";//图片
	  private String ALARMDESC = "";//报警描述
	  
	  private String encX="";//加密X
	  private String encY="";//加密Y
	  private Float TX = 0.0f;//坐标偏转的经度
	  private Float TY = 0.0f;//坐标偏转的纬度
	  private String jmx = "";//加密偏转经度
	  private String jmy = "";//加密偏转纬度
	  private String routeCorrectX=""; //道理纠偏后经度
	  private String routeCorrectY = "";//道理纠偏后纬度
	  
	  private int coordType ; //坐标类型 0原始坐标 1偏移坐标
	  private String locateType; //定位类型 0LBS 1GPS
	  private boolean isTrack = true;//是否需要记录轨迹
		
	  private java.sql.Timestamp gpsTime=null;
	  private java.sql.Timestamp sysTime = null; //系统时间
	  //定位状态 0未定位 1已定位 2补偿信息
	  private String status;
	  
	  private AbstractTTermStatusRecord statusRecord;
	  private AbstractTermExtendStatus extendStatus; //开发者扩展状态
	  private boolean isCompense;
	
	  
	  private String objType = "";
	  private String objId = "";
	  private Float oilUsed; //两次上报之间的耗油量
	  //里程
	  private Float mileage=0.0f;
	  
	  private String uuid;
	  
	  private boolean isPost;//是否入库
	private String dispatchMsg;// 调度信息 
	private String reply; // 中心回复终端信息
	private String cmdId;// 指令标识
	  
	  /**
	 * @return the uuid
	 */
	public String getUuid() {
		return this.uuid;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @return the oilUsed
	 */
	public synchronized Float getOilUsed() {
		return this.oilUsed;
	}

	/**
	 * @param oilUsed the oilUsed to set
	 */
	public synchronized void setOilUsed(Float oilUsed) {
		this.oilUsed = oilUsed;
	}

	/**
	 * @return the statusRecord
	 */
	public AbstractTTermStatusRecord getStatusRecord() {
		return this.statusRecord;
	}

	/**
	 * @param statusRecord the statusRecord to set
	 */
	public void setStatusRecord(AbstractTTermStatusRecord statusRecord) {
		this.statusRecord = statusRecord;
	}

	/**
	 * @return the dEVICE_ID
	 */
	public String getDEVICE_ID() {
		return this.DEVICE_ID;
	}

	/**
	 * @return the sIMCARD
	 */
	public String getSIMCARD() {
		return this.SIMCARD;
	}

	/**
	 * @return the tIME
	 */
	public String getTIME() {
		return this.TIME;
	}

	/**
	 * @return the x
	 */
	public Float getX() {
		return this.X;
	}

	/**
	 * @return the y
	 */
	public Float getY() {
		return this.Y;
	}

	/**
	 * @return the s
	 */
	public Float getS() {
		return this.S;
	}

	/**
	 * @return the v
	 */
	public Float getV() {
		return this.direction;
	}

//	/**
//	 * @return the lC
//	 */
//	public Float getLC() {
//		return this.LC;
//	}

	/**
	 * @return the h
	 */
	public Float getH() {
		return this.H;
	}

	/**
	 * @return the c
	 */
	public int getC() {
		return this.C;
	}

	/**
	 * @return the f
	 */
	public String getF() {
		return this.F;
	}

	/**
	 * @return the adress
	 */
	public String getAdress() {
		return this.Adress;
	}

	/**
	 * @return the pICURL
	 */
	public String getPICURL() {
		return this.PICURL;
	}

	/**
	 * @return the aLARMDESC
	 */
	public String getALARMDESC() {
		return this.ALARMDESC;
	}

	/**
	 * @return the encX
	 */
	public String getEncX() {
		return this.encX;
	}

	/**
	 * @return the encY
	 */
	public String getEncY() {
		return this.encY;
	}

	/**
	 * @return the tX
	 */
	public Float getTX() {
		return this.TX;
	}

	/**
	 * @return the tY
	 */
	public Float getTY() {
		return this.TY;
	}

	/**
	 * @return the jmx
	 */
	public String getJmx() {
		return this.jmx;
	}

	/**
	 * @return the jmy
	 */
	public String getJmy() {
		return this.jmy;
	}

	/**
	 * @return the routeCorrectX
	 */
	public String getRouteCorrectX() {
		return this.routeCorrectX;
	}

	/**
	 * @return the routeCorrectY
	 */
	public String getRouteCorrectY() {
		return this.routeCorrectY;
	}

	/**
	 * @return the coordType
	 */
	public int getCoordType() {
		return this.coordType;
	}

	/**
	 * @return the locateType
	 */
	public String getLocateType() {
		return this.locateType;
	}

	/**
	 * @return the gpsTime
	 */
	public java.sql.Timestamp getGpsTime() {
		return this.gpsTime;
	}

	/**
	 * @return the sysTime
	 */
	public java.sql.Timestamp getSysTime() {
		return this.sysTime;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return this.status;
	}

	 
	/**
	 * @param device_id the dEVICE_ID to set
	 */
	public void setDEVICE_ID(String device_id) {
		this.DEVICE_ID = device_id;
	}

	/**
	 * @param simcard the sIMCARD to set
	 */
	public void setSIMCARD(String simcard) {
		this.SIMCARD = simcard;
	}

	/**
	 * @param time the tIME to set
	 */
	public void setTIME(String time) {
		this.TIME = time;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(Float x) {
		this.X = x;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(Float y) {
		this.Y = y;
	}

	/**
	 * @param s the s to set
	 */
	public void setS(Float s) {
		this.S = s;
	}

	/**
	 * @param v the v to set
	 */
	public void setV(Float v) {
		this.direction = v;
	}

	/**
	 * @param lc the lC to set
	 */
//	public void setLC(Float lc) {
//		this.LC = lc;
//	}

	/**
	 * @param h the h to set
	 */
	public void setH(Float h) {
		this.H = h;
	}

	/**
	 * @param c the c to set
	 */
	public void setC(int c) {
		this.C = c;
	}

	/**
	 * @param f the f to set
	 */
	public void setF(String f) {
		this.F = f;
	}

	/**
	 * @param adress the adress to set
	 */
	public void setAdress(String adress) {
		this.Adress = adress;
	}

	/**
	 * @param picurl the pICURL to set
	 */
	public void setPICURL(String picurl) {
		this.PICURL = picurl;
	}

	/**
	 * @param alarmdesc the aLARMDESC to set
	 */
	public void setALARMDESC(String alarmdesc) {
		this.ALARMDESC = alarmdesc;
	}

	/**
	 * @param encX the encX to set
	 */
	public void setEncX(String encX) {
		this.encX = encX;
	}

	/**
	 * @param encY the encY to set
	 */
	public void setEncY(String encY) {
		this.encY = encY;
	}

	/**
	 * @param tx the tX to set
	 */
	public void setTX(Float tx) {
		this.TX = tx;
	}

	/**
	 * @param ty the tY to set
	 */
	public void setTY(Float ty) {
		this.TY = ty;
	}

	/**
	 * @param jmx the jmx to set
	 */
	public void setJmx(String jmx) {
		this.jmx = jmx;
	}

	/**
	 * @param jmy the jmy to set
	 */
	public void setJmy(String jmy) {
		this.jmy = jmy;
	}

	/**
	 * @param routeCorrectX the routeCorrectX to set
	 */
	public void setRouteCorrectX(String routeCorrectX) {
		this.routeCorrectX = routeCorrectX;
	}

	/**
	 * @param routeCorrectY the routeCorrectY to set
	 */
	public void setRouteCorrectY(String routeCorrectY) {
		this.routeCorrectY = routeCorrectY;
	}

	/**
	 * @param coordType the coordType to set
	 */
	public void setCoordType(int coordType) {
		this.coordType = coordType;
	}

	/**
	 * @param locateType the locateType to set
	 */
	public void setLocateType(String locateType) {
		this.locateType = locateType;
	}

	/**
	 * @param gpsTime the gpsTime to set
	 */
	public void setGpsTime(java.sql.Timestamp gpsTime) {
		this.gpsTime = gpsTime;
	}

	/**
	 * @param sysTime the sysTime to set
	 */
	public void setSysTime(java.sql.Timestamp sysTime) {
		this.sysTime = sysTime;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}

	 
	public String toString(){
		  String str = null;
		  StringBuilder sbulider = new StringBuilder();
		  sbulider.append("DEVICEID="+DEVICE_ID+",SIMCARD="+SIMCARD);
		  sbulider.append(",LNG="+X+",LAT="+Y+",SPEED="+S+",Derection="+direction+",HEIGHT="+H+",STALLITE NUM="+C+",GPSTIME="+TIME);
		 // sbulider.append(",加密坐标=（"+encX+","+encY+");");
		  //sbulider.append(",偏转坐标=（"+TX+","+TY+");加密偏转坐标=（"+jmx+","+jmy+");道路纠偏坐标=（"+routeCorrectX+","+routeCorrectY+")");
		  sbulider.append(",位置描述："+Adress);
		  sbulider.append(",coordType="+coordType+",locateType="+locateType+",status="+status);
		  str = sbulider.toString();
		  return str;
	  }

	public void setTrack(boolean isTrack) {
		this.isTrack = isTrack;
	}

	public boolean isTrack() {
		return isTrack;
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return the objType
	 */
	public String getObjType() {
		return this.objType;
	}

	/**
	 * @return the objId
	 */
	public String getObjId() {
		return this.objId;
	}

	/**
	 * @param objType the objType to set
	 */
	public void setObjType(String objType) {
		this.objType = objType;
	}

	/**
	 * @param objId the objId to set
	 */
	public void setObjId(String objId) {
		this.objId = objId;
	}

	public Float getMileage() {
		return mileage;
	}

	public void setMileage(Float mileage) {
		this.mileage = mileage;
	}

	public Float getDirection() {
		return direction;
	}

	public void setDirection(Float direction) {
		this.direction = direction;
	}
		/**
	 * 获取是否入库
	 * @return the isPost
	 */
	public boolean isPost() {
		return this.isPost;
	}

	/**
	 * 设置是否加入到入库池
	 * @return
	 * @author 
	 */
	public void setPost(boolean isPost) {
		this.isPost = isPost;
	}
	
	    /**
	 * @return the extendStatus
	 */
	public AbstractTermExtendStatus getExtendStatus() {
		return this.extendStatus;
	}

	/**
	 * @param extendStatus the extendStatus to set
	 */
	public void setExtendStatus(AbstractTermExtendStatus extendStatus) {
		this.extendStatus = extendStatus;
	}

	public String getDispatchMsg() {
		return dispatchMsg;
	}

	public void setDispatchMsg(String dispatchMsg) {
		this.dispatchMsg = dispatchMsg;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getCmdId() {
		return cmdId;
	}

	public void setCmdId(String cmdId) {
		this.cmdId = cmdId;
	}

	public boolean isCompense() {
		return isCompense;
	}

	public void setCompense(boolean isCompense) {
		this.isCompense = isCompense;
	}
	    
}
