package com.bcgogo.enums;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-1-17
 * Time: 下午4:57
 * To change this template use File | Settings | File Templates.
 */
public enum RelationMidStatus {
  UN_APPLY_RELATED("未申请关联"),
  APPLY_RELATED("已申请关联"),
  BE_APPLY_RELATED("被申请关联"),

  RELATED("已关联"),

  COLLECTED("已收藏对方"),
  BE_COLLECTED("被对方收藏");

  private final String name;

  private RelationMidStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
