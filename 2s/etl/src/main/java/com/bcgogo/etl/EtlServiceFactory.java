package com.bcgogo.etl;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Jimuchen
 * Date: 14-2-25
 * Time: 下午7:02
 * To change this template use File | Settings | File Templates.
 */
public class EtlServiceFactory extends AbstractSpringServiceFactory {
  public EtlServiceFactory() {
    super("classpath:/META-INF/etl/services.xml");
  }

  public EtlServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/etl/services.xml", jpaProperties);
  }

  public EtlServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }
}
