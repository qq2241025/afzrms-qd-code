package com.mapabc.gater.directl.encode.xinan;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;

 

public class XinanControl extends ControlAdapter{
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
	
	//车台关机复位
	public String reset(Request req) {
		//Log.getInstance().logXinAn("xinan reset...");
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("32");
		buffer.append("0006");
		buffer.append(req.getDeviceId());
  		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[]{vcode}));
		buffer.append(end);
		
		String ret =  buffer.toString().toUpperCase();
		//Log.getInstance().logXinAn("xinan reset, ret : " + ret);
		return ret;
	}
	
	// 油路控制
	public String oilElecControl(Request req) {
		//Log.getInstance().logXinAn("xinan oilElecControl...");
		StringBuffer buffer = new StringBuffer();
		buffer.append(head);

		String deviceid = req.getDeviceId();
		String cmdType = "";

		String type = RequestUtil.getReqData(req, "type");
		if (type.trim().length() > 0) {
			if (type.trim().equals("0")) {//恢复
				cmdType = "38";
			} else {//断开
				cmdType = "39";
			}
		}
		buffer.append(cmdType);
		buffer.append("0006");
		buffer.append(deviceid);

		byte verfyCode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		String vc = Tools.bytesToHexString(new byte[] { verfyCode });
		buffer.append(vc);

		buffer.append(end);
		String ret = buffer.toString().toUpperCase();
		//Log.getInstance().logXinAn("xinan oilElecControl, ret : " + ret);
		return ret;
	}
}
