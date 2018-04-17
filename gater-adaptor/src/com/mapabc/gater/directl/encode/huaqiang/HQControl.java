/**
 * 
 */
package com.mapabc.gater.directl.encode.huaqiang;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.Request;
 

/**
 * @author shiguang.zhou
 *
 */
public class HQControl extends ControlAdapter{
	String head = "C M ";
	
	// 调度信息
	public String msg(Request req) {
		String ret = null;
 
		return ret;
	}

	// 油路控制
	public String oilElecControl(Request req) {
		 
    	String ret = "";
    	 

		return ret;
	}
	
	public String camera(Request req) {
		String ret = "";
    	 
		return ret;
	}

}
