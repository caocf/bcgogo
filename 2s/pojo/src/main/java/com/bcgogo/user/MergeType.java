package com.bcgogo.user;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-15
 * Time: 下午12:02
 * To change this template use File | Settings | File Templates.
 */
public enum MergeType{

  MERGE_CUSTOMER("合并客户"),
  MERGE_SUPPLIER("合并供应商");

  String type;
  MergeType(String type){
    this.type = type;
  }
  public String getType(){
    return this.type;
  }
}