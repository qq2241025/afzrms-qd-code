/**
 * 
 */
package com.mapabc.gater.directl.encode;

import com.mapabc.gater.directl.encode.service.IAlarm;
import com.mapabc.gater.directl.encode.service.IControl;
import com.mapabc.gater.directl.encode.service.ILocator;
import com.mapabc.gater.directl.encode.service.IQuery;
import com.mapabc.gater.directl.encode.service.ISetting;

/**
 * 
 * 下行编码类工厂接口，用于生成实现的下行编码类，该工厂接口的实现是
 *
 */
public interface EncoderFactory {
	
	 /**
	  * 控制类对象生成接口
	  */
	abstract IControl createControl(); 
	/**
	 * 定位类对象生成接口
	 */
	abstract ILocator createLocator();
	/**
	 * 报警类对象生成接口
	 * @return
	 * @author 
	 */
	abstract IAlarm createAlarm();
	/**
	 * 设置类对象生成接口
	 * @return
	 * @author 
	 */
	abstract ISetting createSetting();
	/**
	 * 查询类对象生成接口
	 * @return
	 * @author 
	 */
	abstract IQuery createQuery();
}
