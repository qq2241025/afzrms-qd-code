package com.mapabc.gater.lbsgateway;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
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
import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;
import com.mapabc.gater.lbsgateway.bean.TerminalUDPAddress;
import com.mapabc.gater.lbsgateway.gprsserver.udp.GprsSocketChannel;
import com.mapabc.gater.lbsgateway.poolsave.DataPool;
import com.mapabc.gater.lbsgateway.poolsave.GpsData;
import com.mapabc.gater.lbsgateway.service.flowvolume.FlowVolumeCalculate;
import com.mapabc.gater.util.ProtocalSplitUtil;

/*
 *终端解析类,解析GPRS终端上报的GPS数据,解析短信网关上报的GPS数据
 */
public class GPRSThread implements Runnable {

	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(GPRSThread.class);

	public GPRSThread() {
	}

	private byte[] socketBytes;

	private com.mapabc.gater.lbsgateway.gprsserver.udp.GprsSocketChannel gprsSocketChannel;
	private HashMap<String, String> configMap = AllConfigCache.getInstance()
			.getConfigMap();

	private DataPool dataPool;
	private GpsCompPool gpsPool;
	private String trcode = "";;
	String gpsCode = null;

	public static boolean isInterval = false;

	public GPRSThread(DataPool dataPool, GprsSocketChannel gprsSocketChannel,
			byte[] socketBytes, GpsCompPool gpsPool) {
		this.dataPool = dataPool;
		this.setGprsSocketChannel(gprsSocketChannel);
		this.socketBytes = socketBytes;
		this.gpsPool = gpsPool;
	}

	// 根据上报数据，得到终端类型
	private synchronized String getTerminalCode(String hexContent) {

		trcode = PrepareParse.getInstance().getTerminalCode(hexContent);
		return trcode;
	}

	public synchronized String getTrcode() {

		return this.trcode;
	}

	public synchronized void run() {
		// if (!this.gprsSocketChannel.getSocketChannel().isOpen()) {
		// GprsUdpThreadList.getInstance().removeUDP(
		// this.gprsSocketChannel.getParseBase().getDeviceSN());
		// return;
		// }
		String line = null;
		String hexGpsData = null;
		try {
			line = new String(socketBytes, "ISO8859-1");
			hexGpsData = Tools.bytesToHexString(socketBytes);
			// log.debug("接收数据:" + hexGpsData);

			if (line.trim().length() == 0) {
				return;
			}
			if (line.startsWith("autonaviSM")) { // 接收短信网关转发过来的数据
				// line = line.trim();
				// String simcard = ParseUtility.getSimcard(line);
				// String smContent = ParseUtility.getSMContent(line);
				// log.debug(
				// "解析短信网关转换数据:sim=" + simcard + " content=" + smContent);
				// parseSMData(simcard, smContent);
			} else // 接收GPRS终端的数据
			{
				// if (this.gprsSocketChannel.getParseBase() == null) {
				// gpsCode=getTerminalCode(hexGpsData);
				String terminalDesc = getTerminalCode(hexGpsData);
				if (log.isDebugEnabled())
				log.debug("Desc:" + terminalDesc);

				if (terminalDesc == null || terminalDesc.trim().length() == 0) {
					if (log.isDebugEnabled())
					log.debug(
							"接收未知协议类型数据:" + hexGpsData + " 关闭该连接.");
					// 出现未知协议时服务down掉 sjw
					// this.gprsSocketChannel.getSocketChannel().socket()
					// .close();
					// this.gprsSocketChannel.getSocketChannel().close();
					// this.gprsSocketChannel.getClientChannel().socket()
					// .close();
					// this.gprsSocketChannel.getClientChannel().close();
					Counter.setDbIVCount();
					return;
				}
				String[] tmpsp = terminalDesc.split("#");
				gpsCode = tmpsp[0];
				if (!tmpsp[1].equalsIgnoreCase("null"))
					this.gprsSocketChannel.setHexStart(tmpsp[1]);
				if (!tmpsp[2].equalsIgnoreCase("null"))
					this.gprsSocketChannel.setHexEnd(tmpsp[2]);
				if (gpsCode == null || gpsCode.trim().length() == 0) {
					// //出现未知协议时服务down掉 sjw
					// this.gprsSocketChannel.getSocketChannel().socket()
					// .close();
					// this.gprsSocketChannel.getSocketChannel().close();
					// this.gprsSocketChannel.getClientChannel().socket()
					// .close();
					// this.gprsSocketChannel.getClientChannel().close();
					if (log.isDebugEnabled())
					log.debug(
							"接收未知协议类型数据:" + hexGpsData + " 关闭该连接.");
					Counter.setDbIVCount();
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
					// ParseBase pb =
					// parseService.parseModata(this.socketBytes);

					if (parseService == null) {
						if (log.isDebugEnabled())
						log.debug("创建解析类: " + gpsCode + " 失败");
						return ;

					}
					this.gprsSocketChannel.setParseService(parseService);

				}
				// }
				if (log.isDebugEnabled())
				log.debug(
						"接收 " + gpsCode + " 终端数据:" + hexGpsData);
				// 把终端数据进行切分,分成终端解析协议类能够识别的数据
				// String[] dataLines = this.gprsSocketChannel
				// .getSocketLines(hexGpsData);
				String[] dataLines = ProtocalSplitUtil.getSocketLines(
						hexGpsData, this.gprsSocketChannel.getHexStart(),
						this.gprsSocketChannel.getHexEnd());

				if (dataLines == null) {
					Counter.setDbIVCount();
					log.error("协议分割错误", null);
					return;
				}

//				try {
//					// 代理转发服务
//					ForwardServer.forward(this.socketBytes,
//							this.gprsSocketChannel.getParseBase());
//				} catch (Exception e) {
//				}

				for (int i = 0; i < dataLines.length; i++) {
					String tmpline = dataLines[i];
					byte[] moBytes = Tools.fromHexString(tmpline);

					ArrayList<ParseBase> pblist = this.gprsSocketChannel
							.getParseService().parseModata(moBytes);
					if (pblist == null || pblist.size() <= 0) {
						// 单条信息
						parseSingleGprs(moBytes);
					} else {
						parseBatchGprs(pblist, gpsCode);
					}
				}
			}
		} catch (Exception ex) {
			String errinfo = "GPRSThread解析数据出现异常:终端类型为 " + gpsCode + " ";

			errinfo += "数据为 " + hexGpsData;
			errinfo += ex.getMessage(); 
			log.error(errinfo, ex);

		} finally {
			try {
				Counter.setDCount();
				Counter.setNCount();
				Counter.setTime();

				String isstart = configMap.get("isStartFlow");
				if (isstart != null && isstart.equals("1")) {
					// 八局老版数据库，结构不一样，不进行更新
					if (this.gprsSocketChannel != null) {
						ParseBase pb = this.gprsSocketChannel.getParseBase();
						if (pb != null) {
							String deviceID = pb.getDeviceSN();
							String locateType = pb.getLocateType();
							if (deviceID != null && locateType.equals("1")) {// 针对GPS计算流量
								long flowSize = 20 + 8 + this.socketBytes.length; // IP包头长度+UDP包头长度+数据包大小

								FlowVolumeCalculate.flowVolume(deviceID,
										socketBytes, flowSize, "0", "1");
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

	public void sentData(String data) {
		byte[] bs = null;
		try {
			bs = data.getBytes("ISO8859-1");
		} catch (UnsupportedEncodingException ex) {
		}
		if (bs != null) {
			this.sendUDPByteData(bs);
		}
	}

	// UDP下发byte[]数据,会被多个地方调用，加同步
	public synchronized String sendUDPByteData(byte[] data) {
		if (data == null || data.length == 0) {
			return "error";
		}
		String ret = "error";
		try {
			ByteBuffer bf = ByteBuffer.allocate(data.length);
			bf.clear();
			bf.put(data);
			bf.flip();

			SocketAddress clientAddress = this.gprsSocketChannel
					.getClientAddress();// 客户端地址
			DatagramChannel channel = this.gprsSocketChannel.getClientChannel();
			if (channel != null && channel.isOpen()) {
				int lent = channel.send(bf, clientAddress); // 发送到客户端
				if (log.isDebugEnabled())
				log.debug(
						this.gprsSocketChannel.getParseBase().getDeviceSN()
								+ " 通过UDP发送数据到终端,地址：" + clientAddress
								+ "，HEX内容：" + Tools.bytesToHexString(data));
				ret = "ok";
				String isstart = configMap.get("isStartFlow");
				if (isstart != null && isstart.equals("1")) {
					if (this.gprsSocketChannel != null) {
						ParseBase pb = this.gprsSocketChannel.getParseBase();
						if (pb != null) {
							String deviceID = pb.getDeviceSN();
							String locateType = pb.getLocateType();
							if (deviceID != null) {
								long flowSize = 20 + 8 + data.length; // IP包头长度+UDP包头长度+数据包大小
								FlowVolumeCalculate.flowVolume(deviceID, data,
										flowSize, "1", "1");
							}
						}
					}
				}
			}
		} catch (Exception ex) {
			log.error(
					"发送byte数据给终端 出现异常:" + " " + Tools.bytesToHexString(data), ex);
		} finally {

		}
		return ret;
	}

 

	public boolean isInterval() {
		return isInterval;
	}

	public void setInterval(boolean isInterval) {
		this.isInterval = isInterval;
	}

	public synchronized GprsSocketChannel getGprsSocketChannel() {
		return gprsSocketChannel;
	}

	public synchronized void setGprsSocketChannel(
			GprsSocketChannel gprsSocketChannel) {
		this.gprsSocketChannel = gprsSocketChannel;
	}

	// 单条数据信息
	private void parseSingleGprs(byte[] moBytes) throws Exception {
		ParseBase singlePb = this.gprsSocketChannel.getParseService()
				.parseSingleGprs(moBytes);
		if (singlePb == null){
			return;
		}
		
		this.gprsSocketChannel.setParseBase(singlePb);

		ArrayList<ParseBase> pblist = singlePb.getParseList();
		String locateStatus = null;// singlePb.getLocateStatus();

		AbstractTTermStatusRecord termStatus = singlePb.getStatusRecord();
		if (termStatus != null)
			locateStatus = termStatus.getLocate();
  
		GpsData gpsdata = ParseConfigParamUtil.handleConfig(singlePb);
		 
		// 把改GPS连接放入GPS列表
		if (singlePb != null) {

			TerminalUDPAddress udpbean = new TerminalUDPAddress();
			Date date = new Date();
			udpbean.setDeviceSN(singlePb.getDeviceSN());
			udpbean.setDatagramChannel(this.getGprsSocketChannel()
					.getSocketChannel());
			udpbean.setSocketAddress(this.gprsSocketChannel.getClientAddress());
			udpbean.setDate(date);

			GprsUdpThreadList.getInstance()
					.add(singlePb.getDeviceSN(), udpbean);

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
			log.error(gpsdata.toString()+" :isIsPost is false");
			Counter.setDbIVCount();
		}

		byte[] repB = this.gprsSocketChannel.getParseBase().getReplyByte();
		byte[] repB1 = this.gprsSocketChannel.getParseBase().getReplyByte1();
		byte[] repB2 = this.gprsSocketChannel.getParseBase().getReplyByte2();

		if (repB != null && repB.length > 0) {
			this.sendUDPByteData(repB);
			this.gprsSocketChannel.getParseBase().setReplyByte(null);
		}
		if (repB1 != null && repB1.length > 0) {
			this.sendUDPByteData(repB1);
			this.gprsSocketChannel.getParseBase().setReplyByte1(null);
		}
		if (repB2 != null && repB2.length > 0) {
			this.sendUDPByteData(repB2);
			this.gprsSocketChannel.getParseBase().setReplyByte2(null);
		}

	}

	private void parseBatchGprs(ArrayList<ParseBase> pblist, String gpsCode)
			throws Exception {
		// 批量信息
		for (ParseBase pb : pblist) { 
			this.gprsSocketChannel.setParseBase(pb);
			this.gprsSocketChannel.setTrcode(gpsCode);

			GpsData gpsdata = ParseConfigParamUtil.handleConfig(pb);
			  
			// 把改GPS连接放入GPS列表
			if (pb != null) {

				TerminalUDPAddress udpbean = new TerminalUDPAddress();
				Date date = new Date();
				udpbean.setDeviceSN(pb.getDeviceSN());
				udpbean.setDatagramChannel(this.getGprsSocketChannel()
						.getSocketChannel());
				udpbean.setSocketAddress(this.gprsSocketChannel
						.getClientAddress());
				udpbean.setDate(date);

				GprsUdpThreadList.getInstance().add(pb.getDeviceSN(), udpbean);

			} else {
				if (log.isDebugEnabled())
				log.debug(
						"终端类型为:" + this.gprsSocketChannel.getTrcode()
								+ " 的解析类,没有解析到设备ID,错误!!!!!!!!!!!!!!!"
								+ " 终端数据:"
								+ Tools.bytesToHexString(this.socketBytes));
			}

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
				log.error("tmp.isIsPost is false");
				Counter.setDbIVCount();
			}

			byte[] repB = this.gprsSocketChannel.getParseBase().getReplyByte();
			byte[] repB1 = this.gprsSocketChannel.getParseBase()
					.getReplyByte1();
			byte[] repB2 = this.gprsSocketChannel.getParseBase()
					.getReplyByte2();

			if (repB != null && repB.length > 0) {
				this.sendUDPByteData(repB);
				this.gprsSocketChannel.getParseBase().setReplyByte(null);
			}
			if (repB1 != null && repB1.length > 0) {
				this.sendUDPByteData(repB1);
				this.gprsSocketChannel.getParseBase().setReplyByte1(null);
			}
			if (repB2 != null && repB2.length > 0) {
				this.sendUDPByteData(repB2);
				this.gprsSocketChannel.getParseBase().setReplyByte2(null);
			}

		}
	}
}
