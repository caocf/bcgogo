package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 12-7-21
 * Time: 下午1:49
 * To change this template use File | Settings | File Templates.
 */
public enum MessageValidTimePeriod {
  UNLIMITED("不限期",12000),
  IN_ONE_MONTH("一个月内有效",1),
  IN_TWO_MONTH("二个月内有效",2),
  IN_THREE_MONTH("三个月内有效",3),
  IN_FOUR_MONTH("四个月内有效",4),
  IN_FIVE_MONTH("五个月内有效",5),
  IN_SIX_MONTH("半年内有效",6),
  IN_TWELVE_MONTH("一年内有效",12);

  private final String name;
  private final Integer number;

  private MessageValidTimePeriod(String name) {
    this.name = name;
    this.number = -1;
  }

  private MessageValidTimePeriod(String name,Integer number) {
    this.name = name;
    this.number = number;
  }

  public String getName() {
    return name;
  }

  public Integer getNumber() {
    return number;
  }
}
