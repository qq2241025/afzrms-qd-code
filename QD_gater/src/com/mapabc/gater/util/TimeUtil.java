package com.mapabc.gater.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年1月27日 下午4:34:16
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class TimeUtil {
	public static String changeToString(Date date, String strDatePatten) {
		if (date == null) {
			return "";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(strDatePatten);
		return dateFormat.format(date);
	}

	public static String changeToString(Date date) {
		return changeToString(date, "yyyy-MM-dd HH:mm:ss");
	}
	
	public static Date getDateAtferSeconds(Date date,int seconds){
        if(null == date){
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }
}
