package com.apps.geometry;


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Geometry;  
import com.vividsolutions.jts.geom.GeometryCollection;  
import com.vividsolutions.jts.geom.LineString;  
import com.vividsolutions.jts.geom.LinearRing;  
import com.vividsolutions.jts.geom.Polygon;  
import com.vividsolutions.jts.geom.MultiPolygon;  
import com.vividsolutions.jts.geom.MultiLineString;  
import com.vividsolutions.jts.geom.MultiPoint;  
import com.vividsolutions.jts.io.ParseException;  
import com.vividsolutions.jts.io.WKTReader;  
  
/**   
 * Class GeometryDemo.java  
 * Description Geometry 几何实体的创建，读取操作 
 * Company mapbar  
 * author Chenll E-mail: Chenll@mapbar.com 
 * Version 1.0  
 * Date 2012-2-17 上午11:08:50 
 
 */  
public class GeometryDemo {  
  
    private GeometryFactory geometryFactory =new  GeometryFactory(); 
      
    public void saveImage(Geometry geometry,String filename){
	    try {
	    	BufferedImage image = new BufferedImage(400, 400, BufferedImage.TYPE_3BYTE_BGR);
	        Graphics2D gr = image.createGraphics();
	        //填充背景色
	        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        gr.setColor(Color.WHITE);
	        gr.fillRect(0, 0, image.getWidth(), image.getHeight());
	         
	        Rectangle2D blueRect=new Rectangle2D.Float(160,130,80,80);
	        gr.setPaint(Color.BLUE);
	        gr.fill(blueRect);
	        
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
    
    
    public Point createPoint(){  
        Coordinate coord = new Coordinate(109.013388, 32.715519);  
        Point point = geometryFactory.createPoint( coord );  
        return point;  
    }  
      
    /** 
     * create a point by WKT 
     * @return 
     * @throws ParseException  
     */  
    public Point createPointByWKT() throws ParseException{  
        WKTReader reader = new WKTReader( geometryFactory );  
        Point point = (Point) reader.read("POINT (109.013388 32.715519)");  
        return point;  
    }  
      
    /** 
     * create multiPoint by wkt 
     * @return 
     */  
    public MultiPoint createMulPointByWKT()throws ParseException{  
        WKTReader reader = new WKTReader( geometryFactory );  
        MultiPoint mpoint = (MultiPoint) reader.read("MULTIPOINT(109.013388 32.715519,119.32488 31.435678)");  
        
        return mpoint;  
    }  
    /** 
     *  
     * create a line 
     * @return 
     */  
    public LineString createLine(){  
        Coordinate[] coords  = new Coordinate[] {new Coordinate(2, 2), new Coordinate(2, 2)};  
        LineString line = geometryFactory.createLineString(coords);  
        return line;  
    }  
      
    /** 
     * create a line by WKT 
     * @return 
     * @throws ParseException 
     */  
    public LineString createLineByWKT() throws ParseException{  
        WKTReader reader = new WKTReader( geometryFactory );  
        LineString line = (LineString) reader.read("LINESTRING(0 0, 2 0)");  
        return line;  
    }  
      
    /** 
     * create multiLine  
     * @return 
     */  
    public MultiLineString createMLine(){  
        Coordinate[] coords1  = new Coordinate[] {new Coordinate(2, 2), new Coordinate(2, 2)};  
        LineString line1 = geometryFactory.createLineString(coords1);  
        Coordinate[] coords2  = new Coordinate[] {new Coordinate(2, 2), new Coordinate(2, 2)};  
        LineString line2 = geometryFactory.createLineString(coords2);  
        LineString[] lineStrings = new LineString[2];  
        lineStrings[0]= line1;  
        lineStrings[1] = line2;  
        MultiLineString ms = geometryFactory.createMultiLineString(lineStrings);
        this.saveImage(ms, "createMLine");
        return ms;  
    }  
      
    /** 
     * create multiLine by WKT 
     * @return 
     * @throws ParseException 
     */  
    public MultiLineString createMLineByWKT()throws ParseException{  
        WKTReader reader = new WKTReader( geometryFactory );  
        MultiLineString line = (MultiLineString) reader.read("MULTILINESTRING((0 0, 2 0),(1 1,2 2))");  
        this.saveImage(line, "createMLineByWKT");
        return line;  
    }  
      
    /** 
     * create a polygon(多边形) by WKT 
     * @return 
     * @throws ParseException 
     */  
    public Polygon createPolygonByWKT() throws ParseException{  
        WKTReader reader = new WKTReader( geometryFactory );  
        Polygon polygon = (Polygon) reader.read("POLYGON((20 10, 30 0, 40 10, 30 20, 20 10))");  
        this.saveImage(polygon, "createPolygonByWKT");
        return polygon;  
    }  
      
    /** 
     * create multi polygon by wkt 
     * @return 
     * @throws ParseException 
     */  
    public MultiPolygon createMulPolygonByWKT() throws ParseException{  
        WKTReader reader = new WKTReader( geometryFactory );  
        MultiPolygon mpolygon = (MultiPolygon) reader.read("MULTIPOLYGON(((40 10, 30 0, 40 10, 30 20, 40 10),(30 10, 30 0, 40 10, 30 20, 30 10)))");  
        this.saveImage(mpolygon, "createMulPolygonByWKT");
        return mpolygon;  
    }  
      
    /** 
     * create GeometryCollection  contain point or multiPoint or line or multiLine or polygon or multiPolygon 
     * @return 
     * @throws ParseException 
     */  
    public GeometryCollection createGeoCollect() throws ParseException{  
        LineString line = createLine();  
        Polygon poly =  createPolygonByWKT();  
        Geometry g1 = geometryFactory.createGeometry(line);  
        Geometry g2 = geometryFactory.createGeometry(poly);  
        Geometry[] garray = new Geometry[]{g1,g2};  
        GeometryCollection gc = geometryFactory.createGeometryCollection(garray);  
        this.saveImage(gc, "createMulPolygonByWKT");
        return gc;  
    }  
      
    /** 
     * create a Circle  创建一个圆，圆心(x,y) 半径RADIUS 
     * @param x 
     * @param y 
     * @param RADIUS 
     * @return 
     */  
    public Polygon createCircle(double x, double y, final double RADIUS){  
        final int SIDES = 32;//圆上面的点个数  
        Coordinate coords[] = new Coordinate[SIDES+1];  
        for( int i = 0; i < SIDES; i++){  
            double angle = ((double) i / (double) SIDES) * Math.PI * 2.0;  
            double dx = Math.cos( angle ) * RADIUS;  
            double dy = Math.sin( angle ) * RADIUS;  
            coords[i] = new Coordinate( (double) x + dx, (double) y + dy );  
        }  
        coords[SIDES] = coords[0];  
        LinearRing ring = geometryFactory.createLinearRing( coords );  
        Polygon polygon = geometryFactory.createPolygon( ring, null ); 
        this.saveImage(polygon, "createMulPolygonByWKT");
        return polygon;  
    }  
      
    /** 
     * @param args 
     * @throws ParseException  
     */  
    public static void main(String[] args) throws ParseException {  
        GeometryDemo gt = new GeometryDemo();  
        gt.createLineByWKT();
        gt.createPolygonByWKT();
        gt.createMulPolygonByWKT();
        gt.createPointByWKT();
    }  
}  
