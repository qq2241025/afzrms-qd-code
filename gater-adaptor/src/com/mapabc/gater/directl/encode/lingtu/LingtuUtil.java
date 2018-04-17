/**
 * 
 */
package com.mapabc.gater.directl.encode.lingtu;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mapabc.gater.lbsgateway.GBLTerminalList;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
 

/**
 * @author shiguang.zhou
 *
 */
public class LingtuUtil {
	
	// 根据序列号终端OEM码
	public static String createOemCodeBySn(String gsn) {
  
		String oemCode = "";
		 
		TTerminal term = GBLTerminalList.getInstance().getTerminaInfo(gsn);
		if (term != null){
			oemCode = term.getOemCode();
		}
		
 		return oemCode;

	}

}
