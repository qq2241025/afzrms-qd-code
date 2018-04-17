
package com.mapabc.gater.directl.encode.longhan;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;

 
public class LongHanFactory implements EncoderFactory {

	public IControl createControl() {
		return new LongHanControl();
	}

	public ILocator createLocator() {
		return new LongHanLocator();
	}

	public IAlarm createAlarm() {
		return new LongHanAlarm();
	}

 
	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new LongHanSetting();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return new LongHanQuery();
	}
}
