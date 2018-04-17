/**
 * 
 */
package com.mapabc.gater.directl.encode.doog;

import com.mapabc.gater.directl.encode.EncoderFactory;
import com.mapabc.gater.directl.encode.service.*;

 
/**
 * @author xiaoyang.zhang
 *
 */
public class GAODEFactory implements EncoderFactory {

	public IControl createControl() {
		return new GAODEControl();
	}

	public ILocator createLocator() {
		return new GAODELocator();
	}

	public IAlarm createAlarm() {
		return new GAODEAlarm();
	}

	/* (non-Javadoc)
	 * @see com.autonavi.directl.encode.EncoderFactory#createSetting()
	 */
	public ISetting createSetting() {
		// TODO Auto-generated method stub
		return null;
	}

	public IQuery createQuery() {
		// TODO Auto-generated method stub
		return null;
	}
}
