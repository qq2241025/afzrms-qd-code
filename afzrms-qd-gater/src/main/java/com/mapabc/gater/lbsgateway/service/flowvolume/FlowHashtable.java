/**
 * 
 */
package com.mapabc.gater.lbsgateway.service.flowvolume;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.mapabc.gater.lbsgateway.bean.TFlowVolume;

/**
 * 
 * @author 
 * 
 */
public class FlowHashtable {
	static FlowHashtable instance;
//	private static IMemcachedCache cache = null;

	private ConcurrentMap<String, TFlowVolume> mtFlowVolume = new ConcurrentHashMap<String, TFlowVolume>();
	private ConcurrentMap<String, TFlowVolume> moFlowVolume = new ConcurrentHashMap<String, TFlowVolume>();

	public static FlowHashtable getInstance() {
		if (instance == null) {
			instance = new FlowHashtable();
		}
		return instance;
	}

	private FlowHashtable() {
//		String isload = null;
//		try{
//		isload = AllConfigCache.getInstance().getLoadMap().get(
//				"isOverLoad");
//		}catch(Exception e){}
//		if (isload != null && isload.equals("1"))
//			cache = MemCacheUtil.getInstance().getCache("gaterClient");
	}

	// 下行流量缓存
	public synchronized void addMtFlowObj(String deviceId, TFlowVolume volume) {
		mtFlowVolume.put(deviceId, volume);
//		if (cache != null)
//			cache.put(KeyConstant.MtFlowVolumeCacheKey, mtFlowVolume);
	}

	// 上行流量缓存
	public synchronized void addMoFlowObj(String deviceId, TFlowVolume volume) {
		moFlowVolume.put(deviceId, volume);
//		if (cache != null)
//			cache.put(KeyConstant.MoFlowVolumeCacheKey, moFlowVolume);
	}

	// 获取当前下行流量
	public synchronized TFlowVolume getMtFlowVolume(String deviceId) {
		TFlowVolume volume = null;
		ConcurrentMap flow = null;
//		if (cache != null)
//			flow = (ConcurrentMap) cache.get(KeyConstant.MtFlowVolumeCacheKey);
//		else
			flow = instance.getMtFlowVolume();

		if (flow != null)
			volume = (TFlowVolume) flow.get(deviceId);

		return volume;
	}

	// 获取当前上行流量
	public synchronized TFlowVolume getMoFlowVolume(String deviceId) {
		TFlowVolume volume = null;
		ConcurrentMap flow = null;
//		if (cache != null)
//			flow = (ConcurrentMap) cache.get(KeyConstant.MoFlowVolumeCacheKey);
//		else
			flow = instance.getMoFlowVolume();

		if (flow != null)
			volume = (TFlowVolume) flow.get(deviceId);

		return volume;
	}

	public ConcurrentMap<String, TFlowVolume> getAllDeviceMoFlowVolume() {
		ConcurrentMap<String, TFlowVolume> flowMap = null;
//		if (cache != null)
//			flowMap = (ConcurrentMap) cache
//					.get(KeyConstant.MoFlowVolumeCacheKey);
//		else
			flowMap = instance.moFlowVolume;
		return flowMap;
	}

	public ConcurrentMap<String, TFlowVolume> getAllDeviceMtFlowVolume() {
		ConcurrentMap<String, TFlowVolume> flowMap = null;
//		if (cache != null)
//			flowMap = (ConcurrentMap) cache
//					.get(KeyConstant.MtFlowVolumeCacheKey);
//		else
			flowMap = instance.mtFlowVolume;
		return flowMap;
	}

	/**
	 * @return the mtFlowVolume
	 */
	public ConcurrentMap<String, TFlowVolume> getMtFlowVolume() {
		return this.mtFlowVolume;
	}

	/**
	 * @return the moFlowVolume
	 */
	public ConcurrentMap<String, TFlowVolume> getMoFlowVolume() {
		return this.moFlowVolume;
	}

	/**
	 * @param mtFlowVolume
	 *            the mtFlowVolume to set
	 */
	public void setMtFlowVolume(ConcurrentMap<String, TFlowVolume> mtFlowVolume) {
		this.mtFlowVolume = mtFlowVolume;
	}

	/**
	 * @param moFlowVolume
	 *            the moFlowVolume to set
	 */
	public void setMoFlowVolume(ConcurrentMap<String, TFlowVolume> moFlowVolume) {
		this.moFlowVolume = moFlowVolume;
	}

}
