package com.mapabc.gater.directl.parse;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl;
import com.mapabc.gater.directl.parse.service.ParseService; 

public class ParsePersonLocator extends ParseBase  implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParsePersonLocator.class);
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
 
	public void parseGPRS(String hexString) {

		// #TH100,deviceId,TIME,V,N24.123456,E121.123456,80,16:30:30,31/12/04,1000*

		byte[] cont = Tools.fromHexString(hexString);
		String scont = new String(cont);
		log.info("儿童手机原始数据："+scont);
		String[] split = scont.split(",");
		String deviceId = split[1];
		
		 
		this.setDeviceSN(deviceId);
		 
		String type = split[2];
		String params = "";
		// 请求设置信息返回
		if (type.equalsIgnoreCase("REQUEST")) {
			this.parsePosition(split);

			String track = split[10];
			if (track.equalsIgnoreCase("S")) {
				params += "通道：短信,";
			} else {
				params += "通道：TCP,";
			}

			String repMode = split[11];
			if (repMode.equalsIgnoreCase("TIME")) {
				params += " 回传模式：按时回传,";
			} else if (repMode.equalsIgnoreCase("MOVE")) {
				params += " 回传模式：按距离回传,";
			} else {
				params += "回传模式：未设置, ";
			}

			String tInterval = split[12];
			String dInterval = split[13];
			params += "时间频率：" + tInterval + ",距离频率：" + dInterval + ",";

			String power = split[14];
			params += "缺电上报：" + power + ",";
			String remainingPower = split[15];
			params += "剩余电量：" + remainingPower + ",";
			String smsCenter = split[16];
			params += "短信中心号码：" + smsCenter + ",";
			String ip = split[17];
			String port = split[18];
			params += "IP:" + ip + ",端口：" + port + ",";
			String variablePhone = split[19] + "," + split[20] + ","
					+ split[21] + "," + split[22];
			params += "限拨号码：" + variablePhone;
		} else if (type.equalsIgnoreCase("REPLY")) {
			DBService dbservice = new DBServiceImpl();
			String status = split[3];
			String cmdId = split[4];

			if (status.equalsIgnoreCase("OK")) {

				dbservice.updateInstructionsState(this.getDeviceSN(), "0",
						cmdId);

			} else {
				dbservice.updateInstructionsState(this.getDeviceSN(), "2",
						cmdId);

			}
		} else {
			this.parsePosition(split);
		} 

	}

	public void parsePosition(String[] split) {

		String deviceId = split[1];
		this.setDeviceSN(deviceId);
		String type = split[2];
		String typeDesc = null;
		if (type.equalsIgnoreCase("EMERGENCY")) {
			typeDesc = "紧急回传";
		} else if (type.equalsIgnoreCase("TIME")) {
			typeDesc = "按时间回传";
		} else if (type.equalsIgnoreCase("MOVE")) {
			typeDesc = "按距离回传";
		} else if (type.equalsIgnoreCase("POWER SHORTAGE")) {
			typeDesc = "电量不足回传";
		} else if (type.equalsIgnoreCase("CALL")) {
			typeDesc = "电话回传";
		} else if (type.equalsIgnoreCase("GPS")) {
			typeDesc = "GPS回传";
		}

		String status = split[3];
		String statusDes = null;
		if (status.equalsIgnoreCase("A")) {
			statusDes = "定位成功";
		} else {
			statusDes = "定位失败";
		}
		String y = split[4].substring(1);
		this.setCoordY(y);
		String x = split[5].substring(1);
		this.setCoordX(x);
		String velocity = split[6];
		this.setSpeed(velocity);
		String time = split[7].replaceAll(":", "");
		String date = split[8].replaceAll("/", "");
		String datetime = this.conformtime(time, date);
		this.setTime(datetime);
//		Date gpsdate = formatStrToDate(datetime, "yyyy-MM-dd HH:mm:ss");
//		Timestamp ts = new Timestamp(gpsdate.getTime());
//		this.setTimeStamp(ts);

		String areaFlag = split[9];
		String afstr = "";
		for (int i = 0; i < areaFlag.length(); i++) {
			char c = areaFlag.charAt(i);
			if (c == '1') {
				afstr += "进入" + (i + 1) + "区域";
			} else if (c == '0'){
				afstr += "出" + (i + 1) + "区域";
			}
		}
		log.info("儿童手机定位信息：DEVICE_SN="+this.getDeviceSN()+",X="+x+",Y="+y+",速度="+velocity+"时间："+datetime);
	}

	private Date formatStrToDate(String date, String format) {
		Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			d = sdf.parse(date);
		} catch (ParseException ex) {
		}
		return d;
	}

	 
	
	private static String conformtime(String time, String date) {
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
			 
			Date newDate = new Date(car.getTimeInMillis());
			return simpleDate.format(newDate);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return null;
	}
	
	public static void main(String[] args){
		ParsePersonLocator plp = new ParsePersonLocator();
							
		plp.parseGPRS("2354483130302c31333639383630373437342c4750532c562c3030302e3030303030302c303030302e3030303030302c302c30303a30303a30302c30302f30302f30302c585858582a");
	}

	 
}
