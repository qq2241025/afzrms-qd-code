/**
 * 
 */
package com.mapabc.gater.directl.encode.longhan;

import com.mapabc.gater.directl.encode.QueryAdaptor;
import com.mapabc.gater.directl.encode.Request;
 

/**
 * @author shiguang.zhou
 * 
 */
public class LongHanQuery extends QueryAdaptor {

	// 查看车辆工作状态
	public String workStatus(Request req) {
 
		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "30", 6, "");
		return ret;
	}

	public String queryVersion(Request req) { 
		String ret = "";
		ret = LongHanUtil.makeProtocal(req.getDeviceId(), "3d", 6, "");
		return ret;
	}

}
