package com.mapabc.gater.directl.encode.doog;

import java.util.HashMap;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
 

public class GAODELocator extends LocatorAdapter {
	public String distanceInter(Request req) {
		//req.setCmdId("11");
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("$WZTREQ,");
        buffer.append("11,");
        buffer.append(req.getDeviceId() + ",");
        buffer.append("0,");
        
        buffer.append("(");
        buffer.append((String) req.getDatas().get("interval") + ",");
        buffer.append((String)req.getDatas().get("count"));
        buffer.append(")");
        
        buffer.append("#");

        String hex = Tools.bytesToHexString(buffer.toString().getBytes());

        return hex;
	}

	public String locate(Request req) {
		//req.setCmdId("10");
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("$WZTREQ,");
        buffer.append("14,");
        buffer.append(req.getDeviceId());
        buffer.append(")");
        
        buffer.append("#");

        String hex = Tools.bytesToHexString(buffer.toString().getBytes());

        return hex;
	}

	public String timeInter(Request req) {
		//req.setCmdId("10");
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("$WZTREQ,");
        buffer.append("10,");
        buffer.append(req.getDeviceId() + ",");
        buffer.append("0,");
        
        buffer.append("(");
        buffer.append((String) req.getDatas().get("interval") + ",");
        buffer.append((String)req.getDatas().get("count"));
        buffer.append(")");
        
        buffer.append("#");

        String hex = Tools.bytesToHexString(buffer.toString().getBytes());

        return hex;
	}

	public String timeLocate(Request req) {
		//req.setCmdId("12");
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("$WZTREQ,");
        buffer.append("12,");
        buffer.append(req.getDeviceId() + ",");
        buffer.append("0,");
        
        buffer.append("(");
        buffer.append(convertTime((String) req.getDatas().get("startTime")) + ",");
        buffer.append((String)req.getDatas().get("reportTime") + ",");
        buffer.append(convertTime((String)req.getDatas().get("deadTime")) + ",");
        buffer.append((String)req.getDatas().get("reportWeek"));
        buffer.append(")");
        
        buffer.append("#");

        String hex = Tools.bytesToHexString(buffer.toString().getBytes());

        return hex;
	}
	
	public String locateRecoup(Request req) {
		//req.setCmdId("13");
        StringBuffer buffer = new StringBuffer();
        
        buffer.append("$WZTREQ,");
        buffer.append("13,");
        buffer.append(req.getDeviceId() + ",");
        buffer.append(RequestUtil.getReqData(req, "date")+",");
        buffer.append(RequestUtil.getReqData(req, "time")+",");
        buffer.append(RequestUtil.getReqData(req, "y")+",");
        buffer.append(RequestUtil.getReqData(req, "x")+",");
        String posdesc = convert(RequestUtil.getReqData(req, "posdesc"));
        buffer.append(posdesc+",");
        buffer.append("0");//原始坐标
        buffer.append("#");
        System.out.println(buffer.toString());
        String hex = Tools.bytesToHexString(buffer.toString().getBytes());

        return hex;
	}
	
	public String convertTime(String time){
		return time.replace(",", "&cma;");
	}
	
	public String convert(String str) {
		if (null == str || str.equals("")) {
			return "";
		}
		str = str.replace("&", "&num;");
		str = str.replace("$", "&dol;");
		str = str.replace(",", "&cma;");
		str = str.replace("(", "&lps;");
		str = str.replace(")", "&rps;");
		str = str.replace("#", "&num;");
		return str;
	}
	
	public static void main(String[] args){ }
}
