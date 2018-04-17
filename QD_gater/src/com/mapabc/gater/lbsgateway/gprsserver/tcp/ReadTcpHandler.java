package com.mapabc.gater.lbsgateway.gprsserver.tcp;

import java.nio.channels.*;

import com.mapabc.gater.counter.Counter;
import com.mapabc.gater.gpscom.GpsCompPool;
import com.mapabc.gater.lbsgateway.GprsTcpThread;
import com.mapabc.gater.lbsgateway.gprsserver.ForwardServer;
import com.mapabc.gater.lbsgateway.gprsserver.ThreadPool;
import com.mapabc.gater.lbsgateway.poolsave.DataPool;
 
// Class: Handler
// 1. Read a message,
// 2. Set processor for the massage on the works' queue of the thread pool
class ReadTcpHandler
    implements Runnable {
  private GprsSocketTcpChannel gprsSocketChannel;
  private ThreadPool pool;
  private DataPool dataPool;
  private SelectionKey key;
  private SocketChannel socketChannel;
  private GpsCompPool gpsPool;
  
  public ReadTcpHandler(ThreadPool p,DataPool dataPool,  SelectionKey key,SocketChannel socketChannel,GpsCompPool gpsPool) {
    this.pool = p;
    this.dataPool=dataPool;
    this.socketChannel = socketChannel;
    this.key = key;
    this.gpsPool = gpsPool;
  }

  public void run() {
    // read all data in the socket.
	  
//	  GprsSocketTcpChannel  gprsSocketChannel = new GprsSocketTcpChannel(socketChannel/*(SocketChannel)key.channel()*/);
//     byte[] socketData = gprsSocketChannel.readSocketBytes(key); // manipulates dataFromSocket
//     
//    if (socketData == null) { 
//        return;
//    }
//    pool.add(new GprsTcpThread(dataPool,gprsSocketChannel, socketData,socketChannel,gpsPool));
     
  }

}