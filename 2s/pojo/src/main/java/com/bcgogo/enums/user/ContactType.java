package com.bcgogo.enums.user;

/**
 * Created by IntelliJ IDEA.
 * User: Jimuchen
 * Date: 13-6-18
 * Time: 上午10:43
 */
public enum ContactType {
  CUSTOMER("客户联系人"),
  SUPPLIER("供应商联系人"),
  CUSTOMER_SUPPLIER("既是客户又是供应商联系人"),
  SHOP("店铺联系人");

  private final String name;

  private ContactType(String name){
    this.name = name;
  }

  public String getName(){
    return name;
  }
}
