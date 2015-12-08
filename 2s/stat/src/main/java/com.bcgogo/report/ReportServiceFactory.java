package com.bcgogo.report;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class ReportServiceFactory extends AbstractSpringServiceFactory {

  public ReportServiceFactory() {
    super("classpath:/META-INF/report/services.xml");
  }

  public ReportServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/report/services.xml", jpaProperties);
  }

  public ReportServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }
}
