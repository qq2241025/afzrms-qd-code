package com.apps.boot;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class Test {

	public String getFileText (){
		StringBuffer txt =new  StringBuffer();
		InputStream input = Test.class.getResourceAsStream("track.json") ;
		try {
			InputStreamReader read = new InputStreamReader(input,"utf-8");//考虑到编码格式
			BufferedReader bufferedReader = new BufferedReader(read);
			String lineTxt = null;
			while((lineTxt = bufferedReader.readLine()) != null){
				txt.append(lineTxt);
			}
			read.close();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String msg = txt.toString();
		return msg;
	}
	//
	public int getSubTime(String startTime,String endTime){
		    long dif = 0;
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd H:m:s");
			try {
				Date date1  = dateFormat.parse(startTime);
				Date date2  = dateFormat.parse(endTime);
				if (date2.after(date1)){
					dif = (date2.getTime() - date1.getTime()) / 1000 / 60 ;
				}else{
					dif = (date1.getTime() - date2.getTime()) / 1000 / 60 ;
				}
			} catch (ParseException e) {
                e.printStackTrace();
			}
			return (int) dif;
		
	}
	
	public void tests() {
		String text = this.getFileText();
		JSONArray json = JSONArray.parseArray(text);
		String startStopTime=null;//停车熄火开始时间
		String endStopTime = null;//停车熄火结束时间
		String startTime=null;//停车未熄火开始时间
		String endTime = null; //停车未熄火结束时间
		String lastTime = null;
		int FilterNum = 4 ;
		
		JSONObject startjson = null;
		JSONObject endjson = null;
		
		
		JSONArray list = new JSONArray();
		for(int x=0 ; x<json.size(); x++){
			JSONObject record = json.getJSONObject(x) ;
			String accStatus = record.getString("accStatus");
			double speed = record.getDouble("speed");
			String time = record.getString("time");
			String ax = record.getString("x");
			String ay = record.getString("y");
			record.put("ax", ax);
			record.put("ay", ay);
			list.add(record);
			lastTime = time;
			endjson = record;
			//熄火状态
			if(accStatus.equals("0")){
					if(startStopTime == null){
						startStopTime = time;	
						startjson = record;
					}
					if(startTime != null){
						endTime = time;
						int dif = this.getSubTime(startTime, endTime);
						if(dif >=FilterNum){
							System.out.println("-停车未熄火时间【"+startTime+"----"+endTime+"】"+dif);	
						}
						startTime = null;
						endTime = null;
						
					}
			}
			//着火状态
			if(accStatus.equals("1")){
				//已经连续熄火 直到第一个着火状态
				if(startStopTime != null){
					endStopTime = time;
					int dif = this.getSubTime(startStopTime, endStopTime);
					if(dif >=FilterNum){
						System.out.println("停车熄火时间【"+startStopTime+"----"+endStopTime+"】"+dif);	
					}
					startStopTime = null;
					endStopTime = null;
					//停车未熄火 第一个时间
					if(speed <= 3){
						startTime = time;
					}
				}
				//小于3公里认为是停车未熄火
				if(speed <= 0 ){
					if(startTime == null){
						startTime = time;
						endjson = record;
					}
				}
				if(speed >=0 ){
					if(startTime != null){
						endTime = time;
						endjson = record;
						int dif = this.getSubTime(startTime, endTime);
						if(dif >=FilterNum){
							System.out.println("停车未熄火时间【"+startTime+"----"+endTime+"】"+dif);
						}
						startTime = null;
						endTime = null;					
					}
				}
			}
		}
		System.out.println("lastTime="+lastTime);
		if(startStopTime != null){
			endStopTime = lastTime;
			String msg = startjson.getString("time");
			String msg2 = endjson.getString("time");
			System.out.println(msg+"----"+msg2);
			int dif = this.getSubTime(startStopTime, endStopTime);
			if(dif >=FilterNum){
				System.out.println("停车熄火时间【"+startStopTime+"----"+endStopTime+"】"+dif);
			}
		}
		if(startTime != null){
			endTime = lastTime;
			String msg = startjson.getString("time");
			String msg2 = endjson.getString("time");
			System.out.println(msg+"----"+msg2);
			int dif = this.getSubTime(startTime, endTime);
			if(dif >=FilterNum){
				System.out.println("停车未熄火时间【"+startTime+"----"+endTime+"】"+dif);
			}					
		}
		//saveFile(list.toString());
	}
	
	public void saveFile (String text){
		FileWriter writer;
		try {
				writer = new FileWriter("D://test2.txt");
			 	BufferedWriter bw = new BufferedWriter(writer);
		        bw.write(text.toString());
		       
		        bw.close();
		        writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException{
		Test test = new Test();
		test.tests();
	}

}
