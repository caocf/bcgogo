package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-31
 * Time: 上午9:31
 */
public enum MessageDayRange {
  ONE_WEEK("最近一周",7),
  ONE_MONTH("最近一个月",30),
  THREE_MONTH("最近三个月",90),
  ONE_YEAR("最近一年",365),
  ;

  private final String name;
  private final Integer value;

  private MessageDayRange(String name,Integer value) {
    this.name = name;
    this.value = value;
  }


  public String getName() {
    return name;
  }

  public Integer getValue() {
    return value;
  }
}
