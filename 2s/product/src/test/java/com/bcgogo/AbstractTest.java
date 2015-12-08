package com.bcgogo;

import com.bcgogo.config.ConfigServiceFactory;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.config.service.IConfigService;
import com.bcgogo.product.ProductServiceFactory;
import com.bcgogo.search.SearchServiceFactory;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.search.service.ISearchService;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.util.h2.H2EventListener;
import com.bcgogo.utils.ShopConstant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import java.util.HashMap;
import java.util.Map;

//import com.bcgogo.txn.TxnServiceFactory;

public class AbstractTest {

  @BeforeClass
  public static void init() throws Exception {
    H2EventListener.startServer.set(false);

    Map<String, String> jpaProperties = new HashMap<String, String>();
    System.setProperty("unit.test", "true");
    System.setProperty("solr.solr.home", "../search/src/test/resources/solr");
    jpaProperties.put("bcgogo.dataSource.url",
        "jdbc:h2:mem:test_bcgogo;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=2" +
            ";DATABASE_EVENT_LISTENER='com.bcgogo.util.h2.H2EventListener'");

    SimpleNamingContextBuilder.emptyActivatedContextBuilder();

    new ConfigServiceFactory(jpaProperties);
    new ProductServiceFactory(jpaProperties);
    new SearchServiceFactory(jpaProperties);

    SolrClientHelper.unitTestClear();

    IConfigService configService = ServiceManager.getService(ConfigService.class);
    configService.setConfig("RecentChangedProductExpirationTime", "10", ShopConstant.BC_SHOP_ID);

    configService.setConfig("vehicledataurl", "../product/src/test/resources/productAndVehicleData/车型数据简化.csv", 1L);
    configService.setConfig("productdataurl", "../product/src/test/resources/productAndVehicleData/轮胎数据.csv", 1L);
    configService.setConfig("productvehicledataurl", "../product/src/test/resources/productAndVehicleData/车款用品表.csv", 1L);
    ServiceManager.getService(ISearchService.class).deleteByQuery("*:*", "vehicle");
    ServiceManager.getService(ISearchService.class).deleteByQuery("*:*","product");
  //  productService.readFormFile(1l, "vehicledataurl", "0");
  //  productService.readFormFile(1l, "productdataurl", "1");
  //  productService.readFormFile(1l, "productvehicledataurl", "2");
  }

  @AfterClass
  public static void terminate() {
  }


}
