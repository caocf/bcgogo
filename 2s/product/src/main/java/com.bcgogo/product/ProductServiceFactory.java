package com.bcgogo.product;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: wjl
 * Date: 11-9-23
 * Time: 下午3:48
 * To change this template use File | Settings | File Templates.
 */
public class ProductServiceFactory extends AbstractSpringServiceFactory {
  public ProductServiceFactory() {
    super("classpath:/META-INF/product/services.xml");
  }

  public ProductServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/product/services.xml", jpaProperties);
  }

  public ProductServiceFactory(String configLocation) {
    super(configLocation);
  }

  public void close() {
    super.close();
  }
}
