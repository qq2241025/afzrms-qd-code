package com.mapabc.gater.directl.encode.sege;

import com.mapabc.gater.directl.Tools;

 


public class SegeUtil {
	
	public static int getReverseValue(byte b) {
		int ret = 0;
		ret = b ^ (byte) 0x50;
		return ret;
	}
	
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

			if (espBytes[i] == (byte) 0x5b || espBytes[i] == (byte) 0x5d
					|| espBytes[i] == (byte) 0x5c) {
				espB[1] = (byte) (espBytes[i] ^ (byte) 0x50);
				espB[0] = (byte) 0x5c;
				hex += Tools.bytesToHexString(espB); 
			} else {
				hex += Tools.bytesToHexString(new byte[] { espBytes[i] });
			}
		}

		return hex;
	}

	/**
	 * 反转义
	 * 
	 * @param hex
	 * @return by shiguang.zhou
	 */
	public static String reverseEscape(String hex) {
		byte[] retb = null;
		String ret = "";
		byte[] bs = Tools.fromHexString(hex);
		// byte[] bs = Tools.fromHexString(hex);

		for (int i = 0; i < bs.length; i++) {
			if (bs[i] == (byte) 0x5c
					&& (bs[i + 1] == (byte) 0x0b || bs[i + 1] == (byte) 0x0c || bs[i + 1] == (byte) 0x0d)) {

				byte tmpb = (byte) (bs[++i] ^ 0x50);

				ret += Tools.bytesToHexString(new byte[] { tmpb });

			} else {
				ret += Tools.bytesToHexString(new byte[] { bs[i] });
			}
		} 
		return ret;
	}

	public static void main(String[] args){
		String s = SegeUtil.escape("9630303030303030303030565b04412233949801135423900655562240474901134929120044200408230253550823030833019201651000004300364000838800000000000000000000000000000000000000000000000000000000000000000058");
		System.out.println(s);
	}

}
