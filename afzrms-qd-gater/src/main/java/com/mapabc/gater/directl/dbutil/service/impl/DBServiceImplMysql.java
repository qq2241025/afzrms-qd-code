package com.mapabc.gater.directl.dbutil.service.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.LogFactory;

import com.eaio.uuid.UUID;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.TermOnlieStatus;
import com.mapabc.gater.directl.bean.command.TStructions;
import com.mapabc.gater.directl.dbutil.DbOperation;
import com.mapabc.gater.directl.dbutil.DbUtil;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestHelp;
import com.mapabc.gater.directl.encode.service.IAlarm;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.lbmp.service.CoordinateConvertService;
import com.mapabc.gater.lbsgateway.bean.TFlowVolume;

//import com.mapabc.wzt.service.monitor.structions.service.StructionsService;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年1月29日 上午11:48:24
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class DBServiceImplMysql implements DBService {
	private org.apache.commons.logging.Log log = LogFactory.getLog(this
			.getClass());
	private CoordinateConvertService coordinateConvertService;

	public CoordinateConvertService getCoordinateConvertService() {
		return coordinateConvertService;
	}

	public void setCoordinateConvertService(
			CoordinateConvertService coordinateConvertService) {
		this.coordinateConvertService = coordinateConvertService;
	}

	@Override
	public TStructions[] getStructionBySendStatus(String deviceId, String state) {
		TStructions overspeedThresholdAlarmTStruction = null;
		List<TStructions> areaAlarmTStructions = new ArrayList<TStructions>();
		
		com.mapabc.gater.directl.encode.doog.GAODEFactory gaodeFactory = new com.mapabc.gater.directl.encode.doog.GAODEFactory();
		IAlarm createAlarm = gaodeFactory.createAlarm();
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = null;
		try {
			// overspeed_threshold
			sql = "select overspeed_threshold from T_TERMINAL_ALARMSET where device_id=?";
			conn = DbUtil.getConnection();
			pst = conn.prepareStatement(sql);
			pst.setString(1, deviceId);
			rs = pst.executeQuery();
			log.info("query overspeed_threshold sql:" + sql);
			if (rs.next()) {
				float overspeed_threshold = rs.getFloat("overspeed_threshold");
				Request req = RequestHelp.generateRequestByOverspeedAlarm(deviceId, String.valueOf(overspeed_threshold));
				String instruction = createAlarm.overspeedAlarm(req);
				
				overspeedThresholdAlarmTStruction = new TStructions();
				overspeedThresholdAlarmTStruction.setDeviceId(deviceId);
				overspeedThresholdAlarmTStruction.setInstruction(instruction);
			}

			DbOperation.release(null, rs, pst, null, null);
			
			// alarmrule
			sql = "select ref2.alarm_type,ref2.area_no,ref2.overspeed_threshold,astext(a.xys) as xys from REF_TERMINAL_ALARMRULE_AREA ref"
					+ " left join REF_ALARMRULE_AREA_ALARMAREA ref2 on ref.alarmrule_area_id=ref2.alarmrule_area_id"
					+ " left join T_ALARM_AREA a on ref2.alarm_area_id=a.id"
					+ " where ref.device_id=?";

			pst = conn.prepareStatement(sql);
			pst.setString(1, deviceId);
			rs = pst.executeQuery();
			log.info("query alarmrule sql:" + sql);

			
			while (rs.next()) {
				String alarmType = rs.getString("alarm_type");
				String areaNo = rs.getString("area_no");
				String maxSpeed = rs.getString("overspeed_threshold");
				String points = rs.getString("xys");
				points = jta_polygon(killNull(points));

				Request req = RequestHelp.generateRequestByAreaAlarm(deviceId,
						areaNo, alarmType, maxSpeed, points);
				String instruction = createAlarm.areaAlarm(req);

				TStructions areaAlarmTStruction = new TStructions();
				areaAlarmTStruction.setDeviceId(deviceId);
				areaAlarmTStruction.setInstruction(instruction);

				areaAlarmTStructions.add(areaAlarmTStruction);
			}
		} catch (SQLException e) {
			 log.error("getStructionBySendStatus error", e);
		} finally {
			DbOperation.release(null, rs, pst, null, conn);
		}
		
		if(overspeedThresholdAlarmTStruction != null){
			areaAlarmTStructions.add(overspeedThresholdAlarmTStruction);
		}
		
		return areaAlarmTStructions
				.toArray(new TStructions[areaAlarmTStructions.size()]);
	}
	
	private String killNull(String s) {
		if (s == null || s.trim().equals("")) {
			s = "";
		}
		return s;
	}
	private String jta_polygon(String s){
		//POLYGON((116.222835487816 39.8911339121506,116.16078553185 40.183149996357,116.32303309583 40.2151505127008,116.378427435023 39.9241437011166,116.222835487816 39.8911339121506))
		s = s.trim();
		s = s.replace("POLYGON((", "");
		s = s.replace("))", "");
		s = s.replaceAll(",", ";");
		s = s.replaceAll(" ", ",");
		return s;
	}

	// @Override
	// public void updateInstructionsState(String deviceId, String state, String
	// cmdId) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// }

	// @Override
	// public QQTChildCardProperty getChildProperty(String deviceId) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// return null;
	// }

	@Override
	public HashMap<String, String> getStatusList(String deviceId) {
		// TODO Auto-generated method stub
		log.info("not realize");
		return null;
	}

	@Override
	public void saveTermOnlineStatus(String deviceId, String curIp,
			String status, String type, boolean isonline) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = null;
		String isSave = AllConfigCache.getInstance().getConfigMap()
				.get("isSaveOnline");

		if (isSave != null && isSave.equals("0")) {
			return;
		}
		if (isonline) {
			sql = "insert into t_term_online_record( cur_ip,status,device_id,type,in_date,id) values(?,?,?,?,?,?)";
		} else {
			sql = "insert into t_term_online_record(cur_ip,status,device_id,type,out_date,id) values(?,?,?,?,?,?)";
		}
		try {
			// TODO
			// conn = DbUtil.getConnection();
			// conn.setAutoCommit(false);
			// pst = conn.prepareStatement(sql);
			// pst.setString(1, curIp);
			// pst.setString(2, status);
			// pst.setString(3, deviceId);
			// pst.setString(4, type);
			// pst.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			// pst.setString(6, new UUID().toString());
			// pst.execute();
			// conn.commit();

			log.info("online sql:" + sql);
			// } catch (SQLException e) {
			// log.error("保存终端在线状态异常", e);
			// e.printStackTrace();
		} finally {
			DbOperation.release(null, rs, pst, null, conn);
		}
	}

	@Override
	public TermOnlieStatus getTermOnlineStatus(String deviceId, String type) {
		// TODO Auto-generated method stub
		log.info("not realize");
		return null;
	}

	// @Override
	// public boolean updateRemoteStatus(String deviceId, String status, String
	// desc) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// return false;
	// }

	// @Override
	// public boolean isUpdateSuccess(String deviceId) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// return false;
	// }

	@Override
	public void saveMoreAlarm(ArrayList<ParseBase> baseList) throws Exception {
		String sql = "{call PROC_ALARM_INFO(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";

		Connection conn = DbOperation.getConnection();
		CallableStatement cstm = null;
		try {
			conn.setAutoCommit(false);
			cstm = conn.prepareCall(sql);

			String curYyyyMMdd = Tools.getCurYyyyMMdd();
			
			for (int j = 0; j < baseList.size(); j++) {
				ParseBase base = baseList.get(j);
				Float x = Float.parseFloat(base.getCoordX() == null ? "0"
						: base.getCoordX());
				Float y = Float.parseFloat(base.getCoordY() == null ? "0"
						: base.getCoordY());

				UUID uuid = new UUID();
				String id = uuid.toString();
				cstm.setString(1, id);
				cstm.setString(2, base.getDeviceSN());
				cstm.setFloat(3, x);
				cstm.setFloat(4, y);
				cstm.setString(5, "");
				cstm.setString(6, base.getAltitude());
				cstm.setFloat(7, Float.parseFloat(base.getSpeed() == null ? "0"
						: base.getSpeed()));
				cstm.setFloat(8,
						Float.parseFloat(base.getDirection() == null ? "0"
								: base.getDirection()));
				cstm.setFloat(9,
						Float.parseFloat(base.getMileage() == null ? "0" : base
								.getMileage()));
				cstm.setTimestamp(
						10,
						/**
						 * new Timestamp(System.currentTimeMillis())
						 */
						new Timestamp(Tools.formatStrToDate(base.getTime(),
								"yyyy-MM-dd HH:mm:ss").getTime()));
				cstm.setString(
						11,
						base.getAlarmType() == null ? "-1" : base
								.getAlarmType());
//				cstm.setString(12, null);// CALLNUMBER
//				cstm.setString(13, null);// EID
				
				cstm.setString(12, curYyyyMMdd + base.getAlarmStartTime());// 报警持续开始时间
				cstm.setString(13, curYyyyMMdd + base.getAlarmEndTime());//　报警持续结束时间
				
				cstm.setString(14, null);// OBJ_ID
				cstm.setString(15, null);// OBJ_TYPE
				cstm.setString(16, base.getAlarmSubType());
				cstm.setFloat(17,
						Float.parseFloat(base.getSpeedThreshold() == null ? "0"
								: base.getSpeedThreshold()));// 超速阀值
				cstm.setString(18, base.getAreaNo());// 区域报警区域编号

				cstm.addBatch();
			}
			cstm.executeBatch();
			conn.commit();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error("批量保存报警异常", e);
			e.printStackTrace();
		} finally {
			DbOperation.release(null, null, null, cstm, conn);
		}
	}

	// @Override
	// public boolean insertPicInfo(Picture p) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// return false;
	// }

	// @Override
	// public boolean saveBaJuTask(BaJuTask task) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// return false;
	// }

	// @Override
	// public void updateBaJuTaskState(String deviceId, String state, String
	// type) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// }

	// @Override
	// public ArrayList<BaJuTask> getBaJuTaskByState(String flag) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// return null;
	// }
	//
	// @Override
	// public void batchUpdateBaJuTaskState(ArrayList<BaJuTask> taskList) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// }

	@Override
	public void updateInstructions(String seq, String ins, String cmdType,
			Date receiveDate, Date sendDate, String sendCount, String state)
			throws Exception {
		// TODO Auto-generated method stub
		log.info("not realize");
	}

	// @Override
	// public void saveFlowVolume(String deviceId, long size, String actiontype,
	// String linktype) throws SQLException {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// }

	@Override
	public void saveFlowVolume(ArrayList<TFlowVolume> list) throws SQLException {
		// TODO Auto-generated method stub
		log.info("not realize");
	}

	@Override
	public void initOlineState() {
		// TODO Auto-generated method stub
		log.info("not realize");
	}

	@Override
	public List<TStructions> loadWillSendInstruction(String cmdType,
			String cmdState) {
		// TODO Auto-generated method stub
		log.info("not realize");
		return null;
	}

	// @Override
	// public void saveTurnOutRecord(String deviceId, String icCard, Date
	// startDate, Date endDate, String driverName,
	// String driverLicense, String optType) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// }
	//
	// @Override
	// public void saveMessage(String sender, String name, String cont, String
	// type, String gid) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// }

	// @Override
	// public ArrayList<ForwardSettingBean> loadForwardCofig() {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// return null;
	// }
	// @Override
	// public List<TaskStatusBean> loadTaskStatus(String deviceId) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// return null;
	// }

	// @Override
	// public String saveInstruction(String deviceId, String userId, String
	// reqXml) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// return null;
	// }

	@Override
	public String saveStructions(String deviceId, String objId, String objType,
			String creator, java.util.Date createTime, String req,
			java.util.Date receiveTime, String type, String instruction,
			String param, java.util.Date sendTime, String sendCount,
			String reply, String state, String descp) {
		// String uuidSeq = null;
		// try {
		// StructionsService structionsService = (StructionsService)
		// AppCtxServer.getInstance().getBean(
		// ("structionsService"));
		// if (structionsService != null) {
		// uuidSeq = structionsService.saveStructions(deviceId, objId, objType,
		// creator, createTime, req,
		// receiveTime, type, instruction, param, sendTime, sendCount, reply,
		// state, descp);
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

		String uuidSeq = new UUID().toString();
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = "INSERT INTO T_STRUCTIONS"
				+ "	(id, device_id, instruction, state, `type`, param, reply, obj_id, obj_type, req, receive_time, send_time, send_count, creator, create_time, descp)"
				+ "	VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null, ?)";
		try {
			conn = DbUtil.getConnection();
			conn.setAutoCommit(false);
			pst = conn.prepareStatement(sql);
			pst.setString(1, uuidSeq);
			pst.setString(2, deviceId);
			pst.setString(3, instruction);
			pst.setString(4, state);
			pst.setString(5, type);
			pst.setString(6, param);
			pst.setString(7, reply);
			pst.setString(8, objId);
			pst.setString(9, objType);
			pst.setString(10, req);
			pst.setTimestamp(11, new Timestamp(receiveTime.getTime()));
			pst.setTimestamp(12, new Timestamp(sendTime.getTime()));
			pst.setString(13, sendCount);
			pst.setString(14, creator);
			pst.setString(15, descp);
			pst.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
			pst.setString(6, new UUID().toString());
			pst.execute();
			conn.commit();
			log.info("instruction sql:" + sql);
		} catch (SQLException e) {
			log.error("保存终端指令异常", e);
		} finally {
			DbOperation.release(null, rs, pst, null, conn);
		}
		return uuidSeq;
	}

	// @Override
	// public String saveStructions(String deviceId, String objId, String
	// objType, String creator,
	// java.util.Date createTime, String req, String state) {
	// // TODO Auto-generated method stub
	// log.info("not realize");
	// return null;
	// }

	@Override
	public String saveTask(String taskName, long taskType, String taskDesc,
			String creator, java.util.Date createTime, String taskPoints,
			String onUse, String deviceId, String actionType) {
		// TODO Auto-generated method stub
		log.info("not realize");
		return null;
	}

}
