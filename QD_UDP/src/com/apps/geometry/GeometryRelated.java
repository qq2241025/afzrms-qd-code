package com.apps.geometry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class GeometryRelated {
	    private GeometryFactory geometryFactory =new  GeometryFactory(); 
        
	    public void saveImage(Geometry geometry,String filename){
		    try {
		    	BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_3BYTE_BGR);
		        Graphics2D gr = image.createGraphics();
		        //填充背景色
		        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        gr.setColor(Color.WHITE);
		        gr.fillRect(0, 0, image.getWidth(), image.getHeight());
		        
		        ShapeWriter sw = new ShapeWriter();
				Shape shape = sw.toShape(geometry);
		        gr.draw(shape);
		        String filePath= "D://"+filename +".png";
		        File outFile = new File(filePath);  
			    if(!outFile.exists()){
			    	outFile.mkdirs();
			    }
				ImageIO.write(image, "png", new File(filePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
	    }
	    
	    public Point createPoint(String lon,String lat){  
	        Coordinate coord = new Coordinate(Double.parseDouble(lon), Double.parseDouble(lat));  
	        Point point = geometryFactory.createPoint( coord );  
	        return point;  
	    }  
	      
	    /** 
	     *  will return true as the two line strings define exactly the same shape. 
	     *  两个几何对象是否是重叠的 
	     * @return 
	     * @throws ParseException 
	     */  
	    public boolean equalsGeo() throws ParseException{  
	        WKTReader reader = new WKTReader( geometryFactory );  
	        LineString geometry1 = (LineString) reader.read("LINESTRING(0 0, 2 0, 5 0)");  
	        LineString geometry2 = (LineString) reader.read("LINESTRING(5 0, 0 0)");  
	        Geometry[] garray = new Geometry[]{geometry1,geometry2};
	        GeometryCollection gc = geometryFactory.createGeometryCollection(garray);
	        this.saveImage(gc, "equalsGeo");
	        return geometry1.equals(geometry2);//true  
	    }  
	      
	    /** 
	     * The geometries have no points in common 
	     * 几何对象没有交点(相邻) 
	     * @return 
	     * @throws ParseException 
	     */  
	    public boolean disjointGeo() throws ParseException{  
	        WKTReader reader = new WKTReader( geometryFactory ); 
	        Geometry geometry1 =  reader.read("LINESTRING(100 50, 200 50, 300 50)");  
	        Geometry geometry2 =  reader.read("LINESTRING(100 100, 300 20)"); 
	        Geometry[] garray = new Geometry[]{geometry1,geometry2};
	        GeometryCollection gc = geometryFactory.createGeometryCollection(garray);
	        this.saveImage(gc, "disjointGeo");
	        return geometry1.disjoint(geometry2);  
	    }  
	      
	    /** 
	     * The geometries have at least one point in common. 
	     * 至少一个公共点(相交) 
	     * @return 
	     * @throws ParseException 
	     */  
	    public boolean intersectsGeo() throws ParseException{  
	        WKTReader reader = new WKTReader( geometryFactory );  
	        LineString geometry1 = (LineString) reader.read("LINESTRING(20 40, 30 50, 40 60)");  
	        LineString geometry2 = (LineString) reader.read("LINESTRING(40 20, 20 100)");  
	        Geometry interPoint = geometry1.intersection(geometry2);//相交点  
	        System.out.println(interPoint.toText()); 
	        Geometry[] garray = new Geometry[]{geometry1,geometry2};
	        GeometryCollection gc = geometryFactory.createGeometryCollection(garray);
	        this.saveImage(gc, "intersectsGeo");
	        return geometry1.intersects(geometry2);  
	    }  
	    /** 
	     * @param args 
	     * @throws ParseException  
	     */  
	    public static void main(String[] args) throws ParseException {  
	        GeometryRelated gr = new GeometryRelated();  
	        gr.equalsGeo();  
	        gr.disjointGeo();  
	        gr.intersectsGeo();  
	    }  
}
