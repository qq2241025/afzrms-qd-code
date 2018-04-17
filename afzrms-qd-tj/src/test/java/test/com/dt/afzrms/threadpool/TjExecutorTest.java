/**
 * @Company AutoNavi - MapABC Co.,Ltd.
 * @Copyright &copy 2011 MapABC Co.,Ltd.
 * @author jingwei.sun
 * @date 2015年9月14日 下午2:29:07
 * 
 */
package test.com.dt.afzrms.threadpool;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.threadpool.TjExecutor;
import com.dt.afzrms.util.DateUtil;

import test.help.HelpTest;

/**
 * @Title TjExecutor Test
 * @Description test
 * @author jingwei.sun
 * @createDate 2015年9月14日 下午2:29:07
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class TjExecutorTest extends HelpTest {
	@Autowired
	TjExecutor tjExecutor;

	@Test
	public void testTj() throws InterruptedException {
		String deviceId = "354525045369640";
		Integer deptId = 2;
		Date tjDate = DateUtil.strToDateTime("2017-03-24 00:00:00", Const.DATETIME_PATTERN);
		boolean tj = tjExecutor.tj(deviceId, deptId, tjDate);
		assertTrue(tj);

		Thread.sleep(100 * 1000);
	}

}
