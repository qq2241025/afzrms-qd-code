package com.mapabc.gater.directl.encode.sege;

import java.io.*;
import java.nio.*;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;



public class SegLPG52Locator extends LocatorAdapter {
	String centerSn = "01";
	String zuoSn = "001";

	public String locate(Request req) {
		String cmdsn = centerSn + zuoSn;
		String inSeq = Tools.getRandomString(5);
		while (inSeq.length() < 5) {
			inSeq = "0" + inSeq;
		}
		cmdsn = cmdsn + inSeq;

		String ret = "";
		ByteBuffer buf = ByteBuffer.allocate(23);
		buf.put((byte) 0x5B);
		buf.put((byte) 0x10);
		buf.put(cmdsn.getBytes());
		buf.put((byte) 0x09);
		buf.put("(CTR,000)".getBytes());
		buf.put((byte) 0x5D);
		ret = Tools.bytesToHexString(buf.array());

		return ret;
	}

	public String timeInter(Request req) {

		String ret = "";
		String time = "";

		String cnt = "";
		String cmdsn = centerSn + zuoSn;
		String inSeq = Tools.getRandomString(5);
		while (inSeq.length() < 5) {
			inSeq = "0" + inSeq;
		}
		cmdsn = cmdsn + inSeq;
		String tts = RequestUtil.getReqData(req, "interval");
		String tc = RequestUtil.getReqData(req, "count");
		int tt = Integer.parseInt(tts);
		int cou = 0;
		try {
			cou = Integer.parseInt(tc);
		} catch (Exception e) {
			cou = 0;
		}
		time = Tools.convertToHex(tts.trim(), 2);
		if (cou <= 0 || tc.length() <= 0) {
			cnt = "FF";
		} else {
			cnt = Tools.convertToHex(tc.trim(), 2);
		}
		String cmd = "(CMD,002," + cnt + "," + time + ")";
		ByteBuffer buf = ByteBuffer.allocate(29);
		buf.put((byte) 0x5B);
		buf.put((byte) 0x11);
		buf.put(cmdsn.getBytes());
		buf.put((byte) 0x0F);
		buf.put(cmd.getBytes());
		buf.put((byte) 0x5D);
		ret = Tools.bytesToHexString(buf.array());
		return ret;
	}

	public String distanceInter(Request req) {
		String cmd = "";
		String state = "";

		String distance = RequestUtil.getReqData(req, "interval");
		String count = RequestUtil.getReqData(req, "count");
		int cou = Integer.parseInt(count);
		if (cou <= 0 || count.length() <= 0) {
			state = "FF";
		} else {
			state = Tools.convertToHex(count.trim(), 2);
		}
		String hexDist = Tools.convertToHex(distance, 3);
		String hexCount = Tools.convertToHex(count, 3);
		String content = "(CMD,DUT," + state + "," + hexCount + "," + hexDist
				+ ")";
		ByteBuffer buf = ByteBuffer.allocate(14 + content.length());
		buf.put((byte) 0x5b);
		buf.put((byte) 0x10);
		buf.put("0100100001".getBytes());
		buf.put((byte) content.length());
		buf.put(content.getBytes());
		buf.put((byte) 0x5d);

		cmd = Tools.bytesToHexString(buf.array());

		return cmd;
	}

	public static void main(String[] args) {
	}
}
