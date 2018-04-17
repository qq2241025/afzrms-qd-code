package com.mapabc.gater.directl.encode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.TermOnlieStatus;
import com.mapabc.gater.directl.bean.command.TStructions;
import com.mapabc.gater.directl.constant.StructionResult;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.parse.service.ResponseParseService;
import com.mapabc.gater.lbsgateway.GBLTerminalList;
import com.mapabc.gater.lbsgateway.TerminalTypeList;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;
import com.mapabc.gater.lbsgateway.service.ICommonGatewayService;
import com.mapabc.gater.lbsgateway.service.licence.LicenceValidator;
import com.mapabc.gater.util.AppHelper;
import com.mapabc.gater.util.HttpUtil;
import com.mapabc.gater.util.ReplyResponseUtil;
import com.mapabc.gater.util.TimeUtil;

public class Controller {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(Controller.class);

	private static String flag = null;
	private static String isLoad = null;
	private static int timeOut = 60;

	private static String localMtUrl;
	private static OrderHandler handle = new OrderHandler();
	private static String order = null;

	private static HashMap<String, Integer> resultMap = new HashMap<String, Integer>();

	public static void loadConfig() throws Exception {
		HashMap<String, String> configMap = AllConfigCache.getInstance()
				.getConfigMap();
		if (configMap != null) {
			flag = configMap.get("isNeedBuffer");
			timeOut = Integer.parseInt(configMap.get("TIME_OUT"));
		}
		HashMap<String, String> loadMap = AllConfigCache.getInstance()
				.getLoadMap();
		if (loadMap != null) {
			isLoad = loadMap.get("isOverLoad");
			localMtUrl = loadMap.get("localMtUrl");
		}
	}

	/**
	 * handle Http get request
	 * 
	 * @param httpReq
	 * @return
	 * @throws Exception
	 */
	public static Document doGet(HttpServletRequest httpReq) throws Exception {
		try {
			if (!LicenceValidator.getInstance().validateGater()) {
				Document errordoc = DocumentHelper.createDocument();

				// 加入根节点
				Element root = errordoc.addElement("response");

				Element errorCode = root.addElement("errorCode");
				errorCode.setText("-1");
				Element errorCause = root.addElement("errorCause");
				errorCause.setText(LicenceValidator.getInstance()
						.getErrorInfo());
				log.info("接口调用返回结果：" + errordoc.asXML());
				return errordoc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		loadConfig();
		List<Request> reqs = RequestHandler.parse(httpReq);
		return sendOrders(reqs);
	}

	/**
	 * handle Http Post request
	 * 
	 * @param doc
	 * @return
	 * @throws Exception
	 */
	public static Document doPost(Document doc) throws Exception {
		try {
			if (!LicenceValidator.getInstance().validateGater()) {
				Document errordoc = DocumentHelper.createDocument();

				// 加入根节点
				Element root = errordoc.addElement("response");

				Element errorCode = root.addElement("errorCode");
				errorCode.setText("-1");
				Element errorCause = root.addElement("errorCause");
				errorCause.setText(LicenceValidator.getInstance()
						.getErrorInfo());
				log.info("接口调用返回结果：" + errordoc.asXML());
				return errordoc;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			loadConfig();
		} catch (Exception e) {
		}
		log.info("Order service received req : " + doc.asXML());

		Element root = doc.getRootElement();
		Attribute typeAtt = root.attribute("type");
		String type = null;
		if (null != typeAtt) {
			type = (String) typeAtt.getData();
		}
		if (type != null && type.trim().equals("1")) {// 非终端指令下行接口调用
			Document resDoc = null;

			Request req = RequestHandler.parseNonCmdRequest(doc);
			Object obj = NonCmdEncodeServer.encodeNonCmdQuquest(req);
			if (obj != null)
				resDoc = (Document) obj;
			if (resDoc != null) {
				log.info("response result:" + resDoc.asXML());
			}
			return resDoc;
		}

		List<Request> reqs = RequestHandler.parse(doc);
		log.info("Order service parse req sucess, req : " + doc.asXML());
		return sendOrders(reqs);
	}

	public static Document sendOrders(List<Request> reqs) {
		if (reqs == null || reqs.size() > 0) {
			for (int i = 0; i < reqs.size(); i++) {
				Request req = reqs.get(i);
				req
						.setReceiveDate(new java.sql.Date(System
								.currentTimeMillis()));

				int result = sendOrder(req);

				resultMap.put(req.getDeviceId(), result);
			}
		} else {
			resultMap.put("", 14);// 传输参数异常
		}

		Document retDoc = RequestHandler.createDocument(resultMap);
		resultMap.clear();
		return retDoc;
	}

	// 有负载均衡机器的情况
	public static int reloadForward(Request req, int res) {
		int result = res;

		if (isLoad != null && isLoad.trim().equals("1")) {// 对于发送失败的转发到负载机器
			// 避免两条机器失败循环互相发送情况
			TerminalTypeBean typeBean = (TerminalTypeBean) TerminalTypeList
					.getInstance().getTerminalType(req.getDeviceType());

			if (typeBean != null) {
				String linktype = typeBean.getMtType() == null ? "0" : typeBean
						.getMtType();
				result = forwardReq(req, linktype);

			}
		}
		return result;
	}

	// 此处错误码返回
	public static String getTerminalType(String deviceId) {
		TTerminal ter = GBLTerminalList.getInstance().getTerminaInfo(deviceId);
		String termType = "";

		if (null != ter) {

			termType = ter.getTEntTermtype();
			if (null == termType || termType == "") {
				ter = GBLTerminalList.getInstance()
						.reloadTerminalById(deviceId);
				if (ter != null)
					termType = ter.getTEntTermtype();
				else
					termType = Const.PROTOCAL_TYPE_DEFAULT;
			}

		} else {
			termType = Const.PROTOCAL_TYPE_DEFAULT;
			log.info("缓存未加载到关联组终端：" + deviceId + ",使用默认类型Const.PROTOCAL_TYPE_DEFAULT");
			// return "error_" + StructionResult.NO_DEVICE_IN_CACHE;
		}

		return termType;

		// return (termType == null || termType.trim().length() <= 0) ?
		// "GP-UNKNOW-GPRS"
		// : termType;
	}

	public static int sendOrder(Request req) {
		int result = StructionResult.SEND_FALIED;

		order = (String) EncodeServer.encode(req);
		Order od = new Order(req.getCmdId(), req.getDeviceId(), req
				.getDeviceType(), order);
		log.info("################Sending order, DeviceId : "
				+ od.getDeviceId() + " DeviceType : " + od.getDeviceType()
				+ " order : " + od.getContent());

		if (order != null && order.trim().length() > 0
				&& order.startsWith("error_")) {
			// 错误提示处理
			String errCode = order.substring(order.indexOf("error_") + 6);
			order = null;
			result = Integer.parseInt(errCode);
		} else if (order == null || order.trim().length() <= 0) {
			// 无适配的指令
			log.info("Send failed,because cmd is null,deviceId="
					+ req.getDeviceId());
			result = StructionResult.CMD_NULL;
		} else if (null == od) {
			result = StructionResult.CMD_NULL;
			log.info("Send failed, because : order is null!");
			// return result;
		} else if (null == od.getDeviceId() || od.getDeviceId().equals("")) {
			result = StructionResult.DEVICEID_NULL;// 请求设备ID为空
			log.info("Send failed, because : deviceId is null");
			// return result;
		} else if (null == od.getDeviceType() || od.getDeviceType().equals("")) {
			result = StructionResult.DEVICE_TYPE_NULL;// 设备对应的终端类型为空
			log.info("Send failed, because : device type is null,"
					+ "Device is " + od.getDeviceId());
			// return result;
		} else {

			try {
				result = send(req, od);
				if (result == 1) {// 不在本机，进行转发， 避免失败互相转发情况
					result = reloadForward(req, result);
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = StructionResult.SEND_FALIED;
				log.info("Send failed, because:" + e.getMessage());
				log.error("发送指令异常", e);
			}
		}

		if (result == 0) {
			log.info("Send success, Device is " + od.getDeviceId());
		} else {

			log.info("Send failed, because : " + RequestHandler.getDesc(result)
					+ ", device is " + od.getDeviceId());
			result = synchWatingDeal(req, od, result);
		}
		req.setSendDate(new java.sql.Date(System.currentTimeMillis()));
		req.increseReSendCount();

		try {
			String isSaveIns = AllConfigCache.getInstance().getConfigMap().get(
					"isSaveIns");
			// 默认保存
			if (isSaveIns == null || isSaveIns.equals("1")) {

				if (!req.isLoad()) {// 对于负载转发的请求不更新数据，由转发的机器完成数据库操作
					DBService dbservice = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
					dbservice.updateInstructions(req.getSequence(), order, req
							.getCmdType(), null, req.getSendDate(), req
							.getReSendCount()
							+ "", String.valueOf(result));
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("更新指令异常：" + e.getMessage());
		}
		return result;
	}

	public static int send(Request req, Order od) throws Exception {
		int result = StructionResult.SEND_FALIED;

		TerminalTypeBean typeBean = (TerminalTypeBean) TerminalTypeList
				.getInstance().get(od.getDeviceType());
		String defaultServiceImpl = "com.mapabc.gater.lbsgateway.service.CommonGatewayServiceImpl";
		String sendServiceImpl = null;
		ICommonGatewayService gate = null;

		try {
			sendServiceImpl = AllConfigCache.getInstance()
					.getNonMtCmdServiceMap().get("sendService");

			if (sendServiceImpl == null || sendServiceImpl.length() <= 0) {
				sendServiceImpl = defaultServiceImpl;
			}

		} catch (Exception e) {
			// e.printStackTrace();
			log
					.error(
							"non-mtcmd-service中下发接口实现类未找到或无non-mtcmd-service配置文件，使用默认接口发送",
							e);

			sendServiceImpl = defaultServiceImpl;
			log.error("下发指令默认接口：" + sendServiceImpl);
		}
		gate = (ICommonGatewayService) ParseConfigParamUtil
				.getClassInstance(sendServiceImpl);

		if (typeBean != null) {

			String startHex = typeBean.getStart();
			String endHex = typeBean.getEnd();
			String hexCommond = od.getContent();

			String[] starts = startHex.split(",");
			for (int i = 0; i < starts.length; i++) {

				if (hexCommond.startsWith(starts[i])) {// 验证指令是否有效
					break;
				}
				if (i == starts.length - 1) {
					log.info("Send failed, because : commond is invalid,"
							+ "Device is " + od.getDeviceId() + ", cmd="
							+ od.getContent());
					return StructionResult.CMD_INVALID;
				}
			}

			// 发送方式
			String[] sendtypes = typeBean.getMtType().trim().split(",".trim());
			if (sendtypes == null || sendtypes.length <= 0) {
				log
						.info("Send failed, because terminallist.xml mtType field config is null.");
				return StructionResult.LINK_CHANNEL_TYPE_NULL;
			}

			for (int k = 0; k < sendtypes.length; k++) {

				String sendType = sendtypes[k].trim();
				if (null != sendType && sendType.length() > 0) {

					// 下发编码全部以16进制返回,与16进制指令保持兼容
					byte[] contBytes = Tools.fromHexString(od.getContent());

					// UDP下发特殊处理
					if (od.getContent().startsWith("292962")) {
						result = gate.sendDataToUdpTerminal(od.getDeviceId(),
								contBytes, od.getCmdId());
						if (result != 0)
							result = StructionResult.NO_RESP0NSE;
						return result;
					} else if (sendType.equals("0")) {// TCP发送
						result = gate.sendDataToTcpTerminal(od.getDeviceId(),
								contBytes, od.getCmdId());

					} else if (sendType.equals("1")) {// UDP发送
						result = gate.sendDataToUdpTerminal(od.getDeviceId(),
								contBytes, od.getCmdId());

					} else if (sendType.equals("2")) {// HTTP POST发送
						String postUrl = typeBean.getMtUrl();
						if (postUrl == null || postUrl.trim().length() <= 0) {
							log.error("post url is null!", null);
							return StructionResult.LINK_CHANNEL_ADDRESS_NULL;
						}
						byte[] resValue = gate.sendByHttpPost(od.getDeviceId(),
								contBytes, postUrl);
						if (null == resValue || resValue.length <= 0) {
							log.info(
											"http post Send failed, because : terminal no return");
							return StructionResult.NO_RESP0NSE;
						} else {
							result = 0;

							try {
								String className = AllConfigCache.getInstance()
										.getNonMtCmdServiceMap().get(
												"responseParseService");
								if (className != null) {
									ResponseParseService ps = (ResponseParseService) ParseConfigParamUtil
											.getClassInstance(className);
									result = ps.parseResponse(resValue);
								}
							} catch (Exception e) {
							}

							log.info("POST下发内容回应结果：" + new String(resValue));
						}

					} else if (sendType.equals("3")) {
						// 转发到指定第三方TCP客户端
						String postUrl = typeBean.getMtUrl();
						if (postUrl == null || postUrl.trim().length() <= 0) {
							log.error("post url is null!", null);
							return StructionResult.LINK_CHANNEL_ADDRESS_NULL;
						}
						try {
							String ip = postUrl.substring(0, postUrl
									.indexOf(":"));
							String port = postUrl.substring(
									postUrl.indexOf(":") + 1).trim();
							int iport = 0;

							iport = Integer.parseInt(port);
							result = gate.fwdOtherTcp(od.getDeviceId(), ip,
									iport, contBytes);
						} catch (Exception e) {
							log.error("获取下行转发地址异常", e);
							return StructionResult.LINK_CHANNEL_ADDRESS_NULL;
						}

						// result = gate.sendDoogNaviTask(od.getDeviceId(),
						// contBytes, null);
					} else if (sendType.equals("4")) {// http get 方式
						String postUrl = typeBean.getMtUrl();
						if (postUrl == null || postUrl.trim().length() <= 0) {
							log.error("post url is null!", null);
							return StructionResult.LINK_CHANNEL_ADDRESS_NULL;
						}
						// 编码类组合参数url
						String params = new String(contBytes);
						postUrl = postUrl + "?" + params;
						log.info("http get url:" + postUrl);
						result = gate.sendByHttpGet(postUrl);

						log.info("http get下发内容回应结果：" + result);

					}

				} else {
					result = StructionResult.LINK_CHANNEL_TYPE_NULL;
					log
							.info("commod send type config in terminallist.xml is null.");
				}
			}
		} else {
			result = StructionResult.DEVICE_TYPE_NULL;
			log.info("Send failed: because terminallist.xml "
					+ od.getDeviceType() + " config is null!");
		}
		return result;
	}

	// 同步等待处理
	private static int synchWatingDeal(Request req, Order od, int result) {
		int res = result;

		if (!req.isSynch() || (flag != null && flag.equals("1"))) {// 是否异步缓存
			if (result != 0) {
				log.info("gater 请求端开始控制异步缓存.");
				handle.addOrder(req);
			}
		} else {
			log.info("gater 请求端开始同步等待终端回应.");
			res = getTerReply(od.getDeviceId(), od.getCmdId());
			log.info("gater 请求端开始同步等待结果：" + result);

		}

		return res;
	}

	private static int getTerReply(String deviceId, String cmdId) {

		long max = TimeUtil.getDateAtferSeconds(
				new Date(System.currentTimeMillis()), timeOut).getTime();

		String replyResult = "";
		while (true) {

			replyResult = ReplyResponseUtil.terReplyHs.get(deviceId + ":"
					+ cmdId);
			if (null != replyResult && !replyResult.equals("")) {
				break;
			}

			if (System.currentTimeMillis() > max) {
				break;
			}

			// try {
			// Thread.sleep(1 * 1000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }
		}

		ReplyResponseUtil.terReplyHs.remove((new StringBuilder(String
				.valueOf(deviceId))).append(":").append(cmdId).toString());

		if (null == replyResult || replyResult.trim().length() <= 0) {
			return StructionResult.NO_RESP0NSE;
		}

		if (replyResult.equals("1")) {
			return StructionResult.SEND_SUCCESS;
		} else if (replyResult.equals("0")) {
			return StructionResult.TERM_SETTING_FAILED;
		} else {
			return StructionResult.SEND_FALIED;
		}
	}

	public static void addElementsByHs(Map<String, Object> hs, Element ele) {
		if (null == hs) {
			return;
		}
		Iterator<String> it = hs.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = (String) hs.get(key);
			if (null != value) {
				ele.addElement(key).setText(value);
			}
		}
	}

	private static int forwardReq(Request req, String type) {
		DBService db = null;
		try {
			db = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
		} catch (Exception e) {
			log.error("get DBService bean error", e);
		}
		String deviceId = req.getDeviceId();

		log.info("localMtUrl:" + localMtUrl);

		if (null == deviceId) {
			log.info("负载转发DEVICEID IS NULL.");
			return StructionResult.DEVICEID_NULL;
		}
		if (null == type || type.trim().length() <= 0) {
			log.info("terminallist.xml mtype is null.");
			return StructionResult.LINK_CHANNEL_TYPE_NULL;
		}

		TermOnlieStatus term = db.getTermOnlineStatus(deviceId, type); // 获取库中终端链路状态

		if (null == term || null == term.getStatus()
				|| term.getStatus().equals("0")) {
			log.info(deviceId + " is not online.");
			return StructionResult.NOT_ONLINE;
		}
		String curip = term.getCurIp();
		if (null == curip || curip.equals("")) {
			return StructionResult.NOT_ONLINE;
		}
		if (localMtUrl != null && !localMtUrl.equals(curip)
				&& term.getStatus().equals("1")) {// 不在本机,转到负载

			Document reqDoc = DocumentHelper.createDocument();
			Element root = reqDoc.addElement("request");
			root.addAttribute("userId", req.getUserId());
			root.addAttribute("isLoad", "1");// 标识为负载转发
			Element serviceEle = root.addElement("service");
			serviceEle.addAttribute("name", req.getServiceName());
			serviceEle.addAttribute("deviceId", req.getDeviceId());

			addElementsByHs(req.getDatas(), serviceEle);

			Document respDoc = null;
			try {
				log.info("forward to " + curip + "/"
						+ AppHelper.getWebAppName() + "/service");
				log.info("req : " + reqDoc.asXML());
				respDoc = HttpUtil.getXmlRespByPostXml(curip + "/"
						+ AppHelper.getWebAppName() + "/service", reqDoc,
						"utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (DocumentException e) {
				e.printStackTrace();
			}

			if (respDoc == null) {
				return StructionResult.LOAD_FORWARD_FAILED;
			} else {
				String code = respDoc.getRootElement().element("device")
						.elementTextTrim("code");
				return Integer.parseInt(code);
			}
		} else {
			return StructionResult.NOT_ONLINE;
		}
	}

	public static void startOrderListen() {
		handle.start();
		log.info("Order listenner start");
		// loadWillSendCmd();
	}

	// public static void addResult(String key, String errorDesc){
	// resultMap.put(key, errorDesc)
	// }

	// 加载待发指令
	public static void loadWillSendCmd() {
		DBService service = null;
		try {
			service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
		} catch (Exception e) {
			log.error("get DBService bean error", e);
		}
		List<Request> cmdList = new ArrayList<Request>();

		List<TStructions> list = service.loadWillSendInstruction(null, "6");
		if (list != null) {
			log.info("Server start,load willing send cmd count:" + list.size());

			for (int i = 0; i < list.size(); i++) {
				String reqXml = list.get(i).getReq();
				org.dom4j.Document doc = null;
				try {
					doc = org.dom4j.DocumentHelper.parseText(reqXml);
				} catch (Exception e) {
					// TODO 自动生成 catch 块
					e.printStackTrace();
				}
				try {
					doPost(doc);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// List<Request> reqs = RequestHandler.parse(doc);
				// cmdList.addAll(reqs);

			}
		}

	}

	// 非下行接口解析
	private static Document parseCommonReqService(Element root)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Document resDoc = null;
		// if (root != null) {
		//
		// Element service = root.element("service");
		// String serviceName = service.attributeValue("name");
		//
		// if (serviceName == null || serviceName.trim().length() <= 0) {
		// log.info("common serviceName is null");
		// return null;
		// }
		// RequestService reqService = new RequestServiceImpl();
		// Method method = reqService.getClass().getMethod(serviceName,
		// Element.class);
		// resDoc = (Document) method.invoke(reqService, service);
		//
		// }
		return resDoc;
	}

	public static void main(String[] args) {
		try {
			System.out
					.println(new String(
							Tools
									.fromHexString("5812455081490217431407103611719500117082539e001357fffffbffff0001")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String content =
		// "(200808082000001000,S08,1@1,0=13900000000!1=13900000001!2=13900000002!3=13900000003!4=13900000004!5=13900000005!)";

	}
}
