/**
 * 
 */
package com.mapabc.gater.directl.parse;

import java.net.Socket;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;

import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService;

/**
 * 上行解析接口适配类，开发者可以通过继承该类来重写接口方法
 *
 */
public class ParseServiceAdaptor extends ParseBase implements ParseService {

 
	public ArrayList<ParseBase> parseModata(byte[] moBytes) {
		// TODO Auto-generated method stub
		return null;
	}
 	 

	public ArrayList<ParseBase> parseHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}


	public ParseBase parseGprs(byte[] moBytes) {
		// TODO Auto-generated method stub
		return null;
	}


	public ParseBase parseSingleGprs(byte[] moBytes) {
		// TODO Auto-generated method stub
		return null;
	}


	public ParseBase parseSingleHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}


	 

}
