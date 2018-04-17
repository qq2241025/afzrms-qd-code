package com.mapabc.gater.directl.parse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.parse.service.ParseService; 
 
 

public class ParseBaJu extends ParseBase implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseBaJu.class);
	public ArrayList<ParseBase> parseHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ParseBase> parseModata(byte[] moBytes) {
		// TODO Auto-generated method stub
		return null;
	}

	public ParseBase parseSingleGprs(byte[] moBytes) {
		String hexString = Tools.bytesToHexString(moBytes);
		this.parseGPRS(hexString);
		return this;
	}

	public ParseBase parseSingleHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}
	//<?xml version="1.0" encoding="GB2312" ?>
	//	<mapabc version="1.0">
	//		<request userId="13855961414" swId="13855961414">
	//			<posReportRequest>
	//				<posReportList>
	//					<posReport>
	//						<pos>
	//						<y>144263008</y>
	//						<x>418734208</x>
	//						</pos>
	//					<speed>43.9</speed>
	//					<status>0</status>
	//					<direction>208</direction>
	//					<time>2009-04-21 08:50:06</time>
	//					</posReport>
	//				</posReportList>
	//			</posReportRequest>
	//			</request>
	//	</mapabc>
 
	public void parseGPRS(String hexString) {
		hexString = hexString.substring(8);//删除baju
		byte[] bs = Tools.fromHexString(hexString);
		//String content= new String(bs);
		SAXBuilder sb = new SAXBuilder();
		try {
			InputStream is = new ByteArrayInputStream(bs);
			Document doc = sb.build(is);
			Element root = doc.getRootElement();
			Element eRequest = root.getChild("request");
			String phnum = eRequest.getAttributeValue("userId");
			 

			Element posReq = eRequest.getChild("posReportRequest");
			Element posList = posReq.getChild("posReportList");
			Element posReport = posList.getChild("posReport");
			Element pos = posReport.getChild("pos");
			String x = pos.getChild("x").getTextTrim();
			String y = pos.getChildTextTrim("y");
			float lng = Float.parseFloat(x) / 3600000f;
			float lat = Float.parseFloat(y) / 3600000f;
			//          com.mapabc.geom.CoordCvtAPI cca = new com.mapabc.geom.CoordCvtAPI();
			//          com.mapabc.geom.DPoint point = cca.decryptConvert(lng, lat);
			//          float  yx = (float)point.getX();
			//          float yy = (float)point.getY();

			String s = posReport.getChildTextTrim("speed");
			String status = posReport.getChildTextTrim("status");
			String direction = posReport.getChildTextTrim("direction");
			double dir = Double.parseDouble(direction);
			double ddir = 360 - dir;
			direction = ddir + "";
			String time = posReport.getChildTextTrim("time");

			this.setCoordX(lng + "");
			this.setCoordY(lat + "");
			this.setTime(time);
			this.setSpeed(s);
			this.setDirection(direction);
			 

			log.info(
					this.getDeviceSN() + "交互式导航位置信息：x=" + lng + ",y=" + lat
							+ ",time=" + time);
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
			log.info("debug_info_1004" + ex.getMessage());
			ex.printStackTrace();

		}

	}

	 

}
