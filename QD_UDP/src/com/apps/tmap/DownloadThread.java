package com.apps.tmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
  
  
public class DownloadThread extends Thread {  
    private List<Tile> list =null;
    private String filePath =null; 
    private String thName = null;
      
    public DownloadThread (String threadName,List<Tile> list,String FilePath) {  
       this.list = list;
       this.thName = threadName;
       this.filePath  = FilePath;
    }  
    public List<Tile> getList() {
		return list;
	}
	public void setList(List<Tile> list) {
		this.list = list;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getThName() {
		return thName;
	}
	public void setThName(String thName) {
		this.thName = thName;
	}
	public void run() {  
    	if(list!=null && list.size()>0){
    		for (int i = 0; i < list.size(); i++) {
    			Tile tile = list.get(i);
    			String url = tile.getUrl();
    	        int x = tile.getX(),y = tile.getY(),zoom = tile.getZoom();
    	        saveFile(url, zoom, x, y);
			}
    	}
    }  
    
    
	public  void saveFile(String url,int zoom, int x, int y) {
		int nRead = 0;
		InputStream  input = null;
		FileOutputStream  out = null;
		HttpURLConnection  connection =null;
		try {
			 URL httpurl= new URL(url);
		     connection = (HttpURLConnection) httpurl.openConnection();
			 connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
			 connection.setReadTimeout(60* 1000);
			 connection.connect();
			 String fileP = filePath+"/"+zoom+"/"+x;
	         File file=new File(fileP);
	         if(!file.exists()){
	        	file.mkdirs();
	         }
	         String savepath = fileP+"/"+y+".png";
			if(connection.getResponseCode() == 200){
				input =connection.getInputStream();
				out = new FileOutputStream(savepath);
				byte[] buff = new byte[1024];
				while ((nRead = input.read(buff, 0, buff.length)) != -1) {
					out.write(buff, 0, nRead);
				}
				System.out.println(zoom +"--" + x+ "--"+y + url+"OK");
			}
			if(input.available()==0){
				input.close();
			}
			out.close();
			connection.disconnect();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
          
}  
