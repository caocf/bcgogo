package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: lenovo
 * Date: 12-10-31
 * Time: 上午9:38
 * To change this template use File | Settings | File Templates.
 */
public enum KindStatus {
  ENABLE("可用"),
  DISABLED("不可用");

  private final String name;
  
  private KindStatus(String name){
    this.name=name;
  }

  public String getName() {
    return name;
  }
}
