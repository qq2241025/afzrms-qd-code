package com.mapabc.gater.directl.encode.xinan;

import com.mapabc.gater.directl.Tools;

 

public class XinanTool {
	public static String getDeviceSN(String deviceId){
		StringBuffer sb = new StringBuffer();
		String dsn = "";
		String hexdsn = "";
		if (deviceId != null && deviceId.length() == 8) {
			for (int i = 0; i < deviceId.length(); i = i + 2) {
				dsn = deviceId.substring(i, i+2);
				hexdsn = Tools.convertToHex(dsn, 2);
				if(i == 2 || i == 4){
					String n1 = hexdsn.substring(0, 1);
					String n2 = hexdsn.substring(1, 2);
					hexdsn = hexAdd(n1, "8") + hexAdd(n2, "0");
				}
				sb.append(hexdsn);
			}
		}
		return sb.toString();
	}
	
	public static String hexAdd(String ha, String hb){
		int a = Integer.valueOf(ha,16);
		int b = Integer.valueOf(hb,16);
		return Tools.convertToHex(a + b + "", 1);
	}
	
	public static String hexSub(String ha, String hb){
		int a = Integer.valueOf(ha,16);
		int b = Integer.valueOf(hb,16);
		return Tools.convertToHex(a - b + "", 1);
	}
	
	public static String getDeviceId(String deviceSn){
		StringBuffer sb = new StringBuffer();
		String dsn = "";
		String hexdsn = "";
		if (deviceSn != null && deviceSn.length() == 8) {
			for (int i = 0; i < deviceSn.length(); i = i + 2) {
				hexdsn = deviceSn.substring(i, i+2);
				if(i == 2 || i == 4){
					String n1 = hexdsn.substring(0, 1);
					String n2 = hexdsn.substring(1, 2);
					hexdsn = hexSub(n1, "8") + hexAdd(n2, "0");
				}
				dsn = new String(Tools.fromHexString(hexdsn));
				sb.append(hexdsn);
			}
		}
		return sb.toString();
	}
	
	public static void main(String[] args){
		System.out.println("getDeviceSN : " + getDeviceSN("29819353"));
		System.out.println("" + Integer.parseInt("b1", 16));
		System.out.println("getDeviceId : " + getDeviceId("1dd1dd35"));
	}
}
