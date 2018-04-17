package com.mapabc.gater.directl.parse.sege;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Image;

import com.mapabc.gater.directl.Base64;
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.encode.Controller;
import com.mapabc.gater.directl.encode.sege.SegeUtil;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.ParseHQ20;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.directl.pic.PictureKey;   
import com.mapabc.gater.lbsgateway.GBLTerminalList;
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.service.CommonGatewayServiceImpl;
import com.mapabc.gater.lbsgateway.service.ICommonGatewayService;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.RenderingHints;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.IIOImage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.sun.image.codec.jpeg.JPEGImageDecoder;
import java.awt.Graphics;
import java.awt.image.ImageObserver;
import java.text.NumberFormat;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class ParseSEGLPG52 extends ParseBase implements ParseService {

	private String sIndex;
	private TTermStatusRecord statusRec = new TTermStatusRecord();
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseSEGLPG52.class);
	
	public ParseSEGLPG52() {

	}

	public void parseGPRS(String hexString) {
		log.info("赛格设备原始数据：" + hexString);
		 
		String deviceId = null;
		try { 
  			ICommonGatewayService service = new CommonGatewayServiceImpl();
			deviceId = service.getDeviceIdByTcpAddress(this.getSocket());
			log.info("赛格设备 从缓存获取  （TCP客户端地址，序列号）《=》（" + this.getSocket() + ","
							+ deviceId + "）");

			if (deviceId != null) {
				this.setDeviceSN(deviceId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		String typeCode = GBLTerminalList.getInstance().getTermTypeCode(this.getDeviceSN());
//		if (typeCode != null && typeCode.equals("GP-HQ-GPRS")){
//			ParseHQ20 hq = new ParseHQ20();
//  			hq.parseGPRS(hexString);
// 			this.resetObject(hq);
// 			return;
//		}
		if (hexString.equals("5bf05d")) {
			this.setReplyByte(Tools.fromHexString("5BFF5D"));
			log.info(this.getDeviceSN() + " 回复链路维持指令：5BFF5D");
			return;
		}
		String reverEscape = hexString.substring(2, hexString.length() - 2);
		String tmpHex = SegeUtil.reverseEscape(reverEscape);

		hexString = "5b" + tmpHex + "5d";
		log.info(this.getDeviceSN() + "反转义后的数据：" + hexString);
		// deviceId = "122343";
		// this.setDeviceSN(deviceId);

		byte[] content = Tools.fromHexString(hexString);
		byte cmd = content[1];
		byte[] index = new byte[10];
		for (int i = 0; i < 10; i++) {
			index[i] = content[i + 2];
		}
		sIndex = new String(index);

		int contentLeng = content.length;
		if (content[contentLeng - 1] != (byte) 0x5d) {
			log.info("赛格接收的指令不完整");
			return;
		} 

		byte[] lenth = new byte[1];
		lenth[0] = content[12];
		int length = Tools.byte2Int(lenth);

		byte[] bmobile;
		String mobile = "";
		byte[] data = new byte[length]; // 存储报文数据
		for (int i = 0; i < length; i++) {
			data[i] = content[i + 13];
		}
		switch (cmd) {
		case (byte) 0x80:
			bmobile = new byte[length];
			for (int i = 13; i < 13 + length; i++) {
				bmobile[i - 13] = content[i];
			}
			mobile = new String(bmobile);
			this.setDeviceSN(mobile);

			String login = AnserLogin(mobile, sIndex);
			this.setReplyByte(Tools.fromHexString(login));
			log.info(this.getDeviceSN() + " 登录回应：" + login);
			break;
		case (byte) 0x01:
			bmobile = new byte[length];
			for (int i = 13; i < 13 + length; i++) {
				bmobile[i - 13] = content[i];
			}
			mobile = new String(bmobile);
			this.setDeviceSN(mobile);

			// String logout = AnserLogout(mobile, sIndex);
			// this.setReplyByte(Tools.fromHexString(logout));
			// log.info(this.getDeviceSN() + " 退录回应：" + logout);
			break;
		case (byte) 0xF0:
			bmobile = new byte[length];
			for (int i = 13; i < 13 + length; i++) {
				bmobile[i - 13] = content[i];
			}
			mobile = new String(bmobile);
			this.setDeviceSN(mobile);
			String selfCheck = AnserSelfCheck(mobile, sIndex);
			this.setReplyByte(Tools.fromHexString(selfCheck));
			log.info(
					this.getDeviceSN() + " 链路检查应答：" + selfCheck);
			break;
		case (byte) 0x70: //
			log.info(this.getDeviceSN() + " 报警信息。");
			String rep = this.replyAlarm(this.getDeviceSN(), sIndex);
			this.setReplyByte1(Tools.fromHexString(rep));
			parsePosition(data, sIndex);
			break;
		case (byte) 0x90: // 查车
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":10", "0");
			log.info(this.getDeviceSN() + " 点名。");
			parsePosition(data, sIndex);
			break;
		case (byte) 0xa0:
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":20", "0");
			log.info(this.getDeviceSN() + " 断油电控制。");
			parsePosition(data, sIndex);
			break;
		case (byte) 0xa1:
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":21", "0");
			log.info(this.getDeviceSN() + " 恢复油电控制。");
			parsePosition(data, sIndex);
			break;
		case (byte) 0x93: // 解析定时上报信息
			parsePosition(data, sIndex);

			break;
		case (byte) 0xc7:
			log.info(this.getDeviceSN() + " 设置通讯地址应答。");
			break;
		case (byte) 0xe0:
			String sms = null;
			try {
				sms = new String(data, "UTF-16");
				log.info("接收到终端的信息指令：" + sms);
			} catch (UnsupportedEncodingException ex) {
				ex.printStackTrace();
			}
 
			break;
		case (byte) 0x81:
			bmobile = new byte[length];
			for (int i = 13; i < 13 + length; i++) {
				bmobile[i - 13] = content[i];
			}
			mobile = new String(bmobile);
			this.setDeviceSN(mobile);
			this.setCoordX("0");
			this.setCoordY("0"); 
 
			break;
		case (byte) 0x72:
			log.info(this.getDeviceSN() + " 监听设置成功。");
			break;
		case (byte) 0xe8: 
			break;
		case (byte) 0xb1:
			String cont = new String(data);
			String speed = cont.substring(9, 12);
			int sped = Integer.parseInt(speed);
			log.info(this.getPhnum() + "设置最大限制速度值为：" + sped);
 
			break;
		case (byte) 0xdc: // 终端拍照解析
			String contents = new String(data);
			break;
		case (byte) 0xdf: // 图片数据包内容

			break;
		case (byte) 0xb2:
			break;

		default:

			break;
		}
	}
 
 

	private void responseTask(String index, String task, String funId,
			int total, int curNo) {
		String hex = null;

		hex = "5b59" + Tools.bytesToHexString(index.getBytes());
		String stsk = funId + Tools.int2Hexstring(total, 4)
				+ Tools.int2Hexstring(curNo, 4)
				+ Tools.int2Hexstring(task.length() / 2, 4) + task;

		int len = stsk.length() / 2 + 1;

		hex += Tools.int2Hexstring(len, 4) + "05" + stsk + "5d";

		log.info(this.getDeviceSN() + " 回应给设备的任务单消息为：" + hex);

		this.setReplyByte(Tools.fromHexString(hex));

	}

	public String AnserLogin(String mobile, String index) {
		String ret = "";
		int length = 15 + mobile.length();
		ByteBuffer buf = ByteBuffer.allocate(length);
		buf.put((byte) 0x5b);
		buf.put((byte) 0x0);
		buf.put(index.getBytes());
		buf.put((byte) (mobile.length() + 1));
		buf.put((byte) 0x33);
		buf.put(mobile.getBytes());
		buf.put((byte) 0x5d);
		ret = Tools.bytesToHexString(buf.array());
		return ret;
	}

	public String AnserLogout(String mobile, String index) {
		String ret = "";
		int length = 14 + mobile.length();
		ByteBuffer buf = ByteBuffer.allocate(length);
		buf.put((byte) 0x5B);
		buf.put((byte) 0x01);
		buf.put(index.getBytes());
		buf.put((byte) mobile.length());
		buf.put(mobile.getBytes());
		buf.put((byte) 0x5d);
		ret = Tools.bytesToHexString(buf.array());
		return ret;
	}

	public String AnserSelfCheck(String mobile, String index) {
		String ret = "";
		int length = 15 + mobile.length();
		ByteBuffer buf = ByteBuffer.allocate(length);
		buf.put((byte) 0x5b);
		buf.put((byte) 0xFF);
		buf.put(index.getBytes());
		buf.put((byte) (mobile.length() + 1));
		buf.put((byte) 0x33);
		buf.put(mobile.getBytes());
		buf.put((byte) 0x5d);
		ret = Tools.bytesToHexString(buf.array());
		return ret;
	}

	// 接收图像数据包异或校验值
	private byte checkData(byte[] b) {
		byte result = b[0];
		int i = 1;
		while (i < b.length) {
			result ^= b[i];
			i++;
		}
		return result;
	}

	// 下发短信应答
	private String mtAnswer(String index, String mobile) {
		String ret = "";
		int size = 4 + index.length() + mobile.length();
		ByteBuffer buf = ByteBuffer.allocate(size);
		buf.put((byte) 0x5b);
		buf.put((byte) 0xe8);
		buf.put(index.getBytes());
		buf.put((byte) mobile.length());
		buf.put(mobile.getBytes());
		buf.put((byte) 0x5d);
		ret = Tools.bytesToHexString(buf.array());
		return ret;

	}

	private String convertToHex(String num, int n) {
		String temp = "";
		int i = Integer.parseInt(num);
		String hex = Integer.toHexString(i);
		if (hex.length() > n) {
			int off = 0;
			while (off < n) {
				temp = temp + "F";
				off++;
			}
			return temp;
		} else if (hex.length() < n) {
			while (hex.length() < n) {
				hex = "0" + hex;
			}
			temp = hex;
		} else {
			temp = hex;
		}
		return temp;
	}

	// 解析位置信息
	public void parsePosition(byte[] data, String indexSn) {
		String recive = new String(data);
		String head = recive.substring(1, 4);
		String stime = recive.substring(4, 10); // HH:MM:SS

		String sflag = recive.substring(10, 11); // 定位状态
		if (sflag.equals("A")) {
			this.setLocateStatus("1");
			statusRec.setLocate("1");
		} else {
			this.setLocateStatus("0");
			statusRec.setLocate("0");
		}
		this.setStatusRecord(statusRec);
		
		String sLat = recive.substring(11, 20); // 纬度
		this.setCoordY(formatY(sLat));
		String yFlag = recive.substring(20, 21); // 纬度标识
		String sLong = recive.substring(21, 31); // 经度
		this.setCoordX(formatX(sLong));
		String xFlag = recive.substring(31, 32); // 经度标识
		String sSpeed = recive.substring(32, 37); // 速度
		this.setSpeed(formatSpeed(sSpeed));
		String sDrect = recive.substring(37, 39); // 方向
		this.setDirection(formatDirect(sDrect));

		String date = recive.substring(39, 45);
		String sDate = Tools.conformtime(stime, date);
		this.setTime(sDate);
		Date gpsdate = Tools.formatStrToDate(sDate, "yyyy-MM-dd HH:mm:dd");
		Timestamp ts = new Timestamp(gpsdate.getTime());
 

 
		String sState = recive.substring(45, 53); // 状态
		String[] alarmAndState = MakeState(sState, indexSn);
		log.info(
				this.getDeviceSN() + " gps信息：x=" + this.getCoordX() + ",y="
						+ this.getCoordY() + ",s=" + this.getSpeed()
						+ ",drect=" + this.getDirection() + ",t="
						+ this.getTime());
		log.info(
				this.getDeviceSN() + " 报警及状态信息：" + alarmAndState[0] + ","
						+ alarmAndState[1]);
		int index = recive.indexOf("$");
		double height = 0.0f;
		if (index != -1) {
			height = Double.parseDouble(recive.substring(index + 1, index + 8)); // 高程，海拔,小数点后1位
			this.setAltitude(String.valueOf(height));
		}

	}

	public String replyAlarm(String mobile, String index) {
		String ret = "";
		int length = 14 + mobile.length();
		ByteBuffer buf = ByteBuffer.allocate(length);
		buf.put((byte) 0x5b);
		buf.put((byte) 0x70);
		buf.put(index.getBytes());
		buf.put((byte) (mobile.length()));
		buf.put(mobile.getBytes());
		buf.put((byte) 0x5d);
		ret = Tools.bytesToHexString(buf.array());
		log.info("车台报警回应信息:" + ret);
		return ret;
	}

	public void parseGPRS(byte[] content) {
	}

	private String formatX(String sX) {
		String ret = "";
		String dd = sX.substring(0, 3);
		double d = 0;
		try {
			d = Double.parseDouble(dd);
		} catch (java.lang.NumberFormatException ex) {
		}
		String mm = sX.substring(3, 10);
		double m = 0;
		try {
			m = Double.parseDouble(mm);
		} catch (java.lang.NumberFormatException ex) {
			ex.printStackTrace();
		}
		double x = d + m / 60;
		// ret = "" + x;
		// if (ret.length() > 8) {
		// ret = ret.substring(0, 8);
		// }
		NumberFormat nformat = NumberFormat.getNumberInstance();
		nformat.setMaximumFractionDigits(6);
		nformat.setMinimumFractionDigits(6);
		ret = nformat.format(x).replaceAll("\\,", "");
		;

		return ret;
	}

	private String formatY(String sX) {
		String ret = "";
		String dd = sX.substring(0, 2);
		double d = 0;
		try {
			d = Double.parseDouble(dd);
		} catch (java.lang.NumberFormatException ex) {
		}
		String mm = sX.substring(2, 9);
		double m = 0;
		try {
			m = Double.parseDouble(mm);
		} catch (java.lang.NumberFormatException ex) {
			ex.printStackTrace();
		}
		double y = d + m / 60;
		// ret = "" + y;
		// if (ret.length() > 7) {
		// ret = ret.substring(0, 7);
		// }
		NumberFormat nformat = NumberFormat.getNumberInstance();
		nformat.setMaximumFractionDigits(6);
		nformat.setMinimumFractionDigits(6);
		ret = nformat.format(y).replaceAll("\\,", "");
		;

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
		;

		return ret;
	}

	private String formatDirect(String direct) {
		String ret = "";
		int iDirect = 0;
		if (direct != null) {
			try {
				iDirect = Integer.parseInt(direct);
			} catch (java.lang.NumberFormatException ex) {
				ex.printStackTrace();
			}
		}
		iDirect = iDirect * 10;
		ret = "" + iDirect;
		return ret;
	}

	private double convertToDouble(String data) {
		double result = 0.0;
		try {
			result = Double.parseDouble(data);
		} catch (java.lang.NumberFormatException ex) {
			ex.printStackTrace();
		}

		return result;
	}

	private String[] MakeState(String state, String index) {
		String[] ret = new String[2];
		String alarm = "";
		String states = "";
		byte[] bState = state.getBytes();
		byte state1 = bState[0];
		if ((state1 & 0x01) == 1) {
			alarm = alarm + "申请医疗服务；";
		}
		if ((state1 & 0x02) == 2) {
			alarm = alarm + "申请故障服务；";
		}
		if ((state1 & 0x04) == 4) {
			alarm = alarm + "电屏欠压；";
		}
		if ((state1 & 0x08) == 8) {
			alarm = alarm + "主电被切断；";
			this.setAlarmType("5");
			AlarmQueue.getInstance().addAlarm(this);
		}

		byte state2 = bState[1];
		if ((state2 & 0x01) == 1) {
			alarm = alarm + "劫警；";
			String ack = this.replyAlarm(this.getDeviceSN(), index);
			this.setReplyByte(Tools.fromHexString(ack));

			this.setAlarmType("3");
			AlarmQueue.getInstance().addAlarm(this);

		}
		if ((state2 & 0x02) == 2) {
			alarm = alarm + "盗警；";
		}
		if ((state2 & 0x04) == 4) {
			alarm = alarm + "申请信息服务；";
		}
		if ((state2 & 0x08) == 8) {
			alarm = alarm + "遥控报警；";
		}

		byte state3 = bState[2];
		if ((state3 & 0x01) == 1) {
			alarm = alarm + "手柄故障；";
		}
		if ((state3 & 0x02) == 2) {
			alarm = alarm + "GPS定位时间过长；";
		}
		if ((state3 & 0x04) == 4) {
			alarm = alarm + "二重密码锁报警；";
		}
		if ((state3 & 0x08) == 8) {
			alarm = alarm + "碰撞报警；";
		}

		byte state4 = bState[3];
		if ((state4 & 0x01) == 1) {
			alarm = alarm + "越界报警；";
			this.setAlarmType("2");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if ((state4 & 0x02) == 2) {
			alarm = alarm + "超速报警；";
			this.setAlarmType("1");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if ((state4 & 0x04) == 4) {
			alarm = alarm + "遥控器电池电压过低；";
		}
		if ((state4 & 0x08) == 8) {
			alarm = alarm + "GPS接收机没有输出；";
		}

		byte state5 = bState[4];
		if ((state5 & 0x01) == 1) {
			alarm = alarm + "托吊报警；";
		}
		if ((state5 & 0x02) == 2) {
			alarm = alarm + "震动报警；";
		}
		if ((state5 & 0x04) == 4) {
			alarm = alarm + "后葙车门开启；";
		}
		if ((state5 & 0x08) == 8) {
			alarm = alarm + "遥控器故障；";
		}

		byte state6 = bState[5];
		if ((state6 & 0x01) == 1) {
			states = states + "车门没上锁；";
		} else {
			states = states + "车门已上锁；";
		}
		if ((state6 & 0x02) == 2) {
			states = states + "车辆设防；";
		}
		if ((state6 & 0x04) == 4) {
			states = states + "车辆熄火；";
		} else {
			states = states + "车辆发动；";
		}
		if ((state6 & 0x08) == 8) {
			alarm = alarm + "禁止时间行驶报警；";
		}
		ret[0] = alarm;
		ret[1] = states;
		return ret;
	}

	public String multiLocator(String t, String count) {

		String ret = "";
		String time = "";
		if (t == null) {
			log.info("定位时间间隔为空");
			return null;
		}
		int tt = Integer.parseInt(t);
		if (tt > 255) {
			time = "FF";
		} else {
			time = Integer.toHexString(tt).toUpperCase();
			if (time.length() < 2) {
				time = "0" + time;
			} else {
				time = time.substring(time.length() - 2);
			}
			time = time.toUpperCase();
		}
		String cmd = "(CMD,002,FF," + time + ")";
		ByteBuffer buf = ByteBuffer.allocate(29);
		buf.put((byte) 0x5B);
		buf.put((byte) 0x11);
		buf.put("0100100001".getBytes());
		buf.put((byte) 0x0F);
		buf.put(cmd.getBytes());
		buf.put((byte) 0x5D);
		try {
			ret = new String(buf.array(), "ISO8859-1");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		return ret;
	}

	private String formatFen(String fen) {
		String tmp = "";
		tmp = "" + Long.parseLong(fen, 16);
		while (tmp.length() < 7) {
			tmp = tmp + "0";
		}
		return tmp;
	}

	private String formatDateTime(String str, int max) {
		String temp = "";
		int dtime = Integer.parseInt(str, 16);
		String st = Integer.toString(dtime);
		if (st.length() < max) {
			while (st.length() < max) {
				st = "0" + st;
			}
			temp = st;
		} else {
			temp = st.substring(0, max);
		}
		return temp;

	}

	// 申请上传图像应答
	private String uploadAnswer(String index, String picNo, String photoId,
			String packSize) {
		String ret = "";
		String content = "(PIC,03," + picNo + "," + photoId + "," + packSize
				+ ")";
		int size = 14 + content.length();
		ByteBuffer buf = ByteBuffer.allocate(size);
		buf.put((byte) 0x5b);
		buf.put((byte) 0xdc);
		buf.put(index.getBytes());
		buf.put((byte) content.length());
		buf.put(content.getBytes());
		buf.put((byte) 0x5d);
		try {
			ret = new String(buf.array(), "ISO8859-1");
		} catch (UnsupportedEncodingException ex) {
			ex.printStackTrace();
		}
		log.info("申请上传图片应答：" + ret);
		return ret;
	}

	public static void main(String[] args) {
		ParseSEGLPG52 pg = new ParseSEGLPG52();
		pg
				.parseGPRS("5bf05d5b93303030303030303030304c2849545630333138353341333131322e313031394e31323132352e37343136453030302e3430333137313031313030303030303030264b3130303231323241422c4b353030303030303030295d");

	}

 

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

	
}
