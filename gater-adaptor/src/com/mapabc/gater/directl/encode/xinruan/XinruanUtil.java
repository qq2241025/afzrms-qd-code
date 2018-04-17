/**
 * 
 */
package com.mapabc.gater.directl.encode.xinruan;


import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;


/**
 * @author chen.peng
 *
 */
public class XinruanUtil {
	
	public static int getVerfyCode(byte[] cont) {
		 
		int sum = 0;
		for (int i = 0; i < cont.length; i++) {
			sum += cont[i] & 0xff;
		}
	 
		return sum;
	}
	public static byte[] getXinRuanVerfyCode(byte[] bb,int tolen){
		
		byte[] temp = bb;
		
		byte[] cmd = new byte[tolen];
		
		System.arraycopy(temp, 4, cmd, 0, tolen);
		
		int vocde = getVerfyCode(cmd);
		System.out.println(Integer.toHexString(vocde));
		
		String verCode = Tools.int2Hexstring(vocde, 4);
		
		return Tools.fromHexString(verCode);
	}
	public static byte[] comhead(Request req,int protoclNum){
		
		int serquee = 1;
		
		ByteBuffer bb = ByteBuffer.allocate(17);
		
		bb.put("@@PS".getBytes());//头
		
		String proctol = Tools.int2Hexstring(protoclNum, 2);
		bb.put(Tools.fromHexString(proctol));//协议号
		
		String deviceId = req.getDeviceId();
		bb.put(deviceId.getBytes());//设备ID
		
		String slh = Tools.int2Hexstring(serquee, 4);
		bb.put(Tools.fromHexString(slh));//序列号
		
		return bb.array();
	}
	public static void main(String[] args){
		String hex = "404050535538313031303731343032000100055761020014";
		byte[] bh = Tools.fromHexString(hex);
		byte[] bs =XinruanUtil.getXinRuanVerfyCode(bh, bh.length-4);
		System.out.println("===="+Tools.bytesToHexString(bs));
		 
		 
	}
}
