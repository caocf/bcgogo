package com.bcgogo.enums.importData;

/**
 * Created by IntelliJ IDEA.
 * User: cfl
 * Date: 13-2-6
 * Time: 下午1:47
 * To change this template use File | Settings | File Templates.
 */
public enum ImportType {
  CUSTOMER("客户"),
  SUPPLIER("供应商"),
  INVENTORY("库存"),
  MEMBER_SERVICE("会员服务"),
  ORDER("单据"),
  OBD_INVENTORY("OBD库存");


  String type;

  ImportType(String type){
    this.type = type;
  }

  public String getType(){
    return this.type;
  }
}
