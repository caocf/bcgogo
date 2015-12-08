package com.bcgogo.stat;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class StatServiceFactory extends AbstractSpringServiceFactory {

  public StatServiceFactory() {
    super("classpath:/META-INF/stat/services.xml");
  }

  public StatServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/stat/services.xml", jpaProperties);
  }

  public StatServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }
}
