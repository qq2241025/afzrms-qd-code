package com.mapabc.gater.lbmp.service.impl;

import com.mapabc.gater.lbmp.bean.DPoint;
import com.mapabc.gater.lbmp.service.CoordinateConvertService;

/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2015年1月29日 上午11:36:21
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */

public class CoordinateConvertServiceImplDoNothing implements CoordinateConvertService {

	public DPoint getEncryptPoint(double x, double y, String coordType) throws Exception {
		return helpMakeCoordinate(x, y);
	}

	public DPoint getDeflectionPoint(double x, double y, String coordType) throws Exception {
		return helpMakeCoordinate(x, y);
	}

	public DPoint getRouteCorrectPoint(double x, double y, double direction, String coordType) throws Exception {
		return helpMakeCoordinate(x, y);
	}

	public String getPositionDesc(double x, double y, String coordType) throws Exception {
		return "";
	}

	public DPoint getDeflectEncrypt(double x, double y) throws Exception {
		return helpMakeCoordinate(x, y);
	}

	public DPoint reverseDeflect(double desx, double desy) {
		return helpMakeCoordinate(desx, desy);
	}

	private DPoint helpMakeCoordinate(double x, double y){
		DPoint tmpPnt = new DPoint();
		tmpPnt.setX(x);
		tmpPnt.setY(y);
		tmpPnt.setEncryptX(String.valueOf(x));
		tmpPnt.setEncryptY(String.valueOf(y));
		return tmpPnt;
	}
}
