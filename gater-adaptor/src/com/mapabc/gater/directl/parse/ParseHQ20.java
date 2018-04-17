package com.mapabc.gater.directl.parse;
 
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.dbutil.DbOperation;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl;
import com.mapabc.gater.directl.parse.service.ParseService; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.geom.CoordCvtAPI;
import com.mapabc.geom.DPoint;

import java.io.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * 山东华强终端协议解析
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author yang lei
 * 
 * 测试数据：2a485132303131353831303333303432332c414826413038313931393232333933353637313133353432363234373030303030363031383026423033303030303030303026463030303023
 * @version 1.0
 * 
 */

public class ParseHQ20 extends ParseBase  implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseHQ20.class);
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
	private static String protocolHead = "*HQ20";
	private static String protocolEnd = "#";
	private static String spProtocolHead = "[HQ20";
	private static String spProtocolEnd = "]";

	public ParseHQ20() {
	}

	public static void main(String[] args) {
		// 解析华强终端发送数据是：*HQ20015806660749,YYA&A1757503640717911702859460000080709&B0300000000&F0000#
		// 2009-07-08 17:57:52,793 - 华强终端15806660749
		// GPS信息：x=117.028594,y=36.407179,speed=0,time=2009-07-08
		// 17:57:50，状态=ACC开;重车;
		ParseHQ20 parseHQ201 = new ParseHQ20();
		String s = "*HQ20015206331749,AD1&A1523143525430511932002360005110909&B0000000000&C00249827&F0003#";// "*HQ20013455033294,BA&A1124463525278011931116160001200709&B0100000000&C0000;35;&F0001#";
		// String lc = parseHQ201.formatSpeed(".2");
		// String hex = Tools.bytesToHexString("00004?=?".getBytes());
		parseHQ201.parseGPRS(Tools.bytesToHexString(s.getBytes()));

		String lcstr = "00033322";
		int i = 0;
		while (i < lcstr.length()) {
			if (lcstr.charAt(i) != '0') {
				break;
			}
			i++;
		}
		lcstr = lcstr.substring(i);
		int ilc = Integer.parseInt(lcstr);
	}

	private String getGpsSN(String content) {
		String gpssn = null;

		return gpssn;
	}

	static String getGPSTag(String p) {
		String tmp = "";
		int tag = Integer.parseInt(p);
		switch (tag) {
		case 0:
			tmp = "西经、南纬、定位";
			break;
		case 1:
			tmp = "西经、南纬、非定位";
			break;
		case 2:
			tmp = "西经、北纬、定位";
			break;
		case 3:
			tmp = "西经、北纬、非定位";
			break;
		case 4:
			tmp = "东经、南纬、定位";
			break;
		case 5:
			tmp = "东经、南纬、非定位";
			break;
		case 6:
			tmp = "东经、北纬、定位";
			break;
		case 7:
			tmp = "东经、北纬、非定位";
			break;
		}
		return tmp;
	}

	public void parseGPRS(String simCard, String content) {

	}

	public void parseSMS(String phnum, String content) {

	}

	// 格式："*HQ20&AhhmmssaaaaaaaaoooooooooFvvffddmmyyabc";
	// *HQ20015810330423,AD1&A0804202239356711354262470000060180&B0300000000&F0000#
	public void parseGPRS(String hexString) {
		try {

			hexString = new String(Tools.fromHexString(hexString), "ISO8859-1");
			log.info("解析华强终端发送数据是：" + hexString);
		} catch (UnsupportedEncodingException ex1) {
			log.info(
					"UnsupportedEncodingException is: " + ex1.getMessage());
		}

		if (hexString.startsWith("*HQ") && hexString.endsWith("#")) {
			// 普通协议
			commonProtocolFunc(hexString);
		} else if (hexString.startsWith("[HQ") && hexString.endsWith("]")) {
			// 特殊协议
			specialProtocolFunc(hexString);
		}
	}

	private void commonProtocolFunc(String hexString) {
		try {
			// *HQ20115866751224,AH&A1104403640721411702869460000140409&B0300000000&F0000#
			int flag = hexString.indexOf(",");
			String head = hexString.substring(0, flag); // eg:*HQ20015810330423
			String deviceId = head.substring(6);
			 

			String backProperties = head.substring(5, 6); // 回复属性
			// 36 40.7306 11702861660021
			int p = hexString.indexOf("&A"); // GPS定位数据标志位；
			int sp = hexString.indexOf("&B"); // 状态及报警编码标志位；
			int lc = hexString.indexOf("&C"); // 里程信息
			int lci = hexString.indexOf("&E"); // X80系列会携带&E里程信息
			int spi = hexString.indexOf("&F"); // 精确的速度

			String funNumAndkey = hexString.substring(flag + 1, p); // 功能编号、关键字

			String tmp = null;
			if (p < 0) {
				return;
			}
			tmp = hexString.substring(p + 2, p + 2 + 34);
			String statusDatas = hexString.substring(sp + 2, sp + 2 + 10); // 状态及报警编码数据;

			String strTime = "20" + tmp.substring(32, 34) + "-"
					+ tmp.substring(30, 32) + "-" + tmp.substring(28, 30) + " ";
			strTime += tmp.substring(0, 2) + ":" + tmp.substring(2, 4) + ":"
					+ tmp.substring(4, 6);
			String x = "";// tmp.substring(14, 14 + 3) + "."+
			// tmp.substring(17, 17 + 6);
			x = Tools.formatXtoDu(tmp.substring(14, 23));
			String y = "";// tmp.substring(6, 6 + 2) + "." + tmp.substring(8,
			// 8 + 6);
			y = Tools.formatYtoDu(tmp.substring(6, 14));
			String f = tmp.substring(23, 23 + 1);
			String gpsTag = getGPSTag(f);
			String strSpeed = tmp.substring(24, 24 + 2);
			if (strSpeed.charAt(0) == '0') {
				strSpeed = strSpeed.charAt(1) + "";
			}
			int spd = Integer.parseInt(strSpeed) * 2;
			String speed = this.formatSpeed(spd + "");
			String dreicet = tmp.substring(26, 28);
			if (dreicet.charAt(0) == '0') {
				dreicet = dreicet.charAt(1) + "";
			}
			int tDirect = Integer.parseInt(dreicet) * 10;

			if ("1".equals(backProperties)) {
				// 需要中心回复
				this
						.setReplyByte(this.restoreInfor(funNumAndkey.substring(0,
								2)).getBytes());
			}
			if (spi != -1) { // &F
				String spstr = hexString.substring(spi + 2, spi + 6);
				int i = 0;
				while (i < spstr.length()) {
					if (spstr.charAt(i) != '0') {
						break;
					}
					i++;
				}
				String spdi = "";
				if (i != spstr.length())
					spdi = spstr.substring(i, spstr.length() - 1) + "."
							+ spstr.charAt(spstr.length() - 1);
				else
					spdi = "0";
				String speeds = this.formatSpeed(spdi + "");
				this.setSpeed(speeds);
			}
			if (lc != -1) {// &C
				String lcstr = hexString.substring(lc + 2, lc + 10);
				int i = 0;
				while (i < lcstr.length()) {
					if (lcstr.charAt(i) != '0') {
						break;
					}
					i++;
				}
				if (i != lcstr.length())
					lcstr = lcstr.substring(i);
				else
					lcstr = "0";
				String distance = this.formatLC(lcstr);
				this.setMileage(distance);
			}
			if (lci != -1) {// &E
				String lcs = hexString.substring(lci + 2, lci + 10);
				int i = 0;
				while (i < lcs.length()) {
					if (lcs.charAt(i) != '0') {
						break;
					}
					i++;
				}
				if (i != lcs.length())
					lcs = lcs.substring(i);
				else
					lcs = "0";
				String distance = this.formatLC(lcs);
				this.setMileage(distance);
			}

			// Date gpsdate = Tools
			// .formatStrToDate(strTime, "yyyy-MM-dd HH:mm:ss");
			Timestamp ts = new Timestamp(new Date().getTime());
			 
			this.setTime(strTime);
			this.setCoordX(x);
			this.setCoordY(y); 
			this.setSpeed(speed);
			this.setDirection(tDirect + "");
			String status = this.getStatusAndAlarm(statusDatas); // 得到机车状态和报警信息；

			if (funNumAndkey.equalsIgnoreCase("AD1")) {// 终端进入GPSRS
				this.setReplyByte(getOdograph().getBytes());// 要求终端回传里程信息
				// this.sentPost(false);
				String onLocate = "*HQ2011AI(A1)#";// 打开定位信息无速度上传
				this.setReplyByte1(onLocate.getBytes());

			} else if (funNumAndkey.equalsIgnoreCase("AH")) {// 链路维持,
				// this.setReplyByte(getOdograph().getBytes());// 要求终端回传里程信息
				// //this.sentPost(false);
				// String onLocate = "*HQ2011AI(A1)#";//打开定位信息无速度上传
				// this.setReplyByte1(onLocate.getBytes());

			} else if (funNumAndkey.startsWith("Y")) {// 终端应答指令

				DBService dbservice = new DBServiceImpl();
				dbservice.updateInstructionsState(this.getDeviceSN(), "0",
						funNumAndkey.substring(1)); // 更新指令状态
				// this.sentPost(true);
			} 
			log.info(
					"华强终端" + this.getDeviceSN() + " GPS信息：x=" + x + ",y=" + y
							+ ",speed=" + this.getSpeed() + ",time=" + strTime
							+ "，状态=" + status);
		} catch (Exception ex) {
			log.info("深圳华强解析终端上报数据异常是：" + ex.getMessage());
			ex.printStackTrace();
		}
	}

	// 里程信息
	public String getOdograph() {
		String tmp = "BH11";
		String cmdStr = makeCommandStr(tmp, true, true);
		return cmdStr;
	}

	/**
	 * 生成控制指令
	 * 
	 * @param cmdKey
	 *            String 原始控制指令
	 * @param save
	 *            boolean 是否保存
	 * @param replay
	 *            boolean 是否需要回应
	 * @return String 加了包头包尾的指令
	 */
	public static String makeCommandStr(String cmdKey, boolean save,
			boolean replay) {

		StringBuffer buffer = new StringBuffer();
		buffer.append(protocolHead);
		buffer.append(save ? '1' : '0');
		buffer.append(replay ? '1' : '0');
		buffer.append(cmdKey);
		buffer.append(protocolEnd);
		return buffer.toString();
	}

	/**
	 * 中心回复。
	 * 
	 * @param simcard
	 *            String
	 * @return String
	 */
	public String restoreInfor(String funNumAndkey) {
		StringBuffer buf = new StringBuffer();
		buf.append("*HQ20");
		buf.append("Y");
		buf.append(funNumAndkey);
		buf.append("#");
		String cmd = buf.toString();// HQ20Util.makeCommandStr(buf.toString(),
									// false, false);
		log.info("普通中心回应华强终端指令：" + cmd);
		return cmd;// this.sent(cmd);
	}

	private void specialProtocolFunc(String hexString) {
		int flag = hexString.indexOf(",");
		String head = hexString.substring(0, flag); // eg:[HQ20015810330423
		String deviceId = head.substring(6);
		String end = hexString.substring(flag + 1);
		String backProperties = head.substring(5, 6); // 回复属性
		int p = hexString.indexOf("&A"); // GPS定位数据标志位；
		int sp = hexString.indexOf("&B"); // 状态及报警编码标志位；
		int lc = hexString.indexOf("&C"); // 里程信息
		 
		this.setDeviceSN(deviceId);
		if (end.startsWith("AO")) {
			DBService dbservice = new DBServiceImpl();
			dbservice.updateInstructionsState(this.getDeviceSN(), "0", Tools
					.bytesToHexString("AO".getBytes())); // 更新区域设置指令状态
			return;
		} else if (end.startsWith("Y")) {// 终端应答指令
			String fkey = end.substring(end.indexOf("]") - 2, end.indexOf("]"));
			DBService dbservice = new DBServiceImpl();
			dbservice.updateInstructionsState(this.getDeviceSN(), "0", fkey); // 更新指令状态

		}
		String funNumAndkey = hexString.substring(flag + 1, p); // 功能编号、关键字

		if ("1".equals(backProperties)) {
			// 需要中心回复
 			this.setReplyByte(this.restoreSPInfor(funNumAndkey.substring(0, 2)).getBytes());
		}
	}

	/**
	 * 中心特殊协议回复。
	 * 
	 * @param simcard
	 *            String
	 * @return String
	 */
	public String restoreSPInfor(String funNumAndkey) {
		StringBuffer buf = new StringBuffer();
		buf.append("[HQ20");
		buf.append("Y");
		buf.append(funNumAndkey);
		buf.append("]");
		String cmd = buf.toString();
		log.info("特殊中心回应华强终端指令：" + cmd);

		return cmd;// this.sent(cmd);
	}

	public void parseGPRS(String phnum, byte[] content) {
	}

	public void parseGPRS(byte[] hexString) {
	}

	/**
	 * 根据状态及报警编码数据得到机车的状态。
	 * 
	 * @param datas
	 *            String
	 * @return String
	 */
	public String getStatusAndAlarm(String datas) throws Exception {
		DbOperation dop = new DbOperation();
		long caseid = 0;

		StringBuffer buffer = new StringBuffer();
		// StringBuffer tempBuffer = new StringBuffer();
		// for (int s = 0; s < datas.length(); s++) {
		// tempBuffer.append("0" + datas.substring(s, s + 1));
		// }
		// 03000 02000
		// datas = tempBuffer.toString();
		byte[] bArrays = datas.getBytes();// Tools.fromHexString(datas);
		// 状态编码：
		int b00 = this.getByteBit(bArrays[0], 0);
		int b01 = this.getByteBit(bArrays[0], 1);
		int b02 = this.getByteBit(bArrays[0], 2);
		int b03 = this.getByteBit(bArrays[0], 3);
		if (b00 == 1) {
			buffer.append("总线故障;");
		}
		if (b01 == 1) {
			buffer.append("GSM模块故障;");
		}
		if (b02 == 1) {
			buffer.append("GPS模块故障;");
		}
		if (b03 == 1) {
			buffer.append("锁车电路故障;");
		}

		int b10 = this.getByteBit(bArrays[1], 0);
		int b11 = this.getByteBit(bArrays[1], 1);
		int b12 = this.getByteBit(bArrays[1], 2);
		if (b10 == 1) {
			buffer.append("ACC开;");
		}
		if (b11 == 1) {
			buffer.append("重车;");
		}
		if (b12 == 1) {
			buffer.append("车门开;");
		}

		int b20 = this.getByteBit(bArrays[2], 0);
		if (b20 == 1) {
			buffer.append("私密状态;");
		}

		int b30 = this.getByteBit(bArrays[3], 0);
		int b31 = this.getByteBit(bArrays[3], 1);
		int b32 = this.getByteBit(bArrays[3], 2);
		int b33 = this.getByteBit(bArrays[3], 3);
		if (b30 == 1) {
			buffer.append("远光灯开;");
		}
		if (b31 == 1) {
			buffer.append("右转灯开;");
		}
		if (b32 == 1) {
			buffer.append("左转灯开;");
		}
		if (b33 == 1) {
			buffer.append("刹车灯开;");
		}

		int b40 = this.getByteBit(bArrays[4], 0);
		int b41 = this.getByteBit(bArrays[4], 1);
		int b42 = this.getByteBit(bArrays[4], 2);
		int b43 = this.getByteBit(bArrays[4], 3);
		if (b40 == 1) {
			buffer.append("倒车灯开or后雾灯开or喇叭鸣;");
		}
		if (b41 == 1) {
			buffer.append("前雾灯开;");
		}
		if (b42 == 1) {
			buffer.append("车门关;");
		}
		if (b43 == 1) {
			buffer.append("近光灯开;");
		}
		// 报警编码：
		int b50 = this.getByteBit(bArrays[5], 0);
		int b51 = this.getByteBit(bArrays[5], 1);
		if (b50 == 1) {
			buffer.append("劫警;");
		}
		if (b51 == 1) {
			buffer.append("盗警;");
		}

		int b60 = this.getByteBit(bArrays[6], 0);
		int b61 = this.getByteBit(bArrays[6], 1);
		int b62 = this.getByteBit(bArrays[6], 2);
		int b63 = this.getByteBit(bArrays[6], 3);
		if (b60 == 1) {
			buffer.append("进范围报警;");
		}
		if (b61 == 1) {
			buffer.append("出范围报警;");
		}
		if (b62 == 1) {
			buffer.append("超速报警;");
		}

		DBService dbservice = new DBServiceImpl();
		if (b62 == 1) {// 超速报警
			this.setAlarmType("1");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (b60 == 1 || b61 == 1) {// 区域报警
			this.setAlarmType("2");
			AlarmQueue.getInstance().addAlarm(this);
			String cancelAlarm = "*HQ2001BC#";
			this.setReplyByte2(cancelAlarm.getBytes());// 发生区域报警后主动取消报警标志位
		}
		if (b50 == 1) {// 主动报警
			this.setAlarmType("3");
			AlarmQueue.getInstance().addAlarm(this);
		}

		if (b63 == 1) {
			buffer.append("偏离路线报警;");
		}

		int b70 = this.getByteBit(bArrays[7], 0);
		int b71 = this.getByteBit(bArrays[7], 1);
		int b72 = this.getByteBit(bArrays[7], 2);
		int b73 = this.getByteBit(bArrays[7], 3);
		if (b70 == 1) {
			buffer.append("非法时段行驶报警;");
		}
		if (b71 == 1) {
			buffer.append("停车休息时间不足报警;");
		}
		if (b72 == 1) {
			buffer.append("越站报警;");
		}
		if (b73 == 1) {
			buffer.append("非法开车门;");
		}

		int b80 = this.getByteBit(bArrays[8], 0);
		int b81 = this.getByteBit(bArrays[8], 1);
		int b82 = this.getByteBit(bArrays[8], 2);
		int b83 = this.getByteBit(bArrays[8], 3);
		if (b80 == 1) {
			buffer.append("设防;");
		}
		if (b81 == 1) {
			buffer.append("剪线报警;");
		}
		if (b82 == 1) {
			buffer.append("电瓶电压低报警;");
		}
		if (b83 == 1) {
			buffer.append("密码错误报警;");
		}
		// Log.getInstance().outHQLog("HQ状态：" + buffer.toString());
		return buffer.toString();
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

	// 换算里程
	private String formatLC(String dis) {
		String ret = "";
		long lc = Tools.byte2Long(dis.getBytes()) * 2;

		double dlc = lc * 1.852 / 3600;
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(0);
		ret = nf.format(dlc).replaceAll("\\,", "");
		return ret;
	}

	private String formatSpeed(String tmpSpeed) {
		String ret = "";
		double speed = 0;
		if (tmpSpeed != null) {
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

 

}
