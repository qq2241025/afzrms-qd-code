/**
 * 
 */
package com.mapabc.gater.directl.encode;

import org.apache.commons.logging.LogFactory;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.Tools;
import com.mapabc.gater.directl.bean.command.TStructions;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.lbsgateway.GBLTerminalList;
import com.mapabc.gater.lbsgateway.TerminalTypeList;
import com.mapabc.gater.lbsgateway.bean.TerminalTCPAddress;
import com.mapabc.gater.lbsgateway.bean.TerminalTypeBean;
import com.mapabc.gater.lbsgateway.bean.TerminalUDPAddress;

/**
 * @author 
 * 
 */
public class RequestUtil {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(RequestUtil.class);

	/**
	 * 设置Request 构建的命令类型、命令ID
	 * 
	 * @param req
	 * @param cmdType
	 * @param cmdId
	 * @return
	 * @author 
	 */
	public static Request getDealRequest(Request req, String cmdType,
			String cmdId) {

		String seqs = getReqData(req, "sequence");
		if (seqs != "") {

			req.setSequence(seqs);
		}
		String isSynchs = getReqData(req, "issynch");
		if (isSynchs.equals("0")) {
			req.setSynch(false);
		}
		if (isSynchs.equals("1")) {
			req.setSynch(true);
		}
		req.setCmdType(cmdType);
		req.setCmdId(cmdId);

		return req;
	}

	/**
	 * 获取参数值
	 * 
	 * @param req
	 * @param key
	 * @return
	 * @author 
	 */
	public static String getReqData(Request req, String key) {
		String ret = "";

		ret = (String) req.getDatas().get(key) == null ? "" : (String) req
				.getDatas().get(key);

		return ret;
	}

	public static void reSendIns(TStructions[] insList, Object gpsClient) {
		for (TStructions bean : insList) {
			String ins = bean.getInstruction();
			try {
				String typeCode = GBLTerminalList.getInstance()
						.getTermTypeCode(bean.getDeviceId());
				TerminalTypeBean typebean = TerminalTypeList.getInstance()
						.getTerminalType(typeCode);
				String mttype = null;
				if (typebean != null)
					mttype = typebean.getMtType();
				if (log.isInfoEnabled())
				log.info(bean.getDeviceId() + " 终端类型：" + typeCode + " 下发类型："
						+ mttype);
				int result = -1;
				if (mttype != null) {
					if (gpsClient instanceof TerminalUDPAddress) {
						if (mttype.equals("1")) {
							TerminalUDPAddress gprs = (TerminalUDPAddress) gpsClient;
							result = gprs.sendUDPByteData(Tools
									.fromHexString(ins));
						}
					} else if (gpsClient instanceof TerminalTCPAddress) {
						if (mttype.equals("0")) {
							TerminalTCPAddress gprs = (TerminalTCPAddress) gpsClient;
							result = gprs.sendByteArrayData(Tools
									.fromHexString(ins));
						}
					}

					DBService dbservice = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
					try {
						dbservice.updateInstructions(bean.getId(), ins, bean
								.getType(), null, new java.sql.Date(System
								.currentTimeMillis()), String.valueOf(Integer
								.parseInt(bean.getSendCount()) + 1), String
								.valueOf(result));
						if (log.isInfoEnabled())
						log.info(bean.getDeviceId() + " 上线补发指令：" + ins
								+ ",发送结果：" + result + ",is udp:"
								+ (gpsClient instanceof TerminalUDPAddress)
								+ ",is tcp:"
								+ (gpsClient instanceof TerminalTCPAddress));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

}
