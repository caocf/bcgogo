package com.bcgogo.userreport;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

/**
 * User: Xiao Jian
 * Date: 12-1-5
 */

public class UserReportServiceFactory extends AbstractSpringServiceFactory {

  public UserReportServiceFactory() {
    super("classpath:/META-INF/userreport/services.xml");
  }

  public UserReportServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/userreport/services.xml", jpaProperties);
  }

  public UserReportServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }
}
