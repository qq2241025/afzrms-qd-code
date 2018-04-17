package com.mapabc.gater.directl.encode.kaiyan;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.mapabc.gater.directl.CRC;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.Request;
 
/**
 * @author chen.peng
 *
 */
public class KaiYanUtil {
	

	public static String getId(Request req){
		//13316948387--->A39DB4C5
		
		String Id = "";
		
		String deviceID = req.getDeviceId();
		
		long lastId = Long.parseLong(deviceID.substring(1), 10);
		
		String stringId  = Long.toHexString(lastId);
		
		while(stringId.length()<8){
			stringId = "0" + stringId;
		}

		Id = stringId.subSequence(6, 8)+stringId.substring(4, 6)+stringId.substring(2, 4)+stringId.substring(0, 2);
		
		return Id.toUpperCase();
	}
	public static String getCRC(String cmd){
		
		byte[] b = Tools.fromHexString(cmd);
		
		return Tools.int2Hexstring(CRC.get_CRC8(b), 2);
	}
	public static String encodeTime(String time){
		
		Calendar c = Calendar.getInstance();
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		try {
			
			Date date = df.parse(time);
			
			c.setTime(date);
			
			c.add(Calendar.HOUR,-8);
			
			Date initDate = df.parse("2000-1-1 00:00:00");
		
			long differ= (c.getTimeInMillis() - initDate.getTime())/1000;
		
			String hexDiffer = Long.toHexString(differ);
			
			if(hexDiffer.length()<8){
				hexDiffer = "0" + hexDiffer;
			}
			
			return hexDiffer.substring(6, 8)+hexDiffer.substring(4, 6)+hexDiffer.substring(2, 4)+hexDiffer.substring(0, 2);
		
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
		
	}
	public static String encodeLon(String lon){
		// 114.003950-->114°02.237'-->37224011
		String du = lon.split("\\.")[0];
		String fen = "0."+lon.split("\\.")[1];
		
		//0.28*60 = 16.8
		
		double dfen = Double.parseDouble(fen)*60;
		DecimalFormat format = new DecimalFormat("00.000");
		
		String sfen = format.format(dfen);
		
		//11402.237
		String dufen = du + sfen; 
		
		return dufen.substring(7, 9) + dufen.substring(4, 5)+dufen.substring(6, 7)+dufen.substring(2, 4)+dufen.substring(0, 2);
	}
	public static String encodeLat(String lat) {
		
		//22.502540-->22°30.1524'-->24153022
		
		String du = lat.split("\\.")[0];
		String fen = "0."+lat.split("\\.")[1];
		
		//0.502540*60 = 30.1524
		double dfen = Double.parseDouble(fen)*60;
		DecimalFormat format = new DecimalFormat("00.0000");
		
		String sfen = format.format(dfen);
		//2230.152
		String dufen = du + sfen;
		
		return dufen.substring(7, 9) + dufen.substring(5, 7)+ dufen.substring(2, 4)+ dufen.substring(0, 2);
	}
	public static String encodeIP(String ip) {
		String[] ips = ip.split("\\.");
		String ipCmd = "";
		for(int i=0;i<ips.length;i++){
			ipCmd += Tools.int2Hexstring(Integer.parseInt(ips[i]),2);
		}
		return ipCmd.toUpperCase();
	}
	public static void main(String[] args) throws IOException{ }
}

