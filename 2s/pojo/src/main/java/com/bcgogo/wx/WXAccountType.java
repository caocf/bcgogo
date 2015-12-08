package com.bcgogo.wx;

/**
 * 微信公共号类型
 * Author: ndong
 * Date: 2015-4-13
 * Time: 13:58
 */
public enum WXAccountType {
  YIFA("一发软件"),
  MIRROR("后视镜"),;

  WXAccountType(String type) {
    this.type = type;
  }

  private String type;
}
