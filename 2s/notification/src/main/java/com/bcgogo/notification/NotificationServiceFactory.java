package com.bcgogo.notification;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Xiao Jian
 * To change this template use File | Settings | File Templates.
 */
public class NotificationServiceFactory extends AbstractSpringServiceFactory {

  public NotificationServiceFactory() {
    super("classpath:/META-INF/notification/services.xml");
  }

  public NotificationServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/notification/services.xml", jpaProperties);
  }

  public NotificationServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }

}
