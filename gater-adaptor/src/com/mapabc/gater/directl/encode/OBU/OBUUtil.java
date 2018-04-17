package com.mapabc.gater.directl.encode.OBU;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.mapabc.gater.directl.Tools;

/**
 * @author chen.peng
 * 
 */
public class OBUUtil {

	public static String getParity(String cmd) {
		byte[] b = Tools.fromHexString(cmd);
		byte result = b[0];
		int i = 1;
		while (i < b.length) {
			result ^= b[i];
			i++;
		}
		return Tools.bytesToHexString(new byte[]{result});
	}

	public static String downParameterSet(String deviceId, int count,
			String cmdHex) {
		String head = "87";
		String ret = "";

		int leng = 1 + cmdHex.length() / 2 + 1;
		ret = Tools.int2Hexstring(leng, 2) + Tools.int2Hexstring(count, 2)+ cmdHex;
		byte vcode = Tools.checkData(Tools.fromHexString(Tools.int2Hexstring(count, 2)+ cmdHex));

		ret += Tools.bytesToHexString(new byte[] { vcode });
		ret = head + ret;
		//Log.getInstance().obitsLog(deviceId+" 设置参数指令："+ret);
		return ret;
	}

	public static String downWirelessTransfer(String deviceSn, String commandHex) {
		String ret = "bd";
		String deviceid = "00";
		int len = 1 + commandHex.length() / 2 + 1;
		String lenHex = Tools.bytesToHexString(Tools
				.convertBytePos(new byte[] { (byte) len }));
		while (lenHex.length() < 4) {
			lenHex = lenHex + "0";
		}

		ret += lenHex + deviceid + commandHex;
		byte vcode = Tools
				.checkData(Tools.fromHexString(deviceid + commandHex));
		ret += Tools.bytesToHexString(new byte[] { vcode });
		//Log.getInstance().obitsLog(deviceSn + " 下发指令：" + ret);
		return ret;
	}

	public static void main(String[] args) {
	}
}
