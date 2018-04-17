/**
 * 
 */
package com.mapabc.gater.directl.encode.huaqiang;

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
public class HQLocator extends LocatorAdapter {
	 

	public String timeInter(Request req) {

		String hex = "";
		String t = RequestUtil.getReqData(req, "interval");
		int frequence = Integer.parseInt(t);

		String hexFrequence = Integer.toHexString(frequence);
		hexFrequence = HQ20Util.extentString(hexFrequence, 4).toUpperCase();

		String tmp = "BI" + hexFrequence + "FFFF";
		String cmd = HQ20Util.makeCommandStr(tmp, true, true);

		hex = Tools.bytesToHexString(cmd.getBytes());

		return hex;

	}

	public static void main(String[] args) {

	}

}
