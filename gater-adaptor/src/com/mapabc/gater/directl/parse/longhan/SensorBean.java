package com.mapabc.gater.directl.parse.longhan;

import java.io.Serializable;
import java.util.HashMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Log; 

public class SensorBean implements Serializable {
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(SensorBean.class);
	
	private String deviceId;

	private long sensorId;

	private String sensorName;

	private String status1;

	private String status2;

	private String status3;

	private String status4;

	private String status5;

	private String status6;

	private String status7;

	private String status8;

	private HashMap<String, String> statusMap = new HashMap<String, String>();
	private HashMap<String, String> sensorMap = new HashMap<String, String>();

	public SensorBean() {

		statusMap.put("1", "acc");
		statusMap.put("2", "rightLight");
		statusMap.put("3", "leftLight");
		statusMap.put("4", "brakeLight");
		statusMap.put("5", "farLight");
		statusMap.put("6", "lowElecTwo");
		statusMap.put("7", "lowElecOne");
		statusMap.put("8", "highElecTwo");
		statusMap.put("9", "highElecOne");
		statusMap.put("10", "oilElec");
		statusMap.put("11", "fortification");
		statusMap.put("12", "carDoor");
		statusMap.put("13", "conditioning");
	}

	public void loadSensorInfo() {

		if (this.getStatus1() != null) {
			sensorMap.put("1", getStatusMethodName(this.getStatus1()));
		}
		if (this.getStatus2() != null) {
			sensorMap.put("2", getStatusMethodName(this.getStatus2()));
		}
		if (this.getStatus3() != null) {
			sensorMap.put("3", getStatusMethodName(this.getStatus3()));
		}
		if (this.getStatus4() != null) {
			sensorMap.put("4", getStatusMethodName(this.getStatus4()));
		}
		if (this.getStatus5() != null) {
			sensorMap.put("5", getStatusMethodName(this.getStatus5()));
		}
		if (this.getStatus6() != null) {
			sensorMap.put("6", getStatusMethodName(this.getStatus6()));
		}
		if (this.getStatus7() != null) {
			sensorMap.put("7", getStatusMethodName(this.getStatus7()));
		}
		if (this.getStatus8() != null) {
			sensorMap.put("8", getStatusMethodName(this.getStatus8()));
		}

		for (int i = 1; i <= sensorMap.size(); i++) {
			log.info(
					this.getDeviceId() + " 第" + i + "路对应状态方法名："
							+ getStatusMethodName("" + i));
		}
	}

	public String getStatusMethodName(String key) {
		String methodName = null;

		String property = this.statusMap.get(key);
		if (property != null)
		methodName = "set" + property.substring(0, 1).toUpperCase()
				+ property.substring(1);

		return methodName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public long getSensorId() {
		return sensorId;
	}

	public void setSensorId(long sensorId) {
		this.sensorId = sensorId;
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String getStatus1() {
		return status1;
	}

	public void setStatus1(String status1) {
		this.status1 = status1;
	}

	public String getStatus2() {
		return status2;
	}

	public void setStatus2(String status2) {
		this.status2 = status2;
	}

	public String getStatus3() {
		return status3;
	}

	public void setStatus3(String status3) {
		this.status3 = status3;
	}

	public String getStatus4() {
		return status4;
	}

	public void setStatus4(String status4) {
		this.status4 = status4;
	}

	public String getStatus5() {
		return status5;
	}

	public void setStatus5(String status5) {
		this.status5 = status5;
	}

	public String getStatus6() {
		return status6;
	}

	public void setStatus6(String status6) {
		this.status6 = status6;
	}

	public String getStatus7() {
		return status7;
	}

	public void setStatus7(String status7) {
		this.status7 = status7;
	}

	public String getStatus8() {
		return status8;
	}

	public void setStatus8(String status8) {
		this.status8 = status8;
	}

	public static void main(String[] args) {
		String property = "acc";
		String methodName = "set" + property.substring(0, 1).toUpperCase()
				+ property.substring(1);
		System.out.println(methodName);
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "传感方案ID=" + this.getSensorId() + ",deviceId="
				+ this.getDeviceId() + ",name=" + this.getSensorName()
				+ ",status1=" + this.getStatus1() + ",status2="
				+ this.getStatus2() + ",status3=" + this.getStatus3()
				+ ",status4=" + this.getStatus4() + ",status5="
				+ this.getStatus5() + ",status6=" + this.getStatus6()
				+ ",status7=" + this.getStatus7() + ",status8="
				+ this.getStatus8();
	}

	public HashMap<String, String> getStatusMap() {
		return statusMap;
	}

	public void setStatusMap(HashMap<String, String> statusMap) {
		this.statusMap = statusMap;
	}

	public HashMap<String, String> getSensorMap() {
		return sensorMap;
	}

	public void setSensorMap(HashMap<String, String> sensorMap) {
		this.sensorMap = sensorMap;
	}

}
