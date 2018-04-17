package com.mapabc.gater.directl.parse.wxp;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools; 

 

public class WxpUtil {
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(WxpUtil.class);
	public final static byte[][] encodeMap = {
			{ (byte) 0x2E, (byte) 0x2C, (byte) 0x30, (byte) 0x31, (byte) 0x32,
					(byte) 0x33, (byte) 0x34, (byte) 0x35 },
			{ (byte) 0x36, (byte) 0x37, (byte) 0x38, (byte) 0x39, (byte) 0x41,
					(byte) 0x42, (byte) 0x43, (byte) 0x44 },
			{ (byte) 0x45, (byte) 0x46, (byte) 0x47, (byte) 0x48, (byte) 0x49,
					(byte) 0x4a, (byte) 0x4b, (byte) 0x4c },
			{ (byte) 0x4d, (byte) 0x4e, (byte) 0x4f, (byte) 0x50, (byte) 0x51,
					(byte) 0x52, (byte) 0x53, (byte) 0x54 },
			{ (byte) 0x55, (byte) 0x56, (byte) 0x57, (byte) 0x58, (byte) 0x59,
					(byte) 0x5a, (byte) 0x61, (byte) 0x62 },
			{ (byte) 0x63, (byte) 0x64, (byte) 0x65, (byte) 0x66, (byte) 0x67,
					(byte) 0x68, (byte) 0x69, (byte) 0x6a },
			{ (byte) 0x6b, (byte) 0x6c, (byte) 0x6d, (byte) 0x6e, (byte) 0x6f,
					(byte) 0x70, (byte) 0x71, (byte) 0x72 },
			{ (byte) 0x73, (byte) 0x74, (byte) 0x75, (byte) 0x76, (byte) 0x77,
					(byte) 0x78, (byte) 0x79, (byte) 0x7a } };

	public static String crtProtocal(String verHex, String prority,
			String type, String data) {
		String ret = "";
		String head = "7e", end = "7e";
		String codeInit = "00";

		ret = verHex + codeInit + prority + type + data;

		String vertifyCode = "";
		vertifyCode = getVerfityCodeFromBytes(Tools.fromHexString(ret));
		log.info("下行gprs帧校验码：" + vertifyCode);
		//		
		ret = verHex + vertifyCode + prority + type + data;

		ret = head + WxpUtil.escape(ret) + end;

		log.info("危险品协议下行指令：" + ret);
		return ret;
	}

	public static String crtImageProtocal(String verHex, String prority,
			String type, String data) {
		String ret = "";
		String head = "7e", end = "7e";
		String codeInit = "00";

		ret = verHex + codeInit + prority + type + data;

		String vertifyCode = "";
		vertifyCode =  getVerfityCodeFromBytes(Tools.fromHexString(ret));
		log.info("下行gprs帧校验码：" + vertifyCode);
		//		
		ret = verHex + vertifyCode + prority + type + data;

		ret = head + ret + end;

		log.info("image危险品协议下行指令：" + ret);
		return ret;
	}
	
	// SMS帧
	public static String crtSmsPtl(String areaNo, String ver, String btype,
			String dataType, String userData) {
		String ret = "";

		String cmd = areaNo + ver + btype + dataType + userData;
		cmd = singleCharEscape(cmd);

		String code = getVerfityCodeFromBytes(Tools.fromHexString(cmd));
		if (code.length() < 2)
			code = "0" + code;
		if (code.equals("00")) {
			code = "7f";
		}

		log.info("下行SMS帧校验码：" + code);

		cmd += code;

		ret = cmd;

		return ret;
	}
	
	// SMS帧
	public static String crtImgageSmsPtl(String areaNo, String ver, String btype,
			String dataType, String userData) {
		String ret = "";

		String cmd = areaNo + ver + btype + dataType ;
		 
		cmd = cmd + userData;

		String code = getVerfityCodeFromBytes(Tools.fromHexString(cmd));
		if (code.length() < 2)
			code = "0" + code; 
		cmd += code;

		ret = cmd;

		return ret;
	}

	public static String getVerfityCodeFromBytes(byte[] br) {
		String ret = "";

		int sum = 0;
		for (int i = 0; i < br.length; i++) {
			sum += br[i];
			sum = sum & 0xff;
			if (sum < (br[i])) {
				sum++;
			}
		}
		sum &= 0x7f;
		if (sum == 0x00) {
			sum = 0x7f;
		}
		ret = Integer.toHexString(sum);

		return ret;
	}

	// 转移0X00
	public static String singleCharEscape(String dataHex) {

		byte[] espBytes = Tools.fromHexString(dataHex);
		byte[] espB = new byte[2];
		String hex = "";

		for (int i = 0; i < espBytes.length; i++) {

			if (espBytes[i] == (byte) 0x00) {

				hex += "7f";
			} else {
				hex += Tools.bytesToHexString(new byte[] { espBytes[i] });
			}
		}
		return hex;
	}

	// 反转移0X00
	public static String revSigleCharEscape(String dataHex) {
		byte[] retb = null;
		String ret = "";

		byte[] bs = Tools.fromHexString(dataHex);

		for (int i = 0; i < bs.length; i++) {
			if (bs[i] == (byte) 0x7F) {

				byte tmpb = (byte) 0x00;

				ret += Tools.bytesToHexString(new byte[] { tmpb });

			} else {
				ret += Tools.bytesToHexString(new byte[] { bs[i] });
			}
		}
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

			if (espBytes[i] == (byte) 0x7d) {

				hex += "7d00";
			} else if (espBytes[i] == (byte) 0x7e) {

				hex += "7d01";
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
			if (bs[i] == (byte) 0x7d && (bs[i + 1] == (byte) 0x00)) {

				byte tmpb = (byte) 0x7d;
				i = i + 1;
				ret += Tools.bytesToHexString(new byte[] { tmpb });

			} else if (bs[i] == (byte) 0x7d && (bs[i + 1] == (byte) 0x01)) {

				byte tmpb = (byte) 0x7e;
				i = i + 1;
				ret += Tools.bytesToHexString(new byte[] { tmpb });

			} else {
				ret += Tools.bytesToHexString(new byte[] { bs[i] });
			}
		}
		retb = Tools.fromHexString(ret);

		return retb;
	}
	//调度信息编码
	public static String encodeDispatchMsg(byte[] b) {
		byte[] bs = null;
		String cvtBits = "";
		
		String binStr = Tools.bytes2BinaryString(b);
		int row = binStr.length() / 6;
		if (binStr.length() % 6 != 0)
			row = row + 1;
		String[][] cvt = new String[row][6];
		 
		int binIndex = 0;
		// 8/6 BIT转换
		for (int i = 0; i < row; i++) {
			String tmp = "";
			for (int j = 0; j < 6; j++) {
				if (binIndex < binStr.length())
					cvt[i][j] = String.valueOf(binStr.charAt(binIndex));
				else
					cvt[i][j] = "0";
				binIndex++;
				 System.out.print(cvt[i][j]+",");
				tmp += cvt[i][j];
			}
			int r = Integer.parseInt(tmp.substring(0, 3), 2);
			int c = Integer.parseInt(tmp.substring(3), 2);
			cvtBits += Tools.bytesToHexString(new byte[]{encodeMap[r][c]});
			 System.out.println("\r\n("+r+","+c+")=>"+Tools.bytesToHexString(new byte[]{encodeMap[r][c]}));
		}
		 System.out.println(cvtBits);
		
		 
		return cvtBits;
	}

	public static void main(String[] args) {
		String s = "0d97432001220220052031323334";
		System.out.println(Tools.compressHexData(Tools.bytesToHexString(s.getBytes())));
	}

}
