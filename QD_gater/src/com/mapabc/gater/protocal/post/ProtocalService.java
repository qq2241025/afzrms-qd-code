/**
 * 
 */
package com.mapabc.gater.protocal.post;

/**
 * @author 
 *
 */
public interface ProtocalService {
	
	//根据不同类型生成下发的POST内容
	public abstract byte[] makeProtocal(String deviceId, String termType, byte[] cont) throws Exception;
	
	public abstract String makePostSmsXml(String deviceid, byte[] cont,String smsCode) throws Exception;
	
	//根据不同类型获取POST返回结果
	public int parsePostResult(String termType, byte[] resByte) throws Exception;
	
	//构建通用短信接口协议
	public byte[] makeCommSmsPtl(String deviceId, byte[] sms) throws Exception;
	

}
