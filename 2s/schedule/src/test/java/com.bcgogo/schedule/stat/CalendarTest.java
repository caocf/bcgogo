package com.bcgogo.schedule.stat;

import com.bcgogo.AbstractTest;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: Xiao Jian
 * Date: 12-1-9
 */

public class CalendarTest extends AbstractTest {
  @Test
  public void testCalendar() throws Exception {
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-M-d");
    Date d = simpleDateFormat.parse("2012-1-06");
    calendar.setTime(d);
    calendar.setFirstDayOfWeek(Calendar.MONDAY);
    calendar.setMinimalDaysInFirstWeek(1);
    calendar.set(Calendar.YEAR, 2011);
    int week = calendar.get(Calendar.WEEK_OF_YEAR);
    calendar.set(Calendar.MONTH, Calendar.JANUARY);
    calendar.set(Calendar.DATE, 1);
    int dayofweek = calendar.get(Calendar.DAY_OF_WEEK);
    String s = calendar.getTime().toLocaleString();
    calendar.add(Calendar.DATE, -1);
    s = calendar.getTime().toLocaleString();
    Date date = new Date();
    date.setTime(61286169600000L);
    calendar.setTime(date);
    date.setTime(1325825448709L);
    calendar.setTime(date);
    s = calendar.getTime().toLocaleString();

    calendar.set(2012, 0, 1, 0, 0, 0);
    calendar.setMinimalDaysInFirstWeek(1);
    calendar.setFirstDayOfWeek(Calendar.MONDAY);
    int lastWeekOfCurrentYear = calendar.get(Calendar.WEEK_OF_YEAR);
  }
}
