/**
 *  通讯协议切分
 */
package com.mapabc.gater.util;

import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author 
 * 
 */
public class ProtocalSplitUtil {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(ProtocalSplitUtil.class);

	// 把接收到的数据按照协议的标识头尾来进行切分,分成多行.
	// 注意：禁止标识尾有多个标识符号;终端上行的数据除头尾外的内容如果包含头尾必须做转义
	public static String[] getSocketLines(String hex, String hexstart,
			String hexend) {
		String[] ret = null;
		String[] retArr = null;
		if ((hexstart == null || hexstart.trim().equals(""))
				&& (hexend == null || hexend.trim().equals(""))) {// 无明确协议头和尾的协议
			retArr = new String[1];
			retArr[0] = hex;
			return retArr;
		}

		List<String> splitCollection = new ArrayList<String>();
		try {
			String tmpstart = hexstart;
			String tmpend = hexend;
			if (tmpstart != null && tmpstart.split(",").length > 0) {
				String[] tmplines = tmpstart.split(",");
				for (int i = 0; i < tmplines.length; i++) {
					String tmpline = tmplines[i];

					if (tmpline == null || tmpline.trim().length() == 0)
						continue;
					// if (hex.startsWith(tmpline)) {//
					ret = getsplitLines(hex, tmpline, tmpend); // 多个头和尾进行匹配

					if (ret != null && ret.length > 0) {
						for (int j = 0; j < ret.length; j++) {
							splitCollection.add(ret[j]);
						}
					}
					// }
				}
			} else {
				ret = getsplitLines(hex, tmpstart, tmpend);
				if (ret != null && ret.length > 0) {
					for (int j = 0; j < ret.length; j++) {
						splitCollection.add(ret[j]);
					}
				}
			}

			if (splitCollection.size() > 0) {
				retArr = new String[splitCollection.size()];
				splitCollection.toArray(retArr);
			}
			if (retArr.length>1){
				 log.info("分割成多行数据成功!一共:" + retArr.length + "行数据.");
				log.info("分割后的多条协议："+Arrays.toString(retArr));
			   
			}
		} catch (Exception ex) {
			String errinfo = "分割数据出现错误:" + ex.getMessage();
			errinfo += "数据:" + hex + "标识头:" + hexstart + "标识尾:" + hexend;
			log.info(errinfo);
			log.error(errinfo, ex);
			String[] shytdata = new String[1];
			shytdata[0] = hex;
			return shytdata;
		}
		return retArr;
	}

	// 切分函数
	private static String[] getsplitLines(String hex, String hexstart,
			String hexend) {
		if (hex == null || (hex.length() % 2 != 0))
			return null;

		String cData = addSpaceToHexstring(hex);
		String cStartData = null;
		String cEndData = null;

		if (hexstart != null && hexstart.trim().length() > 0
				&& !hexstart.equals("null")) {
			cStartData = addSpaceToHexstring(hexstart);
		}
		if (hexend != null && hexend.trim().length() > 0
				&& !hexend.equals("null")) {
			cEndData = addSpaceToHexstring(hexend);
		}

		String[] rettmp = null;
		String strTmp = "";
		int f = -1;
		ArrayList<String> spltS = new ArrayList<String>();

		if (cStartData != null && cEndData != null
				&& cStartData.trim().length() > 0
				&& cEndData.trim().length() > 0) {
			// 包头包尾,删除包头前和包尾后的数据
			int m = cData.indexOf(cStartData);
			int n = cData.lastIndexOf(cEndData);
			if (m == -1 || n == -1)
				return null;

			cData = cData.substring(m, cData.lastIndexOf(cEndData)
					+ cEndData.length());
			// rettmp = new String[1];
			// rettmp[0] = cData;

			{
				int tempS = cData.indexOf(cStartData);
				int tempE = cData.indexOf(cEndData);

				String tmpData = null;

				while (cData.length() >= (cStartData.length() + cEndData
						.length())) {
					if (tempS != -1 && tempE != -1 && tempS != tempE) {

						tmpData = cData.substring(tempS, tempE
								+ cEndData.length());

						cData = cData.substring(tmpData.length());

						tempS = cData.indexOf(cStartData);
						tempE = cData.indexOf(cEndData);

						// if (tempE != -1) {
						// if (tempS > tempE) {// 下一组开头标识并非指定的开头标识，即结尾标识在开头标识之前
						//
						// String partTmp = cData.substring(0, tempS
						// + cEndData.length());
						// tmpData += partTmp; // 进行拼接
						//
						// cData = cData.substring(tmpData.length());
						// tempS = cData.indexOf(cStartData);
						// tempE = cData.indexOf(cEndData);
						//
						// }
						// }
						spltS.add(tmpData);

					} else {// 不完整数据忽略

						cData = cData.substring(tempE + cEndData.length());

						tempE = cData.indexOf(cEndData);
						if (log.isDebugEnabled())
						log.debug("head:" + cStartData + ",end:" + cEndData
								+ "不完整数据：" + cData);
						// continue;
					}

				}

			}

			f = 0;
		} else if (cStartData != null && cStartData.trim().length() > 0) {
			// 包头,删除包头前的数据
			int m = cData.indexOf(cStartData);
			if (m == -1)
				return null;
			cData = cData.substring(cData.indexOf(cStartData), cData.length());
			rettmp = cData.split(cStartData);

			for (int sindex = 0; sindex < rettmp.length; sindex++) {
				if (rettmp[sindex].length() > 0) {
					spltS.add(cStartData+rettmp[sindex]);
				}
			}
			f = 1;
		} else if (cEndData != null && cEndData.trim().length() > 0) {
			// 包尾,删除包尾后的数据

			int n = cData.lastIndexOf(cEndData);
			if (n == -1)
				return null;

			cData = cData.substring(0, cData.lastIndexOf(cEndData)
					+ cEndData.length());
			rettmp = cData.split(cEndData);

			for (int eindex = 0; eindex < rettmp.length; eindex++) {
				if (rettmp[eindex].length() > 0) {
					spltS.add(rettmp[eindex]+cEndData);
				}
			}

			f = 2;
		}

		String[] retArry = new String[spltS.size()];

		for (int i = 0; i < spltS.size(); i++) {
			String tmp = spltS.get(i);
			String tmpRmTrim = "";
			for (int k = 0; k < tmp.length(); k = k + 3) {
				tmpRmTrim += tmp.substring(k, k + 3).trim();
			}
			retArry[i] = tmpRmTrim;
		}

		return retArry;

	}

	// 把十六进制度的字符串每个字节之间与' ' 隔开。
	private static String addSpaceToHexstring(String hex) {
		if (hex == null || hex.trim().length() == 0 || (hex.length() % 2 != 0)) {
			return null;
		}
		char c = ' ';
		char[] cs = hex.toCharArray();
		CharBuffer cb = java.nio.CharBuffer.wrap(cs);
		int size = cs.length * 2;
		CharBuffer result = CharBuffer.allocate(size);
		for (int i = 0; i < hex.length(); i++) {
			result.put(cb.get(i));
			if (i > 0 && (i % 2 == 1)) {
				result.put(c);
			}
		}
		result.flip();
		return result.toString();
	}

	public static void main(String[] args) {

		String org = "2a48512c4923 2a48512c5923 2a54482a5448";
		// "2490906186040541491608093958842000116179818e000000fffffbffff005a" +
		// "2490906186040541541608093958842000116179818e000000fffffbffff005b" +
		// "2490906186040541591608093958842000116179818e000000fffffbffff005c" +
		// "2490906186040542041608093958842000116179818e000000fffffbffff005d" +
		// "2490906186040542091608093958842000116179818e000000fffffbffff005e" +
		// "2490906186040542141608093958842000116179818e000000fffffbffff005f" +
		// "2490906186040542191608093958842000116179818e000000fffffbffff0060" +
		// "2490906186040542241608093958842000116179818e000000fffffbffff0061" +
		// "2490906186040542291608093958842000116179818e000000fffffbffff0062" +
		// "2490906186040542341608093958842000116179818e000000fffffbffff0063" +
		// "2490906186040542391608093958842000116179818e000000fffffbffff0064";
		// String org =
		// "41205520353535353a3133343036333333343432203039313231313039333832302031203939203864203c1c7b1db1f0d2b24d286af87dc17c32a0ebb79f50049232a01f22720b16f5edbddf2ff2f1fe55fdabbfbbd773e673fd9ff15774eceeccc5f64eefef67da59fdbd92e8fa2ec2dcfdd3dbbba7a63bafa377f67a26de5b0f6e6c7ab9372ee2aec1cb55bfa9dde04a97324e5f2c8ffbab659e5b8259e021b517a11a3517628c84e42a1d4c455c62bd1b43147fbf798ed61b350915c828544750449a44681d2451dacd20d0a1a919cd385927f2c2da15b175f6f6df7d8d99dd38ac26e9eabea8db7b7faab71767e7f39d65b17ad30fb2ea32b5f51b276b65be74fccaa5dbdb6aa25dcad4067c849b67294f0e37c5f68948624434da203e14924c485645014b12a140ae3e20613765320d0a";

		String[] tmpsp = "2323#null#null".split("#");
		String gpsCode = tmpsp[0];
		if (!tmpsp[1].equalsIgnoreCase("null"))
			System.out.println(tmpsp[1]);
		if (!tmpsp[2].equalsIgnoreCase("null"))
			System.out.println(tmpsp[2]);

		String[] s = ProtocalSplitUtil.getSocketLines(org,
				"null", "23");
		System.out.println(s.length);
	}

}
