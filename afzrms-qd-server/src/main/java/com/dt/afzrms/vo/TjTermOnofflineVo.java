package com.dt.afzrms.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @Title tj term alarm vo
 * @Description TODO
 * @author
 * @createDate 2015年3月31日 下午3:31:25
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class TjTermOnofflineVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date tjDate;
	private int onlineCount;
	private int offlineCount;
	private float onlineRate;

	public TjTermOnofflineVo() {
		super();
	}

	public TjTermOnofflineVo(Date tjDate, int onlineCount, int offlineCount, float onlineRate) {
		super();
		this.tjDate = tjDate;
		this.onlineCount = onlineCount;
		this.offlineCount = offlineCount;
		this.onlineRate = onlineRate;
	}

	public Date getTjDate() {
		return tjDate;
	}

	public void setTjDate(Date tjDate) {
		this.tjDate = tjDate;
	}

	public int getOnlineCount() {
		return onlineCount;
	}

	public void setOnlineCount(int onlineCount) {
		this.onlineCount = onlineCount;
	}

	public int getOfflineCount() {
		return offlineCount;
	}

	public void setOfflineCount(int offlineCount) {
		this.offlineCount = offlineCount;
	}

	public float getOnlineRate() {
		return onlineRate;
	}

	public void setOnlineRate(float onlineRate) {
		this.onlineRate = onlineRate;
	}

}
