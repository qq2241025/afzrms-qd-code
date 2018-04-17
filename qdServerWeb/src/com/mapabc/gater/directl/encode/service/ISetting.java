/**
 * 
 */
package com.mapabc.gater.directl.encode.service;

import com.mapabc.gater.directl.encode.Request;

/**
 * 设置类接口
 * 
 */
public interface ISetting {

	/**
	 * 设置呼入号码
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String callNumberSetting(Request req);

	/**
	 * 按键设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String keySetting(Request req);

	/**
	 * 定位请求设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String locateSetting(Request req);

	/**
	 * 通讯参数设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String addrSetting(Request req);


	/**
	 * 心跳周期设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String heartSetting(Request req);

	/**
	 * 报警时间设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String alarmTimeSetting(Request req);

	/**
	 * 疲劳驾驶设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String fatigueDriveSetting(Request req);

	/**
	 * 安装时间设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String installDateSetting(Request req);

	/**
	 * 终端时钟校对设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String clockSetting(Request req);

	/**
	 * 短信中心号码设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String smsCenterSetting(Request req);

	/**
	 * 拍照设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String camera(Request req);

	/**
	 * APN设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String apnSetting(Request req);

	/**
	 * 车台ID设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String carIdSetting(Request req);

	/**
	 * 设防设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String fortificationSetting(Request req);

	/**
	 * 油量按时间间隔回传设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String oilInterSetting(Request req);

	/**
	 * 温度按时间间隔设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String temperatureInterSetting(Request req);

	/**
	 * 熄火上报间隔设置
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String flameOutInterSetting(Request req);

	/**
	 * 设置当前GPS的总里程
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String totalMileageSetting(Request req);

	/**
	 * 设置普通定位包中里程的上报类型
	 * 
	 * @param Request
	 *            Requestget包含了传入的相关参数
	 * @return 返回指令内容,以16进制形式返回
	 * 
	 */
	public String mileageTypeSetting(Request req);
}
