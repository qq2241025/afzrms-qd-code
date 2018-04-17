package com.mapabc.gater.directl.encode.sege;

import java.io.*;
import java.nio.*;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.encode.ControlAdapter;
import com.mapabc.gater.directl.encode.Request;
import com.mapabc.gater.directl.encode.RequestUtil;



public class SegLPG52Control extends ControlAdapter {
 
	String centerSn = "01";
	String zuoSn = "001"; 
	public SegLPG52Control() {

	}
 
	public String listen(Request req) {
		String cmdsn = centerSn+zuoSn;
        String inSeq = Tools.getRandomString(5);
		 while(inSeq.length()<5){
			inSeq = "0"+inSeq;
		 }
		 cmdsn = cmdsn + inSeq; 
		 
		String backNumber = RequestUtil.getReqData(req, "callBackNumber");
		if (backNumber.length() <= 0) {
			 
			return null;
		}
		String sms = "(CMD,003,"+backNumber+")";
		String hex ="5b72"+Tools.bytesToHexString(cmdsn.getBytes())+Tools.int2Hexstring(sms.getBytes().length, 2)+Tools.bytesToHexString(sms.getBytes())+"5d";
		return hex;
	}
	
	public String convey(Request req) {
		String hex = null;
		String ret = "";
		String cmdsn = centerSn+zuoSn;
        String inSeq = Tools.getRandomString(5);
		 while(inSeq.length()<5){
			inSeq = "0"+inSeq;
		 }
		 cmdsn = cmdsn + inSeq; 
	     String head = "5b59"+Tools.bytesToHexString(cmdsn.getBytes());
	         
		String task = RequestUtil.getReqData(req, "content");
		  
		String dataType = "0";
		if (task.getBytes().length <= 256)
			dataType = "0";
		else
			dataType = "1";  

		String data = task;
		 
		if (dataType.equals("1")) {
			long st = System.currentTimeMillis();
			data = Tools.compressHexData(Tools
					.bytesToHexString(data.getBytes()));
			long et = System.currentTimeMillis();
			 
		} else {
			data = Tools.bytesToHexString(data.getBytes());
			 
		}
		// long st = System.currentTimeMillis();
		// String repData = new
		// String(Tools.fromHexString(Tools.decompressHexData(data)));
		// long et = System.currentTimeMillis();
		// log.info(this.getDeviceSN()+"
		// 赛格解压缩回应终端任务单耗时："+(et-st)+" ms");
		// log.info(this.getDeviceSN()+" 赛格回应终端任务单："+repData+"
		// \r\n共"+repData.getBytes().length+" 字节");
		data = SegeUtil.escape(data);
		int leng = data.length()/2+1;
		hex = head+Tools.int2Hexstring(leng, 4)+"05"+data+"5d";
 

		return hex;

	}
	
	public String oilElecControl(Request req) {
		
		String ret = "";
		String cmdsn = centerSn+zuoSn;
        String inSeq = Tools.getRandomString(5);
		 while(inSeq.length()<5){
			inSeq = "0"+inSeq;
		 }
		 cmdsn = cmdsn + inSeq;
    	String state = RequestUtil.getReqData(req, "type");
    	
		if (state != null && state.equals("0")) {//断开
			int length = 23;
			ByteBuffer buf = ByteBuffer.allocate(length);
			buf.put((byte) 0x5B);
			buf.put((byte) 0x20);
			buf.put(cmdsn.getBytes());
			buf.put((byte) 0x09);
			buf.put("(CTR,555)".getBytes());
			buf.put((byte) 0x5D);
			ret = Tools.bytesToHexString(buf.array()) ;
			 
			return ret;
		} else {
			if (state != null && state.equals("1")) {//恢复
				int length = 23;
				ByteBuffer buf = ByteBuffer.allocate(length);
				buf.put((byte) 0x5B);
				buf.put((byte) 0x21);
				buf.put(cmdsn.getBytes());
				buf.put((byte) 9);
				buf.put("(CTR,666)".getBytes());
				buf.put((byte) 0x5D);
				ret = Tools.bytesToHexString(buf.array()) ;
				 
				return ret;
			}
		}
		return null;
	}

 
	public String switchDoorControl(Request req) {
		String cmdsn = centerSn+zuoSn; 
        String inSeq = Tools.getRandomString(5);
		 while(inSeq.length()<5){
			inSeq = "0"+inSeq;
		 }
		 cmdsn = cmdsn + inSeq;
		
		String ret = "";
		String state = RequestUtil.getReqData(req, "type");
		if (state != null && state.equals("0")) {
			int length = 23;
			ByteBuffer buf = ByteBuffer.allocate(length);
			buf.put((byte) 0x5B);
			buf.put((byte) 0x3B);
			buf.put(cmdsn.getBytes());
			buf.put((byte) 9);
			buf.put("(CTR,333)".getBytes());
			buf.put((byte) 0x5D);

			ret = Tools.bytesToHexString(buf.array()) ;

			return ret;
		} else {
			if (state != null && state.equals("1")) {
				int length = 23;
				ByteBuffer buf = ByteBuffer.allocate(length);
				buf.put((byte) 0x5B);
				buf.put((byte) 0x3C);
				buf.put(cmdsn.getBytes());
				buf.put((byte) 9);
				buf.put("(CTR,444)".getBytes());
				buf.put((byte) 0x5D);
				ret = Tools.bytesToHexString(buf.array());

				return ret;
			}
		}
		return null;
	}

 
    public String setTakePhoto(String seq,String number, String count, String interval,
                               String isUpload) {
    	String cmdsn = centerSn+zuoSn; 
        String inSeq = seq;
		 while(inSeq.length()<5){
			inSeq = "0"+inSeq;
		 }
		 cmdsn = cmdsn + inSeq;
		
        String cmd = "";

        String content = "(PIC,01," + this.convertToHex(number, 1) + "," +
                         this.convertToHex(count, 2) + ",2000,J," +
                         this.convertToHex(interval, 4) + "," + isUpload + ")";
        int size = 14 + content.length();
        ByteBuffer buf = ByteBuffer.allocate(size);
        buf.put((byte) 0x5B);
        buf.put((byte) 0xDC);
        buf.put(cmdsn.getBytes());
        buf.put((byte) content.length());
        buf.put(content.getBytes());
        buf.put((byte) 0x5D);
       
            cmd = Tools.bytesToHexString(buf.array());
         
        return cmd;
    }
 
    public String camera(Request req){
    	String ret = null;
    	String seq = Tools.getRandomString(5);
    	ret = this.setTakePhoto(seq, "1", "1", "10", "1");
    	return ret;
    }
    
    private String convertToHex(String num, int n) {
        String temp = "";
        int i = Integer.parseInt(num);
        String hex = Integer.toHexString(i).toUpperCase();
        if (hex.length() > n) {
            int off = 0;
            while (off < n) {
                temp = temp + "F";
                off++;
            }
            return temp;
        } else if (hex.length() < n) {
            while (hex.length() < n) {
                hex = "0" + hex;
            }
            temp = hex;
        } else {
            temp = hex;
        }
        return temp;
    }

	public String msg(Request req) {
		String cmdsn = centerSn+zuoSn; 
        String inSeq = Tools.getRandomString(5);
		 while(inSeq.length()<5){
			inSeq = "0"+inSeq;
		 }
		 cmdsn = cmdsn + inSeq;
		
		String ret = null;
		String mesg = "";// .replaceAll("\\s","");//Tools.BQchange(msg);
							// //msg.replaceAll("\\s",""); //convertUTF16(msg);
							// //ChinaConvert(msg);
		int size = 0;
		try {
			String msg = RequestUtil.getReqData(req, "content");
			byte[] bmsg = msg.getBytes("UTF-16");
 
			String retmsg = Tools.bytesToHexString(bmsg);
			String  submsg = retmsg.substring(4);
			
			byte[] subb = Tools.fromHexString(submsg); 
			
			ByteBuffer buf = ByteBuffer.allocate(subb.length+14);
			buf.put((byte) 0x5B);
			buf.put((byte) 0xE0);
			buf.put(cmdsn.getBytes());
			buf.put((byte) subb.length);
			buf.put(subb);
			buf.put((byte) 0x5D);
			 
			ret =  Tools.bytesToHexString(buf.array());
			 
		} catch (UnsupportedEncodingException ex) {
 		}
		return ret;
	}

 	public static String bytesToHexString(byte[] bs) {
		String s = "";
		for (int i = 0; i < bs.length; i++) {
			String tmp = Integer.toHexString(bs[i] & 0xff);
			if (tmp.length() < 2) {
				tmp = "0" + tmp;
			}
			s = s + tmp;
		}
		return s;
	}

	private static String ChinaConvert(String chs) {
		String qw = "";
		for (int i = 0; i < chs.length(); i++) {
			String c = chs.substring(i, i + 1);
			byte[] b = c.getBytes();
			if (b.length == 1) {
				char c1 = 127;
				qw = qw + c1 + c;
			} else {
				if (b.length == 3) {
					byte b1 = b[0];
					byte b2 = b[1];
					byte b3 = b[3];
					byte b11 = (byte) (b1 + (byte) (96 + 27));
					byte b22 = (byte) (b2 + (byte) (96 + 27));
					byte b33 = (byte) (b3 + (byte) (96 + 27));
					char c11 = (char) b11;
					char c22 = (char) b22;
					char c33 = (char) b33;
					qw = qw + c11 + c22 + c33;
				}
			}
		}
		return qw;
	}

	 
 
	
	public static void main(String[] args){ }
}
