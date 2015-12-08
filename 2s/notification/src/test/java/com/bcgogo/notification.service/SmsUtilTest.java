package com.bcgogo.notification.service;

import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.model.SmsJob;
import com.bcgogo.notification.smsSend.SmsUtil;
import com.bcgogo.notification.smsSend.SmsYiMeiSender;
import com.bcgogo.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-9-7
 * Time: 上午11:36
 * To change this template use File | Settings | File Templates.
 */
public class SmsUtilTest {
  private static final Logger LOG = LoggerFactory.getLogger(SmsUtilTest.class);

  @Test
  public void jointStringsTest() {
    String a= "<益加益汽修><陆雷>您好!感谢使用苏州统购信息科技有限公司的一发软件,为您开通的用户名：15962178504和密码168818,祝您使用愉快!如有任何问题请随时拨打客服电话:0512-66733331与我们联系。【苏州统购】";
    System.out.println(SmsUtil.calculateSmsNum(a,"18916572273"));
    String mobiles = "18916,18916572273";
    LOG.info(StringUtil.jointStrings(mobiles, mobiles));
    LOG.info(StringUtil.jointStrings(mobiles, ""));
    LOG.info(StringUtil.jointStrings("", mobiles));
    System.out.println("=====");
    System.out.println(new SmsYiMeiSender().caculateSmsNum("免费喷漆整形、划痕！省心省力省烦恼！我司即日推出“漆彩人生路”活动，原厂进口漆无色差符合4S店标准。T：0512-65285800.【苏州华信】", "18916572273,18916572273"));
    System.out.println(new SmsYiMeiSender().caculateSmsNum("免费喷漆整形、划痕！省心省力省烦恼！我司即日推出“漆彩人生路”活动。原厂进口无色差符合4S店标准。T：65285800【苏州华信】","18916572273,18916572273"));
    System.out.println(new SmsYiMeiSender().caculateSmsNum("免费喷漆整形、划痕！省心省力省烦恼！我司即日推出“漆彩人生路”活动。原厂进口无色差符合4S店标准。T：0512-65285800【苏州华信】","18916572273,18916572273,18916572273,18916572273"));
  }

  @Test
  public void groupingMobilesToListTest() {
    String mobiles = "18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273," +
        "18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273" +
        "18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273" +
        "18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273" +
        "18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273" +
        "18916572273,18916572273";
    List<String[]> mobilesList = SmsUtil.groupingMobilesToList(mobiles, 40);
    LOG.info("group 1:");
    Assert.assertEquals(2, mobilesList.size());
    for (String[] strings : mobilesList) {
      LOG.info(String.valueOf(strings.length));
      LOG.info(StringUtil.arrayToStr(",", strings));
    }
    LOG.info("group 2:");
    mobiles = "18916,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273,18916572273," +
        "18916572273,18916572273,";
    mobilesList = SmsUtil.groupingMobilesToList(mobiles, 40);
    Assert.assertEquals(1, mobilesList.size());
    Assert.assertEquals(11, mobilesList.get(0).length);
    for (String[] strings: mobilesList) {
      LOG.info(String.valueOf(strings.length));
      LOG.info(StringUtil.arrayToStr(",", strings));
    }


    LOG.info("group 3:");
    mobiles = "18916572273,";
    mobilesList = SmsUtil.groupingMobilesToList(mobiles, 40);
    Assert.assertEquals(1, mobilesList.size());
    Assert.assertEquals(1, mobilesList.get(0).length);
    for (String[] strings: mobilesList) {
      LOG.info(String.valueOf(strings.length));
      LOG.info(StringUtil.arrayToStr(",", strings));
    }
     String vip="VIPzjt";
     Assert.assertEquals(true, vip.toUpperCase().contains("VIP"));
  }
  @Test
  public void testInvitationCodeSmsFilterByCustomerName(){
    List<String> nameList = new ArrayList<String>();
    nameList.add("**客户**");
    nameList.add("苏E9RT30");
    nameList.add("苏E9tt30");
    nameList.add("苏E9tt30学");
    nameList.add("苏E9tt30xxxxx");
    junit.framework.Assert.assertEquals(false, invitationCodeSmsFilterByCustomerName(nameList.get(0)));
    junit.framework.Assert.assertEquals(false, invitationCodeSmsFilterByCustomerName(nameList.get(1)));
    junit.framework.Assert.assertEquals(false, invitationCodeSmsFilterByCustomerName(nameList.get(2)));
  }

  private boolean invitationCodeSmsFilterByCustomerName(String name){
    if(StringUtils.isBlank(name) || name.indexOf("**客户**")>=0){
      return false;
    }
    if(name.matches("(^[\\u4e00-\\u9fa5]{2}$)|(^([a-zA-Z\\d]{5,6}|[a-zA-Z\\d]{9})$)|(^[\\u4e00-\\u9fa5]{1}([a-zA-Z\\d]{6}|[a-zA-Z\\d]{5}[\\u4e00-\\u9fa5]{1}$)$)")){
      return false;
    }
    return true;
  }
}
