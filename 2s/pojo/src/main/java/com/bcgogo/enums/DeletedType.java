package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: xzhu
 * 逻辑删除的标识     一般逻辑删除标识字段名称都为 deleted
 * status用于别的业务场景
 */
public enum DeletedType {
  TRUE("已删除"),    //已处理
  FALSE("未删除"),  //未处理
  DELETED("删除");

  private String value;

  DeletedType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
