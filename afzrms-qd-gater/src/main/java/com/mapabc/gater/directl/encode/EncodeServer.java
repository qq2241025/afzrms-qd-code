/**
 * 
 */
package com.mapabc.gater.directl.encode;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.constant.StructionResult;
import com.mapabc.gater.lbsgateway.TerminalTypeList;
import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;

/**
 * @author 
 * 
 */
public class EncodeServer {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(EncodeServer.class);

	
	private static HashMap<String, String> serviceMap = AllConfigCache
			.getInstance().getServiceMap();
	private static HashMap<String, String> serviceKeyMap = AllConfigCache
			.getInstance().getServiceKeyMap();

	public static Object encode(Request req) {
		Object order = "";
		if (null == req) {
			return order;
		}

		String serviceName = req.getServiceName();
		String deviceType = req.getDeviceType();
		if (null == serviceName || serviceName.equals("")) {
			order = "error_" + StructionResult.REQ_SERVICE_NAME_NULL;
			log.info("request serviceName is null");
			return order;
		}
		if (null == deviceType || deviceType.equals("")) {
			order = "error_" + StructionResult.DEVICE_TYPE_NULL;
			log.info("request deviceType is null");
			return order;
		} else if (deviceType.equals("error_"
				+ StructionResult.NO_DEVICE_IN_CACHE)) {// 缓存未加载到该终端
			return "error_" + StructionResult.NO_DEVICE_IN_CACHE;
		}
		String serviceKey = req.getServiceKey();

		EncoderFactory sf = getFactory(deviceType);
		if (sf == null) {
			order = "error_" + StructionResult.ENCODE_CLASS_NULL;
			log.info("encode class is null");
			return order;
		}
		Object encoder = getService(sf, serviceName, serviceKey);
		if (null == encoder) {
			order = "error_" + StructionResult.NO_ADAPT_ENCODE_CLASS;
			log.info(
					"Encoder encoder = getService(sf, serviceName, serviceKey)=> is null");
			return order;
		}
		try {
			// order = encoder.encode(req);

			Method[] mds = encoder.getClass().getMethods();
			boolean isHaveMethod = false;
			for (int i = 0; i < mds.length; i++) {
				if (mds[i].getName().equals(serviceName)) {
					isHaveMethod = true;
					break;
				}
			}
			if (!isHaveMethod) {
				order = "error_" + StructionResult.NO_INTERFACE;
			} else {
				Method method = encoder.getClass().getMethod(serviceName,
						new Class[] { Request.class });
 
				order = method.invoke(encoder, req);
			}

		} catch (Exception e) {
			order = "error_" + StructionResult.CRT_PROTOCAL_ERROR;
			e.printStackTrace();
		}
		return order;
	}

	public static Object getService(EncoderFactory sf, String serviceName,
			String serviceKey) {
		Object ser = null;
		if (null == sf) {
			return ser;
		}
		if (serviceKey == null || serviceKey.equals("")) {// 内部实现v1.1
			String type = getType(serviceName);
			if (null == type) {
				log.info(
						"The serviceName " + serviceName
								+ " is not configed in service.properties");
				return ser;
			}
			if (type.equals("control")) {
				ser = sf.createControl();
			} else if (type.equals("locator")) {
				ser = sf.createLocator();
			} else if (type.equals("alarm")) {
				ser = sf.createAlarm();
			} else if (type.equals("setting")) {
				ser = sf.createSetting();
			} else if (type.equals("query")) {
				ser = sf.createQuery();
			}
		} else {// 适用于用户自扩展终端
			try {

				Method method = sf.getClass().getMethod(
						getServiceKeyType(serviceKey), new Class[] {});
				ser = method.invoke(sf);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				log.error("查找适配接口类异常", e);
				e.printStackTrace();
			}

		}

		return ser;
	}

	@SuppressWarnings("unchecked")
	public static EncoderFactory getFactory(String deviceType) {
		EncoderFactory sf = null;
		if (null == deviceType || deviceType.equals("")) {
			log.info("getFactory by deviceType, type is null");
			return sf;
		}
		try {
			String clsName = getClassName(deviceType);
			if (null == clsName || clsName.equals("")) {
				log.info(deviceType + " encodeClass is null");
				return sf;
			}
			Class serviceCls = Class.forName(clsName);
 			sf = (EncoderFactory) serviceCls.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return sf;
	}

	public static String getClassName(String deviceType) {
		String encodeClass = null;
		if (deviceType != null) {
			TerminalTypeBean typeBean = (TerminalTypeBean) TerminalTypeList
					.getInstance().get(deviceType);
			if (typeBean == null) {
				log.info(
						"\r\n error:<Terminal id=" + deviceType
								+ "> is not exist in "
								+ "terminallist.xml \r\n");
				return null;
			}
			encodeClass = typeBean.getEncodeClass();
		}
		return encodeClass;// (String) prop.getProperty(deviceType);
	}

	private static String getType(String serviceName) {
		return (String) serviceMap.get(serviceName);
	}

	private static String getServiceKeyType(String serviceName) {
		return (String) serviceKeyMap.get(serviceName);
	}

}
