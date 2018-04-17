package com.dt.afzrms.util;

import java.util.Date;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年4月8日 下午8:47:37
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class ObjectUtil {
	public static Integer obj2Integer(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Integer) {
			return (Integer) obj;
		} else {
			try {
				return Integer.parseInt(obj.toString());
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static Float obj2Float(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Float) {
			return (Float) obj;
		} else {
			try {
				return Float.parseFloat(obj.toString());
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static Double obj2Double(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Double) {
			return (Double) obj;
		} else {
			try {
				return Double.parseDouble(obj.toString());
			} catch (Exception e) {
				return null;
			}
		}
	}

	public static byte[] obj2ByteArray(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof byte[]) {
			return (byte[]) obj;
		} else {
			return null;
		}
	}

	public static Date obj2Date(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof Date) {
			return (Date) obj;
		} else {
			return null;
		}
	}
}
