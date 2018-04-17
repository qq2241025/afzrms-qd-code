package com.mapabc.gater.directl.parse;

import java.io.*;
import java.sql.*;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Log; 
 

/**
 * <p>Title: </p>
 *
 * <p>Description:
 * 前提条件：
 * 1）每个终端同时只对一个摄像头的图片进行上传
 * 2）上传时按照包号顺序进行上传，如果上传未成功重新上传
 * 3）图像数据存储在服务的本地临时文件中，图像的基本信息在本类中缓存
 * </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Beijing Mapabc Co., Ltd.</p>
 *
 * @author 终端产品部 黄山项目组
 * @version 1.0
 */
public class PictureCacher {
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(PictureCacher.class);
	
    /** 本地存储的文件名 */
    private String localFileName;

    /** 图片总字节数 */
    private int byteLength;

    /** 上传分包数 */
    private int packageCount;

    /** 已经上传的包号 */
    private int curPackageNum = -1;

    /** 当前已经上传的包数 */
    private int curPackageCount = 0;

    /** 当前已经传输的字节数 */
    private int curByteSize = 0;
    /**上传拍照时的位置*/
    private float x = 0.0f;
    private float y = 0.0f;
    /**上传时间*/
    private Timestamp picTime = null;
    /**拍照手机的SIM卡*/
    private String simCard = null;

    private boolean transOver;
    private byte[] bytes;


    /**
     * 默认构造函数
     */
    public PictureCacher() {

    }

    /**
     * 构造函数
     * @param localFileName String
     * @param byteLength int
     * @param packageCount int
     */
    public void init(String localFileName, int byteLength, int packageCount) {
        this.localFileName = localFileName;
        this.byteLength = byteLength;
        this.packageCount = packageCount;

        //删除原有的同名文件
        java.io.File file = new java.io.File(localFileName);
        if (file.isFile()) {
            file.delete();
        }

        curByteSize = 0;
        curPackageCount = 0;
        curPackageNum = -1;
    }

    /**
     * 向本类中缓存数据
     * @param curPackageNum int 数据的包号
     * @param pics byte[] 数据字节数组
     * @param length int  数组中的有效位数
     * @return int 0代表存储成功，1代表存储错误，2代表参数错误, 3代表数据逻辑检验错误
     */
    public synchronized int pushBytes(int curPackageNum, byte[] pics,
                                      int length) {
        //参数检验
        if (curPackageNum < 0 || pics == null) {
            return 2;
        }
        

        //将数据存储到临时文件中
        java.io.OutputStream os = null;
        try {
            java.io.File file = new java.io.File(localFileName);
            os = new java.io.FileOutputStream(file, true);
            os.write(pics, 0, length);

            //保存操作信息
            curByteSize += length;
            curPackageCount++;
            this.curPackageNum = curPackageNum;
        } catch (FileNotFoundException ex1) {
            String msg = "图像缓存时未找到缓存文件'" + localFileName + "'：" +
                         ex1.getMessage();
            Log.getInstance().errorLog(msg, ex1);
        } catch (IOException ee) {
            String msg = "向图像缓存文件'" + localFileName + "'添加数据时出错：" +
                         ee.getMessage();
            Log.getInstance().errorLog(msg, ee);
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException ex) {
                }
            }
        }

        return 0;
    }

    public InputStream getImg() {
        //将数据存储到临时文件中
        java.io.InputStream is = null;
        java.io.File file = null;
        try {
            file = new java.io.File(localFileName);
            is = new java.io.FileInputStream(file);
            // clean();//清除临时文件
        } catch (Exception e) {
            Log.getInstance().errorLog("获取图片异常：" + e.getMessage(), e);
        }
        return is;
    }


    /**
     *  所有信息是否已上传完毕
     * @return boolean
     */
    public boolean isTransOver() {
        if (curByteSize == this.byteLength && curPackageCount == packageCount) {
            return true;
        }
        return false;
    }

    /**
     * 在必要的时候清空缓存的文件
     * @return boolean
     */
    public boolean clean() {
        boolean bRet = false;
        //删除原有的同名文件
        java.io.File file = new java.io.File(localFileName);
        if (file.isFile()) {
            bRet = file.delete();
        }
        return bRet;
    }

    /**
     * 获得图像数据，从缓存文件中读取出来
     * @return byte[]
     */
    public byte[] getBytes() {
        byte[] pics = new byte[byteLength];
        byte[] picTemp = new byte[2048];
        //将数据存储到临时文件中
        java.io.InputStream is = null;
        try {
            java.io.File file = new java.io.File(localFileName);
            is = new java.io.FileInputStream(file);
            int pos = 0;
            int readSize = is.read(picTemp);
            while (readSize != -1) {
                System.arraycopy(picTemp, 0, pics, pos, readSize);
                pos += readSize;
                readSize = is.read(picTemp);
            }
        } catch (FileNotFoundException ex1) {
            String msg = "图像缓存时未找到缓存文件'" + localFileName + "'：" +
                         ex1.getMessage();
            Log.getInstance().errorLog(msg, ex1);
        } catch (IOException ee) {
            String msg = "向图像缓存文件'" + localFileName + "'添加数据时出错：" +
                         ee.getMessage();
            Log.getInstance().errorLog(msg, ee);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                }
            }
        }

        return pics;
    }


    public static void main(String[] args) {

        try {
            String srcName = "c:/src.bin";
            String outName = "c:/out.bin";
            int packageSize = 1024;
            java.io.File srcFile = new java.io.File(srcName);
            java.io.InputStream is = new FileInputStream(srcFile);
            int fileSize = is.available();
            int packageNum = fileSize / packageSize + 1; //简单的判断
            PictureCacher picturecacher = new PictureCacher();
            picturecacher.init(outName, fileSize, packageNum);
            byte[] bytes = new byte[1024];
            int readByte = is.read(bytes);
            int curPackageNum = 0;
            while (readByte != -1) {
                int iRet = picturecacher.pushBytes(curPackageNum, bytes,
                        readByte);
                if (iRet == 0) {
                    curPackageNum++;
                }
                readByte = is.read(bytes);
            }

            if (picturecacher.isTransOver()) {
                log.info("trans over");
            }

        } catch (Exception ee) {
            ee.printStackTrace();
        }
    }

    public String getLocalFileName() {
        return localFileName;
    }

    public int getCurByteSize() {
        return curByteSize;
    }

    public int getCurPackageCount() {
        return curPackageCount;
    }

    public int getCurPackageNum() {
        return curPackageNum;
    }

    public int getPackageCount() {
        return packageCount;
    }

    public int getByteLength() {
        return byteLength;
    }
 
    public synchronized float getX() {
		return x;
	}

	public synchronized void setX(float x) {
		this.x = x;
	}

	public synchronized float getY() {
		return y;
	}

	public synchronized void setY(float y) {
		this.y = y;
	}

	public Timestamp getPicTime() {
        return picTime;
    }

    public String getSimCard() {
        return simCard;
    }

    public void setCurByteSize(int curByteSize) {
        this.curByteSize = curByteSize;
    }

    public void setCurPackageCount(int curPackageCount) {
        this.curPackageCount = curPackageCount;
    }

    public void setCurPackageNum(int curPackageNum) {
        this.curPackageNum = curPackageNum;
    }

    public void setLocalFileName(String localFileName) {
        this.localFileName = localFileName;
    }

    public void setPackageCount(int packageCount) {
        this.packageCount = packageCount;
    }

    public void setByteLength(int byteLength) {
        this.byteLength = byteLength;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    

    public void setPicTime(Timestamp picTime) {
        this.picTime = picTime;
    }

    public void setSimCard(String simCard) {
        this.simCard = simCard;
    }
}
