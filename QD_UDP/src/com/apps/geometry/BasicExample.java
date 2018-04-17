package com.apps.geometry;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.vividsolutions.jts.awt.ShapeWriter;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class BasicExample {
	public void overlaps() throws ParseException, FileNotFoundException{
		WKTReader reader = new WKTReader();
		Geometry geometry1 = reader.read("LINESTRING (20 20,40 40,50 50,120 240, 231 123)");
		Geometry geometry2 = reader.read("MULTIPOINT(109  32 ,119  31,245  131,189  91,179 98)");
		Geometry geometry3 = reader.read("POLYGON((109  32 ,119  45,145  56,195  67,145  23,109  32))");
		
		
		this.saveImage(geometry1,"D://geometry1.png");
		this.saveImage(geometry2,"D://geometry2.png");
		this.saveImage(geometry3,"D://geometry3.png");
	}
	public void saveImage(Geometry geometry,String filePath){
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
	        File outFile = new File(filePath);  
		    if(!outFile.exists()){
		    	outFile.mkdirs();
		    }
			ImageIO.write(image, "png", outFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
	public void test2(){
		try {
			  Geometry g1 = new WKTReader().read("LINESTRING (0 0, 10 10, 20 20)");
			  System.out.println("Geometry 1: " + g1);
			  Coordinate[] coordinates = new Coordinate[]{new Coordinate(0, 0),
			    new Coordinate(10, 10), new Coordinate(20, 20)};
			  Geometry g2 = new GeometryFactory().createLineString(coordinates);
			  System.out.println("Geometry 2: " + g2);
			  Geometry g3 = g1.intersection(g2);
			  System.out.println("G1 intersection G2: " + g3);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	public static void main(String[] args)throws Exception{
		BasicExample ss =new BasicExample();
		ss.overlaps();
	}
}
