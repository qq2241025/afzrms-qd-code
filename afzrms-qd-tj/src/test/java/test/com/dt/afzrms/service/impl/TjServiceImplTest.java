package test.com.dt.afzrms.service.impl;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import test.help.HelpTest;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.service.TjService;
import com.dt.afzrms.util.DateUtil;

public class TjServiceImplTest extends HelpTest {
	@Autowired
	TjService tjService;

	@Test
	public void testFindLocrecordList() throws Exception {
		String deviceId = "354525045370549";
		Date beginDate = DateUtil.strToDateTime("2016-11-14 00:00:00",
				Const.DATETIME_PATTERN);
		Date endDate = DateUtil.strToDateTime("2016-11-15 00:00:00",
				Const.DATETIME_PATTERN);
		List<Object[]> findLocrecordList = tjService.findLocrecordList(
				deviceId, beginDate, endDate);
		assertNotNull(findLocrecordList);
		assertTrue(findLocrecordList.size() > 0);
	}

}
