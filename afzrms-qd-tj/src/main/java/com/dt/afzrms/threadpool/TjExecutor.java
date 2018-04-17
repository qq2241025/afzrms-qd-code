package com.dt.afzrms.threadpool;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.TjService;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.util.ObjectUtil;

/**
 * @Title tj executor
 * @Description TODO
 * @author
 * @createDate 2015年4月6日 上午10:11:38
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class TjExecutor {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private TjService tjService;

	public boolean tj(final String deviceId, final Integer deptId, final Date tjDate) {
		final String logStart = "..........";
		final String logEnd = "..........";
		final String tjDateStr = DateUtil.dateTimeToStr(tjDate, Const.DATE_PATTERN);
		try {
			taskExecutor.execute(new Runnable() {

				@Override
				public void run() {
					Date beginDate = tjDate;
					Calendar c = Calendar.getInstance();
					c.setTime(tjDate);
					c.add(Calendar.DAY_OF_MONTH, 1);
					Date endDate = c.getTime();
					try {
						List<Object[]> findLocrecordList = tjService.findLocrecordList(deviceId, beginDate, endDate);
						logger.info(logStart + "tj executor:deviceId=" + deviceId + ",date=" + tjDate
								+ "locrecord size=" + findLocrecordList.size() + logEnd);
						if (findLocrecordList.size() == 0) {// offline
						} else {// online
							tjService.updateTjTermOnofflineAddOnlineCount(tjDate);
							logger.info(logStart + "update tj_term_onoffline over,deviceId=" + deviceId + logEnd);
							// 逻辑计算
							Object[] help = help(deviceId, tjDate, findLocrecordList);
							logger.info(logStart + "logic calculation over,deviceId=" + deviceId + logEnd);
							StringBuffer sb = new StringBuffer();
							for (Object object : help) {
								sb.append(object.toString());
								sb.append(" | ");
							}
							logger.info(logStart + sb.toString() + logEnd);
							// tj_term_operation
							Integer travelTime = ObjectUtil.obj2Float(help[2]).intValue();
							Integer engineRunningTime = ObjectUtil.obj2Float(help[3]).intValue();
							Float distance = ObjectUtil.obj2Float(help[4]);
							Float maxSpeed = ObjectUtil.obj2Float(help[5]);
							Date maxSpeedTime = ObjectUtil.obj2Date(help[6]);
							Float averageSpeed = ObjectUtil.obj2Float(help[7]);
							tjService.saveTjTermOperation(deviceId, tjDate, travelTime, engineRunningTime, distance,
									maxSpeed, averageSpeed, maxSpeedTime);
							logger.info(logStart + "save tj_term_operation over,deviceId=" + deviceId + logEnd);
							// tj_term_alarm
							Integer speedAlarmCount = ObjectUtil.obj2Integer(help[8]);
							Integer areaAlarmCount = ObjectUtil.obj2Integer(help[9]);
							Integer areaSpeedAlarmCount = ObjectUtil.obj2Integer(help[10]);
							tjService.saveTjTermAlarm(deviceId, tjDate, speedAlarmCount, areaAlarmCount,
									areaSpeedAlarmCount);
							logger.info(logStart + "save tj_term_alarm over,deviceId=" + deviceId + logEnd);
							// tj_dept_alarm
							tjService.updateTjDeptAlarmAdd(deptId, tjDate, speedAlarmCount, areaAlarmCount,
									areaSpeedAlarmCount);
							logger.info(logStart + "update tj_dept_alarm over,deviceId=" + deviceId + logEnd);
						}
					} catch (Exception e) {
						logger.error(logStart + "tj find locrecord list error.deviceId=" + deviceId + ",date="
								+ tjDateStr + logEnd, e);
					}
				}
			});
			return true;
		} catch (Exception e) {
			logger.error(logStart + "tj execute error.deviceId=" + deviceId + ",date=" + tjDateStr + logEnd, e);
			return false;
		}
	}

	private Object[] help(String deviceId, Date tjDate, List<Object[]> objs) {
		Float travel_time = 0F;// 行驶时间，单位分钟
		Float engine_running_time = 0F;// 发动机运行时间，单位分钟
		Float distance = 0f;// 里程，单位公里
		Float max_speed = 0f;// 最高车速，单位公里/小时
		Float average_speed = 0f;// 平均车速，单位公里/小时
		Date max_speed_time = null;// 最高车速时间

		Date _tempDateTime = null;
		Date _tempEngineRunningDateTime = null;
		Float _tempDistance = 0f;

		Integer speed_alarm_count = 0;// 超速报警次数
		Integer area_alarm_count = 0;// 区域报警次数
		Integer area_speed_alarm_count = 0;// 区域限速报警次数
		//
		Float totalSpeed = 0.0f; ///hzg ---add 
		int totalRecord = objs.size();
		for (Object[] objects : objs) {
			// Double _x = ObjectUtil.obj2Double(objects[0]);
			// Double _y = ObjectUtil.obj2Double(objects[1]);
			Float _speed = ObjectUtil.obj2Float(objects[2]);
			// Float _direction = ObjectUtil.obj2Float(objects[3]);
			// Float _height = ObjectUtil.obj2Float(objects[4]);
			Float _distance = ObjectUtil.obj2Float(objects[5]);
			Date _gps_time = (Date) objects[6];
			byte[] _device_status = ObjectUtil.obj2ByteArray(objects[7]);
			byte[] _alarm_type = ObjectUtil.obj2ByteArray(objects[8]);
			byte[] _alarm_sub_type = ObjectUtil.obj2ByteArray(objects[9]);
			// Integer _area_no = ObjectUtil.obj2Integer(objects[10]);
			// Float _speed_threshold = ObjectUtil.obj2Float(objects[11]);

			// 最高车速
			if (_speed != null && _speed > max_speed) {
				max_speed = _speed;
				max_speed_time = _gps_time;
			}
			// 行驶时间
			if (_speed != null && _speed > 3) {// 行驶中
				if (_tempDateTime == null) {// 上次未行驶
				} else {// 上次也在行驶
					travel_time += betweenMinutes(_gps_time, _tempDateTime);
				}
				_tempDateTime = _gps_time;
			} else {// 未行驶
				if (_tempDateTime != null) {// 上次也在行驶
					// TODO
				}
				_tempDateTime = null;
			}
			if(_speed!=null){
				totalSpeed +=_speed; //hzg ----add
			}
			// 发动机运行时间
			if (_device_status != null && _device_status.length > 1 && _device_status[0] == 1) {// accStatus
																								// on
				if (_tempEngineRunningDateTime == null) {// 上次未启动
				} else {// 上次已启动
					engine_running_time += betweenMinutes(_gps_time, _tempEngineRunningDateTime);
				}
				_tempEngineRunningDateTime = _gps_time;
			} else {// accStatus off
				if (_tempEngineRunningDateTime != null) {// 上次也在行驶
					// TODO
				}
				_tempEngineRunningDateTime = null;
			}
			// 里程
			distance = add(distance, _distance) ;
			if (_tempDistance == 0) {
				_tempDistance = _distance;
			}
			// if (_distance > _tempDistance) {
			// distance += _distance - _tempDistance;
			// _tempDistance = _distance;
			// }

			// 报警
			if (_alarm_type != null && _alarm_type.length > 0) {
				byte tempByte = _alarm_type[0];
				if ((tempByte & 1) == 1) {// speed
					speed_alarm_count++;
				}
				if ((tempByte & 2) == 2) {// area
					// TODO
					if (_alarm_sub_type != null && _alarm_sub_type.length > 0 && (_alarm_sub_type[0] & 2) == 2) {
						area_speed_alarm_count++;
					} else {
						area_alarm_count++;
					}
				}
			}
		}

		// 平均车速
		if (travel_time != 0) {
			//average_speed = (distance - _tempDistance) * 60 / travel_time;
			average_speed =  totalSpeed / totalRecord;
			System.out.println("平均车速============"+average_speed)  ;
		}

		// System.out.println(travel_time);
		// System.out.println(engine_running_time);
		// System.out.println(distance);
		// System.out.println(max_speed);
		// System.out.println(average_speed);
		// System.out.println();
		// System.out.println(speed_alarm_count);
		// System.out.println(area_alarm_count);
		// System.out.println(area_speed_alarm_count);
		return new Object[] { deviceId, tjDate, travel_time, engine_running_time, distance, max_speed, max_speed_time,
				average_speed, speed_alarm_count, area_alarm_count, area_speed_alarm_count };
	}

	private Float betweenMinutes(Date endTime, Date beginTime) {
		if (endTime == null || beginTime == null) {
			return 0F;
		}
		if (endTime.compareTo(beginTime) == 1) {
			long between = endTime.getTime() - beginTime.getTime();
			return new Float(between) / 60000f;
		} else {
			return 0F;
		}
	}
	//科学计数法
	public  float add(float v1, float v2) {
		BigDecimal b1 = new BigDecimal(Float.toString(v1));
		BigDecimal b2 = new BigDecimal(Float.toString(v2));
		return b1.add(b2).floatValue();
	}
}
