/**
 * 
 */
package com.mapabc.gater.protocal.post;

import java.util.HashMap;

import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.lbsgateway.GBLTerminalList;

/**
 * @author 
 * 
 */
public class ProtocalServiceImpl implements ProtocalService {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(ProtocalServiceImpl.class);

	public byte[] makeProtocal(String deviceId, String termType, byte[] cont)
			throws Exception {
		byte[] postCont = null;

		if (termType.equals("GP-ZGDX-GPRS")) {
			String cmd = new String(cont);
			String[] fields = cmd.split(":");
			
			if (cmd.startsWith("//BREW:DISP")) {
				cmd = cmd.substring(11);
				postCont = this.makePostSmsXml(deviceId, cmd.getBytes(), "15").getBytes();
			}else if(fields[2].startsWith("23")){//暂时屏蔽下发行程
				postCont = this.makePostSmsXml(deviceId, "".getBytes(), "0").getBytes();
			} else {
				postCont = this.makePostSmsXml(deviceId, cont, "0").getBytes();
			}
		} else {
			//postCont = cont;
			postCont = this.makeCommSmsPtl(deviceId, cont);
		}
		return postCont;
	}

	public int parsePostResult(String termType, byte[] resCont)
			throws Exception {
		int result = -1;
		if (termType.equals("GP-ZGDX-GPRS")) {
			result = this.getPostResult(resCont);
		} else {

		}
		return result;
	}

	// 返回结果
	private int getPostResult(byte[] resValue) {
		int res = -1;

		Document doc = null;
		String resXml = new String(resValue);
		try {
			doc = org.dom4j.DocumentHelper.parseText(resXml);
			org.dom4j.Element root = doc.getRootElement();
			Element elem = root.element("BODY");
			String resCode = elem.elementTextTrim("ErrorCode");
			if (resCode != null && resCode.equals("0")) {// 成功
				res = 0;
			} else {// 失败
				res = -1;
			}
		} catch (Exception e) {
			// TODO 自动生成 catch 块
			e.printStackTrace();
			res = -1;
			log.error("解析post返回结果异常", e);
		}

		return res;
	}
	
	

	public String makePostSmsXml(String deviceId, byte[] bcont, String smsCode)
			throws Exception {
		Document doc = null;
		HashMap<String, String> smsMap = AllConfigCache.getInstance().getSmsMap();
		
		String ismgtype = smsMap.get ("IsmgType");
		String smsType = smsMap.get ("SmsType");
		String serviceId = smsMap.get ("ServiceId");
		String serviceSource = smsMap.get ("ServiceSource");
		String serviceSor = null;
		String phoneNum = null;
		String cont = null;

		try {
 			 
			phoneNum = GBLTerminalList.getInstance().getSimcardNum(deviceId);
			if (phoneNum == null) {
				log.info(deviceId + "对应的手机号码为空，不能发送短信！");
				return null;
			}

			cont = new String(bcont);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String userId = smsMap.get("userId");
		String eid = smsMap.get("entId");
		String uname = smsMap.get("userName");
		String upwd = smsMap.get("password");

		doc = DocumentHelper.createDocument();
		Element root = doc.addElement("Request");
		root.addAttribute("service", "SmsServerService");
		root.addAttribute("method", "sendSms");

		Element head = root.addElement("HEAD");
		head.addElement("EID").setText(eid);
		head.addElement("UNAME").setText(uname);
		head.addElement("PWD").setText(upwd);
		head.addElement("VER").setText("1.0");

		Element end = root.addElement("BODY");
		end.addElement("UserId").setText(userId);
		end.addElement("IsmgType").setText(ismgtype);
		end.addElement("SmsType").setText(smsType);
		end.addElement("ServiceId").setText(serviceId);
		end.addElement("PhoneNum")
				.setText(phoneNum == null ? "NULL" : phoneNum);
		end.addElement("SmsContent").setText(cont == null ? "NULL" : cont);
		end.addElement("ServiceSource").setText(serviceSor);
		end.addElement("SmsCode").setText(smsCode);
		doc.setXMLEncoding("GBK");

		return doc.asXML();
	}

	
//	<?xml version="1.0" encoding="GBK"?>
//	<Request service="SmsService" method="sendSms">
//	    <IsmgType>cmpp</IsmgType>   <!-- 短信网关，必须 -->
//	    <PhoneNums>1388888999</PhoneNums>   <!-- 接收短信的手机号码，必须 -->
//	    <ServiceID>10086</ServiceID>   <!-- 服务号码（发送短信的号码），必须 -->
//	    <ServiceSource>位置通产品</ServiceSource>   <!-- 服务源名称，必须 -->
//	    <SmsContent>短信内容asd</SmsContent>    <!-- 短信内容，必须 -->
//	</Request>

	public byte[] makeCommSmsPtl(String deviceId, byte[] sms) throws Exception {
		 
		Document doc = null;
		HashMap<String, String> smsMap = AllConfigCache.getInstance().getSmsMap();
		String ismgtype = smsMap.get ("IsmgType");
		String smsType = smsMap.get ("SmsType");
		String serviceId = smsMap.get ("ServiceId");
		String serviceSource = smsMap.get ("ServiceSource");
		String serviceSor = null;
		String phoneNum = null;
		String cont = null;

		try {
 			phoneNum = GBLTerminalList.getInstance().getSimcardNum(deviceId);
			if (phoneNum == null) {
				log.info(deviceId + "对应的手机号码为空，不能发送短信！");
				return null;
			}

			cont = new String(sms);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		doc = DocumentHelper.createDocument();
		Element root = doc.addElement("Request");
		root.addAttribute("service", "SmsService");
		root.addAttribute("method", "sendSms");

		Element IsmgType = root.addElement("IsmgType");
		Element PhoneNums = root.addElement("PhoneNums");
		Element ServiceID = root.addElement("ServiceID");
		Element ServiceSource = root.addElement("ServiceSource");
		Element SmsContent = root.addElement("SmsContent");
		
		IsmgType.setText(ismgtype);
		PhoneNums.setText(phoneNum);
		ServiceID.setText(serviceId);
		ServiceSource.setText(serviceSource);
		SmsContent.setText(cont);
		 
		doc.setXMLEncoding("GBK");

		return doc.asXML().getBytes();
 	}
	
//<?xml version="1.0" encoding="GBK"?>
//	<response>
//	 <sms_id>10101</sms_id>
//	 <code>0</code>
//	 <desc></desc>
//	</response>
public int parseCommonResponse(byte[] response){
	int res = -1;

	Document doc = null;
	String resXml = new String(response);
	try {
		doc = org.dom4j.DocumentHelper.parseText(resXml);
		org.dom4j.Element root = doc.getRootElement();
		 
		String resCode = root.elementTextTrim("code");
		if (resCode != null && resCode.equals("0")) {// 成功
			res = 0;
		} else {// 失败
			res = -1;
		}
	} catch (Exception e) {
		// TODO 自动生成 catch 块
		e.printStackTrace();
		res = -1;
		log.error("解析post返回结果异常", e);
	}

	return res;
	}

public static void main(String[] args){
	ProtocalServiceImpl imp = new ProtocalServiceImpl();
	String s = "<?xml version=\"1.0\" encoding=\"GBK\"?><response><sms_id>10101</sms_id><code>0</code><desc></desc></response>";
	imp.parseCommonResponse(s.getBytes());
}
}

