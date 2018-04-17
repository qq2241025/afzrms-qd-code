/**
 * 
 */
package com.mapabc.gater.directl.encode.guomai;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;

/**
 * @author shiguang.zhou
 * 
 */
public class GuoMaiControl extends ControlAdapter {
	private String centerId = "1";
	private String centerPwd = "";
	private String oemCode = "1";

	// 调度信息
	public String msg(Request req) {
		 
		
		String seq = Tools.getRandomString(2);
		String deviceId = req.getDeviceId();
		String content = RequestUtil.getReqData(req, "content");
		String hexCont = "";

		try {
			hexCont = Tools.bytesToHexString(content.getBytes("GB2312"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			//Log.getInstance().errorLog(null, e);
		}
		String ptlCont = hexCont;
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "15", ptlByte);
	 

		return hex;
	}

	// 油路控制
	public String oilElecControl(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String type = RequestUtil.getReqData(req, "type").equals("0")?"1":"0";
		String effectDate = RequestUtil.getReqData(req, "effectDate");

		String ptlCont = Tools.convertToHex(type, 2) + effectDate;
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, "19", ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 设置断油电指令：" + hex);

		return hex;
	}

	public String remoteControl(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String reset = RequestUtil.getReqData(req, "reset");
		String recover = RequestUtil.getReqData(req, "recover");
		String selfcheck = RequestUtil.getReqData(req, "selfcheck");

		String ptlNum = "";// 协议号
		String ptlCont = ""; // 参数内容

		if (recover != "" && reset != "") {// 复位，恢复出厂设置
			//req.setCmdType("307");
			//req.setCmdId("1c");
			
			ptlNum = "1c";
			ptlCont = recover + reset;
			int cs = Integer.parseInt(ptlCont, 2);
			ptlCont = Tools.int2Hexstring(cs, 2);
			
		}else if(recover =="" && reset !=""){
			ptlNum = "1c";
			ptlCont = "01";
			int cs = Integer.parseInt(ptlCont, 2);
			ptlCont = Tools.int2Hexstring(cs, 2);
		}else if(reset == "" && recover !=""){
			ptlNum = "1c";
			ptlCont = "10";
			int cs = Integer.parseInt(ptlCont, 2);
			ptlCont = Tools.int2Hexstring(cs, 2);
		}else {
			ptlNum = "1c";
			ptlCont = "00";
			int cs = Integer.parseInt(ptlCont, 2);
			ptlCont = Tools.int2Hexstring(cs, 2);
		}
		//Log.getInstance().guomaiLog("reset recover:"+ptlCont);
		if (selfcheck != "") {// 自检
			//req.setCmdType("308");
			//req.setCmdId("0f");
			ptlNum = "0f";
			if (selfcheck.equals("0")) {
				ptlCont = "77";
			} else if (selfcheck.equals("1")) {
				ptlCont = "55";
			} else if (selfcheck.equals("2")) {
				ptlCont = "66";
			}
			//Log.getInstance().guomaiLog("self check:"+ptlCont);
		}
		
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, ptlNum, ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 设置复位或恢复出厂设置或自检指令：" + hex);

		return hex;
	}

	public static void main(String[] args) {
		System.out.print(Tools.int2Hexstring(Integer.parseInt("10", 2), 2));
	}

}
