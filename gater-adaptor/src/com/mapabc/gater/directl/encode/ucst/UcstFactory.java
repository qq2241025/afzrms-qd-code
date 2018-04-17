
package com.mapabc.gater.directl.encode.ucst;

 
import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;
 
public class UcstFactory implements EncoderFactory {

	public IControl createControl() {
		return new UcstControl();
	}

	public ILocator createLocator() {
		return new UcstLocator();
	}

	public IAlarm createAlarm() {
		return new UcstAlarm();
	}

 
	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return new UcstSetting();
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return new UcstQuery();
	}
}
