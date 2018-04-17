package com.dt.afzrms.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年2月10日 上午10:43:40
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class DateUtil {
	public static Date strToDateTime(String str, String datetimePattern) {
		Date date = null;
		if (str != null) {
			SimpleDateFormat df = new SimpleDateFormat(datetimePattern);
			try {
				date = df.parse(str);
			} catch (ParseException e) {
			}
		}
		return date;
	}

	public static Date strToDateTime(String str, String datetimePattern, Date defaultValue) {
		Date date = null;
		if (str != null) {
			SimpleDateFormat df = new SimpleDateFormat(datetimePattern);
			try {
				date = df.parse(str);
			} catch (ParseException e) {
				date = defaultValue;
			}
		}
		return date;
	}

	public static String dateTimeToStr(Date date, String datetimePattern) {
		String str = null;
		SimpleDateFormat df = new SimpleDateFormat(datetimePattern);
		if (date != null) {
			str = df.format(date);
		}
		return str;
	}
}
