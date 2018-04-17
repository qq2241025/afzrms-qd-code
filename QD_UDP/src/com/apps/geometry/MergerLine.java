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
import com.vividsolutions.jts.geom.Geometry;  
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
  /**
   * LineMerger 线路合并，线路之间不能有交点，并且只在线路末尾有公共交点
   * @author zhengang.he
   */
public class MergerLine {  
  
    private static GeometryFactory factory = new GeometryFactory();
    private static WKTReader reader = new WKTReader();
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
    public static void main(String[] args) {  
    	MergerLine aa = new MergerLine();
		try {
			Geometry geometry1 = factory.createGeometry(reader.read("LINESTRING (3 3,2 2,0 0)"));
			Geometry geometry2 = factory.createGeometry(reader.read("LINESTRING (3 3,6 6,0 10)")); 
			Geometry geometry3 = factory.createGeometry(reader.read("LINESTRING (0 10,3 1,10 1)"));
			Geometry[] garray = new Geometry[]{geometry1,geometry2,geometry3};
	        GeometryCollection gc = factory.createGeometryCollection(garray);
	        aa.saveImage(gc,"MergerLine");
		} catch (ParseException e) {
			e.printStackTrace();
		}  
    }  
} 