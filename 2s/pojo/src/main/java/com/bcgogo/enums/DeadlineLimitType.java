package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-7-29
 * Time: 上午11:32
 * To change this template use File | Settings | File Templates.
 */
public enum DeadlineLimitType {
  LIMITED("有限期"),
  UNLIMITED("不限期");

  String type;

  DeadlineLimitType(String type)
  {
    this.type = type;
  }

  public String getType()
  {
    return type;
  }
}
