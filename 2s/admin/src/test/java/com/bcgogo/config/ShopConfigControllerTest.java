package com.bcgogo.config;

import com.bcgogo.AbstractTest;
import com.bcgogo.config.cache.ShopConfigCacheManager;
import com.bcgogo.config.dto.ApplyShopSearchCondition;
import com.bcgogo.config.dto.ShopConfigDTO;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.model.Shop;
import com.bcgogo.config.model.ShopConfig;
import com.bcgogo.config.util.MemCacheAdapter;
import com.bcgogo.constant.MemcachePrefix;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.enums.ShopConfigStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.ui.ModelMap;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-13
 * Time: 上午10:35
 * To change this template use File | Settings | File Templates.
 */
public class ShopConfigControllerTest extends AbstractTest{

  private static final Logger LOG = LoggerFactory.getLogger(ShopConfigControllerTest.class);

  @Before
  public void setUp() throws Exception
  {
    shopConfigController = new ShopConfigController();
    modelMap = new ModelMap();
    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
  }

  @After
  public void tearDown() throws Exception {
    request = null;
    response = null;
    modelMap = new ModelMap();
    MemCacheAdapter.flushAll();
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.deleteAllShopConfig();
      writer.commit(status);
    } finally {
      writer.rollback(status);
    }
  }

  @Test
  public void testGetShopConfigBySceneAndShop() throws Exception
  {
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setName("testOne");
    shopDTO.setMobile("12345678987");

    shopDTO = createShop(shopDTO);

    shopConfigController.getShopConfigBySceneAndShop(request,response,1,10, ShopConfigScene.MEMBER);

    List<ShopConfigDTO> shopConfigDTOs = (List<ShopConfigDTO>)request.getAttribute("shopConfigDTOs");

    Assert.assertEquals(null,shopConfigDTOs);

    createShopConfig(shopDTO.getId(),ShopConfigScene.MEMBER, ShopConfigStatus.ON);

    shopConfigController.getShopConfigBySceneAndShop(request,response,1,10, ShopConfigScene.MEMBER);
    shopConfigDTOs = (List<ShopConfigDTO>)request.getAttribute("shopConfigDTOs");

    Assert.assertEquals(1,shopConfigDTOs.size());
    Assert.assertEquals(shopDTO.getId(),shopConfigDTOs.get(0).getShopId());
    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfigDTOs.get(0).getScene());
    Assert.assertEquals(ShopConfigStatus.ON,shopConfigDTOs.get(0).getStatus());

    request.setParameter("shopId","123");
    shopConfigController.getShopConfigBySceneAndShop(request,response,1,10, ShopConfigScene.MEMBER);
    shopConfigDTOs = (List<ShopConfigDTO>)request.getAttribute("shopConfigDTOs");

    Assert.assertEquals(null,shopConfigDTOs);


    request.removeParameter("shopId");
    request.setParameter("shopName",shopDTO.getName());

    shopConfigController.getShopConfigBySceneAndShop(request,response,1,10, ShopConfigScene.MEMBER);
    shopConfigDTOs = (List<ShopConfigDTO>)request.getAttribute("shopConfigDTOs");

    Assert.assertEquals(1,shopConfigDTOs.size());
    Assert.assertEquals(shopDTO.getId(),shopConfigDTOs.get(0).getShopId());
    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfigDTOs.get(0).getScene());
    Assert.assertEquals(ShopConfigStatus.ON,shopConfigDTOs.get(0).getStatus());

  }

  @Test
  public void testChangeConfigSwitch() throws Exception
  {
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setName("testTwo");
    shopDTO.setMobile("12345678980");

    shopDTO = createShop(shopDTO);

    createShopConfig(shopDTO.getId(),ShopConfigScene.MEMBER, ShopConfigStatus.ON);

    request.removeParameter("shopId");
    request.setParameter("shopName",shopDTO.getName());

    shopConfigController.getShopConfigBySceneAndShop(request,response,1,10, ShopConfigScene.MEMBER);
    List<ShopConfigDTO> shopConfigDTOs = (List<ShopConfigDTO>)request.getAttribute("shopConfigDTOs");

    Assert.assertEquals(1,shopConfigDTOs.size());
    Assert.assertEquals(shopDTO.getId(),shopConfigDTOs.get(0).getShopId());
    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfigDTOs.get(0).getScene());
    Assert.assertEquals(ShopConfigStatus.ON,shopConfigDTOs.get(0).getStatus());

    shopConfigController.changeConfigSwitch(request,response,shopDTO.getId(),ShopConfigScene.MEMBER,ShopConfigStatus.OFF);

    ShopConfig shopConfig = (ShopConfig)request.getAttribute("shopConfig");

    Assert.assertEquals(shopDTO.getId(),shopConfig.getShopId());
    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfig.getScene());
    Assert.assertEquals(ShopConfigStatus.OFF,shopConfig.getStatus());

    shopConfigController.changeConfigSwitch(request,response,shopDTO.getId(),ShopConfigScene.MEMBER,ShopConfigStatus.ON);

    shopConfig = (ShopConfig)request.getAttribute("shopConfig");

    Assert.assertEquals(shopDTO.getId(),shopConfig.getShopId());
    Assert.assertEquals(ShopConfigScene.MEMBER,shopConfig.getScene());
    Assert.assertEquals(ShopConfigStatus.ON,shopConfig.getStatus());

  }

  @Test
  public void testCheckShop() throws Exception
  {
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setName("testThree");
    shopDTO.setMobile("12345678982");

    shopDTO = createShop(shopDTO);

    shopConfigController.checkShop(request,response,shopDTO.getName());

    String jsonStr = (String)request.getAttribute("jsonStr");

    Assert.assertEquals(shopDTO.getId().toString(),jsonStr);

  }

  @Test
  public void testCheckShopExistAndShopConfigExist() throws Exception
  {
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setName("testFour");
    shopDTO.setMobile("12345678985");

    shopDTO = createShop(shopDTO);

    shopConfigController.checkShopExistAndShopConfigExist(request,response,"ssss",ShopConfigScene.MEMBER);

    String jsonStr = (String)request.getAttribute("jsonStr");

    Assert.assertEquals("noShop",jsonStr);

    shopConfigController.checkShopExistAndShopConfigExist(request,response,shopDTO.getName(),ShopConfigScene.MEMBER);

    jsonStr = (String)request.getAttribute("jsonStr");

    Assert.assertEquals(shopDTO.getId().toString(),jsonStr);

    createShopConfig(shopDTO.getId(),ShopConfigScene.MEMBER, ShopConfigStatus.ON);

    shopConfigController.checkShopExistAndShopConfigExist(request,response,shopDTO.getName(),ShopConfigScene.MEMBER);

    jsonStr = (String)request.getAttribute("jsonStr");

    Assert.assertEquals("hasShopConfig",jsonStr);

  }

  @Test
  public void testSaveShopConfig() throws Exception
  {
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setName("testFive");
    shopDTO.setMobile("12345678988");

    shopDTO = createShop(shopDTO);

    ShopConfigDTO shopConfigDTO = new ShopConfigDTO();

    shopConfigDTO.setShopId(shopDTO.getId());

    shopConfigDTO.setScene(ShopConfigScene.MEMBER);

    shopConfigDTO.setStatus(ShopConfigStatus.ON);

    shopConfigController.saveShopConfig(request,response,shopConfigDTO);

    ShopConfig shopConfig = (ShopConfig)request.getAttribute("shopConfig");

    Assert.assertNotNull(shopConfig.getId());
    Assert.assertEquals(shopDTO.getId(),shopConfig.getShopId());
    Assert.assertEquals(shopConfigDTO.getScene(),shopConfig.getScene());
    Assert.assertEquals(shopConfigDTO.getStatus(),shopConfig.getStatus());

    shopConfig = ShopConfigCacheManager.getShopConfig(shopConfigDTO.getShopId(),shopConfigDTO.getScene());

    Assert.assertEquals(shopDTO.getId(),shopConfig.getShopId());
    Assert.assertEquals(shopConfigDTO.getScene(),shopConfig.getScene());
    Assert.assertEquals(shopConfigDTO.getStatus(),shopConfig.getStatus());

    shopConfig = (ShopConfig)MemCacheAdapter.get(MemcachePrefix.shopConfig.getValue() + shopConfigDTO.getScene().toString() + shopConfigDTO.getShopId());

    Assert.assertNotNull(shopConfig.getId());
    Assert.assertEquals(shopDTO.getId(),shopConfig.getShopId());
    Assert.assertEquals(shopConfigDTO.getScene(),shopConfig.getScene());
    Assert.assertEquals(shopConfigDTO.getStatus(),shopConfig.getStatus());
  }

}
