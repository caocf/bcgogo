package com.bcgogo.payment;

import com.bcgogo.util.spring.AbstractSpringServiceFactory;

import java.util.Map;


public class PaymentServiceFactory extends AbstractSpringServiceFactory {

  public PaymentServiceFactory() {
    super("classpath:/META-INF/payment/services.xml");
  }

  public PaymentServiceFactory(Map<String, String> jpaProperties) {
    super("classpath:/META-INF/payment/services.xml", jpaProperties);
  }

  public PaymentServiceFactory(String configLocation) {
    super(configLocation);
  }

  @Override
  public void close() {
    super.close();
  }

}
