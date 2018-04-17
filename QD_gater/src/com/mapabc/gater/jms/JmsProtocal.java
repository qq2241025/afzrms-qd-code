/**
 * 
 */
package com.mapabc.gater.jms;

import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.lbsgateway.GBLTerminalList;
 

/**
 * @author 
 *
 */
public class JmsProtocal {

	// 转发到JMS的格式
	public  static String makeJMSInfo(ParseBase base) {
		// 标识，1，设备ID,经度,纬度,速度,方向,时间,类型,位置描述长度,位置描述,状态
//		TTerminal terminal = GBLTerminalList.getInstance().getTerminaInfo(parse.getDEVICE_ID());
//		if (terminal == null){
//			String deviceType = terminal.getTEntTermtype();
//			if (deviceType != null && (deviceType.equals("GP-DOOG-GPRS")||deviceType.equals("GP-BAJU-GPRS"))){
//				return  makeBaJuJMSInfo(base);
//			}
//		} else 
		{
			String posDes = "";
			posDes = base.getAddress();
			if (posDes == null)
				posDes = "";

			String ret = "";
			ret ="LOC,1,"+ base.getDeviceSN() + "," + base.getCoordX() + ","
					+ base.getCoordY() + "," + base.getSpeed();
			ret += "," + base.getDirection() + "," + base.getTime() + ","
					+ base.getLocateType() + "," + posDes.getBytes().length
					+ "," + posDes + ",[" + /**base.getExtend3() +*/ "]\r\n";

			return ret;
		}
	}

	// 转发到JMS的格式
	public static  String makeBaJuJMSInfo(ParseBase base) {
		// SIM卡卡号,经度,纬度,速度,方向,时间,类型,位置描述长度,位置描述,状态
		// String isLocDesc = Config.getInstance().getString("isLocateDesc");
		String posDes = "";
		if (null != base.getAddress()) {
			posDes = base.getAddress();
		}
		// try {
		// if (isLocDesc != null && isLocDesc.equals("0")) {
		// GPSLocateInfoFiler filler = GPSLocateInfoFiler.getInstance();
		// ReverseGisCode locInfo = ReverseGisCode.getInstance();
		// if (filler.IsNeedLocateInfo(phnum, locInfo.GetTimeInterval())) {
		// ReverseGisCode li = ReverseGisCode.getInstance();
		// String x = base.getCoordX();
		// String y = base.getCoordY();
		// posDes = li.getAddress(x, y);
		// filler.SetLocInfo(phnum, posDes);
		// } else {
		// posDes = filler.GetLocInfo(phnum);
		// }
		// }
		// } catch (Exception ee) {
		// if (posDes == null)
		// posDes = "";
		// }

		String ret = "";
		ret = "LOC,1,"+base.getDeviceSN() + "," + base.getCoordX() + ","
				+ base.getCoordY() + "," + base.getSpeed();
		ret += "," + base.getDirection() + "," + base.getTime() + ",0,"
				+ posDes.getBytes().length + "," + posDes + ","
				+/** base.getStatus() +*/ "\r\n";

		return ret;
	}

	// 报警信息转发到JMS的格式
	public static  String makeAlarmJMSInfo(ParseBase base) {
		// 标识，记录条数，设备ID，报警类型，报警信息描述(保存超速值或区域ID)，经度，纬度，速度，方向，高度，里程，时间，位置描述长度，位置描述，状态，应用信息，案例ID
		String posDes = "";
		posDes = base.getAddress();
		if (posDes == null)
			posDes = "";

		String ret = "";
		ret = "ALARM,1,"+base.getDeviceSN() + "," + base.getAlarmType() + ","
				+ base.getAlarmSubType() + "," + base.getSpeedThreshold() + ","
				+ base.getAreaNo() + "," + base.getCoordX() + ","
				+ base.getCoordY() + "," + base.getSpeed();
		ret += "," + base.getDirection() + "," + base.getTime() + ","
				+ base.getLocateType() + "," + posDes.getBytes().length + ","
				+ posDes + ",[" +/** base.getExtend3()+*/  "]\r\n";

		return ret;
	}

	// 调度信息转发
	public static  String makeDispatchJMSInfo(ParseBase base) {
		// DISPATCH, recordCount ,deviceid,message,disDate\r\n
		String ret = null;
		String msg = "";//base.getDispatchMsg();
		if (msg == null)
			msg = "";

		ret = "DISPATCH,1,"+base.getDeviceSN() + "," + msg.length() + "," + msg + ","
				+ base.getTime() + "\r\n";
		return ret;
	}

	// 设置反馈转发
	public static  String makeCtrlResJMSInfo(ParseBase base) {
		// CTRL, recordCount ,deviceid,optType,optResult\r\n
		String ret = null;

//		ret = "CTRL,1,"+base.getDeviceSN() + "," + base.getCmdId() + ","
//				+ base.getReply() + "," + base.getTime() + "\r\n";

		return ret;
	}

	// 设置状态转发
	public static  String makeStatusResJMSInfo(ParseBase base) {
		// CTRL, recordCount ,deviceid,optType,optResult\r\n
		String ret = null;

		//ret = "STATUS,1,"+base.getDeviceSN() + ",[" + base.getStatus() + "]\r\n";

		return ret;
	}

}
