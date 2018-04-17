
package com.dt.afzrms.singleLogin.Login;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @Title TODO
 * @Description TODO
 * @author 
 * @createDate 2016年6月11日 下午2:08:41
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */

public class LoginUtils {
	private static final String SALT = "websocket";

	  public static String getEncryptCode(String pw) {
	      return MD5(pw + SALT);
	  }
	  public static String MD5(String input)
	  {
	    try
	    {
	      MessageDigest mdInst = MessageDigest.getInstance("MD5");

	      mdInst.update(input.getBytes());

	      byte[] md = mdInst.digest();

	      StringBuffer hexString = new StringBuffer();

	      for (int i = 0; i < md.length; ++i) {
	        String shaHex = Integer.toHexString(md[i] & 0xFF);
	        if (shaHex.length() < 2) {
	          hexString.append(0);
	        }
	        hexString.append(shaHex);
	      }
	      return hexString.toString();
	    } catch (NoSuchAlgorithmException e) {
	      e.printStackTrace();
	    }
	    return "";
	  }
}
