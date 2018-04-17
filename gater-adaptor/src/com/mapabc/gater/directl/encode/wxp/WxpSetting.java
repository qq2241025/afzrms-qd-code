package com.mapabc.gater.directl.encode.wxp;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;



public class WxpSetting extends SettingAdaptor {

	public String smsCenterSetting(Request req) {
		String smscenter = null;

		smscenter = RequestUtil.getReqData(req, "number");

		String smscenterData = "";

		String ysim = "00000000000    ";
		String did = req.getDeviceId();
		while (did.length() < 15) {
			did = did + " ";
		}
		String mobile = Tools.bytesToHexString((did + ysim).getBytes());

		smscenterData = smscenter;
		while (smscenterData.length() < 22) {
			smscenterData = smscenterData + " ";
		}
		smscenterData = mobile
				+ Tools.bytesToHexString(smscenterData.getBytes())
				+ Tools.bytesToHexString("111111".getBytes());
		String smsptl = WxpUtil.crtSmsPtl("2700", "1070", "10", "3f",
				smscenterData);
		String ptl = WxpUtil.crtProtocal("03", "10", "83", smsptl);
		return ptl;

	}
	
	public String camera(Request req) {
		 

		String ysim = "               ";
		String did = req.getDeviceId();
		while (did.length() < 15) {
			did = did + " ";
		}
		String mobile = Tools.bytesToHexString((did + ysim).getBytes());

		String data = mobile;
		String pixel = RequestUtil.getReqData(req, "pixel");
		if (pixel.equals("7")){
			pixel = "010101";
		}else if (pixel.equals("8")){
			pixel = "010102";
		}else if (pixel.equals("9")){
			pixel = "010103";
		}else {
			pixel = "010101";
		}
		
		String channel = RequestUtil.getReqData(req, "chanel");
		StringBuffer sbuf = new StringBuffer("00000000");
		String[] chns = channel.split(",");
		for (int i=0; i<chns.length; i++){
			sbuf.replace(Integer.parseInt(chns[i]), Integer.parseInt(chns[i])+1, "1");
		}
		String bchannel = sbuf.reverse().toString();
		channel = "0201"+Tools.int2Hexstring(Integer.parseInt(bchannel, 2), 2);
		String quanlity = "030103";//低等质量
		String count = RequestUtil.getReqData(req, "count");
		 
		int c = Integer.parseInt(count);
		if(c<=0){
			count = "040101";
		}else if(c>10){
			count = "04010a";
		}else{
			count = "0401"+Tools.convertToHex(count, 2);
		}
		
		//data += "05" + pixel + channel + quanlity + count;
		data += "0501010102010F03010204010106050808080808";
		
		String smsptl = WxpUtil.crtSmsPtl("2700", "1070", "41", "01",
				data);
		String ptl = WxpUtil.crtProtocal("03", "04", "83", smsptl);
		return ptl;
	}
	
	public static void main(String[] args){
		String channel = "0,1,2";
		StringBuffer sbuf = new StringBuffer("00000000");
		String[] chns = channel.split(",");
		for (int i=0; i<chns.length; i++){
			sbuf.replace(Integer.parseInt(chns[i]), Integer.parseInt(chns[i])+1, "1");
		}
		String bchannel = sbuf.reverse().toString();
		System.out.println(bchannel);
		
	}
	

}
