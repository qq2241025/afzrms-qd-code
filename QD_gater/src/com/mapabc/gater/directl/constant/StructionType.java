/**
 * 
 */
package com.mapabc.gater.directl.constant;

import java.util.HashMap;

/**
 * 指令类型常量
 * @author 
 *
 */
public abstract class StructionType {
 	
	/**
	 * 抽象类映射命令类型码到接口名称
	 */
	public abstract void  mapCmdTypeToService();
	 
	
	/**
	 * 频率设置
	 */
	public static final String FREQUENCE_COMMOND_TYPE="频率设置"; 
	/**
	 * 点名
	 */
	public static final String SINGLE_LOCATE_COMMOND_TYPE="点名"; 
	/**
	 * 按距离回传
	 */
	public static final String DISTANCE_COMMOND_TYPE="按距离回传"; 
	/**
	 * 定时定位
	 */
	public static final String TIME_POINT_LOCATE_COMMOND_TYPE="定时定位";
	
	/**
	 * 区域围栏设置
	 */
	public static final String AREA_SET_COMMOND_TYPE="区域围栏设置"; 
	/**
	 * 取消区域设置
	 */
	public static final String AREA_CANCEL_COMMOND_TYPE="取消区域设置"; 
	/**
	 * 路线设置
	 */
	public static final String LINE_SET_COMMOND_TYPE="路线设置"; 
	/**
	 * 取消路线设置
	 */
	public static final String LINE_CANCEL_COMMOND_TYPE="取消路线设置";
	/**
	 * 超速设置
	 */
	public static final String OVERSPEED_SET_COMMOND_TYPE="超速设置"; 
	/**
	 * 取消超速设置
	 */
	public static final String OVERSPEED_CANCEL_COMMOND_TYPE="取消超速设置"; 
	/**
	 * 取消报警
	 */
	public static final String ALARM_CANCEL_COMMOND_TYPE="取消报警"; 
	/**
	 * 报警参数设置
	 */
	public static final String ALARM_PARAM_SET_COMMOND_TYPE="报警参数设置"; 
	/**
	 * 报警开关设置
	 */
	public static final String ALARM_SWITCH_SET_COMMOND_TYPE="报警开关设置"; 
	/**
	 * 报警时间设置
	 */
	public static final String ALARM_TIME_SET_COMMOND_TYPE="报警时间设置"; 
	/**
	 * 区域监控参数设置
	 */
	public static final String AREA_CTL_PARAM_SET_COMMOND_TYPE="区域监控参数设置"; 
	/**
	 * 区域监控参数查询
	 */
	public static final String AREA_CTL_PARAM_QUERY_COMMOND_TYPE="区域监控参数查询"; 
	/**
	 * 路线监控参数设置
	 */
	public static final String LINE_CTL_PARMA_SET_COMMOND_TYPE="路线监控参数设置"; 
	/**
	 * 路线监控参数查询
	 */
	public static final String LINE_CTL_PARAM_QUERY_COMMOND_TYPE="路线监控参数查询"; 
	/**
	 * 区域围栏查询
	 */
	public static final String AREA_QUERY_COMMOND_TYPE="区域围栏查询"; 
	/**
	 * 路线查询
	 */
	public static final String LINE_QUERY_COMMOND_TYPE="路线查询";   
	/**
	 * 温度报警设置
	 */
	public static final String TERMERATURE_SET_COMMAND_TYPE="温度报警设置";
	/**
	 * 停车报警设置
	 */
	public static final String STOP_CAR_SET_COMMAND_TYPE="停车报警设置";
	
	/**
	 * 下发消息
	 */
	public static final String MSG_SEND_COMMOND_TYPE="下发消息"; 
	/**
	 * 目的地任务
	 */
	public static final String DESTINATION_COMMOND_TYPE="目的地任务"; 
	/**
	 * 路径任务
	 */
	public static final String ROUTE_COMMOND_TYPE="路径任务"; 
	/**
	 * 数据透传
	 */
	public static final String DATA_THROUGH_COMMOND_TYPE="数据透传"; 
	/**
	 * 油电控制
	 */
	public static final String OIL_CTL_COMMOND_TYPE="油电控制"; 
	/**
	 * 开、关门控制
	 */
	public static final String DOOR_CTL_COMMOND_TYPE="开、关门控制"; 
	/**
	 * 远程下载控制
	 */
	public static final String REMOTE_LOAD_COMMOND_TYPE="远程下载控制"; 
	/**
	 * 复位重启控制
	 */
	public static final String RESET_RECOVER_CTL_COMMOND_TYPE="复位重启控制"; 
	/**
	 * 终端自检控制
	 */
	public static final String TERM_SELF_CHECK_COMMOND_TYPE="终端自检控制";
	
	/**
	 * 车机状态查询
	 */
	public static final String WORK_STATUS_QUERY_COMMOND_TYPE="车机状态查询"; 
	/**
	 * 疑点数据查询
	 */
	public static final String DOUBT_QUERY_COMMOND_TYPE="疑点数据查询"; 
	/**
	 * 里程查询
	 */
	public static final String DISTANCE_QUERY_COMMOND_TYPE="里程查询"; 
	/**
	 * 驾驶员标识查询
	 */
	public static final String DRIVER_IDENTITY_QUERY_COMMOND_TYPE="驾驶员标识查询";     
	/**
	 * 车辆标识代码查询
	 */
	public static final String CAR_IDENTITYCODE_QUERY_COMMOND_TYPE="车辆标识代码查询"; 
	/**
	 * 车牌号查询
	 */
	public static final String CAR_CARD_NUM_QUERY_COMMOND_TYPE="车牌号查询"; 
	/**
	 * 车牌类别查询
	 */
	public static final String CAR_CARD_CATALOG_QUERY_COMMOND_TYPE="车牌类别查询"; 
	/**
	 * 驾驶员代码查询
	 */
	public static final String DRIVER_CODE_QUERY_COMMOND_TYPE="驾驶员代码查询"; 
	/**
	 * 驾驶证号查询
	 */
	public static final String DRIVER_LICENSE_QUERY_COMMOND_TYPE="驾驶证号查询"; 
	/**
	 * 安装日期查询
	 */
	public static final String INSTALL_DATE_QUERY_COMMOND_TYPE="安装日期查询"; 
	/**
	 * 终端时钟查询
	 */
	public static final String TERM_CLOCK_QUERY_COMMOND_TYPE="终端时钟查询"; 
	/**
	 * 通讯地址查询
	 */
	public static final String ADDRESS_QUERY_COMMOND_TYPE="通讯地址查询"; 
	/**
	 * 终端ID查询
	 */
	public static final String TERM_ID_QUERY_COMMOND_TYPE="终端ID查询"; 
	/**
	 * 终端版本号查询
	 */
	public static final String TERM_VER_QUERY_COMMOND_TYPE="终端版本号查询"; 
	/**
	 * 特征系数查询
	 */
	public static final String FEATURE_COFFICIENT_QUERY_COMMOND_TYPE="特征系数查询"; 
	/**
	 * 短信中心号码查询
	 */
	public static final String SMS_CENTER_NUM_QUERY_COMMOND_TYPE="短信中心号码查询"; 
	
	/**
	 * 疲劳驾驶设置
	 */
	public static final String FATIGUE_SET_COMMOND_TYPE="疲劳驾驶设置"; 
	/**
	 * 通讯地址设置
	 */
	public static final String ADDRESS_SET_COMMOND_TYPE="通讯地址设置"; 
	/**
	 * 通话限制设置
	 */
	public static final String CALL_RESTICT_SET_COMMOND_TYPE="通话限制设置"; 
	/**
	 * 监听电话设置
	 */
	public static final String LISTEN_PHONE_SET_COMMOND_TYPE="监听电话设置"; 
	/**
	 * 休眠设置
	 */
 	public static final String SLEEP_SET_COMMOND_TYPE="休眠设置"; 
	/**
	 * 呼入号码设置
	 */
 	public static final String CALL_IN_SET_COMMOND_TYPE="呼入号码设置"; 
	/**
	 * 按键设置
	 */
	public static final String KEY_SET_COMMOND_TYPE="按键设置"; 
	/**
	 * 心跳设置
	 */
	public static final String HEART_SET_COMMOND_TYPE="心跳设置"; 
	/**
	 * 定位模式设置
	 */
	public static final String LOCATE_MODEL_SET_COMMOND_TYPE="定位模式设置"; 
	/**
	 * 车辆标识代码设置
	 */
	public static final String CAR_IDENTITYCODE_SET_COMMOND_TYPE="车辆标识代码设置"; 
	/**
	 * 车牌号设置
	 */
	public static final String CAR_CARD_NUM_SET_COMMOND_TYPE="车牌号设置"; 
	/**
	 * 车牌类别设置
	 */
	public static final String CAR_CARD_CATALOG_COMMOND_TYPE="车牌类别设置"; 
	/**
	 * 驾驶员代码设置
	 */
	public static final String DRIVER_CODE_SET_COMMOND_TYPE="驾驶员代码设置"; 
	/**
	 * 驾驶证号设置
	 */
	public static final String DRIVER_LICENSER_SET_COMMOND_TYPE="驾驶证号设置"; 
	/**
	 * 安装日期设置
	 */
	public static final String INSTALL_DATE_SET_COMMOND_TYPE="安装日期设置"; 
	/**
	 * 时钟设置
	 */
	public static final String CLOCK_SET_COMMOND_TYPE="时钟设置"; 
	/**
	 * 特征系数设置
	 */
	public static final String FEATURE_COFFICIENT_SET_COMMOND_TYPE="特征系数设置"; 
	/**
	 * 短信中心号码设置
	 */
	public static final String SMS_CENTER_NUM_SET_COMMOND_TYPE="短信中心号码设置"; 
	/**
	 * 拍照设置
	 */
	public static final String CAMERA_SET_COMMOND_TYPE="拍照设置"; 
	/**
	 * APN设置
	 */
	public static final String APN_SET_COMMAND_TYPE="APN设置";
	/**
	 * 车辆ID设置
	 */
	public static final String CAR_ID_SET_CMD_TYPE="车辆ID设置";
	/**
	 * 输出端口设置
	 */
	public static final String OUTPORT_SET_CMD_TYPE="输出端口设置";
	/**
	 * IO报警端口设置
	 */
	public static final String IO_ALARM_PORT_SET_CMD_TYPE="IO报警端口设置";
	/**
	 * 设防指令
	 */
	public static final String FORTIFICATION_SET_CMD_TYPE="设防指令";
	/**
	 * 熄火上报间隔设置(UCSTC)
	 */
	public static final String FLAMOUTINTERVAL_SET_CMD_TYPE = "熄火上报间隔设置";
	/**
	 * 设置当前GPS的总里程
	 */
	public static final String TOTALMILEAGE_SET_CMD_TYPE = "设置当前GPS的总里程";
	/**
	 * 设置普通定位包中里程的上报类型
	 */
	public static final String MILEAGETYPE_SET_CMD_TYPE = "设置普通定位包中里程的上报类型";
}
