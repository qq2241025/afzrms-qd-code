package com.dt.afzrms.service;

public interface DeleteMonthDataService {
	public void deleteMonthData(int month)  throws Exception;
	
	public void setExecuteEveryMonthPartTableTask();
}
