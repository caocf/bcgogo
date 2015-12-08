package com.bcgogo.enums.common;

/**
 * Created by IntelliJ IDEA.
 * User: ndong
 * Date: 13-1-2
 * Time: 上午3:10
 * To change this template use File | Settings | File Templates.
 */
public enum ObjectStatus {
  ENABLED("存在"),
  DISABLED("逻辑删除");

  String status;

  ObjectStatus(String status){
    this.status = status;
  }

  public String getStatus(){
    return this.status;
  }
}
