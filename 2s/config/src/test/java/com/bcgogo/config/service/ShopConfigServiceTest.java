package com.bcgogo.config.service;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.cache.ShopConfigCacheManager;
import com.bcgogo.config.model.ShopConfig;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.enums.ShopConfigStatus;
import com.bcgogo.service.ServiceManager;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-9
 * Time: 下午2:07
 * 这里暂时只有会员的设置，所以只拿会员来做测试
 * To change this template use File | Settings | File Templates.
 */

public class ShopConfigServiceTest extends AbstractTest {
  @Test
  public void testSetShopConfig() throws Exception
  {
    Long shopId = createShop();

    IShopConfigService shopConfigService = ServiceManager.getService(IShopConfigService.class);

    ShopConfig shopConfig = shopConfigService.setShopConfig(shopId, ShopConfigScene.MEMBER, ShopConfigStatus.ON);

    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfig.getScene());
    Assert.assertEquals(ShopConfigStatus.ON,shopConfig.getStatus());

    shopConfig = ShopConfigCacheManager.getShopConfig(shopId,ShopConfigScene.MEMBER);

    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfig.getScene());
    Assert.assertEquals(ShopConfigStatus.ON,shopConfig.getStatus());

    shopConfig = (ShopConfig)MemCacheAdapter.get(MemcachePrefix.shopConfig.getValue() + ShopConfigScene.MEMBER.toString() + shopId);

    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfig.getScene());
    Assert.assertEquals(ShopConfigStatus.ON,shopConfig.getStatus());

    shopConfig = shopConfigService.setShopConfig(shopId, ShopConfigScene.MEMBER, ShopConfigStatus.OFF);

    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfig.getScene());
    Assert.assertEquals(ShopConfigStatus.OFF,shopConfig.getStatus());

    shopConfig = ShopConfigCacheManager.getShopConfig(shopId,ShopConfigScene.MEMBER);

    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfig.getScene());
    Assert.assertEquals(ShopConfigStatus.OFF,shopConfig.getStatus());

    shopConfig = (ShopConfig)MemCacheAdapter.get(MemcachePrefix.shopConfig.getValue() + ShopConfigScene.MEMBER.toString() + shopId);

    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfig.getScene());
    Assert.assertEquals(ShopConfigStatus.OFF,shopConfig.getStatus());

    MemCacheAdapter.delete(MemcachePrefix.shopConfig.getValue()+ShopConfigScene.MEMBER.toString()+shopId);
  }

  @Test
  public void testGetConfigSwitchStatus() throws Exception
  {
    IShopConfigService shopConfigService = ServiceManager.getService(IShopConfigService.class);

    Long shopId = createShop();

    MemCacheAdapter.delete(MemcachePrefix.shopConfig.getValue()+ShopConfigScene.MEMBER.toString()+shopId);

    ShopConfigStatus switchStatus = shopConfigService.getConfigSwitchStatus(ShopConfigScene.MEMBER,shopId);

    Assert.assertEquals(ShopConfigStatus.ON,switchStatus);

    ShopConfig shopConfig = ShopConfigCacheManager.getShopConfig(shopId,ShopConfigScene.MEMBER);

    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfig.getScene());
    Assert.assertEquals(ShopConfigStatus.ON,shopConfig.getStatus());

    shopConfig = (ShopConfig)MemCacheAdapter.get(MemcachePrefix.shopConfig.getValue() + ShopConfigScene.MEMBER.toString() + shopId);

    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfig.getScene());
    Assert.assertEquals(ShopConfigStatus.ON,shopConfig.getStatus());

    MemCacheAdapter.delete(MemcachePrefix.shopConfig.getValue()+ShopConfigScene.MEMBER.toString()+shopId);
  }
}
