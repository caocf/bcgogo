package com.bcgogo.common;

import com.bcgogo.utils.RegexUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-7-7
 * Time: 下午1:02
 * To change this template use File | Settings | File Templates.
 */
public class RegexUtilsTest {
  @Test
  public void mobileTest() {
    Assert.assertEquals(true, RegexUtils.isMobile("15851654173"));
    Assert.assertEquals(true, RegexUtils.isMobile("18951654173"));
    Assert.assertEquals(true, RegexUtils.isMobile("+8618951654173"));
    Assert.assertEquals(false, RegexUtils.isMobile("1233444"));
    Assert.assertEquals(false, RegexUtils.isMobile(""));

  }

  @Test
  public void emailTest() {
    Assert.assertEquals(false, RegexUtils.isEmail("sldh"));
    Assert.assertEquals(false, RegexUtils.isEmail("sldh@2131"));
    Assert.assertEquals(true, RegexUtils.isEmail("sldh@2131.ddsf"));
  }

  @Test
  public void phoneTest() {
    Assert.assertEquals(true, RegexUtils.isTelephone("0518-7677678"));
    Assert.assertEquals(true, RegexUtils.isTelephone("0518-76776789"));
    Assert.assertEquals(true, RegexUtils.isTelephone("7677679"));
    Assert.assertEquals(true, RegexUtils.isTelephone("97677789"));
    Assert.assertEquals(false, RegexUtils.isTelephone("051876776789"));
    Assert.assertEquals(false, RegexUtils.isTelephone("767769"));
    Assert.assertEquals(false, RegexUtils.isTelephone("976778933"));
    Assert.assertEquals(false, RegexUtils.isTelephone("0517677789"));
    Assert.assertEquals(false, RegexUtils.isTelephone("dsf*"));
  }

    @Test
  public void regexTest() {
    Assert.assertEquals(false, RegexUtils.isDigital("0518-76776789"));
    Assert.assertEquals(true, RegexUtils.isDigital("0517677789"));
    Assert.assertEquals(false, RegexUtils.isDigital("dsf*"));
    Assert.assertEquals(false, RegexUtils.isAlpha("dsf*"));
    Assert.assertEquals(true, RegexUtils.isAlpha("dsf"));
    Assert.assertEquals(true, RegexUtils.isAlpha("HGGd"));
    Assert.assertEquals(true, RegexUtils.isChinese("张峻滔"));
    Assert.assertEquals(false, RegexUtils.isChinese("张峻滔d"));
  }

  @Test
  public void isVehicleNoTest() {
    Assert.assertEquals(true, RegexUtils.isVehicleNo("粤B12345"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("苏B12S45"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("WJ01警0081"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("WJ0913425"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("粤Z1234港"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("粤Z1234港"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("BA12345"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("苏B12S451"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("领A231C"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("京安2a32"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("苏0213E15"));
    Assert.assertEquals(true, RegexUtils.isVehicleNo("江苏C13E12"));
    Assert.assertEquals(false, RegexUtils.isVehicleNo("111AB12S"));
    Assert.assertEquals(false, RegexUtils.isVehicleNo("test1"));
  }


}
