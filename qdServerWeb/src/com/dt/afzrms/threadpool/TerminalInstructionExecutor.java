package com.dt.afzrms.threadpool;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.dt.afzrms.dao.hibernate.TTermInstructionRecordDao;
import com.dt.afzrms.po.TTermInstructionRecord;
import com.dt.afzrms.util.HttpUtilUseHttpclient;
import com.dt.afzrms.util.StringUtil;

/**
 * @Title terminal instruction executor
 * @Description TODO
 * @author
 * @createDate 2015年3月30日 上午10:57:43
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@Service
public class TerminalInstructionExecutor {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	@Value("${gater.service.url}")
	private String url;
	@Autowired
	private TaskExecutor taskExecutor;
	@Autowired
	private TTermInstructionRecordDao tTermInstructionRecordDao;

	public boolean sendInstruction(final Map<String, String> params, final boolean isSaveDb) {
		try {
			taskExecutor.execute(new Runnable() {

				@Override
				public void run() {
					int status = 0;
					try {
						logger.info("instruction:" + params.toString());
						try {
							String sendGet = HttpUtilUseHttpclient.sendGet(url, params);
							logger.info(sendGet);
							int code = helpParseXML(sendGet);
							logger.info("instruction return code:" + code);
							// TODO
							status = 1;
						} catch (Exception e) {
							logger.error("", e);
							status = 2;
						}
					} catch (Exception e) {
						logger.error("", e);
						status = 2;
					}
					saveDb(params, isSaveDb, status);
				}
			});
			return true;
		} catch (Exception e) {
			logger.error("input:" + params.toString(), e);
			return false;
		}
	}

	private int helpParseXML(String responseXML) {
		/*
		 * <Response> <device id="1,2"> <code>1</code> <desc>终端不在线</desc>
		 * </device> </Response>
		 */
		int code = -1;
		Document document;
		try {
			document = DocumentHelper.parseText(responseXML);
			Element root = document.getRootElement();
			String codeStr = root.element("device").elementTextTrim("code");
			if (!StringUtils.isEmpty(codeStr)) {
				code = StringUtil.str2Integer(codeStr);
			}
		} catch (Exception e) {
		}
		return code;
	}

	private void saveDb(Map<String, String> params, boolean isSaveDb, int status) {
		if (isSaveDb) {
			String name = params.get("name");
			Integer instructionType = -1;
			if ("timeInter".equals(name)) {
				instructionType = 1;
			}
			if ("overspeedAlarm".equals(name)) {
				instructionType = 2;
			}
			if ("areaAlarm".equals(name)) {
				instructionType = 1;
			}
			if ("cancleArea".equals(name)) {
				instructionType = 3;
			}
			String instructionContent = params.toString();
			TTermInstructionRecord tTermInstructionRecord = new TTermInstructionRecord(null, instructionType, status,
					instructionContent);
			tTermInstructionRecordDao.save(tTermInstructionRecord);
			logger.info("saveDb return:" + tTermInstructionRecord.getId());
		}
	}
}
