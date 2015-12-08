package com.bcgogo.txn;


import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

public class TxnServiceFactory extends AbstractSpringServiceFactory {

  public TxnServiceFactory() {
    super("classpath:/META-INF/txn/services.xml");
  }

  public TxnServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/txn/services.xml", jpaProperties);
  }

  public TxnServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }

}