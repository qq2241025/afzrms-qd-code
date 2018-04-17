package com.mapabc.gater.lbsgateway.gprsserver.tcp;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.lbsgateway.GprsTcpThreadList;

/**
 * GprsSocketChannel:连接到网关的GPRS终端类 注意该类可能同时被多个线程使用 所以该类所有的方法必须加锁.
 */
public class GprsSocketTcpChannel {
	
	private static org.apache.commons.logging.Log log=LogFactory.getLog(GprsSocketTcpChannel.class);

	
	private SocketChannel socketChannel;

	private com.mapabc.gater.directl.parse.ParseBase parseBase;// 终端解析类
	
	private ParseService parseService;//解析接口

	private String trcode;// 终端类型

	private String hexStart;// 包头标识，标识头标识符号,可以有多个标识符,中间用逗号隔开。

	private String hexEnd;// 包尾标识,标识尾标识符号,目前只支持一个标识符

	public GprsSocketTcpChannel(SocketChannel socketChannel) {
		this.setSocketChannel(socketChannel);
		// log.info("===============================创建一个GprsSocketTcpChannel对象");

	}

	public GprsSocketTcpChannel() {
	}

	public synchronized SocketChannel getSocketChannel() {
		return socketChannel;
	}

	private synchronized void setSocketChannel(SocketChannel socketChannel) {
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
		SocketChannel socket = this.getSocketChannel();
		if (socket == null || !socket.isConnected() || !socket.isOpen() ) {
			try {
				
				socket.socket().close();
				socket.close();
				key.cancel();
				
				if (this.getParseBase() != null) {
					GprsTcpThreadList.getInstance().remove2(
							this.getParseBase().getDeviceSN());
				}
			} catch (IOException ex) {
			}
		 
		return null;
	}
		byte[] ret = null;
		boolean bRead = true;
		while (bRead) {
			try {

				ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
				byteBuffer.clear();
				int nbytes = socket.read(byteBuffer);
				if (nbytes == -1) {
					bRead = false;
					socket.socket().close();
					socket.close();
					key.cancel();
					

					// Log.getInstance().outLog("====Delete
					// "+this.getDeviceID()+" read=-1");
					if (this.getParseBase() != null) {
						GprsTcpThreadList.getInstance().remove2(
								this.getParseBase().getDeviceSN());
						log.error(
								this.getParseBase().getDeviceSN()
										+ "读取终端数据失败,终端 关闭连接,read=-1", null);

					}
					return null;
				}
				if (nbytes > 0) {
					byteBuffer.flip();
					byte[] tmp = new byte[byteBuffer.limit()];
					byteBuffer.get(tmp, 0, byteBuffer.limit());
					if (ret == null) {
						ret = new byte[tmp.length];
						System.arraycopy(tmp, 0, ret, 0, tmp.length);
					} else {
						byte[] tmpret = new byte[ret.length];
						System.arraycopy(ret, 0, tmpret, 0, ret.length);
						ret = new byte[ret.length + tmp.length];
						System.arraycopy(tmpret, 0, ret, 0, tmpret.length);
						System
								.arraycopy(tmp, 0, ret, tmpret.length,
										tmp.length);
					}
				}

				if (nbytes < 256) {
					return ret;
				}
			} catch (Exception e) {
				bRead = false;
				log.error("读取终端数据失败:" + e.getMessage(), e);

				// Log.getInstance().outLog(e.getMessage() + "====Delete "+
				// this.getParseBase().getDeviceSN());
				
				try {
					socket.socket().close();
					socket.close();
					key.cancel();
					if (this.getParseBase() != null) {
						GprsTcpThreadList.getInstance().remove2(
								this.getParseBase().getDeviceSN());
					}
				} catch (IOException ex) {
				}

				break;
			}
		}
		 
		return ret;
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

	// 发送byte[]数据
	public synchronized String sendByteArrayData(byte[] data) {
		if (data == null || data.length == 0) {
			return "error";
		}
		String ret = "error";
		try {
			ByteBuffer bf = ByteBuffer.wrap(data);
			this.getSocketChannel().write(bf);
			ret = "ok";
		} catch (Exception ex) {
			log.info("发送byte数据给终端 出现异常:" + ex.getMessage());
		}
		return ret;
	}

	public static void main(String[] args) {

	}

	/**
	 * @return the parseService
	 */
	public synchronized  ParseService getParseService() {
		return this.parseService;
	}

	/**
	 * @param parseService the parseService to set
	 */
	public synchronized void setParseService(ParseService parseService) {
		this.parseService = parseService;
	}

}
