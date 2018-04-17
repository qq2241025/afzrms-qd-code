package com.mapabc.gater.lbsgateway.service;

import java.net.Socket;

public interface ICommonGatewayService {

	public int send2Terminal(String deviceid, byte[] cmd, String cmdId);

	public int sendDataToUdpTerminal(String deviceid, byte[] cmd, String cmdId);

	public int sendDataToTcpTerminal(String deviceid, byte[] cmd, String cmdId);

	public boolean isOnLine(String simcard);

	public byte[] sendByHttpPost(String deviceid, byte[] cmd, String httpServer);

	public int sendByHttpGet(String httpServer);

	public int sendDoogNaviTask(String deviceId, byte[] cmd, String taskType);

	public int fwdOtherTcp(String deviceid, String ip, int port, byte[] content);
	
	public String getDeviceIdByTcpAddress(Socket socket);
	
	public Socket getTcpSocketByDeviceId(String deviceId);

}