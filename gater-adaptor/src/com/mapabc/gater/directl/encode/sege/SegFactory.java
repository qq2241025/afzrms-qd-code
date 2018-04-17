package com.mapabc.gater.directl.encode.sege;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.IAlarm;
import com.mapabc.gater.directl.encode.service.IControl;
import com.mapabc.gater.directl.encode.service.ILocator;
import com.mapabc.gater.directl.encode.service.IQuery;
import com.mapabc.gater.directl.encode.service.ISetting;
 



public class SegFactory implements EncoderFactory {

	public IControl  createControl() {
		return new SegLPG52Control();
	}

	public ILocator createLocator() {
		return new SegLPG52Locator();
	}

	public IAlarm createAlarm() {
		return new SegLPG52Alarm();
	}

 
	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new SegLPG52TerminalSetting();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return null;
	}
}
