/**
 * 非下行指令XML解析
 */
package com.mapabc.gater.directl.encode;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mapabc.gater.directl.AllConfigCache;

public class NonCmdEncodeServer {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(NonCmdEncodeServer.class);


	private static HashMap<String, String> nonCmdServiceKeyMap = AllConfigCache
			.getInstance().getNonMtCmdServiceMap();

	public static Object encodeNonCmdQuquest(Request req) {

		String serviceName = req.getServiceName();
		Object encoder = null;
		if (null == serviceName || serviceName.equals("")) {
			log.info(
							"request serviceName is null,xml====\r\n"
									+ req.getReqXml());
			encoder = responseDocment("0","XML接口名称不允许为空。");
			return encoder;
		}

		String serviceKey = req.getServiceKey();


		try {
			encoder = getNonCmdService(serviceName, serviceKey, req);
		} catch (Exception e) {
			log.error("接口解析失败",e);
			encoder = responseDocment("-1","接口解析失败");
		}
		return encoder;
	}

	private static Document responseDocment(String result,String desc) {

		Document retDoc = DocumentHelper.createDocument();
		Element resp = retDoc.addElement("response");
		Element resemt = resp.addElement("result");
		resemt.setText(result);
		Element descemt = resp.addElement("desc");
		descemt.setText(desc);
		retDoc.setXMLEncoding("utf-8");
		return retDoc;
	}

	// 非下行指令
	public static Object getNonCmdService(String serviceName,
			String serviceKey, Request req) {
		Object ser = null;

		String className=null;
		try {
			className = getClassName(serviceKey);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (className == null || className.equals("")) {
			className = "com.mapabc.gater.lbsgateway.service.common.RequestServiceImpl";
		}
		Class cls = null;

		try {
			cls = Class.forName(className);
			Object obj = cls.newInstance();
			if (cls != null) {

				Method method = obj.getClass().getMethod(serviceName,
						new Class[] { Request.class });

				ser = method.invoke(obj, req);

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("查找适配接口类异常", e);
			e.printStackTrace();
		}

		return ser;
	}

	public static String getClassName(String classKey) throws Exception{
		String encodeClass = null;
		if (classKey != null && classKey.length()>0) {

			encodeClass = nonCmdServiceKeyMap.get(classKey);

		}
		return encodeClass;// (String) prop.getProperty(deviceType);
	}

}
