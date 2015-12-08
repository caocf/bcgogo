package com.bcgogo.enums.user.userGuide;

/**
 * 新手指引-名字类型
 * Created by IntelliJ IDEA.
 * User: lw
 * Date: 13-3-29
 * Time: 下午3:27
 * To change this template use File | Settings | File Templates.
 */
public enum GuideStepNameEnum {
  CONTRACT_CUSTOMER_GUIDE_BEGIN("开始关联客户"),
  CUSTOMER_CONTRACT("客户关联"),
  SUPPLIER_CONTRACT("供应商关联");

  private String name;

  GuideStepNameEnum(String name) {
    this.name = name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
