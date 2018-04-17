
package com.mapabc.gater.directl.encode.tjzh;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;

 

 
public class TjzhFactory implements EncoderFactory {

	public IControl createControl() {
		return new TjzhControl();
	}

	public ILocator createLocator() {
		return new TjzhLocator();
	}

	public IAlarm createAlarm() {
		return new TjzhAlarm();
	}

 
	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new TjzhSetting();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return new TjzhQuery();
	}
}
