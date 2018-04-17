package com.mapabc.gater.directl.encode.xinan;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;

public class XinanFactory implements EncoderFactory{

	public IControl createControl() {
		return new XinanControl();
	}

	public ILocator createLocator() {
		return new XinanLocator();
	}

	public IAlarm createAlarm() {
		return new XinanAlarm();
	}

 
	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new XinanSetting();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return new XinanQuery();
	}
}
