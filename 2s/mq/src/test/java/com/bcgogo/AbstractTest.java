package com.bcgogo;

/**
 * @author zhangjuntao
 * @version 1.0
 * @since <pre>8,30, 2013</pre>
 */

import com.bcgogo.api.ApiResponse;
import com.bcgogo.api.LoginDTO;
import com.bcgogo.api.RegistrationDTO;
import com.bcgogo.config.ConfigServiceFactory;
import com.bcgogo.config.dto.ShopDTO;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.config.service.IShopService;
import com.bcgogo.enums.app.AppPlatform;
import com.bcgogo.enums.shop.ShopStatus;
import com.bcgogo.notification.NotificationServiceFactory;
import com.bcgogo.payment.PaymentServiceFactory;
import com.bcgogo.product.ProductServiceFactory;
import com.bcgogo.search.SearchServiceFactory;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.stat.StatServiceFactory;
import com.bcgogo.txn.TxnServiceFactory;
import com.bcgogo.user.UserServiceFactory;
import com.bcgogo.user.dto.permission.ShopVersionDTO;
import com.bcgogo.user.service.permission.IShopVersionService;
import com.bcgogo.util.h2.H2EventListener;
import com.bcgogo.utils.ConfigConstant;
import com.bcgogo.utils.ShopConstant;
import com.bcgogo.utils.SmsConstant;
import com.bcgogo.utils.StringUtil;
import org.junit.BeforeClass;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockServletContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.support.XmlWebApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class AbstractTest {
  protected MockHttpServletRequest request;
  protected MockHttpServletResponse response;
  protected ModelMap modelMap;

  @BeforeClass
  public static void init() throws Exception {
    XmlWebApplicationContext ctx;

      H2EventListener.startServer.set(false);

      Map<String, String> jpaProperties = new HashMap<String, String>();
      System.setProperty("unit.test", "true");
      System.setProperty("solr.solr.home", "../search/src/test/resources/solr");
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
      new PaymentServiceFactory(jpaProperties);

      SolrClientHelper.unitTestClear();


      String[] paths = {};
      ctx = new XmlWebApplicationContext();
      ctx.setConfigLocations(paths);
      ctx.setServletContext(new MockServletContext(""));
      ctx.refresh();
      IConfigService configService = ServiceManager.getService(IConfigService.class);
      configService.setConfig("MOCK_SMS", "on", ShopConstant.BC_SHOP_ID);
      configService.setConfig("smsTag", "on", ShopConstant.BC_SHOP_ID);
//      configService.setConfig("SmsSenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
      configService.setConfig("SmsMarketingSenderStrategy", SmsConstant.SmsLianYuConstant.name, ShopConstant.BC_SHOP_ID);
      configService.setConfig("SmsIndustrySenderStrategy", SmsConstant.SmsYiMeiConstant.name, ShopConstant.BC_SHOP_ID);
      configService.setConfig("SelectOptionNumber", "5", ShopConstant.BC_SHOP_ID);

      configService.setConfig("RecentChangedProductExpirationTime", "10", ShopConstant.BC_SHOP_ID);
      configService.setConfig(ConfigConstant.UP_YUN_DOMAIN_URL, "http://image.bcgogo.com", ShopConstant.BC_SHOP_ID);
      configService.setConfig(ConfigConstant.UP_YUN_SEPARATOR, "!", ShopConstant.BC_SHOP_ID);
  }
      //版本
  public ShopVersionDTO createIntegratedShopVersionDTO(){
    ShopVersionDTO shopVersionDTO = new ShopVersionDTO();
    shopVersionDTO.setName("INTEGRATED_SHOP");
    shopVersionDTO.setValue("汽修综合版");
    ServiceManager.getService(IShopVersionService.class).saveOrUpdateShopVersion(shopVersionDTO);
    return shopVersionDTO;
  }

  public ShopDTO createShop(ShopVersionDTO shopVersionDTO) throws Exception {
    if(shopVersionDTO == null){
      shopVersionDTO =  ServiceManager.getService(IShopVersionService.class).getShopVersionByName("INTEGRATED_SHOP");
      if(shopVersionDTO == null) {
        shopVersionDTO = createIntegratedShopVersionDTO();
      }
    }
    ShopDTO shopDTO = new ShopDTO();
    shopDTO.setShopStatus(ShopStatus.REGISTERED_PAID);
    shopDTO.setName(StringUtil.getCharacterNumberOrChinese(10));
    shopDTO.setMobile("18"+StringUtil.getRandomNumberStr(9));
    shopDTO.setStoreManagerMobile("18"+StringUtil.getRandomNumberStr(9));
    shopDTO.setShopVersionId(shopVersionDTO.getId());
    ServiceManager.getService(IShopService.class).createShop(shopDTO);
    return shopDTO;
  }

}
