
package com.mapabc.gater.directl.encode.lingtu;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;



 
public class LingtuFactory implements EncoderFactory {

	public IControl createControl() {
		return new LingtuControl();
	}

	public ILocator createLocator() {
		return new LingtuLocator();
	}

	public IAlarm createAlarm() {
		return new LingTuAlarm();
	}

 
	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new LingtuSetting();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return null;
	}
}
