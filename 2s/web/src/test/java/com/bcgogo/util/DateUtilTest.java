package com.bcgogo.util;

import com.bcgogo.utils.DateUtil;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.ParseException;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-7-1
 * Time: 下午2:06
 * To change this template use File | Settings | File Templates.
 */
public class DateUtilTest {
//  @Test
  public void dateLongToStringTest() {
    System.out.println(DateUtil.dateLongToStr(1341046870923l));
    System.out.println(DateUtil.dateLongToStr(1341121846966l));
    System.out.println(DateUtil.dateLongToStr(1341121846967l));
    System.out.println(DateUtil.dateLongToStr(1341122568206l));
  }
  //@Test
  public  void testgetLastDayDateTimeOfYear() throws ParseException {
    String startOfYear=DateUtil.getStartOfYear();
    String endOfYear=DateUtil.getEndOfYear();
    System.out.println("ok");
  }

  //@Test
  public void testCurrentMilli(){
    System.out.println(System.currentTimeMillis());
    System.out.println(DateUtil.dateLongToStr(12993047245437l));
  }

  @Test
  public void testGetLocalHost() throws UnknownHostException {
    String ip = InetAddress.getLocalHost().getHostAddress();

       System.out.println(ip);
  }

}
