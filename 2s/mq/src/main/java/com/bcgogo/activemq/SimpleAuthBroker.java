package com.bcgogo.activemq;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-3-24
 * Time: 16:12
 */

import org.apache.activemq.broker.Broker;
import org.apache.activemq.security.SimpleAuthenticationBroker;
import org.apache.activemq.xbean.XBeanBrokerService;

import java.util.HashMap;
import java.util.Map;

public class SimpleAuthBroker extends XBeanBrokerService {

  private String user;
  private String password;

  @SuppressWarnings("unchecked")
  protected Broker addInterceptors(Broker broker) throws Exception {
    broker = super.addInterceptors(broker);
    Map passwords = new HashMap();
    passwords.put(getUser(), getPassword());
    broker = new SimpleAuthenticationBroker(broker, passwords, new HashMap());
    return broker;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
