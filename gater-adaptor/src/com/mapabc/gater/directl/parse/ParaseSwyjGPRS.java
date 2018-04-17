package com.mapabc.gater.directl.parse;

import java.lang.reflect.Array;
import java.net.Socket;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
 
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.constant.AlarmType;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.directl.pic.PicCache;
import com.mapabc.gater.directl.pic.Picture; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.service.CommonGatewayServiceImpl;
import com.mapabc.gater.lbsgateway.service.ICommonGatewayService;

 

/**
 * <p>
 * Title: GPS网关
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2006
 * </p>
 * <p>
 * Company: www.mapabc.com
 * </p>
 * 
 * @author yang lei
 * @version 1.0
 */
public class ParaseSwyjGPRS extends ParseBase implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParaseSwyjGPRS.class);
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
	Timestamp ts = null;
	TTermStatusRecord statusRec = new TTermStatusRecord();

	/**
	 * parseGPRS
	 * 
	 * @param hexString
	 *            String
	 * @todo Implement this com.mapabc.directl.parse.ParseBase method
	 */
	// 2490906186041248011308093958792300116179891e000000fffffbffff0019
	public void parseGPRS(String hexString) {
		log.info("SWYJ原始数据：" + hexString);
		if (hexString.startsWith("24")) {

			this.parse$Position(hexString);
			

		} else if (hexString.startsWith("4d")) {

			this.parse$Position(hexString);
			

		} else if (hexString.startsWith("50")) {// 图片包数据
			
		} else if (hexString.startsWith("2a48512c")) {

			byte[] cont = Tools.fromHexString(hexString);
			// *XX,YYYYYYYYYY,V1,HHMMSS,S,latitude,D,longitude,G,speed,direction,DDMMYY,vehicle_status#
			this.parseV1Position(cont);

		} else if (hexString.startsWith("58")) {

			try {
				String deviceId = null;
//				deviceId = GprsTcpThreadList.getInstance().getDeviceIdByTcpAddress(
//						socketAddress);
				ICommonGatewayService service = new CommonGatewayServiceImpl();
				service.getDeviceIdByTcpAddress(this.getSocket());
				log.info(
						"==从缓存获取  （TCP客户端地址，序列号）《=》（" + this.getSocket() + ","
								+ deviceId + "）");

				if (deviceId != null) {
					this.setDeviceSN(deviceId);
				}
			} catch (Exception e) {

			}

			 
			this.parseXPosition(hexString);

			

		}
 

	}

	// 带里程、温度位置信息
	private void parseXPosition(String hex) {
		int secLen = hex.length() / 64;
		ArrayList<ParseBase> pblist = new ArrayList<ParseBase>();
		
		for (int i = 0; i < secLen; i++) {
			ParaseSwyjGPRS swyj = new ParaseSwyjGPRS();
			swyj.setDeviceSN(this.getDeviceSN());
			
			String hexString = hex.substring(i * 64, i * 64 + 64);
			String dis = hexString.substring(2, 12);
			String sdis = Tools.removeZeroStr(dis);
			long ldis = Long.parseLong(sdis);
			float ddis = (ldis * 0.51444f) / 1000; // 里程，单位公里
			swyj.setMileage(ddis + "");

			String tempHex = hexString.substring(30, 32);
			String stemp = "";
			if (tempHex.equals("ff")) {
				stemp = "127.5";
				log.info(swyj.getDeviceSN() + " 没有配置温度传感器.");
			} else if (tempHex.equals("fe")) {
				stemp = "127";
				log.info(swyj.getDeviceSN() + " 运行中拆除温度传感器.");
			} else {
				stemp = Integer.parseInt(tempHex, 16) / 2.0 + ""; // 温度
				
			}

			String hms = hexString.substring(12, 12 + 6);
			String dmy = hexString.substring(18, 18 + 6);
			String ystr = hexString.substring(24, 28) + "."
					+ hexString.substring(28, 32);
			String latitude = swyj.getLatitudeValue(ystr);
			String xstr = hexString.substring(34, 39) + "."
					+ hexString.substring(39, 43);
			String longitude = swyj.getLongitudeValue(xstr);

			byte[] gs = Tools.fromHexString("0" + hexString.substring(43, 44));

			StringBuffer sbuf = new StringBuffer();

			String speed = hexString.substring(44, 47);
			speed = swyj.formatSpeed(speed);
			String direct = hexString.substring(47, 50);
			direct = Integer.parseInt(direct) + "";

			String vehicleStatus = hexString.substring(50, 58);
			byte[] statusBytes = Tools.fromHexString(vehicleStatus);// vehicleStatus.getBytes();

			String Usr_alarm_flag = hexString.substring(58, 60);
			String recordNo = hexString.substring(62, 64);

			String gpstime = Tools.conformtime(hms, dmy);

			ts = new Timestamp(new Date().getTime());

			swyj.setCoordX(longitude);
			swyj.setCoordY(latitude);
			// this.setPhnum(phnum);
			swyj.setTime(gpstime); 
			swyj.setSpeed(speed);
			swyj.setDirection(direct);
			String alarmStatus = null;
			//alarmStatus = this.getAlarmStatus(statusBytes);

			if (swyj.getByteBit(gs[0], 0) == 1) {
				stemp = "-" + stemp;
			}
//			swyj.setTemperature(stemp);
			log.info(
					"思维远见 X记录模式数据,deviceid=" + swyj.getDeviceSN() + "lng="
							+ swyj.getCoordX() + ",lat=" + swyj.getCoordY()
							+ ",speed=" + swyj.getSpeed() + ",direction="
							+ swyj.getDirection() + ",date=" + swyj.getTime()
							+ ",distance=" + swyj.getMileage()  );
			
			if (swyj.getByteBit(gs[0], 1) == 1) {
				sbuf.append("定位数据有效;");
				statusRec.setLocate("1");
				 
				 
			} else {
				statusRec.setLocate("0");
				sbuf.append("定位数据无效;");
			}
			if (this.getByteBit(gs[0], 2) == 1) {
				sbuf.append("北纬");
			} else {
				sbuf.append("南纬");
			}
			if (this.getByteBit(gs[0], 3) == 1) {
				sbuf.append("东经");
			} else {
				sbuf.append("西经");
			}
		
			log.info(
					"SWYJ 标准记录数据状态:"+sbuf.toString());
				 
			
		}
		this.setParseList(pblist);
 
	}

	private void parseV1Position(byte[] cont) {
		String HHMMSS = null; // 车载机时间,标准时间，与北京时间有8小时时差
		String s = null; // 数据有效位（A/V）
		String latitude = null; // 纬度
		String d = null; // 纬度标志（N：北纬，S：南纬）
		String longitude = null; // 经度
		String g = null; // 经度标志（E：东经，W：西经）
		String speed = null; // 速度,范围000.00 ~ 999.99 节，保留两位小数
		String direction = null; // 方位角，正北为0度，分辨率1度，顺时针方向
		String ddmmyy = null; // 日/月/年
		String dateConfirm = null;
		String keyS = null;
		String status = null;

		String dataStr = new String(cont);

		if (dataStr != null) {
			String[] contArr = dataStr.split("\\*HQ");
		 
			
			for (int i = 0; i < contArr.length; i++) {
				if (!contArr[i].endsWith("#")) {
					continue;
				} else {
					dataStr = "*HQ" + contArr[i];
				}
				String[] contents = dataStr.split(",");
				String gpssn = contents[1];
				this.setDeviceSN(gpssn);
				 

				String modeFlag = contents[2];

				String vehicleStatus = null; // 车辆状态，共四字节，表示车载机部件状态、车辆部件状态以及报警状态等
				if (modeFlag.equals("V1")) {
					HHMMSS = contents[3];
					s = contents[4];//有效位
					latitude = contents[5];
					d = contents[6];
					longitude = contents[7];
					g = contents[8];
					speed = this.formatSpeed(contents[9]);
					direction = contents[10];
					ddmmyy = contents[11];
					vehicleStatus = contents[12].substring(0, contents[12]
							.indexOf("#") + 1);
					keyS = "D1";
					DBService dbservice = new DBServiceImpl();
					dbservice.updateInstructionsState(gpssn, "0", keyS); //
					// 更新频率设置指令状态
					log.info("频率设置指令应答：" + dataStr);
				} else if (modeFlag.equals("V4")) {
					// *XX,YYYYYYYYYY,V4,CMD,hhmmss,HHMMSS,S,latitude,D,longitude,G,speed,direction,DDMMYY,vehicle_status#
					log.info("V4应答指令：" + dataStr);
					String cmd = contents[3];
					if (cmd.equals("S14")) {
						dateConfirm = contents[8];
						String param = contents[4] + "," + contents[5] + ","
								+ contents[6] + "," + contents[7];
						keyS = cmd + "," + dateConfirm + "," + param;
						status = "0";
						log.info(
								this.getDeviceSN() + " 超速设置应答。");
					} else if (cmd.equals("S18")) {
						dateConfirm = contents[6];
						String param = contents[4] + "," + contents[5];
						keyS = cmd + "," + dateConfirm + "," + param;
						status = "0";
						log.info(
								this.getDeviceSN() + " 设置区域报警持续时间应答。");
					} else if (cmd.equals("S21")) {
						dateConfirm = contents[6];
						String param = contents[4] + "," + contents[5];
						keyS = cmd + "," + dateConfirm + "," + param;
						status = "0";
						log.info(
								this.getDeviceSN() + " 设置围栏应答。");
					} else if (cmd.equals("S20")) {
						String desc = contents[4];
						if (desc != null && desc.equals("ERROR")) {
							status = "2";
							log.info(
									this.getDeviceSN() + " 不支持断油电功能");
						} else {
							status = "0";
						}
						dateConfirm = contents[5];
						keyS = cmd + "," + dateConfirm;

					} else if (cmd.equals("S6")) {
						String desc = contents[4];
						if (desc != null && desc.equals("ERROR")) {
							status = "2";
							 
						} else {
							status = "0";
						}
						dateConfirm = contents[5];
						keyS = cmd + "," + dateConfirm;

					} else if (cmd.equals("R8")) {
						String desc = contents[4];
						if (desc != null && desc.equals("ERROR")) {
							status = "2";
							log.info(
									this.getDeviceSN() + " 处于禁止监听状态");
						} else {
							status = "0";
						}
						dateConfirm = contents[5];
						keyS = cmd + "," + dateConfirm;
					} else if (cmd.equals("S23")) {
						String addr = contents[4].replaceAll("\\.", ",")
								.replaceAll("\\:", ",");
						dateConfirm = contents[5];
						keyS = cmd + "," + dateConfirm + "," + addr;
						status = "0";
						log.info(
								this.getDeviceSN() + " 通讯地址设置应答。");
					} else if (cmd.equals("S24")) {
						String addr = contents[4];
						dateConfirm = contents[5];
						keyS = cmd + "," + dateConfirm + "," + addr;
						status = "0";
						log.info(
								this.getDeviceSN() + " APN设置应答。");
					} else if (cmd.equals("S26")) {
						dateConfirm = contents[4];
						keyS = cmd + "," + dateConfirm;
						HHMMSS = contents[5];
						status = "0";
						String interval = contents[7];
						String maxSpd = contents[9];
						String minSpd = contents[10];
						String spdContinue = contents[11];
						String areaContinue = contents[12];
						String ctlState = contents[13];
						String state = this.parseCtrlState(ctlState);
						String ip = contents[23];
						log.info(
								this.getDeviceSN() + " 读取设备状态应答,监控间隔="
										+ interval + ",maxSpd=" + maxSpd
										+ ",minSpd=" + minSpd + ",spdDruation="
										+ spdContinue + ",areaDuration="
										+ areaContinue + ",ip=" + ip
										+ ",state=" + state);

					} else if (cmd.equals("S31")) {
						String desc = contents[4];
						if (desc != null && desc.equals("ERROR")) {
							status = "2";
							log.info(
									this.getDeviceSN() + " 没有传感器");
						} else {
							status = "0";
						}
						dateConfirm = contents[5];
						keyS = cmd + "," + dateConfirm;
					} else if (cmd.endsWith("S32")) {
						String dis = contents[4];
						double fdis = Double.parseDouble(dis) * 0.51444 / 1000; // 单位公里
						log.info(
								this.getDeviceSN() + "里程数据：" + fdis + "公里");
						dateConfirm = contents[5];
						keyS = cmd + "," + dateConfirm;
					} else if (cmd.equals("S34")) {
						String desc = contents[4];

						dateConfirm = contents[5];
						keyS = cmd + "," + dateConfirm;
					} else if (cmd.equals("S40")) {
						// *HQ,6091127203,V4,S40,2,1800,1200,180,180,FF,093234,001232,V,3958.8510,N,11617.9313,E,000.00,089,160710,FFFFFBFF#
						// *HQ,6091127203, S40,093234,2,1800,1200,180,180,FF#
						keyS = contents[3] + "," + contents[10];
						String  param =   contents[4] + "," + contents[5] + "," + contents[6] + "," + contents[7] + "," + contents[8] + "," + contents[9];
		 				log.info(
								this.getDeviceSN() + "设置疲劳驾驶参数："
										+ param);

					} else {
						dateConfirm = contents[4];
						keyS = cmd + "," + dateConfirm;
						HHMMSS = contents[5];
						status = "0";
					}
					ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + cmd, "0");
					log.info("keyS=" + keyS);
					try {
						DBService dbservice = new DBServiceImpl();
						dbservice.updateInstructionsState(gpssn, status, keyS);
					} catch (Exception e) {
						e.printStackTrace();
					}
					// 更新指令状态
				} else if (modeFlag.equals("V6")) {

					log.info(this.getDeviceSN() + "疲劳驾驶报告！");
					this.setAlarmType(AlarmType.FATIGUE_ALARM_TYPE);
					AlarmQueue.getInstance().addAlarm(this);

				} else if (modeFlag.equals("V2")) {
					String picNum = contents[3];
					log.info(
							this.getDeviceSN() + "新图片报告,图片编号：" + picNum);

				}
				if (modeFlag.equals("V1")) {
					if (longitude == null || "".equals(longitude)) {
						longitude = String.valueOf(0);
					} else {
						longitude = this.getLongitudeValue(longitude);
					}

					if (latitude == null || "".equals(latitude)) {
						latitude = String.valueOf(0);
					} else {
						latitude = this.getLatitudeValue(latitude);
					}

					if (direction == null || "".equals(direction)) {
						direction = String.valueOf(0);
					} else {
						direction = Integer.parseInt(direction) + "";
					}

					String gpstime = Tools.conformtime(HHMMSS, ddmmyy);
					// Date gdate = Tools.formatStrToDate(gpstime,
					// "yyyy-MM-dd HH:mm:ss");
					ts = new Timestamp(new Date().getTime());

					this.setCoordX(longitude);
					this.setCoordY(latitude);
					// this.setPhnum(term.getSimcard());
					this.setDeviceSN(gpssn);

					this.setTime(gpstime); 
					this.setSpeed(speed);
					this.setDirection(direction);

					byte[] statusBytes = Tools.fromHexString(vehicleStatus
							.substring(0, vehicleStatus.length() - 1));// vehicleStatus.getBytes();
					String alarmStatus = null;
					 alarmStatus = this.getAlarmStatus(statusBytes,this);
//					this.setAlarmDesc(alarmStatus);
					log.info(alarmStatus);
					
					if (s.equals("A")) {
						 
							statusRec.setLocate("1");
							 
					} else {
						statusRec.setLocate("0");
						log.info(this.getDeviceSN() + " 未定位！");
					}
				 
				}
			}
		}
	}

	private String parseCtrlState(String ctlState) {
		StringBuffer buf = new StringBuffer();
		byte stateB = ctlState.getBytes()[0];
		if (this.getByteBit(stateB, 0) == 1) {
			buf.append("禁止打入电话;");
		} else {
			buf.append("允许打入电话;");
		}
		if (this.getByteBit(stateB, 1) == 1) {
			buf.append("禁止打出电话;");
		} else {
			buf.append("允许打出电话;");
		}
		if (this.getByteBit(stateB, 2) == 1) {
			buf.append("速度限制是否定位都有效;");
		} else {
			buf.append("速度限制只在定位时有效;");
		}
		if (this.getByteBit(stateB, 3) == 1) {
			buf.append("越界报警触发S17监控;");
		} else {
			buf.append("越界报警不触发S17监控;");
		}
		if (this.getByteBit(stateB, 4) == 1) {
			buf.append("静态断油电;");
		} else {
			buf.append("动态断油电;");
		}
		if (this.getByteBit(stateB, 5) == 1) {
			buf.append("打开条件打入打出;");
		} else {
			buf.append("关闭条件打入打出;");
		}
		if (this.getByteBit(stateB, 6) == 1) {
			buf.append("打开GPS信息输出");
		} else {
			buf.append("关闭GPS信息输出");
		}

		return buf.toString();
	}

	private void parseImage(String hex) {
		String device_id = hex.substring(2, 2 + 10);
		this.setDeviceSN(device_id);

		  
		String hms = hex.substring(12, 12 + 6);
		String dmy = hex.substring(18, 18 + 6);
		String ystr = hex.substring(24, 28) + "." + hex.substring(28, 32);
		String latitude = this.getLatitudeValue(ystr);

		String bl = hex.substring(32, 34);// 保留

		String xstr = hex.substring(34, 39) + "." + hex.substring(39, 43);
		String longitude = this.getLongitudeValue(xstr);

		byte[] gs = Tools.fromHexString("0" + hex.substring(43, 44));

		StringBuffer sbuf = new StringBuffer();

		String speed = hex.substring(44, 47);
		speed = this.formatSpeed(speed);
		String direct = hex.substring(47, 50);
		direct = Integer.parseInt(direct) + "";
		String vehicleStatus = hex.substring(50, 58);
		byte[] statusBytes = Tools.fromHexString(vehicleStatus);// vehicleStatus.getBytes();

		String Usr_alarm_flag = hex.substring(58, 60);
		String pic_num = hex.substring(60, 62);
		int picNum = Integer.parseInt(pic_num, 16);
		String block_num = hex.substring(62, 64);
		int blockNum = Integer.parseInt(block_num, 16);
		String imgHex = hex.substring(64);
		Picture pic = null;
		if (blockNum == 0) { // 第一包
			String headHex = Tools
					.bytesToHexString(this.hx_4DHT_2DQT_tables_low);
			imgHex = imgHex.substring(0, 4) + headHex + imgHex.substring(4);
			pic = new Picture();
			pic.setDeviceId(this.getDeviceSN());
			pic.addImgContHex(blockNum + "", imgHex);
			pic.setNum(picNum);
			pic.setPakcNo(blockNum);
			pic.setDate(new Date()); 
			pic.setX(Float.parseFloat(this.getCoordX()));
			pic.setY(Float.parseFloat(this.getCoordY()));
			PicCache.getInstance().addPicture(this.getDeviceSN(), pic);
		}
		pic = PicCache.getInstance().getPicture(this.getDeviceSN());
		if (blockNum > 0 && pic != null) {
			pic.setDate(new Date());
			pic.addImgContHex(blockNum + "", imgHex);
			pic.setPakcNo(blockNum);
			PicCache.getInstance().addPicture(this.getDeviceSN(), pic);
			if (imgHex.lastIndexOf("ffffffff") != -1) {
				log.info(this.getDeviceSN() + "图片传输完毕！");
				DBService service = new DBServiceImpl();
				try {
					boolean flag = service.insertPicInfo(pic);
				} catch (Exception e) {
					e.printStackTrace();
				}
				pic.reset();

				PicCache.getInstance().removePicture(this.getDeviceSN());
			}
		}

		String gpstime = Tools.conformtime(hms, dmy);
		//ts = new Timestamp(new Date().getTime());

		this.setCoordX(longitude);
		this.setCoordY(latitude); 
		this.setTime(gpstime);
		//this.setTimeStamp(ts);
		this.setSpeed(speed);
		this.setDirection(direct);
		String alarmStatus = null;
		 alarmStatus = this.getAlarmStatus(statusBytes,this);

		if (this.getByteBit(gs[0], 1) == 1) {
			sbuf.append("定位数据有效;");
			statusRec.setLocate("1");
			 
		} else {
			sbuf.append("定位数据无效;");
			statusRec.setLocate("0");
		}

		log.info(
				this.getDeviceSN() + " $标准记录数据：" + sbuf.toString());

	}

	private void parse$Position(String hexString) {
		// 249090618604 124801 130809 39587923 00 116179891e 000000 fffffbff ff
		// 00 19
		int secLen = hexString.length() / 64;
		ArrayList<ParseBase> pblist = new ArrayList<ParseBase>();
		
		for (int i = 0; i < secLen; i++) {
			ParaseSwyjGPRS swyj = new ParaseSwyjGPRS();
			
			String hex = hexString.substring(i * 64, i * 64 + 64);
			if (hex.length() < 64)
				return;

			String device_id = hex.substring(2, 2 + 10);
			swyj.setDeviceSN(device_id);
			this.setDeviceSN(device_id);
 

//			String phnum = term.getSimcard();
			String hms = hex.substring(12, 12 + 6);
			String dmy = hex.substring(18, 18 + 6);
			String ystr = hex.substring(24, 28) + "." + hex.substring(28, 32);
			String latitude = swyj.getLatitudeValue(ystr);

			String bl = hex.substring(32, 34);// 保留

			String xstr = hex.substring(34, 39) + "." + hex.substring(39, 43);
			String longitude = swyj.getLongitudeValue(xstr);

			byte[] gs = Tools.fromHexString("0" + hex.substring(43, 44));

			StringBuffer sbuf = new StringBuffer();

			String speed = hex.substring(44, 47);
			speed = swyj.formatSpeed(speed);
			String direct = hex.substring(47, 50);
			direct = Integer.parseInt(direct) + "";
			String vehicleStatus = hex.substring(50, 58);
			byte[] statusBytes = Tools.fromHexString(vehicleStatus);// vehicleStatus.getBytes();

			String Usr_alarm_flag = hex.substring(58, 60);
			String recordNo = hex.substring(62, 64);

			String gpstime = Tools.conformtime(hms, dmy);
			// Date gdate = Tools.formatStrToDate(gpstime,
			// "yyyy-MM-dd HH:mm:ss");
			//ts = new Timestamp(new Date().getTime());

			swyj.setCoordX(longitude);
			swyj.setCoordY(latitude);
			 
			swyj.setTime(gpstime);
			//swyj.setTimeStamp(ts);
			swyj.setSpeed(speed);
			swyj.setDirection(direct);
			String alarmStatus = null;
			
			log.info(
					"SWYJ 标准记录数据 deviceid=" + swyj.getDeviceSN() + "lng="
							+ swyj.getCoordX() + ",lat=" + swyj.getCoordY()
							+ ",speed=" + swyj.getSpeed() + ",direction="
							+ swyj.getDirection() + ",date=" + swyj.getTime());

			if (this.getByteBit(gs[0], 1) == 1) {
				sbuf.append("定位数据有效;");
				statusRec.setLocate("1");
				 
			
				
			} else {
				sbuf.append("定位数据无效;");
				statusRec.setLocate("0");
			}
			if (this.getByteBit(gs[0], 2) == 1) {
				sbuf.append("北纬");
			} else {
				sbuf.append("南纬");
			}
			if (this.getByteBit(gs[0], 3) == 1) {
				sbuf.append("东经");
			} else {
				sbuf.append("西经");
			}
			alarmStatus = swyj.getAlarmStatus(statusBytes,swyj);
//			swyj.setAlarmDesc(alarmStatus);
			sbuf.append(";"+alarmStatus);
			log.info(
					"SWYJ 标准记录数据状态:"+sbuf.toString());
			 
					}
		
		this.setParseList(pblist);
	}

	public String getAlarmStatus(byte[] statusBytes,ParaseSwyjGPRS swyj) {

		StringBuffer statusBuffer = new StringBuffer();
		DBService dbs = new DBServiceImpl();
		
		byte status = statusBytes[1];
		if (this.getByteBit(status, 0) == 0) {
			statusBuffer.append("GPS接收机故障报警；");
			this.setAlarmType(AlarmType.GPS_MODEL_FAIL_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(swyj);
		}
		if (this.getByteBit(status, 3) == 0) {
			statusBuffer.append("主电断电；");
			this.setAlarmType(AlarmType.CUT_POWER_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(swyj);
		}
		if (this.getByteBit(status, 5) == 0) {
			statusBuffer.append("GPS开路；");
			this.setAlarmType(AlarmType.GPS_MAST_OPEN_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(swyj);
			
			// statusRec.setCarDoor("1");
		}
		if (this.getByteBit(status, 6) == 0) {
			statusBuffer.append("GPS短路；");
			this.setAlarmType(AlarmType.GPS_MAST_SHORT_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(swyj);
			// statusRec.setFortification("1");
		}

		byte vehCmptStatus = statusBytes[2];
		byte alarmStatus = statusBytes[3];
		if (this.getByteBit(vehCmptStatus, 0) == 0) {
			statusBuffer.append("车门开；");
			statusRec.setCarDoor("1");
		}
		if (this.getByteBit(vehCmptStatus, 1) == 0) {
			statusBuffer.append("车辆设防；");
			statusRec.setFortification("1");
		}
		if (this.getByteBit(vehCmptStatus, 2) == 0) {
			statusBuffer.append("ACC关；");
			statusRec.setAcc("0");
		} else {
			statusRec.setAcc("1");
		}

		if (this.getByteBit(alarmStatus, 0) == 0) {
			statusBuffer.append("盗警；");
			swyj.setAlarmType(AlarmType.SECURITY_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (this.getByteBit(alarmStatus, 1) == 0) {
			statusBuffer.append("劫警；");
			swyj.setAlarmType(AlarmType.ACTIVE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (this.getByteBit(alarmStatus, 2) == 0) {
			statusBuffer.append("超速报警；");
			swyj.setAlarmType(AlarmType.SPEED_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (this.getByteBit(alarmStatus, 3) == 0) {
			statusBuffer.append("非法点火报警；");
		}
		if (this.getByteBit(alarmStatus, 4) == 0) {
			statusBuffer.append("禁止驶入越界报警；");
			swyj.setAlarmType(AlarmType.AREA_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (this.getByteBit(alarmStatus, 7) == 0) {
			statusBuffer.append("禁止驶出越界报警；");
			swyj.setAlarmType(AlarmType.AREA_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		return statusBuffer.toString();
	}

	private String formatSpeed(String tmpSpeed) {
		String ret = "";
		double speed = 0;
		if (tmpSpeed != null && !tmpSpeed.trim().equals("")) {
			try {
				speed = Double.parseDouble(tmpSpeed);
			} catch (java.lang.NumberFormatException ex) {
				ex.printStackTrace();
			}
			speed = speed * 1.852;
		}
		// ret = "" + speed;
		// if (ret.length() > 4) {
		// ret = ret.substring(0, 4);
		// }
		NumberFormat nformat = NumberFormat.getNumberInstance();
		nformat.setMaximumFractionDigits(2);
		nformat.setMinimumFractionDigits(2);
		ret = nformat.format(speed).replaceAll("\\,", "");

		return ret;
	}

	/**
	 * parseGPRS
	 * 
	 * @param phnum
	 *            String
	 * @param content
	 *            String
	 * @todo Implement this com.mapabc.directl.parse.ParseBase method
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
	 * @todo Implement this com.mapabc.directl.parse.ParseBase method
	 */
	public void parseGPRS(String phnum, byte[] content) {
	}

	/**
	 * parseGPRS
	 * 
	 * @param hexString
	 *            byte[]
	 * @todo Implement this com.mapabc.directl.parse.ParseBase method
	 */
	public void parseGPRS(byte[] hexString) {

	}

	/**
	 * 
	 * @param longitude
	 *            String
	 * @return String
	 */
	public String getLongitudeValue(String longitude) {
		String result = "";
		double degree = Double.parseDouble(longitude.substring(0, 3));
		double xDegree = Double.parseDouble(longitude.substring(3, longitude
				.length())) / 60;
		java.text.DecimalFormat df1 = new java.text.DecimalFormat("##.000000");

		String blxs = "0" + df1.format(xDegree); // 保留6位小数
		result = String.valueOf(degree + Double.parseDouble(blxs));
		return result;
	}

	/**
	 * 
	 * @param latitude
	 *            String
	 * @return String
	 */
	public String getLatitudeValue(String latitude) {
		String result = "";
		double degree = Double.parseDouble(latitude.substring(0, 2));
		double xDegree = Double.parseDouble(latitude.substring(2, latitude
				.length())) / 60;
		java.text.DecimalFormat df1 = new java.text.DecimalFormat("##.000000");
		String blxs = "0" + df1.format(xDegree); // 保留6位小数
		result = String.valueOf(degree + Double.parseDouble(blxs));
		return result;
	}

	public String getTime(String value) {
		String result = "";
		String temp = value.substring(0, 2);
		int hour = Integer.parseInt(temp) + 8;
		if (hour < 10) {
			result = "0" + String.valueOf(hour) + value.substring(2);
		} else if (10 <= hour && hour <= 23) {
			result = String.valueOf(hour) + value.substring(2);
		} else if (hour > 23) {
			result = "0" + String.valueOf(Math.abs(hour - 24))
					+ value.substring(2);
		}
		return result;
	}

	/**
	 * 得到byte中的位值
	 * 
	 * @param data
	 *            byte
	 * @param pos
	 *            int
	 * @return int
	 */
	public static int getByteBit(byte data, int pos) {
		int bitData = 0;
		byte compare = (byte) Math.pow(2.0, pos);
		if ((data & compare) == compare) {
			bitData = 1;
		}
		return bitData;
	}

	/**
	 * parseSMS
	 * 
	 * @param phnum
	 *            String
	 * @param content
	 *            String
	 * @todo Implement this com.mapabc.directl.parse.ParseBase method
	 */
	public void parseSMS(String phnum, String content) {
	}

	public static void main(String[] args) {
		ParaseSwyjGPRS test = new ParaseSwyjGPRS();
		String command = "2460911272030137290903112308389100113194585e000237ffffffffff00782460911272030137390903112308388600113194595e000241ffffffffff00792460911272030137490903112308388400113194588e000243ffffffffff007a2460911272030137590903112308389200113194560e000253ffffffffff007b2460911272030138090903112308389400113194547e000257ffffffffff007c2460911272030138190903112308390000113194536e000248ffffffffff007d2460911272030138290903112308390100113194534e000230ffffffffff007e2460911272030138390903112308389700113194552e000135ffffffffff007f";
		test.parseGPRS(command);

	}

 

	private final byte[] hx_4DHT_2DQT_tables_low = { (byte) 0xff, (byte) 0xc4,
			(byte) 0x00, (byte) 0x1f, (byte) 0x00, (byte) 0x00, (byte) 0x01,
			(byte) 0x05, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
			(byte) 0x01, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05,
			(byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0a,
			(byte) 0x0b, (byte) 0xff, (byte) 0xc4, (byte) 0x00, (byte) 0xb5,
			(byte) 0x10, (byte) 0x00, (byte) 0x02, (byte) 0x01, (byte) 0x03,
			(byte) 0x03, (byte) 0x02, (byte) 0x04, (byte) 0x03, (byte) 0x05,
			(byte) 0x05, (byte) 0x04, (byte) 0x04, (byte) 0x00, (byte) 0x00,
			(byte) 0x01, (byte) 0x7d, (byte) 0x01, (byte) 0x02, (byte) 0x03,
			(byte) 0x00, (byte) 0x04, (byte) 0x11, (byte) 0x05, (byte) 0x12,
			(byte) 0x21, (byte) 0x31, (byte) 0x41, (byte) 0x06, (byte) 0x13,
			(byte) 0x51, (byte) 0x61, (byte) 0x07, (byte) 0x22, (byte) 0x71,
			(byte) 0x14, (byte) 0x32, (byte) 0x81, (byte) 0x91, (byte) 0xa1,
			(byte) 0x08, (byte) 0x23, (byte) 0x42, (byte) 0xb1, (byte) 0xc1,
			(byte) 0x15, (byte) 0x52, (byte) 0xd1, (byte) 0xf0, (byte) 0x24,
			(byte) 0x33, (byte) 0x62, (byte) 0x72, (byte) 0x82, (byte) 0x09,
			(byte) 0x0a, (byte) 0x16, (byte) 0x17, (byte) 0x18, (byte) 0x19,
			(byte) 0x1a, (byte) 0x25, (byte) 0x26, (byte) 0x27, (byte) 0x28,
			(byte) 0x29, (byte) 0x2a, (byte) 0x34, (byte) 0x35, (byte) 0x36,
			(byte) 0x37, (byte) 0x38, (byte) 0x39, (byte) 0x3a, (byte) 0x43,
			(byte) 0x44, (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48,
			(byte) 0x49, (byte) 0x4a, (byte) 0x53, (byte) 0x54, (byte) 0x55,
			(byte) 0x56, (byte) 0x57, (byte) 0x58, (byte) 0x59, (byte) 0x5a,
			(byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x67,
			(byte) 0x68, (byte) 0x69, (byte) 0x6a, (byte) 0x73, (byte) 0x74,
			(byte) 0x75, (byte) 0x76, (byte) 0x77, (byte) 0x78, (byte) 0x79,
			(byte) 0x7a, (byte) 0x83, (byte) 0x84, (byte) 0x85, (byte) 0x86,
			(byte) 0x87, (byte) 0x88, (byte) 0x89, (byte) 0x8a, (byte) 0x92,
			(byte) 0x93, (byte) 0x94, (byte) 0x95, (byte) 0x96, (byte) 0x97,
			(byte) 0x98, (byte) 0x99, (byte) 0x9a, (byte) 0xa2, (byte) 0xa3,
			(byte) 0xa4, (byte) 0xa5, (byte) 0xa6, (byte) 0xa7, (byte) 0xa8,
			(byte) 0xa9, (byte) 0xaa, (byte) 0xb2, (byte) 0xb3, (byte) 0xb4,
			(byte) 0xb5, (byte) 0xb6, (byte) 0xb7, (byte) 0xb8, (byte) 0xb9,
			(byte) 0xba, (byte) 0xc2, (byte) 0xc3, (byte) 0xc4, (byte) 0xc5,
			(byte) 0xc6, (byte) 0xc7, (byte) 0xc8, (byte) 0xc9, (byte) 0xca,
			(byte) 0xd2, (byte) 0xd3, (byte) 0xd4, (byte) 0xd5, (byte) 0xd6,
			(byte) 0xd7, (byte) 0xd8, (byte) 0xd9, (byte) 0xda, (byte) 0xe1,
			(byte) 0xe2, (byte) 0xe3, (byte) 0xe4, (byte) 0xe5, (byte) 0xe6,
			(byte) 0xe7, (byte) 0xe8, (byte) 0xe9, (byte) 0xea, (byte) 0xf1,
			(byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5, (byte) 0xf6,
			(byte) 0xf7, (byte) 0xf8, (byte) 0xf9, (byte) 0xfa, (byte) 0xff,
			(byte) 0xc4, (byte) 0x00, (byte) 0x1f, (byte) 0x01, (byte) 0x00,
			(byte) 0x03, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
			(byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01, (byte) 0x01,
			(byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
			(byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
			(byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09,
			(byte) 0x0a, (byte) 0x0b, (byte) 0xff, (byte) 0xc4, (byte) 0x00,
			(byte) 0xb5, (byte) 0x11, (byte) 0x00, (byte) 0x02, (byte) 0x01,
			(byte) 0x02, (byte) 0x04, (byte) 0x04, (byte) 0x03, (byte) 0x04,
			(byte) 0x07, (byte) 0x05, (byte) 0x04, (byte) 0x04, (byte) 0x00,
			(byte) 0x01, (byte) 0x02, (byte) 0x77, (byte) 0x00, (byte) 0x01,
			(byte) 0x02, (byte) 0x03, (byte) 0x11, (byte) 0x04, (byte) 0x05,
			(byte) 0x21, (byte) 0x31, (byte) 0x06, (byte) 0x12, (byte) 0x41,
			(byte) 0x51, (byte) 0x07, (byte) 0x61, (byte) 0x71, (byte) 0x13,
			(byte) 0x22, (byte) 0x32, (byte) 0x81, (byte) 0x08, (byte) 0x14,
			(byte) 0x42, (byte) 0x91, (byte) 0xa1, (byte) 0xb1, (byte) 0xc1,
			(byte) 0x09, (byte) 0x23, (byte) 0x33, (byte) 0x52, (byte) 0xf0,
			(byte) 0x15, (byte) 0x62, (byte) 0x72, (byte) 0xd1, (byte) 0x0a,
			(byte) 0x16, (byte) 0x24, (byte) 0x34, (byte) 0xe1, (byte) 0x25,
			(byte) 0xf1, (byte) 0x17, (byte) 0x18, (byte) 0x19, (byte) 0x1a,
			(byte) 0x26, (byte) 0x27, (byte) 0x28, (byte) 0x29, (byte) 0x2a,
			(byte) 0x35, (byte) 0x36, (byte) 0x37, (byte) 0x38, (byte) 0x39,
			(byte) 0x3a, (byte) 0x43, (byte) 0x44, (byte) 0x45, (byte) 0x46,
			(byte) 0x47, (byte) 0x48, (byte) 0x49, (byte) 0x4a, (byte) 0x53,
			(byte) 0x54, (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58,
			(byte) 0x59, (byte) 0x5a, (byte) 0x63, (byte) 0x64, (byte) 0x65,
			(byte) 0x66, (byte) 0x67, (byte) 0x68, (byte) 0x69, (byte) 0x6a,
			(byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76, (byte) 0x77,
			(byte) 0x78, (byte) 0x79, (byte) 0x7a, (byte) 0x82, (byte) 0x83,
			(byte) 0x84, (byte) 0x85, (byte) 0x86, (byte) 0x87, (byte) 0x88,
			(byte) 0x89, (byte) 0x8a, (byte) 0x92, (byte) 0x93, (byte) 0x94,
			(byte) 0x95, (byte) 0x96, (byte) 0x97, (byte) 0x98, (byte) 0x99,
			(byte) 0x9a, (byte) 0xa2, (byte) 0xa3, (byte) 0xa4, (byte) 0xa5,
			(byte) 0xa6, (byte) 0xa7, (byte) 0xa8, (byte) 0xa9, (byte) 0xaa,
			(byte) 0xb2, (byte) 0xb3, (byte) 0xb4, (byte) 0xb5, (byte) 0xb6,
			(byte) 0xb7, (byte) 0xb8, (byte) 0xb9, (byte) 0xba, (byte) 0xc2,
			(byte) 0xc3, (byte) 0xc4, (byte) 0xc5, (byte) 0xc6, (byte) 0xc7,
			(byte) 0xc8, (byte) 0xc9, (byte) 0xca, (byte) 0xd2, (byte) 0xd3,
			(byte) 0xd4, (byte) 0xd5, (byte) 0xd6, (byte) 0xd7, (byte) 0xd8,
			(byte) 0xd9, (byte) 0xda, (byte) 0xe2, (byte) 0xe3, (byte) 0xe4,
			(byte) 0xe5, (byte) 0xe6, (byte) 0xe7, (byte) 0xe8, (byte) 0xe9,
			(byte) 0xea, (byte) 0xf2, (byte) 0xf3, (byte) 0xf4, (byte) 0xf5,
			(byte) 0xf6, (byte) 0xf7, (byte) 0xf8, (byte) 0xf9, (byte) 0xfa,
			(byte) 0xff, (byte) 0xdb, (byte) 0x00, (byte) 0x43, (byte) 0x00,
			(byte) 0x08, (byte) 0x06, (byte) 0x06, (byte) 0x07, (byte) 0x06,
			(byte) 0x05, (byte) 0x08, (byte) 0x07, (byte) 0x07, (byte) 0x07,
			(byte) 0x09, (byte) 0x09, (byte) 0x08, (byte) 0x0a, (byte) 0x0c,
			(byte) 0x14, (byte) 0x0d, (byte) 0x0c, (byte) 0x0b, (byte) 0x0b,
			(byte) 0x0c, (byte) 0x19, (byte) 0x12, (byte) 0x13, (byte) 0x0f,
			(byte) 0x14, (byte) 0x1d, (byte) 0x1a, (byte) 0x1f, (byte) 0x1e,
			(byte) 0x1d, (byte) 0x1a, (byte) 0x1c, (byte) 0x1c, (byte) 0x20,
			(byte) 0x24, (byte) 0x2e, (byte) 0x27, (byte) 0x20, (byte) 0x22,
			(byte) 0x2c, (byte) 0x23, (byte) 0x1c, (byte) 0x1c, (byte) 0x28,
			(byte) 0x37, (byte) 0x29, (byte) 0x2c, (byte) 0x30, (byte) 0x31,
			(byte) 0x34, (byte) 0x34, (byte) 0x34, (byte) 0x1f, (byte) 0x27,
			(byte) 0x39, (byte) 0x3d, (byte) 0x38, (byte) 0x32, (byte) 0x3c,
			(byte) 0x2e, (byte) 0x33, (byte) 0x34, (byte) 0x32, (byte) 0xff,
			(byte) 0xdb, (byte) 0x00, (byte) 0x43, (byte) 0x01, (byte) 0x09,
			(byte) 0x09, (byte) 0x09, (byte) 0x0c, (byte) 0x0b, (byte) 0x0c,
			(byte) 0x18, (byte) 0x0d, (byte) 0x0d, (byte) 0x18, (byte) 0x32,
			(byte) 0x21, (byte) 0x1c, (byte) 0x21, (byte) 0x32, (byte) 0x32,
			(byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32,
			(byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32,
			(byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32,
			(byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32,
			(byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32,
			(byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32,
			(byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32,
			(byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32,
			(byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32, (byte) 0x32,
			(byte) 0x32, (byte) 0x32, (byte) 0x32 };
}
