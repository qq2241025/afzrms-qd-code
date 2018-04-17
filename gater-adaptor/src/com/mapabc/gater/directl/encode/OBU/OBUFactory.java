package com.mapabc.gater.directl.encode.OBU;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;

 
public class OBUFactory implements EncoderFactory {

	public IAlarm createAlarm() {
		// TODO Auto-generated method stub
		return new OBUAlarm();
	}

	public IControl createControl() {
		// TODO Auto-generated method stub
		return new OBUControl();
	}

	public ILocator createLocator() {
		// TODO Auto-generated method stub
		return new OBULocator();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return new OBUQuery();
	}

	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new OBUSetting();
	}

	
}
