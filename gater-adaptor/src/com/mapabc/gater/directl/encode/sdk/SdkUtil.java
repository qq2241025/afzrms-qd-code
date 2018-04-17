/**
 * 
 */
package com.mapabc.gater.directl.encode.sdk;

import com.mapabc.gater.directl.DES;
import com.mapabc.gater.directl.Tools;
 
/**
 * 
 * @author shiguang.zhou
 * 
 */
public class SdkUtil {

	/**
	 * 转义下发指令
	 * 
	 * @param hexString：下发的指令内容（16进制），不包含头尾
	 * @return by shiguang.zhou
	 */
	public static String escape(String hexString) {

		byte[] espBytes = Tools.fromHexString(hexString);
		byte[] espB = new byte[2];
		String hex = "";

		for (int i = 0; i < espBytes.length; i++) {

			if (espBytes[i] == (byte) 0x7d || espBytes[i] == (byte) 0xa6
					|| espBytes[i] == (byte) 0xd7 || espBytes[i] == (byte) 0x0d) {
				espB[1] = (byte) (espBytes[i] ^ (byte) 0x20);
				espB[0] = (byte) 0x7d;
				hex += Tools.bytesToHexString(espB);
			} else {
				hex += Tools.bytesToHexString(new byte[] { espBytes[i] });
			}
		}

		return hex;
	}

	// 反转义
	public static String resEscape(String hex) {

		String ret = "";
		 
		  byte[] bs = Tools.fromHexString(hex);

		for (int i = 0; i < bs.length; i++) {
			if (bs[i] == (byte) 0x7d
					&& (bs[i + 1] == (byte) 0x5d || bs[i + 1] == (byte) 0x86
							|| bs[i + 1] == (byte) 0xf7 || bs[i + 1] == (byte) 0x2d)) {

				byte tmpb = (byte) (bs[++i] ^ 0x20);

				ret += Tools.bytesToHexString(new byte[] { tmpb });

			} else {
				ret += Tools.bytesToHexString(new byte[] { bs[i] });
			}
		}
		// retb = Tools.fromHexString(ret);

		return ret;
	}

	/**
	 * DES加密
	 * 
	 * @param mingwen：明文
	 * @return 返回密文
	 * @author shiguang.zhou
	 */
	public static String desEncrypt(String mingwen) {
		String ret = "";

		DES des = new DES();

		ret = des.Encode(Tools.fromHexString(mingwen));

		return ret;
	}

	/**
	 * DES解密
	 * 
	 * @param miwen:密文
	 * @return 返回明文
	 * @author shiguang.zhou
	 */
	public static String desDecrypt(String miwen) {
		String ret = "";
		DES des = new DES();
		ret = des.DecodeBinay(miwen);
		return ret;
	}

	/**
	 * 构建协议
	 * 
	 * @param deviceId：设备ID
	 * @param ptlNo：协议号
	 * @param params：参数值
	 * @return
	 * @author shiguang.zhou
	 */
	public static String crtProtocal(String deviceId, String ptlNo,
			String params) {
		String ret = "";
		String head = "a6d7";
		String end = "0d";

		while (deviceId.length() < 16) {
			deviceId = deviceId + "f";
		}
		if (null == params)
			params = "";

		String cmd = deviceId + params;

		cmd = Tools.convertToHex(cmd.length() / 2 + "", 4) + cmd;

		cmd = ptlNo + cmd;

		String encrypt = SdkUtil.desEncrypt(cmd);

		String escape = SdkUtil.escape(encrypt);

		ret = head + escape + end;

		System.out.println("构建的协议为：" + ret + ",协议号：" + ptlNo);

		return ret;
	}

	public static String formatXY(String x) {
		String du = x.substring(0, x.indexOf("."));
		String hexdu = Tools.convertToHex(du, 2);
		String fat = x.substring(x.indexOf(".") + 1);
		if (fat.length() > 6) {
			fat = fat.substring(0, 6);
		} else {
			while (fat.length() < 6) {
				fat = fat + "0";
			}
		}
		
		return du+fat;
		
		
	}

	public static void main(String[] args) {
		String e = "a6d7";
		String s = SdkUtil.crtProtocal("13518309191", "14", "a6d70d7d");
		
		System.out.println(SdkUtil
				.resEscape(s));
		
		System.out.println(SdkUtil
				.desDecrypt("b1f8ffbdadd4d9adc8e0b49875b88686"));
	}

}
