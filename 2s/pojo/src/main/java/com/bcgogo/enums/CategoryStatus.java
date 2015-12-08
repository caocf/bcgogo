package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-11-20
 * Time: 下午2:34
 * To change this template use File | Settings | File Templates.
 */
public enum CategoryStatus {
  DISABLED("删除"),
  ENABLED("恢复");

  private String status;

  private CategoryStatus(String status)
  {
    this.status = status;
  }

  public String getStatus()
  {
    return this.status;
  }
}
