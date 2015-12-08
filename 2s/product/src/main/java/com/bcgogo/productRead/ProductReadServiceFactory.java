package com.bcgogo.productRead;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-6-24
 * Time: 上午11:39
 */
public class ProductReadServiceFactory extends AbstractSpringServiceFactory {

  public ProductReadServiceFactory() {
    super("classpath:/META-INF/productRead/services.xml");
  }

  public ProductReadServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/productRead/services.xml", jpaProperties);
  }

  public ProductReadServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }

}