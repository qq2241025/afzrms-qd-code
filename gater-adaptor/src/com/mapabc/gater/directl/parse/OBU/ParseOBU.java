package com.mapabc.gater.directl.parse.OBU;

import java.awt.image.BufferStrategy;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.constant.AlarmType;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.gater.directl.encode.PropertyReader;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.lbsgateway.GBLTerminalList; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.lbsgateway.service.CommonGatewayServiceImpl;
import com.mapabc.gater.lbsgateway.service.ICommonGatewayService;
import com.mapabc.gater.util.HttpUtil;

 

public class ParseOBU extends ParseBase implements ParseService {
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseOBU.class);
	
	TTermStatusRecord termStatus = new TTermStatusRecord();

	private static String isOverLoad = null;// 是否有负载
	private PropertyReader proRead;
	TTerminal term = null;

	public ParseOBU() {

	}

	public void parseGPRS(String hexString) {

		log.info("OBU上传数据--------------->" + hexString);

		byte messageId = Tools.fromHexString(hexString.substring(0, 2))[0];
		Socket socketAddress = this.getSocket();
		String deviceId = null;
 

		// byte[] heart = this.reply("a2");
		// this.setReplyByte2(heart);

		// 上传终端信息，包括ID、协议版本
		if (messageId == (byte) 0xD0) {
			int count = hexString.length() / 22;
			for (int i = 0; i < count; i++) {
				if (this.getDeviceSN() == null) {
					String hexTmp = hexString.substring(0, 22);
					log.info(
							"d0上传数据--------------->" + hexTmp);

					String protocolVersion = hexTmp.substring(2, 6);

					String id = hexTmp.substring(6, 14);
					byte[] idbyte = Tools.convertBytePos(Tools
							.fromHexString(id));
					if (this.getDeviceSN() == null
							&& !hexTmp.equals("d0ec030000010000000000")) {
						String deviceSN = Tools.byte2Int(idbyte) + "";
						this.setDeviceSN(deviceSN);
					}

					String manufactureId = hexTmp.substring(14, 22);
					byte[] midbyte = Tools.convertBytePos(Tools
							.fromHexString(manufactureId));
					String mfid = Tools.byte2Int(midbyte) + "";
					log.info(
							"OBU上传ID、协议版本信息---->协议版本：" + protocolVersion
									+ " 终端Id：" + this.getDeviceSN() + " 厂商Id号："
									+ mfid);
				}
				String repHex = "8503d001";
				byte[] ogilData = Tools.fromHexString("d001");

				byte vercode = Tools.checkData(ogilData);

				repHex += Tools.bytesToHexString(new byte[] { vercode });
				this.setReplyByte(Tools.fromHexString(repHex));
				this.setReplyByte1(Tools.fromHexString(repHex));

				log.info("OBU登陆回应： " + repHex);

			}
		} else {

			term = GBLTerminalList.getInstance().getTerminaInfo(
					this.getDeviceSN());
			// 从内存获取终端SIMCARD
			if (term == null) {
				log.info(
						"系统中没有适配到指定的终端：device_id=" + this.getDeviceSN());
				// return;
			} else {

				//this.setObjId(term.getObjId());
				//this.setObjType(term.getObjType());
				this.setPhnum(term.getSimcard());
			}
			switch (messageId) {

			case (byte) 0xb9:// 开通终端
				String messageLen = hexString.substring(2, 4);
				String imei = hexString.substring(4, 34);
				String strImei = new String(Tools.fromHexString(imei));

				this.setReplyByte(this.reply("b9"));
				log.info(
						"OBU上传开通终端信息---->数据长度：" + messageLen + "IMEI:"
								+ strImei + " 中心回复："
								+ Tools.bytesToHexString(this.reply("b9")));
				break;
			case (byte) 0x86:// 上传消息处理结果
				String messageLen1 = hexString.substring(2, 4);
				String messageId1 = hexString.substring(4, 6);
				String result = hexString.substring(6, 8);
				ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + messageId1, "0");
				log.info(
						"OBU上传消息处理结果---->数据长度：" + messageLen1 + "命令Id:"
								+ messageId1 + " 执行结果：" + result
								+ "(1/成功，0/失败)");
				break;
			case (byte) 0x89:// 上传终端参数
				String messageLen2 = hexString.substring(2, 4);
				String parametersCount = hexString.substring(4, 6);
				int paramCount = Integer.parseInt(parametersCount, 16);
				this.parseParameters(hexString.substring(6));

				log.info(
						"OBU上传终端参数---->数据长度：" + messageLen2 + "参数总数"
								+ parametersCount);
				break;
			case (byte) 0xAC:// 通知服务器参数已回复至出厂设置
				String messageLen3 = hexString.substring(2, 4);
				log.info(
						"OBU通知服务器参数已回复至出厂设置---->数据长度：" + messageLen3);
				break;
			case (byte) 0xAE:// 终端向服务器请求更新程序
				String messageLen4 = hexString.substring(2, 4);
				String hardwareVersion = new String(Tools
						.fromHexString(hexString.substring(4, 12)));
				String softwareVersion = new String(Tools
						.fromHexString(hexString.substring(12, 24)));
				String programSize = Integer.parseInt(hexString.substring(24,
						28), 16)
						+ "";
				String startPosition = Integer.parseInt(hexString.substring(28,
						32), 16)
						+ "";
				String packageSize = Integer.parseInt(hexString.substring(32,
						36), 16)
						+ "";
				if (packageSize.equals("0")
						&& packageSize.equals(startPosition)) {
					log.info(
							this.getDeviceSN() + "OBU终端向服务器请求更新程序,程序下载已结束。");
				}
				log.info(
						"OBU终端向服务器请求更新程序,数据长度：" + messageLen4 + "硬件版本："
								+ hardwareVersion + "软件版本：" + softwareVersion
								+ " 程序尺寸：" + programSize + " 开始位置："
								+ startPosition + " 设置服务器对程序分包的大小："
								+ packageSize);
				break;
			case (byte) 0xD1:// 汇报终端GPS信息（状态扩展2）
				this.parseGPS(hexString.substring(2));
				log.info("OBU汇报终端GPS信息（状态扩展2）");
				break;
			case (byte) 0xD2:// 终端紧急报警
				String messageLen5 = hexString.substring(2, 4);
				String hextime = hexString.substring(4, 10);
				String time = this.parseTime(hextime);
				this.setAlarmType(AlarmType.ACTIVE_ALARM_TYPE);
				AlarmQueue.getInstance().addAlarm(this);
				log.info(
						"OBU上传终端紧急报警---->数据长度" + messageLen5 + " 时间：" + time);
				break;
			case (byte) 0xBA:// 终端上传外部设备数据
				String messageLen6 = hexString.substring(2, 6);
				int mlen = Tools.byte2Int(Tools.convertBytePos(Tools
						.fromHexString(messageLen6)));

				String devicId = hexString.substring(6, 8);
				String data = hexString.substring(8, hexString.length() - 2);

				parseWirelessData(data);

				String checkSum = hexString.substring(hexString.length() - 2,
						hexString.length());

				log.info(
						"OBU终端上传外部设备数据---->数据长度" + mlen + " 外部设备ID：" + devicId
								+ "数据内容：" + data + "校验值：" + checkSum);
				break;
			case (byte) 0xA2:// 终端上传的心跳消息
				this.setReplyByte(this.reply("a2"));
				log.info(this.getDeviceSN() + "OBU终端上传的心跳消息");
				break;
			}
		}

	}

	private void makeResponseXml(String methodName, String[] paramNames,
			String[] values, boolean isSend) {

		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("request");
		root.addAttribute("name", methodName);
		root.addAttribute("deviceId", this.getDeviceSN());

		for (int i = 0; i < paramNames.length; i++) {
			Element opttype = root.addElement(paramNames[i]);
			opttype.setText(values[i]);
		}

		doc.setXMLEncoding("gbk");
		String req = doc.asXML();
		log.info("转发到信息到应用端：" + req + "\r\nisSend:" + isSend);
		// try {
		// req = new String(doc.asXML().getBytes("utf-8"));
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// log.info("转发到信息到应用端：" + req);
		if (isSend)
			this.sendData(req);
	}

	private String sendData(String req) {
		String doc = "";
		String res = "";
		String desc = "";
		PropertyReader prop = null;
		try {
			prop = new PropertyReader("kyt.properties");
			String url = prop.getProperty("fwdUrl");
			Log.getInstance()
					.outLog("OBITS运单转发URL=" + url + ",内容：\r\n" + req);
			byte[] retb = HttpUtil.getPostURLData(url, req.getBytes());

			if (retb != null) {
				doc = new String(retb);
				Log.getInstance()
						.outLog("OBITS终端转发数据请求结果 ：-----------" + doc);
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

	private void parseWirelessData(String data) {

		String cmdHex = data.substring(0, 4);
		byte[] cmdid = Tools.fromHexString(cmdHex);
		String cmdId = Tools.bytesToHexString(Tools.convertBytePos(cmdid));
		String repCmdId = "";

		if (cmdId.equals("8001")) {
			// 确认任务基本数据下发（OBU->服务器）
			repCmdId = "8180";
			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));
			String activityVersion = data.substring(28, 40);
			activityVersion = new String(Tools.fromHexString(activityVersion));
			log.info(
					"OBU终端-----" + this.getDeviceSN()
							+ "-----任务基本数据下发回复,activityID:" + activityID
							+ " activityVersion:" + activityVersion);
			this.makeResponseXml("confirmTaskBasiData", new String[] {
					"ActivityId", "ActivityVersion" }, new String[] {
					activityID, activityVersion }, false);

		} else if (cmdId.equals("8002")) {
			// 确认任务扩展数据下发（OBU->服务器）
			repCmdId = "8280";
			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String activityVersion = data.substring(28, 40);
			activityVersion = new String(Tools.fromHexString(activityVersion));

			String nodeCount = data.substring(40, 42);
			nodeCount = Integer.parseInt(nodeCount, 16) + "";

			String willCount = data.substring(42, 44);
			willCount = Integer.parseInt(willCount, 16) + "";

			log.info(
					"OBU终端-----" + this.getDeviceSN()
							+ "-----任务扩展数据下发回复,activityID:" + activityID
							+ " activityVersion:" + activityVersion
							+ " 下发的特殊节点数目:" + nodeCount + " 下发的运单数目:"
							+ willCount);
			this.makeResponseXml("confirmTaskExtendData", new String[] {
					"ActivityId", "ActivityVersion", "NodeCount",
					"WayBillCount" }, new String[] { activityID,
					activityVersion, nodeCount, willCount }, false);

		} else if (cmdId.equals("8003")) {
			// 确认运单基本数据下发（OBU->服务器）
			repCmdId = "8380";

			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String activityVersion = data.substring(28, 40);
			activityVersion = new String(Tools.fromHexString(activityVersion));

			String consignmentId = data.substring(28, 52);
			consignmentId = new String(Tools.fromHexString(consignmentId));

			String consignmentVersion = data.substring(52, 64);
			consignmentVersion = new String(Tools
					.fromHexString(consignmentVersion));

			log.info(
					"OBU终端-----" + this.getDeviceSN()
							+ "-----运单基本数据下发回复,activityID:" + activityID
							+ " consignmentID:" + consignmentId
							+ " consignmentVersion:" + consignmentVersion);
			this
					.makeResponseXml("confirmWayBillBasicData", new String[] {
							"ActivityId", "ActivityVersion", "ConsignmentId",
							"ConsignmentVersion" },
							new String[] { activityID, activityVersion,
									consignmentId, consignmentVersion }, false);

		} else if (cmdId.equals("8004")) {// 确认运单扩展数据下发（OBU->服务器）

			repCmdId = "8480";

			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String activityVersion = data.substring(28, 40);
			activityVersion = new String(Tools.fromHexString(activityVersion));

			String consignmentId1 = data.substring(28, 52);
			consignmentId1 = new String(Tools.fromHexString(consignmentId1));

			String consignmentVersion1 = data.substring(52, 64);
			consignmentVersion1 = new String(Tools
					.fromHexString(consignmentVersion1));

			log.info(
					"OBU终端-----" + this.getDeviceSN()
							+ "-----运单扩展数据下发回复,activityID:" + activityID
							+ " consignmentID:" + consignmentId1
							+ " consignmentVersion:" + consignmentVersion1);
			this.makeResponseXml("confirmWayBillExtendData", new String[] {
					"ActivityId", "ActivityVersion", "ConsignmentId",
					"ConsignmentVersion" }, new String[] { activityID,
					activityVersion, consignmentId1, consignmentVersion1 },
					false);

		} else if (cmdId.equals("8005")) {// 确认运单参数清单下发（OBU->服务器）
			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String activityVersion = data.substring(28, 40);
			activityVersion = new String(Tools.fromHexString(activityVersion));

			String consignmentId2 = data.substring(28, 52);
			consignmentId2 = new String(Tools.fromHexString(consignmentId2));

			String consignmentVersion2 = data.substring(52, 64);
			consignmentVersion2 = new String(Tools
					.fromHexString(consignmentVersion2));

			String paraCount = data.substring(64);

			// log.info(
			// "OBU终端-----" + this.getDeviceSN() + "-----运单参数清单下发回复,activityID:"
			// + activityID + " consignmentID:" + consignmentId2
			// + " consignmentVersion:" + consignmentVersion2
			// + " 参数总数：" + paraCount);

		} else if (cmdId.equals("8006")) {// 确认运单货品清单下发（OBU->服务器）
			repCmdId = "8680";

			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String consignmentId3 = data.substring(28, 52);
			consignmentId3 = new String(Tools.fromHexString(consignmentId3));

			String consignmentVersion3 = data.substring(52, 64);
			consignmentVersion3 = new String(Tools
					.fromHexString(consignmentVersion3));

			String goodCount = data.substring(64);
			goodCount = Integer.parseInt(goodCount, 16) + "";

			log.info(
					"OBU终端-----" + this.getDeviceSN()
							+ "-----运单参数清单下发回复,activityID:" + activityID
							+ " consignmentID:" + consignmentId3
							+ " consignmentVersion:" + consignmentVersion3
							+ " 货物总数：" + goodCount);

			this.makeResponseXml("confirmWayBillGoodsList", new String[] {
					"ActivityId", "ConsignmentId", "ConsignmentVersion",
					"GoodsCount" }, new String[] { activityID, consignmentId3,
					consignmentVersion3, goodCount }, false);

		} else if (cmdId.equals("8007")) {// 确认运单参数下发（OBU->服务器）
			String activityID = data.substring(4, 28);
			String activityVersion = data.substring(28, 40);
			String consignmentId4 = data.substring(28, 52);
			String parameters = data.substring(52, 54);
			String parametersName = data.substring(54, 56);

			log.info(
					"OBU终端-----" + this.getDeviceSN()
							+ "-----运单参数下发回复,activityID:" + activityID
							+ " consignmentID:" + consignmentId4 + " 参数数目:"
							+ parameters + " 参数名称长度：" + parametersName);
		} else if (cmdId.equals("8008")) {// 确认运单货品下发（OBU->服务器）
			repCmdId = "8880";

			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String consignmentId3 = data.substring(28, 52);
			consignmentId3 = new String(Tools.fromHexString(consignmentId3));

			String GoodsCount = data.substring(52, 54);
			GoodsCount = Integer.parseInt(GoodsCount, 16) + "";

			String GoodsNums = data.substring(54);
			GoodsNums = new String(Tools.fromHexString(GoodsNums));
			int count = GoodsNums.length() / 12;
			String tmp = "";
			for (int i = 0; i < count; i++) {
				String nums = GoodsNums.substring(i * 12, i * 12 + 12);
				if (i != count - 1)
					tmp = nums + ",";
				else
					tmp += nums;
			}

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----运单货品下发回复");
			this
					.makeResponseXml("confirmWayBillGoods", new String[] {
							"ActivityId", "ConsignmentId", "GoodsCount",
							"GoodsNums" }, new String[] { activityID,
							consignmentId3, GoodsCount, tmp }, false);

		} else if (cmdId.equals("8009")) {// 确认中途点下发（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----中途点下发回复");
			repCmdId = "8980";

			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String HalfWayPointCount = data.substring(28, 30);
			int hwpcount = Integer.parseInt(HalfWayPointCount, 16);

			String nodes = data.substring(30);
			nodes = new String(Tools.fromHexString(nodes));
			int count = nodes.length() / 18;

			Document doc = DocumentHelper.createDocument();
			Element root = doc.addElement("request");
			root.addAttribute("name", "confirmHalfWayPoint ");
			root.addAttribute("deviceId", this.getDeviceSN());
			Element opttype = root.addElement("ActivityID");
			opttype.setText(activityID);
			Element PointCount = root.addElement("HalfWayPointCount");
			PointCount.setText(count + "");
			String nodelist = "";
			for (int i = 0; i < count; i++) {
				try {
					String nodeid = nodes.substring(i * 18, i * 18 + 12);
					String nodever = nodes.substring(i * 18 + 12,
							i * 18 + 12 + 6);

					Element nodeEle = root.addElement("HalfWayPointNum");
					Element subNode = nodeEle.addElement("NodeId");
					subNode.setText(nodeid);
					Element subNodeV = nodeEle.addElement("NodeVersion");
					subNodeV.setText(nodever);
				} catch (Exception e) {
					Log.getInstance().errorLog("", e);
					continue;

				}

			}

			doc.setXMLEncoding("gbk");
			String req = doc.asXML();
			log.info("转发确认中途点 信息到应用端：" + req);
			// this.sendData(req);

		} else if (cmdId.equals("800a")) {// 确认电子围栏下发（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----电子围栏下发回复");
		} else if (cmdId.equals("800b")) {// 任务查询响应（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----任务查询回复");
		} else if (cmdId.equals("800c")) {// 运单查询响应（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----运单查询回复");
		} else if (cmdId.equals("800d")) {// 运单参数查询响应（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----运单参数查询回复");
		} else if (cmdId.equals("800e")) {// 运单货品查询响应（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----运单货品查询回复");
		} else if (cmdId.equals("800f")) {// 中途点查询响应（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----中途点查询回复");
		} else if (cmdId.equals("8010")) {// 要求司机修改中途点预计到达时间响应（OBU->服务器）

			log.info(
					"OBU终端-----" + this.getDeviceSN()
							+ "-----要求司机修改中途点预计到达时间回复");
		} else if (cmdId.equals("8011")) {// 电子围栏查询响应（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----电子围栏查询回复");

		} else if (cmdId.equals("8012")) {// 请求变更任务状态（OBU->服务器）

			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String activityVer = data.substring(28, 40);
			activityVer = new String(Tools.fromHexString(activityVer));

			String ActState = data.substring(40, 42);
			ActState = Integer.parseInt(ActState, 16) + "";
			String driverNum = data.substring(42, 66);
			byte[] bdriverNum = Tools.fromHexString(driverNum);
			String hexdn = "";
			for (int i = 0; i < bdriverNum.length; i++) {
				if (bdriverNum[i] == (byte) 0x00) {
					continue;
				} else {
					hexdn += Tools
							.bytesToHexString(new byte[] { bdriverNum[i] });
				}
			}

			driverNum = new String(Tools.fromHexString(hexdn));
			String reqDate = data.substring(66);

			this.makeResponseXml("reqChangeTaskState", new String[] {
					"ActivityId", "ActivityVersion", "ActivityState",
					"DriverNum", "ReqDate" }, new String[] { activityID,
					activityVer, ActState, driverNum, reqDate }, true);

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----请求变更任务状态回复");
		} else if (cmdId.equals("8013")) {// 请求变更中途点状态（OBU->服务器）

			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String nodeId = data.substring(28, 52);
			nodeId = new String(Tools.fromHexString(nodeId));

			String nodeVersion = data.substring(52, 64);
			nodeVersion = new String(Tools.fromHexString(nodeVersion));

			String state = data.substring(64, 66);
			state = Integer.parseInt(state, 16) + "";

			String driverNum = data.substring(66, 90);
			int lastPos = driverNum.lastIndexOf("3");
			// driverNum = driverNum.substring(0, lastPos + 2);
			// driverNum = new String(Tools.fromHexString(driverNum));
			byte[] bdriverNum = Tools.fromHexString(driverNum);
			String hexdn = "";
			for (int i = 0; i < bdriverNum.length; i++) {
				if (bdriverNum[i] == (byte) 0x00) {
					continue;
				} else {
					hexdn += Tools
							.bytesToHexString(new byte[] { bdriverNum[i] });
				}
			}

			driverNum = new String(Tools.fromHexString(hexdn));

			String date = data.substring(90);

			this.makeResponseXml("reqChangeHalfWayPointState", new String[] {
					"ActivityId", "NodeId ", "NodeVersion", "ActivityState",
					"DriverNum", "ReqDate" }, new String[] { activityID,
					nodeId, nodeVersion, state, driverNum, date }, true);

			log.info(
					"OBU终端-----" + this.getDeviceSN()
							+ "-----请求变更中途点状态:actid =" + activityID
							+ ",nodeid=" + nodeId + ",nodever=" + nodeVersion
							+ ",date=" + date + ",drivernum=" + driverNum
							+ ",请求状态：" + state);

		} else if (cmdId.equals("8014")) {// 请求变更预计到达时间（OBU->服务器）

			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String nodeId = data.substring(28, 52);
			nodeId = new String(Tools.fromHexString(nodeId));

			String nodeVersion = data.substring(52, 64);
			nodeVersion = new String(Tools.fromHexString(nodeVersion));

			String date = data.substring(64);

			this.makeResponseXml("reqChangeExpectedTime", new String[] {
					"ActivityId", "NodeId ", "NodeVersion", "ArriveDate" },
					new String[] { activityID, nodeId, nodeVersion, date },
					true);

			log.info(
					"OBU终端-----" + this.getDeviceSN()
							+ "-----请求变更预计到达时间:actid =" + activityID
							+ ",nodeid=" + nodeId + ",nodever=" + nodeVersion
							+ ",date=" + date);

		} else if (cmdId.equals("8015")) {// 电子围栏汇报（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----电子围栏汇报回复");
		} else if (cmdId.equals("8016")) {// 请求安装（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----请求安装");
		} else if (cmdId.equals("8017")) {// 请求发送安装检查报告（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----请求发送安装检查报告");

		} else if (cmdId.equals("8018")) {// 请求登录（OBU->服务器）
			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----请求登录");

			String ulen = data.substring(4, 6);
			int len = Integer.parseInt(ulen, 16);
			String username = data.substring(6, 6 + len * 2);
			int plen = Integer.parseInt(data.substring(6 + len * 2,
					6 + len * 2 + 2), 16);
			String pwd = data.substring(6 + len * 2 + 2, 6 + len * 2 + 2 + plen
					* 2);
			username = new String(Tools.fromHexString(username));
			pwd = new String(Tools.fromHexString(pwd));
			String date = data.substring(data.length() - 12);
			log.info(
					this.getDeviceSN() + " 登陆用户名：" + username + ",登陆密码：" + pwd
							+ ",登陆时间：" + date);
			PropertyReader prop = null;
			String type = "";
			try {
				prop = new PropertyReader("kyt.properties");
				type = prop.getProperty("obu_user");
			} catch (Exception e) {
				type = "3";
			}
			String repHex = "9880" + ulen + data.substring(6, 6 + len * 2)
					+ Tools.convertToHex(type, 2);
			byte[] retbyte = this.DWirelessTransfer(repHex);
			this.setReplyByte(retbyte);

		} else if (cmdId.equals("8019")) {// 请求注销（OBU->服务器）
			// 1980 01 31 03 01 01 12 50 27 cf
			String userlen = data.substring(4, 6);
			int len = Integer.parseInt(userlen, 16);
			String user = data.substring(6, 6 + len * 2);

			String commandHex = "9980" + userlen + user + "01";

			byte[] rebtype = this.DWirelessTransfer(commandHex);

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----请求注销,username:"
							+ new String(Tools.fromHexString(user)) + ",注销确认："
							+ Tools.bytesToHexString(rebtype));

		} else if (cmdId.equals("801a")) {// 确认通用消息（OBU->服务器）

			repCmdId = "9a80";

			String fileid = data.substring(4, 28);
			fileid = new String(Tools.fromHexString(fileid));
			if (fileid != null && fileid.equals("000000000000")) {
				log.info(
						"OBU终端快易通平台：" + this.getDeviceSN()
								+ "-----确认通用消息,messageid=" + fileid);
			} else {
				this.makeResponseXml("confirmCommonMsgRequest",
						new String[] { "MessageId" }, new String[] { fileid },
						false);
				log.info(
						"OBU终端----->ERP " + this.getDeviceSN()
								+ "-----确认通用消息,messageid=" + fileid);
			}

		} else if (cmdId.equals("801b")) {// 通用消息回复（OBU->服务器）

			String msgID = data.substring(4, 28);
			String repHex = "9b80" + msgID + "00";
			byte[] retbyte = this.DWirelessTransfer(repHex);
			this.setReplyByte(retbyte);

			msgID = new String(Tools.fromHexString(msgID));

			String activityLen = data.substring(28, 30);
			int atvlen = Integer.parseInt(activityLen, 16);
			String ActivityId = data.substring(30, atvlen * 2 + 30);
			ActivityId = new String(Tools.fromHexString(ActivityId));

			String consignLen = data
					.substring(atvlen * 2 + 30, atvlen * 2 + 32);
			int consignlen = Integer.parseInt(consignLen, 16);
			String consignmentId = data.substring(atvlen * 2 + 32, atvlen * 2
					+ 32 + consignlen * 2);
			consignmentId = new String(Tools.fromHexString(consignmentId));

			String nodeLen = data.substring(atvlen * 2 + 32 + consignlen * 2,
					atvlen * 2 + 32 + consignlen * 2 + 2);
			int nodelen = Integer.parseInt(nodeLen, 16);
			String nodeId = data.substring(
					atvlen * 2 + 32 + consignlen * 2 + 2, atvlen * 2 + 32
							+ consignlen * 2 + 2 + nodelen * 2);
			nodeId = new String(Tools.fromHexString(nodeId));

			String receiverLen = data.substring(atvlen * 2 + 32 + consignlen
					* 2 + 2 + nodelen * 2, atvlen * 2 + 32 + consignlen * 2 + 2
					+ nodelen * 2 + 2);
			int receiverlen = Integer.parseInt(receiverLen, 16);
			String receiver = data.substring(atvlen * 2 + 32 + consignlen * 2
					+ 2 + nodelen * 2 + 2, atvlen * 2 + 32 + consignlen * 2 + 2
					+ nodelen * 2 + 2 + receiverlen * 2);
			receiver = new String(Tools.fromHexString(receiver));

			String sendDate = data.substring(atvlen * 2 + 32 + consignlen * 2
					+ 2 + nodelen * 2 + 2 + receiverlen * 2, atvlen * 2 + 32
					+ consignlen * 2 + 2 + nodelen * 2 + 2 + receiverlen * 2
					+ 6 * 2);

			String senderLen = data.substring(atvlen * 2 + 32 + consignlen * 2
					+ 2 + nodelen * 2 + 2 + receiverlen * 2 + 6 * 2, atvlen * 2
					+ 32 + consignlen * 2 + 2 + nodelen * 2 + 2 + receiverlen
					* 2 + 6 * 2 + 2);
			int senderlen = Integer.parseInt(senderLen, 16);
			String sender = data.substring(atvlen * 2 + 32 + consignlen * 2 + 2
					+ nodelen * 2 + 2 + receiverlen * 2 + 6 * 2 + 2, atvlen * 2
					+ 32 + consignlen * 2 + 2 + nodelen * 2 + 2 + receiverlen
					* 2 + 6 * 2 + 2 + senderlen * 2);
			sender = new String(Tools.fromHexString(sender));

			String contentLen = data.substring(atvlen * 2 + 32 + consignlen * 2
					+ 2 + nodelen * 2 + 2 + receiverlen * 2 + 6 * 2 + 2
					+ senderlen * 2, atvlen * 2 + 32 + consignlen * 2 + 2
					+ nodelen * 2 + 2 + receiverlen * 2 + 6 * 2 + 2 + senderlen
					* 2 + 2);
			int contentlen = Integer.parseInt(contentLen, 16);
			String content = data.substring(atvlen * 2 + 32 + consignlen * 2
					+ 2 + nodelen * 2 + 2 + receiverlen * 2 + 6 * 2 + 2
					+ senderlen * 2 + 2, atvlen * 2 + 32 + consignlen * 2 + 2
					+ nodelen * 2 + 2 + receiverlen * 2 + 6 * 2 + 2 + senderlen
					* 2 + 2 + contentlen * 2);

			content = new String(Tools.fromHexString(content)).replaceAll(
					"&amp;", "&");
			// 测试时LINUX系统字符编码为GB2312,下发时繁体字为乱码，修改系统编码为GBK正常
			// try {
			// content = new String(content.getBytes("utf-8"));
			// } catch (UnsupportedEncodingException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }

			String msgvalueLen = data.substring(atvlen * 2 + 32 + consignlen
					* 2 + 2 + nodelen * 2 + 2 + receiverlen * 2 + 6 * 2 + 2
					+ senderlen * 2 + 2 + contentlen * 2, atvlen * 2 + 32
					+ consignlen * 2 + 2 + nodelen * 2 + 2 + receiverlen * 2
					+ 6 * 2 + 2 + senderlen * 2 + 2 + contentlen * 2 + 2);

			int msgvaluelen = Integer.parseInt(receiverLen, 16);
			String msgvalue = data.substring(atvlen * 2 + 32 + consignlen * 2
					+ 2 + nodelen * 2 + 2 + receiverlen * 2 + 6 * 2 + 2
					+ senderlen * 2 + 2 + contentlen * 2 + 2);
			// try {
			// msgvalue = new String(Tools.fromHexString(msgvalue),"gb18030");
			// } catch (UnsupportedEncodingException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			msgvalue = new String(Tools.fromHexString(msgvalue));

			try {
				log.info(
						"OBU终端" + this.getDeviceSN() + "activeID："
								+ ActivityId  + ",consignmentId："
								+ consignmentId+",nodeId:"+nodeId);
				
				if (ActivityId.equals("0") && consignmentId.equals("0")&&nodeId.equals("0")) {
//					DBService service = new DBServiceImpl();
//					long gid = this.term.getGid();
//					service.saveMessage(this.getDeviceSN(), "监控平台", msgvalue,
//							"1", gid);
					log.info(
							"OBU终端调度消息" + this.getDeviceSN() + "-----上行通用消息："
									+ msgvalue  + ",原始信息："
									+ content);
					
				}else{
					String values[] = new String[] { ActivityId, consignmentId, nodeId,
							receiver, sendDate, sender, content, msgID, msgvalue };

					this.makeResponseXml("uploadCommondMsg", new String[] {
							"ActivityId", "ConsignmentId", "NodeId", "Receiver",
							"SendDate", "Sender", "Content", "MsgId", "MsgValue" },
							values, true);
					log.info(
							"OBU终端-----" + this.getDeviceSN() + "-----上行通用消息："
									+ msgvalue + ",中心确认："
									+ Tools.bytesToHexString(retbyte) + ",原始信息："
									+ content);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			

		} else if (cmdId.equals("801c")) {// 确认文档下发请求（OBU ->服务器）
			repCmdId = "9c80";
			String fileid = data.substring(4, 28);
			fileid = new String(Tools.fromHexString(fileid));
			this.makeResponseXml("confirmFileRequest",
					new String[] { "FileId" }, new String[] { fileid }, false);

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----确认文档下发请求,fileid="
							+ fileid);

		} else if (cmdId.equals("801d")) {// 文档下载完成通知（OBU->服务器）

			String fileid = data.substring(4, 28);
			fileid = new String(Tools.fromHexString(fileid));
			String ftime = data.substring(28);

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----文档下载完成通知,fileid="
							+ fileid + ",date=" + ftime);

		} else if (cmdId.equals("801e")) {// 确认特殊消息（OBU->服务器）

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----确认特殊消息");

		} else if (cmdId.equals("801f")) {// 确认特殊消息参数下发（OBU->服务器）

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----确认特殊消息参数下发回复");

		} else if (cmdId.equals("8020")) {// 汇报任务费用信息（OBU->服务器）

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----汇报任务费用信息");

		} else if (cmdId.equals("8021")) {// 确认任务完整信息下发结束（OBU->服务器）

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----确认任务完整信息下发结束");

		} else if (cmdId.equals("8022")) {// 确认运单完整信息下发结束（OBU->服务器）

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----确认运单完整信息下发结束");

		} else if (cmdId.equals("8023")) {// 确认任务查询完整信息汇报结束（OBU->服务器）

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----确认任务查询完整信息汇报结束");

		} else if (cmdId.equals("8024")) {// 确认运单查询完整信息汇报结束（OBU->服务器）

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----确认运单查询完整信息汇报结束");

		} else if (cmdId.equals("8025")) {// 确认特殊消息及参数完整信息下发结束（OBU->服务器）

			log.info(
					"OBU终端-----" + this.getDeviceSN()
							+ "-----确认特殊消息及参数完整信息下发结束");

		} else if (cmdId.equals("8026")) {// 确认查询运单参数完整信息汇报结束（OBU->服务器）

			Log.getInstance()
					.outLog(
							"OBU终端-----" + this.getDeviceSN()
									+ "-----确认查询运单参数完整信息汇报结束");

		} else if (cmdId.equals("8027")) {// 确认查询运单货物完整信息汇报结束（OBU->服务器）

			Log.getInstance()
					.outLog(
							"OBU终端-----" + this.getDeviceSN()
									+ "-----确认查询运单货物完整信息汇报结束");

		} else if (cmdId.equals("8028")) {// OBU请求捆绑
			String activityID = data.substring(4, 28);
			activityID = new String(Tools.fromHexString(activityID));

			String consignmentId = data.substring(28, 52);
			consignmentId = new String(Tools.fromHexString(consignmentId));

			String consignmentVersion = data.substring(52, 64);
			consignmentVersion = new String(Tools
					.fromHexString(consignmentVersion));

			String optState = data.substring(64);
			optState = Integer.parseInt(optState, 16) + "";
			this.makeResponseXml("reqModifyBindState", new String[] {
					"ActivityId", "ConsignmentId", "ConsignmentVersion",
					"State" }, new String[] { activityID, consignmentId,
					consignmentVersion, optState }, true);

			log.info(
					"OBU终端-----" + this.getDeviceSN() + "-----请求修改捆绑状态："
							+ optState);
		}

		ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + repCmdId, "0");

	}

	private byte[] DWirelessTransfer(String commandHex) {
		String ret = "bd";
		String deviceid = "00";
		int len = 1 + commandHex.length() / 2 + 1;
		String lenHex = Tools.bytesToHexString(Tools
				.convertBytePos(new byte[] { (byte) len }));
		while (lenHex.length() < 4) {
			lenHex = lenHex + "0";
		}
		// lenHex = Tools.bytesToHexString(Tools.convertBytePos(Tools
		// .fromHexString(lenHex)));
		ret += lenHex + deviceid + commandHex;
		byte vcode = Tools
				.checkData(Tools.fromHexString(deviceid + commandHex));
		ret += Tools.bytesToHexString(new byte[] { vcode });
		log.info(this.getDeviceSN() + " 无线传输确认：" + ret);
		return Tools.fromHexString(ret);
	}

	private void parseParameters(String substring) {
		String paramegersId = substring.substring(0, 2);
		int paraId = Integer.parseInt(paramegersId, 16);
		String len = substring.substring(2, 4);
		String value = substring.substring(4);
		switch (paraId) {
		case 0x00:// MDT ID
			String mdtId = substring.substring(4, 12);
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数， MDT ID：" + mdtId);
			break;
		case 0x01:// 软件版本
			String hexsofeVersion = substring.substring(4, 16);
			String sofeVersion = new String(Tools.fromHexString(hexsofeVersion));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数， 软件版本：" + sofeVersion);
			break;
		case 0x02:// 硬件版本
			String hexHardVersion = substring.substring(4, 16);
			String hardVersion = new String(Tools.fromHexString(hexHardVersion));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数， 硬件版本：" + hardVersion);
			break;
		case 0x20:// 主通讯服务器IP地址或域名
			String ip = new String(Tools.fromHexString(substring.substring(4)));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数， 主通讯服务器IP地址或域名：" + ip);
			break;
		case 0x21:// 主通讯服务器IP端口
			String hexPort = substring.substring(4);
			int prot = Integer.parseInt(hexPort, 16);
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数， 主通讯服务器IP端口：" + prot);
			break;
		case 0x22:// 备份通讯服务器一IP地址或域名
			String backupIp1 = new String(Tools.fromHexString(substring
					.substring(4)));
			log.info(
					"OBU终端上传终端参数， 备份通讯服务器一IP地址或域名：" + backupIp1);
			break;
		case 0x23:// 备份通讯服务器一IP端口
			String hexBackupPort1 = substring.substring(4);
			int backupPort1 = Integer.parseInt(hexBackupPort1, 16);
			log.info(
					"OBU终端上传终端参数， 备份通讯服务器一IP端口：" + backupPort1);
			break;
		case 0xAC:// 备份通讯服务器二IP地址或域名
			String backupIp2 = new String(Tools.fromHexString(substring
					.substring(4)));
			log.info(
					"OBU终端上传终端参数， 备份通讯服务器一IP地址或域名：" + backupIp2);
			break;
		case 0xAD:// 备份通讯服务器二IP端口
			String hexBackupPort2 = substring.substring(4);
			int backupPort2 = Integer.parseInt(hexBackupPort2, 16);
			log.info(
					"OBU终端上传终端参数， 备份通讯服务器一IP端口：" + backupPort2);
			break;
		case 0xAE:// 备份通讯服务器三IP地址或域名
			String backupIp3 = new String(Tools.fromHexString(substring
					.substring(4)));
			log.info(
					"OBU终端上传终端参数， 备份通讯服务器一IP地址或域名：" + backupIp3);
			break;
		case 0xAF:// 备份通讯服务器三IP端口
			String hexBackupPort3 = substring.substring(4);
			int backupPort3 = Integer.parseInt(hexBackupPort3, 16);
			log.info(
					"OBU终端上传终端参数， 备份通讯服务器一IP端口：" + backupPort3);
			break;
		case 0x30:// 汇报模式
			String upMode = substring.substring(4);
			this.parseUpMode(upMode);
			break;
		case 0x31:// SIM卡１空车位置汇报时间间隔
			String hexInterval = substring.substring(4);
			int interval = Integer.parseInt(hexInterval, 16);
			log.info(
					"OBU终端上传终端参数， SIM卡１空车位置汇报时间间隔：" + interval);
			break;
		case 0x32:// SIM卡１重车位置汇报时间间隔
			String hexInterval1 = substring.substring(4);
			int interval1 = Integer.parseInt(hexInterval1, 16);
			log.info(
					"OBU终端上传终端参数， SIM卡１空车位置汇报时间间隔：" + interval1);
			break;
		case 0x33:// 报警时位置汇报间隔
			String hexInterval2 = substring.substring(4);
			int interval2 = Integer.parseInt(hexInterval2, 16);
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，报警时位置汇报间隔：" + interval2);
			break;
		case 0xBA:// 报警信息重发间隔
			String hexInterval3 = substring.substring(4);
			int interval3 = Integer.parseInt(hexInterval3, 16);
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，报警信息重发间隔：" + interval3);
			break;
		case 0x34:// SIM卡１未登录时位置汇报时间间隔
			String hexInterval4 = substring.substring(4);
			int interval4 = Integer.parseInt(hexInterval4, 16);
			log.info(
					"OBU终端上传终端参数，SIM卡１未登录时位置汇报时间间隔：" + interval4);
			break;
		case 0x90:// SIM卡１睡眠模式时位置汇报间隔
			String hexInterval5 = substring.substring(4);
			int interval5 = Integer.parseInt(hexInterval5, 16);
			log.info(
					"OBU终端上传终端参数，SIM卡１睡眠模式时位置汇报间隔：" + interval5);
			break;
		case 0xB0:// SIM卡１空车位置汇报距离间隔
			String hexInterval6 = substring.substring(4);
			int interval6 = Integer.parseInt(hexInterval6, 16) * 10;
			log.info(
					"OBU终端上传终端参数，SIM卡１空车位置汇报距离间隔：" + interval6 + "/m");
			break;
		case 0xB1:// SIM卡１重车位置汇报距离间隔
			String hexInterval7 = substring.substring(4);
			int interval7 = Integer.parseInt(hexInterval7, 16) * 10;
			log.info(
					"OBU终端上传终端参数，SIM卡１重车位置汇报距离间隔：" + interval7 + "/m");
			break;
		case 0xB2:// SIM卡１未登录时位置汇报距离间隔
			String hexInterval8 = substring.substring(4);
			int interval8 = Integer.parseInt(hexInterval8, 16) * 10;
			log.info(
					"OBU终端上传终端参数，SIM卡１未登录时位置汇报距离间隔：" + interval8 + "/m");
			break;
		case 0xB3:// SIM卡2空车位置汇报时间间隔
			String hexInterval9 = substring.substring(4);
			int interval9 = Integer.parseInt(hexInterval9, 16);
			log.info(
					"OBU终端上传终端参数，SIM卡2空车位置汇报时间间隔：" + interval9);
			break;
		case 0xB4:// SIM卡2重车位置汇报时间间隔
			String hexInterval10 = substring.substring(4);
			int interval10 = Integer.parseInt(hexInterval10, 16);
			log.info(
					"OBU终端上传终端参数，SIM卡2空车位置汇报时间间隔：" + interval10);
			break;
		case 0xB5:// SIM卡2未登录时位置汇报时间间隔
			String hexInterval11 = substring.substring(4);
			int interval11 = Integer.parseInt(hexInterval11, 16) * 10;
			log.info(
					"OBU终端上传终端参数，SIM卡2未登录时位置汇报时间间隔：" + interval11);
			break;
		case 0xB6:// SIM卡2睡眠模式时位置汇报间隔
			String hexInterval12 = substring.substring(4);
			int interval12 = Integer.parseInt(hexInterval12, 16) * 10;
			log.info(
					"OBU终端上传终端参数，SIM卡2睡眠模式时位置汇报间隔：" + interval12);
			break;
		case 0xB7:// SIM卡2空车位置汇报距离间隔
			String hexInterval13 = substring.substring(4);
			int interval13 = Integer.parseInt(hexInterval13, 16) * 10;
			log.info(
					"OBU终端上传终端参数，SIM卡2空车位置汇报距离间隔：" + interval13 + "/m");
			break;
		case 0xB8:// SIM卡2重车位置汇报距离间隔
			String hexInterval14 = substring.substring(4);
			int interval14 = Integer.parseInt(hexInterval14, 16) * 10;
			log.info(
					"OBU终端上传终端参数，SIM卡2重车位置汇报距离间隔：" + interval14 + "/m");
			break;
		case 0xB9:// SIM卡2未登录时位置汇报距离间隔
			String hexInterval15 = substring.substring(4);
			int interval15 = Integer.parseInt(hexInterval15, 16) * 10;
			log.info(
					"OBU终端上传终端参数，SIM卡2未登录时位置汇报距离间隔：" + interval15 + "/m");
			break;
		case 0x35:// 动态补偿角度阀值
			String hexDu = substring.substring(4);
			int du = Integer.parseInt(hexDu, 16);
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，动态补偿角度阀值：" + du);
			break;
		case 0x40:// 空车超速行驶速度
			String hexEmptySpeed = substring.substring(4);
			int emptySpeed = Integer.parseInt(hexEmptySpeed, 16);
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，空车超速行驶速度：" + emptySpeed);
			break;
		case 0x41:// 超速持续时间
			String hexTime = substring.substring(4);
			int time = Integer.parseInt(hexTime, 16);
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，超速持续时间：" + time);
			break;
		case 0x46:// 怠速报警时间
			String hexMinTime = substring.substring(4);
			int minTime = Integer.parseInt(hexMinTime, 16);
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，怠速报警时间：" + minTime);
			break;
		case 0x47:// 重车超速行驶速度
			String hexFullSpeed = substring.substring(4);
			int fullSpeed = Integer.parseInt(hexFullSpeed, 16);
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，空车超速行驶速度：" + fullSpeed);
			break;
		case 0x48:// 允许紧急报警（SOS）
			int isSOS = Integer.parseInt(substring.substring(4), 16);
			log.info(
					"OBU终端上传终端参数，是否允许紧急报警（SOS）：" + isSOS + ",1/许可，0/禁止");
			break;
		case 0x91:// 紧急报警信号持续时间
			String sosTime = Integer.parseInt(substring.substring(4), 16) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，紧急报警信号持续时间：" + sosTime);
			break;
		case 0x92:// 允许盗窃报警（Alarm）
			int isDao = Integer.parseInt(substring.substring(4), 16);
			log.info(
					"OBU终端上传终端参数，是否允许盗窃报警（Alarm）：" + isDao + ",1/许可，0/禁止");
			break;
		case 0x93:// 盗窃报警信号持续时间
			String daoTime = Integer.parseInt(substring.substring(4), 16) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，紧急报警信号持续时间：" + daoTime);
			break;
		case 0x94:// 主电源欠压阀值
			String acc = Integer.parseInt(substring.substring(4), 16) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，主电源欠压阀值：" + acc);
			break;
		case 0x95:// 主电源欠压持续时间
			String accTime = Integer.parseInt(substring.substring(4), 16) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，主电源欠压持续时间：" + accTime);
			break;
		case 0x96:// 点火/熄火信号持续时间
			String fireTime = Integer.parseInt(substring.substring(4), 16) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，点火/熄火信号持续时间：" + fireTime);
			break;
		case 0x65:// 休眠模式
			String sleepMode = Integer.parseInt(value, 16) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，休眠模式：" + sleepMode);
			break;
		case 0x66:// 自动休眠等待时间
			String autoSleepTime = Integer.parseInt(value, 16) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，休眠模式：" + autoSleepTime);
			break;
		case 0x70:// 车辆车牌号码
			String carNo = new String(Tools.fromHexString(value));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，车辆车牌号码：" + carNo);
			break;
		case 0x72:// 终端手机号
			String phone = new String(Tools.fromHexString(value));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，终端手机号：" + phone);
			break;
		case 0x73:// 公司号
			String compNo = Integer.parseInt(value, 16) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，公司号：" + compNo);
			break;
		case 0x80:// 短消息中心号码1
			String smsCenter1 = new String(Tools.fromHexString(value));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，短消息中心号码1：" + smsCenter1);
			break;
		case 0x81:// APN1\
			String apn1 = new String(Tools.fromHexString(value));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，APN1：" + apn1);
			break;
		case 0x82:// APN1 LOGIN NAME
			String apn1LogName = new String(Tools.fromHexString(value));
			log.info(
					"OBU终端上传终端参数，APN1 LOGIN NAME：" + apn1LogName);
			break;
		case 0x83:// APN1 PASSWORD
			String apn1Password = new String(Tools.fromHexString(value));
			log.info(
					"OBU终端上传终端参数，APN1 PASSWORD：" + apn1Password);
			break;
		case 0x97:// 短消息中心号码2
			String smsCenter2 = new String(Tools.fromHexString(value));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，短消息中心号码2：" + smsCenter2);
			break;
		case 0x98:// APN2
			String apn2 = new String(Tools.fromHexString(value));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，APN2：" + apn2);
			break;
		case 0x99:// APN2 LOGIN NAME
			String apn2logname = new String(Tools.fromHexString(value));
			log.info(
					"OBU终端上传终端参数，APN2 LOGIN NAME：" + apn2logname);
			break;
		case 0x9A:// APN2 PASSWORD
			String apn2Password = new String(Tools.fromHexString(value));
			log.info(
					"OBU终端上传终端参数，APN2 PASSWORD：" + apn2Password);
			break;
		case 0x9B:// 缺省SIM卡槽
			String defaultsim = Integer.parseInt(value, 16) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，缺省SIM卡槽：" + defaultsim);
			break;
		case 0xBB:// 导航／正常模式切换采用的触发信号
			String sign = Integer.parseInt(value, 16) + "";
			log.info(
					"OBU终端上传终端参数，导航／正常模式切换采用的触发信号(１刹车信号２速度)：" + sign);
			break;
		case 0x9C:// 导航/正常模式切换，触发信号持续时间
			String lastTime = Integer.parseInt(value, 16) + "";
			log.info(
					"OBU终端上传终端参数，导航/正常模式切换，触发信号持续时间：" + lastTime);
			break;
		case 0x9D:// 使用导航
			String isNavi = Integer.parseInt(value, 16) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，使用导航(0－不允许1－允许)："
							+ isNavi);
			break;
		case 0x9E:// 使用A4打印机
			String isPrint = Integer.parseInt(value, 16) + "";
			log.info(
					"OBU终端上传终端参数，使用A4打印机(0－不允许1－允许)：" + isPrint);
			break;
		case 0x9F:// 使用小打印机
			String isSmallPrint = Integer.parseInt(value, 16) + "";
			log.info(
					"OBU终端上传终端参数，使用小打印机(0－不允许1－允许)：" + isSmallPrint);
			break;
		case 0xA0:// 接收天气消息
			String isweather = Integer.parseInt(value, 16) + "";
			log.info(
					"OBU终端上传终端参数，接收天气消息(0－不允许1－允许)：" + isweather);
			break;
		case 0xA1:// 接收交通消息
			String isTran = Integer.parseInt(value, 16) + "";
			log.info(
					"OBU终端上传终端参数，接收交通消息(0－不允许1－允许)：" + isTran);
			break;
		case 0xA2:// 求救电话1
			String help1 = new String(Tools.fromHexString(value));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，求救电话1：" + help1);
			break;
		case 0xA3:// 求救电话2
			break;
		case 0xA4:// 求救电话3
			break;
		case 0xA5:// 求救电话4
			break;
		case 0xA6:// 求救电话5
			break;
		case 0xA7:// 求救电话6
			break;
		case 0xA8:// 每天自动重启
			String isReset = Integer.parseInt(value, 16) + "";
			log.info(
					"OBU终端上传终端参数，每天自动重启(0－不允许1－允许)：" + isReset);
			break;
		case 0xA9:// 系统重启时间
			String resetTime = new String(Tools.fromHexString(value));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，系统重启时间：" + resetTime);
			break;
		case 0xAA:// IMEI
			String IMEI = new String(Tools.fromHexString(value));
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，IMEI：" + IMEI);
			break;
		case 0xAB:// 使用外部IO
			String isIo = Integer.parseInt(value) + "";
			log.info(
					"OBU终端上传终端参数，是否允许外部IO(0－不允许1－允许)：" + isIo);
			break;
		case 0xBC:// 加密密码
			String password = Integer.parseInt(value) + "";
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，加密密码：" + password);
			break;
		}
	}

	private void parseUpMode(String upMode) {
		String bupMode = Integer.toBinaryString(Integer.parseInt(upMode, 16));
		// BIT5：按距离汇报，0-关，1-开
		if (bupMode.substring(2, 3).equals("1")) {
			// this.setAccStatus(accStatus)
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：按距离汇报，开");
		} else {
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：按距离汇报，关");
		}
		// BIT4：盲区补偿，0-关，1-开
		if (bupMode.substring(3, 4).equals("1")) {
			// this.setAccStatus(accStatus)
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：盲区补偿汇报，开");
		} else {
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：盲区补偿汇报，关");
		}
		// BIT3：停车前汇报，0-关，1-开。
		if (bupMode.substring(4, 5).equals("1")) {
			// this.setAccStatus(accStatus)
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：停车前汇报，开");
		} else {
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：停车前汇报，关");
		}
		// BIT2：停车时汇报，0-关，1-开。
		if (bupMode.substring(5, 6).equals("1")) {
			// this.setAccStatus(accStatus)
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：停车时汇报，开");
		} else {
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：停车时汇报，关");
		}
		// BIT1：动态补偿，0-关，1-开。
		if (bupMode.substring(6, 7).equals("1")) {
			// this.setAccStatus(accStatus)
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：动态补偿，开");
		} else {
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：动态补偿，关");
		}
		// BIT0：按时间汇报，0-关，1-开
		if (bupMode.substring(7, 8).equals("1")) {
			// this.setAccStatus(accStatus)
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：按时间汇报，开");
		} else {
			log.info(
					this.getDeviceSN() + "OBU终端上传终端参数，汇报模式：按时间汇报，关");
		}

	}

	private byte[] reply(String messageId) {
		byte Parity = Tools.checkData(Tools.fromHexString(messageId + "01"));
		String cmd = "85" + "03" + messageId + "01"
				+ Tools.bytesToHexString(new byte[] { Parity });
		return Tools.fromHexString(cmd);
	}

	public void parseGPS(String data) {
		// 纬度
		String Blat = data.substring(0, 8);
		byte[] latbyte = Tools.convertBytePos(Tools.fromHexString(Blat));

		String formatLat = this.formatLat(Tools.bytesToHexString(latbyte));
		this.setCoordY(formatLat);

		// 经度
		String Clon = data.substring(8, 16);
		byte[] lngbyte = Tools.convertBytePos(Tools.fromHexString(Clon));
		Clon = Tools.bytesToHexString(lngbyte);

		String formatLon = this.formatLat(Clon);
		this.setCoordX(formatLon);

		// 速度
		String hexspeed = data.substring(16, 18);
		String speed = Integer.parseInt(hexspeed, 16) + "";
		this.setSpeed(speed);

		// 方向角
		String hexDirection = data.substring(18, 20);
		String diriction = Integer.parseInt(hexDirection, 16) * 2 + "";
		this.setDirection(diriction);

		// 时间
		String time = data.substring(20, 26);
		String date = parseTime(time);
		this.setTime(date);

		// status1
		String status1 = data.substring(26, 28);
		this.parseStatus1(status1);
		// status2
		String status2 = data.substring(28, 30);
		this.parseStatus2(status2);
		// status3
		String status3 = data.substring(30, 32);
		this.parseStatus3(status3);

		String lc = data.substring(32, 40);
		byte[] lcbyte = Tools.convertBytePos(Tools.fromHexString(lc));
		float flc = Tools.byte2Int(lcbyte) * 0.1f;
		this.setMileage(flc + "");

		this.setStatusRecord(termStatus);
		log.info(
				this.getDeviceSN() + "OBU终端，上传位置信息，经度为：" + formatLon + "纬度为："
						+ formatLat + ",速度：" + this.getSpeed() + ",方向："
						+ this.getDirection() + ",里程：" + this.getMileage() + ",时间："
						+ this.getTime());

	}

	private void parseStatus3(String status3) {
		String bStatus = Integer.toString(Integer.parseInt(status3, 16), 2);
		while (bStatus.length() < 8) {
			bStatus = "0" + bStatus;
		}
		// 休眠：1－有效；0－无效
		if (bStatus.substring(0, 1).equals("1")) {
			// this.setAccStatus(accStatus)
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，休眠有效");
		} else {

		}
		// DRM：1－使用；0－未使用；
		if (bStatus.substring(1, 2).equals("1")) {
			// this.setAccStatus(accStatus)
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，DRM在使用");
		} else {
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，DRM未使用");
		}
		// 主电源欠压：1－有效；0－无效；
		if (bStatus.substring(3, 4).equals("1")) {
			this.setAlarmType(AlarmType.LACK_PRESSURE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，主电源欠压");
		} else {

		}
	}

	private void parseStatus2(String status2) {
		String bStatus = Integer.toString(Integer.parseInt(status2, 16), 2);
		while (bStatus.length() < 8) {
			bStatus = "0" + bStatus;
		}
		// 盗窃报警：1－有效；0－无效；
		if (bStatus.substring(0, 1).equals("1")) {
			this.setAlarmType(AlarmType.SECURITY_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，盗窃报警有效");
		} else {

		}
		// 怠速报警：1－有效；0－无效；
		if (bStatus.substring(3, 4).equals("1")) {

			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，怠速报警有效");
		} else {

		}
		// 点火状态：1－点火；0－熄火；
		if (bStatus.substring(7, 8).equals("1")) {
			termStatus.setFlameOut("1");
			termStatus.setAcc("1");
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，点火状态:点火");
		} else {
			termStatus.setFlameOut("1");
			termStatus.setAcc("0");
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，点火状态:熄火");
		}

	}

	private void parseStatus1(String status1) {
		String bStatus = Integer.toString(Integer.parseInt(status1, 16), 2);
		while (bStatus.length() < 8) {
			bStatus = "0" + bStatus;
		}

		// GPRS盲区：1－有效；0－无效；
		if (bStatus.substring(0, 1).equals("1")) {

			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，GPRS盲区有效");
		} else {
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，GPRS盲区无效");
		}
		// GPS未定位：1－有效；0－无效；
		if (bStatus.substring(1, 2).equals("1")) {
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，GPS未定位");
			termStatus.setLocate("0");
			this.setLocateStatus("0");
		} else {
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，GPS已定位");
			termStatus.setLocate("1");
			this.setLocateStatus("1");
		}
		 

		// 超速：1－有效；0－无效；
		if (bStatus.substring(4, 5).equals("1")) {
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，超速报警状态有效");
			this.setAlarmType(AlarmType.SPEED_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		// 紧急报警：1－有效；0－无效；
		if (bStatus.substring(5, 6).equals("1")) {
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，紧急报警状态有效");
			this.setAlarmType(AlarmType.ACTIVE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		} else {

		}
		// 驾驶员登录：1－有效；0－无效；
		if (bStatus.substring(6, 7).equals("1")) {
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，驾驶员已登录");
		} else {
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，驾驶未登录");
		}
		// 运营状态：1－重车；0－空车；
		if (bStatus.substring(7, 8).equals("1")) {
			termStatus.setFullEmpty("1");
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，运营状态:重车");
		} else {
			termStatus.setFullEmpty("0");
			log.info(
					this.getDeviceSN() + "OBU终端-----上传位置信息，运营状态:空车");
		}
	}

	private String formatLat(String clat) {
		int fen = Integer.parseInt(clat, 16);
		double lfen = fen / 10000.0 / 60.0;
		String ret = Tools.getNumberFormatString(lfen, 6, 6);
		return ret;

	}

	private String parseTime(String time) {
		String t1 = time.substring(0, 2);
		String t2 = time.substring(2, 4);
		String t3 = time.substring(4, 6);
		String date1 = Integer.toString(Integer.parseInt(t1, 16), 2);
		while (date1.length() < 8) {
			date1 = "0" + date1;
		}
		String date2 = Integer.toString(Integer.parseInt(t2, 16), 2);
		while (date2.length() < 8) {
			date2 = "0" + date2;
		}
		String date3 = Integer.toString(Integer.parseInt(t3, 16), 2);
		while (date3.length() < 8) {
			date3 = "0" + date3;
		}
		String day = Integer.parseInt(date1.substring(2, 7), 2) + "";
		if (day.length() < 2)
			day = "0" + day;
		String hour = Integer.parseInt(date1.charAt(7) + date2.substring(0, 4),
				2)
				+ "";
		if (hour.length() < 2)
			hour = "0" + hour;

		String minute = Integer.parseInt(date2.substring(4)
				+ date3.substring(0, 2), 2)
				+ "";
		if (minute.length() < 2)
			minute = "0" + minute;

		String seconds = Integer.parseInt(date3.substring(2), 2) + "";
		if (seconds.length() < 2)
			seconds = "0" + seconds;
		String stime = hour + minute + seconds;
		String ym = Tools.formatDate2Str(new Date(), "MMyy");
		String date = day + ym;
		String gpstime = Tools.conformtime(stime, date);

		log.info(
				this.getDeviceSN() + "OBU终端, 上传位置信息，上传时间为：" + gpstime);

		return gpstime;

	}

	public void parseHttpGrps(HttpServletRequest request,
			HttpServletResponse response, String hexString) {

	}

	public void parseSMS(String phnum, String content) {

	}

	public static void main(String[] args) {

		// ba 1000 01 1880023132023132030101125721 ff
		// System.out.println(new String(Tools.fromHexString("3000")));
		ParseOBU obu = new ParseOBU();
		obu
				.parseGPRS("ba29000112805441534b303030303030303330303030303101313233000000000000000000110127163115a9");
		// d139d8d300b2880d0400001c75b801000000000000

		// 2010-11-01 20:15:05,427 -
		// 解析异常:ba1a000118800b313233343536373839313203313233030101122345e5
		// java.lang.IllegalArgumentException: fromHexString requires an even
		// number of hex characters
		// at com.autonavi.directl.Tools.fromHexString(Tools.java:127)
		// at
		// com.autonavi.directl.parse.OBU.ParseOBU.parseGPRS(ParseOBU.java:174)
		// at com.autonavi.lbsgateway.GprsTcpThread.run(GprsTcpThread.java:195)
		// at
		// com.autonavi.lbsgateway.gprsserver.ThreadPool$WorkerThread.run(ThreadPool.java:112)
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
