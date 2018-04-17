package com.mapabc.gater.directl.parse.kaiyan;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.constant.AlarmType;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.gater.directl.encode.kaiyan.KaiYanUtil;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.directl.pic.PicCache;
import com.mapabc.gater.directl.pic.Picture;
import com.mapabc.gater.lbsgateway.GBLTerminalList; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.lbsgateway.service.CommonGatewayServiceImpl;
import com.mapabc.gater.lbsgateway.service.ICommonGatewayService;
import com.mapabc.gater.util.OverLoadUtil;

public class ParseKaiYan extends ParseBase implements ParseService{
	
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseKaiYan.class);
	
	
	TTermStatusRecord termStatus = new TTermStatusRecord();
	String hexString;
	private static String isOverLoad = null;// 是否有负载

	public ParseKaiYan() {

	}

	public void parseGPRS(String hexString) {
		// 头 ID 地址 功能码 子 功能码 数据长度 GPS数据包 crc 尾
		// FE A39DB4C5 03 01 00 1F
		// D5D1A40839523722593141110C0C0C830000010500058000001F F4 FF
		log.info("开研上传数据--------------->" + hexString);
		this.hexString = hexString;
		int headIndex = hexString.indexOf("fe");
		int endIndex = hexString.indexOf("ff");

		if (headIndex == -1 && endIndex == -1) {
			log.info("开研上传信息格式错误，包头，包尾不完整！" + hexString);
			return;
		}

		String ID = hexString.substring(2, 10);
		String realID = this.parseID(ID);
		this.setDeviceSN(realID);

		// System.out.println("开研终端上传数据。设备ID为："+realID);

		log.info("开研终端上传数据。设备ID为：" + realID);

		TTerminal term = null;

		term = GBLTerminalList.getInstance().getTerminaInfo(realID);//
		// 从内存获取终端SIMCARD
		if (term == null) {
			log.info("系统中没有适配到指定的终端：device_id=" + realID);
			return;
		}
		//this.setObjId(term.getObjId());
		//this.setObjType(term.getObjType());
		this.setPhnum(term.getSimcard());

		String address = hexString.substring(10, 12);

		String featureCodes = hexString.substring(12, 14);

		String childFeatureDatalen = hexString.substring(14, 16);
		String datalen = "";
		String data = "";
		if (Integer.parseInt(address) % 2 == 0) {

			datalen = hexString.substring(16, 20);

			data = hexString.substring(20, hexString.length() - 4);
		} else {

			datalen = hexString.substring(16, 18);

			data = hexString.substring(18, hexString.length() - 4);
		}

		String CRC = hexString.substring(hexString.length() - 4, hexString
				.length() - 2);
		// 控制中心1----------------------------------------------------------------------0x03（使用UDP发送）
		if (address.equals("02")) {

			this.parseControlCenter1(featureCodes, data);

		}

		// 控制中心1----------------------------------------------------------------------0x03（使用UDP发送）
		if (address.equals("03")) {

			this.parseControlCenter1(featureCodes, data);

		}
		// 控制中心2----------------------------------------------------------------------0x05（使用TCP发送）
		if (address.equals("05")) {
			// 握手信息
			if (address.equals("05")) {

				int shakeHandsTime = Integer.parseInt(featureCodes, 16) * 10;

				Log.getInstance()
						.outLog("开研终端，握手的时间为：" + shakeHandsTime + "/秒");

				// System.out.println("开研终端，握手的时间为："+shakeHandsTime+"/秒");
			}
		}
		// 控制中心3----------------------------------------------------------------------0x07（使用TCP发送）
		if (address.equals("07")) {

			// 登录信息
			if (featureCodes.equals("01")) {
				this.parseGPS(data);
				log.info("开研终端，登录系统。");
			}
			// 脱网信息
			if (featureCodes.equals("02")) {
				this.parseGPS(data);
				log.info("开研终端，退出系统。");
			}
		}
		// 控制中心1*---------------------------------------------------------------------0x09（使用TCP发送）
		if (address.equals("09")) {

		}
		// 控制单元1----------------------------------------------------------------------0x21（使用TCP发送）
		if (address.equals("21")) {

		}
		// 控制单元2（总线模拟中心）------------------------------------------------------0x33（使用TCP发送）
		if (address.equals("33")) {

		}
		// 防盗单元------------------------------------------------------------------------0x41（使用TCP发送）
		if (address.equals("41")) {

		}
		// 手柄或者LCD--------------------------------------------------------------------0x51（使用TCP发送）
		if (address.equals("51")) {

		}
		// 摄像头附件----------------------------------------------------------------------0x71（使用TCP发送）
		if (address.equals("71")) {

		}
		// 关锁附件------------------------------------------------------------------------0x81（使用TCP发送）
		if (address.equals("81")) {

		}
		// 音频附件------------------------------------------------------------------------0x91（使用TCP发送）
		if (address.equals("91")) {

		}
		// 齿轮传感器附件------------------------------------------------------------------0xA1（使用TCP发送）
		if (address.equals("a1")) {

		}
		// ID识别器附件-------------------------------------------------------------------0xB1（使用TCP发送）
		if (address.equals("b1")) {

		}

	}

	private void parseControlCenter1(String featureCodes, String data) {

		// 0x03 0x01-点名查询回复
		if (featureCodes.equals("01")) {
			this.parseGPS(data);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "2101", "0");
			// System.out.println("开研终端--------------------->"+this.getDeviceSN()+"--------------------点名查询回复。");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------点名查询回复。");
		}
		// 0x03 0x02- 临时监控查询回复
		if (featureCodes.equals("02")) {
			this.parseGPS(data);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "2102", "0");
			// System.out.println("开研终端--------------------->"+this.getDeviceSN()+"--------------------临时监控查询回复");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------临时监控查询回复");
		}
		// 0x03 0x03-基本监控查询回复
		if (featureCodes.equals("03")) {
			this.parseGPS(data);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "2103", "0");
			// System.out.println("开研终端--------------------->"+this.getDeviceSN()+"--------------------基本监控查询回复");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------基本监控查询回复");
		}
		// 0x03 0x04-定距查询指令回复
		if (featureCodes.equals("04")) {
			this.parseGPS(data);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "2104", "0");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------定距查询指令回复");
		}
		// 0x03 0x06-黑匣子数据定时查询指令回复
		if (featureCodes.equals("06")) {
			// this.parseGPS(data);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "2106", "0");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------黑匣子数据定时查询指令回复");
		}
		// 0x03 0x08-黑匣子数据定距查询指令回复
		if (featureCodes.equals("08")) {
			// this.parseGPS(data);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "2108", "0");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------黑匣子数据定距查询指令回复");
		}
		// 0x03 0x0A-行驶报表查询指令回复
		if (featureCodes.equals("0a")) {
			// this.parseGPS(data);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "210A", "0");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------行驶报表查询指令回复");
		}
		// 0x03 0x0B-查询终端参数回复
		if (featureCodes.equals("0b")) {

			this.parseQueryParameterReply(data);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "030B", "0");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------回复中心查询终端参数.");
		}
		// 0x03 0x0D-----------------------------------------报警信息
		if (featureCodes.equals("0d")) {
			String alarmStatus = data.substring(0, 8);
			this.parseAlarmStatus(alarmStatus);
			this.parseGPS(data.substring(8));

			String reply = "FE210D0005D0FF";
			ICommonGatewayService gateway = new CommonGatewayServiceImpl();
			int res = gateway.sendDataToTcpTerminal(this.getDeviceSN(), Tools
					.fromHexString(reply), "030d");
			log.info(
					this.getDeviceSN() + " 中心图片应答：" + reply + ",发送结果：" + res);

			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------上传报警信息和位置信息。");
		}
		// 0x03 0x0F-----------------------------------------故障文字信息
		if (featureCodes.equals("0f")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------故障文字信息");
		}
		// 0x03 0x10-----------------------------------------提示文字信息
		if (featureCodes.equals("10")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------提示文字信息");
		}
		// 0x03 0x11----------------------------------------- 手柄（ LC D ）输入文字信息
		if (featureCodes.equals("11")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------手柄（ LC D ）输入文字信息。");
		}
		// 0x03 0x12----------------------------------------- 手柄（ LC D ）回复填空信息
		if (featureCodes.equals("12")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------手柄（ LC D ）回复填空信息");
		}
		// 0x03 0x13-----------------------------------------回复抢答指令信息
		if (featureCodes.equals("13")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------回复抢答指令信息");
		}
		// 0x03 0x14-----------------------------------------索取无线编程信息指令
		if (featureCodes.equals("14")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------索取无线编程信息指令");
		}
		// 0x03 0x16-----------------------------------------指令回码
		if (featureCodes.equals("16")) {
			this.parseReply(data);
			// this.parseGPS(data);
			// log.info("开研终端--------------------->"+this.getDeviceSN()+"--------------------指令回码");
		}
		// 0x03 0x1B-----------------------------------------剪线信息上报（主动）
		if (featureCodes.equals("1b")) {
			String timeDiffer = data.substring(0, 8);
			String hexTimeDiffer = timeDiffer.substring(6, 8)
					+ timeDiffer.substring(4, 6) + timeDiffer.substring(2, 4)
					+ timeDiffer.substring(0, 2);
			int intTimeDiffer = Integer.parseInt(hexTimeDiffer, 16);

			String offTime = data.substring(8, 16);
			String formatOfferTime = this.parseTime(offTime);

			String onTime = data.substring(16, 24);
			String formatOnTime = this.parseTime(onTime);
			this.setAlarmType(AlarmType.DEVICE_REMOVE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);

			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------剪线信息上报（主动）,时间偏差："
							+ intTimeDiffer + ";剪线时间：" + formatOfferTime
							+ ";上线时间：" + formatOnTime);
		}
		// 0x03 0x1D-----------------------------------------回复中心询问数据流量
		if (featureCodes.equals("1d")) {
			String resieveNotCenter = data.substring(0, 8);
			String hexResieveNotCenter = resieveNotCenter.substring(6, 8)
					+ resieveNotCenter.substring(4, 6)
					+ resieveNotCenter.substring(2, 4)
					+ resieveNotCenter.substring(0, 2);
			int intResieveNotCenter = Integer.parseInt(hexResieveNotCenter, 16);

			String resieceCinter = data.substring(8, 16);
			String hexResieceCinter = resieceCinter.substring(6, 8)
					+ resieceCinter.substring(4, 6)
					+ resieceCinter.substring(2, 4)
					+ resieceCinter.substring(0, 2);
			int intResieveCenter = Integer.parseInt(hexResieceCinter, 16);

			String sent = data.substring(16, 24);
			String hexSent = sent.substring(6, 8) + sent.substring(4, 6)
					+ sent.substring(2, 4) + sent.substring(0, 2);
			int intSent = Integer.parseInt(hexSent, 16);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "311D", "0");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "-------------------- 回复中心询问数据流量，接收非中心下发数据量："
							+ intResieveNotCenter + "/字节； 中心下发数据量："
							+ intResieveCenter + "/字节；发出总数据量：" + intSent
							+ "/字节");
		}
		// 0x03 0x1F-----------------------------------------里程补偿
		if (featureCodes.equals("1f")) {
			String mile = data.substring(6, 8) + data.substring(4, 6)
					+ data.substring(2, 4) + data.substring(0, 2);
			int formtatMile = Integer.parseInt(mile, 16) / 19438;

			log.info(
					"开研终--------------------->" + this.getDeviceSN()
							+ "-------------------- 里程补偿  距离为：" + formtatMile
							+ "/km");
		}
		// 0x03 0x20-----------------------------------------保存的时间信息上发指令
		if (featureCodes.equals("20")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------保存的时间信息上发指令");
		}
		// 0x03 0x21-----------------------------------------接收摄像头图像数据帧
		if (featureCodes.equals("21")) {

			this.parsePicture(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------上传摄像头图像数据帧");
		}
		// 0x03 0x22-----------------------------------------附件登录信息
		if (featureCodes.equals("22")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------附件登录信息");
		}
		// 0x03 0x23-----------------------------------------索取无线编程字库指令
		if (featureCodes.equals("23")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------索取无线编程字库指令");
		}
		// 0x03 0x25-----------------------------------------上传定标位置信息指令
		if (featureCodes.equals("25")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------上传定标位置信息指令");
		}
		// 0x03 0x26-----------------------------------------上传空重柜信息
		if (featureCodes.equals("26")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------上传空重柜信息");
		}
		// 0x03 0x28-----------------------------------------空重车转换信息
		if (featureCodes.equals("28")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------空重车转换信息");
		}
		// 0x03 0x29-----------------------------------------确认司机在车上
		if (featureCodes.equals("29")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "-------------------- 确认司机在车上");
		}
		// 0x03 0x2A-----------------------------------------提示中心有大量盲区补偿数据
		if (featureCodes.equals("2a")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------提示中心有大量盲区补偿数据");
		}
		// 0x03 0x2B-----------------------------------------少量盲区补点主动上传
		if (featureCodes.equals("2b")) {
			this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------少量盲区补点主动上传");
		}
		// 0x03 0x2C----------------------------------------- 将 GPS 信息完整上传
		if (featureCodes.equals("2c")) {
			for (int i = 0; i < data.length(); i += 52) {
				this.parseGPS(data.substring(i, i + 52));
			}
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "212C", "0");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "-------------------- 将 GP S 信息完整上传");
		}
		// 0x03 0x2D-----------------------------------------关锁指令回码
		if (featureCodes.equals("2d")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------关锁指令回码");
		}
		// 0x03 0x2E----------------------------------------- 查询 LC D 货车信息类型
		if (featureCodes.equals("2e")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------查询 LC D 货车信息类型");
		}
		// 0x03 0x2F----------------------------------------- 查询 GPR S
		// 盲区短信上传设置参数回复指令
		if (featureCodes.equals("2f")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------查询 GPR S 盲区短信上传设置参数回复指令");
		}
		// 0x03 0x31-----------------------------------------终端回复中心询问当前报警的区域参数
		if (featureCodes.equals("31")) {
			String infor = this.parseArea(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------回复中心询问当前报警的区域参数。" + infor);
		}
		// 0x03 0x32-----------------------------------------终端回复中心询问当前终端温度
		if (featureCodes.equals("32")) {
			String U = data.substring(0, 2);
			double intU = Integer.parseInt(U, 16) * 0.1;

			String temperature = data.substring(2, 10);
			String hexTemperature = temperature.subSequence(6, 8)
					+ temperature.substring(4, 6) + temperature.substring(2, 4)
					+ temperature.substring(0, 2);
			int intTemperature = ((Integer.parseInt(hexTemperature, 16) - 673) * 423) / 1024;
			this.parseGPS(data);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "4132", "0");
			log.info(
					"开研终端---------------------->" + this.getDeviceSN()
							+ "--------------------回复中心询问当前前终端电压温度, 电压为："
							+ intU + "/伏；温度为：" + intTemperature + "/℃");
		}
		// 0x03 0x36-----------------------------------------询问音频编码列表回复
		if (featureCodes.equals("36")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------询问音频编码列表回复");
		}
		// 0x03 0x37-----------------------------------------回复中心查询特定电话号码信息
		if (featureCodes.equals("37")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------回复中心查询特定电话号码信息");
		}
		// 0x03 0x38-----------------------------------------回复中心即时播放语音准备就绪信息
		if (featureCodes.equals("38")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------回复中心即时播放语音准备就绪信息");
		}
		// 0x03 0x3A-----------------------------------------回复中心查询限制拨打号码
		if (featureCodes.equals("3a")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------回复中心查询限制拨打号码");
		}
		// 0x03 0x3B-----------------------------------------回复中心目前终端的允许通话状态
		if (featureCodes.equals("3b")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------回复中心目前终端的允许通话状态");
		}
		// 0x03 0x3C-----------------------------------------要求中心设置本机号码及网络参数
		if (featureCodes.equals("3c")) {

			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------要求中心设置本机号码及网络参数");
		}
		// 0x03 0x3D----------------------------------------- 主动发送 AC C 变换信息
		if (featureCodes.equals("3d")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------主动发送 AC C 变换信息");
		}
		// 0x03 0x3E-----------------------------------------上传司机识别信息
		if (featureCodes.equals("3e")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------上传司机识别信息");
		}
		// 0x03 0x3F-----------------------------------------定时上传正反转信息
		if (featureCodes.equals("3f")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------定时上传正反转信息");
		}
		// 0x03 0x40----------------------------------------- 上传 I D 信息
		if (featureCodes.equals("40")) {
			// this.parseGPS(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------上传 I D 信息");
		}
		// 0x03 0x41-----------------------------------------上传附件工作状态信息
		if (featureCodes.equals("41")) {
			this.parseWordStatus(data);
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------上传附件工作状态信息");
		}
		// 0x03 0x42-----------------------------------------回复保存照片索引
		if (featureCodes.equals("42")) {
			for (int i = 0; i < data.length(); i += 8) {
				String date = this.parseTime(data.substring(i, i + 8));
				log.info(
						"开研终端--------------------->" + this.getDeviceSN()
								+ "--------------------回复保存照片索引" + date);
			}
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "7142", "0");
			// log.info("开研终端--------------->"+this.getDeviceSN()+"回复保存照片索引");
		}
		// 0x03 0x43-----------------------------------------回复查询自动拍照参数指令
		if (featureCodes.equals("43")) {
			for (int i = 0; i < data.length(); i += 10) {
				this.parseCameraParameters(data.substring(i, i + 10));
			}
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "7143", "0");
			log.info(
					"开研终端--------------------->" + this.getDeviceSN()
							+ "--------------------回复查询自动拍照参数指令");
		}
		// 0x03 0x44-----------------------------------------设备回传温度信息令
		if (featureCodes.equals("44")) {
			String gpsData = data.substring(0, 52);
			this.parseGPS(gpsData);

			String interval = data.substring(52, 56);
			String formatInterval = Integer.parseInt(interval.substring(2, 4)
					+ interval.substring(0, 2), 16)
					+ "";
			log.info(
					"开研终端:--------------------->" + this.getDeviceSN()
							+ "--------------------上传温度及位置信息。时间间隔为："
							+ formatInterval + "/秒");

			String temperatureData = data.substring(56);
			for (int i = 0; i < temperatureData.length(); i += 6) {
				this.parseTemperature(temperatureData.substring(i, i + 6));
			}
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "D101", "0");
			log.info(
					"开研终端:--------------------->" + this.getDeviceSN()
							+ "--------------------上传温度及位置信息。");
		}
		// 0x03 0x45-----------------------------------------设备回传油量信息
		if (featureCodes.equals("45")) {
			String gpsData = data.substring(0, 52);
			this.parseGPS(gpsData);
			String interval = data.substring(52, 56);
			String formatInterval = Integer.parseInt(interval.substring(2, 4)
					+ interval.substring(0, 2), 16)
					+ "";
			log.info(
					"开研终端:---------->" + this.getDeviceSN()
							+ "上传油量及位置信息。时间间隔为：" + formatInterval + "/秒");
			String oil = data.substring(56, 62);
			String formatOil = Integer.parseInt(oil.substring(2, 4)
					+ oil.substring(0, 2), 16)
					+ "";

			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "D103", "0");
			log.info(
					"开研终端:---------->" + this.getDeviceSN() + "上传油量及位置信息。油量值为："
							+ formatOil);
			log.info(
					"开研终端:---------->" + this.getDeviceSN() + "上传油量及位置信息。");
		}
		// 0x03 0x46------------------------------设备回传停车时间统计信息
		if (featureCodes.equals("46")) {
			String type = data.substring(0, 2);
			String time = data.substring(2, 10);
			String formatTime = Integer.parseInt(time.substring(6, 8)
					+ time.substring(4, 6) + time.substring(2, 4)
					+ time.substring(0, 2), 16)
					+ "";
			if (type.equals("30")) {
				log.info(
						"开研终端------------>" + this.getDeviceSN()
								+ " 回传停车时间统计信息，停车总时间为：" + formatTime + "/秒");
			} else {
				log.info(
						"开研终端------------>" + this.getDeviceSN()
								+ " 回传停车时间统计信息，不熄火停车时间为：" + formatTime + "/秒");
			}
			String gps = data.substring(10);
			this.parseGPS(gps);

		}
		/*
		 * //0x03 0x47------------------------------设备回传计价器数据信息
		 * if(featureCodes.equals("47")){
		 * 
		 * log.info("开研终端，设备回传计价器数据信息"); } //0x03
		 * 0x48-----------------------------------------回复查询香港自动拍照参数指令
		 * if(featureCodes.equals("48")){
		 * 
		 * log.info("开研终端，回复查询香港自动拍照参数指令"); } //0x03
		 * 0xC0------------------------------设备回传给中心货车各种监测器数据
		 * if(featureCodes.equals("C0")){
		 * 
		 * log.info("开研终端，设备回传给中心货车各种监测器数据"); } //0x03
		 * 0xD0-----------------------------------------透传公车数据
		 * if(featureCodes.equals("D0")){
		 * 
		 * log.info("开研终端，-透传公车数据"); } //0x03
		 * 0xD1-----------------------------------------透传公车语音报站器数据
		 * if(featureCodes.equals("D1")){
		 * 
		 * log.info("开研终端，透传公车语音报站器数据"); } //0x03
		 * 0xE0------------------------------公安处警结果信息
		 * if(featureCodes.equals("E0")){
		 * 
		 * log.info("开研终端，公安处警结果信息"); } //0x03
		 * 0xE1------------------------------透传 LE D 发出的数据
		 * if(featureCodes.equals("E1")){ log.info("开研终端，透传 LE D
		 * 发出的数据"); } //0x03 0xE2------------------------------转发站牌给公交服务器的指令
		 * if(featureCodes.equals("E2")){
		 * log.info("开研终端，转发站牌给公交服务器的指令"); } //0x03
		 * 0xE3------------------------------透传站牌给公交服务器的数据
		 * if(featureCodes.equals("E3")){
		 * 
		 * log.info("开研终端，透传站牌给公交服务器的数据"); } //0x03
		 * 0xF1------------------------------上发查询货源信息
		 * if(featureCodes.equals("F1")){
		 * 
		 * log.info("开研终端，上发查询货源信息"); } //0x03
		 * 0xF2------------------------------上发查询货源分页信息
		 * if(featureCodes.equals("F2")){
		 * log.info("开研终端，上发查询货源分页信息"); } //0x03
		 * 0xF4------------------------------上发竞标信息
		 * if(featureCodes.equals("F4")){
		 * 
		 * log.info("开研终端，上发竞标信息"); } //0x03
		 * 0xF5------------------------------上发车主确认交易信息
		 * if(featureCodes.equals("F5")){
		 * 
		 * log.info("开研终端，上发车主确认交易信息"); } //0x03
		 * 0xF6------------------------------上发缴费信息
		 * if(featureCodes.equals("F6")){
		 * 
		 * log.info("开研终端，上发缴费信息"); } //0x03
		 * 0xF7------------------------------上传易流货物空重载信息
		 * if(featureCodes.equals("F7")){ String gpsData = data.substring(0,
		 * 52); this.parseGPS(gpsData); String infor= data.substring(52);
		 * if(infor.equals("30")){
		 * log.info("开研终端:---------->"+this.getDeviceSN()+"上传易流货物空重载信息。货物状态：空");
		 * }else{
		 * log.info("开研终端:---------->"+this.getDeviceSN()+"上传易流货物空重载信息。货物状态：重"); }
		 *  }
		 */
	}

	private void parseWordStatus(String data) {
		// 00000000000000000000000000000000ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff
		String address = data.substring(0, 32);
		String status = data.substring(32, 64);
		String activeAddress = data.substring(64);

		for (int i = 0; i < address.length(); i += 2) {
			String result = this.parseTermStatus(address.substring(i, i + 2),
					status.substring(i, i + 2), activeAddress.substring(i,
							i + 2));

			log.info(result);

		}
		log.info(
				"开研终端------------->" + this.getDeviceSN() + "上传附件检测信息");
	}

	private String parseTermStatus(String substring, String substring2,
			String substring3) {
		String result = "";
		if (!substring3.equals("FF")) {
			result = "终端检测到活动的附件地址：" + substring3 + "  ";
		}
		if (substring.equals("ff") || substring.equals("00")) {
			return result;
		} else {
			if (substring2.equals("FF")) {
				result += "附件地址" + substring + "没有工作";
			}
			if (substring2.equals("55")) {
				result += "附件地址" + substring + "工作正常";
			}
			if (substring.equals("31")) {
				result += "附件地址" + substring + "正在检测";
			}
		}
		return result;
	}

	private void parseReply(String data) {
		// fe 9d31d4d4 03 16 00 08 1e311e 67 ff
		String functionCode = data.substring(0, 2);
		String cmdCode = data.substring(2);
		if (cmdCode.equals("2101")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心点名查询指令！");
		}
		if (cmdCode.equals("2102")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心按起止时间回传指令！");
		}
		if (cmdCode.equals("2103")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置定时回传指令！");
		}
		if (cmdCode.equals("2104")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置定距回传指令！");
		}
		if (cmdCode.equals("0124")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置超速报警指令！");
		}
		if (cmdCode.equals("0125")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置区域报警指令！");
		}
		if (cmdCode.equals("0126")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置取消区域报警指令！");
		}
		if (cmdCode.equals("210e")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心取消报警状态位指令！");
		}
		if (cmdCode.equals("5111")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复下发短消息指令！");
		}
		if (cmdCode.equals("3116")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置重启指令！");
		}
		if (cmdCode.equals("3107")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置恢复出厂设置指令！");
		}
		if (cmdCode.equals("311e")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置监听电话指令！");
		}
		if (cmdCode.equals("4101")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置远程开锁门指令！");
		}
		if (cmdCode.equals("5103")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心通话限制指令！");
		}
		if (cmdCode.equals("5104")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置可以通话指令！");
		}
		if (cmdCode.equals("3101")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置IP,TCP,UDP端口指令！");
		}
		if (cmdCode.equals("3105")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置终端ID指令！");
		}
		if (cmdCode.equals("3124")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置通讯模式指令！");
		}
		if (cmdCode.equals("d102")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置温度按时间间隔上传指令！");
		}
		if (cmdCode.equals("d104")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置油量按时间间隔上传指令！");
		}
		if (cmdCode.equals("7101")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置单张拍照指令！");
		}
		if (cmdCode.equals("7102")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置定时拍照指令！");
		}
		if (cmdCode.equals("7103")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心拍照查询指令！");
		}
		if (cmdCode.equals("7104")) {
			log.info(
					"开研终端----------->" + this.getDeviceSN()
							+ "---------回复中心设置摄像头自动拍照参数设置指令！");
		}

	}

	private void parseCameraParameters(String substring) {
		String condition = substring.substring(0, 2);
		String uploadCount = substring.substring(2, 4);
		String saveInterval = substring.substring(4, 8);
		String saveCount = substring.substring(8, 10);

		String conditionInfor = "";
		// 重到空拍照
		if (condition.equals("01")) {
			conditionInfor = "重到空拍照";
		}
		// 空到重拍照
		if (condition.equals("02")) {
			conditionInfor = "空到重拍照";
		}
		// ACC由关到开拍照
		if (condition.equals("03")) {
			conditionInfor = "ACC由关到开拍照";
		}
		// ACC由开到关拍照
		if (condition.equals("04")) {
			conditionInfor = "ACC由开到关拍照";
		}
		// 门关到开拍照
		if (condition.equals("05")) {
			conditionInfor = "门关到开拍照";
		}
		// 门开到关拍照
		if (condition.equals("06")) {
			conditionInfor = "门开到关拍照";
		}
		// 劫警拍照
		if (condition.equals("07")) {
			conditionInfor = "劫警拍照";
		}
		// 盗警拍照
		if (condition.equals("08")) {
			conditionInfor = "盗警拍照";
		}
		// 超速报警拍照
		if (condition.equals("09")) {
			conditionInfor = "超速报警拍照";
		}

		String formatUploadCount = "";
		if (uploadCount.equals("00")) {
			formatUploadCount = "无需上传照片！";
		} else {
			formatUploadCount = "需要上传 " + Integer.parseInt(uploadCount, 16)
					+ "/张照片！";
		}

		String formatSaveInterval = "";
		if (saveInterval.equals("0000")) {
			formatSaveInterval = "无需保存照片！";
		} else {
			formatSaveInterval = "保存照片的时间间隔为："
					+ Integer.parseInt(saveInterval.substring(2, 4)
							+ saveInterval.substring(0, 2), 16) + "/秒";
		}

		String formatSaveCount = "";
		if (saveCount.equals("00")) {
			formatSaveCount = "无需保存照片！";
		} else {
			formatSaveCount = "需要保存照片 " + Integer.parseInt(saveCount, 16)
					+ "/张！";
		}

		log.info(
				"开研终端----->" + this.getDeviceSN()
						+ "------回复中心查询自动拍照参数指令 .拍照类型为：" + conditionInfor
						+ ",上传照片信息：" + formatUploadCount + ",存储照片信息："
						+ formatSaveInterval + ",存储张数：" + formatSaveCount);

	}

	private void parsePicture(String data) {
		HashMap<String, String> loadMap = null;
		String udpAddr = null;

		try {
			loadMap = AllConfigCache.getInstance().getLoadMap();
			if (loadMap != null) {
				isOverLoad = loadMap.get("isOverLoad");
				udpAddr = loadMap.get("overLoadUdpAddr");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String head = "FFD8FFE000114A464946000102030405060708090AFFDB004300100C0C0E0C0A100E0E0E1212101418281A181616183224261E283A343E3C3A34383840485C4E404458463838506E525860626868683E4E727A7064785C666864FFDB004301121212161616301A1A30644238426464646464646464646464646464646464646464646464646464646464646464646464646464646464646464646464646464FFC4001F0000010501010101010100000000000000000102030405060708090A0BFFC400B5100002010303020403050504040000017D01020300041105122131410613516107227114328191A1082342B1C11552D1F02433627282090A161718191A25262728292A3435363738393A434445464748494A535455565758595A636465666768696A737475767778797A838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE1E2E3E4E5E6E7E8E9EAF1F2F3F4F5F6F7F8F9FAFFC4001F0100030101010101010101010000000000000102030405060708090A0BFFC400B51100020102040403040705040400010277000102031104052131061241510761711322328108144291A1B1C109233352F0156272D10A162434E125F11718191A262728292A35363738393A434445464748494A535455565758595A636465666768696A737475767778797A82838485868788898A92939495969798999AA2A3A4A5A6A7A8A9AAB2B3B4B5B6B7B8B9BAC2C3C4C5C6C7C8C9CAD2D3D4D5D6D7D8D9DAE2E3E4E5E6E7E8E9EAF2F3F4F5F6F7F8F9FAFF";
		String picData = "";
		String no = data.substring(0, 2);
		String time = data.substring(2, 10);
		String camera = data.substring(10, 12);
		String gps = data.substring(12, 64);
		this.parseGPS(gps);

		String total = data.substring(64, 68);
		String current = data.substring(68, 72);
		String infor = Integer.parseInt(current, 16) == 0 ? "最后一包" : Integer
				.parseInt(current, 16)
				+ "";

		log.info(
				"图片:" + no + ",数据-------->共：" + Integer.parseInt(total, 16)
						+ "包,当前为第：" + infor + "   数据内容：" + data.substring(72));
		String reply = "FE"
				+ camera
				+ "210008"
				+ no
				+ current.substring(2, 4)
				+ current.substring(0, 2)
				+ KaiYanUtil.getCRC(camera + "210008" + no
						+ current.substring(2, 4) + current.substring(0, 2))
				+ "FF";
		ICommonGatewayService gateway = new CommonGatewayServiceImpl();
		int res = gateway.sendDataToTcpTerminal(this.getDeviceSN(), Tools
				.fromHexString(reply), camera + "21");
		// this.setReplyByte(Tools.fromHexString(reply));
		log.info(
				this.getDeviceSN() + " 中心图片应答：" + reply + ",发送结果：" + res);

		// 图片的第一包数据
		if (current.equals("0001")) {

			Picture pic = new Picture();
			pic.setFirstReq(true);
			pic.setNum(Integer.parseInt(no, 16));
			pic.setPakcNo(Integer.parseInt(current, 16));
			pic.setDeviceId(this.getDeviceSN());
			pic.setDate(new Date());
			pic.setPackcounts(Integer.parseInt(total, 16));
			// pic.setImgStrCont(data.substring(72));

			if (!data.substring(72, 78).equals("ffd8ff")) {
				picData = head + data.substring(72);
			} else {
				picData = data.substring(72);
			}

			pic.addImgContHex(current, picData);

			pic.setTimeStamp(new Timestamp(System.currentTimeMillis()));

			PicCache.getInstance().addPicture(this.getDeviceSN(), pic);
		} else {

			Picture picture = PicCache.getInstance().getPicture(
					this.getDeviceSN());
			if (picture != null && picture.isFirstReq()) {
				picture.setFirstReq(true);
				picture.setNum(Integer.parseInt(no, 16));
				picture.setPakcNo(Integer.parseInt(current, 16));
				picture.setDeviceId(this.getDeviceSN());
				picture.setDate(new Date());
				picture.setTimeStamp(new Timestamp(System.currentTimeMillis()));
				// picture.setImgStrCont(Tools.bytesToHexString(picbytes));
				picture.setPackcounts(Integer.parseInt(total, 16));
				picture.addImgContHex(Integer.parseInt(current, 16) + "", data
						.substring(72));

				PicCache.getInstance().addPicture(this.getDeviceSN(), picture);

				if (current.equals("0000")) {
					// 图片传输完毕
					DBService service = new DBServiceImpl();
					picture.addImgContHex(Integer.parseInt(total, 16) + "",
							data.substring(72));
					picture.setReaded(true);
					service.insertPicInfo(picture);
					log.info(
							"开研终端---->" + this.getDeviceSN() + "-----上传的图片："
									+ no + " 最后一包接收完毕，已经入库。");
					picture.reset();
					PicCache.getInstance().removePicture(this.getDeviceSN());
				}
			} else if (isOverLoad != null && isOverLoad.equals("1")) {
				// 负载转发
				if (udpAddr != null) {
					String host = udpAddr.split(":")[0];
					String sport = udpAddr.split(":")[1];
					int port = Integer.parseInt(sport);

					OverLoadUtil.sendToUdp(host, port, Tools
							.fromHexString(hexString));
				}
			} else {
				Picture pic = new Picture();
				pic.setFirstReq(true);
				pic.setNum(Integer.parseInt(no, 16));
				pic.setPakcNo(Integer.parseInt(current, 16));
				pic.setDeviceId(this.getDeviceSN());
				pic.setDate(new Date());
				pic.setPackcounts(Integer.parseInt(total, 16));
				// pic.setImgStrCont(data.substring(72));

				if (!data.substring(72, 78).equals("ffd8ff")) {
					picData = head + data.substring(72);
				} else {
					picData = data.substring(72);
				}

				pic.setTimeStamp(new Timestamp(System.currentTimeMillis()));
				picture
						.addImgContHex(Integer.parseInt(total, 16) + "",
								picData);

				picture.setReaded(true);
				DBService service = new DBServiceImpl();
				service.insertPicInfo(picture);
				log.info(
						"开研终端---->" + this.getDeviceSN() + "-----上传的图片：" + no
								+ " 最后一包接收完毕，已经入库。");
				picture.reset();
			}

		}

	}

	private void parseTemperature(String substring) {
		String hexNo = substring.substring(0, 2);
		int no = Integer.parseInt(hexNo, 16);

		String last = substring.substring(2);
		String sign = last.substring(2, 3);
		String temp = "";
		String temperature = last.substring(3, 4) + last.substring(0, 2);
		if (sign.equals("0")) {
			temp = Integer.parseInt(temperature, 16) * 0.5 + "";
		} else {
			temp = "-" + Integer.parseInt(temperature, 16) * 0.5;
		}
		log.info("温度探头：" + no + " 上传的温度为：" + temp + "℃");
	}

	/*
	 * private byte[] reply(String string) { //FE210D0005D0FF int len =
	 * string.length()/2; ByteBuffer bb = ByteBuffer.allocate(len); for(int
	 * i=0;i<string.length();i++){
	 * bb.put((byte)Integer.parseInt(string.substring(i, i+2), 16)); }
	 * 
	 * return bb.array(); }
	 */
	private void parseQueryParameterReply(String data) {
		// 器件代码 参数代码 具体参数内容
		// 1 2

		String deviceCode = data.substring(0, 2);
		String parameterCode = data.substring(2, 6);
		String parameterContent = data.substring(6);

		// 0x21---------------------通讯板（主板）
		if (deviceCode.equals("21")) {
			// 超速参数
			if (parameterCode.equals("2401")) {

				int maxSpeed = Integer.parseInt(parameterContent, 16);

				String kmMaxSpeed = Tools.formatKnotToKm(maxSpeed + "");

				Log.getInstance()
						.outLog(
								"开研终端------->" + this.getDeviceSN()
										+ ",回复中心查询超速报警指令,最大速度为：" + kmMaxSpeed
										+ " km/h");

			}
			// 区域参数0
			if (parameterCode.equals("2501") || parameterCode.equals("4501")
					|| parameterCode.equals("6501")
					|| parameterCode.equals("8501")
					|| parameterCode.equals("A501")
					|| parameterCode.equals("C501")
					|| parameterCode.equals("E501")
					|| parameterCode.equals("0502")
					|| parameterCode.equals("2502")
					|| parameterCode.equals("4502")
					|| parameterCode.equals("6502")
					|| parameterCode.equals("8502")
					|| parameterCode.equals("A502")
					|| parameterCode.equals("C502")
					|| parameterCode.equals("E502")
					|| parameterCode.equals("0503")) {

				String infor = this.parseArea(parameterContent);
				// System.out.println(infor);
				log.info(infor);
			}
			// 短信登录状态
			if (parameterCode.equals("3903")) {

			}
			// 短信脱网状态
			if (parameterCode.equals("3A03")) {

			}
			// 超区域超时报警时间（秒）
			if (parameterCode.equals("3B03")) {

			}
			// GPS省电时间（秒）
			if (parameterCode.equals("0505")) {

			}

		}
		// 0x41---------------------防盗板（副板）
		if (deviceCode.trim().equals("41")) {
			// 恢复初始化
			if (parameterCode.equals("0000")) {

			}
			// 低压报警参数
			if (parameterCode.equals("0100")) {
				String U = Integer.parseInt(parameterContent, 16) * 0.1 + "";
				log.info(
						"开研终端------->" + this.getDeviceSN()
								+ "------------回复中心查询超低压警指令,最低电压为：" + U + " 伏");
			}
			// 高温报警参数
			if (parameterCode.equals("0200")) {
				String formatParameterContent = parameterContent
						.substring(2, 4)
						+ parameterContent.substring(0, 2);
				log.info(
						"开研终端------->" + this.getDeviceSN()
								+ "------------回复中心查询高温报警压警指令,温度为："
								+ Integer.parseInt(formatParameterContent, 16));
			}
			// 关GPRS时间
			if (parameterCode.equals("0400")) {
				String formatParameterContent = parameterContent
						.substring(2, 4)
						+ parameterContent.substring(0, 2);
				log.info(
						"开研终端------->" + this.getDeviceSN()
								+ "------------回复中心查询关闭GPS时间指令,时间为 ："
								+ Integer.parseInt(formatParameterContent, 16)
								+ "/秒");
			}
			// 关附件时间
			if (parameterCode.equals("0600")) {
				String formatParameterContent = parameterContent
						.substring(2, 4)
						+ parameterContent.substring(0, 2);
				log.info(
						"开研终端------->" + this.getDeviceSN()
								+ "------------回复中心查询关附件时间指令,时间为 ："
								+ Integer.parseInt(formatParameterContent, 16)
								+ "/秒");
			}
			// 屏蔽状态参数
			if (parameterCode.equals("0800")) {

			}
			// 屏蔽报警参数
			if (parameterCode.equals("0900")) {

			}
			// 判断剪线时间
			if (parameterCode.equals("0A00")) {
				String formatParameterContent = parameterContent
						.substring(2, 4)
						+ parameterContent.substring(0, 2);
				log.info(
						"开研终端------->" + this.getDeviceSN()
								+ "------------回复中心查询判断剪线时间指令,时间为 ："
								+ Integer.parseInt(formatParameterContent, 16)
								+ "/秒");
			}
			// 温度补偿系数
			if (parameterCode.equals("0C00")) {

			}
			// 序列号
			if (parameterCode.equals("0E00")) {

			}
			// 保存错误问题
			if (parameterCode.equals("F00E")) {

			}
			// 保存锁车指令
			if (parameterCode.equals("0F00")) {

			}
			// 保存盗警信息
			if (parameterCode.equals("200F")) {

			}
			// 保存ID号
			if (parameterCode.equals("210F")) {
				String id = new String(Tools.fromHexString(parameterContent));
				log.info(
						"开研终端---------->" + this.getDeviceSN()
								+ "------------最后一次操作人员的ID号：" + id);
			}
			// 有效ID号
			if (parameterCode.equals("250F")) {
				String id = new String(Tools.fromHexString(parameterContent));
				log.info(
						"开研终端---------->" + this.getDeviceSN()
								+ "------------有效ID号：" + id);
			}
			// 保存需要上发的ID号
			if (parameterCode.equals("760F")) {
				String id = new String(Tools.fromHexString(parameterContent));
				log.info(
						"开研终端---------->" + this.getDeviceSN()
								+ "------------保存需要上发的ID号：" + id);
			}

		}
		// 0x51---------------------LCD
		if (deviceCode.trim().equals("51")) {
			// 参数初始化
			if (parameterCode.equals("0000")) {

			}
			// 用户1开机密码
			if (parameterCode.equals("0100")) {

				String hexpassword = parameterContent.substring(2, 18);
				String password = "";
				for (int i = 0; i < hexpassword.length(); i += 2) {
					password += Integer.parseInt(hexpassword
							.substring(i, i + 2), 16)
							+ "";
				}

				log.info(
						"开研终端------->" + this.getDeviceSN()
								+ ",回复中心查询用户一开机密码指令,密码为：" + password);

			}
			// 用户2开机密码
			if (parameterCode.equals("0B00")) {

			}
			// 用户3开机密码
			if (parameterCode.equals("1500")) {

			}
			// 用户4开机密码
			if (parameterCode.equals("1F00")) {

			}
			// 用户1设置密码
			if (parameterCode.equals("2900")) {

			}
			// 用户2设置密码
			if (parameterCode.equals("3300")) {

			}
			// 用户3设置密码
			if (parameterCode.equals("3D00")) {

			}
			// 用户4设置密码
			if (parameterCode.equals("4700")) {

			}
			// 密码状态
			if (parameterCode.equals("5100")) {

			}
			// 用户私密状态
			if (parameterCode.equals("5200")) {

			}
			// 自动接听电话状态
			if (parameterCode.equals("5300")) {

			}
			// 显示公司名称
			if (parameterCode.equals("5400")) {

			}
			// 自定义公司名称
			if (parameterCode.equals("5500")) {

			}
			// 定标类型
			if (parameterCode.equals("7D00")) {

			}
			// 一键拨号号码
			if (parameterCode.equals("7E00")) {

			}
			// 货车信息菜单类型
			if (parameterCode.equals("9F00")) {

			}
		}
		// 0x91---------------------音频
		if (deviceCode.trim().equals("91")) {
			// 恢复初始化
			if (parameterCode.equals("0000")) {

			}
			// 音量大小
			if (parameterCode.equals("0100")) {

			}
		}
	}

	private String parseArea(String parameterContent) {

		String areaInfor = parameterContent.substring(0, 28);

		String maxLat = parameterContent.substring(28, 36);
		String formatMaxLat = parseLat(maxLat);

		String maxLon = parameterContent.substring(36, 44);
		String formatMaxLon = parseLon(maxLon);

		String minLat = parameterContent.substring(44, 52);
		String formatMinLat = parseLat(minLat);

		String minLon = parameterContent.substring(52, 60);
		String formatMinLon = parseLon(minLon);

		String areaStatus = parameterContent.substring(60, 62);
		String alarmType = "";
		if (areaStatus.equals("FF")) {
			alarmType = "没设置报警参数";
		}
		if (areaStatus.equals("30")) {
			alarmType = "进区域报警";
		}
		if (areaStatus.equals("31")) {
			alarmType = "出区域报警";
		}
		if (areaStatus.equals("32")) {
			alarmType = "进区域关机";
		}
		String areaNo = parameterContent.substring(62, 64);

		return "区域报警信息-------->区域序号：" + Integer.parseInt(areaNo, 16) + "  坐标点："
				+ formatMinLat + "," + formatMinLon + ";" + formatMaxLat + ","
				+ formatMaxLon + "   报警状态为：" + alarmType;
	}

	private String parseID(String id) {

		// A39DB4C5
		String longId = "";
		String ID = id.substring(6, 8) + id.substring(4, 6)
				+ id.substring(2, 4) + id.substring(0, 2);
		longId = Long.parseLong(ID, 16) + "";
		while (longId.length() < 10) {
			longId = "0" + longId;
		}
		// if(longId.substring(0,1).equals("2")){
		// longId = "5" + longId.substring(1, 10);
		// }
		//		
		String realID = "1" + longId;

		return realID;

	}

	public void parseGPS(String data) {
		// 时间 经度 纬度 速度 方向 定位状态 里程 车状态 报警位 校验
		// B5361108 12993222 04264011 00 00 10 030000 000000 00000000 9E
		// 时间
		String Atime = data.substring(0, 8);
		String date = parseTime(Atime);
		this.setTime(date);
		// this.setTimeStamp(new Timestamp());
		// 纬度
		String Blat = data.substring(8, 16);
		String formatLat = this.parseLat(Blat);
		this.setCoordY(formatLat);
		log.info("开研终端，上传位置信息，纬度为：" + formatLat);
		// System.out.println("开研终端，上传位置信息，纬度为："+formatLat);

		// 经度
		String Clon = data.substring(16, 24);
		String formatLon = this.parseLon(Clon);
		this.setCoordX(formatLon);
		log.info("开研终端，上传位置信息，经度为：" + formatLon);
		// System.out.println("开研终端，上传位置信息，经度为："+formatLon);

		// 速度
		String Dspeed = data.substring(24, 26);
		int speed = Integer.parseInt(Dspeed, 16);
		String kmSpeed = Tools.formatKnotToKm(speed + "");
		log.info("开研终端，上传位置信息，速度为：" + kmSpeed + "km/h");
		// System.out.println("开研终端，上传位置信息，速度为："+kmSpeed+"km/h");
		this.setSpeed(kmSpeed);

		// 方向及GPS标志
		String EderictionAndGPSsign = data.substring(26, 28);
		this.parseDrictionAndGPSsign(EderictionAndGPSsign);

		// GPS天线状态，定位状态，司机编号。
		String FGPSmastLocateStatusDriverNo = data.substring(28, 30);
		this.parseGPSmastLocateStatusDriverNo(FGPSmastLocateStatusDriverNo);

		// 里程
		String gmileage = data.substring(30, 36);
		String mile = gmileage.substring(4, 6) + gmileage.substring(2, 4)
				+ gmileage.substring(0, 2);
		int mileage = Integer.parseInt(mile, 16) / 76;
		this.setMileage(mileage + "");
		log.info("开研终端，上传位置信息，里程为：" + mileage);
		// System.out.println("开研终端，上传位置信息，里程为："+mileage);

		// 车辆状态
		String HcarStatus = data.substring(36, 42);
		this.parseCarStatus(HcarStatus);

		// 报警位
		String IalarmBit = data.substring(42, 50);
		this.parseAlarmStatus(IalarmBit);

		// 校验位
		String Jverify = data.substring(50, 52);

		 
	}

	private String parseLat(String clat) {
		// 77 15 33 22 ---->22度33.1577分（北纬）
		String fen = clat.substring(4, 6) + "." + clat.subSequence(2, 4)
				+ clat.substring(0, 2);
		double lfen = Double.parseDouble(fen);

		double lat = Integer.parseInt(clat.substring(6)) + lfen / 60;

		DecimalFormat format = new DecimalFormat("0.000000");
		format.setMaximumFractionDigits(6);

		return format.format(lat);

	}

	private String parseLon(String blon) {

		// 35 25 40 11 -》114度02.535分（东经）

		String fen = blon.substring(5, 6) + blon.substring(2, 3) + "."
				+ blon.subSequence(3, 4) + blon.substring(0, 2);

		double lfen = Double.parseDouble(fen);

		String du = blon.substring(6, 8) + blon.substring(4, 5);

		double lon = Integer.parseInt(du) + lfen / 60;

		DecimalFormat format = new DecimalFormat("0.000000");
		format.setMaximumFractionDigits(6);

		return format.format(lon);

	}

	private void parseAlarmStatus(String ialarmBit) {
		String bit = Tools.HexToBinary(ialarmBit);

		String s0 = bit.substring(0, 8);
		String s1 = bit.substring(8, 16);
		String s2 = bit.substring(16, 24);
		String s3 = bit.substring(24, 32);

		// s1
		// BIT 7=1，超时报警（停车一段时间后报警）
		if (s1.substring(0, 1).equals("1")) {
			this.setAlarmType(AlarmType.STOP_CAR_ALARM_TYPE);
			this.setAlarmSubType("1");
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					"开研终端----->" + this.getDeviceSN() + "上传位置信息，报警类型为：超时报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：超时报警。");
		}
		// BIT 6=1，出站报警
		if (s1.substring(1, 2).equals("1")) {
			// this.setAlarmType(AlarmType.DEVICE_FAIL_ALARM_TYPE);
			log.info(
					"开研终端----->" + this.getDeviceSN() + "上传位置信息，报警类型为：出站报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：出站报警。");
		}
		// BIT 5=1，进站报警
		if (s1.substring(2, 3).equals("1")) {
			// this.setAlarmType(AlarmType.DEVICE_FAIL_ALARM_TYPE);
			log.info(
					"开研终端----->" + this.getDeviceSN() + "上传位置信息，报警类型为：进站报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：进站报警。");
		}
		// BIT 4=1，LCD传输故障
		if (s1.substring(3, 4).equals("1")) {
			log.info(
					"开研终端----->" + this.getDeviceSN()
							+ "上传位置信息，报警类型为：LCD传输故障报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：LCD传输故障报警。");
		}
		// BIT 3=1，GPS接收机故障
		if (s1.substring(4, 5).equals("1")) {
			this.setAlarmType(AlarmType.GPS_MODEL_FAIL_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					"开研终端----->" + this.getDeviceSN()
							+ "上传位置信息，报警类型为：GPS接收机故障报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：GPS接收机故障报警。");
		}
		// BIT 2=1，GPS天线故障报警
		if (s1.substring(5, 6).equals("1")) {
			this.setAlarmType(AlarmType.GPS_ANTENNA_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					"开研终端----->" + this.getDeviceSN()
							+ "上传位置信息，报警类型为：GPS天线故障报警报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：GPS天线故障报警报警。");
		}
		// BIT 1=1，超速报警
		if (s1.substring(6, 7).equals("1")) {
			this.setAlarmType(AlarmType.SPEED_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					"开研终端----->" + this.getDeviceSN() + "上传位置信息，报警类型为：超速报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：超速报警。");
		}
		// BIT 0=1，超出范围报警
		if (s1.substring(7, 8).equals("1")) {
			this.setAlarmType(AlarmType.AREA_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					"开研终端----->" + this.getDeviceSN() + "上传位置信息，报警类型为：超出范围报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：超出范围报警。");
		}
		// s0

		// BIT 7=1，备用
		if (s0.substring(0, 1).equals("1")) {

		} else {

		}
		// BIT 7=1，备用
		if (s0.substring(1, 2).equals("1")) {

		} else {

		}
		// BIT 5=1，司机密码错误
		if (s0.substring(2, 3).equals("1")) {
			// this.setAlarmType(AlarmType.DEVICE_FAIL_ALARM_TYPE);
			log.info(
					"开研终端----->" + this.getDeviceSN()
							+ "上传位置信息，报警类型为：司机密码错误报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：司机密码错误报警。");
		}
		// BIT 4=1，终端温度异常报警
		if (s0.substring(3, 4).equals("1")) {
			this.setAlarmType(AlarmType.TEMPERATURE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					"开研终端----->" + this.getDeviceSN()
							+ "上传位置信息，报警类型为：终端温度异常报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：终端温度异常报警。");
		}
		// BIT 3=1，电压低报警
		if (s0.substring(4, 5).equals("1")) {
			this.setAlarmType(AlarmType.LACK_PRESSURE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					"开研终端----->" + this.getDeviceSN() + "上传位置信息，报警类型为：电压低报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：电压低报警。");
		}
		// BIT 2=1，剪线报警
		if (s0.substring(5, 6).equals("1")) {
			this.setAlarmType(AlarmType.DEVICE_REMOVE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					"开研终端----->" + this.getDeviceSN() + "上传位置信息，报警类型为：剪线报警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：剪线报警。");
		}
		// BIT 1=1，盗警
		if (s0.substring(6, 7).equals("1")) {
			this.setAlarmType(AlarmType.SECURITY_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					"开研终端----->" + this.getDeviceSN() + "上传位置信息，报警类型为：盗警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：盗警。");
		}
		// BIT 0=1，劫警
		if (s0.substring(7, 8).equals("1")) {
			this.setAlarmType(AlarmType.ACTIVE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					"开研终端----->" + this.getDeviceSN() + "上传位置信息，报警类型为：劫警。");
			// System.out.println("开研终端----->"+this.getDeviceSN()+"上传位置信息，报警类型为：劫警。");
		}

	}

	private void parseCarStatus(String hcarStatus) {
		String bit = Tools.HexToBinary(hcarStatus);

		String s0 = bit.substring(0, 8);
		String s1 = bit.substring(8, 16);
		String s2 = bit.substring(16, 24);

		String protocolType = s2.substring(0, 2) + s1.substring(1, 2);
		if (protocolType.equals("000")) { 
			log.info("开研终端，上传位置信息，协议类型为：开研协议");
			// System.out.println("开研终端，上传位置信息，协议类型为：开研协议");
		}
		if (protocolType.equals("001")) { 
			log.info("开研终端，上传位置信息，协议类型为：海事协议");
			// System.out.println("开研终端，上传位置信息，协议类型为：海事协议");
		}
		if (protocolType.equals("010")) { 
			log.info("开研终端，上传位置信息，协议类型为：KY协议");
			// System.out.println("开研终端，上传位置信息，协议类型为 ：KY协议");
		}
		if (protocolType.equals("011")) { 
			log.info("开研终端，上传位置信息，协议类型为 ：华强短信");
			// System.out.println("开研终端，上传位置信息，协议类型为：华强短信");
		}
		if (protocolType.equals("100")) { 
			log.info("开研终端，上传位置信息，协议类型为：海南渔政短信");
			// System.out.println("开研终端，上传位置信息，协议类型为：海南渔政短信");
		}

		// s2

		// BIT 2=1，GPS关断状态
		if (s2.substring(5, 6).equals("1")) {
			termStatus.setAntenna("1");
			log.info("开研终端，上传位置信息，GPS关断状态：关");
			// System.out.println("开研终端，上传位置信息，GPS关断状态：关");
		} else {
			termStatus.setAntenna("3");
			log.info("开研终端，上传位置信息，GPS关断状态：开");
			// System.out.println("开研终端，上传位置信息，GPS关断状态：开");
		}
		// BIT 1=1，需要PIN码加密
		if (s2.substring(6, 7).equals("1")) {
			log.info("开研终端，上传位置信息，需要PIN码加密。");
			// System.out.println("开研终端，上传位置信息，需要PIN码加密。");
		} else {
			log.info("开研终端，上传位置信息，不需要PIN码加密。");
			// System.out.println("开研终端，上传位置信息，不需要PIN码加密。");
		}
		// BIT 0=1，纯短信方式
		if (s2.substring(7, 8).equals("1")) {
			log.info("开研终端，上传位置信息，纯短信方式。");
			// System.out.println("开研终端，上传位置信息，纯短信方式");
		} else {
			log.info("开研终端，上传位置信息，非纯短信方式");
			// System.out.println("开研终端，上传位置信息，非纯短信方式");
		}

		// s1

		// BIT 7=1，使用GPRS盲区短信回传
		if (s1.substring(0, 1).equals("1")) {
			log.info("开研终端，上传位置信息，使用GPRS盲区短信回传");
			// System.out.println("开研终端，上传位置信息，使用GPRS盲区短信回传");
		} else {
			log.info("开研终端，上传位置信息， 非使用GPRS盲区短信回传");
			// System.out.println("开研终端，上传位置信息，非使用GPRS盲区短信回传");
		}
		// BIT 6=1，协议类型标志位BIT0（原来协议：使用海事局协议作为主协议）
		if (s1.substring(1, 2).equals("1")) {

		} else {

		}
		// BIT 5=1，网络信号弱
		if (s1.substring(2, 3).equals("1")) {
			log.info("开研终端，上传位置信息，网络信号弱 ");
			// System.out.println("开研终端，上传位置信息，网络信号弱");
		} else {
			log.info("开研终端，上传位置信息， 网络信号强");
			// System.out.println("开研终端，上传位置信息，网络信号强");
		}
		// BIT 4=1，主机升级中
		if (s1.substring(3, 4).equals("1")) {
			log.info("开研终端，上传位置信息， 主机升级中");
			// System.out.println("开研终端，上传位置信息，主机升级中");
		} else {
			log.info("开研终端，上传位置信息，主机没有升级中");
			// System.out.println("开研终端，上传位置信息，主机没有升级中");
		}
		// BIT 3=1，使用SIM卡2（香港卡）
		if (s1.substring(4, 5).equals("1")) {
			log.info("开研终端，上传位置信息，使用SIM卡2（香港卡）");
			// System.out.println("开研终端，上传位置信息，使用SIM卡2（香港卡）");
		} else {
			log.info("开研终端，上传位置信息， 非使用SIM卡2（香港卡）");
			// System.out.println("开研终端，上传位置信息，非使用SIM卡2（香港卡）");
		}
		// BIT 2=1，报警中
		if (s1.substring(5, 6).equals("1")) {
			log.info("开研终端，上传位置信息，报警中");
			// System.out.println("开研终端，上传位置信息，报警中");
		} else {
			log.info("开研终端，上传位置信息， 非报警中");
			// System.out.println("开研终端，上传位置信息，非报警中");
		}
		// BIT 1=1，没有GPRS网络
		if (s1.substring(6, 7).equals("1")) {
			log.info("开研终端，上传位置信息， 没有GPRS网络");
			// System.out.println("开研终端，上传位置信息，没有GPRS网络");
		} else {
			log.info("开研终端，上传位置信息，有GPRS网络");
			// System.out.println("开研终端，上传位置信息，有GPRS网络");
		}
		// BIT 0=1，没有同中心保持联系
		if (s1.substring(7, 8).equals("1")) {
			log.info("开研终端，上传位置信息， 没有同中心保持联系");
			// System.out.println("开研终端，上传位置信息，没有同中心保持联系");
		} else {
			log.info("开研终端，上传位置信息， 有同中心保持联系");
			// System.out.println("开研终端，上传位置信息，有同中心保持联系");
		}

		// s0
		// BIT 7=1，备用
		if (s0.substring(0, 1).equals("1")) {

		} else {

		}
		// BIT 6=1，设防状态 （施封状态）
		if (s0.substring(0, 1).equals("1")) {
			log.info("开研终端，上传位置信息， 设防状态 : 设防。");
			this.termStatus.setFortification("1");
			// System.out.println("开研终端，上传位置信息，设防状态 : 设防。");
		} else {
			this.termStatus.setFortification("0");
			log.info("开研终端，上传位置信息，设防状态 : 没设防。");
			// System.out.println("开研终端，上传位置信息，设防状态 : 没设防。");
		}
		// BIT 5=1，附件关断状态
		if (s0.substring(0, 1).equals("1")) {
			log.info("开研终端，上传位置信息， 附件关断状态：关");
			// System.out.println("开研终端，上传位置信息，附件关断状态：关");
		} else {
			log.info("开研终端，上传位置信息，  附件关断状态：开");
			// System.out.println("开研终端，上传位置信息， 附件关断状态：开");
		}
		// BIT 4=1，检测口有信号状态（出租车表示重车状态，货车表示上锁状态）
		if (s0.substring(0, 1).equals("1")) {
			log.info("开研终端，上传位置信息， 检测口有信号状态");
			// System.out.println("开研终端，上传位置信息，检测口有信号状态");
		} else {
			log.info("开研终端，上传位置信息， 检测口有信号状态");
			// System.out.println("开研终端，上传位置信息，检测口有信号状态");
		}
		// BIT 3=1，报警喇叭响状态（用于货车则为空调开）
		if (s0.substring(0, 1).equals("1")) {
			log.info("开研终端，上传位置信息， 报警喇叭响状态");
			// System.out.println("开研终端，上传位置信息，报警喇叭响状态");
		} else {
			log.info("开研终端，上传位置信息， 报警喇叭响状态");
			// System.out.println("开研终端，上传位置信息，报警喇叭响状态");
		}
		// BIT 2=1，车门开状态
		if (s0.substring(0, 1).equals("1")) {
			termStatus.setCarDoor("1");
			log.info("开研终端，上传位置信息， 车门开状态：开");
			// System.out.println("开研终端，上传位置信息，车门开状态：开");
		} else {
			termStatus.setCarDoor("0");
			log.info("开研终端，上传位置信息，车门开状态：关");
			// System.out.println("开研终端，上传位置信息，车门开状态：关");
		}
		// BIT 1=1，车辆处于断油断电状态
		if (s0.substring(0, 1).equals("1")) {
			termStatus.setMainPower("0");
			termStatus.setOilElec("0");
			log.info("开研终端，上传位置信息， 车辆处于断油断电状态：是");
			// System.out.println("开研终端，上传位置信息，车辆处于断油断电状态：是");
		} else {
			termStatus.setMainPower("3");
			termStatus.setOilElec("1");
			log.info("开研终端，上传位置信息，车辆处于断油断电状态：否");
			// System.out.println("开研终端，上传位置信息，车辆处于断油断电状态：否");
		}
		// BIT 0=1，ACC开状态
		if (s0.substring(0, 1).equals("1")) {
			termStatus.setAcc("1");
			log.info("开研终端，上传位置信息，ACC开状态：开");
			// System.out.println("开研终端，上传位置信息，ACC开状态：开");
		} else {
			termStatus.setAcc("0");
			log.info("开研终端，上传位置信息， ACC开状态：关");
			// System.out.println("开研终端，上传位置信息，ACC开状态：关");
		}

		this.setStatusRecord(termStatus);

	}

	private void parseGPSmastLocateStatusDriverNo(
			String smastLocateStatusDriverNo) {

		String bit = Tools.HexToBinary(smastLocateStatusDriverNo);

		String d5d4 = bit.substring(2, 4);
		if (d5d4.equals("00")) {
			termStatus.setAntenna("3");
			log.info("开研终端，上传位置信息，天线正常");
			// System.out.println("开研终端，上传位置信息，天线正常");
		}
		if (d5d4.equals("01")) {
			termStatus.setAntenna("1");
			log.info("开研终端，上传位置信息，天线开路");
			this.setAlarmType(AlarmType.GPS_MAST_OPEN_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			// System.out.println("开研终端，上传位置信息，天线开路");
		}
		if (d5d4.equals("10")) {
			termStatus.setAntenna("0");
			log.info("开研终端，上传位置信息，天线短路");
			this.setAlarmType(AlarmType.GPS_MAST_SHORT_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			// System.out.println("开研终端，上传位置信息，天线短路");
		}
		if (d5d4.equals("11")) {
			termStatus.setAntenna("2");
			log.info("开研终端，上传位置信息，天线不合适");
			this.setAlarmType(AlarmType.GPS_MODEL_FAIL_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			// System.out.println("开研终端，上传位置信息，天线不合适");
		}
		String d3d2 = bit.substring(4, 6);

		if (d3d2.equals("01")) {
			termStatus.setLocate("0");
			log.info("开研终端，上传位置信息，定位无效！");
			// System.out.println("开研终端，上传位置信息，定位无效！");
		}
		if (d3d2.equals("10")) {
			termStatus.setLocate("1");
			log.info("开研终端，上传位置信息，2D定位！");
			// System.out.println("开研终端，上传位置信息，2D定位！");
		}
		if (d3d2.equals("11")) {
			termStatus.setLocate("1");
			log.info("开研终端，上传位置信息，3D定位！");
			// System.out.println("开研终端，上传位置信息，3D定位！");
		}

		String d1d0 = bit.substring(6, 8);
		if (d1d0.equals("00")) {
			log.info("开研终端，上传位置信息，司机编号为：---->1");
			// System.out.println("开研终端，上传位置信息，司机编号为：---->1");
		}
		if (d1d0.equals("01")) {
			log.info("开研终端，上传位置信息，司机编号为：---->2");
			// System.out.println("开研终端，上传位置信息，司机编号为：---->2");
		}
		if (d1d0.equals("10")) {
			log.info("开研终端，上传位置信息，司机编号为：---->3");
			// System.out.println("开研终端，上传位置信息，司机编号为：---->3");
		}
		if (d1d0.equals("11")) {
			log.info("开研终端，上传位置信息，司机编号为：---->4");
			// System.out.println("开研终端，上传位置信息，司机编号为：---->4");
		}
		this.setStatusRecord(termStatus);
	}

	private void parseDrictionAndGPSsign(String ederictionAndGPSsign) {
		String bit = Tools.HexToBinary(ederictionAndGPSsign);

		String GPSsign = bit.substring(0, 2);

		// 北纬东经
		if (GPSsign.equals("00")) {

			log.info("开研终端，上传位置信息，北纬东经。");
			// System.out.println("开研终端，上传位置信息，北纬东经。");
		}
		// 北纬西经
		if (GPSsign.equals("01")) {

			log.info("开研终端，上传位置信息，北纬西经。");
			// System.out.println("开研终端，上传位置信息，北纬西经。");

		}
		// 南纬东经
		if (GPSsign.equals("02")) {

			log.info("开研终端，上传位置信息，南纬东经。");
			// System.out.println("开研终端，上传位置信息，南纬东经。");
		}
		// 南纬西经
		if (GPSsign.equals("03")) {

			log.info("开研终端，上传位置信息，南纬西经。");
			// System.out.println("开研终端，上传位置信息，南纬西经。");
		}
		String deriction = bit.substring(2);

		int de = Integer.parseInt(deriction, 2);
		log.info("开研终端，上传位置信息，方向为：" + de * 10 + "°");
		// System.out.println("开研终端，上传位置信息，方向为："+de*10+"°");
		this.setDirection(de * 10 + "");

	}

	private String parseTime(String atime) {

		// B5361108
		String time = atime.substring(6, 8) + atime.substring(4, 6)
				+ atime.substring(2, 4) + atime.substring(0, 2);
		int second = Integer.parseInt(time, 16);

		Calendar c = Calendar.getInstance();
		c.set(2000, 0, 1, 0, 0, 0);
		c.add(Calendar.SECOND, second);

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		c.add(Calendar.HOUR, 8);

		Date d = new Date(c.getTimeInMillis());

		String date = df.format(d);

		// System.out.println("开研终端, 上传位置信息，上传时间为："+date);
		log.info("开研终端, 上传位置信息，上传时间为：" + date);

		return date;

	}

	public void parseHttpGrps(HttpServletRequest request,
			HttpServletResponse response, String hexString) {

	}

	public void parseSMS(String phnum, String content) {

	}

	public static void main(String[] args) {
		ParseKaiYan pky = new ParseKaiYan();
		// 081136B5
		// pky.parseTime("07FCE8FF");
		System.out.println(pky.parseTime("FFE8FC07"));
		pky
				.parseGPRS("fe3538d08b0303001f69180215913408238594311101050cb10000010500010000009a25ff");
		// System.out.println(pky.parseLat("77153322"));
		// pky.parseGPRS("FEA39DB4C50301001FD5D1A40839523722593141110C0C0C830000010500058000001FF4FF");
		// FEA39DB4C5 03 0B 00 27 21 2501
		// 3132333435363738394041424344241533223722401124153022370240113000 4B
		// FF
		// System.out.println(Tools.HexToBinary("000000"));
		// System.out.println(pky.parseLon("35254011"));

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
