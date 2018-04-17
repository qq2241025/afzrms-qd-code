package com.apps.tmap;

import java.util.ArrayList;
import java.util.List;

public class MapDownload {
    public static String filePath = "E:\\TMap";
	public static int pix  = 256;
	public static int maxStore  = 30000;
	
	public static String tileUrl = "http://t7.tianditu.com/DataServer?T=cva_w&x={x}2&y={y}&l={z}";
	//
	public static String statUrl = "http://t4.tianditu.cn/img_w/wmts?SERVICE=WMTS&REQUEST=GetTile&VERSION=1.0.0&LAYER=img&STYLE=default&TILEMATRIXSET=w&FORMAT=tiles&TILECOL={y}&TILEROW={x}&TILEMATRIX={z}";
	
	
	
	private static List<Tile> list = new ArrayList<Tile>();
	//2D栅格瓦片
	public static String get2DTileURL(int x,int y,int zoom){
	     String url = tileUrl.replace("{x}", ""+x).replace("{y}", ""+y).replace("{z}", ""+zoom);
	     return url ;
    }
	//栅格卫星影像
	public static String getStatTileURL(int x,int y,int zoom){
	     String url = statUrl.replace("{x}", ""+x).replace("{y}", ""+y).replace("{z}", ""+zoom);
	     return url ;
   }
	
	public static void mapdownTile(TPoint start,TPoint end,int zoom){
		int[]  startTile =  TMap.getGirdNumber(start, zoom);
		int[]  endTile   =  TMap.getGirdNumber(end, zoom);
		System.out.println(startTile[0]+"-"+startTile[1]);
		System.out.println(endTile[0]+"-"+endTile[1]);
		
		
		int maxX = Math.max(startTile[0], endTile[0]),minX = Math.min(startTile[0], endTile[0]); 
		int maxY = Math.max(startTile[1], endTile[1]),minY = Math.min(startTile[1], endTile[1]); 
		String tName = "Thread mapdownTile name is ";
		int packIndex = 0;
	    for(int x =minX ;x< maxX +1;x++){
	    	for(int y = minY;y<maxY+1;y++){
		    	String url = getStatTileURL(x,y,zoom); //卫星图片
		    	Tile tile = new Tile(zoom,url,x,y);
		    	list.add(tile);
		    	if(list.size() == maxStore ){ //如果够了一个完成的包则开启一个线程处理下载栅格瓦片
		    		String threadname= tName + packIndex;
		    		DownloadThread down = new DownloadThread(threadname,list,filePath);
		    		down.start();
		    		packIndex ++; //线程记录数
		    		list =  new ArrayList<Tile>();
		    	}
		    }
	    }
	    //最后一波数据 不够 一批次的逻辑处理
	    if(list.size() > 0){
	    	String threadname= tName + packIndex;
	    	DownloadThread down = new DownloadThread(threadname,list,filePath);
    		down.start();
	    }
	}

    public static void main(String[] args) {
	     int zoomStart = 10;
		 int zoomEnd = 15;
		 TPoint start = new TPoint(36.26147,120.74491); //获取左上角point ----map.getBounds().getNorthEast()
		 TPoint end = new TPoint(35.96526,119.96488);  //获取右下角point ----map.getBounds().getSouthWest()
		 //下载瓦片,
		 for(int index =zoomStart;index<=zoomEnd;index++){
			 mapdownTile(start, end, index);
		 }
   }
   
}
