package com.mapabc.gater.lbsgateway.poolsave;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.LogFactory;

import com.eaio.uuid.UUID;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.AbstractTTermStatusRecord;
import com.mapabc.gater.directl.bean.status.AbstractTermExtendStatus;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.lbsgateway.GBLTerminalList;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.lbsgateway.service.ext.ExtStatusConfig;
import com.mapabc.gater.lbsgateway.service.ext.LoadExtStatusConfig;
import com.mapabc.gater.lbsgateway.service.ext.Property;
import com.mapabc.gater.util.TimeUtil;

public class BatchSaveUtil {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(BatchSaveUtil.class);

	public static void addBatchLocreocord(PreparedStatement pstmt, GpsData tmp)
			throws SQLException {
		if (tmp.getX() == 0 || tmp.getY() == 0)
			return;

		pstmt.setString(1, tmp.getDEVICE_ID());
		pstmt.setFloat(2, tmp.getX());
		pstmt.setFloat(3, tmp.getY());
		pstmt.setFloat(4, tmp.getS());
		pstmt.setFloat(5, tmp.getH());
		pstmt.setFloat(6, tmp.getV());
		pstmt.setFloat(7, tmp.getMileage());
		pstmt.setTimestamp(8, tmp.getGpsTime());// 保存时间和日期
		pstmt.setString(9, tmp.getLocateType());// 类型：GPS
		pstmt.setInt(10, tmp.getCoordType());// 坐标类型
		pstmt.setString(11, tmp.getStatus());// 定位状态

		pstmt.setString(12, tmp.getUuid());
		pstmt.setString(13, tmp.getAdress());
		pstmt.setString(14, tmp.getObjType());
		pstmt.setString(15, tmp.getObjId());

		pstmt.addBatch();

	}

	public static void addBatchStatus(PreparedStatement pstmt,
			AbstractTTermStatusRecord tmp) throws SQLException {
		pstmt.setString(1, new UUID().toString());
		pstmt.setString(2, tmp.getDeviceId());
		pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
		pstmt.setString(4, tmp.getLocate());
		pstmt.setString(5, tmp.getAcc());
		pstmt.setString(6, tmp.getFullEmpty());
		pstmt.setString(7, tmp.getTank());
		pstmt.setString(8, tmp.getBackCar());
		pstmt.setString(9, tmp.getBrake());
		pstmt.setString(10, tmp.getCarDoor());
		pstmt.setString(11, tmp.getFrontCarDoor());

		pstmt.setString(12, tmp.getRearCarDoor());
		pstmt.setString(13, tmp.getFortification());
		pstmt.setString(14, tmp.getAntenna());
		pstmt.setString(15, tmp.getMainPower());
		pstmt.setString(16, tmp.getOilElec());
		pstmt.setString(17, tmp.getGpsModule());
		pstmt.setString(18, tmp.getGsmModule());
		pstmt.setString(19, tmp.getBackupBattery());
		pstmt.setString(20, tmp.getEngine());
		pstmt.setString(21, tmp.getTire());

		pstmt.setString(22, tmp.getBigLight());
		pstmt.setString(23, tmp.getLeftLight());
		pstmt.setString(24, tmp.getRightLight());
		pstmt.setString(25, tmp.getBrakeLight());
		pstmt.setString(26, tmp.getFarLight());
		pstmt.setString(27, tmp.getNearLight());
		pstmt.setString(28, tmp.getFrontFogLight());
		pstmt.setString(29, tmp.getBackFogLight());
		pstmt.setString(30, tmp.getOilBox());
		pstmt.setString(31, tmp.getGoodsBox());

		pstmt.setString(32, tmp.getHandle());
		pstmt.setString(33, tmp.getDisplay());
		pstmt.setString(34, tmp.getImageCollector());
		pstmt.setString(35, tmp.getMeter());
		pstmt.setString(36, tmp.getVoiceDialer());
		pstmt.setString(37, tmp.getCall());
		pstmt.setString(38, null);
		pstmt.setString(39, null);
		pstmt.setString(40, null);
		pstmt.setString(41, null);

		pstmt.setString(42, tmp.getKeyCheck());
		pstmt.setString(43, tmp.getStopCar());
		pstmt.setString(44, tmp.getLoginOut());
		pstmt.setString(45, tmp.getStopCarTime());
		// pstmt.setLong(45, tmp.getLocId());
		pstmt.setString(46, tmp.getUuid());
		// 增加
		pstmt.setString(47, tmp.getCpu());
		pstmt.setString(48, tmp.getMemory());
		pstmt.setString(49, null);
		pstmt.setString(50, tmp.getSdCard());
		pstmt.setString(51, tmp.getPrinter());
		pstmt.setString(52, tmp.getIsTimerLocate());
		pstmt.setString(53, tmp.getIsDistanceLocate());
		pstmt.setString(54, tmp.getConditioning());
		if (tmp.getTemperator() != null) {
			pstmt.setFloat(55, tmp.getTemperator());
		} else {
			pstmt.setNull(55, Types.FLOAT);
		}
		if (tmp.getOilMass() != null) {
			pstmt.setFloat(56, tmp.getOilMass());
		} else {
			pstmt.setNull(56, Types.FLOAT);
		}

		pstmt.setString(57, tmp.getFlameOut());
		// 增加油耗温度信息
		pstmt.setString(58, tmp.getTemeratureRouteNum());

		if (tmp.getOilUsed() != null) {
			pstmt.setFloat(59, tmp.getOilUsed());
		} else {
			pstmt.setNull(59, Types.FLOAT);
		}
		if (tmp.getOilAdded() != null) {
			pstmt.setFloat(60, tmp.getOilAdded());
		} else {
			pstmt.setNull(60, Types.FLOAT);
		}

		// 增加CMMP状态
		pstmt.setString(61, null);
		pstmt.setString(62, null);
		pstmt.setString(63, null);
		pstmt.setString(64, null);
		pstmt.setString(65, null);
		pstmt.setString(66, null);

		pstmt.addBatch();
	}

	public static void addBatchLocreocordBySql(PreparedStatement pstmt,
			GpsData tmp) throws SQLException {
		if (tmp.getX() == 0 || tmp.getY() == 0)
			return;

		pstmt.setString(1, tmp.getDEVICE_ID());
		pstmt.setFloat(2, tmp.getX());
		pstmt.setFloat(3, tmp.getY());
		pstmt.setFloat(4, tmp.getS());
		pstmt.setFloat(5, tmp.getH());
		pstmt.setFloat(6, tmp.getV());
		pstmt.setFloat(7, tmp.getMileage());
		pstmt.setTimestamp(8, tmp.getGpsTime());// 保存时间和日期
		pstmt.setString(9, tmp.getLocateType());// 类型：GPS
		pstmt.setInt(10, tmp.getCoordType());// 坐标类型
		pstmt.setString(11, null);

		pstmt.setString(12, tmp.getUuid());
		pstmt.setString(13, tmp.getAdress());
		pstmt.setString(14, tmp.getObjType());
		pstmt.setString(15, tmp.getObjId());

		pstmt.addBatch();

	}

	// 批量提交
	public static void commit(java.sql.Connection conn, Statement pstmt)
			throws SQLException {

		pstmt.executeBatch();
		conn.commit();

	}

	public static List<Map<String, String>> change(ArrayList gpsdatalist) {
		List<Map<String, String>> ls = new ArrayList<Map<String, String>>();
		for (int i = 0; i < gpsdatalist.size(); i++) {
			GpsData gpsdata = (GpsData) gpsdatalist.get(i);
			ls.add(change(gpsdata));
		}
		return ls;
	}

	private static Map<String, String> change(GpsData gpsdata) {
		Map<String, String> mp = new HashMap<String, String>();
		mp.put("id", gpsdata.getUuid() + "");
		mp.put("deviceId", gpsdata.getDEVICE_ID());
		mp.put("time", TimeUtil.changeToString(new Date(gpsdata.getGpsTime()
				.getTime())));
		mp.put("x", gpsdata.getX() + "");
		mp.put("y", gpsdata.getY() + "");
		mp.put("speed", gpsdata.getS() + "");
		mp.put("direction", gpsdata.getV() + "");
		mp.put("height", gpsdata.getH() + "");
		mp.put("distance", gpsdata.getMileage() + "");
		mp.put("satelliteNum", gpsdata.getC() + "");
		mp.put("oilUsed", gpsdata.getOilUsed() + "");
		return mp;
	}

	/**
	 * 过滤服务端缓存无此终端数据；过滤经度或纬度为0的点；过滤掉时间格式有误点
	 * 过滤掉不在中国境内的位置点；过滤GPS时间和服务端时间有20分钟误差的数据；
	 * 
	 * @return
	 * @author 
	 */
	public static GpsData getGpsDataFromParseBase(ParseBase pb) {
		GpsData gpsdata = null;
		float maxX = 135.041666F;
		float minX = 73.666666f;
		float maxY = 53.55f;
		float minY = 3.866666F;

		try {
			TTerminal term = null;
			term = GBLTerminalList.getInstance().getTerminaInfo(
					pb.getDeviceSN());
			// if (term == null) {
			// if (log.isDebugEnabled())
			// log.debug(
			// "系统中没有适配到指定的终端：device_id=" + pb.getDeviceSN());
			// return null;
			// }

			gpsdata = new GpsData();
			float tmpx = 0;
			float tmpy = 0;
			try {
				tmpx = Float.parseFloat(pb.getCoordX() == null ? "0" : pb
						.getCoordX());
				tmpy = Float.parseFloat(pb.getCoordY() == null ? "0" : pb
						.getCoordY());
			} catch (Exception ex) {
				tmpx = 0;
				tmpy = 0;
			}

			if (tmpx > 0 && tmpy > 0) {
				gpsdata.setX(tmpx);
				gpsdata.setY(tmpy);
			} else {
				gpsdata.setX(0.0f);
				gpsdata.setY(0.0f);
				if (pb.getStatusRecord() != null)
					pb.getStatusRecord().setLocate("0");
				// return null; // 0坐标不入库
			}

			if (tmpx > maxX || tmpx < minX || tmpy > maxY || tmpy < minY) {
				if (log.isDebugEnabled())
					log.debug(pb.getDeviceSN() + " 位置不中国境内的点，过滤不处理" + tmpx
							+ "," + tmpy + ",标为未定位状态");
				// return null;
				if (pb.getStatusRecord() != null)
					pb.getStatusRecord().setLocate("0");
			}

			gpsdata.setSIMCARD(pb.getPhnum());
			gpsdata.setDEVICE_ID(pb.getDeviceSN());

			SimpleDateFormat simpleDate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String strDate = simpleDate.format(new Date());
			Calendar curCal = Calendar.getInstance();
			gpsdata
					.setSysTime(new java.sql.Timestamp(curCal.getTimeInMillis()));

			if (pb.getTime() == null || pb.getTime().trim().length() == 0) {
				// 系统时间
				gpsdata.setTIME(strDate);
				pb.setTime(strDate);
			} else {
				Pattern pattern1 = Pattern
						.compile("^(\\d{4})-(0\\d{1}|1[0-2])-(0\\d{1}|[12]\\d{1}|3[01]) (0\\d{1}|1\\d{1}|2[0-3]):[0-5]\\d{1}:([0-5]\\d{1})$"); // 匹配'YYYY-MM-DD
				// HH24:MI:SS'格式
				Matcher matcher1 = pattern1.matcher(pb.getTime());
				boolean flag1 = matcher1.matches();
				if (flag1) {

					String isFilterGpsTimeLessThanServerTime = null;
					String isFilterGpsTimeGreaterThanServerTime = null;
					
					int lessThanInterval = 20;
					int greaterThanInterval = 20;
					try{
					isFilterGpsTimeLessThanServerTime = AllConfigCache
							.getInstance().getConfigMap().get(
									"isFilterLessThanServerTime");
					isFilterGpsTimeGreaterThanServerTime = AllConfigCache
							.getInstance().getConfigMap().get(
									"isFilterGreaterThanServerTime");
					}catch(Exception e){}
					if (isFilterGpsTimeLessThanServerTime != null
							&& isFilterGpsTimeLessThanServerTime.equals("1")) {
						String sLessThanInterval = AllConfigCache.getInstance()
								.getConfigMap().get("lessThanInterval");
						try {
							lessThanInterval = Integer
									.parseInt(sLessThanInterval);
						} catch (Exception e) {
						}
						Date gpstime = Tools.formatStrToDate(pb.getTime(),
								"yyyy-MM-dd HH:mm:ss");
						Calendar gpsCal = Calendar.getInstance();
						gpsCal.setTime(gpstime);
						if (log.isDebugEnabled())
							log.debug("服务器时间："
									+ Tools.formatDate2Str(
											curCal.getTime(),
											"yyyy-MM-dd HH:mm:ss")
									+ "-"
									+ "GPS时间："
									+ Tools.formatDate2Str(
											gpsCal.getTime(),
											"yyyy-MM-dd HH:mm:ss"));
						
						gpsCal.add(Calendar.MINUTE, lessThanInterval);

						int com1 = curCal.compareTo(gpsCal); // GPS时间比当前时间大于20分钟
						if (com1 > 0) {
							
							if (pb.getLocateType() != null
									&& !pb.getLocateType().equals("0")) {// 针对GPS进行过滤，LBS的不过滤

								if (log.isDebugEnabled())
									log.debug("无效GPS数据:" + gpsdata.toString());
								// return null; // 当成信号弱不文档数据，直接丢弃
								if (pb.getStatusRecord() != null)
									pb.getStatusRecord().setLocate("0");
							}
						}

					}

					if (isFilterGpsTimeGreaterThanServerTime != null
							&& isFilterGpsTimeGreaterThanServerTime.equals("1")) {
						String sGreaterThanInterval = AllConfigCache
								.getInstance().getConfigMap().get(
										"greaterThanInterval");
						try {
							greaterThanInterval = Integer
									.parseInt(sGreaterThanInterval);
						} catch (Exception e) {
						}

						Date gpstime = Tools.formatStrToDate(pb.getTime(),
								"yyyy-MM-dd HH:mm:ss");
						Calendar gpsCal = Calendar.getInstance();
						gpsCal.setTime(gpstime);
						if (log.isDebugEnabled())
							log.debug("服务器时间："
									+ Tools.formatDate2Str(
											curCal.getTime(),
											"yyyy-MM-dd HH:mm:ss")
									+ "-"
									+ "GPS时间："
									+ Tools.formatDate2Str(
											gpsCal.getTime(),
											"yyyy-MM-dd HH:mm:ss"));
						
						gpsCal.add(Calendar.MINUTE, -greaterThanInterval);

						int com1 = curCal.compareTo(gpsCal); // GPS时间比当前时间大于20分钟
						if (com1 < 0) {
							
							if (pb.getLocateType() != null
									&& !pb.getLocateType().equals("0")) {// 针对GPS进行过滤，LBS的不过滤

								if (log.isDebugEnabled())
									log.debug("无效GPS数据:" + gpsdata.toString());
								// return null; // 当成信号弱不文档数据，直接丢弃
								if (pb.getStatusRecord() != null)
									pb.getStatusRecord().setLocate("0");
							}
						}

					}

					gpsdata.setTIME(pb.getTime());

//					if (pb.getLocateStatus() != null
//							&& pb.getLocateStatus().equals("2")) {// GPS补偿数据是否进行时间过滤？？？
//
//						gpsdata.setTIME(pb.getTime());
//
//					}

				} else {// 如果格式错误，则采用系统时间
					if (log.isDebugEnabled())
						log.debug(pb.getDeviceSN() + " 上报的时间有误,要求转换GPS时间格式为：yyyy-MM-dd HH:mm:ss");
					if (pb.getStatusRecord() != null)
						pb.getStatusRecord().setLocate("0"); 
					
				}
			}

			Date date = simpleDate.parse(gpsdata.getTIME());
			Calendar relCal = Calendar.getInstance();
			relCal.setTime(date);
			gpsdata
					.setGpsTime(new java.sql.Timestamp(relCal.getTimeInMillis()));

			gpsdata.setS(Float.parseFloat(pb.getSpeed() == null ? "0" : pb
					.getSpeed()));
			if (null == pb.getDirection()) {
				pb.setDirection("0");
			}
			gpsdata.setDirection(Float.parseFloat(pb.getDirection()));

			if (pb.getMileage() != null && pb.getMileage() != "")
				gpsdata.setMileage(Float.parseFloat(pb.getMileage()));
			else {
				gpsdata.setMileage(0f);
			}

			if (pb.getAltitude() != null && pb.getAltitude().trim() != "")
				gpsdata.setH(Float.parseFloat(pb.getAltitude()));
			else
				gpsdata.setH(0f);

			if (pb.getSatellites() != null && pb.getSatellites() != "")
				gpsdata.setC(Integer.parseInt(pb.getSatellites()));
			else
				gpsdata.setC(0);
			// gpsdata.setLocateStatus(getLocateStatus());

			gpsdata.setAdress(pb.getAddress() == null ? "" : pb.getAddress());// 位置描述
			gpsdata.setCoordType(pb.getCoordType());
			gpsdata.setLocateType(pb.getLocateType());

			// if (pb.getStatusRecord() != null)
			// pb.getStatusRecord().setLocate(pb.getLocateStatus());

			if (pb.getStatusRecord() != null) {
				if (pb.getStatusRecord().getLocate().equals("1"))
					gpsdata.setTrack(true);
			} else {
				gpsdata.setTrack(false);
			}
			if (pb.getStatusRecord() != null)
				pb.getStatusRecord().setGpsTime(gpsdata.getGpsTime());

			gpsdata.setStatusRecord(pb.getStatusRecord());

			gpsdata.setExtendStatus(pb.getExtendStatus());
			if (term != null) {
				gpsdata.setObjId(term.getObjId());
				gpsdata.setObjType(term.getObjType());
				gpsdata.setSIMCARD(term.getSimcard());
			}
			gpsdata.setCompense(pb.isCompense());
			if (log.isDebugEnabled()) {

				log.debug("ParseBase term status is null:" + (pb.getStatusRecord() == null));
				log.debug("GpsData term status is null:" + (gpsdata.getStatusRecord()==null));
			}
		} catch (Exception ex2) {
			gpsdata = null;
			ex2.printStackTrace();
			log.error("GpsDataSave.class 出现错误.", ex2);
		}

		return gpsdata;
	}

	public static String getExtStatusSql() {
		String isLoadExt = AllConfigCache.getInstance().getConfigMap().get(
				"isLoadExtStatus");
		if (isLoadExt == null || isLoadExt.equals("0")) {
			return null;
		}

		String sql = null;

		ExtStatusConfig extCfg = LoadExtStatusConfig.getInstance()
				.getExtStatusCfg();
		String extSql = "";

		if (extCfg == null) {
			LoadExtStatusConfig.getInstance().loadConfig();
			extCfg = LoadExtStatusConfig.getInstance().getExtStatusCfg();
		}

		if (extCfg != null) {
			String className = extCfg.getClassName();
			String tableName = extCfg.getTableName();
			Property[] props = extCfg.getProperteis();

			extSql = "insert into " + tableName + " (device_id,loc_id,";

			try {

				Class extStatusClass = Class.forName(className);
				Object extStatusObj = extStatusClass.newInstance();
				// AbstractTermExtendStatus exts = tmp.getExtendStatus();

				if (extStatusObj instanceof AbstractTermExtendStatus) {

					for (int k = 0; k < props.length; k++) {
						Property p = props[k];
						String column = p.getColumn();
						String propName = p.getName();
						String type = p.getType();

						if (k != props.length - 1) {
							extSql += column + ",";
						} else {
							extSql += column + ")";
						}
					}

					extSql += " values(?,?,";

					for (int k = 0; k < props.length; k++) {
						if (k != props.length - 1) {
							extSql += "?,";
						} else {
							extSql += "?)";
						}
					}
					sql = extSql;
				} else {
					log.info("ext-status-config.xml中自定义状态扩展类 " + className
							+ "未继承AbstractTermExtendStatus。");
				}

				log.info("自定义扩展状态SQL:" + extSql);

			} catch (Exception e) {
				log.error("生成自定义扩展状态SQL异常", e);
			}

		}

		return sql;
	}

	public static void saveExtStatus(PreparedStatement extPstm, GpsData tmp)
			throws SQLException {

		ExtStatusConfig extCfg = LoadExtStatusConfig.getInstance()
				.getExtStatusCfg();

		if (extCfg == null) {
			LoadExtStatusConfig.getInstance().loadConfig();
			extCfg = LoadExtStatusConfig.getInstance().getExtStatusCfg();
		}

		if (extCfg != null) {

			String className = extCfg.getClassName();
			String tableName = extCfg.getTableName();
			Property[] props = extCfg.getProperteis();

			try {

				Class extStatusClass = Class.forName(className);
				AbstractTermExtendStatus exts = tmp.getExtendStatus();
				if (exts == null) {
					log
							.info(tmp.getDEVICE_ID()
									+ " 对应的解析类未实现扩展状态类AbstractTermExtendStatus，无扩展状态保存。");
					return;
				}

				if (exts instanceof AbstractTermExtendStatus) {
					// Class subClass =
					// exts.getClass().asSubclass(extStatusClass);

					extPstm.setString(1, tmp.getDEVICE_ID());
					extPstm.setString(2, tmp.getUuid());

					for (int k = 0; k < props.length; k++) {
						String type = props[k].getType();

						String propName = props[k].getName();
						propName = propName.replaceFirst(String
								.valueOf(propName.charAt(0)), String.valueOf(
								propName.charAt(0)).toUpperCase());

						Method m = extStatusClass.getMethod("get" + propName);
						Object res = m.invoke(exts, null);
						extPstm.setObject(k + 3, res);

						log.info(tmp.getDEVICE_ID() + " ,method:" + m.getName()
								+ ",value:" + res);
					}
					extPstm.addBatch();

				} else {
					log.info(tmp.getDEVICE_ID() + "自定义的扩展状态类 " + className
							+ "未继承AbstractTermExtendStatus,不能完成扩展状态保存操作");
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("增加扩展状态批量参数异常", e);
			}

		}

	}
}
