package com.mapabc.gater.lbsgateway.gprsserver.jhs;

public class JHSHandler implements Runnable{
	Clientable lbsclient=null;
	byte[] data = null;
	public JHSHandler(Clientable lbsClient, byte[] bs){
		this.lbsclient = lbsClient;
		this.data = bs;
	}
	public void run() {
		this.lbsclient.sentDataBytes(this.data);
	}
}
