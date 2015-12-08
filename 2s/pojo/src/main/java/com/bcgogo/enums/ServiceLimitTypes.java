package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * 服务次数的状态
 * User: cfl
 * Date: 12-7-19
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
public enum ServiceLimitTypes {
  LIMITED("有限次"),
  UNLIMITED("无限次"),
  DELETE("被删除的服务");

  String status;
  ServiceLimitTypes(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return this.status;
  }
}
