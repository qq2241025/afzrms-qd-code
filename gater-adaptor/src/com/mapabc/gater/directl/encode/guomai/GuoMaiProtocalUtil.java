/**
 * 
 */
package com.mapabc.gater.directl.encode.guomai;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.mapabc.gater.directl.Base64;
import com.mapabc.gater.directl.CRC;
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools; 
import com.mapabc.gater.lbsgateway.GBLTerminalList;

/**
 * @author shiguang.zhou
 * 
 */
public class GuoMaiProtocalUtil {

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
		//
		// //Log.getInstance().guomaiLog(deviceId + "加密前（协议号+内容）:" + ptl);
		byte[] ptlBytes = Tools.fromHexString(ptl);
		// 加密
		byte[] ptlEncrytBytes = GuoMaiProtocalUtil.enCrypt(Key, ptlBytes);

		// //Log.getInstance().guomaiLog(
		// deviceId + "加密后（协议号+内容）:"
		// + Tools.bytesToHexString(ptlEncrytBytes));

		int enLen = ptlEncrytBytes.length;
		// 加密后协议长度
		String encLenHex = Tools.int2Hexstring(enLen, 4);

		tmpCmd += seqHex + oemHex + snHex + centerIdHex + centerPwdHex
				+ encLenHex + Tools.bytesToHexString(ptlEncrytBytes);
		// //Log.getInstance().guomaiLog(deviceId + "加密后协议：" + tmpCmd);

		// CRC校验
		int crcCode = CRC.CRC_CCITT(Tools.fromHexString(tmpCmd));
		String crcCodeHex = Tools.int2Hexstring(crcCode, 4);
		tmpCmd += crcCodeHex;
		// //Log.getInstance().guomaiLog(deviceId + "CRC校验后协议：" + tmpCmd);
		// 转义
		String escapPtl = GuoMaiProtocalUtil.escape(tmpCmd);
		// //Log.getInstance().guomaiLog(deviceId + "转义后协议：" + escapPtl);
		// Base64
//		String base64Bytes = null;
//		try {
//			base64Bytes = Base64.base64encode(Tools.fromHexString(escapPtl));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		tmpCmd =  Tools.bytesToHexString(base64Bytes.getBytes());
		byte[] base64Bytes = Base64.encode(Tools.fromHexString(escapPtl), 0);
		tmpCmd = Tools.bytesToHexString(base64Bytes);
 		
		 //Log.getInstance().guomaiLog(deviceId + "base64编码后协议：" + tmpCmd);

		ret = "7e" + tmpCmd + "7f";
		// //Log.getInstance().guomaiLog(deviceId + "构建的完整协议：" + ret);

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

	/**
	 * 对BASE64解密、转义、ACC校验后的数据进行解密
	 * 
	 * @param UpData
	 *            by shiguang.zhou
	 */
	public static byte[] gmEnCrypt(int key, byte[] UpData) {

		byte[] tmpUpdata = UpData;

		byte[] Content = new byte[tmpUpdata.length - 22];// 待加解密内容
		System.arraycopy(UpData, 19, Content, 0, Content.length);

		byte[] encByte = enCrypt(key, Content);// 加解密后数据
		System.arraycopy(encByte, 0, tmpUpdata, 19, encByte.length);

		return tmpUpdata;

	}

	public static byte[] enCrypt(int key, byte[] content) {

		int idx = 0;
		long M1 = 0;
		long IA1 = 0;
		long IC1 = 0;
		int tmpkey = 0;
		
		int leng = content.length;
		byte[] tmpcont = content;// new byte[leng];
		//"GP-UCSTC-GPRS";//
		String typeCode = GBLTerminalList.getInstance().getTermTypeCode(key + "");
		//Log.getInstance().guomaiLog(key+" typeCode="+typeCode);
		if (typeCode == null) {
			//Log.getInstance().guomaiLog(key + " 对应的设备类型为空!");
			return null;
		}
		if (typeCode.equals("GP-GUOMAIC-GPRS")) { //市标加解密参数
			M1 = Long.parseLong("F0F0F0F0", 16);
			IA1 = Long.parseLong("A0A0A0A0", 16);
			IC1 = Long.parseLong("C0C0C0C0", 16);
			tmpkey = Integer.parseInt("000007D9", 16);
		} else if (typeCode.equals("GP-GUOMAIP-GPRS")) {//地标加解密参数
			M1 = Long.parseLong("fafafafa", 16);
			IA1 = Long.parseLong("f7f7f7f7", 16);
			IC1 = Long.parseLong("f5f5f5f5", 16);
			 tmpkey = key;// 原因在此,如果long类型可能就有问题
		}else if(typeCode.equals("GP-UCSTC-GPRS")){
			M1 = Long.parseLong("F0F0F0F0", 16);
			IA1 = Long.parseLong("A0A0A0A0", 16);
			IC1 = Long.parseLong("C0C0C0C0", 16);
			tmpkey =  Integer.parseInt("000007D9", 16);
		}
		 

		if (key == 0)
			key = 1;

		while (idx < leng) {// 对协议号及协议内容统一加解密
			tmpkey = (int) (IA1 * (tmpkey % M1) + IC1);
			tmpcont[idx] ^= (byte) ((tmpkey >> 20) & 0xFF);
			idx++;
		}

		return tmpcont;
	}

	public static void main(String[] args) {// 5929 d49b
		String hex = "47010008030a33bc00000000000007d900028bec1849";
//		byte[] b= GuoMaiProtocalUtil.gmEnCrypt(51000252, Tools.fromHexString(hex),1);
//		System.out.println(Tools.bytesToHexString(b));
	}

}
