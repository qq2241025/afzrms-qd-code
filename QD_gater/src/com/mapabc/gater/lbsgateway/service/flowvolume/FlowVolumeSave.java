/**
 * 
 */
package com.mapabc.gater.lbsgateway.service.flowvolume;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.LogFactory;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.common.Const;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.lbsgateway.bean.TFlowVolume;

/**
 * @author 
 * 
 */
public class FlowVolumeSave extends TimerTask {
	private static org.apache.commons.logging.Log log=LogFactory.getLog(FlowVolumeSave.class);

	private static boolean isSaving = false;
	private Timer timer = new Timer();

	public void run() {

		try {
			if (!isSaving) {
				isSaving = true;
				checkAlarmQueue();
				isSaving = false;
			}

		} catch (Exception e) {
			isSaving = false;
			e.printStackTrace();
			log.error("checking flow queue error", e);

		}

	}

	private synchronized void checkAlarmQueue() {

		ConcurrentMap<String, TFlowVolume> mtFlow = FlowHashtable.getInstance()
				.getAllDeviceMoFlowVolume();
		ConcurrentMap<String, TFlowVolume> moFlow = FlowHashtable.getInstance()
				.getAllDeviceMoFlowVolume();

		ArrayList<TFlowVolume> list = new ArrayList<TFlowVolume>();
		if (mtFlow != null) {
			Collection<TFlowVolume> mtcollection = mtFlow.values();
			for (TFlowVolume mtflow : mtcollection) {
				list.add(mtflow);
			}
		}
		if (moFlow != null) {
			Collection<TFlowVolume> mocollection = moFlow.values();
			for (TFlowVolume moflow : mocollection) {
				list.add(moflow);
			}
		}

		long s = System.currentTimeMillis();

		try {
			if (list.size() > 0) {
				DBService dbserv = (DBService) AppCtxServer.getInstance().getBean(Const.SPRING_BEAN_NAME_DBSERVICE);
				dbserv.saveFlowVolume(list);
				log.info(
						"保存上行流量条数：" + moFlow.size() + ",下行终端流量条数："
								+ mtFlow.size());

				FlowHashtable.getInstance().getMtFlowVolume().clear();
				FlowHashtable.getInstance().getMoFlowVolume().clear();

				log.info(
						"流量入库完毕，清除流量上下行缓存,当前上行缓存终端数为："
								+ FlowHashtable.getInstance().getMoFlowVolume()
										.size()
								+ ",下行缓存终端条数为："
								+ FlowHashtable.getInstance().getMtFlowVolume()
										.size());

			}

		} catch (Exception e) {
			isSaving = false;
			log.error("按批终端流量异常", e);
			e.printStackTrace();
		}
		long e = System.currentTimeMillis();

	}
}
