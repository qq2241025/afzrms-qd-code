/**
 * 
 */
package com.mapabc.gater.directl.encode.ucst;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.QueryAdaptor;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;


/**
 * @author shiguang.zhou
 * 
 */
public class UcstQuery extends QueryAdaptor {
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
	public String findDistances(Request req) {
		return null;
	}
	
	//疑点数据查询
	public String queryDoubt(Request req) {
		 
		
		String no = RequestUtil.getReqData(req, "doubtNo");
		//String dt = RequestUtil.getReqData(req, "time");
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String protocalNo = "10";
		String protocalCont = Tools.convertToHex(no, 2) ;//+ dt;
        byte[] protocalBCont = Tools.fromHexString(protocalCont);
		
        String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, protocalNo, protocalBCont);

		//Log.getInstance().ucstLog(deviceId + " 疑点数据查询指令：" + hex);

		return hex;
 	 
	}
 
	//驾驶员身份信息查询
	public String findDriverInfo(Request req) {
		String seqs = RequestUtil.getReqData(req, "sequence");
		 
		
		String deviceId = req.getDeviceId();
		String seq = Tools.getRandomString(2);
		String protocalNo = "12";
 
		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, protocalNo, null);

		//Log.getInstance().ucstLog(deviceId + " 查询驾驶员身份指令：" + hex);

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
		byte[] paramByte = Tools.fromHexString("01"+paramHex);
 		
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
 			 
 			break;
		case 21:
		 
 			break;
		case 26:
			 
 			break;
 		}
		//req.setCmdId("1a"+paramHex);
		
		String hex = UcstProtocalUtil.createMtProtocal(seq, oemCode,
				deviceId, centerId, deviceId, protocalNo, paramByte);

		//Log.getInstance().ucstLog(deviceId + " 查询基本参数指令：" + hex);

		return hex;
	}

}
