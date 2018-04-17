package com.apps.gps;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class GpsTrackLineUDP {
    private String jsonText ;
    private List<GPSPoint> trackList ;
    private  String host = "127.0.0.1"; //10.17.131.2 [测试服务器] --> 218.58.56.113
	private  int port = 9002;
	private String deviceId = "354525045423835";
	private DatagramSocket client = null;
	private int timer = 3 * 1000;
	private int dataIndex = 0;
    
	public GpsTrackLineUDP(String filepath) {
		trackList =  new ArrayList<GPSPoint>();
		InputStream infile =  this.getClass().getResourceAsStream(filepath);
		BufferedReader reader = null;
		StringBuffer laststr =  new StringBuffer();
		try{
			InputStreamReader inputStreamReader = new InputStreamReader(infile, "UTF-8");
			reader = new BufferedReader(inputStreamReader);
			String tempString = null;
			while((tempString = reader.readLine()) != null){
				laststr.append(tempString);
			}
			reader.close();
			jsonText = laststr.toString();
		}catch(IOException e){
			e.printStackTrace();
		}
		this.initUdpConfig();
		this.initTrackDataList();
	}
	
	private void initTrackDataList(){
		JSONObject root = JSONObject.parseObject(jsonText);
		JSONArray list = root.getJSONArray("data");
		if(list!=null && list.size() > 0){
			 int len = list.size();
			 for (int i = 0; i < len; i++) {
				 JSONObject record = list.getJSONObject(i) ;
				 GPSPoint poin = new GPSPoint(record.getDoubleValue("x"), record.getDoubleValue("y"));
				 trackList.add(poin);
			 }
		}
	}
	
	
	public void startUdp(){
		try {
			int len = this.trackList.size();
			while(true){
				if(dataIndex > len){
					System.out.println("******************************************************");
					dataIndex = 0;
				}
				GPSPoint point = this.trackList.get(dataIndex);
				Thread.sleep(this.getTimer());
				String  msg = this.getSocketmsg(deviceId,point,dataIndex);
				this.sendMsgToServer(msg);
				dataIndex++;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
    public String getSocketmsg(String deviceID,GPSPoint xy,int dataIndex){
	    Date nowTime=new Date(); 
	    SimpleDateFormat time=new SimpleDateFormat("yyMMdd");
	    SimpleDateFormat time2=new SimpleDateFormat("HHmmss");
	    SimpleDateFormat time3=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String year = time.format(nowTime);
	    String year2 = time2.format(nowTime);
	    String year3 = time3.format(nowTime);
	    String speed = Math.random() * 100+"";
	    String direct = Math.random() * 10+200+"";
	    //LOC,1,设备ID,X[纬度lng],Y[经度lat],Speed,Direction,Time,LocateType,length,posDes,[]
	   	//LOC,1,354525045393426,115.665074,34.472677,8.96368,249.89,2014-06-24 15:53:50,1,0,,[]
	    String msg = "$WZTREQ,610,"+deviceID+","+year+","+year2+","+xy.getX()+","+xy.getY()+",91.09,"+speed+","+direct+",3,,01,0,0#";
	    String alamsg = deviceID +"---***----" +dataIndex+ "---" + year3 +"---" + msg;
        System.out.println(alamsg);
	    return msg;
    }
	
	public static void main(String[] args) {
		String path = "/com/apps/gpsTrack.json";
		GpsTrackLineUDP line = new GpsTrackLineUDP(path);
		line.startUdp();
	}
    
}
