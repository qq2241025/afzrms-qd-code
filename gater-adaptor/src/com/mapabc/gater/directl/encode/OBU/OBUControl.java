package com.mapabc.gater.directl.encode.OBU;

/**
 * 
 */

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.Parameters;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.lbsgateway.service.CommonGatewayServiceImpl;
import com.mapabc.gater.lbsgateway.service.ICommonGatewayService;


/**
 * @author chen.peng
 * 
 */
public class OBUControl extends ControlAdapter {

	private void throwParamException(String desc) throws Exception {

		Exception e = new Exception(desc);
		//Log.getInstance().errorLog("", e);
		throw e;
	}

	public String msg(Request req) {
 
		String deviceId = req.getDeviceId();
		String MessageId = Tools.getRandomString(12);
		while (MessageId.length() < 12) {
			MessageId = "0" + MessageId;
		}
		String ActivityId = "0";
		String NodeId = "0";
		String ConsignmentId = "0";
		String ValidDate = Tools.formatDate2Str(new Date(), "yyMMddHHmmss");
		String InvalidDate = Tools.formatDate2Str(new Date(System
				.currentTimeMillis()
				+ 24 * 60 * 60 * 1000), "yyMMddHHmmss");
		 

		String Type = "0";// 消息类别

		String Content = RequestUtil.getReqData(req, "content");
		String Title = "调度信息（默认标题）";
		if (Content.length() > 10)
			Title = Content.substring(0, 10);
		else
			Title = Content;
 		String DisplayKey = "1";
		String DisplayIcon = "0";
		String Sender = "";
		String DriverNum = "";
		String IsDisplay = "1";
		String IsPrint = "0";
		String IsReply = "1";
		String MainCatalog = "";
		String ViceCatalog = "";
		String dataHex = "";
		try {
			dataHex = "9a80"
					+ Tools.bytesToHexString(MessageId.getBytes("gb18030"));
			dataHex += Tools.int2Hexstring(
					ActivityId.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(ActivityId.getBytes("gb18030"));
			dataHex += Tools.int2Hexstring(
					ConsignmentId.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(ConsignmentId.getBytes("gb18030"));

			dataHex += Tools
					.int2Hexstring(NodeId.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(NodeId.getBytes("gb18030"));
			dataHex += ValidDate + InvalidDate + Tools.convertToHex(Type, 2);
			dataHex += Tools.int2Hexstring(Title.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(Title.getBytes("gb18030"));
			dataHex += Tools.int2Hexstring(Content.getBytes("gb18030").length,
					2)
					+ Tools.bytesToHexString(Content.getBytes("gb18030"));
			dataHex += Tools.convertToHex(DisplayKey, 2)
					+ Tools.convertToHex(DisplayIcon, 2);
			dataHex += Tools
					.int2Hexstring(Sender.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(Sender.getBytes("gb18030"));
			dataHex += Tools.int2Hexstring(
					DriverNum.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(DriverNum.getBytes("gb18030"));
			dataHex += Tools.convertToHex(IsDisplay, 2)
					+ Tools.convertToHex(IsPrint, 2)
					+ Tools.convertToHex(IsReply, 2);
			dataHex += Tools.int2Hexstring(
					MainCatalog.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(MainCatalog.getBytes("gb18030"));
			dataHex += Tools.int2Hexstring(
					ViceCatalog.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(ViceCatalog.getBytes("gb18030"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

		return ret;
	}

	public String sendTaskBasicData(Request req) throws Exception {
		// <ActivityId></ActivityId>
		// <ActivityVersion></ActivityVersion>
		// <ActivityState></ActivityState>
		// <ActivityName></ActivityName>
		// <CustomerNum><CustomerNum>
		// <VehicleNum><VehicleNum>
		// <DriverNum></DriverNum>
		// <CustomNum></CustomNum>
		RequestUtil.getDealRequest(req, "", "8180");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		if (ActivityId.length() > 12) {
			this.throwParamException("ActivityId 长度不能大于12字节！");
		}
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}
		String ActivityVersion = RequestUtil.getReqData(req, "ActivityVersion");
		if (ActivityVersion.length() > 12) {
			this.throwParamException("ActivityVersion 长度不能大于6字节！");
		}
		while (ActivityVersion.length() < 6) {
			ActivityVersion = "0" + ActivityVersion;
		}
		String ActivityState = RequestUtil.getReqData(req, "ActivityState");

		String ActivityName = RequestUtil.getReqData(req, "ActivityName");
		if (ActivityName.length() > 50) {
			this.throwParamException("ActivityName 最多50个字节");
		}
		String CustomerNum = RequestUtil.getReqData(req, "CustomerNum");
		if (CustomerNum.length() > 6) {
			this.throwParamException("CustomerNum 最多6个字节");
		}
		String VehicleNum = RequestUtil.getReqData(req, "VehicleNum");
		if (VehicleNum.length() > 12) {
			this.throwParamException("VehicleNum 最多12个字节");
		}

		String DriverNum = RequestUtil.getReqData(req, "DriverNum");
		if (DriverNum.length() > 12) {
			this.throwParamException("DriverNum 最多12个字节");
		}
		String CustomNum = RequestUtil.getReqData(req, "CustomNum");
		if (CustomNum.length() > 20) {
			this.throwParamException("CustomNum 最多20个字节");
		}

		String dataHex = "8180"
				+ Tools.bytesToHexString((ActivityId + ActivityVersion)
						.getBytes("gb18030"));

		dataHex += Tools.convertToHex(ActivityState, 2);

		dataHex += Tools.int2Hexstring(ActivityName.getBytes("gb18030").length,
				2)
				+ Tools.bytesToHexString(ActivityName.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(CustomerNum.getBytes("gb18030").length,
				2)
				+ Tools.bytesToHexString(CustomerNum.getBytes("gb18030"));

		dataHex += Tools
				.int2Hexstring(VehicleNum.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(VehicleNum.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(DriverNum.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(DriverNum.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(CustomNum.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(CustomNum.getBytes("gb18030"));

		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

		return ret;
	}

	public String sendTaskExtendData(Request req) throws Exception {
		// <ActivityId></ActivityId>
		// <ActivityVersion></ActivityVersion>
		// <NodeNums></NodeNums>
		// <WayBillNums></WayBillNums>
		RequestUtil.getDealRequest(req, "", "8280");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		if (ActivityId.length() > 12) {
			this.throwParamException("ActivityId 长度不能大于12字节！");
		}
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}
		String ActivityVersion = RequestUtil.getReqData(req, "ActivityVersion");
		if (ActivityVersion.length() > 6) {
			this.throwParamException("ActivityVersion 长度不能大于12字节！");
		}
		while (ActivityVersion.length() < 6) {
			ActivityVersion = "0" + ActivityVersion;
		}
		String NodeNums = RequestUtil.getReqData(req, "NodeNums");
		String WayBillNums = RequestUtil.getReqData(req, "WayBillNums");

		String dataHex = "8280"
				+ Tools.bytesToHexString((ActivityId + ActivityVersion)
						.getBytes("gb18030"));

		String[] nodeSplit = NodeNums.split(",");
		String nodes = "";
		for (int i = 0; i < nodeSplit.length; i++) {
			if (nodeSplit[i].length() > 12) {
				this.throwParamException("节点编号最大长度不能大于12！");
			}
			String nodenum = nodeSplit[i];
			while (nodenum.length() < 12) {
				nodenum = "0" + nodenum;
			}
			nodes += nodenum;
		}

		dataHex += Tools.int2Hexstring(nodeSplit.length, 2)
				+ Tools.bytesToHexString(nodes.getBytes("gb18030"));

		String waybill = "";
		String[] waybillSplit = WayBillNums.split(",");
		for (int i = 0; i < waybillSplit.length; i++) {
			if (waybillSplit[i].length() > 12) {
				this.throwParamException("运单编号最大长度不能大于12！");
			}
			String waybillnum = waybillSplit[i];
			while (waybillnum.length() < 12) {
				waybillnum = "0" + waybillnum;
			}
			waybill += waybillnum;
		}
		dataHex += Tools.int2Hexstring(waybillSplit.length, 2)
				+ Tools.bytesToHexString(waybill.getBytes("gb18030"));

		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

		return ret;
	}

	public String sendWayBillBasicData(Request req) throws Exception {
		// <ActivityId></ActivityId>
		// <ConsignmentId></ ConsignmentId>
		// < ConsignmentVersion></ ConsignmentVersion>
		// < ConsignmentName></ ConsignmentName>
		// < PlaceOrderTime></ PlaceOrderTime>
		// < HasRoucars></ HasRoucars>
		// < GetGoodsPlaceNum></ GetGoodsPlaceNum>
		// < HarborNum></ HarborNum>
		// < SendGoodsPlaceNum ></ SendGoodsPlaceNum >
		// < WayBillType ></ WayBillType >
		// < PayGoodsMan ></ PayGoodsMan >
		// < PayGoodsManPhone ></ PayGoodsManPhone >
		// < PayGoodsManAddr ></ PayGoodsManAddr >
		// < ReceiveGoodsMan ></ ReceiveGoodsMan >
		// < ReceiveGoodsManPhone ></ ReceiveGoodsManPhone >
		// < ReceiveGoodsManAddr ></ ReceiveGoodsManAddr >
		// < Agent ></ Agent >
		// < AgentPhone ></ AgentPhone >
		// < ExpectedInOutTime ></ ExpectedInOutTime >
		// < GoodsType ></ GoodsType >
		// < DocNum ></ DocNum >
		// < RefNum ></ RefNum >
		// < InvoiceNum ></ InvoiceNum >
		// < Remark ></ Remark >
		// < Salesman ></ Salesman >
		RequestUtil.getDealRequest(req, "", "8380");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		if (ActivityId.length() > 12) {
			this.throwParamException("ActivityId 长度不能大于12字节！");
		}
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}
		String ConsignmentId = RequestUtil.getReqData(req, "ConsignmentId");
		if (ConsignmentId.length() > 12) {
			this.throwParamException("ConsignmentId 长度不能大于12字节！");
		}
		while (ConsignmentId.length() < 12) {
			ConsignmentId = "0" + ConsignmentId;
		}
		String ConsignmentVersion = RequestUtil.getReqData(req, "ConsignmentVersion");
		if (ConsignmentVersion.length() > 6) {
			this.throwParamException("ConsignmentVersion 长度不能大于6字节！");
		}
		while (ConsignmentVersion.length() < 6) {
			ConsignmentVersion = "0" + ConsignmentVersion;
		}

		String ConsignmentName = RequestUtil.getReqData(req, "ConsignmentName");
		if (ConsignmentName.length() > 50) {
			this.throwParamException("ConsignmentName 最多允许50字节!");
		}

		String PlaceOrderTime = RequestUtil.getReqData(req, "PlaceOrderTime");

		String HasRoucars = RequestUtil.getReqData(req, "HasRoucars");

		String GetGoodsPlaceNum = RequestUtil.getReqData(req, "GetGoodsPlaceNum");
		if (GetGoodsPlaceNum.length() > 12) {
			this.throwParamException("GetGoodsPlaceNum 长度不能大于12字节！");
		}
		while (GetGoodsPlaceNum.length() < 12) {
			GetGoodsPlaceNum = "0" + GetGoodsPlaceNum;
		}

		String HarborNum = RequestUtil.getReqData(req, "HarborNum");
		// if (HarborNum.length() > 12) {
		// this.throwParamException("HarborNum 长度不能大于12字节！");
		// }
		// while (HarborNum.length() < 12) {
		// HarborNum = "0" + HarborNum;
		// }
		String SendGoodsPlaceNum = RequestUtil.getReqData(req, "SendGoodsPlaceNum");
		if (SendGoodsPlaceNum.length() > 12) {
			this.throwParamException("SendGoodsPlaceNum 长度不能大于12字节！");
		}
		while (SendGoodsPlaceNum.length() < 12) {
			SendGoodsPlaceNum = "0" + SendGoodsPlaceNum;
		}

		String WayBillType = RequestUtil.getReqData(req, "WayBillType");

		String PayGoodsMan = RequestUtil.getReqData(req, "PayGoodsMan");
		if (PayGoodsMan.length() > 40) {
			this.throwParamException("PayGoodsMan 长度不能大于40字节！");
		}
		String PayGoodsManPhone = RequestUtil.getReqData(req, "PayGoodsManPhone");
		if (PayGoodsManPhone.length() > 20) {
			this.throwParamException("PayGoodsManPhone 长度不能大于20字节！");
		}
		String PayGoodsManAddr = RequestUtil.getReqData(req, "PayGoodsManAddr");
		if (PayGoodsManAddr.length() > 300) {
			this.throwParamException("PayGoodsManAddr 长度不能大于300字节！");
		}
		String ReceiveGoodsMan = RequestUtil.getReqData(req, "ReceiveGoodsMan");
		if (ReceiveGoodsMan.length() > 40) {
			this.throwParamException("ReceiveGoodsMan 长度不能大于40字节！");
		}

		String ReceiveGoodsManPhone = RequestUtil.getReqData(req,
				"ReceiveGoodsManPhone");
		if (ReceiveGoodsManPhone.length() > 20) {
			this.throwParamException("ReceiveGoodsManPhone 长度不能大于20字节！");
		}

		String ReceiveGoodsManAddr = RequestUtil
				.getReqData(req, "ReceiveGoodsManAddr");
		if (ReceiveGoodsManAddr.length() > 300) {
			this.throwParamException("ReceiveGoodsManAddr 长度不能大于300字节！");
		}

		String Agent = RequestUtil.getReqData(req, "Agent");
		if (Agent.length() > 40) {
			this.throwParamException("Agent 长度不能大于40字节！");
		}

		String AgentPhone = RequestUtil.getReqData(req, "AgentPhone");
		if (AgentPhone.length() > 20) {
			this.throwParamException("AgentPhone 长度不能大于20字节！");
		}

		String ExpectedInOutTime = RequestUtil.getReqData(req, "ExpectedInOutTime");

		String GoodsType = RequestUtil.getReqData(req, "GoodsType");
		if (GoodsType.length() > 20) {
			this.throwParamException("GoodsType 长度不能大于20字节！");
		}
		String DocNum = RequestUtil.getReqData(req, "DocNum");
		if (DocNum.length() > 50) {
			this.throwParamException("DocNum 长度不能大于50字节！");
		}

		String RefNum = RequestUtil.getReqData(req, "RefNum");
		if (RefNum.length() > 50) {
			this.throwParamException("RefNum 长度不能大于50字节！");
		}
		String InvoiceNum = RequestUtil.getReqData(req, "InvoiceNum");
		if (InvoiceNum.length() > 50) {
			this.throwParamException("InvoiceNum 长度不能大于50字节！");
		}
		String Remark = RequestUtil.getReqData(req, "Remark");
		if (Remark.length() > 200) {
			this.throwParamException("Remark 长度不能大于200字节！");
		}
		String Salesman = RequestUtil.getReqData(req, "Salesman");
		if (Salesman.length() > 20) {
			this.throwParamException("Salesman 长度不能大于20字节！");
		}
		String dataHex = "8380"
				+ Tools
						.bytesToHexString((ActivityId + ConsignmentId + ConsignmentVersion)
								.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(
				ConsignmentName.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(ConsignmentName.getBytes("gb18030"));

		dataHex += PlaceOrderTime + Tools.convertToHex(HasRoucars, 2);

		dataHex += Tools.bytesToHexString(GetGoodsPlaceNum.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(HarborNum.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(HarborNum.getBytes("gb18030"));

		dataHex += Tools.bytesToHexString((SendGoodsPlaceNum)
				.getBytes("gb18030"))
				+ Tools.convertToHex(WayBillType, 2);

		dataHex += Tools.int2Hexstring(PayGoodsMan.getBytes("gb18030").length,
				2)
				+ Tools.bytesToHexString(PayGoodsMan.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(
				PayGoodsManPhone.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(PayGoodsManPhone.getBytes("gb18030"));

		String paddrlenHex = Tools.int2Hexstring(PayGoodsManAddr
				.getBytes("gb18030").length, 4);
		byte[] paddlen = Tools.convertBytePos(Tools.fromHexString(paddrlenHex));

		dataHex += Tools.bytesToHexString(paddlen)
				+ Tools.bytesToHexString(PayGoodsManAddr.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(
				ReceiveGoodsMan.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(ReceiveGoodsMan.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(
				ReceiveGoodsManPhone.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(ReceiveGoodsManPhone
						.getBytes("gb18030"));

		String raddlenHex = Tools.int2Hexstring(ReceiveGoodsManAddr
				.getBytes("gb18030").length, 4);
		byte[] raddlen = Tools.convertBytePos(Tools.fromHexString(raddlenHex));
		dataHex += Tools.bytesToHexString(raddlen)
				+ Tools.bytesToHexString(ReceiveGoodsManAddr
						.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(Agent.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(Agent.getBytes("gb18030"));

		dataHex += Tools
				.int2Hexstring(AgentPhone.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(AgentPhone.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(
				ExpectedInOutTime.getBytes("gb18030").length / 2, 2)
				+ ExpectedInOutTime;

		dataHex += Tools.int2Hexstring(GoodsType.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(GoodsType.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(DocNum.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(DocNum.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(RefNum.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(RefNum.getBytes("gb18030"));

		dataHex += Tools
				.int2Hexstring(InvoiceNum.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(InvoiceNum.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(Remark.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(Remark.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(Salesman.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(Salesman.getBytes("gb18030"));

		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

		return ret;

	}

	public String sendWayBillExtendData(Request req) throws Exception {
		// <ActivityId></ActivityId>
		// <ConsignmentId></ConsignmentId>
		// <ConsignmentVersion></ConsignmentVersion>
		// <CoustomsNum></CoustomsNum>
		// <ClearNum></ClearNum>
		// <ExceptedThroughTime></ExceptedThroughTime>
		// <BindState></BindState>
		RequestUtil.getDealRequest(req, "", "8480");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		if (ActivityId.length() > 12) {
			this.throwParamException("ActivityId 长度不能大于12字节！");
		}
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}
		String ConsignmentId = RequestUtil.getReqData(req, "ConsignmentId");
		if (ConsignmentId.length() > 12) {
			this.throwParamException("ConsignmentId 长度不能大于12字节！");
		}
		while (ConsignmentId.length() < 12) {
			ConsignmentId = "0" + ConsignmentId;
		}
		String ConsignmentVersion = RequestUtil.getReqData(req, "ConsignmentVersion");
		if (ConsignmentVersion.length() > 6) {
			this.throwParamException("ConsignmentVersion 长度不能大于6字节！");
		}
		while (ConsignmentVersion.length() < 6) {
			ConsignmentVersion = "0" + ConsignmentVersion;
		}

		String CoustomsNum = RequestUtil.getReqData(req, "CoustomsNum");
		if (CoustomsNum.length() > 20) {
			this.throwParamException("CoustomsNum 长度不能大于20字节！");
		}
		String ClearNum = RequestUtil.getReqData(req, "ClearNum");
		if (ClearNum.length() > 20) {
			this.throwParamException("ClearNum 长度不能大于20字节！");
		}

		String ExceptedThroughTime = RequestUtil
				.getReqData(req, "ExceptedThroughTime");

		String BindState = RequestUtil.getReqData(req, "BindState");

		String dataHex = "8480"; // 低字节在前，高字节在后
		dataHex += Tools
				.bytesToHexString((ActivityId + ConsignmentId + ConsignmentVersion)
						.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(CoustomsNum.getBytes("gb18030").length,
				2)
				+ Tools.bytesToHexString(CoustomsNum.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(ClearNum.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(ClearNum.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(
				ExceptedThroughTime.getBytes("gb18030").length, 2)
				+ ExceptedThroughTime;
		dataHex += Tools.convertToHex(BindState, 2);

		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

		return ret;

	}

	public String sendWayBillGoodsList(Request req) throws Exception {
		// <ActivityId></ActivityId>
		// <ConsignmentId></ConsignmentId>
		// <ConsignmentVersion></ConsignmentVersion>
		// <GoodsNums></GoodsNums>
		RequestUtil.getDealRequest(req, "", "8680");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		if (ActivityId.length() > 12) {
			this.throwParamException("ActivityId 长度不能大于12字节！");
		}
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}
		String ConsignmentId = RequestUtil.getReqData(req, "ConsignmentId");
		if (ConsignmentId.length() > 12) {
			this.throwParamException("ConsignmentId 长度不能大于12字节！");
		}
		while (ConsignmentId.length() < 12) {
			ConsignmentId = "0" + ConsignmentId;
		}
		String ConsignmentVersion = RequestUtil.getReqData(req, "ConsignmentVersion");
		if (ConsignmentVersion.length() > 6) {
			this.throwParamException("ConsignmentVersion 长度不能大于6字节！");
		}
		while (ConsignmentVersion.length() < 6) {
			ConsignmentVersion = "0" + ConsignmentVersion;
		}
		String GoodsNums = RequestUtil.getReqData(req, "GoodsNums");
		String[] gdsNums = GoodsNums.split(",");
		String goodsnums = "";
		for (int i = 0; i < gdsNums.length; i++) {
			if (gdsNums[i].length() > 12) {
				this.throwParamException("货品编号 长度不能大于12字节！");
			}
			String waybillnum = gdsNums[i];
			while (waybillnum.length() < 12) {
				waybillnum = "0" + waybillnum;
			}
			goodsnums += waybillnum;
		}

		String dataHex = "8680";
		dataHex += Tools
				.bytesToHexString((ActivityId + ConsignmentId + ConsignmentVersion)
						.getBytes("gb18030"));
		dataHex += Tools.int2Hexstring(gdsNums.length, 2)
				+ Tools.bytesToHexString(goodsnums.getBytes("gb18030"));

		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

		return ret;

	}

	public String sendWayBillGoods(Request req) throws Exception {
		// <GoodsCount></GoodsCount>
		// <GoodsDetails>
		// <GoodsNum></GoodsNum>
		// <GoodsDesc></GoodsDesc>
		// <GoodsQuantity></GoodsQuantity>
		// <GoodsQuantityUnit></GoodsQuantityUnit>
		// </GoodsDetails>
		// <GoodsDetails>
		// <GoodsNum></GoodsNum>
		// <GoodsDesc></GoodsDesc>
		// <GoodsQuantity></GoodsQuantity>
		// <GoodsQuantityUnit><GoodsQuantityUnit>
		// </GoodsDetails>
		// </service>
		// </request>
		RequestUtil.getDealRequest(req, "", "8880");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		if (ActivityId.length() > 12) {
			this.throwParamException("ActivityId 长度不能大于12字节！");
		}
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}
		String ConsignmentId = RequestUtil.getReqData(req, "ConsignmentId");
		if (ConsignmentId.length() > 12) {
			this.throwParamException("ConsignmentId 长度不能大于12字节！");
		}
		while (ConsignmentId.length() < 12) {
			ConsignmentId = "0" + ConsignmentId;
		}
		String GoodsCount = "";// RequestUtil.getReqData(req, "GoodsCount");
		ArrayList<Parameters> GoodsDetails = (ArrayList) req.getDatas().get(
				"GoodsDetails");
		GoodsCount = "1";// GoodsDetails.size() + "";

		String lastGoods = "";
		for (int i = 0; i < GoodsDetails.size(); i++) {
			String details = "";
			Parameters p = GoodsDetails.get(i);
			HashMap<String, String> pv = p.getNoChildSubNodeFromLayer4();
			String GoodsNum = pv.get("GoodsNum");
			if (GoodsNum.length() > 12) {
				this.throwParamException("GoodsNum 长度不能大于12字节！");
			}
			while (GoodsNum.length() < 12) {
				GoodsNum = "0" + GoodsNum;
			}
			String GoodsDesc = pv.get("GoodsDesc");
			if (GoodsDesc.length() > 200) {
				this.throwParamException("GoodsDesc 长度不能大于200字节！");
			}
			String GoodsQuantity = pv.get("GoodsQuantity") == "" ? "0" : pv
					.get("GoodsQuantity");
			String qtyHex = Tools.convertToHex(GoodsQuantity, 8);

			byte[] b = Tools.convertBytePos(Tools.fromHexString(qtyHex));
			String GoodsQuantityUnit = pv.get("GoodsQuantityUnit");
			if (GoodsQuantityUnit.length() > 10) {
				this.throwParamException("GoodsQuantityUnit 长度不能大于10字节！");
			}

			details += Tools.bytesToHexString(GoodsNum.getBytes("gb18030"))
					+ Tools.int2Hexstring(GoodsDesc.getBytes("gb18030").length,
							2)
					+ Tools.bytesToHexString(GoodsDesc.getBytes("gb18030"));

			details += Tools.bytesToHexString(b)
					+ Tools.int2Hexstring(
							GoodsQuantityUnit.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(GoodsQuantityUnit
							.getBytes("gb18030"));

			if (i != GoodsDetails.size() - 1) {
				String dataHex = "8880";
				dataHex += Tools.bytesToHexString((ActivityId + ConsignmentId)
						.getBytes("gb18030"))
						+ Tools.convertToHex(GoodsCount, 2);
				dataHex += details;
				String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

				ICommonGatewayService gateway = new CommonGatewayServiceImpl();
				gateway.sendDataToTcpTerminal(deviceId, Tools
						.fromHexString(ret), null);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				lastGoods = details;
				break;
			}

		}
		String dataHex = "8880";
		dataHex += Tools.bytesToHexString((ActivityId + ConsignmentId)
				.getBytes("gb18030"))
				+ Tools.convertToHex(GoodsCount, 2);
		dataHex += lastGoods;
		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);
		return ret;
	}

	public String sendHalfWayPoint(Request req)
			throws UnsupportedEncodingException {
		// <ActivityId></ActivityId>
		// <ConsignmentId></ConsignmentId>
		// <HalfWayPointCount></HalfWayPointCount>
		// <HalfWayDetails>
		// <NodeId></NodeId>
		// <NodeVersion></NodeVersion>
		// <PreNodeIdList></PreNodeIdList>
		// <HalfWayPointPurpose></HalfWayPointPurpose>
		// <HalfWayPointDetail></HalfWayPointDetail>
		// <HalfWayPointPlace></HalfWayPointDetail>
		// <PlanArriveTime></PlanArriveTime>
		// <AreaIdList></AreaIdList>
		// </HalfWayDetails>
		RequestUtil.getDealRequest(req, "", "8980");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}

		ArrayList<Parameters> HalfWayDetails = (ArrayList) req.getDatas().get(
				"HalfWayDetails");
		String HalfWayPointCount = "1";// HalfWayDetails.size() + "";//
		// RequestUtil.getReqData(req,
		// "HalfWayPointCount");

		String lastNode = "";
		for (int i = 0; i < HalfWayDetails.size(); i++) {
			String details = "";
			Parameters p = HalfWayDetails.get(i);
			HashMap<String, String> pv = p.getNoChildSubNodeFromLayer4();
			String NodeId = pv.get("NodeId");
			if (NodeId == null)
				NodeId = "";
			while (NodeId.length() < 12) {
				NodeId = "0" + NodeId;
			}

			String NodeVersion = pv.get("NodeVersion");
			if (NodeVersion == null)
				NodeVersion = "";
			while (NodeVersion.length() < 6) {
				NodeVersion = "0" + NodeVersion;
			}
			String PreNodeIdList = pv.get("PreNodeIdList");
			if (PreNodeIdList == null)
				PreNodeIdList = "";

			String prenodes = "";
			int prenodecount = 0;

			String[] pnodelist = PreNodeIdList.split(",");
			for (int j = 0; j < pnodelist.length; j++) {

				String tmpnode = pnodelist[j].trim();
				if (tmpnode.equals(""))
					continue;
				while (tmpnode.length() < 12) {
					tmpnode = "0" + tmpnode;
				}
				prenodes += tmpnode;
			}

			if (PreNodeIdList.trim().equals("")) {
				prenodes = "";
				prenodecount = 0;
			} else {
				prenodecount = pnodelist.length;
			}

			String HalfWayPointPurpose = pv.get("HalfWayPointPurpose") == null ? ""
					: pv.get("HalfWayPointPurpose");
			String HalfWayPointDetail = pv.get("HalfWayPointDetail") == null ? ""
					: pv.get("HalfWayPointDetail");
			String HalfWayPointPlace = pv.get("HalfWayPointPlace") == null ? ""
					: pv.get("HalfWayPointPlace");
			String PlanArriveTime = pv.get("PlanArriveTime") == null ? "" : pv
					.get("PlanArriveTime");
			String AreaIdList = pv.get("AreaIdList") == null ? "" : pv
					.get("AreaIdList");

			String areaids = "";
			int areacount = 0;
			String areas[] = AreaIdList.split(",");

			for (int k = 0; k < areas.length; k++) {
				String tmpid = areas[k].trim();
				if (tmpid.equals(""))
					continue;
				while (tmpid.length() < 12) {
					tmpid = "0" + tmpid;
				}
				areaids += tmpid;
			}
			if (AreaIdList.trim().equals("")) {
				areaids = "";
				areacount = 0;
			} else {
				areacount = areas.length;
			}

			details += Tools.bytesToHexString((NodeId + NodeVersion)
					.getBytes("gb18030"));

			details += Tools.int2Hexstring(prenodecount, 2)
					+ Tools.bytesToHexString(prenodes.getBytes("gb18030"));

			details += Tools.int2Hexstring(HalfWayPointPurpose
					.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(HalfWayPointPurpose
							.getBytes("gb18030"));

			details += Tools.int2Hexstring(HalfWayPointDetail
					.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(HalfWayPointDetail
							.getBytes("gb18030"));

			int pleng = HalfWayPointPlace.getBytes("gb18030").length;
			String qtyHex = Tools.convertToHex(pleng + "", 4);

			byte[] b = Tools.convertBytePos(Tools.fromHexString(qtyHex));

			details += Tools.bytesToHexString(b)
					+ Tools.bytesToHexString(HalfWayPointPlace
							.getBytes("gb18030"));

			details += PlanArriveTime;

			details += Tools.int2Hexstring(areacount, 2)
					+ Tools.bytesToHexString(areaids.getBytes("gb18030"));
			if (i != HalfWayDetails.size() - 1) {
				String dataHex = "8980";

				dataHex += Tools.bytesToHexString((ActivityId)
						.getBytes("gb18030"))
						+ Tools.convertToHex(HalfWayPointCount, 2) + details;

				String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);
				ICommonGatewayService gateway = new CommonGatewayServiceImpl();
				gateway.sendDataToTcpTerminal(deviceId, Tools
						.fromHexString(ret), null);
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				lastNode = details;
				break;
			}

		}

		String dataHex = "8980";

		dataHex += Tools.bytesToHexString((ActivityId).getBytes("gb18030"))
				+ Tools.convertToHex(HalfWayPointCount, 2) + lastNode;

		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

		return ret;
	}

	public String confirmChangeTaskState(Request req)
			throws UnsupportedEncodingException {
		// <ActivityId></ActivityId>
		// <ActivityVersion></ActivityVersion>
		// <State ></State>
		RequestUtil.getDealRequest(req, "", "9280");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}
		String ActivityVersion = RequestUtil.getReqData(req, "ActivityVersion");
		while (ActivityVersion.length() < 6) {
			ActivityVersion = "0" + ActivityVersion;
		}
		String State = RequestUtil.getReqData(req, "State");
		String dataHex = "9280"
				+ Tools.bytesToHexString((ActivityId + ActivityVersion)
						.getBytes("gb18030")) + Tools.convertToHex(State, 2);
		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);
		return ret;
	}

	public String confirmChangeHalfWayState(Request req)
			throws UnsupportedEncodingException {
		// <ActivityId></ActivityId>
		// <NodeId ></NodeId>
		// <NodeVersion></NodeVersion>
		// <State></State>
		RequestUtil.getDealRequest(req, "", "9380");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}
		String NodeId = RequestUtil.getReqData(req, "NodeId");
		while (NodeId.length() < 12) {
			NodeId = "0" + NodeId;
		}
		String NodeVersion = RequestUtil.getReqData(req, "NodeVersion");
		while (NodeVersion.length() < 6) {
			NodeVersion = "0" + NodeVersion;
		}
		String State = RequestUtil.getReqData(req, "State");
		String dataHex = "9380"
				+ Tools.bytesToHexString((ActivityId + NodeId + NodeVersion)
						.getBytes("gb18030")) + Tools.convertToHex(State, 2);
		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);
		return ret;
	}

	public String confirmChangeExpectedArriveTime(Request req)
			throws UnsupportedEncodingException {
		// <ActivityId></ActivityId>
		// <NodeId ></NodeId>
		// <NodeVersion></NodeVersion>
		// <Flag></Flag>
		RequestUtil.getDealRequest(req, "", "9480");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}
		String NodeId = RequestUtil.getReqData(req, "NodeId");
		while (NodeId.length() < 12) {
			NodeId = "0" + NodeId;
		}
		String NodeVersion = RequestUtil.getReqData(req, "NodeVersion");
		while (NodeVersion.length() < 6) {
			NodeVersion = "0" + NodeVersion;
		}
		String Flag = RequestUtil.getReqData(req, "Flag");
		String dataHex = "9480"
				+ Tools.bytesToHexString((ActivityId + NodeId + NodeVersion)
						.getBytes("gb18030")) + Tools.convertToHex(Flag, 2);
		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);
		return ret;
	}

	public String sendCommonMsg(Request req)
			throws UnsupportedEncodingException {
		// <MessageId></MessageId>
		// <ActivityId></ActivityId>
		// <NodeId ></NodeId>
		// <ConsignmentId ></ConsignmentId>
		// <ValidDate ></ValidDate>
		// <InvalidDate></InvalidDate>
		// <Type></Type>
		// <Title></Title>
		// <Content></Content>
		// <DisplayKey></DisplayKey>
		// <DisplayIcon></DisplayIcon>
		// <Sender></Sender>
		// <DriverNum></DriverNum>
		// <IsDisplay></IsDisplay>
		// <IsPrint></IsPrint>
		// <IsReply></IsReply>
		// <MainCatalog></MainCatalog>
		// <ViceCatalog></ViceCatalog>
		RequestUtil.getDealRequest(req, "", "9a80");
		String deviceId = req.getDeviceId();
		String MessageId = RequestUtil.getReqData(req, "MessageId");
		while (MessageId.length() < 12) {
			MessageId = "0" + MessageId;
		}
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		String NodeId = RequestUtil.getReqData(req, "NodeId");
		String ConsignmentId = RequestUtil.getReqData(req, "ConsignmentId");
		String ValidDate = RequestUtil.getReqData(req, "ValidDate");
		String InvalidDate = RequestUtil.getReqData(req, "InvalidDate");
		String Type = RequestUtil.getReqData(req, "Type");// 消息类别
		String Title = RequestUtil.getReqData(req, "Title");
		String Content = RequestUtil.getReqData(req, "Content");
		String DisplayKey = RequestUtil.getReqData(req, "DisplayKey");
		String DisplayIcon = RequestUtil.getReqData(req, "DisplayIcon");
		String Sender = RequestUtil.getReqData(req, "Sender");
		String DriverNum = RequestUtil.getReqData(req, "DriverNum");
		String IsDisplay = RequestUtil.getReqData(req, "IsDisplay");
		String IsPrint = RequestUtil.getReqData(req, "IsPrint");
		String IsReply = RequestUtil.getReqData(req, "IsReply");
		String MainCatalog = RequestUtil.getReqData(req, "MainCatalog");
		String ViceCatalog = RequestUtil.getReqData(req, "ViceCatalog");
		String dataHex = "9a80"
				+ Tools.bytesToHexString(MessageId.getBytes("gb18030"));
		dataHex += Tools
				.int2Hexstring(ActivityId.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(ActivityId.getBytes("gb18030"));
		dataHex += Tools.int2Hexstring(
				ConsignmentId.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(ConsignmentId.getBytes("gb18030"));

		dataHex += Tools.int2Hexstring(NodeId.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(NodeId.getBytes("gb18030"));
		dataHex += ValidDate + InvalidDate + Tools.convertToHex(Type, 2);
		try {
			dataHex += Tools.int2Hexstring(Title.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(Title.getBytes("gb18030"));
			dataHex += Tools.int2Hexstring(Content.getBytes("gb18030").length,
					2)
					+ Tools.bytesToHexString(Content.getBytes("gb18030"));

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataHex += Tools.convertToHex(DisplayKey, 2)
				+ Tools.convertToHex(DisplayIcon, 2);
		dataHex += Tools.int2Hexstring(Sender.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(Sender.getBytes("gb18030"));
		dataHex += Tools.int2Hexstring(DriverNum.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(DriverNum.getBytes("gb18030"));
		dataHex += Tools.convertToHex(IsDisplay, 2)
				+ Tools.convertToHex(IsPrint, 2)
				+ Tools.convertToHex(IsReply, 2);
		dataHex += Tools.int2Hexstring(MainCatalog.getBytes("gb18030").length,
				2)
				+ Tools.bytesToHexString(MainCatalog.getBytes("gb18030"));
		dataHex += Tools.int2Hexstring(ViceCatalog.getBytes("gb18030").length,
				2)
				+ Tools.bytesToHexString(ViceCatalog.getBytes("gb18030"));
		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

		return ret;
	}

	public String sendFileRequest(Request req)
			throws UnsupportedEncodingException {
		// <ActivityId></ActivityId>
		// <ConsignmentId ></ConsignmentId>
		// <NodeId ></NodeId>
		// <FileId></FileId>
		// <FileType></FileType>
		// <Title></Title>
		// <Content></Content>
		// <FileFormat></FileFormat>
		// <FileLength ></FileLength>
		// <FileUrl></FileUrl>
		RequestUtil.getDealRequest(req, "", "9c80");
		String deviceId = req.getDeviceId();
		String FileId = RequestUtil.getReqData(req, "FileId");
		while (FileId.length() < 12) {
			FileId = "0" + FileId;
		}
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		String NodeId = RequestUtil.getReqData(req, "NodeId");
		String ConsignmentId = RequestUtil.getReqData(req, "ConsignmentId");
		String Title = RequestUtil.getReqData(req, "Title");
		String Content = RequestUtil.getReqData(req, "Content");
		String FileType = RequestUtil.getReqData(req, "FileType");// 消息类别
		String FileFormat = RequestUtil.getReqData(req, "FileFormat");
		String FileLength = RequestUtil.getReqData(req, "FileLength");
		String fleng = Tools.convertToHex(FileLength, 4);
		byte[] bflen = Tools.convertBytePos(Tools.fromHexString(fleng));
		String FileUrl = RequestUtil.getReqData(req, "FileUrl");
		String dataHex = "9c80"
				+ Tools.bytesToHexString(FileId.getBytes("gb18030"));
		dataHex += Tools
				.int2Hexstring(ActivityId.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(ActivityId.getBytes("gb18030"));
		dataHex += Tools.int2Hexstring(
				ConsignmentId.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(ConsignmentId.getBytes("gb18030"));
		dataHex += Tools.int2Hexstring(NodeId.getBytes("gb18030").length, 2)
				+ Tools.bytesToHexString(NodeId.getBytes("gb18030"));
		try {
			dataHex += Tools.int2Hexstring(Title.getBytes("gb18030").length, 2)
					+ Tools.bytesToHexString(Title.getBytes("gb18030"));
			dataHex += Tools.int2Hexstring(Content.getBytes("gb18030").length,
					2)
					+ Tools.bytesToHexString(Content.getBytes("gb18030"));
			dataHex += Tools.int2Hexstring(FileType.getBytes("gb18030").length,
					2)
					+ Tools.bytesToHexString(FileType.getBytes("gb18030"));
			dataHex += Tools.convertToHex(FileFormat, 2);
			dataHex += Tools.bytesToHexString(bflen);
			dataHex += Tools.int2Hexstring(FileUrl.getBytes("gb18030").length,
					2)
					+ Tools.bytesToHexString(FileUrl.getBytes("gb18030"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

		return ret;
	}

	public String modifyBindState(Request req) throws Exception {
		// <ActivityId></ActivityId>
		// <ConsignmentId ></ConsignmentId>
		// < ConsignmentVersion ></ ConsignmentVersion>
		// <State></State>
		RequestUtil.getDealRequest(req, "", "a180");
		String deviceId = req.getDeviceId();
		String ActivityId = RequestUtil.getReqData(req, "ActivityId");
		while (ActivityId.length() < 12) {
			ActivityId = "0" + ActivityId;
		}
		String ConsignmentId = RequestUtil.getReqData(req, "ConsignmentId");
		while (ConsignmentId.length() < 12) {
			ConsignmentId = "0" + ConsignmentId;
		}
		String ConsignmentVersion = RequestUtil.getReqData(req, "ConsignmentVersion");
		while (ConsignmentVersion.length() < 6) {
			ConsignmentVersion = "0" + ConsignmentVersion;
		}
		String State = RequestUtil.getReqData(req, "State");
		String dataHex = "a180"
				+ Tools
						.bytesToHexString((ActivityId + ConsignmentId + ConsignmentVersion)
								.getBytes("gb18030"))
				+ Tools.convertToHex(State, 2);
		String ret = OBUUtil.downWirelessTransfer(deviceId, dataHex);

		return ret;
	}

	public static void main(String[] args) {
		String Content = "點速度的飛灑是否222饿";
		String Title = "";
		if (Content.length() > 10)
			Title = Content.substring(0, 10);
		else
			Title = Content;
		System.out.println(Title);
	}

}
