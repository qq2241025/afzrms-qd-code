package com.dt.afzrms.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.AbstractController;
import com.dt.afzrms.common.Const;
import com.dt.afzrms.vo.UserVo;

public abstract class BaseController extends AbstractController {

	protected Logger logger;

	protected BaseController() {
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	/**
	 * 获取页码，始终返回 >= 1
	 * 
	 * @param request
	 * @param string
	 *            页码在参数名称
	 * @return
	 */
	protected int getPageNO(HttpServletRequest request, String paramName, int pageSize) {
		int pageNo = 0;
		try {
			int start = Integer.parseInt(request.getParameter(paramName));
			pageNo = start / pageSize + 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageNo > 0 ? pageNo : Const.PAGE_NO;
	}
	
	protected UserVo getLoginUser(HttpServletRequest request) {
		UserVo vo = null;
		HttpSession session =request.getSession();
		if(session!=null){
			vo = (UserVo) session.getAttribute(Const.CURRENT_USER);
		}
		return vo;
	}

	protected int getPageSize(HttpServletRequest request, String paramName) {
		int pageSize = 0;
		try {
			pageSize = Integer.parseInt(request.getParameter(paramName));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageSize > 0 ? pageSize : Const.PAGE_SIZE;
	}

	/**
	 * 根据#Locale.getDefault()获取text
	 * 
	 * @param key
	 * @param locale
	 * @return
	 */
	public String getText(String key, HttpServletRequest request) {
		return getText(key, request.getSession());
	}

	/**
	 * 获取session中由{@code SessionLocaleResolver}存放的Locale, 如果不存在则使用
	 * {@code Locale.getDefault()}
	 * 
	 * @param key
	 * @param session
	 * @return
	 */
	public String getText(String key, HttpSession session) {
		String sessionLocale = (String) session.getAttribute(SessionLocaleResolver.LOCALE_SESSION_ATTRIBUTE_NAME);
		return getText(key, sessionLocale == null ? Locale.getDefault() : new Locale(sessionLocale));
	}

	/**
	 * 根据locale获取text
	 * 
	 * @param key
	 * @param locale
	 *            the current locale
	 * @return
	 */
	public String getText(String key, Locale locale) {
		return getMessageSourceAccessor().getMessage(key, locale);
	}

	/**
	 * Convenient method for getting a i18n key's value with a single string
	 * argument.
	 * 
	 * @param key
	 * @param arg
	 * @param locale
	 *            the current locale
	 * @return
	 */
	public String getText(String key, String arg, Locale locale) {
		return getText(key, new Object[] { arg }, locale);
	}

	/**
	 * 
	 * 
	 * @param key
	 * @param args
	 * @param locale
	 *            the current locale
	 * @return
	 */
	public String getText(String key, Object[] args, Locale locale) {
		return getMessageSourceAccessor().getMessage(key, args, locale);
	}

	/**
	 * 去创建
	 * 
	 * @RequestMapping(value = "new", method = RequestMethod.GET)
	 * @return
	 */
	protected String create() {
		return null;
	}

	/**
	 * 创建
	 * 
	 * @RequestMapping(value = "new", method = RequestMethod.POST)
	 * @return
	 */
	protected String save() {
		return null;
	}

	/**
	 * 去修改
	 * 
	 * @RequestMapping(value = "{id}/modify", method = RequestMethod.GET)
	 * @return
	 */
	protected String modify() {
		return null;
	}

	/**
	 * 修改
	 * 
	 * @RequestMapping(value = "{id}/modify", method = RequestMethod.POST)
	 * @return
	 */
	protected String update() {
		return null;
	}

	/**
	 * 删除
	 * 
	 * @RequestMapping(value = "{id}/delete", method = RequestMethod.GET)
	 * @return
	 */
	protected String delete() {
		return null;
	}

	/**
	 * 在控制器类内部直接配置Web数据绑定
	 * 
	 * @param binder
	 * @param request
	 */
	@InitBinder
	public void initBinder(WebDataBinder binder, WebRequest request) {
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
		binder.registerCustomEditor(Byte.class, null, new CustomNumberEditor(Byte.class, true));
	}

	/**
	 * 到错误页面，发生错误时跳转至此页。
	 * 
	 * @param request
	 * @param message
	 *            页面显示的错误消息
	 * @param where
	 *            页面返回地址
	 * @return
	 */
	protected String error(HttpServletRequest request, String message, String where) {
		request.setAttribute("message", message);
		request.setAttribute("where", where);
		return "/console/500";
	}

	/**
	 * 
	 * @param request
	 * @param message
	 *            页面显示的消息
	 * @param where
	 *            页面返回地址
	 * @return
	 */
	protected String error404(HttpServletRequest request, String message, String where) {
		request.setAttribute("message", message);
		request.setAttribute("where", where);
		return "/console/404";
	}

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return null;
	}

	/**
	 * 数据未找到
	 */
	protected final static String EMPTY = "empty";

	/**
	 * 图片太小
	 */
	protected final static String TOO_SMALL = "2small";

	/**
	 * 数据未找到
	 */
	protected final static String DATA404 = "data404";
	/**
	 * 文件过大
	 */
	protected final static String TOO_LARGE = "2large";

	/**
	 * 成功
	 */
	protected final static String SUCCESS = "success";

	/**
	 * 失败
	 */
	protected final static String FAILED = "failed";
	/**
	 * 刷新
	 */
	protected final static String REFRESH = "refresh";

	/**
	 * 错误
	 */
	protected final static String ERROR = "error";

	/**
	 * 生成错误消息Map
	 * 
	 * @param type
	 *            错误类型
	 * @param message
	 *            错误信息
	 * @param operation
	 *            显示错误的后续操作
	 * @return
	 */
	protected Map<String, ? extends Object> buildErrorResult(String type, String message) {
		Map<String, Object> errorResult = new HashMap<String, Object>();
		errorResult.put("type", type);
		errorResult.put("message", message);
		return Collections.singletonMap("error", errorResult);
	}

	/**
	 * 创建一个字段错误信息Map
	 * 
	 * @param fieldName
	 * @param message
	 * @return
	 */
	protected Map<String, ? extends Object> buildFieldErrorResult(String fieldName, String message) {
		return Collections.singletonMap("ferrors", Collections.singletonMap(fieldName, message));
	}

	/**
	 * 转化错误信息
	 * 
	 * @param result
	 * @param request
	 * @return
	 */
	protected Map<String, String> transformError(BindingResult result, HttpServletRequest request,
			Map<String, String>... errorMap) {
		Map<String, String> errors = null;
		if (errorMap == null || errorMap.length == 0) {
			errors = new LinkedHashMap<String, String>();
		} else {
			errors = errorMap[0];
		}
		if (result.hasFieldErrors()) {
			for (FieldError error : result.getFieldErrors()) {
				errors.put(error.getField(), getText(error.getDefaultMessage(), request));
			}
		}
		return errors;
	}

	/**
	 * 创建一个字段错误信息Map
	 * 
	 * @param fieldName
	 * @param message
	 * @return
	 */
	protected Map<String, ? extends Object> buildSueccessResult(String successUrl) {
		return Collections.singletonMap("url", successUrl);
	}

	protected void writeJsonString(HttpServletResponse response, String jsonString) {
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setContentLength(jsonString.getBytes("UTF-8").length);
			response.getWriter().write(jsonString);
		} catch (IOException e) {
			logger.error("输出JSON错误.", e);
		}
	}

	protected void writeStringWithGzip(HttpServletResponse response, String string) {
		try {
			response.setContentType("text/html;charset=UTF-8");
			response.setHeader("content-encoding", "gzip");
			// response.setContentLength(string.getBytes("UTF-8").length);
			GZIPOutputStream gzipOutputStream = new GZIPOutputStream(response.getOutputStream());
			gzipOutputStream.write(string.getBytes("UTF-8"));
			gzipOutputStream.close();
			// PrintWriter pw = new PrintWriter(new
			// GZIPOutputStream(response.getOutputStream()));
			// pw.write(string);
			// pw.close();
		} catch (IOException e) {
			logger.error("输出string错误.", e);
		}
	}

	private UserVo currentUser(HttpServletRequest request) {
		Object attribute = request.getSession().getAttribute(Const.CURRENT_USER);
		if (attribute != null) {
			return (UserVo) attribute;
		} else {
			return null;
		}
	}
	protected String currentUserName(HttpServletRequest request) {
		UserVo currentUser = currentUser(request);
		if (currentUser != null) {
			return currentUser.getName();
		} else {
			return null;
		}
	}
	protected Integer currentUserRoleId(HttpServletRequest request) {
		UserVo currentUser = currentUser(request);
		if (currentUser != null) {
			return currentUser.getRoleId();
		} else {
			return null;
		}
	}
}
