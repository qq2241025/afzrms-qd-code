package com.dt.afzrms.listeners;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

import com.dt.afzrms.singleLogin.Login.Constant;
import com.dt.afzrms.singleLogin.Login.HttpLoginUserSession;
import com.dt.afzrms.singleLogin.Login.LoginUserCache;


public class ListUserStatus implements HttpSessionListener{
    public void sessionCreated(HttpSessionEvent event) {
    	System.out.println("session 已经创建");
    }

    public void sessionDestroyed(HttpSessionEvent event) {
    	System.out.println("session 已经销毁");
        HttpSession session = event.getSession();
        HttpLoginUserSession loginSession = (HttpLoginUserSession) session.getAttribute(Constant.LOGINUSER);
        if(loginSession!=null){
        	 String logedId = loginSession.getLogcodeId();
             LoginUserCache.removeUserInfo(logedId);
        }
    }
}
