package com.mapabc;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppCtxServer {
	private static AppCtxServer instance = null;

	private ApplicationContext clsPathCtx = null;

	private AppCtxServer() {

	}

	public static AppCtxServer getInstance() {
		if (null == instance) {
			instance = new AppCtxServer();
		}

		return instance;
	}

	public void initSystem(ServletContext sCtx) {
		try {
			clsPathCtx = new ClassPathXmlApplicationContext(beanConfigFiles());
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	public Object getBean(String beanName) throws Exception {
		return getBeanByClsPathCtx(beanName);
	}

	private Object getBeanByClsPathCtx(String beanName) throws Exception {
		if (null == clsPathCtx) {
			throw new Exception("Class path context is null");
		}
		return clsPathCtx.getBean(beanName);
	}

	private String[] beanConfigFiles() throws Exception {
		return new String[] { "classpath*:dataAccessContext.xml"};
	}
}
