package com.mapabc.gater.directl.dbutil.service;

import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mapabc.gater.directl.bean.TermOnlieStatus;
import com.mapabc.gater.directl.bean.command.TStructions;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.lbsgateway.bean.TFlowVolume;

public interface DBService {
	
	/**
	 * 加载失败指令
	 * @param deviceId
	 * @param state
	 * @return
	 * 
	 */
	public TStructions[] getStructionBySendStatus(String deviceId,
			String state) ;
	
	/**
	 * 更新无序列号指令状态(儿童手机终端,华强终端)
	 * 
	 * @param deviceId:终端号码
	 * @param state:状态
	 *            0 成功，1，待发 2，失败
	 * @param cmdId:指令标识
	 * @return
	 */
//	public  void updateInstructionsState(String deviceId, String state, String cmdId) ;
	
//	 public QQTChildCardProperty getChildProperty(String deviceId);
	 
	 /**
	 * 终端状态下载,查询任务状态列表
	 * @param deviceId
	 * @return
	 * 
	 */
	public HashMap<String, String> getStatusList(String deviceId);
	 
	 /**
	 * 存储在线状态
	 * @param deviceId
	 * @param curIp
	 * @param status
	 * @param type 0:TCP 1:UDP
	 * @param isonline
	 * 
	 */
	public  void saveTermOnlineStatus(String deviceId, String curIp,
				String status,String type,boolean isonline);
	 /**
	  * 获取在线状态
	  * @param deviceId
	  * @param type 0:TCP 1:UDP
	  * @return
	  * by 
	  */
	 public  TermOnlieStatus getTermOnlineStatus(String deviceId,String type) ;
	 
	 /**
	 * 保存自动远程更新状态
	 * @param deviceId
	 * @param status
	 * @param desc
	 * @return
	 * 
	 */
//	public boolean updateRemoteStatus(String deviceId,String status,String desc);
	 
	 /**
	 * 查询远程更新临时状态表
	 * @param deviceId
	 * @return
	 * 
	 */
//	public boolean isUpdateSuccess(String deviceId);
	 
	 /**
	 * 批量保存报警记录
	 * @param baseList
	 * @throws Exception
	 * 
	 */
	public void saveMoreAlarm(ArrayList<ParseBase> baseList) throws Exception;
	 
//	 public boolean insertPicInfo(Picture p);
	 
	 //保存BAJU任务
//	 public boolean saveBaJuTask(BaJuTask task);
	 
	 //更新BAJU任务状态
//	 public void updateBaJuTaskState(String deviceId, String state ,String type);
	 
	 //获取BAJU所有待发状态
//	 public ArrayList<BaJuTask> getBaJuTaskByState(String flag);
	 
	//批量更新BAJU任务状态
//	 public void batchUpdateBaJuTaskState(ArrayList<BaJuTask> taskList);
	 
	 //更新指令
	 public void updateInstructions(String seq, String ins, String cmdType, Date receiveDate, Date sendDate,String sendCount,String state) throws Exception ;
	 
	 /**
	  * 流量数据保存
	  * @param deviceId 设备ID
	  * @param size 流量大小 单位字节
	  * @param actiontype 0上行 1下行
	  * @param linktype 0TCP 1UDP
	  * @param date 日期
	  * by 
	  */
//	 public void saveFlowVolume(String deviceId, long size, String actiontype, String linktype) throws SQLException ;

	 /**
	  * 流量数据批量保存
	  * by 
	  */
	 public void saveFlowVolume(ArrayList<TFlowVolume> list) throws SQLException ;
	 
	 /**
	 * 初始化在线状态
	 * 
	 */
	public  void initOlineState();
	 
	 /**
	  * 加载待发指令
	  * @param cmdType：指令类型码
	  * @param cmdState：指令状态
	  * @return
	  * by 
	  */
	 public List<TStructions> loadWillSendInstruction(String cmdType,String cmdState);

	 /**
	  * 保存司机登签、退签时间
	  * @param deviceId：设备ID
	  * @param icCard:IC卡号
	  * @param startDate：登陆时间
	  * @param endDate：退出时间
	  * @param driverName：驾驶员名称
	  * @param driverLicense：驾驶证号
	  * @param optType:操作类型 1登陆 0退出
	  * by 
	  */
//	 public void saveTurnOutRecord(String deviceId, String icCard,Date startDate,Date endDate,String driverName,String driverLicense,String optType);

	 /**
	  * 调度信息入库
	  * @param sender：发送者
	  * @param receiver：接收者
	  * @param cont：内容
	  * @param type：类型 0下行 1上行
	  * @param gid: 企业/组ID
	  * by 
	  */
//	 public void saveMessage(String sender, String name,String cont,String type,String gid);
	 
	 /**
	  * 加载数据转发配置
	  * 
	  * @author 
	  */
//	 public ArrayList<ForwardSettingBean> loadForwardCofig();
	 //下载预设任务状态
//	 public List<TaskStatusBean> loadTaskStatus(String deviceId);
	 
	 /**
	  * 保存指令并返回UUID
	  * @param deviceId 设备ID
	  * @param userId 用户ID
	  * @param reqXml 请求的XML接口 
	  */
	 
//	 public String saveInstruction(String deviceId,String userId,String reqXml);
	 /**
	  * 保存指令并返回UUID
	  * @param deviceId 设备ID
	  * @param userId 用户ID
	  * @param reqXml 请求的XML接口 
	  * @param objId 对象ID
	  * @param objType 对象类型
	  * @param creator 创建人
	  * @param createTime 创建时间
	  * @param receiveTime 接收时间
	  * @param type 指令类型
	  * @param instruction 指令协议内容
	  * @param param 参数描述
	  * @param sendTime 发送时间
	  * @param sendCount 发送次数
	  * @param reply 回复内容
	  * @param state 发送状态
	  * @param descp 描述
	  */
	 public String saveStructions(String deviceId, String objId, String objType, String creator, java.util.Date createTime, String req, 
			 java.util.Date receiveTime, String type, String instruction, String param, java.util.Date sendTime,String  sendCount, String reply, String state,String  descp);
	 
	 /**
	  * 保存指令
	  * 
	  */
	 
//	 public String saveStructions(String deviceId, String objId, String objType, String creator, java.util.Date createTime, String req, 
//				String state);
	 /*
	  * 保存任务(同时保存任务和终端的关联)
	  */
	 public String saveTask(String taskName,long taskType,String taskDesc,
			 String creator,java.util.Date createTime,String taskPoints,
			 String onUse,String deviceId,String actionType);
	
}
