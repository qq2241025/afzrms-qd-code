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
	private org.apache.commons.logging.Log log = LogFactory.getLog(this.getClass());
	private CoordinateConvertService coordinateConvertService;

	public CoordinateConvertService getCoordinateConvertService() {
		return coordinateConvertService;
	}

	public void setCoordinateConvertService(CoordinateConvertService coordinateConvertService) {
		this.coordinateConvertService = coordinateConvertService;
	}

	@Override
	public TStructions[] getStructionBySendStatus(String deviceId, String state) {
		log.info("not realize");
		// TODO Auto-generated method stub
		return null;
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
	public void saveTermOnlineStatus(String deviceId, String curIp, String status, String type, boolean isonline) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = null;
		String isSave = AllConfigCache.getInstance().getConfigMap().get("isSaveOnline");

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

			for (int j = 0; j < baseList.size(); j++) {
				ParseBase base = baseList.get(j);
				Float x = Float.parseFloat(base.getCoordX() == null ? "0" : base.getCoordX());
				Float y = Float.parseFloat(base.getCoordY() == null ? "0" : base.getCoordY());

				UUID uuid = new UUID();
				String id = uuid.toString();
				cstm.setString(1, id);
				cstm.setString(2, base.getDeviceSN());
				cstm.setFloat(3, x);
				cstm.setFloat(4, y);
				cstm.setString(5, "");
				cstm.setString(6, base.getAltitude());
				cstm.setFloat(7, Float.parseFloat(base.getSpeed() == null ? "0" : base.getSpeed()));
				cstm.setFloat(8, Float.parseFloat(base.getDirection() == null ? "0" : base.getDirection()));
				cstm.setFloat(9, Float.parseFloat(base.getMileage() == null ? "0" : base.getMileage()));
				cstm.setTimestamp(10, /**
				 * new
				 * Timestamp(System.currentTimeMillis())
				 */
				new Timestamp(Tools.formatStrToDate(base.getTime(), "yyyy-MM-dd HH:mm:ss").getTime()));
				cstm.setString(11, base.getAlarmType() == null ? "-1" : base.getAlarmType());
				cstm.setString(12, null);// 电话
				cstm.setString(13, null);
				cstm.setString(14, null);
				cstm.setString(15, null);
				cstm.setString(16, base.getAlarmSubType());
				cstm.setFloat(17, Float.parseFloat(base.getSpeedThreshold() == null ? "0" : base.getSpeedThreshold()));// 超速阀值
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
	public void updateInstructions(String seq, String ins, String cmdType, Date receiveDate, Date sendDate,
			String sendCount, String state) throws Exception {
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
	public List<TStructions> loadWillSendInstruction(String cmdType, String cmdState) {
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
	public String saveStructions(String deviceId, String objId, String objType, String creator,
			java.util.Date createTime, String req, java.util.Date receiveTime, String type, String instruction,
			String param, java.util.Date sendTime, String sendCount, String reply, String state, String descp) {
//		String uuidSeq = null;
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
	public String saveTask(String taskName, long taskType, String taskDesc, String creator, java.util.Date createTime,
			String taskPoints, String onUse, String deviceId, String actionType) {
		// TODO Auto-generated method stub
		log.info("not realize");
		return null;
	}

}
