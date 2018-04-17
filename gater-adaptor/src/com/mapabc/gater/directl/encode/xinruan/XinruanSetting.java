/**
 * 
 */
package com.mapabc.gater.directl.encode.xinruan;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;


/**
 * @author chen.peng
 *
 */
public class XinruanSetting extends SettingAdaptor {
	String head = "@@PS";
	String end = "%%";
	String seq = "1";
	
	public String addrSetting(Request req) {
		 //开始标志	协议号	ID号 	序列号	命令长度	   	命令内容  	校验和	结束标志
		// @@PS      0x50	  			 		                                          %%
		//命令字	            命令体             命令内容	
		//  W/R	       A	 210021045021

		ByteBuffer ipbb = ByteBuffer.allocate(37);
		ByteBuffer portbb = ByteBuffer.allocate(29);
		ByteBuffer localportbb = ByteBuffer.allocate(29);
		//设置IP
		//60.247.103.22
		//40405053 50 38313031303731343032 0001 000E 5741303630323437313033303232 054A 2525
		String ip = RequestUtil.getReqData(req, "ip");
		
		if(!ip.trim().equals("")){
			String[] ips = ip.split("\\.");
			String centerip = "";
			for(int i=0;i<ips.length;i++){
				while(ips[i].length()<3){
					ips[i] = "0"+ips[i];
				}
				centerip+=ips[i];
			}
			
			
			ipbb.put(XinruanUtil.comhead(req, 0x50));
			
			ipbb.put(Tools.fromHexString(Tools.int2Hexstring(14, 4)));
			
			ipbb.put("WA".getBytes());
			
			ipbb.put(centerip.getBytes());
			
			ipbb.put(XinruanUtil.getXinRuanVerfyCode(ipbb.array(), 29));
			
			ipbb.put(end.getBytes());
			
			return Tools.bytesToHexString(ipbb.array());
			
		}
		
		//设置端口
		String port = RequestUtil.getReqData(req, "port");
		//40405053 50 38313031303731343032 0001 0006 57423535363 7035 22525
		if(!"".equals(port)){
			
			while(port.length()<4){
				port = "0"+port;
			}
			portbb.put(XinruanUtil.comhead(req, 0x50));
			
			portbb.put(Tools.fromHexString(Tools.int2Hexstring(6, 4)));
			
			portbb.put("WB".getBytes());
			
			portbb.put(port.getBytes());
			
			portbb.put(XinruanUtil.getXinRuanVerfyCode(portbb.array(),21));
			
			portbb.put(end.getBytes());
			
			return Tools.bytesToHexString(portbb.array());
		}
		//设置终端本地端口
		String localport = RequestUtil.getReqData(req, "localPort");
		//40405053 50 31383939323831353835 0001 0006 574335353637 03E0 2525
		if(!"".equals(localport)){
			
			while(localport.length()<4){
				localport = "0"+localport;
			}
			localportbb.put(XinruanUtil.comhead(req, 0x50));
			
			localportbb.put(Tools.fromHexString(Tools.int2Hexstring(6, 4)));
			
			localportbb.put("WC".getBytes());
			
			localportbb.put(localport.getBytes());
			
			localportbb.put(XinruanUtil.getXinRuanVerfyCode(localportbb.array(),21));
			
			localportbb.put(end.getBytes());
			
			return Tools.bytesToHexString(localportbb.array());
		}

		return null;
	}



	public String apnSetting(Request req) {
		//开始标志	协议号	ID号	          序列号	命令长度	命令字	命令内容     校验和	结束标志
		//@@PS  	0x2D	  			             W/R	 APN值　　		 %%
		//	4		1		10		2			2	  1      apnlen   2        2

		//40405053 71 31383939323831353835 0001 0011 57 3148545450533B31776170687474703B 0865 2525
	 
		String type = RequestUtil.getReqData(req, "type");
		String apns = RequestUtil.getReqData(req, "apn");
		
		
		if(apns.trim().length()!=0){
			
			String carType = "";
			
			if(type.trim().equals("CLS")){
				carType = "2";
			}
			if(type.trim().equals("CMCC")){
				carType = "1";
			}
			
			String[] apn = apns.split(";");
			
			
			String apncontent = "";
			for(int i=0;i<apn.length;i++){
				apncontent +=(carType+apn[i]+";");
				
			}
			
			int apnlen = apncontent.length();
			ByteBuffer bb = ByteBuffer.allocate(apnlen+24);
			
			bb.put(XinruanUtil.comhead(req, 0x71));
			
			bb.put(Tools.fromHexString(Tools.int2Hexstring(apnlen+1, 4)));
			
			bb.put("W".getBytes());
			
			bb.put(apncontent.getBytes());
			
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(),apnlen+16));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}

	}


	@Override
	public String smsCenterSetting(Request req) {
		//开始标志	协议号	ID号  	序列号	命令长度    命令内容	  校验和	结束标志
		//@@PS	    0x50	  			       		                                       %%
		
		//40405053 50 38313031303731343032 0001 0008 5746313030383646 0433 2525
	 
		String number = RequestUtil.getReqData(req, "number");
		
		if(!number.trim().equals("")){
			
			number = number+"F";
			
			ByteBuffer bb = ByteBuffer.allocate(number.length()+25);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(Tools.fromHexString(Tools.int2Hexstring(number.length()+2, 4)));
			
			bb.put("WF".getBytes());
			
			bb.put((number).getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), number.length()+17));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}
		
	}
	
	public String carIdSetting(Request req) {

		//开始标志	协议号	ID号  	序列号	命令长度	命令字	命令体	命令内容	校验和	结束标志
		//@@PS	    0x50	  			       		                                   %%
		//40405053 50 31383939323831353835 0001 000d 57 48 3030303031323334353646 0550 2525
 
		ByteBuffer idbb = ByteBuffer.allocate(36);
		String newid = RequestUtil.getReqData(req, "newId");
		
		if(!newid.trim().equals("")){
			while(newid.length()<10){
				newid = "0"+newid;
			}
			newid = newid+"F";
			
			
			idbb.put(XinruanUtil.comhead(req, 0x50));
			
			idbb.put(Tools.fromHexString(Tools.int2Hexstring(13, 4)));
			
			idbb.put("WH".getBytes());
			
			idbb.put(newid.getBytes());
			
			idbb.put(XinruanUtil.getXinRuanVerfyCode(idbb.array(), 28));
			
			idbb.put(end.getBytes());
			
			return Tools.bytesToHexString(idbb.array());
			
		}else{
			//Log.getInstance().xinruanLog("设置车辆ID失败；参数为空");
			return null;
		}
	}
	
	
	public String vehicleIdSetting(Request req) {

		//开始标志	协议号	ID号  	序列号	命令长度	命令字	命令体	命令内容	校验和	结束标志
		//@@PS	    0x50	  			       		                                   %%
		//40405053 50 31383939323831353835 0001 000d 57 48 3030303031323334353646 0550 2525
 
		ByteBuffer idbb = ByteBuffer.allocate(36);
		String newid = RequestUtil.getReqData(req, "newId");
		
		if(!newid.trim().equals("")){
			while(newid.length()<10){
				newid = "0"+newid;
			}
			newid = newid+"F";
			
			
			idbb.put(XinruanUtil.comhead(req, 0x50));
			
			idbb.put(Tools.fromHexString(Tools.int2Hexstring(13, 4)));
			
			idbb.put("WH".getBytes());
			
			idbb.put(newid.getBytes());
			
			idbb.put(XinruanUtil.getXinRuanVerfyCode(idbb.array(), 28));
			
			idbb.put(end.getBytes());
			
			return Tools.bytesToHexString(idbb.array());
			
		}else{
			//Log.getInstance().xinruanLog("设置车辆ID失败；参数为空");
			return null;
		}
	}
	
	public	String fortificationSetting(Request req){
   String type = RequestUtil.getReqData(req, "type");
		if(!type.trim().equals("")){
			
			//开始标志	协议号	ID号	              序列号	  数据长度	 数据区	                              校验和	结束标志
			//@@PS   	0x64						                  0x02                              %%

			ByteBuffer bb = ByteBuffer.allocate(24);
			
			bb.put(XinruanUtil.comhead(req, 0x64));
			
			bb.put((byte)0x00);
			bb.put((byte)0x01);
			
			if(type.equals("1")){
				bb.put((byte)0x02);
			}
			if(type.equals("0")){
				bb.put((byte)0x03);
			}
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 16));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
			
		}else{
			//Log.getInstance().xinruanLog("设置车辆设防失败；参数为空");
			return null;
		}


	}
	
	@Override
	public String heartSetting(Request req) {
		
		//开始标志	协议号	ID号  	序列号	命令长度  	命令内容   	校验和	结束标志
		//@@PS	    0x50	  			       		                              %%
		//40405053 50 31383939323831353835 0001 0004 574D3630 0377 2525
		//40405053 50 38313031303731343033 0001 0004 574D3630 0358 2525
		//40405053 50 38313031303731343033 0001 0004 574D3630 0358 2525
		 	String interval = RequestUtil.getReqData(req, "interval");
		
		if(!interval.trim().equals("")&& interval.length()<=8){
			
			ByteBuffer bb = ByteBuffer.allocate(interval.length()+25);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(Tools.fromHexString(Tools.int2Hexstring(interval.length()+2, 4)));
			
			bb.put("WM".getBytes());
			
			bb.put(interval.getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), interval.length()+17));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString((bb.array()));
		}else{
			return null;
		}
	
	}



	public static void main(String arg[]){ }

}
