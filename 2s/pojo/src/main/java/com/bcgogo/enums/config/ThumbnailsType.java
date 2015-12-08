package com.bcgogo.enums.config;

/**
 * Created with IntelliJ IDEA.
 * User: xzhu
 * Date: 13-7-26
 * Time: 下午5:40
 * To change this template use File | Settings | File Templates.
 */
public enum ThumbnailsType {
  FIX_WIDTH("限定宽度，高度自适应"),
  FIX_HEIGHT("限定高度，宽度自适应"),
  FIX_WIDTH_OR_HEIGHT("限定宽度和高度"),
  FIX_MAX("限定最长边，短边自适应"),
  FIX_MIN("限定最短边，长边自适应"),
  FIX_SCALE("等比例缩小图片"),
  UNCHANGED("保持原尺寸不变");

  private String value;

  private ThumbnailsType(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
