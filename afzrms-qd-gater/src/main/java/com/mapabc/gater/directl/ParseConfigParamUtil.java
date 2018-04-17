/**
 * 
 */
package com.mapabc.gater.directl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.bean.status.AbstractTTermStatusRecord;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.jms.JmsProtocal;
import com.mapabc.gater.jms.service.JmsTransmitService;
import com.mapabc.gater.lbsgateway.GBLTerminalList;
import com.mapabc.gater.lbsgateway.GprsTcpThreadList;
import com.mapabc.gater.lbsgateway.GprsUdpThreadList;
import com.mapabc.gater.lbsgateway.TerminalTypeList;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.lbsgateway.bean.TerminalTCPAddress;
import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;
import com.mapabc.gater.lbsgateway.bean.TerminalUDPAddress;
import com.mapabc.gater.lbsgateway.poolsave.BatchSaveUtil;
import com.mapabc.gater.lbsgateway.poolsave.GpsData;

//import com.mapabc.geom.CoordCvtAPI;
//import com.mapabc.geom.DPoint;

/**
 * @author 
 * 
 */
public class ParseConfigParamUtil {

	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(ParseConfigParamUtil.class);

	private final static float maxX = 135.041666F;
	private final static float minX = 73.666666f;
	private final static float maxY = 53.55f;
	private final static float minY = 3.866666F;

	private ParseConfigParamUtil() {

	}

	public static GpsData handleConfig(ParseBase pb) throws Exception {
		TerminalTypeBean typeBean = getTerminalType(pb.getDeviceSN());

		if (typeBean != null && !typeBean.getId().equals(Const.PROTOCAL_TYPE_DEFAULT)) {
			if (log.isDebugEnabled()) {
				log.debug(pb.getDeviceSN() + " 设备类型码：" + typeBean.getId());
			}
			TTerminal terminal = GBLTerminalList.getInstance().getTerminaInfo(
					pb.getDeviceSN());

			if (terminal != null) {
				// 设备停用，上传的数据进行后续处理，主动关闭设备的链接
				if (log.isDebugEnabled()) {
					log.debug(terminal.getDeviceId() + " 使用状态:"
							+ terminal.getUsageFlag());
				}
				long useflag = terminal.getUsageFlag();
				if (useflag != 1) {
					if (log.isDebugEnabled()) {
						log.debug(terminal.getDeviceId() + " 已处于停用状态,停用原因："
								+ terminal.getUsageFlag());
					}
					TerminalTCPAddress gprs = GprsTcpThreadList.getInstance()
							.getGpsTcpThreadBySim(pb.getDeviceSN());
					if (gprs != null) {
						GprsTcpThreadList.getInstance().remove2(
								pb.getDeviceSN());
					}
					TerminalUDPAddress udp = GprsUdpThreadList.getInstance()
							.getGpsThreadBySim(pb.getDeviceSN());
					if (udp != null) {
						GprsUdpThreadList.getInstance().removeUDP(
								pb.getDeviceSN());
					}

					return null;
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug(

					"系统中没有适配到指定的终端：device_id=" + pb.getDeviceSN());
				}
 				return null;
			}
		}

		return handleConfig(typeBean, pb);
	}

	/**
	 * 终端配置实现
	 * 
	 * @param pb
	 *            by 
	 */
	public static GpsData handleConfig(TerminalTypeBean typeBean, ParseBase pb) {

		AbstractTTermStatusRecord status = pb.getStatusRecord();

		double x = Double.parseDouble(pb.getCoordX() == null ? "0" : pb
				.getCoordX());
		double y = Double.parseDouble(pb.getCoordY() == null ? "0" : pb
				.getCoordY());
		double d = Double.parseDouble(pb.getDirection() == null ? "0" : pb
				.getDirection());
		double s = Double.parseDouble(pb.getSpeed() == null ? "0" : pb
				.getSpeed());

		if (pb.getLocateType() != null /** && pb.getLocateType().equals("1") */
		) {
			if (typeBean == null) {
				log.debug(pb.getDeviceSN() + " TerminalTypeBean is null！");
				return null;
			}
		}

		GpsData gpsdata = BatchSaveUtil.getGpsDataFromParseBase(pb);
		if (gpsdata == null) {
			return null;
		}
		String coordClassName = typeBean.getLbmsInterfaceImpl();
		/* 默认原始坐标 */
		String coordType = (typeBean.getCoordType() == null || typeBean
				.getCoordType().trim() == "") ? "0" : typeBean.getCoordType();
		if (pb.getCoordType() != -1) {
			coordType = pb.getCoordType() + "";
			pb.setCoordType(Integer.parseInt(coordType));
		}

		String locateType = pb.getLocateType();
		try {
			if (coordClassName == null || coordClassName.trim().length() == 0) {
				if (log.isDebugEnabled()) {
					log.debug("terminallist.xml 中终端类型" + typeBean.getId()
							+ " 未设置坐标服务实现类。");
				}
				// return null;
			}
			if (coordClassName != null && coordClassName.trim().length() > 0
					&& x != 0 && y != 0) {

				if (typeBean.isEncrypt()) {
					// TODO
//					DPoint point = getCoordConvert(coordClassName, "0", x, y,
//							d, coordType);
//					if (point != null) {
//						gpsdata.setEncX(point.getEncryptX());
//						gpsdata.setEncY(point.getEncryptY());
//					}
				}

				if (typeBean.isDeflection()) {// 偏转
					log.debug(pb.getDeviceSN() + " is need req  Deflection:"
							+ typeBean.isDeflection());
					// TODO
//					DPoint point = getCoordConvert(coordClassName, "1", x, y,
//							d, coordType);
//					if (point != null) {
//						gpsdata.setX((float) point.getX());
//						gpsdata.setY((float) point.getY());
//						pb.setCoordX(point.getX() + "");
//						pb.setCoordY(point.getY() + "");
//						gpsdata.setCoordType(1);
//						pb.setCoordType(1);
//						coordType = "1";
//						x = point.getX();
//						y = point.getY();
//					}
				}

				if (typeBean.isRouteCorret()) {// 道路纠偏
					log.debug(pb.getDeviceSN() + " is need req  RouteCorret:"
							+ typeBean.isRouteCorret());

					if (s > 5) {
						// TODO
//						DPoint point = getCoordConvert(coordClassName, "2", x,
//								y, d, coordType);
//						if (point != null) {
//							gpsdata.setX((float) point.getX());
//							gpsdata.setY((float) point.getY());
//
//							pb.setCoordX(point.getX() + "");
//							pb.setCoordY(point.getY() + "");
//
//							pb.setCoordType(2);
//							gpsdata.setCoordType(2);
//							coordType = "2";
//
//							x = point.getX();
//							y = point.getY();
//						}
					} else {
						if (log.isDebugEnabled()) {
							log.debug(pb.getDeviceSN() + " 速度小于5,不抓路只偏转, x:"
									+ x + " ,y:" + y + " ,s:" + s);
						}
						// TODO
//						DPoint point = getCoordConvert(coordClassName, "1", x,
//								y, d, coordType);
//						if (point != null) {
//							gpsdata.setX((float) point.getX());
//							gpsdata.setY((float) point.getY());
//							pb.setCoordX(point.getX() + "");
//							pb.setCoordY(point.getY() + "");
//
//							pb.setCoordType(1);
//							gpsdata.setCoordType(1);
//							coordType = "1";
//							x = point.getX();
//							y = point.getY();
//						}
					}
				}

				if (typeBean.isLocateDesc()) {// 逆地理编码
					log.debug(pb.getDeviceSN() + " is need req posDes:"
							+ typeBean.isLocateDesc());

					String address = null;

					// TODO
					// 缓存位置描述
//					GPSLocateInfoFiler filler = GPSLocateInfoFiler
//							.getInstance();

//					if (filler.IsNeedLocateInfo(pb.getDeviceSN(),
//							ReverseGisCode.getInstance().GetTimeInterval())) {// 到了间隔时间则请求
//						try {
//							address = getLocateDesc(coordClassName, x, y,
//									coordType);
//						} catch (Exception e) {
//							address = "";
//							log.error("逆地理编码异常", e);
//						}
//
//						filler.SetLocInfo(pb.getDeviceSN(), address);
//						if (log.isDebugEnabled())
//							log.debug("获取新位置描述：" + address);
//					} else {// 从缓存中读取
//						address = filler.GetLocInfo(pb.getDeviceSN());
//						if (log.isDebugEnabled())
//							log
//									.debug(pb.getDeviceSN() + "从缓存获取位置描述："
//											+ address);
//					}

					pb.setAddress(address);
					gpsdata.setAdress(address);
				}

				if (typeBean.isEncryptCvt()) {// 偏转加密
					// TODO
//					DPoint point = getCoordConvert(coordClassName, "3", x, y,
//							d, coordType);
//					if (point != null) {
//						// gpsdata.setEncryptConvertPoint(point);
//						gpsdata.setJmx(point.getEncryptX());
//						gpsdata.setJmy(point.getEncryptY());
//					}
				}
			}
		} catch (Exception e) {
			log.error("坐标转换异常", e);
		}

		try {
			String locStatus = pb.getLocateStatus();// 定位状态
			gpsdata.setStatus(locStatus);
			if (typeBean.isSendJms()) {
				log.debug(pb.getDeviceSN() + " is need send to jms:"
						+ typeBean.isSendJms());

				// 需要分发到JMS
				Date gpstime = Tools.formatStrToDate(pb.getTime(),
						"yyyy-MM-dd HH:mm:ss");
				Calendar gpsCal = Calendar.getInstance();
				gpsCal.setTime(gpstime);

				Calendar curCal = Calendar.getInstance();
				curCal.add(Calendar.MINUTE, 20);// 当前时间加20分钟

				int com1 = curCal.compareTo(gpsCal); // GPS时间比当前时间小于X分钟

				curCal.add(Calendar.MINUTE, -40); // GPS时间比当前时间大于X分钟
				int com2 = curCal.compareTo(gpsCal);

				// if (com1 < 0 || com2 > 0) {
				// //
				// 信号弱时GPS卫星时间可能为非正常日期，如2090，2080，2001等很大或很小的日期,用系统时间补偿可能会出现的现象：由于GPS时间与系统时间不一定同步，这样查询轨迹的时候可能会出现往前走走，往后退退
				// if (pb.getLocateType() != null
				// && !pb.getLocateType().equals("0")) {// 针对GPS终端
				// log.debug(
				// pb.getDeviceSN()
				// + "上报时间："
				// + pb.getTime()
				// + ",系统时间："
				// + Tools.formatDate2Str(new Date(),
				// "yyyy-MM-dd HH:mm:ss")
				// + ",时间偏差大当成错误数据，不转发到JMS.");
				//
				// }
				//
				// } else {
				dealJmsInfo(typeBean, pb, gpsdata);
				// }
			}

		} catch (Exception e) {
			log.error("转发到JMS异常", e);
		}

		if (typeBean.isSaveDb()) {// 需要入库，则放入数据库池
			log.debug(pb.getDeviceSN() + " is need save to db:"
					+ typeBean.isSaveDb());
			if(pb.getAlarmType()!=null && !pb.getAlarmType().equals("")){
				log.debug("TODO........alarm single save to db use alarm queue");
				return null;
			}
			gpsdata.setPost(true);
		}
		return gpsdata;
	}

	// 处理JMS转发
	public static void dealJmsInfo(TerminalTypeBean typeBean, ParseBase pb,
			GpsData gpsdata) throws Exception {

		ArrayList jmsList = pb.getJmsInfoList();

		HashMap<String, String> configMap = AllConfigCache.getInstance()
				.getConfigMap();

		String isBatch = null;

		isBatch = typeBean.getIsBatchToJms();

		String isSendPro = null;
		try {
			isSendPro = configMap.get("isSend2Proxy");
		} catch (Exception e) {
			log.error("获取配置isBatchSend异常", e);
		}

		String subject = typeBean.getTopicName();// configMap.get("topicName");
		String jmsImpl = typeBean.getJmsInterfaceImpl();
		JmsProtocal jmsPtl = new JmsProtocal();

		// if (null == isBatch || !Boolean.valueOf(isBatch)) {// 默认单次发送
		long startTime = System.currentTimeMillis();
		log.debug("jms size:" + jmsList.size());

		if (jmsList != null && jmsList.size() > 0) {
			for (int k = 0; k < jmsList.size(); k++) {

				String type = (String) jmsList.get(k);
				String content = "";
				try {
					if (type.equals("LOC")) {
						content += jmsPtl.makeJMSInfo(pb);

					} else if (type.startsWith("ALARM")) {
						content += jmsPtl.makeAlarmJMSInfo(pb);
					} else if (type.startsWith("DISPATCH")) {
						content += jmsPtl.makeDispatchJMSInfo(pb);
					} else if (type.startsWith("CTRL")) {
						content += jmsPtl.makeCtrlResJMSInfo(pb);
					} else if (type.startsWith("STATUS")) {
						content += jmsPtl.makeStatusResJMSInfo(pb);
					}
					// JMS转发
					if (jmsImpl != null && jmsImpl.trim().length() > 0
							&& subject != null && subject.trim().length() > 0) {
						transToJms(jmsImpl, subject, content);
					}
					// 代理转发
					if (isSendPro != null && isSendPro.equals("1")) {
						// TODO
//						ProxyPushService sender = new ProxyPushService(type);
//						transToProxy(sender, pb, content);
					}

				} catch (Exception e) {
				}

			}
			pb.setJmsInfoList(new ArrayList());
		} else {// 默认
			String content = "LOC,1,";
			content += jmsPtl.makeJMSInfo(pb);
			// transToJms(jmsImpl, subject, content);

			if (isSendPro != null && isSendPro.equals("1")) {
				// TODO
//				ProxyPushService sender = new ProxyPushService("LOC");
//				transToProxy(sender, pb, content);
			}
		}

		// } else if (Boolean.valueOf(isBatch)) {// 批量发送
		// if (jmsImpl != null && jmsImpl.trim().length() > 0
		// && subject != null && subject.trim().length() > 0) {
		// transMoreToJms(jmsImpl, subject, jmsList);
		// pb.setJmsInfoList(new ArrayList());
		// }
		// }

	}

	// private static void transToProxy(InfoSender sender, ParseBase pb,
	// String content) {
	// String msg = "";
	// if (null != content) {
	// msg = "$WZTREQ," + content.trim() + "#";
	// }
	//
	// GpsData tmpgpsdata = BatchSaveUtil.getGpsDataFromParseBase(pb);
	// String deviceId = tmpgpsdata.getDEVICE_ID();
	// TTerminal terminal = GBLTerminalList.getInstance().getTerminaInfo(
	// deviceId);
	// String eid = "";
	// String gid = "";
	// if (null != terminal) {
	// eid = terminal.getEntCode();
	// gid = terminal.getGid() + "";
	// } else {
	// log.debug(
	// "Send msg to proxy fail, can not find the device : "
	// + deviceId);
	// }
	//
	// try {
	// sender.send(deviceId, eid, gid, msg);
	// log.debug("Send msg to proxy success, msg is " + msg);
	// } catch (JMSException e1) {
	// e1.printStackTrace();
	// log.error("代理异常", e1);
	// }
	// }

	/**
	 * 批次发送信息到JMS
	 * 
	 * @param className
	 * @param subject
	 * @param content
	 *            by 
	 */
	private static void transMoreToJms(String className, String subject,
			ArrayList<String> contList) throws Exception {

		JmsTransmitService jmsService = null;
		jmsService = (JmsTransmitService) getClassInstance(className);

		boolean flag = jmsService.sendMoreToJms(subject, contList);
		if (flag) {
			log.debug("发送到JMS成功,topicname:" + subject + ",发送条数："
					+ contList.size() + "," + contList.toString());
		} else {
			log.debug("发送到JMS失败,topicname:" + subject + ",内容："
					+ contList.size() + "，" + contList.toString());
		}

	}
	
//	private static void transToProxy(ProxyPushService sender, ParseBase pb,
//			String content) {
//		String msg = "";
//		if (null != content) {
//			msg = "$WZTREQ," + content + "#";
//		}
//
//		GpsData tmpgpsdata = BatchSaveUtil.getGpsDataFromParseBase(pb);
//		String deviceId = tmpgpsdata.getDEVICE_ID();
//		TTerminal terminal = GBLTerminalList.getInstance().getTerminaInfo(
//				deviceId);
//		String eid = "";
//		String gid = "";
//		if (null != terminal) {
//			eid = terminal.getEntCode();
//			gid = terminal.getGid() + "";
//		} else {
//			log.debug("Send msg to proxy fail, can not find the device : "
//					+ deviceId);
//		}
//
//		sender.receive(deviceId, eid, gid, msg);
//		log.debug("转发信息到push服务成功, deviceId:" + deviceId + ", eid:" + eid
//				+ ", gid:" + gid + ", msg is " + msg);
//	}

	public static TerminalTypeBean getTerminalType(String deviceid) {
		TTerminal term = null;
		String ttype = null;

		if (deviceid == null) {
			return null;
		}
		term = GBLTerminalList.getInstance().getTerminaInfo(deviceid);
		if (term == null) {
			log.debug("内存及数据库终端列表中无" + deviceid + "的终端信息");
			term = GBLTerminalList.getInstance().reloadTerminalById(deviceid);
			if (term == null) {
				TTerminal defaultterm = new TTerminal();
				defaultterm.setDeviceId(deviceid);
				defaultterm.setUsageFlag(1L);
				defaultterm.setTEntTermtype("GP-PND-GPRS");
				GBLTerminalList.getInstance().getTermMap().put(deviceid,
						defaultterm);
				log.debug("使用默认类型GP-PND-GPRS");
				return (TerminalTypeBean) TerminalTypeList.getInstance().get(
						"GP-PND-GPRS");
			}
			return null;
		}

		ttype = term.getTEntTermtype();

		log.debug("memory term type:" + ttype);

		TerminalTypeBean typeBean = null;
		if (ttype != null) {
			typeBean = (TerminalTypeBean) TerminalTypeList.getInstance().get(
					ttype);
			if (typeBean == null) {
				log.debug("\r\n error:<Terminal id=" + ttype
						+ "> is not exist in " + "terminallist.xml \r\n");
				// GBLTerminalList.getInstance().removeTerminal(deviceid);
			}
		} else {
			// GBLTerminalList.getInstance().removeTerminal(deviceid);
			term = GBLTerminalList.getInstance().reloadTerminalById(deviceid);
			if (term != null) {
				typeBean = (TerminalTypeBean) TerminalTypeList.getInstance()
						.get(term.getTEntTermtype());
			} else {
				typeBean = (TerminalTypeBean) TerminalTypeList.getInstance()
						.get(Const.PROTOCAL_TYPE_DEFAULT);
			}
		}

		return typeBean;
	}

	/**
	 * 发送信息到JMS
	 * 
	 * @param className
	 * @param subject
	 * @param content
	 *            by 
	 */
	public static void transToJms(String className, String subject,
			String content) throws Exception {

		JmsTransmitService jmsService = null;
		jmsService = (JmsTransmitService) getClassInstance(className);

		boolean flag = jmsService.sendSingleToJms(subject, content);
		if (flag) {
			log.debug("发送到JMS成功,topicname:" + subject + ",内容：" + content);
		} else {
			log.debug("发送到JMS成功,topicname:" + subject + ",内容：" + content);
		}

	}

	public static Object getClassInstance(String className) {
		if (className == null || className.trim().length() == 0) {
			log.debug("className is null.");
			return null;
		}
		Class objClass = null;
		Object obj = null;
		try {
			objClass = Class.forName(className);
			obj = objClass.newInstance();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage(), e);
			e.printStackTrace();
		}
		return obj;

	}

	/**
	 * 获取坐标转换点
	 * 
	 * @param className：坐标转换接口类
	 * @param type：0加密
	 *            1偏转 2道理纠偏 3偏转加密
	 * @param x
	 * @param y
	 * @param d
	 * @return by 
	 */
//	public static DPoint getCoordConvert(String className, String type,
//			double x, double y, double d, String coordType) {
//		CoordinateConvertService coordService = null;
//		DPoint point = null;
//		if (x == 0 || y == 0) {
//			return null;
//		}
//		try {
//			coordService = (CoordinateConvertService) getClassInstance(className);
//			if (type.equals("0")) {
//				point = coordService.getEncryptPoint(x, y, coordType);
//			} else if (type.equals("1")) {
//				point = coordService.getDeflectionPoint(x, y, coordType);
//			} else if (type.equals("2")) {
//				point = coordService.getRouteCorrectPoint(x, y, d, coordType);
//			} else if (type.equals("3")) {
//				point = coordService.getDeflectEncrypt(x, y);
//			}
//		} catch (Exception e) {
//			log.error("坐标转换异常", e);
//			point = null;
//		}
//
//		return point;
//
//	}

//	public static String getLocateDesc(String className, double x, double y,
//			String coordType) {
//		CoordinateConvertService coordService = null;
//		DPoint point = null;
//
//		String posdesc = null;
//		try {
//			coordService = (CoordinateConvertService) getClassInstance(className);
//			if (x != 0 && y != 0 && coordService != null) {
//				posdesc = coordService.getPositionDesc(x, y, coordType);
//			}
//
//		} catch (Exception e) {
//			log.error("请求位置描述异常", e);
//			posdesc = null;
//		}
//
//		return posdesc;
//
//	}

	public static void main(String[] args) {
		System.out.print(Boolean.getBoolean("1"));
	}

}
