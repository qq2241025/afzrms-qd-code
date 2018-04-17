/**
 * 
 */
package com.mapabc.gater.directl.parse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.eaio.uuid.UUID;
import com.mapabc.AppCtxServer;
import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.bean.status.TTermStatusRecord;
import com.mapabc.gater.directl.dbutil.DbOperation;
import com.mapabc.gater.directl.dbutil.DbUtil;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.lbsgateway.alarmpool.AlarmQueue;
import com.mapabc.gater.util.ReplyResponseUtil;

/**
 * @author 
 * 
 */
public class ParsePND extends ParseBase implements ParseService {
	private static org.apache.commons.logging.Log log = LogFactory.getLog(ParsePND.class);
	TTermStatusRecord statusRecord = new TTermStatusRecord();

	public ArrayList<ParseBase> parseHttpGrps(HttpServletRequest request, HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ParseBase> parseModata(byte[] moBytes) {
		// TODO Auto-generated method stub
		// String hexString = Tools.bytesToHexString(moBytes);
		// this.parseGPRS(hexString);
		this.parseGPRS(moBytes);
		return this.pblist;
	}

	public ParseBase parseSingleGprs(byte[] moBytes) {
		// String hexString = Tools.bytesToHexString(moBytes);

		this.parseGPRS(moBytes);

		return this;

	}

	public ParseBase parseSingleHttpGrps(HttpServletRequest request, HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}

	ArrayList<ParseBase> pblist = new ArrayList<ParseBase>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.autonavi.directl.parse.ParseBase#parseGPRS(java.lang.String)
	 */

	public void parseGPRS(byte[] bcont) {

		// TODO Auto-generated method stub
		byte[] bs = bcont;
		// $WZTREQ,Login,15966668888#
		String cont = new String(bs);
		String[] contSplits = cont.split("\\$");

		for (int i = 0; i < contSplits.length; i++) {
			ParsePND pnd = new ParsePND();

			cont = contSplits[i];
			if (contSplits[i] == null || contSplits[i].trim().length() == 0 || cont.indexOf("#") == -1)
				continue;

			if (log.isDebugEnabled())
				log.debug("原始数据：" + cont + ",size=" + contSplits.length);
			if (!cont.startsWith("$WZTREQ") && cont.lastIndexOf("#") == -1) {
				if (log.isDebugEnabled())
					log.debug("非法数据：" + cont);
				return;
			}
			cont = cont.substring(0, cont.indexOf("#"));
			String[] sps = cont.split(",");
			String deviceid = "";

			deviceid = sps[2].trim();
			String phnum = null;

			this.setDeviceSN(deviceid);
			pnd.setDeviceSN(deviceid);
			this.setPhnum(deviceid);

			if (sps[1].equals("600")) { // 登陆
				String url = (String) AllConfigCache.getInstance().getPdcwztMap().get("UDPIP");

				String callCenterNum = (String) AllConfigCache.getInstance().getPdcwztMap().get("callCenterNumber");
				String statelliteNum = (String) AllConfigCache.getInstance().getPdcwztMap().get("statelliteNum");

				System.out.println(url);
				pnd.setReplyByte(replyLogin(callCenterNum, "", statelliteNum, url).getBytes());

			} else if (sps[1].equals("0")) {// 握手
				this.setReplyByte1(bs);
				pnd.setReplyByte1(bs);
				if (log.isDebugEnabled())
					log.debug("回应握手信号：" + cont);

			} else if (sps[1].trim().equals("610")) {// 位置
				// $WZTREQ,PositionReport,15966668888,YYMMDD,HHMMSS,lat,lon,altitude,speed,track,wxs,flag,status#
				String[] gps = new String[sps.length - 3];
				System.arraycopy(sps, 3, gps, 0, gps.length);
				// pnd.setReplyByte("ok".getBytes());
				pnd.parsePosition(gps, pnd);

			} else if (sps[1].equals("611")) {// 状态上报
				String taskid = sps[4];
				String taskstatus = sps[5];
				String sendtime = sps[6];
				if (log.isDebugEnabled())
					log.debug(this.getDeviceSN() + " tskid=" + taskid + ",tskstatus=" + taskstatus + ",sendtime="
							+ sendtime);

				this.saveMessage(this.getDeviceSN(), "任务确认", taskstatus, null, taskid);

			} else if (sps[1].equals("614")) {// 终端状态下载
				DBService service;
				HashMap<String, String> map = null;
				try {
					service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
					map = service.getStatusList(deviceid);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				List keyList = new ArrayList(map.entrySet());

				Collections.sort(keyList, new Comparator() {

					public int compare(Object o1, Object o2) {
						Map.Entry ent1 = (Map.Entry) o1;
						Map.Entry ent2 = (Map.Entry) o2;

						return (ent1.getKey()).toString().compareTo(ent2.getKey().toString());
					}

				});

				String strList = "";
				Iterator iterator = keyList.iterator();
				while (iterator.hasNext()) {
					Map.Entry met = (Map.Entry) iterator.next();
					String key = (String) met.getKey();
					String value = (String) met.getValue();
					strList += key + "=" + value;
				}

				String reply = "$WZTREQ,614," + deviceid + ",(" + strList + ")#";
				this.setReplyByte(reply.getBytes());
			} else if (sps[1].equals("615")) {// 采集时间段
				// $WZTREQ,CMDID,DEVICEID,startTime，endTime，routeName#
				String startTime = sps[3];
				String endTime = sps[4];
				String routeName = sps[5];

			} else if (sps[1].equals("616")) {// 盲区补偿
				// $WZTREQ,cmdid,deviceid,GPS数据个数，yymmdd,hhmmss,lat,lon,height,speed,track,starNum,…,
				// yymmdd,hhmmss,lat,lon,height,speed,track,starNum #
				String scount = sps[3];
				int count = Integer.parseInt(scount);

				String[] gps = new String[sps.length - 4];
				System.arraycopy(sps, 4, gps, 0, sps.length - 4);
				int gpslen = gps.length / count;

				for (int m = 0; m < count; m++) {
					String[] tmpgpsdata = new String[gpslen];
					System.arraycopy(gps, m * gpslen, tmpgpsdata, 0, gpslen);
					this.parsePosition(tmpgpsdata, null);
					this.getParseList().add(this);
				}

			} else if (sps[1].equals("617")) {// 进出隧道信息
				// $WZTREQ,CMDID,DEVICEID,InOrOutTime，type #
				String time = sps[3];
				String type = sps[4];
				String tunnelName = null;
				if (sps.length == 6) {
					tunnelName = sps[5];
				}

			} else {// 其他指令，终端回应
				String cmdId = sps[1].trim(); // 回应的cmdId
				// 更新数据库指令状态
				String flag = sps[3].trim();
				ReplyResponseUtil.addReply(deviceid + ":" + cmdId, flag);

				if (flag.equals("0")) {
					// DBService service = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
					//
					// if (cmdId.equals("50")) {
					// service.updateBaJuTaskState(this.getDeviceSN(), "0",
					// "0");
					// } else if (cmdId.equals("51")) {
					// service.updateBaJuTaskState(this.getDeviceSN(), "0",
					// "1");
					// } else if (cmdId.equals("53")) {
					// service.updateBaJuTaskState(this.getDeviceSN(), "0",
					// "2");
					// }
				}
				// GBLTerminalList.getInstance()
				// .addDistinct(this.getDeviceSN(), cmdId);
				if (log.isDebugEnabled())
					log.debug("下发指令终端回应信息：" + cont);
			}
			pblist.add(pnd);
		}
	}

	private void parsePosition(String[] sps, ParsePND pnd) {
		String ymd = sps[0].trim();
		String hms = sps[1].trim();
		// Date gpsdate = Tools.formatStrToDate(ymd + hms, "yyMMddHHmmss");
		// if (gpsdate == null) {
		// if (log.isDebugEnabled())
		// log.debug(this.getDeviceSN() + " 上报的日期格式不合法！");
		// }
		// String gpstime = null;
		// try {
		// gpstime = Tools.formatDate2Str(gpsdate, "yyyy-MM-dd HH:mm:ss");
		// } catch (Exception e) {
		// log.error("日期转换异常：", e);
		// }
		String gpstime = "20" + ymd.substring(0, 2) + "-" + ymd.substring(2, 4) + "-" + ymd.substring(4, 6) + " "
				+ hms.substring(0, 2) + ":" + hms.substring(2, 4) + ":" + hms.substring(4, 6);
		this.setTime(gpstime);

		// Timestamp tamp = new Timestamp(gpsdate.getTime());

		String lat = sps[2].trim();
		String lng = sps[3].trim();
		String height = sps[4].trim();
		String speed = sps[5].trim();
		String direction = sps[6].trim();
		String xnum = sps[7];

		if (null != pnd) {
			pnd.setTime(gpstime);
			pnd.setCoordX(lng);
			pnd.setCoordY(lat);
			pnd.setAltitude(height == "" ? "0" : height);
			pnd.setSpeed(speed);
			pnd.setDirection(direction);
			pnd.setSatellites(xnum);
			pnd.setDeviceSN(this.getDeviceSN());
		}
		this.setCoordX(lng);
		this.setCoordY(lat);
		this.setAltitude(height == "" ? "0" : height);
		this.setSpeed(speed);
		this.setDirection(direction);
		this.setSatellites(xnum);
		// 报警
		if (sps.length > 8) {
			String flag = sps[8];
			if (null != flag && flag.trim().length() > 0) {
				this.getJmsInfoList().add("ALARM");
				// 警情信息
				String[] alarms = flag.split("!");
				int alarmType = 0;
				int alarmSubType = 0;
				for (int k = 0; k < alarms.length; k++) {
					if (null == pnd) {
						break;
					}
					String alarm = alarms[k];
					if (alarm.startsWith("1")) {
						//1=0.10000000149011612
						if (log.isDebugEnabled())
							log.debug(this.getDeviceSN() + " 超速报警：" + alarm);
						alarmType |= 1;
						String speedThreshold = alarm.substring(alarm.indexOf("=") + 1);
						pnd.setSpeedThreshold(speedThreshold);
					} else if (alarm.startsWith("2")) {
						//2=0|3
						if (log.isDebugEnabled())
							log.debug(this.getDeviceSN() + " 区域报警：" + alarm);
						alarmType |= 2;
						
						String subType = alarm.substring(alarm.indexOf("=") + 1, alarm.indexOf("|"));
						try {
							alarmSubType = Integer.parseInt(subType);
						} catch (Exception e) {
							log.error("subType is invalid:"+ subType, e);
						}
						pnd.setAlarmSubType(String.valueOf(alarmSubType));
						
						String areaNo = alarm.substring(alarm.indexOf("|") + 1);
						pnd.setAreaNo(areaNo);
//					} else if (alarm.startsWith("3")) {
//						if (log.isDebugEnabled())
//							log.debug(this.getDeviceSN() + " 偏航报警：" + alarm);
//						pnd.setAlarmType("6");
//						String subType = alarm.substring(alarm.indexOf("=") + 1, alarm.indexOf("|"));
					} else if (alarm.startsWith("4")) {
						if (log.isDebugEnabled())
							log.debug(this.getDeviceSN() + " 天线报警：" + alarm);
						alarmType |= 4;
						String subType = alarm.substring(alarm.indexOf("=") + 1, alarm.indexOf("|"));
						try {
							alarmSubType = Integer.parseInt(subType);
						} catch (Exception e) {
							log.error("subType is invalid:"+ subType, e);
						}
						pnd.setAlarmSubType(String.valueOf(alarmSubType));
						String times = alarm.substring(alarm.indexOf("|") + 1);
						int indexOf = times.indexOf(";");
						if(indexOf != -1){
							String startTimeStr = times.substring(0, indexOf);
							String endTimeStr = times.substring(indexOf + 1);
							pnd.setAlarmStartTime(startTimeStr);
							pnd.setAlarmEndTime(endTimeStr);
						}else{
							alarmType = 0;
						}
					}
				}
				if (alarmType > 0) {
					pnd.setAlarmType(String.valueOf(alarmType));
					AlarmQueue.getInstance().addAlarm(pnd);
				}
			}else{
				this.getJmsInfoList().add("LOC");
			}
		}
		// 位置状态
		if (sps.length > 9) {
			String isTrack = sps[9].substring(1, 2);
			if (null != isTrack && isTrack.equals("0")) {
				if (log.isDebugEnabled())
					log.debug(this.getDeviceSN() + "状态：未定位。");
				statusRecord.setLocate("0");
			} else if (null != isTrack && isTrack.equals("1")) {
				if (log.isDebugEnabled())
					log.debug(this.getDeviceSN() + "状态：已定位。");
				statusRecord.setLocate("1");
			} else {
				statusRecord.setLocate("1");
			}

		}
		// 位置坐标类型
//		if (sps.length > 10) {
//			String coordType = sps[10];
//			if (coordType != null && coordType.trim().length() > 0)
//				if (null != pnd) {
//					pnd.setCoordType(Integer.parseInt(coordType));
//				}
//			this.setCoordType(Integer.parseInt(coordType));
//		}
		// 任务状态类型
//		if (sps.length > 11) {
//			String taskType = sps[11];
//			if (null != taskType && taskType.trim().length() > 0) {
//				if (taskType.equals("0")) {
//					if (log.isDebugEnabled())
//						log.debug(this.getDeviceSN() + " 无任务！");
//				} else if (taskType.equals("1")) {
//					if (log.isDebugEnabled())
//						log.debug(this.getDeviceSN() + " 准备出发！");
//				} else if (taskType.equals("2")) {
//					if (log.isDebugEnabled())
//						log.debug(this.getDeviceSN() + " VIP在车！");
//				} else if (taskType.equals("3")) {
//					if (log.isDebugEnabled())
//						log.debug(this.getDeviceSN() + " VIP离车！");
//				}
//
//			}
//		}
		// 定位状态 0为未定位(差分)
		if (sps.length > 10) {
			String quality = sps[10];
			this.setLocateStatus(quality);
		}
		// 里程
		if (sps.length > 11) {
			String mileage = sps[11];
			this.setMileage(mileage);
		}
		//acc status
		if (sps.length > 12) {
			String acc = sps[12];
			statusRecord.setAcc(acc);
		}
		

		statusRecord.setDeviceId(this.getDeviceSN());
		// statusRecord.setGpsTime(tamp);
		this.setStatusRecord(statusRecord);
		if (null != pnd) {
			pnd.setStatusRecord(statusRecord);
		}
		// 自定义扩展状态表测试
		// ExtendsTermStatus extendStatus = new ExtendsTermStatus();
		// extendStatus.setCmmpLdpc("ldpc");
		// extendStatus.setCmmpPower( "p0");
		// extendStatus.setCmmpRs( "rs");
		// extendStatus.setCmmpPower1("p1" );
		// extendStatus.setCmmpPower2( "p2");
		// extendStatus.setCmmpSnr(1.0f);
		//
		// this.setExtendStatus(extendStatus);
		if (log.isDebugEnabled())
			log.debug("PND GPS 信息：deviceid=" + this.getDeviceSN() + ",x=" + lng + ",y=" + lat + ",speed=" + speed
					+ ",xnum=" + xnum + ",time=" + gpstime);

	}

	private String replyLogin(String centerNum, String auth, String minXS, String url) {
		String ret = "";
		// $WZTRES,Login,15966668888, 0,( 01066668888,
		// Login&cma;ReportPosition,3) #
		ret = "$WZTREQ,600," + this.getDeviceSN() + ",0,(" + centerNum + "," + auth + "," + minXS + "," + url + ")#";
		if (log.isDebugEnabled())
			log.debug("回应PND终端登陆信息：" + ret);
		return ret;

	}

	public void saveMessage(String sender, String name, String cont, String gid, String parentId) {

		Connection con = null;
		PreparedStatement pst = null;

		String sequenceNext = null;
		String sequenceCurrval = null;
		String ref_seq_next = null;
		ResultSet rs = null;

		sequenceNext = new UUID().toString();

		String sql = "insert into t_task(task_id,task_name,task_desc,task_type,create_time,is_use,action_type,gid,PARENT_TASK_ID) values('"

				+ sequenceNext + "',?,?,'0',sysdate,'1','1',?,?)";

		String curSql = "insert into ref_task_term  (id,task_id,device_id,create_date,is_use) ";
		curSql += "values('" + sequenceNext + "','" + sequenceNext + "',?,sysdate,'1')";

		String maxIdSql = "";

		try {
			con = DbUtil.getConnection();
			con.setAutoCommit(false);
			pst = con.prepareStatement(sql);
			pst.setString(1, name);
			pst.setString(2, cont);
			pst.setString(3, gid);
			pst.setString(4, parentId);
			pst.execute();

			pst.clearParameters();
			pst.close();

			pst = con.prepareStatement(curSql);
			pst.setString(1, sender);
			pst.execute();

			con.commit();

			con.setAutoCommit(true);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error("插入信息异常", e);
		} finally {
			DbOperation.release(null, rs, pst, null, con);
		}

	}

	public static void main(String[] args) {

		ParsePND PND = new ParsePND();
		String s = "$WZTREQ,611,deviceid,1," + new UUID().toString() + ",taskstatus,2011-12-08#";
		s = "$WZTREQ,610,2011090201,120113,111649,34.220717,108.889198,0,5,353,3,1=100#";
		s = "$WZTREQ,610,354525045370549,160927,213738,0.0,0.0,0.0,0.0,0.0,0,4=0|213638;213738,01,0,0.0,1#";

		PND.parseGPRS(s.getBytes());

		// PND.saveMessage("999999", "taskTest", "taskContent", null, new
		// UUID().toString());
	}

}
