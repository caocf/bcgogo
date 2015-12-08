package com.bcgogo.enums;

/**
 * Created by IntelliJ IDEA.
 * User: zhoudongming
 * Date: 12-7-9
 * Time: 下午4:49
 * To change this template use File | Settings | File Templates.
 */
public enum MemberStatus {
  ENABLED("有效"),
  PARTENABLED("部分有效"),
  DISABLED("失效");

  String status;

  MemberStatus(String status){
    this.status = status;
  }

  public String getStatus(){
    return this.status;
  }
}
