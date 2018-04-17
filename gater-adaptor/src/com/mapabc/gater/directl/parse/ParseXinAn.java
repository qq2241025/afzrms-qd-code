/**
 * 
 */
package com.mapabc.gater.directl.parse;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
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
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.gater.directl.parse.service.ParseService; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
 
/**
 * @author shiguang.zhou
 * 
 */
public class ParseXinAn extends ParseBase  implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseXinAn.class);
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
 
	public void parseGPRS(String hexString) {
		// TODO Auto-generated method stub
		if (hexString == null || hexString.trim().length() <= 0) {
			return;
		}
		log.info("接收星安终端原始数据:" + hexString);
		String head = hexString.substring(0, 4);
		String cmd = hexString.substring(4, 6);
		String length = hexString.substring(6, 10);
		String deviceId = hexString.substring(10, 18);
		String subCmd = hexString.substring(18, 20);
		String check = hexString.substring(hexString.length() - 4, hexString
				.length() - 2);
		String end = hexString.substring(hexString.length() - 2, hexString
				.length());
		log.info("head:" + head);
		log.info("cmd:" + cmd);
		log.info("length:" + length);
		log.info("deviceId:" + deviceId);
		log.info("subCmd:" + subCmd);
		log.info("check:" + check);
		log.info("end:" + end);
 
		this.setDeviceSN(deviceId);
	 
		 

		if (cmd.equalsIgnoreCase("b1")) {// 握手
			StringBuffer bs = new StringBuffer();
			bs.append(head);
			bs.append("21");
			bs.append("0005");
			bs.append(check);
			bs.append(cmd);
			bs.append(subCmd);
			bs.append(Tools.checkData(Tools.fromHexString(bs.toString())));
			bs.append(end);
			this.setReplyByte1(bs.toString().getBytes());
			log.info("回应握手信号：" + bs.toString());
		} else if (cmd.equalsIgnoreCase("80")) {// position
			String position = hexString.substring(18, hexString.length() - 4);
			log.info("获得位置信息：：" + position);

			parsePosition(position, 0); 
			 

			StringBuffer bs = new StringBuffer();
			bs.append(head);
			bs.append("21");
			bs.append("0005");
			bs.append(check);
			bs.append(cmd);
			bs.append(subCmd);
			bs.append(Tools.checkData(Tools.fromHexString(bs.toString())));
			bs.append(end);
			this.setReplyByte1(bs.toString().getBytes());
			log.info(
					this.getDeviceSN() + "位置数据：x=" + this.getCoordX() + ",y="
							+ this.getCoordY() + ",s=" + this.getSpeed()
							+ ",dirction=" + this.getDirection() + ",distance="
							+ this.getMileage() + ",time=" + this.getTime());
		} else if (cmd.equalsIgnoreCase("81")) {
			log.info("终端回复：点名位置");
			String position = hexString.substring(18, hexString.length() - 4);

			parsePosition(position, 0);
			 

			log.info(
					this.getDeviceSN() + "点名位置数据：x=" + this.getCoordX() + ",y="
							+ this.getCoordY() + ",s=" + this.getSpeed()
							+ ",dirction=" + this.getDirection() + ",distance="
							+ this.getMileage() + ",time=" + this.getTime());
		} 
		else if(cmd.equalsIgnoreCase("82")){
			log.info("终端上报报警信息：" + hexString);
			String position = hexString.substring(18, hexString.length() - 4);
			parsePosition(position, 1);
			parseSpecialAlarm(hexString);
		}else if (cmd.equalsIgnoreCase("83")) {
			log.info("终端回复：查看状态");
			String position = hexString.substring(18, hexString.length() - 4);
			parsePosition(position, 0);
		} else if (cmd.equalsIgnoreCase("84")) {
			log.info("终端回复：查看车台版本信息");
			String versionInfo = hexString
					.substring(18, hexString.length() - 4);
			log.info("车台版本信息：" + versionInfo);
		} else if (cmd.equalsIgnoreCase("85")) {
			String position = hexString.substring(18, hexString.length() - 4);
			parsePosition(position, 0);
			
			try {
				String date = Tools.formatDate2Str(new Date(),
						"yyyy-MM-dd HH:mm:ss");
				this.setTime(date); 
//				ReplyResponseUtil.addReply(deviceId + ":" + this.getCmdId(), "1");
			} catch (Exception e) {
				Log.getInstance().errorLog("ReplyResponseUtil.addReply failed", e);
			}
//			log.info(
//					this.getDeviceSN() + "设置指令成功，信令ID=" + this.getCmdId());
		}
	}
 

	private void parsePosition(String position, int type) {
		String date = position.substring(0, 2) + "-" + position.substring(2, 4)
				+ "-" + position.substring(4, 6);
		String time = position.substring(6, 8) + ":"
				+ position.substring(8, 10) + ":" + position.substring(10, 12);
		String gpstime = "20" + date + " " + time;
		this.setTime(gpstime);

		// 03909301 11712342
		String dy = position.substring(12, 15);// 纬度度
		String fy = position.substring(15, 20);// 纬度分
		double y = 0;
		try {
			y = Integer.parseInt(dy) + Double.parseDouble(fy) / 1000 / 60.0d;// 纬度
		} catch (NumberFormatException e) {
			y = 0;
		}
		String coordY = Tools.getNumberFormatString(y, 6, 6);
		this.setCoordY(coordY);
		this.riginalY = coordY;

		String dx = position.substring(20, 23);// 经度度
		String fx = position.substring(23, 28);// 经度分
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
			speed = Integer.parseInt(position.substring(28, 32)) + "";// 速度
		} catch (NumberFormatException e) {
			speed = "0";
		}
		this.setSpeed(speed);

		String direction = "";
		try {
			direction = Integer.parseInt(position.substring(32, 36)) + "";// 方向
		} catch (NumberFormatException e) {
			direction = "0";
		}
		this.setDirection(direction);

		String status = position.substring(38, 40);// 定位，天线，电源状态
		int status_int = Tools.byte2Int(Tools.fromHexString(status));
		String binayStr = Integer.toBinaryString(status_int);
		binayStr = buZero(binayStr, 8);
		String resSt = parseStatus(binayStr);

		if (type == 0) {// 一般位置信息有如下内容
			String hexLC = position.substring(40, 44);
			String distance = Tools.byte2Long(Tools.fromHexString(hexLC)) + "";// 里程,单位米

			this.setMileage(distance); 
			String st1 = position.substring(44, 46);
			int ist1 = Tools.byte2Int(Tools.fromHexString(st1));
			st1 = this.buZero(Integer.toBinaryString(ist1), 8);
			String resSt1 = this.parseStatus1(st1);

			String st2 = position.substring(46, 48);
			int ist2 = Tools.byte2Int(Tools.fromHexString(st2));
			st2 = this.buZero(Integer.toBinaryString(ist2), 8);
			String resSt2 = this.parseStatus2(st2);

			String st3 = position.substring(48, 50);
			int ist3 = Tools.byte2Int(Tools.fromHexString(st3));
			st3 = this.buZero(Integer.toBinaryString(ist3), 8);
			String resSt3 = this.parseStatus3(st3);

			String st4 = position.substring(50, 52);
			int ist4 = Tools.byte2Int(Tools.fromHexString(st4));
			st4 = this.buZero(Integer.toBinaryString(ist4), 8);
			String resSt4 = this.parseStatus4(st4);

			String locTime = Tools.byte2Int(Tools.fromHexString(position
					.substring(52, 56)))
					+ "";// 定位发送时间
			String stopCartime = Tools.byte2Int(Tools.fromHexString(position
					.substring(56, 58)))
					+ "";// 停车设置时间
			String overSpeedTime = Tools.byte2Int(Tools.fromHexString(position
					.substring(58, 60)))
					+ "";// 超速设置时间
			String areaCount = Tools.byte2Int(Tools.fromHexString(position
					.substring(60, 62)))
					+ "";// 区域个数
			String repCmdId = position.substring(66, 68);// 主命令ID
//			this.setCmdId(repCmdId);
		}
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
			AlarmQueue.getInstance().add(this);
		} else if (status.charAt(3) == '0' && status.charAt(4) == '1') {
			sbuf.append("主电源过高或过低，");
			ret += "3:1;";
		} 
		log.info(
				this.getDeviceSN() + "状态1：" + sbuf.toString());
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
		if (binay.charAt(5) == '0') {
			sbuf.append("油路断开，");
			ret += "5:0;";
		} else {
			sbuf.append("油路正常，");
			ret += "5:1;";
		} 
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
			AlarmQueue.getInstance().add(this);
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
				AlarmQueue.getInstance().add(this);
			}
		}
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

	public String parseStatus3(String binay) {
		String ret = "";

		StringBuilder sbuf = new StringBuilder();
		if (binay.charAt(0) == '0') {
			sbuf.append("GPRS已注册，");
		} else {
			sbuf.append("GPRS未注册，");
		}
		if (binay.charAt(1) == '0') {
			sbuf.append("中心不需下发21指令，");
		} else {
			sbuf.append("中心需下发21指令，");
		}
 
		log.info(this.getDeviceSN() + "状态4：" + sbuf.toString());
		return ret;
	}

	public String parseStatus4(String binay) {
		StringBuilder sbuf = new StringBuilder();
		return sbuf.toString();
	}
	
	private void parseSpecialAlarm(String hexString) {
		String s = "";
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
		log.info(this.getDeviceSN() + "报警信息：" + s);

	}

	 
}
