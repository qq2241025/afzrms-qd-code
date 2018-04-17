package com.apps.udp;

public class Devices {
	private String deviceId ;
	private int timers;
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public int getTimers() {
		return timers;
	}
	public void setTimers(int timers) {
		this.timers = timers;
	}
	public Devices() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Devices(String deviceId, int timers) {
		super();
		this.deviceId = deviceId;
		this.timers = timers;
	}
}
