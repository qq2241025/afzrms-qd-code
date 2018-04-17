package com.dt.afzrms.singleLogin.Login;

import javax.servlet.http.HttpSession;

import com.dt.afzrms.vo.UserVo;

/**
 * @Title TODO
 * @Description TODO
 * @author Administrator
 * @createDate 2016年6月11日 下午2:12:19
 * @modifier 
 * @modifyDate 
 * @version 1.0
 * 
 */
public class HttpLoginUserSession {
	private HttpSession httpsession ;
	private UserVo user;
	private String logcodeId ;
	private boolean islogined;
	public HttpSession getHttpsession() {
		return httpsession;
	}
	public void setHttpsession(HttpSession httpsession) {
		this.httpsession = httpsession;
	}
	
	public String getLogcodeId() {
		return logcodeId;
	}
	public void setLogcodeId(String logcodeId) {
		this.logcodeId = logcodeId;
	}
	public HttpLoginUserSession() {
		super();
		// TODO Auto-generated constructor stub
	}
	public UserVo getUser() {
		return user;
	}
	public void setUser(UserVo user) {
		this.user = user;
	}
	public boolean isIslogined() {
		return islogined;
	}
	public void setIslogined(boolean islogined) {
		this.islogined = islogined;
	}
	public HttpLoginUserSession(HttpSession httpsession, UserVo user,
			String logcodeId, boolean islogined) {
		super();
		this.httpsession = httpsession;
		this.user = user;
		this.logcodeId = logcodeId;
		this.islogined = islogined;
	}
}
