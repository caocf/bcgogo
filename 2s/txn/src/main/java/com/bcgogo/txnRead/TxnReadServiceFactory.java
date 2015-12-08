package com.bcgogo.txnRead;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-6-24
 * Time: 上午11:39
 */
public class TxnReadServiceFactory extends AbstractSpringServiceFactory {

  public TxnReadServiceFactory() {
    super("classpath:/META-INF/txnRead/services.xml");
  }

  public TxnReadServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/txnRead/services.xml", jpaProperties);
  }

  public TxnReadServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }

}