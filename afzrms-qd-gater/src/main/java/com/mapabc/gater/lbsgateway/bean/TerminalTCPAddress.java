/**
 * 
 */
package com.mapabc.gater.lbsgateway.bean;

import java.io.Serializable;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.ParseConfigParamUtil;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.constant.StructionResult;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.lbsgateway.GprsTcpThreadList;
import com.mapabc.gater.lbsgateway.service.flowvolume.FlowVolumeCalculate;

/**
 * @author 
 * 
 */
public class TerminalTCPAddress implements Serializable {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(TerminalTCPAddress.class);

	private String deviceSN;

	private transient SocketAddress socketAddress;

	private transient SocketChannel socketChannel;

	private Date date;

	private String localAddr;

	/**
	 * @return the deviceSN
	 */
	public String getDeviceSN() {
		return this.deviceSN;
	}

	/**
	 * @return the socketAddress
	 */
	public SocketAddress getSocketAddress() {
		return this.socketAddress;
	}

	/**
	 * @return the socketChannel
	 */
	public SocketChannel getSocketChannel() {
		return this.socketChannel;
	}

	/**
	 * @return the date
	 */
	public Date getDate() {
		return this.date;
	}

	/**
	 * @param deviceSN
	 *            the deviceSN to set
	 */
	public void setDeviceSN(String deviceSN) {
		this.deviceSN = deviceSN;
	}

	/**
	 * @param socketAddress
	 *            the socketAddress to set
	 */
	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	/**
	 * @param socketChannel
	 *            the socketChannel to set
	 */
	public void setSocketChannel(SocketChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	/**
	 * @param date
	 *            the date to set
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * 通过TCP通道发送内容到终端
	 * 
	 * @param data
	 * @return
	 * @author 
	 */
	public synchronized int sendByteArrayData(byte[] data) {
		int ret = -1;

		if (data == null || data.length == 0) {
			return StructionResult.CMD_INVALID;
		}

		try {
			ByteBuffer bf = ByteBuffer.wrap(data);
			SocketChannel schanel = getSocketChannel();
			Socket client = schanel.socket();
			log.info(getDeviceSN() + " TerminalTCPAddress isclosed:"
					+ client.isClosed() + ",isConnected="
					+ client.isConnected() + ",isInputShutdown="
					+ client.isInputShutdown() + ",isOutShutdown="
					+ client.isOutputShutdown() + ",addr="
					+ client.getRemoteSocketAddress());

			if (schanel == null || !schanel.isOpen() || !client.isConnected()
					|| client.isClosed()
					|| client.getRemoteSocketAddress() == null) {
				GprsTcpThreadList.getInstance().remove2(this.getDeviceSN());
				return 1;
			}

			schanel.write(bf);
			ret = StructionResult.SEND_SUCCESS;
			log.info(getDeviceSN() + "通过TCP发送数据到终端："
					+ Tools.bytesToHexString(data));

		} catch (Exception ex) {

			log.error("发送byte数据给终端 出现异常:" + " ", ex);
			ex.printStackTrace();
			ret = StructionResult.SEND_FALIED;
		} finally {
			HashMap<String, String> configMap = AllConfigCache.getInstance()
					.getConfigMap();
			String isstart = configMap.get("isStartFlow");
			if (isstart != null && isstart.equals("1")) {
				String deviceID = getDeviceSN();
				if (deviceID != null) {
					long flowSize = 20 + 20 + data.length; // IP包头长度+TCP包头长度+数据包大小
					FlowVolumeCalculate.flowVolume(deviceID, data, flowSize,
							"1", "0");
				}
			}
		}
		return ret;
	}

	/**
	 * @return the localAddr
	 */
	public String getLocalAddr() {
		return this.localAddr;
	}

	/**
	 * @param localAddr
	 *            the localAddr to set
	 */
	public void setLocalAddr(String localAddr) {
		this.localAddr = localAddr;
	}

}
