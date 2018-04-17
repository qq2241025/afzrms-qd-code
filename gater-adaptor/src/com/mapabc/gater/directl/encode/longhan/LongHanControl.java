/**
 * 
 */
package com.mapabc.gater.directl.encode.longhan;

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
public class LongHanControl extends ControlAdapter {

	// 调度信息
	public String msg(Request req) { 
		String content = RequestUtil.getReqData(req, "content");
		String hexCont = "";
		String deviceType = req.getDeviceType();
		String deviceId = req.getDeviceId();

		try {
			if (deviceType != null && deviceType.equals("GP-LH-HK-GPRS")) {
				  
				String msgNum = "1234567890";// Tools.getRandomString(10);
				String replyFlag = "1";
				content = RequestUtil.getReqData(req, "content");
				 

				String cmd = Tools.bytesToHexString(msgNum.getBytes()) + "0"
						+ replyFlag;

				int contentLen = 0;
				try {
					contentLen = content.getBytes("gb18030").length;
					cmd += "00"
							+ Tools
									.bytesToHexString(content
											.getBytes("gb18030"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				int len = 18 + contentLen;
				String ret = LongHanUtil.makeProtocal(req.getDeviceId(), "f3",
						len, cmd);
			 
				return ret;
			} else {
				hexCont = Tools.bytesToHexString(content.getBytes("GB2312"));
			 

				int length = 6 + content.getBytes().length;

				String ret = "";
				ret = LongHanUtil.makeProtocal(deviceId, "3A", length, hexCont);
				return ret;
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.getInstance().errorLog("字符编码异常", e);
		}
		return null;
		
	}

	// 油路控制
	public String oilElecControl(Request req) {

		String cmdType = "";

		String type = RequestUtil.getReqData(req, "type");
		if (type.trim().length() > 0) {
			if (type.trim().equals("1")) {// 恢复
				cmdType = "38";
			} else {// 断开
				cmdType = "39";
			}
		}

 

		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), cmdType, 6, null);
		return ret;
	}

	// 锁门控制
	public String switchDoorControl(Request req) {

		String cmdType = "";

		String type = RequestUtil.getReqData(req, "type");
		if (type.trim().length() > 0) {
			if (type.trim().equals("0")) {
				cmdType = "68";
			} else {
				cmdType = "67";
			}
		} 

		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), cmdType, 6, null);
		return ret;
	}

	// 通话限制
	public String callRestrict(Request req) { 
		String type = RequestUtil.getReqData(req, "type");

		String ptlCont = Tools.convertToHex(type, 2);

		if (type.equals("4")) {
			ptlCont = "00";
		}
		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "24", 7, ptlCont);
		return ret;
	}

	// 监听
	public String listen(Request req) { 
		String backNumber = RequestUtil.getReqData(req, "callBackNumber");
		if (backNumber.length() <= 0) {
			//Log.getInstance().longhanLog("监听号码为空。");
			return null;
		}
		if (backNumber.length() % 2 == 0) {
			backNumber += "FF";
		} else {
			backNumber += "F";
		}
		String deviceType = req.getDeviceType();

		if (deviceType.equals("GP-LH-HK-GPRS"))
			backNumber += "FFFFFF";

		int leng = backNumber.length() / 2 + 6;

		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "3e", leng,
				backNumber);
		return ret;
	}

	// 远程升级请求
	public String remoteLoading(Request req) { 
		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "62", 6, "");
		return ret;

	}

	public String reset(Request req) { 
		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "32", 6, "");
		return ret;
	}

	public String wayBillOptControl(Request req) { 
		String optType = Tools.int2Hexstring(Integer.parseInt(RequestUtil.getReqData(
				req, "optType")), 2);
		String wayBillNum = RequestUtil.getReqData(req, "wayBillNum");
		String wayBillState = Tools.int2Hexstring(Integer.parseInt(RequestUtil
				.getReqData(req, "wayBillState")), 2);
		String ccrnNum = RequestUtil.getReqData(req, "ccrnNum");
		String bindState = Tools.int2Hexstring(Integer.parseInt(RequestUtil
				.getReqData(req, "bindState")), 2);
		String date = RequestUtil.getReqData(req, "date");
		String lockState = Tools.int2Hexstring(Integer.parseInt(RequestUtil
				.getReqData(req, "lockState")), 2);
		// String encoding = RequestUtil.getReqData(req, "encoding");
		String details = RequestUtil.getReqData(req, "details");

		while (wayBillNum.length() < 12) {
			wayBillNum = "0" + wayBillNum;
		}
		while (ccrnNum.length() < 20) {
			ccrnNum = "0" + ccrnNum;
		}
		if (optType.equals("02")) {
			String cmd = Tools.bytesToHexString(wayBillNum.getBytes());
			String ret = LongHanUtil.makeProtocal(req.getDeviceId(), "f1", 18,
					cmd); 

			return ret;
		}

		String cmd = optType + Tools.bytesToHexString(wayBillNum.getBytes())
				+ wayBillState + Tools.bytesToHexString(ccrnNum.getBytes())
				+ bindState + Tools.bytesToHexString(date.getBytes())
				+ lockState;
		int detaLen = 0;
		try {
			detaLen = details.getBytes("gb18030").length;
			cmd += "00" + Tools.bytesToHexString(details.getBytes("gb18030"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int len = 6 + 45 + detaLen;

		String ret = LongHanUtil
				.makeProtocal(req.getDeviceId(), "f0", len, cmd);
 
		return ret;
	}

	public String wayBillMsg(Request req) {
		 

		String msgNum = RequestUtil.getReqData(req, "msgNum");
		String replyFlag = RequestUtil.getReqData(req, "replyFlag");
		// String encoding = RequestUtil.getReqData(req, "encoding");
		String content = RequestUtil.getReqData(req, "content");
		while (msgNum.length() < 10) {
			msgNum = "0" + msgNum;
		}

		String cmd = Tools.bytesToHexString(msgNum.getBytes()) + "0"
				+ replyFlag;

		int contentLen = 0;
		try {
			contentLen = content.getBytes("gb18030").length;
			cmd += "00" + Tools.bytesToHexString(content.getBytes("gb18030"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int len = 18 + contentLen;
		String ret = LongHanUtil
				.makeProtocal(req.getDeviceId(), "f3", len, cmd);
 
		return ret;
	}

	public String wayBillOptReply(Request req) { 

		String wayBillNum = RequestUtil.getReqData(req, "wayBillNum");
		String optRequest = RequestUtil.getReqData(req, "optRequest");
		String result = RequestUtil.getReqData(req, "result");
		while (wayBillNum.length() < 12) {
			wayBillNum = "0" + wayBillNum;
		}

		String cmd = Tools.bytesToHexString(wayBillNum.getBytes()) + "0"
				+ optRequest + "0" + result; 
		return LongHanUtil.makeProtocal(req.getDeviceId(), "f5", 20, cmd);
	}

	// 修改运单状态
	public String wayBillStateUpdate(Request req) { 
		String deviceid = req.getDeviceId();
		String wayBillNum = RequestUtil.getReqData(req, "wayBillNum");
		while (wayBillNum.length() < 12) {
			wayBillNum = "0" + wayBillNum;
		}
		String state = RequestUtil.getReqData(req, "state");
		String hexCont = Tools.bytesToHexString(wayBillNum.getBytes()) + "0"
				+ state;
		return LongHanUtil.makeProtocal(req.getDeviceId(), "fd", 18, hexCont);
	}

	public static void main(String[] args) {
		try {
			byte[] gbk = "繁體".getBytes("GBK");
			byte[] big5 = "繁體".getBytes("GB18030");

			System.out.println(new String(Tools.fromHexString("30")));
			System.out.println(new String(big5, "gbk"));

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
