package com.mapabc.gater.lbsgateway.gprsserver;   

import java.util.TimerTask;

import com.mapabc.gater.lbsgateway.service.licence.LicenceValidator;

/**
 * @author dingfeng.liu
 * @date：Jul 19, 2011
 * 类说明
 */
public class LicenceTimeTask extends TimerTask{

	@Override
	public void run() {
		LicenceValidator licenceValidator=LicenceValidator.getInstance();
//		System.out.println("定时licence验证");
		
		licenceValidator.validateGater();
		
//		if(!"sucess".equals(licenceValidator.gaterValidate())||!"sucess".equals(licenceValidator.lbsValidate()))
//		{
//			licenceValidator.deActive();
//		}
		
	}

}
 