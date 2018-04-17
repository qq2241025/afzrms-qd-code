/**
 * 
 */
package com.mapabc.gater.lbsgateway.service.flowvolume;

import org.apache.commons.logging.LogFactory;

import com.mapabc.gater.lbsgateway.bean.TFlowVolume;

/**
 * 流量计算
 * 
 * @author 
 * 
 */
public class FlowVolumeCalculate {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(FlowVolumeCalculate.class);

	public static void flowVolume(String deviceId, byte[] socketBytes,
			long flowSize, String actionType, String linkType) {
		TFlowVolume flowVolume = null;
		
		if (actionType.equals("0")) {
			flowVolume = FlowHashtable.getInstance().getMoFlowVolume(deviceId);
		} else if (actionType.equals("1")) {
			flowVolume = FlowHashtable.getInstance().getMtFlowVolume(deviceId);
		}
		
		if (flowVolume == null) {
			flowVolume = new TFlowVolume();
			flowVolume.setActionType(actionType);
			flowVolume.setDeviceId(deviceId);
			flowVolume.setFlowSize(flowSize);
			// flowVolume.setLinkType("0");
		} else {
			flowVolume.setFlowSize(flowVolume.getFlowSize() + flowSize);
		}
		
		if (actionType.equals("0")) {
			FlowHashtable.getInstance().addMoFlowObj(deviceId, flowVolume);
		} else if (actionType.equals("1")) {
			FlowHashtable.getInstance().addMtFlowObj(deviceId, flowVolume);
		}
		

		if (linkType.equals("0") && actionType.equals("0")) {
			log.info(
					deviceId + " 当前缓存上行数据总流量为：" + flowVolume.getFlowSize()
							+ "字节，当前TCP上行信息流量为="
							+ (20 + 20 + socketBytes.length) + " 字节");
		} else if (linkType.equals("0") && actionType.equals("1")) {
			log.info(
					deviceId + " 当前缓存下行数据总流量为：" + flowVolume.getFlowSize()
							+ "字节，当前TCP下行信息流量为="
							+ (20 + 20 + socketBytes.length) + " 字节");
		} else if (linkType.equals("1") && actionType.equals("1")) {
			log.info(
					deviceId + " 当前缓存下行数据总流量为：" + flowVolume.getFlowSize()
							+ "字节，当前UDP下行信息流量为="
							+ (20 + 8 + socketBytes.length) + " 字节");
		} else if (linkType.equals("1") && actionType.equals("0")) {
			log.info(
					deviceId + " 当前缓存上行数据总流量为：" + flowVolume.getFlowSize()
							+ "字节，当前UDP上行信息流量为="
							+ (20 + 8 + socketBytes.length) + " 字节");
		}
	}

}
