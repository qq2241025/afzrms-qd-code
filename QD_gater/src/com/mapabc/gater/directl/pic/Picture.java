package com.mapabc.gater.directl.pic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.Set;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.lbsgateway.GprsUdpThreadList;
import com.mapabc.gater.lbsgateway.bean.TerminalUDPAddress;
 

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 * 
 * <p>
 * Company: www.mapabc.com
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class Picture extends Hashtable {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(Picture.class);

	private int num; // 图象编号
	private int pakcNo; // 包号
	private int packcounts; // 本图象包总数
	private byte[] imgcontent; // 图象内容
	private String imgStrCont; // 图象内容
	private String localFileName; // 存储图象路径
	private String type; // 拍照触发条件
	private Timestamp timeStamp; // 照片上传时间
	private String deviceId; // 拍照的终端号码
	private float x;
	private float y;
	private byte[] imgBytes;
	private boolean isFirstReq; // 是否存在图片第一包请求
	private String oemCode;
	private Date date;
	private boolean isReaded; // 图片是否已经被读取完
	private String chanelNo;// 通道号
	private String reqImageCmd; // 补发图片请求指令
	private String deviceType;

	/**
	 * @return the deviceType
	 */
	public String getDeviceType() {
		return this.deviceType;
	}

	/**
	 * @param deviceType
	 *            the deviceType to set
	 */
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public synchronized void addImgCont(String packNo, ByteCont bc) {
		if (packNo != null) {
			if (this.get(packNo) == null) {
				this.put(packNo, bc);
			}
		}

	}

	public synchronized void addImgContHex(String packNo, String bc) {
		// if (packNo != null) {
		// if (this.get(packNo) == null) {
		this.put(packNo, bc);
		//				 
		// }
		// }

	}

	public synchronized void addImgBytes(String packNo, byte[] packBytes) {
		this.put(packNo, packBytes);
	}

	/**
	 * @return the localFileName
	 */
	public synchronized String getLocalFileName() {
		return this.localFileName;
	}

	/**
	 * @return the timeStamp
	 */
	public synchronized Timestamp getTimeStamp() {
		return this.timeStamp;
	}

	/**
	 * @param localFileName
	 *            the localFileName to set
	 */
	public synchronized void setLocalFileName(String localFileName) {
		this.localFileName = localFileName;
	}

	/**
	 * @param timeStamp
	 *            the timeStamp to set
	 */
	public synchronized void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the deviceId
	 */
	public synchronized String getDeviceId() {
		return this.deviceId;
	}

	/**
	 * @param deviceId
	 *            the deviceId to set
	 */
	public synchronized void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public static void main(String[] args) {
		Picture picture = new Picture();
	}

	public synchronized InputStream getImgcontent() {

		ByteArrayOutputStream baos = null;
		ByteArrayInputStream bais = null;

		try {
			baos = new ByteArrayOutputStream();
			int size = this.size();
			 
			String tmp = "";
			for (int i = 0; i < size; i++) {
				// ByteCont bc = (ByteCont) instance.get((i + 1) + "");
				String bcs   = (String) this.get((i + 1) + "");
				 tmp+=bcs;
				if (bcs != null) {
					byte[] bb = Tools.fromHexString(bcs);
					baos.write(bb);
				}
			}

			bais = new ByteArrayInputStream(baos.toByteArray());
			log.info(this.getDeviceId()+" 保存的图片数据为："+ tmp);

			this.isReaded = true;

		} catch (Exception e) {
			e.printStackTrace();
			log.error("出现异常", e);
		} finally {

			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}

			} catch (IOException ex1) {
				log.error(ex1.getMessage(), ex1);
			}

		}

		return bais;
	}

	public synchronized int getNum() {
		return num;
	}

	public synchronized int getPackcounts() {
		return packcounts;
	}

	public synchronized String getImgStrCont() {
		return imgStrCont;
	}

	public synchronized int getPakcNo() {
		return pakcNo;
	}

	public synchronized String getType() {
		return type;
	}

	public synchronized void setImgcontent(byte[] imgcontent) {
		this.imgcontent = imgcontent;
	}

	public synchronized void setNum(int num) {
		this.num = num;
	}

	public synchronized void setPackcounts(int packcounts) {
		this.packcounts = packcounts;
	}

	public synchronized void setImgStrCont(String imgStrCont) {
		this.imgStrCont = imgStrCont;
	}

	public synchronized void setPakcNo(int pakcNo) {
		this.pakcNo = pakcNo;
	}

	public synchronized void setType(String type) {
		this.type = type;
	}

	// 数据包是否传输完毕
	public synchronized boolean isTraverOver() {
		boolean ret = false;
		log.info(
				"check is over ,deviceid=" + this.deviceId + ",curPackNo="
						+ this.pakcNo + ",totalPacks=" + this.packcounts
						+ ",size=" + this.size());
		if (this.packcounts > 0 && this.size() > 0
				&& this.packcounts == this.size()) {
			ret = true;
		}
		return ret;
	}

	// 数据包是否传输完毕
	public synchronized boolean isGuoMaiTraverOver() {
		boolean ret = false;
		log.info(
				"check 国脉图片传输 isover ,deviceid=" + this.deviceId
						+ ",curPackNo=" + this.pakcNo + ",totalPacks="
						+ this.packcounts + ",size=" + this.size());

		if (this.size() > 0 && this.packcounts == this.size()) {
			ret = true;
		}
		return ret;
	}

	// 重置图片各参数
	public synchronized void reset() {
		this.clear();
		this.packcounts = 0;
		this.pakcNo = -1;
		this.type = null;
		this.x = 0f;
		this.y = 0f;
		this.timeStamp = null;
		this.localFileName = null;
		this.isFirstReq = false;
		this.isReaded = false;
	}

	/**
	 * @return the x
	 */
	public synchronized float getX() {
		return this.x;
	}

	/**
	 * @return the y
	 */
	public synchronized float getY() {
		return this.y;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public synchronized void setX(float x) {
		this.x = x;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public synchronized void setY(float y) {
		this.y = y;
	}

	/**
	 * @return the imgBytes
	 */
	public synchronized byte[] getImgBytes() {
		return this.imgBytes;
	}

	/**
	 * @param imgBytes
	 *            the imgBytes to set
	 */
	public synchronized void setImgBytes(byte[] imgBytes) {
		this.imgBytes = imgBytes;
	}

	private synchronized String sendPicCmd(String packNo) {
		String bufaCmd = this.getOemCode() + ":" + this.getDeviceId()
				+ "|302|1;" + packNo + " ";

		String bfrep = "C M " + Tools.getRandomString(4) + " " + bufaCmd
				+ Tools.getVerfyCode(bufaCmd.getBytes())+"\r\n";

		return bfrep;
	}

	// 检查丢失的第一个包
	public synchronized String checkMissingPacks() {

		String misspacks = "";

		Set keySets = this.keySet();

		for (int i = 1; i <= this.getPackcounts(); i++) {
			if (keySets.contains(i + "")) {
				continue;
			} else {
				misspacks += String.valueOf(i-1) + ",";
			}
		}
		log.info(
				this.getDeviceId() + "丢失的数据包号为：" + misspacks);

		return misspacks;
	}

	public synchronized void checkIsResponse() {
		java.util.Date preDate = this.getDate();
		if (preDate == null)
			return;

		Date curDate = new Date();
		String interval = null;
		try{
			interval = AllConfigCache.getInstance().getConfigMap().get("checkPicTime");
		}catch(Exception e){
			e.printStackTrace();
		}
		String ivl = ((interval == null) || (interval.trim().length() <= 0)) ? "5"
				: interval;
		int val = Integer.parseInt(ivl);
		log.info(
				this.getDeviceId() + " 开始数据包延时检测，延时标准=" + val + "秒");
		if (curDate.getTime() - preDate.getTime() > val * 1000
				&& !this.isTraverOver() && this.isFirstReq()) {
			log.info(
					this.getDeviceId() + "已经超过" + val + "秒未收到新图片数据包。");

			String chk = this.checkMissingPacks();

			TerminalUDPAddress udp = GprsUdpThreadList.getInstance()
					.getGpsThreadBySim(this.getDeviceId());
			if (udp != null) {

				String[] spl = chk.split(",");

				for (int i = 0; i < spl.length; i++) {
					if (spl[i].trim().equals("0"))
						continue;

					String hexPck = Tools.convertToHex(spl[i], 2);
					String cmd = this.reqImageCmd;// ,不同终端适配 不同的协议
					if (cmd != null) {
						int res = udp.sendUDPByteData(cmd.getBytes());

						log.info(
								this.getDeviceId() + " 补发第" + spl[i]
										+ "个数据包请求:" + cmd + ",发送结果：" + res);
					}
				}
			}
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

	}

	/**
	 * @return the oemCode
	 */
	public synchronized String getOemCode() {
		return this.oemCode;
	}

	/**
	 * @param oemCode
	 *            the oemCode to set
	 */
	public synchronized void setOemCode(String oemCode) {
		this.oemCode = oemCode;
	}

	/**
	 * @return the date
	 */
	public synchronized Date getDate() {
		return this.date;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public synchronized void setDate(Date date) {
		this.date = date;
	}

	/**
	 * @return the isReaded
	 */
	public synchronized boolean isReaded() {
		return this.isReaded;
	}

	/**
	 * @param isReaded
	 *            the isReaded to set
	 */
	public synchronized void setReaded(boolean isReaded) {
		this.isReaded = isReaded;
	}

	/**
	 * @return the isFirstReq
	 */
	public synchronized boolean isFirstReq() {
		return this.isFirstReq;
	}

	/**
	 * @param isFirstReq
	 *            the isFirstReq to set
	 */
	public synchronized void setFirstReq(boolean isFirstReq) {
		this.isFirstReq = isFirstReq;
	}

	/**
	 * @return the chanelNo
	 */
	public synchronized String getChanelNo() {
		return this.chanelNo;
	}

	/**
	 * @param chanelNo
	 *            the chanelNo to set
	 */
	public synchronized void setChanelNo(String chanelNo) {
		this.chanelNo = chanelNo;
	}

	/**
	 * @return the reqImageCmd
	 */
	public synchronized String getReqImageCmd() {
		return this.reqImageCmd;
	}

	/**
	 * @param reqImageCmd
	 *            the reqImageCmd to set
	 */
	public synchronized void setReqImageCmd(String reqImageCmd) {
		this.reqImageCmd = reqImageCmd;
	}

}
