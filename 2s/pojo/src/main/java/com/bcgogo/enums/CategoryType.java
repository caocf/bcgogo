package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-12
 * Time: 下午2:40
 * To change this template use File | Settings | File Templates.
 */
public enum CategoryType {
  BUSINESS_CLASSIFICATION("营业分类");

  String type;

  CategoryType(String type){
    this.type = type;
  }

  public String getType(){
    return this.type;
  }
}
