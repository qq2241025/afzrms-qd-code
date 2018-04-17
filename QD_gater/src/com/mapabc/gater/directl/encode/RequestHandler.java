/**
 * 
 */
package com.mapabc.gater.directl.encode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.ParseConfigParamUtil;
import com.mapabc.gater.directl.constant.StructionResult;
import com.mapabc.gater.directl.constant.StructionTypeMap;
import com.mapabc.gater.directl.dbutil.service.DBService;

/**
 * @author 
 * 
 */
public class RequestHandler {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(RequestHandler.class);
	private static String isSaveIns = "1";
	
	@SuppressWarnings("unchecked")
	public static List<Request> parse(Document doc) {

		List<Request> reqs = new ArrayList<Request>();

		if (doc == null) {
			return reqs;
		}

		Element root = doc.getRootElement();
		if (null == root) {
			return reqs;
		}
		Attribute serviceKey = root.attribute("serviceKey");
		String serviceKeyName = "";
		if (serviceKey != null) {
			serviceKeyName = (String) serviceKey.getData();
		}
		Attribute userIdAtt = root.attribute("userId");
		String userId = "";
		if (null != userIdAtt) {
			userId = (String) userIdAtt.getData();
		}
		Attribute typeAtt = root.attribute("type");
		String type = "";
		if (null != typeAtt) {
			type = (String) typeAtt.getData();
		}
		Attribute isLoadAtt = root.attribute("isLoad");
		String isLoad = "";
		if (null != isLoadAtt) {
			isLoad = (String) isLoadAtt.getData();
		}

		Element service = root.element("service");
		if (null != service) {
			String serviceName = "";
			Attribute serviceAtt = service.attribute("name");
			if (null != serviceAtt) {
				serviceName = (String) serviceAtt.getData();
			}
			if (serviceName == null || serviceName.trim().length() <= 0) {
				log.info("request service name is null.");
				return reqs;
			}

			Iterator<Element> it = service.elementIterator();
			HashMap<String, Object> datas = new HashMap<String, Object>();

			HashMap<String, Boolean> isdealed = new HashMap<String, Boolean>();

			// 支持service子节点下扩展节点 by 2010.11.08 sg.z
			while (it.hasNext()) {// 第三层节点
				Element e = (Element) it.next();

				String name = e.getName();

				List eleList = service.elements(name);// service下指定的节点列表
				int size = eleList.size();
				int i = e.nodeCount(); // 子节点数目
				boolean isText = e.isTextOnly();

				if (size > 0 && i > 0 && !isText) {
					if (isdealed.get(name) != null
							&& isdealed.get(name) == true) {// 过滤已轮询的节点
						continue;
					}

					ArrayList<Parameters> moreNodeList = new ArrayList<Parameters>();// 存储多节点信息
					Iterator itsub = eleList.iterator();
					while (itsub.hasNext()) {// 第三层
						Element subElement = (Element) itsub.next();
						Iterator sub = subElement.elementIterator();
						String subName = subElement.getName();

						Parameters param = new Parameters();
						ArrayList<HashMap<String, HashMap<String, String>>> subsets = new ArrayList<HashMap<String, HashMap<String, String>>>();
						HashMap<String, Boolean> subIsDealed = new HashMap<String, Boolean>();

						while (sub.hasNext()) {// 第四层节点
							Element node = (Element) sub.next();
							String nodename = node.getName();
							String nodedata = (String) node.getData();

							List eleList1 = subElement.elements(nodename);// service下指定的节点列表
							int size1 = eleList1.size();
							int i1 = node.nodeCount(); // 子节点数目
							boolean isText1 = node.isTextOnly();

							if (size1 > 0 && i1 > 0 && !isText1) {
								if (subIsDealed.get(nodename) != null
										&& subIsDealed.get(nodename) == true) {// 过滤已轮询的节点
									continue;
								}
								Iterator lastiterator = eleList1.iterator();

								while (lastiterator.hasNext()) {// 第四层节点
									Element lastprenode = (Element) lastiterator
											.next();
									// String lastprenodename = lastprenode
									// .getName();
									Iterator lastit = lastprenode
											.elementIterator();

									HashMap<String, String> lastmap = new HashMap<String, String>();
									while (lastit.hasNext()) {// 第五层节点
										Element lastnode = (Element) lastit
												.next();
										String lastnodename = lastnode
												.getName();
										String lastnodedata = lastnode
												.getTextTrim();
										lastmap.put(lastnodename, lastnodedata);

									}
									HashMap<String, HashMap<String, String>> tmpNode = new HashMap<String, HashMap<String, String>>();
									tmpNode.put(nodename, lastmap);
									param.addSubNodeList(tmpNode);

									subsets.add(tmpNode);

									subIsDealed.put(nodename, true);
								}
								if (subsets.size() > 0) {

									param.addSubSets(name, subsets);
								}

							} else {

								param.addParams(nodename, nodedata);
							}
						}
						moreNodeList.add(param);

					}
					if (moreNodeList.size() > 0)
						datas.put(name, moreNodeList);

					isdealed.put(name, true);

				} else {// 第三层无子节点的节点内容
					datas.put(e.getName(), e.getData());
				}

			}

			String deviceId = "";
			Attribute deviceIdAtt = service.attribute("deviceId");
			if (null != deviceIdAtt) {
				deviceId = (String) deviceIdAtt.getData();
			}

			if (deviceId == null || deviceId.trim().length() <= 0) {
				log.info("request deviceid is null.");
				return reqs;
			}
			String[] devs = deviceId.split(";");

			for (int i = 0; i < devs.length; i++) {

				Request req = new Request();

				req.setReqXml(doc.asXML());
				req.setUserId(userId);
				req.setServiceName(serviceName);
				req.setCmdType(serviceName);
				req.setServiceKey(serviceKeyName);

				if (isLoad != null && isLoad.equals("1")) {
					req.setLoad(true);// 为负载转发请求
				} else {

					String uuidSeq = null;
					try {
						isSaveIns = AllConfigCache.getInstance().getConfigMap().get("isSaveIns");
						//默认保存
						if (isSaveIns == null || isSaveIns.equals("1")){
							
						DBService dbservice = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
						HashMap<String, String> funDesc = StructionTypeMap
								.getInstance().getCmdTypeToServiceDesc();

						uuidSeq = dbservice.saveStructions(devs[i], null, null,
								userId, new Date(), doc.asXML(), new Date(),
								serviceName, null, null, null, null, null, "1",
								funDesc.get(serviceName));
						req.setSequence(uuidSeq);
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					try {
						String isSaveTaskByGate = AllConfigCache.getInstance()
								.getConfigMap().get("isSaveTaskByWzt");
						if (isSaveTaskByGate == null
								|| isSaveTaskByGate.equals("0")) {
							String taskid = saveTask(serviceName, datas,
									devs[i]);// 存入任务指令
							datas.put("taskid", taskid);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					if (datas.containsKey("issynch")) {
						String isSynchs = (String) datas.get("issynch");
						if (isSynchs.equals("1")) {
							req.setSynch(true);
						} else {
							req.setSynch(false);
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}

				req.setDatas(datas);
				req.setDeviceId(devs[i]);
				req.setDeviceType(Controller.getTerminalType(devs[i]));
				reqs.add(req);

			}
		}

		return reqs;
	}

	public static String saveTask(String serviceName,
			HashMap<String, Object> datas, String deviceId) {
		String uuidSeq = "";
		// 消息任务下发存储及关联表入库
		DBService dbservice = null;
		try {
			dbservice = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
		} catch (Exception e) {
			log.error("get DBService bean error", e);
		}
		if ("msg".equals(serviceName)) {
			String title = "";
			String sender = "";
			String desc = "";
			if (datas.containsKey("title")) {
				title = (String) datas.get("title");
			}
			if (datas.containsKey("sender")) {
				sender = (String) datas.get("sender");
			}
			if (datas.containsKey("desc")) {
				desc = (String) datas.get("desc");
			}
			uuidSeq = dbservice.saveTask(title, 0L, desc, sender, null, null,
					"1", deviceId, "0");
		}

		// 目的地任务下发及关联表入库
		if ("destination".equals(serviceName)) {
			String title = "";
			String sender = "";
			String x = "";
			String y = "";
			String desc = "";
			if (datas.containsKey("title")) {
				title = (String) datas.get("title");
			}
			if (datas.containsKey("x")) {
				x = (String) datas.get("x");
			}
			if (datas.containsKey("y")) {
				y = (String) datas.get("y");
			}
			if (datas.containsKey("desc")) {
				desc = (String) datas.get("desc");
			}
			if (datas.containsKey("sender")) {
				sender = (String) datas.get("sender");
			}
			uuidSeq = dbservice.saveTask(title, 1L, desc, sender, null, x + ","
					+ y, "1", deviceId, "1");
		}

		// 路径任务下发及关联表入库
		if ("route".equals(serviceName)) {
			String title = "";
			String sender = "";
			String startPoint = "";
			String viaPoints = "";
			String endPoint = "";
			String desc = "";
			if (datas.containsKey("title")) {
				title = (String) datas.get("title");
			}
			if (datas.containsKey("startPoint")) {
				startPoint = (String) datas.get("startPoint");
			}
			if (datas.containsKey("viaPoints")) {
				viaPoints = (String) datas.get("viaPoints");
			}
			if (datas.containsKey("endPoint")) {
				endPoint = (String) datas.get("endPoint");
			}

			if (datas.containsKey("sender")) {
				sender = (String) datas.get("sender");
			}
			if (datas.containsKey("desc")) {
				desc = (String) datas.get("desc");
			}
			uuidSeq = dbservice.saveTask(title, 2L, desc, sender, null,
					startPoint + ";" + viaPoints + ";" + endPoint, "1",
					deviceId, "1");
		}

		return uuidSeq;
	}

	public static List<Request> parse(HttpServletRequest httpReq) {
		List<Request> reqs = new ArrayList<Request>();
		if (null == httpReq) {
			return reqs;
		}

		String deviceId = (String) httpReq.getParameter("deviceId");
		String userId = (String) httpReq.getParameter("userId");
		// GET方式和POST方式serviceName参数统一成name
		// service=>name
		String serviceName = (String) httpReq.getParameter("name");
		String serviceKey = (String) httpReq.getParameter("serviceKey");

		if (null == deviceId || deviceId.equals("")) {
			log.info("request deviceid is null.");
			return reqs;
		}
		if (null == serviceName || serviceName.equals("")) {
			log.info("request service name is null.");
			return reqs;
		}

		HashMap<String, Object> datas = new HashMap<String, Object>();
		Enumeration<?> en = httpReq.getParameterNames();
		while (en.hasMoreElements()) {
			String key = (String) en.nextElement();
			if (!key.equals("deviceId") && !key.equals("userId")
					&& !key.equals("service") & !key.equals("serviceKey")) {
				String value = httpReq.getParameter(key);
				try {
					value = new String(value.getBytes("ISO-8859-1"), "utf-8");
				} catch (UnsupportedEncodingException e) {
					log.error("Get请求编码错误！", e);
					e.printStackTrace();
				}
				datas.put(key, value);
			}
		}

		String[] devs = deviceId.split(";");
		for (int i = 0; i < devs.length; i++) {
			Request req = new Request();
			req.setUserId(userId);
			req.setServiceName(serviceName);
			req.setCmdType(serviceName);
			req.setDatas(datas);
			req.setDeviceId(devs[i]);
			req.setServiceKey(serviceKey);
			req.setDeviceType(Controller.getTerminalType(devs[i]));
			reqs.add(req);
		}

		return reqs;
	}

	// 非下行到终端XML接口解析
	public static Request parseNonCmdRequest(Document doc) {
		Request req = new Request();

		Element root = doc.getRootElement();

		Attribute serviceKey = root.attribute("serviceKey");
		String serviceKeyName = "";
		if (serviceKey != null) {
			serviceKeyName = (String) serviceKey.getData();
		}
		Attribute userIdAtt = root.attribute("userId");
		String userId = "";
		if (null != userIdAtt) {
			userId = (String) userIdAtt.getData();
		}
		Attribute typeAtt = root.attribute("type");
		String type = "";
		if (null != typeAtt) {
			type = (String) typeAtt.getData();
		}
		Element service = root.element("service");
		if (null != service) {
			String serviceName = "";
			Attribute serviceAtt = service.attribute("name");
			if (null != serviceAtt) {
				serviceName = (String) serviceAtt.getData();
			}
			if (serviceName == null || serviceName.trim().length() <= 0) {
				log.info("request service name is null.");
				return req;
			}

			Iterator<Element> it = service.elementIterator();
			HashMap<String, Object> datas = new HashMap<String, Object>();

			HashMap<String, Boolean> isdealed = new HashMap<String, Boolean>();

			// 支持service子节点下扩展节点 by 2010.11.08 sg.z
			while (it.hasNext()) {// 第三层节点
				Element e = (Element) it.next();

				String name = e.getName();

				List eleList = service.elements(name);// service下指定的节点列表
				int size = eleList.size();
				int i = e.nodeCount(); // 子节点数目
				boolean isText = e.isTextOnly();

				if (size > 0 && i > 0 && !isText) {
					if (isdealed.get(name) != null
							&& isdealed.get(name) == true) {// 过滤已轮询的节点
						continue;
					}

					ArrayList<Parameters> moreNodeList = new ArrayList<Parameters>();// 存储多节点信息
					Iterator itsub = eleList.iterator();
					while (itsub.hasNext()) {// 第三层
						Element subElement = (Element) itsub.next();
						Iterator sub = subElement.elementIterator();
						String subName = subElement.getName();

						Parameters param = new Parameters();
						ArrayList<HashMap<String, HashMap<String, String>>> subsets = new ArrayList<HashMap<String, HashMap<String, String>>>();
						HashMap<String, Boolean> subIsDealed = new HashMap<String, Boolean>();

						while (sub.hasNext()) {// 第四层节点
							Element node = (Element) sub.next();
							String nodename = node.getName();
							String nodedata = (String) node.getData();

							List eleList1 = subElement.elements(nodename);// service下指定的节点列表
							int size1 = eleList1.size();
							int i1 = node.nodeCount(); // 子节点数目
							boolean isText1 = node.isTextOnly();

							if (size1 > 0 && i1 > 0 && !isText1) {
								if (subIsDealed.get(nodename) != null
										&& subIsDealed.get(nodename) == true) {// 过滤已轮询的节点
									continue;
								}
								Iterator lastiterator = eleList1.iterator();

								while (lastiterator.hasNext()) {// 第四层节点
									Element lastprenode = (Element) lastiterator
											.next();
									// String lastprenodename = lastprenode
									// .getName();
									Iterator lastit = lastprenode
											.elementIterator();

									HashMap<String, String> lastmap = new HashMap<String, String>();
									while (lastit.hasNext()) {// 第五层节点
										Element lastnode = (Element) lastit
												.next();
										String lastnodename = lastnode
												.getName();
										String lastnodedata = lastnode
												.getTextTrim();
										lastmap.put(lastnodename, lastnodedata);

									}
									HashMap<String, HashMap<String, String>> tmpNode = new HashMap<String, HashMap<String, String>>();
									tmpNode.put(nodename, lastmap);
									param.addSubNodeList(tmpNode);

									subsets.add(tmpNode);

									subIsDealed.put(nodename, true);
								}
								if (subsets.size() > 0) {

									param.addSubSets(name, subsets);
								}

							} else {

								param.addParams(nodename, nodedata);
							}
						}
						moreNodeList.add(param);

					}
					if (moreNodeList.size() > 0)
						datas.put(name, moreNodeList);

					isdealed.put(name, true);

				} else {// 第三层无子节点的节点内容
					datas.put(e.getName(), e.getData());
				}

			}

			String deviceId = "";
			Attribute deviceIdAtt = service.attribute("deviceId");
			if (null != deviceIdAtt) {
				deviceId = (String) deviceIdAtt.getData();
			}

			String[] devs = deviceId.split(";");

			req.setReqXml(doc.asXML());

			req.setUserId(userId);
			req.setServiceName(serviceName);
			req.setServiceKey(serviceKeyName);
			req.setDatas(datas);
			req.setDeviceId(deviceId);
			// req.setDeviceType(Controller.getTerminalType(devs[i]));

		}

		return req;
	}

	public static Document createDocument(HashMap<String, Integer> resultMap) {
		// 创建空的Document
		Document doc = DocumentHelper.createDocument();

		// 加入根节点
		Element root = doc.addElement("Response");

		Iterator<String> it = resultMap.keySet().iterator();
		while (it.hasNext()) {
			String deviceId = it.next();
			int code = ((Integer) resultMap.get(deviceId)).intValue();

			Element device = root.addElement("device");
			device.addAttribute("id", deviceId);

			Element cd = device.addElement("code");
			cd.setText(code + "");

			Element desc = device.addElement("desc");
			desc.setText(getDesc(code));
		}
		doc.setXMLEncoding("utf-8");
		log.info("接口调用返回结果：" + doc.asXML());
		return doc;
	}

	public static String getDesc(int code) {
		if (code == StructionResult.SEND_SUCCESS) {
			return "命令下发成功";
		} else if (code == StructionResult.NOT_ONLINE) {
			return "终端不在线";
		} else if (code == StructionResult.CMD_INVALID) {
			return "指令无效";
		} else if (code == StructionResult.NO_RESP0NSE) {
			return "终端无应答";
		} else if (code == StructionResult.SEND_FALIED) {
			return "命令下发失败";
		} else if (code == StructionResult.CMD_NULL) {
			return "无适配指令";
		} else if (code == StructionResult.DEVICEID_NULL) {
			return "设备ID为空";
		} else if (code == StructionResult.DEVICE_TYPE_NULL) {
			return "终端类型不存在";
		} else if (code == StructionResult.LINK_CHANNEL_TYPE_NULL) {
			return "链路通道类型为空";
		} else if (code == StructionResult.TERM_SETTING_FAILED) {
			return "终端设置失败";
		} else if (code == StructionResult.LOAD_FORWARD_FAILED) {
			return "负载转发失败,请检查负载网关是否正常.";
		} else if (code == StructionResult.REQ_SERVICE_NAME_NULL) {
			return "serviceName is null";
		} else if (code == StructionResult.ENCODE_CLASS_NULL) {
			return "编码类不存在";
		} else if (code == StructionResult.NO_INTERFACE) {
			return "接口方法不存在";
		} else if (code == StructionResult.CRT_PROTOCAL_ERROR) {
			return "构建协议异常,请检查XML接口参数是否正确！";
		} else if (code == StructionResult.NO_DEVICE_IN_CACHE) {
			return "缓存无此终端";
		} else if (code == StructionResult.XML_FMT_ERROR) {
			return "请求XML格式有误";
		} else if (code == StructionResult.LICENCE_ERROR) {
			return "Licence错误";
		} else if (code == StructionResult.NO_ADAPT_ENCODE_CLASS) {
			return "接口类不存在";
		} else {
			return "下发失败";
		}
	}

}
