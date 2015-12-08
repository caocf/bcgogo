package com.bcgogo.enums.app;

/**
 * Created with IntelliJ IDEA.
 * User: XinyuQiu
 * Date: 13-11-13
 * Time: 下午1:52
 */
public enum  EnquiryShopResponseStatus {
  DISABLED("已删除"),
  RESPONSE("已报价"),
  UN_RESPONSE("未报价")
  ;
  private final String name;

  private EnquiryShopResponseStatus(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
