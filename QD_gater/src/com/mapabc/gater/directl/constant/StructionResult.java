/**
 * 
 */
package com.mapabc.gater.directl.constant;

/**
 * 指令执行结果状态码定义
 * @author 
 *
 */
public class StructionResult {
 
	/**
	 * 发送成功
	 */
	public static final int SEND_SUCCESS=0;
 	/**
 	 * 不在线
 	 */
	public static final int NOT_ONLINE=1;
 	/**
 	 * 指令无效
 	 */
	public static final int CMD_INVALID=2;
 	/**
 	 * 无应答
 	 */
	public static final int NO_RESP0NSE=3;
 	/**
 	 * 发送失败
 	 */
	public static final int SEND_FALIED=4;
 	/**
 	 * 无适配的指令
 	 */
	public static final int CMD_NULL=5;
 	/**
 	 * 设备ID为空
 	 */
	public static final int DEVICEID_NULL =6 ; 
 	/**
 	 * 无对应的终端类型
 	 */
	public static final int DEVICE_TYPE_NULL = 7;
 	/**
 	 * 指令下发链路通道类型为空
 	 */
	public static final int LINK_CHANNEL_TYPE_NULL = 8;
 	/**
 	 * 终端设置失败
 	 */
	public static final int TERM_SETTING_FAILED = 9;
 	/**
 	 * 负载转发失败
 	 */
	public static final int LOAD_FORWARD_FAILED = 10;
	/**
	 * serviceName空
	 */
	public static final int REQ_SERVICE_NAME_NULL = 11;
	/**
	 * 配置文件编码类不存在
	 */
	public static final int ENCODE_CLASS_NULL = 12;
	/**
	 * 接口不存在
	 */
	public static final int NO_INTERFACE = 13;
	/**
	 * 协议编码,参数错误
	 */
	public static final int CRT_PROTOCAL_ERROR = 14;
	/**
	 * 缓存无此终端
	 */
	public static final int NO_DEVICE_IN_CACHE = 15;
	/**
	 * XML格式错误
	 */
	public static final int XML_FMT_ERROR = 16;
	/**
	 * 接口类不存在
	 */
	public static final int NO_ADAPT_ENCODE_CLASS = 17;
	/**
	 * 指令转发链路地址为空
	 */
	public static final int LINK_CHANNEL_ADDRESS_NULL = 18;
	/**
	 * licence过期
	 */
	public static final int LICENCE_ERROR = 19;
	

}
