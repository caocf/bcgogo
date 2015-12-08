package com.bcgogo.enums.txn;

/**
 * User: lw
 * Date: 14-4-14
 * Time: 下午4:29
 */
public enum AdvertStatus {
  ACTIVE("已生效"), //就是已发布
  OVERDUE("已过期"),
  WAIT_PUBLISH("待发布"),
  REPEALED("已作废");

  private String name;

  AdvertStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
