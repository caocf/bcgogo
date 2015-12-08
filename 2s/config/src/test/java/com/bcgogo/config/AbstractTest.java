package com.bcgogo.config;

/**
 * Created by IntelliJ IDEA.
 * User: ZouJianhong
 * Date: 12-4-18
 * Time: 下午4:20
 * To change this template use File | Settings | File Templates.
 */

import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.util.h2.H2EventListener;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import java.util.HashMap;
import java.util.Map;

public class AbstractTest {

  @BeforeClass
  public static void init() throws Exception {
    H2EventListener.startServer.set(false);

    Map<String, String> jpaProperties = new HashMap<String, String>();
    System.setProperty("unit.test", "true");
    jpaProperties.put("bcgogo.dataSource.url",
        "jdbc:h2:mem:test_bcgogo;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=2" +
            ";DATABASE_EVENT_LISTENER='com.bcgogo.util.h2.H2EventListener'");

    SimpleNamingContextBuilder.emptyActivatedContextBuilder();
    new ConfigServiceFactory(jpaProperties);

  }

  public Long createShop() throws Exception {
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setAccount("test");
    shopDTO.setName("test");
    ServiceManager.getService(IShopService.class).createShop(shopDTO);
    return shopDTO.getId();
  }

  public void createConfig() throws Exception {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    configService.setConfig("name1", "value1", -1L);
    configService.setConfig("name2", "value2", -1L);
    configService.setConfig("name3", "value3", -1L);
  }

  @AfterClass
  public static void terminate() {
  }


}
