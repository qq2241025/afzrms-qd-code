package com.apps.geometry;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class GeometryClipperTest {

    static final double EPS = 1e-3;
    static final double EPS_CORNERS = 1e-1;
    Geometry boundsPoly;
    WKTReader wkt;
    public GeometryClipperTest(){
        try {
            wkt = new WKTReader();
			boundsPoly = wkt.read("POLYGON((0 0, 0 10, 10 10, 10 0, 0 0))");
		} catch (ParseException e) {
			e.printStackTrace();
		}
    }
    
    public void testFullyInside() throws Exception {
        LineString ls = (LineString) wkt.read("LINESTRING(1 1, 2 5, 9 1)");
        showResult("Fully inside", ls);
    }
    
    public void testInsideBorders() throws Exception {
        LineString ls = (LineString) wkt.read("LINESTRING(0 0, 2 5, 10 0)");
        showResult("Inside touching borders", ls);
    }
    
    public void testFullyOutside() throws Exception {
        LineString ls = (LineString) wkt.read("LINESTRING(-5 0, -5 15, 15 15)");
        showResult("Inside touching borders", ls);
    }
    
    public void testCross() throws Exception {
        LineString ls = (LineString) wkt.read("LINESTRING(-5 -5, 15 15)");
        showResult("Cross", ls);
    }
    
    public void testTouchLine() throws Exception {
        LineString ls = (LineString) wkt.read("LINESTRING(0 0, 0 10)");
        showResult("Touch border", ls);
    }
    
    public void testTouchPoint() throws Exception {
        LineString ls = (LineString) wkt.read("LINESTRING(-5 5, 0 5)");
        showResult("Touch point", ls);
    }
    
    public void testMultiTouch() throws Exception {
        LineString ls = (LineString) wkt.read("LINESTRING(-5 0, 0 1, -5 2, 0 3, -5 4, 0 5)");
        showResult("Multitouch", ls);
    }
    
    public void testTouchAndCross() throws Exception {
        LineString ls = (LineString) wkt.read("LINESTRING(-5 0, 0 1, -5 2, 5 2, 5 3, -5 3, 0 4)");
        showResult("Touch and cross", ls);
    }
    
    public void testTouchAndParallel() throws Exception {
        LineString ls = (LineString) wkt.read("LINESTRING(-5 0, 0 1, -5 2, 0 2, 0 3, -5 3, 0 4)");
        showResult("Touch and parallel", ls);
    }
    
    public void testInsideOut() throws Exception {
        LineString ls = (LineString) wkt.read("LINESTRING(-2 8, 12 8, 12 2, -2 2)");
        showResult("Touch border", ls);
    }
    
    
    public void testFullyOutsideCircle() throws Exception {
        Point p = (Point) wkt.read("POINT(5 5)");
        LineString ls = ((Polygon) p.buffer(10)).getExteriorRing();
        showResult("Circle around", ls);
    }
    
    
    public void testCrossingCircle() throws Exception {
        Point p = (Point) wkt.read("POINT(5 5)");
        LineString ls = ((Polygon) p.buffer(6)).getExteriorRing();
        showResult("Circle around", ls);
    }
    
    
    public void testInsidePolygon() throws Exception {
        Geometry g = wkt.read("POINT(5 5)").buffer(2);
        showResult("Polygon inside", g);
    }
    
    
    public void testOutsidePolygon() throws Exception {
        Geometry g = wkt.read("POINT(5 5)").buffer(10);
        showResult("Polygon outside", g);
    }
    
    
    public void testPolygonCrossingSide() throws Exception {
        Geometry g = wkt.read("POLYGON((-2 2, 2 2, 2 4, -2 4, -2 2))");
        showResult("Crossing side", g);
    }
    
    
    public void testCrossingOtherSide() throws Exception {
        Geometry g = wkt.read("POLYGON((6 2, 12 2, 12 6, 6 6, 6 2))");
        showResult("Donut crossing", g);
    }
    
    
    public void testPolygonCrossingTwoSides() throws Exception {
        Geometry g = wkt.read("POLYGON((-2 2, 2 2, 2 12, -2 12, -2 2))");
        showResult("Crossing two sides", g);
    }
    
    
    public void testPolygonCrossingThreeSides() throws Exception {
        Geometry g = wkt.read("POLYGON((-2 2, 12 2, 12 12, -2 12, -2 2))");
        showResult("Crossing three sides", g);
    }
    
    
    public void testDonutCrossingInvalid() throws Exception {
        Geometry g = wkt.read("POLYGON((6 2, 14 2, 14 8, 6 8, 6 2), (8 4, 12 4, 12 6, 8 6, 8 4))");
        showResult("Donut crossing, invalid geom", g);
    }
    
    
    public void testDonutHoleOutside() throws Exception {
        Geometry g = wkt.read("POLYGON((6 2, 14 2, 14 8, 6 8, 6 2), (11 4, 12 4, 12 6, 11 6, 11 4))");
        showResult("Donut crossing, invalid geom", g);
    }
    
    public void assertTrue(boolean falg){
    	System.out.println(falg);
    }
    public void testDonutCrossingValid() throws Exception {
        Geometry g = wkt.read("POLYGON((6 2, 14 2, 14 8, 6 8, 6 2), (8 4, 12 4, 12 6, 8 6, 8 4))");
        showResult("Donut crossing, valid geom", g);
    }
    
    public void showResult(String title, Geometry original) throws Exception {
        
        BufferedImage image = new BufferedImage(600, 600, BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D gr = image.createGraphics();
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gr.setColor(Color.WHITE);
        gr.fillRect(0, 0, image.getWidth(), image.getHeight());
        gr.setColor(Color.LIGHT_GRAY);

        gr.setStroke(new BasicStroke(1, 0, 0, 1, new float[] { 5, 5 }, 0));
        gr.draw(new Line2D.Double(0, 300, 600, 300));
        gr.draw(new Line2D.Double(300, 0, 300, 600));

        AffineTransform at = new AffineTransform();
        at.translate(300, 300);
        at.scale(10, -10);

        gr.setStroke(new BasicStroke(1));
        gr.setColor(Color.LIGHT_GRAY);
       // gr.draw(new LiteShape(boundsPoly, at, false));
        gr.setStroke(new BasicStroke(0.5f));
        gr.setColor(Color.BLUE);
        gr.dispose();
        File outFile = new File("D://"+title+".png");  
	    if(!outFile.exists()){
	    	outFile.mkdirs();
	    }
		ImageIO.write(image, "png", outFile);
    }
    
    public static void main(String[] args) throws Exception {
    	GeometryClipperTest clipper = new GeometryClipperTest();
        clipper.testFullyOutside();
        clipper.testInsideOut();
        clipper.testInsidePolygon();
        clipper.testDonutHoleOutside();
        clipper.testInsidePolygon();
        clipper.testDonutCrossingInvalid();
        clipper.testCrossingCircle();
        clipper.testInsideBorders();
        clipper.testPolygonCrossingThreeSides();
    }
    
    
}

