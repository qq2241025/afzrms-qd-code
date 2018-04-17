package com.mapabc.gater.directl.parse.xinruan;

import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.constant.AlarmType;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.gater.directl.encode.xinruan.XinruanUtil;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;

 


public class ParseXinRuan extends ParseBase implements ParseService{
	
	Date UnRARdate ;
	double lon;
	double lat;
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseXinRuan.class);

	TTermStatusRecord termStatus = new TTermStatusRecord();
	
	public void parseGPRS(String hexString) {
		log.info("新软终端上传信息"+hexString);
		//开始标志	协议号	  ID号      	序列号	数据长度   	    数据区	                       				 	     校验和	    结束标志
		//   4          1      10        2          2         <900           									 2         2
		// @@PS      0x00   1899281585   05         88  20100719104400,419048028,419048028,11,0062,0062,0062          	   %%
		String content = "";
		int laststartIndex = hexString.lastIndexOf("40405053"); //@@PS的16进制表示
		int endIndex = hexString.lastIndexOf("2525"); // "%%结束符的16进制表示"

		if (laststartIndex!=-1&&endIndex!=-1) {
			content = hexString.substring(laststartIndex+8, endIndex);// 截取@@PS之后   %%之前的字节
			
		} else{
			log.info("新软协议车机数据格式有误：数据报头或报尾不完整 " + hexString);
			return;
		}
		 
		int proctolNum = Integer.parseInt(content.substring(0, 2),16);

		String hexdeviceId = content.substring(2, 22);
		
		String deviceId = new String(Tools.fromHexString(hexdeviceId));
		this.setDeviceSN(deviceId);
		
		
		String num = content.substring(22, 26);
		//int numb = Integer.parseInt(num, 16);
		int dataLength = Integer.parseInt(content.substring(26,30),16);
		
		
		String data = content.substring(30,content.length()-4);//数据区的内容

		int vcode = Integer.parseInt(new String(content.substring(content.length()-4)),16);
		
 
 
		
		if(0x20<=proctolNum&&proctolNum<=0x4f){
			
			log.info("新软终端上传信息"+hexString);
			//回送终端基本设置参数
			if(proctolNum==0x20){
				this.parseBasicSettingReply(data);
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"50", "0");
				log.info("新软终端："+this.getDeviceSN()+" 设置基本参数成功！终端回传");
				//System.out.println("回送终端基本设置参数");
				
			}
			//上传非压缩定位信息
			if(proctolNum==0x21){
				if(dataLength%22!=0||data.length()%44!=0){
					log.info("新软协议车机数据格式有误：GPS数据格式错误" +hexString);
					//System.out.println("新软协议车机数据格式有误：" +hexString);
					return;
				}
				for(int i=0;i<data.length();i+=44){
					this.parsePostion(data.substring(i, i+44));
				}
				this.setReplyByte(this.reply(this.getDeviceSN(), 0x60));
				log.info("新软终端： "+this.getDeviceSN()+"终端上传非压缩位置数据成功");
			}
			//上传压缩定位信息
			if(proctolNum==0x22){
				
				log.info("新软终端："+this.getDeviceSN()+"终端上传压缩位置数据成功");
				//System.out.println("上传压缩定位信息");
				
			}
			//终端上传点名数据
			if(proctolNum==0x23){
				if(dataLength%22!=0){
					log.info("新软协议车机数据格式有误：GPS数据格式错误" +hexString);
					//System.out.println("新软协议车机数据格式有误：" +hexString);
					return;
				}		
				this.parsePostion(data);
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"51", "0");
				log.info("新软终端："+this.getDeviceSN() +"终端上传点名数据");
				//System.out.println("终端上传点名数据");
				
			}
			//上传中心指定的定位信息
			if(proctolNum==0x24){
				log.info("新软终端："+this.getDeviceSN()+"上传中心指定的定位信息");
				//System.out.println("上传中心指定的定位信息");
				
			}
			//上传短消息
			if(proctolNum==0x25){
				
				this.parsePostion(data.substring(0, 44));
				
				String msg = data.substring(44);
				log.info("新软终端： "+this.getDeviceSN() +" 上传固定短消息:"+new String(Tools.fromHexString(msg)));
				//System.out.println("上传固定短消息");
				this.setReplyByte(this.reply(this.getDeviceSN(), 0x60));
			}
			//上传报警信息
			if(proctolNum==0x26){
				
				this.parseAlarm(data);
				log.info("新软终端： "+this.getDeviceSN() +" 上传报警信息");
				//System.out.println("上传报警信息");
				this.setReplyByte(this.reply(this.getDeviceSN(), 0x62));
			}
			//回送应用设置参数
			if(proctolNum==0x27){
				
				this.parseApplySettingReply(data);
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"55", "0");
				log.info("新软终端： "+this.getDeviceSN()+" 设置应用参数成功！终端回传");
				
				//System.out.println("回送应用设置参数");
			}
			//终端回送电子围栏信息
			if(proctolNum==0x28){
				this.parseElectronicfenceReply(data);
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"56", "0");
				log.info("新软终端： "+this.getDeviceSN()+" 设置电子围栏信息成功！终端回送");
				//System.out.println("终端回送电子围栏信息");
			}
			//h回复固定短消息
			if(proctolNum==0x29){
				this.parseSmsReply(data);
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"57", "0");
				log.info("新软终端： "+this.getDeviceSN()+" 读取设置固定短消息成功信息！终端回送");
				//System.out.println("回送固定短消息：");
			}
			if(proctolNum==0x2a){
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"58", "0");
				log.info("新软终端： "+this.getDeviceSN()+" 设置读取车载电话使用状态成功！终端回送:"+new String(Tools.fromHexString(data)));
				//System.out.println("回送车载电话通话使用状态");
			}
			if(proctolNum==0x2b){
				this.parseTerminalStatue(data);
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"59", "0");
				log.info("新软终端："+this.getDeviceSN()+" 回送终端自检和状态查询结果");
				//System.out.println("回送终端自检和状态查询结果");
			}
			if(proctolNum==0x2c){

				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"5a", "0");
				log.info("新软终端： "+this.getDeviceSN()+" 双卡设置确认");
				//System.out.println("双卡设置确认");
			}
			if(proctolNum==0x2d){
				parseAPNsetting(data);
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"71", "0");
				log.info("新软终端： "+this.getDeviceSN()+" 设置APN成功！终端回送");
				//System.out.println("deviceId:"+this.getDeviceSN()+" 回送设置APN成功信息");
			}
			if(proctolNum==0x30){
				
				String msg = data;
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"53", "0");
				log.info("新软终端："+this.getDeviceSN()+" 下发短消息成功！终端会送,消息内容："+new String(Tools.fromHexString(msg)));
				//System.out.println("下发短消息确认信息，中心下发的短消息内容为："+msg);
			}
			if(proctolNum==0x31){
				//40405053 3138313031303731343033 0001 000c 303135333133313236343735 049d 252500
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"52", "0");
				log.info("新软终端："+this.getDeviceSN()+" 中心监听确认:终端回拨的电话是："+new String(Tools.fromHexString(data.substring(2))));
				//System.out.println("中心监听确认");
			}
			if(proctolNum==0x33){
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"74", "0");
				log.info("新软终端： "+this.getDeviceSN()+" 终端启动/关闭紧急监控回馈命令");
				//System.out.println("终端启动/关闭紧急监控回馈命令");
			}
			if(proctolNum==0x36){
				
				ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"64", "0");
				//System.out.println("中心下传报警控制信息确认");
			}
		}
		//数据通道检测
		else if(proctolNum==0x72){

			this.parsePostion(data);
			log.info("新软终端："+this.getDeviceSN()+"  数据通道检测包解析完成！");
			//System.out.println("数据通道检测包");
			this.setReplyByte(reply(this.getDeviceSN(),0x63));
		}
		//客车型上传非压缩定位信息
		else if(proctolNum==0x81){
			for(int i=0;i<data.length();i+=46){
				this.parsePostion(data.substring(i, i+46));
			}
			
			log.info("客车型新软终端 ："+this.getDeviceSN()+"上传非压缩定位信息");
		}
		//客车型上传压缩定位信息
		else if(proctolNum==0x82){
			this.parseRARPostion(data);
			this.setReplyByte(reply(this.getDeviceSN(),0x60));
			log.info("客车型新软终端 ："+this.getDeviceSN()+"上传压缩定位信息");
		}
		//客车型回送点名数据
		else if(proctolNum==0x83){
			this.parsePostion(data);
			ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"51", "0");
			log.info("客车型新软终端："+this.getDeviceSN() +"终端上传点名数据");
		}
		else{
			log.info("新软协议车机数据格式有误： 协议号不在上传范围内" + hexString);
			//System.out.println("新软协议车机数据格式有误：" +hexString);
			return;
			
		}
		
	
		
	}
	private byte[] reply(String deviceSN, int protocolNum) {
		
		//40405053 60 38313031303731343032 0001 0000 0259 2525
		ByteBuffer bb = ByteBuffer.allocate(23);
		
		bb.put("@@PS".getBytes());
		bb.put((byte)protocolNum);
		bb.put(deviceSN.getBytes());
		bb.put((byte)0x00);//序列号
		bb.put((byte)0x01);
		bb.put((byte)0x00);//命令长度
		bb.put((byte)0x00);
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 15));//校验和
		bb.put("%%".getBytes());
		//System.out.println(new String(bb.array()));
		return bb.array();
	
	}
	public void parsePostion(String data){
		
		//解析时间
		String date = data.substring(0, 14);
		
		String MM = Integer.parseInt(date.substring(4,6), 16)<10?"0"+Integer.parseInt(date.substring(4,6), 16):Integer.parseInt(date.substring(4,6), 16)+"";
		String dd = Integer.parseInt(date.substring(6,8),16)<10?"0"+Integer.parseInt(date.substring(6,8),16):Integer.parseInt(date.substring(6,8),16)+"";
		String yyyy = Integer.parseInt(date.substring(0,4),16)+"";
		String dates = yyyy+"-"+MM+"-"
				+ dd+" ";// yyMMdd
		
		String HH = Integer.parseInt(data.substring(8,10),16)<10?"0"+Integer.parseInt(data.substring(8,10),16):Integer.parseInt(data.substring(8,10),16)+"";
		String mm = Integer.parseInt(data.substring(10,12),16)<10?"0"+Integer.parseInt(data.substring(10,12),16):Integer.parseInt(data.substring(10,12),16)+"";
		String ss = Integer.parseInt(data.substring(12,14),16)<10?"0"+Integer.parseInt(data.substring(12,14),16):Integer.parseInt(data.substring(12,14),16)+"";
		String times = HH+":"+mm+":"+ss;// hhmmss
		
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d = simpleDate.parse(dates+times); 
			UnRARdate = d;
			//System.out.println(d);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		this.setTime(dates+times);
		
		//解析经纬度
		String Xlng = data.substring(14,22);
		String X = Tools.fromMs2XY(Xlng);
		this.setCoordX(X);
		
		lon = Double.parseDouble(X);
		
		String Yloc = data.substring(22,30);
		String Y = Tools.fromMs2XY(Yloc);
		this.setCoordY(Y);
		
		lat = Double.parseDouble(Y);
		//解析标志位
		String sign = data.substring(30,32);
		
		byte[] states = Tools.HexToBinary(sign).getBytes();
		
		this.parseStatus(states);
	
		//解析速度
		String speed = data.substring(32,36);
		this.setSpeed(Integer.parseInt(speed, 16)+"");
		//解析方向
		String direction = data.substring(36,40);
		this.setDirection(Integer.parseInt(direction, 16)+"");
		
		//解析高度
		String height = data.substring(40,44);
		this.setAltitude(Integer.parseInt(height,16)+"");
		
		this.setStatusRecord(termStatus);
		 
		//客车型终端IO信息
		if(data.length()>44){
			String IO = data.substring(44);
			
			byte[] IOINfor = Tools.HexToBinary(IO).getBytes();
			this.parseIO(IOINfor);
		}
	 
		
	}
	private void parseIO(byte[] infor) {
		byte b0 = infor[7];
		byte b1 = infor[6];
		byte b2 = infor[5];
		byte b3 = infor[4];
		byte b4 = infor[3];
		byte b5 = infor[2];
		byte b6 = infor[1];
		byte b7 = infor[0];
		if (b7 == '1') {
			
		} else {
			
		}
		if (b6 == '1') {
			
		} else {
			
		}
		//远灯
		if (b5 == '1') {
			termStatus.setFarLight("1");
			log.info(" 客车型新软终端:"+this.getDeviceSN()+"远灯开");
		}else{
			termStatus.setFarLight("0");
			log.info(" 客车型新软终端:"+this.getDeviceSN()+"远灯关");
		}
		//车门
		if (b4 == '1') {
			termStatus.setCarDoor("1");
			log.info(" 客车型新软终端:"+this.getDeviceSN()+"车门开");
			
		} else {
			termStatus.setCarDoor("0");
			log.info(" 客车型新软终端:"+this.getDeviceSN()+"车门关");
			
		}
		//刹车
		if (b3 == '1') {
			termStatus.setBrakeLight("0");
			log.info(" 客车型新软终端:"+this.getDeviceSN()+"刹车制动");
			
		}else{
			termStatus.setBrakeLight("1");
			log.info(" 客车型新软终端:"+this.getDeviceSN()+"刹车正常");
		}
		//左转灯
		if (b2 == '1') {
			termStatus.setLeftLight("1");
			log.info(" 客车型新软终端:"+this.getDeviceSN()+"左转灯开");
		}else{
			termStatus.setLeftLight("0");
			log.info(" 客车型新软终端:"+this.getDeviceSN()+"左转灯关");
		}
		//右转灯
		if (b1 == '1') {
			termStatus.setRightLight("1");
			log.info(" 客车型新软终端:"+this.getDeviceSN()+"右转灯开");
			
		} else {
			termStatus.setRightLight("0");
			log.info(" 客车型新软终端:"+this.getDeviceSN()+"右转灯关");
		}
		
		if (b0 == '1') {
				
		} else {
			
		}
		
	
	}
	private void parseRARPostion(String data){
		this.parsePostion(data.substring(0,46));//前面22字节为非压缩数据

		String rardata = data.substring(46);
		int last = rardata.length()%26;
		for(int i=0;i<rardata.length();i+=26){
			
			this.parseRaRdata(rardata.substring(i, i+26));
			
		}
	}
	private void parseRaRdata(String rardata) {
		Calendar car = Calendar.getInstance();
		
		car.setTime(UnRARdate);
		  
        int  timeSpan = Integer.parseInt(rardata.substring(0,4), 16);
		car.add(Calendar.SECOND, timeSpan);//时间
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.setTime(sdf.format(car.getTime())); 
		String lonSpan = Tools.fromMs2XY(rardata.substring(4,8));
		
		double dlonSpan = Double.parseDouble(lonSpan);

		String latSpan = Tools.fromMs2XY(rardata.substring(8, 12));
		
		double dlanSpan = Double.parseDouble(latSpan);
		
		//解析标志位
		String sign = rardata.substring(12,14);
		
		byte ssign = (byte)Integer.parseInt(sign,16);
		
		if((ssign&4) == 4){
			dlonSpan = -dlonSpan;
		}
		if((ssign&8) == 8){
			dlanSpan = -dlanSpan;
		}
		double rarlon = lon + dlonSpan;
		this.setCoordX(rarlon+"");
		
		double rarlat = lat + dlanSpan;
		this.setCoordY(rarlat+"");
		byte[] states = Tools.HexToBinary(sign).getBytes();
		
		this.parseStatus(states);
		
		//速度
		int speed = Integer.parseInt(rardata.substring(14, 16), 16);
		this.setSpeed(speed+"");
		//解析方向
		int direction = Integer.parseInt(rardata.substring(16,20),16);
		this.setDirection(direction+"");
		//解析高度
		int height = Integer.parseInt(rardata.substring(20,24),16);
		this.setAltitude(height+"");
		//IO
		String IO = rardata.substring(24,26);
		
		byte[] IOINfor = Tools.HexToBinary(IO).getBytes();
		
		this.parseIO(IOINfor);
		
		this.setStatusRecord(termStatus);  
		
	}
	private void parseStatus(byte[] status) {
		

		byte b0 = status[7];
		byte b1 = status[6];
		byte b2 = status[5];
		byte b3 = status[4];
		byte b4 = status[3];
		byte b5 = status[2];
		byte b6 = status[1];
		byte b7 = status[0];


		if (b7 == '1') {
			//log.info(this.getDeviceSN() + " ");
		} else {
			//log.info(this.getDeviceSN() + "");
		}
		//定位标志
		if (b6 == '1') {
			termStatus.setLocate("1");//终端已定位
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：终端已定位。");
		} else {
			termStatus.setLocate("0");//终端未定位
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：终端未定位。");
		}
		//高度有效
		if (b5 == '1') {
			
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：高度有效。");
		}else{
			log.info( " 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：高度无效。");
		}
		//1表示当前为移动卡，0表示香港CSL卡；
		if (b4 == '1') {
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成： 移动卡");
			
		} else {
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：香港CSL卡");
			
		}
		//0表示纬度压缩数据正偏，1表示负偏
		if (b3 == '1') {
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：纬度压缩数据负偏！");
			
		}else{
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：纬度压缩数据正偏！");
		}
		//表示经度压缩数据正偏，1表示负偏；
		if (b2 == '1') {
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：纬度压缩数据负偏！");
		}else{
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：纬度压缩数据正偏！");
		}
		//表示东西经，1表示东经；
		if (b1 == '1') {
			
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：经度为：东经");
			
		} else {
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：经度为：西经");
		}
		//表示南北纬，1表示南纬
		if (b0 == '1') {
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：纬度为：南纬");
			
		} else {
			log.info(" 新软终端:"+this.getDeviceSN()+"GPRS信息解析完成：纬度为：北纬");
		}
		
	}
	private void parseAlarm(String data){
		this.parsePostion(data.substring(0,44));
	
		String alarm = data.substring(44);
		
		String alarmType = alarm.substring(0, 2);
		
		int intalarmType = Integer.parseInt(alarmType, 16);
		//蓄电池低电压报警
		if(intalarmType==0x00){ 
			
			//termStatus.setLowElecOne("2");
			//1超速 2区域 3紧急 4偏航 5断电
			this.setAlarmType(AlarmType.LACK_PRESSURE_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		//超速报警
		if(intalarmType==0x01){
			
			this.setAlarmType(AlarmType.SPEED_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);		
		}
		//电子围栏报警/0x02
		if(intalarmType==0x02){
			this.setAlarmType(AlarmType.AREA_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		//（隐蔽）紧急求助/0x03
		if(intalarmType==0x03){
			this.setAlarmType(AlarmType.ACTIVE_ALARM_TYPE);	
			AlarmQueue.getInstance().addAlarm(this);
		}
		//车辆检测报警/0x04
		if(intalarmType==0x04){
			String alarmCode = alarm.substring(2, 4);
			int intalarmCode = Integer.parseInt(alarmCode, 16);
			//防劫报警
			if(intalarmCode==0x33){
				this.setAlarmType(AlarmType.ACTIVE_ALARM_TYPE);
				AlarmQueue.getInstance().addAlarm(this);
			}
			//求助报警
			if(intalarmCode==0x41){
				this.setAlarmType(AlarmType.HELP_ALARM_TYPE);
				AlarmQueue.getInstance().addAlarm(this);
			}
			//点火报警
			if(intalarmCode==0x51){
				
			}
			//开门报警
			if(intalarmCode==0x52){
				termStatus.setCarDoor("1");
			}
			//　探测报警
			if(intalarmCode==0x61){
				
			}
		}
		//终端故障报警/0x05
		if(intalarmType==0x05){
			String faulttype = alarm.substring(2, 4);
			int intfaulttype = Integer.parseInt(faulttype, 16);
			this.setAlarmType(AlarmType.DEVICE_FAIL_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
			//手柄坏
			if(intfaulttype==0x01){
				//this.setAlarmType(AlarmType.ACTIVE_ALARM_TYPE);
				termStatus.setHandle("0");//未接入
				
			}
			//GPS模块坏
			if(intfaulttype==0x02){
				termStatus.setGpsModule("0");//GPS模块异常
				this.setAlarmType(AlarmType.GPS_MODEL_FAIL_ALARM_TYPE);
				AlarmQueue.getInstance().addAlarm(this);
			}
			//GSM模块坏
			if(intfaulttype==0x03){
				termStatus.setGsmModule("0");//gsm模块异常
				
			}
			//外部RAM损坏
			if(intfaulttype==0x04){
				
			}
			//内部RAM损坏
			if(intfaulttype==0x05){
			
			}
			//EEPROM数据损坏
			if(intfaulttype==0x06){
					
			}
			//MIC坏
			if(intfaulttype==0x07){
				
			}
			
		}
		//电源断线报警/0x06
		if(intalarmType==0x06){
			this.setAlarmType(AlarmType.CUT_POWER_ALARM_TYPE);
			AlarmQueue.getInstance().addAlarm(this);
		}
		//紧急报警线断报警/0x07
		if(intalarmType==0x07){
			//this.setAlarmType("5");		
		}
	this.setStatusRecord(termStatus);
	}
	private void parseGPShandletypeReply(String data){
		
		String cmdcode = data.substring(0, 2);//命令字
		String cmdbody = data.substring(2, 4);//命令体
		String cmdContent = data.substring(4);
		
		
		int GPShandleType = Integer.parseInt(cmdContent.substring(0, 2), 16);
		//不处理
		if(GPShandleType==0x00){
			log.info(" 新软终端:"+this.getDeviceSN()+"中心设置终端GPS数据处理方式为： 不处理");
		}
		//定距处理
		if(GPShandleType==0x01){
			int distance = Integer.parseInt(cmdContent.substring(2,cmdContent.length()), 16);
			log.info(" 新软终端:"+this.getDeviceSN()+"中心设置终端GPS数据处理方式为： 定距处理。距离间隔为："+distance+"米");
		}
		//定时与车辆状态综合处理
		if(GPShandleType==0x02){
			int time = Integer.parseInt(cmdContent.substring(2,cmdContent.length()), 16);
			log.info(" 新软终端:"+this.getDeviceSN()+"中心设置终端GPS数据处理方式为：定时与车辆状态综合处理。时间间隔为："+time+"秒");
		}
		
	}
	private void parseGPSuptypeReply(String data){
	
		String cmdcode = data.substring(0, 2);
		String cmdbody = data.substring(2, 4);
		String cmdContent = data.substring(4);
		
		int GPSupType = Integer.parseInt(cmdContent.substring(0, 2), 16);
		//定时
		if(GPSupType==0x00){
			int time = Integer.parseInt(cmdContent.substring(2,cmdContent.length()), 16);
			log.info("中心设置的GPS数据上传方式是定时上传；时间间隔是："+time+"秒");
		}
		//定条数 
		if(GPSupType==0x01){
			int num = Integer.parseInt(cmdContent.substring(2,cmdContent.length()), 16);
			log.info("中心设置的GPS数据上传方式是定条上传；条数间隔是："+num+"条");
			
		}
		//定时与车状态综合方式 
		if(GPSupType==0x02){
			int time = Integer.parseInt(cmdContent.substring(2,cmdContent.length()), 16);
			log.info("中心设置的GPS数据上传方式是定时与车状态综合方式 ；时间间隔是："+time+"秒");
		}
		//定条与车状态综合方式
		if(GPSupType==0x03){
			int num = Integer.parseInt(cmdContent.substring(2,cmdContent.length()), 16);
			log.info("中心设置的GPS数据上传方式是定条与车状态综合方式 ；条数间隔是："+num+"条");
		}
	}
	private void parseoverspeedAlarmReply(String data){
		String cmdcode = data.substring(0, 2);
		String cmdbody = data.substring(2, 4);
		String maxSpeed = data.substring(4);
		int intMaxSpeed = Integer.parseInt(maxSpeed, 16);
		log.info("中心设置的终端的超速报警最大值 ："+intMaxSpeed+"  km/h");
	}
	private void parseElectronicfenceReply(String data){
		String cmdcode = data.substring(0, 2);
		
		String total = data.substring(2, 4);
		
		String num = data.substring(4, 6);
		
		String lastdata = data.substring(6);
		
		List lon = new ArrayList();
		List lat = new ArrayList();
		List alarmStatus = new ArrayList();
		for(int i=0;i<lastdata.length();i+=34){
			
			String status = lastdata.substring(i, i+2);
			String lon1 = Tools.fromMs2XY(lastdata.substring(i+2, i+10));
			String lon2 = Tools.fromMs2XY(lastdata.substring(i+10, i+18));
			String lat1 = Tools.fromMs2XY(lastdata.substring(i+18, i+26));
			String lat2 = Tools.fromMs2XY(lastdata.substring(i+26, i+34));
			
			alarmStatus.add(status);
			lon.add(lon1);
			lon.add(lon2);
			lat.add(lat1);
			lat.add(lat2);
		}
		for(int i=0;i<lon.size();i++){
				if(i%2==0){
					log.info("报警类型为： "+alarmStatus.get(i/2));
				}
				log.info("电子围栏第"+(i+1)+"个点："+lon.get(i)+","+lat.get(i));
				//System.out.println("长度 "+lon.size());
			
			
		}
	}
	private void parseSmsReply(String data){
		// 命令字     N（共N条）	第N条（1字节）	长度（1字节）	短消息内容	……
		// W          5             1              1  
		String cmdcode = data.substring(0, 2);
		String smstotal = data.substring(2, 4);
		String smsNum = data.substring(4, 6);
		String smscontent = data.substring(6);
		int start = 0;
		String[] sms = new String[5];
		int contentLength;
		String content;
		for(int i=0;i<5;i++){
			contentLength = Integer.parseInt(smscontent.substring(start,start+2), 16);
			start += 2;
			content = smscontent.substring(start, contentLength*2+start);
			
			start = contentLength*2+start;
			sms[i] = new String(Tools.fromHexString(content));
			log.info("新软终端："+this.getDeviceSN()+" 回复中心设置读取固定短消息指令。短消息内容："+sms[i]);
		}
		
		
	}
	private void parseTerminalStatue(String data){
		String cmdcode = data.substring(0, 2);
		String GPSdata = data.substring(2, 46);
		this.parsePostion(GPSdata);
		String status = data.substring(46);
		String lastStatus = status;
		//0x83
		String statuscode1 = lastStatus.substring(0, 2);
		int intStatuscode1 = Integer.parseInt(statuscode1, 16);
		if(intStatuscode1==0x83){
			
			String GPSlocateStatus = new String(Tools.fromHexString(lastStatus.substring(2, 4)));
			if(GPSlocateStatus.equals("0")){
				
				log.info("新软终端：deviceId:"+this.getDeviceSN()+"定位未成功！");
			}
			if(GPSlocateStatus.equals("1")){
				log.info("新软终端： deviceId:"+this.getDeviceSN()+"定位成功！");
			 }
		}
		lastStatus = lastStatus.substring(4);
		
		//0x84
		String statuscode2 = lastStatus.substring(0, 2);
		int intStatuscode2 = Integer.parseInt(statuscode2, 16);
		if(intStatuscode2==0x84){
			String cardType = new String (Tools.fromHexString(lastStatus.substring(2, 4)));
			if(cardType.equals("1")){
				//CMCC
				log.info("新软终端："+this.getDeviceSN()+"自检，卡类型为： CMCC");
			}
			if(cardType.equals("2")){
				//csl
				log.info("新软终端："+this.getDeviceSN()+"自检，卡类型为： CLS");
			}
			if(cardType.equals("3")){
				//SUNDAY
				log.info("新软终端："+this.getDeviceSN()+"自检，卡类型为：SUNDAY");
			}
			String callType = new String(Tools.fromHexString(lastStatus.substring(4, 6)));
			if(callType.equals("0")){
				//公网拨号
				log.info("新软终端："+this.getDeviceSN()+"自检，拨号方式为：公网拨号");
			}
			if(callType.equals("1")){
				//APN
				log.info("新软终端："+this.getDeviceSN()+"自检，拨号方式为 ：APN拨号");
			}
		}
		
		lastStatus = lastStatus.substring(6);
		
		//0x85
		String statuscode3 = lastStatus.substring(0, 2);
		int intStatuscode3 = Integer.parseInt(statuscode3, 16);
		
		if(intStatuscode3==0x85){
			String softVersion = new String(Tools.fromHexString(lastStatus.substring(2, 12)));
			log.info("新软终端："+this.getDeviceSN()+"自检，软件版本："+softVersion);
			
		}
		
		lastStatus = lastStatus.substring(12);
		
		//0x86
		String statuscode4 = lastStatus.substring(0, 2);
		int intStatuscode4 = Integer.parseInt(statuscode4, 16);

		if(intStatuscode4==0x86){
			
			String Versiondate = new String(Tools.fromHexString(lastStatus.substring(2, lastStatus.indexOf("87")-2)));
			log.info("新软终端："+this.getDeviceSN()+"自检，软件日期："+Versiondate);
		}
		
		
		lastStatus = lastStatus.substring(lastStatus.indexOf("87"));
		
		//0x87
		String statuscode5 = lastStatus.substring(0, 2);
		int intStatuscode5 = Integer.parseInt(statuscode5, 16);
		if(intStatuscode5==0x87){
			String isStop = new String(Tools.fromHexString(lastStatus.substring(2,4)));
			
			if(isStop.equals("0")){
				log.info("新软终端："+this.getDeviceSN()+"自检，行车中");
			}
			if(isStop.equals("1")){
				log.info("新软终端："+this.getDeviceSN()+"自检，停车中");
			}
			
		}
		lastStatus = lastStatus.substring(4);
		
		//0x88
		String statuscode6 = lastStatus.substring(0, 2);
		int intStatuscode6 = Integer.parseInt(statuscode6, 16);
		if(intStatuscode6==0x88){
			String isFlameout = new String(Tools.fromHexString(lastStatus.substring(2)));
			
			if(isFlameout.equals("0")){
				log.info("新软终端："+this.getDeviceSN()+"自检，启动中");
			}
			if(isFlameout.equals("1")){
				log.info("新软终端："+this.getDeviceSN()+"自检，熄火中");
			}
		}
	}
	private void parseAPNsetting(String data){
		String hexcmdcode = data.substring(0,2);
		String cmdcode = new String(Tools.fromHexString(hexcmdcode));
		String hexapnContent = data.substring(2);
		String apncontent = new String(Tools.fromHexString(hexapnContent));
		
		String[] apns = apncontent.split(";");
		for(int i=0;i<apns.length;i++){
			if(apns[i].substring(0, 1).equals("1")){
				log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置APN：cardType:CMCC  接入方式："+apns[i].substring(1));
			}
			if(apns[i].substring(0, 1).equals("2")){
				log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置APN：cardType:CLS 接入方式："+apns[i].substring(1));
			}
			
		}
		
	}
	private void parseBasicSettingReply(String data){
		String cmdcode = new String(Tools.fromHexString(data.substring(0,2)));
		String cmdbody = new String(Tools.fromHexString(data.substring(2,4)));
		
		//设置中心公网IP地址
		if(cmdbody.equals("A")){
			String centerIP = new String(Tools.fromHexString(data.substring(4)));	
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置中心公网IP："+centerIP);
			//this.setAdress(address);

		}
		//中心端口号
		if(cmdbody.equals("B")){
			String centerPort = new String(Tools.fromHexString(data.substring(4)));	
			
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置中心端口号："+centerPort);

		}
		//本地端口号
		if(cmdbody.equals("C")){
			String localPort = new String(Tools.fromHexString(data.substring(4)));	
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置本地端口号："+localPort);
		}
		//中心手机号码
		if(cmdbody.equals("D")){
			String centerPhoneNumber = new String(Tools.fromHexString(data.substring(4,data.length()-2)));
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置中心手机号码："+centerPhoneNumber);
		}
		//GPRS拨号号码
		if(cmdbody.equals("E")){
			String GPRSPhoneNumber = new String(Tools.fromHexString(data.substring(4,data.length()-2)));
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置GPRS拨号号码："+GPRSPhoneNumber);
		}
		//短消息服务中心号码1
		if(cmdbody.equals("F")){
			String msgCenterhoneNumber = new String(Tools.fromHexString(data.substring(4,data.length()-2)));
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置短消息服务中心号码1："+msgCenterhoneNumber);
		}
		//短消息服务中心号码2
		if(cmdbody.equals("G")){
			String msgCenterhoneNumber = new String(Tools.fromHexString(data.substring(4,data.length()-2)));
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置短消息服务中心号码2："+msgCenterhoneNumber);
		}
		//终端ID号码（10位）
		if(cmdbody.equals("H")){
			String terminalID = new String(Tools.fromHexString(data.substring(4,data.length()-2)));
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置短终端ID："+terminalID);
		}
		//终端控制手机号码
		if(cmdbody.equals("I")){
			String terminalControlPhoneNumber = new String(Tools.fromHexString(data.substring(4,data.length()-2)));
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置终端控制手机号码："+terminalControlPhoneNumber);
		}
		//设置优先数据通信方式（GPRS/SMS）
		if(cmdbody.equals("J")){
			String first = "";
			String gprsOrsms = new String (Tools.fromHexString(data.substring(4)));
			
			if(gprsOrsms.equals("1")){
				first = "GPRS";
				
			}
			if(gprsOrsms.equals("0")){
				first = "SMS";
			}
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置优先数据通信方式："+first);
		}
		//设置波特率
		if(cmdbody.equals("K")){
			String boterate = "";
			String bote =  new String (Tools.fromHexString(data.substring(4)));
			if(bote.equals("1")){
				boterate = "4800";
			}
			if(bote.equals("2")){
				boterate = "9600";
			}
			if(bote.equals("3")){
				boterate = "19200";
			}
			if(bote.equals("4")){
				boterate = "38400";
			}
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置波特率："+boterate);
		}
		//标准AT命令控制
		if(cmdbody.equals("L")){
			
		}
		//定时检测GPRS网络时间（秒）
		if(cmdbody.equals("M")){
			String checkGPRStime = new String (Tools.fromHexString(data.substring(4)));
			log.info("DeviceSN:  "+this.getDeviceSN()+"设置基本参数成功!终端回传！定时检测GPRS网络时间（秒）"+checkGPRStime);
		}
		//数据传输确认时间（秒）
		if(cmdbody.equals("N")){
			String dataTransferConfirmTime = new String (Tools.fromHexString(data.substring(4)));
			log.info("DeviceSN:  "+this.getDeviceSN()+"设置基本参数成功!终端回传！设置数据传输确认时间："+dataTransferConfirmTime);
		}
		//对终端进行软件复位
		if(cmdbody.equals("O")){
		
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！软件复位");
		}
		//终端设置密码(6位)
		if(cmdbody.equals("P")){
			String password =  new String (Tools.fromHexString(data.substring(4,data.length()-2)));
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置密码"+password);
		}
		//设置香港卡GPRS功能参数
		if(cmdbody.equals("Q")){
			
		}
		//网络代号和优先级请参考需求定义以及本地设置定义
		if(cmdbody.equals("R")){
			
		}
		//终端上传数据后，等待中心反馈确认时间
		if(cmdbody.equals("S")){
			int waiteTime = Integer.parseInt(new String (Tools.fromHexString(data.substring(4))), 16);
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置等待中心反馈确认时间："+waiteTime);
		}
		//中心局域网IP地址
		if(cmdbody.equals("T")){
			String centerIP =  new String (Tools.fromHexString(data.substring(4)));
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置等待中心局域网IP地址："+centerIP);
		}
		//Gps上传压缩非压缩数据设定
		if(cmdbody.equals("U")){
			boolean isRAR;
			String rarOrnot = new String (Tools.fromHexString(data.substring(2)));
			if(rarOrnot.equals("1")){
				isRAR = true;
			}else{
				isRAR = false;
			}
			log.info("新软终端："+this.getDeviceSN()+"设置基本参数成功!终端回传！设置终端上传压缩或非压缩设定："+isRAR);
		}
		ReplyResponseUtil.addReply(this.getDeviceSN()+":"+"50", "0");
		//System.out.println(cmdbody);
	}
	private void parseApplySettingReply(String data){
		String hexcmdbody = data.substring(2, 4);
		String cmdbody = new String(Tools.fromHexString(hexcmdbody));
		//设置/读取GPS数据处理方式回复
		if(cmdbody.equals("a")){
			this.parseGPShandletypeReply(data);
			log.info("新软终端： deviceId:"+this.getDeviceSN()+" 设置读取GPS数据处理方式参数成功！终端回送");
		}
		//设置/读取GPS数据上传方式回复
		if(cmdbody.equals("b")){
			this.parseGPSuptypeReply(data);
			log.info("新软终端： deviceId:"+this.getDeviceSN()+" 设置读取GPS上传方式参数成功！终端会送");
		}
		//设置/读取超速报警限值回复
		if(cmdbody.equals("c")){
			this.parseoverspeedAlarmReply(data);
			log.info("新软终端： deviceId:"+this.getDeviceSN()+" 设置读取超速报警参数成功！终端会送");
		}
		//设置/读取电子围栏信息回复
		if(cmdbody.equals("d")){
			log.info("新软终端： deviceId:"+this.getDeviceSN()+" 设置读取电子围栏信息成功！终端会送");
		}
		//设置/读取车载电话允许/禁止通话号码回复
		if(cmdbody.equals("f")){
			log.info("新软终端： deviceId:"+this.getDeviceSN()+" 设置读取车载电话通话号码成功！终端会送");
		}
		//设置/读取固定短消息回复
		if(cmdbody.equals("g")){
			log.info("新软终端： deviceId:"+this.getDeviceSN()+" 设置读取固定短消息成功！终端会送");
		}
		//设置APN回复
		if(cmdbody.equals("n")){
			log.info("新软终端： deviceId:"+this.getDeviceSN()+" 设置读APN成功！终端会送");
			
		}
		
	}

	public static void main(String st[]){
		ParseXinRuan px = new ParseXinRuan();

		String upGPRSstr = "40405053273138393932383135383500010005576202007c037c2525";//设置定时上传测试
		String upGPSstr2 = "40405053553138393932383135383500010005576101007C03A82525";//设置定距上传测试 
		String callname = "40405053233138393932383135383500010000023c2525";//中心点名
		String up_unrarGPS_data = "4040505321313839393238313538350001001607da0717112202197243b2197243b2ff00600061006200ff2525";
		String upUnrarGPSdata = "40405053213131303336303031373829da009a07da071a0b081a185323d404ebefce4200160054000007da071a0b082218532a7604ebf028420015005c000007da071a0b082a1853311804ebf0764200120057000007da071a0b08321853359804ebeefc42000e0083000007da071a0b083a1853365204ebe98042001400ac000007da071a0b0906185336a004ebe71642000100b1000007da071a0b0907185336a604ebe71642000100f300002a1b252500";
		
		//设置围栏信息回传
		String  upSetAreaAlarm = "404050532831383939323831353835000101135710011018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A59001018F3738018F37380087A5960087A5900650B2525";
		//取消区域报警-》设置所有坐标信息为0
		String cancelAreaAlarm = "40405053283138393932383135383500010112571601000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000002F02525";
		//短消息回传
		String upmsg = "404050533031383939323831353835000125E4BB8AE5A4A9E4B88BE58D8833E782B9E4B880E58FB7E4BC9AE8AEAEE5AEA4E5BC80E4BC9A1C962525";
		String upapn = "404050532D313839393238313538350001000C5731434D43433B32434C533B057A2525";
		String overspeed = "40405053273138393932383135383500010003576360035D2525";
		String setport = "404050532031383939323831353835000106574231313131036B2525";
		String selfcheck = "404050532b38313031303731343032001200325207da08040e392a18f47bec0894367ee200000000000083318431318556322e323286323030372d30382d303100873188300fd7252500";
		String rarGPS = "4040505382383130313037313430320006035707da08040a231318f46be4089431fee2000100c8000000000a01c800a2e20000ac000000000a00e40030ee0000a6000000000a0072002aee000098000000000a0108003cee0000a6000000000a01920060ee0000cb000000000a01080060ee0000dc000000000a001e0012ea0000cf000000000a0018000cea0000af000000000a00120018ea0000a2000000000a01560072ee000097000000000a00720012e2000094000000000a00120006ea0000b6000000000a00300012e2000097000000000a004e0030e2000076000000000a0072003ce2000065000000000a00660024e200005c000000000a005a001ee2000058000000000a005a0012e2000057000000000a027c00b4ee000073000000000a00f6002ae200006e000000000a011a003ce200006f000000000a01f2009cee00007c000000000a01020060ee0000ad000000000a00fc005aee0100c8000000000a00b40030ee0000c6000000000a00c60030e20000a3000000000a020a009ce2010079000000000a02ee00eae2020061000000000a022200bae2020053000000000a00cc0030ee000050000000000a00480018e2000055000000000a0132005ae2000052000000000a005a0030e2000052000000000a00960012ee000074000000000a01ce006cee0100ab000000000a01e0009cee0100ce000000000a0066001eee0100e0000000000a00de004eee0100e9000000000a00a80048ee0100ed000000000a00360018ee0100ef000000000a029a00eaee0200f1000000000a00d20048ee0100f2000000000a003c002aee0100f1000000000a00600012e20000f0000000000a01f800a2e20000dd000000000a029400dee20000aa000000001a00e4004ee2000081000000000e00ae003cee000095000000000a00600036ee0000bb000000000b00960048ee0000d5000000000a01ec00c0ee0100e2000000000a00c60054ee0100e8000000000a02940102ee0100ea000000000a0066004eee0100ec000000000a004e0006e20100eb000000000a00d80042e20000df000000000e06360246e20000b8000000000a00d20030e20000aa000000000a005a0018e2000096000000000a005a000ce2000084000000000a0030001eee00008c000000000a003c0000e200008b000000000a005a000ce2000083000000000a00ae0030e200006f000000a1012525";
		px.parseGPRS("404050532538313031303731343033000a003607da08060e170d65408bd4be368970a000000000000048656c6c6f2c74696d652069733a323031302d30382d30362031343a32343a30108f252500");
		//bdf1cceccfc2cee733b5e3d2bbbac5bbe1d2e9cad2bfaabb
		//57010119bdf1cceccfc2cee733b5e3d2bbbac5bbe1d2e9cad2bfaabb154a252500
		//4040505329383130313037313430320001001c57010119bdf1cceccfc2cee733b5e3d2bbbac5bbe1d2e9cad2bfaabb154a252500
		String s = "40405053" +
				"82" +
				"38313031303731343032" +
				"0005" +
				"0357" +
				"07da08050f0101" +
				"18f47cfa" +
				"0894369c" +
				"e2" +
				"0000" +//time
				"00fb" +//jingdu
				"0000" +//weidu
				"00" +//biaozi
				"00" +//sudu
				"0500" +//fangxiang
				"1200" +//gaodu
				"0c" +//io
				"e2" +
				"00" +
				"00fa" +
				"000000" +
				
				
				
				
				"000500360024e20000f6000000" +
				"0005001e000ce2000000000000" +
				"000500120006ea0000f7000000" +
				"0005002a0012ee0000f7000000" +
				"000500420018ee0000f4000000" +
				"0005007e001eee0000f9000000" +
				"00050036000cee0000ff000000" +
				"000500060006ee000102000000" +
				"000500060006ee0000fe000000" +
				"000500060006e60000f9000000" +
				"0005001e0018e20000f9000000" +
				"0005001e001ee20000fd000000" +
				"0005002a0012e20000f3000000" +
				"0005001e0006ea0000f4000000" +
				"000500300000e20000f4000000" +
				"000500180000e20000f7000000" +
				"00050006000ce60000fa000000" +
				"000500120006ee0000f6000000" +
				"000500060000e2000000000000" +
				"000500180000e20000f4000000" +
				"00050006000cee0000f3000000" +
				"000600060000e60000f5000000" +
				"0005000c0006e60000f4000000" +
				"000500000006e2000000000000" +
				"000500120006e20000f4000000" +
				"0005000c0006ea0000f9000000" +
				"00050000000cea0000f8000000" +
				"000500060006ee0000fc000000" +
				"0005000c0000e60000fc000000" +
				"000500180006ee000000000000" +
				"00050006000cee0000f6000000" +
				"0005000c000cee0000f6000000" +
				"0005000c0006e60000f9000000" +
				"000500060006ee0000fa000000" +
				"00050006000ce60000f8000000" +
				"00050000001ee20000f8000000" +
				"00050006001ee60000fc000000" +
				"000500000000e20000fe000000" +
				"000500060000e6000000000000" +
				"000500060006ee000102000000" +
				"000500000006ea0000fd000000000500000000e2000100000000000500060006ee000107000000000500000000e2000107000000000500060006ea000106000000000600000000e2000000000000000500060006ee000104000000000500000000e2000104000000000500000006ea0000ff000000000500000006e2000000000000000500060000e2000104000000000500000006ea000101000000000500000000e2000000000000000500000006ea000104000000000500000000e2000107000000000500000000e2000104000000000500000006ea000106000000000500000000e2000000000000000500000000e2000000000000000500000000e20000fe000000000500000006e20000fd000000000500060000e20000fc000000000500060000e20000fb000000711e2525";
	}
	public ArrayList<ParseBase> parseHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}
	public ArrayList<ParseBase> parseModata(byte[] moBytes) {
		// TODO Auto-generated method stub
		return null;
	}
	public ParseBase parseSingleGprs(byte[] moBytes) {
		String hexString = Tools.bytesToHexString(moBytes);
		this.parseGPRS(hexString);
		return this;
	}
	public ParseBase parseSingleHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}
	
 

}
