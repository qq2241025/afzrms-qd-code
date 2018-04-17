
package com.dt.afzrms.util;

import javax.servlet.http.HttpServletRequest;

/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2015年1月20日 上午11:47:23
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */

public class NetUtil {
	public static String getRemoteAddress(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}
}
