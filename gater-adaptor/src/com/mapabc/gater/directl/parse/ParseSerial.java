/**
 * 
 */
package com.mapabc.gater.directl.parse;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.directl.Log;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.parse.service.ParseService; 
 

/**
 * @author shiguang.zhou
 * 
 */
public class ParseSerial extends ParseBase  implements ParseService{
	private static org.apache.commons.logging.Log log = LogFactory
	.getLog(ParseSerial.class);
	public ArrayList<ParseBase> parseHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}

	public ArrayList<ParseBase> parseModata(byte[] moBytes) {
		// TODO Auto-generated method stub
		return null;
	}

	public ParseBase parseSingleGprs(byte[] moBytes) {
		String hexString = Tools.bytesToHexString(moBytes);
		this.parseGPRS(hexString);
		return this;
	}

	public ParseBase parseSingleHttpGrps(HttpServletRequest request,
			HttpServletResponse response, byte[] cont) {
		// TODO Auto-generated method stub
		return null;
	}

	 
	public void parseGPRS(String hexString) {
		// TODO Auto-generated method stub
		log.info("串口数据：" + hexString);
		byte[] cont = Tools.fromHexString(hexString);
		try {
			log.info(
					"ASCII串口数据：" + new String(cont, "ISO8859-1"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 
		String time = Tools.formatDate2Str(new Date(), "yyyy-MM-dd HH:mm:ss");
		this.setTime(time);

		byte[] blen = new byte[2];
		System.arraycopy(cont, 5, blen, 0, 2);
		long len = Tools.byte2Long(blen);

		byte[] b_device_id = new byte[3];
		System.arraycopy(cont, 7, b_device_id, 0, 3);

		long bitID = Tools.byte2Long(b_device_id);
		this.setDeviceSN(String.valueOf(bitID));

		byte[] catalog = new byte[] { cont[10] };
		int intCata = Tools.byte2Int(catalog);

		if (hexString.startsWith("2444575858")) {// 位置信息

			this.parseCatalog(Tools.bytes2BinaryString(catalog));

			byte[] baddr = new byte[3];
			System.arraycopy(cont, 11, baddr, 0, 3);
			long laddr = Tools.byte2Long(baddr);// 查询地址

			byte[] bx = new byte[] { cont[18] };
			long lx = Tools.byte2Long(bx); // 度
			byte[] bxfen = new byte[] { cont[19] };
			long lxfen = Tools.byte2Long(bxfen);// 分
			byte[] bxs = new byte[] { cont[20] };
			long lxs = Tools.byte2Long(bxs); // 秒
			byte[] bmills = new byte[] { cont[21] };
			long lmills = Tools.byte2Long(bmills); // 毫秒
			double lng = lx + lxfen / 60.0 + lxs / 60.0 / 60.0 + (lmills * 0.1)
					/ 60.0 / 60.0 / 60.0;
			NumberFormat nf = NumberFormat.getInstance();
			nf.setMaximumFractionDigits(6);

			String xlng = nf.format(lng);
			this.setCoordX(xlng);

			byte[] by = new byte[] { cont[22] };
			long ly = Tools.byte2Long(by); // 度
			byte[] byfen = new byte[] { cont[23] };
			long lyfen = Tools.byte2Long(byfen);// 分
			byte[] bys = new byte[] { cont[24] };
			long lys = Tools.byte2Long(bys); // 秒
			byte[] bymills = new byte[] { cont[25] };
			long lymills = Tools.byte2Long(bymills); // 毫秒
			double lat = ly + lyfen / 60.0 + lys / 60.0 / 60.0
					+ (lymills * 0.1) / 60.0 / 60.0 / 60.0;
			String ylat = nf.format(lat);
			this.setCoordY(ylat);

			byte[] bheight = new byte[2];
			bheight[0] = cont[26];
			bheight[1] = cont[27];
			String hexH = Tools.bytes2BinaryString(bheight);
			String flag = hexH.substring(0, 2);// 00为正 01为负
			String sh = hexH.substring(2);
			// byte bh = Byte.parseByte(hexH, 10);
			long h = Tools.byte2Long(bheight);// 高度
			this.setAltitude(h + "");
 
		} else if (hexString.startsWith("2454585858")) {// 通信信息
			byte[] msgleng = new byte[2];// 电文长度
			System.arraycopy(cont, 16, msgleng, 0, msgleng.length);
			try {

				String bcds = Tools.bcd2Str(msgleng);
				String hexcont = hexString
						.substring(38, hexString.length() - 4);
				byte[] msgcont = Tools.fromHexString(hexcont);

				String content = null;

				content = new String(msgcont, "GB18030");
				log.info(
						this.getDeviceSN() + "上传信息内容：" + content + ",上传时间："
								+ time);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		log.info(
				"串口定位数据：DEVICE_ID=" + this.getDeviceSN() + ",lng="
						+ this.getCoordX() + ",lat=" + this.getCoordY()
						+ ",height=" + this.getAltitude() + ",time=" + time);

	}

	// 解析通信类别
	private String parseMsgType(String s) {
		return null;
	}

	// 解析信息类别
	private String parseCatalog(String s) {
		String ret = "";
		String type = s.substring(2, 3);
		if (type.equals("0"))
			log.info(
					"申请定位用户的位置信息,deviceid=" + this.getDeviceSN());
		else
			log.info(
					"被指挥型用户所查询用户的位置信息,deviceid=" + this.getDeviceSN());
		String my = s.substring(3, 4);
		if (my.equals("0")) {
			log.info("密钥：无,deviceid=" + this.getDeviceSN());
		} else {
			log.info("密钥：有,deviceid=" + this.getDeviceSN());
		}
		String jingd = s.substring(4, 5);
		if (jingd.equals("0")) {
			log.info("精度：20米,deviceid=" + this.getDeviceSN());
		} else {
			log.info("精度：100米,deviceid=" + this.getDeviceSN());
		}
		String jinji = s.substring(5, 6);
		if (jinji.equals("0")) {
			log.info("紧急定位：否,deviceid=" + this.getDeviceSN());
		} else {
			log.info("紧急定位：是,deviceid=" + this.getDeviceSN());
		}
		String dzj = s.substring(6, 7);
		if (dzj.equals("0")) {// 用以提示用户本帧传输的定位信息是否为正确的定位信息
			log.info("多值解：否,deviceid=" + this.getDeviceSN());
		} else {
			log.info("多值解：是,deviceid=" + this.getDeviceSN());
		}

		String heightType = s.substring(7, 8);
		if (heightType.equals("0")) {
			log.info("普通高度,deviceid=" + this.getDeviceSN());
		} else {
			log.info("高空,deviceid=" + this.getDeviceSN());
		}

		return ret;
	}

	 

}
