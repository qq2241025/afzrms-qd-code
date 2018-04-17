package com.mapabc.gater.directl.parse.longhan;

public class BoyuanTimer {
	
	public static long stime ;
	public static long etime ;
	public static long count;
	
	public static long interval(){
		return (etime-stime)/1000;
	}

}
