/**
 * 
 */
package com.mapabc.gater.directl.encode.longhan;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;


/**
 * @author shiguang.zhou
 * 
 */
public class LongHanLocator extends LocatorAdapter {
	 

	// 点名定位
	public String locate(Request req) {
 

		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "30", 6, "");
		return ret;
	}

	public String timeInter(Request req) {

		String count = RequestUtil.getReqData(req, "count");
		if (count.trim().length() == 0)
			count = "0";
		int c = 0;
		try {
			c = Integer.parseInt(count);
		} catch (Exception e) {
			c = 0;
			// e.printStackTrace();
		}
		if (c <= 0 || c > 65535) {// 只按时
			return this.locateFrequence(req);
		}
		// 按时按次回传

		String interval = RequestUtil.getReqData(req, "interval");
		if (interval.trim().length() == 0)
			interval = "0";
		String hexInter = Tools.convertToHex(interval, 4);
		String hexCount = Tools.convertToHex(count, 4);

		 

		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "7b", 10, hexInter
				+ hexCount);
		return ret;
	}

	// 频率设置
	private String locateFrequence(Request req) {

		String interval = RequestUtil.getReqData(req, "interval");
		if (interval.trim().length() == 0)
			interval = "0";
		String hexInter = Tools.convertToHex(interval, 4);
 

		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "34", 8, hexInter);
		return ret;
	}

	public static void main(String[] args) {
		String d = "1.1.1.1";
		LongHanLocator loc = new LongHanLocator();
		System.out.println(Tools.convertToHex("10", 4));
	}

}
