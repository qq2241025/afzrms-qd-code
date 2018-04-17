package com.mapabc.gater.directl.encode.sege;
 
 
 
import java.nio.ByteBuffer;
import java.io.*;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.directl.encode.SettingAdaptor;
 

public class SegLPG52TerminalSetting extends SettingAdaptor {
	
	String centerSn = "01"; 
	String zuoSn = "001";  
	
    public SegLPG52TerminalSetting() {
    }
 

    private String getIp(String ip) {
        String temp = ip;
        if (temp != null && temp.trim().length() != 0) {
            while (temp.length() < 3) {
                temp = "0" + temp;
            }
        }
        return temp;
    }
//ASCII：（CGP,CMNET,202105138021,11000,2048,T,3,01,,）
    public String addrSetting(Request req) { 
        String content = "";
        String simCard =  req.getDeviceId();
        String serverIP = RequestUtil.getReqData(req, "ip");
        String serverPort = RequestUtil.getReqData(req, "port");
        String type = RequestUtil.getReqData(req, "type");
        if (type.equals("1")){
        	       	return null;
        }
        String[] ips = serverIP.split("\\.");
        String ipStr = getIp(ips[0]) + getIp(ips[1]) + getIp(ips[2]) +
                       getIp(ips[3]);
        int interval = Integer.parseInt("5");
        String hexInter = Integer.toHexString(interval).toUpperCase();
        if (hexInter.length() > 3) {
            hexInter = "FF";
        }
        if (hexInter.length() == 1) {
            hexInter = "0" + hexInter;
        }
//        content = "(RPM," + simCard + "," + ipStr + "," + serverPort + "," +
//                  "2048,T,3," + hexInter + ",,)";
        content = "(CGP,CMNET,"+ipStr+","+serverPort+",2048,T,3,01,,)";
        int size = 14 + content.length();
        ByteBuffer buf = ByteBuffer.allocate(size);
        buf.put((byte) 0x5B);
        buf.put((byte) 0x37);
        buf.put("0100100001".getBytes());
        buf.put((byte) content.length());
        buf.put(content.getBytes());
        buf.put((byte) 0x5D); 
        String ret = Tools
        .bytesToHexString(buf.array());
        return ret;
    }

    public static void main(String[] args) {
        SegLPG52TerminalSetting s = new SegLPG52TerminalSetting();

    }

 

    private String convertToHex(String num, int n) {
        String temp = "";
        int i = Integer.parseInt(num);
        String hex = Integer.toHexString(i).toUpperCase();
        if (hex.length() > n) {
            int off = 0;
            while (off < n) {
                temp = temp + "F";
                off++;
            }
            return temp;
        } else if (hex.length() < n) {
            while (hex.length() < n) {
                hex = "0" + hex;
            }
            temp = hex;
        } else {
            temp = hex;
        }
        return temp;
    }
}
