package com.bcgogo.enums.txn.pushMessage;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-6-5
 * Time: 下午5:02
 * To change this template use File | Settings | File Templates.
 */
public enum PushMessageLevel {
  HIGH(15),
  NORMAL(10),
  LOW(5);

  private final int value;

  private PushMessageLevel(int value) {
    this.value = value;
  }

  public Integer getValue() {
    return value;
  }

  public static PushMessageLevel valueOf(int value) {
    for (PushMessageLevel level : PushMessageLevel.values()) {
      if (level.getValue() == value) {
        return level;
      }
    }
    return null;
  }
}