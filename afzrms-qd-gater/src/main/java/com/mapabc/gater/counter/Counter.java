package com.mapabc.gater.counter;

public class Counter {
    	private static byte[] lock=new byte[0];
	// 总数
	public static long tcount;
	// 已处理数
	public static long dcount;
	// 未处理数
	public static long ncount;
	
	public static long receCount;
	
	public static long keyCount;
	
	public static long rhCount;
	//接收到第一条时间
	public static long btime ;
	//接收完最后一条时间
	public static long etime;
	//接收所有数据耗时
	public static long time ;
	//数据库池收到的数据总数
	public static long dbPoolTCount;
	//数据库入库总数
	public static long dbPoolSCount;
	
	public static long dbInvalidDataCount;
	
	public static long sDbTime ;
	
	public static long eDbTime;
	
	public static void setTCount(){
		synchronized (lock) {
			if (Counter.tcount==1){
				Counter.btime = System.currentTimeMillis();
				time=0;
				dbInvalidDataCount=0;
			}
			tcount++;
		}
	}
	public static void setDCount(){
		synchronized (lock) {
				dcount++;
		}
	}
	public static void setNCount(){
		synchronized (lock) {
				ncount = tcount-dcount;
				if (ncount ==0){
					sDbTime = System.currentTimeMillis();
				}
		}
	}
	public static void setTime(){
		synchronized (lock) {
		    etime =System.currentTimeMillis();
			 time = etime - btime;
		}
	}
	public static void setETime(){
		synchronized (lock) {
			   etime =System.currentTimeMillis();
		}
	}
	 
	public static void setDbPoolTCount() {
		synchronized (lock) {
			dbPoolTCount++;
	}
	}
	 
	public static void setDbPoolSCount(int count) {
		synchronized (lock) {
			dbPoolSCount+=count;
	}
	}
	public static void setDbIVCount() {
		synchronized (lock) {
		    dbInvalidDataCount++;
	}
	}	 
	public static long lastCount = 0;
	public static long lastCostTime=0;
	public static long getDbDelayTime(){
		synchronized (lock) {
		 long eDbTime = System.currentTimeMillis();
		 if ((dbPoolTCount - dbPoolSCount)==0){
			 
			 return lastCostTime;
		 }
		 if ((dbPoolTCount - dbPoolSCount)==lastCount){
			 return lastCostTime;
		 }
		 lastCount = dbPoolTCount - dbPoolSCount;
		 lastCostTime = (eDbTime-sDbTime)/1000;
			 return (eDbTime-sDbTime)/1000;
		}
	}
	
	public static long lastParseTime;
	public static long lastParseCostTime;
	public static long parseDelayTime(){
		synchronized (lock) {
			 long eDbTime = System.currentTimeMillis();
			 if ((tcount - dcount)==0){
				 
				 return lastParseCostTime;
			 }
			 if ((tcount - dcount)==lastParseTime){
				 return lastParseCostTime;
			 }
			 lastParseTime = tcount - dcount;
			 lastParseCostTime = (eDbTime-etime)/1000;
				 return (eDbTime-etime)/1000;
			}
	}
}
