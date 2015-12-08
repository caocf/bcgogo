package com.bcgogo.notification.service;

import com.bcgogo.enums.notification.SmsChannel;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * User: ZhangJuntao
 * Date: 12-9-7
 * Time: 下午2:03
 * To change this template use File | Settings | File Templates.
 */
public class SmsConstantsTest {
  private static final Logger LOG = LoggerFactory.getLogger(SmsConstantsTest.class);

  @Test
  public void channelTest() {
    Assert.assertEquals("INDUSTRY", SmsChannel.INDUSTRY.name());
    LOG.info(SmsChannel.INDUSTRY.name());
    LOG.info(SmsChannel.INDUSTRY.getValue());
  }
}
