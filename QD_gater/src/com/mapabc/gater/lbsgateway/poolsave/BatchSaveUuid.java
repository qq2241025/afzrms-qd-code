package com.mapabc.gater.lbsgateway.poolsave;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.LogFactory;

import com.eaio.uuid.UUID;
import com.mapabc.gater.counter.Counter;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Config;
import com.mapabc.gater.directl.bean.status.AbstractTTermStatusRecord;
import com.mapabc.gater.directl.dbutil.DbOperation;
import com.mapabc.gater.directl.dbutil.DbUtil;
import com.mapabc.gater.directl.encode.PropertyReader;
import com.mapabc.gater.lbsgateway.GBLTerminalList;
import com.mapabc.gater.lbsgateway.bean.TTerminal;

public class BatchSaveUuid {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(BatchSaveUuid.class);

//	private String isRelateObj;
//	private String dbType;
	private java.util.ArrayList<?> datalist;
	private java.sql.Connection conn;
	private Hashtable<String, GpsData> lastLoc = new Hashtable<String, GpsData>();

	/** 存储过程方式 */
	private final String statusPro = "{call PROC_TERM_STATUS_RECORD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private final String lastStatusPro = "{call PROC_TERM_STATUS_LAST_RECORD(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private final String insertProObj = "{call PROC_ADD_LOCRECORD_OBJ(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
	private final String callProcObj = "{ call PROC_ADD_Last_LOC_OBJ(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

	/** sql语句方式 */
	private final String insertLocSql = "INSERT INTO T_LOCRECORD(DEVICE_ID,LONGITUDE,LATITUDE,SPEED,HEIGHT,DIRECTION,DISTANCE,GPSTIME,LOCATE_TYPE,COORD_TYPE,STATE,ID,POS_DESC,OBJ_TYPE,OBJ_ID) VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String insertLastLocSql = "insert into  t_last_locrecord(longitude,latitude,speed,direction,height,distance,gpstime,device_id,locate_type,coord_type,state,ID,POS_DESC, Obj_Type,Obj_Id)  values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
	private final String updateLastLocSql = "update  t_last_locrecord l  set l.longitude=?,L.LATITUDE=?,L.SPEED=?,L.DIRECTION=?,L.HEIGHT=?,L.DISTANCE=?,L.GPSTIME=?,L.LOCATE_TYPE=?,l.inputdate=?,L.COORD_TYPE=?,l.state=?,L.LOC_ID=?,POS_DESC=?,OBJ_TYPE=?,OBJ_ID=?  where l.device_id = ?;";
	private final String insertStatusSql = " INSERT INTO  T_TERM_STATUS_RECORD(ID,DEVICE_ID,GPSTIME,LOCATE,ACC,FULL_EMPTY,TANK,BACK_CAR,BRAKE,CAR_DOOR,FRONT_CAR_DOOR,REAR_CAR_DOOR,FORTIFICATION,ANTENNA,MAIN_POWER,OIL_ELEC,GPS_MODULE,GSM_MODULE,BACKUP_BATTERY,ENGINE,TIRE,BIG_LIGHT,LEFT_LIGHT,RIGHT_LIGHT,BRAKE_LIGHT,FAR_LIGHT,NEAR_LIGHT,FRONT_FOG_LIGHT,BACK_FOG_LIGHT,OIL_BOX,GOODS_BOX,HANDLE,DISPLAY,IMAGE_COLLECTOR,METER,VOICE_DIALER,CALL,HIGH_ELEC_ONE,HIGH_ELEC_TWO,LOW_ELEC_ONE,LOW_ELEC_TWO,KEY_CHECK,STOP_CAR,LOGIN_OUT,STOP_CAR_TIME,LOC_ID,CPU,MEMORY,FLASH,SD_CARD,PRINTER,IS_TIMER_LOCATE,IS_DISTANCE_LOCATE,CONDITIONING,TEMPERATURE,OIL_MASS,FLAME_OUT,TEMPERATURE_ROUTE_NUM,OIL_USED,OIL_ADDED,CMMP_POWER,CMMP_LDPC,CMMP_RS,CMMP_POWER1,CMMP_POWER2,CMMP_SNR) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String insertLastStatusSql = "INSERT INTO T_TERM_STATUS_LAST_RECORD(ID,DEVICE_ID,GPSTIME,LOCATE,ACC,FULL_EMPTY,TANK,BACK_CAR,BRAKE,CAR_DOOR,FRONT_CAR_DOOR,REAR_CAR_DOOR,FORTIFICATION,ANTENNA,MAIN_POWER,OIL_ELEC,GPS_MODULE,GSM_MODULE,BACKUP_BATTERY,ENGINE,TIRE,BIG_LIGHT,LEFT_LIGHT,RIGHT_LIGHT,BRAKE_LIGHT,FAR_LIGHT,NEAR_LIGHT,FRONT_FOG_LIGHT,BACK_FOG_LIGHT,OIL_BOX,GOODS_BOX,HANDLE,DISPLAY,IMAGE_COLLECTOR,METER,VOICE_DIALER,CALL,HIGH_ELEC_ONE,HIGH_ELEC_TWO,LOW_ELEC_ONE,LOW_ELEC_TWO,KEY_CHECK,STOP_CAR,LOGIN_OUT,STOP_CAR_TIME,LAST_LOC_ID,CPU,MEMORY,FLASH,SD_CARD,PRINTER,IS_TIMER_LOCATE,IS_DISTANCE_LOCATE,conditioning,TEMPERATURE,OIL_MASS,FLAME_OUT,TEMPERATURE_ROUTE_NUM,OIL_USED,OIL_ADDED,CMMP_POWER,CMMP_LDPC,CMMP_RS,CMMP_POWER1,CMMP_POWER2,CMMP_SNR) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private final String updateLastStatusSql = "update  T_TERM_STATUS_LAST_RECORD l set GPSTIME=?,LOCATE=?,ACC=?,FULL_EMPTY=?,TANK=?,BACK_CAR=?,BRAKE=?,CAR_DOOR=?,FRONT_CAR_DOOR=?,REAR_CAR_DOOR=?,FORTIFICATION=?,ANTENNA=?,MAIN_POWER=?,OIL_ELEC=?,GPS_MODULE=?,GSM_MODULE=?,BACKUP_BATTERY=?,ENGINE=?,TIRE=?,BIG_LIGHT=?,LEFT_LIGHT=?,RIGHT_LIGHT=?,BRAKE_LIGHT=?,FAR_LIGHT=?,NEAR_LIGHT=?,FRONT_FOG_LIGHT=?,BACK_FOG_LIGHT=?,OIL_BOX=?,GOODS_BOX=?,HANDLE=?,DISPLAY=?,IMAGE_COLLECTOR=?,METER=?,VOICE_DIALER=?,CALL=?,HIGH_ELEC_ONE=?,HIGH_ELEC_TWO=?,LOW_ELEC_ONE=?,LOW_ELEC_TWO=?,KEY_CHECK=?,STOP_CAR=?,LOGIN_OUT=?,STOP_CAR_TIME=?,LAST_LOC_ID=?,CPU=?,MEMORY=?,FLASH=?,SD_CARD=?,PRINTER=?,IS_TIMER_LOCATE=?,IS_DISTANCE_LOCATE=?,CONDITIONING=?,  L.TEMPERATURE=?,L.OIL_MASS=?,L.FLAME_OUT=?,l.temperature_route_num=?,l.oil_used=?,l.oil_added=?,CMMP_POWER=?,CMMP_LDPC=?,CMMP_RS=?,CMMP_POWER1=?,CMMP_POWER2=?,CMMP_SNR=? where l.device_id = ?";

	public static boolean isSentAlarm = false;
	public static boolean isSaveTermStatus = true;
	static {
		String s = Config.getInstance().getProperty("isSentAlarm");

		if (s != null && s.equals("1")) {
			isSentAlarm = true;
		}
		String isSaveTermStatus = AllConfigCache.getInstance().getConfigMap()
				.get("isSaveTermStatus");
		if (isSaveTermStatus != null && isSaveTermStatus.equals("0")) {
			BatchSaveUuid.isSaveTermStatus = false;
		}

	}

	public BatchSaveUuid() {
	}

	public void rebuildConnection() throws SQLException {
		if (conn == null || conn.isClosed()) {
			conn = DbUtil.getConnection();
		}
	}

	public void setDataList(ArrayList<?> li) {
		this.datalist = li;
	}

	public void batchSave(ArrayList<?> datalist) {
		if (datalist.size() <= 0) {
			return;
		}
		String saveType = "1";// AllConfigCache.getInstance().getConfigMap().get("dbSaveOff").trim();
		String saveOff = (saveType == null || saveType.length() <= 0) ? "1"
				: saveType;

		if (log.isDebugEnabled())
			log.debug("入库队列SIZE=" + datalist.size());

		ArrayList<GpsData> gpsdatalist = new ArrayList<GpsData>();
		for (int i = 0; i < datalist.size(); i++) {
			GpsData tmpgpsdata = (GpsData) datalist.get(i);
			try {
				if (tmpgpsdata != null) {
					gpsdatalist.add(tmpgpsdata);
					if (saveType.equals("2"))
						lastLoc.put(tmpgpsdata.getDEVICE_ID(), tmpgpsdata);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		if (gpsdatalist.size() <= 0) {
			return;
		}

		long dbS = System.currentTimeMillis();
		conn = DbOperation.getConnection();//
		long dbE = System.currentTimeMillis();
		if (log.isDebugEnabled())
			log.debug("获取数据库连接耗时：" + (dbE - dbS) + "ms");

		if (log.isDebugEnabled())
			log.debug("入库方式为：" + saveOff);

		if (saveOff.equals("1")) {
			this.saveDataByProAddObj(gpsdatalist);
		} else if (saveOff.equals("0")) {

		} else if (saveOff.equals("2")) {// sql方式入库
			saveDataBySql(gpsdatalist);
		}
		try {

			if (isSentAlarm) {

				List<Map<String, String>> list = BatchSaveUtil.change(gpsdatalist);
				AlarmDataPool.getInstance().add(list);
				// 批量报警判断：add by 
				// long alarmS = System.currentTimeMillis();
				// AlarmService alarm = new AlarmServiceLocalImpl();
				//					
				// if ((alarmServiceType != null)
				// && alarmServiceType.trim().equals("1")) {
				// alarm = new AlarmServiceRemoteImpl(AllConfigCache
				// .getInstance().getConfigMap()
				// .get("alarmServiceUrl").trim(), "utf-8");
				// }
				//					
				// alarm.alarmJudge(BatchSaveUtil.change(gpsdatalist));
				// long alarmE = System.currentTimeMillis();
				// if (log.isDebugEnabled())
				// log.debug("发送位置到报警服务成功, gpsdatalist : " + gpsdatalist
				// + ", 耗时:" + (alarmE - alarmS) + "ms");
				// alarm = null;
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			gpsdatalist = null;
			lastLoc.clear();
			lastLoc = null;
		}

	}

	// 存储过程方式入库
	private void saveDataByProAddObj(ArrayList gpsdatalist) {

		String uuid = new UUID().toString();

		CallableStatement pstmt = null;
		CallableStatement cstmt = null;
		CallableStatement statusCstmt = null;
		CallableStatement lastStatusCstmt = null;

		String issaveInvalid = "0";
		String isSaveLbsOfGps = null;
		try {
			issaveInvalid = AllConfigCache.getInstance().getConfigMap().get(
					"isSaveInvalid");
			isSaveLbsOfGps = AllConfigCache.getInstance().getConfigMap().get(
					"isSaveLbsOfGps");
		} catch (Exception e5) {
			// TODO Auto-generated catch block
			e5.printStackTrace();
		}

		PreparedStatement extPstm = null;
		PropertyReader pr = null;
		String isLoadExt = null;

		try {
			isLoadExt = AllConfigCache.getInstance().getConfigMap().get(
					"isLoadExtStatus");
			if (isLoadExt != null && isLoadExt.equals("1")) {
				String extSql = BatchSaveUtil.getExtStatusSql();
				if (extSql != null)
					extPstm = conn.prepareStatement(extSql);
				// log.debug("自定义扩展状态SQL:" + extSql);
			}

		} catch (Exception e) {
			log.error("自定义扩展状态SQL异常", e);
		}

		try {

			conn.setAutoCommit(false);
			pstmt = conn.prepareCall(insertProObj);// conn.prepareStatement(insertSql);
			cstmt = conn.prepareCall(callProcObj);
			statusCstmt = conn.prepareCall(statusPro);
			lastStatusCstmt = conn.prepareCall(lastStatusPro);
			int hisLocCount = 0;
			int lastLocCount = 0;
			int hisStatusCount = 0;
			int lastStatusCount = 0;
			int extStatusCount = 0;

			for (int i = 0; i < gpsdatalist.size(); i++) {
				GpsData tmp = (GpsData) gpsdatalist.get(i);
				AbstractTTermStatusRecord statusRecord = tmp.getStatusRecord();
				String locateStatus = null;
				if (statusRecord != null) {
					locateStatus = statusRecord.getLocate();
				}
				boolean iscompense = tmp.isCompense();

				uuid = new UUID().toString();

				log.debug(tmp.getDEVICE_ID() + " locrecordNextSeq=" + uuid);
				if (locateStatus != null && locateStatus.equals("0")) {// 无效数据处理

					if (issaveInvalid != null && issaveInvalid.equals("0")) {
						Counter.setDbIVCount();
						if (log.isDebugEnabled())
							log.debug("无效数据，不入库：" + tmp.toString());
					} else {
						tmp.setUuid(uuid);
						if (tmp.getX() != 0 && tmp.getY() != 0) {

							if (isSaveLbsOfGps != null
									&& isSaveLbsOfGps.equals("0")) {
								TTerminal terminal = GBLTerminalList
										.getInstance().getTerminaInfo(
												tmp.getDEVICE_ID());
								if (terminal != null) {
									String deviceType = terminal
											.getLocateType();
									if (deviceType != null
											&& deviceType.equals("1")
											&& tmp.getLocateType().equals("0")) {
										log.debug("GPS设备 " + tmp.getDEVICE_ID()
												+ " 定位类型为LBS，不更新最近位置表.");
									} else {

										BatchSaveUtil.addBatchLocreocord(cstmt,
												tmp);
										lastLocCount++;
									}
								}
							} else {

								BatchSaveUtil.addBatchLocreocord(cstmt, tmp);
								lastLocCount++;
							}

							BatchSaveUtil.addBatchLocreocord(pstmt, tmp);
							hisLocCount++;
							if (log.isDebugEnabled())
								log.info("保存无效数据：" + tmp.toString() + ",id="
										+ uuid);
						}

					}

				} else if (iscompense) {// 终端盲区补偿数据只入历史位置表

					if (log.isDebugEnabled())
						log.info("终端盲区补偿数据：" + tmp.toString() + ",id=" + uuid
								+ ",只保存历史位置表，不更新最近位置表。");

					tmp.setUuid(uuid);
					BatchSaveUtil.addBatchLocreocord(pstmt, tmp);
					hisLocCount++;

				} else {
					// 更新最后位置表
					tmp.setUuid(uuid);
					if (isSaveLbsOfGps != null && isSaveLbsOfGps.equals("0")) {
						TTerminal terminal = GBLTerminalList.getInstance()
								.getTerminaInfo(tmp.getDEVICE_ID());
						if (terminal != null) {
							String deviceType = terminal.getLocateType();
							if (deviceType != null && deviceType.equals("1")
									&& tmp.getLocateType().equals("0")) {
								log.debug("GPS设备 " + tmp.getDEVICE_ID()
										+ " 定位类型为LBS，不更新最近位置表.");
							} else {
								BatchSaveUtil.addBatchLocreocord(cstmt, tmp);
								lastLocCount++;
							}
						}
					} else {
						BatchSaveUtil.addBatchLocreocord(cstmt, tmp);
						lastLocCount++;
					}

					if (tmp.isTrack()) {// 历史位置表
						if (log.isDebugEnabled())
							log.debug("终端：" + tmp.getDEVICE_ID()
									+ "，状态:忙，轨迹开始入库！");
						// tmp.setId(id);
						tmp.setUuid(uuid);
						BatchSaveUtil.addBatchLocreocord(pstmt, tmp);
						hisLocCount++;

					}
				}

				if (statusRecord != null) {// 保存终端状态信息,无效GPS数据携带的状态也将保存
					if (BatchSaveUuid.isSaveTermStatus) {
						tmp.setOilUsed(statusRecord.getOilUsed());
						statusRecord.setUuid(uuid);
						statusRecord.setDeviceId(tmp.getDEVICE_ID());
						// 状态记录
						BatchSaveUtil.addBatchStatus(statusCstmt, statusRecord);
						lastStatusCount++;
						// 最近状态记录
						BatchSaveUtil.addBatchStatus(lastStatusCstmt,
								statusRecord);
						hisStatusCount++;
					} 
				}
				try {
					if (isLoadExt != null && isLoadExt.equals("1")) {
						if (extPstm != null)
							BatchSaveUtil.saveExtStatus(extPstm, tmp);
						extStatusCount++;
					}

				} catch (Exception e) {
					log.error("保存自定义扩展状态异常", e);
				}
			}

			try {
				if (hisLocCount > 0) {
					long s3 = System.currentTimeMillis();

					BatchSaveUtil.commit(conn, pstmt);

					long e3 = System.currentTimeMillis();

					Counter.setDbPoolSCount(gpsdatalist.size());

					if (log.isDebugEnabled()) {
						log.debug("commit loc time：" + (e3 - s3) + "ms");
					}
				}

			} catch (Exception e) {

				log.error("提交历史位置表异常", e);
				e.printStackTrace();
			}
			try {
				if (lastLocCount > 0) {
					long s1 = System.currentTimeMillis();

					BatchSaveUtil.commit(conn, cstmt);

					long e1 = System.currentTimeMillis();
					if (log.isDebugEnabled()) {

						log.debug("commit last loc time：" + (e1 - s1) + "ms");
						log.debug("已入库数：" + Counter.dbPoolSCount);

					}
				}
			} catch (Exception e) {

				log.error("提交最近位置表异常", e);
				e.printStackTrace();
			}

			try {
				if (hisStatusCount > 0) {
					if (statusCstmt != null) {
						long s4 = System.currentTimeMillis();

						BatchSaveUtil.commit(conn, statusCstmt);

						long e4 = System.currentTimeMillis();
						if (log.isDebugEnabled())
							log.debug("commit status time：" + (e4 - s4) + "ms");
					}
				}
			} catch (Exception e) {

				log.error("提交历史状态表异常", e);
				e.printStackTrace();
			}
			try {
				if (lastStatusCount > 0) {
					if (lastStatusCstmt != null) {
						long s2 = System.currentTimeMillis();

						BatchSaveUtil.commit(conn, lastStatusCstmt);

						long e2 = System.currentTimeMillis();
						if (log.isDebugEnabled())
							log.debug("commit lastStatus time：" + (e2 - s2)
									+ "ms");
					}
				}
			} catch (Exception e) {

				log.error("提交最近状态表异常", e);
				e.printStackTrace();
			}

			try {
				if (extStatusCount > 0) {
					long s5 = System.currentTimeMillis();
					if (null != extPstm)
						BatchSaveUtil.commit(conn, extPstm);
					long e5 = System.currentTimeMillis();
					if (log.isDebugEnabled())
						log.debug("commit extend status time：" + (e5 - s5)
								+ "ms");
				}
			} catch (Exception e) {

				log.error("提交扩展状态表异常", e);
				e.printStackTrace();
			} finally {
				if (extPstm != null) {
					extPstm.close();
				}
			}

		} catch (SQLException ex) {

			log.error(" batch save byProcedure：" + gpsdatalist.size()
					+ " fail!", ex);
			ex.printStackTrace();

		} finally {
			try {
				if (cstmt != null) {
					cstmt.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (statusCstmt != null) {
					statusCstmt.close();
				}
				if (lastStatusCstmt != null) {
					lastStatusCstmt.close();
				}
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}

			this.datalist = null;
		}
	}

	/**
	 * 通过SQL语句方式
	 * 
	 * @param gpsdatalist
	 */
	public void saveDataBySql(ArrayList gpsdatalist) {
		try {
			this.insertLocAndStatus(gpsdatalist);
		} catch (Exception e) {
		}

		try {
			this.saveOrUpdateLastLocAndLastStatus();
		} catch (Exception e) {
		}
	}

	public void saveOrUpdateLastLocAndLastStatus() {
		String issaveInvalid = "0";
		try {
			issaveInvalid = AllConfigCache.getInstance().getConfigMap().get(
					"isSaveInvalid");
		} catch (Exception e) {
		}

		PreparedStatement lastLocStmt = null;
		PreparedStatement updateLastlocStmt = null;
		PreparedStatement lastStatusCstmt = null;
		PreparedStatement updateLastStatusCstmt = null;

		try {
			this.rebuildConnection();

			lastLocStmt = conn.prepareStatement(insertLastLocSql);
			updateLastlocStmt = conn.prepareStatement(updateLastLocSql);
			lastStatusCstmt = conn.prepareStatement(insertLastStatusSql);
			updateLastStatusCstmt = conn.prepareStatement(updateLastStatusSql);

			Collection<GpsData> collection = lastLoc.values();
			Iterator<GpsData> itera = collection.iterator();
			int i = 0;
			if (log.isDebugEnabled())
				log.debug("本次待保存最近位置表数据条数：" + collection.size());

			while (itera.hasNext()) {
				String uuid = new UUID().toString();
				GpsData tmp = itera.next();
				boolean isExist = GBLTerminalList.getInstance().getLastLocMap(
						tmp.getDEVICE_ID());

				AbstractTTermStatusRecord statusRecord = tmp.getStatusRecord();
				String locateStatus = null;
				if (statusRecord != null) {
					locateStatus = statusRecord.getLocate();
				}

				if (locateStatus != null && locateStatus.equals("1")) {// 只保存有效数据

					tmp.setUuid(uuid);
					if (!isExist) {
						BatchSaveUtil.addBatchLocreocord(lastLocStmt, tmp);
						GBLTerminalList.getInstance().addLastLocToMap(
								tmp.getDEVICE_ID());
					} else {
						BatchSaveUtil
								.addBatchLocreocord(updateLastlocStmt, tmp);
					}

				} else {
					if (issaveInvalid != null && issaveInvalid.equals("0")) {
						if (log.isDebugEnabled())
							log.debug("无效数据，不入库：" + tmp.toString() + ",id="
									+ uuid);
					} else {
						tmp.setUuid(uuid);
						if (!isExist) {
							BatchSaveUtil.addBatchLocreocord(lastLocStmt, tmp);
							GBLTerminalList.getInstance().addLastLocToMap(
									tmp.getDEVICE_ID());
						} else {
							BatchSaveUtil.addBatchLocreocord(updateLastlocStmt,
									tmp);
						}
						if (log.isDebugEnabled())
							log.debug("保存无效数据：" + tmp.toString() + ",id="
									+ uuid);
					}
				}

				if (statusRecord != null) {// 保存终端状态信息,无效GPS数据携带的状态也将保存
					boolean isExistLastStatus = GBLTerminalList.getInstance()
							.getLastStatusMap(tmp.getDEVICE_ID());

					statusRecord.setUuid(uuid);
					statusRecord.setDeviceId(tmp.getDEVICE_ID());

					if (!isExistLastStatus) {
						// 插入新的最近状态记录
						BatchSaveUtil.addBatchStatus(lastStatusCstmt,
								statusRecord);
						GBLTerminalList.getInstance().addLastStatusToMap(
								tmp.getDEVICE_ID());
					} else {
						// 更新以有记录
						BatchSaveUtil.addBatchStatus(updateLastStatusCstmt,
								statusRecord);
					}
				}
				if (log.isDebugEnabled())
					log.debug("本次最近位置表数据：" + tmp.toString());

			}

			lastLoc.clear();

			try {
				long s1 = System.currentTimeMillis();
				BatchSaveUtil.commit(conn, lastLocStmt);
				long e1 = System.currentTimeMillis();
				log.debug("insert lastLoc cost time：" + (e1 - s1) + "ms");
			} catch (Exception e) {
				log.error("插入最近位置表异常", e);
				e.printStackTrace();
			}
			try {
				long s2 = System.currentTimeMillis();
				BatchSaveUtil.commit(conn, updateLastlocStmt);
				long e2 = System.currentTimeMillis();
				if (log.isDebugEnabled())
					log.debug("update  lastLoc cost time：" + (e2 - s2) + "ms");
			} catch (Exception e) {
				log.error("更新最近位置表异常", e);
				e.printStackTrace();
			}

			try {
				long s3 = System.currentTimeMillis();
				BatchSaveUtil.commit(conn, lastStatusCstmt);
				long e3 = System.currentTimeMillis();
				if (log.isDebugEnabled())
					log.debug("insert  lastStatus cost time：" + (e3 - s3)
							+ "ms");
			} catch (Exception e) {
				log.error("插入最近状态表异常", e);
				e.printStackTrace();
			}
			try {
				long s4 = System.currentTimeMillis();
				BatchSaveUtil.commit(conn, updateLastStatusCstmt);
				long e4 = System.currentTimeMillis();
				if (log.isDebugEnabled())
					log
							.debug("update lastStatus cost time：" + (e4 - s4)
									+ "ms");
			} catch (Exception e) {
				log.error("更新最近状态表异常", e);
				e.printStackTrace();
			}

		} catch (Exception e) {
			log.error("更新最近位置表异常", e);
		} finally {
			try {
				if (lastLocStmt != null) {
					lastLocStmt.close();
				}
				if (updateLastlocStmt != null) {
					updateLastlocStmt.close();
				}
				if (lastStatusCstmt != null) {
					lastStatusCstmt.close();
				}

				DbOperation.release(null, null, updateLastStatusCstmt, null,
						conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 插入位置及状态
	 * 
	 * @param gpsdatalist
	 */
	public void insertLocAndStatus(ArrayList gpsdatalist) {

		String issaveInvalid = "0";
		try {
			issaveInvalid = AllConfigCache.getInstance().getConfigMap().get(
					"isSaveInvalid");
		} catch (Exception e) {
		}

		PreparedStatement insertHisLocPstm = null;
		PreparedStatement insertHisStatusPstm = null;

		String uuid = null;
		try {
			this.rebuildConnection();

			insertHisLocPstm = conn.prepareStatement(insertLocSql);
			insertHisStatusPstm = conn.prepareStatement(insertStatusSql);

			for (int i = 0; i < gpsdatalist.size(); i++) {
				uuid = new UUID().toString();
				GpsData tmp = (GpsData) gpsdatalist.get(i);
				tmp.setUuid(uuid);
				lastLoc.put(tmp.getDEVICE_ID(), tmp);

				AbstractTTermStatusRecord statusRecord = tmp.getStatusRecord();
				String locateStatus = null;
				if (statusRecord != null) {
					locateStatus = statusRecord.getLocate();
				}

				if (locateStatus != null && locateStatus.equals("1")) {// 只保存有效数据
					if (tmp.isTrack()) {// 历史位置表
						if (log.isDebugEnabled())
							log.debug("终端：" + tmp.getDEVICE_ID()
									+ "，状态:忙，轨迹开始入库！");
						BatchSaveUtil.addBatchLocreocord(insertHisLocPstm, tmp);
					}
				} else {
					if (issaveInvalid != null && issaveInvalid.equals("0")) {
						uuid = "0";
						if (log.isDebugEnabled())
							log.debug("无效数据，不入历史位置库：" + tmp.toString() + ",id="
									+ uuid);
					} else {
						BatchSaveUtil.addBatchLocreocord(insertHisLocPstm, tmp);
						if (log.isDebugEnabled())
							log.debug("保存无效数据到历史位置表：" + tmp.toString() + ",id="
									+ uuid);
					}
				}

				if (statusRecord != null) {// 保存终端状态信息,无效GPS数据携带的状态也将保存
					tmp.setOilUsed(statusRecord.getOilUsed());
					statusRecord.setUuid(uuid);
					statusRecord.setDeviceId(tmp.getDEVICE_ID());
					BatchSaveUtil.addBatchStatus(insertHisStatusPstm,
							statusRecord);
				}

			}

			try {
				long s1 = System.currentTimeMillis();
				BatchSaveUtil.commit(conn, insertHisLocPstm);
				long e1 = System.currentTimeMillis();
				if (log.isDebugEnabled())
					log.debug("insert hisLoc costtime：" + (e1 - s1) + "ms");
			} catch (Exception e) {

				log.error("提交历史位置表异常", e);
				e.printStackTrace();
			}
			try {
				long s2 = System.currentTimeMillis();
				BatchSaveUtil.commit(conn, insertHisStatusPstm);
				long e2 = System.currentTimeMillis();
				if (log.isDebugEnabled())
					log.debug("insert hisStatus cost time：" + (e2 - s2) + "ms");
			} catch (Exception e) {

				log.error("提交最近状态表异常", e);
				e.printStackTrace();
			}

		} catch (SQLException ex) {
			log.error("保存历史位置及状态异常", ex);
			ex.printStackTrace();

		} finally {
			try {
				if (insertHisLocPstm != null) {
					insertHisLocPstm.close();
				}
				DbOperation
						.release(null, null, insertHisStatusPstm, null, conn);
			} catch (Exception e) {
				e.printStackTrace();
			}
			this.datalist = null;
		}

	}

	// 只保存轨迹位置表记录，对于补偿数据不保存到最近位置表
	public void saveTrackLoc(ArrayList parseList) {
		Statement stm = null;
		ResultSet rs = null;
		long ids[] = new long[parseList.size()];
		if (parseList.size() <= 0) {
			return;
		}
		ArrayList gpsdatalist = parseList;// new ArrayList();

		// for (int i = 0; i < parseList.size(); i++) {
		// GpsData tmpgpsdata = (GpsData) parseList.get(i);
		// if (tmpgpsdata != null) {
		// gpsdatalist.add(tmpgpsdata);
		// }
		// }

		CallableStatement pstmt = null;
		long id = 0;
		try {
			this.rebuildConnection();
			conn.setAutoCommit(false);
			pstmt = conn.prepareCall(insertProObj);

			for (int i = 0; i < gpsdatalist.size(); i++) {
				GpsData tmp = (GpsData) gpsdatalist.get(i);
				String uuid = new UUID().toString();
				tmp.setUuid(uuid);
				BatchSaveUtil.addBatchLocreocord(pstmt, tmp);
			}

			pstmt.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException ex) {

			ex.printStackTrace();
			log.error(" GPS盲区补偿 batch save byProcedure：" + gpsdatalist.size()
					+ " fail!", ex);
		} finally {
			try {

				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}

		}
	}

}
