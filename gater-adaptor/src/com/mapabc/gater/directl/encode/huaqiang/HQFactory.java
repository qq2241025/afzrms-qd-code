
package com.mapabc.gater.directl.encode.huaqiang;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;

 
public class HQFactory implements EncoderFactory {

	public IControl createControl() {
		return new HQControl();
	}

	public ILocator createLocator() {
		return new HQLocator();
	}

	public IAlarm createAlarm() {
		return new HQAlarm();
	}

 
	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return null;
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return null;
	}
}
