package com.bcgogo.enums.config;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-26
 * Time: 下午5:51
 * To change this template use File | Settings | File Templates.
 */
public enum  WaterMarkPosition {
  LEFT_TOP("顶部居左"),
  CENTER_TOP("顶部居中"),
  RIGHT_TOP("顶部居右"),
  LEFT_MIDDLE("中部居左"),
  CENTER_MIDDLE("中部居中"),
  RIGHT_MIDDLE("中部居右"),
  LEFT_BOTTOM("底部居左"),
  CENTER_BOTTOM("底部居中"),
  RIGHT_BOTTOM("底部居右");

  private String value;

  private WaterMarkPosition(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
