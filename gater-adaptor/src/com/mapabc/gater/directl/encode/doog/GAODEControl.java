package com.mapabc.gater.directl.encode.doog;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
 
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.PropertyReader;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;
import com.mapabc.gater.util.HttpUtil;
import com.mapabc.geom.CoordCvtAPI;

public class GAODEControl extends ControlAdapter {
	private String verNum;
	private PropertyReader propertyRead;

	public GAODEControl() {
		try {
			propertyRead = new PropertyReader("version.properties");
			String ver = propertyRead.getProperty("curVer");// 获取版本号
			verNum = ver;
		} catch (Exception e) {

			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String msg(Request req) {
		////req.setCmdId("50");
		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("50,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");

		buffer.append("(");
		buffer.append(convert((String) req.getDatas().get("title")) + ",");
		buffer.append(convert((String) req.getDatas().get("content")));
		buffer.append(")");
		buffer.append(",");
		buffer.append(Tools.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss")
				+ ",");
		buffer.append(convert((String) req.getDatas().get("sender")) + ",");
		buffer.append(convert((String) req.getDatas().get("taskid")));
		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());
		
		 

		return hex;
	}

	public String destination(Request req) {
		////req.setCmdId("51");
		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("51,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");

		buffer.append("(");
		buffer.append(convert((String) req.getDatas().get("title")) + ",");
		buffer.append(convert((String) req.getDatas().get("desc")) + ",");
		buffer.append(convertX((String) req.getDatas().get("x")) + "!");
		buffer.append(convertX((String) req.getDatas().get("y")));
		buffer.append(")");
		buffer.append(",");
		buffer.append(Tools.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss")
				+ ",");
		buffer.append(convert((String) req.getDatas().get("sender")) + ",");
		buffer.append(convert((String) req.getDatas().get("posdesc")) + ",");
		buffer.append(convert((String) req.getDatas().get("taskid")));
		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());
	 
		return hex;
	}

	// public String route(Request req) {
	// ////req.setCmdId("53");
	// String doogCmd = "$WZTREQ,53," + req.getDeviceId() + ",0,(";
	//
	// String title = (String) req.getDatas().get("title");
	// String desc = (String) req.getDatas().get("desc");
	// String startP = (String) req.getDatas().get("startPoint");
	// String viaPoints = (String) req.getDatas().get("viaPoints");
	// String endP = (String) req.getDatas().get("endPoint");
	// String sender = convert((String) req.getDatas().get("sender"));
	// String taskId = convert((String) req.getDatas().get("taskid"));
	// Date date = new Date();
	// String sendTime = Tools.formatDate2Str(date, "yyyy-MM-dd HH:mm:ss");
	//
	// String doogReqXml = convert(this.createRouteRequestXml(req.getDeviceId(),
	// title, desc,
	// startP, viaPoints, endP));
	//
	// // doogCmd += doogReqXml + ",";
	// doogCmd += title + "," + desc;
	//
	// String routeXml = this.makeRouteXml(startP, viaPoints, endP);
	// String routeHex = "";
	// try {
	// byte[] routeBytes = this.getRouteBytes(routeXml);
	//
	// if (routeBytes != null) {
	// routeHex = Tools.bytesToHexString(routeBytes);
	// doogCmd += "," + routeHex + "," + sendTime + "," + sender + "," + taskId
	// + ")#";
	// }
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// String hex = Tools.bytesToHexString(doogCmd.getBytes());//
	// +doogReqHex+Tools.bytesToHexString(",".getBytes())+routeHex+Tools.bytesToHexString("#".getBytes());
	// log.info("SDK路径内容:" + doogCmd);
	// if (verNum != null && verNum.equals("0.4")) {
	// BaJuTask bjTask = new BaJuTask();
	// bjTask.setDeviceId(req.getDeviceId());
	// bjTask.setReqCont(req.getReqXml());
	// bjTask.setTaskContet(doogCmd);
	// bjTask.setState("1");
	// bjTask.setTaskTitle((String) req.getDatas().get("title"));
	// bjTask.setType("2");
	// bjTask.setUserId(req.getUserId());
	// bjTask.setTimestamp(new Timestamp(date.getTime()));
	// bjTask.setTaskId((String) req.getDatas().get("taskid"));
	// bjTask.setSender((String) req.getDatas().get("sender"));
	//
	// DBService service = new BajuDBServiceImpl();
	// service.saveBaJuTask(bjTask);
	// }
	// return hex;
	// }
    //协议的括号可有可无，终端过滤掉
	public String route(Request req) {
		////req.setCmdId("52");

		String title = (String) req.getDatas().get("title");
		String desc = (String) req.getDatas().get("desc");
		String startP = (String) req.getDatas().get("startPoint");
		String viaPoints = (String) req.getDatas().get("viaPoints");
		String endP = (String) req.getDatas().get("endPoint");
		String sender = convert((String) req.getDatas().get("sender"));
		String taskId = convert((String) req.getDatas().get("taskid"));
		String isReqNavi = RequestUtil.getReqData(req, "isReqNavi");

		Date date = new Date();
		String sendTime = Tools.formatDate2Str(date, "yyyy-MM-dd HH:mm:ss");

		String doogReqXml = convert(this.createRouteRequestXml(req
				.getDeviceId(), title, desc, startP, viaPoints, endP));
		String isdeal = "0";
		if (isReqNavi.equals("1"))
			isdeal = "1";
		String doogCmd = "$WZTREQ,52," + req.getDeviceId() + "," + isdeal
				+ ",";
		doogCmd += this.convert(title) + "," + this.convert(desc) + ",";

		CoordCvtAPI api = new CoordCvtAPI();
		String startAddress = "";
		try {
			startAddress = api.getAddress(startP.split(",")[0], startP
					.split(",")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String startTmp = startP.split(",")[0] + "!" + startP.split(",")[1]
				+ "!" + startAddress;

		String endAddress = "";
		try {
			endAddress = api.getAddress(endP.split(",")[0], endP.split(",")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}

		String endTmp = endP.split(",")[0] + "!" + endP.split(",")[1] + "!"
				+ endAddress;

		String[] virPs = viaPoints.split(";");

		StringBuilder sbuilder = new StringBuilder();
		StringBuilder virBuf = new StringBuilder();

		for (int k = 0; k < virPs.length; k++) {
			String[] vpxy = virPs[k].split(",");
			if (vpxy == null || vpxy.length < 2)
				continue;
			String vx = vpxy[0];
			String vy = vpxy[1];
			String viraddr = "";
			try {
				viraddr = api.getAddress(vx, vy);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String tmp = this.convert(vx + "!" + vy + "!" + viraddr) + ",";
			virBuf.append(tmp);
		}
		doogCmd += this.convert(startTmp) + "," + virBuf.toString() + this.convert(endTmp) + ",";

		String routeHex = "";
		if (isReqNavi.equals("1")) {
			String routeXml = this.makeRouteXml(startP, viaPoints, endP);
			try {
				byte[] routeBytes = this.getRouteBytes(routeXml);

				if (routeBytes != null) {
					routeHex = Tools.bytesToHexString(routeBytes);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		doogCmd += routeHex + "," + sendTime + "," + sender + "," + taskId
				+ "#";

		String hex = Tools.bytesToHexString(doogCmd.getBytes());// +doogReqHex+Tools.bytesToHexString(",".getBytes())+routeHex+Tools.bytesToHexString("#".getBytes());
		 
		return hex;
	}

	private String SicenToComm(double value) {
		String retValue = null;
		NumberFormat df = NumberFormat.getInstance();

		df.setMinimumFractionDigits(0);
		df.setMaximumFractionDigits(0);
		retValue = df.format(value);
		retValue = retValue.replaceAll(",", "");
		return retValue;
	}

	// 导航路径请求XML
	private String makeRouteXml(String startPoint, String viaPoint,
			String endPoint) {
		String xml = "<route Type=0 Flag=1966539 Uuid=7654321 Vers=2.0>";
		String viaXml = "";
		String[] startxy = startPoint.split(",");
		String[] endxy = endPoint.split(",");
		String[] viaPoints = viaPoint.split(";");

		if (null != viaPoints) {
			for (int i = 0; i < viaPoints.length; i++) {
				String[] p = viaPoints[i].split(",");
				if (p == null || p.length < 2)
					continue;
				viaXml += "<viapoint>";
				viaXml += "<x>" + p[0] + "</x>";
				viaXml += "<y>" + p[1] + "</y>";
				viaXml += "</viapoint>";
			}
		}
		if (startxy != null && endxy != null) {
			xml += "<startpoint><x>" + startxy[0] + "</x><y>" + startxy[1]
					+ "</y></startpoint>" + viaXml + "<endpoint><x>" + endxy[0]
					+ "</x><y>" + endxy[1] + "</y></endpoint>";

		}
		xml += "</route>";
		 
		return xml;
	}

	// 获取导航路径内容
	public byte[] getRouteBytes(String requestxml) throws Exception {
		byte[] route = null;
		String routeService = null;
//		try{
//		routeService = AllConfigCache.getInstance().getConfigMap().get("routeService");
//		}catch(Exception e){
//			
//		}
		route = HttpUtil.getPostURLData(routeService, requestxml.getBytes());
		 
		return route;
	}

	/**
	 * 创建下发路径地指令 deviceId:手机号码 title:消息标题 content:消息内容
	 */
	public String createRouteRequestXml(String deviceId, String title,
			String content, String startPoint, String virPoints, String endPoint) {
		// TODO Auto-generated method stub
		String xml = null;

		String[] startP = startPoint.split(",");
		String fsx = startP[0];
		String fsy = startP[1];

		String[] endP = endPoint.split(",");
		String fex = endP[0];
		String fey = endP[1];

		String[] virPs = virPoints.split(";");

		StringBuilder sbuilder = new StringBuilder();
		StringBuilder virBuf = new StringBuilder();

		for (int k = 0; k < virPs.length; k++) {
			String[] vpxy = virPs[k].split(",");
			if (vpxy == null || vpxy.length < 2)
				continue;
			String vx = vpxy[0];
			String vy = vpxy[1];
			virBuf.append("<viaPoint>");
			virBuf.append("<x>" + vx + "</x>");
			virBuf.append("<y>" + vy + "</y>");
			virBuf.append("</viaPoint>");

		}
		String pdate = Tools.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss");

		sbuilder.append("<?xml version=\"1.0\" encoding=\"GB2312\"?>");
		sbuilder.append("<mapabc version=\"1.0\">");
		sbuilder.append("<request requestId=\"" + deviceId + "\" userId=\""
				+ deviceId + "\" >");
		sbuilder.append("<taskDispatchRequest>");
		sbuilder.append("<taskInfo taskId=\"" + pdate + "\" userId=\""
				+ deviceId + "\" sendTime=\"" + pdate + "\">");
		sbuilder.append("<taskDescription>" + content + "</taskDescription>");
		sbuilder.append("<taskTitle>" + title + "</taskTitle>");
		sbuilder.append("<navigationInfo>");

		sbuilder.append("<startPoint>");
		sbuilder.append("<x>" + fsx + "</x>");
		sbuilder.append("<y>" + fsy + "</y>");
		sbuilder.append("</startPoint>");

		sbuilder.append(virBuf.toString()); // 中途点

		sbuilder.append("<endPoint>");
		sbuilder.append("<x>" + fex + "</x>");
		sbuilder.append("<y>" + fey + "</y>");
		sbuilder.append("</endPoint>");

		sbuilder.append("</navigationInfo>");
		sbuilder.append("</taskInfo>");
		sbuilder.append("</taskDispatchRequest>");
		sbuilder.append("</request>");
		sbuilder.append("</mapabc>");
		try {
			xml = URLDecoder.decode(sbuilder.toString(), "GB2312");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return xml;
	}

	public String convey(Request req) {
		////req.setCmdId("999");
		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("999,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");
		buffer.append(convert((String) req.getDatas().get("data")));
		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());

		return hex;
	}

	public String querySetting(Request req) {
		////req.setCmdId("40");
		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("40,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");
		buffer.append("()");
		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());

		return hex;
	}

	public String initSetting(Request req) {
		////req.setCmdId("31");
		StringBuffer buffer = new StringBuffer();

		buffer.append("$WZTREQ,");
		buffer.append("31,");
		buffer.append(req.getDeviceId() + ",");
		buffer.append("0,");

		buffer.append("(");
		buffer.append(convert((String) req.getDatas().get("url")) + ",");
		buffer.append(convert((String) req.getDatas().get("num")));
		buffer.append(")");

		buffer.append("#");

		String hex = Tools.bytesToHexString(buffer.toString().getBytes());

		return hex;
	}

	public String convertX(String x) {
		if (null == x || x.equals("")) {
			return "";
		}
		return x.replace(",", "!");
	}

	public String convert(String str) {
		if (null == str || str.equals("")) {
			return "";
		}
		str = str.replace("&", "&num;");
		str = str.replace("$", "&dol;");
		str = str.replace(",", "&cma;");
		str = str.replace("(", "&lps;");
		str = str.replace(")", "&rps;");
		str = str.replace("#", "&num;");
		return str;
	}
}
