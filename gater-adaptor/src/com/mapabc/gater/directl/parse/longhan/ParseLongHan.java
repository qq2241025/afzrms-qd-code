/**
 * 
 */
package com.mapabc.gater.directl.parse.longhan;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.constant.AlarmType;
import com.mapabc.gater.directl.dbutil.DbOperation;
import com.mapabc.gater.directl.dbutil.DbUtil;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.gater.directl.encode.PropertyReader;
import com.mapabc.gater.directl.encode.longhan.LongHanUtil;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.directl.pic.PicCache;
import com.mapabc.gater.directl.pic.Picture;
import com.mapabc.gater.directl.pic.PictureKey;
import com.mapabc.gater.lbsgateway.GBLTerminalList; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.lbsgateway.service.CommonGatewayServiceImpl;
import com.mapabc.gater.lbsgateway.service.ICommonGatewayService;
import com.mapabc.gater.util.HttpUtil;
import com.mapabc.gater.util.OverLoadUtil;
 

/**
 * 
 * @author shiguang.zhou
 * 
 */
public class ParseLongHan extends ParseBase implements ParseService {
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseLongHan.class);
	private String riginalX;
	private String riginalY;
	private String longid = "";
	String deviceid = ""; 
	private String isOverLoad;
	private String deviceType = "GP-LONGHAN-GPRS";
	private boolean isCutOil;
	private boolean isLogin;
	private TTerminal term = null;
	ICommonGatewayService icservice = new CommonGatewayServiceImpl();

	private TTermStatusRecord termStatus = new TTermStatusRecord();
 

	public ParseLongHan() {

	}
 
	public void parseGPRS(String hexString) {
		// TODO Auto-generated method stub
		if (hexString == null || hexString.trim().length() <= 0) {
			return;
		}
		log.info("接收龙翰终端原始数据:" + hexString);
		byte[] cont = Tools.fromHexString(hexString);
		String hexLen = hexString.substring(6, 10); // 包长度hex
		int leng = Tools.byte2Int(Tools.fromHexString(hexLen));

		byte bcmd = cont[2]; // 主信令码
		byte subCmd = cont[9]; // 子信令码
		byte verfyCode = cont[cont.length - 2];// 校验值

		// // if (bcmd != (byte) 0x88 || bcmd != (byte) 0x22) {
		String simIp = null;// hexString.substring(10, 18);// 伪IP,由SIM卡转换得到
		// String simcard = LongHanUtil.ipToSim(null, simIp);
		// this.setDeviceSN(simcard);
		//
		// log.info(
		// "龙翰终端" + this.getDeviceSN() + " 数据包长度：" + leng);
		// String deviceid = null;
		//
		// term = GBLTerminalList.getInstance().getTerminaInfo(simcard);
		// // // 从内存获取终端SIMCARD
		// if (term == null) {
		// log.info("系统中没有适配到指定的终端：device_id=" + simcard);
		// return;
		// }
		// deviceid = term.getDeviceId();
		//
		// //this.setObjId(term.getObjId());
		// //this.setObjType(term.getObjType());
		// this.setPhnum(term.getSimcard());
		// deviceType = term.getTEntTermtype();
		// ====================天津中环设备
		// ID=======================================//
		int ip1 = Tools.byte2Int(Tools.fromHexString(hexString
				.substring(10, 12)));
		int ip2 = Tools.byte2Int(Tools.fromHexString(hexString
				.substring(12, 14)));
		int ip3 = Tools.byte2Int(Tools.fromHexString(hexString
				.substring(14, 16)));
		int ip4 = Tools.byte2Int(Tools.fromHexString(hexString
				.substring(16, 18)));
		String tjzhId = String.valueOf(ip1) + "." + String.valueOf(ip2) + "."
				+ String.valueOf(ip3) + "." + String.valueOf(ip4);

//		term = GBLTerminalList.getInstance().getTerminaInfo(tjzhId);
//		if (term != null) {
//			deviceType = term.getTEntTermtype();
//			if (deviceType != null && deviceType.equals("GP-TJZH-GPRS")) {
//				log.info(
//						this.getDeviceSN() + " 转到天津中环解析类，DEVICE_TYPE="
//								+ deviceType);
//
//				ParseTJZH tjzh = new ParseTJZH();
//
//				tjzh.setObjId(term.getObjId());
//				tjzh.setObjType(term.getObjType());
//				tjzh.setPhnum(term.getSimcard());
//				tjzh.parseGPRS(hexString);
//
//				this.resetObject(tjzh);
//
//				return;
//			}
//		}
		// ===========================================================//
//		else 
		{
			simIp = hexString.substring(10, 18);// 伪IP,由SIM卡转换得到
			String simcard = LongHanUtil.ipToSim(null, simIp);
			this.setDeviceSN(simcard);

			log.info(
					"龙翰终端" + this.getDeviceSN() + " 数据包长度：" + leng);

			term = GBLTerminalList.getInstance().getTerminaInfo(simcard);
			// 从内存获取终端SIMCARD
			if (term == null) {
				log.info(
						"系统中没有适配到指定的终端：device_id=" + simcard);
				return;
			}
			deviceid = term.getDeviceId();

			//this.setObjId(term.getObjId());
			//this.setObjType(term.getObjType());
			this.setPhnum(term.getSimcard());
			deviceType = term.getTEntTermtype();
		}
		log.info(
				this.getDeviceSN() + " 设备类型码：" + deviceType);
		String locStatus = null;
		switch (bcmd) {
		case (byte) 0xb3:
			byte[] bmsg = new byte[cont.length - 11];
			System.arraycopy(cont, 9, bmsg, 0, bmsg.length);

			String msg = "";

			try {
				msg = new String(bmsg, "GB18030");
				this.termStatus.setCpu(msg);
				this.setStatusRecord(termStatus);
				 
				Log.getInstance()
						.outLog(this.getDeviceSN() + " 版本号：" + msg);
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			break;
		case (byte) 0x80:// 一般位置
			byte[] repb80 = this.reply2Terminal(hexString, verfyCode, bcmd,
					bcmd);
			this.setReplyByte(repb80);
			icservice.sendDataToTcpTerminal(deviceid, repb80, null);

			this.parsePosition(hexString, verfyCode, bcmd, bcmd, 0);

			 

			break;
		case (byte) 0x81:// 点名位置

			this.parsePosition(hexString, verfyCode, bcmd, subCmd, 0);
			// try {
			// locStatus = this.getLocateStatus();
			// if (locStatus != null && !locStatus.equals("0")) {
			// this.getRepList().add("LOC");
			// ParseConfigParamUtil.handleConfig(this);
			// } else {
			// log.info(
			// "点名==过滤未定位数据：" + this.getDeviceSN() + ","
			// + this.getCoordX() + "," + this.getCoordY()
			// + "," + this.getTime());
			// }
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			break;
		case (byte) 0x83:// 查看车辆状态

			// 2929 83 001a 4a8d6308 000000000000 0000 00 01 0078 02 ff 3c 00 00
			// 00 0000 8d 0d

			this.parsePosition(hexString, verfyCode, bcmd, subCmd, 0);
			// try {
			// locStatus = this.getLocateStatus();
			// if (locStatus != null && !locStatus.equals("0")) {
			// this.getRepList().add("STATUS");
			// ParseConfigParamUtil.handleConfig(this);
			// } else {
			// log.info(
			// "查看车辆状态==过滤未定位数据：" + this.getDeviceSN() + ","
			// + this.getCoordX() + "," + this.getCoordY()
			// + "," + this.getTime());
			// }
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			break;
		case (byte) 0x84: // 调度信息

			this.parseMessage(cont, verfyCode, bcmd, bcmd);

			try {
				byte[] repb = null;
				if (this.deviceType != null
						&& this.deviceType.equals("GP-LH-HK-GPRS")) {
					repb = this.reply2Terminal(hexString, verfyCode, bcmd,
							subCmd);
				} else {
					repb = this
							.reply2Terminal(hexString, verfyCode, bcmd, bcmd);
				}
				this.setReplyByte(repb);

				icservice.sendDataToTcpTerminal(deviceid, repb, null);
			 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		case (byte) 0x85:// 终端相关设置返回的位置
			String flag = "0";
			try {
				this.parsePosition(hexString, verfyCode, bcmd, subCmd, 0);
				// ParseConfigParamUtil.handleConfig(this);
				 
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			log.info(
					this.getDeviceSN() + " 设置指令成功");
			 

			break;
		case (byte) 0x8e: // 盲区补偿
			byte[] repb = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			this.setReplyByte(repb);
			icservice.sendDataToTcpTerminal(deviceid, repb, null);

			if (this.deviceType != null
					&& this.deviceType.equals("GP-LH-HK-GPRS")) {
				this.parsePosition(hexString, verfyCode, bcmd, subCmd, 0);
			} else {
				this.parsePosition(hexString, verfyCode, bcmd, subCmd, 1);
			}

			try {
				if (this.getLocateStatus() != null
						&& this.getLocateStatus().equals("1")) {
					this.setLocateStatus("2");// 补偿类型
					// ParseConfigParamUtil.handleConfig(this);
					this.getParseList().add(this);
					log.info(
							this.getDeviceSN() + "盲区补偿位置数据：x="
									+ this.getCoordX() + ",y="
									+ this.getCoordY() + ",s="
									+ this.getSpeed() + ",dirction="
									+ this.getDirection() + ",distance="
									+ this.getMileage() + ",time=" + this.getTime());
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
			byte[] repb82 = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			this.setReplyByte(repb82);
			icservice.sendDataToTcpTerminal(deviceid, repb82, null);
			log.info(
					"82报警应答：" + Tools.bytesToHexString(repb82));

			if (this.deviceType != null
					&& this.deviceType.equals("GP-LH-HK-GPRS")) {
				this.parseSpecialAlarmHK(hexString, verfyCode, bcmd, subCmd, 0);

				if (this.termStatus.getLoginOut() != null
						&& this.termStatus.getLoginOut().equals("1")) {
					DBService dbservice = new DBServiceImpl();
					try {
						dbservice.saveTurnOutRecord(this.getDeviceSN(),
								this.longid, null, null, null, null, "1");
					} catch (Exception e) {
						Log.getInstance().errorLog("保存登签记录异常", e);
					}
				} else if (this.termStatus.getLoginOut().equals("0")) {
					try {
						DBService dbservice = new DBServiceImpl();
						dbservice.saveTurnOutRecord(this.getDeviceSN(),
								this.longid, null, null, null, null, "0");
					} catch (Exception e) {
						Log.getInstance().errorLog("保存退签记录异常", e);
					}
				}
			} else {
				this.parseSpecialAlarm(hexString, verfyCode, bcmd, subCmd, 1);
			}
			break;
		case (byte) 0x8f:// 报警数据补传指令
			byte[] repb_8f = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			this.setReplyByte(repb_8f);
			icservice.sendDataToTcpTerminal(deviceid, repb_8f, null);

			log.info(
					"特殊报警补偿应答：" + Tools.bytesToHexString(repb_8f));
			this.parseSpecialAlarmHK(hexString, verfyCode, bcmd, subCmd, 0);
			break;

		case (byte) 0x8d:
			// byte[] repb_8d = this.reply2Terminal(hexString, verfyCode, bcmd,
			// subCmd);
			// this.setReplyByte(repb_8d);
			// icservice.sendDataToTcpTerminal(deviceid, repb_8d, null);

			byte[] repbb = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			this.setReplyByte(repbb);

			this.parseImage(hexString);

			break;

		case (byte) 0xa3:// 上传捆绑操作请求[0xA3]
			byte[] reply1 = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			icservice.sendDataToTcpTerminal(this.getDeviceSN(), reply1, null);
			// this.setReplyByte(reply1);
			String hexWayBillNu = hexString.substring(18, 42);
			String wayBillNu = new String(Tools.fromHexString(hexWayBillNu));

			String bindState = hexString.substring(42, 44);
			String optType1 = "";
			String bindStatus = "";
			if (bindState.equals("00")) {
				optType1 = "0";
				bindStatus = "捆绑请求";
			} else {
				optType1 = "1";
				bindStatus = "取消捆绑请求";
			}
			String msgConfigCode = "";
			String hexMsgConfigCode = hexString.substring(44, 56);
			if (!hexMsgConfigCode.equals("000000000000")
					&& !hexMsgConfigCode.equals("202020202020")) {
				msgConfigCode = new String(Tools
						.fromHexString(hexMsgConfigCode));
			}
			String hexDriverCode = hexString.substring(56, 60);
			int driverCode = Integer.parseInt(hexDriverCode, 16);

			log.info(
					"龙翰终端---" + this.getDeviceSN() + "---上传捆绑操作请求 ：" + "运单编号："
							+ wayBillNu + "  操作请求：" + bindStatus + "  短信确认码："
							+ msgConfigCode + "司机卡号：" + driverCode);

			// <?xml version="1.0" encoding="utf-8"?>
			// <request>
			// <optType></optType>
			// <wayBillNum></wayBillNum>
			// <confirmCode></confirmCode>
			// <cardNo></cardNo>
			// </request>
			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("request");
			root.addAttribute("name", "requestOrderBinding");
			root.addAttribute("deviceId", this.getDeviceSN());

			Element opttype = root.addElement("optType");
			opttype.setText(optType1);

			Element waybillNum = root.addElement("wayBillNum");
			waybillNum.setText(wayBillNu);

			Element confirmCode = root.addElement("confirmCode");
			confirmCode.setText(msgConfigCode);

			Element carNo = root.addElement("cardNo");
			carNo.setText(hexDriverCode);
			Element direction = root.addElement("direction");

			doc.setXMLEncoding("gb2312");
			String req = doc.asXML();
			String res = this.sendData(req);
			log.info(res);
			break;
		case (byte) 0xa5:// 更新/发送/删除/封锁/解锁运单回复[0xA5]
			byte[] reply2 = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			icservice.sendDataToTcpTerminal(this.getDeviceSN(), reply2, null);
			// this.setReplyByte(reply2);

			String optType2 = hexString.substring(18, 20);
			String opt = optType2.substring(1);
			String hexWayBillNo = hexString.substring(20, 44);
			String wayBillNo = new String(Tools.fromHexString(hexWayBillNo));

			String optResult = hexString.substring(44, 46);
			String optresult = optResult.substring(1, 2);
			String reason = "";
			String failReason = "";
			// WayBillBean way = new WayBillBean();
			// way.setDeviceId(this.getDeviceSN());
			// way.setWayNum(wayBillNo);

			if (optResult.equals("00")) {
				reason = hexString.substring(46, 48);
				failReason = reason.substring(1, 2);
				// way.setFlag(false);
				// WayBillCache.getInstance().addWayBill(way, false);
			} else {
				// way.setFlag(true);
				// WayBillCache.getInstance().addWayBill(way, true);
			}

			log.info(
					"龙翰终端---" + this.getDeviceSN() + "---更新/发送/删除/封锁/解锁运单回复"
							+ "操作类型：" + optType2 + "  运单编号：" + wayBillNo
							+ "  操作结果：" + optResult + "失败原因：" + reason);

			// <?xml version="1.0" encoding="utf-8"?>
			// <request>
			// <optType></optType>
			// <wayBillNum></wayBillNum>
			// <result></result>
			// <errorCode></errorCode>
			// </request>
			// if (!WayBillCache.getInstance().isResponse(way)) {
			Document docu = DocumentHelper.createDocument();
			Element root1 = docu.addElement("request");
			root1.addAttribute("name", "setDriverActionCode");
			root1.addAttribute("deviceId", this.getDeviceSN());

			Element opttype2 = root1.addElement("optType");
			opttype2.setText(opt);

			Element waybillNum2 = root1.addElement("wayBillNum");
			waybillNum2.setText(wayBillNo);

			Element result = root1.addElement("result");
			result.setText(optresult);

			Element errorCode = root1.addElement("errorCode");
			errorCode.setText(failReason);
			docu.setXMLEncoding("gb2312");
			String req2 = docu.asXML();

			String rels = this.sendData(req2);
			log.info(rels);

			// }
			break;
		case (byte) 0xb1:
			log.info(
					"龙翰终端---" + this.getDeviceSN() + "---心跳数据。");
			byte[] repb_b1 = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			this.setReplyByte(repb_b1);
			icservice.sendDataToTcpTerminal(deviceid, repb_b1, null);
			this.setReplyByte(repb_b1);

			break;
		case (byte) 0xab:// 里程数据
			byte[] replyab = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			// this.setReplyByte(replyab);
			icservice.sendDataToTcpTerminal(this.getDeviceSN(), replyab, null);

			String hexMileage = hexString.substring(18, 32);
			String mileage = new String(Tools.fromHexString(hexMileage));

			String hexDrivercode = hexString.substring(32, 36);
			String drivercode = Integer.parseInt(hexDrivercode, 16) + "";
			log.info(
					"龙翰终端---" + this.getDeviceSN() + "---上传里程数据,里程：" + mileage
							+ "  司机卡号：" + drivercode);
			break;
		case (byte) 0xa2:
			byte[] replya2 = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			// this.setReplyByte(replya2);
			icservice.sendDataToTcpTerminal(this.getDeviceSN(), replya2, null);

			String addOilNo = new String(Tools.fromHexString(hexString
					.substring(18, 38)));

			String addOilCount = new String(Tools.fromHexString(hexString
					.substring(38, 52)));
			int num = Integer.parseInt(addOilCount);

			String addOilMile = new String(Tools.fromHexString(hexString
					.substring(52, 66)));
			int mil = Integer.parseInt(addOilMile);

			String money = new String(Tools.fromHexString(hexString.substring(
					66, 80)));
			float cost = Float.parseFloat(money);

			String driverNo = hexString.substring(80, 84);
			String ic = Integer.parseInt(driverNo, 16) + "";

			try {

				this.saveInOilData(this.getDeviceSN(), addOilNo, num, mil,
						cost, ic);

			} catch (Exception e) {

			}

			log.info(
					"龙翰终端---" + this.getDeviceSN() + "---上传入油数据。入油单号："
							+ addOilNo + " 入油量：" + addOilCount + " 入油里程："
							+ addOilMile + " 入油费用：" + money + " 司机编号："
							+ driverNo);
			break;
		case (byte) 0xaa:// 运单操作请求
			// 中心回复
			byte[] reply31 = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			// this.setReplyByte(reply31);

			icservice.sendDataToTcpTerminal(this.getDeviceSN(), reply31, null);

			log.info(
					"运单操作AA请求：" + Tools.bytesToHexString(reply31));

			String wayNo = new String(Tools.fromHexString(hexString.substring(
					18, 42)));
			String optType = hexString.substring(42, 44);
			String dri = Integer.parseInt(hexString.substring(44, 48), 16) + "";

			String optInfor = "";
			String optCode = "";
			if (optType.equals("00")) {
				optInfor = "请求确认";// 运单确认请求操作
				optCode = "0";
			}
			if (optType.equals("01")) {
				optInfor = "请求开始";// 运单开始请求操作
				optCode = "1";
			}
			if (optType.equals("02")) {
				optInfor = "请求完成";// 运单完成请求操作
				optCode = "2";
			}
			if (optType.equals("03")) {
				optInfor = "请求取消";// 运单取消请求操作
				optCode = "3";
			}
			Document cfmDoc = DocumentHelper.createDocument();
			Element cfmRoot = cfmDoc.addElement("request");
			cfmRoot.addAttribute("name", "optCtlConfirm");
			cfmRoot.addAttribute("deviceId", this.getDeviceSN());

			Element cfmOptType = cfmRoot.addElement("optType");
			cfmOptType.setText(optCode);

			Element cfmwaybillNum2 = cfmRoot.addElement("wayBillNum");
			cfmwaybillNum2.setText(wayNo);

			Element cfmicard = cfmRoot.addElement("cardNo");
			cfmicard.setText(dri);

			cfmDoc.setXMLEncoding("gb2312");
			String cfmcont = cfmDoc.asXML();
			this.sendData(cfmcont);

			log.info(
					"龙翰终端---" + this.getDeviceSN() + "---上传运单操作请求数据："
							+ optInfor + "运单：" + wayNo + "司机号：" + dri);

			break;
		case (byte) 0xac:
			byte[] replyac = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			// this.setReplyByte(replyac);

			icservice.sendDataToTcpTerminal(this.getDeviceSN(), replyac, null);

			String driver = Integer.parseInt(hexString.substring(18, 22), 16)
					+ "";
			log.info(
					"龙翰终端---" + this.getDeviceSN() + "---上传司机姓名数据,司机编号："
							+ driver);
			break;
		case (byte) 0xa4:
			byte[] replya4 = this.reply2Terminal(hexString, verfyCode, bcmd,
					subCmd);
			// this.setReplyByte(replya4);

			icservice.sendDataToTcpTerminal(this.getDeviceSN(), replya4, null);

			String smsNo = new String(Tools.fromHexString(hexString.substring(
					18, 24)));
			String waybillNo = new String(Tools.fromHexString(hexString
					.substring(24, 48)));
			String drivNo = Integer.parseInt(hexString.substring(48, 52), 16)
					+ "";
			log.info(
					"龙翰终端---" + this.getDeviceSN() + "---上传固定短消息，消息编号：" + smsNo
							+ " 运单编号：" + waybillNo + " 司机编号：" + drivNo);
			break;
		case (byte) 0xAD:
			byte[] repbad = this.reply2Terminal(hexString, verfyCode, bcmd,
					bcmd);
			// this.setReplyByte(repbad);
			icservice.sendDataToTcpTerminal(deviceid, repbad, null);

			String minOil = Integer.parseInt(hexString.substring(18, 22), 16)
					+ "";
			String maxOil = Integer.parseInt(hexString.substring(22, 26), 16)
					+ "";
			String ratio = Integer.parseInt(hexString.substring(26, 28), 16)
					+ "";
			String relate = Integer.parseInt(hexString.substring(28, 30), 16) == 0 ? "1"
					: "0";

			Document reduceDoc = DocumentHelper.createDocument();
			Element reduceRoot = reduceDoc.addElement("request");
			reduceRoot.addAttribute("name", "oilReduceAlarmParam");
			reduceRoot.addAttribute("deviceId", this.getDeviceSN());

			Element minOilEmt = reduceRoot.addElement("minOilValuev");
			minOilEmt.setText(minOil);

			Element maxOilEmt = reduceRoot.addElement("maxOilValuev");
			maxOilEmt.setText(maxOil);

			Element reduceRatio = reduceRoot.addElement("reduceRatio");
			reduceRatio.setText(ratio);

			Element relation = reduceRoot.addElement("relation");
			relation.setText(relate);

			reduceDoc.setXMLEncoding("gbk");
			String reducecont = reduceDoc.asXML();

			this.sendData(reducecont);

			log.info(
					this.getDeviceSN() + " 设置的最大油耗电阻值为：" + maxOil + ",最小电阻值为："
							+ minOil + ",降幅比率阀值：" + ratio);

			break;

		}

	}

	private String sendData(String req) {
		String doc = "";
		String res = "";
		String desc = "";
		try {
			PropertyReader prop = new PropertyReader("kyt.properties");
			String url = prop.getProperty("fwdUrl");
			log.info("运单转发URL=" + url + ",内容：\r\n" + req);
			byte[] retb = HttpUtil.getPostURLData(url, req.getBytes());
			if (retb != null) {
				// WayBillCache.getInstance().setResponse(true);

				doc = new String(retb);
				log.info("龙翰终端转发数据请求结果 ：-----------" + doc);
				String result = doc.substring(doc.indexOf("result") + 7, doc
						.lastIndexOf("result") - 2);
				res += "请求结果： " + result;

				if (result.equals("0")) {
					desc = doc.substring(doc.indexOf("desc") + 5, doc
							.lastIndexOf("desc") - 2);
				}
				res += " 错误描述：" + desc;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block

			Log.getInstance().errorLog("", e);
		}

		return res;
	}

	// 解析图片包
	private void parseImage(String hexString) {

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
		String packNoHex = hexString.substring(18, 20);
		int packNo = Integer.parseInt(packNoHex, 16);

		String cameraHex = hexString.substring(20, 22);
		String picNumHex = cameraHex.substring(0, 1);
		String chnaelHex = cameraHex.substring(1, 2);

		int chanel = Integer.parseInt(chnaelHex, 16);
		int picNum = Integer.parseInt(picNumHex, 16);

		String imgType = "";
		String type = hexString.substring(22, 24);
		if (type.equals("01")) {
			// 劫警图片
			imgType = "1";
		} else if (type.equals("02")) {
			imgType = "4";
		} else if (type.equals("03")) {
			imgType = "5";
		} else if (type.equals("04")) {
			// 定时图片
			imgType = "2";
		} else if (type.equals("05")) {
			// 即时捕捉图片
			imgType = "3";
		}
		String totalPackCntHex = hexString.substring(24, 26);
		int totalPacnCnt = Integer.parseInt(totalPackCntHex, 16);

		String imgCont = hexString.substring(26, hexString.length() - 4);

		log.info(
				this.getDeviceSN() + " 图片信息：包号=" + packNo + ",图片类型=" + imgType
						+ ",通道=" + chanel + ",编号=" + picNum + ",总包号="
						+ totalPacnCnt + ",图片数据=" + imgCont);

		PictureKey pkey = new PictureKey();
		pkey.setDeviceId(this.getDeviceSN());
		pkey.setPicNum(picNum);
		pkey.setChannel(chanel);

		Picture picture = PicCache.getInstance().getPicture(pkey);

		if (picture == null) {
			picture = new Picture();

			picture.setFirstReq(true);
			picture.setPackcounts(totalPacnCnt);
			picture.setNum(picNum);
			picture.setPakcNo(packNo);
			picture.setDeviceId(this.getDeviceSN());
			picture.setDate(new Date());
			picture.setImgStrCont(imgCont);
			picture.setType(imgType);
			picture.setChanelNo(chanel + "");
			picture.addImgContHex(packNo + "", imgCont);
			picture.setTimeStamp(new Timestamp(System.currentTimeMillis()));
			picture.setDeviceType(this.deviceType);
			// PicCache.getInstance().addPicture(this.getDeviceSN(), pic);
			PicCache.getInstance().addMorePictureObj(pkey, picture);

		} else {
			// Picture picture = PicCache.getInstance().getPicture(
			// /** this.getDeviceSN() */
			// pkey);
			if (picture.isFirstReq()) {
				picture.setFirstReq(true);
				picture.setPackcounts(totalPacnCnt);
				picture.setNum(picNum);
				picture.setPakcNo(packNo);
				picture.setDeviceId(this.getDeviceSN());
				picture.setDate(new Date());
				picture.setImgStrCont(imgCont);
				picture.setType(imgType);
				picture.setChanelNo(chanel + "");
				picture.addImgContHex(packNo + "", imgCont);
				picture.setDeviceType(this.deviceType);
				picture.setTimeStamp(new Timestamp(System.currentTimeMillis()));
				PicCache.getInstance().addMorePictureObj(pkey, picture);
				if (/** packNo == totalPacnCnt &&* */
				picture.size() > 0 && totalPacnCnt == picture.size()) {
					// 图片传输完毕
					DBService service = new DBServiceImpl();
					service.insertPicInfo(picture);
					picture.reset();
					PicCache.getInstance().removePicture(/** this.getDeviceSN() */
					pkey);
					log.info(
							this.getDeviceSN() + " 图片数据已入库。");
				}

			} else if (isOverLoad != null && isOverLoad.equals("1")) {
				// 负载转发
			if (udpAddr != null){
				String host = udpAddr.split(":")[0];
				String sport = udpAddr.split(":")[1];
				int port = Integer.parseInt(sport);
				byte[] cont = Tools.fromHexString(hexString);
				OverLoadUtil.sendToUdp(host, port, cont);
			}
			}
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
		// Timestamp timestamp = new Timestamp(Tools.formatStrToDate(
		// this.getTime(), "yyyy-MM-dd HH:mm:ss").getTime());
		// this.setTimeStamp(timestamp);

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
		String imageInterval = null;
		String icCard = null;

		if (type == 0) {// 一般位置信息有如下内容
			String hexLC = hexString.substring(56, 62);
			String distance = Integer.parseInt(hexLC, 16) / 1000.0f + "";// 里程,单位公里

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
			resSt3 = this.parseStatus3(hexString, st3, verfyCode, bcmd, subCmd);

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

			if (this.deviceType != null
					&& this.deviceType.equals("GP-LH-HK-GPRS")) {
				longid = Tools.byte2Int(Tools.fromHexString(hexString
						.substring(80, 84)))// 登签ID
						+ "";
			} else {
				String ID2 = hexString.substring(80, 82);
				// log.info(
				// this.getDeviceSN() + " 登签ID2:" + ID2);

				longid = Tools.byte2Int(Tools.fromHexString(this.longid + ID2))// 登签ID2
						+ "";
				// log.info(
				// this.getDeviceSN() + " 登签ID:" + this.longid);

				imageInterval = Tools.byte2Int(Tools.fromHexString(hexString
						.substring(82, 84)))
						+ "";// 图像定时间隔
				if (hexString.length() > 45 * 2) {// 扩展数据
					String extendData = hexString.substring(86, hexString
							.length() - 4);
					String hexLen = extendData.substring(0, 4);
					int len = Integer.parseInt(hexLen, 16);
					String extCmd = extendData.substring(4, 8);
					String tempData = extendData.substring(8);

					if (extCmd.equals("0001")) {// 油耗电阻值数据

						String intPart = extendData.substring(8, 12);
						int oilInt = Integer.parseInt(intPart, 16);
						String decimalPart = extendData.substring(12, 14);
						int oilDec = Integer.parseInt(decimalPart, 16);
						String oilMass = oilInt + "." + oilDec;
						float R = Float.parseFloat(oilMass);
						this.calculatorOilMass(R);

						log.info(
								this.getDeviceSN() + " 当前油耗电阻值为：" + oilMass
										+ "欧姆");

					} else if (extCmd.equals("0003")) {// 温度数据

						int secLen = tempData.length() / 4;
						int i = 0;
						while (i < secLen) {
							String s = tempData.substring(i * 4, i * 4 + 4);
							if (s.equalsIgnoreCase("FF00")) {
								log.info(
										this.getDeviceSN() + "第" + i
												+ "路未接入温感探头。");
							} else {
								float dtemp = 0.0f;
								String intPart = s.substring(0, 2);
								String decPart = s.substring(2);

								int tempInt = Integer.parseInt(intPart, 16);
								int tempDec = Integer.parseInt(decPart, 16);

								if (tempInt >= Integer.parseInt("80", 16)) {
									tempInt = -(tempInt - Integer.parseInt(
											"80", 16));
								}
								dtemp = Float.parseFloat(tempInt + "."
										+ tempDec);
								this.termStatus.setTemperator(dtemp);
								this.termStatus.setTemeratureRouteNum(i + "");
								log.info(
										this.getDeviceSN() + "上传的温度为：" + dtemp
												+ "度，传感路为：" + i);
							}
							i++;
						}

					} else if (extCmd.equals("0088")) {// 里程数据

						float lc = Integer.parseInt(tempData, 16) / 1000f;
						this.setMileage(lc + "");
						log.info(
								this.getDeviceSN() + " 扩展里程为：" + lc + "公里");
					}

				}
			}

			repCmdId = hexString.substring(84, 86);// 主命令ID

			// 在85指令后设置ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + repCmdId,
			// "0");
 

		}

		this.setStatusRecord(termStatus);
		log.info(
				this.getDeviceSN() + "龙翰终端位置数据：x=" + this.getCoordX() + ",y="
						+ this.getCoordY() + ",s=" + this.getSpeed()
						+ ",dirction=" + this.getDirection() + ",distance="
						+ this.getMileage() + ",time=" + this.getTime());
		log.info(
				this.getDeviceSN() + "状态：" + resSt + "," + resSt1 + ","
						+ resSt2 + "," + resSt3 + "," + resSt4);
		log.info(
				this.getDeviceSN() + "参数，定位发送时间=" + locTime + ",停车设置时间="
						+ stopCartime + ",超速设置阀值=" + overSpeedTime + ",区域个数="
						+ areaCount + ",拍照间隔：" + imageInterval + "*30"

		);

	}

	public void calculatorOilMass(float r) {
		ArrayList<TOilboxCoefficient> cache = OilReferCoefficient.getInstance()
				.getOilCoefficient(this.getDeviceSN());
		if (cache == null || cache.size() <= 0) {
			OilReferCoefficient.getInstance().loadOilCoefficient(
					this.getDeviceSN());
			cache = OilReferCoefficient.getInstance().getOilCoefficient(
					this.getDeviceSN());
		}
		if (cache != null && cache.size() > 0) {
			log.info(
					this.getDeviceSN() + " 油耗特征系数表个数：" + cache.size());

			// 按电阻值先排序
			java.util.Collections.sort(cache, new Comparator() {
				public int compare(Object o1, Object o2) {
					TOilboxCoefficient p1 = (TOilboxCoefficient) o1;
					TOilboxCoefficient p2 = (TOilboxCoefficient) o2;
					if (p1.getResistanceE() > p2.getResistanceE()) {
						return 1;
					} else if (p1.getResistanceE() < p2.getResistanceE()) {
						return -1;
					} else {
						return 0;
					}
				}
			});
			float minR = 0.0F;
			float minOil = 0.0f;
			float maxR = 0.0f;
			float maxOil = 0.0f;
			float capacity = 0.0f;

			boolean isFirst = false; // 是否只是比较了第一组系数

			for (int i = 0; i < cache.size(); i++) {
				TOilboxCoefficient obj = cache.get(i);
				log.info(
						this.getDeviceSN() + "缓存中油耗系数,编号="
								+ obj.getCoefficientNum()
								+ " :(小端电阻，大端电阻，油耗值)=》(" + obj.getResistanceS()
								+ "," + obj.getResistanceE() + ","
								+ obj.getOilMass() + "),油箱容积："
								+ obj.getTankCapacity());
				float r1 = obj.getResistanceE();
				capacity = obj.getTankCapacity();

				if (r1 < r || r1 == r) {
					minR = r1;
					maxOil = obj.getOilMass();
				}
				if (minR == 0.0f) {
					// 只比较了最小边界的一组
					minR = obj.getResistanceS();
					maxOil = obj.getTankCapacity();
				}
				if (r1 > r) {
					maxR = r1;
					minOil = obj.getOilMass();
					break;
				}
			}

			log.info(
					this.getDeviceSN() + " minR=" + minR + ",maxR=" + maxR
							+ ",minOil=" + minOil + ",maxOil=" + maxOil
							+ ",capacity=" + capacity);
			if (maxOil < minOil) {
				Log
						.getInstance()
						.outLog(
								this.getDeviceSN()
										+ " 设置的油量系数有问题或者设置的油量阀值超过了油箱容积,前一组参考油量值应大于后一组参考油量值");
			}

			String formula = "maxOil + ((minOil - maxOil ) / (maxR - minR)) * (r - minR);";
			log.info("油量计算公式：" + formula);

			float oilMass = 0.0f;

			oilMass = maxOil + ((minOil - maxOil) / (maxR - minR)) * (r - minR);// 转换得到油量
			if (oilMass > capacity) {// 超过油箱容量时用油箱容量表示
				oilMass = capacity;
			}
			if (r > maxR) {
				log.info(
						this.getDeviceSN() + " 油箱油量耗尽,maxR=" + maxR + ",curR="
								+ r);
				oilMass = 0.0f;
			}

			this.termStatus.setOilMass(oilMass);

			float preOilMass = OilReferCoefficient.getInstance().getCurOilMass(
					this.getDeviceSN());
			float oilUsed = preOilMass - oilMass;// 两次上报间隔之间耗油量
			if (oilUsed < 0) {
				float oilAdded = Math.abs(oilUsed);// 第一次上报油箱油量【加油量】
				this.termStatus.setOilAdded(oilAdded);
			}
			if (preOilMass == 0)
				oilUsed = Math.abs(oilUsed);
			this.termStatus.setOilUsed(oilUsed);

			OilReferCoefficient.getInstance().setCurOilMass(this.getDeviceSN(),
					oilMass);

			log.info(
					this.getDeviceSN() + " 转换得到的油箱油量：" + oilMass + "L,间隔耗油="
							+ oilUsed + "L,加油量="
							+ this.termStatus.getOilAdded() + "L");
		}

	}

	// 解析上行调度信息
	private void parseMessage(byte[] cont, byte verfyCode, byte bcmd,
			byte subCmd) {

		byte[] bmsg = new byte[cont.length - 11];
		System.arraycopy(cont, 9, bmsg, 0, bmsg.length);

		String msg = "";

		try {
			// msg = new String(bmsg,"GB2312");
			if (this.deviceType != null
					&& this.deviceType.equals("GP-LONGHAN-GPRS")) {
				msg = new String(bmsg, "GB18030");
			} else {
				msg = new String(bmsg, "GB18030");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (msg.startsWith("Gps Type:")) {
			String ver = msg;
			int i = msg.indexOf("Gps Type:") + "Gps Type:".getBytes().length;
			String type = ver.substring(i, ver.indexOf("NEW"));
			String pver = ver.substring(ver.indexOf("Program:") + 8, ver
					.indexOf("Time:"));
			String t = ver.substring(ver.indexOf("Time:") + 5, ver
					.indexOf("Time:") + 5 + 10);
			try {
				this.termStatus.setCpu(type + " " + pver + " " + t);
				this.setStatusRecord(termStatus);
				log.info(
						this.getDeviceSN() + " 版本信息：" + ver);
				log.info(
						this.getDeviceSN() + " version："
								+ this.termStatus.getCpu()); 
				return;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
 
			String date = Tools.formatDate2Str(new Date(),
					"yyyy-MM-dd HH:mm:ss");
			this.setTime(date);
			// if (this.getCmdId().equals("3d"))
			log.info(this.getDeviceSN() + "上行了调度信息：" + msg);

 
		}

	}

	// 解析报警特殊处理-大陆版
	private void parseSpecialAlarm(String hexString, byte verfyCode, byte bcmd,
			byte subCmd, int type) {

		this.parsePosition(hexString, verfyCode, bcmd, subCmd, type);
		String s = "";

		String statusHex = hexString.substring(30 * 2);

		String st1 = statusHex.substring(0, 2);

		int ist1 = Tools.byte2Int(Tools.fromHexString(st1));
		st1 = this.buZero(Integer.toBinaryString(ist1), 8);
		if (st1.charAt(0) == '1') {
			// 如区域
			s += "入区域，";
			this.setAlarmType(AlarmType.AREA_ALARM_TYPE);
			this.setAlarmSubType("1");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st1.charAt(1) == '1') {
			// 如区域
			s += "出区域，";
			this.setAlarmType(AlarmType.AREA_ALARM_TYPE);
			this.setAlarmSubType("0");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st1.charAt(7) == '1') {
			// 如区域
			s += "非法启动，";
		}

		String st2 = statusHex.substring(2, 4);
		int ist2 = Tools.byte2Int(Tools.fromHexString(st2));
		st2 = this.buZero(Integer.toBinaryString(ist2), 8);
		if (st2.charAt(0) == '1') {
			// 如区域
			s += "非法启动，";
		}
		if (st2.charAt(1) == '1') {
			// 如区域
			s += "拖车报警，";
			this.setAlarmType(AlarmType.DRAG_HANG_ALARM_TYPE);

			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st2.charAt(2) == '1') {
			// 如区域
			s += "震动报警，";
			this.setAlarmType(AlarmType.COLLIDE_ALARM_TYPE);

			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st2.charAt(3) == '1') {
			// 如区域
			s += "网关报警，";
		}

		if (st2.charAt(4) == '1') {
			// 如区域
			s += "断电报警，";
			this.setAlarmType(AlarmType.CUT_POWER_ALARM_TYPE);

			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st2.charAt(5) == '1') {
			// 如区域
			s += "停车报警，";
			this.setAlarmType(AlarmType.STOP_CAR_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}

		if (st2.charAt(6) == '1') {
			// 如区域
			s += "超速报警，";
			this.setAlarmType(AlarmType.SPEED_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st2.charAt(7) == '1') {
			// 如区域
			s += "应急报警,";
		}

		String st3 = statusHex.substring(4, 6);
		int ist3 = Tools.byte2Int(Tools.fromHexString(st3));
		st3 = this.buZero(Integer.toBinaryString(ist3), 8);
		if (st3.charAt(0) == '1') {
			s += "SIM被折报警";
		}
		this.setStatusRecord(termStatus);
		String areaNum = hexString.substring(hexString.length() - 14, hexString
				.length() - 12);
		int iareaNum = Tools.byte2Int(Tools.fromHexString(areaNum));
		s += "区域编号=" + iareaNum;
		log.info(this.getDeviceSN() + "特殊报警信息：" + s);
	}

	// 香港版
	private void parseSpecialAlarmHK(String hexString, byte verfyCode,
			byte bcmd, byte subCmd, int type) {

		SensorBean sensor = DeviceSensorCache.getInstance().getDeviceSensor(
				this.getDeviceSN());
		String method = null;
		HashMap<String, String> sensorMethod = null;
		if (sensor != null) {
			sensorMethod = sensor.getSensorMap();
			log.info("从缓存获取传感方案：" + sensor.toString());
		}

		this.parsePosition(hexString, verfyCode, bcmd, subCmd, type);
		String s = "";
		String statusHex = hexString.substring(43 * 2);

		String st1 = statusHex.substring(0, 2);

		int ist1 = Tools.byte2Int(Tools.fromHexString(st1));
		st1 = this.buZero(Integer.toBinaryString(ist1), 8);
		if (st1.charAt(0) == '1') {
			// 如区域
			s += "入区域，";
			this.setAlarmType(AlarmType.AREA_ALARM_TYPE);
			this.setAlarmSubType("1");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st1.charAt(1) == '1') {
			// 如区域
			s += "出区域，";
			this.setAlarmType(AlarmType.AREA_ALARM_TYPE);
			this.setAlarmSubType("0");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st1.charAt(2) == '1') {
			// 如区域
			s += "主电源正常，";
		}
		if (st1.charAt(3) == '1') {
			// 如区域
			s += "主电源掉电，";
			this.setAlarmType(AlarmType.CUT_POWER_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st1.charAt(4) == '1') {
			// 如区域
			s += "主电源过低，";
			this.setAlarmType(AlarmType.LACK_PRESSURE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st1.charAt(5) == '1') {
			// 如区域
			s += "主电源过高，";
		}
		if (st1.charAt(6) == '1') {
			// 如区域
			s += "ACC开，";
		}
		if (st1.charAt(7) == '1') {
			// 如区域
			s += "ACC关，";
		}

		String st2 = statusHex.substring(2, 4);
		int ist2 = Tools.byte2Int(Tools.fromHexString(st2));
		st2 = this.buZero(Integer.toBinaryString(ist2), 8);
		if (st2.charAt(0) == '1') {
			// 如区域
			s += "自定义高3路  状态为高（对应界面上第一路），";
			if (sensorMethod != null)
				method = sensorMethod.get("1");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "1");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance()
						.errorLog(
								this.getDeviceSN()
										+ " 执行自定义高3路  状态为高（对应界面上第一路）状态异常", e);
				e.printStackTrace();
			}

		}
		if (st2.charAt(1) == '1') {
			// 如区域
			s += "自定义高3路  状态为低（第一路），";
			if (sensorMethod != null)
				method = sensorMethod.get("1");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "0");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义高3路  状态为低（第一路）状态异常", e);
				e.printStackTrace();
			}
		}
		if (st2.charAt(2) == '1') {
			// 如区域
			s += "自定义低3路  状态为低（第二路），";
			if (sensorMethod != null)
				method = sensorMethod.get("2");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "0");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义低3路  状态为低（第二路）状态异常", e);
				e.printStackTrace();
			}
		}
		if (st2.charAt(3) == '1') {
			// 如区域
			s += "自定义低3路  状态为高（第2路），";
			if (sensorMethod != null)
				method = sensorMethod.get("2");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "1");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义低3路  状态为高（第2路）状态异常", e);
				e.printStackTrace();
			}

		}

		if (st2.charAt(4) == '1') {
			// 如区域
			s += "油量骤减报警，";
			this.setAlarmType(AlarmType.ORI_REDUCE_ALAMR_TYPE);

			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st2.charAt(5) == '1') {
			// 如区域
			s += "停车报警，";
			this.setAlarmType(AlarmType.STOP_CAR_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}

		if (st2.charAt(6) == '1') {
			// 如区域
			s += "超速报警，";
			this.setAlarmType(AlarmType.SPEED_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st2.charAt(7) == '1') {
			// 如区域
			s += "应急报警,";
		}

		String st3 = statusHex.substring(4, 6);
		int ist3 = Tools.byte2Int(Tools.fromHexString(st3));
		st3 = this.buZero(Integer.toBinaryString(ist3), 8);
		if (st3.charAt(0) == '1') {
			s += "自定义高1路  状态为高（第3路）,";
			if (sensorMethod != null)
				method = sensorMethod.get("3");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "1");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义高1路  状态为高（第3路）状态异常", e);
				e.printStackTrace();
			}
		}
		if (st3.charAt(1) == '1') {
			s += "自定义高1路  状态为低（第3路）,";
			if (sensorMethod != null)
				method = sensorMethod.get("3");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "0");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义高1路  状态为低（第3路）状态异常", e);
				e.printStackTrace();
			}
		}
		if (st3.charAt(2) == '1') {
			s += "自定义低1路  状态为低（第4路）,";
			if (sensorMethod != null)
				method = sensorMethod.get("4");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "0");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义低1路  状态为低（第4路）状态异常", e);
				e.printStackTrace();
			}
		}
		if (st3.charAt(3) == '1') {
			s += "自定义低1路  状态为高（第4路）,";
			if (sensorMethod != null)
				method = sensorMethod.get("4");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "0");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义低1路  状态为高（第4路）状态异常", e);
				e.printStackTrace();
			}
		}
		if (st3.charAt(4) == '1') {
			s += "自定义高2路  状态为高（第5路）,";
			if (sensorMethod != null)
				method = sensorMethod.get("5");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "1");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义高2路  状态为高（第5路）状态异常", e);
				e.printStackTrace();
			}
		}
		if (st3.charAt(5) == '1') {
			s += "自定义高2路  状态为低（第5路）,";
			if (sensorMethod != null)
				method = sensorMethod.get("5");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "0");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义高2路  状态为低（第5路）状态异常", e);
				e.printStackTrace();
			}
		}
		if (st3.charAt(6) == '1') {
			s += "自定义低2路  状态为低（第6路）,";
			if (sensorMethod != null)
				method = sensorMethod.get("6");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "0");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义低2路  状态为低（第6路）状态异常", e);
				e.printStackTrace();
			}
		}
		if (st3.charAt(7) == '1') {
			s += "自定义低2路  状态为高（第6路）,";
			if (sensorMethod != null)
				method = sensorMethod.get("6");
			s += ",对应的状态方法：" + method;
			try {
				Class c = this.termStatus.getClass();
				Method m = c.getMethod(method, String.class);
				m.invoke(this.termStatus, "1");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Log.getInstance().errorLog(
						this.getDeviceSN() + " 执行自定义低2路  状态为高（第6路）状态异常", e);
				e.printStackTrace();
			}
		}
		String st4 = hexString.substring(hexString.length() - 14, hexString
				.length() - 12);
		int ist4 = Tools.byte2Int(Tools.fromHexString(st4));
		st4 = this.buZero(Integer.toBinaryString(ist4), 8);
		if (st4.charAt(0) == '1') {
			s += "登签,";
			this.termStatus.setLoginOut("1");
		}
		if (st4.charAt(1) == '1') {
			s += "退签,";
			this.termStatus.setLoginOut("0");
		}
		if (st4.charAt(2) == '1') {

		}
		if (st4.charAt(3) == '1') {
			s += "车辆盗警,";
			this.setAlarmType(AlarmType.SECURITY_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st4.charAt(4) == '1') {
			s += "GPRS上线,";
		}
		if (st4.charAt(5) == '1') {
			s += "GPRS断线,";
		}
		if (st4.charAt(6) == '1') {
			s += "GPS天线故障,";
			this.setAlarmType(AlarmType.GPS_ANTENNA_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (st4.charAt(7) == '1') {
			s += "GPS天线正常,";
		}

		String areaNum = hexString.substring(hexString.length() - 12, hexString
				.length() - 10);
		int iareaNum = Tools.byte2Int(Tools.fromHexString(areaNum));
		s += "区域编号=" + iareaNum;
		log.info(this.getDeviceSN() + "LH-HK 特殊报警信息：" + s);
		this.setStatusRecord(termStatus);
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
			this.termStatus.setLocate("1");
			ret += "1:1;";
		} else {
			sbuf.append("未定位，");
			this.setLocateStatus("0");
			this.termStatus.setLocate("0");
			ret += "1:0;";
			// lbs buchang
		}
		try {
			String locStatus = this.getLocateStatus();
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

		if (status.charAt(1) == '1' && status.charAt(2) == '1') {
			sbuf.append("GPS正常，");
			this.termStatus.setAntenna("3");
			ret += "2:3;";
		} else if (status.charAt(1) == '1' && status.charAt(2) == '0') {
			sbuf.append("GPS短路，");
			this.termStatus.setAntenna("0");
			ret += "2:2;";
			this.setAlarmType(AlarmType.GPS_MAST_SHORT_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		} else if (status.charAt(1) == '0' && status.charAt(2) == '1') {
			sbuf.append("GPS开路，");
			ret += "2:1;";
			this.termStatus.setAntenna("1");
			this.setAlarmType(AlarmType.GPS_MAST_OPEN_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);

		} else if (status.charAt(1) == '0' && status.charAt(2) == '0') {
			if (this.deviceType != null
					&& !this.deviceType.equals("GP-LH-HK-GPRS")) {
				sbuf.append("GPS故障，");
				this.termStatus.setAntenna("2");
				ret += "2:0;";
				this.setAlarmType(AlarmType.GPS_MODEL_FAIL_ALARM_TYPE);
				AlarmQueue.getInstance().addAlarm(this);
			}
		}
		if (this.deviceType != null && !this.deviceType.equals("GP-LH-HK-GPRS")) {
			if (status.charAt(3) == '1' && status.charAt(4) == '1') {
				sbuf.append("电源正常，");
				this.termStatus.setMainPower("3");
				ret += "3:2;";
			} else if (status.charAt(3) == '1' && status.charAt(4) == '0') {
				sbuf.append("主电掉电，");
				this.termStatus.setMainPower("0");
				// 断电报警
				this.setAlarmType(AlarmType.CUT_POWER_ALARM_TYPE); 
				ret += "3:0;";
				// 增加断电报警到报警队列
				AlarmQueue.getInstance().addAlarm(this);
			} else if (status.charAt(3) == '0' && status.charAt(4) == '1') {
				sbuf.append("主电源过低，");
				this.termStatus.setMainPower("2");
				ret += "3:1;";
				this.setAlarmType(AlarmType.LACK_PRESSURE_ALARM_TYPE);
				AlarmQueue.getInstance().addAlarm(this);
			} 
			String loginBin = status.substring(5);
			this.longid = Tools.int2Hexstring(Integer.parseInt(loginBin, 2), 2);
			log.info(
					this.getDeviceSN() + "状态1：" + sbuf.toString() + ",登签ID1:"
							+ this.longid);
		} else {
			if (status.charAt(3) == '1' && status.charAt(4) == '1'
					&& status.charAt(5) == '1') {
				sbuf.append("电源正常，");
				this.termStatus.setMainPower("3");
				ret += "3:2;";
			} else if (status.charAt(4) == '0') {
				sbuf.append("主电掉电，");
				this.termStatus.setMainPower("0");
				// 断电报警
				this.setAlarmType(AlarmType.CUT_POWER_ALARM_TYPE); 
				ret += "3:0;";
				// 增加断电报警到报警队列
				AlarmQueue.getInstance().addAlarm(this);
			} else if (status.charAt(5) == '0') {
				sbuf.append("主电源过高，");
				this.termStatus.setMainPower("1");
				ret += "3:1;";
			} else if (status.charAt(3) == '0') {
				sbuf.append("主电源过过低，");
				this.termStatus.setMainPower("2");
				ret += "3:1;";
				this.setAlarmType(AlarmType.LACK_PRESSURE_ALARM_TYPE);
				AlarmQueue.getInstance().addAlarm(this);
			}
		}
		log.info(
				this.getDeviceSN() + " 基本状态：" + sbuf.toString());
		return ret;

	}

	public String parseStatus1(String binay) {
		String ret = "";
		StringBuilder sbuf = new StringBuilder();
		SensorBean sensor = DeviceSensorCache.getInstance().getDeviceSensor(
				this.getDeviceSN());

		HashMap<String, String> sensorMethod = null;
		if (sensor != null)
			sensorMethod = sensor.getSensorMap();

		if (binay.charAt(0) == '0') {
			sbuf.append("ACC开，");
			this.termStatus.setAcc("1");
			ret += "4:0;";
		} else {
			this.termStatus.setAcc("0");
			sbuf.append("ACC关，");
			ret += "4:1;";
		}
		if (binay.charAt(1) == '0') {
			// sbuf.append("自定义1路高传感器状态为高，");
			// String method = sensorMethod.get("1");
			// sbuf.append(",对应的状态方法："+method);
			// try {
			// Class c = this.termStatus.getClass();
			// Method m = c.getMethod(method, String.class);
			// m.invoke(c, "0");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		} else {
			// sbuf.append("自定义1路高传感器状态为低，");
			// String method = sensorMethod.get("1");
			// sbuf.append(",对应的状态方法："+method);
			// try {
			// Class c = this.termStatus.getClass();
			// Method m = c.getMethod(method, String.class);
			// m.invoke(c, "0");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
		if (binay.charAt(2) == '0') {
			// sbuf.append("自定义2路高传感器状态为高，");
			// String method = sensorMethod.get("2");
			// sbuf.append(",对应的状态方法："+method);
			// try {
			// Class c = this.termStatus.getClass();
			// Method m = c.getMethod(method, String.class);
			// m.invoke(c, "0");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		} else {
			// sbuf.append("自定义2路高传感器状态为低，");
			// String method = sensorMethod.get("2");
			// sbuf.append(",对应的状态方法："+method);
			// try {
			// Class c = this.termStatus.getClass();
			// Method m = c.getMethod(method, String.class);
			// m.invoke(c, "0");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
		if (binay.charAt(3) == '0') {
			// sbuf.append("自定义1路低传感器状态为低，");
			// String method = sensorMethod.get("1");
			// sbuf.append(",对应的状态方法："+method);
			// try {
			// Class c = this.termStatus.getClass();
			// Method m = c.getMethod(method, String.class);
			// m.invoke(c, "0");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

		} else {
			// sbuf.append("自定义1路低传感器状态为高，");
			// String method = sensorMethod.get("1");
			// sbuf.append(",对应的状态方法："+method);
			// try {
			// Class c = this.termStatus.getClass();
			// Method m = c.getMethod(method, String.class);
			// m.invoke(c, "0");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
		if (binay.charAt(4) == '0') {
			// sbuf.append("自定义2路低传感器状态为低，");
			// String method = sensorMethod.get("2");
			// sbuf.append(",对应的状态方法："+method);
			// try {
			// Class c = this.termStatus.getClass();
			// Method m = c.getMethod(method, String.class);
			// m.invoke(c, "0");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		} else {
			// sbuf.append("自定义2路低传感器状态为高，");
			// String method = sensorMethod.get("2");
			// sbuf.append(",对应的状态方法："+method);
			// try {
			// Class c = this.termStatus.getClass();
			// Method m = c.getMethod(method, String.class);
			// m.invoke(c, "0");
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
		if (binay.charAt(5) == '0') {
			sbuf.append("油路断开，");
			this.isCutOil = true;
			this.termStatus.setOilElec("0");
			ret += "5:0;";
		} else {
			sbuf.append("油路正常，");
			this.termStatus.setOilElec("1");
			ret += "5:1;";
		}

		if (binay.charAt(6) == '0') {
			sbuf.append("已登签，");
			this.isLogin = true;
			this.termStatus.setLoginOut("1");

		} else {
			sbuf.append("没有登签，");
			this.termStatus.setLoginOut("0");

		}
		if (this.deviceType.equals("GP-BOYUAN-CAR-GPRS")) {
			if (binay.charAt(7) == '0') {
				sbuf.append("超速提示控制模式，");
			} else {
				sbuf.append("油路控制模式，");
			}
		} else if (this.deviceType != null
				&& !this.deviceType.equals("GP-LH-HK-GPRS")) {
			if (binay.charAt(7) == '0') {
				sbuf.append("已设防，");
				this.termStatus.setFortification("1");
			} else {
				sbuf.append("未设防，");
				this.termStatus.setFortification("0");
			}
		}
		this.setStatusRecord(termStatus); 
		log.info(
				this.getDeviceSN() + "状态2：" + sbuf.toString());
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
		if (binay.charAt(2) == '0') {
			sbuf.append("停车超长报警，");
			this.setAlarmType(AlarmType.STOP_CAR_ALARM_TYPE);
			this.setAlarmSubType("1");
			AlarmQueue.getInstance().addAlarm(this);

		}
		if (this.deviceType != null
				&& !this.deviceType.equals("GP-BOYUAN-CAR-GPRS")) {
			if (binay.charAt(3) == '0') {
				sbuf.append("驶出区域报警，");
				this.setAlarmType(AlarmType.AREA_ALARM_TYPE);
				this.setAlarmSubType("0");
				AlarmQueue.getInstance().addAlarm(this);
			}
			if (binay.charAt(4) == '0') {
				sbuf.append("驶入区域报警，");
				this.setAlarmType(AlarmType.AREA_ALARM_TYPE);
				this.setAlarmSubType("1");
				AlarmQueue.getInstance().addAlarm(this);
				this.setAlarmType("3");
			}
			if (binay.charAt(5) == '0') {
				sbuf.append("看车密码错误报警，");
			}
			if (binay.charAt(6) == '0') {
				sbuf.append("GPRS已上线，");
				ret += "6:3;";
			} else {
				sbuf.append("GPRS未上线，");
				ret += "6:2;";
			}
			// 常闭继电器状态为闭合
			if (this.deviceType != null
					&& !this.deviceType.equals("GP-LH-HK-GPRS")) {
				if (binay.charAt(7) == '0') {
					sbuf.append("终端拨号成功，");
				} else {
					sbuf.append("终端拨号未成功，");
				}
			} else {
				if (binay.charAt(7) == '0') {
					sbuf.append("常闭继电器状态为闭合，");
				} else {
					sbuf.append("常闭继电器状态为断开，");
				}
			}
		} 
		log.info(
				this.getDeviceSN() + "状态3：" + sbuf.toString());
		return ret;
	}

	public String parseStatus3(String hex, String binay, byte revVerfyCode,
			byte MainCmdid, byte SubId) {
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

			sbuf.append("中心需下发21指令，");
		}
		// if (binay.charAt(2) == '0') {
		// sbuf.append("UDP通讯方式，");
		// } else {
		// sbuf.append("TCP通讯方式，");
		// } 
		log.info(
				this.getDeviceSN() + "状态4：" + sbuf.toString());
		return ret;
	}

	public String parseStatus4(String binay) {
		StringBuilder sbuf = new StringBuilder();
		// if (binay.charAt(0) == '1') {
		// sbuf.append("手柄没接入，");
		// } else {
		// sbuf.append("手柄已接入，");
		SensorBean sensor = DeviceSensorCache.getInstance().getDeviceSensor(
				this.getDeviceSN());

		HashMap<String, String> sensorMethod = null;
		if (sensor != null)
			sensorMethod = sensor.getSensorMap();

		// }
		if (this.deviceType != null && !this.deviceType.equals("GP-LH-HK-GPRS")) {
			if (binay.charAt(1) == '1') {
				sbuf.append(" 手柄或显示屏已接入，");
				this.termStatus.setDisplay("1");
				this.termStatus.setHandle("1");
			} else {
				this.termStatus.setDisplay("0");
				this.termStatus.setHandle("0");
				sbuf.append(" 手柄或显示屏没接入，");
			}
			// if (binay.charAt(2) == '1') {
			// sbuf.append("图像采集器没接入，");
			// } else {
			// sbuf.append("图像采集器已接入，");
			// }
			// if (binay.charAt(3) == '1') {
			// sbuf.append("计价器没接入，");
			// } else {
			// sbuf.append("计价器已接入，");
			// }
			// if (binay.charAt(4) == '1') {
			// sbuf.append("语音波号器器没接入，");
			// } else {
			// sbuf.append("语音波号器器已接入，");
			// }
		} else {
			if (binay.charAt(1) == '1') {
				// sbuf.append(" 自定义3路低传感器状态为高，");
				// String method = sensorMethod.get("1");
				// sbuf.append(",对应的状态方法："+method);
				// try {
				// Class c = this.termStatus.getClass();
				// Method m = c.getMethod(method, String.class);
				// m.invoke(c, "0");
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			} else {
				// sbuf.append(" 自定义3路低传感器状态为低，");
				// String method = sensorMethod.get("1");
				// sbuf.append(",对应的状态方法："+method);
				// try {
				// Class c = this.termStatus.getClass();
				// Method m = c.getMethod(method, String.class);
				// m.invoke(c, "0");
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
			if (binay.charAt(2) == '1') {
				// sbuf.append("自定义3路高传感器状态为低，");
				// String method = sensorMethod.get("1");
				// sbuf.append(",对应的状态方法："+method);
				// try {
				// Class c = this.termStatus.getClass();
				// Method m = c.getMethod(method, String.class);
				// m.invoke(c, "0");
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			} else {
				// sbuf.append("自定义3路高传感器状态为高，");
				// String method = sensorMethod.get("1");
				// sbuf.append(",对应的状态方法："+method);
				// try {
				// Class c = this.termStatus.getClass();
				// Method m = c.getMethod(method, String.class);
				// m.invoke(c, "0");
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
			if (binay.charAt(3) == '1') {
				// sbuf.append("一路高电平输出线状态为高，");
				// String method = sensorMethod.get("1");
				// sbuf.append(",对应的状态方法："+method);
				// try {
				// Class c = this.termStatus.getClass();
				// Method m = c.getMethod(method, String.class);
				// m.invoke(c, "0");
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			} else {
				// sbuf.append("一路高电平输出线状态为低，");
				// String method = sensorMethod.get("1");
				// sbuf.append(",对应的状态方法："+method);
				// try {
				// Class c = this.termStatus.getClass();
				// Method m = c.getMethod(method, String.class);
				// m.invoke(c, "0");
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
			if (binay.charAt(4) == '1') {
				// sbuf.append("二路高电平输出线状态为高，");
				// String method = sensorMethod.get("2");
				// sbuf.append(",对应的状态方法："+method);
				// try {
				// Class c = this.termStatus.getClass();
				// Method m = c.getMethod(method, String.class);
				// m.invoke(c, "0");
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }

			} else {
				// sbuf.append("二路高电平输出线状态为低，");
				// String method = sensorMethod.get("2");
				// sbuf.append(",对应的状态方法："+method);
				// try {
				// Class c = this.termStatus.getClass();
				// Method m = c.getMethod(method, String.class);
				// m.invoke(c, "0");
				// } catch (Exception e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
			}
		}
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
		log.info(
				this.getDeviceSN() + " 状态5" + sbuf.toString());
		return sbuf.toString();
	}

	// 回复终端
	private byte[] reply2Terminal(String hex, byte revVerfyCode,
			byte MainCmdid, byte subCmd) {

		byte[] ret = new byte[10];

		String head = "2929" + "210005";
		byte[] hb = Tools.fromHexString(head);
		System.arraycopy(hb, 0, ret, 0, 5);

		ret[5] = revVerfyCode;
		ret[6] = MainCmdid;
		ret[7] = subCmd;

		byte verfyCode = Tools.checkData(ret);
		ret[8] = verfyCode;

		ret[9] = (byte) 0x0D;

		log.info(
				"中心应答：" + Tools.bytesToHexString(ret) + ",收到数据：" + hex);
		return ret;
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

	// 保存入油数据
	private void saveInOilData(String deviceId, String order, int num, int mil,
			float cost, String ic) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement pst = null;
		String sql = "insert into e_oil(device_id,oil_order,oil_number,oil_mileage,oil_cost,driver_ic,upload_time,input_time) ";
		sql += " values(?,?,?,?,?,?,GETDATE(),GETDATE())";

		try {

			pst = conn.prepareStatement(sql);
			pst.setString(1, deviceId);
			pst.setString(2, order);
			pst.setInt(3, num);
			pst.setInt(4, mil);
			pst.setFloat(5, cost);
			pst.setString(6, ic);
			pst.execute();
			conn.commit();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getInstance().errorLog("插入油量异常", e);
			e.printStackTrace();
		} finally {
			DbOperation.release(null, null, pst, null, conn);
		}

	}

	private String findEtermSimcard(String deviceId) {
		Connection conn = DbUtil.getConnection();
		PreparedStatement pst = null;
		ResultSet rs = null;
		String simcard = null;

		String sql = "SELECT * FROM E_TERMINAL  E WHERE E.DEVICE_ID=?";

		try {

			pst = conn.prepareStatement(sql);
			pst.setString(1, deviceId);
			rs = pst.executeQuery();
			if (rs.next()) {
				simcard = rs.getString("SIMCARD_NO_1");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getInstance().errorLog("插入油量异常", e);
			e.printStackTrace();
		} finally {
			DbOperation.release(null, rs, pst, null, conn);
		}
		return simcard;
	}

 

	public static void main(String[] args) {
		// 香港34字节 大陆21字节
		ParseLongHan lh = new ParseLongHan();
		lh
				.parseGPRS("29298000280b9c082d0211070800000395892611618007000000007b0002bc7dfc78ff000f003200670421740d");

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
