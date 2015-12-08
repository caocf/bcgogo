package com.bcgogo.enums.txn.message;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-7-21
 * Time: 下午1:49
 */
public enum MessageType {
  PROMOTIONS_MESSAGE("促销"),
  WARN_MESSAGE("提醒");

  private final String name;

  private MessageType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
