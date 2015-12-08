package com.bcgogo.search;

import com.bcgogo.config.ConfigServiceFactory;
import com.bcgogo.config.service.ConfigService;
import com.bcgogo.search.client.SolrClientHelper;
import com.bcgogo.service.ServiceManager;
import com.bcgogo.util.h2.H2EventListener;
import com.bcgogo.utils.ShopConstant;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 10/4/11
 * Time: 10:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractTest {
  @BeforeClass
  public static void init() throws Exception {
    H2EventListener.startServer.set(false);

    Map<String, String> jpaProperties = new HashMap<String, String>();
    System.setProperty("unit.test", "true");
    System.setProperty("solr.solr.home", "src/test/resources/solr");
    jpaProperties.put("bcgogo.dataSource.url",
        "jdbc:h2:mem:test_bcgogo;DB_CLOSE_DELAY=-1;TRACE_LEVEL_SYSTEM_OUT=2" +
            ";DATABASE_EVENT_LISTENER='com.bcgogo.util.h2.H2EventListener'");

    SimpleNamingContextBuilder.emptyActivatedContextBuilder();

    new ConfigServiceFactory(jpaProperties);
    new SearchServiceFactory(jpaProperties);
    ServiceManager.getService(ConfigService.class).setConfig("RecentChangedProductExpirationTime", "10", ShopConstant.BC_SHOP_ID);

    SolrClientHelper.unitTestClear();
  }

  @AfterClass
  public static void terminate() {
  }

}
