package com.mapabc.gater.directl.encode.xinan;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.*;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;

public class XinanQuery extends QueryAdaptor {
	private String head = "2929";
	private String end = "0D";

	private String getFormatDeSN(String deviceid) {
		String dsn = "";
		if (deviceid != null && deviceid.trim().length() > 0) {// 此处待定
			String[] dsns = deviceid.split("\\.");
			for (int i = 0; i < dsns.length; i++) {
				dsn += Tools.convertToHex(dsns[i], 2);
			}
		}
		return dsn;
	}
	
	//查看车辆工作状态
	public String workStatus(Request req) {
		//Log.getInstance().logXinAn("xinan query workStatus...");
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("31");
		buffer.append("0006");
		buffer.append(req.getDeviceId());
  		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[]{vcode}));
		buffer.append(end);
		
		String ret =  buffer.toString().toUpperCase();
		//Log.getInstance().logXinAn("xinan query workStatus, ret : " + ret);
		return ret;
	}
	
	//查看车台版本信息
	public String versionInfo(Request req) {
		//Log.getInstance().logXinAn("xinan query version info...");
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("3D");
		buffer.append("0006");
		buffer.append(req.getDeviceId());
  		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[]{vcode}));
		buffer.append(end);
		
		String ret =  buffer.toString().toUpperCase();
		//Log.getInstance().logXinAn("xinan query version info, ret : "  + ret);
		return ret;
	}
}
