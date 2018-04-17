package com.mapabc.gater.directl.encode.kaiyan;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;

 
public class KaiYanFactory implements EncoderFactory {

	public IAlarm createAlarm() {
		// TODO Auto-generated method stub
		return new KaiYanAlarm();
	}

	public IControl createControl() {
		// TODO Auto-generated method stub
		return new KaiYanControl();
	}

	public ILocator createLocator() {
		// TODO Auto-generated method stub
		return new KaiYanLocator();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return null;
	}

	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new KaiYanSetting();
	}

	
}
