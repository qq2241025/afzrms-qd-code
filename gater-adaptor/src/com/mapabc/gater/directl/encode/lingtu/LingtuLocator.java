/**
 * 
 */
package com.mapabc.gater.directl.encode.lingtu;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
 
 

/**
 * @author shiguang.zhou
 * 
 */
public class LingtuLocator extends LocatorAdapter {
	String head = "C M ";
	String end  = "\r\n";

	String cmdSeq = Tools.getRandomString(4);
	
	public String timeInter(Request req) {

		String ret = "";
		String time = "";
		RequestUtil.getDealRequest(req, "100", cmdSeq );

		String interval = RequestUtil.getReqData(req, "interval");
		int tt = Integer.parseInt(interval);
		time = Integer.toHexString(tt).toUpperCase();
		head = head + cmdSeq  + " ";

		String cmd = LingtuUtil.createOemCodeBySn(req.getDeviceId()) + ":"
				+ req.getDeviceId() + "|5|0;" + time + "!" + time + "; ";
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode + end;
		
		String hex = Tools.bytesToHexString(ret.getBytes());

		return hex;

	}
 
	public static void main(String[] args) {
		String d = "1.1.1.1";
		LingtuLocator loc = new LingtuLocator();
		System.out.println(Tools.convertToHex("10", 4));
	}

}
