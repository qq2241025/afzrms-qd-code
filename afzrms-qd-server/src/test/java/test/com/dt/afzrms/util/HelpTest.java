/**
 * @Company 
 * @Copyright 
 * @author 
 * @date 2014-3-3 下午02:39:11
 * 
 */
package test.com.dt.afzrms.util;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @Title help test
 * @Description
 * @author
 * @createDate 2014-3-3 下午02:39:11
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml", "classpath:springMVC-servlet.xml" })
public class HelpTest extends AbstractJUnit4SpringContextTests {

}
