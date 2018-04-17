package com.mapabc.gater.directl.encode.kaiyan;

/**
 * 
 */
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.AlarmAdaptor;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;

/**
 * @author chen.peng
 * 
 */
public class KaiYanAlarm extends AlarmAdaptor {

	String head = "fe";
	String end ="ff";

	public String overspeedAlarm(Request req) {
		//Log.getInstance().kaiyanLog("中心设置开研终端----->"+req.getDeviceId()+"--------------超速报警值");
		/*地址           功能代码           子功能数据量           数据长度                               数据内容                          CRC
		0x21      0x0C      子功能数据量          数据长度         参数代码   参数长度  参数内容        CRC
		                                               0x0124   0x0001
		 1          1            1            1          2       2       1         1*/
		
		/*设置超速报警速度为100节
		21 0C 00 0A 2401 010064FE
		21：地址
		0C：功能码
		00：子功能长度
		0A：总长度
		2401：参数代码
		0100：参数长度
		64：100节
		FE：CRC*/
 
		String cmd = "";
		String CRC = "";
		String maxSpeed = RequestUtil.getReqData(req, "max");
		String duration = RequestUtil.getReqData(req, "duration");
		
		if(duration.trim()!=""){
			//Log.getInstance().kaiyanLog("中心设置开研终端-->"+req.getDeviceId()+" 超速报警，该终端不支持 duration 参数！");
		}
		if(maxSpeed.trim().equals("0")){
			
			//Log.getInstance().kaiyanLog("中心设置开研终端-->"+req.getDeviceId()+" 取消超速报警, 终端不支持。");
			
		}else{
			
			String speed = Tools.formatKmToKnot(maxSpeed);
			String hexSpeed = Tools.int2Hexstring(Integer.parseInt(speed), 2);
			CRC = KaiYanUtil.getCRC("21" + "0C" + "03" + "0D" + "2410" + "0100" + hexSpeed +"010124");
			cmd = head + "21" + "0C" + "03" + "0D" + "2410" + "0100" + hexSpeed +"010124"+ CRC + end;
		}
		
		return cmd;
	}
	
	public String areaAlarm(Request req) {
		//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------区域报警值");
		/*设置进入区域报警参数：大纬度22°33.1524'，大经度114°02.237'
		小纬度22°30.1524'，小经度114°00.237'*/

		/*210C00292501200031323334353637383940414243442415332237224011241530223702401130004B
		具体说明：
		21：地址
		0C：功能码
		00：子功能长度
		29：总长度
		2501：参数代码   （注意是0号区域报警的参数代码）
		2000：参数长度
		3132333435363738394041424344：区域说明
		24153322：大纬度22°33.1524'
		37224011：大经度114°02.237'
		24153022：小纬度22°30.1524'
		37024011：小经度114°00.237'
		30：表示进入区域报警
		00：编号0
		4B：CRC

		注意：如果需要取消某个报警区域，则相应的下发一条数据内容全部为0xFF的区域报警设置即可*/
		 
		String cmd = "";
		String CRC = "";
		
		String parameterCode = "";
		int intAreaNo = 0;
		String areaNo = RequestUtil.getReqData(req, "areaNo");
		String areaType = RequestUtil.getReqData(req, "areaType");
		String alarmType = RequestUtil.getReqData(req, "alarmType");
		String points = RequestUtil.getReqData(req, "points");
		
		if(areaNo.trim()!=""){
			 intAreaNo = Integer.parseInt(areaNo);
		}
		if(intAreaNo>16||intAreaNo<=0){
			//Log.getInstance().kaiyanLog("中新设置开研终端——>"+req.getDeviceId()+"区域报警功能，但该终端只支持16个区域。");
			return cmd;
			
		}
		if(!areaType.equals("1")){
			//Log.getInstance().kaiyanLog("中新设置开研终端——>"+req.getDeviceId()+"区域报警功能，但该终端只支持矩形区域。");
			return cmd;
		}
		
		switch(intAreaNo){

		case 1:{
			parameterCode = "2501";
			break;
		}
		case 2:{
			parameterCode = "4501";
			break;
		}
		case 3:{
			parameterCode = "6501";
			break;
		}
		case 4:{
			parameterCode = "8501";
			break;
		}
		case 5:{
			parameterCode = "A501";
			break;
		}

		case 6:{
			parameterCode = "C501";
			break;
		}
		case 7:{
			parameterCode = "E501";
			break;
		}
		case 8:{
			parameterCode = "0501";
			break;
		}
		case 9:{
			parameterCode = "2502";
			break;
		}
		case 10:{
			parameterCode = "4502";
			break;
		}
		case 11:{
			parameterCode = "6502";
			break;
		}
		case 12:{
			parameterCode = "8502";
			break;
		}
		case 13:{
			parameterCode = "A502";
			break;
		}
		case 14:{
			parameterCode = "C502";
			break;
		}
		case 15:{
			parameterCode = "E502";
			break;
		}
		case 16:{
			parameterCode = "0503";
			break;
		}
		}
		String ararno = intAreaNo<10?"0"+intAreaNo+"":intAreaNo+"";
		String areaInfor = ararno+"00000000000000000000000000";
		//cmd = head + "210C00" + "29";
		
		String[] point  = points.split(";");
		if(point.length>2){
			//Log.getInstance().kaiyanLog("中新设置开研终端——>"+req.getDeviceId()+"区域报警功能，但该终端只支持矩形区域,坐标点只能为两个。");
			return null;
		}
		String[] maxLonLat = Tools.getRecMaxLntLat(point[0],point[1]);
		String maxLon = KaiYanUtil.encodeLon(maxLonLat[0]);
		String maxLat = KaiYanUtil.encodeLat(maxLonLat[1]);
		String[] minLonLat = Tools.getRecMinLntLat(point[0], point[1]);
		String minLon = KaiYanUtil.encodeLon(minLonLat[0]);
		String minLat = KaiYanUtil.encodeLat(minLonLat[1]);
		
		String kyalarmType = "";
		//出报警
		if(alarmType.equals("0")){
			kyalarmType = "31";
		}
		//入报警
		if(alarmType.equals("1")){
			kyalarmType = "30";
		}
		//出入报警
		if(alarmType.equals("2")){
			//Log.getInstance().kaiyanLog("中新设置开研终端——>"+req.getDeviceId()+"区域报警功能，但该终端不支持出入报警！");
			return null;
		}
		
		String areaNumber = Tools.int2Hexstring(intAreaNo-1, 2);
		
		CRC = KaiYanUtil.getCRC("210C03" + "2C" + parameterCode + "2000" + 
				areaInfor + maxLat + maxLon + minLat + minLon + kyalarmType + areaNumber + "010125");
		
		cmd = head + "210C03" + "2C" + parameterCode + "2000" + 
		areaInfor + maxLat + maxLon + minLat + minLon + kyalarmType + areaNumber + "010125"+CRC + end;
		return cmd;
	}

	public String cancleArea(Request req) {
		//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消区域报警");
		//注意：如果需要取消某个报警区域，则相应的下发一条数据内容全部为0xFF的区域报警设置即可*/
	 
		String areaNo = RequestUtil.getReqData(req, "areaNo");
		String cmd = "";
		String CRC = "";
		String parameterCode = "";
		int intAreaNo = Integer.parseInt(areaNo);
		if(intAreaNo>16||intAreaNo<=0){
			//Log.getInstance().kaiyanLog("中新设置开研终端——>"+req.getDeviceId()+"区域报警功能，但该终端只支持16个区域。");
			return cmd;
		}
		switch(intAreaNo){

			case 1:{
				parameterCode = "2501";
				break;
			}
			case 2:{
				parameterCode = "4501";
				break;
			}
			case 3:{
				parameterCode = "6501";
				break;
			}
			case 4:{
				parameterCode = "8501";
				break;
			}
			case 5:{
				parameterCode = "A501";
				break;
			}
	
			case 6:{
				parameterCode = "C501";
				break;
			}
			case 7:{
				parameterCode = "E501";
				break;
			}
			case 8:{
				parameterCode = "0501";
				break;
			}
			case 9:{
				parameterCode = "2502";
				break;
			}
			case 10:{
				parameterCode = "4502";
				break;
			}
			case 11:{
				parameterCode = "6502";
				break;
			}
			case 12:{
				parameterCode = "8502";
				break;
			}
			case 13:{
				parameterCode = "A502";
				break;
			}
			case 14:{
				parameterCode = "C502";
				break;
			}
			case 15:{
				parameterCode = "E502";
				break;
			}
			case 16:{
				parameterCode = "0503";
				break;
			}
		}
		String areaNumber = Tools.int2Hexstring(intAreaNo-1, 2);
		
		CRC = KaiYanUtil.getCRC("210C03" + "2C" + parameterCode + "2000" + 
				"0000000000000000000000000000" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FF" + areaNumber +"010126");
		
		cmd = head + "210C03" + "2C" + parameterCode + "2000" + 
		"0000000000000000000000000000" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FFFFFFFF" + "FF" + areaNumber +"010126"+ CRC + end;
		return cmd;
	}
	
	public String cancelAlarm(Request req) {
		//fe 21 0E 03 0C 00000000 01 210E CD ff
		//
		
 		String cmd = "";
		String CRC = "";
		String type = RequestUtil.getReqData(req, "type");
		String cancelAlarm = "11111111111111111111111111111111";
		
		//取消所有
		if(type.equals("0")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消所有报警");
			cancelAlarm = "00000000000000000000000000000000";
		}
		//超速
		if(type.equals("1")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消超速报警");
			cancelAlarm = "11111111111111011111111111111111";
						 //11111111111111011111111111111111
		}
		//区域
		if(type.equals("2")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消区域报警,终端不支持该报警功能。");
			return null;
		}
		//主动
		if(type.equals("3")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消主动报警,终端不支持该报警功能。");
			return null;
		}
		//紧急
		if(type.equals("4")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消紧急报警,终端不支持该报警功能。");
			return null;
		}
		//断电
		if(type.equals("5")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消断电报警");
			cancelAlarm = "11111011111111111111111111111111";
		}
		//偏航
		if(type.equals("6")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消偏航报警,终端不支持该报警功能。");
			return null;
		}
		//设备故障
		if(type.equals("7")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消设备故障,终端不支持该报警功能。");
			return null;
		}
		//求助
		if(type.equals("8")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消求助报警,终端不支持该报警功能。");
			return null;
		}
		//防盗
		if(type.equals("9")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消防盗报警");
			cancelAlarm = "1111110111111111111111111111111";
		}
		//疲劳驾驶
		if(type.equals("10")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消疲劳驾驶报警,终端不支持该报警功能。");
			return null;
		}
		//温度
		if(type.equals("11")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消温度报警");
			cancelAlarm = "1111011111111111111111111111111";
		}
		//碰撞
		if(type.equals("12")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消碰撞报警,终端不支持该报警功能。");
			return null;
		}
		//超时停车
		if(type.equals("13")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消超时报警");
			cancelAlarm = "1111111101111111111111111111111";
		}
		//主电源破坏
		if(type.equals("14")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消主电源破坏报警");
			cancelAlarm = "1111101111111111111111111111111";
		}
		//GPS天线开路
		if(type.equals("15")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消GPS天线开路报警状态位");
			cancelAlarm = "1111111111111011111111111111111";
		}
		//GPS天线短路
		if(type.equals("16")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消GPS天线短路报警状态位");
			cancelAlarm = "1111111111111011111111111111111";
		}
		//欠压
		if(type.equals("17")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消欠压报警状态位");
			cancelAlarm = "1111011111111111111111111111111";
		}
		//超声波
		if(type.equals("18")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消超声波报警状态位,终端不支持该报警功能。");
			return null;
		}
		//拖吊
		if(type.equals("19")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消拖吊报警状态位,终端不支持该报警功能。");
			return null;
		}
		//熄火
		if(type.equals("20")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消熄火报警状态位,终端不支持该报警功能。");
			return null;
		}
		//停留
		if(type.equals("21")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消停留报警状态位,终端不支持该报警功能。");
			return null;
		}
		//停车
		if(type.equals("22")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消停车报警状态位,终端不支持该报警功能。");
			return null;
		}
		//迟到
		if(type.equals("23")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消迟到报警状态位,终端不支持该报警功能。");
			return null;
		}
		//疲劳
		if(type.equals("24")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消疲劳报警状态位,终端不支持该报警功能。");
			return null;
		}
		//离线
		if(type.equals("25")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消离线报警状态位,终端不支持该报警功能。");
			return null;
		}
		//流量报警
		if(type.equals("26")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消流量报警状态位,终端不支持该报警功能。");
			return null;
		}
		//罐车反转报警
		if(type.equals("27")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消罐车反转报警状态位,终端不支持该报警功能。");
			return null;
		}
		//GPS接收机故障
		if(type.equals("28")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消GPS接收机故障报警状态位");
			cancelAlarm = "1111111111110111111111111111111";
		}
		//胎压报警
		if(type.equals("29")){
			//Log.getInstance().kaiyanLog("中心控制开研终端----->"+req.getDeviceId()+"--------------取消胎压报警状态位,终端不支持该报警功能。");
			return null;
		}
		
		if(!cancelAlarm.equals("")){
			String s0 = Tools.int2Hexstring(Integer.parseInt(cancelAlarm.substring(0, 8), 2), 2);
			String s1 = Tools.int2Hexstring(Integer.parseInt(cancelAlarm.substring(8, 16), 2), 2);
			String s2 = Tools.int2Hexstring(Integer.parseInt(cancelAlarm.substring(16, 24), 2), 2);
			String s3 = Tools.int2Hexstring(Integer.parseInt(cancelAlarm.substring(24, 32), 2), 2);
			
			String hexcmd = s0+s1+s2+s3;
			
			CRC = KaiYanUtil.getCRC("210E030C" + hexcmd + "01210E");
			cmd = head + "210E030C" + hexcmd + "01210E"+CRC + end;
			
			return cmd;
		}else{
			//Log.getInstance().kaiyanLog("中心设置取消开研终端，报警状态，参数为空");
			return null;
		}
		
	}

	public static void main(String aft[]){ }
}
