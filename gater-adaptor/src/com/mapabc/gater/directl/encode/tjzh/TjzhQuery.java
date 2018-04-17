/**
 * 
 */
package com.mapabc.gater.directl.encode.tjzh;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.QueryAdaptor;
import com.mapabc.gater.directl.encode.Request;
 

/**
 * @author shiguang.zhou
 *
 */
public class TjzhQuery extends QueryAdaptor {
	private String head = "2929";
	private String end = "0d";

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
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("31");
		buffer.append("0006");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));
  		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[]{vcode}));
		buffer.append(end);
		
		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret =  buffer.toString().toUpperCase();

		return ret;
	}
	
	public String queryVersion(Request req) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("3d");
		buffer.append("0006");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));
  		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[]{vcode}));
		buffer.append(end);
		
		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret =  buffer.toString().toUpperCase();

		return ret;
	}


}
