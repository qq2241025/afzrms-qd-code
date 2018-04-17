/**
 * 
 */
package com.mapabc.gater.directl.encode.guomai;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.constant.StructionType;
import com.mapabc.gater.directl.encode.QueryAdaptor;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;

/**
 * @author shiguang.zhou
 * 
 */
public class GuoMaiQuery extends QueryAdaptor {
	private String centerId = "1";
	private String centerPwd = "";
	private String oemCode = "1";

	// 查看车辆工作状态
	public String workStatus(Request req) {
		StringBuffer buffer = new StringBuffer();

		String ret = buffer.toString().toUpperCase();

		return ret;
	}

	//里程查询
	/*public String findDistances(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		if (seqs != ""){
			long sequence = Long.parseLong(seqs);
			req.setSequence(sequence);
		}
		String isSynchs = RequestUtil.getReqData(req, "issynch");
		if (isSynchs.equals("0")){
			req.setSynch(false);
		}
		if (isSynchs.equals("1")){
			req.setSynch(true);
		}
		//req.setCmdType("402");
		//req.setCmdId("f4");
		
		
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String protocalNo = "f4";
 
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, protocalNo, null);

		//Log.getInstance().guomaiLog(deviceId + " 10天里程查询指令：" + hex);

		return hex;
	}*/
	
	//疑点数据查询
	public String queryDoubt(Request req) {
		 
		String no = RequestUtil.getReqData(req, "doubtNo");
		String dt = RequestUtil.getReqData(req, "time");
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String protocalNo = "10";
		String protocalCont = Tools.convertToHex(no, 2) + dt;
        byte[] protocalBCont = Tools.fromHexString(protocalCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
        String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, protocalNo, protocalBCont);

		//Log.getInstance().guomaiLog(deviceId + " 疑点数据查询指令：" + hex);

		return hex;
 	 
	}
 
	//驾驶员身份信息查询
	public String findDriverInfo(Request req) {
		 
		
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String protocalNo = "12";
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, protocalNo, null);

		//Log.getInstance().guomaiLog(deviceId + " 查询驾驶员身份指令：" + hex);

		return hex;
	}

	 //查询当前GPS统计的总里程。(UCAST无此功能)
	public String findDistances(Request req){
		 
		
		
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String protocalNo = "f8";
		
		String ptlCont = Tools.convertToHex("1", 2);
		byte[] ptlByte = Tools.fromHexString(ptlCont);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, protocalNo,ptlByte);

		//Log.getInstance().guomaiLog(deviceId + " 查询当前GPS统计的总里程指令：" + hex);

		return hex;

	}
	
	public String queryParam(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
 		
		
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String protocalNo = "1a";
		String param = RequestUtil.getReqData(req, "paramType");
		int type = Integer.parseInt(param);
		
		String paramHex = Tools.convertToHex(param, 2);
		byte[] paramByte = Tools.fromHexString(paramHex);
		
		
		
		String sparam = null;
		switch (type) {
		case 1:
			//req.setCmdType("404");
 			break;
		case 2:
			//req.setCmdType("405");
 			break;
		case 3:
			//req.setCmdType("406");
 			break;
		case 4:
			//req.setCmdType("407");
	 			break;
		case 5:
			//req.setCmdType("408");
 			break;
		case 6:
			//req.setCmdType("412");
 			break;
		case 7:
			//req.setCmdType("413");
 			break;
		case 8:
			//req.setCmdType("409");
 			break;
		case 9:
			//req.setCmdType("410");
 			break;
		case 10:
			//req.setCmdType("411");
			break;
 		case 11:
			break;
		case 12:
			//req.setCmdType("415");
			break;
		case 13:
			break;
		case 15:
			//req.setCmdType("414");
 			break;
 		}
		//req.setCmdId("1a"+paramHex);
		String typeCode = req.getDeviceType();
		
		if(typeCode.equals("GP-UCSTC-GPRS")){
			centerPwd = Integer.parseInt("000007D9", 16)+"";
		}else{
			centerPwd = deviceId;
		}
		String hex = GuoMaiProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, centerPwd, protocalNo, paramByte);

		//Log.getInstance().guomaiLog(deviceId + " 查询基本参数指令：" + hex);

		return hex;
	}

}
