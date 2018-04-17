package com.apps.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class UDPClient {
	private  String host = "123.57.70.174"; //10.17.131.2 [测试服务器] --> 218.58.56.113
	private  int port = 9002;
	private String deviceId = "354525045393426";
	private DatagramSocket client = null;
	private int timer = 3 * 1000;
	
	public void startUdp(){
		try {
			while(true){
				Thread.sleep(this.getTimer());
				String  msg = this.getSocketmsg(deviceId);
				this.sendMsgToServer(msg);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public UDPClient(String host,int port){
		this.host = host;
		this.port = port;
		this.initUdpConfig();
	}
	
	public UDPClient(){
		this.initUdpConfig();
	}
	
	public void  initUdpConfig(){
		try {
			this.client = new DatagramSocket();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DatagramPacket  sendMsgToServer(String sendStr) throws IOException{
		byte[] sendBuf =  sendStr.getBytes();
		InetAddress addr = InetAddress.getByName(this.getHost());
		DatagramPacket sendPacket = new DatagramPacket(sendBuf ,sendBuf.length , addr , this.getPort());
	    this.getClient().send(sendPacket);
	    return sendPacket;
	}
	
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public DatagramSocket getClient() {
		return client;
	}

	public void setClient(DatagramSocket client) {
		this.client = client;
	}

	
	public int getTimer() {
		return timer;
	}

	public void setTimer(int timer) {
		this.timer = timer;
	}

	//随机模拟经纬度
    public String getSocketmsg(String deviceID){
	    double[] lnglat = {120.37997672446,36.2662141537};
	    double xlng =lnglat[0] +Math.random() * 0.003;
	    double xlat =lnglat[1] +Math.random() * 0.006;
	    Date nowTime=new Date();
	    SimpleDateFormat time=new SimpleDateFormat("yyMMdd");
	    SimpleDateFormat time2=new SimpleDateFormat("HHmmss");
	    SimpleDateFormat time3=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String year = time.format(nowTime);
	    String year2 = time2.format(nowTime);
	    String year3 = time3.format(nowTime);
	    Random random =new Random();
	    String speed = random.nextInt(100)+"";
	    String direct = random.nextInt(360)+"";
	    
	    String alrsm = "";
	    int locAndAlarm = random.nextInt(10); 
	    if(locAndAlarm > 4){
	    	alrsm ="1=0.10000000149011612!2=0|3";
	    }
	    
	    //LOC,1,设备ID,X[纬度lng],Y[经度lat],Speed,Direction,Time,LocateType,length,posDes,[]
	   	//LOC,1,354525045393426,115.665074,34.472677,8.96368,249.89,2014-06-24 15:53:50,1,0,,[]
	    String msg = "$WZTREQ,610,"+deviceID+","+year+","+year2+","+xlat+","+xlng+",91.09,"+speed+","+direct+",3,"+alrsm+",01,0,0#";
	    String alamsg = deviceID  + "---" + year3 +"---" + msg;
        System.out.println(alamsg);
	    return msg;
    }
	public static void main(String[] args) {
		UDPClient ud = new UDPClient();
		ud.startUdp();
	}
}
