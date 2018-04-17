package com.mapabc.gater.directl.encode.kaiyan;
import java.util.HashMap;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
 

/**
 * @author chen.peng
 * 
 */
public class KaiYanLocator extends LocatorAdapter {
	String head = "fe";
	String end ="ff";

	public String timeInter(Request req) {
		
 		String cmd = "";
		String interval = RequestUtil.getReqData(req, "interval");
		//String Id = KaiYanUtil.getId(req);
		String CRC = "";
		int intinterval = Integer.parseInt(interval);
		
		if(intinterval<=0){
			CRC = KaiYanUtil.getCRC("2103030A0000"+"012103");
			cmd = head+"2103030A0000"+"012103"+CRC+end;
			//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------停止按时间间隔回传！");
		}else{
			String hexInterval = Tools.int2Hexstring(intinterval, 4);
			String lastInterval = hexInterval.substring(2, 4)+hexInterval.substring(0, 2);
			CRC = KaiYanUtil.getCRC("2103030A"+lastInterval+"012103");
			cmd = head+"2103030A"+lastInterval+"012103"+CRC+end;
			//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------按时间回传位置信息，时间间隔为："+interval);
		}
		
		return cmd;
	}

	public String locate(Request req) {
		//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------实时定位");
		//13316948387--->A39DB4C5
	 		String cmd = "";
		String CRC = "";
		//String Id = KaiYanUtil.getId(req);
		CRC = KaiYanUtil.getCRC("21010308"+"012101");
		cmd = head+"21010308"+"012101"+CRC+end;
		
		return cmd;
		//return Tools.bytesToHexString(cmd.getBytes());
	}


	public String distanceInter(Request req) {
		
	 		String cmd = "";
		String interval = RequestUtil.getReqData(req, "interval");
		String count = RequestUtil.getReqData(req, "count");
		if(count.trim()!=""){
			//Log.getInstance().kaiyanLog("中心设置开研终端-->"+req.getDeviceId()+"定距回传，该终端不支持Count参数。");
		}
		//String Id = KaiYanUtil.getId(req);
		
		int intinterval = Integer.parseInt(interval);
		String CRC = "";
		if(intinterval<=0){
			CRC = KaiYanUtil.getCRC("2104030B000000"+"012104");
			cmd = head+"2104030B000000"+"012104"+CRC+end;
			//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------停止按距离回传位置信息");
		}else{
			intinterval = (int) (intinterval/0.0514444);
			String hexInterval = Tools.int2Hexstring(intinterval, 6);
			String lastInterval = hexInterval.substring(4, 6)+hexInterval.substring(2, 4)+hexInterval.substring(0, 2);
			
			CRC = KaiYanUtil.getCRC("2104030B"+lastInterval+"012104");
			
			cmd = head+"2104030B"+lastInterval+"012104"+CRC+end;
			//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------按距离回传位置信息，距离间隔为："+interval);
		}
		
		return cmd;
	}
	

	public String timeLocate(Request req) {
		//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------按起止日期回传位置信息");
		//  0x21  0x02  子功能数据量     数据长度     起始时间      结束时间间       隔时间        CRC
	 	
		String cmd = "";
		String CRC = "";
		String startTime = RequestUtil.getReqData(req, "startTime");
		String endTime = RequestUtil.getReqData(req, "deadTime");
		String reportWeek = RequestUtil.getReqData(req, "reportWeek");
		String reportTime  = RequestUtil.getReqData(req, "reportTime");
		String interval = RequestUtil.getReqData(req, "interval");
		
		String hexstartTime = KaiYanUtil.encodeTime(startTime);
		String hexendTime = KaiYanUtil.encodeTime(endTime);
		
		int intInterval = Integer.parseInt(interval);
		
		if(intInterval<=0){
			CRC = KaiYanUtil.getCRC("21020312" + hexstartTime + hexendTime +"0000"+"012102");
			cmd = head + "21020312" + hexstartTime + hexendTime +"0000"+"012102"+ CRC + end; 
		}else{
			String hex = Tools.int2Hexstring(intInterval, 4);
			
			String hexInterval = hex.substring(2, 4)+hex.substring(0, 2);
			
			CRC = KaiYanUtil.getCRC("21020312" + hexstartTime + hexendTime + hexInterval + "012102");
			cmd = head + "21020312" + hexstartTime + hexendTime + hexInterval + "012102"+CRC + end;
		}
		
		
		return cmd;
	}

	public static void main(String aft[]){ }
	
}
