package com.bcgogo;

/**
 * Created by IntelliJ IDEA.
 * User: 张传龙
 * Date: 12-4-5
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */

import com.bcgogo.admin.DataMaintenanceController;
import com.bcgogo.config.ConfigServiceFactory;
import com.bcgogo.config.ShopConfigController;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.model.Config;
import com.bcgogo.config.model.ConfigDaoManager;
import com.bcgogo.config.model.ConfigWriter;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.ShopConfigScene;
import com.bcgogo.enums.ShopConfigStatus;
import com.bcgogo.enums.shop.ShopState;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.exception.BcgogoException;
import com.bcgogo.notification.NotificationServiceFactory;
import com.bcgogo.notification.model.MessageTemplate;
import com.bcgogo.notification.model.NotificationDaoManager;
import com.bcgogo.notification.model.NotificationWriter;
import com.bcgogo.product.ProductServiceFactory;
import com.bcgogo.search.SearchServiceFactory;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.StatServiceFactory;
import com.bcgogo.txn.TxnServiceFactory;
import com.bcgogo.user.UserServiceFactory;
import com.bcgogo.util.h2.H2EventListener;
import com.bcgogo.utils.ShopConstant;
import org.junit.BeforeClass;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AbstractTest {
  protected MockHttpServletRequest request;
  protected MockHttpServletResponse response;
  protected ModelMap modelMap;
  public static NotificationWriter notificationWriter;
  public static NotificationDaoManager notificationDaoManager;
  public static ConfigDaoManager configDaoManager;
  public static DataMaintenanceController dataMaintenanceController;
  public static ShopConfigController shopConfigController;
  public static MessageTemplate msgTemplate;
  public static  Config config;


  @BeforeClass
  public static void init() throws Exception {
        XmlWebApplicationContext ctx;
    H2EventListener.startServer.set(false);

    Map<String, String> jpaProperties = new HashMap<String, String>();
    System.setProperty("solr.solr.home", "src/test/resources/solr");
    System.setProperty("unit.test", "true");
    jpaProperties.put("bcgogo.dataSource.url",
        "jdbc:h2:mem:test_bcgogo;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=2" +
            ";DATABASE_EVENT_LISTENER='com.bcgogo.util.h2.H2EventListener'");

    SimpleNamingContextBuilder.emptyActivatedContextBuilder();

    new ConfigServiceFactory(jpaProperties);
    new UserServiceFactory(jpaProperties);
    new NotificationServiceFactory(jpaProperties);
    new SearchServiceFactory(jpaProperties);
    new ProductServiceFactory(jpaProperties);
    new TxnServiceFactory(jpaProperties);
    new StatServiceFactory(jpaProperties);
    new UserServiceFactory(jpaProperties);

        //统一在单元测试的时候清理 索引数据  防止老脏数据影响单元测试   导致单元测试过不了

    String[] paths = {};
    ctx = new XmlWebApplicationContext();
    ctx.setConfigLocations(paths);
    ctx.setServletContext(new MockServletContext(""));
    ctx.refresh();
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
    configService.setConfig("RecentChangedProductExpirationTime", "10", ShopConstant.BC_SHOP_ID);

    dataMaintenanceController= new DataMaintenanceController();
    createConfigForTest();
    createMsgTemplateForTest();

  }
  public static void createMsgTemplateForTest(){
    if(msgTemplate!=null){
      return;
    }
    msgTemplate=new MessageTemplate();
   msgTemplate.setType("m1");
   msgTemplate.setContent("m1_content");
   msgTemplate.setShopId(-1l);
   notificationDaoManager = ServiceManager.getService(NotificationDaoManager.class);
   notificationWriter=notificationDaoManager.getWriter();
   NotificationWriter writer = notificationDaoManager.getWriter();
   Object status = writer.begin();
   try {
     writer.save(msgTemplate);
     writer.commit(status);
   }
   finally {
     writer.rollback(status);
   }
 }
  public static void createConfigForTest(){
    if(config!=null){
      return;
    }
    config =new Config();
    config.setName("MerId");
    config.setValue("808080580006213");
    config.setShopId(-1l);
    configDaoManager = ServiceManager.getService(ConfigDaoManager.class);
    ConfigWriter writer = configDaoManager.getWriter();
    Object status = writer.begin();
    try {
      writer.save(config);
      writer.commit(status);
    }
    finally {
      writer.rollback(status);
    }
  }

  public ShopDTO createShop() throws BcgogoException, IOException {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setAccount("test");
    shopDTO.setName("test");
    shopDTO.setMobile("1234567899");
    shopDTO.setStoreManagerMobile("1234567890");
    shopDTO.setAccount("6224021");
    shopDTO.setAddress("安徽省");
    shopDTO.setAgent("张三");
    shopDTO.setAgentId("120202020");
    shopDTO.setBank("建设银行");
    shopDTO.setEmail("www.224422@qq.com");
    shopDTO.setBusinessScope("个体经营");
    shopDTO.setContact("张三");
    shopDTO.setFax("0512-125565656");
    shopDTO.setName("统购车业");
    shopDTO.setStoreManager("张传龙");
    ServiceManager.getService(IShopService.class).createShop(shopDTO);
    return shopDTO;
  }

  public ShopDTO createShop(ShopDTO shopDTO) throws Exception
  {
    IConfigService configService = ServiceManager.getService(IConfigService.class);
    shopDTO.setShopStatus(ShopStatus.REGISTERED_PAID);
    shopDTO.setShopState(ShopState.ACTIVE);
    ServiceManager.getService(IShopService.class).createShop(shopDTO);
    return shopDTO;
  }

  public void createShopConfig(Long shopId,ShopConfigScene scene,ShopConfigStatus switchStatus)
  {
    IShopConfigService shopConfigService = ServiceManager.getService(IShopConfigService.class);

    shopConfigService.setShopConfig(shopId,scene,switchStatus);
  }
}
