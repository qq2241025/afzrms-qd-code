/**
 * 
 */
package com.mapabc.gater.directl.encode.tjzh;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.service.*;

/**
 * @author shiguang.zhou
 * 
 */
public class TjzhLocator extends LocatorAdapter {
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

	// 点名定位
	public String locate(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("30");
		buffer.append("0006");

		String deviceid = req.getDeviceId();
		String dsn = this.getFormatDeSN(deviceid);
		buffer.append(dsn);
		// 校验码
		byte verfyCode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		String vc = Tools.bytesToHexString(new byte[] { verfyCode });
		buffer.append(vc);
		buffer.append(end);

		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret =  buffer.toString().toUpperCase();

		return ret;
	}

	public String timeInter(Request req) {

		StringBuffer buffer = new StringBuffer();
		String count = RequestUtil.getReqData(req, "count");
		if (count.trim().length()==0)
			count = "0";
		int c = 0;
		try {
			c = Integer.parseInt(count);
		} catch (Exception e) {
			c = 0;
			//e.printStackTrace();
		}
		if (c <= 0 || c > 65535) {// 只按时
			return this.locateFrequence(req);
		}
		// 按时按次回传
		buffer.append(head);
		buffer.append("7b");
		buffer.append("000A");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));
		String interval = RequestUtil.getReqData(req, "interval");
		if (interval.trim().length()==0)
			interval = "0";
		String hexInter = Tools.convertToHex(interval, 4);
		String hexCount = Tools.convertToHex(count, 4);
		// 间隔
		buffer.append(hexInter);
		// 次数
		buffer.append(hexCount);
		// 校验
		byte verfyCode = Tools.checkData(Tools.fromHexString(buffer.toString()));
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
		buffer.append(this.getFormatDeSN(req.getDeviceId()));
		String interval = RequestUtil.getReqData(req, "interval");
		if (interval.trim().length()==0)
			interval = "0";
		String hexInter = Tools.convertToHex(interval, 4);

		// 间隔
		buffer.append(hexInter);

		// 校验
		byte verfyCode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { verfyCode }));

		buffer.append(end);
		String cmd = buffer.toString().toUpperCase();

		return cmd;
	}
	
	public String distanceInter(Request req) {
		 
        StringBuffer buffer = new StringBuffer();
        buffer.append(head);
		buffer.append("7C");
		buffer.append("0007");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));
		String dis = RequestUtil.getReqData(req, "interval");
		if (dis.trim().length()==0)
			dis = "0";
		int iDis = Integer.parseInt(dis);
		if (iDis > 255)
			dis = "255";
		String hexInter = Tools.convertToHex(dis, 2);
		// 间隔
		buffer.append(hexInter);

		// 校验
		byte verfyCode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { verfyCode }));

		buffer.append(end);
		String ret = buffer.toString().toUpperCase();//new String(cmd);

		return ret;
        
	}
	
	public static void main(String[] args){
		String d  = "1.1.1.1";
		TjzhLocator loc =  new TjzhLocator();
		System.out.println(Tools.convertToHex("10", 4));
	}

}
