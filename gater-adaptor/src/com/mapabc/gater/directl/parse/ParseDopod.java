package com.mapabc.gater.directl.parse;

import java.util.*;
 

import java.text.ParseException;
import java.text.SimpleDateFormat;
 
  
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException; 
import java.text.NumberFormat;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import java.rmi.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.dbutil.DbOperation;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.lbsgateway.GBLTerminalList; 
import com.mapabc.gater.util.AppHelper;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company: Beijing autonavi Co., Ltd.
 * </p>
 * 
 * @author 终端产品部 黄山项目组
 * @version 1.0
 */

public class ParseDopod extends ParseBase  implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseDopod.class);
	public ArrayList<ParseBase> parseHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ParseBase> parseModata(byte[] moBytes) {
		// TODO Auto-generated method stub
		return null;
	}

	public ParseBase parseSingleGprs(byte[] moBytes) {
		String hexString = Tools.bytesToHexString(moBytes);
		this.parseGPRS(hexString);
		return this;
	}

	public ParseBase parseSingleHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}
	/** 心跳消息 */
	private final static int MSG_TYPE_HEARTBEAT = 1;

	/** 普通定位 */
	private final static int MSG_TYPE_LOC = 2;

	/** 报警 */
	private final static int MSG_TYPE_ALARM = 3;

	/** 调度反馈 */
	private final static int MSG_TYPE_DISP = 4;

	/** 图像文件头 */
	private final static int MSG_TYPE_PICINFO = 5;

	/** 图像数据包 */
	private final static int MSG_TYPE_PIC = 6;

	// 设置采集时间段确认
	private final static int MSG_TYPE_TIME = 7;

	private Connection conn = null;

	// webservice接口地址
	// private String endpoint = Config.getInstance().getProperty("endpoint");

	// private boolean isInterval;

	// 图片根路径
	// String path = AppHelper.getWebAppPath();
	// //Config.getInstance().getString("PICPATH");

	public ParseDopod() {
	}

	/**
	 * parseGPRS
	 * 
	 * @param hexString
	 *            String
	 * @todo Implement this com.autonavi.directl.parse.ParseBase method
	 */
	public void parseGPRS(String hexString) {
		byte[] b = Tools.fromHexString(hexString);
		String msgData = new String(b);
		log.info("接收到多普达终端信息：" + msgData);
		StringTokenizer token = new StringTokenizer(msgData, ",");

		try {
			String mark = token.nextToken();
			String deviceSN = token.nextToken();
			this.setDeviceSN(deviceSN);

			// 保存卡号
//			String phone = this.deviceNum2PhoneNum(deviceSN) ;
//			 if (phone == null || hexString == null
//						|| phone.trim().length() == 0
//						|| hexString.trim().length() == 0) {
//				 
//				 log.info("系统中没有适配到指定的终端：device_id="+deviceSN);
//
//					return;
//				}
//			this.setPhnum(phone);
			this.setDeviceSN(deviceSN);
			String msgType = token.nextToken();
			int iMsgType = Integer.parseInt(msgType);
			switch (iMsgType) {
			case MSG_TYPE_HEARTBEAT:
				parseHeartBeat(token);

				break;
			case MSG_TYPE_LOC:

				parseLocation(msgData);
				break;
			case MSG_TYPE_ALARM:
				parseAlarm(token);
				break;
			case MSG_TYPE_DISP:
				parseDisp(token);
				break;
			case MSG_TYPE_PICINFO:
				parsePicInfo(token);
				break;
			case MSG_TYPE_PIC:
//				parsePic(token, b);
				break;
			case MSG_TYPE_TIME:
				this.parseTimeInterval(token, this.getPhnum());
				break;
			default:
				break;
			}
		} catch (Exception ee) {
			// 解析

		}
	}

	/**
	 * parseGPRS
	 * 
	 * @param phnum
	 *            String
	 * @param content
	 *            String
	 * @todo Implement this com.autonavi.directl.parse.ParseBase method
	 */
	public void parseGPRS(String phnum, String content) {
	}

	/**
	 * parseGPRS
	 * 
	 * @param phnum
	 *            String
	 * @param content
	 *            byte[]
	 * @todo Implement this com.autonavi.directl.parse.ParseBase method
	 */
	public void parseGPRS(String phnum, byte[] content) {
		 
	}

	/**
	 * 
	 * @param hexString
	 *            byte[]
	 * @todo Implement this com.autonavi.directl.parse.ParseBase method
	 */
	public void parseGPRS(byte[] hexString) {
		String msgData = new String(hexString);
		log.info("接收到多普达终端信息：" + msgData);
		StringTokenizer token = new StringTokenizer(msgData, ",");
		try {
			String mark = token.nextToken();
			String deviceSN = token.nextToken();
			

			// 保存卡号
			String phone =  this.deviceNum2PhoneNum(deviceSN) ;
			if (phone == null) {
				log.info("数据库中没有" + deviceSN + "设备对应的SIM卡号");
				return;
			}
			this.setPhnum(phone);
			this.setDeviceSN(deviceSN);

			String msgType = token.nextToken();
			int iMsgType = Integer.parseInt(msgType);

			switch (iMsgType) {
			case MSG_TYPE_HEARTBEAT:
				parseHeartBeat(token);

				break;
			case MSG_TYPE_LOC:
				parseLocation(msgData);
				break;
			case MSG_TYPE_ALARM:
				parseAlarm(token);
				break;
			case MSG_TYPE_DISP:
				parseDisp(token);
				break;
			case MSG_TYPE_PICINFO:
				parsePicInfo(token);
				break;
			case MSG_TYPE_PIC:
//				parsePic(token, hexString);
				break;
			case MSG_TYPE_TIME:
				this.parseTimeInterval(token, this.getPhnum());
				break;
			default:
				break;
			}
		} catch (Exception ee) {
			// 解析

		}
	}

	private  String deviceNum2PhoneNum(String deviceSN) {
		String simcard = null;
		simcard = GBLTerminalList.getInstance().getSimcardNum(deviceSN);
		return simcard;
		 
	}

	// 从数据库中查询采集时间间隔
	public String[] getIntervalParams() {
		String[] params = new String[2];
//		Connection con = com.mapabc.db.DBConnectionManager.getInstance()
//				.getConnection();
		 Connection con = DbOperation
		 .getConnection();
		Statement stm = null;
		ResultSet rs = null;
		String sql = "select * from GPSINTERVAL";
		try {
			stm = con.createStatement();
			rs = stm.executeQuery(sql);
			if (rs.next()) {
				params[0] = rs.getString("BEGINTIME");
				params[1] = rs.getString("ENDTIME");
			} else {
				params[0] = "6:00";
				params[1] = "19:00";
			}
		} catch (SQLException ex) {
			Log.getInstance().errorLog("数据查询异常：" + ex.getMessage(), ex);
		} finally {
			DbOperation.release(stm, rs, null, null,conn);
//			 com.mapabc.db.DBConnectionManager.close(con, stm, rs);
		}

		return params;

	}

	// rec:SDGPS,设备号,1,序列号
	// resp:SDGPS,设备号,1,
	private void parseHeartBeat(StringTokenizer token) {
		String sNum = token.nextToken();
		String interval = "";
		String simcard =  getDeviceSN() ;
		String resp = "SDGPS," + getDeviceSN() + ",1," + sNum + "\r\n";
		// GPRSThread gpsinstance = GPRSThreadList.getInstance()
		// .getGpsThreadBySim(simcard);
		// if (sNum.equals("0")) {
		// // 第一次握手
		// gpsinstance.isInterval = false;
		// }
		// if (!gpsinstance.isInterval) {
		// DopodTerminalSetting dopodSetting = new DopodTerminalSetting(null);
		// // 当系统管理员设定时，从数据库中更新的记录中查询参数
		// String[] intervalParams = this.getIntervalParams();
		// interval = dopodSetting.setInterval(getDeviceSN(),
		// intervalParams[0], intervalParams[1]);
		//
		// }
		//
		// resp = resp + interval;
		log.info("回应握手信号：" + resp);
		 

	}

	/**
	 * 解析通用的GPS信息
	 * 
	 * @param token
	 *            StringTokenizer
	 */
	private void parseCommonInfo(String loc) {
		StringTokenizer token = new StringTokenizer(loc, ",");

		String longitude = token.nextToken();
		this.setCoordX(longitude);
		String latitude = token.nextToken();
		this.setCoordY(latitude);
		String speed = this.formatSpeed(token.nextToken());
		this.setSpeed(speed);
		String heigh = this.formatHeight(token.nextToken());
		this.setAltitude(heigh);
		String datetime = token.nextToken(); // yy-mm-dd hh:mm:ss
		String satelliteNum = token.nextToken().replaceAll("\\|", ""); // 卫星数量
		this.setSatellites(satelliteNum);
		// String seq = token.nextToken(); // 流水号
		// this.setSequence(seq);
		// String msgNum = token.nextToken(); // 信息条数
		// this.setPositionNum(msgNum);
		// int num = Integer.parseInt(msgNum);

		String date = datetime.substring(6, 8) + datetime.substring(3, 5)
				+ datetime.substring(0, 2);

		String time = datetime.substring(9, 11) + datetime.substring(12, 14)
				+ datetime.substring(15, 17);

		String formtDate = conformtime(time, date);

		this.setTime(formtDate);

		Date gpsdate = formatStrToDate(formtDate, "yyyy-MM-dd HH:mm:ss");
 

		log.info(
				this.getPhnum() + "Mobile位置信息:X=" + longitude + ",Y="
						+ latitude + ",TIME=" + formtDate + "，卫星数="
						+ satelliteNum + "，流水号=" +   ",信息数="
						   );
	}

	/**
	 * 解析补发信息
	 * 
	 * @param token
	 *            StringTokenizer
	 */
	private void parseCommonInfos(PreparedStatement pst, String loc) {

		StringTokenizer token = new StringTokenizer(loc, ",");

		String longitude = token.nextToken();
		String latitude = token.nextToken();
		String speed = this.formatSpeed(token.nextToken());
		String heigh = this.formatHeight(token.nextToken());
		String datetime = token.nextToken(); // yy-mm-dd hh:mm:ss
		String satelliteNum = token.nextToken().replaceAll("\\|", ""); // 卫星数量
		String date = datetime.substring(6, 8) + datetime.substring(3, 5)
				+ datetime.substring(0, 2);
		String time = datetime.substring(9, 11) + datetime.substring(12, 14)
				+ datetime.substring(15, 17);
		String formtDate = conformtime(time, date);
		Date gpsdate = formatStrToDate(formtDate, "yyyy-MM-dd HH:mm:dd");
		Timestamp ts = new Timestamp(gpsdate.getTime());

		try {
			pst.setDouble(1, Double.parseDouble(longitude));
			pst.setDouble(2, Double.parseDouble(latitude));
			pst.setDouble(3, Double.parseDouble(heigh));
			pst.setDouble(4, 0.0);
			pst.setDouble(5, Double.parseDouble(speed));
			pst.setTimestamp(6, ts);
			pst.setString(7, "13681914166");
			pst.setInt(8, 1001);// this.getTargetObjectIdBySim(this.getPhnum()));
			pst.addBatch();
		} catch (NumberFormatException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
		} 
	}
	
	/**
     * 解析老版通用的GPS信息
     * @param token StringTokenizer
     */
    private void parseCommonInfo(StringTokenizer token) {
        String longitude = token.nextToken();
        this.setCoordX(longitude);
        String latitude = token.nextToken();
        this.setCoordY(latitude);
        String speed = this.formatSpeed(token.nextToken());
        this.setSpeed(speed);
        String heigh = this.formatHeight(token.nextToken());
        this.setAltitude(heigh);
        String datetime = token.nextToken(); //yy-mm-dd hh:mm:ss
        String date = datetime.substring(6, 8) + datetime.substring(3, 5) +
                      datetime.substring(0, 2);

        String time = datetime.substring(9, 11) + datetime.substring(12, 14) +
                      datetime.substring(15, 17);
        String formtDate = conformtime(time, date);
        this.setTime(formtDate);
        
     
    }

	/**
	 * 解析定位信息 rec:SDGPS,设备号,2,流水号,信息条数,(经度,纬度,速度,高度,时间,卫星数|经度,纬度,速度,高度,时间,卫星数)
	 * 
	 * @param token
	 *            StringTokenizer
	 */
	private void parseLocation(String loc) {
		StringTokenizer loctoken = new StringTokenizer(loc, ",");
		String head = loctoken.nextToken();
		String desn = loctoken.nextToken();
		String cid = loctoken.nextToken();
		int index = loc.indexOf("(");
		 if (index == -1) {
			//老版程序
			parseCommonInfo(loctoken);
	         
	        return ;
		}
		 
		
		String seq = loctoken.nextToken();
		String num = loctoken.nextToken();
		 
		int pnum = Integer.parseInt(num);

		// String phone = deviceNum2PhoneNum(desn);
		// this.setPhnum(phone);
		
		if (pnum > 1 ) {
			String parsemsg = loc.substring(loc.indexOf("(") + 1, loc.indexOf(")"));

			int i = 0;
			String[] locs = parsemsg.split("\\|");
			Connection conn = DbOperation.getConnection();
//			conn = com.mapabc.db.DBConnectionManager.getInstance()
//					.getConnection();
			PreparedStatement pst = null;
			CallableStatement cst = null;
//			String insertSql = "insert into t_locrecord (id,LONGITUDE,LATITUDE,SPEED,HEIGHT,STATLLITE_NUM,GPSTIME,DEVICE_ID)";
//
//			insertSql += "values(SEQ_LOCRECORD.nextval,?,?,?,?,?,?,?)";
			
			String insertPro = "{call PROC_ADD_LOCRECORD(?,?,?,?,?,?,?,?,?)}";
			String callProc = "{call PROC_ADD_Last_LOC(?,?,?,?,?,?,?,?,?)}";

//			String rep = "SDGPS," + this.getDeviceSN() + ",0,"
//					+ this.getSequence() + "," + this.getPositionNum() + "\r\n";
//			this.setExtend2(rep);

			try {
				conn.setAutoCommit(false);
				pst = conn.prepareCall(insertPro);
				cst = conn.prepareCall(callProc);
				while (i < locs.length) {
					StringTokenizer token = new StringTokenizer(locs[i], ",");

					String longitude = token.nextToken();
					String latitude = token.nextToken();
					String speed = this.formatSpeed(token.nextToken());
					String heigh = this.formatHeight(token.nextToken());
					String datetime = token.nextToken(); // yy-mm-dd hh:mm:ss
					String satelliteNum = token.nextToken(); // 卫星数量
					String date = datetime.substring(6, 8)
							+ datetime.substring(3, 5)
							+ datetime.substring(0, 2);
					String time = datetime.substring(9, 11)
							+ datetime.substring(12, 14)
							+ datetime.substring(15, 17);
					String formtDate = conformtime(time, date);
					Date gpsdate = formatStrToDate(formtDate,
							"yyyy-MM-dd HH:mm:ss");
					Timestamp ts = new Timestamp(gpsdate.getTime());
					// parseCommonInfos(pst, locs[i]);
					
					pst.setString(1, this.getDeviceSN());
					pst.setFloat(2, Float.parseFloat(longitude));
					pst.setFloat(3,  Float.parseFloat(latitude));
					pst.setFloat(4, Float.parseFloat(speed==null?"0":speed));
					pst.setFloat(5, Float.parseFloat(heigh==null?"0":heigh));
					pst.setFloat(6, 0);
					pst.setFloat(7, 0);
					pst.setTimestamp(8, ts);// 保存时间和日期
					pst.setString(9, "1");//类型：GPS
					pst.addBatch();

					
					cst.setFloat(1, Float.parseFloat(longitude));
					cst.setFloat(2,  Float.parseFloat(latitude));
					cst.setFloat(3, Float.parseFloat(speed==null?"0":speed));
					cst.setFloat(4, Float.parseFloat(heigh==null?"0":heigh));
					cst.setFloat(5, 0);
					cst.setFloat(6, 0);
					cst.setTimestamp(7, ts);// 保存时间和日期
					cst.setString(8, this.getDeviceSN());
					cst.setString(9, "1");//类型：GPS
					
					cst.addBatch();

					i++;
					
//					log.info(
//							this.getPhnum() + "Mobile补发位置信息:X=" + longitude + ",Y="
//									+ latitude + ",TIME=" + formtDate + "，卫星数="
//									+ satelliteNum + "，流水号=" + this.getSequence() + ",信息数="
//									+ this.getPositionNum());
				}
				pst.executeBatch();
				cst.executeBatch();
				conn.commit();
				conn.setAutoCommit(true);
//				log.info(this.getPhnum() + "补发位置回复确认：" + rep);

			} catch (SQLException e) {

				e.printStackTrace();
				Log.getInstance().errorLog(this.getDeviceSN()+"批量保存补发位置信息出错", e);
			} finally {
				if (cst!=null){
					try {
						cst.close();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				DbOperation.release(null,null,pst, null, conn);
			}

		} else if (pnum == 1 ){
			String parsemsg = loc.substring(loc.indexOf("(") + 1, loc.indexOf(")"));

//			String rep = "SDGPS," + this.getDeviceSN() + ",0,"
//					+ this.getSequence() + "," + this.getPositionNum() + "\r\n";
//			this.setExtend2(rep);

			parseCommonInfo(parsemsg);

//			log.info(this.getPhnum() + "单条位置回复确认：" + rep);
		}

	}

	// SDGPS,设备号,3,经度,纬度,速度,高度,时间
	public void parseAlarm(StringTokenizer token) {
		// parseCommonInfo(token);
		// this.setIsReply(false);
		//
		// /**在手机未得到准确卫星信号时，入库时间不准确，需用服务器时间入库*/
		// Calendar car = Calendar.getInstance();
		// this.setTimeStamp(new Timestamp(car.getTimeInMillis()));
		//
		// DbOperation db = new DbOperation();
		// boolean ispost = db.insertAlarmInfo(this, "1");
		// String name = db.getTerminalNameBySim(this.getPhnum());
		// if (name == null) {
		// log.info("无此号码对应的名字");
		// }
		// //调用webservice接口
		// if (ispost) {
		// WebServiceClientUtil.alarmNotify(this.getPhnum(), name,
		// this.getCoordX(), this.getCoordY(),
		// "1");
		// }

	}

	// 采集时间段确认
	private synchronized void parseTimeInterval(StringTokenizer token,
			String simcard) {
		if (simcard == null) {
			return;
		}
//		GprsTcpThread gpsinstance = GPRSThreadList.getInstance()
//				.getGpsTcpThreadBySim(simcard);
//		if (token == null) {
//			gpsinstance.setInterval(false);
//			return;
//		}
//		String ok = token.nextToken();
//		if (ok.equalsIgnoreCase("ok")) {
//			gpsinstance.setInterval(true);
//			log.info("设置手机" + simcard + "采集时间段已OK");
//		} else {
//			gpsinstance.setInterval(false);
//			log.info("设置手机" + simcard + "采集时间段失败");
//		}

	}

	// SDGPS,设备号,4,经度,纬度,速度,高度,时间,是否服从调度(0 = 否, 1 = 是),不服从原因
	public void parseDisp(StringTokenizer token) {
		// parseCommonInfo(token);
		/** 在手机未得到准确卫星信号时，入库时间不准确，用服务器时间入库 */
		Calendar car = Calendar.getInstance();
		// this.setTimeStamp(new Timestamp(car.getTimeInMillis()));

		// String respOpt = token.nextToken();
		// this.setDispRespOpt(respOpt);
		String resion = "";
		while (token.hasMoreTokens()) {
			if (token.countTokens() == 0) {
				resion += token.nextToken();
				break;
			}
			resion += token.nextToken() + ",";

		}
		// String resion = token.nextToken(); //原因中可能含有分隔符“，”

		// this.setDispRespResion(resion);
 
		// DbOperation db = new DbOperation();
		// db.insertMessageInfo(this);

	}

	// rec:SDGPS,设备号,5,经度,纬度,速度,高度,时间,文件名,文件长度,分包总数
	// resp:SDGPS,设备号,6,文件名,数据包号
	private void parsePicInfo(StringTokenizer token) {
		// parseCommonInfo(token);
		// ---------------------------------------------------------------//
		String longitude = token.nextToken();
		//picCacher.setX(Double.parseDouble(longitude));
		String latitude = token.nextToken();
		//picCacher.setY(Double.parseDouble(latitude));
		String speed = this.formatSpeed(token.nextToken());
		String heigh = this.formatHeight(token.nextToken());
		String datetime = token.nextToken(); // yy-mm-dd hh:mm:ss

		Calendar uploadtime = Calendar.getInstance();
		Timestamp ts = new Timestamp(uploadtime.getTimeInMillis());
//		picCacher.setPicTime(ts);
//		picCacher.setSimCard(this.getPhnum());
		// ------------------------------------------------------------//
		String fileName = token.nextToken();
		String fileLength = token.nextToken();
		String packageNum = token.nextToken();
		// picCacher.setLocalFileName(fileName);
//		picCacher.setByteLength(Integer.parseInt(fileLength));
//		picCacher.setPackageCount(Integer.parseInt(packageNum));
		// -----------------------------------------------------------//

		// String path = "";
		// path = createPicPath(fileName);
		//
		// log.info("手机图片目录：" + path);

//		picCacher.init(null, Integer.parseInt(fileLength), Integer
//				.parseInt(packageNum));

		String resp = "SDGPS," + getDeviceSN() + ",6," + fileName + ","
				+ packageNum + "\r\n"; // gpsInterval;
		log.info("图片文件头回应：" + resp);
		 

	}

	// 生成多媒体图片存储路径,客户端可通过http://localhost:8080/MultiPicInfo/yyyymmdd/xxx.jpg来取得图片
	public static String createPicPath(String fileName) {
		String path = "";
		String webpath = AppHelper
				.getWebAppPath();
		webpath = webpath.substring(0, webpath.indexOf("empuser"));
		String childPath = "MultiPicInfo/" + getYMD() + "/";// 按日期生成文件夹
		String dir = webpath + childPath;
		createDir(dir);
		path = dir + fileName;
		return path;
	}

	// rec:SDGPS,设备号,6,文件名,数据包号,数据长度,文件数据
	// resp:SDGPS,设备号,6,文件名,数据包号
//	private void parsePic(StringTokenizer token, byte[] hexString) {
//
//		String fileName = token.nextToken();
//		String packageNum = token.nextToken();
//		String dataLength = token.nextToken();
//		// 从协议中截取出实际的图像数据
//		int length = Integer.parseInt(dataLength);
//
//		int fullLength = hexString.length;
//		byte[] bytes = new byte[length];
//
//		System.arraycopy(hexString, fullLength - length, bytes, 0, length);
//
//		// 向缓存中增加图像数据
//		int iRet = picCacher.pushBytes(Integer.parseInt(packageNum), bytes,
//				Integer.parseInt(dataLength));
//
//		// 处理回应
//		if (iRet == 0) {
//			String resp = "SDGPS," + getDeviceSN() + ",6," + fileName + ","
//					+ packageNum + "\r\n";
//			this.setExtend2(resp);
////			Picture p = new Picture();
////			p.setPakcNo(Integer.parseInt(packageNum));
////			p.setImgcontent(bytes);
//			// picCacher.add(p); // 数据包对象加入本类缓存
//		} else if (iRet == 3) {
//			String resp = "SDGPS," + getDeviceSN() + ",6," + fileName + ","
//					+ packageNum + "\r\n";
//			this.setExtend2(resp);
//		} else {
//			this.setIsReply(false);
//			this.setIsPost(false);
//			// picCacher.resetParam();// 恢复属性到默认值
//		}
//		if (picCacher.isTransOver()) {
//			String rep = "SDGPS," + getDeviceSN() + ",6," + fileName + ","
//					+ packageNum + "\r\n";
//			log.info("传输完毕回应:" + rep);
//			this.setExtend2(rep);
//
//			// this.dealPicCache(picCacher);
//			// picCacher.resetParam();// 传输完毕，恢复属性到默认值
//		}
//
//	}

	/**
	 * // 对缓存进行排序并入库 public void dealPicCache(PictureCacher picCache) {
	 * //log.info("缓存大小：" + picCache.size());
	 * java.util.Collections.sort(picCache, new Comparator() { public int
	 * compare(Object o1, Object o2) { Picture p1 = (Picture) o1; Picture p2 =
	 * (Picture) o2; if (p1.getPakcNo() > p2.getPakcNo()) { return 1; } else if
	 * (p1.getPakcNo() < p2.getPakcNo()) { return -1; } else { return 0; } } });
	 * 
	 * int i = 0; ByteArrayOutputStream baos = null; ByteArrayInputStream bais =
	 * null; try { baos = new ByteArrayOutputStream(); while (i <
	 * picCache.size()) { Picture pic = (Picture) picCache.get(i);
	 * baos.write(pic.getImgcontent()); i++; } bais = new
	 * ByteArrayInputStream(baos.toByteArray()); insertPicStreamInfo(picCache,
	 * bais); } catch (Exception ex) { Log.getInstance().errorLog("车载图片处理异常：" +
	 * ex.getMessage(), ex); } finally { if (baos != null) { try { baos.flush();
	 * baos.close(); } catch (IOException ex1) {
	 * Log.getInstance().errorLog(ex1.getMessage(), ex1); } if (bais != null) {
	 * try { bais.close(); } catch (IOException ex2) { } } } } }
	 */
	/**
	 * parseSMS
	 * 
	 * @param phnum
	 *            String
	 * @param content
	 *            String
	 * @todo Implement this com.autonavi.directl.parse.ParseBase method
	 */
	public void parseSMS(String phnum, String content) {
	}

	// 把速度单位海里/小时转换成公里/小时
	private String formatSpeed(String tmpSpeed) {
		String ret = "";
		double speed = 0;
		if (tmpSpeed != null) {
			try {
				speed = Double.parseDouble(tmpSpeed);
			} catch (java.lang.NumberFormatException ex) {
			}
			speed = speed * 1.852;
		}
		// ret = "" + speed;
		// if (ret.length() > 4) {
		// ret = ret.substring(0, 4);
		// }
		NumberFormat nformat = NumberFormat.getIntegerInstance(); // getNumberInstance();
		nformat.setMaximumFractionDigits(4);
		nformat.setMinimumFractionDigits(4);
		ret = nformat.format(speed).replaceAll("\\,", "");
		;

		return ret;
	}

	private String formatHeight(String height) {
		String h = "";
		double heigh = 0;
		if (height != null) {
			try {
				heigh = Double.parseDouble(height);
			} catch (java.lang.NumberFormatException ex) {
			}
			heigh = heigh;// * 3.280839895013;
		}
		NumberFormat nformat = NumberFormat.getIntegerInstance(); // getNumberInstance();
		nformat.setMaximumFractionDigits(4);
		nformat.setMinimumFractionDigits(4);
		h = nformat.format(heigh).replaceAll("\\,", ""); // 1,222.344 ,去掉逗号分割

		return h;
	}

	// 处理上报时间
	private String conformtime(String time, String date) {

		try {
			String hour = time.substring(0, 2);
			String min = time.substring(2, 4);
			String sec = time.substring(4, 6);
			String day = date.substring(0, 2);
			String month = date.substring(2, 4);
			String year = date.substring(4, 6);
			String result = "";
			result = "20" + year + "-" + month + "-" + day + " ";
			result += hour + ":" + min + ":" + sec;
			SimpleDateFormat simpleDate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date d = simpleDate.parse(result);
			Calendar car = Calendar.getInstance();
			car.setTime(d);

			int hr = Integer.parseInt(hour);
			int dayOfMonth = car.get(Calendar.DAY_OF_MONTH);
			// if (dayOfMonth != 1 && hr >= 0 && hr < 8) {
			// car.add(Calendar.DAY_OF_MONTH, 1);
			// }
			Date newDate = new Date(car.getTimeInMillis());
			String dateTime = simpleDate.format(newDate);

			return dateTime;
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return null;
	}

	public static Date formatStrToDate(String date, String format) {
		Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			d = sdf.parse(date);
		} catch (ParseException ex) {
		}
		return d;
	}

	// 年月日
	public static String getYMD() {
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
		Calendar car = Calendar.getInstance();
		Date newDate = new Date(car.getTimeInMillis());
		String date = simpleDate.format(newDate);
		return date;
	}

	// 创建目录
	public static boolean createDir(String path) {
		boolean flag = false;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
			// file.mkdir();
			flag = true;
		}
		return flag;
	}

	// 手机图片基本信息入库
	private boolean insertPicStreamInfo(PictureCacher pic, InputStream stream) {
		boolean flag = false;
		Connection conn = DbOperation.getConnection();
//			com.mapabc.db.DBConnectionManager.getInstance()
//				.getConnection();

		PreparedStatement pst = null;
		String sql = "";
		sql = "insert into MUTILMEDIAINFO(MMID,GPSID,UPLOADTIME,X,Y,IMGTYPE,IMG)";
		sql += "values(SEQ_MUL.nextval,?,?,?,?,?,?)";
		BigDecimal phone = new BigDecimal(pic.getSimCard());
		try {
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.setBigDecimal(1, phone);
			pst.setTimestamp(2, pic.getPicTime());
			pst.setDouble(3, pic.getX());
			pst.setDouble(4, pic.getY());
			pst.setString(5, "0");
			pst.setBinaryStream(6, stream, stream.available());
			pst.execute();
			conn.commit();
			conn.setAutoCommit(true);
			flag = true;
			log.info("手机" + phone + "传输图片完毕");
		} catch (Exception ex) {
			flag = false;
			Log.getInstance().errorLog("插入图片信息异常：" + ex.getMessage(), ex);
		} finally {

			// 释放资源
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException ex1) {
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					// TODO 自动生成 catch 块
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO 自动生成 catch 块
					e.printStackTrace();
				}
			}
		}

		return flag;

	}

	// 获取终端在库中的ID
	private int getTargetObjectIdBySim(String sim) {
		int targetId = 0;
		java.sql.Connection conn = null;
		java.sql.ResultSet rs = null;
		String sql = null;
		if (sim == null || sim.trim().length() == 0)
			return 0;

		conn = DbOperation.getConnection();//com.mapabc.db.DBConnectionManager.getInstance().getConnection();

		sql = "select t.id ID from t_target_object t where t.SIM_ID =?";

		PreparedStatement pst = null;
		String sn = "";
		String type = "";
		String code = "";
		try {
			pst = conn.prepareStatement(sql);
			pst.setString(1, sim);
			rs = pst.executeQuery();
			if (rs.next()) {
				targetId = rs.getInt("ID");
			}
		} catch (SQLException ex1) {
			Log.getInstance().getInstance().outLog(
					"查询终端ID异常：" + ex1.getMessage());
		} finally {
//			com.mapabc.db.DBConnectionManager.getInstance()
//					.close(conn, pst, rs);
			DbOperation.release(null, rs, pst, null, conn);
		}
		return targetId;
	}

	public static void main(String[] args) {
		// 经度,纬度,速度,高度,时间
		// SDGPS,设备号,3,经度,纬度,速度,高度,时间
		// SDGPS,设备号,4,经度,纬度,速度,高度,时间,是否服从调度(0 = 否, 1 = 是),不服从原因
		ParseDopod pd = new ParseDopod();
		 
		String ss = "SDGPS,13681914166,2,2,1,(121.417152,31.197772,10.000000,192.000000,08-01-24 15:22:31,5|121.417152,31.197772,10.000000,192.000000,08-01-24 15:22:31,5)";
 
		String old = "SDGPS,123,2,116,39,2,2,2009-07-17 18:00:00\r\n";
		//pd.parseGPRS(Tools.bytesToHexString(old.getBytes()));
		pd.parseLocation(ss);

	}

 
}
