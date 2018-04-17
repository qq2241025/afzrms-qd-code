
package com.mapabc.gater.directl.encode.wxp;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;

 
public class WxpFactory implements EncoderFactory {

	public IAlarm createAlarm() {
		// TODO Auto-generated method stub
		return null;
	}

	public IControl createControl() {
		// TODO Auto-generated method stub
		return new WxpControl();
	}

	public ILocator createLocator() {
		// TODO Auto-generated method stub
		return new WxpLocator();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return new WxpQuery();
	}

	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new WxpSetting();
	}
 
}
