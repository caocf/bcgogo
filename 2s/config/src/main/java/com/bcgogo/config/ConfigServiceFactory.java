package com.bcgogo.config;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

public class ConfigServiceFactory extends AbstractSpringServiceFactory {

  public ConfigServiceFactory() {
    super("classpath:/META-INF/config/services.xml");
  }

  public ConfigServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/config/services.xml", jpaProperties);
  }

  public ConfigServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }

}