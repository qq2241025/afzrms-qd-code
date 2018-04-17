package com.dt.afzrms.threadpool;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.TjService;
import com.dt.afzrms.util.DateUtil;
import com.dt.afzrms.util.ObjectUtil;

/**
 * @Title tj service impl class
 * @Description TODO
 * @author
 * @createDate 2015年4月6日 上午10:19:45
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Component
public class ScheduleServiceImpl implements ScheduleService {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TjService tjService;
	@Autowired
	private Delete6MonthDataExecutor delete6MonthDataExecutor;
	@Autowired
	private TjExecutor tjExecutor;

	@Scheduled(cron = "0 30 0 * * ?")
	@Override
	public void tj() {
		String logStart = "..........";
		String logEnd = "..........";
		try {
			Calendar now = Calendar.getInstance();
			now.add(Calendar.DAY_OF_MONTH, -1);// 昨天
			now.set(Calendar.MILLISECOND, 0);
			now.set(Calendar.SECOND, 0);
			now.set(Calendar.MINUTE, 0);
			now.set(Calendar.HOUR_OF_DAY, 0);
			Date tjDate = now.getTime();
			String tjDateStr = DateUtil.dateTimeToStr(tjDate, Const.DATE_PATTERN);
			logger.info(logStart + "tj schedule start,tjDate=" + tjDateStr + logEnd);
			List<Object[]> findDeviceIdList = tjService.findDeviceIdList();
			if (findDeviceIdList != null && findDeviceIdList.size() > 0) {
				logger.info(logStart + "find used DeviceId size=" + findDeviceIdList.size() + logEnd);

				// init tj_dept_alarm
				tjService.saveTjDeptAlarmInit(tjDate);
				logger.info(logStart + "init tj_dept_alarm over" + logEnd);
				// init tj_term_onoffline
				tjService.saveTjTermOnofflineInit(tjDate, findDeviceIdList.size());
				logger.info(logStart + "init tj_term_onoffline over" + logEnd);

				for (Object[] objs : findDeviceIdList) {
					String deviceId = (String) objs[0];
					Integer deptId = ObjectUtil.obj2Integer(objs[1]);

					boolean tj = tjExecutor.tj(deviceId, deptId, tjDate);
					if (tj) {
						logger.info(logStart + "tj executor success.deviceId=" + deviceId + ",date=" + tjDateStr
								+ logEnd);
					} else {
						logger.error(logStart + "tj executor error.deviceId=" + deviceId + ",date=" + tjDateStr
								+ logEnd);
					}
				}
			} else {
				logger.error(logStart + "terminal list is null" + logEnd);
			}
		} catch (Exception e) {
			logger.error(logStart + "schedule execute error" + logEnd, e);
		}
	}
	
	@Scheduled(cron = "0/20 * *  * * ? ")
	@Override
	public void deleteEvery6MonthData() {
		delete6MonthDataExecutor.executeDeleteMonthDay();
	}
	
	@Scheduled(cron = "0/10 * *  * * ? ")
	@Override
	public void everyMonthPartTable() {
		delete6MonthDataExecutor.setExecuteEveryMonthPartTableTask();
	}
}
