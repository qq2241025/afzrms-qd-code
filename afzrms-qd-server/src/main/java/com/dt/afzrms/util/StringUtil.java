package com.dt.afzrms.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年1月28日 下午2:25:15
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class StringUtil {
	public static Integer[] split2IntArray(String src, String delim) {
		String[] splitPreserveAllTokens = StringUtils.splitPreserveAllTokens(src, delim);
		List<Integer> temp = new ArrayList<Integer>(splitPreserveAllTokens.length);
		for (String string : splitPreserveAllTokens) {
			temp.add(Integer.parseInt(string));
		}
		Integer[] a = new Integer[splitPreserveAllTokens.length];
		return temp.toArray(a);
	}

	public static String[] split2StringArray(String src, String delim) {
		return StringUtils.splitPreserveAllTokens(src, delim);
	}

	public static Integer str2Integer(String string) {
		Integer re = null;
		try {
			re = Integer.parseInt(string);
		} catch (Exception e) {
		}
		return re;
	}

	public static Float str2Float(String string) {
		Float re = null;
		try {
			re = Float.parseFloat(string);
		} catch (Exception e) {
		}
		return re;
	}
}
