package com.mapabc.gater.directl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class Base64 {

	private static final byte[] encodingTable = { (byte) 'A', (byte) 'B',
			(byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G',
			(byte) 'H', (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L',
			(byte) 'M', (byte) 'N', (byte) 'O', (byte) 'P', (byte) 'Q',
			(byte) 'R', (byte) 'S', (byte) 'T', (byte) 'U', (byte) 'V',
			(byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z', (byte) 'a',
			(byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
			(byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k',
			(byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p',
			(byte) 'q', (byte) 'r', (byte) 's', (byte) 't', (byte) 'u',
			(byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y', (byte) 'z',
			(byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
			(byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9',
			(byte) '+', (byte) '/' };
	private static final byte[] decodingTable;
	static {
		decodingTable = new byte[128];
		for (int i = 0; i < 128; i++) {
			decodingTable[i] = (byte) -1;
		}
		for (int i = 'A'; i <= 'Z'; i++) {
			decodingTable[i] = (byte) (i - 'A');
		}
		for (int i = 'a'; i <= 'z'; i++) {
			decodingTable[i] = (byte) (i - 'a' + 26);
		}
		for (int i = '0'; i <= '9'; i++) {
			decodingTable[i] = (byte) (i - '0' + 52);
		}
		decodingTable['+'] = 62;
		decodingTable['/'] = 63;
	}
	/**
	 * base64编码
	 * @param data：待编码内容
	 * @param offset：起始位置
	 * @return
	 * @author 
	 */
	public static byte[] encode(byte[] data, int offset) {
		byte[] bytes;
		int realCount = data.length - offset;
		int modulus = realCount % 3;
		if (modulus == 0) {
			bytes = new byte[(4 * realCount) / 3];
		} else {
			bytes = new byte[4 * ((realCount / 3) + 1)];
		}
		int dataLength = (data.length - modulus);
		int a1;
		int a2;
		int a3;
		for (int i = offset, j = 0; i < dataLength; i += 3, j += 4) {
			a1 = data[i] & 0xff;
			a2 = data[i + 1] & 0xff;
			a3 = data[i + 2] & 0xff;
			bytes[j] = encodingTable[(a1 >>> 2) & 0x3f];
			bytes[j + 1] = encodingTable[((a1 << 4) | (a2 >>> 4)) & 0x3f];
			bytes[j + 2] = encodingTable[((a2 << 2) | (a3 >>> 6)) & 0x3f];
			bytes[j + 3] = encodingTable[a3 & 0x3f];
		}
		int b1;
		int b2;
		int b3;
		int d1;
		int d2;
		switch (modulus) {
		case 0: /* nothing left to do */
			break;
		case 1:
			d1 = data[data.length - 1] & 0xff;
			b1 = (d1 >>> 2) & 0x3f;
			b2 = (d1 << 4) & 0x3f;
			bytes[bytes.length - 4] = encodingTable[b1];
			bytes[bytes.length - 3] = encodingTable[b2];
			bytes[bytes.length - 2] = (byte) '=';
			bytes[bytes.length - 1] = (byte) '=';
			break;
		case 2:
			d1 = data[data.length - 2] & 0xff;
			d2 = data[data.length - 1] & 0xff;
			b1 = (d1 >>> 2) & 0x3f;
			b2 = ((d1 << 4) | (d2 >>> 4)) & 0x3f;
			b3 = (d2 << 2) & 0x3f;
			bytes[bytes.length - 4] = encodingTable[b1];
			bytes[bytes.length - 3] = encodingTable[b2];
			bytes[bytes.length - 2] = encodingTable[b3];
			bytes[bytes.length - 1] = (byte) '=';
			break;
		}
		return bytes;
	}
	/**
	 * base64解码
	 * @param data：待解码字节
	 * @return
	 * @author 
	 */
	public static byte[] decode(byte[] data) {
		byte[] bytes;
		byte b1;
		byte b2;
		byte b3;
		byte b4;
		data = discardNonBase64Bytes(data);
		if (data[data.length - 2] == '=') {
			bytes = new byte[(((data.length / 4) - 1) * 3) + 1];
		} else if (data[data.length - 1] == '=') {
			bytes = new byte[(((data.length / 4) - 1) * 3) + 2];
		} else {
			bytes = new byte[((data.length / 4) * 3)];
		}
		for (int i = 0, j = 0; i < (data.length - 4); i += 4, j += 3) {
			b1 = decodingTable[data[i]];
			b2 = decodingTable[data[i + 1]];
			b3 = decodingTable[data[i + 2]];
			b4 = decodingTable[data[i + 3]];
			bytes[j] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[j + 2] = (byte) ((b3 << 6) | b4);
		}
		if (data[data.length - 2] == '=') {
			b1 = decodingTable[data[data.length - 4]];
			b2 = decodingTable[data[data.length - 3]];
			bytes[bytes.length - 1] = (byte) ((b1 << 2) | (b2 >> 4));
		} else if (data[data.length - 1] == '=') {
			b1 = decodingTable[data[data.length - 4]];
			b2 = decodingTable[data[data.length - 3]];
			b3 = decodingTable[data[data.length - 2]];
			bytes[bytes.length - 2] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 1] = (byte) ((b2 << 4) | (b3 >> 2));
		} else {
			b1 = decodingTable[data[data.length - 4]];
			b2 = decodingTable[data[data.length - 3]];
			b3 = decodingTable[data[data.length - 2]];
			b4 = decodingTable[data[data.length - 1]];
			bytes[bytes.length - 3] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 2] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[bytes.length - 1] = (byte) ((b3 << 6) | b4);
		}
		return bytes;
	}
	/**
	 * base64解码
	 * @param data：字符串
	 * @return
	 * @author 
	 */
	public static byte[] decode(String data) {
		byte[] bytes;
		byte b1;
		byte b2;
		byte b3;
		byte b4;
		data = discardNonBase64Chars(data);
		if (data.charAt(data.length() - 2) == '=') {
			bytes = new byte[(((data.length() / 4) - 1) * 3) + 1];
		} else if (data.charAt(data.length() - 1) == '=') {
			bytes = new byte[(((data.length() / 4) - 1) * 3) + 2];
		} else {
			bytes = new byte[((data.length() / 4) * 3)];
		}
		for (int i = 0, j = 0; i < (data.length() - 4); i += 4, j += 3) {
			b1 = decodingTable[data.charAt(i)];
			b2 = decodingTable[data.charAt(i + 1)];
			b3 = decodingTable[data.charAt(i + 2)];
			b4 = decodingTable[data.charAt(i + 3)];
			bytes[j] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[j + 1] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[j + 2] = (byte) ((b3 << 6) | b4);
		}
		if (data.charAt(data.length() - 2) == '=') {
			b1 = decodingTable[data.charAt(data.length() - 4)];
			b2 = decodingTable[data.charAt(data.length() - 3)];
			bytes[bytes.length - 1] = (byte) ((b1 << 2) | (b2 >> 4));
		} else if (data.charAt(data.length() - 1) == '=') {
			b1 = decodingTable[data.charAt(data.length() - 4)];
			b2 = decodingTable[data.charAt(data.length() - 3)];
			b3 = decodingTable[data.charAt(data.length() - 2)];
			bytes[bytes.length - 2] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 1] = (byte) ((b2 << 4) | (b3 >> 2));
		} else {
			b1 = decodingTable[data.charAt(data.length() - 4)];
			b2 = decodingTable[data.charAt(data.length() - 3)];
			b3 = decodingTable[data.charAt(data.length() - 2)];
			b4 = decodingTable[data.charAt(data.length() - 1)];
			bytes[bytes.length - 3] = (byte) ((b1 << 2) | (b2 >> 4));
			bytes[bytes.length - 2] = (byte) ((b2 << 4) | (b3 >> 2));
			bytes[bytes.length - 1] = (byte) ((b3 << 6) | b4);
		}
		// for(int i=0;i<bytes.length;i++) System.out.println(","+bytes[i]);
		return bytes;
	}
	
	private static byte[] discardNonBase64Bytes(byte[] data) {
		byte[] temp = new byte[data.length];
		int bytesCopied = 0;
		for (int i = 0; i < data.length; i++) {
			if (isValidBase64Byte(data[i])) {
				temp[bytesCopied++] = data[i];
			}
		}
		byte[] newData = new byte[bytesCopied];
		System.arraycopy(temp, 0, newData, 0, bytesCopied);
		return newData;
	}

	private static String discardNonBase64Chars(String data) {
		StringBuffer sb = new StringBuffer();
		int length = data.length();
		for (int i = 0; i < length; i++) {
			if (isValidBase64Byte((byte) (data.charAt(i)))) {
				sb.append(data.charAt(i));
			}
		}
		return sb.toString();
	}

	private static boolean isValidBase64Byte(byte b) {
		if (b == '=') {
			return true;
		} else if ((b < 0) || (b >= 128)) {
			return false;
		} else if (decodingTable[b] == -1) {
			return false;
		}
		return true;
	}
	/**
	 * base64编码
	 * @param data：待编码字符串
	 * @param charset：字符集
	 * @return
	 * @throws Exception
	 * @author 
	 */
	public static String encode(String data, String charset) throws Exception {
		// byte[] result = (data.getBytes("Unicode"));
		if (data == null || data.length() == 0)
			return data;
		int offset = 0;
		// getBytes("unicode")转完后会在前头加上两字节”FE“
		byte[] result = encode(data.getBytes(charset), offset);
		StringBuffer sb = new StringBuffer(result.length);
		for (int i = 0; i < result.length; i++)
			sb.append((char) result[i]);
		return sb.toString();
	}
	/**
	 * base64解码
	 * @param data：带解码字符串
	 * @param charset：字符集
	 * @return
	 * @throws Exception
	 * @author 
	 */
	public static String decode(String data, String charset) throws Exception {
		if (data == null || data.length() == 0)
			return data;
		return new String(Base64.decode(data), charset);
	}

	public static String getBASE64(String s) {
		if (s == null)
			return null;
		return new BASE64Encoder().encode(s.getBytes());
	}

	/**
	 * 将 BASE64 编码的字符串 s 进行解码
	 */
	public static String getFromBASE64(String s) {
		if (s == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 对象转Byte数组
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static byte[] objectToBytes(Object obj) throws Exception {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream sOut = new ObjectOutputStream(out);
		sOut.writeObject(obj);
		sOut.flush();
		byte[] bytes = out.toByteArray();

		return bytes;
	}

	/**
	 * 字节数组转对象
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static Object bytesToObject(byte[] bytes) throws Exception {

		// byte转object
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		ObjectInputStream sIn = new ObjectInputStream(in);
		return sIn.readObject();

	}

	/**
	 * Description: 编码
	 * 
	 * @Version1.0 Jan 2, 2009 11:48:48 PM by 创建
	 * @param pBytes
	 * @return
	 */
	public static String base64Encode(byte[] pBytes) {
		BASE64Encoder base64 = new BASE64Encoder();
		return base64.encode(pBytes);
	}

	/**
	 * Description: 编码
	 * 
	 * @Version1.0 Jan 2, 2009 11:49:02 PM by 创建
	 * @param str
	 * @return
	 */
	public static String base64Encode(String str) {
		try {
			return base64Encode(str.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Description: 解码
	 * 
	 * @Version1.0 Jan 2, 2009 11:49:08 PM by 创建
	 * @param pContent
	 * @return
	 * @throws IOException
	 */
	public static byte[] base64Decode(String pContent) throws IOException {
		BASE64Decoder base64 = new BASE64Decoder();
		byte[] bs = base64.decodeBuffer(pContent);

		return bs;
	}

	/**
	 * @Description: 编码码
	 * @param pContent
	 * @return
	 * @throws IOException
	 */
	public static String base64encode(byte[] bs) throws IOException {

		BASE64Encoder be = new BASE64Encoder();
		String bb = be.encodeBuffer(bs);

		return bb;
	}



	public static void main(String[] args) throws Exception {
//		String data = "lingtu";
//		byte[] dl = data.getBytes("UTF-16");
//		String hexs = "527745414157744b442b41414141414261306f5034414143574655416b513d3d";
		// 7e 47 01 0001 6b4a0fe0 00000001 6b4a0fe0 0002 55f0 0091 7f
//		String hex = "7e470100016b4a0fe0000000016b4a0fe00002585500917f";
//		java.nio.ByteBuffer bbuf = java.nio.ByteBuffer.allocate(2);
//		bbuf.put((byte) 0x55);
//		bbuf.put((byte) 0xf0);
//
//		Base64.GMEnCrypt(Tools.fromHexString(hex));//EnCrypt(1800015584, bbuf.array(), 2);
		//       
//		System.out
//				.println(Tools
//						.bytesToHexString(Base64
//								.decode(Tools
//										.fromHexString("7E527745414157744B442B41414141414261306F5034414143535655536F673D3D7F"))));
//
//		System.out.println(Integer.parseInt("6b4a0fe0", 16));
		//       
		// byte[] m_ByteDatas = Tools.fromHexString(hexs);
		//       
		// int Key = (((int) m_ByteDatas[5]) << 24)
		// + (((int) m_ByteDatas[6]) << 16)
		// + (((int) m_ByteDatas[7]) << 8)
		// + m_ByteDatas[8];
		// System.out.println(Key);

	}

}
