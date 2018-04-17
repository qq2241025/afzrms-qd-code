package com.mapabc.gater.directl.encode.kaiyan;

/**
 * 
 */


import java.nio.ByteBuffer;
import java.util.HashMap;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;


/**
 * @author peng.chen
 *
 */
public class KaiYanSetting extends SettingAdaptor {
	String head = "fe";
	String end = "ff";

	public String addrSetting(Request req) {
		
		
		//0x31  0x01   子功能数据量         数据长度            IP地址         TCP端口号       UDP端口号          APN     CRC
		//fe31 01 00 26 313CF76716 20 1B59 00FFFF 00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF 63 ff
		//fe31 01 00 26 00FFFFFFFF 20 1B59 00FFFF 00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF 3D ff
		//fe31 01 00 26 00FFFFFFFF 00 FFFF 2015BE 00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF DB ff
 		String cmd ="";
		String CRC ="";
		
		String ip = RequestUtil.getReqData(req, "ip");
		String ipCmd = KaiYanUtil.encodeIP(ip);
		
		String port = RequestUtil.getReqData(req, "port");
		String portCmd = Tools.int2Hexstring(Integer.parseInt(port), 4).toUpperCase();
		String type = RequestUtil.getReqData(req, "type");
		
		String apncmd = "00FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
		
		//TCP
		if(type.equals("0")){
			
			if(ip.equals("0")){
				//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------TCP端口值");
				CRC = KaiYanUtil.getCRC("31010329" +"00" +"FFFFFFFF" + "20" +portCmd +"00FFFF" + apncmd +"013101");
				cmd = head +"31010329" +"00" +"FFFFFFFF" + "20" +portCmd +"00FFFF" + apncmd +"013101"+CRC +end;
			}else{
				//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------IP地址和TCP，端口值");
				CRC = KaiYanUtil.getCRC("31010329" +"31" +ipCmd + "20" +portCmd +"00FFFF" + apncmd +"013101");
				cmd = head +"31010329" +"31" +ipCmd + "20" +portCmd +"00FFFF" + apncmd +"013101"+CRC +end;
			}
		
		}
		//udp
		if(type.equals("1")){
			if(ip.equals("0")){
				//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------UDP端口值");
				CRC = KaiYanUtil.getCRC("31010329" +"00" +"FFFFFFFF" +"00FFFF" + "20" +portCmd + apncmd +"013101");
				cmd = head +"31010329" +"00" +"FFFFFFFF" +"00FFFF" + "20" +portCmd + apncmd +"013101"+CRC +end;
			}else{
				//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------IP地址和UDP端口值");
				CRC = KaiYanUtil.getCRC("31010329" +"31" +ipCmd +"00FFFF" + "20" +portCmd + apncmd +"013101");
				cmd = head +"31010329" +"31" +ipCmd +"00FFFF" + "20" +portCmd + apncmd +"013101"+CRC +end;
			}
			
		}
		
		
		return cmd;
	}

	public String apnSetting(Request req) {
		//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------APN值");
		//FE 31 01 00 26 00 FFFFFFFF 00 FFFF 00 FFFF 20636D6363FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF 76 FF
	 		String cmd = "";
		String CRC = "";
		
		String apns = RequestUtil.getReqData(req, "apn");
		if(apns.split(";").length>1){
			//Log.getInstance().kaiyanLog("中心设置开研终端------->"+req.getDeviceId()+"APN,终端只支持设置一个。");
			return null;
		}
		
		String apnCmd = "20" + Tools.bytesToHexString(apns.getBytes());
		
		while(apnCmd.length()<44){
			apnCmd += "FF";
		}
		
		CRC = KaiYanUtil.getCRC("31010329" +"00"+"FFFFFFFF" +"00"+"FFFF"+"00"+"FFFF"+apnCmd +"013101");
		cmd = head + "31010329" +"00"+"FFFFFFFF" +"00"+"FFFF"+"00"+"FFFF"+apnCmd +"013101"+ CRC+end;
		return cmd;
	}

	@Override
	public String modeSetting(Request req) {
		//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------通讯模式设置");
		//RequestUtil.getDealRequest(req, StructionType.CAR_ID_SET_CMD_TYPE, "3124");
		String cmd = "";
		String CRC = "";
		String type = RequestUtil.getReqData(req, "type");
		if(type.equals("0")){
			CRC = KaiYanUtil.getCRC("31240309"+"31"+"013124");
			cmd = head+"31240309"+"31"+"013124"+CRC+end;
		}else{
			CRC = KaiYanUtil.getCRC("31240309"+"FF"+"013124");
			cmd = head+"31240309"+"FF"+"013124"+CRC+end;
		}
		
		return cmd;
	}

	public String vehicleIdSetting(Request req) {
		//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------终端ID设置");
		//FE 31 05 00 10 3031353331333233363437 31 FF
		//RequestUtil.getDealRequest(req, StructionType.CAR_ID_SET_CMD_TYPE, "3105");
		
		String newId = RequestUtil.getReqData(req, "id");
		String cmd = "";
		String CRC = "";
		
		if(newId.equals("")){
			//Log.getInstance().kaiyanLog("中心设置开研终端------->"+req.getDeviceId()+"的ID，但ID参数为空。");
			return null;
		}
		while(newId.length()<11){
			newId  = "0" + newId;
		}
		CRC = KaiYanUtil.getCRC("31050313" +Tools.bytesToHexString(newId.getBytes())+"013105");
		cmd = head+"31050313" +Tools.bytesToHexString(newId.getBytes())+"013105"+CRC+end;
		return cmd;
	}
	
	public String carIdSetting(Request req) {
		//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------终端ID设置");
		//FE 31 05 00 10 3031353331333233363437 31 FF
		//RequestUtil.getDealRequest(req, StructionType.CAR_ID_SET_CMD_TYPE, "3105");
		
		String newId = RequestUtil.getReqData(req, "newId");
		String cmd = "";
		String CRC = "";
		
		if(newId.equals("")){
			//Log.getInstance().kaiyanLog("中心设置开研终端------->"+req.getDeviceId()+"的ID，但ID参数为空。");
			return null;
		}
		while(newId.length()<11){
			newId  = "0" + newId;
		}
		CRC = KaiYanUtil.getCRC("31050313" +Tools.bytesToHexString(newId.getBytes())+"013105");
		cmd = head+"31050313" +Tools.bytesToHexString(newId.getBytes())+"013105"+CRC+end;
		return cmd;
	}
	
	public String temperatureInterSetting(Request req){
		
		String cmd = "";
		String CRC = "";
		
		String interval = RequestUtil.getReqData(req, "interval");
		
		if(interval.equals("0")){
			//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------停止温度定时回传。");
			CRC = KaiYanUtil.getCRC("D10203"+"0A"+"0000"+"01D102");
			cmd = head+"D10203"+"0A"+"0000"+"01D102"+CRC+end;
		}
		
		String hexInterval = Tools.int2Hexstring(Integer.parseInt(interval), 4).toUpperCase();
		
		String temperatureCmd = hexInterval.substring(2, 4)+hexInterval.substring(0, 2);
		
		CRC = KaiYanUtil.getCRC("D10203" +"0A"+temperatureCmd+"01D102");
		cmd = head +"D10203" +"0A"+temperatureCmd+"01D102"+CRC+end;
		//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------温度定时回传。时间间隔为："+interval);
		//fe D1 02 03 0A 1E00 01D102 32 ff
		//fe D1 02 03 0A 0000 01D102 ec ff
		return cmd;
	}
	
	public String oilInterSetting(Request req){
		
		String cmd = "";
		String CRC = "";

		String interval = RequestUtil.getReqData(req, "interval");
		
		if(interval.equals("0")){
			//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------停止油量按时回传");
			CRC = KaiYanUtil.getCRC("D10403"+"0A"+"0000"+"01D104");
			cmd = head+"D10403"+"0A"+"0000"+"01D104"+CRC+end;
		}
		
		String hexInterval = Tools.int2Hexstring(Integer.parseInt(interval), 4).toUpperCase();
		
		String oilCmd = hexInterval.substring(2, 4)+hexInterval.substring(0, 2);
		
		CRC = KaiYanUtil.getCRC("D10403" +"0A"+oilCmd+"01D104");
		cmd = head +"D10403" +"0A"+oilCmd+"01D104"+CRC+end;
		//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------油量按时回传，时间间隔为："+interval);
		//fe D1 04 03 0A 1E00 01D104 7c ff
		
		return cmd;
	}
	
	@Override
	public String camera(Request req) {
		
		//RequestUtil.getDealRequest(req, StructionType.CAMERA_SET_COMMOND_TYPE, "3105");
		
		String funType = RequestUtil.getReqData(req, "funType");
		String cameraType = RequestUtil.getReqData(req, "cameraType");
		String chanel = RequestUtil.getReqData(req, "chanel");
		String chanelCnt = RequestUtil.getReqData(req, "chanelCnt");
		String interval = RequestUtil.getReqData(req, "interval");
		String count = RequestUtil.getReqData(req, "count");
		String pixel = RequestUtil.getReqData(req, "pixel");
		String condition = RequestUtil.getReqData(req, "condition");
		String isupload = RequestUtil.getReqData(req, "isupload");
		String startTime = RequestUtil.getReqData(req, "startDate");
		String endTime = RequestUtil.getReqData(req, "endDate");
		//String saveInterval = RequestUtil.getReqData(req, "saveInterval");
		/*if(!startTime.equals("")||!endTime.equals("")){
			//Log.getInstance().kaiyanLog("中心设置开研终端------------------>"+req.getDeviceId()+"拍照功能，但终端不支持该参数(isupload,startTime,endTime)");
		}*/
		String cmd = "";
		String CRC = "";
		//拍照设置
		if(funType.equals("1")){
			//单张拍照
			if(cameraType.equals("1")){
				//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------单张拍照设置");
				//RequestUtil.getDealRequest(req, StructionType.CAMERA_SET_COMMOND_TYPE, "7101");
				String hexPixel = Tools.int2Hexstring(Integer.parseInt(pixel), 2);
				
				CRC = KaiYanUtil.getCRC("71010309" + hexPixel +"017101");
				cmd = head + "71010309" + hexPixel +"017101" +CRC +end;
				//fe 71 01 03 09 02 017101 e1 ff
				return cmd;
			}
			//定时拍照
			if(cameraType.equals("2")){
				//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------定时拍照设置。");
				//RequestUtil.getDealRequest(req, StructionType.CAMERA_SET_COMMOND_TYPE, "7102");
				if(interval.equals("0")||interval.equals("255")){
					//Log.getInstance().kaiyanLog("中心设置开研终端------------------>"+req.getDeviceId()+"拍照功能，但终端不支持该参数。");
					return null;
					
				}

				String hexInterval = Tools.int2Hexstring(Integer.parseInt(interval), 8);
				String reverse = hexInterval.substring(6, 8)+hexInterval.substring(4, 6)+hexInterval.substring(2, 4)+hexInterval.substring(0, 2);
				String hexPixel = Tools.int2Hexstring(Integer.parseInt(pixel), 2);
				CRC = KaiYanUtil.getCRC("7102030D"+reverse+hexPixel+"017102");
				cmd = head+"7102030D"+reverse+hexPixel+"017102"+CRC+end;
				return cmd;
			}
		}
		//照片提取
		if(funType.equals("2")){
			//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------提取保存照片数据");
			//RequestUtil.getDealRequest(req, StructionType.CAMERA_SET_COMMOND_TYPE, "7103");
			String formatStartTime = KaiYanUtil.encodeTime(startTime);
			String formatEndTime =  KaiYanUtil.encodeTime(endTime);
			String formatInterval = Tools.int2Hexstring(Integer.parseInt(interval), 4).substring(2, 4)+Tools.int2Hexstring(Integer.parseInt(interval), 4).substring(0, 2);
			
			CRC = KaiYanUtil.getCRC("71030312"+formatStartTime+formatEndTime+formatInterval+"017103");
			cmd = head + "71030312"+formatStartTime+formatEndTime+formatInterval+"017103"+CRC+end;
			//fe 71 03 03 12 10980714 30b40714 1e00 017103 07 ff
			return cmd;
		}
		//摄像头参数设置
		if(funType.equals("3")){
			//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------自动拍照参数设置");
			//RequestUtil.getDealRequest(req, StructionType.CAMERA_SET_COMMOND_TYPE, "7104");
			String formatCondition = "";
			String formatIsupload = "";
			String formatSaveInterval = "";
			String formatSaveCount = "";
			//点火拍照
			if(condition.equals("1")){
				//Log.getInstance().kaiyanLog("中心设置开研终端自动拍照设置，终端不支持，点火拍照！");
				return null;
			}
			//劫警拍照
			if(condition.equals("2")){
				formatCondition = "07";
			}
			//超时拍照
			if(condition.equals("3")){
				//Log.getInstance().kaiyanLog("中心设置开研终端自动拍照设置，终端不支持，超时拍照！");
				return null;
			}
			//进区域报警拍照
			if(condition.equals("4")){
				//Log.getInstance().kaiyanLog("中心设置开研终端自动拍照设置，终端不支持，进区域拍照！");
				return null;
			}
			//出区域报警拍照
			if(condition.equals("5")){
				//Log.getInstance().kaiyanLog("中心设置开研终端自动拍照设置，终端不支持，出区域拍照！");
				return null;
			}
			//车门开（配合车辆速度）拍照
			if(condition.equals("6")){
				formatCondition = "05";
			}
			//7 防盗报警拍照
			if(condition.equals("7")){
				formatCondition = "08";
			}
			//8超速报警拍照
			if(condition.equals("8")){
				formatCondition = "09";
			}
			//9门开到关拍照
			if(condition.equals("9")){
				formatCondition = "06";
			}
			//10门关到开拍照
			if(condition.equals("10")){
				formatCondition = "05";
			}
			//11ACC由开到关拍照
			if(condition.equals("11")){
				formatCondition = "04";
			}
			//12ACC由关到开拍照
			if(condition.equals("12")){
				formatCondition = "03";
			}
			//13空到重拍照
			if(condition.equals("13")){
				formatCondition = "02";
			}
			//14重到空拍照
			if(condition.equals("14")){
				formatCondition = "01";
			}
			//
			if(isupload.equals("0")){
				formatIsupload = "00";
			}else{
				formatIsupload = Tools.int2Hexstring(Integer.parseInt(isupload), 2).toUpperCase();
			}
			if(interval.equals("0")){
				formatSaveInterval = "0000";
			}else{
				formatSaveInterval = Tools.int2Hexstring(Integer.parseInt(interval), 4).substring(2, 4)+Tools.int2Hexstring(Integer.parseInt(interval), 4).substring(0, 2);
			}
			if(count.equals("0")){
				formatSaveCount = "00";
			}else{
				formatSaveCount = Tools.int2Hexstring(Integer.parseInt(count), 2);
			}
			CRC = KaiYanUtil.getCRC("7104030D"+formatCondition+formatIsupload+formatSaveInterval+formatIsupload+"017104");
			cmd = head+"7104030D"+formatCondition+formatIsupload+formatSaveInterval+formatSaveCount+"017104"+CRC+end;
			//fe 71 04 03 0D 05 01 1e00 01 017104 03 ff
			return cmd;
		}
		//参数查询
		if(funType.equals("4")){
			//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------自动拍照参数查询");
			//RequestUtil.getDealRequest(req, StructionType.CAMERA_SET_COMMOND_TYPE, "7143");
			CRC = KaiYanUtil.getCRC("71430005");
			cmd = head+"71430005"+CRC+end;
			return cmd;
		}
		
		return cmd;
	}

	public static void main(String arg[]){ }
	
	
}
