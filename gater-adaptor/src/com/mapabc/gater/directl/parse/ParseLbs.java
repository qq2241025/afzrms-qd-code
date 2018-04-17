/**
 * 
 */
package com.mapabc.gater.directl.parse;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

 
import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.ParseConfigParamUtil;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.parse.service.ParseService; 
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;
import com.mapabc.gater.util.PropertyReader;

/**
 * @author shiguang.zhou
 * 
 */
public class ParseLbs extends ParseBase implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseLbs.class);
	  
	public ArrayList<ParseBase> parseHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		ArrayList<ParseBase> list = new ArrayList<ParseBase>();
		list.add(parseSingleGprs(cont));
		return list;
	}

	public ArrayList<ParseBase> parseModata(byte[] moBytes) {
		String hexString = Tools.bytesToHexString(moBytes);
		 		  
		return null;
	}

	public ParseBase parseSingleGprs(byte[] moBytes) {
		String hexString = Tools.bytesToHexString(moBytes);
		
		TTermStatusRecord statusRecord = new TTermStatusRecord();
		// TODO Auto-generated method stub
		// LBS,DEVICEID,X,Y,LBSTIME,ERROR_CODE,ERROR_DESC_LENG, ERROR_DESC
		String cont = new String(Tools.fromHexString(hexString));
		log.info("接收到LBS位置信息：" + cont);
		String[] lbsinfos = cont.split(",");
		String simcard = lbsinfos[1];
		String x = lbsinfos[2];
		String y = lbsinfos[3];
		String lbstime = lbsinfos[4];
		String err_code = lbsinfos[5];
		String err_des_leng = lbsinfos[6];
		this.setDeviceSN(simcard);
		this.setPhnum(simcard);
		this.setCoordX(x);
		this.setCoordY(y);
		this.setTime(lbstime);
		this.setLocateType("0");
		
		try {
			if (err_code.equals("0")) {
				this.getJmsInfoList().add("LOC");
				statusRecord.setLocate("1");
				this.setStatusRecord(statusRecord);
				ParseConfigParamUtil.handleConfig(this);
				log.info("LBS定位成功：" + cont);
 
			} else {
				log.info("LBS定位失败：" + cont);
				//this.getRepList().add("ALARM");
				statusRecord.setLocate("0");
				this.setAlarmType("lbs_" + err_code);
				//this.setStatusRecord(statusRecord);
				AlarmQueue.getInstance().add(this);
				//ParseConfigParamUtil.handleConfig(this);

 
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	return this;
	}

	public ParseBase parseSingleHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}

	 

}
