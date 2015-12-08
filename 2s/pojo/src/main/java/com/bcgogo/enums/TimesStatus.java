package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-29
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */
public enum TimesStatus {
  //timesStatus单选框中的值0表示有次数的单选框，1表示无限次的单选框
  //cardTimesStatus中的0表示不是从套餐中带来的服务，1表示是从套餐中带出来的服务
  LIMITED("0"),
  UNLIMITED("1");

  String status;

  TimesStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return status;
  }
}
