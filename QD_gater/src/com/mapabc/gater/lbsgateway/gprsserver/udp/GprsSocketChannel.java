package com.mapabc.gater.lbsgateway.gprsserver.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SocketChannel;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.charset.Charset;
import java.nio.CharBuffer;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.counter.Counter;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService;

import com.mapabc.gater.lbsgateway.GprsUdpThreadList;
import com.mapabc.gater.lbsgateway.gprsserver.tcp.GprsSocketTcpChannel;

/**
 * GprsSocketChannel:连接到网关的GPRS终端类 注意该类可能同时被多个线程使用 所以该类所有的方法必须加锁.
 */
public class GprsSocketChannel {

	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(GprsSocketChannel.class);

	private DatagramChannel socketChannel;

	private DatagramChannel clientChannel;

	private SocketAddress clientAddress; // 发送方地址

	private com.mapabc.gater.directl.parse.ParseBase parseBase;// 终端解析类

	private ParseService parseService;// 解析接口

	private String trcode;// 终端类型

	private String hexStart;// 包头标识，标识头标识符号,可以有多个标识符,中间用逗号隔开。

	private String hexEnd;// 包尾标识,标识尾标识符号,目前只支持一个标识符

	public GprsSocketChannel(DatagramChannel socketChannel) {
		// Log
		// .getInstance()
		// .outLog(
		// "------------------------------------------创建一个
		// GprsSocketChannel对象");
		this.setSocketChannel(socketChannel);
	}

	public synchronized DatagramChannel getSocketChannel() {
		return socketChannel;
	}

	private synchronized void setSocketChannel(DatagramChannel socketChannel) {
		this.socketChannel = socketChannel;
	}

	public synchronized String getTrcode() {
		return trcode;
	}

	public synchronized void setTrcode(String trcode) {
		this.trcode = trcode;
	}

	public synchronized String getHexStart() {
		return hexStart;
	}

	public synchronized void setHexStart(String hexstart) {
		this.hexStart = hexstart;
	}

	public synchronized String getHexEnd() {
		return hexEnd;
	}

	public synchronized void setHexEnd(String hexend) {
		this.hexEnd = hexend;
	}

	public synchronized ParseBase getParseBase() {
		return parseBase;
	}

	public synchronized void setParseBase(ParseBase parseBase) {
		this.parseBase = parseBase;
	}

	// 从Socket中读出数据流
	public synchronized byte[] readSocketBytes(SelectionKey key) {

		DatagramChannel schanel = this.socketChannel;

		byte[] ret = null;
		boolean bRead = true;
		String validHex = "";// 缓存中有效内容

		try {
			// 对于大数据，如图片需要扩充缓冲量
			ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
			byteBuffer.clear();
			clientAddress = (InetSocketAddress) schanel.receive(byteBuffer);
			int leng = byteBuffer.position(); // 有效数据长度
			byteBuffer.flip();

			byte[] bcont = byteBuffer.array(); // 缓冲数据
			byte[] validData = new byte[leng]; // 有效数据
			System.arraycopy(bcont, 0, validData, 0, leng);

			// String hex = Tools.bytesToHexString(validData);//
			// 把byteBuffer转换成16进制

			this.setClientChannel(schanel);
			this.setClientAddress(clientAddress);
			if (validData != null && leng > 0) {

				String s = new String(validData);
				if (s.equals("default send string")) {
					log.debug("莫名奇妙的数据："+s);
					return null;
				}

				if (log.isDebugEnabled()) {
					String hex = Tools.bytesToHexString(validData);
					log.debug("中心收到UDP原始数据报内容：" + hex);
				}

				Counter.setTCount();
			}

			return validData;

			// byteBuffer.array();//
			// Tools.fromHexString(validHex);

		} catch (Exception e) {
			bRead = false;
			log.error("读取终端数据失败，连接已断开:" + e.getMessage(), e);
			// key.cancel();
			e.printStackTrace();
			// try {
			// //schanel.socket().close();
			// //schanel.close();
			//
			// } catch (IOException ex) {
			// }
			// if (this.getParseBase() != null) {
			// GprsUdpThreadList.getInstance().removeUDP(
			// this.getParseBase().getDeviceSN());
			// }

		} 

		return null;
	}

	// 把接收到的数据按照协议的标识头尾来进行切分,分成多行.
	// 注意：禁止标识尾有多个标识符号!
	public synchronized String[] getSocketLines(String hex) {
		String[] ret = null;
		try {
			String tmpstart = this.getHexStart();
			String tmpend = this.getHexEnd();
			if (tmpstart != null && tmpstart.split(",").length > 1) {
				String[] tmplines = tmpstart.split(",");
				for (int i = 0; i < tmplines.length; i++) {
					String tmpline = tmplines[i];

					if (tmpline == null || tmpline.trim().length() == 0)
						continue;
					ret = getsplitLines(hex, tmpline, tmpend);
					// log.info("切分指令："+ret+"tmpline:"+tmpline);
					if (ret != null)
						break;
				}
			} else {
				ret = getsplitLines(hex, tmpstart, tmpend);
			}
			// log.info("分割成多行数据成功!一共:" + ret.length + "行数据.");
		} catch (Exception ex) {
			String errinfo = "分割数据出现错误:" + ex.getMessage();
			errinfo += "数据:" + hex + "标识头:" + this.getHexStart() + "标识尾:"
					+ this.getHexEnd();
			log.info(errinfo);
			log.error(errinfo, ex);
			String[] shytdata = new String[1];
			shytdata[0] = hex;
			return shytdata;
		}
		return ret;
	}

	// 切分函数
	private synchronized String[] getsplitLines(String hex, String hexstart,
			String hexend) {
		if (hex == null || (hex.length() % 2 != 0))
			return null;
		if (hexstart == null && hexend == null)
			return null;
		if (hexstart.trim().length() <= 0 && hexend.trim().length() <= 0)
			return null;

		String cData = addSpaceToHexstring(hex);
		String cStartData = addSpaceToHexstring(hexstart);
		String cEndData = addSpaceToHexstring(hexend);
		String[] rettmp = null;
		String strTmp = "";
		int f = -1;
		if (hexstart != null && hexend != null && hexstart.trim().length() > 0
				&& hexend.trim().length() > 0) {
			// 包头包尾,删除包头前和包尾后的数据
			int m = cData.indexOf(cStartData);
			int n = cData.lastIndexOf(cEndData);
			if (m == -1 || n == -1)
				return null;

			cData = cData.substring(m, cData.lastIndexOf(cEndData)
					+ cEndData.length());
			if (hexstart.equals("3032") && hexend.equals("23")) {
				rettmp = cData.split(cEndData);
			} else {
				rettmp = cData.split(cStartData);
			}

			f = 0;
		} else if (hexstart != null && hexstart.trim().length() > 0) {
			// 包头,删除包头前的数据
			int m = cData.indexOf(cStartData);
			if (m == -1)
				return null;
			cData = cData.substring(cData.indexOf(cStartData), cData.length());
			rettmp = cData.split(cStartData);
			f = 1;
		} else if (hexend != null && hexend.trim().length() > 0) {
			// 包尾,删除包尾后的数据

			int n = cData.lastIndexOf(cEndData);
			if (n == -1)
				return null;

			cData = cData.substring(0, cData.lastIndexOf(cEndData)
					+ cEndData.length());
			rettmp = cData.split(cEndData);
			f = 2;
		}

		for (int i = 0; i < rettmp.length; i++) {
			String tmp = rettmp[i];

			if (tmp == null || tmp.trim().length() == 0)
				continue;
			if (hexstart.equals("3032") && hexend.equals("23") && f == 0) {
				tmp = tmp + cEndData;
			} else if (f == 0 || f == 1) {
				tmp = cStartData + tmp;
			}

			if (f == 2)
				tmp = tmp + cEndData;
			if (f == 0) {
				if (tmp.startsWith(cStartData) && tmp.endsWith(cEndData)) {
					strTmp += tmp + ",";

				}
			} else if (f == 1) {
				if (tmp.startsWith(cStartData)) {
					strTmp += tmp + ",";
				}
			} else if (f == 2) {
				if (tmp.endsWith(cEndData)) {
					strTmp += tmp + ",";
				}
			}
		}

		if (strTmp.trim().length() <= 0) {
			return null;
		}
		strTmp = strTmp.substring(0, strTmp.length() - 1);
		strTmp = strTmp.replaceAll(" ", "");
		return strTmp.split(",");
	}

	// 把十六进制度的字符串每个字节之间与' ' 隔开。
	private synchronized String addSpaceToHexstring(String hex) {
		if (hex == null || hex.trim().length() == 0 || (hex.length() % 2 != 0)) {
			return null;
		}
		char c = ' ';
		char[] cs = hex.toCharArray();
		CharBuffer cb = java.nio.CharBuffer.wrap(cs);
		int size = cs.length * 2;
		CharBuffer result = CharBuffer.allocate(size);
		for (int i = 0; i < hex.length(); i++) {
			result.put(cb.get(i));
			if (i > 0 && (i % 2 == 1)) {
				result.put(c);
			}
		}
		result.flip();
		return result.toString();
	}

	public static void main(String[] args) {
		GprsSocketChannel gs = new GprsSocketChannel(null);

		ByteBuffer byteBuffer = ByteBuffer.allocate(100);
		byteBuffer.put("hello".getBytes());
		byteBuffer.flip();
		System.out.println(byteBuffer.position());

	}

	public synchronized SocketAddress getClientAddress() {
		return clientAddress;
	}

	public synchronized void setClientAddress(SocketAddress clientAddress) {
		this.clientAddress = clientAddress;
	}

	public synchronized DatagramChannel getClientChannel() {
		return clientChannel;
	}

	public synchronized void setClientChannel(DatagramChannel clientChannel) {
		this.clientChannel = clientChannel;
	}

	/**
	 * @return the parseService
	 */
	public synchronized ParseService getParseService() {
		return this.parseService;
	}

	/**
	 * @param parseService
	 *            the parseService to set
	 */
	public synchronized void setParseService(ParseService parseService) {
		this.parseService = parseService;
	}

}
