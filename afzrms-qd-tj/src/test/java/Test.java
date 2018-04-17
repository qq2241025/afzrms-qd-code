import java.util.Calendar;
import java.util.Date;

import com.dt.afzrms.common.Const;
import com.dt.afzrms.util.DateUtil;

/**
 * @Company AutoNavi - MapABC Co.,Ltd.
 * @Copyright &copy 2011 MapABC Co.,Ltd.
 * @author jingwei.sun
 * @date 2015年4月9日 上午11:06:36
 * 
 */

/**
 * @Title TODO
 * @Description TODO
 * @author jingwei.sun
 * @createDate 2015年4月9日 上午11:06:36
 * @modifier
 * @modifyDate
 * @version 1.0
 * 
 */

public class Test {
	public static void main(String[] args) {
		Calendar now = Calendar.getInstance();
		now.set(Calendar.MILLISECOND, 0);
		now.set(Calendar.SECOND, 0);
		now.set(Calendar.MINUTE, 0);
		now.set(Calendar.HOUR_OF_DAY, 0);
		// now.roll(Calendar.DATE, true);
		Date tjDate = now.getTime();
		System.out.println(tjDate);
		String str = DateUtil.dateTimeToStr(tjDate, Const.DATE_PATTERN);
		System.out.println(str);

		Integer travel_time = 120;
		Float distance = 1230.3f;

		Float average_speed = distance * 60 / travel_time;
		System.out.println(average_speed);

		Object[] objs = new Object[] { "123", 1, 2f, Calendar.getInstance().getTime() };
		StringBuffer sb = new StringBuffer();
		for (Object object : objs) {
			sb.append(object.toString());
			sb.append(" | ");
		}
		System.out.println(sb.toString());

		Byte[] _alarm_type = { 0x31 };
		System.out.println(_alarm_type[0]);
		System.out.println(_alarm_type[0] & 1);

		int alarmType = 0;
		alarmType |= 1;
		System.out.println(alarmType);
		alarmType = 0;
		alarmType |= 2;
		System.out.println(alarmType);
		byte b = 1;
		System.out.println(b);
		
		byte bb = 49;
		System.out.println(bb & 1);
		System.out.println(bb & 2);
		bb = 51;
		System.out.println(bb & 1);
		System.out.println(bb & 2);
	}
}
