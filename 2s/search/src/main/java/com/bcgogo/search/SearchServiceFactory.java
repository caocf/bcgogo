package com.bcgogo.search;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Caiwei
 * Date: 10/4/11
 * Time: 10:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class SearchServiceFactory extends AbstractSpringServiceFactory {

  public SearchServiceFactory() {
    super("classpath:/META-INF/search/services.xml");
  }

  public SearchServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/search/services.xml", jpaProperties);
  }

  public SearchServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }

}
