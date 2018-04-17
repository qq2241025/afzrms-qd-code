
package com.mapabc.gater.directl.encode.guomai;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;
 
 
public class GuoMaiFactory implements EncoderFactory {

	public IControl createControl() {
		return new GuoMaiControl();
	}

	public ILocator createLocator() {
		return new GuoMaiLocator();
	}

	public IAlarm createAlarm() {
		return new GuoMaiAlarm();
	}

 
	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new GuoMaiSetting();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return new GuoMaiQuery();
	}
}
