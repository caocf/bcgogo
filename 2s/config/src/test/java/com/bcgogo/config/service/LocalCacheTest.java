package com.bcgogo.config.service;

import com.bcgogo.config.AbstractTest;
import com.bcgogo.config.cache.ConfigCacheManager;
import com.bcgogo.service.ServiceManager;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-5-22
 * Time: 下午7:00
 * To change this template use File | Settings | File Templates.
 */
public class LocalCacheTest extends AbstractTest {

  @Test
  public void testSyncConfigCache() throws Exception {
    createConfig();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    String value1 = configService.getConfig("name1", -1L);
    Assert.assertEquals("value1", value1);
    String value2 = configService.getConfig("name2", -1L);
    Assert.assertEquals("value2", value2);
    Thread.sleep(500L);
    ConfigCacheManager.SYNC_INTERVAL = 500L;
    configService.setConfig("name1", "value11", -1L);
    configService.setConfig("name2", "value22", -1L);
    value1 = configService.getConfig("name1", -1L);
    value2 = configService.getConfig("name2", -1L);
    Assert.assertEquals("value11", value1);
    Assert.assertEquals("value22", value2);
    String value3 = configService.getConfig("name3", -1L);
    Assert.assertEquals("value3", value3);
    value3 = configService.getConfig("name3", -1L);
    Assert.assertEquals("value3", value3);
  }

    public void createConfig() throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    configService.setConfig("name1", "value1", -1L);
    configService.setConfig("name2", "value2", -1L);
    configService.setConfig("name3", "value3", -1L);
  }

}
