/**
 * 
 */
package com.mapabc.gater.directl.encode.lingtu;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

 
import com.mapabc.gater.directl.Base64;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;


/**
 * @author shiguang.zhou
 *
 */
public class LingtuControl extends ControlAdapter{
	String head = "C M ";
	String end = "\r\n";
	
	String cmdSeq = Tools.getRandomString(4);
	
	// 调度信息
	public String msg(Request req) {
		String ret = null;

		 try {
			 RequestUtil.getDealRequest(req, "300", cmdSeq+"");
				 
				 String deviceid =req.getDeviceId();
				 String content = RequestUtil.getReqData(req, "content");
			 
				byte[] msgByte = content.trim().getBytes("UTF-16");
				byte cpyByte[] = new byte[msgByte.length-2];
				System.arraycopy(msgByte, 2, cpyByte, 0, msgByte.length-2);
				String base64str =  Base64.base64encode(cpyByte).trim();
				head = head + cmdSeq + " ";
				 
				String cmd = LingtuUtil.createOemCodeBySn(deviceid) + ":" +  deviceid + "|10|5;" + base64str + " ";
				String vcode = Tools.getVerfyCode(cmd.getBytes());
				ret = head + cmd + vcode + end;
				 
				ret = Tools.bytesToHexString(ret.getBytes());
				
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return ret;
	}

	// 油路控制
	public String oilElecControl(Request req) {
		 
    	String ret = "";
    	String deviceid =req.getDeviceId();
     	String type = RequestUtil.getReqData(req, "type");
     	RequestUtil.getDealRequest(req, "304", cmdSeq);
    	//C M f 4C54:1001|100|0 3B5
		 

		head = head + cmdSeq + " ";
 
		String cmd = LingtuUtil.createOemCodeBySn(deviceid) + ":" + deviceid + "|100|" + type + " ";
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode+ end;
 
		  ret = Tools.bytesToHexString(ret.getBytes());

		return ret;
	}
	
	//修改为紧急报警监听
	public String listen(Request req) {
		String ret = "";
    	String deviceid =req.getDeviceId();
     	String num = RequestUtil.getReqData(req, "callBackNumber");
     	RequestUtil.getDealRequest(req, "222", cmdSeq);
    	//C M f 4C54:1001|100|0 3B5
		 

		head = head + cmdSeq + " ";
 
		String cmd = LingtuUtil.createOemCodeBySn(deviceid) + ":" + deviceid + "|222|" + num + " ";
		String vcode = Tools.getVerfyCode(cmd.getBytes());
		ret = head + cmd + vcode+ end;
 
		  ret = Tools.bytesToHexString(ret.getBytes());

		return ret;
	}
	 
}
