package com.dt.afzrms.threadpool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.dt.afzrms.service.DeleteMonthDataService;

@Service
public class Delete6MonthDataExecutor {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	DeleteMonthDataService deleteMonthDataService;
	
	public void executeDeleteMonthDay(){
		logger.info("开始执行定时任务");
		taskExecutor.execute(new Runnable() {
             
			@Override
			public void run() {
				try {
					int month = 6;
					deleteMonthDataService.deleteMonthData(month);
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("执行定时任务出现异常");
				}
			}
		
		});
		logger.info("执行定时任务完毕");
	}
	
	
	
	public void setExecuteEveryMonthPartTableTask(){
		logger.info("*******************************每月执行定时分表***************bengin****************");
		taskExecutor.execute(new Runnable() {
             
			@Override
			public void run() {
				try {
					//deleteMonthDataService.setExecuteEveryMonthPartTableTask();
				} catch (Exception e) {
					e.printStackTrace();
					logger.info("*******************************每月执行定时异常***************executing****************");
				}
			}
		
		});
		logger.info("*******************************每月执行定时分表******************end*******************");
	}
}
