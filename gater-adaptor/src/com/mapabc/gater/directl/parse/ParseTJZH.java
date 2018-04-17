/**
 * 
 */
package com.mapabc.gater.directl.parse;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.directl.update.*;
import com.mapabc.gater.lbsgateway.GBLTerminalList; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.util.RemoteUpdateProgram;



/**
 * @author shiguang.zhou
 * 
 */
public class ParseTJZH extends com.mapabc.gater.directl.parse.ParseBase  implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseTJZH.class);
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

	private String riginalX;
	private String riginalY;
	TTerminal term = null;
	private TTermStatusRecord termStatus = new TTermStatusRecord();
 
	public void parseGPRS(String hexString) {
		// TODO Auto-generated method stub
		if (hexString == null || hexString.trim().length() <= 0) {
			return;
		}
		log.info("接收天津中环终端原始数据:" + hexString);
		byte[] cont = Tools.fromHexString(hexString);
		String hexLen = hexString.substring(6, 10); // 包长度hex
		int leng = Tools.byte2Int(Tools.fromHexString(hexLen));

		byte bcmd = cont[2]; // 主信令码
		byte subCmd = cont[9]; // 子信令码
		byte verfyCode = cont[cont.length - 2];// 校验值

		// if (bcmd != (byte) 0x88 || bcmd != (byte) 0x22) {
		String simIp = hexString.substring(10, 18);// 伪IP,由SIM卡转换得到
		int ip1 = Tools.byte2Int(Tools.fromHexString(hexString
				.substring(10, 12)));
		int ip2 = Tools.byte2Int(Tools.fromHexString(hexString
				.substring(12, 14)));
		int ip3 = Tools.byte2Int(Tools.fromHexString(hexString
				.substring(14, 16)));
		int ip4 = Tools.byte2Int(Tools.fromHexString(hexString
				.substring(16, 18)));
		String deviceid = String.valueOf(ip1) + "." + String.valueOf(ip2) + "."
				+ String.valueOf(ip3) + "." + String.valueOf(ip4);
		this.setDeviceSN(deviceid);
		
		term = GBLTerminalList.getInstance().getTerminaInfo(deviceid);//
		// 从内存获取终端SIMCARD
		if (term == null) {
			log.info("系统中没有适配到指定的终端：device_id=" + deviceid);
			return;
		}
		
		//this.setObjId(term.getObjId());
		//this.setObjType(term.getObjType());
		this.setPhnum(term.getSimcard());
		// }
		String locStatus = null;
		switch (bcmd) {
		case (byte) 0x80:// 一般位置

			this.parsePosition(hexString, verfyCode, bcmd, subCmd, 0);

			try {
				locStatus = this.getLocateStatus();
				if (locStatus != null && !locStatus.equals("0")) {
	 					 
				} else {
					log.info(
							"过滤未定位数据：" + this.getDeviceSN() + ","
									+ this.getCoordX() + "," + this.getCoordY()
									+ "," + this.getTime());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case (byte) 0x81:// 点名位置

			this.parsePosition(hexString, verfyCode, bcmd, subCmd, 0);
			try {
				locStatus = this.getLocateStatus();
				if (locStatus != null && !locStatus.equals("0")) {
	 				} else {
					log.info(
							"点名==过滤未定位数据：" + this.getDeviceSN() + ","
									+ this.getCoordX() + "," + this.getCoordY()
									+ "," + this.getTime());
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case (byte) 0x83:// 查看车辆状态

			// 2929 83 001a 4a8d6308 000000000000 0000 00 01 0078 02 ff 3c 00 00
			// 00 0000 8d 0d
			if (leng == 26) {// 适配长江终端

				String sampleDate = hexString.substring(18, 30);
				String alarmStatus = hexString.substring(30, 34);
				String locateStatus = hexString.substring(34, 36);
				String sampleType = hexString.substring(36, 38);
				String sampleValue = hexString.substring(38, 42);

				String sendType = hexString.substring(42, 44);
				String stopCar = hexString.substring(44, 46);
				String speedSet = hexString.substring(46, 48);
				String teleSet = hexString.substring(48, 50);
				String nodeSet = hexString.substring(50, 52);

				log.info(
						"查看长江通信终端状态:deviceid=" + this.getDeviceSN()
								+ ",sampleDate=" + sampleDate + ",alarmStatus="
								+ alarmStatus + ",locateStatus=" + locateStatus
								+ ",sampleType=" + sampleType + ",sampleValue="
								+ sampleValue + ",sendType=" + sendType
								+ ",speedSet=" + speedSet);

			} else {// 天津中环终端
				this.parsePosition(hexString, verfyCode, bcmd, subCmd, 0);
				try {
					locStatus = this.getLocateStatus();
					if (locStatus != null && !locStatus.equals("0")) {
 					} else {
						log.info(
								"查看车辆状态==过滤未定位数据：" + this.getDeviceSN() + ","
										+ this.getCoordX() + ","
										+ this.getCoordY() + ","
										+ this.getTime());
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			break;
		case (byte) 0x84: // 调度信息

			this.parseMessage(cont, verfyCode, bcmd, subCmd);
 
			break;
		case (byte) 0x85:// 终端相关设置返回的位置
			String flag = "0";

			if (leng == 11) {// 适配长江终端
				String mainCmd = hexString.substring(18, 20);
	 
				flag = hexString.substring(22, 24);
				flag = flag.equals("01") ? "1" : "0";
			} else {// 天津中环终端
				this.parsePosition(hexString, verfyCode, bcmd, subCmd, 0);
				flag = "1";
			}

			try {
				String date = Tools.formatDate2Str(new Date(),
						"yyyy-MM-dd HH:mm:ss");
				this.setTime(date);
 
				// ParseConfigParamUtil.handleConfig(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info(
					this.getDeviceSN() + "设置指令成功");
			break;
		case (byte) 0x8e: // 盲区补偿

			this.parsePosition(hexString, verfyCode, bcmd, subCmd, 1);

			try {
				if (this.getLocateStatus() != null
						&& this.getLocateStatus().equals("1")) {
					this.setLocateStatus("2");// 补偿类型
 				} else {
					log.info(
							this.getDeviceSN() + " 定位补偿数据处于未定位状态，不入库:"
									+ this.getCoordX() + "," + this.getCoordY()
									+ "," + this.getTime());
				}
				// this.getRepList().add("LOC");

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;
		case (byte) 0x82: // 报警特殊处理
			this.parseSpecialAlarm(hexString, verfyCode, bcmd, subCmd, 1);
			break;
		case (byte) 0x22: // 远程升级请求

			byte scmd = cont[11];
			RemoteUpdateProgram rup = RemoteUpdateProgram.getInstance();

			if (scmd == (byte) 0x62) {

				byte[] pack = rup.getPackBytes("1");// 第一个包
				byte[] repData = this.loadData(1, rup.size(), pack, simIp);
				this.setReplyByte(repData);

				Date date = new Date();

				UpdateBean ub = new UpdateBean();
				ub.setDate(date);
				ub.setCurPackNo(1);
				ub.setDeviceId(this.getDeviceSN());
				ub.setPackCont(repData);
				ub.setTotalPackNum(rup.size());

				RemoteUpdateList.getInstance().addRemoteProgram(
						this.getDeviceSN(), ub);

//				TerminalUDPAddress curUdp = WaitingUpdateList.getInstance()
//						.getGpsThreadBySim(this.getDeviceSN());
//				if (curUdp != null) {
//					curUdp.setSend(false);
//					WaitingUpdateList.getInstance().add(this.getDeviceSN(),
//							curUdp);
//				}

				log.info(
						this.getDeviceSN() + "远程升级：packNO=1,内容："
								+ Tools.bytesToHexString(repData));
			}
			if (scmd == (byte) 0x63) {

				UpdateBean bean = RemoteUpdateList.getInstance()
						.getRemoteUpdateProgram(this.getDeviceSN());// remove(this.getDeviceSN());

				try {

					bean.setCurPackNo(-1);
					bean.setDate(null);
					bean.setDeviceId(null);
					bean.setPackCont(null);
					bean.setTotalPackNum(-1);
					RemoteUpdateList.getInstance().addRemoteProgram(deviceid,
							bean);

					RemoteUpdateList.getInstance().remove(deviceid);

					String date = Tools.formatDate2Str(new Date(),
							"yyyy-MM-dd HH:mm:ss");
					this.setTime(date); 
					// 删除自动升级列表
					WaitingUpdateList.getInstance().finishAutoUpdate(
							this.getDeviceSN());
					DBService dbs = new DBServiceImpl();
					dbs.updateRemoteStatus(this.getDeviceSN(), "1", "success");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				log.info(this.getDeviceSN() + "远程升级结束回应。");
			}
			break;
		case (byte) 0x88: // 远程升级请求
			// //删除自动升级列表
			// WaitingUpdateList.getInstance().removeUDP(this.getDeviceSN());

			byte[] bCurNo = new byte[2];
			byte[] btotal = new byte[2];
			byte[] pack = null;
			byte[] repData = null;

			System.arraycopy(cont, 9, bCurNo, 0, 2);
			System.arraycopy(cont, 11, btotal, 0, 2);

			int curPackNo = Tools.byte2Int(bCurNo);

			int total = Tools.byte2Int(btotal);
			RemoteUpdateProgram requp = RemoteUpdateProgram.getInstance();

			byte vcFlag = cont[13];
			if (vcFlag == 1) {// 验证成功，发下一个包
				if (curPackNo == total) {
					byte[] finishB = this.finish(simIp);
					this.setReplyByte(finishB);
					try {

						String date = Tools.formatDate2Str(new Date(),
								"yyyy-MM-dd HH:mm:ss");
						this.setTime(date); 

						// 删除自动升级列表
						WaitingUpdateList.getInstance().finishAutoUpdate(
								this.getDeviceSN());
						DBService dbs = new DBServiceImpl();
						dbs.updateRemoteStatus(this.getDeviceSN(), "1",
								"success");

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					log.info(
							this.getDeviceSN() + "远程升级结束，共发送了" + total
									+ "个数据包。");
					break;
				}

				Date date = new Date();
				pack = requp.getPackBytes((curPackNo + 1) + "");// 发下一个包

				if (pack != null && pack.length > 0) {
					repData = this.loadData(curPackNo + 1, requp.size(), pack,
							simIp);

					UpdateBean ub = new UpdateBean();// RemoteUpdateList.getInstance().getRemoteUpdateProgram(deviceid);
					ub.setDate(date);
					ub.setCurPackNo(curPackNo + 1);
					ub.setDeviceId(this.getDeviceSN());
					ub.setPackCont(repData);
					ub.setTotalPackNum(requp.size());

					this.setReplyByte(repData);

					RemoteUpdateList.getInstance().addRemoteProgram(
							this.getDeviceSN(), ub);
					log.info(
							this.getDeviceSN() + "远程升级：packNO="
									+ (curPackNo + 1) + ",内容："
									+ Tools.bytesToHexString(repData));
				}
				// RemoteUpdateList.getInstance().addRemoteProgram(this.getDeviceSN(),
				// requp);
			} else if (vcFlag == 2) {// 验证失败，重发
				if (pack != null && pack.length > 0) {
					Date date = new Date();

					pack = requp.getPackBytes((curPackNo) + "");// 补发
					repData = this.loadData(curPackNo, requp.size(), pack,
							simIp);
					this.setReplyByte(repData);

					UpdateBean ub = new UpdateBean();// RemoteUpdateList.getInstance().getRemoteUpdateProgram(deviceid);
					ub.setDate(date);
					ub.setCurPackNo(curPackNo);
					ub.setDeviceId(this.getDeviceSN());
					ub.setPackCont(repData);
					ub.setTotalPackNum(requp.size());

					RemoteUpdateList.getInstance().addRemoteProgram(
							this.getDeviceSN(), ub);
					log.info(
							this.getDeviceSN() + "补发包：packNO=" + (curPackNo)
									+ ",内容：" + Tools.bytesToHexString(repData));
				}
			}

			break;
		}

	}

	// 解析一般的位置信息
	private void parsePosition(String hexString, byte verfyCode, byte bcmd,
			byte subCmd, int type) {

		String date = hexString.substring(18, 20) + "-"
				+ hexString.substring(20, 22) + "-"
				+ hexString.substring(22, 24);
		String time = hexString.substring(24, 26) + ":"
				+ hexString.substring(26, 28) + ":"
				+ hexString.substring(28, 30);
		String gpstime = "20" + date + " " + time;
		this.setTime(gpstime);
//		Timestamp  timestamp = new Timestamp(Tools.formatStrToDate(this.getTime(), "yyyy-MM-dd HH:mm:ss").getTime());
//		this.setTimeStamp(timestamp);

		// 03909301 11712342
		String dy = hexString.substring(30, 33);// 纬度度
		String fy = hexString.substring(33, 38);// 纬度分
		double y = 0;
		try {
			y = Integer.parseInt(dy) + Double.parseDouble(fy) / 1000 / 60.0d;// 纬度
		} catch (NumberFormatException e) {
			y = 0;
		}
		String coordY = Tools.getNumberFormatString(y, 6, 6);
		this.setCoordY(coordY);
		this.riginalY = coordY;

		String dx = hexString.substring(38, 41);// 经度度
		String fx = hexString.substring(41, 46);// 经度分
		double x = 0;
		try {
			x = Integer.parseInt(dx) + Double.parseDouble(fx) / 1000 / 60.0d;// 纬度
		} catch (NumberFormatException e) {
			x = 0;
		}

		String coordX = Tools.getNumberFormatString(x, 6, 6);
		this.setCoordX(coordX);
		this.riginalX = coordX;

		String speed = "";
		try {
			speed = Integer.parseInt(hexString.substring(46, 50)) + "";// 速度
		} catch (NumberFormatException e) {
			speed = "0";
		}

		this.setSpeed(speed);
		String direction = "";
		try {
			direction = Integer.parseInt(hexString.substring(50, 54)) + "";// 方向
		} catch (NumberFormatException e) {
			direction = "0";
		}

		this.setDirection(direction);

		String status = hexString.substring(54, 56);// 定位，天线，电源状态
		int status_int = Tools.byte2Int(Tools.fromHexString(status));
		String binayStr = Integer.toBinaryString(status_int);
		binayStr = this.buZero(binayStr, 8);
		String resSt = this.parseStatus(binayStr);

		String resSt1 = null;
		String resSt2 = null;
		String resSt3 = null;
		String resSt4 = null;
		String locTime = null;
		String stopCartime = null;
		String overSpeedTime = null;
		String areaCount = null;
		String repCmdId = null;

		if (type == 0) {// 一般位置信息有如下内容
			String hexLC = hexString.substring(56, 62);
			String distance = Tools.byte2Long(Tools.fromHexString(hexLC)) + "";// 里程,单位米

			this.setMileage(distance); 

			String st1 = hexString.substring(62, 64);
			int ist1 = Tools.byte2Int(Tools.fromHexString(st1));
			st1 = this.buZero(Integer.toBinaryString(ist1), 8);
			resSt1 = this.parseStatus1(st1);

			String st2 = hexString.substring(64, 66);
			int ist2 = Tools.byte2Int(Tools.fromHexString(st2));
			st2 = this.buZero(Integer.toBinaryString(ist2), 8);
			resSt2 = this.parseStatus2(st2);

			String st3 = hexString.substring(66, 68);
			int ist3 = Tools.byte2Int(Tools.fromHexString(st3));
			st3 = this.buZero(Integer.toBinaryString(ist3), 8);
			resSt3 = this.parseStatus3(st3, verfyCode, bcmd, subCmd);

			String st4 = hexString.substring(68, 70);
			int ist4 = Tools.byte2Int(Tools.fromHexString(st4));
			st4 = this.buZero(Integer.toBinaryString(ist4), 8);
			resSt4 = this.parseStatus4(st4);

			locTime = Tools.byte2Int(Tools.fromHexString(hexString.substring(
					70, 74)))
					+ "";// 定位发送时间
			stopCartime = Tools.byte2Int(Tools.fromHexString(hexString
					.substring(74, 76)))
					+ "";// 停车设置时间
			overSpeedTime = Tools.byte2Int(Tools.fromHexString(hexString
					.substring(76, 78)))
					+ "";// 超速设置时间
			areaCount = Tools.byte2Int(Tools.fromHexString(hexString.substring(
					78, 80)))
					+ "";// 区域个数
			repCmdId = hexString.substring(84, 86);// 主命令ID 
		}

		byte[] repb = this.reply2Terminal(verfyCode, bcmd);
		this.setReplyByte(repb);

		log.info(
				this.getDeviceSN() + "中环终端位置数据：x=" + this.getCoordX() + ",y="
						+ this.getCoordY() + ",s=" + this.getSpeed()
						+ ",dirction=" + this.getDirection() + ",distance="
						+ this.getMileage() + ",time=" + this.getTime());
		log.info(
				this.getDeviceSN() + "状态：" + resSt + "," + resSt1 + ","
						+ resSt2 + "," + resSt3 + "," + resSt4);
		log.info(this.getDeviceSN() + "参数，定位发送时间=" + locTime/**
																			 * +
																			 * ",停车设置时间=" +
																			 * stopCartime +
																			 * ",超速设置时间=" +
																			 * overSpeedTime +
																			 * ",区域个数=" +
																			 * areaCount
																			 */
		);

	}

	// 解析上行调度信息
	private void parseMessage(byte[] cont, byte verfyCode, byte bcmd,
			byte subCmd) {

		byte[] bmsg = new byte[cont.length - 11];
		System.arraycopy(cont, 9, bmsg, 0, bmsg.length);
		try {
			String msg = new String(bmsg, "GB2312"); 
			String date = Tools.formatDate2Str(new Date(),
					"yyyy-MM-dd HH:mm:ss");
			this.setTime(date);
			byte[] repb = this.reply2Terminal(verfyCode, bcmd);
			this.setReplyByte(repb);
			
			try {
				this.termStatus.setCpu(msg );
				this.setStatusRecord(termStatus);
				log.info(
						this.getDeviceSN() + " 版本信息：" + msg);
				log.info(
						this.getDeviceSN() + " version："
								+ this.termStatus.getCpu()); 
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			DBService service = new DBServiceImpl();
//			long gid = this.term.getGid();
//			service.saveMessage(this.getDeviceSN(), "监控平台", msg, "1",gid);
			
			log.info(this.getDeviceSN() + "上行了调度信息：" + msg);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	// 解析报警特殊处理
	private void parseSpecialAlarm(String hexString, byte verfyCode, byte bcmd,
			byte subCmd, int type) {
		String s = "";

		this.parsePosition(hexString, verfyCode, bcmd, subCmd, type);

		String st1 = hexString.substring(hexString.length() - 20, hexString
				.length() - 18);
		int ist1 = Tools.byte2Int(Tools.fromHexString(st1));
		st1 = this.buZero(Integer.toBinaryString(ist1), 8);
		if (st1.charAt(7) == 1) {
			// 如区域
			s += "入区域，";
		}
		if (st1.charAt(6) == 1) {
			// 如区域
			s += "出区域，";
		}
		String st2 = hexString.substring(hexString.length() - 18, hexString
				.length() - 16);
		int ist2 = Tools.byte2Int(Tools.fromHexString(st2));
		st2 = this.buZero(Integer.toBinaryString(ist2), 8);
		if (st1.charAt(7) == 1) {
			// 如区域
			s += "非法开门，";
		}
		if (st1.charAt(6) == 1) {
			// 如区域
			s += "拖车报警，";
		}
		if (st1.charAt(5) == 1) {
			// 如区域
			s += "震动报警，";
		}
		if (st1.charAt(4) == 1) {
			// 如区域
			s += "网关报警，";
		}

		if (st1.charAt(3) == 1) {
			// 如区域
			s += "断电报警，";
		}
		if (st1.charAt(2) == 1) {
			// 如区域
			s += "停车报警，";
		}

		if (st1.charAt(1) == 1) {
			// 如区域
			s += "超速报警，";
		}
		if (st1.charAt(0) == 1) {
			// 如区域
			s += "应急报警,";
		}

		String areaNum = hexString.substring(hexString.length() - 14, hexString
				.length() - 12);
		int iareaNum = Tools.byte2Int(Tools.fromHexString(areaNum));
		s += "区域编号=" + iareaNum;
		log.info(this.getDeviceSN() + "特殊报警信息：" + s);

	}

	// 前缀补0
	private String buZero(String s, int n) {
		String ret = "";
		while (s.length() < n) {
			s = "0" + s;
		}
		ret = s;
		return s;
	}

	private String parseStatus(String status) {
		String ret = "";

		StringBuilder sbuf = new StringBuilder();

		if (status == null || status.trim().length() <= 0) {
			return null;
		}
		if (status.charAt(0) == '1') {
			sbuf.append("已定位，");
			this.setLocateStatus("1");
			ret += "1:1;";
		} else {
			sbuf.append("未定位，");
			this.setLocateStatus("0");
			ret += "1:0;";
			// lbs buchang
		}
		if (status.charAt(1) == '1' && status.charAt(2) == '1') {
			sbuf.append("GPS正常，");
			ret += "2:3;";
		} else if (status.charAt(1) == '1' && status.charAt(2) == '0') {
			sbuf.append("GPS短路，");
			ret += "2:2;";
		} else if (status.charAt(1) == '0' && status.charAt(2) == '1') {
			sbuf.append("GPS开路，");
			ret += "2:1;";
		} else if (status.charAt(1) == '0' && status.charAt(2) == '0') {
			sbuf.append("GPS故障，");
			ret += "2:0;";
		}
		if (status.charAt(3) == '1' && status.charAt(4) == '1') {
			sbuf.append("电源正常，");
			ret += "3:2;";
		} else if (status.charAt(3) == '1' && status.charAt(4) == '0') {
			sbuf.append("主电掉电，");
			// 断电报警
			this.setAlarmType("5"); 
			ret += "3:0;";
			// 增加断电报警到报警队列
			AlarmQueue.getInstance().addAlarm(this);
		} else if (status.charAt(3) == '0' && status.charAt(4) == '1') {
			sbuf.append("主电源过高或过低，");
			ret += "3:1;";
		} 
		log.info(this.getDeviceSN() + "状态1：" + sbuf.toString());
		return ret;

	}

	public String parseStatus1(String binay) {
		String ret = "";
		StringBuilder sbuf = new StringBuilder();

		if (binay.charAt(0) == '0') {
			sbuf.append("ACC开，");
			ret += "4:0;";
		} else {
			sbuf.append("ACC关，");
			ret += "4:1;";
		}
		// if (binay.charAt(1) == '0') {
		// sbuf.append("自定义1路高传感器状态为高，");
		// } else {
		// sbuf.append("自定义1路高传感器状态为低，");
		// }
		// if (binay.charAt(2) == '0') {
		// sbuf.append("自定义2路高传感器状态为高，");
		// } else {
		// sbuf.append("自定义2路高传感器状态为低，");
		// }
		// if (binay.charAt(3) == '0') {
		// sbuf.append("自定义1路低传感器状态为低，");
		// } else {
		// sbuf.append("自定义1路低传感器状态为高，");
		// }
		// if (binay.charAt(4) == '0') {
		// sbuf.append("自定义2路低传感器状态为低，");
		// } else {
		// sbuf.append("自定义2路低传感器状态为高，");
		// }
		if (binay.charAt(5) == '0') {
			sbuf.append("油路断开，");
			ret += "5:0;";
		} else {
			sbuf.append("油路正常，");
			ret += "5:1;";
		}
		// if (binay.charAt(6) == '0') {
		// sbuf.append("已登签，");
		// } else {
		// sbuf.append("没有登签，");
		// }
		// if (binay.charAt(7) == '0') {
		// sbuf.append("已设防，");
		// } else {
		// sbuf.append("未设防，");
		// } 
		log.info(this.getDeviceSN() + "状态2：" + sbuf.toString());
		return ret;
	}

	public String parseStatus2(String binay) {
		String ret = "";

		StringBuilder sbuf = new StringBuilder();
		if (binay.charAt(0) == '0') {
			sbuf.append("劫警报警，");
			this.setAlarmType("3"); 
			// 增加到报警入库队列
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (binay.charAt(1) == '0') {

			double x = Double.parseDouble(this.getCoordX() == null ? "0" : this
					.getCoordX());
			double y = Double.parseDouble(this.getCoordY() == null ? "0" : this
					.getCoordY());
			if (x != 0 && y != 0) {
				sbuf.append("超速报警，");
				this.setAlarmType("1"); 

				// 增加到报警入库队列
				AlarmQueue.getInstance().addAlarm(this);
			}
		}
		// if (binay.charAt(2) == '0') {
		// sbuf.append("停车超长报警，");
		// }
		// if (binay.charAt(3) == '0') {
		// sbuf.append("驶出区域报警，");
		// }
		// if (binay.charAt(4) == '0') {
		// sbuf.append("驶入区域报警，");
		// this.setAlarmType("3");
		// }
		// if (binay.charAt(5) == '0') {
		// sbuf.append("看车密码错误报警，");
		// }
		if (binay.charAt(6) == '0') {
			sbuf.append("GPRS已上线，");
			ret += "6:3;";
		} else {
			sbuf.append("GPRS未上线，");
			ret += "6:2;";
		}
		if (binay.charAt(7) == '0') {
			sbuf.append("终端拨号成功，");
		} else {
			sbuf.append("终端拨号未成功，");
		} 
		log.info(this.getDeviceSN() + "状态3：" + sbuf.toString());
		return ret;
	}

	public String parseStatus3(String binay, byte revVerfyCode, byte MainCmdid,
			byte SubId) {
		String ret = "";

		StringBuilder sbuf = new StringBuilder();
		if (binay.charAt(0) == '0') {
			sbuf.append("GPRS已注册，");
			// ret += "6:1;";
		} else {
			sbuf.append("GPRS未注册，");
			// ret += "6:0;";
		}
		if (binay.charAt(1) == '0') {
			sbuf.append("中心不需下发21指令，");

		} else {
			// byte[] repb = this.reply2Terminal(revVerfyCode, MainCmdid,
			// SubId);
			// this.setReplyByte(repb);

			sbuf.append("中心需下发21指令，");
		}
		// if (binay.charAt(2) == '0') {
		// sbuf.append("UDP通讯方式，");
		// } else {
		// sbuf.append("TCP通讯方式，");
		// } 
		log.info(this.getDeviceSN() + "状态4：" + sbuf.toString());
		return ret;
	}

	public String parseStatus4(String binay) {
		StringBuilder sbuf = new StringBuilder();
		// if (binay.charAt(0) == '0') {
		// sbuf.append("手柄没接入，");
		// } else {
		// sbuf.append("手柄已接入，");
		// }
		// if (binay.charAt(1) == '0') {
		// sbuf.append(" LCD显示屏没接入，");
		// } else {
		// sbuf.append(" LCD显示屏接入，");
		// }
		// if (binay.charAt(2) == '0') {
		// sbuf.append("图像采集器没接入，");
		// } else {
		// sbuf.append("图像采集器已接入，");
		// }
		// if (binay.charAt(3) == '0') {
		// sbuf.append("计价器没接入，");
		// } else {
		// sbuf.append("计价器已接入，");
		// }
		// if (binay.charAt(4) == '0') {
		// sbuf.append("语音波号器器没接入，");
		// } else {
		// sbuf.append("语音波号器器已接入，");
		// }
		// if (binay.charAt(5) == '0') {
		// sbuf.append("允许打出，");
		// } else {
		// sbuf.append("禁止打出，");
		// }
		// if (binay.charAt(6) == '0') {
		// sbuf.append("允许打入，");
		// } else {
		// sbuf.append("禁止打入，");
		// }
		// if (binay.charAt(7) == '0') {
		// sbuf.append("允许通话，");
		// } else {
		// sbuf.append("禁止通话，");
		// }
		return sbuf.toString();
	}

	// 回复终端
	private byte[] reply2Terminal(byte revVerfyCode, byte MainCmdid) {
		ByteBuffer buffer = ByteBuffer.allocate(10);

		byte[] ret = new byte[8];
		String head = "2929" + "210005";
		byte[] hb = Tools.fromHexString(head);
		System.arraycopy(hb, 0, ret, 0, 5);

		ret[5] = revVerfyCode;
		ret[6] = MainCmdid;

		byte verfyCode = Tools.checkData(ret);
		buffer.put(ret);
		buffer.put(verfyCode);

		byte end = (byte) 0x0D;
		buffer.put(end);

		return buffer.array();
	}

	// 远程下载
	private byte[] loadData(int packNum, int totalPack, byte[] packByte,
			String simIp) {
		// 0008 0080
		// 003a02da3030f87001e03730f8704ff0000808a80ef035fb80b2012103000a009a42bff47aacd25d82ea0808491c89b2f5e7dff8a0a7dff8509752464946681d03f075ff80b20528fff47bad5a2200214fa80ef008fdd9f800104fa803f0effe4ea14fa80ef077fc4fa80ef00afb4d4ebaf8002006f1e4014fabc0180ef039fb99f800008df8000000a899f80110417099f80210817099f80310c170baf800100a0a0271417106f5d47003f07cff4fa803f079ff0af025f94ff48472002108a80ef0d1fc0f208df8200008afc2207870e28e1009c0b201000a2902d23030b87001e03730b870100000f00f0001000a2902d23030f87001e03730f870291d09a80ef065fd4ff00008e08e001d80b20121030004e0d25d82ea0808491c89b20a009a42f7d300f8078008a94118f0224a70811c08a800f049ff0af069f9dff89c0647f27f010180dff894761e22002107f114000ef088fcd9f8001007f1140003f06efe0fa138000ef0f6fb38000ef089fabaf8002006f58271c0190ef0bafa06f58a7206f5867101200bf063f9380003f00aff0020a072e072d3e4203a00004c590020222c0000640401086c0401080008014054a00020d59300203a00000094a1002025750000d3930020dea10020dfa10020c4040108cc0401088aa100204ff48472002108a80ef03efc0f208df8200008afc22078703020b8704320f870422038710035fc0d
		String cmd = "";
		String head = "2929" + "87";
		String end = "0D";

		String devid = simIp;
		String packPty = Tools.int2Hexstring(packNum, 4)
				+ Tools.int2Hexstring(totalPack, 4);

		String pack = Tools.bytesToHexString(packByte);

		byte contVertyCode = Tools.checkData(Tools
				.fromHexString(packPty + pack));

		int ivc = Tools.byte2Int(new byte[] { contVertyCode });
		String hexVC = Tools.int2Hexstring(ivc, 4);

		String leng = Tools.int2Hexstring(12 + packByte.length, 4);

		cmd = head + leng + devid + packPty + pack + hexVC;

		byte vcode = Tools.checkData(Tools.fromHexString(cmd));

		String hexV = Tools.bytesToHexString(new byte[] { vcode });
		cmd += hexV + end;

		byte[] ret = Tools.fromHexString(cmd);

		return ret;
	}

	// 远程下载结束
	private byte[] finish(String simIp) {

		String cmd = "2929" + "630006" + simIp;

		byte vcode = Tools.checkData(Tools.fromHexString(cmd));
		String hexV = Tools.bytesToHexString(new byte[] { vcode });
		cmd += hexV + "0D";

		byte[] ret = Tools.fromHexString(cmd);

		return ret;
	}

 

}
