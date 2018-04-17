package com.mapabc.gater.lbsgateway.bean;

import java.io.Serializable;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.constant.StructionResult;
import com.mapabc.gater.lbsgateway.GprsUdpThreadList;
import com.mapabc.gater.lbsgateway.service.flowvolume.FlowVolumeCalculate;

public class TerminalUDPAddress implements Serializable {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(TerminalUDPAddress.class);

	private String deviceSN;

	private transient SocketAddress socketAddress;

	private transient DatagramChannel datagramChannel;

	private Date date;

	private boolean isSend = true;

	private String localAddress;

	/**
	 * @return the localAddress
	 */
	public String getLocalAddress() {
		return this.localAddress;
	}

	/**
	 * @param localAddress
	 *            the localAddress to set
	 */
	public void setLocalAddress(String localAddress) {
		this.localAddress = localAddress;
	}

	/**
	 * @return the isSend
	 */
	public boolean isSend() {
		return this.isSend;
	}

	/**
	 * @param isSend
	 *            the isSend to set
	 */
	public void setSend(boolean isSend) {
		this.isSend = isSend;
	}

	public Date getDate() {
		return date;
	}

	public synchronized void setDate(Date date) {
		this.date = date;
	}

	public DatagramChannel getDatagramChannel() {
		return datagramChannel;
	}

	public void setDatagramChannel(DatagramChannel datagramChannel) {
		this.datagramChannel = datagramChannel;
	}

	public SocketAddress getSocketAddress() {
		return socketAddress;
	}

	public void setSocketAddress(SocketAddress socketAddress) {
		this.socketAddress = socketAddress;
	}

	public String getDeviceSN() {
		return deviceSN;
	}

	public void setDeviceSN(String deviceSN) {
		this.deviceSN = deviceSN;
	}

	/**
	 * 通过UDP通道发送内容到终端
	 * 
	 * @param data
	 * @return
	 * @author 
	 */
	public synchronized int sendUDPByteData(byte[] data) {
		int ret = -1;

		if (data == null || data.length == 0) {
			return StructionResult.CMD_INVALID;
		}

		try {
			ByteBuffer bf = ByteBuffer.allocate(data.length);
			bf.clear();
			bf.put(data);
			bf.flip();

			SocketAddress clientAddress = this.getSocketAddress();// 客户端地址
			DatagramChannel channel = this.getDatagramChannel();
			DatagramSocket client = channel.socket();
			log.info(this.getDeviceSN() + "reply udpSocket isclosed:"
					+ client.isClosed() + ",isConnected="
					+ client.isConnected() + ",addr=" + clientAddress
					+ ",DatagramChannel is Open:" + channel.isOpen());
			 
			if (clientAddress == null || channel == null || !channel.isOpen()
					|| client.isClosed()) {
				GprsUdpThreadList.getInstance().removeUDP(this.getDeviceSN());
				return 1;
			}

			int lent = channel.send(bf, clientAddress); // 发送到客户端
			log.info(this.getDeviceSN() + "======通过UDP发送数据到终端,地址："
					+ clientAddress + "，内容：" + Tools.bytesToHexString(data));
			ret = StructionResult.SEND_SUCCESS;

		} catch (Exception ex) {
			log.error(this.getDeviceSN() + "=======发送byte数据给终端 出现异常:" + " "
					+ Tools.bytesToHexString(data), ex);
			ret = StructionResult.SEND_FALIED;
		} finally {
			HashMap<String, String> configMap = AllConfigCache.getInstance()
					.getConfigMap();
			String isstart = configMap.get("isStartFlow");
			if (isstart != null && isstart.equals("1")) {
				FlowVolumeCalculate.flowVolume(this.getDeviceSN(), data,
						20 + 8 + data.length, "1", "1");
			}
		}
		return ret;
	}

}
