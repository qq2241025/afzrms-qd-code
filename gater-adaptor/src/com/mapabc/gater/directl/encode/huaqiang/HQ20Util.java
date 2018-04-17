package com.mapabc.gater.directl.encode.huaqiang;

 

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class HQ20Util {
  private static char[] CHANG_256_64;
  private static String protocolHead = "*HQ20";
  private static String protocolEnd = "#";
  private static String spProtocolHead = "[HQ20";
  private static String spProtocolEnd = "]";

  static { //初始化CHANG_256_64
    CHANG_256_64 = new char[64];
    for (int i = 0; i <= 9; i++) {
      CHANG_256_64[i] = (char) ('0' + i);
    }
    CHANG_256_64[10] = ':';
    CHANG_256_64[11] = ';';
    for (int i = 0; i < 26; i++) {
      CHANG_256_64[i + 12] = (char) ('A' + i);
    }
    for (int i = 0; i < 26; i++) {
      CHANG_256_64[i + 38] = (char) ('a' + i);
    }
  }

  /**
   * 256<-->64转换算法
   * @param text String 原始数据
   * @return String 转换后的数据
   */
  public static String to64Code(String text) {
    //rebuild short message
    StringBuffer hexbuf = new StringBuffer();
    StringBuffer sixbuf = new StringBuffer();
    byte[] bytes = text.getBytes();
    for (int i = 0; i < bytes.length; i++) {
      String str = Integer.toHexString( (char) bytes[i]);
      if (str.length() == 2) {
        hexbuf.append(str);
      } else {
        hexbuf.append(str.substring(2));
      }
    }
    int loopnum = hexbuf.length() / 3;
    int leave = hexbuf.length() % 3;
    for (int i = 0; i < loopnum; i++) {
      int n = Integer.parseInt(hexbuf.substring(i * 3, i * 3 + 3), 16);
      sixbuf.append( (char) CHANG_256_64[n / 64]);
      sixbuf.append( (char) CHANG_256_64[n % 64]);
    }
    if (leave > 0) {
      int n = Integer.parseInt(hexbuf.substring(loopnum * 3), 16);
      if (leave == 1) {
        n *= 4;
        sixbuf.append( (char) CHANG_256_64[n]);
      } else {
        n *= 16;
        sixbuf.append( (char) CHANG_256_64[n / 64]);
        sixbuf.append( (char) CHANG_256_64[n % 64]);
      }
    }
    return sixbuf.toString();

  }

  /**
   * 生成控制指令
   * @param cmdKey String 原始控制指令
   * @param save boolean 是否保存
   * @param replay boolean 是否需要回应
   * @return String 加了包头包尾的指令
   */
  public static String makeCommandStr(String cmdKey, boolean save, boolean replay) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(protocolHead);
    buffer.append(save ? '1' : '0');
    buffer.append(replay ? '1' : '0');
    buffer.append(cmdKey);
    buffer.append(protocolEnd);
    return buffer.toString();
  }

  /**
   * 生成控制指令
   * @param cmdKey String 原始控制指令
   * @param save boolean 是否保存
   * @param replay boolean 是否需要回应
   * @return String 加了包头包尾的指令
   */
  public static String makespCommandStr(String cmdKey, boolean save, boolean replay) {
    StringBuffer buffer = new StringBuffer();
    buffer.append(spProtocolHead);
    buffer.append(save ? '1' : '0');
    buffer.append(replay ? '1' : '0');
    buffer.append(cmdKey);
    buffer.append(spProtocolEnd);
    return buffer.toString();
  }

  /**
   * 将字符串扩充成指定长度，扩充的方式是在前面增加'0'
   * @param org String
   * @param length int
   * @return String
   */
  public static String extentString(String org, int length) {
    if (org == null) {
      return null;
    }

    if (org.length() == length) {
      return org;
    }

    StringBuffer buf = new StringBuffer();
    int num = length - org.length();
    for (int i = 0; i < num; i++) {
      buf.append('0');
    }
    buf.append(org);
    return buf.toString();
  }

  /**
   * 格式化经纬度坐标，使其格式为纬度：“aa\u00BAaa.aaaa'”经度：“ooo\u00BAoo.oooo'”
   * @param value double 经纬度值
   * @param type int 0代表经度，1代表纬度
   * @return String 格式化后的字符串
   */
  public static String getLatLongString(double value, int type) {
    
    int du = (int) value;
    int length = (type == 0) ? 3 : 2;
    String sdu = extentString(Integer.toString(du), length);
    int fen = (int) ( (value - du) * 60.0);
    String sfen = extentString(Integer.toString(fen), 2);
    double ffen =  (value - du) * 60.0 - fen;
    String subfen = String.valueOf(ffen);
    String ret = sdu+sfen+subfen.substring(subfen.indexOf(".")+1);
    ret = ret.substring(0, 8);
   
    return ret;
  }

  /**
   * 从字符串中解析出经纬度值
   * @param value String 包含有经纬度值的字符串
   * @param type int 0代表经度，1代表纬度
   * @return double 精度或者纬度
   */
  public static double getLatLongValue(String value, int type) {
    if (type == 0) { //解析经度
      int du = Integer.parseInt(value.substring(0, 3));
      int fen = Integer.parseInt(value.substring(3, 5));
      int miao = Integer.parseInt(value.substring(5));
      double longitude = du + (fen + miao / 10000.0) / 60.0;
      return longitude;
    }
    if (type == 1) {
      int du = Integer.parseInt(value.substring(0, 2));
      int fen = Integer.parseInt(value.substring(2, 4));
      int miao = Integer.parseInt(value.substring(4));
      double latitude = du + (fen + miao / 10000.0) / 60.0;
      return latitude;
    }
    return 0.0;
  }

  /**
   * 从节换算成公里/小时单位
   * 一节＝一海里/小时＝1.852公里/小时
   * [节]：为轮船航行速度的单位，后来，也用於风及洋流的速度。
   * “节”的代号是英文“Knot”，是指地球子午线上纬度1分的长度，由于地球略呈椭球体状，
   * 不同纬度处的1分弧度略有差异。在赤道上1海里约等于1843米；纬度45°处约等于1852.2 米，
   * 两极约等于1861.6 米。1929年国际水文地理学会议，通过用1分平均长度1852米作为1海里；
   * 1948年国际人命安全会议承认，1852米或6O76.115英尺为1海里，
   * 故国际上采用1852米为标准海里长度。
   * 中国承认这一标准，用代号“M”表示。
   * @param value double 以节为单位表示的数值
   * @return double 以公里/小时为单位表示的数值
   */
  public static double Knot2KMHOUR(double value) {
    return value * 1.852;
  }

  /**
   * 从公里/小时换算成节单位
   * @param value double 以公里/小时为单位表示的数值
   * @return double 以节为单位表示的数值
   */
  public static double KMHOUR2Knot(double value) {
    return value / 1.852;
  }

  public static void main(String[] args) {
    HQ20Util hq20util = new HQ20Util();
//        String msg = "深圳非典疫情控制的很好，至今无一例医务人员受到感染";
    String msg = "刘A";
    String msgCode2 = HQ20Util.to64Code(msg);
    int n = 0;

    //测试经纬度编码和解码
    double latitude = 12.2067209;
    double longitude = 23.5867292;
    String longStr = HQ20Util.getLatLongString(latitude, 1);
    double longCheck = HQ20Util.getLatLongValue(longStr, 0);
    if (Math.abs(longCheck - longitude) < 0.00001) {
      System.out.println("check longitude change ok");
    } else {
      System.out.println("check longitude change error");
    }

    String latStr = HQ20Util.getLatLongString(latitude, 1);
    double latCheck = HQ20Util.getLatLongValue(latStr, 1);
    if (Math.abs(latCheck - latitude) < 0.00001) {
      System.out.println("check latitude change ok");
    } else {
      System.out.println("check latitude change error");
    }

  }

/**
 * @return the protocolHead
 */
public static String getProtocolHead() {
	return protocolHead;
}

/**
 * @return the protocolEnd
 */
public static String getProtocolEnd() {
	return protocolEnd;
}

/**
 * @param protocolHead the protocolHead to set
 */
public static void setProtocolHead(String protocolHead) {
	HQ20Util.protocolHead = protocolHead;
}

/**
 * @param protocolEnd the protocolEnd to set
 */
public static void setProtocolEnd(String protocolEnd) {
	HQ20Util.protocolEnd = protocolEnd;
}
}
