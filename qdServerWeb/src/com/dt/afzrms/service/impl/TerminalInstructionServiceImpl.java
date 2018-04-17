package com.dt.afzrms.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.RefTerminalAlarmruleAreaDao;
import com.dt.afzrms.dao.hibernate.TAlarmAreaDao;
import com.dt.afzrms.dao.hibernate.TAlarmRuleAreaDao;
import com.dt.afzrms.dao.hibernate.TTerminalAlarmsetDao;
import com.dt.afzrms.po.RefAlarmruleAreaAlarmarea;
import com.dt.afzrms.po.RefTerminalAlarmruleArea;
import com.dt.afzrms.po.TAlarmArea;
import com.dt.afzrms.po.TAlarmruleArea;
import com.dt.afzrms.po.TTerminal;
import com.dt.afzrms.po.TTerminalAlarmset;
import com.dt.afzrms.service.TerminalInstructionService;
import com.dt.afzrms.threadpool.TerminalInstructionExecutor;
import com.dt.afzrms.util.JtsUtils;
import com.dt.afzrms.util.StringUtil;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestHelp;
import com.mapabc.gater.directl.encode.service.IAlarm;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年3月10日 下午2:03:31
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class TerminalInstructionServiceImpl implements
		TerminalInstructionService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${gater.service.url}")
	private String url;

	@Autowired
	private TTerminalAlarmsetDao tTerminalAlarmsetDao;
	@Autowired
	private TAlarmRuleAreaDao tAlarmRuleAreaDao;
	@Autowired
	private RefTerminalAlarmruleAreaDao refTerminalAlarmruleAreaDao;
	@Autowired
	private TAlarmAreaDao tAlarmAreaDao;
	@Autowired
	private TerminalInstructionExecutor terminalInstructionExecutor;

	@Override
	public String timeInter(String deviceIds, int interval, int count) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("deviceId", deviceIds);
		params.put("name", "timeInter");
		params.put("interval", String.valueOf(interval));
		if (count < 0) {
			count = 0;
		}
		params.put("count", String.valueOf(count));

		boolean sendInstruction = terminalInstructionExecutor.sendInstruction(
				params, true);
		if (sendInstruction) {
			return "ok";
		} else {
			return null;
		}
	}

	@Override
	public String saveOverspeedAlarm(String deviceIds, float max, int duration) {
		String[] deviceIdArray = StringUtil.split2StringArray(deviceIds, ";");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deviceIds", Arrays.asList(deviceIdArray));
		tTerminalAlarmsetDao
				.updateWithSqlSupportList(
						"delete from T_TERMINAL_ALARMSET where device_id in (:deviceIds)",
						map);

		List<TTerminalAlarmset> entities = new ArrayList<TTerminalAlarmset>(
				deviceIdArray.length);
		for (String deviceId : deviceIdArray) {
			TTerminalAlarmset tTerminalAlarmset = new TTerminalAlarmset(
					new TTerminal(deviceId), max);
			entities.add(tTerminalAlarmset);
		}
		int batchSave = tTerminalAlarmsetDao.batchSave(entities);
		logger.debug("batchSave TTerminalAlarmset return=" + batchSave
				+ "deviceIds's length=" + deviceIdArray.length);

		Map<String, String> params = new HashMap<String, String>();
		params.put("deviceId", deviceIds);
		params.put("name", "overspeedAlarm");
		params.put("max", String.valueOf(max));

		if (duration > 0) {
			params.put("duration", String.valueOf(duration));
		}

		boolean sendInstruction = terminalInstructionExecutor.sendInstruction(
				params, true);
		if (sendInstruction) {
			return "ok";
		} else {
			return null;
		}
	}

	@Override
	public String saveAreaAlarm(String deviceIds, int alarmruleAreaId) {
		TAlarmruleArea findById = tAlarmRuleAreaDao.findById(
				TAlarmruleArea.class, alarmruleAreaId);
		if (findById == null) {
			return null;
		}
		Set<RefAlarmruleAreaAlarmarea> refAlarmruleAreaAlarmareas = findById
				.getRefAlarmruleAreaAlarmareas();
		if (refAlarmruleAreaAlarmareas.size() == 0) {
			return null;
		}

		String[] deviceIdArray = StringUtil.split2StringArray(deviceIds, ";");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deviceIds", Arrays.asList(deviceIdArray));
		refTerminalAlarmruleAreaDao
				.updateWithSqlSupportList(
						"delete from REF_TERMINAL_ALARMRULE_AREA where device_id in (:deviceIds)",
						map);

		List<RefTerminalAlarmruleArea> entities = new ArrayList<RefTerminalAlarmruleArea>(
				deviceIdArray.length);
		for (String deviceId : deviceIdArray) {
			RefTerminalAlarmruleArea refTerminalAlarmruleArea = new RefTerminalAlarmruleArea(
					new TTerminal(deviceId), findById);
			entities.add(refTerminalAlarmruleArea);
		}
		refTerminalAlarmruleAreaDao.batchSave(entities);

		// 先全取消
		Map<String, String> params = new HashMap<String, String>();
		params.put("deviceId", deviceIds);
		params.put("name", "cancleArea");
		params.put("areaNo", "0");

		boolean sendInstruction = terminalInstructionExecutor.sendInstruction(
				params, false);

		if (!sendInstruction) {
			return null;
		}

		// 再设置
		for (RefAlarmruleAreaAlarmarea refAlarmruleAreaAlarmarea : refAlarmruleAreaAlarmareas) {
			// areaId, int alarmType, int areaNo, float overspeedThreshold
			TAlarmArea tAlarmArea = refAlarmruleAreaAlarmarea.getTAlarmArea();
			int alarmType = refAlarmruleAreaAlarmarea.getAlarmType();
			int areaType = 2;// 多边形
			int areaNo = refAlarmruleAreaAlarmarea.getAreaNo();
			Float overspeedThreshold = refAlarmruleAreaAlarmarea
					.getOverspeedThreshold();

			Map<String, String> _params = new HashMap<String, String>();
			_params.put("deviceId", deviceIds);
			_params.put("name", "areaAlarm");
			_params.put("areaType", String.valueOf(areaType));
			_params.put("alarmType", String.valueOf(alarmType));
			_params.put("points",
					JtsUtils.helpGeometry2Str(tAlarmArea.getXys()));
			// if (areaType == 3) {
			// params.put("radius", String.valueOf(radius));
			// }
			_params.put("areaNo", String.valueOf(areaNo));
			if (overspeedThreshold != null && overspeedThreshold != 0) {
				_params.put("maxSpeed", String.valueOf(overspeedThreshold));
			}

			sendInstruction = terminalInstructionExecutor.sendInstruction(
					_params, true);
		}

		return "ok";
	}

	@Override
	public String saveCancleOverspeed(String deviceIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deviceIds",
				Arrays.asList(StringUtil.split2StringArray(deviceIds, ";")));
		tTerminalAlarmsetDao
				.updateWithSqlSupportList(
						"delete from T_TERMINAL_ALARMSET where device_id in (:deviceIds)",
						map);

		Map<String, String> params = new HashMap<String, String>();
		params.put("deviceId", deviceIds);
		params.put("name", "cancleArea");
		params.put("areaNo", "0");
		params.put("alarmType", "2");

		boolean sendInstruction = terminalInstructionExecutor.sendInstruction(
				params, true);
		if (sendInstruction) {
			return "ok";
		} else {
			return null;
		}
	}

	@Override
	public String saveCancleArea(String deviceIds) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("deviceIds",
				Arrays.asList(StringUtil.split2StringArray(deviceIds, ";")));
		refTerminalAlarmruleAreaDao
				.updateWithSqlSupportList(
						"delete from REF_TERMINAL_ALARMRULE_AREA where device_id in (:deviceIds)",
						map);

		Map<String, String> params = new HashMap<String, String>();
		params.put("deviceId", deviceIds);
		params.put("name", "cancleArea");
		params.put("areaNo", "0");
		// params.put("alarmType", "0|1");

		boolean sendInstruction = terminalInstructionExecutor.sendInstruction(
				params, true);
		if (sendInstruction) {
			return "ok";
		} else {
			return null;
		}
	}

	@Override
	public String getStructions(String deviceId) {
		StringBuffer instructions = new StringBuffer();
		com.mapabc.gater.directl.encode.doog.GAODEFactory gaodeFactory = new com.mapabc.gater.directl.encode.doog.GAODEFactory();
		IAlarm createAlarm = gaodeFactory.createAlarm();
		// overspeed_threshold
		TTerminalAlarmset findById = tTerminalAlarmsetDao.findById(
				TTerminalAlarmset.class, deviceId);
		if (findById != null) {
			float overspeed_threshold = findById.getOverspeedThreshold();
			Request req = RequestHelp.generateRequestByOverspeedAlarm(deviceId,
					String.valueOf(overspeed_threshold));
			String instruction = createAlarm.overspeedAlarm(req);
			instructions.append(instruction);
		}

		// alarmrule
		String sql = "select ref2.alarm_type,ref2.area_no,ref2.overspeed_threshold,astext(a.xys) as xys from REF_TERMINAL_ALARMRULE_AREA ref"
				+ " left join REF_ALARMRULE_AREA_ALARMAREA ref2 on ref.alarmrule_area_id=ref2.alarmrule_area_id"
				+ " left join T_ALARM_AREA a on ref2.alarm_area_id=a.id"
				+ " where ref.device_id=?";
		List<Object> values = new ArrayList<Object>();
		values.add(deviceId);
		@SuppressWarnings("unchecked")
		List<Object[]> findListWithSql = (List<Object[]>) tTerminalAlarmsetDao
				.findListWithSql(sql, values.toArray());
		if (findListWithSql != null) {
			for (Object[] objects : findListWithSql) {
				String alarmType = String.valueOf(objects[0]);
				String areaNo = String.valueOf(objects[1]);
				String maxSpeed = String.valueOf(objects[2]);
				String points = null;
				if(objects[3] instanceof String){
					points = (String) objects[3];
				}else{
					points = new String((byte[]) objects[3]);
				}
				points = jta_polygon(killNull(points));
				Request req = RequestHelp.generateRequestByAreaAlarm(deviceId,
						areaNo, alarmType, maxSpeed, points);
				String instruction = createAlarm.areaAlarm(req);
				if (instructions.length() > 0) {
					instructions.append("@");
				}
				instructions.append(instruction);
			}
		}

		return instructions.toString();
	}

	private String jta_polygon(String s) {
		// POLYGON((116.222835487816 39.8911339121506,116.16078553185
		// 40.183149996357,116.32303309583 40.2151505127008,116.378427435023
		// 39.9241437011166,116.222835487816 39.8911339121506))
		s = s.trim();
		s = s.replace("POLYGON((", "");
		s = s.replace("))", "");
		s = s.replaceAll(",", "@");
		s = s.replaceAll(" ", ",");
		return s;
	}

	private String killNull(String s) {
		if (s == null || s.trim().equals("")) {
			s = "";
		}
		return s;
	}

}
