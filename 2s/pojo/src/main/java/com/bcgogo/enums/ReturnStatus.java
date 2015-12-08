package com.bcgogo.enums;

/**
 * 借调单归还状态
 * User: ndong
 * Date: 13-3-15
 * Time: 上午5:35
 * To change this template use File | Settings | File Templates.
 */
public enum ReturnStatus {
  RETURN_NONE("未归还"),
  RETURN_PARTLY("部分归还"),
  RETURN_ALL("全部归还");

  String status;
  ReturnStatus(String status){
    this.status = status;
  }
  public String getStatus(){
    return this.status;
  }
}
