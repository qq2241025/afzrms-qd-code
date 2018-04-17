/**
 * 
 */
package com.mapabc.gater.directl.encode.longhan;

import com.mapabc.gater.directl.Tools;

/**
 * @author shiguang.zhou
 * 
 */
public class LongHanUtil {
	private static String head = "2929";
	private static String end = "0d";

	// SIM卡到伪IP的转换
	public static String simToIp(String sim) {
		String ret = "";
		if (sim == null) {
			return null;
		}
		String simcard = sim.trim();

		if (simcard.length() != 11) {
			System.out.println("手机号码必须是11位");
			return null;
		}
		String subSim = simcard.substring(1);
		String preTwo = subSim.substring(0, 2);
		int ipreTwo = Integer.parseInt(preTwo);
		int twoC = 0;
		if (preTwo.startsWith("3")) {// 13段
			twoC = ipreTwo - 30;
		} else if (preTwo.startsWith("5")) {// 15段
			twoC = ipreTwo - 30 - 16;
		}
		String bStr = Integer.toBinaryString(twoC);

		while (bStr.length() < 8) {
			bStr = "0" + bStr;
		}

		String four = bStr.substring(4, 8);
		 
		String s1 = subSim.substring(2, 4);
		String s2 = subSim.substring(4, 6);
		String s3 = subSim.substring(6, 8);
		String s4 = subSim.substring(8, 10);

		String hexS1 = Tools.convertToHex(s1, 2);
		String hexS2 = Tools.convertToHex(s2, 2);
		String hexS3 = Tools.convertToHex(s3, 2);
		String hexS4 = Tools.convertToHex(s4, 2);

		String ip1 = "";
		if (four.charAt(0) == '1') {
			int field1 = Integer.parseInt(hexS1.substring(0, 1)) + 8;
			ip1 = Tools.convertToHex(field1 + "", 1) + hexS1.substring(1, 2);
		} else {
			ip1 = hexS1;
		}
		String ip2 = "";
		if (four.charAt(1) == '1') {
			int field1 = Integer.parseInt(hexS2.substring(0, 1)) + 8;
			ip2 = Tools.convertToHex(field1 + "", 1) + hexS2.substring(1, 2);
		} else {
			ip2 = hexS2;
		}
		String ip3 = "";
		if (four.charAt(2) == '1') {
			int field1 = Integer.parseInt(hexS3.substring(0, 1)) + 8;
			ip3 = Tools.convertToHex(field1 + "", 1) + hexS3.substring(1, 2);
		} else {
			ip3 = hexS3;
		}
		String ip4 = "";
		if (four.charAt(3) == '1') {
			int field1 = Integer.parseInt(hexS4.substring(0, 1)) + 8;
			ip4 = Tools.convertToHex(field1 + "", 1) + hexS4.substring(1, 2);
		} else {
			ip4 = hexS4;
		}

		ret = ip1 + ip2 + ip3 + ip4;

		return ret;
		//return sim;
	}

	// 伪IP到SIM的转换,反解析不出15段号码
	public static String ipToSim(String highHex, String ip) {
		String ret = "";
		// 8C 22 B8 CE
		String ip1 = ip.substring(0, 2);
		String ip2 = ip.substring(2, 4);
		String ip3 = ip.substring(4, 6);
		String ip4 = ip.substring(6, 8);

		int ip1_bit1 = Integer.parseInt(ip1.substring(0, 1), 16);
		int ip2_bit1 = Integer.parseInt(ip2.substring(0, 1), 16);
		int ip3_bit1 = Integer.parseInt(ip3.substring(0, 1), 16);
		int ip4_bit1 = Integer.parseInt(ip4.substring(0, 1), 16);

		String flag = "";
		String sim1 = "";
		String sim2 = "";
		String sim3 = "";
		String sim4 = "";

		if (ip1_bit1 >= 8) {
			int ib = ip1_bit1 - 8;
			flag += "1";
			sim1 = Tools.convertToHex(ib + "", 1) + ip1.substring(1);
		} else {
			flag += "0";
			sim1 = ip1;
		}
		if (ip2_bit1 >= 8) {
			int ib = ip2_bit1 - 8;
			flag += "1";
			sim2 = Tools.convertToHex(ib + "", 1) + ip2.substring(1);
		} else {
			flag += "0";
			sim2 = ip2;
		}
		if (ip3_bit1 >= 8) {
			int ib = ip3_bit1 - 8;
			flag += "1";
			sim3 = Tools.convertToHex(ib + "", 1) + ip3.substring(1);
		} else {
			flag += "0";
			sim3 = ip3;
		}
		if (ip4_bit1 >= 8) {
			int ib = ip4_bit1 - 8;
			flag += "1";
			sim4 = Tools.convertToHex(ib + "", 1) + ip4.substring(1);
		} else {
			flag += "0";
			sim4 = ip4;
		}

		String subSim1 = Integer.parseInt(sim1, 16) + "";
		String subSim2 = Integer.parseInt(sim2, 16) + "";
		String subSim3 = Integer.parseInt(sim3, 16) + "";
		String subSim4 = Integer.parseInt(sim4, 16) + "";
		while (subSim1.length() < 2) {
			subSim1 = "0" + subSim1;
		}
		while (subSim2.length() < 2) {
			subSim2 = "0" + subSim2;
		}

		while (subSim3.length() < 2) {
			subSim3 = "0" + subSim3;
		}
		while (subSim4.length() < 2) {
			subSim4 = "0" + subSim4;
		}
		 
		int simHead = 0;
		if (flag.equals("1100") || flag.equals("1101"))
			simHead = Integer.parseInt(flag, 2) + 30 + 16;
		else
			simHead = Integer.parseInt(flag, 2) + 30;

		String subSim = subSim1 + subSim2 + subSim3 + subSim4;// SIM后8位

		// int highLen = Integer.parseInt(highHex, 16);
		// String binaStr = Integer.toBinaryString(highLen);//协议长度字段高字节
		//		
		// String binaFour = binaStr.substring(0,4);
		// String high = Integer.toHexString(Integer.parseInt(binaFour, 2));
		//		
		// int preT = Integer.parseInt(flag, 2);
		//		
		// String stwo = Integer.toHexString(preT);
		//		
		// System.out.println(Integer.parseInt(stwo, 16)+","+subSim);
		ret = "1" + simHead + subSim;
		return ret;
	}

	public static String formatLngLat2DF(String xy, int blen,int type) {
		String ret = "";
		double p = Double.parseDouble(xy);
		int du = (int) p;
		double dfen = p - du;
		double fen = dfen * 60;
		String sfen = "";
		if (type==0){
			sfen = Tools.getNumberFormatString(fen, 4, 4);
		}else{
			sfen = Tools.getNumberFormatString(fen, 3, 3);
		}
		ret = du + sfen.replaceAll("\\.", "");
		while (ret.length() < blen) {
			ret = "0" + ret;
		}
		return ret;
	}

	public static String makeProtocal(String deviceId, String ptlNo, int leng,
			String dataHex) {
		String ret = "";
		if (deviceId.length()!=11){
			//Log.getInstance().longhanLog("下发的设备ID必须为11位手机号,错误号码："+deviceId);
			return null;
		}
		if (dataHex == null)
			dataHex = "";
//		TTerminal term = GBLTerminalList.getInstance().getTerminaInfo(deviceId);
//		String simcard = term.getSimcard();
		 
		
		ret = head + ptlNo + Tools.int2Hexstring(leng, 4) + simToIp(deviceId)+ dataHex;

		byte verfyCode = Tools.checkData(Tools.fromHexString(ret));

		String vc = Tools.bytesToHexString(new byte[] { verfyCode });

		ret += vc + end;
		
		//Log.getInstance().longhanLog(deviceId+"下行指令为："+ret);

		return ret;
	}

	public static void main(String[] args) {
		 
		String s = "00000000000";
		System.out.println(LongHanUtil.simToIp(s));
		String ip = "9120372e";
		System.out.println(LongHanUtil.ipToSim(null, ip));
		// 2929 80 0028 8ac65b49 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
		// 00 00 18 00 00 00 1b fc 5f 81 00 05 ff 3c 00 00 ff 00 ee 0d
		// 13474139908
		// 74.141.99.8

	}

}
