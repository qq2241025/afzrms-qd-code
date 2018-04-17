package com.mapabc.gater.directl.encode.xinruan;

import java.nio.ByteBuffer;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;


public class XinruanDownCmd {
	
	private String head = "@@PS";
	
	private String end = "%%";
	
	private int sequence = 0x0001;
	
	public String sentCmd(Request req,int protoclNum,String RorW,String cmdbody){
		
		//XinruanUtil.comhead(req, protoclNum);
		
		//0x50	读取/设置终端基本参数
		if(protoclNum ==0x50){
			return this.readOrSetBasicParameters(req,RorW,cmdbody);
		}
		//0x51	中心点名
		if(protoclNum ==0x51){
			return this.callName(req);
		}
		//0x52	中心监听
		if(protoclNum==0x52){
			return this.listen(req);
		}
		//0x53	中心下传短消息
		if(protoclNum==0x53){
			return this.msg(req);
		}
		//0x55	读取/设置应用设置参数
		if(protoclNum==0x55){
			return this.readOrSetApplyParameters(req,RorW,cmdbody);
		}
		//0x56	读取/设置电子围栏信息：（1）	电子围栏数值（2）	电子围栏状态
		if(protoclNum==0x56){
			return this.readOrSetElectronicfence(req,RorW);
		}
		//0x57	设置/读取固定短消息
		if(protoclNum==0x57){
			return this.readOrsetRegularMsg(req,RorW);
		}
		//0x58	读取/设置车载电话通话使用状态：
		if(protoclNum==0x58){
			return this.readOrsetcallRestrict(req,RorW);
		}
		//0x59	请求终端自检（包括设备硬件和状态）
		if(protoclNum==0x59){
			return this.selfCheck(req);
		}
		//0x5a	双卡设置
		if(protoclNum==0x5a){
			
		}
		//0x60	中心确认上传短消息
		if(protoclNum==0x60){
			return this.comfire(req,0x60);
		}
		//0x61	中心确认压缩定位信息
		if(protoclNum==0x61){
			
		}
		//0x62	中心确认上传报警信息
		if(protoclNum==0x62){
			return this.comfire(req,0x62);
		}
		//0x63	中心反馈数据通道检测包
		if(protoclNum==0x63){
			return this.backDataChaenlTest(req);
		}
		//0x64	中心下传报警控制信息
		if(protoclNum==0x64){
			return this.alramControl(req);
		}
		//0x71	设置APN
		if(protoclNum==0x71){
			return this.setAPN(req,RorW);
		}
		//0x74	启动/关闭终端紧急监控
		if(protoclNum==0x74){
			return this.quickListen(req);
		}
		return null;
	}

	private String readOrSetApplyParameters(Request req, String rorW,
			String cmdbody) {
		
		return null;
	}

	private String quickListen(Request req) {
		
		return null;
	}

	private String setAPN(Request req, String rorW) {
		
		String apns = RequestUtil.getReqData(req, "apn");
		
		if(apns.trim().length()!=0){
			
			String[] apn = apns.split(";");
			int apnlen = 0;
			String apncontent = "";
			for(int i=0;i<apn.length;i++){
				
				if(apn[i].equals("CMCC")){
					apncontent += "1CMCC;";
				}
				if(apn[i].equals("CLS")){
					apncontent += "2CLS;";
				}
				apnlen+=apn[i].length()+2;
			}
			
			if(apncontent.trim().equals("")){
				//Log.getInstance().xinruanLog("中心设置终端APN格式错误。只能为：CMCC，CLS二者之一");
				return null;
			}
			ByteBuffer bb = ByteBuffer.allocate(apnlen+24);
			
			bb.put(XinruanUtil.comhead(req, 0x71));
			
			bb.put(Tools.fromHexString(Tools.int2Hexstring(apnlen+1, 4)));
			
			bb.put(rorW.getBytes());
			
			bb.put(apncontent.getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(),apnlen+18));
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}
	}

	private String alramControl(Request req) {
		
		return null;
	}

	private String backDataChaenlTest(Request req) {
		
		return null;
	}

	private String comfire(Request req,int proctocal) {
		
		return null;
	}


	private String selfCheck(Request req) {
	
		return null;
	}

	private String readOrsetcallRestrict(Request req, String rorW) {
		
		return null;
	}

	private String readOrsetRegularMsg(Request req, String rorW) {
		
		return null;
	}

	private String readOrSetElectronicfence(Request req, String rorW) {
		

		
		ByteBuffer bb = ByteBuffer.allocate(298);
		
		bb.put(XinruanUtil.comhead(req, 0x56));//开始--》序列号
		
		bb.put(Tools.fromHexString(Tools.int2Hexstring(16*17+3, 4)));//命令长度
		
		bb.put(rorW.getBytes());//命令字
		
		
		//命令内容
		
		//共N个	第几个	报警状态	纬度11	纬度12	经度11	经度12	报警状态	.	经度n2
		//1字节	1字节	1字节	4字节	4字节	4字节	4字节	1字节	.	4字节

		bb.put((byte)16);
		bb.put((byte)1);
		
		ByteBuffer tempdata = ByteBuffer.allocate(17);
		byte[] data = new byte[17*16];
	
		String points = RequestUtil.getReqData(req, "points");
		
		String[] point = points.split(";");
		
		if(point.length<2){
			//Log.getInstance().xinruanLog("该终端不支持该功能！区域报警设置必须为两个点以上！");
			return null;
		}
		String alarmType = RequestUtil.getReqData(req, "alarmType");
		
		String alarmstatus = "";
		//出报警
		if(alarmType.equals("0")){
			alarmstatus = "01";	
		}
		//入报警
		if(alarmType.equals("1")){	
				alarmstatus = "10";	
		}
		//都报警
		if(alarmType.equals("2")){	
				alarmstatus = "11";
		}
		
	
		int pCount = point.length>32?32:point.length;
		
		for(int i=0;i<pCount;i+=2){
			String[] lonlat1 = point[i].split(",");
			String lon1 = lonlat1[0];
			String lat1 = lonlat1[1];
			String[] lonlat2 = point[i+1].split(",");
			String lon2 = lonlat2[0];
			String lat2 = lonlat2[1];
			tempdata.clear();
			tempdata.put(Tools.fromHexString(alarmstatus));
			tempdata.put(Tools.double2Hexstring(Double.parseDouble(lon1),8));
			tempdata.put(Tools.double2Hexstring(Double.parseDouble(lon2), 8));
			tempdata.put(Tools.double2Hexstring(Double.parseDouble(lat1), 8));
			tempdata.put(Tools.double2Hexstring(Double.parseDouble(lat2), 8));
			
			for(int j=0;j<data.length;j+=17){
				System.arraycopy(tempdata.array(), 0, data, j, 16);
			}
			
		}
		for(int i=pCount/2;i<16;i++){
			for(int j=0;j<17;j++){
				data[17*i+j] = (byte)0x00;			     
			}
		}
		
		bb.put(data);
		
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 290));
		
		bb.put(end.getBytes());
		
		return Tools.bytesToHexString(bb.array());
		
	
	}

	private String msg(Request req) {
		
		String content = RequestUtil.getReqData(req, "content");
		
		byte[] msgbyte = content.getBytes();
		
		ByteBuffer bb = ByteBuffer.allocate(22+msgbyte.length);
		
		bb.put(XinruanUtil.comhead(req, 0x53));

		bb.put((byte)msgbyte.length);
		
		bb.put(msgbyte);
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 15+msgbyte.length));
		
		bb.put(end.getBytes());
		
		return Tools.bytesToHexString(bb.array());
		
	}

	private String listen(Request req) {
		
		String callBackNumber = RequestUtil.getReqData(req, "callBackNumber");
		
		if(callBackNumber.trim().length()==0){
			//Log.getInstance().xinruanLog("中心设置终端"+req.getDeviceId()+"的中心手机号码,但请求参数为空");
			return null;
		}

		int cmdlen = callBackNumber.length()+1;
		
		ByteBuffer bb = ByteBuffer.allocate(cmdlen+15);
		
		bb.put(XinruanUtil.comhead(req, 0x52));
		
		bb.put(Tools.fromHexString(Tools.int2Hexstring(cmdlen, 4)));
		
		bb.put("0".getBytes());//不发送Dtmf控制字
		
		bb.put(callBackNumber.getBytes());
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), cmdlen+16));
		
		bb.put(end.getBytes());
		
		return Tools.bytesToHexString(bb.array());
	}

	private String callName(Request req) {
		
		ByteBuffer bb = ByteBuffer.allocate(23);
		
		bb.put(XinruanUtil.comhead(req, 0x51));
		
		bb.put((byte)0x00);
		bb.put((byte)0x00);
		
		bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), 15));
	
		bb.put(end.getBytes());
		
		return Tools.bytesToHexString(bb.array());
	}

	private String readOrSetBasicParameters(Request req, String WorR, String cmdbody) {
		
		if(cmdbody.trim()!=""){
			//定长，12位	中心公网IP地址
			if(cmdbody.equals("A")){

				return this.setOrReadIP(req,WorR);
			}
			//定长，4位	中心端口号
			if(cmdbody.equals("B")){
				return this.setOrReadCenterPort(req,WorR);
			}
			//定长，4位	本地端口号
			if(cmdbody.equals("C")){
				return this.setOrReadLocalPort(req,WorR);
			}
			//可变，最长21位，以F结束	中心手机号码
			if(cmdbody.equals("D")){
				return this.setOrReadCenterPhoneNumber(req,WorR);
			}
			//可变，最长21位，以F结束	GPRS拨号号码
			if(cmdbody.equals("E")){
				return this.setOrReadGPRSNumber(req,WorR);
			}
			//可变，最长21位，以F结束	短消息服务中心号码1
			if(cmdbody.equals("F")){
				return this.setOrReadSMSCenterNumber(req,WorR);
			}
			//可变，最长21位，以F结束	短消息服务中心号码2
			if(cmdbody.equals("G")){
				return this.setOrReadSMSCenterNumber(req,WorR);
			}
			//定长，11位，以F结束	终端ID号码（10位）
			if(cmdbody.equals("H")){
				return this.setOrReadTerminalID(req,WorR);
			}
			//可变，最长21位，以F结束	   终端控制手机号码
			if(cmdbody.equals("I")){
				return this.setOrReadTerminalControllPhoneNumber(req,WorR);
			}
			//定长，1位	设置优先数据通信方式（GPRS/SMS）
			if(cmdbody.equals("J")){
				return this.setOrReadFirstTransferType(req,WorR);
			}
			//定长，1位	设置波特率
			if(cmdbody.equals("K")){
				return this.setOrReadBoat(req,WorR);
			}
			//可变	标准AT命令控制
			if(cmdbody.equals("L")){
				return null;
			}
			//可变，最长8位	定时检测GPRS网络时间（秒）
			if(cmdbody.equals("M")){
				return this.setOrReadCheckGPRSTime(req,WorR);
			}
			//可变，最长4位	数据传输确认时间（秒）
			if(cmdbody.equals("N")){
				return this.setOrReadtransferConfireTime(req,WorR);
			}
			//对终端进行软件复位
			if(cmdbody.equals("O")){
				return this.softReset(req,WorR);
			}
			//定长，7位，以F结束	终端设置密码(6位)
			if(cmdbody.equals("P")){
				return this.setOrReadPassword(req,WorR);
			}
			//设置香港卡GPRS功能参数
			if(cmdbody.equals("Q")){
				return null;
			}
			//2字节，
			//网络代号1字节
			//优先级1字节	网络代号和优先级请参考需求定义以及本地设置定义

			if(cmdbody.equals("R")){
				return null;
			}
			//1字节	终端上传数据后，等待中心反馈确认时间
			if(cmdbody.equals("S")){
				return null;
			}
			//定长，12位	中心局域网IP地址
			if(cmdbody.equals("T")){
				return null;
			}
			//1表示上传压缩数据	Gps上传压缩非压缩数据设定
			if(cmdbody.equals("U")){
				return null;
			}
			return null;
			
		}else{
			return null;
		}

	}

	private String setOrReadPassword(Request req, String worR) {
		ByteBuffer bb = ByteBuffer.allocate(31);
		String password = RequestUtil.getReqData(req, "password");
		
		if(!"".equals(password)){
			while(password.length()<6){
				password = "0"+password;
			}
			password = password+"F";
			
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put((byte)14);
			bb.put(worR.getBytes());
			bb.put("P".getBytes());
			
			bb.put(password.getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(),24));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
			
		}else{
			//Log.getInstance().xinruanLog("设置车辆ID失败；参数为空");
			return null;
		}
	}

	private String softReset(Request req, String worR) {

			ByteBuffer bb = ByteBuffer.allocate(25);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(worR.getBytes());
			bb.put("O".getBytes());
		
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(),17));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());

	}

	private String setOrReadtransferConfireTime(Request req, String worR) {
		
		String transferConfireTime = RequestUtil.getReqData(req, "transferConfireTime");
		
		if(!"".equals(transferConfireTime)){
			
			ByteBuffer bb = ByteBuffer.allocate(transferConfireTime.length()+15);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(worR.getBytes());
			bb.put("N".getBytes());
			
			bb.put((transferConfireTime).getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), transferConfireTime.length()+7));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}
	}

	private String setOrReadCheckGPRSTime(Request req, String worR) {
		
		String CheckGPRSTime = RequestUtil.getReqData(req, "CheckGPRSTime");
		
		if(!"".equals(CheckGPRSTime)){
			
			ByteBuffer bb = ByteBuffer.allocate(CheckGPRSTime.length()+15);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(worR.getBytes());
			bb.put("M".getBytes());
			
			bb.put((CheckGPRSTime).getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), CheckGPRSTime.length()+7));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}
	}

	private String setOrReadBoat(Request req, String worR) {

		
		String  boat = RequestUtil.getReqData(req, "boat");
		
		if(!"".equals(boat)){

			ByteBuffer bb = ByteBuffer.allocate(26);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(worR.getBytes());
			bb.put("K".getBytes());
			
			bb.put((boat).getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(),18));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}
	
	}

	private String setOrReadFirstTransferType(Request req, String worR) {

		
		String  FirstTransferType = RequestUtil.getReqData(req, "FirstTransferType");
		
		if(!"".equals(FirstTransferType)){

			ByteBuffer bb = ByteBuffer.allocate(26);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(worR.getBytes());
			bb.put("J".getBytes());
			
			bb.put((FirstTransferType).getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(),18));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}
	
	}

	private String setOrReadTerminalControllPhoneNumber(Request req, String worR) {

		
		String  terminalControllPhoneNumber = RequestUtil.getReqData(req, "TerminalControllPhoneNumber");
		
		if(!"".equals(terminalControllPhoneNumber)){
			
			terminalControllPhoneNumber = terminalControllPhoneNumber+"F";
			
			ByteBuffer bb = ByteBuffer.allocate(terminalControllPhoneNumber.length()+15);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(worR.getBytes());
			bb.put("I".getBytes());
			
			bb.put((terminalControllPhoneNumber).getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), terminalControllPhoneNumber.length()+7));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}
	
	}

	private String setOrReadTerminalID(Request req, String worR) {
		ByteBuffer idbb = ByteBuffer.allocate(35);
		String id = RequestUtil.getReqData(req, "newId");
		
		if(!"".equals(id)){
			while(id.length()<10){
				id = "0"+id;
			}
			id = id+"F";
			
			
			idbb.put(XinruanUtil.comhead(req, 0x50));
			
			idbb.put((byte)14);
			idbb.put(worR.getBytes());
			idbb.put("H".getBytes());
			
			idbb.put(id.getBytes());
			
			idbb.put(XinruanUtil.getXinRuanVerfyCode(idbb.array(), 28));
			
			idbb.put(end.getBytes());
			
			return Tools.bytesToHexString(idbb.array());
			
		}else{
			//Log.getInstance().xinruanLog("设置车辆ID失败；参数为空");
			return null;
		}
	}

	private String setOrReadSMSCenterNumber(Request req, String worR) {
		
		String SMSCenterNumber = RequestUtil.getReqData(req, "SMSCenterNumber");
		
		if(!"".equals(SMSCenterNumber)){
			
			SMSCenterNumber = SMSCenterNumber+"F";
			
			ByteBuffer bb = ByteBuffer.allocate(SMSCenterNumber.length()+15);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(worR.getBytes());
			bb.put("F".getBytes());
			
			bb.put((SMSCenterNumber).getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), SMSCenterNumber.length()+7));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}
	}

	private String setOrReadGPRSNumber(Request req, String worR) {
		
		String GPRSNumber = RequestUtil.getReqData(req, "GPRSNumber");
		
		if(!"".equals(GPRSNumber)){
			
			GPRSNumber = GPRSNumber+"F";
			
			ByteBuffer bb = ByteBuffer.allocate(GPRSNumber.length()+15);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(worR.getBytes());
			bb.put("E".getBytes());
			
			bb.put((GPRSNumber).getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), GPRSNumber.length()+7));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}
	}

	private String setOrReadCenterPhoneNumber(Request req, String worR) {
		
		String centerPhoneNumber = RequestUtil.getReqData(req, "centerPhoneNumber");
		
		if(!"".equals(centerPhoneNumber)){
			
			centerPhoneNumber = centerPhoneNumber+"F";
			
			ByteBuffer bb = ByteBuffer.allocate(centerPhoneNumber.length()+15);
			
			bb.put(XinruanUtil.comhead(req, 0x50));
			
			bb.put(worR.getBytes());
			bb.put("D".getBytes());
			
			bb.put((centerPhoneNumber).getBytes());
			
			bb.put(XinruanUtil.getXinRuanVerfyCode(bb.array(), centerPhoneNumber.length()+7));
			
			bb.put(end.getBytes());
			
			return Tools.bytesToHexString(bb.array());
		}else{
			return null;
		}
	}

	private String setOrReadLocalPort(Request req, String worR) {
		ByteBuffer localportbb = ByteBuffer.allocate(28);
		//设置终端本地端口
		String localport = RequestUtil.getReqData(req, "localport");
		
		if(!"".equals(localport)){
			
			while(localport.length()<4){
				localport = "0"+localport;
			}
			localportbb.put(XinruanUtil.comhead(req, 0x50));
			
			localportbb.put((byte)6);
			
			localportbb.put(worR.getBytes());
			localportbb.put("C".getBytes());
			
			localportbb.put(localport.getBytes());
			
			localportbb.put(XinruanUtil.getXinRuanVerfyCode(localportbb.array(),19 ));
			
			localportbb.put(end.getBytes());
			
			return Tools.bytesToHexString(localportbb.array());
		}else{
			return null;
		}

	}

	private String setOrReadCenterPort(Request req, String worR) {
		
		//设置端口
		String port = RequestUtil.getReqData(req, "port");
		ByteBuffer portbb = ByteBuffer.allocate(28);
		if(!"".equals(port)){
			
			while(port.length()<4){
				port = "0"+port;
			}
			portbb.put(XinruanUtil.comhead(req, 0x50));
			
			portbb.put((byte)6);
			portbb.put(worR.getBytes());
			portbb.put("B".getBytes());
			
			portbb.put(port.getBytes());
			
			portbb.put(XinruanUtil.getXinRuanVerfyCode(portbb.array(),19 ));
			
			portbb.put(end.getBytes());
			
			return Tools.bytesToHexString(portbb.array());
		}else{
			return null;
		}
	}

	private String setOrReadIP(Request req, String worR) {
		
		ByteBuffer ipbb = ByteBuffer.allocate(36);
		String ip = RequestUtil.getReqData(req, "ip");

		if(!"".equals(ip)){
			
			while(ip.length()<12){
				ip = "0"+ip;
			}		
			ipbb.put(XinruanUtil.comhead(req, 0x50));
			
			ipbb.put((byte)14);
			ipbb.put(worR.getBytes());
			ipbb.put("A".getBytes());
			
			ipbb.put(ip.getBytes());
			
			ipbb.put(XinruanUtil.getXinRuanVerfyCode(ipbb.array(), 29));
			
			ipbb.put(end.getBytes());
			
			return Tools.bytesToHexString(ipbb.array());
			
		}else{
			return null;
		}
	}

	public static void main(String[] args) {
		

	}

}
