package com.bcgogo.pojo.message;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * Author: ndong
 * Date: 2015-6-4
 * Time: 16:29
 */
public class MQLoginMessageDTO implements Serializable{
  private String mqSessionId;
  private String name;
  private String pass;

  public String getMqSessionId() {
    return mqSessionId;
  }

  public void setMqSessionId(String mqSessionId) {
    this.mqSessionId = mqSessionId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPass() {
    return pass;
  }

  public void setPass(String pass) {
    this.pass = pass;
  }
}
