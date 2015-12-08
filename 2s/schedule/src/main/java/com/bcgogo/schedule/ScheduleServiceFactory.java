package com.bcgogo.schedule;


import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

public class ScheduleServiceFactory extends AbstractSpringServiceFactory {

  public ScheduleServiceFactory() {
    super("classpath:/META-INF/schedule/services.xml");
  }

  public ScheduleServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/schedule/services.xml", jpaProperties);
  }

  public ScheduleServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }

}