package com.mapabc.gater.directl.constant;

import java.util.HashMap;

public class StructionTypeMap extends StructionType {

	static HashMap<String, String> cmdTypeMap = new HashMap<String, String>();
	
	static StructionTypeMap instance;
	
	public static StructionTypeMap getInstance(){
		if (instance == null)
			instance = new StructionTypeMap();
		return instance;
	}

	private StructionTypeMap(){
		mapCmdTypeToService();
	}
	
	@Override
	public void mapCmdTypeToService() {
		// TODO Auto-generated method stub
		cmdTypeMap.put("locate", this.SINGLE_LOCATE_COMMOND_TYPE);
		cmdTypeMap.put("timeInter", this.FREQUENCE_COMMOND_TYPE);
		cmdTypeMap.put("distanceInter", this.DISTANCE_COMMOND_TYPE);
		cmdTypeMap.put("timeLocate", this.TIME_POINT_LOCATE_COMMOND_TYPE);
 		
		cmdTypeMap.put("msg", this.MSG_SEND_COMMOND_TYPE);
		cmdTypeMap.put("destination", this.DESTINATION_COMMOND_TYPE);
		cmdTypeMap.put("route", this.ROUTE_COMMOND_TYPE);
 		cmdTypeMap.put("oilElecControl", this.OIL_CTL_COMMOND_TYPE);
		cmdTypeMap.put("switchDoorControl", this.DOOR_CTL_COMMOND_TYPE);
		cmdTypeMap.put("listen", this.LISTEN_PHONE_SET_COMMOND_TYPE);
		cmdTypeMap.put("callRestrict", this.CALL_RESTICT_SET_COMMOND_TYPE);
		cmdTypeMap.put("remoteLoading", this.REMOTE_LOAD_COMMOND_TYPE);
		cmdTypeMap.put("sleep", this.SLEEP_SET_COMMOND_TYPE);
 		cmdTypeMap.put("reset", this.RESET_RECOVER_CTL_COMMOND_TYPE);
		
		
		cmdTypeMap.put("areaAlarm", this.AREA_SET_COMMOND_TYPE);
		cmdTypeMap.put("lineAlarm", this.LINE_SET_COMMOND_TYPE);
		cmdTypeMap.put("overspeedAlarm", this.OVERSPEED_SET_COMMOND_TYPE);
		cmdTypeMap.put("cancleArea", this.AREA_CANCEL_COMMOND_TYPE);
		cmdTypeMap.put("cancleLine", this.LINE_CANCEL_COMMOND_TYPE);
		cmdTypeMap.put("switchAlarm", this.ALARM_SWITCH_SET_COMMOND_TYPE);
		cmdTypeMap.put("alarmParams", this.ALARM_PARAM_SET_COMMOND_TYPE);
		cmdTypeMap.put("cancelAlarm", this.ALARM_CANCEL_COMMOND_TYPE);
 		cmdTypeMap.put("timeoutStopCarAlarm", this.STOP_CAR_SET_COMMAND_TYPE);
 		
 		
 		cmdTypeMap.put("addrSetting", this.ADDRESS_SET_COMMOND_TYPE);
		cmdTypeMap.put("callNumberSetting", this.CALL_IN_SET_COMMOND_TYPE);
		cmdTypeMap.put("heartSetting", this.HEART_SET_COMMOND_TYPE);
		cmdTypeMap.put("keySetting", this.KEY_SET_COMMOND_TYPE);
		cmdTypeMap.put("locateSetting", this.LOCATE_MODEL_SET_COMMOND_TYPE);
		cmdTypeMap.put("modeSetting", this.LOCATE_MODEL_SET_COMMOND_TYPE);
		cmdTypeMap.put("alarmTimeSetting", this.ALARM_TIME_SET_COMMOND_TYPE);
		cmdTypeMap.put("fatigueDriveSetting", this.FATIGUE_SET_COMMOND_TYPE);
 		cmdTypeMap.put("vehicleIdSetting", this.CAR_ID_SET_CMD_TYPE);
 		
 		cmdTypeMap.put("vehicleCardSetting", this.CAR_CARD_NUM_SET_COMMOND_TYPE);
		cmdTypeMap.put("vehicleCardTypeSetting", this.CAR_CARD_CATALOG_COMMOND_TYPE);
		cmdTypeMap.put("driverCodeSetting", this.DRIVER_CODE_SET_COMMOND_TYPE);
		cmdTypeMap.put("driverLicenseSetting", this.DRIVER_LICENSER_SET_COMMOND_TYPE);
		cmdTypeMap.put("installDateSetting", this.INSTALL_DATE_SET_COMMOND_TYPE);
		cmdTypeMap.put("clockSetting", this.CLOCK_SET_COMMOND_TYPE);
		cmdTypeMap.put("featureCofficient", this.FEATURE_COFFICIENT_SET_COMMOND_TYPE);
		cmdTypeMap.put("fatigueDriveSetting", this.FATIGUE_SET_COMMOND_TYPE);
 		cmdTypeMap.put("smsCenterSetting", this.SMS_CENTER_NUM_SET_COMMOND_TYPE);
 		
 		cmdTypeMap.put("camera", this.CAMERA_SET_COMMOND_TYPE);
		cmdTypeMap.put("fortificationSetting", this.FORTIFICATION_SET_CMD_TYPE);
		cmdTypeMap.put("ioPortSetting", this.IO_ALARM_PORT_SET_CMD_TYPE);
		cmdTypeMap.put("ctlOutputPortSetting", this.OUTPORT_SET_CMD_TYPE);
		cmdTypeMap.put("carIdSetting", this.CAR_ID_SET_CMD_TYPE);
		cmdTypeMap.put("apnSetting", this.APN_SET_COMMAND_TYPE);
		 
		cmdTypeMap.put("temperatureInterSetting", this.TERMERATURE_SET_COMMAND_TYPE);
 		cmdTypeMap.put("oilInterSetting", this.OIL_CTL_COMMOND_TYPE);
 		
 		cmdTypeMap.put("flameOutInterSetting", this.FLAMOUTINTERVAL_SET_CMD_TYPE);
 		cmdTypeMap.put("totalMileageSetting", this.TOTALMILEAGE_SET_CMD_TYPE);
 		cmdTypeMap.put("mileageTypeSetting", this.MILEAGETYPE_SET_CMD_TYPE);
   	

	}
	
	public static HashMap<String, String> getCmdTypeToServiceDesc(){
		return  cmdTypeMap;
	}

}
