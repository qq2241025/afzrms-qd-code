package com.mapabc.gater.directl.parse;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory; 
   
import com.eaio.uuid.UUID;
import com.mapabc.gater.directl.Base64;
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.constant.AlarmType;
import com.mapabc.gater.directl.dbutil.DbOperation;
import com.mapabc.gater.directl.dbutil.DbUtil;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.dbutil.service.DBServiceImpl;
import com.mapabc.gater.directl.encode.Controller;
import com.mapabc.gater.directl.encode.PropertyReader;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.directl.pic.PicCache;
import com.mapabc.gater.directl.pic.Picture; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.service.CommonGatewayServiceImpl;
import com.mapabc.gater.lbsgateway.service.ICommonGatewayService;
import com.mapabc.gater.util.OverLoadUtil;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.geom.CoordCvtAPI;
import com.mapabc.geom.DPoint;

//山东 
public class ParseLingTu extends ParseBase  implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseLingTu.class);
	private boolean isChecked = false;
	TTermStatusRecord statusRecord = new TTermStatusRecord();
	
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
	String oemCode = null;
	private static String isOverLoad = null;// 是否有负载
	private PropertyReader proRead;

	public ParseLingTu() {
		try {
			proRead = new PropertyReader("load.properties");
			isOverLoad = proRead.getProperty("isOverLoad");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void parseGPRS(String hex) {
		// A{space}N{space}OEMCODE:COMADDR{space}UTC|Latitude|Longitude|Altitude|Heading|Speed|
		// TransactionFlag|OEMStatus|StatusString{space}VERFYCODE\r\n
		// A P 4C54:13455141509
		// 090217044957|07DD7458|191BB06C|80000000|0108|73C0|00000000,00000000|00040004,000021A2|
		// 1713
		String hexString = hex;
//		int endIndex = hex.lastIndexOf("0d0a"); // "\r\n结束符合"
//		if (endIndex != -1) {
//			hexString = hex.substring(0, endIndex);// 截取\r\n之前的字节
//		} else {
//			log.info("灵图协议车机数据格式有误：" + hex);
//			return;
//		}
		byte[] bc = Tools.fromHexString(hexString);

		String content = new String(bc);
		String[] splitcont = content.split(" "); // 以空格分隔指令
		log.info("灵图车机数据：" + content);

		String cmdtype = splitcont[1]; // 上行类型

		
		String deviceId = null;
		try {
			oemCode = splitcont[2].substring(0, splitcont[2].indexOf(":")); // OEM码
			deviceId = splitcont[2].substring(splitcont[2].indexOf(":") + 1);// 终端ID
			 
			this.setDeviceSN(deviceId);
			 
		} catch (Exception e) {
			e.printStackTrace();
		}

		String vertyCont = "";

		vertyCont = content.substring(4, content.lastIndexOf(" ") + 1);

		byte[] baow = vertyCont.getBytes(); // 校验和前内容

		int checksum = 0;
		String hexVerty = splitcont[splitcont.length - 1];

		String rep = ""; // 回应终端变量
		if (cmdtype.equals("N")) {
			// 090211024834|07DDF7F2|191B5C60|80000000|0|0|N|80000000|00040004|

			String loginmsg = splitcont[3]; // 登陆时的GPS信息
			String[] loginArra = loginmsg.split("\\|");
//			StringBuffer sbuf = new StringBuffer();
//
//			if (loginArra.length >= 9) {
//				for (int k = 0; k < loginArra.length; k++) {
//					if (k != 6) {
//						sbuf.append(loginArra[k]);
//						sbuf.append("|");
//					}
//				}
//			} else {
//				sbuf.append(loginmsg);
//			} 
			try {
				 

				String frq =  getFreByDeviceId(deviceId);
				if (frq != null && !frq.trim().equals("")) {
					String freq = this.timeInter(frq, oemCode);
					Log.getInstance().outLog(
							this.getDeviceSN() + "登录时下发频率：" + freq);
					this.setReplyByte1(freq.getBytes());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				boolean oemFlag = setTerminalOemCode(oemCode, this
						.getDeviceSN());
				if (!oemFlag) {
					log.info(
							"设置" + this.getDeviceSN() + "的OEM码："
									+ oemCode + " 失败！");
					return;
				}
			} catch (Exception e) {
			}
			this.parsePosition(loginmsg); // 解析位置信息
			String cmd = oemCode + ":" + this.getDeviceSN();
			rep = "C R " + cmd + "|N " + this.getVerfyCode(cmd + "|N ");
			this.setReplyByte(rep.getBytes());

			log.info(
					this.getDeviceSN() + "车机登陆时间：" + this.getTime()
							+ " 回复终端信息：" + rep);

		} else if (cmdtype.equals("T")) {

			String loginmsg = splitcont[3]; // 离线时的GPS信息
			String[] loginArra = loginmsg.split("\\|");
			StringBuffer sbuf = new StringBuffer();

			if (loginArra.length >= 9) {
				for (int k = 0; k < loginArra.length; k++) {
					if (k != 6) {
						sbuf.append(loginArra[k]);
						sbuf.append("|");
					}
				}
			} else {
				sbuf.append(loginmsg);
			}
			this.parsePosition(sbuf.toString()); // 解析位置信息 
			log.info(
					this.getDeviceSN() + "车机离线,离线时间：" + this.getTime());

		} else if (cmdtype.equals("K")) {

			String cmd = oemCode + ":" + this.getDeviceSN() + "|K ";
			rep = "C R " + cmd + this.getVerfyCode(cmd);
			this.setReplyByte(rep.getBytes());

			//			
			Log.getInstance()
					.outLog(
							this.getDeviceSN() + "维持连接:" + hexString
									+ " 回应终端信息：" + rep);

		} else if (cmdtype.equals("P")) {

			this.parsePosition(splitcont[3]);

			log.info("定位报文：" + hexString);

		} else if (cmdtype.equals("F")) {

			try {

//				this.parsePosition(splitcont[3]);
				// 回应0X302 :C M 1 4C54:1001|302|1;ff 4C1
				String cmd = oemCode + ":" + this.getDeviceSN()
						+ "|302|1;ff ";
				rep = "C M " + Tools.getRandomString(4) + " " + cmd
						+ this.getVerfyCode(cmd);
				try {
					ICommonGatewayService icgs = new CommonGatewayServiceImpl();
					icgs.sendDataToUdpTerminal(this.getDeviceSN(), rep.getBytes(), null);
					 
				} catch (Exception e) { 
					e.printStackTrace();
					this.setReplyByte1(rep.getBytes());
				}

				this.parsePosition(splitcont[3]);

				Picture pic = new Picture();// .getInstance();
				pic.setFirstReq(true);
				// pic.setDate(new Date());
				pic.setDeviceId(this.getDeviceSN());
				pic.setX(Float.parseFloat(this.getCoordX()));
				pic.setY(Float.parseFloat(this.getCoordY()));
				pic.setTimeStamp(new Timestamp(Tools.formatStrToDate(this.getTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
				// pic.setOemCode(this.getOemCode());
 			
				PicCache.getInstance().addPicture(this.getDeviceSN(), pic);

				// PictureCache.getInstance().addPacks("0", pic);

				log.info("图片请求报文：" + hexString);
				log.info(
						"上传照片时的GPS信息：经度=" + this.getCoordX() + ",纬度="
								+ this.getCoordY() + ",时间=" + this.getTime());
			} catch (Exception e) {
				Log.getInstance().errorLog("解析图片请求异常", e);
			}

		} else if (cmdtype.equals("U")) {
			 

			String date = splitcont[3];
			String dates = date.substring(4, 6) + date.substring(2, 4)
					+ date.substring(0, 2);// ddMMyy
			String times = date.substring(6);// hhmmss
			String format_date = Tools.conformtime(times, dates);// 传输时间
			String trackNum = splitcont[4]; // 传输的通道
			String totalNum = splitcont[5]; // 图像总块数
			int tnum = Integer.parseInt(totalNum, 16);
			String orderNum = splitcont[6]; // 图像编号
			int onum = Integer.parseInt(orderNum, 16);
			// 回应终端，C M 1 4C54:1001|303|1;1 427
			String cmd = oemCode + ":" + this.getDeviceSN() + "|303|"
					+ trackNum + ";" + orderNum + " ";
			rep = "C M " + Tools.getRandomString(4) + " " + cmd
					+ this.getVerfyCode(cmd);
			this.setReplyByte1(rep.getBytes());

			String hexImg = null;

			try {
				String head = content.substring(0, content.indexOf("<") + 1);
				log.info("image head:" + head);

				String end = content.substring(content.length()
						- hexVerty.length() - 2);
				log.info("end:" + end);

				byte[] imageByte = new byte[bc.length - head.length()
						- end.length()];
				System.arraycopy(bc, head.getBytes().length, imageByte, 0,
						imageByte.length);
				hexImg = Tools.bytesToHexString(imageByte);

				log.info(
						"DDD服务收到原始数据字节数=" + (hex.length() / 2) + "原始数据报内容："
								+ hex);
				log.info(
						(onum) + " image bytes size:" + imageByte.length
								+ "cont:" + Tools.bytesToHexString(imageByte));
			} catch (Exception e) {

				Log.getInstance().errorLog(null, e);
				e.printStackTrace();
				hexImg = null;
			}
			boolean isHaveReq = false;
			
			Picture pic = PicCache.getInstance().getPicture(this.getDeviceSN());
			if (pic != null)
				isHaveReq = pic.isFirstReq();
			
			log.info("本机是否存在A F请求包：" + isHaveReq);

			if (isHaveReq) {// 有图片位置时A F指令时

				// Picture pic = new Picture();
				if (pic.get(onum + "") == null) {
					pic.setDeviceId(this.getDeviceSN());
					pic.setPackcounts(tnum);
					pic.setPakcNo(onum);
					pic.setFirstReq(isHaveReq);
					pic.setX(pic.getX());
					pic.setY(pic.getY());
					pic.setTimeStamp(pic.getTimeStamp());
					pic.setType("0");
					pic.setDate(new Date());
					pic.setOemCode(oemCode);
					pic.setReqImageCmd(this.sendPicCmd(onum+""));
					pic.setChanelNo(trackNum);

					log.info(
							this.getDeviceSN() + "图片包基本信息:x=" + pic.getX()
									+ ",y=" + pic.getY() + ",time="
									+ pic.getTimeStamp());

					if (hexImg != null && onum > 0) {
						pic.addImgContHex(onum + "", hexImg);
						PicCache.getInstance().addPicture(this.getDeviceSN(),
								pic);
					}
					if (pic.isTraverOver()) {
						// 传输完毕
						log.info(
								this.getDeviceSN() + "图片传输完毕！");
						this.isChecked = true; 

						DBService service = new DBServiceImpl();
						try {
							boolean flag = service.insertPicInfo(pic);
						} catch (Exception e) { 
							e.printStackTrace();
						} finally {
							pic.reset();
							PicCache.getInstance().removePicture(
									this.getDeviceSN());
							pic = null;

						}

					log.info(
							this.getDeviceSN() + "图片传输通道：" + trackNum + ",总包数="
									+ tnum + ",当前包数=" + onum);
				} else {

					log.info(
							"设备" + this.getDeviceSN() + "缓存中已经存在第" + onum
									+ "包数据");
				}

			} else if (isOverLoad != null && isOverLoad.equals("1")) {// 负载转发
//				if (dataUdpAddr != null && overUdpAddr != null) {
//					overUdpAddr = overUdpAddr.substring(0, overUdpAddr
//							.indexOf(":"));
//					if (dataUdpAddr.contains(overUdpAddr)) {
//						Log.getInstance().outJHSLoger(
//								this.getDeviceSN()
//										+ " 是负载机器转发过来的包，并且该机器也没有第一个请求包，不继续转发。");
//						return;
//					}
//				}
				String udpAddr = proRead.getProperty("overLoadUdpAddr");
				String host = udpAddr.split(":")[0];
				String sport = udpAddr.split(":")[1];
				int port = Integer.parseInt(sport);
				byte[] b = Tools.fromHexString(hex);

				OverLoadUtil.sendToUdp(host, port, b);

			}

			// log.info("图片数据报文：" + hexString);

		} else if (cmdtype.equals("M")) {
			// A M 7367:1001 060118121010|AGwAaQBuAGcAdAB1 XXX\r\n

			String MSG = splitcont[3].split("\\|")[1];
			try {
				byte[] msgB = Base64.base64Decode(MSG);
				String msgCont = new String(msgB, "UTF-16");
				DBService service = new DBServiceImpl();
				service.saveMessage(this.getDeviceSN(), "", msgCont, "", null);

				log.info("上传短信报文：" + msgCont);
			} catch (IOException e) {
				// TODO 自动生成 catch 块
				e.printStackTrace();
			}

		} else if (cmdtype.equals("R")) {
			// A R 7367:13698607474 000000000000|444|00 89F
			String detail = splitcont[3];
			String[] tails = detail.split("\\|");

			String date1 = tails[0];
			String dates1 = date1.substring(4, 6) + date1.substring(2, 4)
					+ date1.substring(0, 2);// ddMMyy
			String times1 = date1.substring(6);// hhmmss
			String format_date1 = Tools.conformtime(times1, dates1);// 登陆日期yyyy-MM-dd
			// HH:mm:ss

			String hexSeq = tails[1];
			int seq = 0;
			try {
				seq = Integer.parseInt(hexSeq);
			} catch (Exception e) {
				//seq = Integer.parseInt(hexSeq, 16);
			}
			String status = null;
			if (tails[2].indexOf(";") != -1) {
				status = tails[2].substring(0, tails[2].indexOf(";"));
			} else {
				status = tails[2];
			}
			DbOperation dopt = new DbOperation();
			// 异常处理

			int state = 1;
			try {
				state = Integer.parseInt(status, 16);
			} catch (Exception e) {
				state = -1;
			}
			if (state == 0) {
				ReplyResponseUtil.addReply(hexSeq, "0");

				//dopt.updateInstructionsState(this.getDeviceSN(), "0", seq);
				log.info(
						this.getDeviceSN() + "更新状态成功,日期：" + format_date + "id:"
								+ seq + " " + hexSeq);
			} else {
				//dopt.updateInstructionsState(this.getDeviceSN(), "2", seq);
				ReplyResponseUtil.addReply(hexSeq, "1");
				log.info(
						this.getDeviceSN() + "更新状态失败,日期：" + format_date
								+ " 指令内容：" + hexString);
			}

		} else if (cmdtype.equals("D")) { 
			
		}  else if (cmdtype.equals("G")) {// 外设透传数据

			this.parsePosition(splitcont[3]);

		}else if (cmdtype.equals("X")) {
		 


			String subtype = "";
			String[] splitCont = content.split(" ");
			subtype = splitCont[4];
			if (subtype.equalsIgnoreCase("E01")) {
				String ymd = Tools.formatDate2Str(new Date(), "yyyyMMdd");
				String hms = Tools.formatDate2Str(new Date(), "HHmmss");
				String datess = this.setLedDate("1", oemCode, ymd, hms);
				this.setReplyByte(datess.getBytes());
				Log.getInstance().outLog(this.getDeviceSN() + " 时间校对：" + datess);
			} else if (subtype.equals("510")) {
				Log.getInstance().outLog("油耗数据报文：" + hexString);
				String oilmass = splitCont[5]; // 瑜版挸澧犲▽纭咃拷妤呭櫤閿涘苯宕熸担锟�.1閸楋拷
				String oilwear = splitCont[6]; // 閸旂姵琛ラ柌蹇ョ礉閸楁洑缍�.1閸楋拷
				float foilmass = Integer.parseInt(oilmass) * 0.1f;
				float foilwear = Integer.parseInt(oilwear) * 0.1f;
				Log.getInstance().outLog(
						this.getDeviceSN() + "当前油耗=" + foilmass + "升，本次加油量="
								+ foilwear);
				this.statusRecord.setOilUsed(foilmass);// .setOilMass(foilmass);
				this.statusRecord.setOilAdded(foilwear);
				this.setStatusRecord(statusRecord);
				 
			} else if (subtype.equalsIgnoreCase("E02")) {// 娑撳褰傛惔鏃傜摕
				// A X 7868:13600030075 041231184539 E02 3E8,1, 0809
				Log.getInstance().outLog(
						this.getDeviceSN() + " E02 指令：" + content);
				String indexHex = splitCont[5].split(",")[0].toLowerCase();

				long index = Long.parseLong(indexHex, 16);
				String cmdE02 = oemCode + ":" + this.getDeviceSN() + "|" + subtype
						+ " ";
				String repLed = "C R " + cmdE02 + this.getVerfyCode(cmdE02);
				this.setReplyByte(repLed.getBytes());
				String cmdId = this.getDeviceSN() + "|" + subtype + "|"
						+ indexHex.toLowerCase();
				String cmdP = this.getDeviceSN() + "|E04|"
						+ indexHex.toLowerCase();

				Log.getInstance().outLog(
						"确认LED中心应答：" + repLed + ",cmdId=" + cmdId + ",E04 ID="
								+ cmdP);

				String state = splitCont[5].split(",")[1];

				if (splitCont[5].equalsIgnoreCase("3,4,")) {// 娴滎喖瀹崇拋鍓х枂鎼存梻鐡�
					DBService service = new DBServiceImpl();
					service.updateInstructionsState(deviceId, "0", deviceId
							+ "|E06|");

					// service.updateInstructionsState(deviceId, "0", cmdP);

					Log.getInstance().outLog(
							this.getDeviceSN() + " LED亮度设置成功，指令为：" + content);

				} else if (state.equals("1")) {
					DBService service = new DBServiceImpl();
					try {
						service.updateInstructionsState(deviceId, "0", cmdId);

						service.updateInstructionsState(deviceId, "0", cmdP);
					} catch (Exception e) {
						Log.getInstance().errorLog("更新广告指令状态异常", e);
					}

					String cmd1 = oemCode + ":" + this.getDeviceSN() + "|"
							+ "E02" + " ";
					String repLed1 = "C R " + cmd + this.getVerfyCode(cmd);

					this.setReplyByte(repLed.getBytes());

					Log.getInstance().outLog(
							this.getDeviceSN() + " LED更新状态成功,短信INDEX=" + index
									+ ",指令为：" + content + ",更新标识E02:" + cmdId
									+ ",更新标识E04:" + cmdP);
				} else {// 娑撳﹦鏁哥悰銉ュ絺
					// DBService service = new DBServiceImpl();
					// ArrayList list = service.getLedInstructions(this
					// .getDeviceSN());
					// CommonGatewayServiceImpl gater = new
					// CommonGatewayServiceImpl();
					//
					// for (int i = 0; i < list.size(); i++) {
					// InstructionBean bean = (InstructionBean) list.get(i);
					// String ledcmd = bean.getInstuction();
					//
					// if (ledcmd != null) {
					// try {
					// gater.sendDataToTerminal(this.getDeviceSN(),
					// ledcmd.getBytes());
					//
					// Log.getInstance().outLog(
					// this.getDeviceSN() + " 娑撳﹦鏁哥悰銉ュ絺LED閹稿洣鎶�
					// + ledcmd);
					// } catch (Exception e) {
					// continue;
					// }
					// }
					// }

				}

			} else if (subtype.equalsIgnoreCase("E03")) {// 閸掔娀娅庢惔鏃傜摕
				// A X 7868:13600030075 041231184539 E02 3E8,1, 0809
				String indexHex = splitCont[5].split(",")[0].toLowerCase();
				long index = Long.parseLong(indexHex, 16);
				String cmdE03 = oemCode + ":" + this.getDeviceSN() + "|" + subtype
						+ " ";
				String repLed = "C R " + cmdE03 + this.getVerfyCode(cmdE03);

				this.setReplyByte(repLed.getBytes());
				String cmdId = this.getDeviceSN() + "|" + subtype + "|"
						+ indexHex;
				Log.getInstance().outLog(
						"LED中心应答：" + repLed + ",cmdId=" + cmdId);

				String cmdP = this.getDeviceSN() + "|E04|" + indexHex;
				String state = splitCont[5].split(",")[1];
				if (state.equals("1")) {
					DBService service = new DBServiceImpl();
					service.updateInstructionsState(deviceId, "0", cmdId);
					service.updateInstructionsState(deviceId, "0", cmdP);
					Log.getInstance().outLog(
							this.getDeviceSN() + " LED信息删除更新状态成功,短信INDEX="
									+ index + ",指令为：" + content);
				} else {
					DBService service = new DBServiceImpl();
					service.updateInstructionsState(deviceId, "2", cmdId);
					service.updateInstructionsState(deviceId, "2", cmdP);
					Log.getInstance().outLog(
							this.getDeviceSN() + " LED信息删除更新状态失败,短信INDEX="
									+ index + ",指令为：" + content);
				}

			}
		}}
		 
		 
	}
	
	private  String sendPicCmd(String packNo) {
		String bufaCmd = oemCode + ":" + this.getDeviceSN()
				+ "|302|1;" + packNo + " ";

		String bfrep = "C M " + Tools.getRandomString(4) + " " + bufaCmd
				+ Tools.getVerfyCode(bufaCmd.getBytes())+"\r\n";

		return bfrep;
	}

	// 解析位置信息
	private void parsePosition(String ps) {
		// 090205102340|08942A90|18F48828|80000000|0|0|A|00000000|00040004|
		// 090205120454|0894311A|18F48252|80000000|0|0|
		// 00000001,00000000|00040004,0000415F| 15A9
		try{
		String[] split = ps.split("\\|");
		String date = split[0].substring(0, 6);
		String dates = date.substring(4, 6) + date.substring(2, 4)
				+ date.substring(0, 2);// ddMMyy
		String times = split[0].substring(6);// hhmmss

		String format_date = Tools.conformtime(times, dates);// 登陆日期yyyy-MM-dd
		// HH:mm:ss
		// Date gpsdate = formatStrToDate(format_date, "yyyy-MM-dd HH:mm:ss");
		// Timestamp ts = null;
		// ts = new Timestamp(gpsdate.getTime());
		// this.setTimeStamp(ts);
		Timestamp ts = new Timestamp(new Date().getTime());
		 
		this.setTime(format_date);

		String y = fromMs2XY(this.removeZeroStr(split[1])); // 纬度
		this.setCoordY(y);
		String x = fromMs2XY(this.removeZeroStr(split[2])); // 经度
		this.setCoordX(x);
		if (!split[3].equals("80000000") && !split[3].equals("ffffffff")) {
			String h = "";
			try {
				h = Integer.parseInt(this.removeZeroStr(split[3]), 16) + "";// 高度，单位米
			} catch (Exception e) {
				e.printStackTrace();
				h = "0";
			}
			this.setAltitude(h);
		} else {
			this.setAltitude("0.0");
		}

		String v = Integer.parseInt(this.removeZeroStr(split[4]), 16) + "";// 方向,单位度
		this.setDirection(v);
		String s = Integer.parseInt(this.removeZeroStr(split[5]), 16) / 1000.0
				+ ""; // 速度，单位KM/H
		this.setSpeed(s);
		String alarmStaus = split[6];// 报警状态字
		String[] firstAlarm = alarmStaus.split(",");
		String alarmHex = firstAlarm[0];
		String alarm_type = null;

		try {
			alarm_type = this.parseAlarm(alarmHex);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}// this.parseAlarmStatus(alarmHex);
		if (firstAlarm.length > 1) {
			String extendAlarm = firstAlarm[1];

		}

		String oemStatus = split[7]; // 扩展状态字
		String[] oemSplit = oemStatus.split(",");
		String oem_type = oemSplit[0];
		String lineAlarm = null;
		try {
			lineAlarm = parseOemStatusNew(oem_type);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (oemSplit.length > 1) {
			String distance = Integer.parseInt(this.removeZeroStr(oemSplit[1]),
					16)
					+ ""; // 里程，单位KM
			this.setMileage(distance);
		}
		if (oemSplit.length > 2) {
			String tpra = oemSplit[2];
			String tempera = null;

			if (tpra.charAt(0) == 'F' || tpra.charAt(0) == 'f') {// 温度，单位0.01摄氏度

				int intTempera = Tools.getValueFromCompCode(tpra);
				tempera = intTempera * 0.01f + "";

			} else {// 温度，单位0.01摄氏度

				tempera = Integer.parseInt(this.removeZeroStr(tpra), 16)
						* 0.01f + "";
			} 
			log.info(this.getDeviceSN() + "当前温度值：" + tempera);
		}
		
		if (split.length > 9) {
			String od = split[9];
			Log.getInstance().outLog(this.getDeviceSN() + " 外设透传数据：" + od);
			String[] extSpt = od.split(";");
			 od = extSpt[2];
			if (od.startsWith("24")) {
				String lenHex = od.substring(2, 4);
				String zuNo = od.substring(4, 6);
				String num = od.substring(6, 18);
				num = new StringBuffer(num).reverse().toString();
				String verCodeHex1 = od.substring(18, 20);
				String state = od.substring(20, 22);
				String edate = od.substring(22, 34);
				edate = edate.substring(10, 12) + "-" + edate.substring(8, 10)
						+ "-" + edate.substring(6, 8) + " "
						+ edate.substring(4, 6) + ":" + edate.substring(2, 4)
						+ ":" + edate.substring(0, 2);
				String verCode = od.substring(32);
			 
				String stateDesc = null;
//				AA    解封下开
//				BB    解封下关
//				CA    施封下开
//				CB    施封下关
//				CC    短路报警
//				EA    施封操作
//				EB    解封操作
//				EC    解除报警

				if (state.equalsIgnoreCase("aa")){
					stateDesc = "解封下开";
				}else if (state.equalsIgnoreCase("BB")){
					stateDesc = " 解封下关";
				}else if (state.equalsIgnoreCase("CA")){
					stateDesc = "施封下开";
				}else if (state.equalsIgnoreCase("CB")){
					stateDesc = "施封下关";
				}else if (state.equalsIgnoreCase("CC")){
					stateDesc = "短路报警";
				}else if (state.equalsIgnoreCase("EA")){
					stateDesc = "施封操作";
				}else if (state.equalsIgnoreCase("EB")){
					stateDesc = " 解封操作";
				}else if (state.equalsIgnoreCase("EC")){
					stateDesc = "解除报警";
				} 
				
				Log.getInstance().outLog(
						this.getDeviceSN() + " 电子签封数据长度："
								+ Integer.parseInt(lenHex, 16) + ",状态：" + state+stateDesc
								+ ",类号：" + zuNo + ",签封号码：" + num + ",签封检验码："
								+ verCodeHex1 + ",数据校验：" + verCode);
				Log.getInstance().outLog(
						this.getDeviceSN() + " 电子签封时间：" + edate + ",GPS时间："
								+ this.getTime());

				// 签封信息入库
				saveEleSeals(zuNo,num,state,stateDesc);
				
			} else if (od.startsWith("25")) {
				// 链路维持信息
				Log.getInstance().outLog(this.getDeviceSN() + " 链路维持信息：" + od);
				String lenHex = od.substring(2, 4);
				String zuNo = od.substring(4, 6);
				String num = od.substring(6, 18);
				num = new StringBuffer(num).reverse().toString();
				String verCodeHex1 = od.substring(18, 20);
				String state = od.substring(20, 22);
				String edate = od.substring(22, 34);
				edate = edate.substring(10, 12) + "-" + edate.substring(8, 10)
						+ "-" + edate.substring(6, 8) + " "
						+ edate.substring(4, 6) + ":" + edate.substring(2, 4)
						+ ":" + edate.substring(0, 2);
				String verCode = od.substring(32);
				
			 
				String stateDesc = null;
//				AA    解封下开
//				BB    解封下关
//				CA    施封下开
//				CB    施封下关
//				CC    短路报警
//				EA    施封操作
//				EB    解封操作
//				EC    解除报警

				if (state.equalsIgnoreCase("aa")){
					stateDesc = "解封下开";
				}else if (state.equalsIgnoreCase("BB")){
					stateDesc = " 解封下关";
				}else if (state.equalsIgnoreCase("CA")){
					stateDesc = "施封下开";
				}else if (state.equalsIgnoreCase("CB")){
					stateDesc = "施封下关";
				}else if (state.equalsIgnoreCase("CC")){
					stateDesc = "短路报警";
				}else if (state.equalsIgnoreCase("EA")){
					stateDesc = "施封操作";
				}else if (state.equalsIgnoreCase("EB")){
					stateDesc = " 解封操作";
				}else if (state.equalsIgnoreCase("EC")){
					stateDesc = "解除报警";
				} 
				
				Log.getInstance().outLog(
						this.getDeviceSN() + " 电子签封数据长度："
								+ Integer.parseInt(lenHex, 16) + ",状态：" + state+stateDesc
								+ ",类号：" + zuNo + ",签封号码：" + num + ",签封检验码："
								+ verCodeHex1 + ",数据校验：" + verCode);
				Log.getInstance().outLog(
						this.getDeviceSN() + " 电子签封时间：" + edate + ",GPS时间："
								+ this.getTime());
				// 签封信息入库
				saveEleSeals(zuNo,num,state,stateDesc);
			}
		 
		}
		}catch(Exception e){
			e.printStackTrace();
			
		}
		this.setStatusRecord(this.statusRecord);

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

	// 把毫秒转换成经纬度
	private String fromMs2XY(String xy) {
		String ret = "";
		try {
			double ms = Integer.parseInt(xy, 16);
			double ds = ms / 1000 / 60 / 60;

			DecimalFormat format = new DecimalFormat("0.000000");
			format.setMaximumFractionDigits(6);
			ret = format.format(ds);
		} catch (Exception e) {
			ret = "0";
		}
		return ret;
	}

	private Date formatStrToDate(String date, String format) {
		Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			d = sdf.parse(date);
		} catch (ParseException ex) {
			Log.getInstance().errorLog(ex.getMessage(), ex);
			return null;
		}
		return d;
	}

	// 解析OEM 状态
	private String parseOemStatusNew(String hex) throws Exception {
		String ret = "";
		while (hex.length() < 8) {
			hex = "0" + hex;
		}
		String bit1 = "0" + String.valueOf(hex.charAt(hex.length() - 1));
		byte b = Tools.fromHexString(bit1)[0];
		DBService dbservice = new DBServiceImpl();
		if (this.getByteBit(b, 0) == 1) {
			this.statusRecord.setFullEmpty("1");
			log.info(this.getDeviceSN() + "重车！");
		}else{
			log.info(this.getDeviceSN() + "空车！");
			this.statusRecord.setFullEmpty("0");
		}
		if (this.getByteBit(b, 1) == 1) {
			log.info(this.getDeviceSN() + "罐车正反转！");
		}
		if (this.getByteBit(b, 2) == 1) {
			log.info(this.getDeviceSN() + "车辆点火！");
			this.statusRecord.setAcc("1");
		}else{
			log.info(this.getDeviceSN() + "车辆熄火！");
			 this.statusRecord.setAcc("0");
		}
		if (this.getByteBit(b, 3) == 1) {
			this.statusRecord.setFrontCarDoor("1");
			log.info(this.getDeviceSN() + "前门开！");
		}else{
			this.statusRecord.setFrontCarDoor("1");
			log.info(this.getDeviceSN() + "前门关！");
		}

		String bit2 = String.valueOf(hex.charAt(hex.length() - 2));
		byte b2 = Byte.parseByte(bit2, 16);
		if (this.getByteBit(b2, 0) == 1) {
			this.statusRecord.setRearCarDoor("1");
			log.info(this.getDeviceSN() + "后门开！");
		}else{
			this.statusRecord.setRearCarDoor("0");
			log.info(this.getDeviceSN() + "后门关！");
		}
		if (this.getByteBit(b2, 1) == 1) {
			this.setAlarmType("5"); 
			AlarmQueue.getInstance().addAlarm(this);
			log.info(this.getDeviceSN() + "发生了主电源破坏报警！");
		}
		if (this.getByteBit(b2, 2) == 1) {
			this.setAlarmType(AlarmType.GPS_MAST_OPEN_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			
			log.info(this.getDeviceSN() + "发生了GPS天线开路报警！");
		}
		if (this.getByteBit(b2, 3) == 1) {
			this.setAlarmType(AlarmType.GPS_MAST_SHORT_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(this.getDeviceSN() + "发生了GPS天线短路报警！");
		}

		String bit3 = String.valueOf(hex.charAt(hex.length() - 3));
		byte b3 = Byte.parseByte(bit3, 16);
		if (this.getByteBit(b3, 0) == 1) {
			this.setAlarmType("5"); 
			AlarmQueue.getInstance().addAlarm(this);
			log.info(this.getDeviceSN() + "发生了断电报警！");
		}
		if (this.getByteBit(b3, 1) == 1) {
			this.setAlarmType(AlarmType.LACK_PRESSURE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(this.getDeviceSN() + "发生了欠压报警！");
		}
		if (this.getByteBit(b3, 2) == 1) {

			this.setAlarmType(AlarmType.GPS_MODEL_FAIL_ALARM_TYPE);
			log.info(this.getDeviceSN() + "GPS接收机故障！");
			AlarmQueue.getInstance().addAlarm(this);
		}
		if (this.getByteBit(b3, 3) == 1) {
			this.setAlarmType(AlarmType.SUPER_WAVE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(this.getDeviceSN() + "发生了超声波报警！");
		}

		String bit4 = String.valueOf(hex.charAt(hex.length() - 4));
		byte b4 = Byte.parseByte(bit4, 16);
		if (this.getByteBit(b4, 0) == 1) {
			this.setAlarmType(AlarmType.DRAG_HANG_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(this.getDeviceSN() + "发生了拖吊报警！");
		}
		if (this.getByteBit(b4, 1) == 1) {
			this.setAlarmType(AlarmType.CLOSE_FIRE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			log.info(this.getDeviceSN() + "发生了熄火报警！");
		}
		if (this.getByteBit(b4, 2) == 1) {
			log.info(this.getDeviceSN() + "发生了偏航报警！");
			try { 
				this.setAlarmType("6");
				AlarmQueue.getInstance().addAlarm(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.getInstance().errorLog("保存偏航报警异常", e);
			}
		}
		if (this.getByteBit(b4, 3) == 1) {

		}

		return ret;

	}

	// 解析报警状态字
	private String parseAlarm(String hex) throws Exception {
		String ret = "";
		while (hex.length() < 8) {
			hex = "0" + hex;
		}
		String bit1 = "0" + String.valueOf(hex.charAt(hex.length() - 1));
		byte b = Tools.fromHexString(bit1)[0];
		DBService dbService = new DBServiceImpl();

		if (this.getByteBit(b, 0) == 1) {
			log.info(this.getDeviceSN() + "发生了主动报警！");
			try { 
				this.setAlarmType("3");
				AlarmQueue.getInstance().addAlarm(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.getInstance().errorLog("保存主动报警异常", e);
			}
		}
		if (this.getByteBit(b, 1) == 1) {
			log.info(this.getDeviceSN() + "发生了超速报警！");
			try { 
				this.setAlarmType("1");
				AlarmQueue.getInstance().addAlarm(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.getInstance().errorLog("保存超速报警异常", e);
			}

		}
		if (this.getByteBit(b, 2) == 1) {
			log.info(this.getDeviceSN() + "发生了区域报警！");
			try { 
				this.setAlarmType("2");
				AlarmQueue.getInstance().addAlarm(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.getInstance().errorLog("保存区域报警异常", e);
			}
		}
		if (this.getByteBit(b, 3) == 1) {
			log.info(this.getDeviceSN() + "发生了设备故障报警！");
		}

		String bit2 = String.valueOf(hex.charAt(hex.length() - 2));
		byte b2 = Byte.parseByte(bit2, 16);
		if (this.getByteBit(b2, 0) == 1) {
			log.info(this.getDeviceSN() + "发生了求助报警！");
			this.setAlarmType(AlarmType.HELP_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		} else if (this.getByteBit(b2, 1) == 1) {

		} else if (this.getByteBit(b2, 2) == 1) {

		} else if (this.getByteBit(b2, 3) == 1) {

		}

		String bit3 = String.valueOf(hex.charAt(hex.length() - 3));
		byte b3 = Byte.parseByte(bit3, 16);
		if (this.getByteBit(b3, 0) == 1) {

		} else if (this.getByteBit(b3, 1) == 1) {

		} else if (this.getByteBit(b3, 2) == 1) {

		} else if (this.getByteBit(b3, 3) == 1) {

		}

		String bit4 = String.valueOf(hex.charAt(hex.length() - 4));
		byte b4 = Byte.parseByte(bit4, 16);
		if (this.getByteBit(b4, 0) == 1) {

		} else if (this.getByteBit(b4, 1) == 1) {

		} else if (this.getByteBit(b4, 2) == 1) {

		} else if (this.getByteBit(b4, 3) == 1) {

		}

		String bit5 = String.valueOf(hex.charAt(hex.length() - 5));
		byte b5 = Byte.parseByte(bit5, 16);
		if (this.getByteBit(b5, 0) == 1) {

		} else if (this.getByteBit(b5, 1) == 1) {

		} else if (this.getByteBit(b5, 2) == 1) {

		} else if (this.getByteBit(b5, 3) == 1) {

		}

		String bit6 = String.valueOf(hex.charAt(hex.length() - 6));
		byte b6 = Byte.parseByte(bit6, 16);
		if (this.getByteBit(b6, 0) == 1) {

		} else if (this.getByteBit(b6, 1) == 1) {

		} else if (this.getByteBit(b6, 2) == 1) {

		} else if (this.getByteBit(b6, 3) == 1) {

		}

		String bit7 = String.valueOf(hex.charAt(hex.length() - 7));
		byte b7 = Byte.parseByte(bit7, 16);
		if (this.getByteBit(b7, 0) == 1) {
			// log.info(this.getDeviceSN() + "发生了疲劳驾驶！");
		} else if (this.getByteBit(b7, 1) == 1) {
			log.info(this.getDeviceSN() + "发生了入界报警！");
			try { 
				this.setAlarmType("2");
				AlarmQueue.getInstance().addAlarm(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.getInstance().errorLog("保存入区域报警异常", e);
			}
		} else if (this.getByteBit(b7, 2) == 1) {
			log.info(this.getDeviceSN() + "发生了出界报警！");
			try { 
				this.setAlarmType("2");
				AlarmQueue.getInstance().addAlarm(this);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.getInstance().errorLog("保存出区域报警异常", e);
			}
		} else if (this.getByteBit(b7, 3) == 1) {
			this.setAlarmType("11");
			AlarmQueue.getInstance().addAlarm(this);
			Log.getInstance().outLog(this.getDeviceSN() + "发生了温度报警！");
		}

		String bit8 = String.valueOf(hex.charAt(hex.length() - 8));
		byte b8 = Byte.parseByte(bit8, 16);
		if (this.getByteBit(b8, 0) == 1) {
			// log.info(this.getDeviceSN() + "发生了碰撞报警！");
		} else if (this.getByteBit(b8, 1) == 1) {
			// log.info(this.getDeviceSN() + "发生了邮箱开盖！");
		} else if (this.getByteBit(b8, 2) == 1) {

		} else if (this.getByteBit(b8, 3) == 1) {
			Log.getInstance().outLog(this.getDeviceSN() + " 未定位状态！");
			this.statusRecord.setLocate("0");
		}

		return ret;
	}

	public static int getByteBit(byte data, double pos) {
		int bitData = 0;

		byte compare = (byte) Math.pow(2.0, pos);

		byte b = (byte) (data & compare);
		if ((data & compare) == compare) {
			bitData = 1;
		}
		return bitData;
	}

 
	 

	// 求校验和，并转换为16进制
	private String getVerfyCode(String cont) {
		String ret = "";
		byte[] br = cont.getBytes();
		int sum = 0;
		for (int i = 0; i < br.length; i++) {
			sum += br[i] & 0xFF;
		}
		ret = Integer.toHexString(sum) + "\r\n";

		return ret;
	}

	/**
	 * 设置终端OEM码
	 * 
	 * @param code
	 */
	private synchronized boolean setTerminalOemCode(String code, String deviceId) {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean flag = false; 
		String sql = "update t_terminal set oem_code=? where device_id=?";
 		try {
			conn = DbOperation.getConnection();
			conn.setAutoCommit(false);
			pst = conn.prepareCall(sql);
			pst.setString(1, code);
			pst.setString(2, deviceId);
			pst.executeUpdate();
			conn.commit();
			conn.setAutoCommit(true);
			flag = true;
		} catch (SQLException e) {
			log.info("设置终端OEM码异常：" + e.getMessage());
			e.printStackTrace();

			flag = false;
		} finally {
			DbOperation.release(null, null, pst, null, conn);
		}
		return flag;

	}
	
	public String timeInter(String freq, String oemCode) {
		String head = "C M ";
		String cmdSeq = Tools.getRandomString(4);

		String ret = "";
		String time = "";

		String interval = freq;
		int tt = Integer.parseInt(interval);
		time = Integer.toHexString(tt).toUpperCase();
		head = head + cmdSeq + " ";

		String cmd = oemCode + ":" + this.getDeviceSN() + "|5|0;" + time + "!"
				+ time + "; ";
		String vcode = this.getVerfyCode(cmd);
		ret = head + cmd + vcode;

		return ret;

	}
	public boolean isAdvValidate(String cmd) {
		boolean flag = true;
		String advDate = "";
		String bdate = "";
		String edate = "";

		String[] splits = cmd.split(" ");
		String props = splits[3];

		int hflag = props.indexOf(";H");
		int iflag = props.indexOf(";I");
		Date curDate = new Date();

		if (hflag != -1) {
			advDate = props.substring(hflag + 2, hflag + 2 + 17);
		}
		if (iflag != -1) {
			advDate = props.substring(iflag + 2, iflag + 2 + 17);
		}
		Log.getInstance().outLog(
				this.getDeviceSN() + "待补发指令，有效时间：" + advDate + ",当前时间："
						+ Tools.formatDate2Str(curDate, "yyyyMMdd"));
		bdate = advDate.substring(0, 8);
		edate = advDate.substring(9);
		Date lastDate = Tools.formatStrToDate(edate, "yyyyMMdd");

		if (lastDate.compareTo(curDate) < 0) {
			flag = false;
			Log.getInstance().outLog(this.getDeviceSN() + " 广告过期无效：" + cmd);
		}
		return flag;
	}
	public String getFreByDeviceId(String deviceId) {
		Connection con = DbUtil.getConnection();
		Statement stm = null;
		String sql = "select t.freq_value from t_frequency t where t.device_id='"+deviceId+"'";
		ResultSet rs = null;
		String fre="";

		try {
			stm = con.createStatement();
			rs = stm.executeQuery(sql);			
		    if (rs.next())
			fre=rs.getString("freq_value");							

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbOperation.release(stm, rs, null, null, con);
		}
		return fre;		
	}
	
	public String setLedDate(String seq, String omeCode, String yyyyMMdd,
			String HHmmss) {
		String ret = "";
		String cmd = "";

		String head = "C M " + seq + " ";

		String date = yyyyMMdd + ";" + HHmmss;

		cmd = omeCode + ":" + this.getDeviceSN() + "|E05|" + date + " ";

		String vcode = this.getVerfyCode(cmd);
		ret = head + cmd + vcode;
		return ret;
	}
	private  boolean saveEleSeals(String catalog,String seals_num,String state,String stateDesc) {
		Connection conn = null;
		PreparedStatement pst = null;
		boolean flag = false;
	 String sql = "insert into t_ele_seals(id,catalog,seals_num,state,latitude,longitude,speed,direction,height,distance,satllite_num,device_id,gpstime,coord_type,obj_id,obj_type,state_desc) ";
	 sql += " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		try {
			conn = DbUtil.getConnection();

			pst = conn.prepareStatement(sql);
			pst.setString(1, new UUID().toString());
			pst.setString(2, catalog);
			pst.setString(3, seals_num);
			pst.setString(4, state);
			pst.setFloat(5, Float.parseFloat(this.getCoordY()== null ? "0"
					: this.getCoordY()));
			pst.setFloat(6, Float.parseFloat(this.getCoordX()== null ? "0"
					: this.getCoordX()));
			pst.setFloat(7, Float.parseFloat(this.getSpeed()== null ? "0"
					: this.getSpeed()));
			pst.setFloat(8, Float.parseFloat(this.getDirection()== null ? "0"
					: this.getDirection()));
			pst.setFloat(9, Float.parseFloat(this.getAltitude()== null ? "0"
					: this.getAltitude()));
			pst.setFloat(10, Float.parseFloat(this.getMileage()== null ? "0"
					: this.getMileage()));
			pst.setFloat(11, Float.parseFloat(this.getSatellites()== null ? "0"
					: this.getSatellites()));
			pst.setString(12, this.getDeviceSN());
			pst.setTimestamp(13, new Timestamp(Tools.formatStrToDate(this.getTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
			pst.setInt(14, this.getCoordType());
			pst.setString(15, null);
			pst.setString(16, null);
			pst.setString(17, stateDesc);
			pst.execute();
			conn.commit();
			flag = true;
		} catch (SQLException e) { 
			Log.getInstance().errorLog("保存电子铅封信息异常", e);
			e.printStackTrace();
			
			flag = false;
		} finally {
 
			DbOperation.release(null, null, pst, null, conn);
		}
		return flag;

	}

}
