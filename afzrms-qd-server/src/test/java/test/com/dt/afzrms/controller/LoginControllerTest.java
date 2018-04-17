package test.com.dt.afzrms.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import test.com.dt.afzrms.util.HelpTest;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.controller.LoginController;

/**
 * @Title TODO
 * @Description TODO
 * @author
 * @createDate 2015年1月20日 下午5:15:56
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:applicationContext.xml")
public class LoginControllerTest extends HelpTest {

	@Autowired
	LoginController loginController;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLogin() throws UnsupportedEncodingException {
		String account = "test";
		String password = "123321";
		account = Base64.encodeBase64String(StringUtils.getBytesUtf8(account));
		password = Base64.encodeBase64String(StringUtils.getBytesUtf8(password));
		String validateCode = "1234";
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		request.setMethod("POST");
		request.addParameter("account", account);
		request.addParameter("password", password);

		// 信息存入session
		request.getSession().setAttribute(Const.VALIDATE_CODE, validateCode);
		request.addParameter("validateCode", validateCode);

		HttpSession session = request.getSession();
		String _account = request.getParameter("account");
		String pwd = request.getParameter("password");
		String _validateCode = request.getParameter("validateCode");
		loginController.login(request, response, session, _account, pwd, _validateCode);
		String responseStr = response.getContentAsString();
		System.out.println(responseStr);
	}

}
