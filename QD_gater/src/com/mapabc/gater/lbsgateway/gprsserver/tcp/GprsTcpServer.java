package com.mapabc.gater.lbsgateway.gprsserver.tcp;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.counter.Counter;
import com.mapabc.gater.directl.AllConfigCache;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.gpscom.GpsCompPool;
import com.mapabc.gater.lbsgateway.GprsTcpThread;
import com.mapabc.gater.lbsgateway.TcpLinkCache;
import com.mapabc.gater.lbsgateway.gprsserver.LicenceTimeTask;
import com.mapabc.gater.lbsgateway.gprsserver.StartServer;
import com.mapabc.gater.lbsgateway.gprsserver.ThreadPool;
import com.mapabc.gater.lbsgateway.poolsave.DataPool;
import com.mapabc.gater.lbsgateway.service.licence.LicenceValidator;

/**
 * 位置网关终端服务.用来接收终端上报的数据,并且进行相应的处理.
 */

public class GprsTcpServer extends StartServer {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(GprsTcpServer.class);

	Selector selector;
	ServerSocketChannel serverSocketChannel;
	private SelectionKey sk;
	private SelectionKey csk;
	private SocketChannel socketChannel;
	private int port;
	private ThreadPool pool;
	private DataPool dataPool;
	private Logger logger;
	private ExecutorService exec;
	private GpsCompPool gpsPool;
	private LicenceValidator licenceValidator;
	private boolean isShort = false;
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
	public GprsTcpServer(int port, int parsethreadCount, int savethreadCount,
			int conThdNum, Logger log1) throws IOException {
		 
			log.info("启动TCP通讯服务");

			// 构造一个GPRS数据解析线程池
			pool = new ThreadPool(parsethreadCount);
			pool.startPool();
			// 构造一个数据入库线程池
			dataPool = new DataPool(savethreadCount);
			dataPool.startPool();
			// GPS补偿数据缓存池
			gpsPool = new GpsCompPool(1);
			gpsPool.startPool();

			// 创建一个新的selector
			selector = Selector.open();

			// 创建一个新的serverSocketChannel
			serverSocketChannel = ServerSocketChannel.open();

			// 绑定本地端口
			ServerSocket sock = serverSocketChannel.socket();

			sock.bind(new InetSocketAddress(port), 3000);
			// 设置为非堵塞模式
			serverSocketChannel.configureBlocking(false);

			log.info("启动终端TCP GPRS服务成功!端口号:" + port);

			// 注册‘接收’事件,serverSocketChannel注册ACCEPT事件，SelectionKey跟踪被注册的事件。
			sk = serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

			// 在‘接收’事件中设置执行事件类,SelectionKey关联一个附件AcceptTcpHandler，当事件发生时可以从SelectionKey
			// 中获得这个附件，该附件用来包含和处理这个时间的相关信息。
			// sk.attach(new AcceptTcpHandler(pool, dataPool, selector,
			// serverSocketChannel, logger));

			// exec = Executors.newFixedThreadPool(conThdNum);

			try{
				String shortLink = AllConfigCache.getInstance().getConfigMap().get("isShortLink");
				if (shortLink != null && shortLink.equals("1")){
					isShort = true;
				}
			String	receiveCacheSizes = AllConfigCache.getInstance().getConfigMap().get("tcpReceiveCacheSize");
			if (receiveCacheSizes != null && receiveCacheSizes.length() > 0){
				receiveCacheSize = Integer.parseInt(receiveCacheSizes);
			}
			}catch(Exception e){}
			
			licenceValidator = LicenceValidator.getInstance();
			try {
				licenceValidator.validateGater();
				Timer timer = new Timer();
				LicenceTimeTask ltt = new LicenceTimeTask();
				timer.schedule(ltt, 60 * 1000, LicenceValidator.VALIDATE_TIME);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			

	}

	public int getPort() {
		return port;
	}

	// 循环监控在selector上注册的事件
	public void run() {
		try {

			while (true) {
				try {
					int n = selector.select();
					if (n == 0)
						continue;

					try {
						if (!licenceValidator.isValid()) {
							log.info("licence不通过");
							// LicenceValidator.outErrorInfo();
							Thread.sleep(30 * 1000);
							continue;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					Set selected = selector.selectedKeys();
					Iterator it = selected.iterator();
					while (it.hasNext()) {
						SelectionKey key = (SelectionKey) it.next();

						it.remove();
						try {
							if (!key.isValid()) {
								log.info(
										"key isvalid:" + key.isValid());
								selected.remove(key);
								continue;
							}

							if (key.isAcceptable()) {
								socketChannel = serverSocketChannel.accept();

								if (socketChannel != null) {
									log.info(
											"connection accept, from "
													+ socketChannel.socket()
															.getInetAddress()
															.getHostAddress()
													+ " : "
													+ socketChannel.socket()
															.getPort());
									Socket socket = socketChannel.socket();
									TcpLinkCache.getInstance().addToTcpCache(
											socket, new Date());
									socketChannel.configureBlocking(false);

									csk = socketChannel.register(selector,
											SelectionKey.OP_READ);

//									csk.attach(new GprsSocketTcpChannel(//此attach导致cpu升高
//											socketChannel));

								} else {
									selected.remove(key);
									continue;
								}
							} else if (key.isReadable()) { 

								byte[] readBytes = readSocketBytes(key);
								if (readBytes != null) {
									SocketChannel sc = (SocketChannel) key
											.channel();

									GprsSocketTcpChannel gprsChannel = new GprsSocketTcpChannel(
											sc);

									GprsTcpThread tcp = new GprsTcpThread(
											dataPool, gprsChannel, readBytes,
											sc, gpsPool);

									this.pool.add(tcp);
								}

							}
						} catch (Exception e) {
							key.cancel();
						}

						selected.remove(key);

					}
					selected.clear();

				} catch (Exception ex) { 
					ex.printStackTrace();
					log.error(
							"Gps tcp server select exception!", ex);
					this.sleep(1000);
				}
			}
		} catch (Exception ex) {
		}
	}

	// 从Socket中读出数据流
	private /**synchronized*/ byte[] readSocketBytes(SelectionKey key) {
		SocketChannel socket = (SocketChannel) key.channel();

		byte[] ret = null;
		boolean bRead = true;
		try {
			ByteBuffer byteBuffer = ByteBuffer.allocate(receiveCacheSize);
			byteBuffer.clear();
			int readLen = -1;
			while (socket.isConnected()
					&& (readLen = socket.read(byteBuffer)) > 0) {
				int leng = byteBuffer.position(); // 有效数据长度

				byteBuffer.flip();

				byte[] bcont = byteBuffer.array(); // 缓冲数据
				ret = new byte[leng]; // 有效数据
				System.arraycopy(bcont, 0, ret, 0, leng);

				byteBuffer.clear();
				Counter.setTCount();
			}

			if (readLen < 0) {// 连接断开
				socket.close();
				key.cancel();
			}
			
			if (ret != null) {
				if (log.isDebugEnabled())
					log.debug(socket.socket().getRemoteSocketAddress()
							+ "   原始数据：" + Tools.bytesToHexString(ret));
				return ret;
			}
		} catch (Exception e) {
			bRead = false;
//			log.error("读取终端数据失败:" + e.getMessage(), e);

 
			try {
				// =============由屏蔽=》打开===测试
				Socket tmpsock = socket.socket();
				if (tmpsock != null)
					tmpsock.close();
			} catch (IOException ex) {
				log.error("", ex);
			}
			try {
				socket.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
				key.cancel();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}

		return ret;
	}
}
