/**
 * 
 */
package com.mapabc.gater.directl.parse.longhan;

import java.util.Hashtable;


/**
 * @author shiguang.zhou
 *
 */
public class WayBillCache {
	
	  private static WayBillCache instance =null;
	  private Hashtable<WayBillBean, Boolean> wayCache = new Hashtable<WayBillBean, Boolean>();
	  private boolean isResponse;
	  
	  
	  public static synchronized WayBillCache getInstance(){
	    if(instance==null){
	      instance = new WayBillCache();
	    }
	    return instance;
	  }
	  
	  public void addWayBill(WayBillBean way, boolean flag){
		  instance.wayCache.put(way, flag);
	  }

	/**
	 * @return the isResponse
	 */
	public boolean isResponse(WayBillBean way) {
		Boolean flag = instance.wayCache.get(way);
		return flag;
	}
 
	  
}
