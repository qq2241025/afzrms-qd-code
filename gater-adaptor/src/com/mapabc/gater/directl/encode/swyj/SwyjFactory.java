
package com.mapabc.gater.directl.encode.swyj;


import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;

 
public class SwyjFactory implements EncoderFactory {

	public IControl createControl() {
		return new SwyjControl();
	}

	public ILocator createLocator() {
		return new SwyjLocator();
	}

	public IAlarm createAlarm() {
		return new SwyjAlarm();
	}

 
	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new SwyjSetting();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return new SwyjQuery();
	}
}
