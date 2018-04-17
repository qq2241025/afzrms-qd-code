/**
 * 
 */
package com.mapabc.gater.directl.encode.xinruan;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;


/**
 * @author chen.peng
 *
 */
public class XinruanControl extends ControlAdapter{
	String head = "@@PS";
	String end = "%%";
	
	// 下发短消息
	public String msg(Request req) {
		// 40405053 53 38313031303731343033 0001 0009 E5BC80E4BC9A616263 07D7 2525
		//开始标志	协议号	ID号	   序列号	  数据长度	数据区	  校验和	 结束标志
		// @@PS	     0x53					               短消息内容		       %%
		//   4        1      10    2       2                   2       2


		String content = RequestUtil.getReqData(req, "content");
		
		int len = 0;
		
		for(int i=0;i<content.length();i++){
			
			len += content.substring(i,i+1).getBytes().length;
		}
		
		if(len>32||len==0){
			
			//Log.getInstance().xinruanLog("新软终端："+req.getDeviceId()+"回复，中心短消息内容为空，或内容过长（小于32字节）。");
			return null;	
		}
		
		byte[] msgbyte = content.getBytes();
		
		ByteBuffer bb = ByteBuffer.allocate(23+msgbyte.length);
		
		bb.put(XinruanUtil.comhead(req, 0x53));

		bb.put(Tools.fromHexString(Tools.int2Hexstring(len, 4)));
		
		bb.put(msgbyte);
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 15+msgbyte.length));
		
		bb.put(end.getBytes());
		
		return Tools.bytesToHexString(bb.array());
	}
/*	public String msg(Request req) {
		//开始标志	协议号	ID号 	序列号	命令长度	 命令字	命令内容      	校验和	结束标志
		//@@PS	     0x57	  			              W/R			               %%
		
		
		// N（共N条）	第N条（1字节）	长度（1字节）	短消息内容	……
		//  1               1              1               <32      
		RequestUtil.getDealRequest(req, StructionType.MSG_SEND_COMMOND_TYPE, "57");
		
		String content = RequestUtil.getReqData(req, "content");
		
		byte[] msgbyte = content.getBytes();
		
		ByteBuffer bb = ByteBuffer.allocate(31+msgbyte.length);
		
		bb.put(XinruanUtil.comhead(req, 0x57));
		

		bb.put(Tools.fromHexString(Tools.int2Hexstring(msgbyte.length+4, 4))); //命令长度
		
		bb.put("W".getBytes());//命令字
		
		bb.put((byte)0x05);//共5条
		bb.put((byte)0x01);//第1条
		
		bb.put((byte)msgbyte.length);//短消息的长度
		bb.put(msgbyte);
		

		bb.put((byte)0x00);
		bb.put((byte)0x00);
		bb.put((byte)0x00);
		bb.put((byte)0x00);
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(),23+msgbyte.length));
		
		bb.put(end.getBytes());
		
		return Tools.bytesToHexString(bb.array());
	}*/

	
	public String remoteControl(Request req) {
		
		//开始标志	协议号	ID号	    序列号	命令长度	命令字	命令体	命令内容	校验和	结束标志
		//@@PS	    0x50	  			 				1字节	1字节			%%
 
	// reset = 40405053 50 38313031303731343032 0001 0002 57 4F 02F1 2525
		//	   40405053 50 38313031303731343032 0001 0002 57 4F 02F1 2525
		ByteBuffer recoverbb = ByteBuffer.allocate(25);
		ByteBuffer selfcheckbb = ByteBuffer.allocate(24);
		
		
		String reset = RequestUtil.getReqData(req, "reset");
		
		String recover = RequestUtil.getReqData(req, "recover");
		
		String selfcheck = RequestUtil.getReqData(req, "selfcheck");
		
		if(recover.equals("1")){
			//40405053 50 38313031303731343033 0001 0002 574F 02F2 2525
			 
			recoverbb.put(XinruanUtil.comhead(req, 0x50));
			
			recoverbb.put((byte)0x00);
			recoverbb.put((byte)0x02);
			recoverbb.put("W".getBytes());
			recoverbb.put("O".getBytes());
			
			recoverbb.put(XinruanUtil.getXinRuanVerfyCode(recoverbb.array(), 17));
			
			recoverbb.put(end.getBytes());
			
			return Tools.bytesToHexString(recoverbb.array());
		}
		
		if(reset!=""){
			
			//Log.getInstance().xinruanLog("中心设置终端重新启动，终端无该指令功能。");
		}
		
		int intselfcheck = selfcheck==""?3:Integer.parseInt(selfcheck);
		
		if(intselfcheck!=3){
			 
			selfcheckbb.put(XinruanUtil.comhead(req, 0x59));
			
			selfcheckbb.put((byte)0x00);
			selfcheckbb.put((byte)0x01);
			
			selfcheckbb.put("R".getBytes());
			
			selfcheckbb.put(XinruanUtil.getXinRuanVerfyCode(selfcheckbb.array(), 16));
			
			selfcheckbb.put(end.getBytes());
			
			//Log.getInstance().xinruanLog("中心下发终端自检命令，终端自检后自动上传自检信息");
			
				
			return Tools.bytesToHexString(selfcheckbb.array());
		}
	
		return null;
	
	}

	public String listen(Request req) {
	
		// 开始标志	协议号	ID号 	序列号	数据长度    	                              数据区	                                    校验和	结束标志
		// @@PS	    0x52				                    Dtmf控制字  中心电话号码		           %%

	
		 
		String callBackNumber = RequestUtil.getReqData(req, "callBackNumber");
		
		if(callBackNumber.trim().length()==0){
			//Log.getInstance().xinruanLog("中心设置终端"+req.getDeviceId()+"的中心手机号码,但请求参数为空");
			return null;
		}

		int cmdlen = callBackNumber.length()+1;
		
		ByteBuffer bb = ByteBuffer.allocate(cmdlen+23);
		
		bb.put(XinruanUtil.comhead(req, 0x52));
		
		bb.put(Tools.fromHexString(Tools.int2Hexstring(cmdlen, 4)));
		
		bb.put("0".getBytes());//不发送Dtmf控制字
		
		bb.put(callBackNumber.getBytes());
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), cmdlen+15));
		
		bb.put(end.getBytes());
		
		return Tools.bytesToHexString(bb.array());
	}

	public String callRestrict(Request req){
		//40405053 58 38313031303731343033 0001 001c 57303031333837383934353631324630313339333135323935343546 0866 2525
		//40405053 58 38313031303731343033 0001 001c57303031333837383934353631324630313339333135323935343546 0820 2525
		//40405053 58 38313031303731343033 0001 001c57303031333837383934353631324630313339333135323935343546 0820 2525
		//开始标志	协议号	ID号 	序列号	命令长度	             命令字	          命令内容	                               校验和	结束标志
		// @@PS	     0x58	  			                 W/R	1	1	21                       %%
		 
		String phones =  RequestUtil.getReqData(req, "phone");
		
		int cmdlen = 0;
		String[] phone = phones.split(";");
		for(int i=0;i<phone.length;i++){
			cmdlen+=(phone[i].length()+2);
		}
		cmdlen+=1;//0
		
		ByteBuffer bb = ByteBuffer.allocate(cmdlen+24);
		
		bb.put(XinruanUtil.comhead(req, 0x58));
		
		bb.put(Tools.fromHexString(Tools.int2Hexstring(cmdlen+1, 4)));
		
		bb.put("W".getBytes());
		
		bb.put("0".getBytes());
		
		String type  = RequestUtil.getReqData(req, "type");
		
		//禁止拨打电话
		if(type.equals("1")||type.equals("2")||type.equals("3")){
			for(int i=0;i<phone.length;i++){
				
				bb.put("0".getBytes());
				bb.put(phone[i].getBytes());
				bb.put("F".getBytes());
			}
		}

		//可以通话
		if(type.equals("4")){
			
			for(int i=0;i<phone.length;i++){
				
				bb.put("3".getBytes());
				bb.put(phone[i].getBytes());
				bb.put("F".getBytes());
			}
			
			
		}
    	
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), cmdlen+16));
		//System.out.println(Tools.bytesToHexString(XinruanUtil.getXinRuanVerfyCode(bb.array(), cmdlen+16)));
		bb.put(end.getBytes());
		

		return Tools.bytesToHexString(bb.array());
	
	}
	public static void main(String[] args){ }
	 
}
