package com.mapabc.gater.lbsgateway.service.licence;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

//import com.caucho.hessian.client.HessianProxyFactory;
//import com.mapabc.boss.security.license.MACAddress;
import com.mapabc.gater.directl.Config;
//import com.mapabc.ubas.license.VerifyBean;
//import com.mapabc.ubas.service.IUbasService;
//import com.mapabc.wzt.util1.DateUtil;

/**
 * @author dingfeng.liu
 * @date：Jul 18, 2011 类说明
 */
public class LicenceValidator {
    public static final int VALIDATE_TIME = 12 * 60 * 60 * 1000;// 定时器间隔时间
    private boolean valid = true;
//    private VerifyBean bean = null;
    private String errorInfo;
    private static LicenceValidator licenceValidator = null;

    private LicenceValidator() {
    }

    public synchronized static LicenceValidator getInstance() {
	if (null == licenceValidator) {
	    licenceValidator = new LicenceValidator();
	}
	return licenceValidator;
    }
    public String getErrorInfo(){
	return errorInfo;
    }
    public boolean isValid(){
	return valid;
    }
    public boolean validateGater(){
	return validate("1");
    }
    public boolean validateLBS(){
	return validate("2");
    }
    private boolean validate(String vPid) {
    	if(true)
    		return true;
//	IUbasService ubasService=getUbasServiceFromRemoteServer();
	String s = null;
//	String errorInfo=null;
	try {
//		  s = ubasService.getLicenseInfo();
	    Document doc = DocumentHelper.parseText(s);
	    String expiry = doc.getRootElement().attributeValue("expiry");
	    List<Element> list = doc.getRootElement().elements();
	    for (Element e : list) {
		String id = e.attributeValue("id");
		String name = e.attributeValue("name");
		List<Element> ps = e.elements();
		for (Element p : ps) {
		    String pid = p.attributeValue("id");
		    String pname = p.attributeValue("name");
		    String authorize_status = p
			    .attributeValue("authorize_status");

		    if (pid != null && pid.equals(vPid)) {
			if (authorize_status != null
				&& authorize_status.equals("0")) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				try {
				    java.util.Date date =dateFormat.parse(expiry);
				    if(date.compareTo(new Date())>0){
					try {
//					    VerifyBean verifyBean= ubasService.verifyLicense(MACAddress.getMACAddress());
//					    if(verifyBean.getSuccessful()){
						valid = true;
						return true;
//					    }else{
//						errorInfo="ErrorCode="+verifyBean.getErrorCode()+"ErrorCause="+verifyBean.getErrorCause();
//					    }
//					} catch (IOException e1) {
					} catch (Exception e1) {
					    errorInfo="License 服务地址拒绝连接";
//					    e1.printStackTrace();
					}
					    
				    }else{
					    errorInfo="License 已过期";
				    }
				} catch (ParseException e1) {
				    errorInfo="License 日期格式错误'"+expiry+"'";
//				    e1.printStackTrace();
				}
			}else{
			    errorInfo="License状态异常.";
			}
		    }
		}
	    }

	} catch (Exception e) {
	    errorInfo="License信息格式错误."+s;
	}
	outErrorInfo(errorInfo);
	valid = true;
	return valid;
    }


    public void outErrorInfo(String errorDesc) {
//	try {
////	    Thread.sleep(30 * 1000);

	    System.out.println("验证不通过'" + errorDesc + "'");
//	} catch (InterruptedException e) {
//	    // TODO Auto-generated catch block
//	    e.printStackTrace();
//	}
	// log
    }

//    private IUbasService getUbasServiceFromRemoteServer() {
//
//	String url = Config.getInstance().getString("licenseService");
//
//	HessianProxyFactory factory = new HessianProxyFactory();
//	factory.setChunkedPost(false);
//	IUbasService service = null;
//	try {
//	    service = (IUbasService) factory.create(IUbasService.class, url);
//	} catch (MalformedURLException e) {
//	    // TODO Auto-generated catch block
//		System.out.println("license服务拒绝连接,检查config.properties licenseService参数。");
//	   // e.printStackTrace();
//	}
//	return service;
//    }

    public static void main(String[] args) {
//	String s = LicenceValidator.getInstance()
//		.getUbasServiceFromRemoteServer().getLicenseInfo();
//	System.out.println(s);

//	try {
//	    Document doc = DocumentHelper.parseText(s);
//	    String expiry = doc.getRootElement().attributeValue("expiry");
//	    java.lang.System.out.println(expiry);
//	    List<Element> list = doc.getRootElement().elements();
//	    for (Element e : list) {
//		String id = e.attributeValue("id");
//		String name = e.attributeValue("name");
//		List<Element> ps = e.elements();
//		for (Element p : ps) {
//		    String pid = p.attributeValue("id");
//		    String pname = p.attributeValue("name");
//		    String authorize_status = p
//			    .attributeValue("authorize_status");
//		}
//	    }
//
//	} catch (DocumentException e) {
//
//	    e.printStackTrace();
//	}
    }
}
