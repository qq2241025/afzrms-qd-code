package com.mapabc.gater.directl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.commons.logging.LogFactory;
//import com.mapabc.gater.lbmp.service.CoordinateConvertService;

public class Tools {
	public Tools() {
	}
	private static org.apache.commons.logging.Log log=LogFactory.getLog(Tools.class);

	 /**
	  * 把度分格式为DDDMMmmmmm的经度转换成度
	  * @param DDDMM.mmmmm
	  * 
	  * @author 
	  */
	public static String formatYtoDu(String DDMMmmmmm) {
		double xy = Double.parseDouble(DDMMmmmmm);
		if (xy == 0) {
			return "0";
		}
		String result = null;
		double DDD = Double.parseDouble(DDMMmmmmm.substring(0, 2));
		double MMmmmm = Double.parseDouble(DDMMmmmmm.substring(2, DDMMmmmmm
				.length()));
		MMmmmm = MMmmmm / 60;
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(6);
		format.setMinimumFractionDigits(6);
		result = format.format(DDD + MMmmmm).replaceAll(",", "");
		return result;
	}

	/**
	 * 把格式为DDMMmmmmm的度分纬度转换成度
	 * @param DDMM.mmmmm
	 * 
	 * @author 
	 */
	public static String formatXtoDu(String DDDMMmmmmm) {
		double xy = Double.parseDouble(DDDMMmmmmm);
		if (xy == 0) {
			return "0";
		}
		String result = null;
		double DDD = Double.parseDouble(DDDMMmmmmm.substring(0, 3));
		double MMmmmm = Double.parseDouble(DDDMMmmmmm.substring(3, DDDMMmmmmm
				.length()));
		MMmmmm = MMmmmm / 60;
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(6);
		format.setMinimumFractionDigits(6);
		result = format.format(DDD + MMmmmm).replaceAll(",", "");
		return result;
	}

	/**
	 *  把坐标度转换成毫秒
	 * @param DDMMmmmmm
	 * 
	 * @author 
	 */
	public static String Du2Second(String DDMMmmmmm) {
		String result = null;
		double DDD = Double.parseDouble(DDMMmmmmm);
		double ms = DDD * 60 * 60 * 1000;
		return (int)ms+"";
	}

	/**
	 * 把16进制的字符转换成 byte数组
	 * @param s 为16进制串，每个字节16进制为2位，s合法长度为偶数
	 * 
	 * @author 
	 */
	public static byte[] fromHexString(String s) {
		int stringLength = s.length();
		if ((stringLength & 0x1) != 0) {
			throw new IllegalArgumentException(
					"fromHexString   requires   an   even   number   of   hex   characters");
		}
		byte[] b = new byte[stringLength / 2];

		for (int i = 0, j = 0; i < stringLength; i += 2, j++) {
			int high = charToNibble(s.charAt(i));
			int low = charToNibble(s.charAt(i + 1));
			b[j] = (byte) ((high << 4) | low);
		}
		return b;
	}

	private static int charToNibble(char c) {
		if ('0' <= c && c <= '9') {
			return c - '0';
		} else if ('a' <= c && c <= 'f') {
			return c - 'a' + 0xa;
		} else if ('A' <= c && c <= 'F') {
			return c - 'A' + 0xa;
		} else {
			throw new IllegalArgumentException("Invalid   hex   character:   "
					+ c);
		}
	}

	/**
	 * 把byte数组转换成16进制字符
	 * @param bs 待转换的数组
	 * 
	 * @author 
	 */
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

	//将低自己在前转换为低字节在后数组
	//将高字节在前转换为高字节在后数组
	public static byte[] convertBytePos(byte[] littleByte){
		
		byte[] ret = new byte[littleByte.length];
		for(int i=0; i<littleByte.length; i++){
			ret[i] = littleByte[littleByte.length-i-1];
		}
		return ret;
	}

	/**
	 * 整合格林威治时间到北京时间
	 * @param time 格式：hhmmss
	 * @param date 格式：ddmmyy
	 *  String
	 */
	public static String conformtime(String time, String date) {
		try {
			String hour = time.substring(0, 2);
			String min = time.substring(2, 4);
			String sec = time.substring(4, 6);
			String day = date.substring(0, 2);
			String month = date.substring(2, 4);
			String year = date.substring(4, 6);
			String result = "";
			result = "20" + year + "-" + month + "-" + day + " ";
			result += hour + ":" + min + ":" + sec;
			SimpleDateFormat simpleDate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			Date d = simpleDate.parse(result);
			Calendar car = Calendar.getInstance();
			car.setTime(d);
			car.add(Calendar.HOUR, 8);
			Date newDate = new Date(car.getTimeInMillis());
			return simpleDate.format(newDate);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		return null;
	}

 

	/**
	 *  bytes to int
	 * @param decBytes
	 * 
	 * @author 
	 */
	public static int byte2Int(byte[] decBytes) {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < decBytes.length; i++) {
			String s = Integer.toHexString(decBytes[i] & 0xFF);
			if (s.length() < 2) {
				s = "0" + s;
			}
			str.append(s);
		}
		int ret = Integer.parseInt(str.toString(), 16);
		return ret;
	}

	/**
	 *  bytes to long
	 * @param decBytes
	 * 
	 * @author 
	 */
	public static long byte2Long(byte[] decBytes) {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < decBytes.length; i++) {
			String s = Integer.toHexString(decBytes[i] & 0xFF);
			if (s.length() < 2) {
				s = "0" + s;
			}
			str.append(s);
		}
		long ret = Long.parseLong(str.toString(), 16);
		return ret;
	}

	/**
	 * 把整型num转换成长度为ws的十六进制的字符
	 * @param num 整数值
	 * @param ws 转换后的长度
	 * 
	 * @author 
	 */
	public static String int2Hexstring(int num, int ws) {
		String intHex = Integer.toHexString(num);
		while (intHex.length() < ws) {
			intHex = "0" + intHex;
		}
		return intHex;
	}

 
    /**
     * 把整型字符串值转换为指定长度的16进制
     * @param num 待转换的整型串
     * @param n 转换后的长度
     * 
     * @author 
     */
	public static String convertToHex(String num, int n) {
		String temp = "";
		int i = Integer.parseInt(num);
		String hex = Integer.toHexString(i);// .toUpperCase();
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

	/**
	 * 对GPS上报数据的时间是格林威治时间,换为北京时间
	 * @param time 日期
	 * @param format 日期格式
	 * 
	 */
	public static String getGPSCurrentTime(String time,String format) {
		SimpleDateFormat simpleDate = null;
		Date newDate = null;
		try {
			simpleDate = new SimpleDateFormat(format);
			Date date = simpleDate.parse(time);
			Calendar car = Calendar.getInstance();
			car.setTime(date);
			car.add(Calendar.HOUR, 8);
			newDate = new Date(car.getTimeInMillis());
		} catch (java.text.ParseException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return simpleDate.format(newDate);
	}

 

	/**
	 * ZLIB压缩16进制串
	 * @param input 16进制串
	 * 
	 * @author 
	 */
	public static String compressHexData(String input) {
		try {

			Deflater compresser = new Deflater(Deflater.BEST_COMPRESSION);
			byte[] inputB = Tools.fromHexString(input);

			byte[] output = new byte[512];
			compresser.setInput(inputB);
			compresser.finish();
			int compressedDataLength = compresser.deflate(output);
			// Log.getInstance().outXWRJLoger("压缩16进制串结果："+(Tools.bytesToHexString(output)));
			byte[] out = new byte[compressedDataLength];
			System.arraycopy(output, 0, out, 0, out.length);
			return Tools.bytesToHexString(out);
		} catch (Exception ex) {
			ex.printStackTrace();
			return "UNZIP_ERR";
		}

	}

	/**
	 * ZLIB解压缩16进制串
	 * @param input
	 * 
	 * @author 
	 */
	public static String decompressHexData(String input) {
		try {

			byte[] output = Tools.fromHexString(input);
			// Decompress the bytes
			Inflater decompresser = new Inflater();
			decompresser.setInput(output);
			byte[] result = new byte[input.length()];
			int resultLength = decompresser.inflate(result);
			decompresser.end();
			byte[] out = new byte[resultLength];
			System.arraycopy(result, 0, out, 0, resultLength);
			// Decode the bytes into a String
			String outputString = Tools.bytesToHexString(out);
			// Log.getInstance().outXWRJLoger("解压缩16进制串结果："+(Tools.bytesToHexString(output)));

			return outputString;
		} catch (Exception ex) {
			ex.printStackTrace();
			return "UNZIP_ERR";
		}
	}

	/**
	 * @函数功能: BCD码转为10进制串(阿拉伯数据)
	 * @输入参数: BCD码
	 * @输出结果: 10进制串
	 */
	public static String bcd2Str(byte[] bytes) {
		StringBuffer temp = new StringBuffer(bytes.length * 2);

		for (int i = 0; i < bytes.length; i++) {
			temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
			temp.append((byte) (bytes[i] & 0x0f));
		}
		return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
				.toString().substring(1) : temp.toString();
	}

	/**
	 * @函数功能: 10进制串转为BCD码
	 * @输入参数: 10进制串
	 * @输出结果: BCD码
	 */
	public static byte[] str2Bcd(String asc) {
		int len = asc.length();
		int mod = len % 2;

		if (mod != 0) {
			asc = "0" + asc;
			len = asc.length();
		}

		byte abt[] = new byte[len];
		if (len >= 2) {
			len = len / 2;
		}

		byte bbt[] = new byte[len];
		abt = asc.getBytes();
		int j, k;

		for (int p = 0; p < asc.length() / 2; p++) {
			if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
				j = abt[2 * p] - '0';
			} else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
				j = abt[2 * p] - 'a' + 0x0a;
			} else {
				j = abt[2 * p] - 'A' + 0x0a;
			}

			if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
				k = abt[2 * p + 1] - '0';
			} else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
				k = abt[2 * p + 1] - 'a' + 0x0a;
			} else {
				k = abt[2 * p + 1] - 'A' + 0x0a;
			}

			int a = (j << 4) + k;
			byte b = (byte) a;
			bbt[p] = b;
		}
		return bbt;
	}

	 
	/**
	 * 把字符串转换为Date
	 * 
	 * @param date
	 *            String:日期
	 * @param format
	 *            String：日期格式
	 *  Date
	 */
	public static Date formatStrToDate(String date, String format) {
		Date d = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		try {
			d = sdf.parse(date);
		} catch (ParseException ex) {
		}
		return d;
	}

	/**
	 * 把Date转换为字符串日期
	 */
	public static String formatDate2Str(Date date, String format) {
		String d = null;
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		d = sdf.format(date);

		return d;
	}
 
	/**
	 * 把度转换成16进制毫秒
	 * @param DDMMmmmmm
	 * 
	 * @author 
	 */
	public static String Du2Mills(String DDMMmmmmm) {
		String result = null;
		double DDD = Double.parseDouble(DDMMmmmmm);
		int ms = (int) (DDD * 60 * 60 * 1000);
		// DecimalFormat format = new DecimalFormat("0");
		// String ss = format.format(ms);
		String hex = Integer.toHexString(ms);
		return hex.toUpperCase();
	}

 
 


	/**
	 * 返回一个n位随机数值
	 * @param size
	 * 
	 * @author 
	 */
	public static String getRandomString(int size) {
		// String
		// seed="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		String seed = "0123456789";
		byte chs[] = seed.getBytes();
		byte bs[] = new byte[size];
		Random random = new Random();
		int length = chs.length;
		for (int i = 0; i < size; i++) {
			bs[i] = chs[random.nextInt(length)];
		}
		return new String(bs);
	}

	/**
	 *  数据包异或校验值
	 * @param b
	 * 
	 * @author 
	 */
	public static byte checkData(byte[] b) {
		byte result = b[0];
		int i = 1;
		while (i < b.length) {
			result ^= b[i];
			i++;
		}
		return result;
	}

	 
	/**
	 * 格式化double数值
	 * @param df
	 * @param maxfracDigit 允许保留最大小数点
	 * @param minfracDigit 允许保留最小小数点
	 * 
	 * @author 
	 */
	public static String getNumberFormatString(double df, int maxfracDigit,
			int minfracDigit) {
		String ret = "";
		NumberFormat nf = NumberFormat.getInstance();

		nf.setMaximumFractionDigits(maxfracDigit);
		nf.setMinimumFractionDigits(minfracDigit);
		ret = nf.format(df).replaceAll("\\,", "");

		return ret;
	}
	/**
	 * 
	 * 判断一个字节二进制串每位的数值，高位在前，地位在后
	 * @param data 
	 * @param pos 字节的第几位
	 * 
	 * @author 
	 */
	public static int getByteBit(byte data, double pos) {
		int bitData = 0;

		byte compare = (byte) Math.pow(2.0, pos);

		byte b = (byte) (data & compare);
		if ((data & compare) == compare) {
			bitData = 1;
		}
		return bitData;
	}

 
 

	/**
	 * 负数从补码获取到原码
	 * @param hexString
	 * 
	 * @author 
	 */
	public static int getValueFromCompCode(String hexString) {
		int ret = 0;
		int i = Integer.parseInt(hexString.substring(1), 16);
		int j = Integer.parseInt("fffffff", 16);
		int m = i ^ j;

		if (hexString.charAt(0) == 'F') {// 负数
			ret = -(m + 1);
		}
		return ret;

	}

 
   /**
    * 16进制毫秒转换为度
    * @param xy 16进制毫秒
    *  
    * @author 
    */
	public static String fromMs2XY(String xy) {
		String ret = "";
		try {
			double ms = Integer.parseInt(xy, 16);
			double ds = ms / 1000 / 60 / 60;

			DecimalFormat format = new DecimalFormat("0.000000");
			format.setMaximumFractionDigits(6);
			ret = format.format(ds);
		} catch (Exception e) {
			ret = "0";
		}
		return ret;
	}
 	/**
	 * 去除字符串前的0
	 * @param str
	 * 
	 * @author 
	 */
	public static String removeZeroStr(String str) {
		String ret = null;
		if (str != null && str.trim() != "") {
			int i = 0;
			while (i < str.length()) {
				if (str.charAt(i) != '0') {
					break;
				}
				i++;
			}
			if (i != str.length())
				ret = str.substring(i);
			else
				ret = "0";
		}

		return ret;
	}

	/**
	 * 把节转换成公里
	 * @param knot
	 * 
	 * @author 
	 */
	public static String formatKnotToKm(String knot) {
		if (knot == null || knot.trim().length() <= 0) {
			return "0";
		}
		String ret = "";
		double speed = 0;
		if (knot != null) {
			try {
				speed = Double.parseDouble(knot);
			} catch (java.lang.NumberFormatException ex) {
				ex.printStackTrace();
			}
			speed = speed * 1.852;
		}
		ret = "" + speed;
		if (ret.length() > 4) {
			ret = ret.substring(0, 4);
		}
		return ret;
	}

	/**
	 *  把公里转换成节
	 * @param km
	 * 
	 * @author 
	 */
	public static String formatKmToKnot(String km) {
		String knot = "";
		double dSpeed = Double.parseDouble(km);
		// if (dSpeed > 999) {
		// dSpeed = 999;
		// }
		double hSpeed = dSpeed / 1.852;
		int iSpeed = (int) hSpeed;
		knot = "" + iSpeed;

		return knot;
	}

	/**
	 * 获取矩形区域最大的经度纬度（即右上点）
	 * @param point1：矩形对角点1,格式: x,y
	 * @param point2：矩形对角点2,格式：x,y
	 *  String[]:[0]为经度，[1]为纬度
	 * by 
	 */
	public static String[] getRecMaxLntLat(String point1, String point2) {
		String[] ret = new String[4];
//		CoordinateConvertService coordService = new MapabcCoordServiceImpl();
//		DPoint recPoint1 = null, recPoint2 = null;
		
		String[] p1 = point1.split(",");
		String[] p2 = point2.split(",");
		if (p1.length <= 1 || p2.length <= 1) {
			log.info("对角点必须经纬度都存在！");
			return null;
		}
		String x1 = p1[0];
		String x2 = p2[0];
		String y1 = p1[1];
		String y2 = p2[1];

		double dx1 = Double.parseDouble(x1);
		double dx2 = Double.parseDouble(x2);
		double dy1 = Double.parseDouble(y1);
		double dy2 = Double.parseDouble(y2);
		if (dx1==dx2 || dy1==dy2){
			log.info("传输的点不能为平行点！");
			return null;
		}
		try {
//			recPoint1 = coordService.reverseDeflect(dx1, dy1);
//			recPoint2 = coordService.reverseDeflect(dx2, dy2);
		} catch (Exception e) {
			log.error("设置区域坐标反偏转异常", e);
			e.printStackTrace();
			 
		}
//		if (recPoint1 != null && recPoint2 != null){
//			dx1 = recPoint1.x;
//			dy1 = recPoint1.y;
//			dx2 = recPoint2.x;
//			dy2 = recPoint2.y;
//		}
		
		double max_lnt = (dx1>dx2)?dx1:dx2;
		double max_lat = (dy1>dy2)?dy1:dy2;
		ret[0] = Tools.getNumberFormatString(max_lnt, 6, 6);
		ret[1] = Tools.getNumberFormatString(max_lat, 6, 6);
		
		return ret;
	}
	
	/**
	 * 获取矩形区域最小的经度纬度（即左下点）
	 * @param point1：矩形对角点1,格式: x,y
	 * @param point2：矩形对角点2,格式: x,y
	 *  String[]:[0]为经度，[1]为纬度
	 * by 
	 */
	public static String[] getRecMinLntLat(String point1, String point2) {
		String[] ret = new String[4];
//		CoordinateConvertService coordService = new MapabcCoordServiceImpl();
//		DPoint recPoint1 = null, recPoint2 = null;
		
		String[] p1 = point1.split(",");
		String[] p2 = point2.split(",");
		if (p1.length <= 1 || p2.length <= 1) {
			log.info("对角点必须经纬度都存在！");
			return null;
		}
		String x1 = p1[0];
		String x2 = p2[0];
		String y1 = p1[1];
		String y2 = p2[1];
		

		double dx1 = Double.parseDouble(x1);
		double dx2 = Double.parseDouble(x2);
		double dy1 = Double.parseDouble(y1);
		double dy2 = Double.parseDouble(y2);
		
		if (dx1==dx2 || dy1==dy2){
			log.info("传输的点不能为平行点！");
			return null;
		}
		try {
//			recPoint1 = coordService.reverseDeflect(dx1, dy1);
//			recPoint2 = coordService.reverseDeflect(dx2, dy2);
		} catch (Exception e) {
			log.error("设置区域坐标反偏转异常", e);
			e.printStackTrace();
			 
		}
//		if (recPoint1 != null && recPoint2 != null){
//			dx1 = recPoint1.x;
//			dy1 = recPoint1.y;
//			dx2 = recPoint2.x;
//			dy2 = recPoint2.y;
//		}
		
		double min_lnt = (dx1<dx2)?dx1:dx2;
		double min_lat = (dy1<dy2)?dy1:dy2;
		ret[0] = Tools.getNumberFormatString(min_lnt, 6, 6);
		ret[1] = Tools.getNumberFormatString(min_lat, 6, 6);
		
		return ret;
	}
	/**
	 * 对bcont字节数组进行字节和校验
	 * @param bcont
	 * 
	 */
	public static String getVerfyCode(byte[] bcont) {
		String ret = "";
		byte[] br = bcont;
		int sum = 0;
		for (int i = 0; i < br.length; i++) {
			sum += br[i] & 0xFF;
		}
		ret = Integer.toHexString(sum);

		return ret;
	}
 
	/**
	 * 根据KEY获取Hashtable的value
	 * @param map
	 * @param key
	 * 
	 */
	public static String getHashMapValue(Hashtable map, String key) {
		String ret = null;
		if (map != null && map.size() > 0) {
			String value = map.get(key).toString();
			ret = (value == null || value.trim().length() <= 0) ? key : map
					.get(key).toString();
		} else {
			log.error("动态数据结构配置无数据", null);
		}
		return ret;
	}

	public static byte[] double2Hexstring(double num, int ws) {

		double n = num * 3600000;
		String douHex = Integer.toHexString((int) n);
		while (douHex.length() < ws) {
			douHex = "0" + douHex;
		}
		return fromHexString(douHex);
	}
	
	// 得到当前HHMMSS
	public static String getCurHMS() {
		String ret = "";
		Date date = new Date();
		Calendar calend = Calendar.getInstance();
		calend.setTime(date);
		SimpleDateFormat simpleDate = null;
		simpleDate = new SimpleDateFormat("HHmmss");
		ret = simpleDate.format(date);
		return ret;
	}
	// 得到当前yyyyMMdd
	public static String getCurYyyyMMdd() {
		String ret = "";
		Date date = new Date();
		Calendar calend = Calendar.getInstance();
		calend.setTime(date);
		SimpleDateFormat simpleDate = null;
		simpleDate = new SimpleDateFormat("yyyyMMdd");
		ret = simpleDate.format(date);
		return ret;
	}
	
	// 把 byte数组转换成二进制表示的字符
	public static String bytes2BinaryString(byte[] bs) {
		String ret = "";
		for (int i = 0; i < bs.length; i++) {
			byte b = bs[i];
			String tmp = Integer.toBinaryString(b & 0xff);
			while (tmp.length() < 8) {
				tmp = "0" + tmp;
			}
			ret = ret + tmp;
		}
		return ret;
	}
	
	public static String HexToBinary(String hexString) {
		long l = Long.parseLong(hexString, 16);
		String binaryString = Long.toBinaryString(l);
		int shouldBinaryLen = hexString.length() * 4;
		StringBuffer addZero = new StringBuffer();
		int addZeroNum = shouldBinaryLen - binaryString.length();
		for (int i = 1; i <= addZeroNum; i++) {
			addZero.append("0");
		}
		return addZero.toString() + binaryString;

	}
	
	// 把格式为度分的纬度转换成度
	public static String formatYtoDu1(String DDMMmmmmm) {
		double xy = Double.parseDouble(DDMMmmmmm);
		if (xy == 0) {
			return "0";
		}
		String result = null;
		double DDD = Double.parseDouble(DDMMmmmmm.substring(0, 2));
		double MMmmmm = Double.parseDouble(DDMMmmmmm.substring(2, DDMMmmmmm
				.length()));
		MMmmmm = MMmmmm / 60;
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(6);
		format.setMinimumFractionDigits(6);
		result = format.format(DDD + MMmmmm);
		return result;
	}

	// 把赛格 格式为度分的经度转换成度
	public static String formatXtoDu1(String DDDMMmmmmm) {
		double xy = Double.parseDouble(DDDMMmmmmm);
		if (xy == 0) {
			return "0";
		}
		String result = null;
		double DDD = Double.parseDouble(DDDMMmmmmm.substring(0, 3));
		double MMmmmm = Double.parseDouble(DDDMMmmmmm.substring(3, DDDMMmmmmm
				.length()));
		MMmmmm = MMmmmm / 60;
		NumberFormat format = NumberFormat.getNumberInstance();
		format.setMaximumFractionDigits(6);
		format.setMinimumFractionDigits(6);
		result = format.format(DDD + MMmmmm);
		return result;
	}
	
	/**
	 * 前补0
	 * @param str
	 * @param i
	 * 
	 */
	public static String fillZeroFront(String str, int i){
		while(str.length()<i){
			str = "0"+str;
		}
		return str;
	}
	
	
	
}
