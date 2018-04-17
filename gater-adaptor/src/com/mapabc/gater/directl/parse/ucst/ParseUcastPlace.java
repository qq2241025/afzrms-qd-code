/**
 * 
 */
package com.mapabc.gater.directl.parse.ucst;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.CRC;
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.constant.AlarmType;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.gater.directl.encode.PropertyReader;
import com.mapabc.gater.directl.encode.ucst.UcstProtocalUtil;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.lbsgateway.GBLTerminalList; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.bean.TTerminal;

 

/**
 * @author shiguang.zhou
 * 
 */
public class ParseUcastPlace extends ParseBase implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseUcastPlace.class);
	private String typeCode = "";
	private String head = "7e";
	private String end = "7f";
	private String iseq = "";
	private String oemCode = "";
	private String sCenterId = "";
	private String sCenPwd = "";
	private static String isOverLoad = null;// 是否有负载
	private PropertyReader proRead;
	TTerminal term = null;

	public ParseUcastPlace() {

	}
 
	public void parseGPRS(String hexString) {
		// TODO Auto-generated method stub
		log.info("UCAST地标GPS原始数据：" + hexString);

		byte[] ucont = Tools.fromHexString(hexString);
		byte[] revBytes = new byte[ucont.length - 2];
		System.arraycopy(ucont, 1, revBytes, 0, revBytes.length);

		byte[] resEspB = UcstProtocalUtil.reverseEscape(revBytes);
		log.info(
				"UCAST地标原始数据反转义：" + Tools.bytesToHexString(resEspB));

		String bs64Esp = "7e" + Tools.bytesToHexString(resEspB) + "7f";

		byte[] crcTmp = new byte[resEspB.length - 2];// 待验证CRS结果的字节
		System.arraycopy(resEspB, 0, crcTmp, 0, crcTmp.length);

		byte[] crcCode = new byte[2];// 上传的CRS结果
		System.arraycopy(resEspB, resEspB.length - 2, crcCode, 0,
				crcCode.length);
		String crcR = Tools.bytesToHexString(crcCode);

		// int crcRes = CRC.CRC_UCST(crcTmp); // 服务端校验CRS结果
		String crcResHex = "";
		crcResHex = CRC.CRC_UCST(crcTmp);

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
						+ crcResHex);

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

		int key = Integer.parseInt(deviceId);

		byte[] encrypt = Tools.fromHexString(bs64Esp);

		byte flag = encrypt[19];// 流向标志，上行或下行
		byte ptlNo = encrypt[20];// 协议号

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
		case (byte) 0x0b:// 线路设置应答
			this.lineSetResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "0b", "0");
			break;
		case (byte) 0x0c:// 查看线路应答
			this.viewLineResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "0c", "0");
			break;
		case (byte) 0x0d:// 线路监控设置应答
			this.lineControlResponse(encrypt);
			ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "0d", "0");
			break;
		case (byte) 0x0e:// 查看线路监控设置应答
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
		case (byte) 0xf5:// 里程上报
			this.listenResponse(encrypt);
			break;
		case (byte) 0xf8:// 心跳设置
			this.heartRes();
			break;

		case (byte) 0xe0:// 终端登录
			this.longin("E0");
			break;
		case (byte) 0xE1:// 市标车机登陆
			this.cityLogin(encrypt);
			break;
		case (byte) 0xe2:// 驾驶员状态上报
			this.outICPos(encrypt);
			break;
		case (byte) 0xe3:// 盲区补偿数据
			this.blindPos(encrypt);
			break;
		case (byte) 0xe4:// 

			break;
		case (byte) 0xe8://  

			break;

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

		String repHex = UcstProtocalUtil.createMtProtocal(iseq, oemCode, this
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

		String repHex = UcstProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, "e1", Tools
				.fromHexString(date));
		byte[] repB = Tools.fromHexString(repHex);
		this.setReplyByte(repB);

		log.info(this.getDeviceSN() + " 市标车机登陆回应：" + repHex);

		byte[] pos = new byte[30];
		byte[] sim = new byte[15];
		System.arraycopy(cityLoginPos, 0, pos, 0, 30);
		System.arraycopy(cityLoginPos, 30, pos, 0, 15);

		this.parsePosition(pos);
		log.info(
				this.getDeviceSN() + " 市标车机登陆时位置：x=" + this.getCoordX() + ",y="
						+ this.getCoordY() + ",t=" + this.getTime()
						+ ",simcard=" + new String(sim));

	}

	// 心跳间隔设置应答
	private void heartRes() {
		String repHex = UcstProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, "F8", null);
		byte[] repB = Tools.fromHexString(repHex);
		this.setReplyByte(repB);

		log.info(this.getDeviceSN() + " 心跳应答.");
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

	// 查看区域监控设置应答
	private void viewLineControlResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		log.info(
				this.getDeviceSN() + " 查看线路监控设置应答，param="
						+ Tools.bytesToHexString(paramb));
	}

	// 区域监控设置应答
	private void areaControlResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		log.info(
				this.getDeviceSN() + " 区域监控设置应答，param="
						+ Tools.bytesToHexString(paramb));
	}

	// 线路监控设置应答
	private void lineControlResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		log.info(
				this.getDeviceSN() + " 线路监控设置应答，param="
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

	// 查看线路设置应答
	private void viewLineResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		log.info(
				this.getDeviceSN() + " 查看线路围栏应答，param="
						+ Tools.bytesToHexString(paramb));
	}

	// 区域设置应答
	private void areaSetResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		log.info(
				this.getDeviceSN() + " 设置区域围栏应答，param="
						+ Tools.bytesToHexString(paramb));
	}

	// 线路设置应答
	private void lineSetResponse(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		log.info(
				this.getDeviceSN() + " 设置线路应答，param="
						+ Tools.bytesToHexString(paramb));
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

		byte bcount = paramst[0];
		int icount = Tools.byte2Int(new byte[] { bcount });

		byte btype = paramst[1];
		int type = Tools.byte2Int(new byte[] { btype });

		byte paramLen = paramst[2];
		int plen = Tools.byte2Int(new byte[] { paramLen });

		byte[] params = new byte[paramst.length - 3];
		System.arraycopy(paramst, 3, params, 0, params.length);

		String typeHex = Tools.int2Hexstring(type, 2);
		ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "1b" + typeHex, "0");
		log.info(
				this.getDeviceSN() + " 基本参数 个数=" + icount + ", 类型=" + plen
						+ ",内容长度=" + plen);

		String sparam = null;
		switch (type) {
		case 1:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " 车辆标识代码：" + sparam);
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

			sparam = Tools.byte2Int(params) + "";
			log.info(this.getDeviceSN() + " 驾驶员代号：" + sparam);

			sparam = Tools.bytesToHexString(params);
			log.info(this.getDeviceSN() + " 驾驶员代号：" + sparam);

			break;
		case 5:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " 驾驶证号：" + sparam);
			break;
		case 6:
			sparam = Tools.byte2Int(params) + "";

			log.info(this.getDeviceSN() + " MTD ID：" + sparam);
			break;
		case 7:
			sparam = new String(params);
			Log.getInstance()
					.outLog(this.getDeviceSN() + " MTD 版本号：" + sparam);
			break;
		case 8:

			sparam = Tools.bytesToHexString(params);
			log.info(this.getDeviceSN() + " 初装日期：" + sparam);
			break;
		case 9:
			// UTC 时间
			sparam = Tools.bytesToHexString(params);
			log.info(this.getDeviceSN() + " 实时时钟：" + sparam);
			break;
		case 10:
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
			break;
		case 11:
			byte bip1 = params[0];
			byte bip2 = params[1];
			byte bip3 = params[2];
			byte bip4 = params[3];
			String bip = Tools.byte2Int(new byte[] { bip1 }) + "."
					+ Tools.byte2Int(new byte[] { bip2 }) + "."
					+ Tools.byte2Int(new byte[] { bip3 }) + "."
					+ Tools.byte2Int(new byte[] { bip4 });

			byte[] bport = new byte[2];
			System.arraycopy(params, 4, bport, 0, 2);
			String bsport = Tools.byte2Int(bport) + "";

			log.info("备用IP地址为：" + bip + ", port=" + bsport);
			break;
		case 12:

			sparam = new String(params);
			log.info("短信中心号码为：" + sparam);
			break;
		case 21:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " APN 名称：" + sparam);

			break;
		case 15:
			sparam = Tools.byte2Int(params) + "";

			log.info(this.getDeviceSN() + " 报警汇报间隔：" + sparam);
			break;

		case 24:
			sparam = Tools.byte2Int(params) + "";

			log.info(
					this.getDeviceSN() + " 紧急报警时连续拍摄照片的张数：" + sparam);
			break;

		case 25:
			sparam = Tools.byte2Int(params) + "";

			log.info(
					this.getDeviceSN() + " 紧急报警时拍摄照片的间隔：" + sparam);
			break;
		case 26:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " 软件版本：" + sparam);

			break;

		}

		log.info(
				this.getDeviceSN() + " 参数查询应答，参数类型：" + type + "内容：" + sparam);

	}

	// 基本参数设置应答
	private void basedParamSetRes(byte[] encrypt) {
		byte[] paramb = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, paramb, 0, paramb.length);

		byte[] paramst = new byte[paramb.length - 30];
		System.arraycopy(paramb, 30, paramst, 0, paramst.length);

		byte bcount = paramst[0];
		int icount = Tools.byte2Int(new byte[] { bcount });

		byte btype = paramst[1];
		int type = Tools.byte2Int(new byte[] { btype });

		byte paramLen = paramst[2];
		int plen = Tools.byte2Int(new byte[] { paramLen });

		byte[] params = new byte[paramst.length - 3];
		System.arraycopy(paramst, 3, params, 0, params.length);

		String typeHex = Tools.int2Hexstring(type, 2);
		ReplyResponseUtil.addReply(this.getDeviceSN() + ":" + "1b" + typeHex, "0");
		log.info(
				this.getDeviceSN() + " 基本参数 个数=" + icount + ", 类型=" + plen
						+ ",内容长度=" + plen);

		String sparam = null;
		switch (type) {
		case 1:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " 车辆标识代码：" + sparam);
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

			sparam = Tools.byte2Int(params) + "";
			log.info(this.getDeviceSN() + " 驾驶员代号：" + sparam);

			sparam = Tools.bytesToHexString(params);
			log.info(this.getDeviceSN() + " 驾驶员代号：" + sparam);

			break;
		case 5:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " 驾驶证号：" + sparam);
			break;
		case 6:
			sparam = Tools.byte2Int(params) + "";

			log.info(this.getDeviceSN() + " MTD ID：" + sparam);
			break;
		case 7:
			sparam = new String(params);
			Log.getInstance()
					.outLog(this.getDeviceSN() + " MTD 版本号：" + sparam);
			break;
		case 8:

			sparam = Tools.bytesToHexString(params);
			log.info(this.getDeviceSN() + " 初装日期：" + sparam);
			break;
		case 9:
			// UTC 时间
			sparam = Tools.bytesToHexString(params);
			log.info(this.getDeviceSN() + " 实时时钟：" + sparam);
			break;
		case 10:
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

			sparam = ip + ":" + sport;
			log.info("IP地址为：" + ip + ", port=" + sport);
			break;
		case 11:
			byte bip1 = params[0];
			byte bip2 = params[1];
			byte bip3 = params[2];
			byte bip4 = params[3];
			String bip = Tools.byte2Int(new byte[] { bip1 }) + "."
					+ Tools.byte2Int(new byte[] { bip2 }) + "."
					+ Tools.byte2Int(new byte[] { bip3 }) + "."
					+ Tools.byte2Int(new byte[] { bip4 });

			byte[] bport = new byte[2];
			System.arraycopy(params, 4, bport, 0, 2);
			String bsport = Tools.byte2Int(bport) + "";
			sparam = bip + ":" + bsport;
			log.info("备用IP地址为：" + bip + ", port=" + bsport);
			break;
		case 12:

			sparam = new String(params);
			log.info("短信中心号码为：" + sparam);
			break;
		case 21:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " APN 名称：" + sparam);

			break;
		case 15:
			sparam = Tools.byte2Int(params) + "";

			log.info(this.getDeviceSN() + " 报警汇报间隔：" + sparam);
			break;

		case 24:
			sparam = Tools.byte2Int(params) + "";

			log.info(
					this.getDeviceSN() + " 紧急报警时连续拍摄照片的张数：" + sparam);
			break;

		case 25:
			sparam = Tools.byte2Int(params) + "";

			log.info(
					this.getDeviceSN() + " 紧急报警时拍摄照片的间隔：" + sparam);
			break;
		case 26:
			sparam = new String(params);
			log.info(this.getDeviceSN() + " 软件版本：" + sparam);

			break;

		}

		log.info(
				this.getDeviceSN() + " 参数查询应答，参数类型：" + type + "内容：" + sparam);

	}

	// 驾驶员身份查询
	private void qureyDriverInfoRes(byte[] encrypt) {
		byte[] driver = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, driver, 0, driver.length);

		byte[] id = new byte[4];
		byte[] time = new byte[2];
		byte[] driverNo = new byte[18];
		System.arraycopy(driver, 0, id, 0, 4);
		System.arraycopy(driver, 4, time, 0, 2);
		System.arraycopy(driver, 6, driverNo, 0, 18);

		log.info(
				this.getDeviceSN() + " 终端的驾驶员身份信息为：" + Tools.bcd2Str(id)
						+ ",驾驶时间为:" + Tools.byte2Int(time) + "分钟,驾驶证号："
						+ new String(driverNo));

	}

	// 油电路设置应答
	private void oilElecResponse(byte[] encrypt) {
		byte[] oe = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, oe, 0, oe.length);
		byte flag = oe[30];

		log.info(
				this.getDeviceSN() + " 执行断油电应答：flag="
						+ Tools.bytesToHexString(new byte[] { flag })
						+ ", 0为恢复，1为断开");
	}

	// 疲劳驾驶设置应答
	private void fatigueDriveSet(byte[] encrypt) {
		byte[] fatigue = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, fatigue, 0, fatigue.length);
		int time = Tools.byte2Int(fatigue);
		log.info(this.getDeviceSN() + " 已连续驾驶：" + time + "分钟");
	}

	private void speedSetResponse(byte[] encrypt) {

		// 根据SEQ,DEVICEID修改指令状态表
		log.info(this.getDeviceSN() + " 设置超速报警值应答：");
	}

	// 监听应答
	private void listenResponse(byte[] encrypt) {

		byte[] lis = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, lis, 0, lis.length);

		log.info(
				this.getDeviceSN() + " 监听应答参数：" + Tools.bytesToHexString(lis));
	}

	// 10天内里程
	private void tenDaysDis(byte[] encrypt) {
		log.info(this.getDeviceSN() + "上报10内的里程！");
		byte[] tenLcs = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, tenLcs, 0, tenLcs.length);

		this.parseTenDayDistance(tenLcs);
		String repHex = UcstProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, "f4", null);
		byte[] repB = Tools.fromHexString(repHex);
		this.setReplyByte(repB);
		log.info(this.getDeviceSN() + " 10天里程上报应答：" + repHex);
	}

	// 普通位置
	private void parseCommonPos(byte[] encrypt) {
		byte[] pos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, pos, 0, pos.length);

		this.parsePosition(pos);

		log.info(
				this.getDeviceSN() + " GPS信息：" + this.getCoordX() + ","
						+ this.getCoordY() + ",speed=" + this.getSpeed()
						+ ",direction=" + this.getDirection() + ",time="
						+ this.getTime());
	}

	// 点名位置
	private void singlePos(byte[] encrypt) {
		byte[] singlepos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, singlepos, 0, singlepos.length);

		this.parsePosition(singlepos);

		log.info(
				this.getDeviceSN() + " 单次GPS信息：" + this.getCoordX() + ","
						+ this.getCoordY() + ",speed=" + this.getSpeed()
						+ ",direction=" + this.getDirection() + ",time="
						+ this.getTime());

	}

	// 频率设置应答
	private void freqResponse(byte[] encrypt) {
		byte[] freqpos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, freqpos, 0, freqpos.length);

		this.parsePosition(freqpos);
		byte[] freqb = new byte[4];
		System.arraycopy(freqpos, 30, freqb, 0, 4);

		Log.getInstance()
				.outLog(
						this.getDeviceSN() + " 频率设置参数："
								+ Tools.bytesToHexString(freqb));

	}

	// 距离设置应答
	private void distanceSetResponse(byte[] encrypt) {
		byte[] dispos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, dispos, 0, dispos.length);

		this.parsePosition(dispos);
		byte[] disb = new byte[5];
		System.arraycopy(dispos, 30, disb, 0, 5);

		log.info(
				this.getDeviceSN() + " 定距离设置应答,距离参数="
						+ Tools.bytesToHexString(disb));

	}

	// 疑点数据上报
	private void doubtPos(byte[] encrypt) {
		byte[] Doubt = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, Doubt, 0, Doubt.length);

		int stopCarNo = Tools.byte2Int(new byte[] { encrypt[21] });// 疑点数据序号
		log.info(
				this.getDeviceSN() + " 序号" + stopCarNo + " 的疑点数据:"
						+ Tools.bytesToHexString(Doubt));

		byte[] stopTime = new byte[6];

		// if (Doubt.length > 0) {
		// System.arraycopy(encrypt, 22, stopTime, 0, 6);
		// String stime = Tools.bcd2Str(stopTime);
		//
		// for (int i = 0; i < Doubt.length; i = i + 30) {
		// int flag = Doubt.length - i;
		// if (flag < 30) {
		// continue;
		// }
		// byte[] subDoubtPos = new byte[30];
		// System.arraycopy(Doubt, i, subDoubtPos, 0, 30);
		// this.parsePosition(Doubt);
		// log.info(
		// this.getDeviceSN() + " 上报的疑点数据：序号=" + stopCarNo
		// + ",停车时间=" + stime + "疑点GPS信息：dt="
		// + this.getTime() + ",x=" + this.getCoordX()
		// + ",y=" + this.getCoordY() + ",s="
		// + this.getSpeed() + ",d=" + this.getDirection()
		// + ",h=" + this.getAltitude() + ",dis="
		// + this.getMileage());
		// }
		// }else {
		// log.info(this.getDeviceSN() +" 无序号"+stopCarNo+"
		// 的疑点数据。");
		// }

	}

	// 打印前上报的数据
	private void printPos(byte[] encrypt) {
		byte[] printpos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, printpos, 0, printpos.length);

		this.parsePosition(printpos);

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

		this.parsePosition(msgpos);
		byte[] bmsg = new byte[msgpos.length - 30];
		System.arraycopy(msgpos, 30, bmsg, 0, bmsg.length);

		try {
			String msg = new String(bmsg, "GB2312");

			String repHex = UcstProtocalUtil.createMtProtocal(iseq, oemCode,
					this.getDeviceSN(), sCenterId, sCenPwd, "16", null);
			byte[] repB = Tools.fromHexString(repHex);
			this.setReplyByte(repB);
			log.info(this.getDeviceSN() + " 上行短消息为：" + msg);
			Log.getInstance()
					.outLog(this.getDeviceSN() + " 上行短消息确认：" + repHex);

//			DBService service = new DBServiceImpl();
//			long gid = this.term.getGid();
//			service.saveMessage(this.getDeviceSN(), "监控平台", msg, "1", gid);

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

		this.parsePosition(activepos);
		log.info(
				this.getDeviceSN() + "主动报警GPS信息：dt=" + this.getTime() + ",x="
						+ this.getCoordX() + ",y=" + this.getCoordY() + ",s="
						+ this.getSpeed() + ",d=" + this.getDirection() + ",h="
						+ this.getAltitude() + ",dis=" + this.getMileage());

		String repHex = UcstProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, "17", null);
		byte[] repB = Tools.fromHexString(repHex);
		this.setReplyByte(repB);

		log.info(this.getDeviceSN() + " 主动报警确认：" + repHex);

		String rmAlarmHex = UcstProtocalUtil.createMtProtocal(iseq, oemCode,
				this.getDeviceSN(), sCenterId, sCenPwd, "18", null);
		byte[] repRmAlarm = Tools.fromHexString(repHex);
		this.setReplyByte(repRmAlarm);

		log.info(this.getDeviceSN() + " 主动报警清楚：" + rmAlarmHex);

	}

	// 插IC上报
	private void intertICPos(byte[] encrypt) {
		log.info(this.getDeviceSN() + " 插IC卡上报.");
		byte[] icpos = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, icpos, 0, icpos.length);
		byte[] icinpos = new byte[30];
		System.arraycopy(icpos, 0, icinpos, 0, 30);
		this.parsePosition(icinpos);

		byte[] workNum = new byte[3];
		byte[] driverLicense = new byte[18];
		byte[] driverName = new byte[16];
		System.arraycopy(icpos, icpos.length - 16, driverName, 0, 16);
		System.arraycopy(icpos, icpos.length - 34, driverLicense, 0, 18);
		System.arraycopy(icpos, icpos.length - 37, workNum, 0, 3);
		String sWorkNum = new String(workNum);
		String sDriverName = new String(driverName);
		String sDriverLic = new String(driverLicense);

		log.info(
				this.getDeviceSN() + " 插卡:工号=" + sWorkNum + ",驾驶证号="
						+ sDriverLic + new String(driverLicense) + ",驾驶员名称="
						+ sDriverName + " GPS信息：dt=" + this.getTime() + ",x="
						+ this.getCoordX() + ",y=" + this.getCoordY() + ",s="
						+ this.getSpeed() + ",d=" + this.getDirection() + ",h="
						+ this.getAltitude() + ",dis=" + this.getMileage());

		String icRepHex = UcstProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, "F1", null);
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
		this.parsePosition(icoutpos1);

		byte[] workNum1 = new byte[3];
		byte[] driverLicense1 = new byte[18];
		byte[] driverName1 = new byte[16];

		System.arraycopy(icoutpos, icoutpos.length - 16, driverName1, 0, 16);
		System.arraycopy(icoutpos, icoutpos.length - 34, driverLicense1, 0, 18);
		System.arraycopy(icoutpos, icoutpos.length - 37, workNum1, 0, 3);

		String sWorkNum = new String(workNum1);
		String sDriverName = new String(driverName1);
		String sDriverLic = new String(driverLicense1);

		log.info(
				this.getDeviceSN() + "拔卡:工号=" + sWorkNum + ",驾驶证号="
						+ sDriverLic + ",驾驶员名称=" + sDriverName + "GPS信息：dt="
						+ this.getTime() + ",x=" + this.getCoordX() + ",y="
						+ this.getCoordY() + ",s=" + this.getSpeed() + ",d="
						+ this.getDirection() + ",h=" + this.getAltitude()
						+ ",dis=" + this.getMileage());
		String icRepHex1 = UcstProtocalUtil.createMtProtocal(iseq, oemCode,
				this.getDeviceSN(), sCenterId, sCenPwd, "F2", null);
		byte[] icrepB1 = Tools.fromHexString(icRepHex1);
		this.setReplyByte(icrepB1);

		log.info(
				this.getDeviceSN() + " 拨IC卡上报中心反馈：" + icRepHex1);
		DBService dbservice = new DBServiceImpl();
		dbservice.saveTurnOutRecord(this.getDeviceSN(), sWorkNum, null, null,
				sDriverName, sDriverLic, "0");

	}

	// 盲区位置
	private void blindPos(byte[] encrypt) {
		byte[] mangqu = new byte[encrypt.length - 24];
		System.arraycopy(encrypt, 21, mangqu, 0, mangqu.length);

		for (int i = 0; i < mangqu.length; i = i + 30) {
			byte[] subDoubtPos = new byte[30];
			System.arraycopy(mangqu, i, subDoubtPos, 0, 30);
			this.parsePosition(mangqu);
			log.info(
					this.getDeviceSN() + " 上报的盲区补偿GPS信息：dt=" + this.getTime()
							+ ",x=" + this.getCoordX() + ",y="
							+ this.getCoordY() + ",s=" + this.getSpeed()
							+ ",d=" + this.getDirection() + ",h="
							+ this.getAltitude() + ",dis=" + this.getMileage());
		}
	}

	// 登录解析
	private void longin(String ptlNo) {
		log.info(this.getDeviceSN() + " 车机登陆.");
		// String protocalNo = "F0";

		String repHex = UcstProtocalUtil.createMtProtocal(iseq, oemCode, this
				.getDeviceSN(), sCenterId, sCenPwd, ptlNo, null);
		byte[] repB = Tools.fromHexString(repHex);
		this.setReplyByte(repB);

		log.info(this.getDeviceSN() + " 登陆回应：" + repHex);
	}

	// 10天内的里程
	private void parseTenDayDistance(byte[] tenLcs) {

		String hexTenLc = Tools.bytesToHexString(tenLcs);
		String sdate1 = "20" + hexTenLc.substring(0, 2)
				+ hexTenLc.substring(2, 4) + hexTenLc.substring(4, 6);
		String date1Lc = Integer.parseInt(removeZeroStr(hexTenLc.substring(6,
				14)), 16)
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate1 + " 里程：" + date1Lc);

		String sdate2 = "20" + hexTenLc.substring(16, 18)
				+ hexTenLc.substring(18, 20) + hexTenLc.substring(20, 22);
		String date2Lc = Integer.parseInt(removeZeroStr(hexTenLc.substring(22,
				30)), 16)
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate2 + " 里程：" + date2Lc);

		String sdate3 = "20" + hexTenLc.substring(30, 32)
				+ hexTenLc.substring(34, 36) + hexTenLc.substring(38, 40);
		String date3Lc = Integer.parseInt(removeZeroStr(hexTenLc.substring(40,
				48)), 16)
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate3 + " 里程：" + date3Lc);

		String sdate4 = "20" + hexTenLc.substring(48, 50)
				+ hexTenLc.substring(50, 52) + hexTenLc.substring(52, 54);
		String date4Lc = Integer.parseInt(removeZeroStr(hexTenLc.substring(54,
				62)), 16)
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate4 + " 里程：" + date4Lc);

		String sdate5 = "20" + hexTenLc.substring(62, 64)
				+ hexTenLc.substring(64, 66) + hexTenLc.substring(66, 68);
		String date5Lc = Integer.parseInt(removeZeroStr(hexTenLc.substring(68,
				72)), 16)
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate5 + " 里程：" + date5Lc);

		String sdate6 = "20" + hexTenLc.substring(72, 74)
				+ hexTenLc.substring(74, 76) + hexTenLc.substring(76, 78);
		String date6Lc = Integer.parseInt(removeZeroStr(hexTenLc.substring(78,
				86)), 16)
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate6 + " 里程：" + date6Lc);

		String sdate7 = "20" + hexTenLc.substring(86, 88)
				+ hexTenLc.substring(88, 90) + hexTenLc.substring(90, 92);
		String date7Lc = Integer.parseInt(removeZeroStr(hexTenLc.substring(92,
				100)), 16)
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate7 + " 里程：" + date7Lc);

		String sdate8 = "20" + hexTenLc.substring(100, 102)
				+ hexTenLc.substring(102, 104) + hexTenLc.substring(104, 106);
		String date8Lc = Integer.parseInt(removeZeroStr(hexTenLc.substring(106,
				114)), 16)
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate8 + " 里程：" + date8Lc);

		String sdate9 = "20" + hexTenLc.substring(114, 116)
				+ hexTenLc.substring(116, 118) + hexTenLc.substring(118, 120);
		String date9Lc = Integer.parseInt(removeZeroStr(hexTenLc.substring(120,
				128)), 16)
				+ "";
		log.info(
				this.getDeviceSN() + " " + sdate9 + " 里程：" + date9Lc);

		String sdate10 = "20" + hexTenLc.substring(128, 130)
				+ hexTenLc.substring(130, 132) + hexTenLc.substring(132, 134);
		String date10Lc = Integer.parseInt(removeZeroStr(hexTenLc.substring(
				134, 142)), 16)
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

	private void parsePosition(byte[] pos) {
		byte[] date = new byte[6];// 日期
		System.arraycopy(pos, 0, date, 0, date.length);

		Date dt = Tools.formatStrToDate(Tools.bcd2Str(date), "yyMMddHHmmss");
		String gpstime = Tools.formatDate2Str(dt, "yyyy-MM-dd HH:mm:ss");
		// Timestamp timeStamp = new Timestamp(dt.getTime());
		// this.setTimeStamp(timeStamp);
		this.setTime(gpstime);

		byte[] bx = new byte[4];// 经度
		System.arraycopy(pos, 6, bx, 0, bx.length);
		String x = Tools.formatXtoDu(this.removeZeroStr(Tools.bcd2Str(bx)));
		this.setCoordX(x);

		byte[] by = new byte[4];// 纬度
		System.arraycopy(pos, 10, by, 0, by.length);
		String y = Tools.formatYtoDu(this.removeZeroStr(Tools.bcd2Str(by)));
		this.setCoordY(y);

		byte bspeed = pos[14];// 单位km/h

		this.setSpeed(Tools.byte2Int(new byte[] { bspeed }) + "");

		int direction = Tools.byte2Int(new byte[] { pos[15] }) * 2;// 方向
		this.setDirection(direction + "");

		byte[] heigh = new byte[2];// 高度
		System.arraycopy(pos, 16, heigh, 0, 2);
		int h = Tools.byte2Int(heigh);
		this.setAltitude(h + "");

		byte[] distance = new byte[4];
		System.arraycopy(pos, 18, distance, 0, 4);
		String dis = Tools.bcd2Str(distance);
		double lc = Long.parseLong(dis) * 0.1d;// 单位KM
		String sdis = Tools.getNumberFormatString(lc, 1, 1);
		this.setMileage(sdis);

		// byte[] status = new byte[8];// 状态码
		// byte[] oil = new byte[2];
		// System.arraycopy(pos, 22, oil, 0, 2);
		// int ioil = Tools.byte2Int(oil);
		// log.info(
		// this.getDeviceSN() + " 油耗为：" + (ioil * 0.1) + "L");
		//
		// byte[] status_UCS = new byte[9];// 状态码
		// System.arraycopy(pos, 24, status_UCS, 0, 8);
		// this.parseStatus(status_UCS);

		byte[] status = new byte[8];// 状态码
		System.arraycopy(pos, 22, status, 0, 8);
		this.parseStatus(status);

		log.info(
				this.getDeviceSN() + " 通用GPS信息：dt=" + this.getTime() + ",x="
						+ this.getCoordX() + ",y=" + this.getCoordY() + ",s="
						+ this.getSpeed() + ",d=" + this.getDirection() + ",h="
						+ this.getAltitude() + ",dis=" + this.getMileage());

	}

	private void parseStatus(byte[] status) {

		TTermStatusRecord termStatus = new TTermStatusRecord();

		byte b1 = status[0];
		byte b2 = status[1];
		byte b3 = status[2];
		byte b4 = status[3];
		byte b5 = status[4];
		byte b6 = status[5];
		byte b7 = status[6];// 记录仪速度

		if (Tools.getByteBit(b3, 0) == 1) {
			termStatus.setLocate("1");
			log.info(this.getDeviceSN() + " 卫星定位已锁定！");
		} else {
			termStatus.setLocate("0");
			log.info(this.getDeviceSN() + " 卫星定位未锁定！");
		}
		 

		// ----------------------------第一字节------------------------------//
		if (Tools.getByteBit(b1, 0) == 1) {
			log.info(this.getDeviceSN() + " 坐标经度为 东经。");
		} else {
			log.info(this.getDeviceSN() + " 坐标经度为 西经。");
		}
		if (Tools.getByteBit(b1, 1) == 1) {
			log.info(this.getDeviceSN() + " 坐标纬度为 北纬。");
		} else {
			log.info(this.getDeviceSN() + " 坐标纬度为 南经。");
		}
		if (Tools.getByteBit(b1, 2) == 1) {
			log.info(this.getDeviceSN() + " 主动报警！");
			this.setAlarmType("3");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (Tools.getByteBit(b1, 3) == 1) {
			log.info(this.getDeviceSN() + " 断开油路！");
			termStatus.setOilElec("0");
		} else {
			log.info(this.getDeviceSN() + " 油路正常！");
			termStatus.setOilElec("1");
		}
		if (Tools.getByteBit(b1, 4) == 1) {
			log.info(this.getDeviceSN() + " 发生超速报警！");
			this.setAlarmType("1");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (Tools.getByteBit(b1, 5) == 1) {
			log.info(this.getDeviceSN() + " 发生震动报警！");
			this.setAlarmType("12");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (Tools.getByteBit(b1, 6) == 1) {
			log.info(this.getDeviceSN() + " 主电源断开！");
			termStatus.setMainPower("0");

			this.setAlarmType("5");
			AlarmQueue.getInstance().addAlarm(this);
		} else {
			termStatus.setMainPower("3");
		}

		// -------------------------第二字节-------------------------//
		if (Tools.getByteBit(b2, 0) == 1) {
			log.info(this.getDeviceSN() + " 刹车制动!");
			termStatus.setBrakeLight("1");
		} else {
			termStatus.setBrakeLight("0");
		}
		if (Tools.getByteBit(b2, 1) == 1) {
			log.info(this.getDeviceSN() + " 门打开！");
			termStatus.setCarDoor("1");
		} else {
			termStatus.setCarDoor("0");
			log.info(this.getDeviceSN() + " 门关闭！");
		}
		if (Tools.getByteBit(b2, 2) == 1) {
			log.info(this.getDeviceSN() + " 左转向灯开！");
			termStatus.setLeftLight("1");
		} else {
			termStatus.setLeftLight("0");
			log.info(this.getDeviceSN() + " 左转向灯关！");
		}
		if (Tools.getByteBit(b2, 3) == 1) {
			termStatus.setRightLight("1");
			log.info(this.getDeviceSN() + " 右转向灯开！");
		} else {
			termStatus.setRightLight("0");
			log.info(this.getDeviceSN() + " 右转向灯关！");
		}
		if (Tools.getByteBit(b2, 4) == 1) {
			termStatus.setFarLight("1");
			log.info(this.getDeviceSN() + " 远光灯开！");
		} else {
			termStatus.setFarLight("0");
			log.info(this.getDeviceSN() + " 远光灯关！");
		}
		if (Tools.getByteBit(b2, 5) == 1) {
			termStatus.setAcc("1");
			log.info(this.getDeviceSN() + " ACC开！");
		} else {
			termStatus.setAcc("0");
			log.info(this.getDeviceSN() + " ACC关！");
		}

		// ------------------------第三个字节------------------------------//
  
		if (Tools.getByteBit(b3, 1) == 1) {
			termStatus.setAntenna("0");
			this.setAlarmType(AlarmType.GPS_MAST_SHORT_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(this.getDeviceSN() + " 卫星定位天线短路！");
		}
		if (Tools.getByteBit(b3, 2) == 1) {
			termStatus.setAntenna("1");
			this.setAlarmType(AlarmType.GPS_MAST_OPEN_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(this.getDeviceSN() + " 卫星定位天线开路！");
		}
		if (Tools.getByteBit(b3, 1) == 0 && Tools.getByteBit(b3, 2) == 0) {
			// 天线正常
			termStatus.setAntenna("3");
		}
		if (Tools.getByteBit(b3, 3) == 1) {
			termStatus.setGpsModule("0");
			log.info(this.getDeviceSN() + " 定位模块异常！");
			this.setAlarmType(AlarmType.GPS_MODEL_FAIL_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		} else {
			termStatus.setGpsModule("1");
		}
		if (Tools.getByteBit(b3, 4) == 1) {
			termStatus.setGsmModule("0");
			log.info(this.getDeviceSN() + " 通讯模块异常！");

		} else {
			termStatus.setGsmModule("1");
		}
		if (Tools.getByteBit(b3, 5) == 1) {
			log.info(this.getDeviceSN() + " 出区域报警！");
			this.setAlarmType("2");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (Tools.getByteBit(b3, 6) == 1) {
			log.info(this.getDeviceSN() + " 入区域报警！");
			this.setAlarmType("2");
			AlarmQueue.getInstance().addAlarm(this);
		}

		// ------------------------------第四个字节---------------------------------//
		if (Tools.getByteBit(b4, 0) == 1) {
			termStatus.setBackupBattery("0");
			log.info(this.getDeviceSN() + " 备用电池异常！");
		} else {
			termStatus.setBackupBattery("1");
		}
		if (Tools.getByteBit(b4, 1) == 1) {
			log.info(this.getDeviceSN() + " 地理栅栏越界！");
		}
		if (Tools.getByteBit(b4, 2) == 1) {
			termStatus.setEngine("1");
			log.info(this.getDeviceSN() + " 发动机打开！");
		} else {
			termStatus.setEngine("0");
			log.info(this.getDeviceSN() + " 发动机关闭！");
		}
		if (Tools.getByteBit(b4, 3) == 1) {
			log.info(this.getDeviceSN() + " 疲劳驾驶报警！");
			this.setAlarmType("10");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (Tools.getByteBit(b4, 4) == 1) {
			log.info(this.getDeviceSN() + " 出道路报警！");
			this.setAlarmType(AlarmType.LINE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (Tools.getByteBit(b4, 5) == 1) {
			log.info(this.getDeviceSN() + " 入道路报警！");
			this.setAlarmType(AlarmType.LINE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);

		}
		// ------------------------------第五个字节（市标有）------------------

		byte[] oilb = new byte[] { b5, b6 };
		int oil = Tools.byte2Int(oilb);
		float foil = oil * 0.1f;

		int rcdInstruSpd = Tools.byte2Int(new byte[] { b7 });

		this.setSpeed(rcdInstruSpd + "");// 为GPS速度

		termStatus.setOilUsed(foil);
		termStatus.setDeviceId(this.getDeviceSN());
		// termStatus.setGpsTime(this.getTimeStamp());
		this.setStatusRecord(termStatus);
	}

	 
	public static void main(String[] args) {
		String hex = "7e47561604030a32c400000001030a32c40029551b100831170307113196200230828000a30079000655910300000000000000010a063cf767161b59a1437f";
		ParseUcastPlace u = new ParseUcastPlace();
		u.parseGPRS(hex);

		System.out.println(Tools.byte2Int((Tools.fromHexString("000007d9"))));
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
