package com.dt.afzrms.singleLogin.Login;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LoginUserCache {
	private static Map<String, HttpLoginUserSession> cache =  new ConcurrentHashMap<String, HttpLoginUserSession>();
    
	
	public static  void PrintAllCache(){
		System.out.println("*******用户登陆缓存的列表:***************************");
		if(cache!=null && cache.size() >0){
			for (Map.Entry<String, HttpLoginUserSession> entry : cache.entrySet()) {  
				HttpLoginUserSession ws = entry.getValue();
				System.out.println(ws.getLogcodeId()+"****************");
			}
		}
		System.out.println("********************************************************");
	}
	
	//check user is or not login
	public static boolean  checkUserIsLogin(String loginCode) {
		 boolean islogin  =false;
		 if(cache.containsKey(loginCode)){
			 islogin = true;
	     }
		 return islogin;
    }
	//放入缓存
   public static void addLoginUser(String loginCode, HttpLoginUserSession httpsession){
	   System.out.println("用户登陆成功，并放入缓存");
	   cache.put(loginCode,httpsession); 
   }
   
   
   public static HttpLoginUserSession getLoginInfo(String loginCode){
	   HttpLoginUserSession session = null;
	   if(cache.containsKey(loginCode)){
		   session = cache.get(loginCode);
	   }
	   return session;
   }
   
   public  static void removeUserInfo(String loginCode){
	   if(cache.containsKey(loginCode)){
	      cache.remove(loginCode); 
	   }
   }
}
