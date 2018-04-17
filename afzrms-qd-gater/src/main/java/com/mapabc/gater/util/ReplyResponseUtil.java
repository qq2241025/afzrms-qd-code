package com.mapabc.gater.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.LogFactory;

/**
 * 终端应答消息处理
 * @author 
 *
 */
public class ReplyResponseUtil {
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ReplyResponseUtil.class);
	
	public static Map<String, String> terReplyHs = new HashMap<String, String>();
	
	/**
	 * @param key 格式：设备ID:命令ID
	 * @param value 0：成功 1：失败
	 */
	public static void addReply(String key, String value) {
		log.info("addReply:key=" + key + ",value=" + value);
		terReplyHs.put(key, value);
	}

}
