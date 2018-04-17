/**
 * 
 */
package com.mapabc.gater.directl.parse.longhan;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.dbutil.DbOperation;
import com.mapabc.gater.directl.dbutil.DbUtil; 
 

/**
 * @author shiguang.zhou
 *
 */
public class OilReferCoefficient {
	private static OilReferCoefficient instance = null;
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(OilReferCoefficient.class); 
	
	private Hashtable<String, ArrayList<TOilboxCoefficient>> oilCoefficient = new Hashtable<String, ArrayList<TOilboxCoefficient>>();
	private Hashtable<String, Float> curOilMass = new Hashtable<String, Float>();
	
	public static synchronized OilReferCoefficient getInstance() {
		if (instance == null) {
			instance = new OilReferCoefficient();
		}
		return instance;
	}
	
	
	public void loadOilCoefficient(String deviceId){
		String sql = "select * from t_oilbox_coefficient c,ref_device_oilbox ref,e_vehicle v  where c.coefficient_num=ref.coefficient_num and v.device_id=ref.device_id and  ref.device_id='"+deviceId+"'";
		
		Connection con = DbUtil.getConnection();
		Statement stm = null;
		ResultSet rs = null;
		ArrayList<TOilboxCoefficient> list = new ArrayList<TOilboxCoefficient>();
		
		try {
			stm = con.createStatement();
			rs = stm.executeQuery(sql);
			
			while(rs.next()){
				TOilboxCoefficient coeff = new TOilboxCoefficient();
				coeff.setCoefficientNum(rs.getLong("coefficient_num"));
				coeff.setOilMass(rs.getFloat("oil_mass"));
				coeff.setResistanceS(rs.getFloat("RESISTANCE_S"));
				coeff.setResistanceE(rs.getFloat("RESISTANCE_E"));
				coeff.setVoltageS(rs.getFloat("VOLTAGE_S"));
				coeff.setVoltageE(rs.getFloat("VOLTAGE_S"));
				coeff.setGrade(rs.getString("GRADE"));
				coeff.setName(rs.getString("NAME"));
				coeff.setTankCapacity(rs.getFloat("petroltank"));
				log.info(deviceId+" 油量系数方案：编号="+coeff.getCoefficientNum()+",小电阻值="+coeff.getResistanceS()+",大电阻值="+coeff.getResistanceE()+",对应的油量值="+coeff.getOilMass()+",油箱容积："+coeff.getTankCapacity());
				list.add(coeff);
 			}
			log.info("加载了"+list.size()+" 个油耗系数。");

			if (list.size()>0){
				instance.oilCoefficient.put(deviceId, list);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getInstance().errorLog("加载油箱参考系数异常", e);
			e.printStackTrace();
			
		}finally{
			DbOperation.release(stm, rs, null, null, con);
		}
		
	
	}
	
	//获取前一次油箱油量
	public float getPreOilMass(String deviceId){
		String sql = "select * from T_TERM_STATUS_LAST_RECORD where device_id='"+deviceId+"'";
		float preOil = 0;
		
		Connection con = DbUtil.getConnection();
		Statement stm = null;
		ResultSet rs = null;
		
		try {
			stm = con.createStatement();
			rs = stm.executeQuery(sql);
			
			if (rs.next()){
				preOil = rs.getFloat("oil_mass");
			}
			log.info(deviceId+" 上次油箱油量："+preOil);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			Log.getInstance().errorLog("加载油箱上次油量异常", e);
		}finally{
			DbOperation.release(stm, rs, null, null, con);
		}
		
		
		return preOil;
		
	}


	/**
	 * @return the oilCoefficient
	 */
	public  ArrayList<TOilboxCoefficient> getOilCoefficient(String deviceId) {
		return instance.oilCoefficient.get(deviceId);
	}
	
	public  void removeOilCoefficient(String deviceId){
		instance.oilCoefficient.remove(deviceId);
		log.info(deviceId+" 删除缓中的油量系数方案。");
	}


	/**
	 * @return the curOilMass
	 */
	public  float getCurOilMass(String deviceId) {
		Float curOil =  instance.curOilMass.get(deviceId);
		if (curOil==null || curOil==0){
			curOil = getPreOilMass(deviceId);
		}
		return curOil;
	}


	/**
	 * @param curOilMass the curOilMass to set
	 */
	public  void setCurOilMass(String deviceId, Float curOilMass) {
		instance.curOilMass.put(deviceId, curOilMass);
	}
 	

}
