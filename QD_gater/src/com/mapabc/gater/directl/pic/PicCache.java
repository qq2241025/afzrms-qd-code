package com.mapabc.gater.directl.pic;

import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.commons.logging.LogFactory;
 
public class PicCache extends Hashtable {
	static PicCache instance = null;
	private Date date;
	private static org.apache.commons.logging.Log log=LogFactory.getLog(PicCache.class);

	public static synchronized PicCache getInstance() {
		if (instance == null) {
			instance = new PicCache();
		}
		return instance;
	}

	private Hashtable<PictureKey, Picture> groupImage = new Hashtable<PictureKey, Picture>();// 终端多组数据

	public void addMorePictureObj(PictureKey pk, Picture pic) {
		groupImage.put(pk, pic);
		log.info("图片缓存个数：" + groupImage.size());
	}

	public synchronized void addPicture(String deviceId, Picture picture) {

		instance.put(deviceId, picture);
		log.info("图片缓存个数：" + instance.size());
		date = new Date();
 
	}

	public synchronized Picture getPicture(String deviceId) {
		Picture pic = null;
		if (deviceId != null) {
			pic = (Picture) instance.get(deviceId);
			if (pic != null) {
				log.info(
						"PicCahge.getPicture,deivcieId=" + deviceId
								+ " curPackNo =" + pic.getPakcNo() + ",total="
								+ pic.getPackcounts() + ", AF=："
								+ pic.isFirstReq());
			}
		}
		return pic;
	}
	
	public synchronized Picture getPicture(PictureKey pk) {
		Picture pic = null;
		if (pk != null) {
			 
			if (groupImage.containsKey(pk))
				pic = (Picture) groupImage.get(pk);
			if (pic != null) {
				log.info(
						"PicCahge.getPicture,deivcieId=" + pk.getDeviceId()
								+ " curPackNo =" + pic.getPakcNo() + ",total="
								+ pic.getPackcounts() + ", AF=："
								+ pic.isFirstReq());
			}else {
				log.info("get null pic by PictureKey");
			}
		}
		return pic;
	}

	public synchronized void removePicture(String deviceId) {

		if (deviceId != null)
			instance.remove(deviceId);
	}
	
	public synchronized void removePicture(PictureKey deviceId) {

		if (deviceId != null)
			groupImage.remove(deviceId);
	}


	public synchronized void checkPackState() {

		Object obj = null;
		boolean flag = false;

		// long curTime = System.currentTimeMillis();
		// long hisTime = 0;
		// if (this.getDate() != null)
		// hisTime = this.getDate().getTime();
		// else {
		// return;
		// }
		// log.info("checking.....:"+(curTime-hisTime-10*1000));

		// if (curTime > hisTime + 10 * 1000) {// 5秒钟PicCache不活动了开始检查
		// log.info("PicCache已经5秒不活动，开始检测图片完整性！");
		Iterator it = this.keySet().iterator();

		while (it.hasNext()) {

			String key = (String) it.next();
			log.info(key + "图像数据包完整性检测！");

			obj = this.get(key);

			if (obj instanceof Picture) {
				Picture bean = (Picture) obj;

				bean.checkIsResponse();

			}

		}
		// }
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	public static void main(String[] args){
		PictureKey pkey = new PictureKey();
		pkey.setDeviceId("1");
		pkey.setPicNum(2);
		
		PictureKey pkey1 = new PictureKey();
		pkey1.setDeviceId("1");
		pkey1.setPicNum(2);
		
		System.out.println(pkey.equals(pkey1));
	}
}
