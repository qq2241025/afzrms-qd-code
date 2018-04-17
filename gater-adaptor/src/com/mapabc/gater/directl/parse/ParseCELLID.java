package com.mapabc.gater.directl.parse;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.parse.service.ParseService;

/**
 * CELLID解析实现类
 * <p>Title: GPS网关</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2006</p>
 *
 * <p>Company: www.mapabc.com</p>
 *
 * @author musicjiang@sohu.com
 * @version 1.0
 */
public class ParseCELLID extends ParseBase implements ParseService{
	
	public ArrayList<ParseBase> parseHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ParseBase> parseModata(byte[] moBytes) {
		// TODO Auto-generated method stub
		return null;
	}

	public ParseBase parseSingleGprs(byte[] moBytes) {
		String hexString = Tools.bytesToHexString(moBytes);
		this.parseGPRS(hexString);
		return this;
	}

	public ParseBase parseSingleHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}
  public ParseCELLID() {
  }

  public void parseGPRS(String phnum, String content) {
  }

  public void parseGPRS(String phnum, byte[] content) {
  }

  public void parseGPRS(String hexString) {
    String[] coords=hexString.split(",");
    this.setPhnum( coords[0] );
     this.setTime( coords[1] );
    this.setCoordX( coords[2] );
    this.setCoordY(coords[3]);
 
  }

  public void parseGPRS(byte[] hexString) {
  }

  public void parseSMS(String phnum, String content) {
  }
 
}
