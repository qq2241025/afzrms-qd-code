package com.dt.afzrms.util;

import java.math.BigDecimal;

public class DoubleUtil {
	/**
	* 加法运算
	* 
	* @param v1
	* @param v2
	* @return
	*/
	//科学计数法
	public static float add(float v1, float v2) {
		BigDecimal b1 = new BigDecimal(Float.toString(v1));
		BigDecimal b2 = new BigDecimal(Float.toString(v2));
		return b1.add(b2).floatValue();
	}
	/**
	* 减法运算
	* 
	* @param v1
	* @param v2
	* @return
	*/
	public static double sub(float v1, float v2) {
		BigDecimal b1 = new BigDecimal(Float.toString(v1));
		BigDecimal b2 = new BigDecimal(Float.toString(v2));
		return b1.subtract(b2).doubleValue();
	}
	/**
	* 乘法运算
	* 
	* @param v1
	* @param v2
	* @return
	*/
	public static double mul(float v1, float v2) {
		BigDecimal b1 = new BigDecimal(Float.toString(v1));
		BigDecimal b2 = new BigDecimal(Float.toString(v2));
		return b1.multiply(b2).doubleValue();
	}
	public static float divideToIntegralValue(float v1, float v2) {
		BigDecimal b1 = new BigDecimal(Float.toString(v1));
		BigDecimal b2 = new BigDecimal(Float.toString(v2));
		return b1.divideToIntegralValue(b2).floatValue();
	}
	public static void main(String[] args) {
		float a= 4.544E-5f +3f;
		float b= 1.544E-6f;
		System.out.println("----------使用BigDecimal消除精度影响------------\n" + DoubleUtil.add(a,b));
	}
}
