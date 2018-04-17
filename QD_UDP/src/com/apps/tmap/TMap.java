package com.apps.tmap;


public class TMap {
	
	public static double xa =  85.0511287798;
	public static double A = Math.PI / 180;
	public static double B = 180 / Math.PI;
	
	private static  TPixed lnglatToPixel(TPoint point,int zoom){
		double c = Math.max(Math.min(xa, point.getLat()), -xa);
		double aa = point.getLng() * A;
		double cc = Math.log(Math.tan(Math.PI / 4 + c * A / 2));
		TPixed d = new TPixed(aa, cc);
		double e =  256 * Math.pow(2, zoom);
		return new TPixed(e * (0.15915494309189535 * d.getX() + 0.5), e * (-0.15915494309189535 * d.getY() + 0.5));
	}
	
	
	 private static TPoint pixelToLngLat(TPixed pix,int zoom){
    	double c = 256 * Math.pow(2, zoom);;
    	TPixed aa = new  TPixed((pix.getX() / c - 0.5) / 0.15915494309189535, (pix.getY() / c - 0.5) / -0.15915494309189535);
    	return new TPoint((2 * Math.atan(Math.exp(aa.getY())) - Math.PI / 2) * B,aa.getX() * B);
	 }
	
	 
	 public static double[] getXY(TPoint point,int zoom){
		 TPixed  px = lnglatToPixel(point, zoom);
		 double x  =  Math.floor(px.getX()/256);
		 double y  =  Math.floor(px.getY()/256);
		 return new double[]{x,y};
	 }
	 
	 public static int[] getGirdNumber(TPoint point,int zoom){
		 TPixed  px = lnglatToPixel(point, zoom);
		 int x  =  (int) Math.floor(px.getX()/256);
		 int y  =  (int) Math.floor(px.getY()/256);
		 return new int[]{x,y};
	 }
	 
	 
	 public static TPoint getPoint(double x,double y ,int zoom){
		 double doublex  =  Math.floor(x * 256) ;
		 double doubley  =  Math.floor(y * 256);
		 TPixed pix = new TPixed(doublex,doubley);
		 TPoint point = pixelToLngLat(pix,zoom);
		 return point;
	 }
	 
	 public void test(){
		    int zoomx = 12;
		 	double lat= 116.51267 ; 
	        double lng=	39.96525; 
		    System.out.println("原始经纬度坐标-->lat:"+lat+"--"+"lng:"+lng);
	    	TPoint  point  = new TPoint(lng,lat);
	    	
	    	
	    	TPixed xx = TMap.lnglatToPixel(point, zoomx);
	    	System.out.println("经纬度----平面坐标"+xx.getX()+"----"+xx.getY());
	    	
	    	TPixed toPix = new TPixed(xx.getX(),xx.getY());
	    	TPoint  Topoint  = TMap.pixelToLngLat(toPix, zoomx);
	    	System.out.println("平面坐标---经纬度lat:"+Topoint.getLat()+"--"+"lng:"+Topoint.getLng());
	    	
	    	double[]  yyy = TMap.getXY(point, zoomx);
	    	System.out.println("X:"+yyy[0]+"---"+"Y:"+yyy[1]);
	}
}
