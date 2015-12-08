package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 12-12-10
 * Time: 上午6:00
 * To change this template use File | Settings | File Templates.
 */
public enum OperateType {
  ADD("新增"),
  UPDATE("更新"),
  LOGIC_DELETE("逻辑删除"),
  DELETE("删除");
  private final String name;

  private OperateType(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
