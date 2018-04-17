package com.mapabc.gater.directl.encode.sdk;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;

 

 
public class SDKFactory implements EncoderFactory {

	public IAlarm createAlarm() {
		// TODO Auto-generated method stub
		return new SDKAlarm();
	}

	public IControl createControl() {
		// TODO Auto-generated method stub
		return new SDKControl();
	}

	public ILocator createLocator() {
		// TODO Auto-generated method stub
		return new SDKLocator();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return new SDKQuery();
	}

	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new SDKSetting();
	}

	
}
