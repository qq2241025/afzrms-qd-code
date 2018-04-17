package com.mapabc.gater.directl.encode.OBU;
import java.util.HashMap;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.LocatorAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;

/**
 * @author chen.peng
 * 
 */
public class OBULocator extends LocatorAdapter {

	public String timeInter(Request req) {
		String cmd = "";
		 
		String interval = RequestUtil.getReqData(req, "interval");
		if(interval.equals("0")){
			//Log.getInstance().obitsLog("中心设置OBU终端----->"+req.getDeviceId()+"--------------停止按时间回传");
		}else{
 		}
		interval = Tools.int2Hexstring(Integer.parseInt(interval), 4);
		byte[] byteInter =Tools.convertBytePos(Tools.fromHexString(interval));
		 
		 
		//按时间
		String parameter0 = "3001fe";
		//SIM1空车
		String parameter2 = "3102"+Tools.bytesToHexString(byteInter);
		//SIM1重车
		String parameter1 = "3202"+Tools.bytesToHexString(byteInter);

		//SIM2空车
		String parameter3 = "b302"+Tools.bytesToHexString(byteInter);
		//sim2重车
		String parameter4 = "b402"+Tools.bytesToHexString(byteInter);
		//SIM1未登陆时位置间隔
		String parameter5 = "3402"+Tools.bytesToHexString(byteInter);
		String parameter6 = "b502"+Tools.bytesToHexString(byteInter);
 		
		String parameter = parameter0+parameter1+parameter2+parameter3+parameter4+parameter5+parameter6;
		//Log.getInstance().obitsLog(req.getDeviceId()+" 时间间隔相关参数："+parameter);
		cmd = OBUUtil.downParameterSet(req.getDeviceId(), 7, parameter);
 		return cmd;
	}

	public String locate(Request req) {
	 		
		String cmd = "8f00";
		return cmd;
		
	}

	public String distanceInter(Request req) {
 		String interval = RequestUtil.getReqData(req, "interval");
		if(interval.equals("0")){
			//Log.getInstance().obitsLog("中心设置OBU终端----->"+req.getDeviceId()+"--------------停止按距离回传");
		}else{
			//Log.getInstance().obitsLog("中心设置OBU终端----->"+req.getDeviceId()+"--------------按时间回传，interval："+"/米");
		}
		return null;
	}
	public static void main(String aft[]){
		
	}
	
}
