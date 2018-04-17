/**
 * 
 */
package com.mapabc.gater.directl.encode.xinruan;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;

 

/**
 * @author chen.peng
 * 
 */
public class XinruanLocator extends LocatorAdapter {
	String head = "@@PS";
	String end ="%%";
	int seq = 1;

	public String timeInter(Request req) {
		//开始标志	协议号	ID号	    序列号	命令长度	命令字	命令体	命令内容	校验和	结束标志
		// @@PS	    0x55	  10	2	  2	    1字节	1字节	 3字节	 2	      %%2
		//4          1       10     2      2      1      1          3    2         2
		
		
		ByteBuffer bb = ByteBuffer.allocate(28);
		bb.put(XinruanUtil.comhead(req, 0x55));
		bb.put((byte)0x00);
		bb.put((byte)0x05);
		bb.put("Wb".getBytes());	
		
		String interval = RequestUtil.getReqData(req, "interval"); 
		String count = RequestUtil.getReqData(req, "count"); 
		
		if(interval.trim()==""&&count.trim()==""){
			//Log.getInstance().xinruanLog("中心设置GPS数据上传方式失败，参数为空！");
			return null;
		}
		if(!interval.trim().equals("")){
			int ival = Integer.parseInt(interval);//间隔
			
			if (ival < 1) {
	 			ival = 0;
			} 
			String hexival = Tools.int2Hexstring(ival, 4);

			bb.put((byte)0x02);
			
			bb.put(Tools.fromHexString(hexival));
			
		}
		if(interval.trim()==""&&count.trim()!=""){
			int intcount = Integer.parseInt(count);//间隔
			
			String hexcount = Tools.int2Hexstring(intcount, 4);

			bb.put((byte)0x03);
			
			bb.put(Tools.fromHexString(hexcount));
		}
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 20));
		
		bb.put(end.getBytes());


		return Tools.bytesToHexString(bb.array());

	}

	public String locate(Request req) {
		
		//开始标志	协议号	ID号 	序列号	数据长度	         数据区	校验和	结束标志
		// @@PS	    0x51			          0	                       空		           %%
		//   4       1       10       2       2          <900      2        2
	
		ByteBuffer bb = ByteBuffer.allocate(23);
		
		bb.put(XinruanUtil.comhead(req, 0x51));
		
		bb.put((byte)0x00);
		bb.put((byte)0x00);
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 15));
	
		bb.put(end.getBytes());
		
		return Tools.bytesToHexString(bb.array());
	}

	//开始标志	协议号	ID号	    序列号	命令长度	命令字	命令体	命令内容	校验和	结束标志
	// @@PS	    0x55	  10	2	  2	    1字节	1字节	 3字节	 2	      %%2
	//4          1       10     2      2      1      1          3    2         2
	public String distanceInter(Request req) {

		String interval = (String) req.getDatas().get("interval"); 
		String count = RequestUtil.getReqData(req, "count");
		int ival = 0;

 		if (interval != ""){
			ival = Integer.parseInt(interval);//间隔
 		}
		
		String hexival = Tools.int2Hexstring(ival, 4);
		
		ByteBuffer bb = ByteBuffer.allocate(28);
	
		bb.put(XinruanUtil.comhead(req, 0x55));
		
		String datalen = Tools.int2Hexstring(5, 4);
		bb.put(Tools.fromHexString(datalen));
	
		bb.put("Wa".getBytes());
		
		bb.put((byte)0x01);
		
		bb.put(Tools.fromHexString(hexival));
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 20));
		
		bb.put(end.getBytes());
		
		String hex = Tools.bytesToHexString(bb.array());
		
		//System.out.println(new String(Tools.fromHexString(hex.substring(8,10))));
		return hex ;
	}
		
	//开始标志	协议号	ID号	    序列号
	//  4          1      10   2 

	public String test(Request req){
		
		ByteBuffer bb = ByteBuffer.allocate(55);
		
		bb.put(XinruanUtil.comhead(req, 0x21));
		
		String datalen = Tools.int2Hexstring(22, 4);
		bb.put(Tools.fromHexString(datalen));
		
		//非压缩GPS数据
		
		String year = Tools.int2Hexstring(2010, 4);
		String mm = Tools.int2Hexstring(7, 2);
		String dd = Tools.int2Hexstring(23, 2);
		String tt = Tools.int2Hexstring(17, 2);
		String MM = Tools.int2Hexstring(34, 2);
		String ss = Tools.int2Hexstring(2, 2);
		
		double d = 118.588565*3600000;
		String lon = Tools.int2Hexstring((int)d, 8);
		String lat = Tools.int2Hexstring((int)d, 8);
		//System.out.println(lon);
		int sin = 0xff;
		
		String speed = Tools.int2Hexstring(96, 4);
		String deriction = Tools.int2Hexstring(97, 4);
		String height = Tools.int2Hexstring(98, 4);
		
		bb.put(Tools.fromHexString(year));
		bb.put(Tools.fromHexString(mm));
		bb.put(Tools.fromHexString(dd));
		bb.put(Tools.fromHexString(tt));
		bb.put(Tools.fromHexString(MM));
		bb.put(Tools.fromHexString(ss));
		bb.put(Tools.fromHexString(lon));
		bb.put(Tools.fromHexString(lon));
		bb.put((byte)sin);
		bb.put(Tools.fromHexString(speed));
		bb.put(Tools.fromHexString(deriction));
		bb.put(Tools.fromHexString(height));
		
		bb.put((byte)0x00);
		bb.put((byte)0xff);
		
		bb.put(end.getBytes());

		
		return Tools.bytesToHexString(bb.array());	
	}
	public static void main(String aft[]){ }
	
}
