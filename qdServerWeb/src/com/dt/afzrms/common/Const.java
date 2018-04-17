package com.dt.afzrms.common;

/**
 * @Title 常量配置
 * @Description TODO
 * @author
 * @createDate 2015年1月20日 上午10:50:06
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class Const {
	// 分页
	public static final int PAGE_SIZE = 20;
	public static final int PAGE_NO = 1;

	// 前台用户登录后再session中存放的属性名
	public static final String CURRENT_USER = "current_user";
	public static final String VALIDATE_CODE = "validateCode";
	
	
	public static final int SYSTEMUSER = 0;  //系统用户
	public static final int COMMONUSER = 1;  //普通用户
	public static final int ISDELETEDYES = 1; //可以删除的用户
	public static final int ISDELETEDNO = 0; //不能删除的用户
	public static final String SYSMSG = "系统用户不能随便删除";
	public static final String EDITSYSMSG = "系统用户不能编辑";
	
	

	// 返回内容
	public static final String RESP_MSG_SUCCESS_ADD = "添加成功";
	public static final String RESP_MSG_FAIL_ADD = "添加失败";
	public static final String RESP_MSG_SUCCESS_UPDATE = "修改成功";
	public static final String RESP_MSG_FAIL_UPDATE = "修改失败";
	public static final String RESP_MSG_SUCCESS_DELETE = "删除成功";
	public static final String RESP_MSG_FAIL_DELETE = "删除失败";
	public static final String RESP_MSG_FAIL_EXIST = "已存在";
	public static final String RESP_MSG_SUCCESS_TERMINALINSTRUCTION_SETUP = "设置成功";
	public static final String RESP_MSG_FAIL_TERMINALINSTRUCTION_SETUP = "设置失败";

	// 时间格式
	public static final String DATE_PATTERN = "yyyy-MM-dd";
	public static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
}
