package com.mapabc.gater.lbsgateway.service;

import java.util.TimerTask;

import org.apache.commons.logging.LogFactory;

public class DynamicLoadTermList extends TimerTask {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(DynamicLoadTermList.class);
	private static boolean isReloading;

	public DynamicLoadTermList() {
		log.info("加载系统终端信息！");
	}

	@Override
	public void run() {
		try {
			if (!isReloading) {
				isReloading = true;
				com.mapabc.gater.lbsgateway.GBLTerminalList.getInstance()
						.loadTerminals();
				isReloading = false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			isReloading = false;
		}

	}

}
