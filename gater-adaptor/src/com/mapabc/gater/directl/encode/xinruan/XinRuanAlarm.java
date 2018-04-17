/**
 * 
 */
package com.mapabc.gater.directl.encode.xinruan;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.*;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;

/**
 * @author chen.peng
 * 
 */
public class XinRuanAlarm extends AlarmAdaptor {

	String head = "@@PS";
	String end ="%%";
	int seq = 1;
	//协议号：0x50
	public String overspeedAlarm(Request req) {
		 
	 
		//开始标志	协议号	ID号	   	序列号	命令长度	 命令字	命令体	命令内容	    校验和	结束标志
		//  4         1      10      2        2        1       1      1        2      2
		ByteBuffer bb = ByteBuffer.allocate(26);
		
		bb.put(XinruanUtil.comhead(req, 0x55));
		
		String cmdlength = Tools.int2Hexstring(3, 4);//命令字+命令提+命令内容
		
		bb.put(Tools.fromHexString(cmdlength));//命令长度
		
		bb.put("Wc".getBytes());
		
		String maxSpeed = RequestUtil.getReqData(req, "max");
		
		if(maxSpeed.trim().equals("0")){
			//Log.getInstance().xinruanLog("中心设置新软终端取消，id："+req.getDeviceId()+"的超速报警。但该终端不支持该指令");
			return null;
		}
		String duration = RequestUtil.getReqData(req, "duration");
		if(!duration.trim().equals("")){
			//Log.getInstance().xinruanLog("中心设置心软终端id："+req.getDeviceId()+"的超速报警时间值。但该终端不支持该指令");
		}

		bb.put(Tools.fromHexString(Tools.int2Hexstring(Integer.parseInt(maxSpeed), 2)));
		
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 18));
		
		
		bb.put(end.getBytes());

		//Log.getInstance().xinruanLog("deviceId:"+req.getDeviceId() + "  Over Speed Set CMD:"+Tools.bytes2HexString(bb.array()));
		
		return Tools.bytesToHexString(bb.array());
	}
	
	public String areaAlarm(Request req) {
		//开始标志	协议号	ID号	      序列号		命令长度		命令字	命令内容		校验和	结束标志
		//@@PS	     0x56	  			                 W/R			                %%
	 
		String areaType = RequestUtil.getReqData(req, "areaType");
		
		if(areaType.equals("1")){
			
			ByteBuffer bb = ByteBuffer.allocate(298);
			
			bb.put(XinruanUtil.comhead(req, 0x56));//开始--》序列号
			
			bb.put(Tools.fromHexString(Tools.int2Hexstring(16*17+3, 4)));//命令长度
			
			bb.put("W".getBytes());//命令字
			
			
			//命令内容
			
			//共N个	第几个	报警状态	纬度11	纬度12	经度11	经度12	报警状态	.	经度n2
			//1字节	1字节	1字节	4字节	4字节	4字节	4字节	1字节	.	4字节

			bb.put((byte)1);
			bb.put((byte)1);
			
			ByteBuffer tempdata = ByteBuffer.allocate(17);
			byte[] data = new byte[17*16];
		
			String points = RequestUtil.getReqData(req, "points");
			
			String[] point = points.split(";");
			
			if(point.length<2){
				//Log.getInstance().xinruanLog("该终端不支持该功能！区域报警设置必须为两个点以上！");
				return null;
			}
			String alarmType = RequestUtil.getReqData(req, "alarmType");
			
			String alarmstatus = "";
			//出报警
			if(alarmType.equals("0")){
				alarmstatus = "01";	
			}
			//入报警
			if(alarmType.equals("1")){	
					alarmstatus = "10";	
			}
			//都报警
			if(alarmType.equals("2")){	
					alarmstatus = "11";
			}
			
		
			int pCount = point.length>32?32:point.length;
			
			for(int i=0;i<pCount;i+=2){
				String[] lonlat1 = point[i].split(",");
				String lon1 = lonlat1[0];
				String lat1 = lonlat1[1];
				String[] lonlat2 = point[i+1].split(",");
				String lon2 = lonlat2[0];
				String lat2 = lonlat2[1];
				tempdata.clear();
				tempdata.put(Tools.fromHexString(alarmstatus));
				tempdata.put(Tools.double2Hexstring(Double.parseDouble(lon1),8));
				tempdata.put(Tools.double2Hexstring(Double.parseDouble(lon2), 8));
				tempdata.put(Tools.double2Hexstring(Double.parseDouble(lat1), 8));
				tempdata.put(Tools.double2Hexstring(Double.parseDouble(lat2), 8));
				
				for(int j=0;j<data.length;j+=17){
					System.arraycopy(tempdata.array(), 0, data, j, 16);
				}
				
			}
			for(int i=pCount/2;i<16;i++){
				for(int j=0;j<17;j++){
					data[17*i+j] = (byte)0x00;			     
				}
			}
			
			bb.put(data);
			
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 290));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
			
		}else{
			//Log.getInstance().xinruanLog("该终端不支持该功能！");
			return null;
		}

	}

	public String cancleArea(Request req) {
		//开始标志	协议号	ID号	      序列号		命令长度		命令字	命令内容		校验和	结束标志
		//@@PS	     0x56	  			                 W/R			                %%
		
		//命令内容
		
		//共N个	第几个	报警状态	纬度11	纬度12	经度11	经度12	报警状态	.	经度n2
		//1字节	1字节	1字节	4字节	4字节	4字节	4字节	1字节	.	4字节
		 
		ByteBuffer bb = ByteBuffer.allocate(298);
		
		bb.put(XinruanUtil.comhead(req, 0x56));
		
		bb.put(Tools.fromHexString(Tools.int2Hexstring(16*17+2, 4)));//命令长度
		
		bb.put("W".getBytes());//命令字
		
		bb.put((byte)0x16);
		bb.put((byte)0x01);
		
		for(int i=0;i<17*16;i++){
			bb.put((byte)0x00);
		}
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 290));
		
		bb.put(end.getBytes());
		
		return Tools.bytesToHexString(bb.array());

	}


	public String viewAreas(Request req){
		 
		ByteBuffer bb = ByteBuffer.allocate(26);
		
		bb.put(XinruanUtil.comhead(req, 0x56));
		
		bb.put(Tools.fromHexString(Tools.int2Hexstring(3, 4)));//命令长度
		
		bb.put("R".getBytes());//命令字
		
		bb.put((byte)0x16);
		bb.put((byte)0x01);

		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 18));
		
		bb.put(end.getBytes());
		
		return Tools.bytesToHexString(bb.array());
	}	
	public static void main(String aft[]){ }
}
