package com.mapabc.gater.lbsgateway.gprsserver.udp;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.counter.Counter;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.gpscom.GpsCompPool;
import com.mapabc.gater.lbsgateway.GPRSThread;
import com.mapabc.gater.lbsgateway.gprsserver.StartServer;
import com.mapabc.gater.lbsgateway.gprsserver.ThreadPool;
import com.mapabc.gater.lbsgateway.poolsave.DataPool;
import com.mapabc.gater.lbsgateway.poolsave.ReadPool;
import com.mapabc.gater.lbsgateway.service.licence.LicenceValidator;

/**
 * 位置网关终端服务.用来接收终端上报的数据,并且进行相应的处理.
 */

public class GprsServer extends StartServer {
	
	private static org.apache.commons.logging.Log log=LogFactory.getLog(GprsServer.class);

	final Selector selector;
	final DatagramChannel serverSocketChannel;
	private int port;
	private ThreadPool pool;
	private ReadPool parsePool;
	private Logger logger;
	private LicenceValidator licenceValidator;
	private DataPool dataPool ;
	private GpsCompPool gpsPool ;
	private SocketAddress clientAddress = null;
	private static long keyCount;
	private static long dataCount;
	private int receiveCacheSize=2048;//接收缓存大小
	/**
	 * 
	 * @param port
	 *            int:端口号
	 * @param parsethreadCount
	 *            int:解析线程池线程个数
	 * @param savethreadCount
	 *            int:入库线程池线程个数
	 * @param log
	 *            Logger: 日志类
	 * @throws IOException
	 */
	public GprsServer(int port, int parsethreadCount, int savethreadCount,
			int readConnThd, Logger log1) throws IOException {
		{
			log.info("启动UDP通讯服务"); 
			// 构造一个GPRS数据解析线程池
			pool = new ThreadPool(parsethreadCount);
			pool.startPool();
			// 构造一个数据入库线程池
			  dataPool = new DataPool(savethreadCount);
			dataPool.startPool();
			// GPS补偿数据池
			  gpsPool = new GpsCompPool(1);
			gpsPool.startPool();

			// 读取池
			parsePool = new ReadPool(readConnThd, dataPool, pool, gpsPool);
			parsePool.startPool();
			// 创建一个新的selector
			selector = Selector.open();
			// 创建一个新的serverSocketChannel
			serverSocketChannel = DatagramChannel.open();

			// 绑定本地端口
			DatagramSocket sock = serverSocketChannel.socket();

			// 缓存大小设为50M
			sock.setReceiveBufferSize(50 * 1024 * 1024);

			sock.bind(new InetSocketAddress(port));
			// log.info("sock.getReceiveBufferSize:"+sock.getReceiveBufferSize());
			log.info("启动终端UDP服务成功!端口号:" + port);

			// 设置为非堵塞模式
			serverSocketChannel.configureBlocking(false);
			// 注册‘接收’事件
			SelectionKey sk = serverSocketChannel.register(selector,
					SelectionKey.OP_READ);
			try{
 
			String	receiveCacheSizes = AllConfigCache.getInstance().getConfigMap().get("udpReceiveCacheSize");
			if (receiveCacheSizes != null && receiveCacheSizes.length() > 0){
				receiveCacheSize = Integer.parseInt(receiveCacheSizes);
			}
			}catch(Exception e){}
			
			try {
				licenceValidator = LicenceValidator.getInstance();
 				licenceValidator.validateGater();
			} catch (Exception e) {
				e.printStackTrace();
			}
 
		}

	}

	public int getPort() {
		return port;
	}

	// 循环监控在selector上注册的事件
	public synchronized void run() {
		try {
			SelectionKey key = null;
			licenceValidator.validateGater();
			while (true) {
				try {

					int n = selector.select();
					if (n == 0)
						continue;
					Set selected = selector.selectedKeys();
					Iterator it = selected.iterator();
					try {
						if (!licenceValidator.isValid()) { 
							log.info("licence不通过");
						    Thread.sleep(30*1000);
//						    licenceValidator.validateGater();
							continue;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					while (it.hasNext()) {
						key = (SelectionKey) it.next();
						it.remove();
//						log.debug("key.isValid:"+key.isValid()+",key.isConnectable:"+key.isConnectable()+",key.isReadable:"+key.isReadable()+",key.isWritable"+key.isWritable());
						
						if (key.isValid() && key.isReadable() ){
//							log.info("udp read key count:"+(keyCount++));
							DatagramChannel schanel =(DatagramChannel)key.channel();// this.socketChannel;

							byte[] socketData = this.readSocketBytes(schanel);
 							
							if(socketData != null && socketData.length > 0){
//								log.info("udp read data count:"+(dataCount++));
								GprsSocketChannel gprsSocketChannel =   new GprsSocketChannel(schanel);
								gprsSocketChannel.setClientChannel(schanel);
								gprsSocketChannel.setClientAddress(clientAddress);
								
								this.pool.add(new GPRSThread(this.dataPool,
										gprsSocketChannel, socketData,gpsPool));
								 
							}
						}
							//parsePool.add(key);
 
					}
 				selected.clear();
				} catch (Exception ex) {
 
					log.error(
							"位置网关:数据接收服务出现异常:" + ex.getMessage(), ex);
					ex.printStackTrace();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info(
					"位置网关:数据接收服务出现异常，请重新启动数据接收服务!!!!!!!!!!!!!!!!");
		}
	}
	
	// 从Socket中读出数据流
	public synchronized byte[] readSocketBytes(DatagramChannel schanel) {

		

		byte[] ret = null;
		boolean bRead = true;
		String validHex = "";// 缓存中有效内容
		
		try {
			 
			ByteBuffer byteBuffer = ByteBuffer.allocate(receiveCacheSize);
			byteBuffer.clear();
			clientAddress = (InetSocketAddress) schanel.receive(byteBuffer);
			int leng = byteBuffer.position(); // 有效数据长度
			byteBuffer.flip();

			byte[] bcont = byteBuffer.array(); // 缓冲数据
			byte[] validData = new byte[leng]; // 有效数据
			System.arraycopy(bcont, 0, validData, 0, leng);
  
 
			if (validData != null && leng > 0) {

				String s = new String(validData);
				if (s.equals("default send string")) {
					log.debug("莫名奇妙的数据："+s+",clientAddress:"+clientAddress.toString());
					return null;
				}

				if (log.isDebugEnabled()) {
					String hex = Tools.bytesToHexString(validData);
					log.debug("中心收到UDP原始数据报内容：" + hex);
				}

				Counter.setTCount();
			}

			return validData;
 
		} catch (Exception e) {
			bRead = false;
			log.error("读取终端数据失败，连接已断开:" + e.getMessage(), e); 
			e.printStackTrace(); 
		} 

		return null;
	}
	
}
