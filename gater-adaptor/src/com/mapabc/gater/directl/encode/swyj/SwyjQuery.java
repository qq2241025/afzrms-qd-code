/**
 * 
 */
package com.mapabc.gater.directl.encode.swyj;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.QueryAdaptor;
import com.mapabc.gater.directl.encode.Request;

/**
 * @author shiguang.zhou
 *
 */
public class SwyjQuery extends QueryAdaptor {
	private String head = "*HQ,";
	private String end = "#";
	
	public String workStatus(Request req) {
		// *XX,YYYYYYYYYY,S26,HHMMSS#
		
		String deviceid = req.getDeviceId();
		String ret = head+deviceid+",S26,"+Tools.getCurHMS()+end;
		String hex = Tools.bytesToHexString(ret.getBytes());
		//Log.getInstance().tianheLog(ret);
		return hex;
	}
	
	public String findDistances(Request req) {
		// *XX,YYYYYYYYYY,S32,HHMMSS,M#

		String ret = head+req.getDeviceId()+",S32,"+Tools.getCurHMS()+",1"+end;
		//Log.getInstance().tianheLog(ret);
		return Tools.bytesToHexString(ret.getBytes());
	}
	

}
