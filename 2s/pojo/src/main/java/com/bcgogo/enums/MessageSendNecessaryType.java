package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 12-8-3
 * Time: 上午9:31
 * To change this template use File | Settings | File Templates.
 */
public enum MessageSendNecessaryType {

  NECESSARY("必要"),
  UNNECESSARY("不必要");

  String type;

  MessageSendNecessaryType(String type)
  {
    this.type = type;
  }

  public String getType()
  {
    return type;
  }
}
