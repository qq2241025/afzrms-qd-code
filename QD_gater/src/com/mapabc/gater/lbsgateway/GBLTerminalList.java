package com.mapabc.gater.lbsgateway;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.dbutil.DbOperation;
import com.mapabc.gater.lbsgateway.bean.TTerminal;
import com.mapabc.gater.lbsgateway.bean.TableConfigBean;
import com.mapabc.gater.util.DBTableConfig;

public class GBLTerminalList {
	private static org.apache.commons.logging.Log log = LogFactory.getLog(GBLTerminalList.class);

	static GBLTerminalList instance = null;
	// private static IMemcachedCache cache;
	private HashMap<String, String> configMap = AllConfigCache.getInstance().getConfigMap();

	private ConcurrentMap<String, TTerminal> termMap = new ConcurrentHashMap<String, TTerminal>();
	private ConcurrentMap<String, String> vehicleRelate = new ConcurrentHashMap<String, String>();
	private ConcurrentMap<String, String> personRelate = new ConcurrentHashMap<String, String>();
	private ConcurrentHashMap<String, Boolean> lastLocRec = new ConcurrentHashMap<String, Boolean>();
	private ConcurrentHashMap<String, Boolean> lastStatusRec = new ConcurrentHashMap<String, Boolean>();

	// private static String isload = null;

	public static synchronized GBLTerminalList getInstance() {
		if (instance == null) {
			instance = new GBLTerminalList();
		}
		return instance;
	}

	private GBLTerminalList() {
	}

	// 从内存中获取指定终端的基本信息
	public TTerminal getTerminaInfo(String termNum) {
		TTerminal term = null;
		ConcurrentMap<String, TTerminal> terms = getTermMap();
		if (terms != null) {
			term = (TTerminal) terms.get(termNum);
			if (term == null) {
				// 从库中遍历查找
				// term = reloadTerminalById(termNum);
			}
		}
		return term;
	}

	// 重置缓存终端信息
	public void resetTerminaInfo(String deviceId, TTerminal terminal) {
		TTerminal term = null;
		ConcurrentMap<String, TTerminal> terms = getTermMap();
		if (terms != null) {
			term = (TTerminal) terms.get(deviceId);
			if (term != null) {
				// 从库中遍历查找
				terms.remove(deviceId);
				// if (cache != null)
				// cache.put(KeyConstant.TermCacheKey, terms);
			}
		}

	}

	/**
	 * 通过ID获得设备SIM卡号
	 * 
	 * @param termNum
	 *            String
	 * @return String
	 */
	public String getSimcardNum(String termNum) {
		TTerminal term = this.getTerminaInfo(termNum);

		String simcard = null;
		if (term != null) {
			String deviceType = term.getTEntTermtype();
			if (deviceType != null && deviceType.equals("GP-BAJU-GPRS")) {
				simcard = term.getDeviceId();// 八局DOOG
			} else {
				simcard = term.getSimcard();
			}
		}
		return simcard;
	}

	// 加载 终端-车辆 关联关系
	public void loadVehicleRelate(Connection conn) {
		Statement stm = null;
		ResultSet rs = null;

		String sql = "select vt.vid,vt.tid  from ref_vehicle_terminal vt where vt.ishold='1'";
		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			while (rs.next()) {
				String tid = rs.getString("tid");
				String vid = rs.getString("vid");
				this.vehicleRelate.put(tid, vid);
			}
		} catch (SQLException e) {
			log.error("加载终端与车关联关系异常：", e);
			e.printStackTrace();
		} finally {
			DbOperation.release(stm, rs, null, null, null);
		}

	}

	// 加载 终端-人 关联关系
	public void loadPersonRelate(Connection conn) {
		Statement stm = null;
		ResultSet rs = null;

		String sql = "select vt.pid,vt.tid  from ref_person_terminal vt where vt.ishold='1'";
		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			while (rs.next()) {
				String tid = rs.getString("tid");
				String pid = rs.getString("pid");

				this.personRelate.put(tid, pid);
			}
		} catch (SQLException e) {
			log.error("加载终端与人关联关系异常：", e);
			e.printStackTrace();
		} finally {
			DbOperation.release(stm, rs, null, null, null);
		}

	}

	// 加载 终端-车辆 关联关系
	public void loadVehicleRelateByDeviceId(Connection conn, String deviceId) {
		Statement stm = null;
		ResultSet rs = null;

		String sql = "select vt.vid,vt.tid  from ref_vehicle_terminal vt where vt.ishold='1' and tid='" + deviceId
				+ "'";
		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			while (rs.next()) {
				String tid = rs.getString("tid");
				String vid = rs.getString("vid");

				this.vehicleRelate.put(tid, vid);
			}
		} catch (SQLException e) {
			log.error("加载终端与车关联关系异常：", e);
			e.printStackTrace();
		} finally {
			DbOperation.release(stm, rs, null, null, null);
		}

	}

	// 加载 终端-人 关联关系
	public void loadPersonRelateByDeviceId(Connection conn, String deviceId) {
		Statement stm = null;
		ResultSet rs = null;

		String sql = "select vt.pid,vt.tid  from ref_person_terminal vt where vt.ishold='1' and tid='" + deviceId + "'";
		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			while (rs.next()) {
				String tid = rs.getString("tid");
				String pid = rs.getString("pid");

				this.personRelate.put(tid, pid);
			}
		} catch (SQLException e) {
			log.error("加载终端与人关联关系异常：", e);
			e.printStackTrace();
		} finally {
			DbOperation.release(stm, rs, null, null, null);
		}

	}

	// 设置车、人、终端关联
	public void setTerminalObj(TTerminal terminal) {

		String isRelateObj = configMap.get("isRelateObj");

		if (isRelateObj != null && isRelateObj.length() > 0) {
			String vechicleId = this.vehicleRelate.get(terminal.getDeviceId());
			String personId = this.personRelate.get(terminal.getDeviceId());
			if (personId != null) {
				terminal.setObjId(personId);
				terminal.setObjType("1");
			}
			if (vechicleId != null) {// 以车为主
				terminal.setObjId(vechicleId);
				terminal.setObjType("2");
			}
		}
	}

	// 重载终端
	public TTerminal reloadTerminalById(String termNum) {
		TTerminal terminal = null;

		String saveType = null;// configMap.get("dbSaveOff");
//		String isRelateObj = configMap.get("isRelateObj");

		String saveOff = (saveType == null || saveType.length() <= 0) ? "1" : saveType;

//		String locateType = configMap.get("locateType");
//		String locType = (locateType == null || locateType.length() <= 0) ? "1" : locateType;

//		String ent_code = configMap.get("eid");
//		String eid = (ent_code == null || ent_code.length() <= 0) ? "null" : ent_code;

		Connection conn = DbOperation.getConnection();
		String deviceId = "DEVICE_ID";
//		String entCode = "ENT_CODE";
		String sim = "SIMCARD";
//		String term_name = "TERM_NAME";
//		String typeCode = "TYPE_CODE";
//		String usageFlag = "USAGE_FLAG";
		String term_name = "NAME";
		String typeCode = "PROTOCAL_TYPE";
		String usageFlag = "IS_USED";
//		String SEid = "EID";
//		String SGid = "GID";
		String tLocType = "locate_type";
//		String oemCode = "OEM_CODE";

		String sql = "select * from T_TERMINAL t ";
		sql += " where t.device_id='" + termNum + "'";

		if (saveOff.equals("0")) {// 动态读取表结构

			DBTableConfig instance = DBTableConfig.getInstance();
			TableConfigBean termTable = (TableConfigBean) instance.get("T_TERMINAL");

			String tableName = termTable.getTableName();
			Hashtable<String, String> columns = termTable.getColumns();

			deviceId = GBLTerminalList.getHashMapValue(columns, "DEVICE_ID");// columns.get("DEVICE_ID").toString();
//			entCode = GBLTerminalList.getHashMapValue(columns, "ENT_CODE");// columns.get("ENT_CODE").toString();
			term_name = GBLTerminalList.getHashMapValue(columns, "TERM_NAME");// columns.get("TERM_NAME").toString();
			typeCode = GBLTerminalList.getHashMapValue(columns, "TYPE_CODE");// columns.get("TYPE_CODE").toString();
			sim = GBLTerminalList.getHashMapValue(columns, "SIMCARD");// columns.get("SIMCARD").toString();
			usageFlag = GBLTerminalList.getHashMapValue(columns, "USAGE_FLAG");
//			oemCode = GBLTerminalList.getHashMapValue(columns, "OEM_CODE");
			sql = "select * from " + tableName + " where " + deviceId + "='" + termNum + "'";
		}

		Statement stm = null;
		ResultSet rs = null;
		try {
			// if (isRelateObj != null && isRelateObj.equals("1")) {
			//
			// this.loadPersonRelateByDeviceId(conn, termNum);
			//
			// this.loadVehicleRelateByDeviceId(conn, termNum);
			// }

			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			if (rs.next()) {// 存在则加载到内存
				terminal = new TTerminal();
				terminal.setDeviceId(rs.getString(deviceId));
				// terminal.setEntCode(rs.getString(entCode));
				terminal.setTermName(rs.getString(term_name));
				terminal.setTEntTermtype(rs.getString(typeCode));
				terminal.setSimcard(rs.getString(sim));
				terminal.setUsageFlag(rs.getLong(usageFlag));
				// terminal.setGid(rs.getString(SGid));
				// terminal.setEntCode(rs.getString(SEid));
				terminal.setLocateType(rs.getString(tLocType));
				// terminal.setOemCode(rs.getString(oemCode));

				if (rs.getString(deviceId) != null && rs.getString(typeCode) != null) {
					// this.setTerminalObj(terminal);

					termMap.put(termNum, terminal);
					// if (cache != null)
					// cache.put(KeyConstant.TermCacheKey, termMap);

					log.info(" 新增了终端" + termNum + ",共加载了" + termMap.size() + "个终端");
				}

			}
		} catch (SQLException ex) {
			log.error("加载新增设备异常：" + ex.getMessage(), ex);
		} finally {
			DbOperation.release(stm, rs, null, null, conn);

		}
		return terminal;
	}

	/**
	 * 加载所有的终端到内存中,减轻数据库压力
	 */
	public ConcurrentMap<String, TTerminal> loadTerminals() {
		// String isRelateObj = configMap.get("isRelateObj");
		Connection conn = DbOperation.getConnection();
		String saveType = configMap.get("dbSaveOff");
		String saveOff = (saveType == null || saveType.length() <= 0) ? "1" : saveType;

		// String locateType = configMap.get("locateType");
		// String locType = (locateType == null || locateType.length() <= 0) ?
		// "1"
		// : locateType;

		// String ent_code = configMap.get("eid");
		// String eid = (ent_code == null || ent_code.length() <= 0) ? "null"
		// : ent_code;

		String deviceId = "DEVICE_ID";
		// String entCode = "ENT_CODE";
//		String term_name = "TERM_NAME";
//		String typeCode = "TYPE_CODE";
//		String usageFlag = "USAGE_FLAG";
		String sim = "SIMCARD";
		String term_name = "NAME";
		String typeCode = "PROTOCAL_TYPE";
		String usageFlag = "IS_USED";
		// String SEid = "EID";
		// String SGid = "GID";
		String tLocType = "locate_type";
		// String oemCode = "OEM_CODE";

		String sql = "select * from T_TERMINAL t";
		// sql += " where t.locate_Type<>'" + locType + "'";

		if (saveOff.equals("0")) {// 动态读取表结构
			DBTableConfig instance = DBTableConfig.getInstance();
			TableConfigBean termTable = (TableConfigBean) instance.get("T_TERMINAL");

			String tableName = termTable.getTableName();
			Hashtable<String, String> columns = termTable.getColumns();

			deviceId = GBLTerminalList.getHashMapValue(columns, "DEVICE_ID");// columns.get("DEVICE_ID").toString();
			// entCode = Tools.getHashMapValue(columns, "ENT_CODE");//
			// columns.get("ENT_CODE").toString();
			term_name = GBLTerminalList.getHashMapValue(columns, "TERM_NAME");// columns.get("TERM_NAME").toString();
			typeCode = GBLTerminalList.getHashMapValue(columns, "TYPE_CODE");// columns.get("TYPE_CODE").toString();
			sim = GBLTerminalList.getHashMapValue(columns, "SIMCARD");// columns.get("SIMCARD").toString();
			usageFlag = GBLTerminalList.getHashMapValue(columns, "USAGE_FLAG");
			// oemCode = getHashMapValue(columns, "OEM_CODE");

			sql = "select * from " + tableName;
		}

		Statement stm = null;
		ResultSet rs = null;
		try {
			// if (isRelateObj != null && isRelateObj.equals("1")) {
			// if (this.personRelate.size() <= 0)
			// this.loadPersonRelate(conn);
			// if (this.vehicleRelate.size() <= 0)
			// this.loadVehicleRelate(conn);
			// }

			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			while (rs.next()) {
				TTerminal terminal = new TTerminal();
				terminal.setDeviceId(rs.getString(deviceId));
				// terminal.setEntCode(rs.getString(entCode));
				terminal.setTermName(rs.getString(term_name));
				terminal.setTEntTermtype(rs.getString(typeCode));
				terminal.setSimcard(rs.getString(sim));
				terminal.setUsageFlag(rs.getLong(usageFlag));
				// terminal.setGid(rs.getString(SGid));
				// terminal.setEntCode(rs.getString(SEid));
				terminal.setLocateType(rs.getString(tLocType));
				// terminal.setOemCode(rs.getString(oemCode));

				if (rs.getString(deviceId) != null) {
					// this.setTerminalObj(terminal);
					termMap.put(rs.getString(deviceId), terminal);
					// if (cache != null)
					// cache.put(KeyConstant.TermCacheKey, termMap);
				}

				log.info(" load terminal, device id : " + terminal.getDeviceId() + " loaded");
			}
			log.info(" 共加载了" + termMap.size() + "个终端");
		} catch (SQLException ex) {
			log.error("加载设备异常：" + ex.getMessage(), ex);
		} finally {
			DbOperation.release(stm, rs, null, null, conn);
		}
		return termMap;
	}

	public void loadAllLastLoc() {
		String sql = "select * from t_last_locrecord";
		Connection conn = DbOperation.getConnection();
		Statement stm = null;
		ResultSet rs = null;

		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			while (rs.next()) {
				lastLocRec.put(rs.getString("DEVICE_ID"), true);
				log.info(rs.getString("DEVICE_ID") + "在数据库中已有最近位置。");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbOperation.release(stm, rs, null, null, conn);
		}

	}

	public void loadAllLastStatus() {
		String sql = "select * from T_TERM_STATUS_LAST_RECORD";
		Connection conn = DbOperation.getConnection();
		Statement stm = null;
		ResultSet rs = null;

		try {
			stm = conn.createStatement();
			rs = stm.executeQuery(sql);
			while (rs.next()) {
				lastStatusRec.put(rs.getString("DEVICE_ID"), true);

				log.info(rs.getString("DEVICE_ID") + "在数据库中已有最近位置状态。");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			DbOperation.release(stm, rs, null, null, conn);
		}

	}

	// 从内存删除指定终端
	public void removeTerminal(String deviceId) {

		ConcurrentMap<String, TTerminal> terms = getTermMap();
		if (terms == null) {
			log.info("get terminal cache is null from memcached.");
			return;
		}
		TTerminal terminal = (TTerminal) terms.get(deviceId);
		if (terminal != null) {
			terms.remove(deviceId);
			// if (cache != null)
			// cache.put(KeyConstant.TermCacheKey, terms);

			log.info("从内存删除终端：" + deviceId + ",当前缓存终端数：" + terms.size());
		}
	}

	public String getTermTypeCode(String deviceId) {
		String devType = null;

		TTerminal bean = this.getTerminaInfo(deviceId);

		if (bean != null) {
			devType = bean.getTEntTermtype();
		}
		if (devType == null) {
			return Const.PROTOCAL_TYPE_DEFAULT;
		}

		return devType;
	}

	/**
	 * @return the vehicleRelate
	 */
	public ConcurrentMap<String, String> getVehicleRelate() {
		return this.vehicleRelate;
	}

	/**
	 * @return the personRelate
	 */
	public ConcurrentMap<String, String> getPersonRelate() {
		return this.personRelate;
	}

	/**
	 * @param vehicleRelate
	 *            the vehicleRelate to set
	 */
	public void setVehicleRelate(ConcurrentMap<String, String> vehicleRelate) {
		this.vehicleRelate = vehicleRelate;
	}

	/**
	 * @param personRelate
	 *            the personRelate to set
	 */
	public void setPersonRelate(ConcurrentMap<String, String> personRelate) {
		this.personRelate = personRelate;
	}

	/**
	 * @return the termMap
	 */
	public ConcurrentMap<String, TTerminal> getTermMap() {

		// if (cache != null) {
		// return (ConcurrentMap) cache.get(KeyConstant.TermCacheKey);
		// }
		return termMap;
	}

	/**
	 * 返回缓存中是否存在最近位置表中的数据
	 * 
	 * @param deviceId
	 * @return
	 */
	public boolean getLastLocMap(String deviceId) {

		if (lastLocRec != null)
			return lastLocRec.get(deviceId);

		return false;

	}

	/**
	 * 对于已保存到最近位置表的设备，增加到缓存
	 * 
	 * @param deviceId
	 */
	public void addLastLocToMap(String deviceId) {
		lastLocRec.put(deviceId, true);
	}

	/**
	 * 返回缓存中是否存在最近状态表中的数据
	 */
	public boolean getLastStatusMap(String deviceId) {

		if (lastStatusRec != null)
			return lastStatusRec.get(deviceId);

		return false;

	}

	/**
	 * 对于已保存到最近位置表的设备，增加到缓存
	 * 
	 * @param deviceId
	 */
	public void addLastStatusToMap(String deviceId) {
		lastStatusRec.put(deviceId, true);
	}

	private static String getHashMapValue(Hashtable<String, String> map, String key) {
		String ret = null;
		if (map != null && map.size() > 0) {
			String value = map.get(key).toString();
			ret = (value == null || value.length() <= 0) ? key : map.get(key).toString();
		} else {
			log.error("动态数据结构配置无数据", null);
		}
		return ret;
	}
}
