
package com.mapabc.gater.directl.encode.xinruan;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;


 
public class XinruanFactory implements EncoderFactory {

	public IAlarm createAlarm() {
		// TODO Auto-generated method stub
		return new XinRuanAlarm();
	}

	public IControl createControl() {
		// TODO Auto-generated method stub
		return new XinruanControl();
	}

	public ILocator createLocator() {
		// TODO Auto-generated method stub
		return new XinruanLocator();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new XinruanSetting();
	}

	
}
