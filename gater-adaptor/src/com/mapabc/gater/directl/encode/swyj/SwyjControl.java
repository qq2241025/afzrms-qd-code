/**
 * 
 */
package com.mapabc.gater.directl.encode.swyj;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;



/**
 * @author shiguang.zhou
 *
 */
public class SwyjControl extends ControlAdapter{
	private String head = "*HQ,";
	private String end = "#";
	
	// 调度信息
	public String msg(Request req) {
		//*XX,YYYYYYYYYY,I1,HHMMSS,Display_Time,Code,Info_lenth,Information
		String ret = null;
	 
		String deviceid = req.getDeviceId();
		String content = RequestUtil.getReqData(req, "content");
		
		String msgHex = "";
		try {
			int len = content.length();
			for (int i=0; i<len; i++){
				String word = content.substring(i,i+1);
				if (word.getBytes().length==2){
					msgHex += Tools.bytesToHexString(word.getBytes("GB2312"));
				}else{
					msgHex += "00"+Tools.bytesToHexString(word.getBytes());
				}
			}
			//msgGB = new String(content.getBytes("GB2312"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ret = head+deviceid+",I1,"+Tools.getCurHMS()+",0,0,"+ msgHex.length()/2+",";
		String hex = Tools.bytesToHexString(ret.getBytes())+msgHex;
		//Log.getInstance().tianheLog(ret+content);
		return hex;
	}

	// 油路控制
	public String oilElecControl(Request req) {
		 
		String ret = "";
 
		String deviceid = req.getDeviceId() ;
		String deviceType = req.getDeviceType();
//		if (deviceType != null && deviceType.equals("GP-TIANHE-GPRS")){
//			head = "*TH,";
//		}
		String type = RequestUtil.getReqData(req, "type");
		if (type.equals("0")) {// 断油电
			ret = head + deviceid + ",S20," + Tools.getCurHMS()
					+ ",1,5" + end;
		} else {// 恢复
			ret = head + deviceid + ",S20," + Tools.getCurHMS() + ",1,0" + end;
		}//Log.getInstance().tianheLog(ret);
		String hex = Tools.bytesToHexString(ret.getBytes());
		return hex;
	}
	
	public String listen(Request req) {
		//*XX,YYYYYYYYYY,R8,HHMMSS,listen_address #,要求电话卡要具有通话功能
	 
		String deviceid = req.getDeviceId() ;
		String callBackNumber = RequestUtil.getReqData(req, "callBackNumber");
		String ret = head+deviceid+",R8,"+Tools.getCurHMS()+","+callBackNumber+end;
		String hex = Tools.bytesToHexString(ret.getBytes());
		//Log.getInstance().tianheLog(ret);
		return hex;
	}
	
	public String remoteControl(Request req) {
		//*XX,YYYYYYYYYY,S25,HHMMSS#


		String deviceid = req.getDeviceId();
		String ret = head+deviceid+",S25,"+Tools.getCurHMS()+end;
        String hex = Tools.bytesToHexString(ret.getBytes());//Log.getInstance().tianheLog(ret);
        
         return hex;
	}
	

	public static void main(String[] args){
		String content = "测试数值3加混合w";
		String msgHex  = "";
		try {
			int len = content.length();
			for (int i=0; i<len; i++){
				String word = content.substring(i,i+1);
				if (word.getBytes().length==2){
					msgHex += Tools.bytesToHexString(word.getBytes("GB2312"));
				}else{
					msgHex += "00"+Tools.bytesToHexString(word.getBytes());
				}
			}
			//msgGB = new String(content.getBytes("GB2312"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 
}
