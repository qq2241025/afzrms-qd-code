/**
 * 
 */
package com.mapabc.gater.directl.parse.guomai;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Base64;
import com.mapabc.gater.directl.CRC;
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.ParseConfigParamUtil;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.constant.AlarmType;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.gater.directl.encode.guomai.GuoMaiProtocalUtil;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.directl.pic.PicCache;
import com.mapabc.gater.directl.pic.Picture;
import com.mapabc.gater.directl.pic.PictureKey;
import com.mapabc.gater.lbsgateway.GBLTerminalList; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.util.OverLoadUtil;

/**
 * @author shiguang.zhou
 * 
 */
public class ParseGuoMai extends ParseBase implements ParseService{
	
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseGuoMai.class);
	
	TTerminal term = null;

	private String typeCode = "GP-GUOMAIC-GPRS";
	private String head = "7e";
	private String end = "7f";
	private String iseq = "";
	private String oemCode = "";
	private String sCenterId = "";
	private String sCenPwd = "";
	private static String isOverLoad = null;// 是否有负载

	TTermStatusRecord termStatus = new TTermStatusRecord();

	public ParseGuoMai() {

	}

	private void parsePhoto(String hexString) {
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
		byte[] cont = Tools.fromHexString(hexString);
		// 55aa aaaa aaaaaaaa
		// byte[] tid = Tools.fromHexString(hexString.substring(4, 8));
		String stid = Integer.parseInt(hexString.substring(6, 8)
				+ hexString.substring(4, 6), 16)
				+ "";
		// byte[] vid = Tools.fromHexString(hexString.substring(8, 16));
		String deviceId = Integer.parseInt(hexString.substring(8, 16), 16) + "";
		this.setDeviceSN(deviceId);
		this.setPhnum(deviceId);

		String msg = hexString.substring(16, 20);
		int leng = Integer.parseInt(hexString.substring(22, 24)
				+ hexString.substring(20, 22), 16);

		int channel = Integer.parseInt(hexString.substring(24, 26), 16);
		int picnum = Integer.parseInt(hexString.substring(28, 30)
				+ hexString.substring(26, 28), 16);// 图像编号从0开始

		int packNum = Integer.parseInt(hexString.substring(32, 34)
				+ hexString.substring(30, 32), 16);// 图像包号从0开始
		packNum = packNum + 1;// 使缓存包号从1开始

		byte[] picbytes = Tools.fromHexString(hexString.substring(34));// 图像数据包内容
		log.info(
				this.getDeviceSN() + " 图片数据:编号=" + picnum + ",当前包号=" + packNum
						+ ",通道=" + channel + "，数据包长度=" + leng + ",数据包内容："
						+ hexString.substring(34));

		byte[] repByte = Tools.fromHexString(hexString.substring(0, 34));// 中心应当
		this.setReplyByte(repByte);
		PictureKey pkey = new PictureKey();
		pkey.setDeviceId(deviceId);
		pkey.setPicNum(picnum);
		pkey.setChannel(channel);

		// if (packNum == 1) {//要求终端必须首先上传第一个包，否则丢弃不处理
		// Picture pic = new Picture();
		// pic.setFirstReq(true);
		// pic.setNum(picnum);
		// pic.setPakcNo(packNum);
		// pic.setChanelNo(channel+"");
		// pic.setDeviceId(deviceId);
		// pic.setDate(new Date());
		// pic.setImgStrCont(Tools.bytesToHexString(picbytes));
		// pic.setTimeStamp(new Timestamp(System.currentTimeMillis()));
		// pic.addImgContHex(packNum+"",Tools.bytesToHexString(picbytes));
		// log.info(this.getDeviceSN()+"
		// 图像编号："+picnum+",第一包数据包号:"+packNum);
		//			
		// PicCache.getInstance().addMorePictureObj(pkey, pic);
		// } else
		{

			Picture picture = PicCache.getInstance().getPicture(pkey);

			if (picture != null && picture.isFirstReq()) {
				picture.setNum(picnum);
				picture.setPakcNo(packNum);
				picture.setChanelNo(channel + "");
				picture.setDeviceId(deviceId);
				picture.setDate(new Date());
				picture.setTimeStamp(new Timestamp(System.currentTimeMillis()));
				picture.addImgContHex(packNum + "", Tools
						.bytesToHexString(picbytes));
				log.info(
						this.getDeviceSN() + " 图像编号：" + picnum + ",数据包号:"
								+ packNum);

				if (picbytes.length < 1024) {
					// 图片传输完毕
					picture.setPackcounts(packNum);
					Log.getInstance()
							.outJHSLoger(this.getDeviceSN() + " 最后一包！");
				}
				PicCache.getInstance().addMorePictureObj(pkey, picture);

				if (picture.isGuoMaiTraverOver()) {
					log.info(
							this.getDeviceSN() + " " + picnum + "编号图片传入完毕！");
					DBService service = new DBServiceImpl();
					service.insertPicInfo(picture);
					picture.reset();
					PicCache.getInstance().removePicture(pkey);

				}
			} else if (picture == null) {
				Picture pic = new Picture();
				pic.setFirstReq(true);
				pic.setNum(picnum);
				pic.setPakcNo(packNum);
				pic.setChanelNo(channel + "");
				pic.setDeviceId(deviceId);
				pic.setDate(new Date());
				pic.setImgStrCont(Tools.bytesToHexString(picbytes));
				pic.setTimeStamp(new Timestamp(System.currentTimeMillis()));
				pic.addImgContHex(packNum + "", Tools
						.bytesToHexString(picbytes));
				log.info(
						this.getDeviceSN() + " 图像编号：" + picnum + ",第一包数据包号:"
								+ packNum);

				PicCache.getInstance().addMorePictureObj(pkey, pic);

			} else if (isOverLoad != null && isOverLoad.equals("1")) {
				// 负载转发
				if (udpAddr != null) {
					String host = udpAddr.split(":")[0];
					String sport = udpAddr.split(":")[1];
					int port = Integer.parseInt(sport);

					OverLoadUtil.sendToUdp(host, port, cont);
				}
			}
		}

	}
 
	public void parseGPRS(String hexString) {
		// TODO Auto-generated method stub
		log.info("国脉GPS原始数据：" + hexString);
		// if (!hexString.startsWith("7e") || !hexString.startsWith("55aa")) {
		// log.info("国脉终端上传数据格式有误,"+hexString);
		// return;
		// }

		if (hexString.startsWith("aa55")) {// 图片数据
			this.parsePhoto(hexString);
			return;
		}

		byte[] ucont = Tools.fromHexString(hexString);

		byte[] decode64Data = Base64.decode(ucont);
		log.info(
				"\r\n国脉原始数据BASE64解码：" + Tools.bytesToHexString(decode64Data));

		byte[] resEspB = GuoMaiProtocalUtil.reverseEscape(decode64Data);
		log.info(
				"国脉原始数据反转义：" + Tools.bytesToHexString(resEspB));

		String bs64Esp = "7e" + Tools.bytesToHexString(resEspB) + "7f";

		byte[] crcTmp = new byte[resEspB.length - 2];// 待验证CRS结果的字节
		System.arraycopy(resEspB, 0, crcTmp, 0, crcTmp.length);

		byte[] crcCode = new byte[2];// 上传的CRS结果
		System.arraycopy(resEspB, resEspB.length - 2, crcCode, 0,
				crcCode.length);
		int crcR = Tools.byte2Int(crcCode);
		int crcRes = CRC.CRC_CCITT(crcTmp);// 服务端校验CRS结果

		byte cycleCode = resEspB[1];// 循环码
		iseq = Tools.byte2Int(new byte[] { cycleCode }) + "";

		byte[] cscode = new byte[2];// 厂商代码
		System.arraycopy(crcTmp, 2, cscode, 0, cscode.length);
		oemCode = Tools.byte2Int(cscode) + "";

		byte[] snb = new byte[4];// 设备ID
		System.arraycopy(crcTmp, 4, snb, 0, snb.length);
		String deviceId = "" + Tools.byte2Int(snb);
		this.setDeviceSN(deviceId);

		term = GBLTerminalList.getInstance().getTerminaInfo(deviceId);//
		// 从内存获取终端SIMCARD
		if (term == null) {
			log.info("系统中没有适配到指定的终端：device_id=" + deviceId);
			return;
		}

		//this.setObjId(term.getObjId());
		//this.setObjType(term.getObjType());
		this.setPhnum(term.getSimcard());
		typeCode = term.getTEntTermtype();

		log.info(
				this.getDeviceSN() + " 接收到CRC检验码为：" + crcR + ",服务端验证校验码为："
						+ crcRes);

		byte[] centerId = new byte[4];// 中心ID
		System.arraycopy(crcTmp, 8, centerId, 0, centerId.length);
		sCenterId = Tools.byte2Int(centerId) + "";

		byte[] pwdb = new byte[4];// 密码
		System.arraycopy(crcTmp, 12, pwdb, 0, pwdb.length);
		sCenPwd = Tools.byte2Int(pwdb) + "";

		byte[] lengb = new byte[2];// 协议长度
		System.arraycopy(crcTmp, 16, lengb, 0, lengb.length);

		byte[] ptlCont = new byte[Tools.byte2Int(lengb)];// 协议流向标志+协议号+内容
		System.arraycopy(crcTmp, 18, ptlCont, 0, ptlCont.length);

		int key = 0;
		if (this.typeCode != null && this.typeCode.equals("GP-GUOMAIP-GPRS"))
			key = Integer.parseInt(sCenPwd);
		else
			key = Integer.parseInt(deviceId);

		byte[] encrypt = GuoMaiProtocalUtil.gmEnCrypt(key, Tools
				.fromHexString(bs64Esp));
		log.info(deviceId + " 解密前数据：" + bs64Esp);
		log.info(
				deviceId + " 解密后数据：" + Tools.bytesToHexString(encrypt));

		byte flag = encrypt[19];// 流向标志，上行或下行
		byte ptlNo = encrypt[20];// 协议号
		log.info(
				this.getDeviceSN() + " 命令id:"
						+ Tools.bytesToHexString(new byte[] { ptlNo }));
		switch (ptlNo) {

		case (byte) 0x00:// 位置信息
			parseCommonPos(encrypt);
			break;
		case (byte) 0x01:// 点名位置信息
			singlePos(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "01", "0");
			break;
		case (byte) 0x02:// 频率设置应答
			freqResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "02", "0");
			break;
		case (byte) 0x04:// 定距离设置应答
			distanceSetResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "04", "0");
			break;
		case (byte) 0x06:// 超速设置应答
			this.speedSetResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "06", "0");
			break;
		case (byte) 0x07:// 围栏设置应答
			this.areaSetResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "07", "0");
			break;
		case (byte) 0x08:// 查看围栏应答
			this.viewAreaResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "08", "0");
			break;
		case (byte) 0x09:// 围栏监控设置应答
			this.areaControlResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "09", "0");
			break;
		case (byte) 0x0a:// 查看围栏监控设置应答
			this.viewAreaControlResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "0a", "0");
			break;
		case (byte) 0x0b:// 线路报警应答
			this.lineSettingResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "0b", "0");
			break;
		case (byte) 0x0c:// 道路设定查看
			this.viewLineSettingResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "0c", "0");
			break;
		case (byte) 0x0d:// 道路监控设置应答
			this.lineControlResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "0d", "0");
			break;
		case (byte) 0x0e:// 查看道路监控应答
			this.viewLineControlResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "0e", "0");
			break;
		case (byte) 0x0f:// 终端自检设置应答
			selfCheckResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "0f", "0");
			break;
		case (byte) 0x10:// 疑点数据上报
			this.doubtPos(encrypt);
			break;
		case (byte) 0x11:// 历史轨迹上报
			this.parseHistoryPostion(encrypt);
			break;
		case (byte) 0x12:// 上报驾驶员信息
			this.qureyDriverInfoRes(encrypt);
			break;
		case (byte) 0x13:// 打印前上报信息
			this.printPos(encrypt);
			break;
		case (byte) 0x14:// 疲劳驾驶设置应答
			fatigueDriveSet(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "14", "0");
			break;
		case (byte) 0x15:// 下行消息终端应答
			this.sendMessageRes(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "15", "0");
			break;
		case (byte) 0x16:// 上行消息
			this.receiveMsg(encrypt);
			break;
		case (byte) 0x17:// 主动报警
			this.activeAlarm(encrypt);
			break;
		case (byte) 0x18:// 解除紧急报警应答
			this.cancelActiveParam(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "18", "0");
			break;
		case (byte) 0x19:// 断油电控制应答
			this.oilElecResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "19", "0");
			break;
		case (byte) 0x1a:// 远程终端参数查询指令应答
			this.queryBasedParamRes(encrypt);

			// ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "1a", "0");
			break;
		case (byte) 0x1b:// 中心设置远程参数，终端应答
			this.basedParamSetRes(encrypt);

			break;
		case (byte) 0x1c:// 控制终端复位，终端应答，应答后1分钟设备重启
			this.resetRecoverRes(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "1c", "0");
			break;
		case (byte) 0xf0:// 终端登录
			this.longin("F0");
			break;
		case (byte) 0xf1:// 插IC上报
			this.intertICPos(encrypt);
			break;
		case (byte) 0xf2:// 拔IC卡上报
			this.outICPos(encrypt);
			break;
		case (byte) 0xf3:// 盲区补偿数据
			this.blindPos(encrypt);
			break;
		case (byte) 0xf4:// 里程上报
			this.tenDaysDis(encrypt);
			break;
		case (byte) 0xf8:// 扩展设置命令回馈
			this.extendSetReply(encrypt);
			break;

		case (byte) 0xe0:// 终端登录
			this.longin("E0");
			break;
		case (byte) 0xE1:// 市标车机登陆//UCAST登录
			this.cityLogin(encrypt);
			break;
		case (byte) 0xe2:// 驾驶员状态上报
			this.cityDriverStaus(encrypt);
			break;
		case (byte) 0xe3:// 盲区补偿数据
			this.blindPos(encrypt);
			break;
		case (byte) 0xe4:// 

			break;
		case (byte) 0xe5:// F0-FF协议控制应答
			parseSwitchFxCmd(encrypt);
			break;
		case (byte) 0xe8://  
			break;
		case (byte) 0xf5:// 拍照应答
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "f5", "0");
			log.info(this.getDeviceSN() + " 拍照应答。");
			break;
		default:
			log.info(
					"UCAST终端---------------" + this.getDeviceSN()
							+ "------------未知数据");
		}

	}

	private void parseSwitchFxCmd(byte[] encrypt) {
		byte[] switchStatus = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, switchStatus, 0, switchStatus.length);

		log.info(
				this.getDeviceSN() + " 开启F0-FF功能应答："
						+ Tools.bytesToHexString(switchStatus));

	}

	private void viewLineControlResponse(byte[] encrypt) {

		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);
		byte lineTotal = paramb[0];
		for (int i = 1; i < paramb.length; i += 9) {
			byte lineNo = paramb[i];
			byte limitSpeed = paramb[i + 1];
			byte type = paramb[i + 2];
			byte[] time = new byte[4];
			System.arraycopy(paramb, i + 3, time, 0, 4);
			byte[] offset = new byte[2];
			// System.arraycopy(paramb, i+, dest, destPos, length)
		}

		log.info(
				"UCAST终端---------" + this.getDeviceSN()
						+ "--------查看路线监控设置应答:param="
						+ Tools.bytesToHexString(paramb));

	}

	private void lineControlResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		log.info(
				"UCAST终端---------" + this.getDeviceSN()
						+ "--------路线监控设置应答: param = "
						+ Tools.bytesToHexString(paramb));

	}

	// 查看线路设定 应答
	private void viewLineSettingResponse(byte[] encrypt) {
		byte[] content = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, content, 0, content.length);

		log.info(
				"UCAST终端---------" + this.getDeviceSN()
						+ "--------道路设定查看应答: param = "
						+ Tools.bytesToHexString(content));

	}

	private void lineSettingResponse(byte[] encrypt) {
		byte[] content = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, content, 0, content.length);

		byte[] total = new byte[1];
		System.arraycopy(content, 0, total, 0, 1);
		int intTotal = Tools.byte2Int(total);

		log.info(
				"UCAST终端---------" + this.getDeviceSN()
						+ "--------道路设定回应，线路总数为:" + intTotal);
	}

	private void extendSetReply(byte[] encrypt) {
		byte[] content = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, content, 0, content.length);

		byte type = content[0];// type
		byte[] gps = new byte[30];
		System.arraycopy(content, 1, gps, 0, 30);
		this.parsePosition(gps, this);

		byte[] con = new byte[content.length - 31];
		System.arraycopy(content, 31, con, 0, con.length);

		switch (type) {
		case 0:
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "f80", "0");
			String interval = Tools.bytesToHexString(con);
			log.info(
					this.getDeviceSN() + "熄火上报时间间隔回复，设置间隔为："
							+ Integer.parseInt(interval, 16));
			break;
		case 1:
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "f81", "0");
			String GPSMile = Tools.bytesToHexString(con);
			log.info(
					this.getDeviceSN() + "GPS总里程查询回复，总里程为："
							+ Integer.parseInt(GPSMile, 16));
			break;
		case 2:
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "f82", "0");
			String setGPSMile = Tools.bytesToHexString(con);
			log.info(
					this.getDeviceSN() + "GPS总里程设置回复，设置的总里程为："
							+ Integer.parseInt(setGPSMile, 16));
			break;
		case 3:
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "f83", "0");
			switch (con[1]) {
			case (byte) 0xaa:
				log.info(
						this.getDeviceSN() + "设置普通定位包中里程的上报类型回复，设置为启动GPS里程");
				break;
			case (byte) 0x55:
				log.info(
						this.getDeviceSN() + "设置普通定位包中里程的上报类型回复，设置为启动记录仪里程。");
				break;
			}
			break;
		}

	}

	// 历史轨迹上报
	private void parseHistoryPostion(byte[] encrypt) {
		ArrayList<ParseBase> plist = new ArrayList<ParseBase>();

		for (int i = 0; i < encrypt.length - 24; i += 13) {
			ParseGuoMai gm = new ParseGuoMai();
//			gm.setDeviceSN(this.getDeviceSN());
//			gm.setObjId(term.getObjId());
//			gm.setObjType(term.getObjType());
//			gm.setPhnum(term.getSimcard());

			if (i + 21 + 30 > encrypt.length)
				continue;

			byte[] hpos = new byte[13];
			System.arraycopy(encrypt, 21 + i, hpos, 0, 13);

			byte[] bx = new byte[4];// 经度
			System.arraycopy(hpos, 0, bx, 0, 4);
			String x = Tools.formatXtoDu(this.removeZeroStr(Tools.bcd2Str(bx)));
			// this.setCoordX(x);

			byte[] by = new byte[4];// 纬度
			System.arraycopy(hpos, 4, by, 0, 4);
			String y = Tools.formatYtoDu(this.removeZeroStr(Tools.bcd2Str(by)));
			// this.setCoordY(y);

			byte[] status = new byte[2];// 16个状态的开关量
			System.arraycopy(hpos, 8, status, 0, 2);

			byte b1 = status[0];
			byte b2 = status[1];

			// -------------------------第一字节-------------------------//
			if (Tools.getByteBit(b1, 0) == 1) {
				log.info(this.getDeviceSN() + " 刹车制动!");
			} else {
				log.info(this.getDeviceSN() + " 刹车正常!");
			}
			if (Tools.getByteBit(b1, 1) == 1) {
				log.info(this.getDeviceSN() + " 门打开！");
			} else {
				log.info(this.getDeviceSN() + " 门关闭！");
			}
			if (Tools.getByteBit(b1, 2) == 1) {
				log.info(this.getDeviceSN() + " 左转向灯开！");
			} else {
				log.info(this.getDeviceSN() + " 左转向灯关！");
			}
			if (Tools.getByteBit(b1, 3) == 1) {
				log.info(this.getDeviceSN() + " 右转向灯开！");
			} else {
				log.info(this.getDeviceSN() + " 右转向灯关！");
			}
			if (Tools.getByteBit(b1, 4) == 1) {
				log.info(this.getDeviceSN() + " 远光灯开！");
			} else {
				log.info(this.getDeviceSN() + " 远光灯关！");
			}
			if (Tools.getByteBit(b1, 5) == 1) {
				log.info(this.getDeviceSN() + " ACC开！");
			} else {
				log.info(this.getDeviceSN() + " ACC关！");
			}

			// ----------------------------第二字节------------------------------//
			if (Tools.getByteBit(b2, 0) == 1) {
				log.info(this.getDeviceSN() + " 坐标经度为 东经。");
			} else {
				log.info(this.getDeviceSN() + " 坐标经度为 西经。");
			}
			if (Tools.getByteBit(b2, 1) == 1) {
				log.info(this.getDeviceSN() + " 坐标纬度为 北纬。");
			} else {
				log.info(this.getDeviceSN() + " 坐标纬度为 南经。");
			}
			if (Tools.getByteBit(b2, 2) == 1) {
				log.info(this.getDeviceSN() + " 主动报警！");
			}
			if (Tools.getByteBit(b2, 3) == 1) {
				log.info(this.getDeviceSN() + " 断开油路！");
			} else {
				log.info(this.getDeviceSN() + " 油路正常！");
			}
			if (Tools.getByteBit(b2, 4) == 1) {
				log.info(this.getDeviceSN() + " 发生超速报警！");
			}
			if (Tools.getByteBit(b2, 5) == 1) {
				log.info(this.getDeviceSN() + " 发生震动报警！");
			}
			if (Tools.getByteBit(b2, 6) == 1) {
				log.info(this.getDeviceSN() + " 主电源断开！");
			}

			byte[] speed = new byte[1];// 记录仪速度
			System.arraycopy(hpos, 10, speed, 0, 1);
			int intSpeed = Tools.byte2Int(speed);

			byte[] gpsSpeed = new byte[1];// GPS速度
			System.arraycopy(hpos, 11, gpsSpeed, 0, 1);
			int intgpsSpeed = Tools.byte2Int(gpsSpeed);

			byte[] derition = new byte[1];// 方向
			System.arraycopy(hpos, 12, derition, 0, 1);
			int intDerition = Tools.byte2Int(derition) * 2;

			log.info(
					this.getDeviceSN() + "历史轨迹信息：x=" + x + ",y=" + y
							+ ",记录仪速度=" + intSpeed + ",GPS速度=" + intgpsSpeed
							+ ",方向=" + intDerition);
		}

	}

	// 市标驾驶员状态上报
	private void cityDriverStaus(byte[] encrypt) {
		byte[] cityDriverStatus = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, cityDriverStatus, 0,
				cityDriverStatus.length);
		byte[] license = new byte[18];
		byte[] status = new byte[1];
		System.arraycopy(cityDriverStatus, 0, license, 0, 18);
		System.arraycopy(cityDriverStatus, 18, status, 0, 1);
		byte bstatus = status[0];

		log.info(
				this.getDeviceSN() + " 驾驶证号：" + new String(license) + ",状态："
						+ (bstatus == 0 ? "开始驾驶" : "结束驾驶"));

		String repHex = GuoMaiProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, "e2", null);
		byte[] repB = Tools.fromHexString(repHex);
		this.setReplyByte(repB);

		log.info(this.getDeviceSN() + " 上报驾驶状态回应：" + repHex);
	}

	// 市标车机登陆
	private void cityLogin(byte[] encrypt) {
		byte[] cityLoginPos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, cityLoginPos, 0, cityLoginPos.length);
		String date = Tools.formatDate2Str(new Date(), "yyMMddHHmmss");

		String repHex = GuoMaiProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, "e1", Tools
				.fromHexString(date));
		byte[] repB = Tools.fromHexString(repHex);
		this.setReplyByte(repB);

		byte[] pos = new byte[30];
		byte[] sim = new byte[15];
		System.arraycopy(cityLoginPos, 0, pos, 0, 30);
		System.arraycopy(cityLoginPos, 30, pos, 0, 15);

		this.parsePosition(pos, this);
		if (typeCode.equals("GP-UCSTP-GPRS")) {

			log.info(
					this.getDeviceSN() + " 市标车机登陆时位置：x=" + this.getCoordX()
							+ ",y=" + this.getCoordY() + ",t=" + this.getTime()
							+ ",simcard=" + new String(sim));
			log.info(
					this.getDeviceSN() + " 市标车机登陆回应：" + repHex);
		} else {

			log.info(
					this.getDeviceSN() + " UCAST车机登陆时位置：x=" + this.getCoordX()
							+ ",y=" + this.getCoordY() + ",t=" + this.getTime()
							+ ",simcard=" + new String(sim));
			log.info(
					this.getDeviceSN() + " UCAST车机登陆回应：" + repHex);
		}

	}

	// 心跳间隔设置应答
	private void heartSetRes(byte[] encrypt) {
		byte[] heartPos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, heartPos, 0, heartPos.length);
		byte[] params = new byte[heartPos.length - 30];
		System.arraycopy(heartPos, 0, params, 0, params.length);

		log.info(this.getDeviceSN() + " 心跳间隔设置应答,param=");
	}

	// 自检应答
	private void selfCheckResponse(byte[] encrypt) {
		byte[] selfPos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, selfPos, 0, selfPos.length);
		log.info(this.getDeviceSN() + " 自检应答。");
	}

	// 查看区域监控设置应答
	private void viewAreaControlResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		log.info(
				this.getDeviceSN() + " 查看区域监控设置应答，param="
						+ Tools.bytesToHexString(paramb));
	}

	// 区域监控设置应答
	private void areaControlResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		log.info(
				this.getDeviceSN() + " -----------------区域监控设置应答，param="
						+ Tools.bytesToHexString(paramb));
	}

	// 查看区域设置应答
	private void viewAreaResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		log.info(
				this.getDeviceSN() + " 查看区域围栏应答，param="
						+ Tools.bytesToHexString(paramb));
	}

	// 区域设置应答
	private void areaSetResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		byte[] total = new byte[] { paramb[0] };
		int intTotal = Tools.byte2Int(total);
		String area = "";
		for (int i = 0; i < intTotal; i++) {
			area += (Tools.byte2Int(new byte[] { paramb[i + 1] }) + ",");
		}

		log.info(
				this.getDeviceSN() + " 设置区域围栏应答，param="
						+ Tools.bytesToHexString(paramb) + "区域总数：" + intTotal
						+ "区域号：" + area);
	}

	// 下发消息应答
	private void sendMessageRes(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		byte[] params = new byte[paramb.length - 30];
		System.arraycopy(paramb, 30, params, 0, params.length);

		try {
			log.info(
					this.getDeviceSN() + " 下发消息应答，消息为："
							+ new String(params, "GB2312"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// 取消紧急报警应答
	private void cancelActiveParam(byte[] encrypt) {
		log.info(this.getDeviceSN() + " 解除了紧急报警！");
	}

	// 复位、恢复出厂设置应答
	private void resetRecoverRes(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		byte type = paramb[30];

		log.info(
				this.getDeviceSN() + " 复位、恢复出厂设置应答，参数为："
						+ Integer.toBinaryString((int) type));

	}

	// 基本参数查询应答
	private void queryBasedParamRes(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		byte[] paramst = new byte[paramb.length - 30];
		System.arraycopy(paramb, 30, paramst, 0, paramst.length);
		byte type = paramst[0];

		byte[] params = new byte[paramst.length - 1];

		System.arraycopy(paramst, 1, params, 0, params.length);

		String typeHex = Tools.bytesToHexString(new byte[] { type });
		ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "1a" + typeHex, "0");
		String sparam = null;
		switch (type) {
		case 1:
			sparam = new String(params);
			log.info(
					this.getDeviceSN() + " 车辆标识代码：" + sparam);
			break;
		case 2:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " 车牌号：" + sparam);
			break;
		case 3:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " 车辆类别：" + sparam);
			break;
		case 4:
			if (typeCode.equals("GP-GUOMAIP-GPRS")) {
				sparam = Tools.byte2Int(params) + "";
				log.info(
						this.getDeviceSN() + " 驾驶员代号：" + sparam);
				ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "1b09", "0");
			} else if (typeCode.equals("GP-GUOMAIC-GPRS")) {
				sparam = Tools.bytesToHexString(params);
				ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "1b09", "0");
				log.info(
						this.getDeviceSN() + " 驾驶员代号：" + sparam);
			} else if (typeCode.equals("GP-UCSTC-GPRS")) {
				sparam = Tools.bytesToHexString(params);
				ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "1b04", "0");
				log.info(
						this.getDeviceSN() + " 驾驶员代号：" + sparam);
			}
			break;
		case 5:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " 驾驶证号：" + sparam);
			break;
		case 6:
			sparam = Tools.byte2Int(params) + "";

			log.info(
					this.getDeviceSN() + " MTD ID：" + sparam);
			break;
		case 7:
			sparam = new String(params);
			log.info(
					this.getDeviceSN() + " MTD 版本号：" + sparam);
			break;
		case 8:
			sparam = Tools.bytesToHexString(params);
			log.info(this.getDeviceSN() + " 初装日期：" + sparam);
			break;
		case 9:
			sparam = Tools.bytesToHexString(params);
			log.info(this.getDeviceSN() + " 实时时钟：" + sparam);
			break;
		case 10:
			if (typeCode.equals("GP-UCSTC-GPRS")) {
				byte ip1 = params[0];
				byte ip2 = params[1];
				byte ip3 = params[2];
				byte ip4 = params[3];
				String ip = Tools.byte2Int(new byte[] { ip1 }) + "."
						+ Tools.byte2Int(new byte[] { ip2 }) + "."
						+ Tools.byte2Int(new byte[] { ip3 }) + "."
						+ Tools.byte2Int(new byte[] { ip4 });

				byte[] port = new byte[2];
				System.arraycopy(params, 4, port, 0, 2);
				String sport = Tools.byte2Int(port) + "";
				log.info("IP地址为：" + ip + ", port=" + sport);
			} else {
				byte ip1 = params[0];
				byte ip2 = params[1];
				byte ip3 = params[2];
				byte ip4 = params[3];
				String ip = Tools.byte2Int(new byte[] { ip1 }) + "."
						+ Tools.byte2Int(new byte[] { ip2 }) + "."
						+ Tools.byte2Int(new byte[] { ip3 }) + "."
						+ Tools.byte2Int(new byte[] { ip4 });

				byte[] port = new byte[2];
				System.arraycopy(params, 4, port, 0, 2);
				String sport = Tools.byte2Int(port) + "";

				byte[] bigPort = new byte[2];
				System.arraycopy(params, 6, bigPort, 0, 2);
				String sbigport = Tools.byte2Int(bigPort) + "";

				byte[] littlePort = new byte[2];
				System.arraycopy(params, 8, littlePort, 0, 2);
				String slittport = Tools.byte2Int(littlePort) + "";

				log.info(
						"IP地址为：" + ip + ", port=" + sport + ",大图端口=" + sbigport
								+ ",小图端口=" + slittport);
			}
			break;
		case 11:// 运营管理中心备用IP地址、端口
			if (typeCode.equals("GP-UCSTC-GPRS")) {
				byte ip1 = params[0];
				byte ip2 = params[1];
				byte ip3 = params[2];
				byte ip4 = params[3];
				String ip = Tools.byte2Int(new byte[] { ip1 }) + "."
						+ Tools.byte2Int(new byte[] { ip2 }) + "."
						+ Tools.byte2Int(new byte[] { ip3 }) + "."
						+ Tools.byte2Int(new byte[] { ip4 });

				byte[] port = new byte[2];
				System.arraycopy(params, 4, port, 0, 2);
				String sport = Tools.byte2Int(port) + "";
				log.info("IP地址为：" + ip + ", port=" + sport);
			}
			break;
		case 12:// 短消息服务中心号码
			if (typeCode.equals("GP-UCSTC-GPRS")) {
				sparam = new String(params);
				log.info(
						this.getDeviceSN() + " 短消息服务中心号码：" + sparam);
			}
			break;
		case 13:// 运营管理中心短消息服务号码一
			if (typeCode.equals("GP-UCSTC-GPRS")) {
				sparam = new String(params);
				log.info(
						this.getDeviceSN() + " 运营管理中心短消息服务号码一号码：" + sparam);
			}
			break;
		case 14:// 运营管理中心短消息服务号码二
			if (typeCode.equals("GP-UCSTC-GPRS")) {
				sparam = new String(params);
				log.info(
						this.getDeviceSN() + " 运营管理中心短消息服务号码二号码：" + sparam);
			}
			break;
		case 15:
			sparam = Tools.byte2Int(params) + "";

			log.info(this.getDeviceSN() + " 特征系数：" + sparam);
			break;

		}

		log.info(
				this.getDeviceSN() + " 参数查询应答，参数类型：" + type + "内容：" + sparam);
	}

	// 基本参数设置应答
	private void basedParamSetRes(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		byte type = paramb[30];
		String typeHex = Tools.bytesToHexString(new byte[] { type });
		ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "1b" + typeHex, "0");

		byte[] params = new byte[paramb.length - 31];
		System.arraycopy(paramb, 31, params, 0, params.length);
		String sparam = null;

		switch (type) {
		case 1:
			sparam = new String(params);
			log.info(
					this.getDeviceSN() + " 设置车辆标识代码：" + sparam);
			break;
		case 2:
			sparam = new String(params);
			Log.getInstance()
					.outLog(this.getDeviceSN() + " 设置车牌号：" + sparam);
			break;
		case 3:
			sparam = new String(params);
			log.info(
					this.getDeviceSN() + " 设置车辆类别：" + sparam);
			break;
		case 4:
			if (typeCode.equals("GP-GUOMAIP-GPRS")) {
				sparam = Tools.byte2Int(params) + "";
				log.info(
						this.getDeviceSN() + " 驾驶员代号：" + sparam);
			} else if (typeCode.equals("GP-GUOMAIC-GPRS")) {
				sparam = Tools.bytesToHexString(params);
				log.info(
						this.getDeviceSN() + " 驾驶员代号：" + sparam);
			} else if (typeCode.equals("GP-UCSTC-GPRS")) {
				sparam = Tools.bytesToHexString(params);
				log.info(
						this.getDeviceSN() + " 驾驶员代号：" + sparam);
			}
			break;
		case 5:
			sparam = new String(params);
			log.info(
					this.getDeviceSN() + " 设置驾驶证号：" + sparam);
			break;
		case 6:
			sparam = Tools.byte2Int(params) + "";

			log.info(
					this.getDeviceSN() + " 设置MTD ID：" + sparam);
			break;
		case 7:
			sparam = new String(params);
			log.info(
					this.getDeviceSN() + " 设置MTD 版本号：" + sparam);
			break;
		case 8:
			sparam = Tools.bytesToHexString(params);
			log.info(
					this.getDeviceSN() + " 设置初装日期：" + sparam);
			break;
		case 9:
			sparam = Tools.bytesToHexString(params);
			log.info(
					this.getDeviceSN() + " 设置实时时钟：" + sparam);
			break;
		case 10:
			if (typeCode.equals("GP-UCSTC-GPRS")) {
				byte ip1 = params[0];
				byte ip2 = params[1];
				byte ip3 = params[2];
				byte ip4 = params[3];
				String ip = Tools.byte2Int(new byte[] { ip1 }) + "."
						+ Tools.byte2Int(new byte[] { ip2 }) + "."
						+ Tools.byte2Int(new byte[] { ip3 }) + "."
						+ Tools.byte2Int(new byte[] { ip4 });

				byte[] port = new byte[2];
				System.arraycopy(params, 4, port, 0, 2);
				String sport = Tools.byte2Int(port) + "";
				log.info("IP地址为：" + ip + ", port=" + sport);
			} else {
				byte ip1 = params[0];
				byte ip2 = params[1];
				byte ip3 = params[2];
				byte ip4 = params[3];
				String ip = Tools.byte2Int(new byte[] { ip1 }) + "."
						+ Tools.byte2Int(new byte[] { ip2 }) + "."
						+ Tools.byte2Int(new byte[] { ip3 }) + "."
						+ Tools.byte2Int(new byte[] { ip4 });

				byte[] port = new byte[2];
				System.arraycopy(params, 4, port, 0, 2);
				String sport = Tools.byte2Int(port) + "";

				byte[] bigPort = new byte[2];
				System.arraycopy(params, 6, bigPort, 0, 2);
				String sbigport = Tools.byte2Int(bigPort) + "";

				byte[] littlePort = new byte[2];
				System.arraycopy(params, 8, littlePort, 0, 2);
				String slittport = Tools.byte2Int(littlePort) + "";

				log.info(
						"IP地址为：" + ip + ", port=" + sport + ",大图端口=" + sbigport
								+ ",小图端口=" + slittport);
			}
			break;
		case 11:// 运营管理中心备用IP地址、端口
			if (typeCode.equals("GP-UCSTC-GPRS")) {
				byte ip1 = params[0];
				byte ip2 = params[1];
				byte ip3 = params[2];
				byte ip4 = params[3];
				String ip = Tools.byte2Int(new byte[] { ip1 }) + "."
						+ Tools.byte2Int(new byte[] { ip2 }) + "."
						+ Tools.byte2Int(new byte[] { ip3 }) + "."
						+ Tools.byte2Int(new byte[] { ip4 });

				byte[] port = new byte[2];
				System.arraycopy(params, 4, port, 0, 2);
				String sport = Tools.byte2Int(port) + "";
				log.info("IP地址为：" + ip + ", port=" + sport);
			}
			break;
		case 12:// 短消息服务中心号码
			if (typeCode.equals("GP-UCSTC-GPRS")) {
				sparam = new String(params);
				log.info(
						this.getDeviceSN() + " 短消息服务中心号码：" + sparam);
			}
			break;
		case 13:// 运营管理中心短消息服务号码一
			if (typeCode.equals("GP-UCSTC-GPRS")) {
				sparam = new String(params);
				log.info(
						this.getDeviceSN() + " 运营管理中心短消息服务号码一号码：" + sparam);
			}
			break;
		case 14:// 运营管理中心短消息服务号码二
			if (typeCode.equals("GP-UCSTC-GPRS")) {
				sparam = new String(params);
				log.info(
						this.getDeviceSN() + " 运营管理中心短消息服务号码二号码：" + sparam);
			}
			break;
		case 15:
			sparam = Tools.byte2Int(params) + "";

			log.info(
					this.getDeviceSN() + " 设置特征系数：" + sparam);
			break;

		}

		log.info(
				this.getDeviceSN() + " 基本参数设置应答，参数类型为：" + type + ",内容为："
						+ sparam);
	}

	// 驾驶员身份查询
	private void qureyDriverInfoRes(byte[] encrypt) {
		byte[] driver = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, driver, 0, driver.length);

		byte[] id = new byte[4];
		byte[] time = new byte[2];
		System.arraycopy(driver, 0, id, 0, 4);
		System.arraycopy(driver, 4, time, 0, 2);
		String sWorkNum = Tools.bytesToHexString(id);

		if (this.typeCode != null && this.typeCode.equals("GP-GUOMAIC-GPRS")) {
			DBService service = new DBServiceImpl();
			service.saveTurnOutRecord(this.getDeviceSN(), sWorkNum, null, null,
					null, null, "1");
		}

		log.info(
				this.getDeviceSN() + " 终端的驾驶员身份信息为："
						+ Tools.bytesToHexString(id) + ",驾驶时间为:"
						+ Tools.byte2Int(time) + "分钟");

	}

	// 油电路设置应答
	private void oilElecResponse(byte[] encrypt) {
		byte[] oe = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, oe, 0, oe.length);
		byte[] gps = new byte[30];
		System.arraycopy(oe, 0, gps, 0, 30);
		this.parsePosition(gps, this);

		byte flag = oe[30];

		if (typeCode.equals("GP-UCSTC-GPRS")) {
			log.info(
					this.getDeviceSN() + " 执行断油电应答：flag="
							+ Tools.bytesToHexString(new byte[] { flag })
							+ ", 81为断油执行成功，1为断油执行失败，80为恢复油路成功，0为恢复油路失败");
		} else {
			log.info(
					this.getDeviceSN() + " 执行断油电应答：flag="
							+ Tools.bytesToHexString(new byte[] { flag })
							+ ", 0为恢复，1为断开");
		}

	}

	// 疲劳驾驶设置应答
	private void fatigueDriveSet(byte[] encrypt) {
		byte[] fatigue = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, fatigue, 0, fatigue.length);
		int time = Tools.byte2Int(fatigue);
		log.info(
				this.getDeviceSN() + " 已连续驾驶：" + time + "分钟");
	}

	private void speedSetResponse(byte[] encrypt) {
		this.parsePosition(encrypt, this);
		// 根据SEQ,DEVICEID修改指令状态表
		log.info(
				this.getDeviceSN() + " ------------设置超速报警值应答：");
	}

	// 10天内里程
	private void tenDaysDis(byte[] encrypt) {
		log.info(this.getDeviceSN() + "上报10内的里程！");
		byte[] tenLcs = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, tenLcs, 0, tenLcs.length);

		this.parseTenDayDistance(tenLcs);
		String repHex = GuoMaiProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, "f4", null);
		byte[] repB = Tools.fromHexString(repHex);
		this.setReplyByte(repB);
		Log.getInstance()
				.outLog(this.getDeviceSN() + " 10天里程上报应答：" + repHex);
	}

	// 普通位置
	private void parseCommonPos(byte[] encrypt) {
		ArrayList<ParseBase> plist = new ArrayList<ParseBase>();

		for (int i = 0; i < encrypt.length - 24; i += 30) {
			ParseGuoMai gm = new ParseGuoMai();
			gm.setDeviceSN(this.getDeviceSN());
			if (term != null) {
//				gm.setObjId(term.getObjId());
//				gm.setObjType(term.getObjType());
//				gm.setPhnum(term.getSimcard());
			}
			byte[] pos = new byte[30];

			if (i + 21 + 30 < encrypt.length) {
				System.arraycopy(encrypt, i + 21, pos, 0, pos.length);

				gm.parsePosition(pos, gm);
				plist.add(gm);
				log.info(
						gm.getDeviceSN() + " GPS信息：" + gm.getCoordX() + ","
								+ gm.getCoordY() + ",speed=" + gm.getSpeed()
								+ ",direction=" + gm.getDirection() + ",time="
								+ gm.getTime());
			}
		}
		this.setParseList(plist);

	}

	// 点名位置
	private void singlePos(byte[] encrypt) {
		byte[] singlepos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, singlepos, 0, singlepos.length);

		this.parsePosition(singlepos, this);

		log.info(
				this.getDeviceSN() + "------------------ 单次GPS信息："
						+ this.getCoordX() + "," + this.getCoordY() + ",speed="
						+ this.getSpeed() + ",direction=" + this.getDirection()
						+ ",time=" + this.getTime());

	}

	// 频率设置应答
	private void freqResponse(byte[] encrypt) {
		byte[] freqpos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, freqpos, 0, freqpos.length);

		this.parsePosition(freqpos, this);
		byte[] freqb = new byte[4];
		System.arraycopy(freqpos, 30, freqb, 0, 4);
		int interval = Tools.byte2Int(new byte[] { freqb[0] });
		int sentCount = Tools.byte2Int(new byte[] { freqb[1] });
		int packCount = Tools.byte2Int(new byte[] { freqb[3], freqb[4] });
		log.info(
				this.getDeviceSN() + " --------------频率设置参数："
						+ Tools.bytesToHexString(freqb) + "时间间隔：" + interval
						+ "  发送条数：" + sentCount + " 发送包数：" + packCount);

	}

	// 距离设置应答
	private void distanceSetResponse(byte[] encrypt) {
		byte[] dispos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, dispos, 0, dispos.length);

		this.parsePosition(dispos, this);
		byte[] disb = new byte[5];
		System.arraycopy(dispos, 30, disb, 0, 5);

		byte[] interval = new byte[] { disb[0], disb[1] };
		byte[] sentCount = new byte[] { disb[2] };
		byte[] packCount = new byte[] { disb[3], disb[4] };

		log.info(
				this.getDeviceSN() + " ---------------定距离设置应答,距离参数="
						+ Tools.bytesToHexString(disb) + " 距离间隔："
						+ Tools.byte2Int(interval) + "发送条数 ："
						+ Tools.byte2Int(sentCount) + " 发送包数："
						+ Tools.byte2Int(packCount));

	}

	// 疑点数据上报
	private void doubtPos(byte[] encrypt) {
		byte[] Doubt = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, Doubt, 0, Doubt.length);
		int stopCarNo = Tools.byte2Int(new byte[] { encrypt[21] });// 停车序号

		for (int i = 0; i < Doubt.length; i = i + 30) {
			int flag = Doubt.length - i;
			if (flag < 30) {
				continue;
			}
			byte[] subDoubtPos = new byte[30];
			System.arraycopy(Doubt, i, subDoubtPos, 0, 30);
			this.parsePosition(Doubt, this);
			log.info(
					this.getDeviceSN() + " 上报的疑点数据：序号=" + stopCarNo
							+ "疑点GPS信息：dt=" + this.getTime() + ",x="
							+ this.getCoordX() + ",y=" + this.getCoordY()
							+ ",s=" + this.getSpeed() + ",d="
							+ this.getDirection() + ",h=" + this.getAltitude()
							+ ",dis=" + this.getMileage());
		}

	}

	// 打印前上报的数据
	private void printPos(byte[] encrypt) {
		byte[] printpos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, printpos, 0, printpos.length);

		this.parsePosition(printpos, this);

		log.info(
				this.getDeviceSN() + "打印前GPS信息：dt=" + this.getTime() + ",x="
						+ this.getCoordX() + ",y=" + this.getCoordY() + ",s="
						+ this.getSpeed() + ",d=" + this.getDirection() + ",h="
						+ this.getAltitude() + ",dis=" + this.getMileage());

	}

	// 接收消息
	private void receiveMsg(byte[] encrypt) {
		byte[] msgpos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, msgpos, 0, msgpos.length);

		this.parsePosition(msgpos, this);
		byte[] bmsg = new byte[msgpos.length - 30];
		System.arraycopy(msgpos, 30, bmsg, 0, bmsg.length);

		try {
			String msg = new String(bmsg, "GB2312");

			String repHex = GuoMaiProtocalUtil.createMtProtocal(iseq, oemCode,
					this.getDeviceSN(), sCenterId, sCenPwd, "16", null);
			byte[] repB = Tools.fromHexString(repHex);
			this.setReplyByte(repB);

			DBService service = new DBServiceImpl();
			try{
			service.saveMessage(this.getDeviceSN(), "监控平台", msg, "1", "");
			}catch(Exception e){}
			log.info(this.getDeviceSN() + " 上行短消息为：" + msg);
			log.info(
					this.getDeviceSN() + " 上行短消息确认：" + repHex);

			log.info(
					this.getDeviceSN() + "上行短消息时GPS信息：dt=" + this.getTime()
							+ ",x=" + this.getCoordX() + ",y="
							+ this.getCoordY() + ",s=" + this.getSpeed()
							+ ",d=" + this.getDirection() + ",h="
							+ this.getAltitude() + ",dis=" + this.getMileage());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 主动报警
	private void activeAlarm(byte[] encrypt) {
		byte[] activepos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, activepos, 0, activepos.length);

		this.parsePosition(activepos, this);
		log.info(
				this.getDeviceSN() + "主动报警GPS信息：dt=" + this.getTime() + ",x="
						+ this.getCoordX() + ",y=" + this.getCoordY() + ",s="
						+ this.getSpeed() + ",d=" + this.getDirection() + ",h="
						+ this.getAltitude() + ",dis=" + this.getMileage());

		String repHex = GuoMaiProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, "17", null);
		byte[] repB = Tools.fromHexString(repHex);
		this.setReplyByte(repB);
		log.info(this.getDeviceSN() + " 主动报警确认：" + repHex);

		String rmAlarm = GuoMaiProtocalUtil.createMtProtocal(iseq, oemCode,
				this.getDeviceSN(), sCenterId, sCenPwd, "18", null);
		byte[] reprmB = Tools.fromHexString(repHex);
		this.setReplyByte(reprmB);
		log.info(this.getDeviceSN() + " 主动报警解除：" + rmAlarm);

	}

	// 插IC上报
	private void intertICPos(byte[] encrypt) {
		log.info(this.getDeviceSN() + " 插IC卡上报.");
		byte[] icpos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, icpos, 0, icpos.length);
		byte[] icinpos = new byte[30];
		System.arraycopy(icpos, 0, icinpos, 0, 30);
		this.parsePosition(icinpos, this);

		byte[] workNum = new byte[3];
		byte[] driverLicense = new byte[18];
		byte[] driverName = new byte[16];
		System.arraycopy(icpos, icpos.length - 16, driverName, 0, 16);
		System.arraycopy(icpos, icpos.length - 34, driverLicense, 0, 18);
		System.arraycopy(icpos, icpos.length - 37, workNum, 0, 3);
		String sWorkNum = Tools.bytesToHexString(new byte[] { workNum[2],
				workNum[1], workNum[0] });
		// sWorkNum = Tools.byte2Int(Tools.fromHexString(sWorkNum))+"";
		String sDriverName = new String(driverName);
		String sDriverLic = new String(driverLicense);
		// if (this.typeCode != null &&
		if (this.typeCode.equals("GP-GUOMAIC-GPRS")
				|| this.typeCode.equals("GP-GUOMAIP-GPRS")) {
			sWorkNum = Tools.byte2Int(Tools.fromHexString(sWorkNum)) + "";
			while (sWorkNum.length() < 7) {
				sWorkNum = "0" + sWorkNum;
			}
		}
		log.info(
				this.getDeviceSN() + " 插卡:工号=" + sWorkNum + ",驾驶证号="
						+ sDriverLic + new String(driverLicense) + ",驾驶员名称="
						+ sDriverName + " GPS信息：dt=" + this.getTime() + ",x="
						+ this.getCoordX() + ",y=" + this.getCoordY() + ",s="
						+ this.getSpeed() + ",d=" + this.getDirection() + ",h="
						+ this.getAltitude() + ",dis=" + this.getMileage());

		String icRepHex = GuoMaiProtocalUtil.createMtProtocal(iseq, oemCode,
				this.getDeviceSN(), sCenterId, sCenPwd, "F1", null);
		byte[] icrepB = Tools.fromHexString(icRepHex);
		this.setReplyByte(icrepB);

		log.info(
				this.getDeviceSN() + " 插IC卡上报中心反馈：" + icRepHex);
		DBService dbservice = new DBServiceImpl();
		dbservice.saveTurnOutRecord(this.getDeviceSN(), sWorkNum, null, null,
				null, null, "1");
	}

	// 拨IC上报
	private void outICPos(byte[] encrypt) {
		log.info(this.getDeviceSN() + " 拔IC卡上报.");
		byte[] icoutpos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, icoutpos, 0, icoutpos.length);
		byte[] icoutpos1 = new byte[30];
		System.arraycopy(icoutpos1, 0, icoutpos1, 0, 30);
		this.parsePosition(icoutpos1, this);

		byte[] workNum1 = new byte[3];
		byte[] driverLicense1 = new byte[18];
		byte[] driverName1 = new byte[16];
		System.arraycopy(icoutpos, icoutpos.length - 16, driverName1, 0, 16);
		System.arraycopy(icoutpos, icoutpos.length - 34, driverLicense1, 0, 18);
		System.arraycopy(icoutpos, icoutpos.length - 37, workNum1, 0, 3);
		String sWorkNum = Tools.bytesToHexString(new byte[] { workNum1[2],
				workNum1[1], workNum1[0] });
		// sWorkNum = Tools.byte2Int(Tools.fromHexString(sWorkNum))+"";
		if (this.typeCode.equals("GP-GUOMAIC-GPRS")
				|| this.typeCode.equals("GP-GUOMAIP-GPRS")) {
			sWorkNum = Tools.byte2Int(Tools.fromHexString(sWorkNum)) + "";
			while (sWorkNum.length() < 7) {
				sWorkNum = "0" + sWorkNum;
			}
		}

		String sDriverName = new String(driverName1);
		String sDriverLic = new String(driverLicense1);

		log.info(
				this.getDeviceSN() + "拔卡:工号=" + sWorkNum + ",驾驶证号="
						+ sDriverLic + ",驾驶员名称=" + sDriverName + " GPS信息：dt="
						+ this.getTime() + ",x=" + this.getCoordX() + ",y="
						+ this.getCoordY() + ",s=" + this.getSpeed() + ",d="
						+ this.getDirection() + ",h=" + this.getAltitude()
						+ ",dis=" + this.getMileage());

		String icRepHex1 = GuoMaiProtocalUtil.createMtProtocal(iseq, oemCode,
				this.getDeviceSN(), sCenterId, sCenPwd, "F2", null);
		byte[] icrepB1 = Tools.fromHexString(icRepHex1);
		this.setReplyByte(icrepB1);

		log.info(
				this.getDeviceSN() + " 拨IC卡上报中心反馈：" + icRepHex1);

		DBService dbservice = new DBServiceImpl();
		dbservice.saveTurnOutRecord(this.getDeviceSN(), sWorkNum, null, null,
				null, null, "0");

	}

	// 盲区位置
	private void blindPos(byte[] encrypt) {
		byte[] mangqu = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, mangqu, 0, mangqu.length);
		ArrayList<ParseBase> plist = new ArrayList<ParseBase>();

		for (int i = 0; i < mangqu.length; i = i + 30) {
			byte[] subDoubtPos = new byte[30];
			ParseGuoMai gm = new ParseGuoMai();
			gm.setDeviceSN(this.getDeviceSN());
//			gm.setObjId(term.getObjId());
//			gm.setObjType(term.getObjType());
//			gm.setPhnum(term.getSimcard());

			if (i + 30 < mangqu.length) {
				System.arraycopy(mangqu, i, subDoubtPos, 0, 30);
				gm.parsePosition(mangqu, gm);
				plist.add(gm);
				log.info(
						gm.getDeviceSN() + " 上报的盲区补偿GPS信息：dt=" + gm.getTime()
								+ ",x=" + gm.getCoordX() + ",y="
								+ gm.getCoordY() + ",s=" + gm.getSpeed()
								+ ",d=" + gm.getDirection() + ",h="
								+ gm.getAltitude() + ",dis=" + gm.getMileage());
			}
		}
		this.setParseList(plist);
	}

	// 登录解析
	private void longin(String ptlNo) {
		log.info(this.getDeviceSN() + " 车机登陆.");
		// String protocalNo = "F0";

		String repHex = GuoMaiProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, ptlNo, null);
		byte[] repB = Tools.fromHexString(repHex);
		this.setReplyByte(repB);
		log.info(this.getDeviceSN() + " 登陆回应：" + repHex);

		if (this.typeCode != null && this.typeCode.equals("GP-GUOMAIC-GPRS")) {

			// 开启IC插拔功能
			String params = "01010101010101010101010101010101";
			String inIcHex = GuoMaiProtocalUtil.createMtProtocal(iseq, oemCode,
					this.getDeviceSN(), sCenterId, sCenPwd, "e5", Tools
							.fromHexString(params));
			byte[] inIC = Tools.fromHexString(inIcHex);
			this.setReplyByte1(inIC);
			log.info(
					this.getDeviceSN() + " 开启插IC卡功能：" + inIcHex);

		}

	}

	// 10天内的里程
	private void parseTenDayDistance(byte[] tenLcs) {

		String hexTenLc = Tools.bytesToHexString(tenLcs);
		String sdate1 = "20" + hexTenLc.substring(0, 2)
				+ hexTenLc.substring(2, 4) + hexTenLc.substring(4, 6);

		String date1Lc = Long.parseLong(Tools.bcd2Str(Tools
				.fromHexString(hexTenLc.substring(6, 14))))
				+ "";

		log.info(
				this.getDeviceSN() + " " + sdate1 + " 里程：" + date1Lc);

		String sdate2 = "20" + hexTenLc.substring(16, 18)
				+ hexTenLc.substring(18, 20) + hexTenLc.substring(20, 22);
		String date2Lc = Long.parseLong(Tools.bcd2Str(Tools
				.fromHexString(hexTenLc.substring(22, 30))))
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate2 + " 里程：" + date2Lc);

		String sdate3 = "20" + hexTenLc.substring(30, 32)
				+ hexTenLc.substring(34, 36) + hexTenLc.substring(38, 40);
		String date3Lc = Long.parseLong(Tools.bcd2Str(Tools
				.fromHexString(hexTenLc.substring(40, 48))))
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate3 + " 里程：" + date3Lc);

		String sdate4 = "20" + hexTenLc.substring(48, 50)
				+ hexTenLc.substring(50, 52) + hexTenLc.substring(52, 54);
		String date4Lc = Long.parseLong(Tools.bcd2Str(Tools
				.fromHexString(hexTenLc.substring(54, 62))))
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate4 + " 里程：" + date4Lc);

		String sdate5 = "20" + hexTenLc.substring(62, 64)
				+ hexTenLc.substring(64, 66) + hexTenLc.substring(66, 68);
		String date5Lc = Long.parseLong(Tools.bcd2Str(Tools
				.fromHexString(hexTenLc.substring(68, 72))))
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate5 + " 里程：" + date5Lc);

		String sdate6 = "20" + hexTenLc.substring(72, 74)
				+ hexTenLc.substring(74, 76) + hexTenLc.substring(76, 78);
		String date6Lc = Long.parseLong(Tools.bcd2Str(Tools
				.fromHexString(hexTenLc.substring(78, 86))))
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate6 + " 里程：" + date6Lc);

		String sdate7 = "20" + hexTenLc.substring(86, 88)
				+ hexTenLc.substring(88, 90) + hexTenLc.substring(90, 92);
		String date7Lc = Long.parseLong(Tools.bcd2Str(Tools
				.fromHexString(hexTenLc.substring(92, 100))))
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate7 + " 里程：" + date7Lc);

		String sdate8 = "20" + hexTenLc.substring(100, 102)
				+ hexTenLc.substring(102, 104) + hexTenLc.substring(104, 106);
		String date8Lc = Long.parseLong(Tools.bcd2Str(Tools
				.fromHexString(hexTenLc.substring(106, 114))))
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate8 + " 里程：" + date8Lc);

		String sdate9 = "20" + hexTenLc.substring(114, 116)
				+ hexTenLc.substring(116, 118) + hexTenLc.substring(118, 120);
		String date9Lc = Long.parseLong(Tools.bcd2Str(Tools
				.fromHexString(hexTenLc.substring(120, 128))))
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate9 + " 里程：" + date9Lc);

		String sdate10 = "20" + hexTenLc.substring(128, 130)
				+ hexTenLc.substring(130, 132) + hexTenLc.substring(132, 134);

		String date10Lc = Long.parseLong(Tools.bcd2Str(Tools
				.fromHexString(hexTenLc.substring(134, 142))))
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate10 + " 里程：" + date10Lc);

	}

	// 去除字符串前的0
	private String removeZeroStr(String str) {
		String ret = null;
		if (str != null && str.trim() != "") {
			int i = 0;
			while (i < str.length()) {
				if (str.charAt(i) != '0') {
					break;
				}
				i++;
			}
			if (i != str.length())
				ret = str.substring(i);
			else
				ret = "0";
		}

		return ret;
	}

	private void parsePosition(byte[] pos, ParseGuoMai gm) {
		byte[] date = new byte[6];// 日期
		System.arraycopy(pos, 0, date, 0, date.length);
		String dtbcd = Tools.bytesToHexString(date);

		Date dt = Tools.formatStrToDate(Tools.bcd2Str(date), "yyMMddHHmmss");
		String gpstime = Tools.formatDate2Str(dt, "yyyy-MM-dd HH:mm:ss");
		// Timestamp timeStamp = new Timestamp(dt.getTime());
		// this.setTimeStamp(timeStamp);
		gm.setTime(gpstime);

		byte[] bx = new byte[4];// 经度
		System.arraycopy(pos, 6, bx, 0, bx.length);

		String x = Tools.formatXtoDu(this.removeZeroStr(Tools.bcd2Str(bx)));
		gm.setCoordX(x);

		byte[] by = new byte[4];// 纬度
		System.arraycopy(pos, 10, by, 0, by.length);
		String y = Tools.formatYtoDu(this.removeZeroStr(Tools.bcd2Str(by)));
		gm.setCoordY(y);

		byte bspeed = pos[14];// 单位km/h
		// if (typeCode.equals("GP-GUOMAIP-GPRS")) {
		// this.setSpeed(Tools.byte2Int(new byte[] { bspeed }) + "");
		// }else if (typeCode.equals("GP-GUOMAIC-GPRS")) {//记录仪速度
		// this.setRecordInstruSpeed(Tools.byte2Int(new byte[] { bspeed }) +
		// "");
		// }else if(typeCode.equals("GP-UCSTC-GPRS")){//记录仪速度
		// this.setRecordInstruSpeed(Tools.byte2Int(new byte[] { bspeed }) +
		// "");
		// }
		gm.setSpeed(Tools.byte2Int(new byte[] { bspeed }) + "");
		int direction = Tools.byte2Int(new byte[] { pos[15] }) * 2;// 方向
		gm.setDirection(direction + "");

		byte[] heigh = new byte[2];// 高度
		System.arraycopy(pos, 16, heigh, 0, 2);
		int h = Tools.byte2Int(heigh);
		gm.setAltitude(h + "");

		byte[] distance = new byte[4];// 里程
		System.arraycopy(pos, 18, distance, 0, 4);
		String dis = Tools.bcd2Str(distance);
		double lc = Long.parseLong(dis) * 0.1d;// 单位KM
		String sdis = Tools.getNumberFormatString(lc, 1, 1);
		gm.setMileage(sdis);
 

		if (typeCode.equals("GP-UCSTP-GPRS")) {
			byte[] status = new byte[8];// 状态码
			byte[] oil = new byte[2];
			System.arraycopy(pos, 22, oil, 0, 2);
			int ioil = Tools.byte2Int(oil);
			log.info(
					gm.getDeviceSN() + " 油耗为：" + (ioil * 0.1) + "L");

			byte[] status_UCS = new byte[9];// 状态码
			System.arraycopy(pos, 24, status_UCS, 0, 8);
			gm.parseStatus(status_UCS, gm);

		} else {
			byte[] status = new byte[8];// 状态码
			System.arraycopy(pos, 22, status, 0, 8);
			gm.parseStatus(status, gm);
		}

		log.info(
				gm.getDeviceSN() + " 通用GPS信息：dt=" + gm.getTime() + ",x="
						+ gm.getCoordX() + ",y=" + gm.getCoordY() + ",s="
						+ gm.getSpeed() + ",d=" + gm.getDirection() + ",h="
						+ gm.getAltitude() + ",dis=" + gm.getMileage());

	}

	private void parseStatus(byte[] status, ParseGuoMai gm) {

		byte b1 = status[0];
		byte b2 = status[1];
		byte b3 = status[2];
		byte b4 = status[3];
		byte b5 = status[4];
		byte b6 = status[5];
		byte b7 = status[6];// 记录仪速度

		// ----------------------------第一字节------------------------------//
		if (Tools.getByteBit(b1, 0) == 1) {
			log.info(gm.getDeviceSN() + " 坐标经度为 东经。");
		} else {
			log.info(gm.getDeviceSN() + " 坐标经度为 西经。");
		}
		if (Tools.getByteBit(b1, 1) == 1) {
			log.info(gm.getDeviceSN() + " 坐标纬度为 北纬。");
		} else {
			log.info(gm.getDeviceSN() + " 坐标纬度为 南纬。");
		}
		if (Tools.getByteBit(b1, 2) == 1) {
			log.info(gm.getDeviceSN() + " 主动报警！");
			gm.setAlarmType(AlarmType.ACTIVE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(gm);
		}
		if (Tools.getByteBit(b1, 3) == 1) {
			log.info(gm.getDeviceSN() + " 断开油路！");
			termStatus.setOilElec("0");
		} else {
			log.info(gm.getDeviceSN() + " 油路正常！");
			termStatus.setOilElec("1");
		}
		if (Tools.getByteBit(b1, 4) == 1) {
			log.info(gm.getDeviceSN() + " 发生超速报警！");
			gm.setAlarmType("1");
			AlarmQueue.getInstance().addAlarm(gm);
		}
		if (Tools.getByteBit(b1, 5) == 1) {
			log.info(gm.getDeviceSN() + " 发生震动报警！");
			gm.setAlarmType("12");
			AlarmQueue.getInstance().addAlarm(gm);
		}
		if (Tools.getByteBit(b1, 6) == 1) {
			log.info(gm.getDeviceSN() + " 主电源断开！");
			termStatus.setMainPower("0");

			gm.setAlarmType(AlarmType.CUT_POWER_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(gm);
		} else {
			termStatus.setMainPower("3");
		}

		// -------------------------第二字节-------------------------//
		if (Tools.getByteBit(b2, 0) == 1) {
			log.info(gm.getDeviceSN() + " 刹车制动!");
			termStatus.setBrakeLight("1");
		} else {
			termStatus.setBrakeLight("0");
		}
		if (Tools.getByteBit(b2, 1) == 1) {
			log.info(gm.getDeviceSN() + " 门打开！");
			termStatus.setCarDoor("1");
		} else {
			termStatus.setCarDoor("0");
			log.info(gm.getDeviceSN() + " 门关闭！");
		}
		if (Tools.getByteBit(b2, 2) == 1) {
			log.info(gm.getDeviceSN() + " 左转向灯开！");
			termStatus.setLeftLight("1");
		} else {
			termStatus.setLeftLight("0");
			log.info(gm.getDeviceSN() + " 左转向灯关！");
		}
		if (Tools.getByteBit(b2, 3) == 1) {
			termStatus.setRightLight("1");
			log.info(gm.getDeviceSN() + " 右转向灯开！");
		} else {
			termStatus.setRightLight("0");
			log.info(gm.getDeviceSN() + " 右转向灯关！");
		}
		if (Tools.getByteBit(b2, 4) == 1) {
			termStatus.setFarLight("1");
			log.info(gm.getDeviceSN() + " 远光灯开！");
		} else {
			termStatus.setFarLight("0");
			log.info(gm.getDeviceSN() + " 远光灯关！");
		}
		if (Tools.getByteBit(b2, 5) == 1) {
			termStatus.setAcc("1");
			log.info(gm.getDeviceSN() + " ACC开！");
		} else {
			termStatus.setAcc("0");
			log.info(gm.getDeviceSN() + " ACC关！");
		}

		// ------------------------第三个字节------------------------------//
		if (Tools.getByteBit(b3, 0) == 1) {
			termStatus.setLocate("1");
			log.info(gm.getDeviceSN() + " 卫星定位已锁定！");
		} else {
			termStatus.setLocate("0");
			log.info(gm.getDeviceSN() + " 卫星定位未锁定！");
		}
		if (Tools.getByteBit(b3, 1) == 1) {
			termStatus.setAntenna("0");
			gm.setAlarmType(AlarmType.GPS_MAST_SHORT_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(gm);
			log.info(gm.getDeviceSN() + " 卫星定位天线短路！");
		}
		if (Tools.getByteBit(b3, 2) == 1) {
			termStatus.setAntenna("1");
			this.setAlarmType(AlarmType.GPS_MAST_OPEN_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(gm);
			log.info(gm.getDeviceSN() + " 卫星定位天线开路！");
		}
		if (Tools.getByteBit(b3, 1) == 0 && Tools.getByteBit(b3, 2) == 0) {
			// 天线正常
			termStatus.setAntenna("3");
		}
		if (Tools.getByteBit(b3, 3) == 1) {
			termStatus.setGpsModule("0");
			log.info(gm.getDeviceSN() + " 定位模块异常！");
			gm.setAlarmType("28");
			AlarmQueue.getInstance().addAlarm(gm);
		} else {
			termStatus.setGpsModule("1");
		}
		if (Tools.getByteBit(b3, 4) == 1) {
			termStatus.setGsmModule("0");
			log.info(gm.getDeviceSN() + " 通讯模块异常！");
		} else {
			termStatus.setGsmModule("1");
		}
		if (Tools.getByteBit(b3, 5) == 1) {
			log.info(gm.getDeviceSN() + " 出区域报警！");
			gm.setAlarmType("2");
			AlarmQueue.getInstance().addAlarm(gm);
		}
		if (Tools.getByteBit(b3, 6) == 1) {
			log.info(gm.getDeviceSN() + " 入区域报警！");
			gm.setAlarmType("2");
			AlarmQueue.getInstance().addAlarm(gm);
		}

		// ------------------------------第四个字节---------------------------------//
		if (Tools.getByteBit(b4, 0) == 1) {
			termStatus.setBackupBattery("0");
			log.info(gm.getDeviceSN() + " 备用电池异常！");
		} else {
			termStatus.setBackupBattery("1");
		}
		if (Tools.getByteBit(b4, 1) == 1) {
			log.info(gm.getDeviceSN() + " 地理栅栏越界！");
		}
		if (Tools.getByteBit(b4, 2) == 1) {
			termStatus.setEngine("1");
			log.info(gm.getDeviceSN() + " 发动机打开！");
		} else {
			termStatus.setEngine("0");
			log.info(gm.getDeviceSN() + " 发动机关闭！");
		}
		if (Tools.getByteBit(b4, 3) == 1) {
			log.info(gm.getDeviceSN() + " 疲劳驾驶报警！");
			gm.setAlarmType("10");
			AlarmQueue.getInstance().addAlarm(gm);
		}
		// ------------------------------第五个字节（市标有）------------------
		if (typeCode.equals("GP-GUOMAIC-GPRS")) {
			if (Tools.getByteBit(b5, 0) == 1) {
				termStatus.setCpu("0");
				log.info(gm.getDeviceSN() + " CPU异常！");
			} else {
				termStatus.setCpu("1");
			}
			if (Tools.getByteBit(b5, 1) == 1) {
				termStatus.setMemory("0");
				log.info(gm.getDeviceSN() + " 内存异常！");
			} else {
				termStatus.setMemory("1");
			}
			if (Tools.getByteBit(b5, 2) == 1) {
				//termStatus.setFlash("0");
				log.info(gm.getDeviceSN() + " FLASH异常！");
			} else {
				//termStatus.setFlash("1");

			}
			if (Tools.getByteBit(b5, 3) == 1) {
				log.info(gm.getDeviceSN() + " SD卡异常！");
				termStatus.setSdCard("0");
			} else {
				termStatus.setSdCard("1");
			}
			if (Tools.getByteBit(b5, 3) == 1) {
				log.info(gm.getDeviceSN() + " 打印机连接断开！");
				termStatus.setPrinter("1");
			} else {
				termStatus.setPrinter("0");
			}
			if (Tools.getByteBit(b5, 3) == 1) {
				log.info(gm.getDeviceSN() + " 摄像头连接断开！");
				termStatus.setImageCollector("0");
			} else {
				termStatus.setImageCollector("1");
			}

			// ------------------------------第六个字节（市标有）------------------
			if (Tools.getByteBit(b6, 0) == 1) {
				termStatus.setIsTimerLocate("0");
				log.info(gm.getDeviceSN() + " is 按时间间隔回传！");
			} else {
				termStatus.setIsTimerLocate("1");
			}
			if (Tools.getByteBit(b6, 1) == 1) {
				termStatus.setIsDistanceLocate("0");
				log.info(gm.getDeviceSN() + " is 按距离间隔回传！！");
			} else {
				termStatus.setIsDistanceLocate("1");
			}

		}
		int rcdInstruSpd = Tools.byte2Int(new byte[] { b7 });

		if (typeCode.equals("GP-GUOMAIP-GPRS")) {// 地标记录仪速度
			if (rcdInstruSpd != 0)
				this.setSpeed(rcdInstruSpd + "");
		} else if (typeCode.equals("GP-GUOMAIC-GPRS")) {
			if (rcdInstruSpd != 0)
				this.setSpeed(rcdInstruSpd + "");// 为市标GPS速度
		} else if (typeCode.equals("GP-UCSTC-GPRS")) {
			if (rcdInstruSpd != 0)
				this.setSpeed(rcdInstruSpd + "");// 为UCAST速度
		}
		termStatus.setDeviceId(this.getDeviceSN());
		// termStatus.setGpsTime(this.getTimeStamp());
		this.setStatusRecord(termStatus);
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
