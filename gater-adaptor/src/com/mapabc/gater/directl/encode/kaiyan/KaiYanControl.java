package com.mapabc.gater.directl.encode.kaiyan;
/**
 * 
 */


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;


 

/**
 * @author chen.peng
 *
 */
public class KaiYanControl extends ControlAdapter{
	String head = "fe";
	String end = "ff";
	
	// 下发短消息
	public String msg(Request req) {
		// 地址  功能代码   子功能数据量    数据长度   数据内容  CRC
		// FE 51 11 00 10 3135333133323336343735 E5 FF
 		String cmd = "";
		String CRC = "";
		
		String content = RequestUtil.getReqData(req, "content");
		int len = content.getBytes().length;
		
		String hexcontent = Tools.bytesToHexString(content.getBytes());
		
		int cmdlen = 5 + len;
		CRC = KaiYanUtil.getCRC("511103" + Tools.int2Hexstring(cmdlen+3, 2) + hexcontent +"015111");
		cmd = head + "511103" + Tools.int2Hexstring(cmdlen+3, 2) + hexcontent +"015111"+ CRC +end;
		//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------下发短消息："+content);
		return cmd;
	}

	public String remoteControl(Request req) {
		
		String cmd = "";
		String CRC = "";
		String reset = RequestUtil.getReqData(req, "reset");
		
		String recover = RequestUtil.getReqData(req, "recover");
		
		String selfcheck = RequestUtil.getReqData(req, "selfcheck");
		
		if(selfcheck.trim()!="1"){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"-------自检，终端不支持该功能！");
		}
		if(reset.equals("1")){
 			
			CRC = KaiYanUtil.getCRC("31160308" +"013116");
			cmd = head + "31160308" +"013116"+ CRC +end;
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------重启！");
		}
		if(recover.equals("1")){
 			
			CRC = KaiYanUtil.getCRC("31070308" +"013107");
			
			cmd = head + "31070308" +"013107"+ CRC + end;
			
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"----------恢复出厂设置！");
		}
		return cmd;
	}

	public String listen(Request req) {
		//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------设置监听电话！");
		 		String callBackNumber = RequestUtil.getReqData(req, "callBackNumber");
		if(callBackNumber.trim().length()==0){
			//Log.getInstance().kaiyanLog("中心设置终端"+req.getDeviceId()+"的中心手机号码,但请求参数为空");
			return null;
		}
		String phoneCmd = Tools.bytesToHexString(callBackNumber.getBytes());
		String len = Tools.int2Hexstring(phoneCmd.length()/2+5+3, 2);
		String cmd = "";
		String CRC = "";
		CRC = KaiYanUtil.getCRC("311E03" + len + phoneCmd +"01311E");
		cmd = head + "311E03" + len + phoneCmd +"01311E"+ CRC + end;
		return cmd;
	}
	
	@Override
	public String switchDoorControl(Request req) {
		
 		String cmd = "";
		String CRC = "";
		String type = RequestUtil.getReqData(req, "type");
		
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String hexdate = KaiYanUtil.encodeTime(df.format(date));
		
		//kaisuo
		if(type.equals("0")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------远程开门");
			CRC = KaiYanUtil.getCRC("41010310"+"00"+hexdate+"01"+"FFFF"+"014101");
			cmd = head+"41010310"+"00"+hexdate+"01"+"FFFF"+"014101"+CRC+end;	
		}else{
			//suomen 
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------远程锁门");
			CRC = KaiYanUtil.getCRC("41010310"+"55"+hexdate+"01"+"FFFF"+"014101");
			cmd = head+"41010310"+"55"+hexdate+"01"+"FFFF"+"014101"+CRC+end;
		}
		
		return cmd;
	}

	public String callRestrict(Request req){

		String cmd = "";
		String CRC = "";
		String type = RequestUtil.getReqData(req, "type");
		String phones =  RequestUtil.getReqData(req, "phone");
		String phoneCmd ="";
		if(!phones.equals("")){
			String[] phone = phones.split(";");
			for(int i=0;i<phone.length;i++){
				phoneCmd +=Tools.int2Hexstring(i, 2)+Tools.int2Hexstring(phone[i].length(), 2) + Tools.bytesToHexString(phone[i].getBytes());
			}
		}
		//禁止拨打电话
		if(type.equals("1")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------禁止拨打电话");
	 			CRC = KaiYanUtil.getCRC("5103030933"+"015103");
			cmd = head + "5103030933"+"015103"+CRC+end;
			return cmd;
		}
		//禁止接听电话
		if(type.equals("2")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------禁止接听电话"); 
			
			CRC = KaiYanUtil.getCRC("510403" + Tools.int2Hexstring(phoneCmd.length()/2+5+3,2) +  phoneCmd + "015104" );
			cmd = head+"510403" + Tools.int2Hexstring(phoneCmd.length()/2+5+3,2) +  phoneCmd + "015104"+ CRC +end;
			
			CRC = KaiYanUtil.getCRC("5103030931"+"015103");
			cmd += head+"5103030931"+"015103"+CRC+end;
			return cmd;
		}
		//禁止拨打和接听
		if(type.equals("3")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------禁止拨打和接听");
	 		CRC = KaiYanUtil.getCRC("5103030932"+"015103");
			cmd = head+"5103030932"+"015103"+CRC+end;
			return cmd;
		}
		//可以拨打和接听
		if(type.equals("4")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------可以拨打和接听");
			CRC = KaiYanUtil.getCRC("5103030930"+"015103");
			cmd = head+"5103030930"+"015103"+CRC+end;
			return cmd;
		}
	
		return cmd;
	}


	public static void main(String[] args){ }
	 
}
