package com.apps;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ReadMapFile {
	private String filepath = "H:\\qd_new";
	private String savePath = "H:\\qd_neww";
	public void readMap(){
		List<FileNode> list = new ArrayList<FileNode>();
		File FileList=new File(filepath);
		String rootFilePath = FileList.getPath();
		this.readFiledir(FileList,list,rootFilePath);
		FileOutputStream out = null;
		InputStream  input = null;
		int nRead = 0;
		try {
			for (int i = 0; i < list.size(); i++) {
				FileNode node = list.get(i);
				File fileonh = node.getFile();
				String[] fileDeep = node.getFileDeep();
				String filename = node.getFileName();
				String[] xys = filename.split("_");
				String files = xys[0];
				System.out.println(files);
				String fileP = savePath+File.separator + fileDeep[0]+File.separator+files;
				File pngFile=new File(fileP);
		        if(!pngFile.exists()){
		        	pngFile.mkdirs();
		        }
		        String savePath = fileP +File.separator + filename;
				input = new FileInputStream(fileonh);
		        out = new FileOutputStream(savePath);
				byte[] buff = new byte[1024];
				while ((nRead = input.read(buff, 0, buff.length)) != -1) {
					out.write(buff, 0, nRead);
				}
				if(input.available()==0){
					input.close();
				}
				out.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//递归所有的文件
	public List<FileNode> readFiledir(File FileList,List<FileNode> files,String rootFilePath){
		  File[] list = FileList.listFiles();
		  if(list!=null && list.length>0){
		      for (int i = 0; i < list.length; i++) {
		    	  File file = list[i];
		    	  if(file.isDirectory()){
		    		  this.readFiledir(file,files,rootFilePath);
		    	  }
		    	  if(file.isFile()){
		    		  String afileName = file.getName();
		    		  String afilepath = file.getPath();
		    		  afilepath = afilepath.substring(rootFilePath.length()+1, afilepath.length());
		    		  String[] pathArr = afilepath.split(File.separator+File.separator);
		    		  FileNode filenode = new FileNode(afileName,afilepath,file,pathArr);
		    		  files.add(filenode);
		    	  }
			  }
		  }
		  return files;
	}
	
	
	
	
    public static void main(String[] args) {
    	ReadMapFile  rd = new ReadMapFile();
    	rd.readMap();
	}
}

class FileNode{
	private String fileName;
	private String filepath;
	private File file;
	private String[] fileDeep;
	
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFilepath() {
		return filepath;
	}
	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}
	
	public String[] getFileDeep() {
		return fileDeep;
	}
	public void setFileDeep(String[] fileDeep) {
		this.fileDeep = fileDeep;
	}
	public FileNode(String fileName, String filepath, File file,String[] fileDeep) {
		super();
		this.fileName = fileName;
		this.filepath = filepath;
		this.file = file;
		this.fileDeep = fileDeep;
	}
	public FileNode() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}

