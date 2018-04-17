package com.dt.afzrms.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.vo.UserVo;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年1月14日 下午4:04:27
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@Repository
public class SystemInterceptor extends HandlerInterceptorAdapter {
	private List<String> authPaths;

	public List<String> getAuthPaths() {
		return authPaths;
	}

	public void setAuthPaths(List<String> authPaths) {
		this.authPaths = authPaths;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		// response.setHeader("P3P", "CP=CAO PSA OUR");

		String uri = request.getRequestURI();

		boolean beFilter = true;
		for (String s : authPaths) {
			if (uri.endsWith(s)) {
				beFilter = false;
				break;
			}
		}
		UserVo currentUserVo = null;
		if (beFilter) {
			currentUserVo = (UserVo) request.getSession().getAttribute(Const.CURRENT_USER);
			if (currentUserVo == null) {// 未登录
				response.setHeader("sessionstatus", "timeout");
				response.getWriter().write("{\"sessionstatus\":\"timeout\",\"info\":\"会话超时，请重新登陆\"}");
				return false;
			}
		}
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// response.setHeader("P3P", "CP=CAO PSA OUR");
		super.afterCompletion(request, response, handler, ex);
	}
}
