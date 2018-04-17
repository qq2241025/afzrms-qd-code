package com.mapabc.gater.util;

/**
 * http响应
 */

public class HttpResponseEntity {
	private int response_code;// 返回码(200为正常)
	private String response_message;// 返回描述(OK为正常)
	private byte[] response_content;// 返回内容
	private String content_type;// 返回类型信息(Content-Type: text/xml)

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public HttpResponseEntity() {

	}

	public int getResponse_code() {
		return response_code;
	}

	public void setResponse_code(int response_code) {
		this.response_code = response_code;
	}

	public String getResponse_message() {
		return response_message;
	}

	public void setResponse_message(String response_message) {
		this.response_message = response_message;
	}

	public byte[] getResponse_content() {
		return response_content;
	}

	public void setResponse_content(byte[] response_content) {
		this.response_content = response_content;
	}

	@Override
	public String toString() {
		String rn = "\r\n";
		StringBuffer re = new StringBuffer();
		re.append("code: ");
		re.append(response_code);
		re.append(rn);
		re.append("messgae: ");
		re.append(response_message == null ? "" : response_message);
		re.append(rn);
		re.append("type: ");
		re.append(content_type == null ? "" : content_type);
		re.append(rn);
		re.append("content: ");
		re.append(response_content == null ? "" : new String(response_content));
		return re.toString();
	}
}
