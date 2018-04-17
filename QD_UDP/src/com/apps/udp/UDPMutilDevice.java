package com.apps.udp;

import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class UDPMutilDevice{
	private List<Devices> list = null;
	
	public List<Devices> getList() {
		return list;
	}

	public void setList(List<Devices> list) {
		this.list = list;
	}

	public UDPMutilDevice(){
		list = new ArrayList<Devices>();
		Devices dq1= new Devices("354525045393426",6*1000);
		Devices dq2= new Devices("354525045444815",5*1000);
		Devices dq3= new Devices("354525045423835",8*1000);
		Devices dq4= new Devices("354525045444682",16*1000);
		list.add(dq1);
		list.add(dq2);
		list.add(dq3);
		list.add(dq4);
	};
	
	
	public void startUdp() throws SocketException{
		List<Devices> list = this.getList();
		if(list!= null && list.size()>0){
			System.out.println("UDP gps上报模拟机开始启动*********************");
			for (Devices device : list) {
				  String devideId = device.getDeviceId();
				  int timers = device.getTimers();
				  DeviceThread thread = new DeviceThread(devideId,timers);
				  thread.start();
			}
		}
	}
	public static void main(String[] args) throws SocketException {
		UDPMutilDevice ud = new UDPMutilDevice();
		ud.startUdp();
	}
}
