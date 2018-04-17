/**
 * 
 */
package com.mapabc.gater.lbmp.service;

import com.mapabc.gater.lbmp.bean.DPoint;

/**
 * @author 
 * 坐标转换接口
 */
public interface CoordinateConvertService {
	
	/**
	 * 获取加密坐标
	 * @param x 原始经度
	 * @param y 原始纬度
	 * @param coordType 坐标类型：0原始坐标 1偏转后坐标 2道路纠偏后坐标
	 * @return
	 * by 
	 */
	public  DPoint getEncryptPoint(double x, double y,String coordType) throws Exception;
	
	/**
	 * 获取偏移坐标
	 * @param x 原始经度
	 * @param y 原始纬度
	 * 
	 * @return
	 * by 
	 */
	public DPoint getDeflectionPoint(double x, double y,String coordType) throws Exception;
	
	/**
	 * 获取道理纠偏坐标
	 * @param x 原始经度
	 * @param y 原始纬度
	 * * @param coordType 坐标类型：0原始坐标 1偏转后坐标  2道路纠偏后坐标
	 * @return
	 * by 
	 */
	public DPoint getRouteCorrectPoint(double x, double y,double direction,String coordType) throws Exception;
	
	/**
	 * 根据坐标获取位置描述
	 * @param x
	 * @param y
	 * * @param coordType 坐标类型：0原始坐标 1偏转后坐标  2道路纠偏后坐标
	 * @return
	 * by 
	 */
	public String getPositionDesc(double x, double y,String coordType) throws Exception;
	
	/**
	 * 偏转加密坐标
	 * @param x
	 * @param y
	 * @return
	 * by 
	 */
	public DPoint getDeflectEncrypt(double x, double y) throws Exception;
	
	/**
	 * 反偏转
	 * @param desx：偏转后坐标
	 * @param desy 偏转后坐标
	 * @return DPoint 反偏转点
	 * by 
	 */
	public DPoint reverseDeflect(double desx, double desy);

}
