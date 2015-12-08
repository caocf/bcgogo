package com.bcgogo;

import com.bcgogo.config.ConfigServiceFactory;
import com.bcgogo.payment.PaymentServiceFactory;
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
    new PaymentServiceFactory(jpaProperties);

  }

  @AfterClass
  public static void terminate() {
  }


}
