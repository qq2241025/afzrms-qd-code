package com.mapabc.gater.util;

import java.net.*;
import java.util.Hashtable;

import javax.servlet.http.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.*;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2005
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */

public class HttpUtil {

	public static byte[] getURLData(String url) throws Exception {
		URLConnection c = null;
		URL imageURL = null;
		DataInputStream is = null;
		byte[] btemp;
		try {
			imageURL = new URL(url);
			c = imageURL.openConnection();
			is = new DataInputStream(c.getInputStream());
			java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
			byte[] bufferByte = new byte[256];
			int l = -1;
			int downloadSize = 0;
			while ((l = is.read(bufferByte)) > -1) {
				downloadSize += l;
				out.write(bufferByte, 0, l);
				out.flush();
			}
			btemp = out.toByteArray();
			out.close();
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception ex) {
				// ex.printStackTrace();
			}
		}

		return btemp;
	}

	public static String getURLDecoderStr(String s) {
		return java.net.URLDecoder.decode(s);
	}

	public static String getURLData(String url, String encoding)
			throws Exception {
		byte[] b = getURLData(url);
		String r = new String(b, encoding);
		return r;
	}

	/**
	 * post data to url
	 * 
	 * @param urlStr
	 *            目标地址
	 * @param postData
	 *            发送的数据
	 * @param propertyValue
	 * @return
	 * @throws Exception
	 * @author 
	 */

	public static byte[] getPostURLData(String urlStr, byte[] postData)
			throws Exception {
		byte[] btemp = null;
		URL url = new URL(urlStr);
		URLConnection urlConn = null;
		DataOutputStream printout = null;
		DataInputStream input = null;
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
		try {
			urlConn = url.openConnection();
			urlConn.setDoInput(true);
			urlConn.setDoOutput(true);
			urlConn.setUseCaches(false);
			urlConn.setConnectTimeout(10 * 1000);
			urlConn.setReadTimeout(10 * 1000);

			// urlConn.setRequestProperty("Content-Type",
			// "application/x-www-form-urlencoded");
			// if (propertyValue != null && propertyValue.trim().length() > 0) {
			// urlConn.setRequestProperty("x-up-calling-line-id",
			// propertyValue);
			// }
			urlConn.setRequestProperty("Content=length", String
					.valueOf(postData.length));

			printout = new DataOutputStream(urlConn.getOutputStream());
			for (int i = 0; i < postData.length; i++) {
				printout.write(postData[i]);
			}
//			printout.write(postData);
			printout.flush();
			printout.close();

			input = new DataInputStream(urlConn.getInputStream());
			byte[] bufferByte = new byte[256];
			int l = -1;
			int downloadSize = 0;

			while ((l = input.read(bufferByte)) > -1) {
				downloadSize += l;
				bout.write(bufferByte, 0, l);
				bout.flush();
			}
			btemp = bout.toByteArray();

			bout.close();
			input.close();
			input = null;
			return btemp;
		} catch (Exception ex) {
		} finally {
		}
		return btemp;
	}

	public static byte[] getPostData(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		java.io.DataInputStream servletIn = null;
		java.io.ByteArrayOutputStream bout = null;
		byte[] inByte = null;
		try {
			servletIn = new java.io.DataInputStream(request.getInputStream());
			bout = new java.io.ByteArrayOutputStream();
			byte[] bufferByte = new byte[256];
			int l = -1;
			while ((l = servletIn.read(bufferByte)) > -1) {
				bout.write(bufferByte, 0, l);
				bout.flush();
			}
			inByte = bout.toByteArray();
		} catch (Exception ex) {
			try {
				servletIn.close();
				bout.close();
			} catch (Exception ex2) {
			}
		}
		if (inByte == null || inByte.length == 0) {
			return null;
		}
		return inByte;
	}

	public static HttpResponseEntity sendGet(String url, int timeout) {
		HttpResponseEntity re = new HttpResponseEntity();

		HttpClient httpClient = new HttpClient();
		httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(
				timeout);

		HttpClientParams params = new HttpClientParams();
		params.setSoTimeout(timeout);

		httpClient.setParams(params);

		GetMethod getMethod = null;
		try {
			getMethod = new GetMethod(url);
			getMethod.setFollowRedirects(false);

			int response_code = httpClient.executeMethod(getMethod);
			String response_message = getMethod.getStatusText();
			byte[] responseContent = null;
			String content_type = null;
			if (response_code == HttpStatus.SC_OK) {
				// content_type = getMethod.getResponseHeader("Content-Type")
				// .toString();
				responseContent = getMethod.getResponseBody();
			}
			re.setContent_type(content_type);
			re.setResponse_code(response_code);
			re.setResponse_content(responseContent);
			re.setResponse_message(response_message);
		} catch (Exception e) {
			re.setContent_type("");
			re.setResponse_code(903);
			re.setResponse_message(e.getMessage());
			re.setResponse_content(null);
			e.printStackTrace();
		} finally {

			if (getMethod != null) {
				try {
					getMethod.releaseConnection();
				} catch (Exception e) {
				}
			}
		}
		return re;
	}

	public static HttpResponseEntity sendPost(String url,
			Hashtable<String, String> postParams, int timeout) {
		HttpResponseEntity re = new HttpResponseEntity();

		HttpClient httpClient = new HttpClient();
		HttpClientParams params = new HttpClientParams();
		params.setContentCharset("UTF-8");
		params.setSoTimeout(timeout);
		httpClient.setParams(params);

		PostMethod postMethod = null;
		try {
			postMethod = new PostMethod(url);

			postMethod.setFollowRedirects(false);

			postMethod.setRequestHeader("Content-Type",
					PostMethod.FORM_URL_ENCODED_CONTENT_TYPE
							+ "; charset=UTF-8");

			for (java.util.Enumeration<String> e = postParams.keys(); e
					.hasMoreElements();) {
				String parameterName = e.nextElement();
				String parameterValue = postParams.get(parameterName);
				postMethod.setParameter(parameterName, parameterValue);
			}

			int response_code = httpClient.executeMethod(postMethod);
			String response_message = postMethod.getStatusText();
			byte[] responseContent = null;
			String content_type = null;
			if (response_code == HttpStatus.SC_OK) {
				content_type = postMethod.getResponseHeader("Content-Type")
						.toString();
				responseContent = postMethod.getResponseBody();
			}
			re.setContent_type(content_type);
			re.setResponse_code(response_code);
			re.setResponse_content(responseContent);
			re.setResponse_message(response_message);
		} catch (Exception e) {
			re.setContent_type("");
			re.setResponse_code(903);
			re.setResponse_message(e.getMessage());
			re.setResponse_content(null);

		} finally {

			if (postMethod != null) {
				try {
					postMethod.releaseConnection();
				} catch (Exception e) {
				}
			}
		}
		return re;
	}

	public static Document getXmlRespByPostXml(String urlStr, Document req,
			String encoding) throws IOException, DocumentException {
		URL url = new URL(urlStr);
		InputStream in = null;
		OutputStream out = null;
		Document respDoc = null;
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		try {
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setConnectTimeout(10 * 1000);
			conn.setReadTimeout(10 * 1000);

			out = conn.getOutputStream();
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding(encoding);
			XMLWriter writer = new XMLWriter(out, format);
			writer.write(req);
			writer.close();
			out.flush();
			out.close();

			in = conn.getInputStream();
			 
			if (in != null && in.available()>0) {
				SAXReader reader = new SAXReader();
				respDoc = reader.read(in);
				in.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (conn != null)
				conn.disconnect();

		}
		return respDoc;
	}

	/**
	 * 通过GET/POST方式转发
	 * 
	 * @param urlStr
	 *            发送的目的地
	 * @param method
	 *            GET或POST
	 * @param content
	 *            发送的内容
	 * @param encoding
	 *            编码
	 * @param timeOut
	 *            超时设置,单位秒
	 * @return
	 */
	public static byte[] forward(String urlStr, String method, byte[] content,
			String encoding, int timeOut) {
		HttpURLConnection conn = null;
		String result = "";
		byte[] btemp = null;

		DataOutputStream printout = null;
		DataInputStream input = null;
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();

		try {
			URL url = new URL(URLDecoder.decode(urlStr, encoding));
			conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod(method.toUpperCase());
			conn.setConnectTimeout(timeOut * 1000);// 超时10秒
			conn.setReadTimeout(timeOut*1000);
			
			// conn.setRequestProperty("Proxy-Connection", "Keep-Alive");
			conn.setRequestProperty("content-type", "text/html");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			if (conn.getRequestMethod() != null
					&& conn.getRequestMethod().equalsIgnoreCase("get")) {

			} else {
//				OutputStreamWriter out = new OutputStreamWriter(conn
//						.getOutputStream(), encoding);
//				out.write(content);
//				
//				out.close();
				printout = new DataOutputStream(conn.getOutputStream());
				for (int i = 0; i < content.length; i++) {
					printout.write(content[i]);
				} 
				printout.flush();
				printout.close();
			}
			input = new DataInputStream(conn.getInputStream());
			byte[] bufferByte = new byte[256];
			int l = -1;
			int downloadSize = 0;

			while ((l = input.read(bufferByte)) > -1) {
				downloadSize += l;
				bout.write(bufferByte, 0, l);
				bout.flush();
			}
			btemp = bout.toByteArray();

			bout.close();
			input.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			conn.disconnect();
		}

		return btemp;
	}
	 
	
	public static Document createDoc(String bIsBCD,String dwAddDest,String msg,String deviceId){
		Document doc = null;
		doc = DocumentHelper.createDocument();
		Element root = doc.addElement("Request");
		root.addAttribute("method", "SendComReq");
		Element bcdEle = root.addElement("bIsBCD");
		bcdEle.setText(bIsBCD==null?"2":bIsBCD);
		Element dwAddDesElem = root.addElement("dwAddDest");
		dwAddDesElem.addText(dwAddDest==null?"":dwAddDest);
		Element cContentElem = root.addElement("cContent");
		cContentElem.setText(msg==null?"":msg);
		root.addElement("dwSendUserId").setText(deviceId);
		
		doc.setXMLEncoding("gb2312");
		
		return doc;
	}

	public static void main(String[] args) {
		
		String xml = HttpUtil.createDoc("2", "23", "23", "we").asXML();
		 
		 try {
			byte[] v = HttpUtil.forward("http://127.0.0.1:4321/", "POST",xml.getBytes(),"gb2312",20);
			System.out.println(new String(v));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 

	}

}
