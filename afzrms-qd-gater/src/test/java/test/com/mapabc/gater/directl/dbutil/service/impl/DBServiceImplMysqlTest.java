package test.com.mapabc.gater.directl.dbutil.service.impl;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import test.help.HelpTest;

import com.mapabc.AppCtxServer;
import com.mapabc.gater.directl.dbutil.service.DBService;
import com.mapabc.gater.directl.parse.ParseBase;

public class DBServiceImplMysqlTest extends HelpTest{
	@Autowired
	DBService dbService;
	@Test
	public void testSaveMoreAlarm() throws Exception {
		ArrayList<ParseBase> baseList = new ArrayList<ParseBase>();
		ParseBase e = new ParseBase();
		e.setDeviceSN("13911111111");
		e.setCoordX("116");
		e.setCoordY("39");
		e.setAltitude("91.09");
		e.setSpeed("100");
		e.setDirection("180");
		e.setMileage("1");
		e.setTime("2016-06-01 00:00:00");
		e.setAlarmType("3");
		e.setAlarmSubType("0");
		e.setAreaNo("3");
		e.setSpeedThreshold("0.10000000149011612");
		baseList.add(e);
		
		AppCtxServer.getInstance().initSystem(null);
		
		dbService.saveMoreAlarm(baseList);
	}

}
