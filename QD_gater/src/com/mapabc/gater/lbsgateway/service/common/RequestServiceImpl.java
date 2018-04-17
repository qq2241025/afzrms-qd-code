/**
 * 
 */
package com.mapabc.gater.lbsgateway.service.common;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.lbsgateway.GBLTerminalList;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.util.AppHelper;
import com.mapabc.gater.util.HttpUtil;
import com.mapabc.gater.util.PropertyReader;

/**
 * @author 
 * 修改"deviceIds"网关统一为"deviceId"
 */
public class RequestServiceImpl {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(RequestServiceImpl.class);

	public Document lbsService(Request req) {
		Document reqDoc = null;
		Document resDoc = null;

		try {
			reqDoc = DocumentHelper.parseText(req.getReqXml());

			Element root = reqDoc.getRootElement();
			Element serviceElement = root.element("service");

			// TODO
//			LbsHandle lbsHand = new LbsHandle();

//			resDoc = lbsHand.handleRequest(serviceElement);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resDoc;
	}

	public Document modifyDeviceNotice(Request req) {
		String isLoad = null;
		String otherUrl = null;
		String localMtUrl = null;
		String tempUrl = null;
		Document reqDoc = null;
		Document resDoc = DocumentHelper.createDocument();
		Element resEmt = resDoc.addElement("response");
		try {
			isLoad = new PropertyReader("load.properties")
					.getProperty("isOverLoad");
			otherUrl = new PropertyReader("load.properties")
					.getProperty("loadMtUrl");
			localMtUrl = new PropertyReader("load.properties")
					.getProperty("localMtUrl");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			reqDoc = DocumentHelper.parseText(req.getReqXml());
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Element root = reqDoc.getRootElement();
		Element serviceElement = root.element("service");
		Element tempEmt = serviceElement.element("flag");

		String deviceIds = serviceElement.attributeValue("deviceId");

		if (deviceIds != null) {//加载指定终端
			String[] devIds = deviceIds.split(",");
			if (devIds != null) {
				for (int i = 0; i < devIds.length; i++) {
					String deviceid = devIds[i];
					TTerminal term = GBLTerminalList.getInstance().reloadTerminalById(deviceid);
					if (term != null){
						Element devEmt = resEmt.addElement("deviceId");
						devEmt.setText(term.getDeviceId());
					}
				}
			}
		} else {//加载所有终端
			ConcurrentMap<String, TTerminal> allTerms = GBLTerminalList.getInstance().loadTerminals();
			Collection<TTerminal> terms = allTerms.values();
			for (TTerminal term:terms){
				Element devEmt = resEmt.addElement("deviceId");
				devEmt.setText(term.getDeviceId());
			}
		}
		if (tempEmt != null) {
			tempUrl = tempEmt.getTextTrim();
			if (tempUrl != null && tempUrl.equals("1")) {// 过滤本机获取到的已通知过的接口
				resDoc.setXMLEncoding("utf-8");
				return resDoc;
			}
		}
		if (isLoad != null && isLoad.equals("1")) {
			Document tempDoc = DocumentHelper.createDocument();
			Element reqEmt = tempDoc.addElement("request");
			reqEmt.addAttribute("type", "1");
			Element service = reqEmt.addElement("service");
			service.addAttribute("name", "modifyDeviceNotice");
			service.addAttribute("deviceId", deviceIds);
			Element flag = service.addElement("flag");
			flag.setText("1");// 临时标志
			tempDoc.setXMLEncoding("utf-8");

			String xml = tempDoc.asXML();
			log.info("负载转发：" + xml);
			try {
				HttpUtil.getXmlRespByPostXml(otherUrl + "/"
						+ AppHelper.getWebAppName() + "/service", tempDoc,
						"utf-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// GPSForwardToJMS.getInstance().sendSingleMessage(otherUrl, xml);

		}
		resDoc.setXMLEncoding("utf-8");
		return resDoc;
	}

	public Document deleteDeviceNotice(Request req) {
		String isLoad = null;
		String otherUrl = null;
		String localMtUrl = null;
		String tempUrl = null;
		Document reqDoc = null;
		Document resDoc = DocumentHelper.createDocument();;
		Element resEmt = resDoc.addElement("response");
		
		try {
			isLoad = new PropertyReader("load.properties")
					.getProperty("isOverLoad");
			otherUrl = new PropertyReader("load.properties")
					.getProperty("loadMtUrl");
			localMtUrl = new PropertyReader("load.properties")
					.getProperty("localMtUrl");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			reqDoc = DocumentHelper.parseText(req.getReqXml());
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Element root = reqDoc.getRootElement();
		Element serviceElement = root.element("service");
		Element tempEmt = serviceElement.element("flag");

		String deviceIds = serviceElement.attributeValue("deviceId");
		String[] devIds = deviceIds.split(",");
		if (devIds != null) {
 			
			for (int i = 0; i < devIds.length; i++) {
				String deviceid = devIds[i];
				GBLTerminalList.getInstance().removeTerminal(deviceid);
				
				Element devEmt = resEmt.addElement("deletedDeviceId");
				devEmt.setText(deviceid);
			}
		}
		if (tempEmt != null) {
			tempUrl = tempEmt.getTextTrim();
			if (tempUrl != null && tempUrl.equals("1")) {// 过滤本机获取到的已通知过的接口
				return null;
			}
		}
		if (isLoad != null && isLoad.equals("1")) {
			Document tempDoc = DocumentHelper.createDocument();
			Element reqEmt = tempDoc.addElement("request");
			reqEmt.addAttribute("type", "1");
			Element service = reqEmt.addElement("service");
			service.addAttribute("name", "deleteDeviceNotice");
			service.addAttribute("deviceId", deviceIds);
			Element flag = service.addElement("flag");
			flag.setText("1");// 临时标志
			tempDoc.setXMLEncoding("utf-8");

			String xml = tempDoc.asXML();
			log.info("负载转发：" + xml);

			try {
				HttpUtil.getXmlRespByPostXml(otherUrl + "/"
						+ AppHelper.getWebAppName() + "/service", tempDoc,
						"utf-8");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return resDoc;
	}

	/**
	 * 数据转发通知
	 * 
	 * @param serviceElement
	 * @return
	 * @author 
	 */
	public Document addForwardNotice(Request req) {
		String isLoad = null;
		String otherUrl = null;
		String localMtUrl = null;
		String tempUrl = null;
		Document reqDoc = null;

		Document retDoc = DocumentHelper.createDocument();
		Element resp = retDoc.addElement("response");
		Element result = resp.addElement("result");

		try {
			isLoad = new PropertyReader("load.properties")
					.getProperty("isOverLoad");
			otherUrl = new PropertyReader("load.properties")
					.getProperty("loadMtUrl");
			localMtUrl = new PropertyReader("load.properties")
					.getProperty("localMtUrl");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			try {
				reqDoc = DocumentHelper.parseText(req.getReqXml());
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Element root = reqDoc.getRootElement();
			Element serviceElement = root.element("service");

			Element tempEmt = serviceElement.element("flag");

			String deviceIds = serviceElement.attributeValue("deviceId");
			String[] devIds = deviceIds.split(",");

			Element eidsemt = serviceElement.element("eids");
			String eids = null;
			if (eidsemt != null)
				eids = eidsemt.getTextTrim();

			Element ipemt = serviceElement.element("ip");
			String ip = ipemt.getTextTrim();

			Element portemt = serviceElement.element("port");
			int port = Integer.parseInt(portemt.getTextTrim());

			Element typeemt = serviceElement.element("type");
			String type = typeemt.getTextTrim().equals("0") ? "tcp" : "udp";

			log.info("增加转发配置通知：eids=" + eids + ",deviceIds=" + deviceIds
					+ ",ip=" + ip + ",port=" + port + ",type=" + type);
			// TODO
//			ProxyForwardService.addForward(null, deviceIds, ip, port, type);

			if (tempEmt != null) {
				tempUrl = tempEmt.getTextTrim();
				if (tempUrl != null && tempUrl.equals("1")) {// 过滤本机获取到的已通知过的接口
					return null;
				}
			}
			if (isLoad != null && isLoad.equals("1")) {
				Document tempDoc = DocumentHelper.createDocument();
				Element reqEmt = tempDoc.addElement("request");
				reqEmt.addAttribute("type", "1");
				Element service = reqEmt.addElement("service");
				service.addAttribute("name", "addForwardNotice");
				service.addAttribute("deviceId", deviceIds);
				Element flag = service.addElement("flag");
				flag.setText("1");// 临时标志

				service.add(ipemt);
				service.add(portemt);
				service.add(typeemt);

				tempDoc.setXMLEncoding("utf-8");

				String xml = tempDoc.asXML();
				log.info("负载转发：" + xml);
				try {
					HttpUtil.getXmlRespByPostXml(otherUrl + "/"
							+ AppHelper.getWebAppName() + "/service", tempDoc,
							"utf-8");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			result.setText("1");
			retDoc.setXMLEncoding("utf-8");
		} catch (Exception e) {
			log.error("增加转发通知异常", e);
			result.setText("0");
			retDoc.setXMLEncoding("utf-8");
		}

		return retDoc;
	}

	public Document deleteForwardNotice(Request req) {
		String isLoad = null;
		String otherUrl = null;
		String localMtUrl = null;
		String tempUrl = null;
		Document reqDoc = null;

		Document retDoc = DocumentHelper.createDocument();
		Element resp = retDoc.addElement("response");
		Element result = resp.addElement("result");

		try {
			isLoad = new PropertyReader("load.properties")
					.getProperty("isOverLoad");
			otherUrl = new PropertyReader("load.properties")
					.getProperty("loadMtUrl");
			localMtUrl = new PropertyReader("load.properties")
					.getProperty("localMtUrl");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {

			try {
				reqDoc = DocumentHelper.parseText(req.getReqXml());
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			Element root = reqDoc.getRootElement();
			Element serviceElement = root.element("service");

			Element tempEmt = serviceElement.element("flag");

			String deviceIds = serviceElement.attributeValue("deviceId");
			String[] devIds = deviceIds.split(",");

			Element eidsemt = serviceElement.element("eids");
			String eids = null;
			if (eidsemt != null)
				eids = eidsemt.getTextTrim();

			Element ipemt = serviceElement.element("ip");
			String ip = ipemt.getTextTrim();

			Element portemt = serviceElement.element("port");
			int port = Integer.parseInt(portemt.getTextTrim());

			Element typeemt = serviceElement.element("type");
			String type = typeemt.getTextTrim().equals("0") ? "tcp" : "udp";

			log.info("删除转发配置通知：eids=" + eids + ",deviceIds=" + deviceIds
					+ ",ip=" + ip + ",port=" + port + ",type=" + type);

			// TODO
//			ProxyForwardService.deleteForward(null, deviceIds, ip, port, type);

			if (tempEmt != null) {
				tempUrl = tempEmt.getTextTrim();
				if (tempUrl != null && tempUrl.equals("1")) {// 过滤本机获取到的已通知过的接口
					return null;
				}
			}
			if (isLoad != null && isLoad.equals("1")) {
				Document tempDoc = DocumentHelper.createDocument();
				Element reqEmt = tempDoc.addElement("request");
				reqEmt.addAttribute("type", "1");
				Element service = reqEmt.addElement("service");
				service.addAttribute("name", "deleteForwardNotice");
				service.addAttribute("deviceId", deviceIds);
				Element flag = service.addElement("flag");
				flag.setText("1");// 临时标志

				tempDoc.setXMLEncoding("utf-8");

				String xml = tempDoc.asXML();
				log.info("负载转发:" + xml);
				try {
					HttpUtil.getXmlRespByPostXml(otherUrl + "/"
							+ AppHelper.getWebAppName() + "/service", tempDoc,
							"utf-8");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (DocumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			result.setText("1");
			retDoc.setXMLEncoding("utf-8");
		} catch (Exception e) {
			log.error("删除转发通知异常", e);
			result.setText("0");
			retDoc.setXMLEncoding("utf-8");
		}
		return retDoc;
	}

}
