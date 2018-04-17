/**
 * 
 */
package com.mapabc.gater.directl.encode.wxp;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;


/**
 * @author shiguang.zhou
 * 
 */
public class WxpControl extends ControlAdapter {

	// 调度信息
	public String msg(Request req) {
		String deviceId = req.getDeviceId();
		while (deviceId.length() < 15) {
			deviceId += " ";
		}
		// 源手机号码
		String ysim = "00000000000    ";

		String content = RequestUtil.getReqData(req, "content");

		String data = Tools.bytesToHexString((deviceId + ysim).getBytes());

		String shijian = "";
		try {
			// 车辆指示
			shijian = "02"
					+ "20"
					+ Tools.int2Hexstring(
							(content.getBytes("GB18030").length + 1), 2) + "21"
					+ Tools.bytesToHexString(content.getBytes("GB18030"));
			// 数据类型+信息类型+信息长度+信息内容
			// shijian ="04"+
			// "20"+Tools.int2Hexstring(content.getBytes("GB18030").length,
			// 2)+Tools.bytesToHexString(content.getBytes("GB18030"));

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 路由+信息
		String info = "43200122" + shijian;
		String code = getVerfityCodeFromBytes(Tools.fromHexString(info));
		info = code + info;
		info = Tools.int2Hexstring(info.length() / 2, 2) + info;
		info = WxpUtil.encodeDispatchMsg(Tools.fromHexString(info));

		String cmd = data + info;

		String userData = WxpUtil.crtSmsPtl("2700", "1070", "05", "01", cmd);
		String ret = WxpUtil.crtProtocal("03", "04", "83", userData);
		return ret;
	}

	public static String getVerfityCodeFromBytes(byte[] br) {
		String ret = "";

		int sum = (byte) 0;
		for (int i = 0; i < br.length; i++) {
			sum += br[i];
			sum = sum & 0xff;

		}
		ret = Tools.bytesToHexString(new byte[] { (byte) sum });

		return ret;
	}

	// 设置监听号码后，需要按劫警按钮才会回拨
	public String listen(Request req) {
		String deviceId = req.getDeviceId();
		while (deviceId.length() < 15) {
			deviceId += " ";
		}
		 
		// 源手机号码
		String ysim = "00000000000    ";

		String backNumber = RequestUtil.getReqData(req, "callBackNumber");
		String[] tels = backNumber.split(",");
		int count = tels.length;
		String calls = "";
		for (int i = 0; i < count; i++) {
			String tel = tels[i];
			while (tel.length() < 15) {
				tel = tel + " ";
			}
			calls += tel;
		}

		String data = Tools.bytesToHexString((deviceId + ysim).getBytes());
		data += Tools.int2Hexstring(count, 2)
				+ Tools.bytesToHexString(calls.getBytes());

		String userData = WxpUtil.crtSmsPtl("2700", "1070", "10", "03", data);
		String ret = WxpUtil.crtProtocal("03", "04", "83", userData);
		return ret;
	}

	public String remoteLoading(Request req) {
		String deviceId = req.getDeviceId();
		while (deviceId.length() < 15) {
			deviceId += " ";
		}
 
		// 源手机号码
		String ysim = "00000000000    ";
		String data = Tools.bytesToHexString((deviceId + ysim).getBytes());

		String type = RequestUtil.getReqData(req, "type");
		String ip = RequestUtil.getReqData(req, "ip");
		while (ip.length() < 15) {
			ip = ip + " ";
		}
		String port = RequestUtil.getReqData(req, "port");
		while (port.length() < 5) {
			port = port + " ";
		}
		String apn = RequestUtil.getReqData(req, "apn");
		while (apn.length() < 20) {
			apn = apn + " ";
		}
		String newVer = RequestUtil.getReqData(req, "newVer");

		String p = "01110111";
		String phex = Tools.int2Hexstring(Integer.parseInt(p, 2), 2);
		String param = phex + Tools.convertToHex(type, 2)
				+ Tools.bytesToHexString(apn.getBytes())
				+ Tools.bytesToHexString(ip.getBytes())
				+ Tools.bytesToHexString(port.getBytes())
				+ Tools.int2Hexstring(newVer.getBytes().length, 2)
				+ Tools.bytesToHexString(newVer.getBytes());
		data += param;
		String userData = WxpUtil.crtSmsPtl("2700", "1070", "10", "3a", data);
		String ret = WxpUtil.crtProtocal("03", "04", "83", userData);
		return ret;
	}

	public static void main(String[] args) { }

}
