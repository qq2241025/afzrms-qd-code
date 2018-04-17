
package com.mapabc.gater.listeners;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.mapabc.gater.directl.dbutil.DbOperation;

/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2015年8月25日 下午3:38:32
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * PS： 本想在数据库建立一个job 
 *  定时执行我的存储过程
	1.每个小时查询查询当前的小时刻和上一个小时刻的数据进行统计，
	2.将统计的结果插入到T_TJ_TREM_HOURS 这个表里面去，这样就将统计的结果小时制 放到 这个T_TJ_TREM_HOURS表里面去
	数据库的账号root 密码不知道是啥，开启不了事件了。目前就只能以这种方式
 */

public class BootTimerTask extends Thread{
	private static Logger log = Logger.getLogger(BootTimerTask.class) ;
	private int timer = 1 * 60 * 60 * 1000; 
	private Connection conn = null;
	public BootTimerTask(){
	}
	public BootTimerTask(int timer){
		this.timer = timer;
	}
	
	public static String getCurrentDateTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String s = df.format(new Date());
		return s;
	}
	
	private  void startMyTask() {  
        while (true) {
        	String date = getCurrentDateTime();
			log.info(date+ "---执行了一次定时任务");
			try {
				Thread.sleep(timer);
				doMyTask();
			} catch (Exception e) {
				log.error(date+ "---执行job出错");
				e.printStackTrace();
			}
		}
    } 
	
	private void doMyTask() throws SQLException{
		conn = DbOperation.getConnection();//
		CallableStatement pstmt = null;
		try {
			if(conn!=null){
				pstmt = conn.prepareCall("{Call PROC_ADD_TERM_HOURS()}");
				int  res= pstmt.executeUpdate();
				log.info("执行结果====="+res);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {  
           if (null != pstmt) {  
        	   pstmt.close();  
           }  
           if (null != conn) {  
               conn.close();  
           }  
       } 
	}
	
	
	
	
	public int getTimer() {
		return timer;
	}
	public void setTimer(int timer) {
		this.timer = timer;
	}
	@Override
	public void run() {
		String date = getCurrentDateTime();
		log.info(date+"------【开启执行timerTask】" );
		this.startMyTask();
	}
}

