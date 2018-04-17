/**
 * 
 */
package com.mapabc.gater.directl.encode.swyj;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.lbsgateway.TerminalTypeList;
import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;

/**
 * @author shiguang.zhou
 * 
 */
public class SwyjLocator extends LocatorAdapter {
	private String head = "*HQ,";
	private String end = "#";
	private String linkType = null;

	public String timeInter(Request req) {

		String hex = "";
		String ret = "";
	
		
		String hms = Tools.getCurHMS();
		String deviceType = req.getDeviceType();
		TerminalTypeBean typeBean = (TerminalTypeBean) TerminalTypeList
				.getInstance().get(deviceType);
		linkType = typeBean.getMtType();

		// *TH,000,D1,130305,5,4#
		String gpssn = req.getDeviceId();

		String t = RequestUtil.getReqData(req, "interval");
		String count = RequestUtil.getReqData(req, "count");
		if (count.equals("")){
			count = "1";
		}

		if (linkType != null && linkType.equals("0")) {
			ret = head + gpssn + ",D1," + hms + "," + t + "," + count + "#";
		} else {
			ret = head + gpssn + ",S17," + hms + "," + t + "," + count + "#";
		}
		//Log.getInstance().tianheLog(ret);
		hex = Tools.bytesToHexString(ret.getBytes());

		return hex;

	}

	public static void main(String[] args) {
		String d = "1.1.1.1";
		SwyjLocator loc = new SwyjLocator();
		System.out.println(Tools.convertToHex("10", 4));
	}

}
