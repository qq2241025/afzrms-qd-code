package com.mapabc.gater.directl.parse.wxp;

import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.constant.AlarmType;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.directl.pic.PicCache;
import com.mapabc.gater.directl.pic.Picture;
import com.mapabc.gater.directl.pic.PictureKey; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.service.CommonGatewayServiceImpl;
import com.mapabc.gater.lbsgateway.service.ICommonGatewayService;

public class ParseWxp extends ParseBase implements ParseService{
	TTermStatusRecord termStatus = new TTermStatusRecord();
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseWxp.class);
	 
	public void parseGPRS(String hexString) {
		// TODO Auto-generated method stub
		boolean isUdp = false;
		if (hexString.startsWith("03")) {// UDP的不需要转义
			hexString = "7e" + hexString + "7e";
			isUdp = true;
		}

		if (!hexString.startsWith("7e") || !hexString.endsWith("7e"))
			return;
		log.info("危险品协议原始数据:" + hexString);

		byte[] reverse = null;
		if (!isUdp) {
			reverse = WxpUtil.reverseEscape(Tools.fromHexString(hexString));
			hexString = Tools.bytesToHexString(reverse);
			log.info(
					"危险品协议反转义后数据:" + Tools.bytesToHexString(reverse));
		}

		Socket socketAddress = this.getSocket();
		
		try {
			String deviceId = null;
//			deviceId = GprsTcpThreadList.getInstance().getDeviceIdByTcpAddress(
//					socketAddress);
			ICommonGatewayService service = new CommonGatewayServiceImpl();
			service.getDeviceIdByTcpAddress(socketAddress);
			log.info(
					"==从缓存获取  （TCP客户端地址，序列号）《=》（" + socketAddress + ","
							+ deviceId + "）");

			if (deviceId != null) {
				this.setDeviceSN(deviceId);
			}
		} catch (Exception e) {

		}

		String ver = hexString.substring(2, 4);
		String code = hexString.substring(4, 6);
		String prority = hexString.substring(6, 8);
		String cmdid = hexString.substring(8, 10);
		String data = hexString.substring(10);
		data = data.substring(0, data.length() - 2);
		if (!isUdp)
			data = WxpUtil.revSigleCharEscape(data);

		if (cmdid.equalsIgnoreCase("01")) {
			String idlenhex = data.substring(16, 18);
			int idlen = Integer.parseInt(idlenhex, 16);

			String deviceId = data.substring(18, 18 + idlen * 2);
			deviceId = new String(Tools.fromHexString(deviceId)).trim();
			this.setDeviceSN(deviceId);

			log.info(
					this.getDeviceSN() + "危险品协议-登录数据:"
							+ Tools.bytesToHexString(reverse));

			String replyHex = WxpUtil.crtProtocal("03", "04", "81", "0101");
			this.setReplyByte(Tools.fromHexString(replyHex));

		} else if (cmdid.equals("02")) {
			String dataType = data.substring(8, 12);

			String ack = data.substring(12, 14);

			if (dataType.equals("1000")) {

				log.info(
						this.getDeviceSN() + " 设置中心号码应答结果：" + ack);
			} else if (dataType.equals("1089")) {
				ReplyResponseUtil.addReply("1089", "0");
				log.info(
						this.getDeviceSN() + " 远程升级更新应答:" + ack);

			} else if (dataType.equals("107a")) {
				ReplyResponseUtil.addReply("103a", "0");
				log.info(
						this.getDeviceSN() + " 远程升级设置应答:" + ack);

			} else if (dataType.equals("107b")) {
				ReplyResponseUtil.addReply("103b", "0");
				log.info(
						this.getDeviceSN() + " 配置移动台初始化参数应答:" + ack);
			} else if (dataType.equals("1043")) {
				ReplyResponseUtil.addReply("1043", "0");
				log.info(
						this.getDeviceSN() + " 监听应答:" + ack);

			} else if (dataType.equals("105a")) {
				ReplyResponseUtil.addReply("105a", "0");
				String statusHex = data.substring(12);
				String stsHex1 = statusHex.substring(0, 2);
				String stsHex2 = statusHex.substring(2, 4);
				byte st1 = Tools.fromHexString(stsHex1)[0];
				byte st2 = Tools.fromHexString(stsHex2)[0];
				if (Tools.getByteBit(st1, 0) == 1) {
					this.termStatus.setGpsModule("1");
					log.info(
							this.getDeviceSN() + " GPS模块状态正常。");
				} else {
					this.termStatus.setGpsModule("0");
					log.info(
							this.getDeviceSN() + " GPS模块状态异常。");
				}
				if (Tools.getByteBit(st2, 1) == 1) {
					this.termStatus.setDisplay("1");
					log.info(
							this.getDeviceSN() + " 调度显示屏状态正常。");
				} else {
					this.termStatus.setDisplay("0");
					log.info(
							this.getDeviceSN() + " 调度显示屏状态异常。");
				}

				String sensorHex = statusHex.substring(4, 6);
				byte sen = Tools.fromHexString(sensorHex)[0];
				if (Tools.getByteBit(sen, 0) == 1) {
					this.termStatus.setAcc("1");
					log.info(
							this.getDeviceSN() + " 传感ACC打开。");
				} else {
					this.termStatus.setAcc("0");
					log.info(
							this.getDeviceSN() + " 传感ACC异常。");
				}
				if (Tools.getByteBit(sen, 1) == 1) {
					this.termStatus.setCarDoor("0");
					Log.getInstance()
							.outLog(this.getDeviceSN() + " 车门关闭。");
				} else {
					this.termStatus.setCarDoor("1");
					Log.getInstance()
							.outLog(this.getDeviceSN() + " 车门打开。");
				}
				String gpsdata = statusHex.substring(6);
				this.parseGps(gpsdata);

			} else if (dataType.equals("1062")) {

				ReplyResponseUtil.addReply("1022", "0");
				String lenHex = ack.substring(0, 2);
				int len = Integer.parseInt(lenHex, 16);

				String verHex = data.substring(14);
				String vers = new String(Tools.fromHexString(verHex));
				log.info(
						this.getDeviceSN() + " 版本号:" + vers);
				this.termStatus.setCpu(vers);
				this.setStatusRecord(termStatus);
				 
			} else if (dataType.equals("4141")) {
				ReplyResponseUtil.addReply("4101", "0");
				log.info(
						this.getDeviceSN() + " 拍照设置应答:" + ack);
			} else if (dataType.equals("4150")) {
				String picData = data.substring(12);
				String deviceId = data.substring(12, 12 + 30);
				deviceId = new String(Tools.fromHexString(deviceId)).trim();
				this.setDeviceSN(deviceId);
				picData = data.substring(42);
				this.parseImage(picData);
			} else if (dataType.equals("4152")) {
				String picData = data.substring(12);
				String flag = picData.substring(0, 2);

				String bitFlag = Integer.toBinaryString(Integer.parseInt(flag,
						16));
				while (bitFlag.length() < 8) {
					bitFlag = "0" + bitFlag;
				}
				String type = "3";
				if (bitFlag.charAt(7) == '1') {
					log.info(
							this.getDeviceSN() + " 事件触发主动上报图片");
				} else {
					log.info(
							this.getDeviceSN() + " 中心抓拍上报图片");
				}

				String vers = bitFlag.substring(4, 6);
				log.info(
						this.getDeviceSN() + " 图片版本：" + vers);

				// 图片编号（索引号）
				String picNumHex = picData.substring(2, 6);
				int picNum = Integer.parseInt(picNumHex, 16);

				String totalPckHex = picData.substring(6, 10);
				int totalPcks = Integer.parseInt(totalPckHex, 16);

				String curPckHex = picData.substring(10, 14);

				String timesHex = picData.substring(14, 16);
				int tryTimes = Integer.parseInt(timesHex, 16);

				String ysim = "               ";
				String did = this.getDeviceSN();
				while (did.length() < 15) {
					did = did + " ";
				}
				String mobile = Tools.bytesToHexString((did + ysim).getBytes());
				PictureKey pkey = new PictureKey();
				pkey.setDeviceId(this.getDeviceSN());
				Picture picture = PicCache.getInstance().getPicture(pkey);

				String missPcks = "";
				if (picture != null)
					missPcks = picture.checkMissingPacks();

				// if (bitFlag.charAt(6) == '1') {
				String userData = mobile + picNumHex + curPckHex + "01";
				if (missPcks.length() > 0) {
					String[] mpck = missPcks.split(",");
					String countHex = Tools.int2Hexstring(mpck.length, 2);
					String misHex = "";
					for (int i = 0; i < mpck.length; i++) {
						misHex += Tools.convertToHex(mpck[i], 4);
					}
					log.info(
							this.getDeviceSN() + " 丢失的数据包号为：" + missPcks);
					userData += countHex + misHex;
				}

				String repSms = WxpUtil.crtImgageSmsPtl("2700", "1070", "41",
						"12", userData);
				String rep = WxpUtil.crtImageProtocal("03", "04", "83", repSms);
				ICommonGatewayService tcp = new CommonGatewayServiceImpl();
				tcp.sendDataToTcpTerminal(this.getDeviceSN(), rep.getBytes(), null);//(rep.getBytes());

				log.info(
						this.getDeviceSN() + " 超时图片帧需要中心应当:" + rep);

				// }

			} else {

				this.parseDataParam(data);
			}
		} else {

			this.parseDataParam(data);
		}

	}

	private void parseImage(String picData) {

		String flag = picData.substring(0, 2);

		String bitFlag = Integer.toBinaryString(Integer.parseInt(flag, 16));
		while (bitFlag.length() < 8) {
			bitFlag = "0" + bitFlag;
		}
		String type = "3";
		if (bitFlag.charAt(0) == '1') {
			log.info(this.getDeviceSN() + " 事件触发主动上报图片");
		} else {
			log.info(this.getDeviceSN() + " 中心抓拍上报图片");
		}

		String vers = bitFlag.substring(2, 4);
		log.info(this.getDeviceSN() + " 图片版本：" + vers);

		// 图片编号（索引号）
		String picNumHex = picData.substring(2, 6);
		int picNum = Integer.parseInt(picNumHex, 16);

		String totalPckHex = picData.substring(6, 10);
		int totalPcks = Integer.parseInt(totalPckHex, 16);

		String curPckHex = picData.substring(10, 14);
		int curPck = Integer.parseInt(curPckHex, 16) + 1;

		PictureKey pkey = new PictureKey();
		pkey.setDeviceId(this.getDeviceSN());
		// pkey.setPicNum(picNum);

		
		Picture picture = null;
		if (curPck == 1) {
			 picture = PicCache.getInstance().getPicture(pkey);
			String beginDate = picData.substring(14, 26);
			String picY = picData.substring(26, 34);
			String picX = picData.substring(34, 42);
			String lat = Tools.formatYtoDu1(this.getFormartXY(picY));
			this.setCoordY(lat);

			String lng = Tools.formatXtoDu1(this.getFormartXY(picX));
			this.setCoordX(lng);

			String pixel = picData.substring(42, 44);
			String quantily = picData.substring(44, 46);
			String typec = picData.substring(46, 48);
			String channel = picData.substring(48, 50);

			String dataLenHex = picData.substring(50, 54);
			int datalen = Integer.parseInt(dataLenHex, 16);
			// pkey.setChannel(Integer.parseInt(channel));

			String data = picData.substring(54, 54 + datalen * 2 + 2);

			log.info(
					this.getDeviceSN() + " 数据包长度：" + datalen + " 图片触发类型："
							+ typec + " 图像编号：" + picNum + ",第一包数据包号:" + curPck
							+ "，像素：" + pixel + ",质量：" + quantily + ",通道："
							+ channel + ",总包号：" + totalPcks + ",拍照开始时间："
							+ beginDate + ",经度：" + this.getCoordX() + ",纬度："
							+ this.getCoordY() + ",数据包：" + data);
			if (picture == null)
				picture = new Picture();
			picture.setChanelNo(channel);
			picture.setDeviceId(this.getDeviceSN());
			picture.setFirstReq(true);
			picture.setNum(picNum);
			picture.setPackcounts(totalPcks);
			picture.setPakcNo(curPck);
			picture.setType(type);
			try {
				picture.setX(Float.parseFloat(lng));
				picture.setY(Float.parseFloat(lat));
			} catch (Exception e) {

			}
			picture.setDate(new Date());
			picture.setTimeStamp(new Timestamp(System.currentTimeMillis()));
			picture.addImgContHex(String.valueOf(curPck), data);

			PicCache.getInstance().addMorePictureObj(pkey, picture);

		} else {
			  picture = PicCache.getInstance().getPicture(pkey);
			if (picture == null) {
				picture = new Picture();
			}
			picture.setDeviceId(this.getDeviceSN());
			picture.setNum(picNum);
			picture.setPackcounts(totalPcks);
			picture.setPakcNo(curPck);
			picture.setType(type);

			String dataLenHex = picData.substring(14, 18);
			int datalen = Integer.parseInt(dataLenHex, 16);

			String data = picData.substring(18, 18 + datalen * 2 + 2);
			// if (curPck==totalPcks){
			// data = picData.substring(18, 18+datalen*2-2);
			// }
			picture.addImgContHex(curPck + "", data);

			log.info(
					this.getDeviceSN() + " 数据包长度：" + datalen + " 图片触发类型："
							+ picture.getType() + " 图像编号：" + picNum + ",数据包号:"
							+ curPck + ",通道：" + picture.getChanelNo() + ",总包号："
							+ totalPcks + ",经度：" + picture.getX() + ",纬度："
							+ picture.getY() + ",数据包：" + data);
			PicCache.getInstance().addMorePictureObj(pkey, picture);

			if (picture.isTraverOver()) {
				String ysim = "               ";
				String did = this.getDeviceSN();
				while (did.length() < 15) {
					did = did + " ";
				}
				String mobile = Tools.bytesToHexString((did + ysim).getBytes());
				String userData = mobile + picNumHex + curPckHex + "01";
				String repSms = WxpUtil.crtImgageSmsPtl("2700", "1070", "41", "10",
						userData);
				String rep = WxpUtil.crtImageProtocal("03", "04", "83", repSms);
				this.setReplyByte1(rep.getBytes());
				log.info(
						this.getDeviceSN() + " 最好一包图片帧需要中心应当:" + rep);

				log.info(
						this.getDeviceSN() + " " + picNum + "编号图片传入完毕！");
				DBService service = new DBServiceImpl();
				service.insertPicInfo(picture);
				picture.reset();
				PicCache.getInstance().removePicture(pkey);
			}

		}
		if (bitFlag.charAt(1) == '1') {
			String ysim = "               ";
			String did = this.getDeviceSN();
			while (did.length() < 15) {
				did = did + " ";
			}
			String mobile = Tools.bytesToHexString((did + ysim).getBytes());
			String userData = mobile+ picNumHex + curPckHex;
			String missPcks = "";
			if (picture != null)
				missPcks = picture.checkMissingPacks();

			 
			if (missPcks.length() > 0) {
				userData += "01";
				String[] mpck = missPcks.split(",");
				String countHex = Tools.int2Hexstring(mpck.length, 2);
				String misHex = "";
				for (int i = 0; i < mpck.length; i++) {
					misHex += Tools.convertToHex(mpck[i], 4);
				}
				log.info(
						this.getDeviceSN() + " 丢失的数据包号为：" + missPcks);
				userData += countHex + misHex;
			}else{
				userData += "01";
			}
			
			String repSms = WxpUtil.crtImgageSmsPtl("2700", "1070", "41", "10",
					userData);
			String rep = WxpUtil.crtImageProtocal("03", "04", "83", repSms);
			ICommonGatewayService tcp = new CommonGatewayServiceImpl();
			tcp.sendDataToTcpTerminal(this.getDeviceSN(), rep.getBytes(), null);//(rep.getBytes());

			log.info(
					this.getDeviceSN() + " 图片帧需要中心应当:" + rep);

		}
	}

	private void parseDataParam(String dataHex) {
		log.info(this.getDeviceSN() + " 业务数据帧：" + dataHex);
		String areaNo = dataHex.substring(0, 4);
		String version = dataHex.substring(4, 8);
		String businessType = dataHex.substring(8, 10);
		String dataType = dataHex.substring(10, 12);
		String userData = dataHex.substring(12, dataHex.length() - 2);

		if (businessType.equals("01")) {
			if (dataType.equals("45") || dataType.equals("54")
					|| dataType.equals("55")) {
				String alarmType = userData.substring(0, 4);
				String gpsCntHex = userData.substring(4, 6);
				int cnt = Integer.parseInt(gpsCntHex, 16);

				String gpsdata = userData.substring(6);
				if (!dataType.equals("55")) {
					this.parseGps(gpsdata);
					log.info(
							this.getDeviceSN() + " 位置信息：X=" + this.getCoordX()
									+ ",y=" + this.getCoordY() + ",s="
									+ this.getSpeed() + ",t=" + this.getTime());
				} else {
					this.parseBlindGps(gpsdata, cnt);
				}

				this.parseAlarm(alarmType);
			} else if (dataType.equals("41")) {// 频率设置应答
				ReplyResponseUtil.addReply("0101", "0");
				String type = userData.substring(0, 2);
				String gpsdata = userData.substring(2);
				this.parseGps(gpsdata);
				log.info(
						this.getDeviceSN() + " 设置回传参数应答：X=" + this.getCoordX()
								+ ",y=" + this.getCoordY() + ",s="
								+ this.getSpeed() + ",t=" + this.getTime());

			} else if (dataType.equals("51")) {// 点名
				ReplyResponseUtil.addReply("0111", "0");
				String type = userData.substring(0, 2);
				String gpsdata = userData.substring(2);
				this.parseGps(gpsdata);
				log.info(
						this.getDeviceSN() + " 点名GPS：X=" + this.getCoordX()
								+ ",y=" + this.getCoordY() + ",s="
								+ this.getSpeed() + ",t=" + this.getTime());
			}
		} else if (businessType.equals("03")) {
			if (dataType.equals("41")) {
				String type = userData.substring(0, 2);
				String gpsdata = userData.substring(2);
				this.parseGps(gpsdata);

				String btype = Integer.toBinaryString(Integer
						.parseInt(type, 16));
				while (btype.length() < 8) {
					btype = "0" + btype;
				}
				type = btype.substring(4);
				if (type.equals("0000")) {
					log.info(
							this.getDeviceSN() + " 医疗求助==》碰撞报警");
					this.setAlarmType(AlarmType.COLLIDE_ALARM_TYPE);
					AlarmQueue.getInstance().addAlarm(this);

				} else if (type.equals("0001")) {
					log.info(
							this.getDeviceSN() + " 交通事故==》卸料报警");
					this.setAlarmType(AlarmType.DISCHARGE_ALARM_TYPE);
					AlarmQueue.getInstance().addAlarm(this);
				} else if (type.equals("0010")) {
					log.info(
							this.getDeviceSN() + " 纠纷==》丢失报警");
					this.setAlarmType(AlarmType.LOSING_ALARM_TYPE);
					AlarmQueue.getInstance().addAlarm(this);
				}

			}
		} else if (businessType.equals("10")) {

		}

	}

	private void parseBlindGps(String gpsdata, int c) {

		byte[] bgpsdata = Tools.fromHexString(gpsdata);

		int datalen = bgpsdata.length / c;
		ArrayList<ParseBase> pblist = new ArrayList<ParseBase>();

		for (int m = 0; m < c; m++) {
			byte[] tmpgpsdata = new byte[datalen];
			System.arraycopy(gpsdata, m * datalen, tmpgpsdata, 0, datalen);

			this.parseGps(Tools.bytesToHexString(tmpgpsdata));
			pblist.add((ParseBase) this);

			log.info(
					this.getDeviceSN() + " 盲区补偿位置信息：X=" + this.getCoordX()
							+ ",y=" + this.getCoordY() + ",s="
							+ this.getSpeed() + ",t=" + this.getTime());

		}
	}

	private void parseGps(String gpsdata) {

		String type = gpsdata.substring(0, 2);
		String date = gpsdata.substring(2, 8);
		date = Tools.fillZeroFront(""
				+ Integer.parseInt(date.substring(0, 2), 16), 2)
				+ Tools.fillZeroFront(""
						+ Integer.parseInt(date.substring(2, 4), 16), 2)
				+ Tools.fillZeroFront(""
						+ Integer.parseInt(date.substring(4), 16), 2);

		String time = gpsdata.substring(8, 14);
		time = Tools.fillZeroFront(""
				+ (Integer.parseInt(time.substring(0, 2), 16) + 8), 2)
				+ Tools.fillZeroFront(""
						+ Integer.parseInt(time.substring(2, 4), 16), 2)
				+ Tools.fillZeroFront(""
						+ Integer.parseInt(time.substring(4), 16), 2);

		Date gpsdate = Tools.formatStrToDate(date + time, "yyMMddHHmmss");
		this.setTime(Tools.formatDate2Str(gpsdate, "yyyy-MM-dd HH:mm:ss"));

		String status = gpsdata.substring(14, 16);
		int istatus = Integer.parseInt(status, 16);
		String statsBit = Integer.toBinaryString(istatus);
		while (statsBit.length() < 8) {
			statsBit = "0" + statsBit;
		}
		if (statsBit.charAt(2) == '1') {
			log.info(this.getDeviceSN() + " GPS数据无效");
			this.termStatus.setLocate("0");
		} else {
			log.info(this.getDeviceSN() + " GPS数据有效");
			this.termStatus.setLocate("1");
		}
		if (statsBit.charAt(3) == '1') {
			log.info(this.getDeviceSN() + " ACC开");
			this.termStatus.setAcc("1");
		} else {
			log.info(this.getDeviceSN() + " ACC关");
			this.termStatus.setAcc("0");
		}
		if (statsBit.charAt(4) == '1') {
			log.info(this.getDeviceSN() + " 重车");
			this.termStatus.setFullEmpty("1");
		} else {
			log.info(this.getDeviceSN() + " 空车");
			this.termStatus.setFullEmpty("0");
		}
		if (statsBit.charAt(5) == '1') {
			log.info(this.getDeviceSN() + " 省电");
			//this.termStatus.setHighElecOne("1");
		} else {
			log.info(this.getDeviceSN() + " 非省电");
			//this.termStatus.setHighElecOne("0");
		}

		String hx = String.valueOf(statsBit.charAt(4));
		String hs = String.valueOf(statsBit.charAt(5));

		String latHex = gpsdata.substring(16, 24);
		String lat = Tools.formatYtoDu1(this.getFormartXY(latHex));
		this.setCoordY(lat);

		String lngHex = gpsdata.substring(24, 32);
		String lng = Tools.formatXtoDu1(this.getFormartXY(lngHex));
		this.setCoordX(lng);

		String spdHex = gpsdata.substring(32, 34);

		int spd = Integer.parseInt(spdHex, 16);
		String speed = Tools.formatKnotToKm(spd + "");
		this.setSpeed(speed);
		if (gpsdata.length() >= 36) {
			String dirctHex = gpsdata.substring(34, 36);
			int direction = Integer.parseInt(dirctHex, 16) * 3;
			this.setDirection(direction + "");
		}
		this.setStatusRecord(termStatus);
		 
		log.info(
				this.getDeviceSN() + " GPS信息,x=" + this.getCoordX() + ",y="
						+ this.getCoordY() + ",s=" + this.getSpeed()
						+ ",direction=" + this.getDirection());
	}

	private String getFormartXY(String xy) {
		String flat = "";
		for (int i = 0; i < xy.length(); i = i + 2) {
			if (xy.substring(i, i + 2).equals("7f")) {
				flat += "00";
			} else {
				flat += xy.substring(i, i + 2);
			}
		}
		int latDu = Integer.parseInt(flat.substring(0, 2), 16);
		int zyfen = Integer.parseInt(flat.substring(2, 4), 16);
		int zyfen1 = Integer.parseInt(flat.substring(4, 6), 16);
		int zyfen2 = Integer.parseInt(flat.substring(6), 16);
		String fy = Tools.fillZeroFront("" + latDu, 2)
				+ Tools.fillZeroFront("" + zyfen, 2) + "."
				+ Tools.fillZeroFront("" + zyfen1, 2)
				+ Tools.fillZeroFront("" + zyfen2, 2);
		return fy;
	}

	private void parseAlarm(String alarmType) {

		String alarm1 = alarmType.substring(0, 2);
		String alarm2 = alarmType.substring(2);
		String bits = Integer.toBinaryString(Integer.parseInt(alarm1, 16));
		while (bits.length() < 8) {
			bits = "0" + bits;
		}

		String bits1 = Integer.toBinaryString(Integer.parseInt(alarm2, 16));

		while (bits1.length() < 8) {
			bits1 = "0" + bits1;
		}
		String outAlarm = bits.substring(1, 3);
		if (outAlarm.equals("01")) {
			log.info(this.getDeviceSN() + " 震动报警");
		} else if (outAlarm.equals("10")) {
			log.info(this.getDeviceSN() + " 非法开门报警");
		}
		if (bits.charAt(3) == '1') {
			log.info(this.getDeviceSN() + " 越区域报警");
		}
		if (bits.charAt(4) == '1') {
			log.info(this.getDeviceSN() + " 超速报警");
		}
		if (bits.charAt(5) == '1') {
			log.info(this.getDeviceSN() + " 在规定时间外行驶报警");
		}
		if (bits.charAt(6) == '1') {
			log.info(this.getDeviceSN() + " 欠压报警");
		}
		if (bits.charAt(7) == '1') {
			log.info(this.getDeviceSN() + " 停留时间过长报警");
		}

		// -------------------------------------------------------//
		if (bits1.charAt(1) == '1') {
			log.info(this.getDeviceSN() + " 断路报警");
		}
		if (bits1.charAt(2) == '1') {
			log.info(this.getDeviceSN() + " 抢劫报警");
			this.setAlarmType("3");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (bits1.charAt(3) == '1') {
			log.info(this.getDeviceSN() + " 非法发动车");
		}
		if (bits1.charAt(4) == '1') {
			log.info(this.getDeviceSN() + " 卸料报警");
			this.setAlarmType("36");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (bits1.charAt(5) == '1') {
			log.info(this.getDeviceSN() + " 偏航报警");
		}

	}

 

	public static void main(String[] args) {
		System.out.println("7e".substring(0, 0));
		ParseWxp wxp = new ParseWxp();
		wxp
				.parseGPRS("03c5080227001070415031353031313238303834352020202021003b0004000205007350eea7034012eea375479a334012e68df9a88bd3738a0094bd3775479a4dd400f2fc542adcd0c78a8d2802626a32692984d003b3519a5cd34d00329a6969a6900d34c34ea43400ca6e69c69b4806d34d3cf4a6d03204fbf8a9302a31feb735350047b4530c62a4a4a404a9fbc97e9566a38530bef5255887502929d40052d145002d3a9b4b40052d252d002d2d252d0035cf6a9107cb50fde7a9c74a007d2d368a007e68cd37345003f34ecd474b9a0093bd04d33345003a834ccf34b9a005a6e68cd3734008e78a6a9a490d22d00389a69a3bd26680129b4b4da00434d34a69b4804a6669c4d30d00069b4ea6d218da434b4948083fe5ad4b51b7fada96980c34da71a6d202f0a75343ad3b23356216968a2800a75252d0014b494b40052d145002d21e296a395bb5002c632d9a9aa38feed49400ea2928f6a005a28a280169734dcd1400ea334da4a00752d474b9a007536929b9a00649d69e3a5424fcf52d002d368a4a0029b4b4da4025369692801b494b4da4036929d4da63129294d36a4089fefd4bdaa293ad483a530129b4ea6d303fffd1ed7ca4a4f2aa5a29010f96deb4624a9e8a604199053848c3a8a9a8a008fcdf6a77980d3b029360f4a00378f5a7e699e50a4f2a8024cd40c77498a52a57bd355589c8a00b229d508f329773d004d4b516f3e94bbe8024a29824146e5a007d14ddc28dd400b4945140052668cd25002d368a693400cfe3a96a04fbf53d201292969298094da5a4ed4ae025252f6a6e698094da75277a901b4da71a4a431b4d34ea4a008a5ed4e5e94d9beed3a3fbb400b4def4ea6d0068d2d14b5621296968c5001452d2d00368a5a7500368a5a5a00865f4a7a0c0a6757a9a800a5c52d1400946de29d4b4011ec1479629f4628023f2e9be59f5a9a8a0087637ad3487a9e928020f9e9bbdbd2ac1a6d0043e61f4a634bed53e054722281d29011a300d52f98b4c48c1a77942980ede2937534c3e8699e59f5a00937515118dfb1a4c3fad2024cd150fef29bbe414013521a8bce6f4a699bda8025eb4951f9b4be68a062d2526f5f5a322900c9beed11fdda24c6deb4911e2801e693bd3a928034b1453a971542131452d140094b4b4b400da29d450025237dda7e2a293d280123152d220c2d3e80128a5a5a006d2d2e28a004ed494ea4a004a7514b400dc5262a5a46a008a99521a650036a393a54b4c7193400883e5a7528e98a2801b4d34ea4a0065277a5345201869b4f349ef40094cc734fa6d00376afa534a2fa53e834011796b4c68454d8a4a0081e32075a62a9353b8f90d321a4026d7a69f30558a4a4335296929d5620a29696801314b4b4ec500371453f14b8a0065438dcf561b8151a0ef400ec52e29d4500371498a7d26da004a314ec52e28019da8a7628a006d2d2d1400521a5a4a008da994f3d69b8a006d31bef549517f1d003c74a4a75250030d369f4cef40094da71a4a0069e9451494804cd253a9b400949de9d4dc5201b452d253018df72a38bad4847c951c7c1a404dda8a292819aabd29d453855084c52d2e29450018a7629c052eda004c51b7352629714015e41da95130b4ac373d4db78a008b1462a5db46da008b145498a4c500376d2e29f8a5db40116da6f4a9f6d3714c08f1462a4c53714808e9b52114cc500474da9314dc50030f4a897ef54ad51a75a007514b49400da4a753680198a4a71a4a006521a75262900de6929f4da006d252d250312929def4dfc69086b74a862fbd529a8d3efd0325a4a5a29d80ffd2f45a5a4039a78a005152629a07351c");
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
