/**
 * 
 */
package com.mapabc.gater.directl.encode.tjzh;

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
public class TjzhControl extends ControlAdapter {

	private String head = "2929";
	private String end = "0d";

	private String getFormatDeSN(String deviceid) {
		String dsn = "";
		if (deviceid != null && deviceid.trim().length() > 0) {// 此处待定
			String[] dsns = deviceid.split("\\.");
			for (int i = 0; i < dsns.length; i++) {
				dsn += Tools.convertToHex(dsns[i], 2);
			}
		}
		return dsn;
	}

	// 调度信息
	public String msg(Request req) {

		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("3A");

		String deviceid = this.getFormatDeSN(req.getDeviceId());
		String content = RequestUtil.getReqData(req, "content");
		String hexCont = "";
		 
			try {
				hexCont = Tools.bytesToHexString(content.getBytes("GB2312"));
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//Log.getInstance().errorLog(null, e);
			}
			//log.info("下发调度信息给"+req.getDeviceId()+",内容为："+new String(Tools.fromHexString(hexCont)));
 
		int length = 6 + content.getBytes().length;
		String hexLen = Tools.int2Hexstring(length, 4);

		buffer.append(hexLen);
		buffer.append(deviceid);
		buffer.append(hexCont);

		byte verfyCode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		String vc = Tools.bytesToHexString(new byte[] { verfyCode });
		buffer.append(vc);

		buffer.append(end);
		
		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret = buffer.toString().toUpperCase();//new String(cmd);

		return ret;
	}

	// 油路控制
	public String oilElecControl(Request req) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(head);

		String deviceid = this.getFormatDeSN(req.getDeviceId());
		String cmdType = "";

		String type = RequestUtil.getReqData(req, "type");
		if (type.trim().length() > 0) {
			if (type.trim().equals("0")) {//恢复
				cmdType = "38";
			} else {//断开
				cmdType = "39";
			}
		}
		buffer.append(cmdType);
		buffer.append("0006");
		buffer.append(deviceid);

		byte verfyCode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		String vc = Tools.bytesToHexString(new byte[] { verfyCode });
		buffer.append(vc);

		buffer.append(end);
		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret = buffer.toString().toUpperCase();//new String(cmd);

		return ret;
	}

	// 锁门控制
	public String switchDoorControl(Request req) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(head);

		String deviceid = this.getFormatDeSN(req.getDeviceId());
		String cmdType = "";

		String type = RequestUtil.getReqData(req, "type");
		if (type.trim().length() > 0) {
			if (type.trim().equals("0")) {
				cmdType = "68";
			} else {
				cmdType = "67";
			}
		}
		buffer.append(cmdType);
		buffer.append("0006");
		buffer.append(deviceid);

		byte verfyCode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		String vc = Tools.bytesToHexString(new byte[] { verfyCode });
		buffer.append(vc);

		buffer.append(end);
		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret = buffer.toString().toUpperCase();//new String(cmd);

		return ret;
	}

	// 通话限制
	public String callRestrict(Request req) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("24");
		buffer.append("0007");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));

		String type = RequestUtil.getReqData(req, "type");
		buffer.append(Tools.convertToHex(type, 2));

		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { vcode }));
		buffer.append(end);

		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		String ret = buffer.toString().toUpperCase();//new String(cmd);

		return ret;
	}

	// 监听
	public String listen(Request req) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("3E");
		String backNumber = RequestUtil.getReqData(req, "callBackNumber");
		if (backNumber.length() <= 0) {
			//log.info("监听号码为空。");
			return null;
		}
		if (backNumber.length() % 2 == 0) {
			backNumber += "FF";
		} else {
			backNumber += "F";
		}
		buffer.append(Tools.int2Hexstring(backNumber.length() / 2 + 6, 4));
		buffer.append(this.getFormatDeSN(req.getDeviceId()));
		buffer.append(backNumber);
		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { vcode }));
		buffer.append(end);

		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
		
		String ret = buffer.toString().toUpperCase();//new String(cmd);
		//log.info("监听指令："+ret);
		return ret;
	}
	
   //远程升级请求
	public String remoteLoading(Request req) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("62");
		buffer.append("0006");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));
		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { vcode }));
		buffer.append(end);
 		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
 		String ret = buffer.toString().toUpperCase();//new String(cmd);

		return ret;

	}
	//休眠控制
	public String sleep(Request req) {
		StringBuffer buffer = new StringBuffer();

		buffer.append(head);
		buffer.append("3B");
		buffer.append("0009");
		buffer.append(this.getFormatDeSN(req.getDeviceId()));
		String sleepTime = RequestUtil.getReqData(req, "time");//休眠时间
		String sleepFlag = RequestUtil.getReqData(req, "flag");//ACC关闭后是否休眠
		if(sleepTime.trim().length()<=0)
			sleepTime= "0";
		String hexTime = Tools.convertToHex(sleepTime, 4);
		String hexFlag = Tools.convertToHex(sleepFlag, 2);
		buffer.append(hexTime);
		buffer.append(hexFlag);
		
		byte vcode = Tools.checkData(Tools.fromHexString(buffer.toString()));
		buffer.append(Tools.bytesToHexString(new byte[] { vcode }));
		buffer.append(end);
 		byte[] cmd = Tools.fromHexString(buffer.toString().toUpperCase());
 		String ret = buffer.toString().toUpperCase();//new String(cmd);

		return ret;
	}
	
	public String reset(Request req) {
		// TODO Auto-generated method stub
		String cmd = "";
		cmd = head+"320006"+this.getFormatDeSN(req.getDeviceId());
		byte vcode = Tools.checkData(Tools.fromHexString(cmd));
		
		cmd += Tools.bytesToHexString(new byte[] { vcode });
		
		cmd += end;
		
		
		return cmd;
	}
	
	

	public static void main(String[] args) {
		String s = "29293e000c0286db5a13602069191f0b0d";
		System.out.println(Tools.bytesToHexString(s.getBytes()));
		 
	}

}
