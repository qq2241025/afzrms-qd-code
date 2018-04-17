/**
 * 
 */
package com.mapabc.gater.directl.encode.ucst;

import java.nio.ByteBuffer;

import com.mapabc.gater.directl.CRC;
import com.mapabc.gater.directl.Tools;

/**
 * @author shiguang.zhou
 * 
 */
public class UcstProtocalUtil {

	/**
	 * 构建下行协议
	 * 
	 * @param seq：循环码（序列号）
	 * @param oemCode：厂商代码
	 * @param deviceId：设备ID
	 * @param CenterId:运营管理中心ID
	 * @param centerPwd：运营管理中心密码
	 * @param protocalNo:协议号
	 * @param protocalCont：协议内容
	 * @return 加密、校验、转义、base64编码后的完整协议 by shiguang.zhou
	 */
	public static String createMtProtocal(String seq, String oemCode,
			String deviceId, String centerId, String centerPwd,
			String protocalNo, byte[] protocalCont) {
		String ret = "";
		String tmpCmd = "47";
		String seqHex = Tools.convertToHex(seq, 2);
		String oemHex = Tools.convertToHex(oemCode, 4);
		String snHex = Tools.convertToHex(deviceId, 8);
		String centerIdHex = Tools.convertToHex(centerId, 8);
		String centerPwdHex = Tools.convertToHex(centerPwd, 8);
		int Key = Integer.parseInt(deviceId);

		String ptl = Tools.bytesToHexString("D".getBytes()) + protocalNo;// 协议号+协议内容
		if (protocalCont != null && protocalCont.length > 0) {
			ptl += Tools.bytesToHexString(protocalCont);
		}
		byte[] ptlBytes = Tools.fromHexString(ptl);
		int enLen = ptlBytes.length;
		// 加密后协议长度
		String encLenHex = Tools.int2Hexstring(enLen, 4);

		tmpCmd += seqHex + oemHex + snHex + centerIdHex + centerPwdHex
				+ encLenHex + Tools.bytesToHexString(ptlBytes);
		// CRC校验
		//int crcCode = CRC.CRC_UCST(Tools.fromHexString(tmpCmd));
		String crcCodeHex = CRC.CRC_UCST(Tools.fromHexString(tmpCmd));
		tmpCmd += crcCodeHex;
		// 转义
		String escapPtl = UcstProtocalUtil.escape(tmpCmd);
		//Log.getInstance().ucstLog(deviceId + "转义后协议：" + escapPtl);
		ret = "7e" + escapPtl + "7f";
		//Log.getInstance().ucstLog(deviceId + " 下行的完整协议：" + ret);

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

			if (espBytes[i] == (byte) 0x7d || espBytes[i] == (byte) 0x7e
					|| espBytes[i] == (byte) 0x7f) {
				espB[1] = (byte) (espBytes[i] ^ (byte) 0x20);
				espB[0] = (byte) 0x7d;
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
	public static byte[] reverseEscape(byte[] bs) {
		byte[] retb = null;
		String ret = "";

		// byte[] bs = Tools.fromHexString(hex);

		for (int i = 0; i < bs.length; i++) {
			if (bs[i] == (byte) 0x7d
					&& (bs[i + 1] == (byte) 0x5d || bs[i + 1] == (byte) 0x5e || bs[i + 1] == (byte) 0x5f)) {

				byte tmpb = (byte) (bs[++i] ^ 0x20);

				ret += Tools.bytesToHexString(new byte[] { tmpb });

			} else {
				ret += Tools.bytesToHexString(new byte[] { bs[i] });
			}
		}
		retb = Tools.fromHexString(ret);

		return retb;
	}

	public static void main(String[] args) {// 5929 d49b
		String hex = "7e473000016b4a0fbe000000016b4a0fbe00155af1166f6950aab911c3252b2fd598be98726a93029dd07f";
		// UcstProtocalUtil.gmEnCrypt(1800015840, Tools.fromHexString(hex));

	}

}
