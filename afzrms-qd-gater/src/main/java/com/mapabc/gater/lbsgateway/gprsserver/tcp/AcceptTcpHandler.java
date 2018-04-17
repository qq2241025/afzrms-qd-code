package com.mapabc.gater.lbsgateway.gprsserver.tcp;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.gpscom.GpsCompPool;
import com.mapabc.gater.lbsgateway.gprsserver.ThreadPool;
import com.mapabc.gater.lbsgateway.poolsave.DataPool;

// Class: Acceptor
// Accepts connection requests from clients that arrive at serverSocketChannel
// and set the new connection socket as SelecteableChannel
// on the selector, with register key over read operations
public class AcceptTcpHandler implements Runnable {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(AcceptTcpHandler.class);

	final Selector selector;
	private ThreadPool pool;
	private DataPool dataPool;
	private ServerSocketChannel serverSocketChannel;
	private GpsCompPool gpsPool;

	public AcceptTcpHandler(ThreadPool pool, DataPool dataPool,
			Selector selector, ServerSocketChannel serverSocketChannel,
			GpsCompPool gpsPool) {
		this.pool = pool;
		this.dataPool = dataPool;
		this.selector = selector;
		this.serverSocketChannel = serverSocketChannel;
		this.gpsPool=gpsPool;
		 
	}

	public void run() {
		try {
			// Accept new client connection request
			SocketChannel socketChannel = serverSocketChannel.accept();
			 
			if (socketChannel != null) {
				log.info("connection accept, from "
						+ socketChannel.socket().getInetAddress().getHostAddress() + " : "
						+ socketChannel.socket().getPort());
 
				socketChannel.configureBlocking(false);
				 
				SelectionKey sk = socketChannel.register(selector,
						SelectionKey.OP_READ);
				 
				 sk.attach(new ReadTcpHandler(pool, dataPool, sk,socketChannel,gpsPool ));
				 
				//selector.wakeup();
  				//放入读取数据池处理
//				 ReadTcpHandler readHanlder = new ReadTcpHandler(pool, dataPool, sk,socketChannel );
//				 pool.add(readHanlder);
 				 
			}
		} catch (IOException ex) {
			//logger.log(Level.WARNING, "Acceptor : failed...");
			log.info(ex.getMessage());
			ex.printStackTrace();
		}
	}
}
