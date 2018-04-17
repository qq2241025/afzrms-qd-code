package com.mapabc.gater.lbsgateway.gprsserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * 
 * @author 
 *
 */
public class UdpSender {
	public static void send(byte[] msg, String url, int port) throws IOException {
		InetAddress ip = InetAddress.getByName(url);

		DatagramSocket socket = new DatagramSocket();
		DatagramPacket dp = new DatagramPacket(msg, msg.length, ip, port);
		socket.send(dp);
		socket.close();
	}
}
