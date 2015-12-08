package com.bcgogo.user;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 9/12/11
 * Time: 9:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class UserServiceFactory extends AbstractSpringServiceFactory {

  public UserServiceFactory() {
    super("classpath:/META-INF/user/services.xml");
  }

  public UserServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/user/services.xml", jpaProperties);
  }

  public UserServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }

}
