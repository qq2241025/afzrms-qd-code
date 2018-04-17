package com.mapabc.gater.directl.encode.xinan;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
 
public class XinanLocator extends LocatorAdapter {
	private String head = "2929";
	private String end = "0D";

	// 点名定位
	public String locate(Request req) {
		//Log.getInstance().logXinAn("xinan locating");

		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("30");
		buffer.append("0006");

		String deviceid = req.getDeviceId();
//		String dsn = XinanTool.getDeviceSN(deviceid);
		buffer.append(deviceid);
		
		//Log.getInstance().logXinAn("xinan locate, buffer : " + buffer.toString());
		
		// 校验码
		byte verfyCode = Tools
				.checkData(Tools.fromHexString(buffer.toString()));
		//Log.getInstance().logXinAn("xinan locate, verfyCode : " + verfyCode);
		String vc = Tools.bytesToHexString(new byte[] { verfyCode });
		buffer.append(vc);
		buffer.append(end);

		String ret = buffer.toString().toUpperCase();
		//Log.getInstance().logXinAn("xinan locate, ret : " + ret);
		return ret;
	}

	public String timeInter(Request req) {

		StringBuffer buffer = new StringBuffer();
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
		buffer.append(head);
		buffer.append("34");
		buffer.append("0008");
		buffer.append(req.getDeviceId());
		String interval = RequestUtil.getReqData(req, "interval");
		if (interval.trim().length() == 0)
			interval = "0";
		String hexInter = Tools.convertToHex(interval, 4);
		String hexCount = Tools.convertToHex(count, 4);
		// 间隔
		buffer.append(hexInter);
		// 次数
		buffer.append(hexCount);
		// 校验
		byte verfyCode = Tools
				.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { verfyCode }));

		buffer.append(end);
		String cmd = buffer.toString().toUpperCase();

		return cmd;
	}

	// 频率设置
	private String locateFrequence(Request req) {
		StringBuffer buffer = new StringBuffer();
		// 按时按次回传
		buffer.append(head);
		buffer.append("34");
		buffer.append("0008");
		buffer.append(req.getDeviceId());
		String interval = RequestUtil.getReqData(req, "interval");
		if (interval.trim().length() == 0)
			interval = "0";
		String hexInter = Tools.convertToHex(interval, 4);

		// 间隔
		buffer.append(hexInter);

		// 校验
		byte verfyCode = Tools
				.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { verfyCode }));

		buffer.append(end);
		String cmd = buffer.toString().toUpperCase();

		return cmd;
	}

	public static void main(String[] args) {
		String d = "1.1.1.1";
		XinanLocator loc = new XinanLocator();
		System.out.println(Tools.convertToHex("10", 4));
	}
}
