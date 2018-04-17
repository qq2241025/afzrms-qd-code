package com.mapabc.gater.directl.encode.xinan;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.AlarmAdaptor;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;



public class XinanAlarm extends AlarmAdaptor {
	private String head = "2929";
	private String end = "0D";
	
	//超速设置
	public String overspeedAlarm(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("3f");
		buffer.append("0007");
		buffer.append(req.getDeviceId());

		String speed = RequestUtil.getReqData(req, "max");
		if (speed.trim().length()==0)speed="0";
		int fs = 0;
		try {
			fs = (int)Float.parseFloat(speed);
		} catch (Exception e) {
			fs = 0;
			e.printStackTrace();
		}

		if (fs < 0)
			fs = 0;
		if (fs > 255)
			fs = 255;
		String hexSpeed = Tools.int2Hexstring(fs, 2);

		buffer.append(hexSpeed);
		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[]{vcode}));
		buffer.append(end);
		
		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret = buffer.toString().toUpperCase();//new String(cmd);

		return ret;
	}
	
	//取消报警
	public String cancelAlarm(Request req) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(head);
		buffer.append("37");
		buffer.append("0006");
		buffer.append(req.getDeviceId());
  		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[]{vcode}));
		buffer.append(end);
		
		String ret =  buffer.toString().toUpperCase();

		return ret;
	}
}
