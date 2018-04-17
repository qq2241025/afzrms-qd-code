package com.mapabc.gater.lbsgateway;

import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.mapabc.gater.common.Const;
import com.mapabc.gater.lbsgateway.bean.ConnectionInfo;
import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;

/**
 * GPRS数据预解析类 把该GPRS数据与配置文件比较，判断出为哪款终端类型
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author
 * @version 1.0
 */
public class PrepareParse {
	// public static final int
	private static org.apache.commons.logging.Log log = LogFactory.getLog(PrepareParse.class);

	private static final String ConfigFilename = "terminallist.xml";
	private static PrepareParse instance;
	private static Document document;

	/**
	 * @return the document
	 */
	public static Document getDocument() {
		return document;
	}

	/**
	 * @param document
	 *            the document to set
	 */
	public static void setDocument(Document document) {
		PrepareParse.document = document;
	}

	PrepareParse() {
		this.loadXml();
	}

	public static synchronized PrepareParse getInstance() {
		if (instance == null) {
			instance = new PrepareParse();
		}

		return instance;
	}

	private void loadXml() {
		SAXReader sb = new SAXReader();
		InputStream is = getClass().getResourceAsStream("/" + ConfigFilename);
		try {
			document = sb.read(is);
		} catch (DocumentException ex) {
			log.info("严重错误!网关读取:" + ConfigFilename + "失败!请重新启动网关.");
			log.error("严重错误!网关读取:" + ConfigFilename + "失败!请重新启动网关.", ex);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception ex) {

				}
			}
		}
	}

	/**
	 * 解析GPS数据，得到GPS类型#终端数据包头标识符#终端数据包尾标识符
	 * 
	 * @param hexDictate
	 *            String
	 * @return String
	 */
	public String getTerminalCode(String hexDictate) {
		String dictate = Const.PROTOCAL_TYPE_DEFAULT + "#null#null";
		String split = "#";
		String none = "null";
		String tmp = "";

		if (hexDictate == null)
			return dictate;
		Element root = document.getRootElement();
		java.util.List<?> conList = root.elements("connection");
		for (int k = 0; k < conList.size(); k++) {
			Element et = (Element) conList.get(k);
			java.util.List<?> children = et.elements("Terminal");
			for (int i = 0; i < children.size(); i++) {
				Element e = (Element) children.get(i);
				String hexStart = e.element("Hex-Start").getText().trim();
				String hexEnd = e.element("Hex-End").getText().trim();
				if (hexStart != null && hexEnd != null && hexEnd.length() > 0 && hexStart.length() > 0) {// 包头和包尾都要进行比较
					String[] startArray = hexStart.split(",");
					for (int j = 0; j < startArray.length; j++) {
						if (hexDictate.startsWith(startArray[j]) && hexDictate.endsWith(hexEnd)) {
							dictate = e.attributeValue("id").trim();
							dictate = dictate + split + hexStart + split + hexEnd;
							break;
						}
					}
				} else if (hexStart != null && hexStart.length() > 0) {// 比较包头
					String[] startArray = hexStart.split(",");
					for (int j = 0; j < startArray.length; j++) {
						if (hexDictate.startsWith(startArray[j])) {
							if (startArray[j].length() > tmp.length()) {
								tmp = startArray[j];
								dictate = e.attributeValue("id").trim();// e.getChild("Terminal-name").getText().trim();
								dictate += split + hexStart + split + none;
							}
						}
					}
				} else if (hexEnd != null && hexEnd.length() > 0) {// 比较包尾
					if (hexDictate.endsWith(hexEnd)) {
						dictate = e.attributeValue("id").trim();// e.getChild("Terminal-name").getText().trim();
						return dictate + split + none + split + hexEnd;
					}
				}
			}
		}
		return dictate;
	}

	// 加载终端类型配置信息
	public ConnectionInfo[] loadTermConfig() {
		ConnectionInfo[] infos = null;

		Element root = document.getRootElement();
		List<?> children = root.elements("connection");
		infos = new ConnectionInfo[children.size()];

		for (int i = 0; i < children.size(); i++) {
			ConnectionInfo conInfo = new ConnectionInfo();

			Element e = (Element) children.get(i);
			String type = e.attributeValue("type");
			String port = e.attributeValue("port");
			conInfo.setType(type);
			conInfo.setPort(port);

			Element sendNum = e.element("parsePoolNum");
			if (null != sendNum) {
				conInfo.setParsePoolNum(Integer.parseInt(sendNum.getText()));
			}

			Element recdNum = e.element("dbPoolNum");
			if (null != sendNum) {
				conInfo.setDbPoolNum(Integer.parseInt(recdNum.getText()));
			}

			Element readNum = e.element("readDataPoolNum");
			if (null != readNum) {
				conInfo.setReadDataPoolNum(Integer.parseInt(readNum.getText()));
			}

			List<?> termTypeList = e.elements("Terminal");
			for (int j = 0; j < termTypeList.size(); j++) {// 端口下终端类型适配信息
				Element elt = (Element) termTypeList.get(j);

				String id = elt.attributeValue("id").trim();
				String typeName = elt.elementText("Terminal-name").trim();
				String encodeClass = elt.elementText("Encode-class").trim();
				String parseClass = elt.elementText("Parse-class").trim();
				String hexStart = elt.elementText("Hex-Start").trim();
				String hexEnd = elt.elementText("Hex-End").trim();

				boolean isSaveDb = Boolean.valueOf(elt.elementText("IsSaveDb").trim());
				boolean isSendJms = Boolean.valueOf(elt.elementText("IsSendJms").trim());
				boolean isEncrypt = Boolean.valueOf(elt.elementText("IsEncrypt").trim());
				boolean isDeflection = Boolean.valueOf(elt.elementText("IsDeflection").trim());
				boolean isEncCvt = Boolean.valueOf(elt.elementText("IsConvertEncrypt").trim());

				boolean isRouteCorrect = Boolean.valueOf(elt.elementText("IsRouteCorrect").trim());
				boolean isLocateDesc = Boolean.valueOf(elt.elementText("IsLocationDesc").trim());

				String lbmsInterfaceImpl = elt.elementText("LbmpImpl-Class").trim();
				String jmsInterfaceImpl = elt.elementText("JmsImpl-Class").trim();
				String coordType = elt.elementTextTrim("coordType");
				String mtType = elt.elementTextTrim("mtType");
				String mtUrl = elt.elementTextTrim("mtUrl");
				String topicName = elt.elementTextTrim("JmsTopicName");
				String isBatchToJms = elt.elementTextTrim("IsBatchToJms");

				TerminalTypeBean bean = new TerminalTypeBean();
				bean.setTerm_type_name(typeName);
				bean.setId(id);
				// bean.setTerm_class(termClass);
				bean.setParseClass(parseClass);
				bean.setEncodeClass(encodeClass);
				bean.setStart(hexStart);
				bean.setEnd(hexEnd);
				bean.setSaveDb(isSaveDb);
				bean.setEncrypt(isEncrypt);
				bean.setDeflection(isDeflection);
				bean.setEncryptCvt(isEncCvt);
				bean.setSendJms(isSendJms);
				bean.setRouteCorret(isRouteCorrect);
				bean.setLocateDesc(isLocateDesc);
				bean.setLbmsInterfaceImpl(lbmsInterfaceImpl);
				bean.setJmsInterfaceImpl(jmsInterfaceImpl);
				bean.setCoordType(coordType);
				bean.setMtType(mtType);
				bean.setMtUrl(mtUrl);
				bean.setTopicName(topicName);
				bean.setIsBatchToJms(isBatchToJms);
				TerminalTypeList.getInstance().add(id, bean);
				conInfo.getTermTypeList().add(bean);
			}
			infos[i] = conInfo;
		}
		return infos;
	}

	public static void main(String[] args) {
		String dop = "7e47051604030a33b200000000000007d900205500100708151221116177900395882500000066000000300320010000000000931f7f";
		String pname = PrepareParse.getInstance().getTerminalCode(dop);
		System.out.println(pname);
	}
}
