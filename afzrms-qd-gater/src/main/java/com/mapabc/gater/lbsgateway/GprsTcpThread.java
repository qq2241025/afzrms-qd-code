package com.mapabc.gater.lbsgateway;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.counter.Counter;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.ParseConfigParamUtil;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.status.AbstractTTermStatusRecord;
import com.mapabc.gater.directl.parse.ParseBase;
import com.mapabc.gater.directl.parse.service.ParseService;
import com.mapabc.gater.gpscom.GpsCompPool;
import com.mapabc.gater.lbsgateway.bean.TerminalTCPAddress;
import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;
import com.mapabc.gater.lbsgateway.gprsserver.ForwardServer;
import com.mapabc.gater.lbsgateway.gprsserver.tcp.GprsSocketTcpChannel;
import com.mapabc.gater.lbsgateway.poolsave.DataPool;
import com.mapabc.gater.lbsgateway.poolsave.GpsData;
import com.mapabc.gater.lbsgateway.service.CommonGatewayServiceImpl;
import com.mapabc.gater.lbsgateway.service.ICommonGatewayService;
import com.mapabc.gater.lbsgateway.service.flowvolume.FlowVolumeCalculate;
import com.mapabc.gater.util.ProtocalSplitUtil;

/*
 *终端解析类,解析GPRS终端上报的GPS数据,解析短信网关上报的GPS数据
 */
public class GprsTcpThread implements Runnable {

	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(GprsTcpThread.class);

	public GprsTcpThread() {
	}

	private byte[] socketBytes;
	private GprsSocketTcpChannel gprsSocketTcpChannel;
	private DataPool dataPool;
	public static boolean isInterval = false;
	private Date date;
	private HashMap<String, String> configMap = AllConfigCache.getInstance()
			.getConfigMap();
	private SocketChannel socketChannel;
	private GpsCompPool gpsPool;
	private Socket socket;

	public GprsTcpThread(DataPool dataPool,
			GprsSocketTcpChannel gprsSocketChannel, byte[] socketBytes,
			SocketChannel socketChannel, GpsCompPool gpsPool) {
		this.dataPool = dataPool;
		this.gprsSocketTcpChannel = gprsSocketChannel;
		this.socketBytes = socketBytes;
		this.date = new Date();
		this.socketChannel = socketChannel;
		this.gpsPool = gpsPool;
		socket = socketChannel.socket();
		// this.socketAddress = socketChannel.socket().getRemoteSocketAddress();

	}

	private String trcode = "";;

	// 根据上报数据，得到终端类型
	private synchronized String getTerminalCode(String hexContent) {

		trcode = PrepareParse.getInstance().getTerminalCode(hexContent);
		return trcode;
	}

	public synchronized String getTrcode() {

		return this.trcode;
	}

	public void run() {
		// if (!this.gprsSocketTcpChannel.getSocketChannel().isOpen()) {
		//    	
		// return;
		// }
		String line = null;
		String hexGpsData = null;
		try {
			hexGpsData = this.bytesToHexString(socketBytes);

			String gpsCode = null;

			{

				String terminalDesc = getTerminalCode(hexGpsData);
				if (log.isDebugEnabled())
					log.debug("Desc:" + terminalDesc);

				if (terminalDesc == null || terminalDesc.trim().length() == 0) {
					Counter.setDbIVCount();
					if (log.isDebugEnabled())
						log.debug("接收未知协议类型数据:" + hexGpsData + " 关闭该连接.");
					// this.gprsSocketTcpChannel.getSocketChannel().socket()
					// .close();
					// this.gprsSocketTcpChannel.getSocketChannel().close();
					return;
				}
				String[] tmpsp = terminalDesc.split("#");
				gpsCode = tmpsp[0];
				String hexStart = null;
				String hexEnd = null;
				if (!tmpsp[1].equalsIgnoreCase("null"))
					hexStart = tmpsp[1];
				if (!tmpsp[2].equalsIgnoreCase("null"))
					hexEnd = tmpsp[2];

				if (gpsCode == null || gpsCode.trim().length() == 0) {
					Counter.setDbIVCount();
					// this.gprsSocketTcpChannel.getSocketChannel().socket()
					// .close();
					// this.gprsSocketTcpChannel.getSocketChannel().close();
					if (log.isDebugEnabled())
						log.debug("接收未知协议类型数据:" + hexGpsData + " 关闭该连接.");
					return;
				} else {
					// 根据终端类型,创建相应的终端解析类
					TerminalTypeBean typeBean = (TerminalTypeBean) TerminalTypeList
							.getInstance().getTerminalType(gpsCode);
					String className = null;
					if (typeBean != null) {
						className = typeBean.getParseClass();
					} else {
						if (log.isDebugEnabled())
							log.debug(gpsCode + " 解析类不存在！");

					}
					ParseService parseService = (ParseService) ParseConfigParamUtil
							.getClassInstance(className);

					if (parseService == null) {
						Counter.setDbIVCount();
						if (log.isDebugEnabled())
							log.debug("创建解析类: " + gpsCode + " 失败");
						return;

					}
					parseService.setSocket(socket);
					this.gprsSocketTcpChannel.setParseService(parseService);

				}

				String[] dataLines = ProtocalSplitUtil.getSocketLines(
						hexGpsData, hexStart, hexEnd);

				if (dataLines == null) {
					Counter.setDbIVCount();
					return;
				}

				for (int i = 0; i < dataLines.length; i++) {
					// parseTest.begin();
					String tmpline = dataLines[i];
					byte[] moByte = Tools.fromHexString(tmpline);
					
					// 解析数据
					// this.gprsSocketTcpChannel.getParseBase().parseGPRS(tmpline);
					ParseService parseService = this.gprsSocketTcpChannel
							.getParseService();

					ArrayList<ParseBase> pblist = parseService
							.parseModata(moByte);

					if (pblist == null || pblist.size() <= 0) {
						// 单条信息
						parseSingleGprs(moByte);
					} else {
						parseBatchGprs(pblist, gpsCode);
						pblist.clear();
					}
				}
			}
		} catch (Exception ex) {
			// this.closeSocket();
			log.error("解析异常:" + hexGpsData, ex);
			ex.printStackTrace();

		} finally {
			// 流量计算
			try {
				Counter.setDCount();
				Counter.setNCount();
				Counter.setTime();

				String isstart = configMap.get("isStartFlow");
				if (isstart != null && isstart.equals("1")) {
					if (this.gprsSocketTcpChannel != null) {
						ParseBase pb = this.gprsSocketTcpChannel.getParseBase();
						if (pb != null) {
							String deviceID = pb.getDeviceSN();
							String locateType = pb.getLocateType();
							if (deviceID != null && locateType.equals("1")) {// 针对GPS计算流量

								long flowSize = 20 + 20 + socketBytes.length; // IP包头长度+TCP包头长度+数据包大小
								FlowVolumeCalculate.flowVolume(deviceID,
										socketBytes, flowSize, "0", "0");
							}
						}
					}
				}
			} catch (Exception e) {
				log.error("", e);
			}
			this.socketBytes = null;
		}

	}

	private synchronized void closeSocket() {
		try {
			GprsSocketTcpChannel gsc = this.getGprsSocketTcpChannel();
			if (null != gsc) {
				SocketChannel sc = this.socketChannel;
				if (null != sc) {
					Socket s = sc.socket();
					if (null != s) {
						s.close();
						log.debug("close socket:" + s.getRemoteSocketAddress());
					}
				}
				sc.close();
			}
		} catch (IOException e) {
			try {
				this.socketChannel.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			log.error("close channel exception", e);
			e.printStackTrace();
		}
	}

	public void sentData(String data) {
		byte[] bs = null;
		try {
			bs = data.getBytes("ISO8859-1");
		} catch (UnsupportedEncodingException ex) {
		}
		if (bs != null) {
			this.sendByteArrayData(bs);
		}
	}

	// 发送byte[]数据,此方法会被多个地方使用
	public synchronized String sendByteArrayData(byte[] data) {
		if (data == null || data.length == 0) {
			return "error";
		}
		String ret = "error";
		try {
//			ByteBuffer bf = ByteBuffer.allocate(data.length);
//			bf.clear();
//			bf.put(data);
//			bf.flip();

			ByteBuffer bf = ByteBuffer.wrap(data);
			
			SocketChannel schanel = this.socketChannel;
			Socket client = schanel.socket();
			log.debug(this.gprsSocketTcpChannel.getParseBase().getDeviceSN()
					+ "reply SOCKET isclosed:" + client.isClosed()
					+ ",isConnected=" + client.isConnected()
					+ ",isInputShutdown=" + client.isInputShutdown()
					+ ",isOutShutdown=" + client.isOutputShutdown() + ",addr="
					+ client.getRemoteSocketAddress());
			if (!client.isConnected() || client.isClosed() || client.getRemoteSocketAddress()==null){
				log.debug(this.gprsSocketTcpChannel.getParseBase().getDeviceSN()+" 连接已关闭："+client.getRemoteSocketAddress());
				return "1";
			}
			if (schanel != null && schanel.isOpen()) {
				log.debug(this.gprsSocketTcpChannel.getParseBase()
						.getDeviceSN()
						+ "=====reply SOCKET isclosed:"
						+ client.isClosed()
						+ ",isConnected="
						+ client.isConnected()
						+ ",isInputShutdown="
						+ client.isInputShutdown()
						+ ",isOutShutdown="
						+ client.isOutputShutdown()
						+ ",addr="
						+ client.getRemoteSocketAddress()
						+ ",socket.isBound=" + socket.isBound());
				 
				int length = 0;
				length = schanel.write(bf);
				boolean isOver = false;
				while (length < data.length) {
					isOver = true;
					int len = schanel.write(bf);
					if (len != 0) {
						length += len;
						if (log.isDebugEnabled())
							log.debug(this.gprsSocketTcpChannel.getParseBase()
									.getDeviceSN()
									+ "通过TCP发送数据到终端length：" + length);

					}
				}

				if (!isOver) {
					if (log.isDebugEnabled())
						log.debug(this.gprsSocketTcpChannel.getParseBase()
								.getDeviceSN()
								+ "通过TCP发送数据到终端："
								+ Tools.bytesToHexString(data));
				}
				ret = "ok";
				String isstart = configMap.get("isStartFlow");
				if (isstart != null && isstart.equals("1")) {
					if (this.gprsSocketTcpChannel != null) {
						ParseBase pb = this.gprsSocketTcpChannel.getParseBase();
						if (pb != null) {
							String deviceID = pb.getDeviceSN();
							String locateType = pb.getLocateType();
							if (deviceID != null) {
								long flowSize = 20 + 20 + data.length; // IP包头长度+TCP包头长度+数据包大小
								FlowVolumeCalculate.flowVolume(deviceID, data,
										flowSize, "1", "0");
							}
						}
					}
				}
			}
		} catch (Exception ex) {

			log.error("发送byte数据给终端 出现异常:" + " " + bytesToHexString(data), ex);
			ex.printStackTrace();
		} finally {

		}
		return ret;
	}

	// 把byte数组转换成16进制字符
	private String bytesToHexString(byte[] bs) {
		String s = "";
		for (int i = 0; i < bs.length; i++) {
			String tmp = Integer.toHexString(bs[i] & 0xff);
			if (tmp.length() < 2) {
				tmp = "0" + tmp;
			}
			s = s + tmp;
		}
		return s;
	}

	public boolean isInterval() {
		return isInterval;
	}

	public void setInterval(boolean isInterval) {
		this.isInterval = isInterval;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public synchronized GprsSocketTcpChannel getGprsSocketTcpChannel() {
		return gprsSocketTcpChannel;
	}

	public synchronized void setGprsSocketTcpChannel(
			GprsSocketTcpChannel gprsSocketTcpChannel) {
		this.gprsSocketTcpChannel = gprsSocketTcpChannel;
	}

	// 单条数据信息
	private void parseSingleGprs(byte[] moBytes) throws Exception {
		ParseBase singlePb = this.getGprsSocketTcpChannel().getParseService()
				.parseSingleGprs(moBytes);
		if (singlePb == null){
			return;
		}
		try {
			// 代理转发服务
			ForwardServer.forward(moBytes, singlePb);
		} catch (Exception e) {
		}

		try {
			if ( singlePb.getDeviceSN() == null) {
				ICommonGatewayService service = new CommonGatewayServiceImpl();
				String deviceId = service.getDeviceIdByTcpAddress(socket);
				if (deviceId != null) {
					singlePb.setDeviceSN(deviceId);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		singlePb.setSocket(socket);
		this.gprsSocketTcpChannel.setParseBase(singlePb);

		ArrayList<ParseBase> pblist = singlePb.getParseList();
		String locateStatus = null;// singlePb.getLocateStatus();

		AbstractTTermStatusRecord termStatus = singlePb.getStatusRecord();
		if (termStatus != null)
			locateStatus = termStatus.getLocate();

		GpsData gpsdata = ParseConfigParamUtil.handleConfig(singlePb);

		// 把改GPS连接放入GPS列表
		if (this.gprsSocketTcpChannel.getParseBase().getDeviceSN() != null) {

			TerminalTCPAddress tcpbean = new TerminalTCPAddress();
			Date date = new Date();
			tcpbean.setDeviceSN(this.gprsSocketTcpChannel.getParseBase()
					.getDeviceSN());

			tcpbean.setSocketChannel(this.socketChannel);
			tcpbean.setDate(date);

			GprsTcpThreadList.getInstance().add(
					this.gprsSocketTcpChannel.getParseBase().getDeviceSN(),
					tcpbean);

		}

		// 把需要入库的数据放入数据池
		if (gpsdata != null && gpsdata.isPost()
				&& gpsdata.getDEVICE_ID() != null) {

			if (null != locateStatus && locateStatus.equals("2")) {// 补偿数据

				if (pblist.size() > 0) {
					for (int k = 0; k < pblist.size(); k++) {
						// 添加到数据池
						this.gpsPool.addGpsCompensate(gpsdata);
						log.error("addGpsCompensate");
						Counter.setDbIVCount();
					}
					singlePb.getParseList().clear();
				} else {
					this.gpsPool.addGpsCompensate(gpsdata);
					log.error("addGpsCompensate");
					Counter.setDbIVCount();
				}

			} else {// 定位、未定位状态数据
				this.dataPool.add(gpsdata); // 添加到数据池
			}
		} else {
 			Counter.setDbIVCount();
		}

		byte[] repB = this.getGprsSocketTcpChannel().getParseBase()
				.getReplyByte();
		byte[] repB1 = this.getGprsSocketTcpChannel().getParseBase()
				.getReplyByte1();
		byte[] repB2 = this.getGprsSocketTcpChannel().getParseBase()
				.getReplyByte2();

		if (repB != null && repB.length > 0) {
			this.sendByteArrayData(repB);
			this.getGprsSocketTcpChannel().getParseBase().setReplyByte(null);
		}
		if (repB1 != null && repB1.length > 0) {
			this.sendByteArrayData(repB1);
			this.getGprsSocketTcpChannel().getParseBase().setReplyByte1(null);
		}
		if (repB2 != null && repB2.length > 0) {
			this.sendByteArrayData(repB2);
			this.getGprsSocketTcpChannel().getParseBase().setReplyByte2(null);
		}

	}

	// 批量信息
	private void parseBatchGprs(ArrayList<ParseBase> pblist, String gpsCode)
			throws Exception {
		for (ParseBase pb : pblist) {

			try {
				if (pb.getDeviceSN() == null) {
					ICommonGatewayService service = new CommonGatewayServiceImpl();
					String deviceId = service.getDeviceIdByTcpAddress(socket);
					if (deviceId != null) {
						pb.setDeviceSN(deviceId);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				// 代理转发服务
				ForwardServer.forward(socketBytes, pb);
			} catch (Exception e) {
			}

			pb.setSocket(socket);

			this.gprsSocketTcpChannel.setParseBase(pb);
			// this.gprsSocketTcpChannel.setTrcode(gpsCode);

			GpsData gpsdata = ParseConfigParamUtil.handleConfig(pb);
			// System.out.println("leng:" + pblist.size() + " "
			// + gpsdata.toString());

			// 把需要入库的数据放入数据池
			if (gpsdata != null && gpsdata.isPost()
					&& gpsdata.getDEVICE_ID() != null) {

				String locateStatus = null;
				AbstractTTermStatusRecord termStatus = gpsdata
						.getStatusRecord();
				if (termStatus != null)
					locateStatus = termStatus.getLocate();

				if (null != locateStatus && locateStatus.equals("2")) {// 补偿数据
					this.gpsPool.addGpsCompensate(gpsdata);
					log.error("addGpsCompensate");
					Counter.setDbIVCount();
				} else {// 定位、未定位状态数据
					this.dataPool.add(gpsdata); // 添加到数据池
				}

			} else {
				Counter.setDbIVCount();
			}

			byte[] repB = pb.getReplyByte();
			byte[] repB1 = pb.getReplyByte1();
			byte[] repB2 = pb.getReplyByte2();

			if (repB != null && repB.length > 0) {
				this.sendByteArrayData(repB);
				pb.setReplyByte(null);
			}
			if (repB1 != null && repB1.length > 0) {
				this.sendByteArrayData(repB1);
				pb.setReplyByte1(null);
			}
			if (repB2 != null && repB2.length > 0) {
				this.sendByteArrayData(repB2);
				pb.setReplyByte1(null);
			}

			// 把改GPS连接放入GPS列表
			if (pb.getDeviceSN() != null) {

				TerminalTCPAddress tcpbean = new TerminalTCPAddress();
				Date date = new Date();
				tcpbean.setDeviceSN(pb.getDeviceSN());

				tcpbean.setSocketChannel(this.socketChannel);
				tcpbean.setDate(date);

				GprsTcpThreadList.getInstance().add(pb.getDeviceSN(), tcpbean);

			}

			// else if (this.gprsSocketTcpChannel.getParseBase().getUserId() !=
			// null) {
			// GprsTcpThreadCsList.getInstance().add(
			// this.gprsSocketTcpChannel.getParseBase().getUserId(),
			// this);
			// }
		}
	}

}
