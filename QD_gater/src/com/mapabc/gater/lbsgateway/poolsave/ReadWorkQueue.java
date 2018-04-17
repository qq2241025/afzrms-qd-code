/**
 * 
 */
package com.mapabc.gater.lbsgateway.poolsave;

import java.nio.channels.SelectionKey;
import java.util.LinkedList;

import org.apache.commons.logging.LogFactory;

/**
 * @author xiaojun.luan
 *
 */
class ReadWorkQueue {

private LinkedList work;

private static org.apache.commons.logging.Log log = LogFactory
.getLog(ReadWorkQueue.class);

  public ReadWorkQueue() {
    work = new LinkedList();
  }

  public synchronized void addWork(SelectionKey task) {
    work.addLast(task);
    log.info("udp 读取池当前数量：" + work.size());
    notifyAll();
  }
  
  public int getSize(){
	  return work.size();
  }
  
  public synchronized Object getWork() throws InterruptedException {

    while (work.isEmpty()) {
      try {
        wait();
      }
      catch (InterruptedException ie) {
        throw ie;
      }
    }
    Object obj =  work.remove(0);
    
    log.info("udp 读取池取出数据，当前数量："+work.size());
    return obj;
  }
}