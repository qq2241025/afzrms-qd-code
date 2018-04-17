package com.dt.afzrms.util;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年3月26日 上午11:15:48
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class JtsUtils {
	public static String helpGeometry2Str(Geometry xys) {
		String xysStr = xys.toText();
		// POLYGON((30 10,10 20,20 40,40 40,30 10))
		String wktPointBegin = "POLYGON((";
		int len = wktPointBegin.length();
		xysStr = xysStr.substring(len + 1, xysStr.length() - 2);
		StringBuffer sb = new StringBuffer();
		String[] xysArray = StringUtil.split2StringArray(xysStr, ",");
		for (String xy : xysArray) {
			String str = xy.trim();
			int index = str.indexOf(" ");
			String x = str.substring(0, index);
			String y = str.substring(index + 1);
			if (sb.length() > 0) {
				sb.append("@");
			}
			sb.append(x);
			sb.append(",");
			sb.append(y);
		}

		return sb.toString();
	}

	public static Geometry helpStr2Geometry(String xys) {
		// POLYGON((30 10,10 20,20 40,40 40,30 10))
		String wktPointBegin = "POLYGON((";
		int len = wktPointBegin.length();
		StringBuffer sb = new StringBuffer(wktPointBegin);
		String[] xysArray = StringUtil.split2StringArray(xys, "@");
		for (String xy : xysArray) {
			String str = xy;
			int index = str.indexOf(",");
			String x = str.substring(0, index);
			String y = str.substring(index + 1);
			if (sb.length() > len) {
				sb.append(",");
			}
			sb.append(x);
			sb.append(" ");
			sb.append(y);
		}
		sb.append("))");
		return wktToGeometry(sb.toString());
	}

	private static Geometry wktToGeometry(String wktPoint) {
		WKTReader fromText = new WKTReader();
		Geometry geom = null;
		try {
			geom = fromText.read(wktPoint);
		} catch (ParseException e) {
			throw new RuntimeException("Not a WKT string:" + wktPoint);
		}
		return geom;
	}
}
